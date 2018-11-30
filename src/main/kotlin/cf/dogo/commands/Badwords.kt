package cf.dogo.commands

import cf.dogo.badwords.BadwordProfile
import cf.dogo.core.DogoBot
import cf.dogo.core.cmdHandler.CommandCategory
import cf.dogo.core.cmdHandler.CommandContext
import cf.dogo.core.cmdHandler.CommandFactory
import cf.dogo.core.cmdHandler.DogoCommand
import cf.dogo.menus.ListReactionMenu

class Badwords(factory: CommandFactory) : DogoCommand("badwords", factory){
    override val minArgumentsSize = 0
    override val usage = ""
    override val aliases = "bw badword"
    override val category = CommandCategory.GUILD_ADMINISTRATION

    override fun execute(cmd: CommandContext) {
        if(cmd.guild!!.badwords.badwords.isEmpty()) {
            cmd.reply(lang.getText(cmd.lang(), "empty"))
        } else {
            ListReactionMenu(cmd, cmd.guild.badwords.badwords).let {
                it.timeout = DogoBot.data.TIMEOUTS.GENERAL
                it.showPage(0)
            }
        }
    }

    class Add(facory: CommandFactory) : DogoCommand("add", facory){
        override val minArgumentsSize = 1
        override val usage = "areallybadword otherbadword"
        override val aliases = ""
        override val category = CommandCategory.GUILD_ADMINISTRATION

        override fun execute(cmd: CommandContext) {
            cmd.tree.args
                    .filter { w -> cmd.guild!!.badwords.badwords.any { w.equals(it, ignoreCase = true) } }
                    .also {
                        cmd.guild!!.badwords.badwords.removeAll(it)
                        cmd.guild.badwords.update()
                    }.let {
                        ListReactionMenu(cmd, it, embedBuild = {it.setTitle(lang.getText(cmd.lang(), "removed"))})
                                .showPage(0)
                    }
        }
    }
}