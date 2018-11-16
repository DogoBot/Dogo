package cf.dogo.core.cmdHandler

import cf.dogo.core.entities.DogoGuild
import cf.dogo.core.entities.DogoUser
import cf.dogo.core.eventBus.EventBus
import cf.dogo.lang.LanguageEntry
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.PrivateChannel
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.awt.Color
import kotlin.reflect.KClass

class CommandFactory (bus : EventBus){
    val busId = bus.register(this).first()
    val commands = HashMap<KClass<*>, DogoCommand>()

    fun registerCommand(cmd : DogoCommand) {
        commands[cmd::class] = cmd
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
        if(prefix != null) {
            cf.dogo.core.DogoBot.cmdProcessorThread.submit {
                val text = event.message.contentRaw.replaceFirst(prefix, "")
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
                                        cf.dogo.core.DogoBot.jdaOutputThread.submit {
                                            val lang = LanguageEntry("text")
                                            event.channel.sendMessage(lang.getText(user.lang, "error.notnsfwchannel")).queue({}, {})
                                        }
                                        break
                                    }
                                }
                            }
                            if(cmd.category == CommandCategory.GUILD_ADMINISTRATION && guild == null) {
                                cf.dogo.core.DogoBot.jdaOutputThread.submit {
                                    val lang = LanguageEntry("text")
                                    event.channel.sendMessage(lang.getText(user.lang, "error.guildrequired")).queue({}, {})
                                }
                                break
                            }

                            val context = CommandContext(event.message, tree)
                            cmd.execute(context)
                            context.next()
                        } else {
                            cf.dogo.core.DogoBot.jdaOutputThread.submit {
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
                        cf.dogo.core.DogoBot.jdaOutputThread.submit {
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