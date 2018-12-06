package cf.dogo.core.command

import cf.dogo.commands.BadWords
import cf.dogo.commands.Help
import cf.dogo.commands.TicTacToe
import cf.dogo.core.DogoBot
import cf.dogo.core.entities.DogoGuild
import cf.dogo.core.entities.DogoUser
import cf.dogo.core.eventBus.EventBus
import cf.dogo.lang.LanguageEntry
import cf.dogo.utils.Holder
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.PrivateChannel
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.awt.Color

class CommandFactory {
    var route : CommandRouter = CommandRouter(CommandRouter.root){}

    fun route(body: CommandRouter.()->Unit){
        CommandRouter(CommandRouter.root, body).also { route = it }
    }

    @EventBus.Listener
    fun onMessage(event : MessageReceivedEvent){
        var prefix : String? = null

        var prefixes = cf.dogo.core.DogoBot.getCommandPrefixes()
        if(event.guild != null){
            prefixes = cf.dogo.core.DogoBot.getCommandPrefixes(DogoGuild(event.guild))
        }
        for(p in prefixes){
            if(event.message.contentRaw.startsWith(p)){
                prefix = p
                break
            }
        }

        prefix?.let {
            DogoBot.cmdProcessorThread.submit {
                val text = event.message.contentRaw.replaceFirst(prefix, "")
                val argsHolder = Holder<Int>()
                val route = this.route.findRoute(text, argsHolder)
                val user = DogoUser(event.author)
                val guild: DogoGuild? = if (event.guild != null) DogoGuild(event.guild) else null
                val pgs = user.getPermGroups()
                guild?.let { pgs.addAll(guild.permgroups.filterApplied(user.id)) }

                route.let { cmd ->
                    if(pgs.can(cmd.getPermission())){
                        val args = text.split(" ").filterIndexed { index, _ -> index >= argsHolder.hold()}
                        if(args.size >= cmd.reference.args){
                            if(cmd.reference.category == CommandCategory.NSFW){
                                if(event.channel !is PrivateChannel){
                                    if(!(event.channel is TextChannel && (event.channel as TextChannel).isNSFW)){
                                        DogoBot.jdaOutputThread.submit {
                                            val lang = LanguageEntry("text")
                                            event.channel.sendMessage(lang.getText(user.lang, "error.notnsfwchannel")).queue({}, {})
                                        }
                                        return@let
                                    }
                                }
                            }
                            if(cmd.reference.category == CommandCategory.GUILD_ADMINISTRATION && guild == null) {
                                DogoBot.jdaOutputThread.submit {
                                    val lang = LanguageEntry("text")
                                    event.channel.sendMessage(lang.getText(user.lang, "error.guildrequired")).queue({}, {})
                                }
                                return@let
                            }
                            if(cmd.reference.category == CommandCategory.OWNER && user.getPermGroups().can("commands.admin.root")){
                                DogoBot.jdaOutputThread.submit {
                                    val lang = LanguageEntry("text")
                                    EmbedBuilder()
                                            .setColor(Color.YELLOW)
                                            .setTitle(lang.getText(user.lang, "error.commandforbidden.title"))
                                            .setDescription(lang.getText(user.lang, "error.commandforbidden.description", "command.admin.root"))
                                }
                                return@let
                            }

                            val context = CommandContext(event.message, route, args)
                            cmd.runs.forEach { it.command(context) }

                        } else {
                            DogoBot.jdaOutputThread.submit {
                                val lang = LanguageEntry("text")
                                event.channel.sendMessage(
                                        EmbedBuilder()
                                                .setColor(Color.YELLOW)
                                                .setTitle(lang.getText(user.lang, "error.argsmissing.title"))
                                                .setDescription(lang.getText(user.lang, "error.argsmissing.description", args.size, cmd.reference.args))
                                                .build()
                                ).queue({}, {})
                            }
                        }
                    } else {
                        DogoBot.jdaOutputThread.submit {
                            val lang = LanguageEntry("text")
                            event.channel.sendMessage(
                                    EmbedBuilder()
                                            .setColor(Color.YELLOW)
                                            .setTitle(lang.getText(user.lang, "error.commandforbidden.title"))
                                            .setDescription(lang.getText(user.lang, "error.commandforbidden.description", cmd.getPermission()))
                                            .build()
                            ).queue({}, {})
                        }
                    }
                }
            }
        }
    }
}