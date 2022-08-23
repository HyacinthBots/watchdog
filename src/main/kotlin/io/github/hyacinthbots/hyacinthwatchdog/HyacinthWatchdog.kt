/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:OptIn(PrivilegedIntent::class)

package io.github.hyacinthbots.hyacinthwatchdog

import com.kotlindiscord.kord.extensions.ExtensibleBot
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.github.hyacinthbots.hyacinthwatchdog.extensions.DowntimeWatcher

suspend fun main() {
    val bot = ExtensibleBot(BOT_TOKEN) {
		// Fill all members so we can get statuses
        members {
            all()
            fillPresences = true
        }

		// Add the GuildMembers intent and GuildPresences intent to allow the bot to see the status of other guild members
        intents {
            +Intent.GuildMembers
            +Intent.GuildPresences
        }

        extensions {
            add(::DowntimeWatcher)
        }

		presence {
			watching("Hyacinth Bots status'!")
		}
    }

    bot.start()
}
