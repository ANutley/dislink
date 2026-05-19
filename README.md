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

### Channel groups
A channel group is a set of channels that forward messages between each other. Each channel in a group has independent `send` and `receive` flags:
* `send = true` — messages sent in this channel get forwarded to other members
* `receive = true` — messages from other members get forwarded into this channel

Both default to `true`, which gives you two-way mirroring. Drop one to make a channel send-only or receive-only.

```yaml
channels = [
  {
    # Optional. Used to match channel-settings overrides across config reloads.
    group-id = "main-bridge"
    # WEBHOOK or PLAINTEXT
    type = WEBHOOK
    members = [
      { channel-id = "12345", webhook-url = "", send = true, receive = true },
      { channel-id = "54321", webhook-url = "", send = true, receive = true },
      # A read-only mirror: receives messages from the others but doesn't push its own back
      { channel-id = "99999", webhook-url = "", send = false, receive = true }
    ]
  },
  {
    group-id = "announcements"
    type = WEBHOOK
    members = [
      { channel-id = "56789", webhook-url = "", send = true, receive = false },
      { channel-id = "98765", webhook-url = "", send = false, receive = true }
    ]
  }
]
```

`webhook-url` is filled in automatically when `auto-create-webhooks` is enabled in `main.conf` and the bot has `MANAGE_WEBHOOKS` in each channel. Custom emotes from other servers need a hand-created webhook URL pasted in — Discord blocks auto-created webhooks from sending them.

### Migrating from the old two-channel config
The pre-v2.1 `first-channel` / `second-channel` / `direction` form still loads — you don't have to touch your existing config. It maps to the new schema like this:

| Old `direction`   | Equivalent `members` flags                                                  |
|-------------------|-----------------------------------------------------------------------------|
| `BOTH`            | both channels `send = true, receive = true`                                 |
| `FIRST_TO_SECOND` | first `send = true, receive = false`, second `send = false, receive = true` |
| `SECOND_TO_FIRST` | first `send = false, receive = true`, second `send = true, receive = false` |

When you're ready to migrate, replace the `first-channel` / `second-channel` / `direction` block with a `members` list. The `channel-settings` block keeps working — to be sure overrides match across upgrades, set a `group-id`.
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
