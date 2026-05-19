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

package me.anutley.dislink.common.util;

import me.anutley.dislink.common.DisLink;
import me.anutley.dislink.common.config.ChannelPairConfig;
import me.anutley.dislink.common.config.ChannelPairConfig.ChannelConfig;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Optional;

public class SettingsUtil {

    private final DisLink disLink;

    public SettingsUtil(DisLink disLink) {
        this.disLink = disLink;
    }

    private ConfigurationNode getSetting(ChannelPairConfig channelPair, String setting) {

        ConfigurationNode originalChannelsNode = disLink.configLoader().originalChannelNode();
        ConfigurationNode globalSettingsNode = disLink.configLoader().originalGlobalSettingsNode();

        Optional<? extends ConfigurationNode> channelNodeOptional = originalChannelsNode.node("channels").childrenList()
                .stream().filter(node -> matchesGroup(node, channelPair))
                .findFirst();

        Object[] settings = setting.split("\\.");

        if (channelNodeOptional.isPresent()) {
            ConfigurationNode node = channelNodeOptional.get().node("channel-settings").node(settings);
            if (!node.virtual()) return node;
        }


        // if both the global setting and channel setting is null then use the default value
        return globalSettingsNode.node("global-settings").node(settings).virtual() ?
                disLink.configLoader().globalSettingsNode().node("global-settings").node(settings) :
                globalSettingsNode.node("global-settings").node(settings);
    }

    private static boolean matchesGroup(ConfigurationNode node, ChannelPairConfig group) {
        String groupId = group.groupId();
        String nodeGroupId = node.node("group-id").getString("");

        if (groupId != null && !groupId.isEmpty() && !nodeGroupId.isEmpty()) {
            return groupId.equals(nodeGroupId);
        }

        // fallback to matching by channel ids if group id is not set
        ChannelConfig first = group.firstChannel();
        ChannelConfig second = group.secondChannel();
        if (first != null && second != null) {
            String nodeFirst = node.node("first-channel").node("channel-id").getString();
            String nodeSecond = node.node("second-channel").node("channel-id").getString();
            if (nodeFirst != null && nodeSecond != null
                    && first.channelId().equals(nodeFirst)
                    && second.channelId().equals(nodeSecond)) {
                return true;
            }
        }

        return false;
    }

    public String getString(ChannelPairConfig channelPair, String setting) {
        return getSetting(channelPair, setting).getString();
    }

    // WILL RETURN FALSE IF NEITHER NODE EXISTS
    public boolean getBoolean(ChannelPairConfig channelPair, String setting) {
        return getSetting(channelPair, setting).getBoolean();
    }
}
