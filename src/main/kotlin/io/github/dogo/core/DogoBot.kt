package io.github.dogo.core

import com.google.common.reflect.TypeToken
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import io.github.dogo.core.command.CommandFactory
import io.github.dogo.core.data.DogoData
import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.eventBus.EventBus
import io.github.dogo.core.queue.DogoQueue
import io.github.dogo.menus.SimpleReactionMenu
import io.github.dogo.server.APIServer
import net.dv8tion.jda.core.JDA
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.json.JSONConfigurationLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.io.FileWriter
import java.util.*

class DogoBot {
    companion object {
        const val version = "1.0"
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
            val config = io.github.dogo.core.DogoBot.Companion.dataLoader.load(
                    ConfigurationOptions.defaults()
                    .setShouldCopyDefaults(true)
            )
            val it = config.getValue(TypeToken.of(DogoData::class.java), DogoData())
            io.github.dogo.core.DogoBot.Companion.dataLoader.save(config)
            it
        }()

        val logger : Logger
            get() = LogManager.getLogger(Throwable().stackTrace[1].className)

        val initTime = System.currentTimeMillis()
        val threads = HashMap<String, DogoQueue>()

        val eventBus = EventBus("EVENT BUS")
        val jdaOutputThread = DogoQueue("JDA OUTPUT")
        val cmdProcessorThread = DogoQueue("CMD PROCESSOR")
        val menuTimeWatcher = DogoQueue("MENU TIME WATCHER").also {
            it.run = {
                SimpleReactionMenu.instances
                        .filter{it.timeout > 0L}
                        .filter { it.lastSend > 0 && System.currentTimeMillis() > it.lastSend + it.timeout }
                        .forEach{it.end(false)}
            }
        }
        val ocWatcher = DogoQueue("OC WATCHER").also {
            it.run = {
                for(t in io.github.dogo.core.DogoBot.Companion.threads.values){
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
                        io.github.dogo.core.DogoBot.Companion.logger.info("Queue ${t.name} overclocked from ${t.clk}Hz (+${t.overclock}Hz oc) to ${t.clk + clk}Hz (+${clk}Hz oc)")
                        t.overclock = clk
                        if ((t.overclock / t.defaultClock) > 10) {
                            io.github.dogo.core.DogoBot.Companion.logger.warn("Overclock from ${t.name} is TOO HIGH!!!")
                        }
                    } else if(t.overclock > clk){
                        io.github.dogo.core.DogoBot.Companion.logger.info("Queue ${t.name} downclocked from ${t.clk}Hz (+${t.overclock}Hz oc) to ${t.clk + clk}Hz (+${clk}Hz oc)")
                        t.overclock = clk
                    }
                }
            }
        }
        val statsWatcher = DogoQueue("STATS WATCHER")

        val cmdFactory = CommandFactory().also { io.github.dogo.core.DogoBot.Companion.eventBus.register(it) }
        var apiServer : APIServer? = null



        var ready = false
        fun isAvailable() = io.github.dogo.core.DogoBot.Companion.ready

        fun getCommandPrefixes(vararg guilds : DogoGuild) : List<String> {
            val list = io.github.dogo.core.DogoBot.Companion.data.COMMAND_PREFIX.toMutableList()
            guilds.map { it.prefix }.forEach { list.addAll(it)}
            return list.sortedBy { -it.length }
        }
    }
}