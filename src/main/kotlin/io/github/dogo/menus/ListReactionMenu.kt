package io.github.dogo.menus

import io.github.dogo.core.command.CommandContext
import io.github.dogo.utils._static.EmoteReference
import io.github.dogo.utils._static.ThemeColor
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
 * Creates an embed menu to display a list of Strings, with pagination.
 *
 * @param[context] a command context to extract information like sender, channel, etc.
 * @param[items] the list of items to display.
 * @param[render] the transformation that the strings should undergo before display.
 * @param[embedBuild] the transformation that the embed should undergo before display.
 *
 * @author NathanPB
 * @since 3.1.0
 */
open class ListReactionMenu(context: CommandContext, val items: List<String>, val render: (String, Int)->String = {it, _ ->"$it\n"}, val embedBuild: (EmbedBuilder)->Unit = {}) : SimpleReactionMenu(context) {

    /**
     * The list of pages to display.
     */
    val pages = mutableListOf<EmbedBuilder>()

    init {
        update()
    }

    /**
     * Sends a page. It also adds buttons to navigate between pages.
     * @see page
     */
    open fun showPage(page: Int) {
        end()
        build(pages[page])
        if(page != 0){
            if(page-1 != 0){
                addAction(EmoteReference.TRACK_PREVIOUS, ""){
                    showPage(0)
                }
            }
            addAction(EmoteReference.ARROW_BACKWARD, ""){
                showPage(page-1)
            }
        }
        if(page != getPageIndex(items.size)){
            addAction(EmoteReference.ARROW_FORWARD, ""){
                showPage(page+1)
            }
            if(page+1 != getPageIndex(items.size)){
                addAction(EmoteReference.TRACK_NEXT, ""){
                    showPage(getPageIndex(items.size))
                }
            }
        }
        send()
    }

    /**
     * Updates all the pages reference.
     * @see pages
     */
    fun update() {
        pages.clear()
        for(i in 0..getPageIndex(items.size)){
            val embed = EmbedBuilder()
                    .setColor(ThemeColor.PRIMARY)
                    .setTitle(context.langEntry.getText("pagetitle", i+1, getPageIndex(items.size)+1))
            embedBuild(embed)
            pages.add(embed)
        }
        items.forEachIndexed {index, it ->
            val pindex = getPageIndex(index)
            pages[pindex].appendDescription(render(it, index-(pindex*9)))
        }
    }

    /**
     * Calculates the page number of an item index.
     *
     * @return the page number of an item index.
     */
    fun getPageIndex(index: Int) : Int {
        return Math.ceil((index+1).toDouble().div(9)).toInt()-1
    }
}