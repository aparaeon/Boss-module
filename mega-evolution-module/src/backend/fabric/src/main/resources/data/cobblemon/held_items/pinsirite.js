{
    name: "Pinsirite",
    spritenum: 666,
    megaStone: "Pinsir-Mega",
    megaEvolves: "Pinsir",
    itemUser: ["Pinsir"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
