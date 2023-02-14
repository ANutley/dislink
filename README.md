# DisLink

A bot that uses webhooks to link channels across guilds

## Setup
To correctly setup dislink, follow the below steps!

1. Clone the project using the command `git clone https://github.com/ANutley/dislink`
2. Enter the directory was created using `cd dislink`
3. Build the project using `gradlew shadowJar` (Windows) or `./gradlew shadowJar` (Linux / Mac)
4. Once the project has been built find the jar named `dislink-1.0-SNAPSHOT-all.jar` in `/builds/libs`
5. Find the default config file [here](https://github.com/ANutley/dislink/blob/master/src/main/resources/config.yml)
6. Copy the jar file and the config file to a new directory
7. Create a bot application [here](https://discord.com/developers/applications) and copy the bot-token
9. Edit the config file and fill in the BotToken and other values
10. Run the jar file using `java -jar dislink-1.0-SNAPSHOT-all.jar`
11. Invite the bot to the server(s) you want to link channels in

After that you should be good to go.

--- 

### DiscordSRV setup

If you want to use this bot as a way to bridge DiscordSRV chat between two discord servers while v2 is in progress, this is how!

First make sure you have followed all the steps above!

Scenario: You have Guild A which is where the channel (Channel A) that DiscordSRV bridges chat with. 
You want this to also be forwarded to a channel (Channel B) which is in Guild B. 
In this example Channel A will have an ID of 123456789012345678 and Channel B will have an ID of 098765432109876543

This is how the Channels definition would look for this example:

```yaml
  - Channel1: "123456789012345678"
    Channel2: "098765432109876543"
    Direction: "3"
    IgnoreBots: false
    IgnoreWebhooks: false
    IgnoreAttachments: false
    IgnoreEmbeds: false
    Mentions:
      Roles: false
      Users: true
      Everyone: false
```

This means that **any** content sent in Channel 1 will be forwarded to Channel 2 and vice versa. This allows people to communicate between the two guilds,
and Minecraft messages in Channel 1 will be sent to Channel 2. You can then customise it to your liking, for example blocking attachments from being forwarded

---

If you have any questions ask [here](https://discord.gg/vwwe3ThHxK)
