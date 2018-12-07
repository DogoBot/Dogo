package io.github.dogo.commands

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.*
import io.github.dogo.utils.Holder
import io.github.dogo.utils.ThemeColor
import net.dv8tion.jda.core.EmbedBuilder

class Help : ReferencedCommand(

        CommandReference(
                "help",
                usage = "help\nhelp command\n help command subcommand ... subcommand",
                aliases = "plsSendHelp",
                category = CommandCategory.BOT
        ),
        { cmd ->

                val route = if(cmd.args.isNotEmpty()){
                    var s = ""
                    cmd.args.forEach { a -> s+="$a " }
                    if(s.isNotEmpty()) {
                        s = s.substring(0, s.length - 1)
                    }
                    DogoBot.cmdFactory.route.findRoute(s, Holder())
                } else DogoBot.cmdFactory.route

                if(route.reference == CommandRouter.root){
                    val embed = EmbedBuilder()
                            .setColor(ThemeColor.PRIMARY)
                            .setAuthor(cmd.langEntry.getText(cmd.sender.lang, "helproot"), null, io.github.dogo.commands.Help.Companion.HELP_IMAGE)

                    val hm = HashMap<CommandCategory, ArrayList<CommandReference>>()

                    DogoBot.cmdFactory.route.children.forEach { c ->
                        if(!hm.containsKey(c.reference.category)) hm[c.reference.category] = ArrayList()
                        (hm[c.reference.category] as ArrayList).add(c.reference)
                    }
                    hm.forEach{
                        var s = StringBuilder()
                        it.value.forEach {s.append("``${it.name}``, ") }
                        s = StringBuilder(s.substring(0, s.length-2))
                        s.append("\n")
                        embed.addField(it.key.getDisplay(cmd.sender.lang), s.toString(), false)
                    }
                    cmd.reply(embed.build())
                } else {
                    cmd.reply(io.github.dogo.commands.Help.Companion.getHelpFor(route, cmd).build())
                }
            }
) { companion object {
        val HELP_IMAGE = "https://i.imgur.com/7HF9zwb.png"

        fun getHelpFor(cmd : CommandRouter, cnt : CommandContext) : EmbedBuilder {
            val embed = EmbedBuilder()
                    .setColor(ThemeColor.PRIMARY)
                    .setAuthor(cnt.langEntry.getText(cnt.sender.lang, "helpfor", cmd.reference.name), null, io.github.dogo.commands.Help.Companion.HELP_IMAGE)

            embed.addField(cnt.langEntry.getText(cnt.sender.lang, "category"), cmd.reference.category.getDisplay(cnt.sender.lang), false)

            var usage = ""
            if(cmd.reference.usage.contains("\n")){
                cmd.reference.usage.split("\n")
                        .forEach { e -> usage+= DogoBot.getCommandPrefixes().first()+e+"\n" }
            } else {
                usage = DogoBot.getCommandPrefixes().first()+cmd.reference.usage
            }
            if(usage.endsWith("\n")){
                usage = usage.substring(0, usage.length-1)
            }

            embed.addField(cnt.langEntry.getText(cnt.sender.lang, "examples"), if(cmd.reference.usage.isNotEmpty()) usage else cnt.langEntry.getText(cnt.sender.lang, "noexamples"), true)
            embed.addField(cnt.langEntry.getText(cnt.sender.lang, "cmddescription"), cmd.langEntry.getText(cnt.sender.lang, "description"), true)

            val subcommands = cnt.route.children.joinToString {"``${DogoBot.getCommandPrefixes().first()}${it.getFullName()}``\n"}
            if(subcommands.isNotEmpty()){
                embed.addField(cnt.langEntry.getText(cnt.sender.lang, "subcommands"), subcommands, false)
            }
            embed.addField(cnt.langEntry.getText(cnt.sender.lang, "permission"), "``${cmd.getPermission()}``", false)
            return embed
        }
    } }