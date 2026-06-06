package gg.mmorealms.module.boss.velocity.config;

import com.raduvoinea.utils.generic.Time;
import gg.mmorealms.module.core.common.utils.AliasTable;
import gg.mmorealms.module.boss.common.BossTier;

import java.util.EnumMap;
import java.util.Map;

public class BossVelocityConfig {

	public Time spawnInterval = Time.minutes(10);
	public double baseSpawnChance = 30.0;
	public double playerSpawnChanceBias = 2.0;

	public Map<BossTier, Integer> tierWeights = new EnumMap<>(BossTier.class) {{
		put(BossTier.COMMON, 40);
		put(BossTier.UNCOMMON, 25);
		put(BossTier.RARE, 15);
		put(BossTier.ULTRA_RARE, 8);
		put(BossTier.LEGENDARY, 4);
		put(BossTier.MEGA, 2);
		put(BossTier.MYTHICAL, 2);
	}};

	public transient AliasTable<BossTier> tierSampler;
}
