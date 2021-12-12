package me.anutley.dislink.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import me.anutley.dislink.DisLink;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;

public class WebhookUtil {

    public static WebhookClient createOrGetWebhook(TextChannel channel) {

        if (!channel.getGuild().retrieveMemberById(DisLink.getJda().getSelfUser().getId()).complete().hasPermission(Permission.MANAGE_WEBHOOKS)) {
            DisLink.debug("The dislink bot doesn't have the permission MANAGE_WEBHOOKS! This is a required permission, and dislink will not be able to function without it");
            return null;
        }

        Webhook webhook = null;
        try {
            webhook =
                    channel.retrieveWebhooks().complete().stream()
                            .filter(hook -> hook.getName().equals("Dislink Webhook"))
                            .findFirst().orElse(null);

            if (webhook == null) {
                webhook = channel.createWebhook("Dislink Webhook").complete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new WebhookClientBuilder(webhook.getUrl()).build();
    }

}
