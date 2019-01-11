package io.github.dogo.commands

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.*
import io.github.dogo.lang.BoundLanguage
import io.github.dogo.utils.Holder
import io.github.dogo.utils._static.ThemeColor
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
class Help : ReferencedCommand(

        CommandReference(
                "help",
                usage = "help\nhelp command\n help command subcommand ... subcommand",
                aliases = "plsSendHelp",
                category = CommandCategory.BOT
        ),
        {
                val route = if(args.isNotEmpty()){
                    var s = ""
                    args.forEach { a -> s+="$a " }
                    if(s.isNotEmpty()) {
                        s = s.substring(0, s.length - 1)
                    }
                    DogoBot.cmdFactory.route.findRoute(s, Holder())
                } else DogoBot.cmdFactory.route

                if(route.reference == CommandRouter.root){
                    val embed = EmbedBuilder()
                            .setColor(ThemeColor.PRIMARY)
                            .setAuthor(langEntry.getText("helproot"), null, io.github.dogo.commands.Help.HELP_IMAGE)

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
                        embed.addField(it.key.getDisplay(sender.lang), s.toString(), false)
                    }
                    reply(embed.build())
                } else {
                    reply(Help.getHelpFor(route, this).build())
                }
            }
) {
    companion object {
        /**
         * Default *help* image.
         */
        const val HELP_IMAGE = "https://i.imgur.com/7HF9zwb.png"

        /**
         * Builds the help embed.
         *
         * @param[cmd] the command to get help.
         * @param[cnt] the context to get information like language and texts.
         */
        fun getHelpFor(cmd: CommandRouter, cnt: CommandContext): EmbedBuilder {
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
            embed.addField(cnt.langEntry.getText(cnt.sender.lang, "cmddescription"), BoundLanguage(cnt.sender.lang, cmd.getPermission()).getText("description"), true)

            val subcommands = cnt.route.children.joinToString {"``${DogoBot.getCommandPrefixes().first()}${it.getFullName()}``\n"}
            if(subcommands.isNotEmpty()){
                embed.addField(cnt.langEntry.getText(cnt.sender.lang, "subcommands"), subcommands, false)
            }
            embed.addField(cnt.langEntry.getText(cnt.sender.lang, "permission"), "``${cmd.getPermission()}``", false)
            return embed
        }
    } }