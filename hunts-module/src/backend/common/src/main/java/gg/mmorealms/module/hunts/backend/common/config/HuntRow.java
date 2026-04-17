package gg.mmorealms.module.hunts.backend.common.config;

import gg.mmorealms.module.hunts.backend.common.gui.AcceptButton;
import gg.mmorealms.module.hunts.backend.common.gui.DenyButton;
import gg.mmorealms.module.hunts.backend.common.gui.HuntBackground;
import gg.mmorealms.module.hunts.backend.common.gui.HuntTarget;

import java.util.List;

public record HuntRow(List<String> commands,
                      HuntTarget target,
                      DenyButton denyButton,
                      AcceptButton acceptButton,
                      HuntBackground background)
{ }
