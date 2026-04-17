/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon

import com.cobblemon.mod.common.api.moves.MoveTemplate
class LevelUpMoves : HashMap<Int, MutableList<MoveTemplate>>() {
    fun getLevelUpMovesUpTo(level: Int) = entries.filter { it.key <= level }.sortedBy { it.key }.flatMap { it.value }.toSet()
}