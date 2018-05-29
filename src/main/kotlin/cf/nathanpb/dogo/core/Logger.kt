package cf.nathanpb.dogo.core

import cf.nathanpb.dogo.core.queue.DogoQueue
import cf.nathanpb.dogo.utils.ConsoleColors
import java.io.File
import java.io.FileWriter
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*

class Logger constructor(ps : PrintStream, name : String){
    val format = SimpleDateFormat("HH:mm:ss - dd/MM/YYYY")
    private val name = name
    private val ps  = ps;
    private val file = File(File(DogoBot.data?.getString("LOG_PATH")), "$name _${format.format(Date()).replace("/", "-").replace(":", "-")}.log")
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


    fun print(obj : Any?) {
        val s = obj.toString()+ConsoleColors.RESET.toString()
        ps.print(s)
        printToFile(s)
    }

    fun println(obj : Any?){
        val s = obj.toString()+ConsoleColors.RESET.toString()+"\n"
        print(s)
    }

    fun info(obj : Any?, color : ConsoleColors) {
        println("$color["+format.format(Date())+"] [INFO] [${Thread.currentThread().name.toUpperCase()}] "+obj)
    }

    fun info(obj : Any?) {
        info(obj, ConsoleColors.RESET)
    }


    fun warn(obj : Any?){
        println(ConsoleColors.YELLOW.toString()+"["+format.format(Date())+"] [WARN] [${Thread.currentThread().name.toUpperCase()}] "+obj.toString())
    }

    fun error(obj : Any?){
        println(ConsoleColors.BLACK_BOLD.toString()+ConsoleColors.RED_BACKGROUND.toString()+"["+format.format(Date())+"] [ERROR] [${Thread.currentThread().name.toUpperCase()}] "+obj.toString())
    }

    fun getPrintStream() : PrintStream {
        return ps
    }


    private fun printToFile(string: String){
        fwqueue.submit {
            var ns = string;
            for(cs in ConsoleColors.values()){
                ns = ns.replace(cs.toString(), "")
            }
            fw.write(ns)
            fw.flush()
        }
    }
}