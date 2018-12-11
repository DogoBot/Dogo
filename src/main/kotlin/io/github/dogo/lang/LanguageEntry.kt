package io.github.dogo.lang

import java.util.concurrent.ConcurrentLinkedDeque

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
class LanguageEntry constructor(val registry : String){
    /**
     * The default language file.
     */
    private val default = LanguageEntry::class.java.getResource("/assets/lang/en_US.lang").readText().toLang()


    /**
     * Gets an text from language files and format it.
     *
     * @param[lang] the language. If the supplied value cannot be found, *en_US* will be the default.
     * @param[entry] the entry to look for. Eg: command.help.*helpfor*. Considering *command.help* as [registry] and *helpfor* as [entry].
     * @param[args] the arguments used to format the text.
     * @see[String.format]
     *
     * @return the text read from language resources. If the entry wasn't found, it will return [registry].[entry].
     *
     */
    fun getText(lang : String, entry : String, vararg args : Any) : String{
        var text = LanguageEntry::class.java.getResource("/assets/lang/${lang.split("_")[0].toLowerCase()}_${lang.split("_")[1].toUpperCase()}.lang")?.readText()?.toLang()
        if(text == null){
           text = default
        }
        return if(text.containsKey("$registry.$entry")){
            java.lang.String.format(text["$registry.$entry"] as String, *args).replace("\\n", "\n")
        } else {
            "$registry.$entry"
        }
    }

    /*
    extension shit
     */
    /**
     * Parses a [String] to a [HashMap]. The key is the entry and the value is the text.
     */
    private fun String.toLang() : HashMap<String, String> {
        val hm = HashMap<String, String>()
        var array = ConcurrentLinkedDeque<String>(this.split("=", "\n"))
        array = ConcurrentLinkedDeque(array.filter { t -> t.isNotEmpty() && !t.equals("\r") && !t.startsWith("#")}.map { t -> t.replace("\r", "") })
        while (array.size >= 2) {
            hm[array.poll()] = array.poll()
        }
        return hm
    }
}
