package io.github.dogo.utils

/**
 * Utility static methods about time unities.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class UnitUtils {
    companion object {

        /**
         * Converts frequency to periods.
         *
         * @param[hz] the frequency value in hertz
         *
         * @return the period in millis.
         */
        fun hzToMs(hz : Int) : Long {
            return (1000 / if(hz == 0) 1 else hz).toLong()
        }

        /**
         * Converts periods to frequency.
         *
         * @param[ms] the period delay in millis.
         *
         * @return the frequency in hertz.
         */
        fun msToHz(ms : Long) : Int {
            return   (1000 / if(ms == 0L) 1 else ms).toInt()
        }

        /**
         * Calculates the delay between two [Long]s.
         *
         * @param[ms] the start of the period.
         * @param[now] the end of period (optional). The default value is [System.currentTimeMillis]
         *
         *  @return the delay between [ms] and [now].
         */
        fun getDelay(ms : Long, now : Long = System.currentTimeMillis()) : Long {
            return now - ms
        }
    }
}