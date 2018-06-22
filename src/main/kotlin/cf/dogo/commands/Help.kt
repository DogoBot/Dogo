package cf.dogo.commands

import cf.dogo.core.cmdHandler.*
import net.dv8tion.jda.core.EmbedBuilder

class Help(factory : CommandFactory) : DogoCommand("help", factory){

    val HELP_IMAGE = "https://i.imgur.com/7HF9zwb.png"

    override val minArgumentsSize = 0
    override val usage = "help\nhelp command\nhelp command subcommand ... subcommand"
    override val aliases = "plsSendHelp"
    override val category = CommandCategory.BOT

    override fun execute(cmd : CommandContext) {
        var s = ""
        cmd.tree.args.forEach { a -> s+="$a " }
        if(s.isNotEmpty()) {
            s = s.substring(0, s.length - 1)
        }
        val tree = CommandTree(s, factory)

        if(tree.isEmpty()){
            val embed = EmbedBuilder()
                    .setColor(cf.dogo.core.DogoBot.themeColor[1])
                    .setAuthor(lang.getText(cmd.sender.lang, "helproot"), null, HELP_IMAGE)

            val hm = HashMap<CommandCategory, ArrayList<DogoCommand>>()
            factory.commands.values.forEach { c ->
                if(!hm.containsKey(c.category)) hm[c.category] = ArrayList()
                (hm[c.category] as ArrayList).add(c)
            }
            hm.forEach{e ->
                var s = ""
                e.value.forEach { c -> s+="``${c.name}``, " }
                s = s.substring(0, s.length-2)
                s+="\n";
                embed.addField(e.key.getDisplay(cmd.sender.lang), s, false)
            }
            cmd.reply(embed.build())
        } else {
            cmd.reply(getHelpFor(tree.last(), cmd).build())
        }
    }

    fun getHelpFor(cmd : DogoCommand, cnt : CommandContext) : EmbedBuilder {
        val embed = EmbedBuilder()
                .setColor(cf.dogo.core.DogoBot.themeColor[1])
                .setAuthor(lang.getText(cnt.sender.lang, "helpfor", cmd.name), null, HELP_IMAGE)

        embed.addField(lang.getText(cnt.sender.lang, "category"), cmd.category.getDisplay(cnt.sender.lang), false)

        var usage = ""
        if(cmd.usage.contains("\n")){
            cmd.usage.split("\n")
                    .forEach { e -> usage+= cf.dogo.core.DogoBot.instance.getCommandPrefixes().first()+e+"\n" }
        } else {
            usage = cf.dogo.core.DogoBot.instance.getCommandPrefixes().first()+cmd.usage
        }
        if(usage.endsWith("\n")){
            usage = usage.substring(0, usage.length-1)
        }

        embed.addField(lang.getText(cnt.sender.lang, "examples"), if(cmd.usage.isNotEmpty()) usage else lang.getText(cnt.sender.lang, "noexamples"), true)
        embed.addField(lang.getText(cnt.sender.lang, "cmddescription"), lang.getText(cnt.sender.lang, "description"), true)

        var subcommands = ""
        cmd.children.forEach { c -> subcommands+="``${cf.dogo.core.DogoBot?.instance.getCommandPrefixes().first()}${c.getFullName()}``\n" }
        if(subcommands.isNotEmpty()) embed.addField(lang.getText(cnt.sender.lang, "subcommands"), subcommands, false)
        embed.addField(lang.getText(cnt.sender.lang, "permission"), cmd.getPermission(), false)
        return embed
    }
}