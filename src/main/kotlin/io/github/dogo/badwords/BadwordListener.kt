package io.github.dogo.badwords

import io.github.dogo.core.DogoBot
import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.core.eventBus.EventBus
import io.github.dogo.events.badword.BadwordMessageCensoredEvent
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.Permission
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

    companion object {
        val echos = mutableMapOf<Message, String>()
    }

    /**
     * Listens the events.
     *
     * @param[e] the event, sent from [EventBus]
     */
    fun listenSend(e: GuildMessageReceivedEvent) = check(e.message)

    /**
     * Checks if the guild's message has a badword and process it.
     *
     * @param[msg] the message.
     */
    fun check(msg: Message) {
        val member = msg.guild.getMember(DogoBot.jda!!.selfUser)
        if(!member.hasPermission(Permission.MESSAGE_WRITE) || !member.hasPermission(Permission.MESSAGE_MANAGE)){
            return
        }

        val guild = DogoGuild(msg.guild)
        val user = DogoUser(echos[msg]?.also { echos.remove(msg) } ?: msg.author.id)
        if (user.id != DogoBot.jda!!.selfUser.id && !user.getPermGroups().can("badword.bypass")) {
            val container = mutableListOf<String>()
            val newmsg = msg.contentDisplay.replaceBadwords(guild.badwords.badwords, container)
            if (container.isNotEmpty()) {
                DogoBot.eventBus.submit(BadwordMessageCensoredEvent(guild, msg, container))
                DogoBot.jdaOutputThread.execute {
                    msg.channel.sendMessage(newmsg.createEmbed(user).build()).complete()
                    msg.delete().complete()
                }
            }
        }
    }

    /**
     * Replaces the badwords in a String
     *
     * @param[badwords] the badwords.
     * @param[container] an empty list. It will be filled with the replaced words.
     *
     * @return the string replaced.
     */
    fun String.replaceBadwords(badwords: List<String>, container: MutableList<String>): String {
        return this.split(" ")
                .filter { it.isNotEmpty() }
                .map { word ->
                    if(badwords.any { word.contains(it, ignoreCase = true) }){
                        container.add(badwords.first { word.contains(it, ignoreCase = true) })
                        val sw = StringBuilder("``")
                        for(i in 0..word.length) sw.append("*")
                        sw.toString()+"``"
                    } else word
                }.joinToString(" ")
    }

    /**
     * Creates a embed with the following message
     *
     * @param[sender] the sender.
     *
     * @return the embed builder.
     */
    fun String.createEmbed(sender: DogoUser): EmbedBuilder {
        return EmbedBuilder()
                .setAuthor("${sender.formatName()} said", null, sender.usr?.effectiveAvatarUrl.orEmpty())
                .setColor(Color.YELLOW)
                .setDescription(this)
    }
}