package io.github.dogo.utils

import java.lang.management.ManagementFactory

/**
 * Utility static methods about Java Beam.
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