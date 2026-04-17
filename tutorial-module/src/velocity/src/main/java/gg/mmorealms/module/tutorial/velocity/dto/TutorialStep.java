package gg.mmorealms.module.tutorial.velocity.dto;

import gg.mmorealms.module.tutorial.velocity.TutorialVelocityModule;

public enum TutorialStep {

	CLAIM_STARTER,
	RTP,
	SEND_POKEMON,
	WARP_HEAL,
	REALM_TP;

	public TutorialStepDescription getDescription() {
		return TutorialVelocityModule.instance().getConfig().tutorialSteps.get(this);
	}

}
