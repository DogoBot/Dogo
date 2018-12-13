package io.github.dogo.core

import io.github.dogo.core.eventBus.EventBus
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener

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
 * Listens to JDA Event Bus and redirect it to Dogo [io.github.dogo.core.eventBus.EventBus]
 *
 * @param[bus] the bus to redirect the events.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class JDAListener(val bus: EventBus) : EventListener {

    /**
     * Listens to JDA Events and redirect it if the event is not null
     */
    override fun onEvent(event: Event?) {
        if(event != null && DogoBot.ready) bus.submit(event)
    }
}