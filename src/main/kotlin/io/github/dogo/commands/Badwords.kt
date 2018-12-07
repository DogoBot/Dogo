package io.github.dogo.commands

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.*
import io.github.dogo.menus.ListReactionMenu

class Badwords : ReferencedCommand(
        CommandReference("badwords", aliases = "bw badword", category = CommandCategory.GUILD_ADMINISTRATION),
        { cmd ->
            if(cmd.guild!!.badwords.badwords.isEmpty()) {
                cmd.reply("empty", preset = true)
            } else {
                ListReactionMenu(
                        cmd,
                        cmd.guild.badwords.badwords,
                        embedBuild = {it.setAuthor(cmd.langEntry.getText(cmd.lang(), "author"))}
                ).let {
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
                            ListReactionMenu(cmd, it,
                                    embedBuild = {it.setAuthor(cmd.langEntry.getText(cmd.lang(), "author"))}
                            ).showPage(0)
                        }
                    } else {
                        cmd.reply("nothing", preset = true)
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
                            ListReactionMenu(cmd, it,
                                    embedBuild = {it.setAuthor(cmd.langEntry.getText(cmd.lang(), "author"))}
                            ).showPage(0)
                        }
                    } else {
                        cmd.reply(cmd.langEntry.getText(cmd.lang, "nothing"))
                    }
                }
    )
}