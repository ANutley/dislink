# DisLink

A bot that uses webhooks to link channels across guilds

## Setup
To correctly setup dislink, follow the below steps!

1. Clone the project using the command `git clone https://github.com/ANutley/dislink`
2. Enter the directory was created using `cd dislink`
3. Build the project using `gradlew shadowJar` (Windows) or `./gradlew shadowJar` (Linux / Mac)
4. Once the project has been built find the jar named `dislink-1.0-SNAPSHOT-all.jar`
5. Find the default config file [here](https://github.com/ANutley/dislink/blob/master/src/main/resources/config.yml)
6. Copy the jar file and the config file to a new directory
7. Create a bot application [here](https://discord.com/developers/applications) and copy the bot-token
9. Edit the config file and fill in the BotToken and other values
10. Run the jar file using `java -jar dislink-1.0-SNAPSHOT-all.jar`
11. Invite the bot to the server(s) you want to link channels in

After that you should be good to go.

If you have any questions ask [here](https://discord.gg/NtbNhGt3XN)