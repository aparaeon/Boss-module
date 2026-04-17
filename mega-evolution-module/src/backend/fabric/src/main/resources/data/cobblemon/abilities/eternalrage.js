{
   onResidualOrder: 28,
    onResidualSubOrder: 2,
    onResidual(pokemon) {
      if (!pokemon.hp) return;
      this.heal(pokemon.maxhp / 12);
      for (const target of pokemon.foes()) {
        if (target.species.isMega || target.species.forme?.startsWith("Mega")) {
          this.damage(target.maxhp / 10, target, pokemon);
        }
      }
    },
		flags: {},
		name: "eternalrage",
		rating: 4.5,
		num: 1000
}