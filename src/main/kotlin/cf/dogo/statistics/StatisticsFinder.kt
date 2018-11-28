package cf.dogo.statistics

import org.bson.Document
import kotlin.reflect.KClass

open class StatisticsFinder<T : Statistic>(private val kClass: KClass<T>) : Document() {

    private val constructor = kClass.constructors.firstOrNull{ it -> it.parameters.any { p -> p.type == kClass } }

    private fun query() = Statistic.col.find(this)

    fun find() : T? {
        return query().let {
            if(it.count() > 0) {
                constructor!!.call(it.first())
            } else null
        }
    }
    fun findAll() : List<T> {
        val list = mutableListOf<T>()
        val iterator = query().iterator()
        while (iterator.hasNext()){
            list.add(constructor!!.call(iterator.next()))
        }
        return list
    }

}