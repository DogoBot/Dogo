package io.github.dogo.commands

import io.github.dogo.core.command.CommandCategory
import io.github.dogo.core.command.CommandContext
import io.github.dogo.core.command.CommandReference
import io.github.dogo.core.command.ReferencedCommand
import io.github.dogo.core.permissions.PermGroup
import io.github.dogo.menus.ListReactionMenu
import io.github.dogo.menus.SelectorReactionMenu
import io.github.dogo.menus.SimpleReactionMenu
import io.github.dogo.menus.TextInputMenu
import io.github.dogo.utils._static.EmoteReference

/*
Copyright 2019 Nathan Bombana

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/**
 *
 * @author NathanPB
 * @since 3.1.0
 */
class Roles : ReferencedCommand(
        CommandReference("role", aliases = "roles", category = CommandCategory.GUILD_ADMINISTRATION, permission = "command.guildowner"),
        {
            val roles = guild!!.g!!.roles
            SelectorReactionMenu(this, roles,
                    { it, _ -> "${it.asMention}\n" },
                    {
                        if(roles.isNotEmpty()) {
                            it.setDescription(langEntry.getText("select")+it.descriptionBuilder)
                        }
                    },
                    { it, instance -> editPermgroup(PermGroup.from(it, guild), this); instance.end() }
            ).showPage(0)
        }
){
    companion object {
        fun editPermgroup(pg: PermGroup, cmd: CommandContext) {
            SimpleReactionMenu(cmd).also { editor ->
                editor.addAction(EmoteReference.PAGE_WITH_CURL, cmd.langEntry.getText("listperms")){
                    val list = mutableListOf<String>()
                    pg.include.forEach { list += ":unlock: ``$it``" }
                    pg.exclude.forEach { list += ":lock: ``$it``" }
                    ListReactionMenu(cmd, list, embedBuild = { it.appendDescription(cmd.langEntry.getText("subtitle")+"\n") })
                            .showPage(0)
                }
                editor.addAction(EmoteReference.HEAVY_PLUS_SIGN, cmd.langEntry.getText("includeperms")){
                    SimpleReactionMenu(cmd).also { editinclude ->
                        editinclude.addAction(EmoteReference.HEAVY_PLUS_SIGN, cmd.langEntry.getText("addperm")){
                            TextInputMenu(cmd){ it, instance ->
                                pg.include.addAll(it.split(" ").filter { !pg.include.contains(it) })
                                cmd.reply("permadded", preset = true)
                                editinclude.send()
                                instance.end()
                            }.let {
                                it.embed.setTitle(cmd.langEntry.getText("addperms"))
                                it.embed.setDescription(cmd.langEntry.getText("typeadd"))
                                it.build()
                                it.send()
                            }
                            editinclude.end(false)
                        }
                        editinclude.addAction(EmoteReference.HEAVY_MINUS_SIGN, cmd.langEntry.getText("removeperm")){
                            TextInputMenu(cmd){ it, instance ->
                                pg.include.removeAll(it.split(" "))
                                cmd.reply("permremoved", preset = true)
                                editinclude.send()
                                instance.end()
                            }.let {
                                it.embed.setTitle(cmd.langEntry.getText("removeperms"))
                                it.embed.setDescription(cmd.langEntry.getText("typeremove"))
                                it.build()
                                it.send()
                            }
                            editinclude.end(false)
                        }
                        editinclude.addAction(EmoteReference.ARROW_LEFT, cmd.langEntry.getText("back")) {
                            editinclude.end()
                            editor.send()
                        }
                    }.let {
                        it.embed.setTitle(cmd.langEntry.getText("editinginclude"))
                        it.build()
                        it.send()
                    }
                    editor.end()
                }
                editor.addAction(EmoteReference.HEAVY_MINUS_SIGN, cmd.langEntry.getText("excludeperms")){
                    SimpleReactionMenu(cmd).also { editexclude ->
                        editexclude.addAction(EmoteReference.HEAVY_PLUS_SIGN, cmd.langEntry.getText("addperm")){
                            TextInputMenu(cmd){ it, instance ->
                                pg.exclude.addAll(it.split(" ").filter { !pg.exclude.contains(it) })
                                cmd.reply("permadded", preset = true)
                                editexclude.send()
                                instance.end()
                            }.let {
                                it.embed.setTitle(cmd.langEntry.getText("addperms"))
                                it.embed.setDescription(cmd.langEntry.getText("typeadd"))
                                it.build()
                                it.send()
                            }
                            editexclude.end()
                        }
                        editexclude.addAction(EmoteReference.HEAVY_MINUS_SIGN, cmd.langEntry.getText("removeperm")){
                            TextInputMenu(cmd){ it, instance ->
                                pg.exclude.removeAll(it.split(" "))
                                cmd.reply("permremoved", preset = true)
                                editexclude.send()
                                instance.end()
                            }.let {
                                it.embed.setTitle(cmd.langEntry.getText("removeperms"))
                                it.embed.setDescription(cmd.langEntry.getText("typeremove"))
                                it.build()
                                it.send()
                            }
                            editexclude.end()
                        }
                        editexclude.addAction(EmoteReference.ARROW_LEFT, cmd.langEntry.getText("back")) {
                            editexclude.end()
                            editor.send()
                        }
                    }.let {
                        it.embed.setTitle(cmd.langEntry.getText("editingexclude"))
                        it.build()
                        it.send()
                    }
                    editor.end()
                }
                editor.embed.setTitle(cmd.langEntry.getText("editing", pg.role?.name ?: "DogoAdmin"))
                editor.build()
            }.send()
        }
    }
}