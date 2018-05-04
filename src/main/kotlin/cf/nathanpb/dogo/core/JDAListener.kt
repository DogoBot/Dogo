package cf.nathanpb.dogo.core

import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener

class JDAListener : EventListener{
    override fun onEvent(event: Event?) {
        if(event != null) DogoBot.eventBus.submit(event)
    }
}