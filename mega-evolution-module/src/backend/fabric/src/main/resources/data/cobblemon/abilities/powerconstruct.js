
  {
  onResidualOrder: 29,
  onResidual(pokemon) {
    if (pokemon.baseSpecies.baseSpecies !== "Zygarde" || pokemon.transformed || !pokemon.hp)
      return;
    if (pokemon.species.id === "zygardecomplete" || pokemon.hp > pokemon.maxhp / 2)
      return;
    this.add("-activate", pokemon, "ability: Power Construct");
    pokemon.formeChange("Zygarde-Complete", this.effect, true);

    const item = pokemon.getItem();
    const hasZygardite = item?.id === "zygardite";
    const megaSpecies = this.dex.species.get("zygarde-megac");

    if (hasZygardite && megaSpecies && megaSpecies.exists) {
      if (pokemon.terastallized) {
        pokemon.terastallized = null;
        pokemon.setType(pokemon.baseSpecies.types);
        this.add(
          "-start",
          pokemon,
          "typechange",
          pokemon.getTypes().join("/"),
          "[silent]"
        );
      }
      if (pokemon.volatiles["dynamax"]) {
        pokemon.removeVolatile("dynamax");
        pokemon.isDynamaxed = false;
        pokemon.canDynamax = false;
        pokemon.maxhp = pokemon.baseMaxhp;
        if (pokemon.hp > pokemon.maxhp) pokemon.hp = pokemon.maxhp;
        this.add("-heal", pokemon, pokemon.getHealth, "[silent]");
      }

      pokemon.canTerastallize = null;
      pokemon.canDynamax = false;
      pokemon.maxMoves = null;
      pokemon.canMegaEvo = megaSpecies.name || "Zygarde-MegaC";
      pokemon.hasCoreSwap = true;

    }
    pokemon.baseMaxhp = Math.floor(Math.floor(
      2 * pokemon.species.baseStats["hp"] +
      pokemon.set.ivs["hp"] +
      Math.floor(pokemon.set.evs["hp"] / 4) +
      100
    ) * pokemon.level / 100 + 10);
    const newMaxHP = pokemon.volatiles["dynamax"]
      ? 2 * pokemon.baseMaxhp
      : pokemon.baseMaxhp;
    pokemon.hp = newMaxHP - (pokemon.maxhp - pokemon.hp);
    pokemon.maxhp = newMaxHP;
    this.add("-heal", pokemon, pokemon.getHealth, "[silent]");
  },
  flags: { failroleplay: 1, noreceiver: 1, noentrain: 1, notrace: 1, failskillswap: 1, cantsuppress: 1 },
  name: "Power Construct",
  rating: 5,
  num: 211
}
