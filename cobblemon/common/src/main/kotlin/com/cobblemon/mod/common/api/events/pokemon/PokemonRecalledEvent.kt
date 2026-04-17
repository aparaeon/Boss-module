/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.events.pokemon

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon

/**
 * Event fired when a [PokemonEntity] is recalled.
 *
 * @author Segfault Guy
 * @since March 25th, 2023
 */
data class PokemonRecalledEvent (
    val pokemon: Pokemon,
    val oldEntity: PokemonEntity?
)