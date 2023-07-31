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

package me.anutley.dislink.common.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SettingsConfig {

    @Comment("""
            Available placeholders can be found in the GitHub README file
            """)
    private MessagesConfig messages = new MessagesConfig();

    @Comment("")
    private IgnoreConfig ignore = new IgnoreConfig();

    @Comment("")
    private MentionsConfig mentions = new MentionsConfig();

    @ConfigSerializable
    public static class MessagesConfig {

        @Comment("Settings relating the webhook delivery method")
        private WebhookConfig webhooks = new WebhookConfig();

        @Comment("Settings relating the webhook plaintext method")
        private PlainTextConfig plaintext = new PlainTextConfig();

        @ConfigSerializable
        public static class WebhookConfig {

            @Comment("")
            private String usernameFormat = "[DisLink] %author_displayname%";

            @Comment("")
            private String messageFormat = "%message%";

            @Comment("")
            private String avatarUrl = "%author_avatar%";

        }

        @ConfigSerializable
        public static class PlainTextConfig {

            @Comment("")
            private String messageFormat = "%author_displayname% > %message%";

        }

    }

    @ConfigSerializable
    public static class IgnoreConfig {

        @Comment("Whether messages sent from bots should be ignored")
        boolean bots = false;

        @Comment("Whether messages sent from webhooks should be ignored")
        boolean webhooks = false;

        @Comment("Whether embeds should be ignored")
        boolean embeds = false;

        @Comment("Whether attachments from bots should be ignored")
        boolean attachments = false;

    }

    @ConfigSerializable
    public static class MentionsConfig {

        @Comment("Whether @user mentions should be parsed")
        boolean user = true;

        @Comment("Whether @role mentions should be parsed. WARNING: enabling this will allow users to ping any role using the bridge")
        boolean role = false;

        @Comment("Whether @everyone should be parsed. WARNING: enabling this will allow users to ping @everyone using the bridge")
        boolean everyone = false;

    }

}
