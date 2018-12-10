package io.github.dogo.commands

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.*
import io.github.dogo.menus.ListReactionMenu

class Badwords : ReferencedCommand(
        CommandReference("badwords", aliases = "bw badword", category = CommandCategory.GUILD_ADMINISTRATION),
        {
            if(guild!!.badwords.badwords.isEmpty()) {
                reply("empty", preset = true)
            } else {
                ListReactionMenu(
                        this,
                        guild.badwords.badwords,
                        embedBuild = {it.setAuthor(langEntry.getText(lang, "author"))}
                ).let {
                    it.timeout = DogoBot.data.TIMEOUTS.GENERAL
                    it.showPage(0)
                }
            }
        }

) {
    class Add : ReferencedCommand(
            CommandReference("add", aliases = "+", args = 1),
            {
                    val words = args.filter { w -> !guild!!.badwords.badwords.any { w.equals(it, ignoreCase = true) } }
                    if(words.isNotEmpty()){
                        words.also {
                            guild!!.badwords.let {
                                it.badwords.addAll(words)
                                it.update()
                            }
                        }.let {
                            ListReactionMenu(this, it,
                                    embedBuild = {it.setAuthor(langEntry.getText(lang, "author"))}
                            ).showPage(0)
                        }
                    } else {
                        reply("nothing", preset = true)
                    }
                }
    )

    class Remove : ReferencedCommand(
            CommandReference("remove", aliases = "-", args = 1),
             {
                    val words = args.filter { w -> guild!!.badwords.badwords.any { w.equals(it, ignoreCase = true) } }
                    if(words.isNotEmpty()){
                        words.also {
                            guild!!.badwords.let {
                                it.badwords.removeAll(words)
                                it.update()
                            }
                        }.let {
                            ListReactionMenu(this, it,
                                    embedBuild = {it.setAuthor(langEntry.getText(lang, "author"))}
                            ).showPage(0)
                        }
                    } else {
                        reply(langEntry.getText(lang, "nothing"))
                    }
                }
    )
}