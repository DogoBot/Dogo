package io.github.dogo.badwords

import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.interfaces.IFinder
import org.bson.Document

class BadwordFinder : Document(), IFinder<io.github.dogo.badwords.BadwordProfile> {

    var guild: DogoGuild
        get() = DogoGuild(getString("guild"))
        set(it) = set("guild", it.id)

    var badwords: Array<String>
        get() = (get("badwords") as List<String>).toTypedArray()
        set(it) = set("badwords", it.toList())

    override fun col() = io.github.dogo.badwords.BadwordProfile.Companion.col
    override fun query() = this
    override fun map(doc: Document) = io.github.dogo.badwords.BadwordProfile.Companion.parse(doc)
}