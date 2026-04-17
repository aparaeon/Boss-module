{
 onSourceAfterFaint(length, target, source, effect) {
      if (effect && effect.effectType === "Move") {
        this.boost({ spe: length }, source);
      }
    },
    onDamagingHit(damage, target, source, effect) {
      this.boost({ def: 1 });
    },
	flags: {},
	name: "warriorglory",
	rating: 4.5,
	num: 1005
}
