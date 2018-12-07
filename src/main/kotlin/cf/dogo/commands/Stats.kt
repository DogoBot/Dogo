package cf.dogo.commands

import cf.dogo.core.DogoBot
import cf.dogo.core.command.CommandCategory
import cf.dogo.core.command.CommandReference
import cf.dogo.core.command.ReferencedCommand
import cf.dogo.lang.LanguageEntry
import cf.dogo.menus.SimpleReactionMenu
import cf.dogo.utils.BeamUtils
import cf.dogo.utils.DisplayUtils
import cf.dogo.utils.EmoteReference
import cf.dogo.utils.ThemeColor
import net.dv8tion.jda.core.EmbedBuilder

class Stats : ReferencedCommand(
        CommandReference("stats", aliases = "status", category = CommandCategory.BOT),
        { cmd->
            val menu = SimpleReactionMenu(cmd).also {
                it.timeout = DogoBot.data.TIMEOUTS.GENERAL
            }
            var basic = false

            val embBasic = getBasicInfo(cmd.sender.lang, cmd.langEntry).appendDescription(":arrows_counterclockwise: ${cmd.langEntry.getText(cmd.lang, "advancedinfo")}\n")
            val embAdvanced = getThreadInfo(cmd.sender.lang, cmd.langEntry).appendDescription(":arrows_counterclockwise: ${cmd.langEntry.getText(cmd.lang, "basicinfo")}\n")

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
                    .addField(langEntry.getText(lang, "users"), cf.dogo.core.DogoBot?.jda?.users?.size.toString(),true)
                    .addField(langEntry.getText(lang, "guilds"), cf.dogo.core.DogoBot?.jda?.guilds?.size.toString(), true)

                    .addField(langEntry.getText(lang, "cpu"), BeamUtils.usedCPU().toString()+"%", true)
                    .addField(langEntry.getText(lang, "ram"), "${BeamUtils.usedMemory()}MB | ${BeamUtils.maxMemory()}MB", true)

                    .addField(langEntry.getText(lang, "ping"), "${cf.dogo.core.DogoBot.jda?.ping}ms", true)
                    .addField(langEntry.getText(lang, "uptime"), DisplayUtils.formatTimeSimple(System.currentTimeMillis() - cf.dogo.core.DogoBot.initTime), true)
        }

        private fun getThreadInfo(lang : String, langEntry: LanguageEntry) : EmbedBuilder {
            val embed = EmbedBuilder()
                    .setColor(ThemeColor.PRIMARY)
                    .setThumbnail("https://i.imgur.com/9rmyKUk.png")
                    .setTitle(langEntry.getText(lang, "threadinfo"))
            cf.dogo.core.DogoBot.threads.values.forEach { t -> embed.addField(t.name, "TPS: ${t.getTps()}Hz | Queue: ${t.queue()}", true) }
            return embed
        }
    }
}