package cf.dogo.core

import cf.dogo.core.queue.DogoQueue
import cf.dogo.utils.ConsoleColors
import java.io.File
import java.io.FileWriter
import java.io.OutputStream
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*

class Logger constructor(os : OutputStream?, private val name : String) : PrintStream(os) {
    val format = SimpleDateFormat("HH:mm:ss - dd/MM/YYYY")
    private val file = File(File(DogoBot.data.LOGGER_PATH), "$name _${format.format(Date()).replace("/", "-").replace(":", "-")}.log")
    val fwqueue = DogoQueue("LOGGER_FW_QUEUE")
    init {
        if(!file.exists()) {
            if(file.parentFile != null && !file.parentFile.exists()){
                file.parentFile.mkdirs()
            }
            file.createNewFile()
        }
    }
    val fw = FileWriter(file, true)



    fun printlnRaw(obj : Any?){
        print("$obj \n")
    }

    override fun print(obj : Any?) {
        super.println(obj)
        printToFile(obj.toString())
    }

    override fun println(obj : Any?) {
        info(obj)
    }

    fun info(obj : Any?, color : cf.dogo.utils.ConsoleColors = ConsoleColors.RESET, newline : Boolean = true) {
        var content = "$color[${format.format(Date())}] [INFO] [${Thread.currentThread().name.toUpperCase()}] $obj ${ConsoleColors.RESET}"
        if(newline) content+="\n"
        print(content)
    }

    fun warn(obj : Any?){
        print("${ConsoleColors.YELLOW}[${format.format(Date())}] [WARN] [${Thread.currentThread().name.toUpperCase()}] $obj ${ConsoleColors.RESET} \n")
    }
    fun error(obj : Any?){
        print("${ConsoleColors.BLACK_BOLD} ${ConsoleColors.RED_BACKGROUND}[${format.format(Date())}] [WARN] [${Thread.currentThread().name.toUpperCase()}] $obj ${ConsoleColors.RESET} \n")
    }

    private fun printToFile(string: String){
        fwqueue.submit {
            var ns = string;
            for(cs in cf.dogo.utils.ConsoleColors.values()){
                ns = ns.replace(cs.toString(), "")
            }
            fw.write(ns)
            fw.flush()
        }
    }
}