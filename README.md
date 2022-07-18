# Lily watchdog

This is a simple bot designed to monitor the status of [LilyBot](https://github.com/IrisShaders/LilyBot).

When the bot is offline for more than 2 consecutive minutes, a notification will be posted to the selected channel, 
provided in the `.env` file. When the bot is then online again, a new notification will be posted and a downtime summary
provided.

Example `.env` file:
```dotenv
BOT_TOKEN=
LILY_ID=
GUILD_ID=
DEV_ROLE=
DOWNTIME_ROLE=
ANNOUNCEMENT_CHANNEL=
```

BOT_TOKEN: The token of the bot.
LILY_ID: The ID of the bot to monitor.
GUILD_ID: The ID of the guild where the bot is located.
DEV_ROLE: The ID of the role that will be pinged when Lily is down.
DOWNTIME_ROLE: The ID of the notification role.
ANNOUNCEMENT_CHANNEL: The ID of the channel where the downtime announcement will be posted.
