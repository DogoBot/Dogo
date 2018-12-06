package cf.dogo.core.command

import cf.dogo.lang.LanguageEntry
import cf.dogo.utils.Holder
import org.json.JSONObject
import java.lang.UnsupportedOperationException

class CommandRouter(val reference: CommandReference, body: CommandRouter.()->Unit) {
    val children = mutableListOf<CommandRouter>()
    val runs = mutableListOf<ReferencedCommand>()
    lateinit var langEntry: LanguageEntry
    var parent: CommandRouter? = null

    init {
        body()
    }




    fun CommandRouter.route(reference: CommandReference, body: CommandRouter.()->Unit) : CommandRouter {
        return CommandRouter(reference, body)
                .also {
                    it.parent = this
                    children.add(it)
                    it.langEntry = LanguageEntry(it.getPermission())
                }
    }

    fun CommandRouter.route(referenced: ReferencedCommand, body: CommandRouter.()->Unit = {}) : CommandRouter {
        return route(referenced.reference, body).also { it.execute(referenced.command);}
    }

    fun CommandRouter.execute(execute: (CommandContext)->Unit){
        runs.add(ReferencedCommand(reference) {execute(it)})
    }


    fun findRoute(str: String, holder: Holder<Int>) : CommandRouter {
        return findRoute(this, holder.also { it.hold(0) }, str.split(" "))
    }

    private fun findRoute(router: CommandRouter, findIndex: Holder<Int>, find: List<String>) : CommandRouter {
        return if(router.children.any { find.size > findIndex.hold() && it.reference.nameMatches(find[findIndex.hold()])}){
            val newRouter : CommandRouter? = router.children.firstOrNull { it.reference.nameMatches(find[findIndex.hold()]) }
            if(newRouter == null) router else findRoute(newRouter, findIndex.also{it.hold(findIndex.hold()+1)}, find)
        } else router
    }

    fun getPermission() : String {
        var currentRoute : CommandRouter? = this
        val routes = mutableListOf<String>()
        do {
            routes.add(currentRoute!!.reference.name)
            currentRoute = currentRoute.parent
        } while(currentRoute != null)
        return routes.reversed().filter{ it.isNotEmpty() }.joinToString(separator = ".", prefix = "command.")
    }

    fun getFullName() : String {
        var currentRoute : CommandRouter? = this
        val routes = mutableListOf<String>()
        do {
            routes.add(currentRoute!!.reference.name)
            currentRoute = currentRoute.parent
        } while (currentRoute != null)
        return routes.joinToString(" ")
    }


    fun isRoot() = parent == null

    companion object {
        val root = CommandReference("")
    }

    override fun toString() = JSONObject().put("reference", reference).put("runs", runs.size).put("children", children.size).put("parent", if(parent == null) null else JSONObject(parent.toString())).toString(4)
}