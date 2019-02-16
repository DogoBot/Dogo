package io.github.dogo.lang

import io.github.dogo.core.DogoBot
import io.ktor.util.extension
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

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
 * Class used to read texts from the language resources and format it to user-friendly texts.
 *
 * @param[registry] the root entry to look for texts. eg. *command.help*.helpfor (*command.help*) is the root
 *
 * @author NathanPB
 * @since 3.1.0
 */
open class LanguageEntry constructor(val registry : String){

    companion object {
        /**
         * Holds all the language file data
         */
        val langs = mutableMapOf<String, MutableMap<String, String>>()

        /**
         * Default language assets (en_US)
         */
        lateinit var default: MutableMap<String, String>

        /**
         * Loads language files from /assets/lang resources into [langs] and [default]
         */
        fun load() {
            DogoBot.logger.info("Loading language files...")
            langs.clear()
            val uri = LanguageEntry::class.java.getResource("/assets/lang").toURI()
            val fs: FileSystem? = if(uri.scheme == "jar") FileSystems.newFileSystem(uri, mutableMapOf<String, Any>()) else null
            Files.walkFileTree(Paths.get(uri), object: SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                    file?.let {
                        if(it.extension == "lang"){
                            val langEntry = file.fileName.toString().split(".")[0]
                            langs[langEntry] = mutableMapOf()
                            Files.readAllLines(file)
                                    .filter { line ->
                                        !line.startsWith("#") &&
                                        line.isNotEmpty() &&
                                        line.contains("=") &&
                                        line !="\r"
                                    }
                                    .forEach { line ->
                                        val split = line.split("=")
                                        langs[langEntry]!![split[0]] = split[1]
                                            .replace("\\n", "\n")
                                            .replace("\r", "")
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE
                }
            })
            fs?.close()
            default = langs["en_US"] ?: mutableMapOf()
            DogoBot.logger.info("Language Assets were loaded successfully!")
        }
    }

    /**
     * Gets an text from language files and format it.
     * Firstly it will search on the specified locale, if the entry was not found, it will try on the default language. If not found (again), it will return the entry itself.
     *
     * @param[lang] the language. If the supplied value cannot be found, *en_US* will be the default.
     * @param[entry] the entry to look for. Eg: command.help.*helpfor*. Considering *command.help* as [registry] and *helpfor* as [entry].
     * @param[args] the arguments used to format the text.
     * @see[String.format]
     *
     * @return the text read from language resources. If the entry wasn't found, it will return [registry].[entry].
     *
     */
    fun getTextIn(lang : String, entry : String, vararg args : Any) = (langs[lang] ?: default)["$registry.$entry"]?.let { String.format(it, *args) } ?: "$registry.$entry"
}
