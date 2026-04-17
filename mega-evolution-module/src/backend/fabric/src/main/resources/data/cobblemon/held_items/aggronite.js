{
    name: "Aggronite",
    spritenum: 666,
    megaStone: "Aggron-Mega",
    megaEvolves: "Aggron",
    itemUser: ["Aggron"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
