package cf.dogo.badwords

import cf.dogo.core.entities.DogoGuild
import cf.dogo.interfaces.IFinder
import org.bson.Document

class BadwordFinder : Document(), IFinder<BadwordProfile> {

    var guild: DogoGuild
        get() = DogoGuild(getString("guild"))
        set(it) = set("guild", it.id)

    var badwords: Array<String>
        get() = (get("badwords") as List<String>).toTypedArray()
        set(it) = set("badwords", it.toList())

    override fun col() = BadwordProfile.col
    override fun query() = this
    override fun map(doc: Document) = BadwordProfile.parse(doc)
}