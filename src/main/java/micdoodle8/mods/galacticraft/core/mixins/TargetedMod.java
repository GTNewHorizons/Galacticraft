package micdoodle8.mods.galacticraft.core.mixins;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

public enum TargetedMod implements ITargetMod {

    DRAGONAPI("Reika.DragonAPI.Auxiliary.DragonAPIASMHandler", "DragonAPI"),
    OPTIFINE("optifine.OptiFineForgeTweaker", "Optifine"),
    PLAYERAPI("api.player.forge.PlayerAPIPlugin", "PlayerAPI");

    private final TargetModBuilder builder;

    TargetedMod(String coreModClass, String modId) {
        this.builder = new TargetModBuilder().setCoreModClass(coreModClass).setModId(modId);
    }

    @Nonnull
    @Override
    public TargetModBuilder getBuilder() {
        return builder;
    }
}
