package io.github.dogo.utils

class UnitUtils {
    companion object {
        fun hzToMs(hz : Int) : Long {
            return (1000 / if(hz == 0) 1 else hz).toLong()
        }

        fun msToHz(ms : Long) : Int {
            return   (1000 / if(ms == 0L) 1 else ms).toInt()
        }

        fun getDelay(ms : Long, now : Long = System.currentTimeMillis()) : Long {
            return now - ms;
        }
    }
}