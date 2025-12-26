package kr.rtustudio.varmor.configuration;

import kr.rtustudio.configurate.objectmapping.meta.Comment;
import kr.rtustudio.framework.bukkit.api.configuration.ConfigurationPart;
import lombok.Getter;

@Getter
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class VanishConfig extends ConfigurationPart {

    @Comment("""
            Make your own armor vanish
            본인의 갑옷을 숨깁니다""")
    private boolean hideSelf = true;

    @Comment("""
            Make other players' armor vanish
            다른 플레이어의 갑옷을 숨깁니다""")
    private boolean hideOther = false;

    @Comment("""
            Make your armor vanish to other players
            다른 플레이어에게 본인의 갑옷을 숨깁니다""")
    private boolean hideFromOther = true;

    @Comment("""
            Cosmetic are not affected by the vanish command
            치장은 갑옷 숨기기 명령어의 영향을 받지 않습니다""")
    private boolean bypassCosmetics = true;
}
