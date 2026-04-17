package gg.mmorealms.module.realms.backend.common.dto;

import gg.mmorealms.module.realms.backend.common.dto.member.TrustLevel;
import lombok.Getter;

@Getter
public enum RealmPermission {
	EVERY_MEMBER(TrustLevel.VISITOR),

	COMMAND_CHANGE_VISITOR_MESSAGE(TrustLevel.MANAGER),
	COMMAND_BAN(TrustLevel.MANAGER),
	COMMAND_UNBAN(TrustLevel.MANAGER),
	COMMAND_TRUST(TrustLevel.MANAGER),
	COMMAND_ADD(TrustLevel.MANAGER),
	COMMAND_REMOVE(TrustLevel.MANAGER),
	COMMAND_MEMBERS(TrustLevel.VISITOR),
	COMMAND_DELETE(TrustLevel.OWNER),
	COMMAND_SET_SPAWN(TrustLevel.OWNER),
	COMMAND_LEGENDARY_CAPTURE_SHARED(TrustLevel.OWNER),

	LEGENDARY_CAPTURE_SHARED(TrustLevel.MEMBER),
	BLOCK_ATTACK(TrustLevel.OFFICER),
	BLOCK_INTERACT(TrustLevel.OFFICER),
	BLOCK_BREAK(TrustLevel.OFFICER),
	BLOCK_PLACE(TrustLevel.OFFICER),
	BOAT_PLACE(TrustLevel.OFFICER),
	DISPENSABLE_BLOCK_PLACE(TrustLevel.OFFICER),

	ENTITY_INTERACT(TrustLevel.MEMBER),
	ENTITY_ATTACK(TrustLevel.MEMBER),
	ENTITY_DAMAGE(TrustLevel.MEMBER),

	DECORATED_POT_INTERACT_EVENT(TrustLevel.OFFICER),
	ARMOR_STAND_INTERACT_EVENT(TrustLevel.OFFICER),
	SIGN_INTERACT_EVENT(TrustLevel.OFFICER),
	ITEM_USE_EVENT(TrustLevel.MEMBER),
	ITEM_DROP_EVENT(TrustLevel.MEMBER),

	CROP_STOMP(TrustLevel.OFFICER),

	SET_HOME(TrustLevel.OFFICER);

	private final TrustLevel level;

	RealmPermission(TrustLevel level) {
		this.level = level;
	}
}
