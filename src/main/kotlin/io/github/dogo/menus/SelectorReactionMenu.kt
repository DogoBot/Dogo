package io.github.dogo.menus

import io.github.dogo.core.command.CommandContext
import io.github.dogo.utils._static.EmoteReference
import net.dv8tion.jda.core.EmbedBuilder

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
 * Creates a menu that able users to select an item. Everything is indexed with Discord emotes.
 *
 * @param[context] a command context to extract information like sender, channel, etc.
 * @param[items] the list of items to display.
 * @param[render] the transformation that the strings should undergo before display.
 * @param[embedBuild] the transformation that the embed should undergo before display.
 * @param[onSelected] triggered whenever the user selects an item.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class SelectorReactionMenu<T>(
        context: CommandContext,
        items: List<T>,
        render: (T, Int)->String = {it, _ ->"$it\n"},
        embedBuild: (EmbedBuilder)->Unit = {},
        val onSelected: (T, SelectorReactionMenu<T>) -> Unit
) : ListReactionMenu<T>(
        context,
        items,
        { it, index -> "${EmoteReference.getRegional(index.toString()[0]).getAsMention()} ${render(it, index)}" },
        embedBuild
) {
    override fun showPage(page: Int) {
        items.forEachIndexed {index, it ->
            val pindex = getPageIndex(index)
            if(pindex == page){
                addAction(EmoteReference.getRegional((index-(pindex*9)).toString()[0]), ""){ onSelected(it, this) }
            }
        }
        super.showPage(page)
    }
}