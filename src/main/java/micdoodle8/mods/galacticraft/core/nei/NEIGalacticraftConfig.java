package micdoodle8.mods.galacticraft.core.nei;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import cpw.mods.fml.common.Loader;
import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;

public class NEIGalacticraftConfig implements IConfigureNEI {

    private static final HashMap<HashMap<Integer, PositionedStack>, PositionedStack> rocketBenchRecipes = new HashMap<>();
    private static final HashMap<HashMap<Integer, PositionedStack>, PositionedStack> buggyBenchRecipes = new HashMap<>();
    private static final HashMap<PositionedStack, PositionedStack> refineryRecipes = new HashMap<>();
    private static final HashMap<HashMap<Integer, PositionedStack>, PositionedStack> circuitFabricatorRecipes = new HashMap<>();
    private static final HashMap<HashMap<Integer, PositionedStack>, PositionedStack> ingotCompressorRecipes = new HashMap<>();

    @Override
    public void loadConfig() {
        this.registerRecipes();

        for (final Item item : GCItems.hiddenItems) {
            API.hideItem(new ItemStack(item, 1, 0));
        }

        for (final Block block : GCBlocks.hiddenBlocks) {
            API.hideItem(new ItemStack(block, 1, 0));
            if (block == GCBlocks.slabGCDouble) {
                for (int j = 1; j < 6; j++) {
                    API.hideItem(new ItemStack(block, 1, j));
                }
            }
        }

        if (Loader.isModLoaded(Constants.MOD_ID_NEW_HORIZONS_CORE_MOD)) {
            API.hideItem(new ItemStack(GCItems.basicItem, 1, 3));
            API.hideItem(new ItemStack(GCItems.basicItem, 1, 4));
            API.hideItem(new ItemStack(GCItems.basicItem, 1, 5));
            API.hideItem(new ItemStack(GCItems.meteoricIronIngot, 1, 0));
        }

        // Handled by GalaxySpace
        /*
         * API.registerRecipeHandler(new RocketT1RecipeHandler()); API.registerUsageHandler(new
         * RocketT1RecipeHandler());
         */
        API.registerRecipeHandler(new BuggyRecipeHandler());
        API.registerUsageHandler(new BuggyRecipeHandler());
        API.registerRecipeHandler(new RefineryRecipeHandler());
        API.registerUsageHandler(new RefineryRecipeHandler());
        API.registerRecipeHandler(new CircuitFabricatorRecipeHandler());
        API.registerUsageHandler(new CircuitFabricatorRecipeHandler());

        /*
         * Not used in GTNH API.registerRecipeHandler(new IngotCompressorRecipeHandler()); API.registerUsageHandler(new
         * IngotCompressorRecipeHandler()); API.registerRecipeHandler(new ElectricIngotCompressorRecipeHandler());
         * API.registerUsageHandler(new ElectricIngotCompressorRecipeHandler());
         */

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
        this.addCircuitFabricatorRecipes();
        this.addIngotCompressorRecipes();
    }

    private void addCircuitFabricatorRecipes() {
        final HashMap<Integer, PositionedStack> input1 = new HashMap<>();
        input1.put(0, new PositionedStack(new ItemStack(Items.diamond), 10, 22));
        final int siliconCount = OreDictionary.getOres(ConfigManagerCore.otherModsSilicon).size();
        final ItemStack[] silicons = new ItemStack[siliconCount];
        // silicons[0] = new ItemStack(GCItems.basicItem, 1, 2); //This is now included
        // in the oredict
        for (int j = 0; j < siliconCount; j++) {
            silicons[j] = OreDictionary.getOres(ConfigManagerCore.otherModsSilicon).get(j);
        }
        input1.put(1, new PositionedStack(silicons, 69, 51));
        input1.put(2, new PositionedStack(silicons, 69, 69));
        input1.put(3, new PositionedStack(new ItemStack(Items.redstone), 117, 51));
        input1.put(4, new PositionedStack(new ItemStack(Blocks.redstone_torch), 140, 25));
        this.registerCircuitFabricatorRecipe(
                input1,
                new PositionedStack(
                        new ItemStack(GCItems.basicItem, ConfigManagerCore.quickMode ? 5 : 3, 13),
                        147,
                        91));

        HashMap<Integer, PositionedStack> input2 = new HashMap<>(input1);
        input2.put(4, new PositionedStack(new ItemStack(Items.dye, 1, 4), 140, 25));
        this.registerCircuitFabricatorRecipe(
                input2,
                new PositionedStack(new ItemStack(GCItems.basicItem, 9, 12), 147, 91));

        input2 = new HashMap<>(input1);
        input2.put(4, new PositionedStack(new ItemStack(Items.repeater), 140, 25));
        this.registerCircuitFabricatorRecipe(
                input2,
                new PositionedStack(
                        new ItemStack(GCItems.basicItem, ConfigManagerCore.quickMode ? 2 : 1, 14),
                        147,
                        91));
    }

    private void addIngotCompressorRecipes() {
        for (final IRecipe rec : CompressorRecipes.getRecipeList()) {
            final HashMap<Integer, PositionedStack> input1 = new HashMap<>();
            if (rec instanceof ShapedRecipes recipe) {
                for (int j = 0; j < recipe.recipeItems.length; j++) {
                    final ItemStack stack = recipe.recipeItems[j];

                    input1.put(j, new PositionedStack(stack, 21 + j % 3 * 18, 26 + j / 3 * 18));
                }
            } else if (rec instanceof ShapelessOreRecipe recipe) {
                for (int j = 0; j < recipe.getInput().size(); j++) {
                    final Object obj = recipe.getInput().get(j);

                    input1.put(j, new PositionedStack(obj, 21 + j % 3 * 18, 26 + j / 3 * 18));
                }
            }

            final ItemStack resultItemStack = rec.getRecipeOutput();
            if (ConfigManagerCore.quickMode
                    && resultItemStack.getItem().getUnlocalizedName(resultItemStack).contains("compressed")) {
                resultItemStack.stackSize *= 2;
            }

            this.registerIngotCompressorRecipe(input1, new PositionedStack(resultItemStack, 140, 46));
        }
    }
}
