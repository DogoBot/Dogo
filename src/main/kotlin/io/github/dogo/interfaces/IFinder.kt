package io.github.dogo.interfaces

import com.mongodb.client.MongoCollection
import io.github.dogo.statistics.Statistic
import org.bson.Document

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
 * Interface to easily create Profile Finders.
 *
 * @author NathanPB
 * @since 3.1.0
 */
interface IFinder<T> {

    /**
     * @return the first match on database.
     */
    fun find() = if(count() >= 1) map(col().find(query()).first()) else null

    /**
     * @return all the matches on database.
     */
    fun findAll() : List<T> {
        return col().find(query())
                .toList().mapNotNull<Document, Any> { map(it) }
                as List<T>
    }

    /**
     * @return the number matches on database.
     */
    fun count() = col().count(query())

    /**
     * Maps a [Document] into [T].
     *
     * @param[doc] the [Document].
     * @return a [T] instance created from [doc].
     */
    fun map(doc: Document) : T?

    /**
     * The collection to query documents.
     *
     * @return the collection that [find], [findAll] and [count] should look for results.
     */
    fun col() : MongoCollection<Document>

    /**
     * Used to filter the query to match the attributes you need.
     *
     * @return the query filter.
     */
    fun query() : Document
}