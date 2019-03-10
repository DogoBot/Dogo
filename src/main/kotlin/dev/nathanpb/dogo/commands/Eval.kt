package dev.nathanpb.dogo.commands

import com.mashape.unirest.http.Unirest
import com.sun.org.glassfish.external.statistics.Statistic
import dev.nathanpb.dogo.discord.badwords.BadwordListener
import dev.nathanpb.dogo.core.DogoBot
import dev.nathanpb.dogo.core.command.*
import dev.nathanpb.dogo.core.data.DogoData
import dev.nathanpb.dogo.core.eventBus.EventBus
import dev.nathanpb.dogo.security.PermGroup
import dev.nathanpb.dogo.security.PermGroupSet
import dev.nathanpb.dogo.discord.DiscordException
import dev.nathanpb.dogo.discord.DiscordManager
import dev.nathanpb.dogo.discord.IRepliable
import dev.nathanpb.dogo.discord.JDAListener
import dev.nathanpb.dogo.discord.menus.ListReactionMenu
import dev.nathanpb.dogo.discord.menus.SelectorReactionMenu
import dev.nathanpb.dogo.discord.menus.SimpleReactionMenu
import dev.nathanpb.dogo.lang.LanguageEntry
import dev.nathanpb.dogo.minigames.tictactoe.ITTTImp
import dev.nathanpb.dogo.minigames.tictactoe.OnePlayerTTT
import dev.nathanpb.dogo.minigames.tictactoe.TTTPlayer
import dev.nathanpb.dogo.minigames.tictactoe.TwoPlayersTTT
import dev.nathanpb.dogo.minigames.tictactoe.discord.TicTacToeImp
import dev.nathanpb.dogo.server.APIException
import dev.nathanpb.dogo.server.APIRequestProcessor
import dev.nathanpb.dogo.server.APIServer
import dev.nathanpb.dogo.server.Token
import dev.nathanpb.dogo.statistics.TicTacToeStatistics
import dev.nathanpb.dogo.utils.Holder
import dev.nathanpb.dogo.utils._static.*
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.User
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import org.jsoup.Jsoup
import java.awt.Color
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URL
import javax.script.Invocable

class Eval {

    companion object {
        val imports = arrayOf(
                BadwordListener::class,
                CommandCategory::class,
                CommandContext::class,
                CommandReference::class,
                CommandRouter::class,
                ReferencedCommand::class,
                DogoData::class,
                EventBus::class,
                PermGroup::class,
                PermGroupSet::class,
                dev.nathanpb.dogo.core.DogoBot::class,
                JDAListener::class,
                APIException::class,
                DiscordException::class,
                IRepliable::class,
                LanguageEntry::class,
                ListReactionMenu::class,
                SelectorReactionMenu::class,
                SimpleReactionMenu::class,
                TicTacToeImp::class,
                ITTTImp::class,
                OnePlayerTTT::class,
                TTTPlayer::class,
                dev.nathanpb.dogo.commands.TicTacToe::class,
                TwoPlayersTTT::class,
                Token::class,
                APIRequestProcessor::class,
                APIServer::class,
                Statistic::class,
                TicTacToeStatistics::class,
                BeamUtils::class,
                DiscordAPI::class,
                DisplayUtils::class,
                EmoteReference::class,
                FacebookUtils::class,
                FileUtils::class,
                HastebinUtils::class,
                Holder::class,
                SystemUtils::class,
                ThemeColor::class,
                UnitUtils::class,

                Guild::class,
                Message::class,
                User::class,
                EmbedBuilder::class,
                Game::class,

                File::class,
                URL::class,

                Unirest::class,
                Jsoup::class,
                DriveUtils::class
        ).joinToString("") { "import ${it.qualifiedName}\n" }
    }

    class KotlinEval : ReferencedCommand(
            CommandReference("kotlin", aliases = "kt", args = 1, permission = "command.admin.root"),
            {
                val embedPast = replySynk(EmbedBuilder().setColor(Color.YELLOW).setTitle(langEntry.getText("evaluating")).build())
                System.setProperty("idea.io.use.fallback", "true")
                val desc = StringBuilder()
                EmbedBuilder()
                        .setAuthor(langEntry.getText("title"))
                        .setColor(Color.GREEN)
                        .also { embed ->
                            try {
                                ((KotlinJsr223JvmLocalScriptEngineFactory()
                                        .scriptEngine
                                        .also {
                                            var code = args.joinToString(" ")
                                            if(code.startsWith("\n```")){
                                                code = code.replaceFirst(code.split("\n")[1], "")
                                                code = code.substring(0, code.length-3)
                                            }
                                            it.eval(dev.nathanpb.dogo.commands.Eval.KotlinEval.Companion.formatCode(code))
                                        } as Invocable)
                                        .invokeFunction("run", this).let { if(it is Unit) null else it } ?: langEntry.getText("noreturn"))
                                        .let { desc.append(it) }
                            } catch (ex: Exception) {
                                embed.setColor(Color.RED)
                                desc.append(StringWriter().also { ex.printStackTrace(PrintWriter(it)) }.toString())
                            }
                        }.let {
                            it.setDescription(
                                langEntry.getText("result")+
                                if(desc.length > 1500){
                                    " [Hastebin](${HastebinUtils.URL}${HastebinUtils.upload(desc.toString())})"
                                } else "\n```$desc```"
                            )
                            DiscordManager.jdaOutputThread.submit {
                                embedPast.editMessage(
                                        it.setFooter(langEntry.getText("took", UnitUtils.timeSince(embedPast.creationTime.toInstant().toEpochMilli())/1000), null)
                                        .setAuthor(langEntry.getText("title"), "https://kotlinlang.org", "https://kotlinlang.org/assets/images/open-graph/kotlin_250x250.png")
                                        .build()
                                ).complete()
                            }
                        }
            }
    ) {
        companion object {
            fun formatCode(code: String) = """
            ${dev.nathanpb.dogo.commands.Eval.Companion.imports}
            val a: CommandContext.()->Any? = {
                $code
            }
            fun run(cmd: CommandContext) = a(cmd)
        """.trimIndent()
        }
    }
}