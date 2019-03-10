package dev.nathanpb.dogo.core.command

import dev.nathanpb.dogo.discord.IRepliable
import dev.nathanpb.dogo.discord.lang
import dev.nathanpb.dogo.lang.BoundLanguage
import net.dv8tion.jda.core.entities.Message

class CommandContext (val msg : Message, val route: CommandRouter, val args: List<String>) : IRepliable {
    val guild = msg.guild
    val sender = msg.author

    override val langEntry = BoundLanguage(sender.lang, route.getPermission())
    override val replyChannel = msg.channel
}