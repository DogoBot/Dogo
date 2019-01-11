package io.github.dogo.utils._static

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
        fun timeSince(ms : Long, now : Long = System.currentTimeMillis()) : Long {
            return now - ms
        }
    }
}