package micdoodle8.mods.galacticraft.planets.mars.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import gregtech.api.util.GT_ModHandler;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.RecipeUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NEIGalacticraftMarsConfig implements IConfigureNEI
{
    private static HashMap<ArrayList<PositionedStack>, PositionedStack> rocketBenchRecipes = new HashMap<ArrayList<PositionedStack>, PositionedStack>();
    private static HashMap<ArrayList<PositionedStack>, PositionedStack> cargoBenchRecipes = new HashMap<ArrayList<PositionedStack>, PositionedStack>();
    private static HashMap<PositionedStack, PositionedStack> liquefierRecipes = new HashMap<PositionedStack, PositionedStack>();
    private static HashMap<PositionedStack, PositionedStack> synthesizerRecipes = new HashMap<PositionedStack, PositionedStack>();
	public static GCMarsNEIHighlightHandler planetsHighlightHandler  = new GCMarsNEIHighlightHandler();

    @Override
    public void loadConfig()
    {
        this.registerRecipes();
        //Handled by GalaxySpace
        //API.registerRecipeHandler(new RocketT2RecipeHandler());
        //API.registerUsageHandler(new RocketT2RecipeHandler());
        API.registerRecipeHandler(new CargoRocketRecipeHandler());
        API.registerUsageHandler(new CargoRocketRecipeHandler());
        
        /* Not used in GTNH
        API.registerRecipeHandler(new GasLiquefierRecipeHandler());
        API.registerUsageHandler(new GasLiquefierRecipeHandler());
        API.registerRecipeHandler(new MethaneSynthesizerRecipeHandler());
        API.registerUsageHandler(new MethaneSynthesizerRecipeHandler()); */
        
        API.registerHighlightIdentifier(MarsBlocks.marsBlock, planetsHighlightHandler);
    }

    @Override
    public String getName()
    {
        return "Galacticraft Mars NEI Plugin";
    }

    @Override
    public String getVersion()
    {
        return Constants.VERSION;
    }

    public void registerRocketBenchRecipe(ArrayList<PositionedStack> input, PositionedStack output)
    {
        NEIGalacticraftMarsConfig.rocketBenchRecipes.put(input, output);
    }

    public void registerCargoBenchRecipe(ArrayList<PositionedStack> input, PositionedStack output)
    {
        NEIGalacticraftMarsConfig.cargoBenchRecipes.put(input, output);
    }

    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getRocketBenchRecipes()
    {
        return NEIGalacticraftMarsConfig.rocketBenchRecipes.entrySet();
    }

    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getCargoBenchRecipes()
    {
        return NEIGalacticraftMarsConfig.cargoBenchRecipes.entrySet();
    }

    private void registerLiquefierRecipe(PositionedStack inputStack, PositionedStack outputStack)
    {
        NEIGalacticraftMarsConfig.liquefierRecipes.put(inputStack, outputStack);
    }

    public static Set<Entry<PositionedStack, PositionedStack>> getLiquefierRecipes()
    {
        return NEIGalacticraftMarsConfig.liquefierRecipes.entrySet();
    }

    private void registerSynthesizerRecipe(PositionedStack inputStack, PositionedStack outputStack)
    {
        NEIGalacticraftMarsConfig.synthesizerRecipes.put(inputStack, outputStack);
    }

    public static Set<Entry<PositionedStack, PositionedStack>> getSynthesizerRecipes()
    {
        return NEIGalacticraftMarsConfig.synthesizerRecipes.entrySet();
    }

    public void registerRecipes()
    {
        //Handled by GalaxySpace
        //final int changeY = 15;

        ArrayList<PositionedStack> input = new ArrayList<PositionedStack>();

        /*input1.add(new PositionedStack(new ItemStack(GCItems.partNoseCone), 45, -8 + changeY));
        input1.add(new PositionedStack(new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, -6 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, -6 + 18 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, -6 + 36 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, -6 + 54 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(MarsItems.marsItemBasic, 1, 3), 36, -6 + 72 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, -6 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, -6 + 18 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, -6 + 36 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, -6 + 54 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(MarsItems.marsItemBasic, 1, 3), 54, -6 + 72 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(GCItems.rocketEngine), 45, 100 + changeY));
        input1.add(new PositionedStack(new ItemStack(GCItems.rocketEngine, 1, 1), 18, 64 + changeY));
        input1.add(new PositionedStack(new ItemStack(GCItems.rocketEngine, 1, 1), 72, 64 + changeY));
        input1.add(new PositionedStack(new ItemStack(GCItems.partFins), 18, 82 + changeY));
        input1.add(new PositionedStack(new ItemStack(GCItems.partFins), 18, 100 + changeY));
        input1.add(new PositionedStack(new ItemStack(GCItems.partFins), 72, 82 + changeY));
        input1.add(new PositionedStack(new ItemStack(GCItems.partFins), 72, 100 + changeY));
        this.registerRocketBenchRecipe(input1, new PositionedStack(new ItemStack(MarsItems.spaceship, 1, 0), 139, 87 + changeY));*/

        ArrayList<PositionedStack> input2 = new ArrayList<PositionedStack>();/*
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(MarsItems.spaceship, 1, 1), 139, 87 + changeY));

        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 26, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(MarsItems.spaceship, 1, 1), 139, 87 + changeY));

        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 52, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(MarsItems.spaceship, 1, 1), 139, 87 + changeY));

        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90, -15 + changeY));
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 26, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(MarsItems.spaceship, 1, 2), 139, 87 + changeY));

        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 26, -15 + changeY));
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 52, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(MarsItems.spaceship, 1, 2), 139, 87 + changeY));

        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90, -15 + changeY));
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 52, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(MarsItems.spaceship, 1, 2), 139, 87 + changeY));

        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90, -15 + changeY));
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 26, -15 + changeY));
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 52, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(MarsItems.spaceship, 1, 3), 139, 87 + changeY));*/

        int x = CargoRocketRecipeHandler.tX - CargoRocketRecipeHandler.x;
        int y = CargoRocketRecipeHandler.tY - CargoRocketRecipeHandler.y;
        input.add(new PositionedStack(new ItemStack(GCItems.basicItem, 1, 14), 134 - x, 10 - y));
        if(GalacticraftCore.isGalaxySpaceLoaded) {
            input.add(new PositionedStack(GT_ModHandler.getModItem(Constants.MOD_ID_GALAXYSPACE, "item.RocketControlComputer", 1, 101), 134 - x, 28 - y));
            input.add(new PositionedStack(GT_ModHandler.getModItem(Constants.MOD_ID_GALAXYSPACE, "item.ModuleSmallFuelCanister", 1), 116 - x, 19 - y));
            input.add(new PositionedStack(GT_ModHandler.getModItem(Constants.MOD_ID_GALAXYSPACE, "item.ModuleSmallFuelCanister", 1), 152 - x, 19 - y));
            input.add(new PositionedStack(GT_ModHandler.getModItem(Constants.MOD_ID_GALAXYSPACE, "item.ModuleSmallFuelCanister", 1), 116 - x, 37 - y));
        }
        input.add(new PositionedStack(new ItemStack(GCItems.partNoseCone), 53 - x, 19 - y));
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 2; j++) {
                input.add(new PositionedStack(new ItemStack(MarsItems.marsItemBasic, 1, 3), 44 + j * 18 - x, 37 + i * 18 - y));
            }
        }
        input.add(new PositionedStack(new ItemStack(GCItems.rocketEngine), 53 - x, 109 - y));
        input.add(new PositionedStack(new ItemStack(GCItems.partFins), 26 - x, 91 - y));
        input.add(new PositionedStack(new ItemStack(GCItems.partFins), 80 - x, 91 - y));
        input.add(new PositionedStack(new ItemStack(GCItems.partFins), 26 - x, 109 - y));
        input.add(new PositionedStack(new ItemStack(GCItems.partFins), 80 - x, 109 - y));
        input2 = new ArrayList<>(input);
        input2.add(new PositionedStack(RecipeUtil.getChestItemStack(1, 3), 134 - x, 46 - y));
        registerCargoBenchRecipe(input2, new PositionedStack(new ItemStack(MarsItems.spaceship, 1, 11), 134 - x, 73 - y));
        input2 = new ArrayList<>(input);
        input2.add(new PositionedStack(RecipeUtil.getChestItemStack(1, 0), 134 - x, 46 - y));
        registerCargoBenchRecipe(input2, new PositionedStack(new ItemStack(MarsItems.spaceship, 1, 12), 134 - x, 73 - y));
        input2 = new ArrayList<>(input);
        input2.add(new PositionedStack(RecipeUtil.getChestItemStack(1, 1), 134 - x, 46 - y));
        registerCargoBenchRecipe(input2, new PositionedStack(new ItemStack(MarsItems.spaceship, 1, 13), 134 - x, 73 - y));

        this.registerLiquefierRecipe(new PositionedStack(new ItemStack(AsteroidsItems.methaneCanister, 1, 1), 2, 3), new PositionedStack(new ItemStack(GCItems.fuelCanister, 1, 1), 127, 3));
        this.registerLiquefierRecipe(new PositionedStack(new ItemStack(AsteroidsItems.atmosphericValve, 1, 0), 2, 3), new PositionedStack(new ItemStack(AsteroidsItems.canisterLN2, 1, 1), 127, 3));
        this.registerLiquefierRecipe(new PositionedStack(new ItemStack(AsteroidsItems.atmosphericValve, 1, 0), 2, 3), new PositionedStack(new ItemStack(AsteroidsItems.canisterLOX, 1, 1), 148, 3));
        this.registerLiquefierRecipe(new PositionedStack(new ItemStack(AsteroidsItems.canisterLN2, 1, 501), 2, 3), new PositionedStack(new ItemStack(AsteroidsItems.canisterLN2, 1, 1), 127, 3));
        this.registerLiquefierRecipe(new PositionedStack(new ItemStack(AsteroidsItems.canisterLOX, 1, 501), 2, 3), new PositionedStack(new ItemStack(AsteroidsItems.canisterLOX, 1, 1), 148, 3));
        this.registerSynthesizerRecipe(new PositionedStack(new ItemStack(AsteroidsItems.atmosphericValve, 1, 0), 23, 3), new PositionedStack(new ItemStack(AsteroidsItems.methaneCanister, 1, 1), 148, 3));
        this.registerSynthesizerRecipe(new PositionedStack(new ItemStack(MarsItems.carbonFragments, 25, 0), 23, 49), new PositionedStack(new ItemStack(AsteroidsItems.methaneCanister, 1, 1), 148, 3));
    }
}
