package gg.mmorealms.module.legendaries.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import gg.mmorealms.module.legendaries.common.dto.enums.LegendaryEventType;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public class LegendaryProxyEvent extends NetworkEvent {
	@Nullable
	private final LegendaryInfo info;
	private final LegendaryEventType type;

	private LegendaryProxyEvent(@Nullable LegendaryInfo info, LegendaryEventType type) {
		super();
		this.info = info;
		this.type = type;
	}

	public static LegendaryProxyEvent spawned(LegendaryInfo info) {
		return new LegendaryProxyEvent(info, LegendaryEventType.SPAWNED);
	}

	public static LegendaryProxyEvent despawned(LegendaryInfo info) {
		return new LegendaryProxyEvent(info, LegendaryEventType.DESPAWNED);
	}

	public static LegendaryProxyEvent captured(LegendaryInfo info) {
		return new LegendaryProxyEvent(info, LegendaryEventType.CAPTURED);
	}

	public static LegendaryProxyEvent fainted(LegendaryInfo info) {
		return new LegendaryProxyEvent(info, LegendaryEventType.FAINTED);
	}

	public static LegendaryProxyEvent failed() {
		return new LegendaryProxyEvent(null, LegendaryEventType.FAILED);
	}
}
