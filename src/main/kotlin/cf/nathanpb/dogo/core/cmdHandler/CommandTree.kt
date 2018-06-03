package cf.nathanpb.dogo.core.cmdHandler

import java.util.*
import kotlin.collections.ArrayList

class CommandTree(factory : CommandFactory) : ArrayList<DogoCommand>(){
    var args  = ArrayList<String>()

    constructor(args : String, factory : CommandFactory) : this(factory){
        if(args.isEmpty()) return
        var lastCmd : ArrayList<DogoCommand> = ArrayList(factory.commands.values)
        var index = 0

        for(cmd in lastCmd){
            if(cmd.getTriggers().contains(args.split(" ")[index])){
                lastCmd = cmd.children
                this.add(cmd)
                index++
            }
        }

        if(args.contains(" ")) {
            for(i in index..args.split(" ").size-1){
                this.args.add(args.split(" ")[i])
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