{
        onStart(source) {
            this.field.addPseudoWeather("gravity", source);
            if (this.field.pseudoWeatherState === undefined) {
            this.field.pseudoWeatherState = { source: source };
        }
        },
        onEnd(pokemon) {
            try {
                this.add('-message', `触发 ${pokemon.name}`);
                if (this.field.pseudoWeatherState === undefined) {
                    this.add('-message', `undefined`);
                    this.field.removePseudoWeather("gravity");
                    return;
                }
                if (this.field.pseudoWeatherState.source !== pokemon) {
                    return;
                }

                let foundReplacement = false;

                for (const target of this.getAllActive()) {

                    if (target === pokemon) {
                        continue;
                    }

                    if (target.hasAbility("moonshot")) {
                        this.field.pseudoWeatherState.source = target;
                        foundReplacement = true;
                        break;
                    }
                }

                if (!foundReplacement) {
                    this.field.removePseudoWeather("gravity");
                }
            
            } catch (error) {
                this.add('-message', `${error}`);
                this.field.removePseudoWeather("gravity");
        }
    },
      onResidualOrder: 28,
    onResidualSubOrder: 2,
    onResidual(pokemon,source) {
       this.field.addPseudoWeather("gravity", source);
        if (this.field.pseudoWeatherState === undefined) {
            this.field.pseudoWeatherState = { source: source };
      }
    
  },
    flags: {},
    name: "moonshot",
    rating: 4.5,
    num: 1005
    }
  