package cf.nathanpb.dogo.core.boot

import cf.nathanpb.dogo.commands.Help
import cf.nathanpb.dogo.commands.Stats
import cf.nathanpb.dogo.core.DogoBot
import cf.nathanpb.dogo.core.DogoData
import cf.nathanpb.dogo.core.Logger
import cf.nathanpb.dogo.core.profiles.PermGroup
import cf.nathanpb.dogo.core.profiles.PermGroupSet
import cf.nathanpb.dogo.utils.ConsoleColors
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import org.bson.Document
import java.io.File
import java.util.concurrent.TimeUnit

fun main(args : Array<String>){
    DogoBot.boot = Boot()
}

class Boot {
    var phaseList = listOf(
            Phase("Initializing JDA", {
                        DogoBot.jda = JDABuilder(AccountType.BOT)
                                .setToken(DogoBot.data?.getString("BOT_TOKEN"))
                                .setGame(Game.watching("myself starting"))
                                .addEventListener(DogoBot.eventBus)
                                .buildBlocking()
                    }),
            Phase("Connecting to Database", {
                        DogoBot.mongoClient = MongoClient(MongoClientURI(DogoBot.data?.getString("MONGO_URI")))
                        DogoBot.db = DogoBot.mongoClient?.getDatabase(DogoBot.data?.getString("DB_NAME"))
                    }),
            Phase("Checking Database", {
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
                    }),
            Phase("Registering Commands",{
                DogoBot.cmdFactory.registerCommand(Help(DogoBot.cmdFactory))
                DogoBot.cmdFactory.registerCommand(Stats(DogoBot.cmdFactory))
            }),
            Phase("Setting up Queues", {
                DogoBot.ocWatcher.run = {
                    for(t in DogoBot.threads.values){
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
                                DogoBot.logger?.warn("Overclock from ${t.name} is TOO FUCKING HIGH!!! Something is really wrong")
                            }
                        } else if(t.overclock > clk){
                            //DogoBot.logger?.info("Queue ${t.name} downclocked from ${t.clk}Hz (+${t.overclock}Hz oc) to ${t.clk + clk}Hz (+${clk}Hz oc)", ConsoleColors.GREEN)
                            t.overclock = clk
                        }
                    }
                }
            }),
            Phase("Setting up Permgroups", {
                val default = PermGroup("0")
                    default.name = "default"
                    default.applyTo = arrayListOf("everyone")
                    default.include = arrayListOf("command.*")
                    default.exclude = arrayListOf("command.admin.*")
                    default.priotiry = 0
                val admins = PermGroup("-1")
                    admins.name = "admin"
                    admins.include = arrayListOf("command.admin.*")
                    admins.exclude = arrayListOf("command.admin.root.*")
                    admins.priotiry = -1
                val root = PermGroup("-2")
                    root.name = "root"
                    root.applyTo = arrayListOf(DogoBot?.data?.getString("OWNER_ID") as String)
                    root.include = arrayListOf("*")
                    root.priotiry = -2
            })
    )

    val init = File("init.json")
    init {
        Thread.currentThread().name = "Boot"
        println("Starting Dogo")
        DogoBot.data = DogoData(init)
        if(!init.exists()){
           println(init.path+" was not found. Creating a blank one...")
            init.createNewFile()
        }
        println("Data successfully loaded")
        DogoBot.logger = Logger(System.out, "console")
        println("Logger successfully created")

        val debug = DogoBot.data?.getBoolean("DEBUG_PROFILE")
        if(debug != null && debug){
            DogoBot.logger?.info("DEBUG PROFILE IS ACTIVE")

        }

        try {
            startup()
        }catch (ex : java.lang.Exception){
            DogoBot.logger?.error("STARTUP FAILED")
            DogoBot.logger?.getPrintStream()?.print(ConsoleColors.RED_BOLD)
            ex.printStackTrace(DogoBot.logger?.getPrintStream())
            DogoBot.logger?.getPrintStream()?.print(ConsoleColors.RESET)
            System.exit(1)
        }
    }

    @Throws(Exception::class)
    fun startup() {
        DogoBot.logger?.println(
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
            DogoBot.logger?.info("["+count+"/"+phaseList.size+"] " + phase.getDisplay(), ConsoleColors.YELLOW)
            DogoBot.jda?.presence?.game = Game.watching("myself starting - "+phase.getDisplay())
            phase.start()
            DogoBot.logger?.info("["+count+"/"+phaseList.size+"] Done in ${time.timeSince()}", ConsoleColors.GREEN)
            count++
        }

        DogoBot.ready = true
        DogoBot.jda?.presence?.game = Game.watching("${DogoBot.jda?.guilds?.size} guilds| dg!help")
        DogoBot.logger?.info("Dogo is Done! ${DogoBot.initTime.timeSince()}", ConsoleColors.GREEN_BACKGROUND)
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