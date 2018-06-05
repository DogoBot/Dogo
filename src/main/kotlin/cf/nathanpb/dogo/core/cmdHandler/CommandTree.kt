package cf.nathanpb.dogo.core.cmdHandler

import java.util.*
import kotlin.collections.ArrayList

class CommandTree(factory : CommandFactory) : ArrayList<DogoCommand>(){
    var args  = ArrayList<String>()

    constructor(args : String, factory : CommandFactory) : this(factory){
        if(args.isEmpty()) return
        var dept = 0
        var next = factory.commands.values

        root@ while (!next.isEmpty()) {
            val i = next.iterator()
            while (i.hasNext()) {
                val r = i.next()
                if (args.split(" ").size > dept && r.getTriggers().contains(args.split(" ")[dept].toLowerCase())) {
                    next = r.children
                    this.add(r)
                    dept++
                    break
                } else if (!i.hasNext()) break@root
            }
        }

        if(args.contains(" ")) {
            for(i in dept..args.split(" ").size){
                this.args.add(args.split(" ")[i-1])
            }
        }
    }

    constructor(cmd : DogoCommand, factory: CommandFactory) : this(factory){
        var cmd : DogoCommand?= cmd;
        while (cmd != null){
            this.add(cmd)
            cmd = cmd.getParent()
        }
    }
}