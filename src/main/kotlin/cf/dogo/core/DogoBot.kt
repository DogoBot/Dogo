package cf.dogo.core

import cf.dogo.core.boot.Boot
import cf.dogo.core.cmdHandler.CommandFactory
import cf.dogo.core.entities.DogoGuild
import cf.dogo.core.eventBus.EventBus
import cf.dogo.core.queue.DogoQueue
import cf.dogo.server.APIServer
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import net.dv8tion.jda.core.JDA
import java.awt.Color
import java.lang.management.ManagementFactory
import kotlin.collections.HashMap

class DogoBot {
    companion object {
        var jda: JDA? = null
        var mongoClient: MongoClient? = null
        var db: MongoDatabase? = null
        var data: cf.dogo.core.DogoData? = null
        var boot: Boot? = null
        var logger: cf.dogo.core.Logger? = null
        val initTime = System.currentTimeMillis()
        var ready = false
        val threads = HashMap<String, DogoQueue>()

        val eventBus = EventBus("EVENT BUS")
        val jdaOutputThread = DogoQueue("JDA OUTPUT")
        val cmdProcessorThread = DogoQueue("CMD PROCESSOR")
        val ocWatcher = DogoQueue("OC WATCHER")
        val statsWatcher = DogoQueue("STATS WATCHER")

        val cmdFactory = CommandFactory(eventBus)
        val instance = DogoBot()
        val apiServer = APIServer()

        val themeColor = arrayOf(Color(245, 214, 143), Color(229, 168, 63))
    }

    fun isAvailable() : Boolean {
        return ready
    }

    fun getCommandPrefixes(vararg guilds : DogoGuild) : ArrayList<String> {
        val list = ArrayList<String>()
        for(s in data?.getArray("COMMAND_PREFIX")!!.iterator()){
            list.add(s.toString())
        }
        guilds.forEach { g -> list.addAll(g.prefix) }
        list.sortedBy { a -> -a.length}
        return list
    }

    fun usedMemory() : Long {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)
    }

    fun maxMemory() : Long {
        return Runtime.getRuntime().totalMemory() / (1024 * 1024)
    }

    fun usedCPU() : Double {
        return ManagementFactory.getOperatingSystemMXBean().systemLoadAverage/ManagementFactory.getOperatingSystemMXBean().availableProcessors
    }
}