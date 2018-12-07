package io.github.dogo.statistics

import io.github.dogo.core.DogoBot
import com.mongodb.client.MongoCollection
import org.bson.Document
import java.util.*

open abstract class Statistic(val data: Document) {
    companion object {
        val col = DogoBot.db?.getCollection("statistics") as MongoCollection
    }

    fun update(){
        data.append("date", Date())
        if(data.containsKey("_id") && col.find(Document().append("_id", data["_id"])).count() > 0){
            col.updateOne(Document().append("_id", data["_id"]), data)
        } else {
            col.insertOne(data)
        }
    }
}