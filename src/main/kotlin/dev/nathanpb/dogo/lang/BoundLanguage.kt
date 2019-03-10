package dev.nathanpb.dogo.lang

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
 * [LanguageEntry] bounded with a single language
 *
 * @param[lang] the language to bound.
 * @param[registry] the root entry to look for texts. eg. *command.help*.helpfor (*command.help*) is the root
 *
 * @author NathanPB
 * @since 3.1.0
 */
class BoundLanguage(val lang: String, registry: String) : LanguageEntry(registry) {

    /**
     * Finds a text with the bounded language.
     *
     * @param[entry] the entry to look for. Eg: command.help.*helpfor*. Considering *command.help* as [registry] and *helpfor* as [entry].
     * @param[args] the arguments used to format the text.
     *
     * @return the text read from language resources. If the entry wasn't found, it will return [registry].[entry].
     */
    fun getText(entry: String, vararg args: Any) = getTextIn(lang, entry, *args)

}