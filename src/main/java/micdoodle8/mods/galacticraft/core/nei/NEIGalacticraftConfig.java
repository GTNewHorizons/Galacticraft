package micdoodle8.mods.galacticraft.core.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.RecipeUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class NEIGalacticraftConfig implements IConfigureNEI {
    private static HashMap<HashMap<Integer, PositionedStack>, PositionedStack> rocketBenchRecipes =
            new HashMap<HashMap<Integer, PositionedStack>, PositionedStack>();
    private static HashMap<HashMap<Integer, PositionedStack>, PositionedStack> buggyBenchRecipes =
            new HashMap<HashMap<Integer, PositionedStack>, PositionedStack>();
    private static HashMap<PositionedStack, PositionedStack> refineryRecipes =
            new HashMap<PositionedStack, PositionedStack>();
    private static HashMap<HashMap<Integer, PositionedStack>, PositionedStack> circuitFabricatorRecipes =
            new HashMap<HashMap<Integer, PositionedStack>, PositionedStack>();
    private static HashMap<HashMap<Integer, PositionedStack>, PositionedStack> ingotCompressorRecipes =
            new HashMap<HashMap<Integer, PositionedStack>, PositionedStack>();

    @Override
    public void loadConfig() {
        this.registerRecipes();

        for (Item item : GCItems.hiddenItems) {
            API.hideItem(new ItemStack(item, 1, 0));
        }

        for (Block block : GCBlocks.hiddenBlocks) {
            API.hideItem(new ItemStack(block, 1, 0));
            if (block == GCBlocks.slabGCDouble) {
                for (int j = 1; j < (GalacticraftCore.isPlanetsLoaded ? 6 : 4); j++)
                    API.hideItem(new ItemStack(block, 1, j));
            }
        }

        // Handled by GalaxySpace
        /*API.registerRecipeHandler(new RocketT1RecipeHandler());
        API.registerUsageHandler(new RocketT1RecipeHandler());*/
        API.registerRecipeHandler(new BuggyRecipeHandler());
        API.registerUsageHandler(new BuggyRecipeHandler());
        API.registerRecipeHandler(new RefineryRecipeHandler());
        API.registerUsageHandler(new RefineryRecipeHandler());
        API.registerRecipeHandler(new CircuitFabricatorRecipeHandler());
        API.registerUsageHandler(new CircuitFabricatorRecipeHandler());

        /* Not used in GTNH
        API.registerRecipeHandler(new IngotCompressorRecipeHandler());
        API.registerUsageHandler(new IngotCompressorRecipeHandler());
        API.registerRecipeHandler(new ElectricIngotCompressorRecipeHandler());
        API.registerUsageHandler(new ElectricIngotCompressorRecipeHandler()); */

        API.registerHighlightIdentifier(GCBlocks.basicBlock, new GCNEIHighlightHandler());
        API.registerHighlightIdentifier(GCBlocks.blockMoon, new GCNEIHighlightHandler());
        API.registerHighlightIdentifier(GCBlocks.fakeBlock, new GCNEIHighlightHandler());
    }

    @Override
    public String getName() {
        return "Galacticraft NEI Plugin";
    }

    @Override
    public String getVersion() {
        return Constants.VERSION;
    }

    public void registerIngotCompressorRecipe(HashMap<Integer, PositionedStack> input, PositionedStack output) {
        NEIGalacticraftConfig.ingotCompressorRecipes.put(input, output);
    }

    public void registerCircuitFabricatorRecipe(HashMap<Integer, PositionedStack> input, PositionedStack output) {
        NEIGalacticraftConfig.circuitFabricatorRecipes.put(input, output);
    }

    public void registerRocketBenchRecipe(HashMap<Integer, PositionedStack> input, PositionedStack output) {
        NEIGalacticraftConfig.rocketBenchRecipes.put(input, output);
    }

    public void registerBuggyBenchRecipe(HashMap<Integer, PositionedStack> input, PositionedStack output) {
        NEIGalacticraftConfig.buggyBenchRecipes.put(input, output);
    }

    public void registerRefineryRecipe(PositionedStack input, PositionedStack output) {
        NEIGalacticraftConfig.refineryRecipes.put(input, output);
    }

    public static Set<Entry<HashMap<Integer, PositionedStack>, PositionedStack>> getIngotCompressorRecipes() {
        return NEIGalacticraftConfig.ingotCompressorRecipes.entrySet();
    }

    public static Set<Entry<HashMap<Integer, PositionedStack>, PositionedStack>> getCircuitFabricatorRecipes() {
        return NEIGalacticraftConfig.circuitFabricatorRecipes.entrySet();
    }

    public static Set<Entry<HashMap<Integer, PositionedStack>, PositionedStack>> getRocketBenchRecipes() {
        return NEIGalacticraftConfig.rocketBenchRecipes.entrySet();
    }

    public static Set<Entry<HashMap<Integer, PositionedStack>, PositionedStack>> getBuggyBenchRecipes() {
        return NEIGalacticraftConfig.buggyBenchRecipes.entrySet();
    }

    public static Set<Entry<PositionedStack, PositionedStack>> getRefineryRecipes() {
        return NEIGalacticraftConfig.refineryRecipes.entrySet();
    }

    public void registerRecipes() {
        this.registerRefineryRecipe(
                new PositionedStack(new ItemStack(GCItems.oilCanister, 1, 1), 2, 3),
                new PositionedStack(new ItemStack(GCItems.fuelCanister, 1, 1), 148, 3));

        // Handled by GalaxySpace
        // this.addRocketRecipes();
        this.addBuggyRecipes();
        this.addCircuitFabricatorRecipes();
        this.addIngotCompressorRecipes();
    }

    private void addBuggyRecipes() {
        int x = BuggyRecipeHandler.tX - BuggyRecipeHandler.x;
        int y = BuggyRecipeHandler.tY - BuggyRecipeHandler.y;
        HashMap<Integer, PositionedStack> input = new HashMap<Integer, PositionedStack>();
        input.put(1, new PositionedStack(new ItemStack(GCItems.basicItem, 1, 19), 62 - x, 19 - y));
        input.put(2, new PositionedStack(new ItemStack(GCItems.partBuggy, 1, 1), 62 - x, 55 - y));
        if (GalacticraftCore.isGalaxySpaceLoaded) {
            input.put(
                    3,
                    new PositionedStack(
                            GT_ModHandler.getModItem(
                                    Constants.MOD_ID_GALAXYSPACE, "item.RocketControlComputer", 1, 100),
                            62 - x,
                            73 - y));
        }
        input.put(4, new PositionedStack(new ItemStack(GCItems.partBuggy), 8 - x, 19 - y));
        input.put(5, new PositionedStack(new ItemStack(GCItems.partBuggy), 116 - x, 19 - y));
        input.put(6, new PositionedStack(new ItemStack(GCItems.partBuggy), 8 - x, 109 - y));
        input.put(7, new PositionedStack(new ItemStack(GCItems.partBuggy), 116 - x, 109 - y));
        input.put(
                8,
                new PositionedStack(
                        GT_OreDictUnificator.get(OrePrefixes.stick, Materials.StainlessSteel, 1), 26 - x, 19 - y));
        input.put(
                9,
                new PositionedStack(
                        GT_OreDictUnificator.get(OrePrefixes.stick, Materials.StainlessSteel, 1), 98 - x, 19 - y));
        input.put(
                10,
                new PositionedStack(
                        GT_OreDictUnificator.get(OrePrefixes.stick, Materials.StainlessSteel, 1), 26 - x, 109 - y));
        input.put(
                11,
                new PositionedStack(
                        GT_OreDictUnificator.get(OrePrefixes.stick, Materials.StainlessSteel, 1), 98 - x, 109 - y));
        input.put(12, new PositionedStack(new ItemStack(GCItems.meteoricIronIngot, 1, 1), 44 - x, 19 - y));
        input.put(13, new PositionedStack(new ItemStack(GCItems.meteoricIronIngot, 1, 1), 80 - x, 19 - y));
        input.put(14, new PositionedStack(new ItemStack(GCItems.meteoricIronIngot, 1, 1), 44 - x, 109 - y));
        input.put(15, new PositionedStack(new ItemStack(GCItems.meteoricIronIngot, 1, 1), 62 - x, 109 - y));
        input.put(16, new PositionedStack(new ItemStack(GCItems.meteoricIronIngot, 1, 1), 80 - x, 109 - y));
        input.put(
                17,
                new PositionedStack(
                        GT_OreDictUnificator.get(OrePrefixes.screw, Materials.StainlessSteel, 1), 8 - x, 37 - y));
        input.put(
                18,
                new PositionedStack(
                        GT_OreDictUnificator.get(OrePrefixes.screw, Materials.StainlessSteel, 1), 26 - x, 37 - y));
        input.put(
                19,
                new PositionedStack(
                        GT_OreDictUnificator.get(OrePrefixes.screw, Materials.StainlessSteel, 1), 98 - x, 37 - y));
        input.put(
                20,
                new PositionedStack(
                        GT_OreDictUnificator.get(OrePrefixes.screw, Materials.StainlessSteel, 1), 116 - x, 37 - y));
        input.put(
                21,
                new PositionedStack(
                        GT_OreDictUnificator.get(OrePrefixes.screw, Materials.StainlessSteel, 1), 8 - x, 91 - y));
        input.put(
                22,
                new PositionedStack(
                        GT_OreDictUnificator.get(OrePrefixes.screw, Materials.StainlessSteel, 1), 26 - x, 91 - y));
        input.put(
                23,
                new PositionedStack(
                        GT_OreDictUnificator.get(OrePrefixes.screw, Materials.StainlessSteel, 1), 98 - x, 91 - y));
        input.put(
                24,
                new PositionedStack(
                        GT_OreDictUnificator.get(OrePrefixes.screw, Materials.StainlessSteel, 1), 116 - x, 91 - y));
        input.put(25, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 44 - x, 37 - y));
        input.put(26, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 62 - x, 37 - y));
        input.put(27, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 80 - x, 37 - y));
        input.put(28, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 44 - x, 55 - y));
        input.put(29, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 80 - x, 55 - y));
        input.put(30, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 44 - x, 73 - y));
        input.put(31, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 80 - x, 73 - y));
        input.put(32, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 44 - x, 91 - y));
        input.put(33, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 62 - x, 91 - y));
        input.put(34, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 80 - x, 91 - y));
        registerBuggyBenchRecipe(input, new PositionedStack(new ItemStack(GCItems.buggy), 143 - x, 64 - y));
        HashMap<Integer, PositionedStack> input2 = new HashMap<>(input);
        input2.put(35, new PositionedStack(RecipeUtil.getChestItemStack(1, 3), 107 - x, 64 - y));
        registerBuggyBenchRecipe(input2, new PositionedStack(new ItemStack(GCItems.buggy, 1, 1), 143 - x, 64 - y));
        input2 = new HashMap<>(input);
        input2.put(35, new PositionedStack(RecipeUtil.getChestItemStack(1, 0), 107 - x, 64 - y));
        registerBuggyBenchRecipe(input2, new PositionedStack(new ItemStack(GCItems.buggy, 1, 2), 143 - x, 64 - y));
        input2 = new HashMap<>(input);
        input2.put(35, new PositionedStack(RecipeUtil.getChestItemStack(1, 1), 107 - x, 64 - y));
        registerBuggyBenchRecipe(input2, new PositionedStack(new ItemStack(GCItems.buggy, 1, 3), 143 - x, 64 - y));
    }

    private void addRocketRecipes() {
        HashMap<Integer, PositionedStack> input1 = new HashMap<Integer, PositionedStack>();
        input1.put(0, new PositionedStack(new ItemStack(GCItems.partNoseCone), 45, 15));
        input1.put(1, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 36, 33));
        input1.put(2, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 36, 51));
        input1.put(3, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 36, 69));
        input1.put(4, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 36, 87));
        input1.put(5, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 54, 33));
        input1.put(6, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 54, 51));
        input1.put(7, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 54, 69));
        input1.put(8, new PositionedStack(new ItemStack(GCItems.heavyPlatingTier1), 54, 87));
        input1.put(9, new PositionedStack(new ItemStack(GCItems.rocketEngine), 45, 105));
        input1.put(10, new PositionedStack(new ItemStack(GCItems.partFins), 18, 87));
        input1.put(11, new PositionedStack(new ItemStack(GCItems.partFins), 18, 105));
        input1.put(12, new PositionedStack(new ItemStack(GCItems.partFins), 72, 87));
        input1.put(13, new PositionedStack(new ItemStack(GCItems.partFins), 72, 105));
        this.registerRocketBenchRecipe(input1, new PositionedStack(new ItemStack(GCItems.rocketTier1, 1, 0), 139, 92));

        HashMap<Integer, PositionedStack> input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(14, new PositionedStack(new ItemStack(Blocks.chest), 90, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(GCItems.rocketTier1, 1, 1), 139, 92));

        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(15, new PositionedStack(new ItemStack(Blocks.chest), 116, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(GCItems.rocketTier1, 1, 1), 139, 92));

        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(16, new PositionedStack(new ItemStack(Blocks.chest), 142, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(GCItems.rocketTier1, 1, 1), 139, 92));

        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(14, new PositionedStack(new ItemStack(Blocks.chest), 90, 8));
        input2.put(15, new PositionedStack(new ItemStack(Blocks.chest), 116, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(GCItems.rocketTier1, 1, 2), 139, 92));

        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(15, new PositionedStack(new ItemStack(Blocks.chest), 116, 8));
        input2.put(16, new PositionedStack(new ItemStack(Blocks.chest), 142, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(GCItems.rocketTier1, 1, 2), 139, 92));

        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(14, new PositionedStack(new ItemStack(Blocks.chest), 90, 8));
        input2.put(16, new PositionedStack(new ItemStack(Blocks.chest), 142, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(GCItems.rocketTier1, 1, 2), 139, 92));

        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(14, new PositionedStack(new ItemStack(Blocks.chest), 90, 8));
        input2.put(15, new PositionedStack(new ItemStack(Blocks.chest), 116, 8));
        input2.put(16, new PositionedStack(new ItemStack(Blocks.chest), 142, 8));
        this.registerRocketBenchRecipe(input2, new PositionedStack(new ItemStack(GCItems.rocketTier1, 1, 3), 139, 92));
    }

    private void addCircuitFabricatorRecipes() {
        HashMap<Integer, PositionedStack> input1 = new HashMap<Integer, PositionedStack>();
        input1.put(0, new PositionedStack(new ItemStack(Items.diamond), 10, 22));
        int siliconCount =
                OreDictionary.getOres(ConfigManagerCore.otherModsSilicon).size();
        ItemStack[] silicons = new ItemStack[siliconCount];
        //        silicons[0] = new ItemStack(GCItems.basicItem, 1, 2);  //This is now included in the oredict
        for (int j = 0; j < siliconCount; j++) {
            silicons[j] =
                    OreDictionary.getOres(ConfigManagerCore.otherModsSilicon).get(j);
        }
        input1.put(1, new PositionedStack(silicons, 69, 51));
        input1.put(2, new PositionedStack(silicons, 69, 69));
        input1.put(3, new PositionedStack(new ItemStack(Items.redstone), 117, 51));
        input1.put(4, new PositionedStack(new ItemStack(Blocks.redstone_torch), 140, 25));
        this.registerCircuitFabricatorRecipe(
                input1,
                new PositionedStack(
                        new ItemStack(GCItems.basicItem, ConfigManagerCore.quickMode ? 5 : 3, 13), 147, 91));

        HashMap<Integer, PositionedStack> input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(4, new PositionedStack(new ItemStack(Items.dye, 1, 4), 140, 25));
        this.registerCircuitFabricatorRecipe(
                input2, new PositionedStack(new ItemStack(GCItems.basicItem, 9, 12), 147, 91));

        input2 = new HashMap<Integer, PositionedStack>(input1);
        input2.put(4, new PositionedStack(new ItemStack(Items.repeater), 140, 25));
        this.registerCircuitFabricatorRecipe(
                input2,
                new PositionedStack(
                        new ItemStack(GCItems.basicItem, ConfigManagerCore.quickMode ? 2 : 1, 14), 147, 91));
    }

    private void addIngotCompressorRecipes() {
        for (int i = 0; i < CompressorRecipes.getRecipeList().size(); i++) {
            HashMap<Integer, PositionedStack> input1 = new HashMap<Integer, PositionedStack>();
            IRecipe rec = CompressorRecipes.getRecipeList().get(i);

            if (rec instanceof ShapedRecipes) {
                ShapedRecipes recipe = (ShapedRecipes) rec;

                for (int j = 0; j < recipe.recipeItems.length; j++) {
                    ItemStack stack = recipe.recipeItems[j];

                    input1.put(j, new PositionedStack(stack, 21 + j % 3 * 18, 26 + j / 3 * 18));
                }
            } else if (rec instanceof ShapelessOreRecipe) {
                ShapelessOreRecipe recipe = (ShapelessOreRecipe) rec;

                for (int j = 0; j < recipe.getInput().size(); j++) {
                    Object obj = recipe.getInput().get(j);

                    input1.put(j, new PositionedStack(obj, 21 + j % 3 * 18, 26 + j / 3 * 18));
                }
            }

            ItemStack resultItemStack = rec.getRecipeOutput();
            if (ConfigManagerCore.quickMode) {
                if (resultItemStack
                        .getItem()
                        .getUnlocalizedName(resultItemStack)
                        .contains("compressed")) resultItemStack.stackSize *= 2;
            }

            this.registerIngotCompressorRecipe(input1, new PositionedStack(resultItemStack, 140, 46));
        }
    }
}
