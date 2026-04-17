{
    name: "Tatsugirinite",
    spritenum: 666,
    megaStone: "Tatsugiri-Mega",
    megaEvolves: "Tatsugiri",
    itemUser: ["Tatsugiri"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
