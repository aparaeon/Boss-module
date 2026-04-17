/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.spawning.prospecting

import com.cobblemon.mod.common.api.spawning.WorldSlice
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.api.spawning.spawner.Spawner
import com.cobblemon.mod.common.api.spawning.spawner.SpawningArea

/**
 * Interface responsible for slicing out an async-save [WorldSlice] that can be used for generating
 * [SpawningContext]s, specifically [AreaSpawningContext]s.
 *
 * @author Hiroku
 * @since January 29th, 2022
 */
interface SpawningProspector {
    fun prospect(spawner: Spawner, area: SpawningArea): WorldSlice
}