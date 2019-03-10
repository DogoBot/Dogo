package dev.nathanpb.dogo.commands

import dev.nathanpb.dogo.discord.badwords.BadwordAddedEvent
import dev.nathanpb.dogo.discord.badwords.BadwordListAddedEvent
import dev.nathanpb.dogo.discord.badwords.BadwordListRemovedEvent
import dev.nathanpb.dogo.discord.badwords.BadwordRemovedEvent
import dev.nathanpb.dogo.core.DogoBot
import dev.nathanpb.dogo.core.command.CommandCategory
import dev.nathanpb.dogo.core.command.CommandReference
import dev.nathanpb.dogo.core.command.ReferencedCommand
import dev.nathanpb.dogo.discord.badwords
import dev.nathanpb.dogo.discord.menus.ListReactionMenu

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
        CommandReference("badwords", aliases = "bw badword", category = CommandCategory.GUILD_ADMINISTRATION, permission = "command.guildowner"),
        {
            if(guild!!.badwords.isEmpty()) {
                reply("empty", preset = true)
            } else {
                ListReactionMenu(
                        this,
                        guild.badwords,
                        embedBuild = {it.setAuthor(langEntry.getText("author"))}
                ).let {
                    it.timeout = dev.nathanpb.dogo.core.DogoBot.data.TIMEOUTS.GENERAL
                    it.showPage(0)
                }
            }
        }

) {
    class Add : ReferencedCommand(
            CommandReference("add", aliases = "+", args = 1, permission = "command.guildowner"),
            {
                val words = args.filter { w -> !guild!!.badwords.any { w.equals(it, ignoreCase = true) } }
                if(words.isNotEmpty()){
                    guild!!.badwords.addAll(words)
                    words.let {
                        dev.nathanpb.dogo.core.DogoBot.eventBus.submit(BadwordListAddedEvent(guild, sender, it))
                        it.forEach { dev.nathanpb.dogo.core.DogoBot.eventBus.submit(BadwordAddedEvent(guild, sender, it)) }
                        ListReactionMenu(this, it,
                                embedBuild = {it.setAuthor(langEntry.getText("author"))}
                        ).showPage(0)
                    }
                } else {
                    reply("nothing", preset = true)
                }
            }
    )

    class Remove : ReferencedCommand(
            CommandReference("remove", aliases = "-", args = 1, permission = "command.guildowner"),
             {
                val words = args.filter { w -> guild!!.badwords.any { w.equals(it, ignoreCase = true) } }
                if(words.isNotEmpty()){
                    guild!!.badwords.removeAll(words)
                    words.let {
                        dev.nathanpb.dogo.core.DogoBot.eventBus.submit(BadwordListRemovedEvent(guild, sender, it))
                        it.forEach { dev.nathanpb.dogo.core.DogoBot.eventBus.submit(BadwordRemovedEvent(guild, sender, it)) }
                        ListReactionMenu(this, it,
                                embedBuild = {it.setAuthor(langEntry.getText("author"))}
                        ).showPage(0)
                    }
                } else {
                    reply(langEntry.getText("nothing"))
                }
            }
    )
}