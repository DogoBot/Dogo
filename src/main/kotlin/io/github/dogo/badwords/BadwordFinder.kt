package io.github.dogo.badwords

import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.finder.Findable
import io.github.dogo.finder.FinderField
import io.github.dogo.finder.IFinder
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
 * Interface used to create a Finder for any subclass of [BadwordProfile].
 *
 * @author NathanPB
 * @since 3.1.0
 */
class BadwordFinder : IFinder<BadwordProfile>() {

    /**
     * The guild that the profile belongs.
     */
    @Findable
    val guild = FinderField()

    /**
     * The badword list.
     */
    @Findable
    val badwords = FinderField()

    override val col = BadwordProfile.col
    override fun map(doc: Document) = BadwordProfile.parse(doc)
}