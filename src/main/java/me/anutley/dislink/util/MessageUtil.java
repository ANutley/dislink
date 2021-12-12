package me.anutley.dislink.util;

import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageUtil {

    public static List<String> splitMessage(Message message) {
        List<String> messages = new ArrayList<>();

        String content = message.getContentRaw();

        int messageLimit = Message.MAX_CONTENT_LENGTH;
        int nitroMessageLimit = Message.MAX_CONTENT_LENGTH * 2;

        if (content.length() > Message.MAX_CONTENT_LENGTH) {
            // first half
            messages.add(content.substring(0, messageLimit));
            // second half
            messages.add(content.substring(messageLimit, Math.min(content.length(), nitroMessageLimit)));
            if (content.length() > nitroMessageLimit)
                // extra
                messages.add(content.substring(nitroMessageLimit));
        } else {
            messages.add(content);
        }
        return messages;
    }

}