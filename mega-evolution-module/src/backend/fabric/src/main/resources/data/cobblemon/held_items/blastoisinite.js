{
    name: "Blastoisinite",
    spritenum: 666,
    megaStone: "Blastoise-Mega",
    megaEvolves: "Blastoise",
    itemUser: ["Blastoise"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
