/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.hyacinthbots.saffronstatus

import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.envOrNull

/** The bot's token. */
val BOT_TOKEN = env("BOT_TOKEN")

/** The string for connection to the database, defaults to localhost. */
val MONGO_URI = envOrNull("MONGO_URI") ?: "mongodb://localhost:27017"
