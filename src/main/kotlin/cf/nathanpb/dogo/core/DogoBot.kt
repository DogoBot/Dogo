package cf.nathanpb.dogo.core

import cf.nathanpb.dogo.core.boot.Boot
import cf.nathanpb.dogo.core.threads.DogoThread
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import net.dv8tion.jda.core.JDA
import kotlin.collections.HashMap

class DogoBot {
    companion object {
        var jda: JDA? = null
        var mongoClient: MongoClient? = null
        var db: MongoDatabase? = null
        var data: DogoData? = null
        var boot: Boot? = null
        var logger: Logger? = null
        val initTime = System.currentTimeMillis()
        var ready = false
        val threads = HashMap<String, DogoThread>()
    }

    fun isAvailable() : Boolean {
        return ready
    }
}