package cf.nathanpb.dogo.core

import cf.nathanpb.dogo.utils.ConsoleColors
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*

class Logger constructor(ps : PrintStream){
    private val ps  = ps;
    val format = SimpleDateFormat("ss:mm:HH - MM/dd/YYYY")

    fun print(obj : Any?) {
        ps.print(obj.toString()+ConsoleColors.RESET.toString())
    }

    fun println(obj : Any?){
        print(obj.toString()+ConsoleColors.RESET.toString()+"\n")
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
}