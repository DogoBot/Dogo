package cf.dogo.core.profiles

class PermGroupSet() : ArrayList<PermGroup>(){
    constructor(list : List<PermGroup>) : this(){
        addAll(list)
    }


    fun removeFromId(id : String) {
        val matched = ArrayList(this).filter { g -> g.id.equals(id) }
        matched.forEach{g -> this.remove(g)}
    }

    fun sort() {
        val ar = sortedBy { g -> -g.priority }
        clear()
        for(g in ar) add(g)
    }

    fun can(perm : String) : Boolean {
        sort()
        var b = false
        forEach { g ->
            if(g.hasIncluded(perm)) b = true
            if(g.hasExcluded(perm)) b = false
         }
        return b
    }

    fun filterApplied(applied: String) : PermGroupSet {
        return PermGroupSet(filter { g -> g.applyTo.contains(applied) })
    }
}