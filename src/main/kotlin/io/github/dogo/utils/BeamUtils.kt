package io.github.dogo.utils

import java.lang.management.ManagementFactory

class BeamUtils {
    companion object {
        fun usedMemory() : Long {
            return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)
        }

        fun maxMemory() : Long {
            return Runtime.getRuntime().totalMemory() / (1024 * 1024)
        }

        fun usedCPU() : Double {
            return ManagementFactory.getOperatingSystemMXBean().systemLoadAverage/ ManagementFactory.getOperatingSystemMXBean().availableProcessors
        }
    }
}