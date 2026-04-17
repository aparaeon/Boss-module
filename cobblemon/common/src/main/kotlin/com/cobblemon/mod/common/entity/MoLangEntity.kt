/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity

import com.bedrockk.molang.runtime.MoLangEnvironment
import com.bedrockk.molang.runtime.struct.QueryStruct

/**
 * An interface representing an entity that might like to add some variables to a MoLang environment
 * for making animations more adaptive.
 *
 * @author Hiroku
 * @since July 17th, 2023
 */
interface MoLangEntity {
    fun applyQueries(queries: QueryStruct)
    fun applyVariables(environment: MoLangEnvironment)
}