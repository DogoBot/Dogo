package dev.nathanpb.dogo.core.command

import dev.nathanpb.dogo.security.PermGroupSet
import dev.nathanpb.dogo.discord.DiscordManager
import dev.nathanpb.dogo.discord.lang
import dev.nathanpb.dogo.discord.sendDontCareAboutIt
import dev.nathanpb.dogo.lang.BoundLanguage
import dev.nathanpb.dogo.lang.LanguageEntry
import dev.nathanpb.dogo.utils.Holder
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.PrivateChannel
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.awt.Color

/**
 * Class responsible on generating and executing commands.
 *
 * @param[manager] its manager.
 */
class CommandFactory(val manager: CommandManager) {

    fun onMessage(event : MessageReceivedEvent){
        val prefix = CommandManager.getCommandPrefixes(event.guild).firstOrNull {
            event.message.contentRaw.startsWith(it)
        }

        prefix?.let {
            CommandManager.cmdProcessorThread.execute {
                if(event.guild?.getMember(DiscordManager.jda?.selfUser)?.hasPermission(Permission.MESSAGE_WRITE) != true){
                    return@execute
                }
                val text = event.message.contentRaw.replaceFirst(prefix, "")
                val argsHolder = Holder<Int>()
                val route = manager.route.findRoute(text, argsHolder)
                val pgs = PermGroupSet.find(event.author, event.guild)


                route.let { cmd ->
                    if(pgs.can(cmd.getPermission())){
                        val args = text.split(" ").filterIndexed { index, _ -> index >= argsHolder.hold()}
                        if(args.size >= cmd.reference.args){

                            //Checks if its a NSFW command being executed outside NSFW Channel or a private one
                            if(cmd.reference.category == CommandCategory.NSFW){
                                if(event.channel !is PrivateChannel){
                                    if(!(event.channel is TextChannel && (event.channel as TextChannel).isNSFW)){
                                        DiscordManager.jdaOutputThread.execute {
                                            event.channel.sendMessage(BoundLanguage(event.author.lang, "text").getText("error.notnsfwchannel")).queue({}, {})
                                        }
                                        return@let
                                    }
                                }
                            }

                            //Checks if its a guild administration command being executed outside guilds
                            if(cmd.reference.category == CommandCategory.GUILD_ADMINISTRATION && event.guild == null) {
                                DiscordManager.jdaOutputThread.execute {
                                    event.channel.sendDontCareAboutIt(LanguageEntry("text").getTextIn(event.author.lang, "error.guildrequired"))
                                }
                                return@let
                            }

                            if(!route.isRoot()){
                                try {
                                    val context = CommandContext(event.message, route, args)
                                    cmd.run?.command?.invoke(context)
                                } catch(ex: Exception) {
                                    DiscordManager.jdaOutputThread.execute {
                                        //todo report
                                        val lang = BoundLanguage(event.author.lang, "text")
                                        EmbedBuilder()
                                                .setColor(Color.YELLOW)
                                                .setTitle(lang.getText("error.commandfailed.title"))
                                                .setDescription(lang.getText("error.commandfailed.description", ex.message.orEmpty()))
                                                .let { event.channel.sendDontCareAboutIt(it.build()) }
                                    }
                                }
                            }

                        } else {
                            DiscordManager.jdaOutputThread.execute {
                                val lang = BoundLanguage(event.author.lang, "text")
                                EmbedBuilder()
                                        .setColor(Color.YELLOW)
                                        .setTitle(lang.getText("error.argsmissing.title"))
                                        .setDescription(lang.getText("error.argsmissing.description", args.size, cmd.reference.args))
                                        .let { event.channel.sendDontCareAboutIt(it.build()) }
                            }
                        }
                    } else {
                        DiscordManager.jdaOutputThread.execute {
                            val lang = BoundLanguage(event.author.lang, "text")
                            EmbedBuilder()
                                    .setColor(Color.YELLOW)
                                    .setTitle(lang.getText("error.commandforbidden.title"))
                                    .setDescription(lang.getText("error.commandforbidden.description", cmd.getPermission()))
                                    .let { event.channel.sendDontCareAboutIt(it.build()) }
                        }
                    }
                }
            }
        }
    }
}