{
    name: "Tyranitarite",
    spritenum: 666,
    megaStone: "Tyranitar-Mega",
    megaEvolves: "Tyranitar",
    itemUser: ["Tyranitar"],
    onTakeItem(item, source) {
        if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
        return true;
    },
    num: -999,
    gen: 5,
    isNonstandard: "Past"
}
