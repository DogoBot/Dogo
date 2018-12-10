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

class Stats : ReferencedCommand(
        CommandReference("stats", aliases = "status", category = CommandCategory.BOT),
        {
            val menu = SimpleReactionMenu(this).also {
                it.timeout = DogoBot.data.TIMEOUTS.GENERAL
            }
            var basic = false

            val embBasic = Stats.getBasicInfo(lang, langEntry).appendDescription(":arrows_counterclockwise: ${langEntry.getText(lang, "advancedinfo")}\n")
            val embAdvanced = Stats.getThreadInfo(sender.lang, langEntry).appendDescription(":arrows_counterclockwise: ${langEntry.getText(lang, "basicinfo")}\n")

            menu.addAction(EmoteReference.ARROW_COUNTERCLOCKWISE, "Info") {
                menu.build(if(basic) embBasic else embAdvanced)
                DogoBot.eventBus.unregister(menu)
                menu.send()
                basic = !basic
            }
            menu.build(embBasic)
            menu.send()
        }
) {
    companion object {
        private fun getBasicInfo(lang : String, langEntry: LanguageEntry) : EmbedBuilder {
            return EmbedBuilder()
                    .setColor(ThemeColor.PRIMARY)
                    .setTitle(langEntry.getText(lang, "amihealthy"))
                    .setThumbnail("https://i.imgur.com/9rmyKUk.png")
                    .addField(langEntry.getText(lang, "users"), io.github.dogo.core.DogoBot?.jda?.users?.size.toString(),true)
                    .addField(langEntry.getText(lang, "guilds"), io.github.dogo.core.DogoBot?.jda?.guilds?.size.toString(), true)

                    .addField(langEntry.getText(lang, "cpu"), BeamUtils.usedCPU().toString()+"%", true)
                    .addField(langEntry.getText(lang, "ram"), "${BeamUtils.usedMemory()}MB | ${BeamUtils.maxMemory()}MB", true)

                    .addField(langEntry.getText(lang, "ping"), "${io.github.dogo.core.DogoBot.jda?.ping}ms", true)
                    .addField(langEntry.getText(lang, "uptime"), DisplayUtils.formatTimeSimple(System.currentTimeMillis() - io.github.dogo.core.DogoBot.initTime), true)
        }

        private fun getThreadInfo(lang : String, langEntry: LanguageEntry) : EmbedBuilder {
            val embed = EmbedBuilder()
                    .setColor(ThemeColor.PRIMARY)
                    .setThumbnail("https://i.imgur.com/9rmyKUk.png")
                    .setTitle(langEntry.getText(lang, "threadinfo"))
            io.github.dogo.core.DogoBot.threads.values.forEach { t -> embed.addField(t.name, "TPS: ${t.getTps()}Hz | Queue: ${t.queue()}", true) }
            return embed
        }
    }
}