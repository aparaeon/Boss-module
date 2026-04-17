/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.events.pokemon

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.trade.TradeParticipant

class TradeCompletedEvent(val tradeParticipant1 : TradeParticipant, val tradeParticipant1Pokemon : Pokemon, val tradeParticipant2 : TradeParticipant, val tradeParticipant2Pokemon : Pokemon)