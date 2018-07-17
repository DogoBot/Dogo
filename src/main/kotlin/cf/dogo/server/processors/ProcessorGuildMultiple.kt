package cf.dogo.server.processors

import cf.dogo.core.DogoBot
import cf.dogo.core.entities.DogoGuild
import cf.dogo.server.APIRequestProcessor
import cf.dogo.server.APIServer
import cf.dogo.server.RequestResponse
import cf.dogo.server.Token
import org.json.JSONObject
import java.net.Socket

class ProcessorGuildMultiple : APIRequestProcessor {

    override fun proccess(data: RequestResponse, sck: Socket, tk: Token?, api: APIServer): RequestResponse {
        val resp = RequestResponse()
        val guilds = ArrayList<JSONObject>()
        for(a in DogoBot.jda!!.guilds){
            val g = DogoGuild(a)
            val d = JSONObject()
            d.put("id", g.id)
                    .put("icon", g.g!!.iconUrl)
                    .put("membercount", g.g.members.count())
                    .put("owner", g.g.owner.user.id)
            if(tk != null && tk.isValid()) {
                if (tk.getPermGroups(g).can("permgroups.view")) {
                    d.put("permgroups", g.permgroups.map { it.id }.toTypedArray())
                }
                if (g.g.isMember(tk!!.owner!!.usr)) {
                    d.put("members", g.g.members.map { it.user.id })
                    d.put("text-channels", g.g.textChannels.map { it.id }.toTypedArray())
                    d.put("voice-channels", g.g.voiceChannels.map { it.id }.toTypedArray())
                    d.put("categories", g.g.categories.map { it.id }.toTypedArray())
                    d.put("roles", g.g.roles.map { it.id }.toTypedArray())
                }
            }
            guilds.add(d)
        }
        resp.put("guilds", guilds.toTypedArray())
        return resp
    }
}