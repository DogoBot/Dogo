package io.github.dogo.interfaces

import com.mongodb.client.MongoCollection
import org.bson.Document

interface IFinder<T> {

    fun find() = if(count() >= 1) map(col().find(query()).first()) else null
    fun findAll() : List<T> {
        return col().find(query())
                .toList().mapNotNull<Document, Any> { map(it) }
                as List<T>
    }
    fun count() = col().count(query())

    fun map(doc: Document) : T?
    fun col() : MongoCollection<Document>
    fun query() : Document
}