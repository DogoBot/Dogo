package io.github.dogo.utils._static

import io.github.dogo.core.DogoBot
import sun.misc.Unsafe
import java.io.File
import java.lang.management.ManagementFactory
import java.text.SimpleDateFormat
import java.util.*

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

        /**
         * Take Thread Dump and Heap Dump.
         * Note: IT'S ONLY AVAILABLE ON LINUX ENVIRONMENTS WITH JMAP AND JSTACK ON PATH.
         * The dumps are stored in dumps/dd-MM-YYY  HH-MM-ss.bin and dumps/dd-MM-YYY  HH-MM-ss.tdump
         */
        fun takeDumps(){
            val currDate = SimpleDateFormat("dd-MM-YYYY_HH-mm-ss").format(Date())
            val heapDump = File(DogoBot.dynamicDir, "$currDate.bin")
            val threadDump = File(DogoBot.dynamicDir, "$currDate.tdump")

            DogoBot.logger.info("Taking Heap Dump and Thread Dump...")
            SystemUtils.exec("jmap -dump:format=b,file=$heapDump ${BeamUtils.pid} ")
            SystemUtils.exec("jstack -l ${BeamUtils.pid} > $threadDump")
            DriveUtils.dumpUploaderThread.submit {
                arrayOf(heapDump, threadDump)
                        .filter { it.exists() && it.length() > 0 }
                        .forEach {
                            val dir = DriveUtils.getDir(DogoBot.data.DUMPS.PATH).firstOrNull() ?: DriveUtils.mkdir(DogoBot.data.DUMPS.PATH)
                            DriveUtils.toDrive(it, dir.id)
                            it.delete()
                        }
            }
        }

        /**
         * The JVM pid.
         */
        val pid = ManagementFactory.getRuntimeMXBean().name.split("@")[0].toInt()

        /**
         * Simple container for [Unsafe]
         */
        val unsafe = Unsafe::class.java.getDeclaredField("theUnsafe")
            .let {
                it.isAccessible = true
                it.get(null) as Unsafe
            }
    }
}