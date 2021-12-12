package me.anutley.dislink.util;

import club.minnced.discord.webhook.send.AllowedMentions;
import me.anutley.dislink.Config;
import me.anutley.dislink.DisLink;
import me.anutley.dislink.objects.DisLinkChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.ArrayList;
import java.util.List;

public class ChannelUtil {

    public static List<DisLinkChannel> getDestinationChannels(TextChannel channel) {
        List<DisLinkChannel> channels = new ArrayList<>();

        for (CommentedConfigurationNode list : Config.get().node("Channels").self().childrenList()) {

            String channel1 = list.node("Channel1").getString();
            String channel2 = list.node("Channel2").getString();


            if (channel1 == null || channel2 == null) {
                DisLink.getLogger().error("A required channel1 / channel2 option is missing from the config file");
                return null;
            }

            AllowedMentions allowedMentions = new AllowedMentions();
            boolean parseUsers = list.node("Mentions").node("Users").getBoolean();
            boolean parseRoles = list.node("Mentions").node("Roles").getBoolean();
            boolean parseEveryones = list.node("Mentions").node("Everyone").getBoolean();
            allowedMentions.withParseUsers(parseUsers).withParseRoles(parseRoles).withParseEveryone(parseEveryones);

            boolean ignoreBots = list.node("IgnoreBots").getBoolean();
            boolean ignoreWebhooks = list.node("IgnoreWebhooks").getBoolean();
            boolean ignoreAttachments = list.node("IgnoreAttachments").getBoolean();
            boolean ignoreEmbeds = list.node("IgnoreEmbeds").getBoolean();

            if (channel1.equals(channel.getId()) && !channel2.equals(channel.getId())) {
                if (list.node("Direction").getString().equals("2")) return null;

                channels.add(
                        new DisLinkChannel(
                                DisLink.getJda().getTextChannelById(list.node("Channel2").getString()),
                                allowedMentions,
                                ignoreBots,
                                ignoreWebhooks,
                                ignoreAttachments,
                                ignoreEmbeds
                        ));
            }
            if (channel2.equals(channel.getId()) && !channel1.equals(channel.getId())) {
                if (list.node("Direction").getString().equals("1")) return null;
                channels.add(
                        new DisLinkChannel(
                                DisLink.getJda().getTextChannelById(list.node("Channel1").getString()),
                                allowedMentions,
                                ignoreBots,
                                ignoreWebhooks,
                                ignoreAttachments,
                                ignoreEmbeds
                        ));
            }
        }

        return channels;
    }
}
