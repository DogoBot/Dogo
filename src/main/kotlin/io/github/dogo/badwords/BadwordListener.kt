package io.github.dogo.badwords

import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.core.eventBus.EventBus
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.lang.StringBuilder

class BadwordListener {

    @EventBus.Listener
    fun listenSend(e: GuildMessageReceivedEvent) = check(e.message)

    fun check(msg: Message) {
        DogoGuild(msg.guild).let { guild ->
            val bw = guild.badwords.badwords
            val user = DogoUser(msg.author)

            if (!user.getPermGroups().can("badword.bypass")) {
                val newmsg = msg.contentDisplay.split(" ")
                        .map { word ->
                            if(bw.any { it.equals(word, ignoreCase = true) }){
                                val sw = StringBuilder("``")
                                for(i in 0..word.length) sw.append("*")
                                sw.toString()+"``"
                            } else word
                        }
                        .joinToString(" ")
                if (!newmsg.equals(msg.contentDisplay)) {
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