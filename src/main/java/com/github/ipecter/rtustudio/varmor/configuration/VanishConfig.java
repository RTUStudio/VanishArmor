package com.github.ipecter.rtustudio.varmor.configuration;

import com.github.ipecter.rtustudio.varmor.VanishArmor;
import kr.rtuserver.framework.bukkit.api.configuration.RSConfiguration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VanishConfig extends RSConfiguration<VanishArmor> {

    private boolean hideSelf = true;
    private boolean hideOther = false;
    private boolean hideFromOther = true;
    private boolean bypassCosmetics = true;

    public VanishConfig(VanishArmor plugin) {
        super(plugin, "Vanish.yml", 1);
        setup(this);
    }

    private void init() {
        hideSelf = getBoolean("hideSelf", hideSelf, """
                Make your own armor vanish
                본인의 갑옷을 숨깁니다""");
        hideOther = getBoolean("hideOther", hideOther, """
                Make other players' armor vanish
                다른 플레이어의 갑옷을 숨깁니다""");
        hideFromOther = getBoolean("hideFromOther", hideFromOther, """
                Make your armor vanish to other players
                다른 플레이어에게 본인의 갑옷을 숨깁니다""");
        bypassCosmetics = getBoolean("bypassCosmetics", bypassCosmetics, """
                Cosmetic are not affected by the vanish command
                치장은 갑옷 숨기기 명령어의 영향을 받지 않습니다""");
    }
}