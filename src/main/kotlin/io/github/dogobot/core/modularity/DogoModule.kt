package io.github.dogobot.core.modularity

abstract class DogoModule {

    lateinit var manifest: ModuleManifest
        internal set

    internal lateinit var classLoader: ModuleClassLoader


    fun onLoad(){}

    fun onUnload(){}

}