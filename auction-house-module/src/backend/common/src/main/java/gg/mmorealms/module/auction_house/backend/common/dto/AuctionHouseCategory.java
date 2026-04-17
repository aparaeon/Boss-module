package gg.mmorealms.module.auction_house.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class AuctionHouseCategory {

	private String name;
	private boolean selectable;
	private List<String> items;

}