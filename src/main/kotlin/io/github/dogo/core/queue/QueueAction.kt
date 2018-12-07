package io.github.dogo.core.queue

class QueueAction(run : () -> Unit) {
    val sourceThread = Thread.currentThread()
    val creationTime = System.currentTimeMillis()
    val run = run

    fun run(){
        kotlin.run(run)
    }

}