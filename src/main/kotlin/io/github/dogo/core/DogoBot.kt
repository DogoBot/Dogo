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
 * The core. Stores a lot of important things.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class DogoBot {
    companion object {
        /**
         * Dogo Version.
         */
        const val version = "3.1.0"

        /**
         * Dogo JDA.
         */
        var jda: JDA? = null

        /**
         * Mongo Client.
         */
        var mongoClient: MongoClient? = null

        /**
         * Mongo Database.
         */
        var db: MongoDatabase? = null

        /**
         * The data loader from Configurate.
         */
        val dataLoader = JSONConfigurationLoader.builder()
                .setFile(File("init.json").also { if(!it.exists()) {
                    it.createNewFile()
                    FileWriter(it).let {
                        it.write("{}")
                        it.close()
                    }
                }})
                .setIndent(3)
                .build()

        /**
         * The data read from [dataLoader].
         */
        val data : DogoData = {
            val config = DogoBot.dataLoader.load(
                    ConfigurationOptions.defaults()
                    .setShouldCopyDefaults(true)
            )
            val it = config.getValue(TypeToken.of(DogoData::class.java), DogoData())
            DogoBot.dataLoader.save(config)
            it
        }()

        /**
         * The logger.
         */
        val logger : Logger
            get() = LogManager.getLogger(Throwable().stackTrace[1].className)

        /**
         * The moment when Dogo was initialized (millis timestamp).
         */
        val initTime = System.currentTimeMillis()

        /**
         * All the [DogoQueue] threads present on the system.
         * @see [DogoQueue]
         */
        val threads = HashMap<String, DogoQueue>()

        /**
         * The Event Bus.
         * @see DogoQueue
         */
        val eventBus = EventBus("EVENT BUS")

        /**
         * The JDA Output queue.
         * @see [DogoQueue]
         */
        val jdaOutputThread = DogoQueue("JDA OUTPUT")

        /**
         * The command processor queue.
         * @see [DogoQueue]
         */
        val cmdProcessorThread = DogoQueue("CMD PROCESSOR")

        /**
         * The time watcher. This thread is responsible to finish up the timeout things.
         * @see [DogoQueue]
         */
        val menuTimeWatcher = DogoQueue("MENU TIME WATCHER").also {
            it.run = {
                SimpleReactionMenu.instances
                        .filter{it.timeout > 0L}
                        .filter { it.lastSend > 0 && System.currentTimeMillis() > it.lastSend + it.timeout }
                        .forEach{it.end(false)}
            }
        }

        /**
         * The Overclock Watcher. This thread is responsible to watch the clock from other queues and overclock/downclock it.
         * @see [DogoQueue]
         */
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
                        //DogoBot.logger.info("Queue ${t.name} overclocked from ${t.clk}Hz (+${t.overclock}Hz oc) to ${t.clk + clk}Hz (+${clk}Hz oc)")
                        t.overclock = clk
                        if ((t.overclock / t.defaultClock) > 10) {
                            //DogoBot.logger.warn("Overclock from ${t.name} is TOO HIGH!!!")
                        }
                    } else if(t.overclock > clk){
                        //DogoBot.logger.info("Queue ${t.name} downclocked from ${t.clk}Hz (+${t.overclock}Hz oc) to ${t.clk + clk}Hz (+${clk}Hz oc)")
                        t.overclock = clk
                    }
                }
            }
        }

        /**
         * The command factory.
         */
        val cmdFactory = CommandFactory().also { DogoBot.eventBus.register(it) }

        /**
         * The API Server.
         */
        var apiServer : APIServer? = null

        /**
         * True when Dogo finished up the boot process and is ready to use.
         */
        var ready = false

        /**
         * Function used to search for valid command prefixes. It will always return the global ones.
         *
         * @param[guilds] guilds to search in for local command prefixes.
         *
         * @return the list with all the valid command prefixes.
         */
        fun getCommandPrefixes(vararg guilds : DogoGuild) : List<String> {
            val list = io.github.dogo.core.DogoBot.Companion.data.COMMAND_PREFIX.toMutableList()
            guilds.map { it.prefix }.forEach { list.addAll(it)}
            return list.sortedBy { -it.length }
        }
    }
}