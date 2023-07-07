/*
This program is/was a part of the LevelledMobs project's source code.
Copyright (C) 2023  Lachlan Adamson (aka lokka30)
Copyright (C) 2023  LevelledMobs Contributors

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.arcaneplugins.levelledmobs.plugin.bukkit.rule.component.context

import io.github.arcaneplugins.levelledmobs.plugin.bukkit.rule.component.Rule
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import java.util.*

//todo doc
class Context(

    val other: MutableMap<String, Any> = mutableMapOf(),

    /* commonly accessed data is stored as a variable */
    var ruleStack: Stack<Rule> = Stack(),
    var entity: Entity? = null,
    var event: Event? = null,
    var livingEntity: LivingEntity? = null,
    var location: Location? = null,
    var player: Player? = null,
    var rule: Rule? = null,
    var world: World? = null,

)