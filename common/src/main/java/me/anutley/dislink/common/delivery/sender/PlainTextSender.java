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

import me.anutley.dislink.common.DisLink;
import me.anutley.dislink.common.config.ChannelPairConfig;
import me.anutley.dislink.common.delivery.message.DisLinkPlainTextMessageBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.concurrent.CompletableFuture;

public class PlainTextSender extends MessageSender<JDA, DisLinkPlainTextMessageBuilder> {

    public PlainTextSender(DisLink disLink, ChannelPairConfig pairConfig, ChannelPairConfig.ChannelConfig originChannel, ChannelPairConfig.ChannelConfig destinationChannel, Message message) {
        super(disLink, pairConfig, originChannel, destinationChannel, message);
    }

    @Override
    public CompletableFuture<JDA> prepare() {
        return message.getAuthor().getId().equals(disLink.jda().getSelfUser().getId()) ? // if the message was from us, we don't want to forward it
                CompletableFuture.completedFuture(null) :
                CompletableFuture.completedFuture(disLink.jda());
    }

    @Override
    public Long send(JDA deliveryMethod, DisLinkPlainTextMessageBuilder messageBuilder) throws ErrorResponseException {
        return destinationChannel().sendMessage(messageBuilder.build()).complete().getIdLong();
    }

    @Override
    public void edit(JDA deliveryMethod, long messageId, DisLinkPlainTextMessageBuilder messageBuilder) {
        destinationChannel().editMessageById(
                        messageId,
                        MessageEditData.fromCreateData(messageBuilder.build())
                ).submit()
                .thenApplyAsync(ISnowflake::getIdLong);
    }

    @Override
    public DisLinkPlainTextMessageBuilder newMessageBuilder() {
        return new DisLinkPlainTextMessageBuilder(this);
    }

    @Override
    public String messageSettingKey() {
        return "messages.plaintext.message-format";
    }

}
