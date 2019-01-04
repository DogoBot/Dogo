package io.github.dogo.core

import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.core.eventBus.EventBus
import io.github.dogo.core.profiles.PermGroup
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.events.guild.update.GuildUpdateOwnerEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

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
 * Listens to owner changes in a guild to update the owner permissions.
 * 
 * @author NathanPB
 * @since 3.1.0
 */
class PermgroupsListener {

    //todo remake permgroups :cccc

    @EventBus.Listener
    fun onEvent1(e: GuildUpdateOwnerEvent){
        //forceUpdate(DogoGuild(e.guild), e.newOwner.user.id)
    }

    @EventBus.Listener
    fun onEvent2(e: GuildMessageReceivedEvent) {
        //forceUpdate(DogoGuild(e.guild), e.guild.ownerId)
    }

    fun forceUpdate(guild: DogoGuild, ownerId: String) {
        guild.permgroups.firstOrNull{ it.name == "guild_owner" }?.let {
            if(it.applyTo.size != 1 || it.applyTo[0] != ownerId){
                it.applyTo = arrayListOf(ownerId)
            }
        }
    }
}