package gg.mmorealms.module.economy.backend.common.dto;

import gg.mmorealms.loader.common.dto.remote.UUIDRemoteObject;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RemoteBalances extends UUIDRemoteObject<IBalances> implements IBalances {
	public RemoteBalances(@NotNull UUID uuid, @NotNull String server) {
		super(IBalances.class, uuid, server);
	}

	@Override
	public void save() {
		sendRequest();
	}

	@Override
	public Double get(CurrencyType currency) {
		return sendRequest(currency);
	}

	@Override
	public void set(CurrencyType currency, Double amount, String source) {
		sendRequest(currency, amount, source);
	}

	public String toPrettyString(Boolean showHeader, Boolean showUnknownCurrencies) {
		return sendRequest(showHeader, showUnknownCurrencies);
	}
}
