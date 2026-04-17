{
	onStart(pokemon) {
		pokemon.abilityState.choiceLock = "";
	},
	onBeforeMove(pokemon, target, move) {
		if (move.isZOrMaxPowered || move.id === 'struggle') return;
		if (pokemon.abilityState.choiceLock && pokemon.abilityState.choiceLock !== move.id) {
			this.addMove('move', pokemon, move.name);
			this.attrLastMove('[still]');
			this.debug("Disabled by Ninja Way");
			this.add('-fail', pokemon);
			return false;
		}
	},
	onModifyMove(move, pokemon) {
		if (pokemon.abilityState.choiceLock || move.isZOrMaxPowered || move.id === 'struggle') return;
		pokemon.abilityState.choiceLock = move.id;
	},
	onModifySpAPriority: 1,
	onModifySpA(spa, pokemon) {
		if (pokemon.volatiles['dynamax']) return;
		this.debug('Ninja Way SpA Boost');
		return this.chainModify(1.5);
	},
	onDisableMove(pokemon) {
		if (!pokemon.abilityState.choiceLock) return;
		if (pokemon.volatiles['dynamax']) return;
		for (const moveSlot of pokemon.moveSlots) {
			if (moveSlot.id !== pokemon.abilityState.choiceLock) {
				pokemon.disableMove(moveSlot.id, false, this.effectState.sourceEffect);
			}
		}
	},
	onEnd(pokemon) {
		pokemon.abilityState.choiceLock = "";
	},
	flags: {},
	name: "Ninja Way",
	rating: 4.5,
	num: 501
}
