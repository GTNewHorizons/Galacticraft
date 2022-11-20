package micdoodle8.mods.galacticraft.core.mixins;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public enum Mixins {
    INJECT_ORIENT_CAMERA_EVENT(new Builder("Inject OrientCameraEvent")
            .setPhase(Phase.EARLY)
            .addMixinClasses("forge.ForgeHooksClientMixin")
            .addTargetedMod(TargetedMod.FORGE)),
    CHECK_OTHER_MOD_PREVENTS_GENERATION(new Builder("Only generate the world if no mod prevents it")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.ChunkProviderServerMixin")
            .addTargetedMod(TargetedMod.VANILLA)),
    RENDER_FOOTPRINTS(new Builder("Render footprints")
            .setPhase(Phase.EARLY)
            .setSide(Side.CLIENT)
            .addMixinClasses("minecraft.EffectRendererMixin")
            .addTargetedMod(TargetedMod.VANILLA)),
    MODIFY_ENTITY_GRAVITY(new Builder("Entities respect changing gravity")
            .setPhase(Phase.EARLY)
            .addMixinClasses(
                    "minecraft.EntityArrowMixin", "minecraft.EntityItemMixin", "minecraft.EntityLivingBaseMixin")
            .addTargetedMod(TargetedMod.VANILLA)),
    ALLOW_GOLEM_BREATHING(new Builder("Golems don't need oxygen to breath")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.EntityGolemMixin")
            .addTargetedMod(TargetedMod.VANILLA)),
    PREVENT_FIRE_RENDERING_WITHOUT_O2(new Builder("No 'onFire' animation is rendered if there is no oxygen")
            .setPhase(Phase.EARLY)
            .setSide(Side.CLIENT)
            .addMixinClasses("minecraft.EntityMixin")
            .addTargetedMod(TargetedMod.VANILLA)),
    ADAPT_ENTITY_RENDERER(new Builder("Adapt lightmap, fogcolor and cameraorientation")
            .setPhase(Phase.EARLY)
            .setSide(Side.CLIENT)
            .addMixinClasses("minecraft.EntityRendererMixin")
            .addTargetedMod(TargetedMod.VANILLA)),
    ADAPT_ENTITY_RENDERER_NO_OF(
            new Builder("Adapt lightmap, fogcolor and cameraorientation (Optifine incompatible part)")
                    .setPhase(Phase.EARLY)
                    .setSide(Side.CLIENT)
                    .addMixinClasses("minecraft.EntityRendererWithoutOptifineMixin")
                    .addTargetedMod(TargetedMod.VANILLA)
                    .addExcludedMod(TargetedMod.OPTIFINE)),
    INJECT_SLEEP_CANCELLED_EVENT(new Builder("Inject SleepCancelledEvent")
            .setPhase(Phase.EARLY)
            .setSide(Side.CLIENT)
            .addMixinClasses("minecraft.GuiSleepMPMxin")
            .addTargetedMod(TargetedMod.VANILLA)),
    RENDER_LIQUID_OVERLAYS(new Builder("Render liquid overlays")
            .setPhase(Phase.EARLY)
            .setSide(Side.CLIENT)
            .addMixinClasses("minecraft.ItemRendererMixin")
            .addTargetedMod(TargetedMod.VANILLA)),
    REPLACE_ENTITY_CLIENT_PLAYER_MP(new Builder("Replace EntityClientPlayerMP with GCEntityClientPlayerMP")
            .setPhase(Phase.EARLY)
            .setSide(Side.CLIENT)
            .addMixinClasses("minecraft.PlayerControllerMPMixin")
            .addTargetedMod(TargetedMod.VANILLA)
            .addExcludedMod(TargetedMod.PLAYERAPI)),
    RENDER_THERMAL_PADDING(new Builder("Render thermal padding")
            .setPhase(Phase.EARLY)
            .setSide(Side.CLIENT)
            .addMixinClasses("minecraft.RendererLivingEntityMixin")
            .addTargetedMod(TargetedMod.VANILLA)),
    MODIFY_RAIN_STRENGTH(new Builder("Modify rain strenght")
            .setPhase(Phase.EARLY)
            .addMixinClasses("minecraft.WorldMixin")
            .addTargetedMod(TargetedMod.VANILLA));

    public final String name;
    public final List<String> mixinClasses;
    private final Supplier<Boolean> applyIf;
    public final Phase phase;
    private final Side side;
    public final List<TargetedMod> targetedMods;
    public final List<TargetedMod> excludedMods;

    private static class Builder {
        private final String name;
        private final List<String> mixinClasses = new ArrayList<>();
        private Supplier<Boolean> applyIf = () -> true;
        private Side side = Side.BOTH;
        private Phase phase = Phase.LATE;
        private final List<TargetedMod> targetedMods = new ArrayList<>();
        private final List<TargetedMod> excludedMods = new ArrayList<>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder addMixinClasses(String... mixinClasses) {
            this.mixinClasses.addAll(Arrays.asList(mixinClasses));
            return this;
        }

        public Builder setPhase(Phase phase) {
            this.phase = phase;
            return this;
        }

        public Builder setSide(Side side) {
            this.side = side;
            return this;
        }

        public Builder setApplyIf(Supplier<Boolean> applyIf) {
            this.applyIf = applyIf;
            return this;
        }

        public Builder addTargetedMod(TargetedMod mod) {
            this.targetedMods.add(mod);
            return this;
        }

        public Builder addExcludedMod(TargetedMod mod) {
            this.excludedMods.add(mod);
            return this;
        }
    }

    Mixins(Builder builder) {
        this.name = builder.name;
        this.mixinClasses = builder.mixinClasses;
        this.applyIf = builder.applyIf;
        this.side = builder.side;
        this.targetedMods = builder.targetedMods;
        this.excludedMods = builder.excludedMods;
        this.phase = builder.phase;
        if (this.targetedMods.isEmpty()) {
            throw new RuntimeException("No targeted mods specified for " + this.name);
        }
        if (this.applyIf == null) {
            throw new RuntimeException("No ApplyIf function specified for " + this.name);
        }
    }

    private boolean shouldLoadSide() {
        return side == Side.BOTH
                || side == Side.SERVER && FMLLaunchHandler.side().isServer()
                || side == Side.CLIENT && FMLLaunchHandler.side().isClient();
    }

    private boolean allModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) {
            return false;
        }

        for (final TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) {
                continue;
            }

            // Check coremod first
            if (!loadedCoreMods.isEmpty()
                    && target.coreModClass != null
                    && !loadedCoreMods.contains(target.coreModClass)) {
                return false;
            } else if (!loadedMods.isEmpty() && target.modId != null && !loadedMods.contains(target.modId)) {
                return false;
            }
        }

        return true;
    }

    private boolean noModsLoaded(List<TargetedMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) {
            return true;
        }

        for (final TargetedMod target : targetedMods) {
            if (target == TargetedMod.VANILLA) {
                continue;
            }

            // Check coremod first
            if (!loadedCoreMods.isEmpty()
                    && target.coreModClass != null
                    && loadedCoreMods.contains(target.coreModClass)) {
                return false;
            } else if (!loadedMods.isEmpty() && target.modId != null && loadedMods.contains(target.modId)) {
                return false;
            }
        }

        return true;
    }

    public boolean shouldLoad(Set<String> loadedCoreMods, Set<String> loadedMods) {
        return shouldLoadSide()
                && applyIf.get()
                && allModsLoaded(targetedMods, loadedCoreMods, loadedMods)
                && noModsLoaded(excludedMods, loadedCoreMods, loadedMods);
    }

    enum Side {
        BOTH,
        CLIENT,
        SERVER
    }

    public enum Phase {
        EARLY,
        LATE,
    }
}
