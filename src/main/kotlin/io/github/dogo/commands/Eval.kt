package io.github.dogo.commands

import com.mashape.unirest.http.Unirest
import com.sun.org.glassfish.external.statistics.Statistic
import io.github.dogo.badwords.BadwordFinder
import io.github.dogo.badwords.BadwordListener
import io.github.dogo.badwords.BadwordProfile
import io.github.dogo.core.DogoBot
import io.github.dogo.core.JDAListener
import io.github.dogo.core.command.*
import io.github.dogo.core.data.DogoData
import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.core.eventBus.EventBus
import io.github.dogo.core.profiles.PermGroup
import io.github.dogo.core.profiles.PermGroupSet
import io.github.dogo.exceptions.APIException
import io.github.dogo.exceptions.DiscordException
import io.github.dogo.interfaces.IFinder
import io.github.dogo.interfaces.IRepliable
import io.github.dogo.lang.LanguageEntry
import io.github.dogo.menus.ListReactionMenu
import io.github.dogo.menus.SimpleReactionMenu
import io.github.dogo.minigames.tictactoe.ITTTImp
import io.github.dogo.minigames.tictactoe.OnePlayerTTT
import io.github.dogo.minigames.tictactoe.TTTPlayer
import io.github.dogo.minigames.tictactoe.TwoPlayersTTT
import io.github.dogo.minigames.tictactoe.discord.TicTacToeImp
import io.github.dogo.server.APIRequestProcessor
import io.github.dogo.server.APIServer
import io.github.dogo.server.token.Token
import io.github.dogo.server.token.TokenFinder
import io.github.dogo.statistics.StatisticsFinder
import io.github.dogo.statistics.TicTacToeStatistics
import io.github.dogo.utils.*
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.User
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.awt.Color
import java.io.PrintWriter
import java.io.StringWriter
import javax.script.Invocable

class Eval {

    companion object {
        val imports = arrayOf(
                BadwordFinder::class,
                BadwordListener::class,
                BadwordProfile::class,
                CommandCategory::class,
                CommandContext::class,
                CommandReference::class,
                CommandRouter::class,
                ReferencedCommand::class,
                DogoData::class,
                DogoGuild::class,
                DogoUser::class,
                EventBus::class,
                PermGroup::class,
                PermGroupSet::class,
                DogoBot::class,
                JDAListener::class,
                APIException::class,
                DiscordException::class,
                IFinder::class,
                IRepliable::class,
                LanguageEntry::class,
                ListReactionMenu::class,
                SimpleReactionMenu::class,
                TicTacToeImp::class,
                ITTTImp::class,
                OnePlayerTTT::class,
                TTTPlayer::class,
                TicTacToe::class,
                TwoPlayersTTT::class,
                Token::class,
                TokenFinder::class,
                APIRequestProcessor::class,
                APIServer::class,
                Statistic::class,
                StatisticsFinder::class,
                TicTacToeStatistics::class,
                BeamUtils::class,
                DiscordAPI::class,
                DisplayUtils::class,
                EmoteReference::class,
                FileUtils::class,
                HastebinUtils::class,
                Holder::class,
                SystemUtils::class,
                ThemeColor::class,
                UnitUtils::class,

                Guild::class,
                Message::class,
                User::class,

                Unirest::class
        )
                .map { "import ${it.qualifiedName}\n" }
                .joinToString("")
    }

    class KotlinEval : ReferencedCommand(
            CommandReference("kotlin", aliases = "kt", args = 1),
            {
                System.setProperty("idea.io.use.fallback", "true")
                val desc = StringBuilder()
                EmbedBuilder()
                        .setFooter("Kotlin Evaluation", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Kotlin-logo.svg/220px-Kotlin-logo.svg.png000000")
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
                                            it.eval(formatCode(code))
                                        } as Invocable)
                                        .invokeFunction("run", this).let { if(it is Unit) null else it } ?: "No Returns")
                                        .let { desc.append(it) }
                            } catch (ex: Exception) {
                                embed.setColor(Color.RED)
                                desc.append(StringWriter().also { ex.printStackTrace(PrintWriter(it)) }.toString())
                            }
                        }.let {
                            it.setDescription(
                                    "Result: "+
                                            if(desc.length > 1500){
                                                "[Hastebin](${HastebinUtils.URL}${HastebinUtils.upload(it.descriptionBuilder.toString())})"
                                            } else "\n```$desc```"
                            )
                            reply(it.build())
                        }
            }
    ) {
        companion object {
            fun formatCode(code: String) = """
            $imports
            val a: CommandContext.()->Any? = {
                $code
            }
            fun run(cmd: CommandContext) = a(cmd)
        """.trimIndent()
        }
    }
}