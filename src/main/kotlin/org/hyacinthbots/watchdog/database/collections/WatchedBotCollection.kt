/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.hyacinthbots.watchdog.database.collections

import com.kotlindiscord.kord.extensions.koin.KordExKoinComponent
import dev.kord.common.entity.Snowflake
import org.hyacinthbots.watchdog.database.Database
import org.hyacinthbots.watchdog.database.entities.WatchedBotData
import org.koin.core.component.inject
import org.litote.kmongo.eq

class WatchedBotCollection : KordExKoinComponent {
	private val db: Database by inject()

	@PublishedApi
	internal val collection = db.watchdogDatabase.getCollection<WatchedBotData>()

	suspend inline fun addWatchedBot(watchedBotData: WatchedBotData) =
		collection.insertOne(watchedBotData)

	suspend inline fun removeWatchedBot(inputGuildId: Snowflake, bot: MutableMap<String, Snowflake>) =
		collection.deleteOne(WatchedBotData::guildId eq inputGuildId, WatchedBotData::bot eq bot)

	suspend inline fun getWatchedBots(inputGuildId: Snowflake): List<WatchedBotData> =
		collection.find(WatchedBotData::guildId eq inputGuildId).toList()
}
