package cf.dogo.commands

import cf.dogo.core.DogoBot
import cf.dogo.core.command.*
import cf.dogo.menus.ListReactionMenu

class Badwords : ReferencedCommand(
        CommandReference("badwords", aliases = "bw badword", category = CommandCategory.GUILD_ADMINISTRATION),
        { cmd ->
            if(cmd.guild!!.badwords.badwords.isEmpty()) {
                cmd.reply(cmd.langEntry.getText(cmd.lang, "empty"))
            } else {
                ListReactionMenu(cmd, cmd.guild.badwords.badwords).let {
                    it.timeout = DogoBot.data.TIMEOUTS.GENERAL
                    it.showPage(0)
                }
            }
        }

) {
    class Add : ReferencedCommand(
            CommandReference("add", aliases = "+", args = 1),
            {cmd ->
                    val words = cmd.args.filter { w -> !cmd.guild!!.badwords.badwords.any { w.equals(it, ignoreCase = true) } }
                    if(words.isNotEmpty()){
                        words.also {
                            cmd.guild!!.badwords.let {
                                it.badwords.addAll(words)
                                it.update()
                            }
                        }.let {
                            ListReactionMenu(cmd, it, embedBuild = {it.setTitle(cmd.langEntry.getText(cmd.lang(), "added"))})
                                    .showPage(0)
                        }
                    } else {
                        cmd.reply(cmd.langEntry.getText(cmd.lang, "nothing"))
                    }
                }
    )

    class Remove : ReferencedCommand(
            CommandReference("remove", aliases = "-", args = 1),
             { cmd ->
                    val words = cmd.args.filter { w -> cmd.guild!!.badwords.badwords.any { w.equals(it, ignoreCase = true) } }
                    if(words.isNotEmpty()){
                        words.also {
                            cmd.guild!!.badwords.let {
                                it.badwords.removeAll(words)
                                it.update()
                            }
                        }.let {
                            ListReactionMenu(cmd, it, embedBuild = {it.setTitle(cmd.langEntry.getText(cmd.lang(), "added"))})
                                    .showPage(0)
                        }
                    } else {
                        cmd.reply(cmd.langEntry.getText(cmd.lang, "nothing"))
                    }
                }
    )
}