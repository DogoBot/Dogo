package io.github.dogobot.core.modularity

/**
 * Holds data about module MANIFEST.md file.
 *
 * @param[id] the module id.
 * @param[name] the module name.
 * @param[version] the module version.
 * @param[requires] IDs of modules that must be loaded first.
 */
data class ModuleManifest(val id: String, val name: String, val version: String, val requires: Array<String> = emptyArray())