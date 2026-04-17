{
    onTryBoost(boost, target, source, effect) {
      if (source && target === source)
        return;
      if (boost.accuracy && boost.accuracy < 0) {
        delete boost.accuracy;
        if (!effect.secondaries) {
          this.add("-fail", target, "unboost", "accuracy", "[from] ability: Aura Power", "[of] " + target);
        }
      }
    },
    onModifyMovePriority: -5,
    onModifyMove(move) {
      move.ignoreEvasion = true;
      if (!move.ignoreImmunity)
        move.ignoreImmunity = {};
      if (move.ignoreImmunity !== true) {
        move.ignoreImmunity["Fighting"] = true;
        move.ignoreImmunity["Normal"] = true;
      }
    },
    flags: { breakable: 1 },
    name: "aurapower",
    rating: 0,
    num: 1782
}