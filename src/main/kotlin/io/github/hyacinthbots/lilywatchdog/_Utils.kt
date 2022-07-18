/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.hyacinthbots.lilywatchdog

import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake

/** The Bots token. */
val BOT_TOKEN = env("BOT_TOKEN")

/** The ID of the bot whose status needs watching. */
val LILY_ID = Snowflake(env("LILY_ID"))

/** The guild to do the watching in. */
val GUILD_ID = Snowflake(env("GUILD_ID"))

/** The id of the developer role who will be pinged when there is downtime. */
val DEV_ROLE = Snowflake(env("DEV_ROLE"))

/** The id of the Downtime notification role which will be pinged when there is downtime. */
val DOWNTIME_ROLE = Snowflake(env("DOWNTIME_ROLE"))

/** The channel the downtime announcement and summary will be posted in. */
val ANNOUNCEMENT_CHANNEL = Snowflake(env("ANNOUNCEMENT_CHANNEL"))
