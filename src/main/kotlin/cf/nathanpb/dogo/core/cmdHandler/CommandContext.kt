package cf.nathanpb.dogo.core.cmdHandler

import cf.nathanpb.dogo.core.entities.DogoGuild
import cf.nathanpb.dogo.core.entities.DogoUser
import net.dv8tion.jda.core.entities.Message
import java.util.*

class CommandContext (msg : Message, tree : CommandTree){
    val tree = tree
    val msg = msg
    val guild = if(msg.guild == null) null else DogoGuild(msg.guild)
    val sender = DogoUser(msg.author)
    var actual :DogoCommand? = tree.first()

    var args : Array<String>
        get() {
            return Arrays.copyOfRange(tree.args.toTypedArray(), tree.indexOf(actual), tree.size-1)
        }
        private set(value){}

    fun next(){
        val index = tree.indexOf(actual)
        if(tree.size < index){
            actual = tree[index+1]
        } else {
            actual = null
        }
    }



}