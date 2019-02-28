package io.github.dogo.core.command

import io.github.dogo.discord.IRepliable
import io.github.dogo.discord.lang
import io.github.dogo.lang.BoundLanguage
import net.dv8tion.jda.core.entities.Message

class CommandContext (val msg : Message, val route: CommandRouter, val args: List<String>) : IRepliable {
    val guild = msg.guild
    val sender = msg.author

    override val langEntry = BoundLanguage(sender.lang, route.getPermission())
    override val replyChannel = msg.channel
}