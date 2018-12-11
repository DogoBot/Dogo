package io.github.dogo.core.command

import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.interfaces.IRepliable
import net.dv8tion.jda.core.entities.Message

class CommandContext (val msg : Message, val route: CommandRouter, val args: List<String>) : IRepliable {
    val guild = if (msg.guild != null) DogoGuild(msg.guild) else null
    val sender = DogoUser(msg.author)

    override val langEntry = route.langEntry
    override val lang = sender.lang
    override val replyChannel = msg.channel
}