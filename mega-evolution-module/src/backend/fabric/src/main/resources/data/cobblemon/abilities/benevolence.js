{
 onSourceAfterFaint(length, target, source, effect) {
      if (effect && effect.effectType === "Move") {
        this.boost({ spa: length }, source);
      }
    },
	flags: {},
	name: "benevolence",
	rating: 4.5,
	num: 1005
}
