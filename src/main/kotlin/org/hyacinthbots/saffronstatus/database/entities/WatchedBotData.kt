/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.hyacinthbots.saffronstatus.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Suppress("DataClassShouldBeImmutable")
@Serializable
data class WatchedBotData(
	val guildId: Snowflake,
	val notificationChannel: Snowflake,
	val notificationRole: Snowflake?,
	val downtimeLength: Int,
	var currentDowntime: CurrentDowntime?,
	val bot: MutableMap<String, Snowflake>
)

@Suppress("DataClassShouldBeImmutable")
@Serializable
data class CurrentDowntime(
	val downtimeStart: Instant?,
	var offlineMinutes: Int = 0,
)
