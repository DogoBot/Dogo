package io.github.dogo.menus

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.CommandContext
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

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
 * Menu to reads text messages from user.
 * @inheritDoc
 *
 * @param[question] anything to ask to user.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class TextInputMenu(val cnt: CommandContext, val onResponse: (String)->Unit) : SimpleReactionMenu(cnt){

    /**
     * @inheritDoc
     * Also register new listeners.
     */
    override fun send() {
        DogoBot.eventBus.register(this::onMessageText)
        super.send()
    }

    /**
     * @inheritDoc
     * Also unregisters new listeners.
     */
    override fun end(delete: Boolean) {
        DogoBot.eventBus.unregister(this::onMessageText)
        super.end(delete)
    }

    /**
     * Listens to events.
     */
    fun onMessageText(e: MessageReceivedEvent){
        if(e.message.textChannel.id == cnt.replyChannel.id && target == e.author.id && e.message.contentRaw.isNotEmpty()){
            onResponse(e.message.contentRaw)
        }
    }
}