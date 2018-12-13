package io.github.dogo.core.queue

import io.github.dogo.core.DogoBot
import io.github.dogo.utils.UnitUtils
import java.util.concurrent.LinkedBlockingQueue

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
 * Probably the most important part of Dogo. Queues are things that process one single action per time at a semi-fixed rate.
 *
 * Every [DogoQueue] has its own [Thread].
 *
 * @param[name] the thread name.
 *
 * @author NathanPB
 * @since 3.1.0
 */
open class DogoQueue(name : String) : Thread(name) {
    /**
     * The list of actions to proccess.
     */
    private val queue = LinkedBlockingQueue<QueueAction>()

    /**
     * Used as buffer to calculate the thread tick rate.
     */
    private var lastTick01 = System.currentTimeMillis()

    /**
     * Used as buffer to calculate the thread tick rate.
     */
    private var lastTick02 = System.currentTimeMillis()

    /**
     * The default queue clock (frequency in Hz)
     */
    var defaultClock = 10

    /**
     * The actual queue clock (frequency in Hz)
     */
    var clk = defaultClock

    /**
     * The actual overclock (frequency in Hz)
     */
    var overclock = 0

    /**
     * The action to run every queue tick.
     */
    open var run = {
        if(!queue.isEmpty()){
            queue.poll().run()
        }
    }

    init {
        DogoBot.threads[name] = this
        start()
    }

    override fun run() {
        while(true) {
            Thread.sleep(UnitUtils.hzToMs(getCurrentClock()+1))
            lastTick02 = lastTick01
            lastTick01 = System.currentTimeMillis()
            kotlin.run(run)
        }
    }

    /**
     * Calculates the expected queue clock.
     *
     * @return the expected queue clock.
     */
    fun getCurrentClock() : Int {
        return clk + overclock
    }

    /**
     * Calculates the current queue clock.
     *
     * @return the current queue clock.
     */
    fun getTps() : Int {
        if(lastTick01 == lastTick02) {
            return 0
        }
        return UnitUtils.msToHz(lastTick01 - lastTick02)
    }

    /**
     * Submits an action to queue.
     *
     * @param[action] the action.
     */
    open fun submit(action : () -> Unit) {
        queue.add(QueueAction(action))
    }

    /**
     * Get the queue size.
     *
     * @return the queue size.
     */
    fun queue() : Int {
        return queue.size
    }

    /**
     * Clears the queue.
     */
    fun clear() {
        queue.clear()
    }
}