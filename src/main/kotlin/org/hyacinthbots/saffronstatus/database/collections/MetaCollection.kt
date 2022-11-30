/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.hyacinthbots.saffronstatus.database.collections

import com.kotlindiscord.kord.extensions.koin.KordExKoinComponent
import org.hyacinthbots.saffronstatus.database.Database
import org.hyacinthbots.saffronstatus.database.entities.MetaData
import org.koin.core.component.inject
import org.litote.kmongo.eq

class MetaCollection : KordExKoinComponent {
	private val db: Database by inject()

	@PublishedApi
	internal val collection = db.watchdogDatabase.getCollection<MetaData>()

	/**
	 * Gets the metadata for the database.
	 *
	 * @return The metadata
	 */
	suspend inline fun get(): MetaData? = collection.findOne()

	/**
	 * Sets the metadata for the database. Used on first creation of table
	 *
	 * @param meta The metadata to insert
	 */
	suspend inline fun set(meta: MetaData) = collection.insertOne(meta)

	/**
	 * Updates the metadata for the database.
	 *
	 * @param meta The new metadata
	 */
	suspend inline fun update(meta: MetaData) = collection.findOneAndReplace(MetaData::id eq "meta", meta)
}
