package cf.dogo.core.cmdHandler

import cf.dogo.exceptions.CommandException
import cf.dogo.lang.LanguageEntry
import kotlin.reflect.full.allSuperclasses

abstract class DogoCommand(name : String, factory : CommandFactory) {
    val name  = name
    val factory = factory
    abstract val minArgumentsSize : Int
    abstract val usage : String
    abstract val aliases : String
    abstract val category : CommandCategory
    val lang = LanguageEntry(getPermission())
    val children = ArrayList<DogoCommand>()


    @Throws(CommandException::class)
    abstract fun execute(cmd : CommandContext)

    fun getTriggers() : List<String> {
        return ("$aliases $name").split(" ")
    }

    fun getPermission() : String {
        var perm = "command"
        for(cmd in CommandTree(this)){
            perm+=".${cmd.name}"
        }
        return perm
    }

    fun getParent() : DogoCommand? {
        if(this::class.allSuperclasses.isNotEmpty()) {
            val a = this::class.allSuperclasses.stream().findFirst().orElse(null)
            if (a != null) {
                return factory.commands[a]
            }
        }
        return null
    }

    fun isRoot() : Boolean {
        return getParent() != null
    }

    fun getFullName() : String {
        var s = ""
        var cmd : DogoCommand? = this
        do {
            s = " ${cmd?.name} $s"
            cmd = cmd?.getParent()
        }while (!isRoot())
        if(s.startsWith(" ")) s.substring(1, s.length-1)
        return s
    }
}