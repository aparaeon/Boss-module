package gg.mmorealms.module.tebex_integration.backend.common.dto;

import com.raduvoinea.utils.message_builder.MessageBuilderList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TebexMilestone {

	private String id;
	private String name;
	private double threshold;
	private String description;
	private MessageBuilderList commands;

}
