/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.adapters

import com.cobblemon.mod.common.api.spawning.context.RegisteredSpawningContext
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Adapter to a serialized [RegisteredSpawningContext] name to the actual registered object.
 *
 * @since January 28th, 2022
 * @author Hiroku
 */
object RegisteredSpawningContextAdapter : JsonSerializer<RegisteredSpawningContext<*>>, JsonDeserializer<RegisteredSpawningContext<*>> {
    override fun serialize(rctx: RegisteredSpawningContext<*>, type: Type, ctx: JsonSerializationContext) = JsonPrimitive(rctx.name)
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) = SpawningContext.getByName(json.asString)
        ?: throw IllegalArgumentException("No such spawning context: ${json.asString}")
}