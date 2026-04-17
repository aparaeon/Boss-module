{
    onUpdate(pokemon) {
      try { 
      if (this.gameType !== "doubles")
        return;
       const ally = pokemon.allies()[0];
      const item = pokemon.getItem();
      const hasTatsugirinite = item?.id === "tatsugirinite";
      if (!ally || pokemon.baseSpecies.baseSpecies !== "Tatsugiri" || ally.baseSpecies.baseSpecies !== "Dondozo") {
        if (pokemon.getVolatile("commanding"))
          pokemon.removeVolatile("commanding");
        return;
      }
      if (!pokemon.getVolatile("commanding")) {
        if (ally.getVolatile("commanded"))
          return;
        if (hasTatsugirinite){
          if (pokemon.terastallized) {
        pokemon.terastallized = null;
      }
      if (pokemon.volatiles["dynamax"]) {
        pokemon.removeVolatile("dynamax");
        pokemon.isDynamaxed = false;
        pokemon.maxhp = pokemon.baseMaxhp;
        if (pokemon.hp > pokemon.maxhp) pokemon.hp = pokemon.maxhp;
        this.add("-heal", pokemon, pokemon.getHealth, "[silent]");
      }
      }
       if (!hasTatsugirinite){
        this.queue.cancelAction(pokemon);
        this.add("-activate", pokemon, "ability: Commander", "[of] " + ally);
        pokemon.addVolatile("commanding");}
        ally.addVolatile("commanded", pokemon);
      } else {
        if (!ally.fainted)
          return;
        pokemon.removeVolatile("commanding");
      }
      } catch (error) {
      this.add('-message', `${error}`);
    }
    },
    flags: { failroleplay: 1, noreceiver: 1, noentrain: 1, notrace: 1, failskillswap: 1 },
    name: "Commander",
    rating: 0,
    num: 279
}