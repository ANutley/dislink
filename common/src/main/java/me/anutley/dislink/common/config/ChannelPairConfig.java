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

import me.anutley.dislink.common.delivery.sender.MessageSender.Type;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public class ChannelPairConfig {

    @Comment("""
            A unique identifier for this block of channels.
            This is used to match the settings in this file to the channels that are used to send messages to or from
            This can be left blank if you are still using the legacy 'first-channel' / 'second-channel' format
            however changing to the 'members' format is recommended
            """)
    private String groupId = "SET TO UNIQUE VALUE";

    @Comment("""
            The list of channels in this group:
            - Messages from any channel where read = true are forwarded to every other channel in the group where write = true. 
              Both flags default to true.
            - channel-id: the Discord channel id (any guild message channel, threads included).
            - webhook-url: the webhook to send through. If auto-create-webhooks is enabled in main.conf
              and the bot has MANAGE_WEBHOOKS, this is filled in for you.
            Side note: custom emotes from other servers require a self-created webhook — Discord won't allow it via auto-created ones.
            """)
    private List<ChannelConfig> members = new ArrayList<>(Arrays.asList(new ChannelConfig(), new ChannelConfig()));

    @Comment("Legacy field — kept readable for migration. Use `members` for new configs. Potentially removed in later version")
    private ChannelConfig firstChannel = null;

    @Comment("Legacy field — kept readable for migration. Use `members` for new configs. Potentially removed in later version")
    private ChannelConfig secondChannel = null;

    @Comment("Legacy field — kept readable for migration. Potentially removed in later version. Values: FIRST_TO_SECOND, SECOND_TO_FIRST, BOTH. ")
    private Direction direction = null;

    @Comment("These values below can be omitted and the default values specified in \"global-settings.conf\" will be used instead")
    private SettingsConfig channelSettings = new SettingsConfig();

    @Comment("Values available: WEBHOOK & PLAINTEXT")
    private Type type = Type.WEBHOOK;

    @ConfigSerializable
    public static class ChannelConfig {

        String channelId = "";

        String webhookUrl = "";

        @Comment("If true, messages sent in this channel are forwarded to other group members.")
        boolean send = true;

        @Comment("If true, messages from other group members are forwarded into this channel.")
        boolean receive = true;

        public String channelId() {
            return channelId;
        }

        public String webhookUrl() {
            return webhookUrl;
        }

        public void webhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
        }

        public boolean send() {
            return send;
        }

        public boolean receive() {
            return receive;
        }
    }

    public enum Direction {
        FIRST_TO_SECOND,
        SECOND_TO_FIRST,
        BOTH
    }

    public String groupId() {
        return groupId;
    }

    public ChannelConfig firstChannel() {
        return firstChannel;
    }

    public ChannelConfig secondChannel() {
        return secondChannel;
    }

    public Direction direction() {
        return direction;
    }

    public Type type() {
        return type;
    }

    /**
     * Returns the channels in this group, transparently handling legacy first/second configs.
     * If the new {@code members} list is populated with any non-empty channel id, it wins.
     * Otherwise, falls back to whichever of first/second are populated.
     */
    public List<ChannelConfig> effectiveMembers() {
        if (members != null && hasAnyPopulated(members)) {
            return members;
        }
        boolean firstPopulated = firstChannel != null && !firstChannel.channelId().isEmpty();
        boolean secondPopulated = secondChannel != null && !secondChannel.channelId().isEmpty();
        if (firstPopulated || secondPopulated) {
            List<ChannelConfig> legacy = new ArrayList<>(2);
            if (firstPopulated) legacy.add(firstChannel);
            if (secondPopulated) legacy.add(secondChannel);
            return Collections.unmodifiableList(legacy);
        }
        return members != null ? members : Collections.emptyList();
    }

    /**
     * Whether messages from this channel should be forwarded to other group members.
     * For new-schema entries, uses the channel's {@code send} flag.
     * For legacy first/second entries, derives from the {@code direction} enum.
     */
    public boolean canSend(ChannelConfig channel) {
        if (isLegacyMember(channel)) {
            if (direction == null) return true;
            if (channel == firstChannel) {
                return direction == Direction.BOTH || direction == Direction.FIRST_TO_SECOND;
            }
            return direction == Direction.BOTH || direction == Direction.SECOND_TO_FIRST;
        }
        return channel.send();
    }

    /**
     * Whether messages from other group members should be forwarded into this channel.
     */
    public boolean canReceive(ChannelConfig channel) {
        if (isLegacyMember(channel)) {
            if (direction == null) return true;
            if (channel == firstChannel) {
                return direction == Direction.BOTH || direction == Direction.SECOND_TO_FIRST;
            }
            return direction == Direction.BOTH || direction == Direction.FIRST_TO_SECOND;
        }
        return channel.receive();
    }

    private boolean isLegacyMember(ChannelConfig channel) {
        return channel != null && (channel == firstChannel || channel == secondChannel)
                && !(members != null && hasAnyPopulated(members));
    }

    private static boolean hasAnyPopulated(List<ChannelConfig> list) {
        for (ChannelConfig c : list) {
            if (c != null && c.channelId() != null && !c.channelId().isEmpty()) return true;
        }
        return false;
    }
}
