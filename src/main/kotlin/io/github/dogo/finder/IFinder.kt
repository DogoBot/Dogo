package io.github.dogo.finder

import com.mongodb.client.MongoCollection
import org.bson.Document
import kotlin.reflect.full.declaredMemberProperties

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
 * Class used to easily create Profile Finders
 *
 * @author NathanPB
 * @since 3.1.0
 */

abstract class IFinder<T> {

    /**
     * Stores the fields properfies
     */
    val selectors = mutableListOf<FinderField>()

    /**
     * Initialize the fields properties. Must be called on init {} of each subclass AFTER the fields.
     */
    protected fun initialize(){
        this::class.declaredMemberProperties
                .filter { it.annotations.any { it.annotationClass == Findable::class }}
                .forEach {
                    val an = it.annotations.first { a -> a.annotationClass == Findable::class } as Findable
                    (it.getter.call(this) as FinderField).let { f ->
                        f.name = if(an.name.isEmpty()) { it.name } else { an.name }
                        selectors += f
                    }
                }
    }

    /**
     * The collection to find the documents
     */
    abstract val col: MongoCollection<Document>

    /**
     * @return the number matches on database.
     */
    fun count() = col.count(buildQuery())

    /**
     * @return the first match on database.
     */
    fun find() = col.find(buildQuery()).first()?.let { map(it) }

    /**
     * @return all the matches on database.
     */
    fun findAll() = col.find(buildQuery())
            .toList().mapNotNull<Document, Any> { map(it) }
            as List<T>

    /**
     * Used to filter the query to match the attributes you need.
     *
     * @return the query filter.
     */
    fun buildQuery(): Document {
        val filters = selectors.filter { it.doc.isNotEmpty() }
        return when(filters.size) {
            0 -> Document()
            1 -> filters.first().doc
            else -> Document().append("\$and", filters.map { it.doc })
        }
    }

    /**
     * Alias for [Document.append]
     */
    fun createDocument(k: String, v: Any?) = Document().append(k, v)

    /**
     * Maps a [Document] into [T].
     *
     * @param[doc] the [Document].
     * @return a [T] instance created from [doc].
     */
    abstract fun map(doc: Document): T?
}