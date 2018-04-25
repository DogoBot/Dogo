package cf.nathanpb.dogo.core.boot

import cf.nathanpb.dogo.core.DogoBot
import cf.nathanpb.dogo.core.DogoData
import cf.nathanpb.dogo.core.DogoThread
import cf.nathanpb.dogo.core.Logger
import cf.nathanpb.dogo.utils.ConsoleColors
import java.io.File

fun main(args : Array<String>){
    DogoBot.boot = Boot()
}

class Boot {
    var phaseList = listOf(
            /*Phase("Connecting to Database",
                    {
                        DogoBot.mongoClient = MongoClient(MongoClientURI(DogoBot.data?.getString("MONGO_URI")))
                        DogoBot.db = DogoBot.mongoClient?.getDatabase(DogoBot.data?.getString("DB_NAME"))
                    }
            ),
            Phase("Checking Database",
                    {
                        //todo checar database
                    }
            ),*/
            Phase("Setting Up Threads",
                    {
                        DogoThread("Test Thread", {}).shedule(0, 50)
                    }
            )
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
        DogoBot.logger = Logger(System.out)
        println("Logger successfully created")

        var debug = DogoBot.data?.getBoolean("DEBUG_PROFILE")
        if(debug != null && debug){
            DogoBot.logger?.info("DEBUG PROFILE IS ON")

        }

        try {
            startup()
        }catch (ex : java.lang.Exception){
            DogoBot.logger?.error("STARTUP FAILED")
            DogoBot.logger?.getPrintStream()?.print(ConsoleColors.RED_BOLD)
            ex.printStackTrace(DogoBot.logger?.getPrintStream())
            DogoBot.logger?.getPrintStream()?.print(ConsoleColors.RESET)
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
                "By NathanPB - https://github.com/NathanPB")

        var count = 1

        for(phase in phaseList){
            DogoBot.logger?.info("["+count+"/"+phaseList.size+"] " + phase.getDisplay(), ConsoleColors.YELLOW)
            phase.start()
            DogoBot.logger?.info("["+count+"/"+phaseList.size+"] Done in "+(System.currentTimeMillis() - DogoBot.initTime)+"ms", ConsoleColors.GREEN)
            count++
        }

        DogoBot.ready = true
        DogoBot.logger?.info("Dogo is Done! "+(System.currentTimeMillis() - DogoBot.initTime)+"ms", ConsoleColors.GREEN_BACKGROUND)
    }


}