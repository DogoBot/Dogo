package io.github.dogo.menus

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.CommandContext
import io.github.dogo.utils._static.EmoteReference
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.PrivateChannel
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent
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
 * Creates simple embed menus with reaction as buttons.
 *
 * @param[context] a command context to extract information like sender, channel, etc.
 *
 * @author NathanPB
 * @since 3.1.0
 */
open class SimpleReactionMenu(val context: CommandContext) {
    companion object {
        /**
         * The current running instances, used by timeout thread.
         * @see DogoBot.menuTimeWatcher
         */
        val instances = mutableListOf<SimpleReactionMenu>()
    }
    init {
        instances.add(this)
    }

    /**
     * The list of buttons on this menu.
     */
    protected var actions: ArrayList<Action> = ArrayList()

    /**
     * The embed to send.
     */
    var embed = EmbedBuilder()

    /**
     * The id from the to accept reactions.
     */
    var target = context.sender.id

    /**
     * The last menu sent.
     */
    var msg: Message? = null

    /**
     * The timeout (timestamp in millis).
     */
    var timeout = 0L

    /**
     * The time when the last message was sent or edited (timestamp in millis).
     */
    var lastSend = 0L

    /**
     * All the IDs registered on event bus for this instance.
     */
    protected var eventbusId : Array<Long> = emptyArray()

    /**
     * Builds the embed to send.
     *
     * @param[preset] the embed. If empty it will automatically build an embed with action descriptions.
     * @see Action.description
     *
     */
    fun build(preset : EmbedBuilder? = null) {
        if(preset == null) {
            actions.forEach {
                embed.appendDescription("${it.emote.getAsMention()} ${it.description}\n")
            }
        } else this.embed = preset

    }

    /**
     * Ends a menu. It can be resendable.
     *
     * @param[delete] deletes the message if true. All is relative to the bot permissions on guild.
     */
    open fun end(delete : Boolean = true) {
        DogoBot.eventBus.unregister(this::onReact)
        instances.remove(this)

        val member = context.guild?.g?.getMember(DogoBot.jda!!.selfUser)
        if(context.guild?.g != null && member?.hasPermission(Permission.MESSAGE_MANAGE) == true){
            if(delete) {
                msg?.delete()?.queue()
            } else {
                msg?.clearReactions()?.queue()
            }
        }
        msg = null
    }

    /**
     * Sends the [embed] and add its reactions. Also remove the old ones.
     *
     * Edit the message if it already exists.
     */
    open fun send() {
        DogoBot.eventBus.register(this::onReact)
        msg = if(msg == null) {
            context.replySynk(embed.build())
        } else {
            msg!!.editMessage(embed.build()).queue()
            msg!!.channel.getMessageById(msg!!.id).complete()
        }

        msg!!.reactions
                .filter { !actions.any { ac -> ac.emote.id == if(it.reactionEmote.isEmote) it.reactionEmote.id else it.reactionEmote.name } }
                .forEach { it.removeReaction().queue() }

        val presentEmotes = msg!!.reactions.filter { r -> r.users.any { it.id == DogoBot.jda!!.selfUser.id } }
                .map { if(it.reactionEmote.isEmote) it.reactionEmote.id else it.reactionEmote.name}
        actions
                .filter { !presentEmotes.contains(it.emote.id) }
                .forEach {
                    msg!!.addReaction(it.emote.id).queue()
                }
        this.lastSend = System.currentTimeMillis()
    }

    /**
     * Adds a action to the button list.
     * @see actions
     */
    fun addAction(emote: EmoteReference, description: String, action: () -> Unit) {
        if (!actions.stream().anyMatch { a -> a.emote == emote }) {
            actions.add( Action(action, description, emote))
        } else throw Exception("Action already exists")
    }

    /**
     * Removes a action from the button list.
     * @see actions
     */
    fun removeAction(emote: EmoteReference) = actions.removeAll { it.emote == emote }


    /**
     * Listen to reactions and process it.
     */
    fun onReact(e: GenericMessageReactionEvent) {
        msg?.let {
            if(e.messageId == it.id){
                if(e.user.id != DogoBot.jda!!.selfUser.id && e is MessageReactionAddEvent && e.channel !is PrivateChannel){
                    e.reaction.removeReaction(e.user).queue()
                    if(e.user.id != target) return
                }
            }
            if(e is MessageReactionAddEvent || (e is MessageReactionRemoveEvent && e.channel is PrivateChannel)) {
                if (e.messageId == it.id && e.user.id == target) {
                    for (ac in actions) {
                        if (e.reactionEmote.name == ac.emote.id) {
                            ac.action()
                            break
                        }
                    }
                }
            }
        }
    }

    /**
     * Just a class to hold button data.
     *
     * @param[action] the code to run when the button is triggered.
     * @param[description] the button description.
     * @param[emote] the button icon.
     */
    data class Action(val action: () -> Unit, val description: String, val emote: EmoteReference)
}