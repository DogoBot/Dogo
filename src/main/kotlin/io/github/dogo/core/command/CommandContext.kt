package io.github.dogo.core.command

import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.interfaces.IRepliable
import io.github.dogo.lang.BoundLanguage
import net.dv8tion.jda.core.entities.Message

class CommandContext (val msg : Message, val route: CommandRouter, val args: List<String>) : IRepliable {
    val guild = if (msg.guild != null) DogoGuild.from(msg.guild) else null
    val sender = DogoUser.from(msg.author)

    override val langEntry = BoundLanguage(sender.lang, route.getPermission())
    override val replyChannel = msg.channel
}