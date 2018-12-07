package io.github.dogo.menus

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.CommandContext
import io.github.dogo.core.eventBus.EventBus
import io.github.dogo.utils.EmoteReference
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.PrivateChannel
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent
import java.util.*

/**
 * Created by nathanpb on 12/2/17.
 */
open class SimpleReactionMenu(val context: CommandContext) {
    companion object {
        val instances = mutableListOf<SimpleReactionMenu>()
    }
    init {
        instances.add(this)
    }

    protected var actions: ArrayList<Action> = ArrayList()
    var embed = EmbedBuilder()
    var target = context.sender.id
    var msg: Message? = null

    var timeout = 0L
    var lastSend = 0L

    protected var eventbusId : Array<Long> = emptyArray()

    fun build(preset : EmbedBuilder? = null): SimpleReactionMenu {
        if(preset == null) {
            for (ac in actions) {
                embed.appendDescription("${ac.emote.getAsMention()} ${ac.description}\n")
            }
        } else {
            this.embed = preset
        }
        return this
    }

    fun end(delete : Boolean = true) {
        DogoBot.eventBus.unregister(eventbusId)
        instances.remove(this)
        if(delete) {
            msg?.delete()?.queue()
            msg = null
        } else {
            msg?.clearReactions()?.queue()
        }
    }

    open fun send() {
        eventbusId = DogoBot.eventBus.register(this)
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

    fun addAction(emote: EmoteReference, description: String, action: () -> Unit): SimpleReactionMenu {
        if (!actions.stream().anyMatch { a -> a.emote == emote }) {
            actions.add( Action(action, description, emote))
        }
        return this
    }

    fun removeAction(emote: EmoteReference): SimpleReactionMenu {
        actions = ArrayList(actions.filter { a -> a.emote != emote })
        return this
    }

    @EventBus.Listener(0)
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

    data class Action(val action: () -> Unit, val description: String, val emote: EmoteReference)
}