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

package me.anutley.dislink.common.delivery.sender;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.AllowedMentions;
import me.anutley.dislink.common.DisLink;
import me.anutley.dislink.common.config.ChannelPairConfig;
import me.anutley.dislink.common.config.ChannelPairConfig.ChannelConfig;
import me.anutley.dislink.common.config.ChannelsConfig;
import me.anutley.dislink.common.delivery.message.DisLinkWebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;

public class WebhookSender extends MessageSender<WebhookClientBuilder, DisLinkWebhookMessageBuilder> {

    private final String DISLINK_BRIDGE_WEBHOOK_NAME = "Dislink Bridge Webhook";
    public WebhookSender(DisLink disLink, ChannelPairConfig pairConfig, ChannelConfig originChannel, ChannelConfig destinationChannel, Message message) {
        super(disLink, pairConfig, originChannel, destinationChannel, message);
    }

    @Override
    public CompletableFuture<WebhookClientBuilder> prepare() {

        Matcher webhookMatcher = Webhook.WEBHOOK_URL.matcher(originChannel.webhookUrl());
        User author = message.getAuthor();

        if (webhookMatcher.matches()) {
            if (webhookMatcher.group("id").equals(author.getId())) {
                return null;
            }
        }

        IWebhookContainer destChannel = destinationChannel().getType().isThread() ?
                (IWebhookContainer) destinationChannel().asThreadChannel().getParentChannel() :
                (IWebhookContainer) destinationChannel();

        return destChannel.retrieveWebhooks().submit().thenApplyAsync(
                webhooks -> {
                    Matcher matcher = Webhook.WEBHOOK_URL.matcher(destinationChannel.webhookUrl());

                    Optional<Webhook> webhookOptional = webhooks.stream().filter(w -> matcher.matches())
                            .filter(w -> Objects.equals(w.getToken(), matcher.group("token")))
                            .findAny(); // find the webhook listed in the config

                    if (webhookOptional.isPresent()) {

                        if (disLink.configManager().mainConfig().cleanUpOldWebhooks()) {
                            webhooks.stream() // clean up orphaned webhooks
                                    .filter(webhook -> webhook.getName().equals(DISLINK_BRIDGE_WEBHOOK_NAME))
                                    .filter(webhook -> !webhook.getUrl().equals(webhookOptional.get().getUrl()))
                                    .forEach(webhook -> webhook.delete().queue());
                        }

                        return createWebhookClientBuilder(destinationChannel.webhookUrl());
                    } else { // none were found we have to create our own

                        if (disLink.configManager().mainConfig().cleanUpOldWebhooks()) {
                            webhooks.stream() // clean up orphaned webhooks
                                    .filter(webhook -> webhook.getName().equals(DISLINK_BRIDGE_WEBHOOK_NAME))
                                    .forEach(webhook -> webhook.delete().queue());
                        }

                        if (!disLink.configManager().mainConfig().createWebhooksAutomatically()) {
                            disLink.debug("No webhooks were found, but auto creating webhooks is disabled in the config, aborting message forwarding for " + message);
                            return null;
                        }

                        try {
                            Webhook webhook = destChannel.createWebhook(DISLINK_BRIDGE_WEBHOOK_NAME).complete();
                            destinationChannel.webhookUrl(webhook.getUrl());
                            disLink.configLoader().saveConfig("channels.conf", disLink.configLoader().channelNode(), ChannelsConfig.class, disLink.configManager().channelsConfig());

                            return createWebhookClientBuilder(webhook.getUrl());
                        } catch (ErrorResponseException errorResponseException) {

                            ErrorResponse errorResponse = errorResponseException.getErrorResponse();

                            String message = "";

                            if (errorResponse == ErrorResponse.MISSING_PERMISSIONS) {
                                message = "DisLink does not have the sufficient permissions to create a webhook, please give DisLink the MANAGE_WEBHOOKS permission";
                            } else if (errorResponse == ErrorResponse.MISSING_ACCESS) {
                                message = "DisLink does not have the sufficient access to view this channel, so cannot create a webhook";
                            } else if (errorResponse == ErrorResponse.MAX_WEBHOOKS) {
                                message = "This channel has reached the maximum amount of webhooks, please delete some and try again";
                            }

                            disLink.debug(message + ", aborting message forwarding for " + message);
                            errorResponseException.printStackTrace();
                            return null;
                        }
                    }
                }
        );
    }

    @Override
    public Long send(WebhookClientBuilder deliveryMethod, DisLinkWebhookMessageBuilder webhookMessageBuilder) throws ExecutionException, InterruptedException {
        if (destinationChannel().getType().isThread())
            deliveryMethod.setThreadId(Long.parseLong(destinationChannel.channelId()));

        return deliveryMethod.build()
                .send(webhookMessageBuilder.build())
                .get().getId();
    }

    @Override
    public void edit(WebhookClientBuilder deliveryMethod, long messageId, DisLinkWebhookMessageBuilder messageBuilder) {
        deliveryMethod.build()
                .edit(messageId, messageBuilder.build())
                .thenApplyAsync(ReadonlyMessage::getId);
    }


    @Override
    public DisLinkWebhookMessageBuilder newMessageBuilder() {
        return new DisLinkWebhookMessageBuilder(this);
    }

    @Override
    public String messageSettingKey() {
        return "messages.webhooks.message-format";
    }

    private WebhookClientBuilder createWebhookClientBuilder(String webhookUrl) {
        return new WebhookClientBuilder(webhookUrl)
                .setAllowedMentions(allowedMentions());
    }

    public AllowedMentions allowedMentions() {
        return AllowedMentions.none()
                .withParseEveryone(getAllowedMentions().users())
                .withParseRoles(getAllowedMentions().roles())
                .withParseUsers(getAllowedMentions().everyone());
    }

}
