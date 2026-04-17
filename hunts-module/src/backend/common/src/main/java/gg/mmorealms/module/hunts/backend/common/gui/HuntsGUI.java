package gg.mmorealms.module.hunts.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.AutoRefreshGUI;
import gg.mmorealms.module.hunts.backend.common.HuntsBackendModule;
import gg.mmorealms.module.hunts.backend.common.config.HuntRow;
import gg.mmorealms.module.hunts.backend.common.config.HuntsConfig;
import gg.mmorealms.module.hunts.backend.common.dto.HuntPool;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;

public class HuntsGUI extends AutoRefreshGUI {
    private final HuntsConfig config;
    private final User user;
    private Hunts hunts; // To avoid fetching hunts for each button separately

    public HuntsGUI(User user) {
        super(user,
                new Settings()
                        .chestSize(HuntsBackendModule
                                .instance()
                                .getConfig()
                                .gui.rows.size())
        );

        this.config = HuntsBackendModule.instance().getConfig();
        this.user = user;

        refreshHunts();
    }

    @Override
    public String getTitleString() {
        return config.gui.title;
    }

    @Override
    public void setup() {
        config.gui.rows.forEach(this::setupRow);
    }

    private void setupRow(HuntType type, HuntRow huntRow) {
        setButton(huntRow.background().create(hunts, user, type));
        setButton(huntRow.target().create(hunts, user, type));
        setButton(huntRow.denyButton().create(hunts, user, type, this::refresh));
        setButton(huntRow.acceptButton().create(hunts, user, type, this::refresh));
    }

    @Override
    public boolean shouldAutoRefresh() {
        return hasRefreshable();
    }

    private boolean hasRefreshable() {
        if (hunts.hasHuntOnCooldown()) {
            return true;
        }

        if (!hunts.hasActiveHunt()) {
            return false;
        }

        HuntType type = hunts.getActiveHuntType();
        HuntPool pool = config.huntPools.get(type);

        return pool.huntDuration() != null;
    }

    @Override
    protected void refresh() {
        refreshHunts();

        super.refresh();
    }

    private void refreshHunts() {
        hunts = Hunts.get(user);
        hunts.refreshHunts();
    }
}
