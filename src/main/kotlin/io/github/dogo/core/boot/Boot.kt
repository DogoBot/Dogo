package io.github.dogo.core.boot

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import io.github.dogo.core.DogoBot
import io.github.dogo.core.JDAListener
import io.github.dogo.core.command.CommandCategory
import io.github.dogo.core.command.CommandReference
import io.github.dogo.core.profiles.PermGroup
import io.github.dogo.server.APIServer
import io.github.dogo.utils.Holder
import io.github.dogo.utils.WebUtils
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
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
     * The list of phases on boot proccess.
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
                DogoBot.mongoClient = MongoClient(
                        ServerAddress(DogoBot.data.DB.HOST, DogoBot.data.DB.PORT),
                        MongoCredential.createCredential(
                                DogoBot.data.DB.USER,
                                DogoBot.data.DB.NAME,
                                DogoBot.data.DB.PWD.toCharArray()
                        ),
                        MongoClientOptions.builder().build()
                ).also {
                    DogoBot.db = it.getDatabase(DogoBot.data.DB.NAME)
                }
            },
            Phase("Checking Database") {
                DogoBot.db?.checkCollection("users")
                DogoBot.db?.checkCollection("guilds")
                DogoBot.db?.checkCollection("permgroups")
                DogoBot.db?.checkCollection("stats")
                DogoBot.db?.checkCollection("tokens")
                DogoBot.db?.checkCollection("badwords")
            },
            Phase("Registering Commands"){
              DogoBot.cmdFactory.route {
                  route(io.github.dogo.commands.Help())
                  route(io.github.dogo.commands.TicTacToe())
                  route(io.github.dogo.commands.Stats())
                  route(CommandReference("trasleite", category = CommandCategory.FUN)){
                      execute {
                          if(java.util.Random().nextInt(1000) == 1){
                              this.reply("nope", preset = true)
                          } else {
                              this.reply("milk", preset = true)
                          }
                      }
                  }
                  route(CommandReference("route", category = CommandCategory.OWNER)){
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
                  route(CommandReference("update", category=CommandCategory.OWNER)){
                      execute {
                          reply(":warning: Preparing to build...")
                          WebUtils.get("${DogoBot.data.JENKINS.URL}/job/${DogoBot.data.JENKINS.JOB_NAME}/build?token=${DogoBot.data.JENKINS.AUTH_TOKEN}")
                          reply("<:nathanbb:390267731846627329> Restarting...")
                          DogoBot.logger.warn("Dogo is restarting to apply new build!")
                          System.exit(3)
                      }
                  }
                  route(io.github.dogo.commands.Badwords()){
                      route(io.github.dogo.commands.Badwords.Add())
                      route(io.github.dogo.commands.Badwords.Remove())
                  }
              }
            },
            Phase("Setting up Permgroups") {
                PermGroup("0").apply {
                    name = "default"
                    priority = 0
                    applyTo = arrayListOf("everyone")
                    include = arrayListOf("command.*")
                    exclude = arrayListOf("command.admin.*")
                }
                PermGroup("-1").apply {
                    name = "admin"
                    include = arrayListOf("command.admin.*", "badword.bypass")
                    exclude = arrayListOf("command.admin.root.*")
                    priority = -1
                }
                PermGroup("-2").apply {
                    name = "root"
                    applyTo = arrayListOf(DogoBot.data.OWNER_ID)
                    include = arrayListOf("*")
                    priority = -2
                }
            },
            Phase("Initializing API"){
                DogoBot.apiServer = APIServer().also {
                    it.server.start()
                }
            },
            Phase("Registering Random Event Listeners"){
                DogoBot.eventBus.register(io.github.dogo.badwords.BadwordProfile.listener)
            }
    )

    init {
        Thread.currentThread().name = "Boot"
        System.setProperty("logFile", File(File(DogoBot.data.LOGGER_PATH), SimpleDateFormat("dd-MM-YYYY HH-mm-ss").format(Date())).absolutePath)

        DogoBot.logger.info("Starting Dogo v${DogoBot.version}")

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
            DogoBot.jda?.presence?.game = Game.watching("myself starting - "+phase.display)
            phase.start()
            DogoBot.logger.info("["+count+"/"+phaseList.size+"] Done in ${time.timeSince()}")
            count++
        }

        DogoBot.ready = true
        DogoBot.jda?.presence?.game = Game.watching("${io.github.dogo.core.DogoBot.jda?.guilds?.size} guilds | dg!help")
        DogoBot.logger.info("Dogo is Done! ${io.github.dogo.core.DogoBot.initTime.timeSince()}")
    }


    /*
     extension shit
     */

    /**
     * Checks if a database has a collection.
     *
     * @param[name] the collection name.
     *
     * @return true if exists, false if not.
     */
    private fun MongoDatabase.hasCollection(name : String) : Boolean {
        return this.listCollectionNames().contains(name)
    }

    /**
     * Get the delay between a long and the current time and formats it to a human-readable format.
     *
     * @return the delay between [this] and the current time in a human-readable format.
     */
    private fun Long.timeSince() : String {
        val time = System.currentTimeMillis() - this
        return when{
            time < 1000 -> time.toString()+"ms"
            time < 60000 -> TimeUnit.MILLISECONDS.toSeconds(time).toString()+"sec"
            else -> TimeUnit.MILLISECONDS.toMinutes(time).toString()+"min"
        }
    }

    /**
     * Creates a collection if it doesn't exists.
     *
     * @param[collection] the collection name.
     */
    private fun MongoDatabase.checkCollection(collection: String) {
        if(!this.hasCollection(collection)) {
            DogoBot.logger.info("'$collection' collection doesn't exists! Creating one...")
            this.createCollection(collection)
        }
    }
}

/**
 * Hello Java
 */
fun main(args : Array<String>){
    Boot()
}