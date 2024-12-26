package com.github.ipecter.rtustudio.varmor.config;

import com.github.ipecter.rtustudio.varmor.VanishArmor;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.config.RSConfiguration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VanishConfig extends RSConfiguration<VanishArmor> {

    private boolean hideSelf = true;
    private boolean hideOther = false;
    private boolean hideFromOther = true;

    public VanishConfig(VanishArmor plugin) {
        super(plugin, "Vanish.yml", 1);
        setup(this);
    }

    private void init() {
        hideSelf = getBoolean("hideSelf", hideSelf, """
                Make your own armor vanish
                본인의 갑옷을 보이지 않게 만듭니다""");
        hideOther = getBoolean("hideOther", hideOther, """
                Make other players' armor vanish
                다른 플레이어의 갑옷을 보이지 않게 만듭니다""");
        hideFromOther = getBoolean("hideFromOther", hideFromOther, """
                Make your armor vanish to other players
                다른 플레이어에게 본인의 갑옷을 보이지 않게 만듭니다""");
    }
}