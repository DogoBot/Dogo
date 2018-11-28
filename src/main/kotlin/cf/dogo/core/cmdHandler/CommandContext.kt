package cf.dogo.core.cmdHandler

import cf.dogo.core.entities.DogoGuild
import cf.dogo.core.entities.DogoUser
import cf.dogo.interfaces.IRepliable
import cf.dogo.lang.LanguageEntry
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel

class CommandContext (val msg : Message, val tree : CommandTree) : IRepliable {
    val guild = if (msg.guild == null) null else DogoGuild(msg.guild)
    val sender = DogoUser(msg.author)
    var actual: DogoCommand? = tree.first()

    override fun replyChannel(): MessageChannel {
        return msg.channel
    }

    override fun lang(): String {
        return sender.lang
    }

    override fun langEntry(): LanguageEntry {
        return actual?.lang ?: tree.first().lang
    }

    fun next() {
        val index = tree.indexOf(actual)
        actual = if (tree.size < index) {
            tree[index + 1]
        } else {
            null
        }
    }
}