package cf.dogo.interfaces

import com.mongodb.client.MongoCollection
import org.bson.Document

interface IFinder<T> {

    fun find() = map(col().find(query()).first())
    fun findAll() = col().find(query()).toList().map { map(it) }.toList()
    fun count() = col().count(query())

    fun map(doc: Document) : T?
    fun col() : MongoCollection<Document>
    fun query() : Document
}