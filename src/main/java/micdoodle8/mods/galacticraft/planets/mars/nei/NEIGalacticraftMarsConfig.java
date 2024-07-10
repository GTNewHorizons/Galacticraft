package micdoodle8.mods.galacticraft.planets.mars.nei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;

public class NEIGalacticraftMarsConfig implements IConfigureNEI {

    private static final HashMap<ArrayList<PositionedStack>, PositionedStack> rocketBenchRecipes = new HashMap<>();
    private static final HashMap<ArrayList<PositionedStack>, PositionedStack> cargoBenchRecipes = new HashMap<>();
    private static final HashMap<PositionedStack, PositionedStack> liquefierRecipes = new HashMap<>();
    private static final HashMap<PositionedStack, PositionedStack> synthesizerRecipes = new HashMap<>();
    public static GCMarsNEIHighlightHandler planetsHighlightHandler = new GCMarsNEIHighlightHandler();

    @Override
    public void loadConfig() {
        this.registerRecipes();
        API.registerRecipeHandler(new CargoRocketRecipeHandler());
        API.registerUsageHandler(new CargoRocketRecipeHandler());
        API.registerHighlightIdentifier(MarsBlocks.marsBlock, planetsHighlightHandler);
    }

    @Override
    public String getName() {
        return "Galacticraft Mars NEI Plugin";
    }

    @Override
    public String getVersion() {
        return Constants.VERSION;
    }

    public void registerRocketBenchRecipe(ArrayList<PositionedStack> input, PositionedStack output) {
        NEIGalacticraftMarsConfig.rocketBenchRecipes.put(input, output);
    }

    public void registerCargoBenchRecipe(ArrayList<PositionedStack> input, PositionedStack output) {
        NEIGalacticraftMarsConfig.cargoBenchRecipes.put(input, output);
    }

    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getRocketBenchRecipes() {
        return NEIGalacticraftMarsConfig.rocketBenchRecipes.entrySet();
    }

    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getCargoBenchRecipes() {
        return NEIGalacticraftMarsConfig.cargoBenchRecipes.entrySet();
    }

    private void registerLiquefierRecipe(PositionedStack inputStack, PositionedStack outputStack) {
        NEIGalacticraftMarsConfig.liquefierRecipes.put(inputStack, outputStack);
    }

    public static Set<Entry<PositionedStack, PositionedStack>> getLiquefierRecipes() {
        return NEIGalacticraftMarsConfig.liquefierRecipes.entrySet();
    }

    private void registerSynthesizerRecipe(PositionedStack inputStack, PositionedStack outputStack) {
        NEIGalacticraftMarsConfig.synthesizerRecipes.put(inputStack, outputStack);
    }

    public static Set<Entry<PositionedStack, PositionedStack>> getSynthesizerRecipes() {
        return NEIGalacticraftMarsConfig.synthesizerRecipes.entrySet();
    }

    public void registerRecipes() {
        this.registerLiquefierRecipe(
                new PositionedStack(new ItemStack(AsteroidsItems.methaneCanister, 1, 1), 2, 3),
                new PositionedStack(new ItemStack(GCItems.fuelCanister, 1, 1), 127, 3));
        this.registerLiquefierRecipe(
                new PositionedStack(new ItemStack(AsteroidsItems.atmosphericValve, 1, 0), 2, 3),
                new PositionedStack(new ItemStack(AsteroidsItems.canisterLN2, 1, 1), 127, 3));
        this.registerLiquefierRecipe(
                new PositionedStack(new ItemStack(AsteroidsItems.atmosphericValve, 1, 0), 2, 3),
                new PositionedStack(new ItemStack(AsteroidsItems.canisterLOX, 1, 1), 148, 3));
        this.registerLiquefierRecipe(
                new PositionedStack(new ItemStack(AsteroidsItems.canisterLN2, 1, 501), 2, 3),
                new PositionedStack(new ItemStack(AsteroidsItems.canisterLN2, 1, 1), 127, 3));
        this.registerLiquefierRecipe(
                new PositionedStack(new ItemStack(AsteroidsItems.canisterLOX, 1, 501), 2, 3),
                new PositionedStack(new ItemStack(AsteroidsItems.canisterLOX, 1, 1), 148, 3));
        this.registerSynthesizerRecipe(
                new PositionedStack(new ItemStack(AsteroidsItems.atmosphericValve, 1, 0), 23, 3),
                new PositionedStack(new ItemStack(AsteroidsItems.methaneCanister, 1, 1), 148, 3));
        this.registerSynthesizerRecipe(
                new PositionedStack(new ItemStack(MarsItems.carbonFragments, 25, 0), 23, 49),
                new PositionedStack(new ItemStack(AsteroidsItems.methaneCanister, 1, 1), 148, 3));
    }
}
