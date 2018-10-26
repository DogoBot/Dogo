package cf.dogo.core.boot

import cf.dogo.core.DogoBot
import cf.dogo.core.profiles.PermGroup
import cf.dogo.server.APIServer
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

fun main(args : Array<String>){
    cf.dogo.core.DogoBot.boot = Boot()
}

class Boot {
    var phaseList = listOf(
            Phase("Initializing Default Configurations"){
                //todo init default configurations on init.json
            },
            Phase("Initializing JDA") {
                cf.dogo.core.DogoBot.jda = JDABuilder(AccountType.BOT)
                        .setToken(cf.dogo.core.DogoBot.data.load().getNode("BOT_TOKEN").string)
                        .setGame(Game.watching("myself starting"))
                        .addEventListener(cf.dogo.core.DogoBot.eventBus)
                        .build().awaitReady()
            },
            Phase("Connecting to Database") {
                DogoBot.mongoClient = MongoClient(
                        ServerAddress(DogoBot.data.load().getNode("DB_HOST").string, DogoBot.data.load().getNode("DB_PORT").int),
                        Arrays.asList(MongoCredential.createCredential(
                                DogoBot.data.load().getNode("DB_USER").string,
                                DogoBot.data.load().getNode("DB_NAME").string,
                                DogoBot.data.load().getNode("DB_PWD").string?.toCharArray()
                        ))
                )
                DogoBot.db = DogoBot.mongoClient?.getDatabase(DogoBot.data.load().getNode("DB_NAME").string)
            },
            Phase("Checking Database") {
                if (!DogoBot.db!!.hasCollection("USERS")) {
                    DogoBot.logger?.info("USERS collection doesn't exists! Creating one...")
                    DogoBot.db?.createCollection("USERS")
                }
                if (!DogoBot.db!!.hasCollection("GUILDS")) {
                    DogoBot.logger?.info("GUILDS collection doesn't exists! Creating one...")
                    DogoBot.db?.createCollection("GUILDS")
                }
                if(!DogoBot.db!!.hasCollection("PERMGROUPS")) {
                    DogoBot.logger?.info("PERMGROUPS collection doesn't exists! Creating one...")
                    DogoBot.db?.createCollection("PERMGROUPS")
                }
                if(!DogoBot.db!!.hasCollection("STATS")) {
                    DogoBot.logger?.info("STATS collection doesn't exists! Creating one...")
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
                            clk = t.defaultClock;
                        }

                        //Fix the queue clock if its wrong
                        if(t.clk < t.defaultClock){
                            t.clk = t.defaultClock;
                        }

                        //Reduces the overclock to ONLY THE NECESSARY
                        clk -= t.clk
                        if(clk < 0) clk = 0

                        //Applies the overclock (if necessary)
                        if(t.overclock < clk) {
                            //DogoBot.logger?.info("Queue ${t.name} overclocked from ${t.clk}Hz (+${t.overclock}Hz oc) to ${t.clk + clk}Hz (+${clk}Hz oc)", ConsoleColors.YELLOW)
                            t.overclock = clk
                            if ((t.overclock / t.defaultClock) > 10) {
                                cf.dogo.core.DogoBot.logger?.warn("Overclock from ${t.name} is TOO HIGH!!!")
                            }
                        } else if(t.overclock > clk){
                            //DogoBot.logger?.info("Queue ${t.name} downclocked from ${t.clk}Hz (+${t.overclock}Hz oc) to ${t.clk + clk}Hz (+${clk}Hz oc)", ConsoleColors.GREEN)
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
                root.applyTo = arrayListOf(cf.dogo.core.DogoBot?.data.load().getNode("OWNER_ID").string as String)
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
        println("Starting Dogo")
        DogoBot.logger = cf.dogo.core.Logger(System.out, "console")
        println("Logger successfully created")

        if(DogoBot.data.load().getNode("DEBUG_PROFILE").boolean){
            cf.dogo.core.DogoBot.logger?.info("DEBUG PROFILE IS ACTIVE")
        }

        try {
            startup()
        } catch (ex : java.lang.Exception){
            DogoBot.logger?.error("STARTUP FAILED")
            DogoBot.logger?.print(cf.dogo.utils.ConsoleColors.RED_BOLD)
            ex.printStackTrace(DogoBot.logger)
            DogoBot.logger?.print(cf.dogo.utils.ConsoleColors.RESET)
            System.exit(1)
        }
    }

    @Throws(Exception::class)
    fun startup() {
        cf.dogo.core.DogoBot.logger?.println(
                "  _____                    ____        _   \n" +
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
            cf.dogo.core.DogoBot.logger?.info("["+count+"/"+phaseList.size+"] " + phase.getDisplay(), cf.dogo.utils.ConsoleColors.YELLOW)
            cf.dogo.core.DogoBot.jda?.presence?.game = Game.watching("myself starting - "+phase.getDisplay())
            phase.start()
            cf.dogo.core.DogoBot.logger?.info("["+count+"/"+phaseList.size+"] Done in ${time.timeSince()}", cf.dogo.utils.ConsoleColors.GREEN)
            count++
        }

        cf.dogo.core.DogoBot.ready = true
        cf.dogo.core.DogoBot.jda?.presence?.game = Game.watching("${cf.dogo.core.DogoBot.jda?.guilds?.size} guilds| dg!help")
        cf.dogo.core.DogoBot.logger?.info("Dogo is Done! ${cf.dogo.core.DogoBot.initTime.timeSince()}", cf.dogo.utils.ConsoleColors.GREEN_BACKGROUND)
    }


    /*
     extension shit
     */

    fun MongoDatabase.hasCollection(name : String) : Boolean {
        return this.listCollectionNames().contains(name)
    }

    fun Long.timeSince() : String {
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