package io.github.dogo.statistics

import com.mongodb.client.MongoCollection
import io.github.dogo.core.DogoBot
import org.bson.Document
import java.util.*

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
 * Holds Statistics data on a simple [Document] instance.
 *
 * @param[data] document with the statistics data to be imported into a [Statistic] instance.
 *
 * @author NathanPB
 * @since 3.1.0
 */
abstract class Statistic(val data: Document) {
    companion object {
        val col = DogoBot.db?.getCollection("statistics") as MongoCollection
    }

    /**
     * Updates the statistic document on database. If the document already exists, its updated. The 'date' field will be always the current time (when [update] is invoked)
     * Be careful on using this method: Statistics shouldn't be updated, just inserted.
     */
    fun update(){
        data.append("date", Date())
        if(data.containsKey("_id") && col.find(Document().append("_id", data["_id"])).count() > 0){
            col.updateOne(Document().append("_id", data["_id"]), data)
        } else {
            col.insertOne(data)
        }
    }
}