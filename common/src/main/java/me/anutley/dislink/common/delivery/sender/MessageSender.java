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
import me.anutley.dislink.common.config.ChannelPairConfig.ChannelConfig;
import me.anutley.dislink.common.delivery.message.DisLinkMessageBuilder;
import me.anutley.dislink.common.delivery.message.mentions.AllowedMentionsBuilder;
import me.anutley.dislink.common.util.StringUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.utils.SplitUtil;
import net.dv8tion.jda.api.utils.SplitUtil.Strategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @param <D> the delivery method
 * @param <M> the message builder
 */
public abstract class MessageSender<D, M extends DisLinkMessageBuilder<?>> {

    public final DisLink disLink;
    public final ChannelPairConfig pairConfig;
    protected final ChannelConfig originChannel;
    protected final ChannelConfig destinationChannel;
    protected final Message message;

    public MessageSender(
            DisLink disLink,
            ChannelPairConfig pairConfig,
            ChannelConfig originChannel,
            ChannelConfig destinationChannel,
            Message message
    ) {
        this.disLink = disLink;
        this.pairConfig = pairConfig;
        this.originChannel = originChannel;
        this.destinationChannel = destinationChannel;
        this.message = message;
    }

    public enum Type {
        WEBHOOK,
        PLAINTEXT
    }


    /**
     * Prepares the necessary delivery to be ready to send/edit messages
     *
     * @return the fully prepared delivery method, or null if an error occurred
     */
    @Nullable
    public abstract CompletableFuture<D> prepare();

    /**
     * Sends a message synchronously to ensure message order is kept
     *
     * @param deliveryMethod the delivery method that it will be sent with
     * @param messageBuilder the message builder that it will be sent with
     * @return the id of the sent message
     */
    public abstract Long send(D deliveryMethod, M messageBuilder) throws Exception;

    /**
     * Edits the message asynchronously
     * @param deliveryMethod the delivery method that it will be edited with
     * @param messageId the id of the message you want to edit
     * @param messageBuilder the message builder that it will be edited with
     */
    public abstract void edit(D deliveryMethod, long messageId, M messageBuilder);

    /**
     * @return the delivery method specific message builder
     */
    public abstract M newMessageBuilder();

    /**
     * @return where the message format for this delivery method is stored in the config
     */
    public abstract String messageSettingKey();


    public @NotNull GuildMessageChannelUnion originChannel() {
        return Objects.requireNonNull(disLink.jda().getChannelById(GuildMessageChannelUnion.class, originChannel.channelId()));
    }

    public @NotNull GuildMessageChannelUnion destinationChannel() {
        return Objects.requireNonNull(disLink.jda().getChannelById(GuildMessageChannelUnion.class, destinationChannel.channelId()));
    }

