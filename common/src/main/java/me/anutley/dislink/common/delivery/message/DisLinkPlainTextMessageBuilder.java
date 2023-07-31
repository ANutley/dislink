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

import me.anutley.dislink.common.delivery.sender.MessageSender;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.Collection;

public class DisLinkPlainTextMessageBuilder extends DisLinkMessageBuilder<MessageCreateData> {

    public DisLinkPlainTextMessageBuilder(MessageSender<?, ?> sender) {
        super(sender);
    }

    @Override
    public MessageCreateData build() {
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
                .setAllowedMentions(allowedMentions())
                .setContent(content());

        embeds().forEach(messageBuilder::addEmbeds);

        attachments().forEach((name, attachments) ->
                attachments.forEach(inputStream ->
                        messageBuilder.addFiles(FileUpload.fromData(inputStream, name))
                )
        );

        return messageBuilder.build();
    }

    public Collection<Message.MentionType> allowedMentions() {
        ArrayList<Message.MentionType> mentionTypes = new ArrayList<>();

        if (sender.getAllowedMentions().users()) mentionTypes.add(Message.MentionType.USER);
        if (sender.getAllowedMentions().roles()) mentionTypes.add(Message.MentionType.ROLE);
        if (sender.getAllowedMentions().everyone()) mentionTypes.add(Message.MentionType.EVERYONE);

        return mentionTypes;
    }
}
