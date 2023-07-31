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

@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public class ChannelPairConfig {

    @Comment("""
            channel-id: The ID of the channel you want chat to be forwarded to/from. This can be any type of thread channelThe URL of the webhook you want the bot to use.
            webhook-url: The url of the webhook. Given that auto-create-webhooks is enabled in "main.conf" and the bot has MANAGE_WEBHOOKS permissions in the channel, these will be created and saved for you.
            """)
    private ChannelConfig firstChannel = new ChannelConfig();

    private ChannelConfig secondChannel = new ChannelConfig();

    @Comment("Values available: FIRST_TO_SECOND, SECOND_TO_FIRST & BOTH")
    private Direction direction = Direction.BOTH;

    @Comment("These values below can be omitted and the default values specified in \"global-settings.conf\" will be used instead")
    private SettingsConfig channelSettings = new SettingsConfig();

    @Comment("Values available: WEBHOOK & PLAINTEXT")
    private Type type = Type.WEBHOOK;

    @ConfigSerializable
    public static class ChannelConfig {

        String channelId = "";

        String webhookUrl = "";

        public String channelId() {
            return channelId;
        }

        public String webhookUrl() {
            return webhookUrl;
        }

        public void webhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
        }
    }

    public enum Direction {
        FIRST_TO_SECOND,
        SECOND_TO_FIRST,
        BOTH
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


}
