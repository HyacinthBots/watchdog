/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.hyacinthbots.saffronstatus.database.collections

import com.kotlindiscord.kord.extensions.koin.KordExKoinComponent
import dev.kord.common.entity.Snowflake
import org.hyacinthbots.saffronstatus.database.Database
import org.hyacinthbots.saffronstatus.database.entities.CurrentDowntime
import org.hyacinthbots.saffronstatus.database.entities.WatchedBotData
import org.koin.core.component.inject
import org.litote.kmongo.eq

class WatchedBotCollection : KordExKoinComponent {
	private val db: Database by inject()

	@PublishedApi
	internal val collection = db.watchdogDatabase.getCollection<WatchedBotData>()

	/**
	 * Adds a bot to the watchlist.
	 *
	 * @param watchedBotData The data of the bot to watch
	 * @author NoComment1105
	 */
	suspend inline fun addWatchedBot(watchedBotData: WatchedBotData) =
		collection.insertOne(watchedBotData)

	/**
	 * Removes a bot for the watchlist.
	 *
	 * @param inputGuildId The ID of the guild the bot is watched by
	 * @param bot The map of the bots name an ID to search for
	 * @author NoComment1105
	 */
	suspend inline fun removeWatchedBot(inputGuildId: Snowflake, bot: MutableMap<String, Snowflake>) =
		collection.deleteOne(WatchedBotData::guildId eq inputGuildId, WatchedBotData::bot eq bot)

	/**
	 * Gets a list of the watched bots for the given [inputGuildId].
	 *
	 * @param inputGuildId The ID of the guild ot search
	 * @return A list of all the [WatchedBotData] for the guild
	 * @author NoComment1105
	 */
	suspend inline fun getWatchedBots(inputGuildId: Snowflake): List<WatchedBotData> =
		collection.find(WatchedBotData::guildId eq inputGuildId).toList()

	/**
	 * Gets a bot from the watched bot section for the given [inputGuildId].
	 *
	 * @param inputGuildId The ID of the guild the bot is being watched by
	 * @param bot The map of the bots name and ID
	 * @return The [WatchedBotData] for the bot
	 * @author NoComment1105
	 */
	suspend inline fun getWatchedBot(inputGuildId: Snowflake, bot: MutableMap<String, Snowflake>): WatchedBotData? =
		collection.findOne(WatchedBotData::guildId eq inputGuildId, WatchedBotData::bot eq bot)

	/**
	 * Updates the downtime for the target [bot] in a given [inputGuildId].
	 *
	 * @param inputGuildId The ID of the guild the bot is being watched by
	 * @param bot The map of the bots name and ID
	 * @param currentDowntime The current downtime to apply to the watched bot
	 * @author NoComment1105
	 */
	suspend inline fun updateDowntime(
		inputGuildId: Snowflake,
		bot: MutableMap<String, Snowflake>,
		currentDowntime: CurrentDowntime?
	) {
		val data = collection.findOne(WatchedBotData::guildId eq inputGuildId, WatchedBotData::bot eq bot)!!
		collection.deleteOne(WatchedBotData::guildId eq inputGuildId, WatchedBotData::bot eq bot)
		collection.insertOne(
			WatchedBotData(
				data.guildId,
				data.notificationChannel,
				data.notificationRole,
				data.downtimeLength,
				currentDowntime,
				data.bot
			)
		)
	}
}
