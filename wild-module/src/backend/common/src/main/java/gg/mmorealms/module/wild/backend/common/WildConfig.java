package gg.mmorealms.module.wild.backend.common;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.Pair2;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.LevelType;

import java.util.List;
import java.util.Map;

public class WildConfig {
	public static final MessageBuilder RTP_COOLDOWN_TEMPLATE = new MessageBuilder("rtp_cooldown_{dimension}");

	public Lang lang = new Lang();

	public Pair2<Range, Range> spawnRange = new Pair2<>(
			new Range(-9000, 9000),
			new Range(-9000, 9000)
	);

	public List<String> spawnDenyList = List.of(
			"minecraft:water",
			"minecraft:lava",
			"minecraft:stone",
			"minecraft:magma_block",
			"minecraft:fire"
	);


	public Map<LevelType, Time> rtpCooldownMap = Map.of(
			LevelType.OVERWORLD, Time.seconds(5),
			LevelType.NETHER, Time.minutes(2),
			LevelType.END, Time.minutes(2)
	);

	public static class Lang {
		public MessageBuilder activeRtpCooldown = new MessageBuilder("<yellow>You're still on cooldown! Please wait <white><bold>{cooldown}</bold></white>before trying to teleport to {dimension} again.");
	}
}
