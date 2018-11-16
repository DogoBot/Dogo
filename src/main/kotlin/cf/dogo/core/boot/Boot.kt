package cf.dogo.core.boot

import cf.dogo.core.DogoBot
import cf.dogo.core.profiles.PermGroup
import cf.dogo.server.APIServer
import cf.dogo.utils.ConsoleColors
import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

fun main(args : Array<String>){
    cf.dogo.core.DogoBot.boot = Boot()
}

class Boot {
    private val phaseList = listOf(
            Phase("Initializing JDA") {
                cf.dogo.core.DogoBot.jda = JDABuilder(AccountType.BOT)
                        .setToken(DogoBot.data.BOT_TOKEN)
                        .setGame(Game.watching("myself starting"))
                        .addEventListener(DogoBot.eventBus)
                        .build().awaitReady()
            },
            Phase("Connecting to Database") {
                DogoBot.mongoClient = MongoClient(
                        ServerAddress(DogoBot.data.DB_HOST, DogoBot.data.DB_PORT),
                        MongoCredential.createCredential(
                                DogoBot.data.DB_USER,
                                DogoBot.data.DB_NAME,
                                DogoBot.data.DB_PWD.toCharArray()
                        ),
                        MongoClientOptions.builder().build()
                ).also {
                    DogoBot.db = it.getDatabase(DogoBot.data.DB_NAME)
                }
            },
            Phase("Checking Database") {
                if (!DogoBot.db!!.hasCollection("USERS")) {
                    DogoBot.logger.info("USERS collection doesn't exists! Creating one...")
                    DogoBot.db?.createCollection("USERS")
                }
                if (!DogoBot.db!!.hasCollection("GUILDS")) {
                    DogoBot.logger.info("GUILDS collection doesn't exists! Creating one...")
                    DogoBot.db?.createCollection("GUILDS")
                }
                if(!DogoBot.db!!.hasCollection("PERMGROUPS")) {
                    DogoBot.logger.info("PERMGROUPS collection doesn't exists! Creating one...")
                    DogoBot.db?.createCollection("PERMGROUPS")
                }
                if(!DogoBot.db!!.hasCollection("STATS")) {
                    DogoBot.logger.info("STATS collection doesn't exists! Creating one...")
                    DogoBot.db?.createCollection("STATS")
                }
            },
            Phase("Registering Commands"){
               DogoBot.cmdFactory.registerCommand(cf.dogo.commands.Help(cf.dogo.core.DogoBot.cmdFactory))
                DogoBot.cmdFactory.registerCommand(cf.dogo.commands.Stats(cf.dogo.core.DogoBot.cmdFactory))
            },
            Phase("Setting up Queues"){
               DogoBot.ocWatcher.run = {
                    for(t in cf.dogo.core.DogoBot.threads.values){
                        var queue = t.queue()
                        var clk = 1

                        //Increases 10Hz every 10 items in queue;
                        while (queue > 10){
                            queue-=10
                            clk+=10
                        }
                        //clk is in Hz, NOT FUCKING PERIOD

                        //Fix the overclock multiplier if its wrong
                        if(clk < t.defaultClock){
                            clk = t.defaultClock
                        }

                        //Fix the queue clock if its wrong
                        if(t.clk < t.defaultClock){
                            t.clk = t.defaultClock
                        }

                        //Reduces the overclock to ONLY THE NECESSARY
                        clk -= t.clk
                        if(clk < 0) clk = 0

                        //Applies the overclock (if necessary)
                        if(t.overclock < clk) {
                            DogoBot.logger.info("Queue ${t.name} overclocked from ${t.clk}Hz (+${t.overclock}Hz oc) to ${t.clk + clk}Hz (+${clk}Hz oc)", ConsoleColors.YELLOW)
                            t.overclock = clk
                            if ((t.overclock / t.defaultClock) > 10) {
                                cf.dogo.core.DogoBot.logger.warn("Overclock from ${t.name} is TOO HIGH!!!")
                            }
                        } else if(t.overclock > clk){
                            DogoBot.logger.info("Queue ${t.name} downclocked from ${t.clk}Hz (+${t.overclock}Hz oc) to ${t.clk + clk}Hz (+${clk}Hz oc)", ConsoleColors.GREEN)
                            t.overclock = clk
                        }
                    }
                }
            },
            Phase("Setting up Permgroups") {
                val default = PermGroup("0")
                default.name = "default"
                default.applyTo = arrayListOf("everyone")
                default.include = arrayListOf("command.*")
                default.exclude = arrayListOf("command.admin.*")
                default.priority = 0
                val admins = PermGroup("-1")
                admins.name = "admin"
                admins.include = arrayListOf("command.admin.*")
                admins.exclude = arrayListOf("command.admin.root.*")
                admins.priority = -1
                val root = PermGroup("-2")
                root.name = "root"
                root.applyTo = arrayListOf(DogoBot.data.OWNER_ID)
                root.include = arrayListOf("*")
                root.priority = -2
            },
            Phase("Initializing API"){
                DogoBot.apiServer = APIServer()
                DogoBot.apiServer?.start()
            }
    )

    init {
        Thread.currentThread().name = "Boot"
        DogoBot.logger.info("Starting Dogo v${DogoBot.version}")

        if(DogoBot.data.DEBUG_PROFILE){
            DogoBot.logger.info("DEBUG PROFILE IS ACTIVE")
        }

        try {
            startup()
        } catch (ex : java.lang.Exception){
            DogoBot.logger.error("STARTUP FAILED", ex)
            System.exit(1)
        }
    }

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
                "By NathanPB - https://github.com/NathanPB/Dogo")

        var count = 1

        for(phase in phaseList){
            val time = System.currentTimeMillis()
            DogoBot.logger.info("["+count+"/"+phaseList.size+"] " + phase.getDisplay(), cf.dogo.utils.ConsoleColors.YELLOW)
            DogoBot.jda?.presence?.game = Game.watching("myself starting - "+phase.getDisplay())
            phase.start()
            DogoBot.logger.info("["+count+"/"+phaseList.size+"] Done in ${time.timeSince()}", cf.dogo.utils.ConsoleColors.GREEN)
            count++
        }

        DogoBot.ready = true
        DogoBot.jda?.presence?.game = Game.watching("${cf.dogo.core.DogoBot.jda?.guilds?.size} guilds| dg!help")
        DogoBot.logger.info("Dogo is Done! ${cf.dogo.core.DogoBot.initTime.timeSince()}", cf.dogo.utils.ConsoleColors.GREEN_BACKGROUND)
    }


    /*
     extension shit
     */

    private fun MongoDatabase.hasCollection(name : String) : Boolean {
        return this.listCollectionNames().contains(name)
    }

    private fun Long.timeSince() : String {
        val time = System.currentTimeMillis() - this;
        if(time < 1000){
            return time.toString()+"ms"
        } else if(time < 60000){
            return TimeUnit.MILLISECONDS.toSeconds(time).toString()+"sec"
        } else {
            return TimeUnit.MILLISECONDS.toMinutes(time).toString()+"min"
        }
    }
}