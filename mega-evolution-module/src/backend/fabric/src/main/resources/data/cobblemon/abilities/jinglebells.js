{
    name: "Jinglebells",
    rating: 5,
    num: -102,
    onStart(pokemon) {
        pokemon.addVolatile("jinglebells");
    },
    flags: {},
    condition: {
        onStart(pokemon) {
            this.effectState.numConsecutive = 0;
        },
        onTryMovePriority: -2,
		onTryMove(pokemon, target, move) {
            if (move.flags.sound) {
                this.effectState.numConsecutive++;
            } else {
                this.effectState.numConsecutive = 0;
            }
        },
        onModifyDamage(damage, source, target, move) {
            const dmgMod = [4096, 4915, 5734, 6553, 7372, 8192];
            const numConsecutive = this.effectState.numConsecutive > 5 ? 5 : this.effectState.numConsecutive;
            this.debug(`Current Jinglebells boost: ${dmgMod[numConsecutive]}/4096`);
            return this.chainModify([dmgMod[numConsecutive], 4096]);
        }
    }
}