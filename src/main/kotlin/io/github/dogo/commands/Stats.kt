package io.github.dogo.commands

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.CommandCategory
import io.github.dogo.core.command.CommandReference
import io.github.dogo.core.command.ReferencedCommand
import io.github.dogo.lang.LanguageEntry
import io.github.dogo.menus.SimpleReactionMenu
import io.github.dogo.utils.BeamUtils
import io.github.dogo.utils.DisplayUtils
import io.github.dogo.utils.EmoteReference
import io.github.dogo.utils.ThemeColor
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
        CommandReference("stats", aliases = "status", category = CommandCategory.BOT),
        {
            val menu = SimpleReactionMenu(this).also {
                it.timeout = DogoBot.data.TIMEOUTS.GENERAL
            }

            menu.addAction(EmoteReference.ARROW_COUNTERCLOCKWISE, "Info") {
                menu.build(getBasicInfo(lang, langEntry))
                DogoBot.eventBus.unregister(menu)
                menu.send()
            }
            menu.build(getBasicInfo(lang, langEntry))
            menu.send()
        }
) {
    companion object {

        /**
         * Builds the basic information embed.
         *
         * @param[lang] the language.
         * @param[langEntry] the [Stats] command [LanguageEntry].
         */
        private fun getBasicInfo(lang : String, langEntry: LanguageEntry) : EmbedBuilder {
            return EmbedBuilder()
                    .setColor(ThemeColor.PRIMARY)
                    .setAuthor("Dogo v${DogoBot.version}", null, DogoBot.jda!!.selfUser.effectiveAvatarUrl)
                    .setTitle(langEntry.getText(lang, "amihealthy"))
                    .setThumbnail("https://i.imgur.com/9rmyKUk.png")
                    .addField(langEntry.getText(lang, "users"), DogoBot.jda?.users?.size.toString(),true)
                    .addField(langEntry.getText(lang, "guilds"), DogoBot.jda?.guilds?.size.toString(), true)

                    .addField(langEntry.getText(lang, "cpu"), BeamUtils.usedCPU().toString()+"%", true)
                    .addField(langEntry.getText(lang, "ram"), "${BeamUtils.usedMemory()}MB | ${BeamUtils.maxMemory()}MB", true)

                    .addField(langEntry.getText(lang, "ping"), "${DogoBot.jda?.ping}ms", true)
                    .addField(langEntry.getText(lang, "uptime"), DisplayUtils.formatTimeSimple(System.currentTimeMillis() - DogoBot.initTime), true)
        }
    }
}