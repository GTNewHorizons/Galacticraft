package micdoodle8.mods.galacticraft.planets.asteroids.nei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks;
import micdoodle8.mods.galacticraft.planets.mars.nei.NEIGalacticraftMarsConfig;

public class NEIGalacticraftAsteroidsConfig implements IConfigureNEI {

    private static final HashMap<ArrayList<PositionedStack>, PositionedStack> rocketBenchRecipes = new HashMap<>();
    private static final HashMap<ArrayList<PositionedStack>, PositionedStack> astroMinerRecipes = new HashMap<>();

    @Override
    public void loadConfig() {
        // Handled by GalaxySpace
        API.registerRecipeHandler(new AstroMinerRecipeHandler());
        API.registerUsageHandler(new AstroMinerRecipeHandler());
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
