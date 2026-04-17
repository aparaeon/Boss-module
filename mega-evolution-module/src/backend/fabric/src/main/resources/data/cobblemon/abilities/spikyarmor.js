{
		onDamagingHit(damage, target, source, move) {
			const side = source.isAlly(target) ? source.side.foe : source.side;
			const Spikes = side.sideConditions['spikes'];
			if (move.category === 'Physical' && (!Spikes || Spikes.layers < 2)) {
				this.add('-activate', target, 'ability: Spiky Armor');
				side.addSideCondition('spikes', target);
			}
		},
		flags: {},
		name: "Spiky Armor",
		rating: 3.5,
		num: 503
}