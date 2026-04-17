{
    name: "Sablenite",
    spritenum: 666,
    megaStone: "Sableye-Mega",
    megaEvolves: "Sableye",
    itemUser: ["Sableye"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
