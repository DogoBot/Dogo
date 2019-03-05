package io.github.dogo.discord.badwords

import io.github.dogo.core.database.Tables
import io.github.dogo.core.DogoBot
import io.github.dogo.core.eventBus.EventBus
import io.github.dogo.discord.DiscordManager
import io.github.dogo.discord.badwords
import io.github.dogo.discord.formatName
import io.github.dogo.discord.permgroups
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.transaction
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
        val echos = mutableMapOf<Message, User>()
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

        //Badwords cannot exists outside guilds!
        if(msg.guild == null){
            return
        }

        //Checks if Dogo has permission to WRITE and DELETE messages
        val dogo = msg.guild.getMember(DiscordManager.jda!!.selfUser)
        if(!dogo.hasPermission(Permission.MESSAGE_WRITE) || !dogo.hasPermission(Permission.MESSAGE_MANAGE)){
            return
        }

        //If the message is found on [echos], the author is its value (to don't blame Dogo if anyone say shitty).
        //Also removes it from echos to let the object be garbage collected.
        //Else, the author is the [msg] author.
        val user = echos[msg]?.also { echos.remove(msg) } ?: msg.author
        if(!user.permgroups.can("badword.admin.bypass")){

        }

        echos.remove(msg)
        if (msg.author.id != DiscordManager.jda!!.selfUser.id && !msg.author.permgroups.can("badword.admin.bypass")) {

            val container = mutableListOf<String>() //will be stored all the changed words
            val newmsg = msg.contentDisplay.replaceBadwords(msg.guild.badwords, container)

            if (container.isNotEmpty()) {
                //Sends the event to EventBus
                DogoBot.eventBus.submit(BadwordMessageCensoredEvent(msg.guild, msg, container))

                //stores the punishment on db
                suspend {
                    transaction {
                        Tables.BADWORDS.slice(Tables.BADWORDS.id).select {
                            (Tables.BADWORDS.guild eq msg.guild.id) and (Tables.BADWORDS.word inList container)
                        }.map { it[Tables.BADWORDS.id] }.forEach { badwordId ->
                            Tables.BADWORDPUNISHMENT.insert {
                                it[this.user] = msg.author.id
                                it[this.badword] = badwordId
                            }
                        }
                    }
                }

                //reply the new message
                DiscordManager.jdaOutputThread.execute {
                    msg.channel.sendMessage(newmsg.createEmbed(msg.author, msg.guild).build()).complete()
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
    @SuppressWarnings("private")
    fun String.replaceBadwords(badwords: List<String>, container: MutableList<String>): String {
        return this.split(" ")
            .filter { it.isNotEmpty() }
            .joinToString(" ") { word ->
                if(badwords.any { word.contains(it, ignoreCase = true) }){
                    container.add(badwords.first { word.contains(it, ignoreCase = true) })
                    val sw = StringBuilder("``")
                    for(i in 0..word.length) sw.append("*")

                    "$sw``" //returned statement
                } else word
            }
    }

    /**
     * Creates a embed with the following message
     *
     * @param[sender] the sender.
     *
     * @return the embed builder.
     */
    @SuppressWarnings("private")
    fun String.createEmbed(sender: User, guild: Guild): EmbedBuilder {
        return EmbedBuilder()
                .setAuthor("${sender.formatName(guild)} said", null, sender.effectiveAvatarUrl.orEmpty())
                .setColor(Color.YELLOW)
                .setDescription(this)
    }
}