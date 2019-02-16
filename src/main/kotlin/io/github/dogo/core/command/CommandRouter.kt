package io.github.dogo.core.command

import io.github.dogo.core.permissions.mapper.PermissionMapper
import io.github.dogo.utils.Holder
import org.json.JSONObject

class CommandRouter(val reference: CommandReference, val permMapper: PermissionMapper, body: CommandRouter.()->Unit) {
    val children = mutableListOf<CommandRouter>()
    var run: ReferencedCommand? = null
    var parent: CommandRouter? = null

    init { body() }

    fun CommandRouter.route(reference: CommandReference, body: CommandRouter.()->Unit) : CommandRouter {
        return CommandRouter(reference, permMapper, body)
                .also {
                    it.parent = this
                    children.add(it)
                }
    }

    fun CommandRouter.route(referenced: ReferencedCommand, body: CommandRouter.()->Unit = {}) : CommandRouter {
        return route(referenced.reference, body).also {
            permMapper.registerPermission(it.getPermission())
            it.execute(referenced.command)
        }
    }

    fun CommandRouter.execute(execute: CommandContext.()->Unit){
        run = ReferencedCommand(reference, execute)
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
        return routes.reversed().filter{ it.isNotEmpty() }.joinToString(separator = ".", prefix = "${reference.permission}.")
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
        val root = CommandReference("", permission = "command")
    }

    override fun toString() = JSONObject().put("reference", reference).put("children", children.size).put("parent", if(parent == null) null else JSONObject(parent.toString())).toString(4)
}