package cf.dogo.commands

import cf.dogo.core.cmdHandler.CommandCategory
import cf.dogo.core.cmdHandler.CommandContext
import cf.dogo.core.cmdHandler.CommandFactory
import cf.dogo.core.cmdHandler.DogoCommand
import cf.dogo.menus.SimpleReactionMenu
import cf.dogo.utils.DisplayUtils
import cf.dogo.utils.EmoteReference
import net.dv8tion.jda.core.EmbedBuilder

class Stats(factory : CommandFactory) : DogoCommand("stats", factory) {
    override val minArgumentsSize = 0
    override val usage = ""
    override val aliases = "status"
    override val category = CommandCategory.BOT

    override fun execute(cmd: CommandContext) {
        var current = 0
        val menu = SimpleReactionMenu(cmd)

        val embeds = arrayOf(
                getBasicInfo(cmd.sender.lang),//.appendDescription(":arrows_counterclockwise: ${lang.getText(cmd.lang(), "advancedinfo")}\n"),
                getThreadInfo(cmd.sender.lang)//.appendDescription(":arrows_counterclockwise: ${lang.getText(cmd.lang(), "basicinfo")}\n")
        )
        var run = {
            current = if (current == 0) {
                1
            } else {
                0
            }
            menu.build(embeds[current])
            menu.send()
        }

        menu.addAction(EmoteReference.ARROW_COUNTERCLOCKWISE, "Info", run)
        menu.build(embeds[0])
        menu.send()
    }

    fun getBasicInfo(l : String) : EmbedBuilder {
        return EmbedBuilder()
                .setColor(cf.dogo.core.DogoBot.themeColor[1])
                .setTitle(lang.getText(l, "amihealthy"))
                .setThumbnail("https://i.imgur.com/9rmyKUk.png")
                .addField(lang.getText(l, "users"), cf.dogo.core.DogoBot?.jda?.users?.size.toString(),true)
                .addField(lang.getText(l, "guilds"), cf.dogo.core.DogoBot?.jda?.guilds?.size.toString(), true)
                //.addBlankField(true)

                .addField(lang.getText(l, "cpu"), cf.dogo.core.DogoBot.instance.usedCPU().toString()+"%", true)
                .addField(lang.getText(l, "ram"), "${cf.dogo.core.DogoBot.instance.usedMemory()}MB | ${cf.dogo.core.DogoBot.instance.maxMemory()}MB", true)
                //.addBlankField(true)

                .addField(lang.getText(l, "ping"), "${cf.dogo.core.DogoBot.jda?.ping}ms", true)
                .addField(lang.getText(l, "uptime"), DisplayUtils().formatTimeSimple(System.currentTimeMillis() - cf.dogo.core.DogoBot.initTime), true)
                //.addBlankField(true)
    }

    fun getThreadInfo(l : String) : EmbedBuilder {
        val embed = EmbedBuilder()
                .setColor(cf.dogo.core.DogoBot.themeColor[1])
                .setThumbnail("https://i.imgur.com/9rmyKUk.png")
                .setTitle(lang.getText(l, "threadinfo"))
        cf.dogo.core.DogoBot.threads.values.forEach { t -> embed.addField(t.name, "TPS: ${t.getTps()}Hz | Queue: ${t.queue()}", true) }
        return embed
    }
}