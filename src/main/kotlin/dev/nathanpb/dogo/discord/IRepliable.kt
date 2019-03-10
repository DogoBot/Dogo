package dev.nathanpb.dogo.discord

import dev.nathanpb.dogo.lang.BoundLanguage
import dev.nathanpb.dogo.lang.LanguageEntry
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.entities.MessageEmbed
import java.util.*

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
 * Interface used to create "repliable" objects on Discord text channels.
 *
 * @author NathanPB
 * @since 3.1.0
 */
interface IRepliable {

    /**
     * The channel to reply.
     */
    val replyChannel : MessageChannel

    /**
     * The language entry to get the text.
     */
    val langEntry : BoundLanguage

    /**
     * Asynchronous reply.
     *
     * @param[content] the content to reply. The first one should be a [String] or [MessageEmbed]. If it isn't, it is converted to [String] using [Object.toString]. The other arguments are values to be used on [String formatting][String.format].
     * @param[preset] if true, means that the first argument of [content] is a entry on [LanguageEntry], and will reply with its text.
     */
    fun reply(vararg content : Any, preset : Boolean = false){
        DiscordManager.jdaOutputThread.submit {
            replySynk(*content, preset = preset)
        }
    }

    /**
     * Synchronous reply.
     *
     * @param[content] the content to reply. The first one should be a [String] or [MessageEmbed]. If it isn't, it is converted to [String] using [Object.toString]. The other arguments are values to be used on [String formatting][String.format].
     * @param[preset] if true, means that the first argument of [content] is a entry on [LanguageEntry], and will reply with its text.
     *
     * @return the replied message.
     */
    fun replySynk(vararg content : Any, preset : Boolean = false) : Message {
        return if(content[0] is MessageEmbed) {
            replyChannel.sendMessage(content[0] as MessageEmbed).complete()
        } else {
            val text = content[0].toString()

            replyChannel.sendMessage(when {
                preset -> langEntry.getText(text, *Arrays.copyOfRange(content, 1, content.size))
                content.size > 1 -> String.format(text, *Arrays.copyOfRange(content, 1, content.size - 1))
                else -> text
            }).complete()
        }
    }
}