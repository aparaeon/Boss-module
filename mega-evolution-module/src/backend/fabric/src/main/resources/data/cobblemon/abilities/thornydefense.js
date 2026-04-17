{
	onDamagingHitOrder: 1,
    onDamagingHit(damage, target, source, move) {
      if (this.checkMoveMakesContact(move, source, target, true)) {
        this.damage(source.baseMaxhp / 8, source, target);
      }
    }, 
	onTryHit(pokemon, target, move) {
      if (move.flags["bullet"]) {
        this.add("-immune", pokemon, "[from] ability: ThornyDefense");
        return null;
      }
    },
	flags: {},
	name: "thornydefense",
	rating: 4.5,
	num: 1002
}
