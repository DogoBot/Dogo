package dev.nathanpb.dogo.commands

import dev.nathanpb.dogo.core.DogoBot
import dev.nathanpb.dogo.core.command.CommandCategory
import dev.nathanpb.dogo.core.command.CommandReference
import dev.nathanpb.dogo.core.command.ReferencedCommand
import dev.nathanpb.dogo.discord.DiscordManager
import dev.nathanpb.dogo.discord.menus.SimpleReactionMenu
import dev.nathanpb.dogo.lang.BoundLanguage
import dev.nathanpb.dogo.lang.LanguageEntry
import dev.nathanpb.dogo.utils._static.BeamUtils
import dev.nathanpb.dogo.utils._static.DisplayUtils
import dev.nathanpb.dogo.utils._static.EmoteReference
import dev.nathanpb.dogo.utils._static.ThemeColor
import net.dv8tion.jda.core.EmbedBuilder

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
class Stats : ReferencedCommand(
        CommandReference("stats", aliases = "status", category = CommandCategory.BOT, permission = "command"),
        {
            val menu = SimpleReactionMenu(this).also {
                it.timeout = dev.nathanpb.dogo.core.DogoBot.data.TIMEOUTS.GENERAL
            }

            menu.addAction(EmoteReference.ARROW_COUNTERCLOCKWISE, "Info") {
                menu.build(dev.nathanpb.dogo.commands.Stats.Companion.getBasicInfo(langEntry))
                dev.nathanpb.dogo.core.DogoBot.eventBus.unregister(menu::onReact)
                menu.send()
            }
            menu.build(dev.nathanpb.dogo.commands.Stats.Companion.getBasicInfo(langEntry))
            menu.send()
        }
) {
    companion object {

        /**
         * Builds the basic information embed.
         *
         * @param[langEntry] the [Stats] command [LanguageEntry].
         */
        private fun getBasicInfo(langEntry: BoundLanguage) : EmbedBuilder {
            return EmbedBuilder()
                    .setColor(ThemeColor.PRIMARY)
                    .setAuthor("Dogo v${dev.nathanpb.dogo.core.DogoBot.version}", null, DiscordManager.jda!!.selfUser.effectiveAvatarUrl)
                    .setTitle(langEntry.getText("amihealthy"))
                    .setThumbnail("https://i.imgur.com/9rmyKUk.png")
                    .addField(langEntry.getText("users"), DiscordManager.jda!!.users.size.toString(),true)
                    .addField(langEntry.getText("guilds"), DiscordManager.jda!!.guilds.size.toString(), true)

                    .addField(langEntry.getText("cpu"), BeamUtils.usedCPU().toString()+"%", true)
                    .addField(langEntry.getText("ram"), "${BeamUtils.usedMemory()}MB | ${BeamUtils.maxMemory()}MB", true)

                    .addField(langEntry.getText("ping"), "${DiscordManager.jda!!.ping}ms", true)
                    .addField(langEntry.getText("uptime"), DisplayUtils.formatTimeSimple(System.currentTimeMillis() - dev.nathanpb.dogo.core.DogoBot.initTime), true)
        }
    }
}