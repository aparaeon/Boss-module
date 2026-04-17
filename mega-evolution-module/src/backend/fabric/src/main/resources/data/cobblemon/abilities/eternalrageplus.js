{
   onResidualOrder: 28,
    onResidualSubOrder: 2,
    onResidual(pokemon) {
      if (!pokemon.hp) return;
      this.heal(pokemon.maxhp / 5);
      for (const target of pokemon.foes()) {
        if (target.species.isMega || target.species.forme?.startsWith("Mega")) {
          this.damage(target.maxhp / 3, target, pokemon);
        }
        if (!target.species.isMega && !target.species.forme?.startsWith("Mega")) {
                  this.damage(target.maxhp / 6, target, pokemon);
                }
      }
    },
    onStart(pokemon) {
      this.add("-ability", pokemon, "endlessrage");
    },
    onModifyMove(move) {
      move.ignoreAbility = true;
    },
    onAllyTryBoost(boost, target, source, effect) {
      if (source && target === source || !target.hasType("Grass"))
        return;
      let showMsg = false;
      let i;
      for (i in boost) {
        if (boost[i] < 0) {
          delete boost[i];
          showMsg = true;
        }
      }
      if (showMsg && !effect.secondaries) {
        const effectHolder = this.effectState.target;
        this.add("-block", target, "ability: Flower Veil", "[of] " + effectHolder);
      }
    },
    onAllySetStatus(status, target, source, effect) {
      if (target.hasType("Grass") && source && target !== source && effect && effect.id !== "yawn") {
        this.debug("interrupting setStatus with Flower Veil");
        if (effect.name === "Synchronize" || effect.effectType === "Move" && !effect.secondaries) {
          const effectHolder = this.effectState.target;
          this.add("-block", target, "ability: Flower Veil", "[of] " + effectHolder);
        }
        return null;
      }
    },
    onAllyTryAddVolatile(status, target) {
      if (target.hasType("Grass") && status.id === "yawn") {
        this.debug("Flower Veil blocking yawn");
        const effectHolder = this.effectState.target;
        this.add("-block", target, "ability: Flower Veil", "[of] " + effectHolder);
        return null;
      }
    },
    flags: {},
    name: "eternalrageplus",
		flags: {},
		name: "eternalrageplus",
		rating: 4.5,
		num: 1000
}