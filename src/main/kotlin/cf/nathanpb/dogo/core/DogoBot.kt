package cf.nathanpb.dogo.core

import cf.nathanpb.dogo.core.boot.Boot
import cf.nathanpb.dogo.core.cmdHandler.CommandFactory
import cf.nathanpb.dogo.core.entities.DogoGuild
import cf.nathanpb.dogo.core.eventBus.EventBus
import cf.nathanpb.dogo.core.queue.DogoQueue
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import net.dv8tion.jda.core.JDA
import org.json.JSONArray
import java.awt.Color
import kotlin.collections.HashMap

class DogoBot {
    companion object {
        var jda: JDA? = null
        var mongoClient: MongoClient? = null
        var db: MongoDatabase? = null
        var data: DogoData? = null
        var boot: Boot? = null
        var logger: Logger? = null
        val initTime = System.currentTimeMillis()
        var ready = false
        val threads = HashMap<String, DogoQueue>()

        val eventBus = EventBus("EVENT BUS")
        val jdaOutputThread = DogoQueue("JDA OUTPUT")
        val cmdProcessorThread = DogoQueue("CMD PROCESSOR")
        val ocWatcher = DogoQueue("OC WATCHER")

        val cmdFactory = CommandFactory(eventBus)
        val instance = DogoBot()

        val themeColor = arrayOf(Color(245, 214, 143), Color(229, 168, 63))
    }

    fun isAvailable() : Boolean {
        return ready
    }

    fun getCommandPrefixes(vararg guilds : DogoGuild) : ArrayList<String> {
        val list = ArrayList<String>()
        for(s in data?.getAny("COMMAND_PREFIX") as JSONArray){
            list.add(s.toString())
        }
        guilds.forEach { g -> list.addAll(g.prefix) }
        list.sortedBy { a -> -a.length}
        return list
    }
}