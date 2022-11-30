/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:OptIn(PrivilegedIntent::class)

package org.hyacinthbots.saffronstatus

import com.kotlindiscord.kord.extensions.ExtensibleBot
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import org.hyacinthbots.saffronstatus.extensions.DowntimeNotifier
import org.hyacinthbots.saffronstatus.extensions.WatchedBot

suspend fun main() {
    val bot = ExtensibleBot(BOT_TOKEN) {
		database(false) // TODO Set to true when the first migration is filled
		// Fill all members so we can get statuses
        members {
            all()
            fillPresences = true
			lockMemberRequests = true
        }

		// Add the GuildMembers intent and GuildPresences intent to allow the bot to see the status of other guild members
        intents {
            +Intent.GuildMembers
            +Intent.GuildPresences
        }

        extensions {
			add(::WatchedBot)
			add(::DowntimeNotifier)
        }

		presence {
			watching("bots for downtime!")
		}
    }

    bot.start()
}
