package dev.nathanpb.dogo.core.boot

import dev.nathanpb.dogo.discord.badwords.BadwordListener
import dev.nathanpb.dogo.core.DogoBot
import dev.nathanpb.dogo.core.command.CommandCategory
import dev.nathanpb.dogo.core.command.CommandReference
import dev.nathanpb.dogo.core.database.DatabaseConnection
import dev.nathanpb.dogo.discord.DiscordManager
import dev.nathanpb.dogo.lang.LanguageEntry
import dev.nathanpb.dogo.server.APIServer
import dev.nathanpb.dogo.utils.Holder
import dev.nathanpb.dogo.utils._static.BeamUtils
import net.dv8tion.jda.core.entities.Game
import java.io.File
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
                DiscordManager.connect(dev.nathanpb.dogo.core.DogoBot.data.BOT_TOKEN)
            },
            Phase("Connecting to Tables") {
                dev.nathanpb.dogo.core.DogoBot.data.DB.apply {
                    DatabaseConnection.connect(HOST, PORT, NAME, USER, PWD)
                }
            },
            Phase("Registering Random Event Listeners"){
                dev.nathanpb.dogo.core.DogoBot.eventBus.register(BadwordListener::listenSend)
                dev.nathanpb.dogo.core.DogoBot.eventBus.register(dev.nathanpb.dogo.core.DogoBot.cmdFactory::onMessage)
            },
            Phase("Loading Language Assets") {
                LanguageEntry.load()
            },
            Phase("Registering Commands") {
              dev.nathanpb.dogo.core.DogoBot.cmdManager.route {
                  route(dev.nathanpb.dogo.commands.Help())
                  route(dev.nathanpb.dogo.commands.TicTacToe())
                  route(dev.nathanpb.dogo.commands.Stats())
                  route(dev.nathanpb.dogo.commands.Roles())
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
                              dev.nathanpb.dogo.core.DogoBot.cmdManager.route
                          } else {
                              var s = ""
                              this.args.forEach { a -> s+="$a " }
                              if(s.isNotEmpty()) {
                                  s = s.substring(0, s.length - 1)
                              }
                              dev.nathanpb.dogo.core.DogoBot.cmdManager.route.findRoute(s, Holder())
                          }.let { r -> this.reply("```json\n$r\n```") }
                      }
                  }
                  route(CommandReference("shutdown", category = CommandCategory.OWNER, permission = "command.admin.root")){
                      execute {
                          replySynk("msg", preset = true)
                          dev.nathanpb.dogo.core.DogoBot.logger.warn("Dogo is shutting down! Called by ${sender.id} in channel ${replyChannel.id}, guild ${guild.id}, message ${msg.id}")
                          System.exit(0)
                      }
                  }
                  route(CommandReference("restart", category = CommandCategory.OWNER, permission = "command.admin.root")){
                      execute {
                          replySynk("msg", preset = true)
                          dev.nathanpb.dogo.core.DogoBot.logger.warn("Dogo is restarting! Called by ${sender.id} in channel ${replyChannel.id}, guild ${guild.id}, message ${msg.id}")
                          System.exit(3)
                      }
                  }
                  route(CommandReference("eval", args = 1, category = CommandCategory.OWNER, permission = "command.admin.root")){
                      route(dev.nathanpb.dogo.commands.Eval.KotlinEval())
                  }
                  route(dev.nathanpb.dogo.commands.WebhookEcho()){
                      route(dev.nathanpb.dogo.commands.WebhookEcho.Configure())
                      route(dev.nathanpb.dogo.commands.WebhookEcho.Current())
                  }
                  route(dev.nathanpb.dogo.commands.Badwords()){
                      route(dev.nathanpb.dogo.commands.Badwords.Add())
                      route(dev.nathanpb.dogo.commands.Badwords.Remove())
                  }
              }
            },
            Phase("Initializing API"){
                dev.nathanpb.dogo.core.DogoBot.apiServer = APIServer().also { it.start() }
            },
            Phase("Inizializing dump logs"){
                Timer().scheduleAtFixedRate(object: TimerTask(){
                    override fun run() {
                        BeamUtils.takeHeapDump()
                    }
                }, dev.nathanpb.dogo.core.DogoBot.data.DUMPS.PERIOD, dev.nathanpb.dogo.core.DogoBot.data.DUMPS.PERIOD)
            }
    )

    init {
        Thread.currentThread().name = "Boot"
        System.setProperty("logFile", File(File(dev.nathanpb.dogo.core.DogoBot.data.LOGGER_PATH), SimpleDateFormat("dd-MM-YYYY HH-mm-ss").format(Date())).absolutePath)

        dev.nathanpb.dogo.core.DogoBot.logger.info("Starting Dogo v${dev.nathanpb.dogo.core.DogoBot.version} on PID ${BeamUtils.pid}")
        if(dev.nathanpb.dogo.core.DogoBot.data.DEBUG_PROFILE){
            dev.nathanpb.dogo.core.DogoBot.logger.warn("DEBUG PROFILE IS ENABLED")
        }

        try {
            startup()
        } catch (ex : java.lang.Exception){
            dev.nathanpb.dogo.core.DogoBot.logger.fatal("STARTUP FAILED", ex)
            System.exit(1)
        }
    }

    /**
     * Executes each phase
     * @see phaseList
     */
    @Throws(Exception::class)
    fun startup() {
        dev.nathanpb.dogo.core.DogoBot.logger.info(
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
            dev.nathanpb.dogo.core.DogoBot.logger.info("["+count+"/"+phaseList.size+"] " + phase.display)

            val now = Holder<Int>().apply { hold(count) }
            object : TimerTask() {
                override fun run() {
                    if(now.hold() == count)
                        DiscordManager.jda?.presence?.game = Game.watching("myself starting - ${phase.display}")
                }
            }.let { Timer().schedule(it, 3000) }

            phase.start()
            dev.nathanpb.dogo.core.DogoBot.logger.info("["+count+"/"+phaseList.size+"] Done in ${time.timeSince()}")
            count++
        }
        dev.nathanpb.dogo.core.DogoBot.ready = true
        DiscordManager.jda?.presence?.game = Game.watching("to ${DiscordManager.jda!!.guilds?.size} guilds | dg!help")
        dev.nathanpb.dogo.core.DogoBot.logger.info("Dogo is Done! ${dev.nathanpb.dogo.core.DogoBot.initTime.timeSince()}")
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