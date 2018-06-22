package cf.dogo.core.boot

import cf.dogo.core.profiles.PermGroup
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import java.io.File
import java.util.concurrent.TimeUnit

fun main(args : Array<String>){
    cf.dogo.core.DogoBot.boot = Boot()
}

class Boot {
    var phaseList = listOf(
            Phase("Initializing JDA", {
                        cf.dogo.core.DogoBot.jda = JDABuilder(AccountType.BOT)
                                .setToken(cf.dogo.core.DogoBot.data?.getString("BOT_TOKEN"))
                                .setGame(Game.watching("myself starting"))
                                .addEventListener(cf.dogo.core.DogoBot.eventBus)
                                .buildBlocking()
                    }),
            Phase("Connecting to Database", {
                        cf.dogo.core.DogoBot.mongoClient = MongoClient(MongoClientURI(cf.dogo.core.DogoBot.data?.getString("MONGO_URI")))
                        cf.dogo.core.DogoBot.db = cf.dogo.core.DogoBot.mongoClient?.getDatabase(cf.dogo.core.DogoBot.data?.getString("DB_NAME"))
                    }),
            Phase("Checking Database", {
                        if (!cf.dogo.core.DogoBot.db!!.hasCollection("USERS")) {
                            cf.dogo.core.DogoBot.logger?.info("USERS collection doesn't exists! Creating one...")
                            cf.dogo.core.DogoBot.db?.createCollection("USERS")
                        }
                        if (!cf.dogo.core.DogoBot.db!!.hasCollection("GUILDS")) {
                            cf.dogo.core.DogoBot.logger?.info("GUILDS collection doesn't exists! Creating one...")
                            cf.dogo.core.DogoBot.db?.createCollection("GUILDS")
                        }
                        if(!cf.dogo.core.DogoBot.db!!.hasCollection("PERMGROUPS")) {
                            cf.dogo.core.DogoBot.logger?.info("PERMGROUPS collection doesn't exists! Creating one...")
                            cf.dogo.core.DogoBot.db?.createCollection("PERMGROUPS")
                        }
                        if(!cf.dogo.core.DogoBot.db!!.hasCollection("STATS")) {
                            cf.dogo.core.DogoBot.logger?.info("STATS collection doesn't exists! Creating one...")
                            cf.dogo.core.DogoBot.db?.createCollection("STATS")
                        }
                    }),
            Phase("Registering Commands",{
                cf.dogo.core.DogoBot.cmdFactory.registerCommand(cf.dogo.commands.Help(cf.dogo.core.DogoBot.cmdFactory))
                cf.dogo.core.DogoBot.cmdFactory.registerCommand(cf.dogo.commands.Stats(cf.dogo.core.DogoBot.cmdFactory))
            }),
            Phase("Setting up Queues", {
                cf.dogo.core.DogoBot.ocWatcher.run = {
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
                                cf.dogo.core.DogoBot.logger?.warn("Overclock from ${t.name} is TOO FUCKING HIGH!!! Something is really wrong")
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
                    default.priority = 0
                val admins = PermGroup("-1")
                    admins.name = "admin"
                    admins.include = arrayListOf("command.admin.*")
                    admins.exclude = arrayListOf("command.admin.root.*")
                    admins.priority = -1
                val root = PermGroup("-2")
                    root.name = "root"
                    root.applyTo = arrayListOf(cf.dogo.core.DogoBot?.data?.getString("OWNER_ID") as String)
                    root.include = arrayListOf("*")
                    root.priority = -2
            }),
            Phase("Initializing API", {

            })
    )

    val init = File("init.json")
    init {
        Thread.currentThread().name = "Boot"
        println("Starting Dogo")
        cf.dogo.core.DogoBot.data = cf.dogo.core.DogoData(init)
        if(!init.exists()){
           println(init.path+" was not found. Creating a blank one...")
            init.createNewFile()
        }
        println("Data successfully loaded")
        cf.dogo.core.DogoBot.logger = cf.dogo.core.Logger(System.out, "console")
        println("Logger successfully created")

        val debug = cf.dogo.core.DogoBot.data?.getBoolean("DEBUG_PROFILE")
        if(debug != null && debug){
            cf.dogo.core.DogoBot.logger?.info("DEBUG PROFILE IS ACTIVE")
        }

        try {
            startup()
        }catch (ex : java.lang.Exception){
            cf.dogo.core.DogoBot.logger?.error("STARTUP FAILED")
            cf.dogo.core.DogoBot.logger?.getPrintStream()?.print(cf.dogo.utils.ConsoleColors.RED_BOLD)
            ex.printStackTrace(cf.dogo.core.DogoBot.logger?.getPrintStream())
            cf.dogo.core.DogoBot.logger?.getPrintStream()?.print(cf.dogo.utils.ConsoleColors.RESET)
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