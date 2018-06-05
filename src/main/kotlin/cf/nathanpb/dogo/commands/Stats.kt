package cf.nathanpb.dogo.commands

import cf.nathanpb.dogo.core.DogoBot
import cf.nathanpb.dogo.core.cmdHandler.CommandCategory
import cf.nathanpb.dogo.core.cmdHandler.CommandContext
import cf.nathanpb.dogo.core.cmdHandler.CommandFactory
import cf.nathanpb.dogo.core.cmdHandler.DogoCommand
import cf.nathanpb.dogo.utils.DisplayUtils
import net.dv8tion.jda.core.EmbedBuilder

class Stats(factory : CommandFactory) : DogoCommand("help", factory) {
    override val minArgumentsSize = 0
    override val usage = ""
    override val aliases = "status"
    override val category = CommandCategory.BOT

    override fun execute(cmd: CommandContext) {
        val embed = EmbedBuilder()
                .setColor(DogoBot.themeColor[1])
                .setTitle(lang.getText(cmd.lang(), "amihealthy"))
                .setThumbnail(DogoBot.jda?.selfUser?.effectiveAvatarUrl)
                .addField(lang.getText(cmd.lang(), "users"), DogoBot?.jda?.users?.size.toString(),true)
                .addField(lang.getText(cmd.lang(), "guilds"), DogoBot?.jda?.guilds?.size.toString(), true)

                .addField(lang.getText(cmd.lang(), "cpu"), DogoBot.instance.usedCPU().toString()+"%", true)
                .addField(lang.getText(cmd.lang(), "ram"), "${DogoBot.instance.usedMemory()}MB | ${DogoBot.instance.maxMemory()}MB", true)

                .addField(lang.getText(cmd.lang(), "ping"), DogoBot.jda?.ping.toString(), true)
                .addField(lang.getText(cmd.lang(), "uptime"), DisplayUtils().formatTimeSimple(DogoBot.initTime), true)
        cmd.reply(embed.build())
    }
}