package gg.mmorealms.module.essentials.velocity.dto;

import gg.mmorealms.loader.common.dto.ServerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class WhitelistState {

	private boolean globalEnabled = true;
	private List<String> whitelistedServers = new ArrayList<>();
	private List<ServerType> whitelistedServerTypes = new ArrayList<>();
	private Set<String> bypassPlayers = new HashSet<>();

}
