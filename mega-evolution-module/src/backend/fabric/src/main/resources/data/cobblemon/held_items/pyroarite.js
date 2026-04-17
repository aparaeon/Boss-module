{
    name: "Pyroarite",
    spritenum: 666,
    megaStone: "Pyroar-Mega",
    megaEvolves: "Pyroar",
    itemUser: ["Pyroar"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
