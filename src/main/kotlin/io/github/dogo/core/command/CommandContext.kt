package io.github.dogo.core.command

import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.interfaces.IRepliable
import io.github.dogo.lang.LanguageEntry
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel

class CommandContext (val msg : Message, val route: CommandRouter, val args: List<String>) : IRepliable {
    val guild = if (msg.guild != null) DogoGuild(msg.guild) else null
    val sender = DogoUser(msg.author)
    val langEntry = route.langEntry
    val lang = sender.lang

    override fun langEntry() = langEntry
    override fun replyChannel() = msg.channel
    override fun lang() = lang
}