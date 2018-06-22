package cf.dogo.core.queue

import cf.dogo.utils.UnitUtils
import java.util.concurrent.LinkedBlockingQueue


open class DogoQueue(name : String) : Thread(name) {
    private val queue = LinkedBlockingQueue<QueueAction>()

    private var lastTick01 = System.currentTimeMillis()
    private var lastTick02 = System.currentTimeMillis()

    var defaultClock = 10
    var clk = defaultClock
    var overclock = 0

    open var run = {
        if(!queue.isEmpty()){
            queue.poll().run()
        }
    }

    init {
        cf.dogo.core.DogoBot.threads[name] = this
        start()
    }

    override fun run() {
        while(true) {
            Thread.sleep(UnitUtils().hzToMs(getCurrentClock()+1))
            lastTick02 = lastTick01
            lastTick01 = System.currentTimeMillis()
            kotlin.run(run)
        }
    }

    fun getCurrentClock() : Int {
        return clk + overclock
    }

    fun getTps() : Int {
        if(lastTick01 == lastTick02) {
            return 0
        }
        return UnitUtils().msToHz(lastTick01 - lastTick02)
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