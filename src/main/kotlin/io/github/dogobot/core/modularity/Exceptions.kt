package io.github.dogobot.core.modularity

import java.io.File
import java.lang.Exception

open class ModuleLoaderException(msg: String)                 : Exception(msg)
class ModuleFileNotFoundException(file: File)                 : ModuleLoaderException("${file.name} could not be found")
class ModuleAlreadyLoadedException(module: ModuleManifest)    : ModuleLoaderException("${module.id} is already loaded")
class ModuleAlreadyUnloadedException(file: File)              : ModuleLoaderException("Module ${file.absoluteFile} is already unloaded")
class MainClassNotFoundException(file: File)                  : ModuleLoaderException("A instance of ${DogoModule::class.qualifiedName} was not found in module ${file.absoluteFile}")
class InvalidManifestException(file: File)                    : ModuleLoaderException("The manifest file for ${file.absoluteFile} is invalid")