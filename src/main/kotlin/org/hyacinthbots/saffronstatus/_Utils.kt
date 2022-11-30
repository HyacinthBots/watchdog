/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.hyacinthbots.saffronstatus

import com.kotlindiscord.kord.extensions.builders.ExtensibleBotBuilder
import com.kotlindiscord.kord.extensions.utils.loadModule
import kotlinx.coroutines.runBlocking
import org.hyacinthbots.saffronstatus.database.Database
import org.hyacinthbots.saffronstatus.database.collections.MetaCollection
import org.hyacinthbots.saffronstatus.database.collections.WatchedBotCollection
import org.koin.dsl.bind

suspend inline fun ExtensibleBotBuilder.database(migrate: Boolean) {
	val db = Database()

	hooks {
		beforeKoinSetup {
			loadModule {
				single { db } bind Database::class
			}

			loadModule {
				single { WatchedBotCollection() } bind WatchedBotCollection::class
				single { MetaCollection() } bind MetaCollection::class
			}

			if (migrate) {
				runBlocking {
					db.migrate()
				}
			}
		}
	}
}
