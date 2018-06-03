package cf.nathanpb.dogo.core.cmdHandler

import cf.nathanpb.dogo.core.entities.DogoGuild
import cf.nathanpb.dogo.core.entities.DogoUser
import net.dv8tion.jda.core.entities.Message
import java.util.*
import kotlin.collections.ArrayList

class CommandContext (msg : Message, tree : CommandTree) {
    val tree = tree
    val msg = msg
    val guild = if (msg.guild == null) null else DogoGuild(msg.guild)
    val sender = DogoUser(msg.author)
    var actual: DogoCommand? = tree.first()

    fun next() {
        val index = tree.indexOf(actual)
        if (tree.size < index) {
            actual = tree[index + 1]
        } else {
            actual = null
        }
    }
}