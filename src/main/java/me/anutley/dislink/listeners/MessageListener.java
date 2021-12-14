package me.anutley.dislink.listeners;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.anutley.dislink.DisLink;
import me.anutley.dislink.objects.DisLinkChannel;
import me.anutley.dislink.util.ChannelUtil;
import me.anutley.dislink.util.MessageUtil;
import me.anutley.dislink.util.WebhookUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (ChannelUtil.getDestinationChannels(event.getChannel()) == null) {
            DisLink.debug("The destination channel was null, aborting!");
            return;
        }

        TextChannel startingChannel = event.getChannel();

        for (DisLinkChannel channel : ChannelUtil.getDestinationChannels(event.getChannel())) {

            if (event.isWebhookMessage() && channel.isIgnoreWebhooks()) {
                DisLink.debug("Received a webhook message, however webhook messages for this link are disabled!");
                continue;
            }

            if (event.getAuthor().isBot() && channel.isIgnoreBots()) {
                DisLink.debug("Received a bot message, however bot messages for this link are disabled!");
                continue;
            }

            if (event.isWebhookMessage()) {
                Webhook webhook = startingChannel.retrieveWebhooks().complete().stream()
                        .filter(hook -> hook.getName().equals("Dislink Webhook"))
                        .findFirst().orElse(null);

                if (webhook != null) {
                    if (webhook.getId().equals(event.getAuthor().getId())) return;
                }
            }

            WebhookClient webhookClient = WebhookUtil.createOrGetWebhook(channel.getChannel());

            if (webhookClient == null) return;

            List<String> messageList = MessageUtil.splitMessage(event.getMessage());
            int count = 0;

            for (String message : messageList) {
                count++;
                WebhookMessageBuilder webhookMessageBuilder = new WebhookMessageBuilder()
                        .setContent(message)
                        .setAvatarUrl(event.getAuthor().getAvatarUrl())
                        .setAllowedMentions(channel.getAllowedMentions());

                if (event.isWebhookMessage())
                    webhookMessageBuilder.setUsername(event.getAuthor().getName() + " [Webhook]");
                else if (event.getAuthor().isBot())
                    webhookMessageBuilder.setUsername(event.getAuthor().getAsTag() + " [Bot]");
                else
                    webhookMessageBuilder.setUsername(event.getAuthor().getAsTag());

                if (count == messageList.size()) {
                    for (Message.Attachment attachment : event.getMessage().getAttachments()) {

                        if (channel.isIgnoreAttachments()) {
                            DisLink.debug("Received an attachment, however attachments for this link are disabled");
                            break;
                        }

                        try {
                            InputStream inputStream = attachment.retrieveInputStream().get();
                            webhookMessageBuilder.addFile(attachment.getFileName(), inputStream);
                            inputStream.close();
                        } catch (Throwable t) {
                            DisLink.debug("Failed to copy attachment " + attachment.getFileName());
                        }
                    }
                    for (MessageEmbed embed : event.getMessage().getEmbeds()) {

                        if (channel.isIgnoreEmbeds()) {
                            DisLink.debug("Received an embed, however embeds for this link are disabled");
                            break;
                        }
                        webhookMessageBuilder.addEmbeds(WebhookEmbedBuilder.fromJDA(embed).build());
                    }
                }
                webhookClient.send(webhookMessageBuilder.build());
            }
        }
    }

}
