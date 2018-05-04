package cf.nathanpb.dogo.core.queue

import java.util.concurrent.LinkedBlockingQueue

open class DogoQueue(name : String) : DogoThread(name, {}) {
    private val queue = LinkedBlockingQueue<QueueAction>()
    init {
        this.run = {
            if(!queue.isEmpty()){
                queue.poll().run()
            }
        }
    }

    open fun submit(action : () -> Unit) {
        queue.add(QueueAction(action))
    }

    fun size() : Int {
        return queue.size
    }

    fun clear() {
        queue.clear()
    }
}