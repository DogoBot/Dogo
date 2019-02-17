package io.github.dogo.core

import com.google.common.reflect.TypeToken
import io.github.dogo.core.command.CommandFactory
import io.github.dogo.core.data.DogoData
import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.eventBus.EventBus
import io.github.dogo.menus.SimpleReactionMenu
import io.github.dogo.server.APIServer
import io.github.dogo.utils._static.SystemUtils
import net.dv8tion.jda.core.JDA
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.json.JSONConfigurationLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.exposed.sql.Database
import sun.misc.Unsafe
import java.io.File
import java.io.FileWriter
import java.lang.management.ManagementFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

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
         * Database connection.
         */
        lateinit var db: Database

        /**
         * The data loader from Configurate.
         */
        val dataLoader = JSONConfigurationLoader.builder()
                .setFile(File("init.json").also { if(!it.exists()) {
                    it.createNewFile()
                    FileWriter(it).apply {
                        write("{}")
                        close()
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
         * Directory used to store temporary things.
         */
        val dynamicDir = File(".dynamic").also { it.mkdirs() }

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
         * The Event Bus.
         */
        val eventBus = EventBus()

        /**
         * Event Bus Thread.
         */

        val eventBusThread = Executors.newSingleThreadExecutor()

        /**
         * The JDA Output queue.
         */
        val jdaOutputThread = Executors.newSingleThreadExecutor()

        /**
         * The command processor queue.
         */
        val cmdProcessorThread = Executors.newSingleThreadExecutor()

        /**
         * The time watcher. This thread is responsible to finish up the timeout things.
         */
        val menuTimeWatcher = object : TimerTask() {
            override fun run() {
                SimpleReactionMenu.instances
                        .filter{it.timeout > 0L}
                        .filter { it.lastSend > 0 && System.currentTimeMillis() > it.lastSend + it.timeout }
                        .forEach{it.end(false)}
            }
        }.also { Timer().schedule(it, 1, 1000) }

        /**
         * The command factory.
         */
        val cmdFactory = CommandFactory()

        /**
         * The API Server.
         */
        var apiServer : APIServer? = null

        /**
         * True when Dogo finished up the boot process and is ready to use.
         */
        var ready = false

        /**
         * Simple container for [Unsafe]
         */
        val unsafe = Unsafe::class.java.getDeclaredField("theUnsafe")
                        .let {
                            it.isAccessible = true
                            it.get(null) as Unsafe
                        }
        /**
         * The JVM pid.
         */
        val pid = ManagementFactory.getRuntimeMXBean().name.split("@")[0].toInt()

        /**
         * Function used to search for valid command prefixes. It will always return the global ones.
         *
         * @param[guilds] guilds to search in for local command prefixes.
         *
         * @return the list with all the valid command prefixes.
         */
        fun getCommandPrefixes(vararg guilds : DogoGuild) : List<String> {
            val list = DogoBot.data.COMMAND_PREFIX.toMutableList()
            guilds.map { it.prefixes }.forEach { list.addAll(it)}
            return list.sortedBy { -it.length }
        }

        /**
         * Take Thread Dump and Heap Dump.
         * Note: IT'S ONLY AVAILABLE ON LINUX ENVIRONMENTS WITH JMAP AND JSTACK ON PATH.
         * The dumps are stored in dumps/dd-MM-YYY  HH-MM-ss.bin and dumps/dd-MM-YYY  HH-MM-ss.tdump
         */
        fun takeDumps(){
            val file = File("dumps").also { if(!it.exists()) it.mkdirs()}
            val currDate = SimpleDateFormat("dd-MM-YYYY HH-mm-ss").format(Date())
            DogoBot.logger.info("Taking Heap Dump and Thread Dump...")
            SystemUtils.exec("jmap -dump:format=b,file=${file.absolutePath}/$currDate.bin ${DogoBot.pid} ")
            SystemUtils.exec("jstack -l ${DogoBot.pid} > ${file.absolutePath}/$currDate.tdump")
        }
    }
}