package me.anutley.dislink;

import ch.qos.logback.classic.Logger;
import me.anutley.dislink.listeners.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class DisLink {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(DisLink.class);
    private static JDA jda;

    public static void main(String[] args) throws LoginException {

        jda = JDABuilder.createDefault(Config.get().node("BotToken").getString())
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS)
                .disableCache(CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)

                // Commands
                .addEventListeners(
                        new MessageListener()
                )
                .build();


    }

    public static void debug(String message) {
        if (!Config.get().node("Debug").getBoolean()) return;
        LOGGER.info("[Debug] " + message);
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static JDA getJda() {
        return jda;
    }
}
