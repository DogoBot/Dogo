package cf.nathanpb.dogo.core.cmdHandler

import cf.nathanpb.dogo.commands.Help
import cf.nathanpb.dogo.core.DogoBot
import cf.nathanpb.dogo.core.entities.DogoGuild
import cf.nathanpb.dogo.core.entities.DogoUser
import cf.nathanpb.dogo.core.eventBus.EventBus
import cf.nathanpb.dogo.core.profiles.PermGroup
import cf.nathanpb.dogo.core.profiles.PermGroupSet
import cf.nathanpb.dogo.lang.LanguageEntry
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.entities.PrivateChannel
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.awt.Color
import kotlin.reflect.KClass

class CommandFactory (bus : EventBus){
    val busId = bus.register(this).first()
    val commands = HashMap<KClass<*>, DogoCommand>()


    @EventBus.Listener(0)
    fun onMessage(event : MessageReceivedEvent){
        var prefix : String? = null
        var prefixes = DogoBot.instance.getCommandPrefixes()
        if(event.guild != null){
            prefixes = DogoBot.instance.getCommandPrefixes(DogoGuild(event.guild))
        }
        for(p in prefixes){
            if(event.message.contentRaw.startsWith(p)){
                prefix = p
                break
            }
        }
        if(prefix != null) {
            DogoBot.cmdProcessorThread.submit {
                var text = event.message.contentRaw.replaceFirst(prefix, "")
                val tree = CommandTree(text, this)
                val user = DogoUser(event.author)
                val guild: DogoGuild? = if (event.guild == null) null else DogoGuild(event.guild)
                val pgs = user.getPermGroups()
                if (guild != null) pgs.addAll(guild.permgroups.filterApplied(user.id))
                for (cmd in tree) {
                    if (pgs.can(cmd.getPermission())) {
                        if (tree.args.size >= cmd.minArgumentsSize) {
                            if(cmd.category == CommandCategory.NSFW){
                                if(!(event.channel is PrivateChannel)){
                                    if(!(event.channel is TextChannel && (event.channel as TextChannel).isNSFW)){
                                        DogoBot.jdaOutputThread.submit {
                                            val lang = LanguageEntry("text")
                                            event.channel.sendMessage(lang.getText(user.lang, "error.notnsfwchannel")).queue({}, {})
                                        }
                                        break
                                    }
                                }
                            }
                            if(cmd.category == CommandCategory.GUILD_ADMINISTRATION && guild == null) {
                                DogoBot.jdaOutputThread.submit {
                                    val lang = LanguageEntry("text")
                                    event.channel.sendMessage(lang.getText(user.lang, "error.guildrequired")).queue({}, {})
                                }
                                break
                            }

                            val context = CommandContext(event.message, tree)
                            cmd.execute(context)
                            context.next()
                        } else {
                            DogoBot.jdaOutputThread.submit {
                                val lang = LanguageEntry("text")
                                event.channel.sendMessage(
                                        EmbedBuilder()
                                                .setColor(Color.YELLOW)
                                                .setTitle(lang.getText(user.lang, "error.argsmissing.title"))
                                                .setDescription(lang.getText(user.lang, "error.argsmissing.description", tree.args.size, cmd.minArgumentsSize))
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
                                            .setDescription(lang.getText(user.lang, "error.commandforbidden.description", user.usr?.name as Any, cmd.getPermission()))
                                            .build()
                            ).queue({}, {})
                        }
                    }
                }
            }
        }
    }
}