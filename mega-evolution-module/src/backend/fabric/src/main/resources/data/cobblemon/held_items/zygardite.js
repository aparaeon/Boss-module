{
    name: "Zygardite",
    spritenum: 666,
    megaStone: "Zygarde-Mega",
    megaEvolves: "Zygarde",
    itemUser: ["Zygarde"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
