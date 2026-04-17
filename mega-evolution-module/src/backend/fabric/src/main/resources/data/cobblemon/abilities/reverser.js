{
 onChangeBoost(boost, target, source, effect) {
      if (effect && effect.id === "zpower")
        return;
      let i;
      for (i in boost) {
        boost[i] *= -2;
      }
    },
    flags: { breakable: 1 },
	name: "reverser",
	rating: 4.5,
	num: 1003
}
