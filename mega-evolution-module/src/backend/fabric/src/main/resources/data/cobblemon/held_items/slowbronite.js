{
    name: "Slowbronite",
    spritenum: 666,
    megaStone: "Slowbro-Mega",
    megaEvolves: "Slowbro",
    itemUser: ["Slowbro"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
