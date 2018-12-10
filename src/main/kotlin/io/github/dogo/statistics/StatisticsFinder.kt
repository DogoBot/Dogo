package io.github.dogo.statistics

import io.github.dogo.interfaces.IFinder
import org.bson.Document
import kotlin.reflect.KClass

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
 * Interface used to create a Finder for any subclass of [Statistic].
 *
 * @param[T] the statistic type.
 *
 * @author NathanPB
 * @since 3.1.0
 */
abstract class StatisticsFinder<T : Statistic> : Document(), IFinder<T> {
    override fun query() = this
    override fun col() = Statistic.col
}