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

package me.anutley.dislink.common.config;

import me.anutley.dislink.common.DisLink;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigLoader {

    private final DisLink disLink;

    private ConfigurationNode originalGlobalSettingsNode;
    private ConfigurationNode globalSettingsNode;
    private ConfigurationNode originalChannelNode;
    private ConfigurationNode channelNode;

    public ConfigLoader(DisLink disLink) {
        this.disLink = disLink;
    }

    public ConfigurationLoader<?> configurationLoader(String fileName) {
        return HoconConfigurationLoader.builder()
                .file(new File(disLink.dataFolder(), fileName))
                .prettyPrinting(true)
                .build();
    }

    public <T> T load(Class<T> configClass, String fileName) {

        if (!Files.exists(disLink.dataFolder().toPath())) {
            try {
                Files.createDirectories(disLink.dataFolder().toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        var loader = this.configurationLoader(fileName);

        ConfigurationNode originalNode;
        ConfigurationNode configNode;

        try {
            originalNode = loader.load();

            configNode = originalNode.copy();

            if (configClass.equals(GlobalSettingsConfig.class)) {
                globalSettingsNode = configNode;
                originalGlobalSettingsNode = originalNode;
            }

            if (configClass.equals(ChannelsConfig.class)) {
                channelNode = configNode;
                originalChannelNode = originalNode;
            }

            var config = configNode.get(configClass);

            if (!Files.exists(new File(disLink.dataFolder(), fileName).toPath())) {
                configNode.set(configClass, config);
                loader.save(configNode);
            }

            return config;
        } catch (ConfigurateException exception) {
            disLink.logger().error("There was an error loading the config file", exception);
            return null;
        }
    }

    public void reloadMainConfig(Class<?> clazz, String fileName) {
        load(clazz, fileName);
    }

    public <V> void saveConfig(String fileName, ConfigurationNode configNode, Class<?> clazz, V config) {
        try {
            configurationLoader(fileName).save(configNode.set(clazz, config));
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
        reloadMainConfig(clazz, fileName);
    }

    public ConfigurationNode originalGlobalSettingsNode() {
        return originalGlobalSettingsNode;
    }

    public ConfigurationNode globalSettingsNode() {
        return globalSettingsNode;
    }

    public ConfigurationNode originalChannelNode() {
        return originalChannelNode;
    }

    public ConfigurationNode channelNode() {
        return channelNode;
    }
}
