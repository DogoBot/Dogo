package io.github.dogo.utils._static

import java.lang.management.ManagementFactory

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
 * Utility static methods about Java Bean.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class BeamUtils {
    companion object {

        /**
         * @return the memory used by JVM in megabytes.
         */
        fun usedMemory() : Long {
            return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)
        }

        /**
         * @return the memory allocated to JVM in megabytes.
         */
        fun maxMemory() : Long {
            return Runtime.getRuntime().totalMemory() / (1024 * 1024)
        }

        /**
         * @return the average CPU usage. In windows it will always return -0.125
         */
        fun usedCPU() : Double {
            return ManagementFactory.getOperatingSystemMXBean().systemLoadAverage/ ManagementFactory.getOperatingSystemMXBean().availableProcessors
        }
    }
}