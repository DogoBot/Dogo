package cf.nathanpb.dogo.core

import java.util.*


class DogoThread(name : String, run : () -> Unit) : TimerTask() {
    private var lastTick01 = System.currentTimeMillis()
    private var lastTick02 = System.currentTimeMillis()
    private var run = run
    private var timer = Timer(name)

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

    fun shedule(delay : Long, period : Long){
        timer.schedule(this, delay, period-1)
    }

}