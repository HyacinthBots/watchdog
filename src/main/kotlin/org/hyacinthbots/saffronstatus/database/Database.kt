/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.hyacinthbots.saffronstatus.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import org.bson.UuidRepresentation
import org.hyacinthbots.saffronstatus.MONGO_URI
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class Database {
	private val settings = MongoClientSettings
		.builder()
		.uuidRepresentation(UuidRepresentation.STANDARD)
		.applyConnectionString(ConnectionString(MONGO_URI))
		.build()

	private val client = KMongo.createClient(settings).coroutine

	val watchdogDatabase get() = client.getDatabase("watchdog")

	suspend fun migrate() {
		// do the thing
	}
}
