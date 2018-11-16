package cf.dogo.core

import cf.dogo.core.boot.Boot
import cf.dogo.core.cmdHandler.CommandFactory
import cf.dogo.core.entities.DogoGuild
import cf.dogo.core.eventBus.EventBus
import cf.dogo.core.queue.DogoQueue
import cf.dogo.server.APIServer
import com.google.common.reflect.TypeToken
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import net.dv8tion.jda.core.JDA
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.json.JSONConfigurationLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileWriter

class DogoBot {
    companion object {
        val version = "1.0"
        var boot: Boot? = null
        var jda: JDA? = null
        var mongoClient: MongoClient? = null
        var db: MongoDatabase? = null

        val dataLoader = JSONConfigurationLoader.builder()
                .setFile(File("init.json").also { if(!it.exists()){
                    it.createNewFile()
                    FileWriter(it).let {
                        it.write("{}")
                        it.close()
                    }
                }})
                .setIndent(3)
                .build()

        val data : DogoData = {
            val config = dataLoader.load(
                    ConfigurationOptions.defaults()
                    .setShouldCopyDefaults(true)
            )
            val it = config.getValue(TypeToken.of(DogoData::class.java), DogoData())
            DogoBot.dataLoader.save(config)
            it
        }()

        val logger: Logger
            get() = LoggerFactory.getLogger(DogoBot::class.java)



        val initTime = System.currentTimeMillis()
        val threads = HashMap<String, DogoQueue>()

        val eventBus = EventBus("EVENT BUS")
        val jdaOutputThread = DogoQueue("JDA OUTPUT")
        val cmdProcessorThread = DogoQueue("CMD PROCESSOR")
        val ocWatcher = DogoQueue("OC WATCHER")
        val statsWatcher = DogoQueue("STATS WATCHER")

        val cmdFactory = CommandFactory(eventBus)
        var apiServer : APIServer? = null



        var ready = false
        fun isAvailable() = ready

        fun getCommandPrefixes(vararg guilds : DogoGuild) : List<String> {
            val list = data.COMMAND_PREFIX.toMutableList()
            guilds.map { it.prefix }.forEach { list.addAll(it)}
            return list.sortedBy { -it.length }
        }
    }
}