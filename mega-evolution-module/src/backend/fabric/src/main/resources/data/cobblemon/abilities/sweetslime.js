{
    onModifyMovePriority: -5,
    onModifyMove(move) {
      if (!move.ignoreImmunity)
        move.ignoreImmunity = {};
      if (move.ignoreImmunity !== true) {
        move.ignoreImmunity["Poison"] = true;
      }
    },
    flags: {},
    name: "sweetslime",
    rating: 3,
    num: 1929
}