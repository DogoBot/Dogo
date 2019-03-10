package dev.nathanpb.dogo.core.command

import dev.nathanpb.dogo.core.DogoBot
import dev.nathanpb.dogo.discord.prefixes
import net.dv8tion.jda.core.entities.Guild
import java.util.concurrent.Executors

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
 *
 * @author NathanPB
 * @since 3.2.0
 */
class CommandManager {
    companion object {
        /**
         * The command processor queue.
         */
        val cmdProcessorThread = Executors.newSingleThreadExecutor()

        /**
         * Function used to search for valid command prefixes. It will always return the global ones.
         *
         * @param[guilds] guilds to search in for local command prefixes.
         *
         * @return the list with all the valid command prefixes.
         */
        fun getCommandPrefixes(vararg guilds : Guild) : List<String> {
            val list = dev.nathanpb.dogo.core.DogoBot.data.COMMAND_PREFIX.toMutableList()
            guilds.map { it.prefixes }.forEach { list.addAll(it)}
            return list.sortedBy { -it.length }
        }
    }

    var route : CommandRouter = CommandRouter(CommandRouter.root){}

    fun route(body: CommandRouter.()->Unit){
        CommandRouter(CommandRouter.root, body).also { route = it }
    }
}