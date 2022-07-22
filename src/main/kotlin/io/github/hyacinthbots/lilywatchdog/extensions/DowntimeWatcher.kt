/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.hyacinthbots.lilywatchdog.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.time.TimestampType
import com.kotlindiscord.kord.extensions.time.toDiscord
import com.kotlindiscord.kord.extensions.utils.scheduling.Scheduler
import com.kotlindiscord.kord.extensions.utils.scheduling.Task
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.channel.NewsChannel
import dev.kord.rest.builder.message.create.embed
import io.github.hyacinthbots.lilywatchdog.ANNOUNCEMENT_CHANNEL
import io.github.hyacinthbots.lilywatchdog.DEV_ROLE
import io.github.hyacinthbots.lilywatchdog.DOWNTIME_ROLE
import io.github.hyacinthbots.lilywatchdog.GUILD_ID
import io.github.hyacinthbots.lilywatchdog.LILY_ID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import mu.KotlinLogging
import kotlin.time.Duration

class DowntimeWatcher : Extension() {
	override val name = "downtime-watcher"

	private val logger = KotlinLogging.logger("DowntimeWatcher")

	/** The scheduler that will run the checks. */
	private val scheduler = Scheduler()

	/** The task that will be run to check statuses. */
	private lateinit var task: Task

	/** The instant the downtime was logged as starting. */
	private var downtimeStart: Instant? = null

	/** The number of minutes the bot has been offline for. */
	private var offlineMinutes = 0

	override suspend fun setup() {
		task = scheduler.schedule(seconds = 60, pollingSeconds = 60, repeat = true, callback = ::checkForDowntime)
	}

	/**
	 * This function will check the status of the bot and log it to the announcement channel if necessary.
	 * @author NoComment1105
	 */
	private suspend fun checkForDowntime() {
		/** The instant this check is being run. */
		val checkTime = Clock.System.now()

		/** The guild we're working in. */
		val guild = kord.getGuild(GUILD_ID)!!

		/** The member object of the bot being watched. */
		val lily = guild.getMember(LILY_ID)

		/** The presence of the bot, if null default to offline. */
		val lilyStatus = lily.getPresenceOrNull()?.status ?: PresenceStatus.Offline

		/** The channel to post the announcement too. */
		val announcementChannel = guild.getChannelOf<NewsChannel>(ANNOUNCEMENT_CHANNEL)

		if (lilyStatus == PresenceStatus.Offline || lilyStatus == PresenceStatus.Invisible) {
			offlineMinutes++
			logger.info { "Offline detected. Duration: $offlineMinutes minutes" }
			if (offlineMinutes == 2) {
				downtimeStart = checkTime.minus(Duration.parse("PT1M"))
				announcementChannel.createMessage {
					content = "${guild.getRole(DOWNTIME_ROLE).mention} Lily is suffering some downtime. " +
							"Please be patient while the ${guild.getRole(DEV_ROLE).mention} resolve the issue."
				}.publish()
			}
		} else {
			if (offlineMinutes > 0 && downtimeStart != null) {
				logger.info { "Online restored!" }
				val onlineTime = Clock.System.now()

				announcementChannel.createMessage {
					content = "${guild.getRole(DOWNTIME_ROLE).mention} Lily is back online! You can find a brief " +
							"summary of the downtime period below."
					embed {
						title = "Downtime Summary"
						field {
							name = "Start"
							value = "${downtimeStart!!.toDiscord(TimestampType.LongDateTime)} ${
								downtimeStart!!.toDiscord(TimestampType.RelativeTime)
							}"
						}
						field {
							name = "End"
							value = "${onlineTime.toDiscord(TimestampType.LongDateTime)} ${
								onlineTime.toDiscord(TimestampType.RelativeTime)
							}"
						}
						field {
							name = "Duration"
							value = "$offlineMinutes minutes"
						}
					}
				}.publish()
			}

			offlineMinutes = 0
		}
	}
}
