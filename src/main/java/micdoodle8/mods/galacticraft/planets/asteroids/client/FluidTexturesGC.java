package micdoodle8.mods.galacticraft.planets.asteroids.client;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;

@SideOnly(Side.CLIENT)
public class FluidTexturesGC {

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new FluidTexturesGC());
    }

    @SubscribeEvent
    public void onStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 0) {
            AsteroidsModule.fluidAtmosphericGases
                    .setIcons(event.map.registerIcon(AsteroidsModule.ASSET_PREFIX + ":fluids/AtmosphericGases"));
            AsteroidsModule.fluidLiquidMethane
                    .setIcons(event.map.registerIcon(AsteroidsModule.ASSET_PREFIX + ":fluids/LiquidMethane"));
            AsteroidsModule.fluidLiquidArgon
                    .setIcons(event.map.registerIcon(AsteroidsModule.ASSET_PREFIX + ":fluids/LiquidArgon"));
            if (!CompatibilityManager.isGT5uNHLoaded()) {
                AsteroidsModule.fluidMethaneGas
                        .setIcons(event.map.registerIcon(AsteroidsModule.ASSET_PREFIX + ":fluids/MethaneGas"));
                AsteroidsModule.fluidLiquidOxygen
                        .setIcons(event.map.registerIcon(AsteroidsModule.ASSET_PREFIX + ":fluids/LiquidOxygen"));
                AsteroidsModule.fluidOxygenGas
                        .setIcons(event.map.registerIcon(AsteroidsModule.ASSET_PREFIX + ":fluids/OxygenGas"));
                AsteroidsModule.fluidLiquidNitrogen
                        .setIcons(event.map.registerIcon(AsteroidsModule.ASSET_PREFIX + ":fluids/LiquidNitrogen"));
                AsteroidsModule.fluidNitrogenGas
                        .setIcons(event.map.registerIcon(AsteroidsModule.ASSET_PREFIX + ":fluids/NitrogenGas"));
                FluidRegistry.getFluid("hydrogen")
                        .setIcons(event.map.registerIcon(AsteroidsModule.ASSET_PREFIX + ":fluids/HydrogenGas"));
                FluidRegistry.getFluid("helium")
                        .setIcons(event.map.registerIcon(AsteroidsModule.ASSET_PREFIX + ":fluids/HeliumGas"));
                FluidRegistry.getFluid("argon")
                        .setIcons(event.map.registerIcon(AsteroidsModule.ASSET_PREFIX + ":fluids/ArgonGas"));
                FluidRegistry.getFluid("carbondioxide")
                        .setIcons(event.map.registerIcon(AsteroidsModule.ASSET_PREFIX + ":fluids/CarbonDioxideGas"));
            }
        }
    }
}
