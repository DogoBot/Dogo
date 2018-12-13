package io.github.dogo.core.queue

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
 * An action to submit to queue.
 *
 * @param[run] what should be executed when the queue finds that action.
 * @see DogoQueue
 *
 * @author NathanPB
 * @since 3.1.0
 */
data class QueueAction(val run : () -> Unit) {

    /**
     * The thread who created this action.
     */
    val sourceThread = Thread.currentThread()

    /**
     * The moment when the action was created (timestamp in millis).
     */
    val creationTime = System.currentTimeMillis()
}