package io.github.dogo.menus

import io.github.dogo.core.command.CommandContext
import io.github.dogo.utils.EmoteReference
import io.github.dogo.utils.ThemeColor
import net.dv8tion.jda.core.EmbedBuilder

class ListReactionMenu(context: CommandContext, val items: List<String>, val render: (String)->String = {"$it\n"}, val embedBuild: (EmbedBuilder)->Unit = {}) : SimpleReactionMenu(context) {

    val pages = mutableListOf<EmbedBuilder>()

    init {
        update()
    }

    fun showPage(page: Int) {
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

    fun update() {
        pages.clear()
        for(i in 0..getPageIndex(items.size)){
            val embed = EmbedBuilder()
                    .setColor(ThemeColor.PRIMARY)
                    .setTitle(context.langEntry().getText(context.lang(), "pagetitle", i+1, getPageIndex(items.size)+1))
            embedBuild(embed)
            pages.add(embed)
        }
        items.forEachIndexed {index, it ->
            val pindex = getPageIndex(index)
            pages[pindex].appendDescription(render(it))
        }
    }

    fun getPageIndex(index: Int) : Int {
        return Math.ceil((index+1).toDouble().div(9)).toInt()-1
    }
}