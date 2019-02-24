package io.github.dogobot.core

import io.github.dogobot.core.modularity.ModuleManager
import io.github.nathanpb.bb.phases.Phase
import java.io.File

fun main() {
    Thread.currentThread().name = "boot"
    DogoCore.bootManager.phase {

        subphase("Loading Modules"){
            val mods = mutableListOf<File>()
            subphase("Searching for Modules"){
                execute {
                    mods.addAll(DogoCore.moduleManager.findModules())
                    if(mods.isNotEmpty()) {
                        logger.info { "${mods.size} modules found in ${DogoCore.moduleManager.modulesDirectory}" }

                        //Adds subphases at runtime if one or more module were found.
                        Phase.PhaseWrapper(phase.parent!!).apply {
                            subphase("Ordering Modules for loading"){
                                execute {
                                    mods.sortWith(Comparator { fileA, fileB ->
                                        val modA = ModuleManager.findManifest(fileA)
                                        val modB = ModuleManager.findManifest(fileB)
                                        if(modA.id in modB.requires) 1 else 0
                                    })
                                }
                            }
                            subphase("Loading Modules"){
                                execute {
                                    logger.warn { "At this time, modules might inject subphases into the phase list" }
                                    mods.forEach {
                                        DogoCore.moduleManager.loadModule(it)
                                        logger.info { "Module ${ModuleManager.findManifest(it).id} loaded successfully" }
                                    }
                                }
                            }
                        }

                    } else {
                        logger.warn { "No modules found! Something might be wrong" }
                    }
                }
            }
        }

    }
    DogoCore.bootManager.startup()
}