package io.github.dogobot.core.modularity

import java.io.File
import java.net.URLClassLoader
import java.util.*
import java.util.zip.ZipFile

/**
 * Class Loader responsible on loading and unloading modules.
 *
 * Each module instance must have it's own [ModuleClassLoader].
 *
 * @param[file] the module jar file.
 */
class ModuleClassLoader internal constructor(val file: File) : URLClassLoader(arrayOf(file.toURI().toURL())) {

    //reflection yey
    private val classes = this::class.java.getField("classes").also { it.isAccessible = true }.get(this) as Vector<Class<*>>

    /**
     * The loaded module.
     */
    var moduleReference: ModuleManifest? = null


    fun load(): DogoModule {
        val manifest = ModuleManager.findManifest(file)

        val zipFile = ZipFile(file)
        zipFile.entries().asIterator().forEach {
            if(it.name.endsWith(".class")){
                loadClass(it.name.substring(0, it.name.length - 6)) //removes ".class" on the end of the name
            }
        }

        val mainClass = (classes.firstOrNull { DogoModule::class.java.isAssignableFrom(it) }
                ?: throw MainClassNotFoundException(file)) as Class<DogoModule>
        return (mainClass.newInstance() as DogoModule).apply {
            this.manifest = manifest
            this.classLoader = this@ModuleClassLoader
        }
    }

    fun unload() {
        moduleReference = null
        classes.clear()
    }
}