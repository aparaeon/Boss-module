{
    name: "Scizorite",
    spritenum: 666,
    megaStone: "Scizor-Mega",
    megaEvolves: "Scizor",
    itemUser: ["Scizor"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
