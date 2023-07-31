dependencies {
    // Discord
    implementation("net.dv8tion:JDA:5.0.0-beta.10") {
        exclude(module = "opus-java")
    }

    implementation("club.minnced:discord-webhooks:0.8.2")

    // Configuration
    implementation("org.spongepowered:configurate-hocon:4.1.2")
}