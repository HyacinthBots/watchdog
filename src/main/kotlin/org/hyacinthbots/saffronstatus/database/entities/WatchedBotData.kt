/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.hyacinthbots.saffronstatus.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * The Data associated with a bot being watched by a guild.
 *
 * @property guildId The ID of the guild watching the bot
 * @property notificationChannel The ID of the channel to post downtime notification too
 * @property notificationRole The ID of the role to ping for notifications, or null of you don't want too
 * @property downtimeLength The time the bot can be down for before a notification is sent
 * @property currentDowntime The data about the current downtime period the bot is in, or null if it's online
 * @property bot A map containing the bots name as the key and its ID as the value
 * @property publishMessage Whether to publish the downtime messages when in an Announcement Channel
 */
@Serializable
data class WatchedBotData(
	val guildId: Snowflake,
	val notificationChannel: Snowflake,
	val notificationRole: Snowflake?,
	val downtimeLength: Int,
	val currentDowntime: CurrentDowntime?,
	val bot: MutableMap<String, Snowflake>,
	val publishMessage: Boolean
)

/**
 * The data for a downtime period the bot is going through.
 *
 * @property downtimeStart The instant the downtime started for a bot
 * @property offlineMinutes The number of minutes the bot has been offline, 0 by default
 */
@Suppress("DataClassShouldBeImmutable")
@Serializable
data class CurrentDowntime(
	val downtimeStart: Instant?,
	var offlineMinutes: Int = 0,
)
