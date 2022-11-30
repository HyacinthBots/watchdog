/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.hyacinthbots.saffronstatus.extensions

import com.kotlindiscord.kord.extensions.checks.anyGuild
import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.ephemeralSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.boolean
import com.kotlindiscord.kord.extensions.commands.converters.impl.channel
import com.kotlindiscord.kord.extensions.commands.converters.impl.int
import com.kotlindiscord.kord.extensions.commands.converters.impl.member
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalRole
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.suggestStringMap
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.getChannelOfOrNull
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.rest.builder.message.create.embed
import org.hyacinthbots.saffronstatus.database.collections.WatchedBotCollection
import org.hyacinthbots.saffronstatus.database.entities.WatchedBotData

class WatchedBot : Extension() {
	override val name: String = "watched-bot"

	override suspend fun setup() {
		ephemeralSlashCommand {
			name = "watched-bot"
			description = "The parent command for all watched bot commands"

			ephemeralSubCommand(::AddArgs) {
				name = "add"
				description = "Add a bot to the watchlist"

				check {
					anyGuild()
					hasPermission(Permission.ManageGuild)
				}

				action {
					val watchedBots = WatchedBotCollection().getWatchedBots(guild!!.id)
					val botMap = mutableMapOf<String, Snowflake>()
					botMap[arguments.bot.username] = arguments.bot.id

					val isBotPresent = watchedBots.find {
						it.bot == botMap
					}

					if (isBotPresent != null) {
						respond {
							content = "You are already watching this bot! Please choose a different bot"
						}
						return@action
					} else if (guild!!.getMemberOrNull(arguments.bot.id) == null) {
						respond {
							content = "I cannot find the bot in this server, make sure the bot is in this server!"
						}
						return@action
					} else if (!arguments.bot.isBot) {
						respond {
							content = "This is not a bot! You can only watch bots. Please try again"
						}
						return@action
					}

					val role = if (arguments.notificationRole?.id != null) {
						guild!!.getRoleOrNull(arguments.notificationRole!!.id)
					} else {
						null
					}

					if (guild!!.getChannelOfOrNull<GuildMessageChannel>(arguments.notificationChannel.id) == null) {
						respond {
							content = "I cannot find the Notification Channel you specified. Please check it exists" +
									"and that I have permission to view it!"
						}
						return@action
					} else if (arguments.notificationRole != null && role == null) {
						respond {
							content = "I cannot find the Notification role you specified. Please check it exists!"
						}
						return@action
					} else if (arguments.notificationRole != null && role?.mentionable != true) {
						respond {
							content =
								"I cannot mention role: `${role?.name}`. Please make sure I can mention it and try again"
						}
						return@action
					}

					WatchedBotCollection().addWatchedBot(
						WatchedBotData(
							guild!!.id,
							arguments.notificationChannel.id,
							arguments.notificationRole?.id,
							arguments.downtimeLength,
							null,
							botMap,
							arguments.publishMessage
						)
					)

					respond {
						content = "Bot added to watchlist!"
					}
				}
			}

			ephemeralSubCommand(::RemoveArgs) {
				name = "remove"
				description = "Remove a bot from the watchlist"

				check {
					anyGuild()
					hasPermission(Permission.ManageGuild)
				}

				action {
					val watchedBots = WatchedBotCollection().getWatchedBots(guild!!.id)
					val botMap = mutableMapOf<String, Snowflake>()
					botMap[arguments.bot.username] = arguments.bot.id

					val isBotPresent = watchedBots.find {
						it.bot == botMap
					}

					if (isBotPresent == null) {
						respond {
							content = "This bot is not in the watchlist"
						}
						return@action
					}

					WatchedBotCollection().removeWatchedBot(guild!!.id, botMap)

					respond {
						content = "Bot removed from watchlist"
					}
				}
			}

			ephemeralSubCommand {
				name = "view"
				description = "View the bots you're watching"

				check {
					anyGuild()
				}

				action {
					val watchedBots = WatchedBotCollection().getWatchedBots(guild!!.id)

					var response = ""
					watchedBots.forEach { entry ->
						entry.bot.forEach {
							response +=
								"Name: ${it.key}\nMention: <@${it.value}>\nDowntime Length: ${entry.downtimeLength}\n---\n"
						}
					}

					respond {
						embed {
							title = "Watched bots"
							description = response
						}
					}
				}
			}
		}
	}

	inner class AddArgs : Arguments() {
		val bot by member {
			name = "bot"
			description = "The bot to watch"
		}

		val downtimeLength by int {
			name = "downtime-length"
			description = "The length of downtime required for a notification to be posted."
		}

		val notificationChannel by channel {
			name = "notification-channel"
			description = "The channel to send the notifications too when the bot goes offline"
		}
		val publishMessage by boolean {
			name = "publish-message"
			description = "Whether to publish the downtime messages for this bot"
		}
		val notificationRole by optionalRole {
			name = "notification-role"
			description = "The role to ping when this bot goes offline"
		}
	}

	inner class RemoveArgs : Arguments() {
		val bot by user {
			name = "bot"
			description = "The bot to remove from the watchlist"

			autoComplete {
				val bots = WatchedBotCollection().getWatchedBots(data.guildId.value!!)
				val map = mutableMapOf<String, String>()

				bots.forEach { mapFromDb ->
					mapFromDb.bot.forEach {
						map[it.key] = it.value.toString()
					}
				}

				suggestStringMap(map)
			}
		}
	}
}
