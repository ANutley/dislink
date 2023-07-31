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
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DisLinkMessageBuilder<M> {

    protected MessageSender<?, ?> sender;

    public DisLinkMessageBuilder(MessageSender<?, ?> sender) {
        this.sender = sender;
    }

    private String content = "";
    private final List<MessageEmbed> embeds = new ArrayList<>();
    private final Map<String, List<InputStream>> attachments = new HashMap<>();

    public abstract M build();

    public String content() {
        return content;
    }

    public void content(String content) {
        this.content = content;
    }

    public List<MessageEmbed> embeds() {
        return embeds;
    }

    public void addEmbed(MessageEmbed embed) {
        this.embeds.add(embed);
    }

    public Map<String, List<InputStream>> attachments() {
        return attachments;
    }

    public void addFile(String name, InputStream inputStream) {
        attachments.computeIfAbsent(name, n -> new ArrayList<>()).add(inputStream);
    }

}
