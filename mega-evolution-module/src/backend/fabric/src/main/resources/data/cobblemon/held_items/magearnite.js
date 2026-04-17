{
    name: "Magearnite",
    spritenum: 666,
    megaStone: "Magearna-Mega",
    megaEvolves: "Magearna",
    itemUser: ["Magearna"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
