package io.github.dogobot.core.modularity

import io.github.dogobot.core.logger
import java.io.File
import java.util.jar.JarInputStream

/**
 * Class that manages the module loading.
 *
 * @param[modulesDirectory] the directory where is located .jar modules files.
 */
class ModuleManager internal constructor(val modulesDirectory: File) {

    companion object {

        /**
         * Searches for MANIFEST.md in a jar file and builds its manifest.
         *
         * @throws InvalidManifestException when the MANIFEST.md file or the field 'Module-Id', 'Module-Name' or 'Module-Version' doesn't exists.
         * @param[modFile] the module .jar file
         * @return the module manifest data.
         */
        fun findManifest(modFile: File): ModuleManifest {
            val jarStream = JarInputStream(modFile.inputStream())
            val attributes = jarStream.manifest?.mainAttributes

            val moduleId = attributes?.getValue("Module-Id")                  ?: throw InvalidManifestException(modFile)
            val moduleName = attributes.getValue("Module-Name")               ?: throw InvalidManifestException(modFile)
            val moduleVersion = attributes.getValue("Module-Version")         ?: throw InvalidManifestException(modFile)
            val requires = attributes.getValue("Module-Requires")?.split(",")?.toTypedArray() ?: emptyArray()

            return ModuleManifest(moduleId, moduleName, moduleVersion, requires)
        }

    }

    /**
     * Lists of loaded modules.
     * DO NOT modify this list. Use [loadModule] and [unloadModule] instead.
     */
    private val _loadedModules = mutableListOf<ModuleClassLoader>()

    /**
     * Simple getter to avoid devs on loading/unloading modules directly at [_loadedModules]
     */
    val loadedModules
        get() = _loadedModules.toList()


    /**
     * Searches for module files on [modulesDirectory] directory.
     */
    fun findModules() = modulesDirectory
            .listFiles { f -> f.extension === "jar" }
            .filter {
                //Filter by searching valid manifest files
                try {
                    findManifest(it)
                    true
                } catch (ex: Exception){
                    false
                }
            }

    /**
     * Loads a module by its jar file.
     */
    fun loadModule(modFile: File) {

        //Checks if the file is valid
        if(!modFile.exists() || !modFile.name.endsWith(".jar")) {
            throw ModuleFileNotFoundException(modFile)
        }

        //Checks if the module is already loaded
        _loadedModules.firstOrNull { it.moduleReference?.id === findManifest(modFile).id }?.let {
            throw ModuleAlreadyLoadedException(it.moduleReference!!)
        }

        //Checks if the module is already loaded
        _loadedModules.firstOrNull { it.file.absoluteFile === modFile.absoluteFile }?.let {
            throw ModuleAlreadyLoadedException(it.moduleReference!!)
        }

        //Loads the module
        val initTime = System.currentTimeMillis()
        val moduleLoader = ModuleClassLoader(modFile).also {
            it.load()
            _loadedModules += it
        }
        //todo call event bus
        //todo format the time to a better human-readable format
        logger.info { "Module ${moduleLoader.moduleReference} loaded in ${System.currentTimeMillis() - initTime}ms" }
    }

    /**
     * Unloads a module that already is loaded.
     * @throws ModuleAlreadyLoadedException if the same file or another module with same id is already loaded
     */
    fun unloadModule(module: DogoModule) {
        val loader = _loadedModules.firstOrNull {
            it.moduleReference?.id === module.manifest.id ||
            it.file.absoluteFile   === module.classLoader.file.absoluteFile
        } //search from the module in the module list
            ?: throw ModuleAlreadyUnloadedException(module.classLoader.file) //if the module was not found, throw exception

        loader.unload()
        _loadedModules -= loader

        //todo unregister from event bus and everything else
        //todo submit to EventBus
        logger.info {
            "Module $module unloaded and ready to be garbage collected!"
        }
    }

}