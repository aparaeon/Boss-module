package gg.mmorealms.module.boss.velocity.config;
import com.raduvoinea.utils.generic.Time;
import gg.mmorealms.module.core.common.utils.AliasTable;
import gg.mmorealms.module.boss.common.BossTier;

import java.util.EnumMap;
import java.util.Map;
public class BossVelocityConfig {
	public Time spawnInterval = Time.minutes(60);

	public Map<BossTier, Integer> tierWeights = new EnumMap<>(BossTier.class) {{
		put(BossTier.COMMON, 20);
		put(BossTier.UNCOMMON, 15);
		put(BossTier.RARE, 15);
		put(BossTier.ULTRA_RARE, 8);
		put(BossTier.LEGENDARY, 15);
		put(BossTier.MEGA, 10);
		put(BossTier.MYTHICAL, 10);
	}};

	public transient AliasTable<BossTier> tierSampler;
}
