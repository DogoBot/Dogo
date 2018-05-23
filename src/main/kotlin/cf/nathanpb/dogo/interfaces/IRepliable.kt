package cf.nathanpb.dogo.interfaces

import cf.nathanpb.dogo.core.DogoBot
import cf.nathanpb.dogo.lang.LanguageEntry
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import java.util.*

interface IRepliable {
    fun replyChannel() : MessageChannel
    fun lang() : String
    fun langEntry() : LanguageEntry

    fun reply(vararg content : Any, preset : Boolean = false){
        DogoBot.jdaOutputThread.submit {
            replySynk(content, preset)
        }
    }

    fun replySynk(vararg content : Any, preset : Boolean = false) : Message {
        var text = content[0].toString()
        if(preset){
            langEntry().getText(lang(), text)
        } else {
            text = String.format(text, Arrays.copyOfRange(content, 1, content.size-1))
        }
        return replyChannel().sendMessage(text).complete()
    }
}