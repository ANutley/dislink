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
public class MainConfig {

    @Comment("The token of the Discord bot application you want DisLink to use")
    private String botToken = "";

    @Comment("Whether webhooks should be automatically created and saved when their is either no webhook set or it is invalid")
    private boolean createWebhooksAutomatically = true;

    @Comment("Whether old, unused DisLink webhooks should be cleaned up")
    private boolean cleanUpOldWebhooks = true;

    @Comment("Enables the output of debug messages, this should only be enabled if you are debugging a problem")
    private boolean debug = true;

    public String botToken() {
        return botToken;
    }

    public boolean createWebhooksAutomatically() {
        return createWebhooksAutomatically;
    }

    public boolean cleanUpOldWebhooks() {
        return cleanUpOldWebhooks;
    }

    public boolean debug() {
        return debug;
    }
}
