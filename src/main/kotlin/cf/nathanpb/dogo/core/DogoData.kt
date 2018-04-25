package cf.nathanpb.dogo.core

import org.json.JSONObject
import java.io.*

/**
 *  Class to store Dogo's configuration, read from a file
 *  @property file File where the configuration is stored
 */
class DogoData constructor(file : File){
    private val file = file
    private var map = HashMap<String, Any>()


    init {
        read()
        setDefault("COMMAND_PREFIX", "!dg")
        setDefault("DEBUG_PROFILE", false)
        write()
    }

    /**
     * Updates the current instance to match its file
     */
    fun read() {
        val fis = FileReader(file)
        var text = fis.readText()
        if(text.isEmpty()){
            text = "{}"
        }

        var json = JSONObject(text)
        fis.close()

        json.keySet().stream()
                .forEach { k -> map.put(k, json.get(k)) }
    }

    /**
     * Updates the current file to match its instance
     */
    fun write() {
        val fos = FileWriter(file)
        try {
            fos.write(toString())
        }catch (ex : Exception){
            ex.printStackTrace()
        } finally {
            fos.close()
        }
    }

    /**
     * Returns an Int from the data
     * @param key Value's key
     */
    fun getInt(key: String) : Int? {
        return getAny(key) as Int
    }


    /**
     * Returns a Long from the data
     * @param key Value's key
     */
    fun getLong(key : String) : Long? {
        return getAny(key) as Long
    }


    /**
     * Returns a Double from the data
     * @param key Value's key
     */
    fun getDouble(key : String) : Double? {
        return getAny(key) as Double
    }


    /**
     * Returns a String from the data
     * @param key Value's key
     */
    fun getString(key : String) : String {
        return getAny(key) as String
    }


    /**
     * Returns a Boolean from the data
     * @param key Value's key
     */
    fun getBoolean(key : String) : Boolean {
        return getAny(key) as Boolean
    }


    /**
     * Returns a value from the data
     * @param key Value's key
     */
    fun getAny(key: String) : Any? {
        return map.get(key)
    }


    /**
     * Defines a value on data and update it all
     * @param key Value's key
     * @param value value to set
     */
    fun set(key : String, value : Any) {
        map.set(key, value)
        write()
    }


    /**
     * Removes a value from the data
     * @param key Value's key
     */
    fun remove(key : String) {
        map.remove(key)
        write()
    }


    /**
     * Defines a value on data (if the value isn't already defined) and update it all
     * @param key Value's key
     * @param value value to set
     */
    fun setDefault(key : String, value: Any) {
        if(!map.containsKey(key)){
            map.put(key, value);
        }
    }

    /**
     * File that the data is matching
     * @return File that the data is matching
     */
    fun getFile() : File {
        return file
    }

    /**
     * Formats the data to JSON
     */
    override fun toString(): String {
        return JSONObject(map).toString()
    }
}