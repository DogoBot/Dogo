package io.github.dogo.core

import com.google.common.reflect.TypeToken
import io.github.dogo.core.command.CommandFactory
import io.github.dogo.core.data.DogoData
import io.github.dogo.core.eventBus.EventBus
import io.github.dogo.server.APIServer
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.json.JSONConfigurationLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.io.FileWriter


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
        const val version = "3.2.0"

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
    }
}