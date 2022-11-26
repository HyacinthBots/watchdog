# Saffron Status

This is a simple bot designed to monitor the online status of a bot

When a bot is offline for more that your specified number of minutes, a notification will be posted to the selected channel, 
provided in /watched-bot add command. When the bot is then online again, a new notification will be posted and a downtime summary
provided.

#### Adding a bot
To add a bot to your guilds watchlist, run `/watched-bot add`. You'll be presented with the following options
* bot - The bot member you want to watch. This bot *must* be in your guild or the command will fail and tell you this.
* downtime-length - The amount of time the bot can be offline for, before sending your downtime notification in minutes.
* notification-channel - The channel to send the notifications too.
* notification-role - The role to ping when the bot is offline. Can be left empty.

Once done, you now have the bot added to your watchlist. You can verify this by running `/watched-bot view`.

#### Removing a bot
To remove a bot from your watchlist, run `/watched-bot remove`. You'll be presented with the following option, aided by autocomplete
* bot - The bot user you would like to remove from the list. This bot doesn't have to be in your guild.

Once done, the bot has been removed from your watchlist. Again you can verify this by running `/watched-bot view`


If for whatever reason the bot is removed from the guild it is being watched by, it will be automatically removed.

Example `.env` file:
```dotenv
BOT_TOKEN=
MONGO_URI=
```

* BOT_TOKEN: The token of the bot.
* MONGO_URI: The connection string to the database, defaults to `mongodb://localhost:27017`
