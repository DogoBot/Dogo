package io.github.dogo.core.permissions.mapper

import java.util.regex.Pattern

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
 * Represents a node of permissions.
 *
 * @param[name] the node name. Can't contains ".", "*" or any regex.
 * @param[parent] the new node parent.
 *
 * @author NathanPB
 * @since 1.0.0
 */
open class PermissionNode(val name: String, val parent: PermissionNode? = null) {

    /**
     * The children list.
     */
    val children = mutableListOf<PermissionNode>()

    /**
     * The node full name.
     * It's the name from all it's family separated by ".".
     * e.g. dogo.commands.help
     * Where "help" is child from "commands", that is child from "dogo".
     * The visual representation for this permission is dogo[commands[help[]]].
     */
    val fullName = {
        val family = mutableListOf<PermissionNode>()
        var next: PermissionNode? = this
        while (next != null) {
            if(next.name.isNotEmpty()) {
                family += next
            }
            next = next.parent
        }
        family.reversed().joinToString("."){ it.name }
    }()

    /**
     * Finds a list of children from this node, matching regex and "*".
     * "*" means one or more characters of any type.
     */
    fun findChildren(name: String = "*") =  mutableListOf<PermissionNode>().also { list ->
        children.forEach {
            if(Pattern.compile(
                name.replace("*", ".*")
            ).matcher(it.name).matches()) list += it
        }
    }

    fun findFamily(nodeName: String): List<PermissionNode> {
        val nodes = mutableListOf<PermissionNode>()
        lateinit var recursive: (String, PermissionNode) -> Unit
        recursive = recursive@ { name, node ->
            if(name.contains(".")){
                node.findChildren(name.split(".")[0]).forEach {
                    recursive(
                            name.split(".").filterIndexed { i, _ -> i > 0 }.joinToString("."),
                            it
                    )
                }
            } else nodes.addAll(node.findChildren(name.split(".")[0]))
        }
        recursive(nodeName, this)
        return nodes
    }

    /**
     * Creates a node named [nodeName] and appends it as children of the current node.
     *
     * @param[nodeName] the name of the child.
     * @return the just created node.
     */
    operator fun plus(nodeName: String) = PermissionNode(nodeName, this).also { children.add(it) }

    /**
     * Created a node named [nodeName] and appends it as children of the current node.
     *
     * @param[nodeName] the name of the child.
     */
    operator fun plusAssign(nodeName: String) {
        plus(nodeName)
    }

    override fun toString() = "$name$children"
}