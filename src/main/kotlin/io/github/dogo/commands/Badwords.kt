package io.github.dogo.commands

import com.google.common.eventbus.EventBus
import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.CommandCategory
import io.github.dogo.core.command.CommandReference
import io.github.dogo.core.command.ReferencedCommand
import io.github.dogo.events.badword.BadwordAddedEvent
import io.github.dogo.events.badword.BadwordListAddedEvent
import io.github.dogo.events.badword.BadwordListRemovedEvent
import io.github.dogo.events.badword.BadwordRemovedEvent
import io.github.dogo.menus.ListReactionMenu

/*
Copyright 2019 Nathan Bombana

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
/**
 * @author NathanPB
 * @since 3.1.0
 */
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
                            DogoBot.eventBus.submit(BadwordListAddedEvent(guild!!, sender, it))
                            it.forEach { DogoBot.eventBus.submit(BadwordAddedEvent(guild, sender, it)) }
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
                            DogoBot.eventBus.submit(BadwordListRemovedEvent(guild!!, sender, it))
                            it.forEach { DogoBot.eventBus.submit(BadwordRemovedEvent(guild, sender, it)) }
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