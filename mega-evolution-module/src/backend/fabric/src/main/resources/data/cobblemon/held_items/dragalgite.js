{
    name: "Dragalgite",
    spritenum: 666,
    megaStone: "Dragalge-Mega",
    megaEvolves: "Dragalge",
    itemUser: ["Dragalge"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
