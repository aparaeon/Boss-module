{
		onStart(pokemon) {
			if (!this.field.setTerrain('psychicterrain') && this.field.isTerrain('psychicterrain')) {
				this.add('-activate', pokemon, 'ability: Magic Pulse');
			}
		},
		onModifySpAPriority: 5,
		onModifySpA(atk, attacker, defender, move) {
			if (this.field.isTerrain('psychicterrain')) {
				this.debug('Magic Pulse boost');
				return this.chainModify([5461, 4096]);
			}
		},
		flags: {},
		name: "Magic Pulse",
		rating: 4.5,
		num: 500
}