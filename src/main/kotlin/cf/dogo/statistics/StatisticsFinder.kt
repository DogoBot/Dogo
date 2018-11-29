package cf.dogo.statistics

import cf.dogo.interfaces.IFinder
import org.bson.Document
import kotlin.reflect.KClass

open class StatisticsFinder<T : Statistic>(private val kClass: KClass<T>) : Document(), IFinder<T> {

    private val constructor = kClass.constructors.firstOrNull{ it -> it.parameters.any { p -> p.type == kClass } }

    override fun query() = this
    override fun col() = Statistic.col
    override fun map(doc: Document) = constructor?.call(doc)
}