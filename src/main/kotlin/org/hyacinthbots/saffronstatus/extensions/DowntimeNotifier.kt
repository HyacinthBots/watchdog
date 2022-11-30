/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.hyacinthbots.saffronstatus.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.time.TimestampType.LongDateTime
import com.kotlindiscord.kord.extensions.time.TimestampType.RelativeTime
import com.kotlindiscord.kord.extensions.time.toDiscord
import com.kotlindiscord.kord.extensions.utils.scheduling.Scheduler
import com.kotlindiscord.kord.extensions.utils.scheduling.Task
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.behavior.channel.asChannelOfOrNull
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.NewsChannel
import dev.kord.rest.builder.message.create.embed
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import org.hyacinthbots.saffronstatus.database.collections.WatchedBotCollection
import org.hyacinthbots.saffronstatus.database.entities.CurrentDowntime
import kotlin.time.Duration

class DowntimeNotifier : Extension() {
	override val name: String = "downtime-notifier"

	private val scheduler = Scheduler()

	private lateinit var task: Task

	override suspend fun setup() {
		task = scheduler.schedule(60, repeat = true, callback = ::downtimes)
	}

	private suspend fun downtimes() {
		val guilds = kord.guilds.toList() // Get all guilds the bot is in

		// Loop all the guilds the bot is in
		guilds.forEach { guild ->
			// Get the watched bots for the guild
			val watchedBots = WatchedBotCollection().getWatchedBots(guild.id)

			// Loop through the watched bots
			watchedBots.forEach { entry ->
				val checkTime = Clock.System.now() // Get the time for the check

				// Loop through the bot map because it's just easier
				entry.bot.forEach botFor@{ bot ->
					val currentBot = guild.getMemberOrNull(bot.value) // Get the bot as a member of the guild

					if (currentBot == null) { // If the bot doesn't exist in this server, remove it to avoid errors
						WatchedBotCollection().removeWatchedBot(guild.id, mutableMapOf(bot.toPair()))
						return@botFor
					}

					// Get the presence of the bot, Offline if null
					val botStatus = currentBot.getPresenceOrNull()?.status ?: PresenceStatus.Offline

					// Get the channel as a guild message channel
					val notificationChannel = guild.getChannelOf<GuildMessageChannel>(entry.notificationChannel)
					// Get the notification role, or null if one wasn't set
					val notificationRole =
						if (entry.notificationRole != null) guild.getRoleOrNull(entry.notificationRole) else null

					// If the bot is offline/invisible, run the downtime code
					if (botStatus == PresenceStatus.Offline || botStatus == PresenceStatus.Invisible) {
						// Get the current downtime from the db or 0 if it's null
						var currentDowntimeMinutes = entry.currentDowntime?.offlineMinutes ?: 0
						// Get the current downtime start time
						val start = entry.currentDowntime?.downtimeStart
						currentDowntimeMinutes++ // Increment the downtime by 1
						// Update the downtime in the database with the start and downtime minutes
						WatchedBotCollection().updateDowntime(
							guild.id,
							mutableMapOf(bot.toPair()),
							CurrentDowntime(start, currentDowntimeMinutes)
						)
						// Get the downtime data as `entry` is now outdated
						val newData = WatchedBotCollection().getWatchedBot(guild.id, mutableMapOf(bot.toPair()))!!

						// If the offline time is greater than the set threshold for notifications
						if (newData.currentDowntime!!.offlineMinutes == newData.downtimeLength) {
							// Update the downtime to have the new downtime minutes and the appropriate start time
							WatchedBotCollection().updateDowntime(
								guild.id, mutableMapOf(bot.toPair()),
								    CurrentDowntime(
									checkTime.minus(Duration.parse("PT${newData.downtimeLength}M")),
									currentDowntimeMinutes
								)
							)
							// Send the notification
							val msg = notificationChannel.createMessage {
								content = "${notificationRole?.mention ?: ""} ${currentBot.mention} " +
										"is suffering downtime. You will be notified when the bot is restored."
							}
							if (msg.channel.asChannel().asChannelOfOrNull<NewsChannel>() != null) {
								// Is a news channel, publish message.
								msg.publish()
							}
						}
					} else {
						// The bot is, in fact, online at this time, so we get the bot from the db
						val newData = WatchedBotCollection().getWatchedBot(guild.id, mutableMapOf(bot.toPair()))!!
						// Check its downtime
						val downtime = newData.currentDowntime

						// If it's not null and has been offline for a given longer than 0 minutes
						if (downtime != null && downtime.offlineMinutes > 0 && downtime.downtimeStart != null) {
							val onlineTime = Clock.System.now() // Get the online time

							// Send a notification about it
							val msg = notificationChannel.createMessage {
								content = "${notificationRole?.mention ?: ""} ${currentBot.mention} is back online, " +
										"you can find a brief summary of the downtime below."
								embed {
									title = "${currentBot.username} Downtime Summary"
									field {
										name = "Start"
										value =
											"${downtime.downtimeStart.toDiscord(LongDateTime)} ${
												downtime.downtimeStart.toDiscord(RelativeTime)
											}"
									}
									field {
										name = "End"
										value =
											"${onlineTime.toDiscord(LongDateTime)} ${onlineTime.toDiscord(RelativeTime)}"
									}
									field {
										name = "Duration"
										value = "${downtime.offlineMinutes} minutes"
									}
								}
							}
							if (msg.channel.asChannel().asChannelOfOrNull<NewsChannel>() != null) {
								// Is a news channel, publish message.
								msg.publish()
							}
							// Yeet the downtime information as it's old now
							WatchedBotCollection().updateDowntime(
								guild.id,
								mutableMapOf(bot.toPair()),
								CurrentDowntime(null)
							)
						}
					}
				}
			}
		}
	}
}
