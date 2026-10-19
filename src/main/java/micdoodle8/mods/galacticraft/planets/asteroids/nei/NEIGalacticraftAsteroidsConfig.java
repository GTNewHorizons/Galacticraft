package micdoodle8.mods.galacticraft.planets.asteroids.nei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.nei.NasaWorkbenchOverlayHandler;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks;
import micdoodle8.mods.galacticraft.planets.asteroids.client.gui.GuiSchematicAstroMiner;
import micdoodle8.mods.galacticraft.planets.asteroids.client.gui.GuiSchematicTier3Rocket;
import micdoodle8.mods.galacticraft.planets.mars.nei.NEIGalacticraftMarsConfig;

public class NEIGalacticraftAsteroidsConfig implements IConfigureNEI {

    private static final HashMap<ArrayList<PositionedStack>, PositionedStack> rocketBenchRecipes = new HashMap<>();
    private static final HashMap<ArrayList<PositionedStack>, PositionedStack> astroMinerRecipes = new HashMap<>();

    @Override
    public void loadConfig() {
        if (!GalacticraftCore.isGalaxySpaceLoaded) {
            RocketT3RecipeHandler rocketHandler = new RocketT3RecipeHandler();
            API.registerRecipeHandler(rocketHandler);
            API.registerUsageHandler(new RocketT3RecipeHandler());
            API.addRecipeCatalyst(new ItemStack(GCBlocks.nasaWorkbench), rocketHandler);
        }
        AstroMinerRecipeHandler astroMinerHandler = new AstroMinerRecipeHandler();
        API.registerRecipeHandler(astroMinerHandler);
        API.registerUsageHandler(new AstroMinerRecipeHandler());
        API.addRecipeCatalyst(new ItemStack(GCBlocks.nasaWorkbench), astroMinerHandler);
        API.registerGuiOverlay(GuiSchematicTier3Rocket.class, "galacticraft.rocketT3", 8, 12);
        API.registerGuiOverlayHandler(
                GuiSchematicTier3Rocket.class,
                new NasaWorkbenchOverlayHandler(8, 12),
                "galacticraft.rocketT3");
        API.registerGuiOverlay(GuiSchematicAstroMiner.class, "galacticraft.astroMiner", 4, 16);
        API.registerGuiOverlayHandler(
                GuiSchematicAstroMiner.class,
                new NasaWorkbenchOverlayHandler(4, 16),
                "galacticraft.astroMiner");
        API.registerHighlightIdentifier(AsteroidBlocks.blockBasic, NEIGalacticraftMarsConfig.planetsHighlightHandler);
    }

    @Override
    public String getName() {
        return "Galacticraft Asteroids NEI Plugin";
    }

    @Override
    public String getVersion() {
        return Constants.VERSION;
    }

    public void registerRocketBenchRecipe(ArrayList<PositionedStack> input, PositionedStack output) {
        NEIGalacticraftAsteroidsConfig.rocketBenchRecipes.put(input, output);
    }

    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getRocketBenchRecipes() {
        return NEIGalacticraftAsteroidsConfig.rocketBenchRecipes.entrySet();
    }

    public void registerAstroMinerRecipe(ArrayList<PositionedStack> input, PositionedStack output) {
        NEIGalacticraftAsteroidsConfig.astroMinerRecipes.put(input, output);
    }

    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getAstroMinerRecipes() {
        return NEIGalacticraftAsteroidsConfig.astroMinerRecipes.entrySet();
    }
}
