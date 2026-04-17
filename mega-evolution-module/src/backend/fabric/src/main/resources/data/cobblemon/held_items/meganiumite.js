{
    name: "Meganiumite",
    spritenum: 666,
    megaStone: "Meganium-Mega",
    megaEvolves: "Meganium",
    itemUser: ["Meganium"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
