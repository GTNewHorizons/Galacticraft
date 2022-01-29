package micdoodle8.mods.galacticraft.planets.asteroids.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import gregtech.api.util.GT_ModHandler;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.RecipeUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import micdoodle8.mods.galacticraft.planets.mars.nei.NEIGalacticraftMarsConfig;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NEIGalacticraftAsteroidsConfig implements IConfigureNEI
{
    private static HashMap<ArrayList<PositionedStack>, PositionedStack> rocketBenchRecipes = new HashMap<ArrayList<PositionedStack>, PositionedStack>();
    private static HashMap<ArrayList<PositionedStack>, PositionedStack> astroMinerRecipes = new HashMap<ArrayList<PositionedStack>, PositionedStack>();

    @Override
    public void loadConfig()
    {
        //Handled by GalaxySpace
        this.registerRecipes();
        //API.registerRecipeHandler(new RocketT3RecipeHandler());
        //API.registerUsageHandler(new RocketT3RecipeHandler());
        API.registerRecipeHandler(new AstroMinerRecipeHandler());
        API.registerUsageHandler(new AstroMinerRecipeHandler());
        API.registerHighlightIdentifier(AsteroidBlocks.blockBasic, NEIGalacticraftMarsConfig.planetsHighlightHandler);
    }

    @Override
    public String getName()
    {
        return "Galacticraft Asteroids NEI Plugin";
    }

    @Override
    public String getVersion()
    {
        return Constants.VERSION;
    }

    public void registerRocketBenchRecipe(ArrayList<PositionedStack> input, PositionedStack output)
    {
        NEIGalacticraftAsteroidsConfig.rocketBenchRecipes.put(input, output);
    }

    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getRocketBenchRecipes()
    {
        return NEIGalacticraftAsteroidsConfig.rocketBenchRecipes.entrySet();
    }

    public void registerAstroMinerRecipe(ArrayList<PositionedStack> input, PositionedStack output)
    {
        NEIGalacticraftAsteroidsConfig.astroMinerRecipes.put(input, output);
    }

    public static Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getAstroMinerRecipes()
    {
        return NEIGalacticraftAsteroidsConfig.astroMinerRecipes.entrySet();
    }

    public void registerRecipes()
    {
        final int changeY = 15;

        ArrayList<PositionedStack> input = new ArrayList<PositionedStack>();

        /*input1.add(new PositionedStack(new ItemStack(AsteroidsItems.heavyNoseCone), 45, -8 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 0), 36, -6 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 0), 36, -6 + 18 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 0), 36, -6 + 36 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 0), 36, -6 + 54 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 0), 36, -6 + 72 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 0), 54, -6 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 0), 54, -6 + 18 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 0), 54, -6 + 36 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 0), 54, -6 + 54 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 0), 54, -6 + 72 + 16 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 1), 45, 100 + changeY));
        input1.add(new PositionedStack(new ItemStack(GCItems.rocketEngine, 1, 1), 18, 64 + changeY));
        input1.add(new PositionedStack(new ItemStack(GCItems.rocketEngine, 1, 1), 72, 64 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 2), 18, 82 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 2), 18, 100 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 2), 72, 82 + changeY));
        input1.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem, 1, 2), 72, 100 + changeY));
        this.registerRocketBenchRecipe(input1, new PositionedStack(new ItemStack(AsteroidsItems.tier3Rocket, 1, 0), 139, 87 + changeY));

        ArrayList<PositionedStack> input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(AsteroidsItems.tier3Rocket, 1, 1), 139, 87 + changeY));

        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 26, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(AsteroidsItems.tier3Rocket, 1, 1), 139, 87 + changeY));

        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 52, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(AsteroidsItems.tier3Rocket, 1, 1), 139, 87 + changeY));

        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90, -15 + changeY));
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 26, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(AsteroidsItems.tier3Rocket, 1, 2), 139, 87 + changeY));

        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 26, -15 + changeY));
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 52, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(AsteroidsItems.tier3Rocket, 1, 2), 139, 87 + changeY));

        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90, -15 + changeY));
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 52, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(AsteroidsItems.tier3Rocket, 1, 2), 139, 87 + changeY));

        input2 = new ArrayList<PositionedStack>(input1);
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90, -15 + changeY));
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 26, -15 + changeY));
        input2.add(new PositionedStack(new ItemStack(Blocks.chest), 90 + 52, -15 + changeY));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(AsteroidsItems.tier3Rocket, 1, 3), 139, 87 + changeY));*/

        int x = AstroMinerRecipeHandler.tX - AstroMinerRecipeHandler.x;
        int y = AstroMinerRecipeHandler.tY - AstroMinerRecipeHandler.y;
        input = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 4; j++) {
                input.add(new PositionedStack(new ItemStack(MarsItems.marsItemBasic, 1, 3), 44 + j * 18 - x, 19 + i * 72 - y));
            }
        }
        input.add(new PositionedStack(new ItemStack(GCItems.flagPole), 116 - x, 19 - y));
        input.add(new PositionedStack(new ItemStack(GCItems.flagPole), 116 - x, 91 - y));
        input.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem), 26 - x, 37 - y));
        input.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem), 8 - x, 55 - y));
        input.add(new PositionedStack(new ItemStack(AsteroidsItems.basicItem), 26 - x, 73 - y));
        input.add(new PositionedStack(new ItemStack(AsteroidsItems.orionDrive), 44 - x, 37 - y));
        input.add(new PositionedStack(new ItemStack(AsteroidsItems.orionDrive), 26 - x, 55 - y));
        input.add(new PositionedStack(new ItemStack(AsteroidsItems.orionDrive), 44 - x, 55 - y));
        input.add(new PositionedStack(new ItemStack(AsteroidsItems.orionDrive), 62 - x, 55 - y));
        if(GalacticraftCore.isGalaxySpaceLoaded) {
            input.add(new PositionedStack(GT_ModHandler.getModItem(Constants.MOD_ID_GALAXYSPACE, "item.RocketControlComputer", 1, 102), 62 - x, 37 - y));
        }
        input.add(new PositionedStack(new ItemStack(GCItems.basicItem, 1, 14), 80 - x, 37 - y));
        input.add(new PositionedStack(new ItemStack(GCItems.basicItem, 1, 14), 98 - x, 37 - y));
        input.add(new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 116 - x, 37 - y));
        input.add(new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 116 - x, 55 - y));
        input.add(new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 116 - x, 73 - y));
        input.add(new PositionedStack(RecipeUtil.getChestItemStack(1, 1), 80 - x, 55 - y));
        input.add(new PositionedStack(RecipeUtil.getChestItemStack(1, 1), 98 - x, 55 - y));
        input.add(new PositionedStack(RecipeUtil.getChestItemStack(1, 8), 44 - x, 73 - y));
        input.add(new PositionedStack(new ItemStack(AsteroidBlocks.beamReceiver), 62 - x, 73 - y));
        input.add(new PositionedStack(GT_ModHandler.getModItem(Constants.MOD_ID_GREGTECH, "gt.metaitem.01", 1, 32603), 80 - x, 73 - y));
        input.add(new PositionedStack(GT_ModHandler.getModItem(Constants.MOD_ID_GREGTECH, "gt.metaitem.01", 1, 32603), 98 - x, 73 - y));
        registerAstroMinerRecipe(input, new PositionedStack(new ItemStack(AsteroidsItems.astroMiner), 143 - x, 55 - y));
    }
}
