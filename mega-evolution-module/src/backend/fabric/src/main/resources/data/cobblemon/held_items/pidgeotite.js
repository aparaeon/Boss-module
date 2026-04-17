{
    name: "Pidgeotite",
    spritenum: 666,
    megaStone: "Pidgeot-Mega",
    megaEvolves: "Pidgeot",
    itemUser: ["Pidgeot"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
