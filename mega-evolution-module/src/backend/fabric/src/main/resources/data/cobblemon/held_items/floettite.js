{
    name: "Floettite",
    spritenum: 666,
    megaStone: "Floette-Mega",
    megaEvolves: "Floette",
    itemUser: ["Floette"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
