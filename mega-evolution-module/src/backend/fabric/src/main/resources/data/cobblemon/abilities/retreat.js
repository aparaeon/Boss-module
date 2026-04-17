{
   onSwitchOut(pokemon) {
       try { 
    this.add("-ability", pokemon, "retreat", "[msg]");
    
    const result = this.random(3);
    
   if (result === 0) {
        const side = pokemon.side.foe;
        const spikes = side.sideConditions["spikes"];
        if (!spikes || spikes.layers < 3) {
          this.add("-activate", pokemon, "ability: retreat");
          side.addSideCondition("spikes", pokemon);
        }
      } 
      else if (result === 1) {
        this.add("-activate", pokemon, "ability: retreat");
        if (pokemon.side.foe.active[0]) {
          this.boost({ accuracy: -1 }, pokemon.side.foe.active[0], pokemon);
        }
      }
      else {
          this.boost({ spe: -1  }, pokemon.side.foe.active[0], pokemon);
        }
    }
    catch (error) {
      this.add('-message', `${error}`);
    }
  }, 
  onEmergencyExit(target,source, move) {
     try { 
      this.add("-activate", target, "ability: retreat");
      if (!this.canSwitch(target.side) || target.forceSwitchFlag || target.switchFlag)
        return;
      for (const side of this.sides) {
        for (const active of side.active) {
          active.switchFlag = false;
        }
      }
      target.switchFlag = true;
      } catch (error) {
      this.add('-message', `${error}`);
    }
    },
	flags: {},
	name: "retreat",
	rating: 4.5,
	num: 1100
}
