package cf.dogo.server

import org.json.JSONObject

class RequestResponse(json : JSONObject, val id : Int) : JSONObject(json.toString()) {
    constructor(id : Int) : this(JSONObject(), id)
    constructor() : this(JSONObject(), 0)

    fun setDesc(str : String, status : Int) : RequestResponse {
        this.put("desc", str).put("status", status)
        return this
    }

    override fun toString(): String {
        setDesc("ok", 200)
        return super.toString()
    }
}