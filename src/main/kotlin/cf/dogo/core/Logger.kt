package cf.dogo.core

import cf.dogo.core.queue.DogoQueue
import java.io.File
import java.io.FileWriter
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*

class Logger constructor(ps : PrintStream, name : String){
    val format = SimpleDateFormat("HH:mm:ss - dd/MM/YYYY")
    private val name = name
    private val ps  = ps;
    private val file = File(File(cf.dogo.core.DogoBot.data?.getString("LOG_PATH")), "$name _${format.format(Date()).replace("/", "-").replace(":", "-")}.log")
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
        val s = obj.toString()+cf.dogo.utils.ConsoleColors.RESET.toString()
        ps.print(s)
        printToFile(s)
    }

    fun println(obj : Any?){
        val s = obj.toString()+cf.dogo.utils.ConsoleColors.RESET.toString()+"\n"
        print(s)
    }

    fun info(obj : Any?, color : cf.dogo.utils.ConsoleColors) {
        println("$color["+format.format(Date())+"] [INFO] [${Thread.currentThread().name.toUpperCase()}] "+obj)
    }

    fun info(obj : Any?) {
        info(obj, cf.dogo.utils.ConsoleColors.RESET)
    }


    fun warn(obj : Any?){
        println(cf.dogo.utils.ConsoleColors.YELLOW.toString()+"["+format.format(Date())+"] [WARN] [${Thread.currentThread().name.toUpperCase()}] "+obj.toString())
    }

    fun error(obj : Any?){
        println(cf.dogo.utils.ConsoleColors.BLACK_BOLD.toString()+cf.dogo.utils.ConsoleColors.RED_BACKGROUND.toString()+"["+format.format(Date())+"] [ERROR] [${Thread.currentThread().name.toUpperCase()}] "+obj.toString())
    }

    fun getPrintStream() : PrintStream {
        return ps
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