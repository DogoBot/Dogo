package io.github.dogo.core.command

import io.github.dogo.core.DogoBot
import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.core.eventBus.EventBus
import io.github.dogo.lang.BoundLanguage
import io.github.dogo.lang.LanguageEntry
import io.github.dogo.utils.Holder
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.Permission
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

        var prefixes = io.github.dogo.core.DogoBot.getCommandPrefixes()
        if(event.guild != null){
            prefixes = io.github.dogo.core.DogoBot.getCommandPrefixes(DogoGuild(event.guild))
        }
        for(p in prefixes){
            if(event.message.contentRaw.startsWith(p)){
                prefix = p
                break
            }
        }

        prefix?.let {
            DogoBot.cmdProcessorThread.execute {
                if(event.guild?.getMember(DogoBot.jda!!.selfUser)?.hasPermission(Permission.MESSAGE_WRITE) != true){
                    return@execute
                }
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
                                        DogoBot.jdaOutputThread.execute {
                                            event.channel.sendMessage(BoundLanguage(user.lang, "text").getText("error.notnsfwchannel")).queue({}, {})
                                        }
                                        return@let
                                    }
                                }
                            }

                            //todo temporary shit
                            if(cmd.reference.category == CommandCategory.GUILD_ADMINISTRATION && guild != null){
                                if(!guild.g!!.getMember(user.usr!!).isOwner) {
                                    return@execute
                                }
                            }

                            if(cmd.reference.category == CommandCategory.GUILD_ADMINISTRATION && guild == null) {
                                DogoBot.jdaOutputThread.execute {
                                    val lang = LanguageEntry("text")
                                    event.channel.sendMessage(lang.getTextIn(user.lang, "error.guildrequired")).queue({}, {})
                                }
                                return@let
                            }
                            if(cmd.reference.category == CommandCategory.OWNER && !user.getPermGroups().can("commands.admin.root")){
                                DogoBot.jdaOutputThread.execute {
                                    val lang = BoundLanguage(user.lang, "text")
                                    EmbedBuilder()
                                            .setColor(Color.YELLOW)
                                            .setTitle(lang.getText("error.commandforbidden.title"))
                                            .setDescription(lang.getText("error.commandforbidden.description", "command.admin.root"))
                                }
                                return@let
                            }
                            if(!route.isRoot()){
                                val context = CommandContext(event.message, route, args)
                                cmd.run?.let { it.command(context) }
                            }

                        } else {
                            DogoBot.jdaOutputThread.execute {
                                val lang = BoundLanguage(user.lang, "text")
                                event.channel.sendMessage(
                                        EmbedBuilder()
                                                .setColor(Color.YELLOW)
                                                .setTitle(lang.getText("error.argsmissing.title"))
                                                .setDescription(lang.getText("error.argsmissing.description", args.size, cmd.reference.args))
                                                .build()
                                ).queue({}, {})
                            }
                        }
                    } else {
                        DogoBot.jdaOutputThread.execute {
                            val lang = BoundLanguage(user.lang, "text")
                            event.channel.sendMessage(
                                    EmbedBuilder()
                                            .setColor(Color.YELLOW)
                                            .setTitle(lang.getText("error.commandforbidden.title"))
                                            .setDescription(lang.getText("error.commandforbidden.description", cmd.getPermission()))
                                            .build()
                            ).queue({}, {})
                        }
                    }
                }
            }
        }
    }
}