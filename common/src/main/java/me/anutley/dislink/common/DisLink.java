/*
 * MIT License
 *
 * Copyright (C) 2021 - 2023 Alfie Nutley (ANutley)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.anutley.dislink.common;

import me.anutley.dislink.common.config.*;
import me.anutley.dislink.common.listener.MessageListener;
import me.anutley.dislink.common.logger.DisLinkLogger;
import me.anutley.dislink.common.util.SettingsUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.File;

public class DisLink {

    private final ConfigManager configManager;

    private final DisLinkLogger logger;
    private final File dataFolder;
    private final ConfigLoader configLoader;
    private final JDA jda;
    private final SettingsUtil settingsUtil;

    public DisLink(DisLinkLogger logger, File dataFolder) {
        this.logger = logger;
        this.dataFolder = dataFolder;

        this.configLoader = new ConfigLoader(this);

        this.configManager = new ConfigManager(
                configLoader.load(MainConfig.class, "main.conf"),
                configLoader.load(ChannelsConfig.class, "channels.conf"),
                configLoader.load(GlobalSettingsConfig.class, "global-settings.conf")
        );

        this.jda = JDABuilder.createDefault(configManager.mainConfig().botToken())
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.MESSAGE_CONTENT)
                .disableCache(CacheFlag.VOICE_STATE, CacheFlag.SCHEDULED_EVENTS)
                .addEventListeners(new MessageListener(this))
                .build();

        this.settingsUtil = new SettingsUtil(this);

    }

    public DisLinkLogger logger() {
        return logger;
    }

    public File dataFolder() {
        return dataFolder;
    }

    public ConfigLoader configLoader() {
        return configLoader;
    }

    public ConfigManager configManager() {
        return configManager;
    }

    public JDA jda() {
        return jda;
    }

    public SettingsUtil settingsUtil() {
        return settingsUtil;
    }

    public void debug(String message) {
        if (configManager.mainConfig().debug()) logger.debug(message);
    }

}