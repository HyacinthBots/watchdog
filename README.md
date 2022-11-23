# SaffronStatus

This is a simple bot designed to monitor the online status of a bot

When a bot is offline for more that your specified number of minutes, a notification will be posted to the selected channel, 
provided in /watched-bot add command. When the bot is then online again, a new notification will be posted and a downtime summary
provided.

Example `.env` file:
```dotenv
BOT_TOKEN=
MONGO_URI=
```

* BOT_TOKEN: The token of the bot.
* MONGO_URI: The connection string to the databased
