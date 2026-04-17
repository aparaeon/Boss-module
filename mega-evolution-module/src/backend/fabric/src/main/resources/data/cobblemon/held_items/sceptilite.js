{
    name: "Sceptilite",
    spritenum: 666,
    megaStone: "Sceptile-Mega",
    megaEvolves: "Sceptile",
    itemUser: ["Sceptile"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
