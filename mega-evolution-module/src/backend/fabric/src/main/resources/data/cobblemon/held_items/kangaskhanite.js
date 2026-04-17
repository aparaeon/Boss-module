{
    name: "Kangaskhanite",
    spritenum: 666,
    megaStone: "Kangaskhan-Mega",
    megaEvolves: "Kangaskhan",
    itemUser: ["Kangaskhan"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
