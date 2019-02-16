package io.github.dogo.core.permissions.mapper

import io.github.dogo.core.DogoBot
import io.github.dogo.core.eventBus.EventBus
import io.github.dogo.core.permissions.mapper.events.PermissionRegisteredEvent

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
 * Class responsible to map all the available permissions.
 * Every single permission must be registered here.
 *
 * @param[bus] the event bus to trigger events.
 *
 * @author NathanPB
 * @since 1.0.0
 */
class PermissionMapper(val bus: EventBus) {

    /**
     * The permission root.
     * Everything starts with 'dogo' node.
     * Eg: dogo.commands.(...), dogo.api.(...)
     */
    val permissions = PermissionNode("")

    /**
     * Registers a permission.
     * Creates its parents if it doesn't exists.
     *
     * @param[permissionName] the permission name. Permission names separated by "."
     */
    fun registerPermission(permissionName: String){
        var nextNode = permissions
        permissionName.toLowerCase().split(".")
            .filterIndexed { index, it -> !(it == permissions.name && index == 0) }
            .forEach { nodeName ->
                nextNode = nextNode.findChildren(nodeName).getOrElse(0){
                    nextNode.plus(nodeName).also { bus.submit(PermissionRegisteredEvent(it)) }
                }
            }
    }

    /**
     * Gets a list of nodes from its full name.
     *
     * @param[nodeName] the node name.
     * @return the nodes found.
     */
    fun getNodeFromName(nodeName: String): List<PermissionNode> {
        val nodes = mutableListOf<PermissionNode>()
        lateinit var recursive: (String, PermissionNode) -> Unit
        recursive = recursive@ { name, node ->
            if(name.contains(".")){
                node.findChildren(name.split(".")[0]).forEach {
                    nodes.add(it)
                    recursive(
                            name.split(".").filterIndexed { i, _ -> i > 0 }.joinToString("."),
                            it
                    )
                }
            } else nodes.addAll(node.findChildren(name.split(".")[0]))
        }
        recursive(nodeName, permissions)
        return nodes
    }

}