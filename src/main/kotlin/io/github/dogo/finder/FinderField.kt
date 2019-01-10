package io.github.dogo.finder

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
 *
 * @author NathanPB
 * @since 3.1.0
 */
class FinderField {
    /**
     * The name of the field. Will be initialized when the [IFinder] instance is created.
     */
    var name: String = ""

    /**
     * The filter. Empty one means to match anything.
     */
    var doc = Document()

    /**
     * Clears [doc], to match any value on DB.
     */
    fun matchAll() {
        doc = Document()
    }

    /**
     * Fills [doc] with a filter.
     */
    fun matchFilter(filter: String.()->Document) {
        doc = filter(name)
    }
}