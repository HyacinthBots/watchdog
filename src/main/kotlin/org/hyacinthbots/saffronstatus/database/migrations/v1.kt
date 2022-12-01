/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.hyacinthbots.saffronstatus.database.migrations

import org.hyacinthbots.saffronstatus.database.entities.WatchedBotData
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.exists
import org.litote.kmongo.setValue

@Suppress("UnusedPrivateMember")
suspend fun v1(db: CoroutineDatabase) {
	// TODO Fill with migration and set true in main function
	with(db.getCollection<WatchedBotData>()) {
		updateMany(
			WatchedBotData::publishMessage exists false,
			setValue(WatchedBotData::publishMessage, false)
		)
	}
}
