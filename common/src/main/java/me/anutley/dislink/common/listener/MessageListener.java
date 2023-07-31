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

package me.anutley.dislink.common.listener;

import me.anutley.dislink.common.DisLink;
import me.anutley.dislink.common.config.ChannelPairConfig;
import me.anutley.dislink.common.config.ChannelPairConfig.ChannelConfig;
import me.anutley.dislink.common.config.ChannelPairConfig.Direction;
import me.anutley.dislink.common.delivery.sender.MessageSender;
import me.anutley.dislink.common.delivery.sender.PlainTextSender;
import me.anutley.dislink.common.delivery.sender.WebhookSender;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class MessageListener extends ListenerAdapter {

    private final DisLink disLink;

    public MessageListener(DisLink disLink) {
        this.disLink = disLink;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (!event.isFromGuild()) return;

        getDestinationChannels(event.getGuildChannel()).forEach((channelPair, destinationChannel) -> {

            if (invalidAuthor(channelPair, event)) return;

            ChannelConfig originChannel = channelPair.firstChannel().channelId().equals(destinationChannel.channelId()) ?
                    channelPair.secondChannel() : channelPair.firstChannel();

            MessageSender<?, ?> delivery;

            if (channelPair.type().equals(MessageSender.Type.WEBHOOK)) {
                delivery = new WebhookSender(
                        disLink,
                        channelPair,
                        originChannel,
                        destinationChannel,
                        event.getMessage()
                );
            } else {
                delivery = new PlainTextSender(
                        disLink,
                        channelPair,
                        originChannel,
                        destinationChannel,
                        event.getMessage()
                );
            }

            delivery.execute();

        });
    }

    private Map<ChannelPairConfig, ChannelConfig> getDestinationChannels(GuildMessageChannelUnion channel) {
        String originChannelId = channel.getId();

        Map<ChannelPairConfig, ChannelConfig> channels = new HashMap<>();

        for (ChannelPairConfig pairConfig : disLink.configManager().channelsConfig().channels()) {

            ChannelConfig firstChannel = pairConfig.firstChannel();
            ChannelConfig secondChannel = pairConfig.secondChannel();


            Direction direction = pairConfig.direction();

            String firstChannelId = firstChannel.channelId();
            String secondChannelId = secondChannel.channelId();

            if (!originChannelId.equals(firstChannelId) && !originChannelId.equals(secondChannelId)) {
                continue;
            }

            if (disLink.jda().getGuildChannelById(firstChannelId) == null) {
                disLink.debug("A message has been sent in a channel listen in the config, however the destination channel (" + firstChannelId + ") doesn't exist ");
                continue;
            }

            if (disLink.jda().getGuildChannelById(secondChannelId) == null) {
                disLink.debug("A message has been sent in a channel listen in the config, however the destination channel (" + secondChannelId + ") doesn't exist ");
                continue;
            }

            if (originChannelId.equals(firstChannelId)) {

                if (direction.equals(Direction.BOTH) || direction.equals(Direction.FIRST_TO_SECOND))
                    channels.put(pairConfig, secondChannel);
            }

            if (originChannelId.equals(secondChannelId)) {

                if (direction.equals(Direction.BOTH) || direction.equals(Direction.SECOND_TO_FIRST))
                    channels.put(pairConfig, firstChannel);

            }
        }

        return channels;
    }

    private boolean invalidAuthor(ChannelPairConfig channelPair, MessageReceivedEvent event) {

        boolean ignoreBots = disLink.settingsUtil().getBoolean(channelPair, "ignore.bots");
        boolean ignoreWebhooks = disLink.settingsUtil().getBoolean(channelPair, "ignore.webhooks");

        if ((event.getAuthor().isBot() && !event.isWebhookMessage()) && ignoreBots) {
            disLink.debug("The author of this message is a bot, and ignore bots is enabled in the config, aborting message forwarding for " + event.getMessage());
            return true;
        }

        if (event.isWebhookMessage() && ignoreWebhooks) {
            disLink.debug("The author of this message is a webhook, and ignore webhooks is enabled in the config, aborting message forwarding for " + event.getMessage());
            return true;
        }

        return false;
    }

}
