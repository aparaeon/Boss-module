package gg.mmorealms.module.gyms.backend.common.dto.gym.clauses;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnArgLambda;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemonParty;

public record GymPreBattleClause(String failMessage, ReturnArgLambda<Boolean, IPokemonParty> clause) {
}