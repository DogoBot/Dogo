package cf.dogo.server

import cf.dogo.core.entities.DogoGuild
import cf.dogo.core.entities.DogoUser
import cf.dogo.core.profiles.PermGroupSet
import org.json.JSONObject
import java.util.*

class Token(val token : String) {
    constructor(auth : JSONObject) : this(auth.getString("token")){
        expiration = Date(auth.getLong("expiration"))
        owner = DogoUser(auth.getString("owner"))
    }


    var owner : DogoUser? = null
    var expiration : Date? = null

    fun isValid() : Boolean {
        return expiration != null && Date(System.currentTimeMillis()).before(expiration) && owner != null
    }

    fun getPermGroups(guild : DogoGuild? = null) : PermGroupSet {
        return if(guild != null && owner != null){
            owner!!.getPermGroups().filterApplied(guild.id)
        } else if(owner != null){
            owner!!.getPermGroups()
        } else {
            PermGroupSet()
        }
    }
}