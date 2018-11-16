package cf.dogo.commands

import cf.dogo.core.DogoBot
import cf.dogo.core.cmdHandler.CommandCategory
import cf.dogo.core.cmdHandler.CommandContext
import cf.dogo.core.cmdHandler.CommandFactory
import cf.dogo.core.cmdHandler.DogoCommand
import cf.dogo.menus.SimpleReactionMenu
import cf.dogo.utils.BeamUtils
import cf.dogo.utils.DisplayUtils
import cf.dogo.utils.EmoteReference
import cf.dogo.utils.ThemeColor
import net.dv8tion.jda.core.EmbedBuilder

class Stats(factory : CommandFactory) : DogoCommand("stats", factory) {
    override val minArgumentsSize = 0
    override val usage = ""
    override val aliases = "status"
    override val category = CommandCategory.BOT

    override fun execute(cmd: CommandContext) {
        val menu = SimpleReactionMenu(cmd).also {
            it.timeout = DogoBot.data.TIMEOUTS.GENERAL
        }
        var basic = false

        val embBasic = getBasicInfo(cmd.sender.lang).appendDescription(":arrows_counterclockwise: ${lang.getText(cmd.lang(), "advancedinfo")}\n")
        val embAdvanced = getThreadInfo(cmd.sender.lang).appendDescription(":arrows_counterclockwise: ${lang.getText(cmd.lang(), "basicinfo")}\n")

        menu.addAction(EmoteReference.ARROW_COUNTERCLOCKWISE, "Info") {
            menu.build(if(basic) embBasic else embAdvanced)
            DogoBot.eventBus.unregister(menu)
            menu.send()
            basic = !basic
        }
        menu.build(embBasic)
        menu.send()
    }

    private fun getBasicInfo(l : String) : EmbedBuilder {
        return EmbedBuilder()
                .setColor(ThemeColor.PRIMARY)
                .setTitle(lang.getText(l, "amihealthy"))
                .setThumbnail("https://i.imgur.com/9rmyKUk.png")
                .addField(lang.getText(l, "users"), cf.dogo.core.DogoBot?.jda?.users?.size.toString(),true)
                .addField(lang.getText(l, "guilds"), cf.dogo.core.DogoBot?.jda?.guilds?.size.toString(), true)

                .addField(lang.getText(l, "cpu"), BeamUtils.usedCPU().toString()+"%", true)
                .addField(lang.getText(l, "ram"), "${BeamUtils.usedMemory()}MB | ${BeamUtils.maxMemory()}MB", true)

                .addField(lang.getText(l, "ping"), "${cf.dogo.core.DogoBot.jda?.ping}ms", true)
                .addField(lang.getText(l, "uptime"), DisplayUtils.formatTimeSimple(System.currentTimeMillis() - cf.dogo.core.DogoBot.initTime), true)
    }

    private fun getThreadInfo(l : String) : EmbedBuilder {
        val embed = EmbedBuilder()
                .setColor(ThemeColor.PRIMARY)
                .setThumbnail("https://i.imgur.com/9rmyKUk.png")
                .setTitle(lang.getText(l, "threadinfo"))
        cf.dogo.core.DogoBot.threads.values.forEach { t -> embed.addField(t.name, "TPS: ${t.getTps()}Hz | Queue: ${t.queue()}", true) }
        return embed
    }
}