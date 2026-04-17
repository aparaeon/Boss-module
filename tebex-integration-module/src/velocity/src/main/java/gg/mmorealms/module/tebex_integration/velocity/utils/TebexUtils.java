package gg.mmorealms.module.tebex_integration.velocity.utils;

import com.google.gson.JsonObject;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.utils.DateUtils;
import gg.mmorealms.loader.common.utils.StringUtils;
import gg.mmorealms.module.tebex_integration.velocity.TebexIntegrationVelocityModule;
import gg.mmorealms.module.tebex_integration.velocity.dto.CouponData;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.HashMap;

public class TebexUtils {

	@SneakyThrows(IOException.class)
	public static @Nullable CouponData createCoupon(CouponData coupon) {
		String code = StringUtils.generateRandomCode(
				TebexIntegrationVelocityModule.instance().getConfig().discountCodeLength,
				TebexIntegrationVelocityModule.instance().getConfig().discountCodeDictionary
		);

		coupon.setCode(code);

		HashMap<String, Object> couponData = new HashMap<>();
		couponData.put("code", code);
		couponData.put("effective_on", "cart");
		couponData.put("discount_type", "percentage");
		couponData.put("discount_amount", 0);
		couponData.put("discount_percentage", coupon.getDiscountPercent());
		couponData.put("redeem_unlimited", false);
		couponData.put("expire_never", false);
		couponData.put("expire_limit", 1);
		couponData.put("expire_date", DateUtils.formatTimestamp(System.currentTimeMillis() + coupon.getExpireIn().toMilliseconds(), "MMM dd, yyyy HH:mm"));
		couponData.put("username", coupon.getUsername());

		String json = TebexIntegrationVelocityModule.instance().toJson(couponData);

		try (HttpClient client = HttpClient.newHttpClient()) {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://plugin.tebex.io/coupons"))
					.header("Content-Type", "application/json")
					.header("X-Tebex-Secret", TebexIntegrationVelocityModule.instance().getConfig().tebexToken)
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() / 100 != 2) {
				Logger.error(new MessageBuilder("[Error {code}] Failed to create coupon: {message}")
						.parse("code", java.lang.String.valueOf(response.statusCode()))
						.parse("message", response.body())
						.parse());
				return null;
			}

			String responseBody = response.body();
			JsonObject jsonResponse = TebexIntegrationVelocityModule.instance().fromJson(responseBody, JsonObject.class);

			int id = jsonResponse.get("data").getAsJsonObject().get("id").getAsInt();
			coupon.setId(id);

			return coupon;
		} catch (InterruptedException exception) {
			Logger.error(exception);
			return null;
		}
	}

	@SneakyThrows(IOException.class)
	public static CouponData getCouponData(int id) {
		try (HttpClient client = HttpClient.newHttpClient()) {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://plugin.tebex.io/coupons/" + id))
					.header("Content-Type", "application/json")
					.header("X-Tebex-Secret", TebexIntegrationVelocityModule.instance().getConfig().tebexToken)
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() / 100 != 2) {
				Logger.error(new MessageBuilder("[Error {code}] Failed to create coupon: {message}")
						.parse("code", String.valueOf(response.statusCode()))
						.parse("message", response.body())
						.parse());
				return null;
			}

			String responseBody = response.body();
			JsonObject jsonResponse = TebexIntegrationVelocityModule.instance().fromJson(responseBody, JsonObject.class);
			JsonObject data = jsonResponse.getAsJsonObject("data");

			int limit = jsonResponse
					.getAsJsonObject("data")
					.getAsJsonObject("expire")
					.get("limit").getAsInt();

			String expireDate = jsonResponse
					.getAsJsonObject("data")
					.getAsJsonObject("expire")
					.get("date").getAsString();

			long expireTimestamp = OffsetDateTime.parse(expireDate).toEpochSecond();
			boolean active = (expireTimestamp > System.currentTimeMillis() / 1000) && limit >= 1;

			return new CouponData(
					data.get("id").getAsInt(),
					data.get("code").getAsString(),
					active,
					data.get("username").getAsString(),
					data.getAsJsonObject("discount").get("percentage").getAsDouble(),
					Time.seconds(expireTimestamp - System.currentTimeMillis() / 1000)
			);
		} catch (InterruptedException exception) {
			Logger.error(exception);
			return null;
		}
	}

}
