{
    name: "Meowsticite",
    spritenum: 666,
    megaStone: "Meowstic-Mega",
    megaEvolves: "Meowstic",
    itemUser: ["Meowstic"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
