package io.github.dogo.finder

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
 * All the fields to query in a [IFinder] must be annotated with this class.
 *
 * @param[name] the name of the field on database. Empty one means the name of the current field (on source code).
 *
 * @author NathanPB
 * @since 3.1.0
 */
annotation class Findable(val name: String = "")