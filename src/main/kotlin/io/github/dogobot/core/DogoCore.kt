package io.github.dogobot.core

import io.github.dogobot.core.modularity.ModuleManager
import java.io.File
import java.lang.management.ManagementFactory
import java.util.jar.JarInputStream
import java.util.jar.Manifest

class DogoCore {
    companion object {
        val bootManager = DogoBootManager()

        /**
         * Directory of the current jar file.
         * Run directory if you are running on IDE.
         */
        val currentJar
            get() = DogoCore::class.java.protectionDomain.codeSource.location.toURI().path.let { File(it) }

        /**
         * Dogo current version.
         *
         * @return 'UNKNOWN' + logs a warn if not found.
         */
        val version: String
            get(){
                return if(currentJar.isDirectory){
                    val manifestFile = File(currentJar, "META-INF/MANIFEST.md")
                    if(manifestFile.exists()){
                        Manifest(manifestFile.inputStream()).mainAttributes.getValue("Dogo-Version")
                    } else {
                        logger.warn { "No MANIFEST.md file or no Dogo-Version entry in it was found. Are you running Dogo on IDE?" }
                        "UNKNOWN"
                    }

                } else {
                    JarInputStream(currentJar.inputStream()).manifest.mainAttributes.getValue("Dogo-Version")
                }
            }

        /**
         * JVM PID
         */
        val pid = ManagementFactory.getRuntimeMXBean().pid

        /**
         * Directory where is located all modules to be loaded.
         */
        val modulesDirectory = File("mods").also { if(!it.exists()) it.mkdirs() }


        val moduleManager = ModuleManager(modulesDirectory)
    }
}
