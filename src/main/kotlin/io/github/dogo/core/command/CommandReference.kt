package io.github.dogo.core.command

data class CommandReference(val name: String, val args: Int = 0, val aliases: String = "", val usage: String = "", val category: CommandCategory = CommandCategory.HIDDEN, val permission: String) {

    fun nameMatches(str: String) = str.equals(name, ignoreCase = true) || aliases.split(" ").any { str.equals(it, ignoreCase = true)}

    override fun toString(): String {
        return name
    }
}