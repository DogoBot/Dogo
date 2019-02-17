package io.github.dogo.core.boot

import io.github.dogo.core.DogoBot
import io.github.dogo.core.listeners.JDAListener
import io.github.dogo.core.command.CommandCategory
import io.github.dogo.core.command.CommandReference
import io.github.dogo.lang.LanguageEntry
import io.github.dogo.server.APIServer
import io.github.dogo.utils.Holder
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import org.jetbrains.exposed.sql.Database
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


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
 *
 * Class that initializes Dogo.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class Boot {
    /**
     * The list of phases on boot process.
     *
     * @see Phase
     */
    private val phaseList = listOf(
            Phase("Initializing JDA") {
                io.github.dogo.core.DogoBot.jda = JDABuilder(AccountType.BOT)
                        .setToken(DogoBot.data.BOT_TOKEN)
                        .setGame(Game.watching("myself starting"))
                        .addEventListener(JDAListener(DogoBot.eventBus))
                        .build().awaitReady()
            },
            Phase("Connecting to Database") {
                DogoBot.db = Database.connect(
                        "jdbc:mysql://${DogoBot.data.DB.HOST}:${DogoBot.data.DB.PORT}/${DogoBot.data.DB.NAME}",
                        driver = "com.mysql.jdbc.Driver",
                        user = DogoBot.data.DB.USER,
                        password = DogoBot.data.DB.PWD
                )
            },
            Phase("Registering Random Event Listeners"){
                DogoBot.eventBus.register(io.github.dogo.core.listeners.BadwordListener::listenSend)
                DogoBot.eventBus.register(DogoBot.cmdFactory::onMessage)
            },
            Phase("Loading Language Assets") {
                LanguageEntry.load()
            },
            Phase("Registering Commands") {
              DogoBot.cmdFactory.route {
                  route(io.github.dogo.commands.Help())
                  route(io.github.dogo.commands.TicTacToe())
                  route(io.github.dogo.commands.Stats())
                  route(io.github.dogo.commands.Roles())
                  route(CommandReference("trasleite", category = CommandCategory.FUN, permission = "command")){
                      execute {
                          if(java.util.Random().nextInt(1000) == 1){
                              this.reply("nope", preset = true)
                          } else {
                              this.reply("milk", preset = true)
                          }
                      }
                  }
                  route(CommandReference("route", category = CommandCategory.OWNER, permission = "command.admin")){
                      execute {
                          if(this.args.isEmpty()){
                              DogoBot.cmdFactory.route
                          } else {
                              var s = ""
                              this.args.forEach { a -> s+="$a " }
                              if(s.isNotEmpty()) {
                                  s = s.substring(0, s.length - 1)
                              }
                              DogoBot.cmdFactory.route.findRoute(s, Holder())
                          }.let { r -> this.reply("```json\n$r\n```") }
                      }
                  }
                  route(CommandReference("shutdown", category = CommandCategory.OWNER, permission = "command.admin.root")){
                      execute {
                          replySynk("msg", preset = true)
                          DogoBot.logger.warn("Dogo is shutting down! Called by ${sender.id} in channel ${replyChannel.id}, guild ${guild?.id}, message ${msg.id}")
                          System.exit(0)
                      }
                  }
                  route(CommandReference("restart", category = CommandCategory.OWNER, permission = "command.admin.root")){
                      execute {
                          replySynk("msg", preset = true)
                          DogoBot.logger.warn("Dogo is restarting! Called by ${sender.id} in channel ${replyChannel.id}, guild ${guild?.id}, message ${msg.id}")
                          System.exit(3)
                      }
                  }
                  route(CommandReference("eval", args = 1, category = CommandCategory.OWNER, permission = "command.admin.root")){
                      route(io.github.dogo.commands.Eval.KotlinEval())
                  }
                  route(io.github.dogo.commands.WebhookEcho()){
                      route(io.github.dogo.commands.WebhookEcho.Configure())
                      route(io.github.dogo.commands.WebhookEcho.Current())
                  }
                  route(io.github.dogo.commands.Badwords()){
                      route(io.github.dogo.commands.Badwords.Add())
                      route(io.github.dogo.commands.Badwords.Remove())
                  }
              }
            },
            Phase("Initializing API"){
                DogoBot.apiServer = APIServer().also { it.start() }
            }
    )

    init {
        Thread.currentThread().name = "Boot"
        System.setProperty("logFile", File(File(DogoBot.data.LOGGER_PATH), SimpleDateFormat("dd-MM-YYYY HH-mm-ss").format(Date())).absolutePath)

        //Take Thread Dump and Heap Dump every 30 minutes
        Timer().scheduleAtFixedRate(object: TimerTask(){
            override fun run() {
               DogoBot.takeDumps()
            }
        }, 0, 1800000.toLong())


        DogoBot.logger.info("Starting Dogo v${DogoBot.version} on PID ${DogoBot.pid}")

        if(DogoBot.data.DEBUG_PROFILE){
            DogoBot.logger.warn("DEBUG PROFILE IS ACTIVE")
        }

        try {
            startup()
        } catch (ex : java.lang.Exception){
            DogoBot.logger.fatal("STARTUP FAILED", ex)
            System.exit(1)
        }
    }

    /**
     * Executes each phase
     * @see phaseList
     */
    @Throws(Exception::class)
    fun startup() {
        DogoBot.logger.info(
                "\n  _____                    ____        _   \n" +
                " |  __ \\                  |  _ \\      | |  \n" +
                " | |  | | ___   __ _  ___ | |_) | ___ | |_ \n" +
                " | |  | |/ _ \\ / _  |/ _ \\|  _ < / _ \\| __|\n" +
                " | |__| | (_) | (_| | (_) | |_) | (_) | |_ \n" +
                " |_____/ \\___/ \\__, |\\___/|____/ \\___/ \\__|\n" +
                "                __/ |                      \n" +
                "               |___/                    \n" +
                "By NathanPB - https://github.com/DogoBot/Dogo")

        var count = 1

        for(phase in phaseList){
            val time = System.currentTimeMillis()
            DogoBot.logger.info("["+count+"/"+phaseList.size+"] " + phase.display)

            val now = Holder<Int>().apply { hold(count) }
            object : TimerTask() {
                override fun run() {
                    if(now.hold() == count)
                        DogoBot.jda?.presence?.game = Game.watching("myself starting - ${phase.display}")
                }
            }.let { Timer().schedule(it, 3000) }

            phase.start()
            DogoBot.logger.info("["+count+"/"+phaseList.size+"] Done in ${time.timeSince()}")
            count++
        }
        DogoBot.ready = true
        DogoBot.jda!!.presence.game = Game.watching("to ${DogoBot.jda!!.guilds?.size} guilds | dg!help")
        DogoBot.logger.info("Dogo is Done! ${io.github.dogo.core.DogoBot.initTime.timeSince()}")
    }


    /*
     extension shit
     */

    /**
     * Get the delay between a long and the current time and formats it to a human-readable format.
     *
     * @return the delay between [this] and the current time in a human-readable format.
     */
    private fun Long.timeSince() : String {
        val time = System.currentTimeMillis() - this
        return when {
            time < 1000 -> time.toString()+"ms"
            time < 60000 -> TimeUnit.MILLISECONDS.toSeconds(time).toString()+"sec"
            else -> TimeUnit.MILLISECONDS.toMinutes(time).toString()+"min"
        }
    }
}

/**
 * Hello Java
 */
fun main(){
    Boot()
}