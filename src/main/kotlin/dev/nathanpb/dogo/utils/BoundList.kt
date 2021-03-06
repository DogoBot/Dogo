package dev.nathanpb.dogo.utils

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
 * List implementation bound with database.
 *
 * @param[push] must push the element to database.
 * @param[pull] must pull the element from database.
 * @param[all] must return all the elements on database.
 *
 * @author NathanPB
 * @since 3.1.0
 */
open class BoundList<T>(
        private val push: (T)->Unit,
        private val pull: (T)->Unit,
        private val all: ()->List<T>
) : MutableList<T> {
    
    override fun add(element: T) = push(element).let { true }

    
    override fun remove(element: T) = pull(element).let { true }

    
    override fun get(index: Int): T = all()[index]

    
    override fun addAll(elements: Collection<T>) = elements.forEach(push).let { true }


    
    override fun removeAll(elements: Collection<T>) = elements.forEach(pull).let { true }


    
    override fun clear() = forEach(pull)


    
    override fun contains(element: T) = all().contains(element)


    
    override fun containsAll(elements: Collection<T>) = all().containsAll(elements)


    
    override fun indexOf(element: T) = all().indexOf(element)


    
    override fun lastIndexOf(element: T) = all().lastIndexOf(element)


    
    override fun isEmpty() = all().isEmpty()


    
    override fun iterator() = all().toMutableList().iterator()


    
    override fun listIterator() = all().toMutableList().listIterator()


    
    override fun listIterator(index: Int) = all().toMutableList().listIterator(index)


    
    override fun subList(fromIndex: Int, toIndex: Int) = all().toMutableList().subList(fromIndex, toIndex)


    override fun toString() = all().toString()
    
    override val size = all().size

    /**
     * Unsupported Operation. Would result in [UnsupportedOperationException]
     */
    override fun add(index: Int, element: T)                 = throw UnsupportedOperationException("Indexing not supported")

    /**
     * Unsupported Operation. Would result in [UnsupportedOperationException]
     */
    override fun addAll(index: Int, elements: Collection<T>) = throw UnsupportedOperationException("Indexing not supported")

    /**
     * Unsupported Operation. Would result in [UnsupportedOperationException]
     */
    override fun removeAt(index: Int)                        = throw UnsupportedOperationException("Indexing not supported")

    /**
     * Unsupported Operation. Would result in [UnsupportedOperationException]
     */
    override fun set(index: Int, element: T)                 = throw UnsupportedOperationException("Indexing not supported")

    /**
     * Unsupported Operation. Would result in [UnsupportedOperationException]
     */
    override fun retainAll(elements: Collection<T>)          = throw UnsupportedOperationException()
}