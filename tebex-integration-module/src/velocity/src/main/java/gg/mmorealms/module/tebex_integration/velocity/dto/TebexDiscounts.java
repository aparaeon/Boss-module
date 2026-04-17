package gg.mmorealms.module.tebex_integration.velocity.dto;

import com.raduvoinea.utils.generic.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TebexDiscounts {

	private double threshold;
	private double discount;
	private Time duration;

}
