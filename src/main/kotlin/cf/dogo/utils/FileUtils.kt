package cf.dogo.utils

import java.io.File
import java.io.InputStream
import java.util.*

class FileUtils {

    fun getChars(file : File) : String {
        var result = ""
        Scanner(file).use{
            while(it.hasNextLine()) result+="${it.nextLine()}\n"
        }
        return result
    }

    fun getChars(input : InputStream) : String {
        var result = ""
        Scanner(input).use {
            while(it.hasNextLine()) result+="${it.nextLine()}\n"
        }
        return result
    }
}