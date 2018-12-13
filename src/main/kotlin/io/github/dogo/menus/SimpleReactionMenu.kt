package io.github.dogo.menus

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.CommandContext
import io.github.dogo.core.eventBus.EventBus
import io.github.dogo.utils.EmoteReference
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.PrivateChannel
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent
import java.security.Permissions
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

    fun build(preset : EmbedBuilder? = null) {
        if(preset == null) {
            actions.forEach {
                embed.appendDescription("${it.emote.getAsMention()} ${it.description}\n")
            }
        } else this.embed = preset

    }

    fun end(delete : Boolean = true) {
        DogoBot.eventBus.unregister(eventbusId)
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

    fun addAction(emote: EmoteReference, description: String, action: () -> Unit) {
        if (!actions.stream().anyMatch { a -> a.emote == emote }) {
            actions.add( Action(action, description, emote))
        } else throw Exception("Action already exists")
    }

    fun removeAction(emote: EmoteReference) = actions.removeAll { it.emote == emote }


    @EventBus.Listener()
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