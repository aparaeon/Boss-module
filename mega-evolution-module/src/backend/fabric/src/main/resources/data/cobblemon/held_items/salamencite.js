{
    name: "Salamencite",
    spritenum: 666,
    megaStone: "Salamence-Mega",
    megaEvolves: "Salamence",
    itemUser: ["Salamence"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
