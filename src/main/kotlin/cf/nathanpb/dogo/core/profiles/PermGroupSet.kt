package cf.nathanpb.dogo.core.profiles

class PermGroupSet : ArrayList<PermGroup>(){


    fun removeFromId(id : String) {
        val matched = ArrayList(this).filter { g -> g.id.equals(id) }
        matched.forEach{g -> this.remove(g)}
    }

    fun sort() {
        val ar = sortedBy { g -> -g.priotiry }
        clear()
        for(g in ar) add(g)
    }

    fun can(perm : String) : Boolean {
        var b = false
        forEach { g ->
            if(g.hasIncluded(perm)) b = true
            if(g.hasExcluded(perm)) b = false
         }
        return b
    }
}