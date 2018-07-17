package cf.dogo.server.processors

import cf.dogo.core.entities.DogoGuild
import cf.dogo.server.APIRequestProcessor
import cf.dogo.server.APIServer
import cf.dogo.server.RequestResponse
import cf.dogo.server.Token
import java.net.Socket

class ProcessorGuildSingle : APIRequestProcessor{
    override fun proccess(data: RequestResponse, sck: Socket, tk: Token?, api: APIServer): RequestResponse {
        if (data.has("id")) {
            val g = DogoGuild(data.getString("id"))
            val data = RequestResponse()
            data.put("id", g.id)

            if (g.g != null) {
                data.put("icon", g.g.iconUrl)
                        .put("name", g.g.name)
                        .put("membercount", g.g.members.size)
                        .put("owner", g.g.owner.user.id)
            } else {
                return RequestResponse().setDesc("guild not found",404)
            }

            if (tk != null && tk.isValid()) {
                if (tk.getPermGroups(g).can("permgroups.view")) {
                    data.put("permgroups", g.permgroups.map { it.id }.toTypedArray())
                }

                if (g.g.isMember(tk.owner!!.usr)) {
                    data.put("members", g.g.members.map { it.user.id })
                    data.put("text-channels", g.g.textChannels.map { it.id }.toTypedArray())
                    data.put("voice-channels", g.g.voiceChannels.map { it.id }.toTypedArray())
                    data.put("categories", g.g.categories.map { it.id }.toTypedArray())
                    data.put("roles", g.g.roles.map { it.id }.toTypedArray())
                }
            }
            return data
        } else {
            return RequestResponse().setDesc("id not provided", 422)
        }
    }
}