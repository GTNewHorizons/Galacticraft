package micdoodle8.mods.galacticraft.core.mixinplugin;

import static micdoodle8.mods.galacticraft.core.mixinplugin.TargetedMod.*;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import java.util.Arrays;
import java.util.List;

public enum Mixin {

    //
    // IMPORTANT: Do not make any references to any mod from this file. This file is loaded quite early on and if
    // you refer to other mods you load them as well. The consequence is: You can't inject any previously loaded
    // classes!
    // Exception: Tags.java, as long as it is used for Strings only!
    //

    ForgeHooksClientMixin("forge.ForgeHooksClientMixin", FORGE),

    ChunkProviderServerMixin("minecraft.ChunkProviderServerMixin", VANILLA),
    EffectRendererMixin("minecraft.EffectRendererMixin", Side.CLIENT, VANILLA),
    EntityArrowMixin("minecraft.EntityArrowMixin", VANILLA),
    EntityGolemMixin("minecraft.EntityGolemMixin", VANILLA),
    EntityItemMixin("minecraft.EntityItemMixin", VANILLA),
    EntityLivingBaseMixin("minecraft.EntityLivingBaseMixin", VANILLA),
    EntityMixin("minecraft.EntityMixin", Side.CLIENT, VANILLA),
    EntityRendererMixin("minecraft.EntityRendererMixin", Side.CLIENT, VANILLA),
    EntityRendererWithoutOptifineMixin(
            "minecraft.EntityRendererWithoutOptifineMixin", Side.CLIENT, true, false, VANILLA),
    GuiSleepMPMxin("minecraft.GuiSleepMPMxin", Side.CLIENT, VANILLA),
    ItemRendererMixin("minecraft.ItemRendererMixin", Side.CLIENT, VANILLA),
    NetHandlerPlayClientMixin("minecraft.NetHandlerPlayClientMixin", Side.CLIENT, VANILLA),
    PlayerControllerMPMixin("minecraft.PlayerControllerMPMixin", Side.CLIENT, false, true, VANILLA),
    RendererLivingEntityMixin("minecraft.RendererLivingEntityMixin", Side.CLIENT, VANILLA),
    WorldMixin("minecraft.WorldMixin", VANILLA);

    public final String mixinClass;
    public final List<TargetedMod> targetedMods;
    private final Side side;
    private final boolean injectAlongPlayerAPI;
    private final boolean isInjectAlongOptifine;

    Mixin(
            String mixinClass,
            Side side,
            boolean injectAlongPlayerAPI,
            boolean isInjectAlongOptifine,
            TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
        this.side = side;
        this.injectAlongPlayerAPI = injectAlongPlayerAPI;
        this.isInjectAlongOptifine = isInjectAlongOptifine;
    }

    Mixin(String mixinClass, Side side, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
        this.side = side;
        this.injectAlongPlayerAPI = true;
        this.isInjectAlongOptifine = true;
    }

    Mixin(String mixinClass, boolean injectAlongPlayerAPI, boolean isInjectAlongOptifine, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
        this.side = Side.BOTH;
        this.injectAlongPlayerAPI = injectAlongPlayerAPI;
        this.isInjectAlongOptifine = isInjectAlongOptifine;
    }

    Mixin(String mixinClass, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
        this.side = Side.BOTH;
        this.injectAlongPlayerAPI = true;
        this.isInjectAlongOptifine = true;
    }

    public boolean shouldLoad(List<TargetedMod> loadedMods) {
        return (side == Side.BOTH
                        || side == Side.SERVER && FMLLaunchHandler.side().isServer()
                        || side == Side.CLIENT && FMLLaunchHandler.side().isClient())
                && loadedMods.containsAll(targetedMods)
                && (loadedMods.contains(PLAYER_API) == false || injectAlongPlayerAPI)
                && (loadedMods.contains(OPTIFINE) == false || isInjectAlongOptifine);
    }
}

enum Side {
    BOTH,
    CLIENT,
    SERVER;
}
