package micdoodle8.mods.galacticraft.core.mixins;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {

    // spotless:off
    INJECT_ORIENT_CAMERA_EVENT(new MixinBuilder()
            .addClientMixins("forge.ForgeHooksClientMixin")),
    CHECK_OTHER_MOD_PREVENTS_GENERATION(
            new MixinBuilder("Only generate the world if no mod prevents it")
                    .addCommonMixins("minecraft.ChunkProviderServerMixin")
                    .addExcludedMod(TargetedMod.DRAGONAPI)),
    CHECK_OTHER_MOD_PREVENTS_GENERATION_DRAGONAPI(
            new MixinBuilder("Only generate the world if no mod prevents it")
                    .addCommonMixins("minecraft.ChunkProviderServerMixin_DragonApi")
                    .addRequiredMod(TargetedMod.DRAGONAPI)),
    RENDER_FOOTPRINTS(new MixinBuilder()
            .addClientMixins("minecraft.EffectRendererMixin")),
    MODIFY_ENTITY_GRAVITY(new MixinBuilder()
            .addCommonMixins(
                    "minecraft.EntityArrowMixin",
                    "minecraft.EntityItemMixin",
                    "minecraft.EntityLivingBaseMixin")),
    ALLOW_GOLEM_BREATHING(new MixinBuilder("Golems don't need oxygen to breath")
            .addCommonMixins("minecraft.EntityGolemMixin")),
    PREVENT_FIRE_RENDERING_WITHOUT_O2(new MixinBuilder()
            .addClientMixins("minecraft.EntityMixin")),
    ADAPT_ENTITY_RENDERER(new MixinBuilder("Adapt lightmap, fogcolor and cameraorientation")
            .addClientMixins("minecraft.EntityRendererMixin")),
    ADAPT_ENTITY_RENDERER_NO_OF(
            new MixinBuilder("Adapt lightmap, fogcolor and cameraorientation (Optifine incompatible part)")
                    .addClientMixins("minecraft.EntityRendererWithoutOptifineMixin")
                    .addExcludedMod(TargetedMod.OPTIFINE)),
    INJECT_SLEEP_CANCELLED_EVENT(new MixinBuilder()
            .addClientMixins("minecraft.GuiSleepMPMxin")),
    RENDER_LIQUID_OVERLAYS(new MixinBuilder()
            .addClientMixins("minecraft.ItemRendererMixin")),
    REPLACE_ENTITY_CLIENT_PLAYER_MP(new MixinBuilder("Replace EntityClientPlayerMP with GCEntityClientPlayerMP")
            .addClientMixins("minecraft.PlayerControllerMPMixin")
            .addExcludedMod(TargetedMod.PLAYERAPI)),
    RENDER_THERMAL_PADDING(new MixinBuilder("Render the thermal armour and the Galacticraft equipment on the player")
            .addClientMixins("minecraft.RendererLivingEntityMixin")
            .addExcludedMod(TargetedMod.RENDERPLAYERAPI)),
    ADAPT_PLAYER_RENDERING(
            new MixinBuilder("Apply the Galacticraft player pose and sleeping/rocket rotations without replacing the player renderer")
                    .addClientMixins("minecraft.ModelBipedMixin", "minecraft.RenderPlayerMixin")
                    .addExcludedMod(TargetedMod.RENDERPLAYERAPI)),
    MODIFY_RAIN_STRENGTH(new MixinBuilder()
            .addCommonMixins("minecraft.WorldMixin")),
    DONOR_CAPES(new MixinBuilder()
            .addClientMixins("minecraft.AbstractClientPlayerMixin")),
    CUSTOM_MUSIC(new MixinBuilder()
            .addClientMixins("minecraft.MusicTickerMixin"));
    // spotless:on

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder.setPhase(Phase.EARLY);
    }

    @NotNull
    @Override
    public MixinBuilder getBuilder() {
        return this.builder;
    }
}
