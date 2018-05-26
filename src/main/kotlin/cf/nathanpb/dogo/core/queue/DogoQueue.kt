package cf.nathanpb.dogo.core.queue

import cf.nathanpb.dogo.core.DogoBot
import java.util.*
import java.util.concurrent.LinkedBlockingQueue


open class DogoQueue(name : String) : TimerTask() {
    private val queue = LinkedBlockingQueue<QueueAction>()
    private var lastTick01 = System.currentTimeMillis()
    private var lastTick02 = System.currentTimeMillis()
    public val name = name
    open var run = {
        if(!queue.isEmpty()){
            queue.poll().run()
        }
    }
    private var timer = Timer(name)
    var minClock = 100

    init {
        DogoBot.threads.put(name, this)
    }

    override fun run() {
        kotlin.run(run)
        lastTick02 = lastTick01
        lastTick01 = System.currentTimeMillis()
    }

    fun getTps() : Int {
        if(lastTick01.equals(lastTick02)) {
            return 0
        }
        return 1000 / (lastTick01 - lastTick02).toInt()
    }

    open fun shedule(period : Long) {
        timer.schedule(this, 0, period-1)
    }



    open fun submit(action : () -> Unit) {
        queue.add(QueueAction(action))
    }

    fun queue() : Int {
        return queue.size
    }

    fun clear() {
        queue.clear()
    }
}