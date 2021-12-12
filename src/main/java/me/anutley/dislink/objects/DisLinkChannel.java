package me.anutley.dislink.objects;

import club.minnced.discord.webhook.send.AllowedMentions;
import net.dv8tion.jda.api.entities.TextChannel;

public class DisLinkChannel {

    private final TextChannel channel;
    private final AllowedMentions allowedMentions;
    private final boolean ignoreBots;
    private final boolean ignoreWebhooks;
    private final boolean ignoreAttachments;
    private final boolean ignoreEmbeds;

    public DisLinkChannel(TextChannel channel, AllowedMentions allowedMentions, boolean ignoreBots, boolean ignoreWebhooks, boolean ignoreAttachments, boolean ignoreEmbeds) {
        this.channel = channel;
        this.allowedMentions = allowedMentions;
        this.ignoreBots = ignoreBots;
        this.ignoreWebhooks = ignoreWebhooks;
        this.ignoreAttachments = ignoreAttachments;
        this.ignoreEmbeds = ignoreEmbeds;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public AllowedMentions getAllowedMentions() {
        return allowedMentions;
    }

    public boolean isIgnoreBots() {
        return ignoreBots;
    }

    public boolean isIgnoreWebhooks() {
        return ignoreWebhooks;
    }

    public boolean isIgnoreAttachments() {
        return ignoreAttachments;
    }

    public boolean isIgnoreEmbeds() {
        return ignoreEmbeds;
    }

}
