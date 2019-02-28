package io.github.dogo.badwords

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.User

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
 * Event thrown on every badword action.
 *
 * @author NathanPB
 * @since 3.1.0
 */
abstract class BadwordEvent(val guild: Guild)

/**
 * Event thrown when a list of badwords were removed.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class BadwordListRemovedEvent(
        guild: Guild,
        val removedBy: User,
        val words: List<String>
) : BadwordEvent(guild)

/**
 * Event thrown when a badword is added.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class BadwordAddedEvent(
        guild: Guild,
        val addedBy: User,
        val word: String
) : BadwordEvent(guild)

/**
 * Event thrown when a list of badwords were added.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class BadwordListAddedEvent(
        guild: Guild,
        val addedBy: User,
        val words: List<String>
) : BadwordEvent(guild)

/**
 * Event thrown when a badword is removed.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class BadwordRemovedEvent(
        guild: Guild,
        val removedBy: User,
        val word: String
) : BadwordEvent(guild)

/**
 * Event thrown when a badword is removed.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class BadwordMessageCensoredEvent(
        guild: Guild,
        val message: Message,
        val words: List<String>
) : BadwordEvent(guild)