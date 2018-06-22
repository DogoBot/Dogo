package cf.dogo.core.boot

class Phase constructor(display : String, run : () -> Unit){

    private val run : () -> Unit = run
    private val display : String = display

    fun start(){
        kotlin.run(run)
    }

    fun getDisplay() : String {
        return display
    }
}