package cf.dogo.statistics

import cf.dogo.core.entities.DogoUser
import org.bson.Document
import java.lang.RuntimeException

data class TicTacToeStatistics(val table: String, val p1: DogoUser, val p2: DogoUser, val winner: DogoUser?) : Statistic({
    if(winner != null && !(p1 == winner || p2 == winner)){
        throw RuntimeException("Winner ${winner.id} is neither p1(${p1.id}) or p2(${p2.id})")
    }
    Document().append("table", table).append("p1", p1.id).append("p2", p2.id).append("winner", winner?.id)
}()){
    constructor(doc: Document) : this(doc.getString("table"), DogoUser(doc.getString("p1")), DogoUser(doc.getString("p2")), DogoUser(doc.getString("winner")))

    class Finder : StatisticsFinder<TicTacToeStatistics>(TicTacToeStatistics::class) {
        var p1 : DogoUser
            get() = DogoUser(getString("p1"))
            set(it){append("p1", it.id)}

        var p2: DogoUser
            get() = DogoUser(getString("p2"))
            set(it){append("p2", it.id)}

        var winner: DogoUser
            get() = DogoUser(getString("winner"))
            set(it){append("winner", it.id)}

        var table: String
            get() = getString("table")
            set(it){append("table", it)}
    }
}