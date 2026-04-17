{
  onPrepareHit(source, target, move) {
      if (move.category === "Status" || move.multihit || move.flags["noparentalbond"] || move.flags["charge"] || move.flags["futuremove"] || move.spreadHit || move.isZ || move.isMax)
        return;
      move.multihit = 3;
    },
     onBasePowerPriority: 30,
    onBasePower(basePower, attacker, defender, move) {
     if (move.hit > 1) {
        delete move.secondaries;
        delete move.self;
        return this.chainModify(0.15);
      }
    },
    onSourceModifySecondaries(secondaries, target, source, move) {
      if (move.id === "secretpower" && move.hit < 2) {
        return secondaries.filter((effect) => effect.volatileStatus === "flinch");
      }
    },
	onModifyMove(move, pokemon) {
      if (move.secondaries && move.hit > 1) {
        delete move.secondaries;
        delete move.self;
        if (move.id === "clangoroussoulblaze")
          delete move.selfBoost;
        move.hasSheerForce = true;
      }
    },
    flags: {},
	name: "teamcombo",
	rating: 4.5,
	num: 1004
}
