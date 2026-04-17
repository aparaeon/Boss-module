{
  onStart(source) {
    this.add("-ability", source, "squidnapped", "[cobblemon.battle.activate.squidnapped]");
    for (const pokemon of source.foes()) {
      if (!pokemon.volatiles["chimi"]) {
        pokemon.addVolatile("chimi", source);
      }
    }
  },
  onEnd(source) {
    for (const pokemon of source.foes()) {
      if (pokemon.volatiles["chimi"] && pokemon.volatiles["chimi"].source === source) {
        pokemon.removeVolatile("chimi");
      }
    }
  },
  onFoeSwitchIn(target, source) {
    if (!target.volatiles["chimi"]) {
      target.addVolatile("chimi", source);
    }
  },
  name: "squidnapped",
  rating: 4,
  num: 114514
}