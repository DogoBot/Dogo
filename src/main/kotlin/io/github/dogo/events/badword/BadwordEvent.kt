package io.github.dogo.events.badword

import io.github.dogo.core.entities.DogoGuild

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
abstract class BadwordEvent(val guild: DogoGuild)