    public void execute() {

        CompletableFuture<D> prepareFuture = prepare();

        if (prepareFuture == null) return;

        prepareFuture.whenCompleteAsync((d, throwable) -> { // this is used to get the webhook client

            if (d == null) {
                disLink.debug("For some reason this message delivery type is null, please report this on DisLink's GitHub/Discord server");
                return;
            }

            List<String> messages;

            try {
                messages = getMessagesSplit(getPlaceholderReplacedMessage(messageSettingKey()), Strategy.WHITESPACE);
            } catch (IllegalStateException exception) {
                messages = getMessagesSplit(getPlaceholderReplacedMessage(messageSettingKey()), Strategy.ANYWHERE);
            }

            if (StringUtil.isEmpty(message.getContentRaw()) && message.getEmbeds().size() == 0) { // only a file has been sent, needs slightly different logic
                sendOnlyFiles(d);
                return;
            }

            M messageBuilder = newMessageBuilder();

            for (int i = 0; i < messages.size(); i++) {

                if (i != (messages.size() - 1)) { // if it's not the final iteration of the split messages

                    messageBuilder.content(messages.get(i));

                    try {
                        send(d, messageBuilder);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else { // final split message

                    if (!StringUtil.isEmpty(message.getContentRaw())) {
                        messageBuilder.content(messages.get(i));
                    }

                    if (getEmbeds() != null) {
                        getEmbeds().forEach(messageBuilder::addEmbed);
                    }


                    long messageId;
                    try {
                        messageId = send(d, messageBuilder);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    editAttachments(messageBuilder, d, messageId);

                }
            }

            if (throwable != null) {
                disLink.logger().error("Error while preparing the message delivery type ", throwable);
            }

        }).whenCompleteAsync((m, e) -> e.printStackTrace());


    }

    private void sendOnlyFiles(D deliveryMethod) {

        M messageBuilder = newMessageBuilder();
        messageBuilder.content("\u200B"); // temporarily send an empty message while we edit the attachments in async

        long messageId;
        try {
            messageId = send(deliveryMethod, messageBuilder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        editAttachments(newMessageBuilder(), deliveryMethod, messageId);

    }

    private void editAttachments(M messageBuilder, D deliveryMethod, long messageId) {
        AtomicReference<Map<String, List<InputStream>>> attachments = new AtomicReference<>(new HashMap<>());

        if (getAttachments().size() == 0) return;

        CompletableFuture.runAsync(() -> {

            for (int j = 0; j < getAttachments().size(); j++) {
                Message.Attachment attachment = getAttachments().get(j);

                try {
                    attachments.get().computeIfAbsent(attachment.getFileName(), k -> new ArrayList<>()).add(
                            attachment.getProxy().download().get()
                    );
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }

            }

        }).whenCompleteAsync((ignored, error) -> {

            attachments.get().forEach((name, inputStreams) -> { // add each of the attachments
                inputStreams.forEach(inputStream -> messageBuilder.addFile(name, inputStream));
            });

            edit(deliveryMethod, messageId, messageBuilder);
            error.printStackTrace();
        });
    }


    public AllowedMentionsBuilder getAllowedMentions() {
        return new AllowedMentionsBuilder(
                disLink.settingsUtil().getBoolean(pairConfig, "mentions.everyone"),
                disLink.settingsUtil().getBoolean(pairConfig, "mentions.role"),
                disLink.settingsUtil().getBoolean(pairConfig, "mentions.user")
        );
    }

    protected List<MessageEmbed> getEmbeds() {
        if (disLink.settingsUtil().getBoolean(pairConfig, "ignore.embeds")) {
            if (message.getEmbeds().size() > 0) {
                disLink.debug("Embeds in the original message were found, but are not being forwarded due to being ignored");
            }
            return null;
        }

        return message.getEmbeds();
    }

    protected List<Message.Attachment> getAttachments() {

        if (disLink.settingsUtil().getBoolean(pairConfig, "ignore.attachments")) {
            if (message.getAttachments().size() > 0) {
                disLink.debug("Attachments in the original message were found, but are not being forwarded due to being ignored");
            }
            return Collections.emptyList();
        }

        return new ArrayList<>(message.getAttachments());
    }


    public List<String> getMessagesSplit(String formattedMessage, Strategy strategy) throws IllegalStateException {
        return SplitUtil.split(formattedMessage, Message.MAX_CONTENT_LENGTH, strategy);
    }

    public String getPlaceholderReplacedMessage(String settingKey) {
        return replaceDefaultMessagePlaceholders(
                disLink.settingsUtil().getString(
                        pairConfig,
                        settingKey
                )
        );
    }


    protected String replaceDefaultMessagePlaceholders(String format) {

        User author = message.getAuthor();

        format = format.replaceAll("%author_username%", author.getName())
                .replaceAll("%author_displayname%", author.getGlobalName() != null ? author.getGlobalName() : author.getName())
                .replaceAll("%author_id%", author.getId())
                .replaceAll("%author_avatar%", author.getEffectiveAvatarUrl())
                .replaceAll("%author_mention%", author.getAsMention());

        Member member = message.getMember();
        boolean memberNull = member == null;

        Role topRole = null;

        if (!memberNull) {
            try {
                topRole = member.getRoles().get(0);
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        format = format.replaceAll("author_nickname", memberNull ? author.getName() : member.getEffectiveName())
                .replaceAll("author_guild_avatar", memberNull ? author.getEffectiveAvatarUrl() : member.getEffectiveAvatarUrl())
                .replaceAll("author_toprole_name", topRole == null ? "" : topRole.getName())
                .replaceAll("author_toprole_mention", topRole == null ? "" : topRole.getAsMention());


        format = replaceChannelPlaceholders(format, "origin", originChannel());
        format = replaceChannelPlaceholders(format, "destination", destinationChannel());

        format = replaceGuildPlaceholders(format, "origin", originChannel());
        format = replaceGuildPlaceholders(format, "destination", destinationChannel());

        format = format.replaceAll("%message%", message.getContentRaw());

        Matcher match = Pattern.compile("%.+%").matcher(format);

        if (match.find()) {
            disLink.debug("There is an invalid placeholder in the format \"" + format + "\"");
        }

        return format;
    }

    public String replaceChannelPlaceholders(String format, String prefix, GuildChannel channel) {
        return format.replaceAll("%" + prefix + "_channel_name%", channel.getName())
                .replaceAll("%" + prefix + "_channel_id%", channel.getId())
                .replaceAll("%" + prefix + "_channel_mention%", channel.getAsMention());
    }

    public String replaceGuildPlaceholders(String format, String prefix, GuildChannel channel) {

        String guildIcon = channel.getGuild().getIconUrl();

        return format.replaceAll("%" + prefix + "_guild_name%", channel.getGuild().getName())
                .replaceAll("%" + prefix + "_guild_id%", channel.getGuild().getId())
                .replaceAll("%" + prefix + "_guild_iconurl%", guildIcon == null ? "" : guildIcon);
    }
}
