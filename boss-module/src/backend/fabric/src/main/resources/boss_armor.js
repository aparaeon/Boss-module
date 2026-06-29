(function () {
	const Battle = require('./sim/battle').Battle;
	const BattleActions = require('./sim/battle-actions').BattleActions;
	const BattleStream = require('./sim/battle-stream').BattleStream;

	if (!BattleStream.prototype.__bossArmor) {
		const writeLine = BattleStream.prototype._writeLine;
		BattleStream.prototype._writeLine = function (type, message) {
			if (type === 'boss') {
				const parts = String(message).split(' ');
				if (parts.length !== 3) {
					return;
				}
				const taken = parseFloat(parts[1]);
				const dealt = parseFloat(parts[2]);
				if (!isFinite(taken) || !isFinite(dealt)) {
					return;
				}
				const mon = this.battle && this.battle.getPokemonByPNX(parts[0]);
				if (mon) {
					mon.bossTaken = taken;
					mon.bossDealt = dealt;
				}
				return;
			}
			return writeLine.call(this, type, message);
		};
		BattleStream.prototype.__bossArmor = true;
	}

	if (!Battle.prototype.__bossArmor) {
		const spreadDamage = Battle.prototype.spreadDamage;
		Battle.prototype.spreadDamage = function (damage, targetArray, source, effect, instafaint) {
			if (Array.isArray(damage) && targetArray) {
				damage = damage.map(function (value, index) {
					const target = targetArray[index];
					if (typeof value === 'number' && value > 0 && target && target.bossTaken) {
						return Math.max(1, Math.floor(value * target.bossTaken));
					}
					return value;
				});
			}
			return spreadDamage.call(this, damage, targetArray, source, effect, instafaint);
		};
		Battle.prototype.__bossArmor = true;
	}

	if (!BattleActions.prototype.__bossArmor) {
		const modifyDamage = BattleActions.prototype.modifyDamage;
		BattleActions.prototype.modifyDamage = function (baseDamage, pokemon, target, move, suppressMessages) {
			const result = modifyDamage.call(this, baseDamage, pokemon, target, move, suppressMessages);
			if (pokemon && pokemon.bossDealt && typeof result === 'number' && result > 0) {
				return Math.floor(result * pokemon.bossDealt);
			}
			return result;
		};
		BattleActions.prototype.__bossArmor = true;
	}
})();
