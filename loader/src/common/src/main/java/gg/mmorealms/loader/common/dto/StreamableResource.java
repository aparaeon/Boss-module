package gg.mmorealms.loader.common.dto;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnLambda;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.CommonLoader;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class StreamableResource {

	private static final List<StreamableResource> streamers = new ArrayList<>();
	private static Jedis jedis;

	private final String id;
	private final CancelableTimeTask task;

	private StreamableResource(String id, Time refreshTime, Time timeToLive, ReturnLambda<String> processor) {
		this.id = id;

		this.task = ScheduleUtils.runTaskTimer(() -> {
			try {
				String value = processor.run();
				if (value == null) {
					unregister();
					return;
				}

				CommonLoader.instance().getRedisManager().executeOnJedisAndForget(jedis ->
					jedis.set(id, value, SetParams.setParams()
						.ex(timeToLive.toSeconds())
					)
				);

			} catch (Exception exception) {
				Logger.error(exception);
				unregister();
			}
		}, refreshTime);
	}

	public static void create(String id, Time refreshTime, Time timeToLive, ReturnLambda<String> processor) {
		new StreamableResource(id, refreshTime, timeToLive, processor).register();
	}

	public static StreamableResource get(String id) {
		for (StreamableResource streamer : streamers) {
			if (streamer.id.equals(id)) {
				return streamer;
			}
		}

		return null;
	}

	public static String streamData(String id) {
		return CommonLoader.instance().getRedisManager().executeOnJedisAndGet(jedis -> jedis.get(id));
	}

	public static void unregister(String id) {
		StreamableResource streamer = get(id);
		if (streamer != null) {
			streamer.unregister();
		}
	}

	private void register() {
		unregister(this.id);
		streamers.add(this);
	}

	public void unregister() {
		task.cancel();

		CommonLoader.instance().getRedisManager().executeOnJedisAndForget(jedis ->
			jedis.del(id)
		);

		streamers.remove(this);
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || getClass() != object.getClass()) {
			return false;
		}

		StreamableResource that = (StreamableResource) object;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
