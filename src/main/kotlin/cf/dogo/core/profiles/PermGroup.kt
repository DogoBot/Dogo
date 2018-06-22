package cf.dogo.core.profiles

import com.mongodb.client.MongoCollection
import org.bson.Document
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class PermGroup(id : String? = null) {
    companion object {
        val col: MongoCollection<Document>
            get() {
                return cf.dogo.core.DogoBot.db?.getCollection("PERMGROUPS") as MongoCollection
            }
    }

    var id = id
        private set
    constructor(doc : Document) : this(doc.getString("id"))
    var doc : Document

    init {
        if(id == null){
            var rand : String
            do {
                rand = Random().nextLong().toString()
            } while (rand.toLong() > 0 && col?.count(Document("ID", rand)) != 0L)
            this.id = rand
            col?.insertOne(Document("ID", this.id))
        }
        if(col.count(Document("ID", this.id)) == 0L){
            col?.insertOne(Document("ID", this.id))
        }
        doc = col?.find(Document("ID", this.id))?.first() as Document
    }

    var name : String
        get() {
            val doc = col?.find(Document("ID", this.id))?.first() as Document
            return if(doc.containsKey("name")) doc.getString("name") else "UNNAMED"
        }
        set(value) {
            col?.updateOne(Document("ID", this.id), Document("\$set", Document("name", value)))
        }

    var priotiry : Int //Less is better
        get() {
            val doc = col?.find(Document("ID", this.id))?.first() as Document
            return if(doc.containsKey("priority")) doc.getInteger("priority") else 0
        }
        set(value) {
            if(id?.toLong() as Long >= 0 && value < 0){
                col?.updateOne(Document("ID", this.id), Document("\$set", Document("priority", 1)))
            } else {
                col?.updateOne(Document("ID", this.id), Document("\$set", Document("priority", value)))
            }
        }

    var include : ArrayList<String>
        get() {
            val doc = col?.find(Document("ID", this.id))?.first() as Document
            return if(doc.containsKey("include")) doc["include"] as ArrayList<String> else ArrayList()
        }
        set(value) {
            col?.updateOne(Document("ID", this.id), Document("\$set", Document("include", value)))
        }

    var exclude : ArrayList<String>
        get() {
            val doc = col?.find(Document("ID", this.id))?.first() as Document
            return if(doc.containsKey("exclude")) doc["exclude"] as ArrayList<String> else ArrayList()
        }
        set(value) {
            col?.updateOne(Document("ID", this.id), Document("\$set", Document("exclude", value)))
        }

    var applyTo : ArrayList<String>
        get() {
            val doc = col?.find(Document("ID", this.id))?.first() as Document
            return if(doc.containsKey("apply")) doc["apply"] as ArrayList<String> else ArrayList()
        }
        set(value) {
            col?.updateOne(Document("ID", this.id), Document("\$set", Document("apply", value)))
        }

    fun hasIncluded(perm: String) : Boolean {
        for(s in include){
            var p2 = s.replace(".", "\\.").replace("\\.*", ".*")
            if(p2.contains("*") && !p2.contains("\\.*")) p2 = p2.replace("*", ".*")
            var pattern2 = Pattern.compile(p2)
            if(pattern2.matcher(perm).matches()) return true
        }
        return false
    }

    fun hasExcluded(perm : String) : Boolean {
        for(s in exclude){
            var p2 = s.replace(".", "\\.").replace("\\.*", ".*")
            if(p2.contains("*") && !p2.contains("\\.*")) p2 = p2.replace("*", ".*")
            var pattern2 = Pattern.compile(p2)
            if(pattern2.matcher(perm).matches()) return true
        }
        return false
    }

    fun can(perm : String) : Boolean {
        return if(hasExcluded(perm)) false else hasIncluded(perm)
    }

    fun affectsEveryone() : Boolean {
        return applyTo.contains("everyone")
    }

    override fun toString(): String {
        return doc.toString()
    }
}