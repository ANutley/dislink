# DisLink V2
DisLink is a Discord bot used to forward messages between Discord channels. This can be done across guilds or within the same guild. Thread channels are also supported

If you need support with DisLink please [join my Discord server](https://discord.gg/vwwe3ThHxK)

## Downloads
|            | Releases                                                                                                        | Snapshots                                                                                                        |
|------------|-----------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| Standalone | [Download](https://repo.anutley.me/api/maven/latest/file/releases/me/anutley/dislink-standalone?classifier=all) | [Download](https://repo.anutley.me/api/maven/latest/file/snapshots/me/anutley/dislink-standalone?classifier=all) |
| Bukkit     | [Download](https://repo.anutley.me/api/maven/latest/file/releases/me/anutley/dislink-bukkit?classifier=all)     | [Download](https://repo.anutley.me/api/maven/latest/file/snapshots/me/anutley/dislink-bukkit?classifier=all)     |

## Setup
1. Create a bot application through the Discord developer panel
2. Generate a bot token and save it somewhere secure
3. Download the relevant DisLink jar from above
4. * If you are running Dislink on a Minecraft server, place Dislink in the plugins folder and restart the server to generate the config files
   * If you are running DisLink standalone, then run the jar to generate the config files
5. Once the config files are generated fill out main.conf with the bot token you generated earlier
6. Then fill out channels.conf with your channel ID's 
7. Save your config and restart the server/application, and you should be ready to go!

## Tips
### Global Settings
Global settings can be found in `global-settings.conf` and define the default values for forwarding messages. This means the `channel-settings` block in `channels.conf` can be deleted to remove repetition if you want all forwarding to be the same format

### Multiple channels
To forward between multiple channels you just need to add another block in channels.conf. In this example we will omit the `channel-settings` option and just take the global settings from `global-settings.conf`

For example: 
```yaml
channels = [
  {
    # Values available: FIRST_TO_SECOND, SECOND_TO_FIRST & BOTH
    direction = BOTH
    # channel-id: The ID of the channel you want chat to be forwarded to/from. This can be any type of thread channelThe URL of the webhook you want the bot to use.
    # webhook-url: The url of the webhook. Given that auto-create-webhooks is enabled in "main.conf" and the bot has MANAGE_WEBHOOKS permissions in the channel, these will be created and saved for you.
    first-channel {
      channel-id = "12345"
      webhook-url = ""
    }
    second-channel {
      channel-id = "54321"
      webhook-url = ""
    }
    # Values available: WEBHOOK & PLAINTEXT
    type = WEBHOOK
  },
  {
    # Values available: FIRST_TO_SECOND, SECOND_TO_FIRST & BOTH
    direction = BOTH
    # channel-id: The ID of the channel you want chat to be forwarded to/from. This can be any type of thread channelThe URL of the webhook you want the bot to use.
    # webhook-url: The url of the webhook. Given that auto-create-webhooks is enabled in "main.conf" and the bot has MANAGE_WEBHOOKS permissions in the channel, these will be created and saved for you.
    first-channel {
      channel-id = "56789"
      webhook-url = ""
    }
    second-channel {
      channel-id = "98765"
      webhook-url = ""
    }
    # Values available: WEBHOOK & PLAINTEXT
    type = WEBHOOK
  }
]
```
## Placeholders available
* `%message%`

### Author
* `%author_username%`
* `%author_displayname%`
* `%author_id%`
* `%author_avatar%`
* `%author_mention%`
* `%author_nickname%`
* `%author_guild_avatar%`
* `%author_toprole_name%`
* `%author_toprole_mention%` 

### Origin 
* `%origin_channel_name%`
* `%origin_channel_id%`
* `%origin_channel_mention%`
* `%origin_guild_name%`
* `%origin_guild_id%`
* `%origin_guild_iconurl%` 

### Destination
* `%destination_channel_name%`
* `%destination_channel_id%`
* `%destination_channel_mention%`
* `%destination_guild_name%`
* `%destination_guild_id%`
* `%destination_guild_iconurl%`
