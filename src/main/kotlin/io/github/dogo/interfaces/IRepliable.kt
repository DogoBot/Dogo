package io.github.dogo.interfaces

import io.github.dogo.lang.LanguageEntry
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.entities.MessageEmbed
import java.util.*

interface IRepliable {
    fun replyChannel() : MessageChannel
    fun lang() : String
    fun langEntry() : LanguageEntry

    fun reply(vararg content : Any, preset : Boolean = false){
        io.github.dogo.core.DogoBot.jdaOutputThread.submit {
            replySynk(*content, preset = preset)
        }
    }

    fun replySynk(vararg content : Any, preset : Boolean = false) : Message {
        return if(content[0] is MessageEmbed) {
            replyChannel().sendMessage(content[0] as MessageEmbed).complete()
        } else {
            var text = content[0].toString()

            replyChannel().sendMessage(when {
                preset -> langEntry().getText(lang(), text, *Arrays.copyOfRange(content, 1, content.size))
                content.size > 1 -> String.format(text, *Arrays.copyOfRange(content, 1, content.size - 1))
                else -> text
            }).complete()
        }
    }
}