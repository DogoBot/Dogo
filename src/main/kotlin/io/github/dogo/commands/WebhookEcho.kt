package io.github.dogo.commands

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.CommandCategory
import io.github.dogo.core.command.CommandFactory
import io.github.dogo.core.command.CommandReference
import io.github.dogo.core.command.ReferencedCommand
import io.github.dogo.discord.menus.SelectorReactionMenu
import io.github.dogo.discord.newsWebhook

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
 *
 * @author NathanPB
 * @since 3.1.0
 */
class WebhookEcho : ReferencedCommand(
        CommandReference("news", args = 1, category = CommandCategory.GUILD_ADMINISTRATION, permission = "command.guildowner"),
        {
            if(guild?.newsWebhook != null){
                guild.newsWebhook!!.newClient().build().also {
                    val text = args.joinToString(" ")
                    if(text.length > 2000) {
                        reply("texttoolong", preset = true)
                    } else {
                        it.send(text)
                    }
                }.close()
            } else {
                reply("notconfigured", CommandFactory.getCommandPrefixes().sortedBy { -it.length }.first(), preset = true)
            }
        }
){
    class Configure : ReferencedCommand(
            CommandReference("configure", permission = "command.guildowner"),
            {
                val hooks = guild!!.webhooks!!.complete()
                if(hooks.isNotEmpty()){
                    SelectorReactionMenu(
                            this,
                            hooks.map{ it.id },
                            {it, _ -> hooks.firstOrNull{ h -> h.id == it }?.name+"\n" },
                            onSelected = { it, instance ->
                                instance.end(true)
                                guild.newsWebhook = hooks.firstOrNull { h -> h.id == it }
                                reply("done", "${guild.newsWebhook?.name}", preset = true)
                            }
                    ).apply {
                        timeout = DogoBot.data.TIMEOUTS.GENERAL
                    }.showPage(0)
                } else {
                    reply("none", preset = true)
                }
            }
    )

    class Current : ReferencedCommand(
            CommandReference("current", permission = "command.guildowner"),
            {
                if(guild?.newsWebhook != null){
                    reply("current", guild.newsWebhook!!.name, preset = true)
                } else {
                    reply("none", preset = true)
                }
            }
    )
}