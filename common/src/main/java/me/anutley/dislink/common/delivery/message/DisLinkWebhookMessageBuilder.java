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

package me.anutley.dislink.common.delivery.message;

import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.anutley.dislink.common.delivery.sender.MessageSender;

public class DisLinkWebhookMessageBuilder extends DisLinkMessageBuilder<WebhookMessage> {

    public DisLinkWebhookMessageBuilder(MessageSender<?, ?> sender) {
        super(sender);
    }

    @Override
    public WebhookMessage build() {
        WebhookMessageBuilder webhookMessageBuilder = new WebhookMessageBuilder()
                .setUsername(sender.getPlaceholderReplacedMessage("messages.webhooks.username-format"))
                .setAvatarUrl(sender.getPlaceholderReplacedMessage("messages.webhooks.avatar-url"))
                .setContent(content());

        embeds().forEach(embed -> webhookMessageBuilder.addEmbeds(WebhookEmbedBuilder.fromJDA(embed).build()));

        attachments().forEach((name, attachments) ->
                attachments.forEach(inputStream ->
                        webhookMessageBuilder.addFile(name, inputStream)
                )
        );

        return webhookMessageBuilder.build();
    }

}
