package io.github.dogo.badwords

import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.core.eventBus.EventBus
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color

/*
Copyright 2019 Nathan Bombana

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/**
 * Simple class to listen the [GuildMessageReceivedEvent] from JDA and check its messages.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class BadwordListener {

    /**
     * Listens the events.
     *
     * @param[e] the event, sent from [EventBus]
     */
    @EventBus.Listener
    fun listenSend(e: GuildMessageReceivedEvent) = check(e.message)

    /**
     * Checks if the guild's message has a badword and process it.
     *
     * @param[msg] the message.
     */
    fun check(msg: Message) {
        DogoGuild(msg.guild).let { guild ->
            val bw = guild.badwords.badwords
            val user = DogoUser(msg.author)

            if (!user.getPermGroups().can("badword.bypass")) {
                val newmsg = msg.contentDisplay.split(" ")
                        .filter { it.isNotEmpty() }
                        .map { word ->
                            if(bw.any { it.contains(word, ignoreCase = true) }){
                                val sw = StringBuilder("``")
                                for(i in 0..word.length) sw.append("*")
                                sw.toString()+"``"
                            } else word
                        }.joinToString(" ")
                if (newmsg != msg.contentDisplay) {
                    msg.channel.sendMessage(
                            EmbedBuilder()
                                    .setAuthor("${user.formatName()} said", null, user.usr?.effectiveAvatarUrl.orEmpty())
                                    .setColor(Color.YELLOW)
                                    .setDescription(newmsg)
                                    .build()
                    ).queue()
                    msg.delete().queue()
                }
            }
        }
    }
}