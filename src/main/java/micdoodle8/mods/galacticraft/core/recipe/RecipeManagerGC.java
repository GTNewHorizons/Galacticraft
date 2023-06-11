package micdoodle8.mods.galacticraft.core.recipe;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import appeng.api.AEApi;
import appeng.api.util.AEColor;
import cpw.mods.fml.common.Loader;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.entity.IFuelable;
import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes;
import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes;
import micdoodle8.mods.galacticraft.api.recipe.RocketFuels;
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe;
import micdoodle8.mods.galacticraft.api.world.SpaceStationType;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockEnclosed.EnumEnclosedBlock;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.entities.EntityTier1Rocket;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.items.ItemBasic;
import micdoodle8.mods.galacticraft.core.items.ItemParaChute;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.RecipeUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityTier3Rocket;
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityTier2Rocket;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;

public class RecipeManagerGC {

    public static ArrayList<ItemStack> aluminumIngots = new ArrayList<>();

    public static void loadRecipes() {
        if (CompatibilityManager.isBCraftTransportLoaded()) {
            RecipeManagerGC.addBuildCraftCraftingRecipes();
        }

        if (CompatibilityManager.isIc2Loaded()) {
            RecipeManagerGC.addIndustrialCraft2Recipes();
        }

        if (CompatibilityManager.isAppEngLoaded()) {
            RecipeManagerGC.addAppEngRecipes();
        }

        RecipeManagerGC.addUniversalRecipes();

        RecipeManagerGC.addExNihiloRecipes();
    }

    @SuppressWarnings("unchecked")
    private static void addUniversalRecipes() {

        // If you change these, please change EventHandlerGC#onItemTooltipEvent too!
        final String[] t2Fuels = { "fluid.rocketfuelmixa", "fluid.rocketfuelmixb", "fluid.rocketfuelmixc",
                "fluid.rocketfuelmixd", "nitrofuel", "rocket_fuel" };
        final String[] t4Fuels = { "fluid.rocketfuelmixa", "fluid.rocketfuelmixc", "fluid.rocketfuelmixd",
                "rocket_fuel" };
        final String[] t6Fuels = { "fluid.rocketfuelmixa", "fluid.rocketfuelmixc", "rocket_fuel" };
        final String[] t8Fuels = { "fluid.rocketfuelmixa", "rocket_fuel" };

        for (String fluidName : t2Fuels) {
            RocketFuels.addFuel(EntityTier1Rocket.class, fluidName);
            RocketFuels.addFuel(EntityTier2Rocket.class, fluidName);
        }
        for (String fluidName : t4Fuels) {
            RocketFuels.addFuel(EntityTier3Rocket.class, fluidName);
        }

        if (Loader.isModLoaded("GalaxySpace")) {
            try {
                Class<? extends IFuelable> rocketT4 = (Class<? extends IFuelable>) Class
                        .forName("galaxyspace.core.entity.rocket.EntityTier4Rocket");
                Class<? extends IFuelable> rocketT5 = (Class<? extends IFuelable>) Class
                        .forName("galaxyspace.core.entity.rocket.EntityTier5Rocket");
                Class<? extends IFuelable> rocketT6 = (Class<? extends IFuelable>) Class
                        .forName("galaxyspace.core.entity.rocket.EntityTier6Rocket");
                Class<? extends IFuelable> rocketT7 = (Class<? extends IFuelable>) Class
                        .forName("galaxyspace.core.entity.rocket.EntityTier7Rocket");
                Class<? extends IFuelable> rocketT8 = (Class<? extends IFuelable>) Class
                        .forName("galaxyspace.core.entity.rocket.EntityTier8Rocket");

                for (String fluidName : t4Fuels) {
                    RocketFuels.addFuel(rocketT4, fluidName);
                }
                for (String fluidName : t6Fuels) {
                    RocketFuels.addFuel(rocketT5, fluidName);
                    RocketFuels.addFuel(rocketT6, fluidName);
                }
                for (String fluidName : t8Fuels) {
                    RocketFuels.addFuel(rocketT7, fluidName);
                    RocketFuels.addFuel(rocketT8, fluidName);
                }
            } catch (ClassNotFoundException e) {
                GCLog.severe("A GalaxySpace rocket class wasn't found although the mod is installed!");
                GCLog.exception(e);
            }
        }

        if (Loader.isModLoaded("GalacticraftAmunRa")) {
            try {
                Class<? extends IFuelable> shuttleRocket = (Class<? extends IFuelable>) Class
                        .forName("de.katzenpapst.amunra.entity.spaceship.EntityShuttle");

                for (String fluidName : t8Fuels) {
                    RocketFuels.addFuel(shuttleRocket, fluidName);
                }
            } catch (ClassNotFoundException e) {
                GCLog.severe("The Amun-Ra shuttle rocket class wasn't found although the mod is installed!");
                GCLog.exception(e);
            }
        }

        final Object meteoricIronIngot = ConfigManagerCore.recipesRequireGCAdvancedMetals ? GCItems.meteoricIronIngot
                : "ingotMeteoricIron";
        final Object meteoricIronPlate = ConfigManagerCore.recipesRequireGCAdvancedMetals
                ? new ItemStack(GCItems.meteoricIronIngot, 1, 1)
                : "compressedMeteoricIron";
        final Object deshIngot = GalacticraftCore.isPlanetsLoaded
                ? ConfigManagerCore.recipesRequireGCAdvancedMetals ? new ItemStack(MarsItems.marsItemBasic, 1, 2)
                        : "ingotDesh"
                : GCItems.heavyPlatingTier1;

        // RocketFuelRecipe.addFuel(GalacticraftCore.fluidFuel,1);
        FurnaceRecipes.smelting()
                .func_151394_a(new ItemStack(GCBlocks.basicBlock, 1, 5), new ItemStack(GCItems.basicItem, 1, 3), 0.5F);
        FurnaceRecipes.smelting()
                .func_151394_a(new ItemStack(GCBlocks.basicBlock, 1, 6), new ItemStack(GCItems.basicItem, 1, 4), 0.5F);
        FurnaceRecipes.smelting()
                .func_151394_a(new ItemStack(GCBlocks.basicBlock, 1, 7), new ItemStack(GCItems.basicItem, 1, 5), 0.5F);
        FurnaceRecipes.smelting().func_151394_a(
                new ItemStack(GCItems.meteorChunk, 1, 0),
                new ItemStack(GCItems.meteorChunk, 1, 1),
                0.1F);
        FurnaceRecipes.smelting()
                .func_151396_a(GCItems.meteoricIronRaw, new ItemStack(GCItems.meteoricIronIngot), 1.0F);
        FurnaceRecipes.smelting()
                .func_151394_a(new ItemStack(GCBlocks.blockMoon, 1, 0), new ItemStack(GCItems.basicItem, 1, 3), 1.0F);
        FurnaceRecipes.smelting()
                .func_151394_a(new ItemStack(GCBlocks.blockMoon, 1, 1), new ItemStack(GCItems.basicItem, 1, 4), 1.0F);
        FurnaceRecipes.smelting()
                .func_151394_a(new ItemStack(GCBlocks.blockMoon, 1, 2), new ItemStack(GCItems.cheeseCurd), 1.0F);
        // Recycling: smelt tin/copper canisters back into ingots
        FurnaceRecipes.smelting()
                .func_151394_a(new ItemStack(GCItems.canister, 1, 0), new ItemStack(GCItems.basicItem, 3, 4), 1.0F);
        FurnaceRecipes.smelting()
                .func_151394_a(new ItemStack(GCItems.canister, 1, 1), new ItemStack(GCItems.basicItem, 3, 3), 1.0F);

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.rocketEngine, 1, 1),
                new Object[] { "ZYZ", "ZWZ", "XVX", 'V', GCItems.oxygenVent, 'W',
                        new ItemStack(GCItems.fuelCanister, 1, 1), 'X', GCItems.heavyPlatingTier1, 'Y',
                        new ItemStack(Blocks.wool, 1, 4), 'Z', meteoricIronPlate });

        // Handled by GalaxySpace
        HashMap<Integer, ItemStack> input = new HashMap<>();
        /*
         * input.put(1, new ItemStack(GCItems.partNoseCone)); input.put(2, new ItemStack(GCItems.heavyPlatingTier1));
         * input.put(3, new ItemStack(GCItems.heavyPlatingTier1)); input.put(4, new
         * ItemStack(GCItems.heavyPlatingTier1)); input.put(5, new ItemStack(GCItems.heavyPlatingTier1)); input.put(6,
         * new ItemStack(GCItems.heavyPlatingTier1)); input.put(7, new ItemStack(GCItems.heavyPlatingTier1));
         * input.put(8, new ItemStack(GCItems.heavyPlatingTier1)); input.put(9, new
         * ItemStack(GCItems.heavyPlatingTier1)); input.put(10, new ItemStack(GCItems.partFins)); input.put(11, new
         * ItemStack(GCItems.partFins)); input.put(12, new ItemStack(GCItems.rocketEngine)); input.put(13, new
         * ItemStack(GCItems.partFins)); input.put(14, new ItemStack(GCItems.partFins)); input.put(15, null);
         * input.put(16, null); input.put(17, null); RecipeUtil.addRocketBenchRecipe(new ItemStack(GCItems.rocketTier1,
         * 1, 0), input);
         */

        HashMap<Integer, ItemStack> input2 = new HashMap<>(
                input); /*
                         * input2.put(15, new ItemStack(Blocks.chest)); input2.put(16, null); input2.put(17, null);
                         * RecipeUtil.addRocketBenchRecipe(new ItemStack(GCItems.rocketTier1, 1, 1), input2); input2 =
                         * new HashMap<Integer, ItemStack>(input); input2.put(15, null); input2.put(16, new
                         * ItemStack(Blocks.chest)); input2.put(17, null); RecipeUtil.addRocketBenchRecipe(new
                         * ItemStack(GCItems.rocketTier1, 1, 1), input2); input2 = new HashMap<Integer,
                         * ItemStack>(input); input2.put(15, null); input2.put(16, null); input2.put(17, new
                         * ItemStack(Blocks.chest)); RecipeUtil.addRocketBenchRecipe(new ItemStack(GCItems.rocketTier1,
                         * 1, 1), input2); input2 = new HashMap<Integer, ItemStack>(input); input2.put(15, new
                         * ItemStack(Blocks.chest)); input2.put(16, new ItemStack(Blocks.chest)); input2.put(17, null);
                         * RecipeUtil.addRocketBenchRecipe(new ItemStack(GCItems.rocketTier1, 1, 2), input2); input2 =
                         * new HashMap<Integer, ItemStack>(input); input2.put(15, new ItemStack(Blocks.chest));
                         * input2.put(16, null); input2.put(17, new ItemStack(Blocks.chest));
                         * RecipeUtil.addRocketBenchRecipe(new ItemStack(GCItems.rocketTier1, 1, 2), input2); input2 =
                         * new HashMap<Integer, ItemStack>(input); input2.put(15, null); input2.put(16, new
                         * ItemStack(Blocks.chest)); input2.put(17, new ItemStack(Blocks.chest));
                         * RecipeUtil.addRocketBenchRecipe(new ItemStack(GCItems.rocketTier1, 1, 2), input2); input2 =
                         * new HashMap<Integer, ItemStack>(input); input2.put(15, new ItemStack(Blocks.chest));
                         * input2.put(16, new ItemStack(Blocks.chest)); input2.put(17, new ItemStack(Blocks.chest));
                         * RecipeUtil.addRocketBenchRecipe(new ItemStack(GCItems.rocketTier1, 1, 3), input2);
                         */

        //

        input = new HashMap<>();

        input.put(1, new ItemStack(GCItems.basicItem, 1, 19));
        input.put(2, new ItemStack(GCItems.partBuggy, 1, 1));
        if (GalacticraftCore.isGalaxySpaceLoaded) {
            input.put(3, GT_ModHandler.getModItem(Constants.MOD_ID_GALAXYSPACE, "item.RocketControlComputer", 1, 100));
        }
        for (int i = 4; i <= 7; i++) {
            input.put(i, new ItemStack(GCItems.partBuggy));
        }
        for (int i = 8; i <= 11; i++) {
            input.put(i, GT_OreDictUnificator.get(OrePrefixes.stick, Materials.StainlessSteel, 1));
        }
        for (int i = 12; i <= 16; i++) {
            input.put(i, new ItemStack(GCItems.meteoricIronIngot, 1, 1));
        }
        for (int i = 17; i <= 24; i++) {
            input.put(i, GT_OreDictUnificator.get(OrePrefixes.screw, Materials.StainlessSteel, 1));
        }
        for (int i = 25; i <= 34; i++) {
            input.put(i, new ItemStack(GCItems.heavyPlatingTier1));
        }

        input2 = new HashMap<>(input);
        input2.put(35, null);
        RecipeUtil.addBuggyBenchRecipe(new ItemStack(GCItems.buggy, 1, 0), input2);

        input2 = new HashMap<>(input);
        input2.put(35, RecipeUtil.getChestItemStack(1, 3));
        RecipeUtil.addBuggyBenchRecipe(new ItemStack(GCItems.buggy, 1, 1), input2);

        input2 = new HashMap<>(input);
        input2.put(35, RecipeUtil.getChestItemStack(1, 0));
        RecipeUtil.addBuggyBenchRecipe(new ItemStack(GCItems.buggy, 1, 2), input2);

        input2 = new HashMap<>(input);
        input2.put(35, RecipeUtil.getChestItemStack(1, 1));
        RecipeUtil.addBuggyBenchRecipe(new ItemStack(GCItems.buggy, 1, 3), input2);

        aluminumIngots.addAll(OreDictionary.getOres("ingotAluminum"));
        final ArrayList<ItemStack> addedList = new ArrayList<>();
        for (final ItemStack ingotNew : OreDictionary.getOres("ingotAluminium")) {
            boolean flag = false;
            for (final ItemStack ingotDone : aluminumIngots) {
                if (ItemStack.areItemStacksEqual(ingotNew, ingotDone)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                addedList.add(ingotNew);
                OreDictionary.registerOre("ingotAluminum", ingotNew);
            }
        }
        if (addedList.size() > 0) {
            aluminumIngots.addAll(addedList);
            addedList.clear();
        }
        for (final ItemStack ingotNew : OreDictionary.getOres("ingotNaturalAluminum")) {
            for (final ItemStack ingotDone : aluminumIngots) {
                if (!ItemStack.areItemStacksEqual(ingotNew, ingotDone)) {
                    addedList.add(ingotNew);
                }
            }
        }
        if (addedList.size() > 0) {
            aluminumIngots.addAll(addedList);
        }

        final HashMap<Object, Integer> inputMap = new HashMap<>();
        inputMap.put(new ItemStack(GCBlocks.basicBlock, 1, 4), 231);
        inputMap.put(new ItemStack(Blocks.glass_pane), 6);
        inputMap.put("circuitAdvanced", 4);
        inputMap.put(new ItemStack(GregTech_API.sBlockMachines, 1, 13), 1); // HV Machine Hull
        GalacticraftRegistry.registerSpaceStation(
                new SpaceStationType(ConfigManagerCore.idDimensionOverworldOrbit, 0, new SpaceStationRecipe(inputMap)));

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.aluminumWire, 6),
                new Object[] { "WWW", "CCC", "WWW", 'W', Blocks.wool, 'C', "ingotAluminum" });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.aluminumWire, 1, 1),
                new Object[] { "X", "Y", "Z", 'X', Blocks.wool, 'Y', new ItemStack(GCBlocks.aluminumWire, 1), 'Z',
                        "ingotAluminum" });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.aluminumWire, 1, 1),
                new Object[] { "Z", "Y", "X", 'X', Blocks.wool, 'Y', new ItemStack(GCBlocks.aluminumWire, 1), 'Z',
                        "ingotAluminum" });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.machineBase, 1, 0),
                new Object[] { "WWW", "XZX", "XYX", 'W', "ingotCopper", 'X', Items.iron_ingot, 'Y',
                        new ItemStack(GCBlocks.aluminumWire, 1, 0), 'Z', Blocks.furnace });
        // Energy Storage Module:
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.machineTiered, 1, 0),
                new Object[] { "SSS", "BBB", "SSS", 'B',
                        new ItemStack(GCItems.battery, 1, GCItems.battery.getMaxDamage()), 'S', "compressedSteel" });
        // Electric Furnace:
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.machineTiered, 1, 4),
                new Object[] { "XXX", "XZX", "WYW", 'W', "compressedAluminum", 'X', "compressedSteel", 'Y',
                        "waferBasic", 'Z', Blocks.furnace });
        if (GalacticraftCore.isPlanetsLoaded) {
            // Energy Storage Cluster:
            RecipeUtil.addRecipe(
                    new ItemStack(GCBlocks.machineTiered, 1, 8),
                    new Object[] { "BSB", "SWS", "BSB", 'B', new ItemStack(GCBlocks.machineTiered, 1, 0), 'S',
                            "compressedSteel", 'W', "waferAdvanced" });
            // Electric Arc Furnace:
            RecipeUtil.addRecipe(
                    new ItemStack(GCBlocks.machineTiered, 1, 12),
                    new Object[] { "XXX", "XZX", "WYW", 'W', meteoricIronIngot, 'X',
                            new ItemStack(GCItems.heavyPlatingTier1), 'Y', "waferAdvanced", 'Z',
                            new ItemStack(GCBlocks.machineTiered, 1, 4) });
        }
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.machineBase, 1, 12),
                new Object[] { "WXW", "WYW", "WZW", 'W', "ingotAluminum", 'X', Blocks.anvil, 'Y', "ingotCopper", 'Z',
                        "waferBasic" });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.machineBase2, 1, 0),
                new Object[] { "WXW", "WYW", "VZV", 'V', new ItemStack(GCBlocks.aluminumWire), 'W', "compressedSteel",
                        'X', Blocks.anvil, 'Y', "compressedBronze", 'Z', "waferAdvanced" });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.machineBase2, 1, 4),
                new Object[] { "WXW", "UYU", "VZV", 'U', Blocks.stone_button, 'V', new ItemStack(GCBlocks.aluminumWire),
                        'W', "ingotAluminum", 'X', Blocks.lever, 'Y', Blocks.furnace, 'Z', Blocks.redstone_torch });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.machineBase2, 1, 8),
                new Object[] { "SSS", "BBB", "SSS", 'B',
                        new ItemStack(GCItems.oxTankHeavy, 1, GCItems.oxTankHeavy.getMaxDamage()), 'S',
                        "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.battery, 1, 100),
                new Object[] { " T ", "TRT", "TCT", 'T', "compressedTin", 'R', Items.redstone, 'C', Items.coal });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.rocketEngine, 1),
                new Object[] { " YV", "XWX", "XZX", 'V', Blocks.stone_button, 'W',
                        new ItemStack(GCItems.canister, 1, 0), 'X', GCItems.heavyPlatingTier1, 'Y',
                        Items.flint_and_steel, 'Z', GCItems.oxygenVent });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.rocketEngine, 1),
                new Object[] { "VY ", "XWX", "XZX", 'V', Blocks.stone_button, 'W',
                        new ItemStack(GCItems.canister, 1, 0), 'X', GCItems.heavyPlatingTier1, 'Y',
                        Items.flint_and_steel, 'Z', GCItems.oxygenVent });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.partNoseCone, 1),
                new Object[] { " Y ", " X ", "X X", 'X', GCItems.heavyPlatingTier1, 'Y', Blocks.redstone_torch });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.oxygenPipe, 6),
                new Object[] { "XXX", "   ", "XXX", 'X', Blocks.glass_pane });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.oxTankLight, 1, GCItems.oxTankLight.getMaxDamage()),
                new Object[] { "YZY", "YXY", "YYY", 'X', new ItemStack(GCItems.canister, 1, 0), 'Y',
                        "compressedAluminum", 'Z', GCBlocks.oxygenPipe });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.oxTankLight, 1, GCItems.oxTankLight.getMaxDamage()),
                new Object[] { "YZY", "YXY", "YYY", 'X', new ItemStack(GCItems.canister, 1, 1), 'Y',
                        "compressedAluminum", 'Z', GCBlocks.oxygenPipe });

        // RecipeUtil.addRecipe(new ItemStack(GCItems.oxTankMedium, 1,
        // GCItems.oxTankMedium.getMaxDamage()), new
        // Object[] { "ZZ", "XX", "YY", 'X', new ItemStack(GCItems.canister, 1, 0), 'Y',
        // "compressedMeteoricIron", 'Z',
        // GCBlocks.oxygenPipe});

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.sensorGlasses, 1),
                new Object[] { "ZWZ", "Z Z", "XYX", 'W', Items.diamond, 'X', GCItems.sensorLens, 'Y', meteoricIronIngot,
                        'Z', Items.string });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.sensorLens, 1),
                new Object[] { "ZXZ", "XYX", "ZXZ", 'X', Blocks.glass_pane, 'Y', meteoricIronPlate, 'Z',
                        Items.redstone });

        if (!ConfigManagerCore.alternateCanisterRecipe) {
            RecipeUtil.addRecipe(
                    new ItemStack(GCItems.canister, 2, 0),
                    new Object[] { "X X", "X X", "XXX", 'X', "ingotTin" });
            RecipeUtil.addRecipe(
                    new ItemStack(GCItems.canister, 2, 1),
                    new Object[] { "X X", "X X", "XXX", 'X', "ingotCopper" });
        } else {
            RecipeUtil.addRecipe(
                    new ItemStack(GCItems.canister, 2, 0),
                    new Object[] { "XXX", "X  ", "XXX", 'X', "ingotTin" });
            RecipeUtil.addRecipe(
                    new ItemStack(GCItems.canister, 2, 1),
                    new Object[] { "XXX", "X  ", "XXX", 'X', "ingotCopper" });
        }

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.oxMask, 1),
                new Object[] { "XXX", "XYX", "XXX", 'X', Blocks.glass_pane, 'Y', Items.iron_helmet });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.canvas, 1),
                new Object[] { " XY", "XXX", "YX ", 'Y', Items.stick, 'X', Items.string });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.parachute, 1, 0),
                new Object[] { "XXX", "Y Y", " Y ", 'X', GCItems.canvas, 'Y', Items.string });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.sealableBlock, 1, 1),
                new Object[] { "XYX", 'Y', GCBlocks.oxygenPipe, 'X', new ItemStack(GCBlocks.basicBlock, 1, 4) });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.sealableBlock, 1, 14),
                new Object[] { "XYX", 'Y', new ItemStack(GCBlocks.aluminumWire, 1, 0), 'X',
                        new ItemStack(GCBlocks.basicBlock, 1, 4) });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.sealableBlock, 1, 15),
                new Object[] { "XYX", 'Y', new ItemStack(GCBlocks.aluminumWire, 1, 1), 'X',
                        new ItemStack(GCBlocks.basicBlock, 1, 4) });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.oxygenGear),
                new Object[] { " Y ", "YXY", "Y Y", 'X', GCItems.oxygenConcentrator, 'Y', GCBlocks.oxygenPipe });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.basicBlock, 4, 3),
                new Object[] { "   ", " XY", "   ", 'X', new ItemStack(Blocks.stone, 4, 0), 'Y', "compressedTin" });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.basicBlock, 4, 4),
                new Object[] { "   ", " X ", " Y ", 'X', new ItemStack(Blocks.stone, 4, 0), 'Y', "compressedTin" });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.flag),
                new Object[] { "XYY", "XYY", "X  ", 'X', GCItems.flagPole, 'Y', GCItems.canvas });

        for (int var2 = 0; var2 < 16; ++var2) {
            CraftingManager.getInstance().addShapelessRecipe(
                    new ItemStack(GCItems.parachute, 1, ItemParaChute.getParachuteDamageValueFromDye(var2)),
                    new ItemStack(Items.dye, 1, var2),
                    new ItemStack(GCItems.parachute, 1, 0));
        }

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.partFins, 1),
                new Object[] { " Y ", "XYX", "X X", 'X', GCItems.heavyPlatingTier1, 'Y', "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.landingPad, 9, 0),
                new Object[] { "YYY", "XXX", 'X', Blocks.iron_block, 'Y', "compressedIron" });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.landingPad, 9, 1),
                new Object[] { "YYY", "XXX", 'X', Blocks.iron_block, 'Y', "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.partBuggy, 1, 0),
                new Object[] { " W ", "WXW", " W ", 'W', Items.leather, 'X', "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.partBuggy, 1, 1),
                new Object[] { "  Y", " ZY", "XXX", 'X', "compressedSteel", 'Y', "compressedSteel", 'Z',
                        "compressedIron" });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.partBuggy, 1, 2),
                new Object[] { "XXX", "YZY", "XXX", 'X', "compressedSteel", 'Y', "compressedIron", 'Z', Blocks.chest });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.oxygenDetector, 1),
                new Object[] { "WWW", "YVY", "ZUZ", 'U', "compressedAluminum", 'V', "waferBasic", 'W',
                        "compressedSteel", 'X', GCItems.oxygenFan, 'Y', GCItems.oxygenVent, 'Z', Items.redstone });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.oxygenDistributor, 1),
                new Object[] { "WXW", "YZY", "WXW", 'W', "compressedSteel", 'X', GCItems.oxygenFan, 'Y',
                        GCItems.oxygenVent, 'Z', "compressedAluminum" });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.oxygenSealer, 1),
                new Object[] { "UZU", "YXY", "UZU", 'U', "compressedAluminum", 'V', GCBlocks.aluminumWire, 'W',
                        "compressedSteel", 'X', GCItems.oxygenFan, 'Y', GCItems.oxygenVent, 'Z', "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.oxygenCollector, 1),
                new Object[] { "WWW", "YXZ", "UVU", 'U', "compressedAluminum", 'V', GCItems.oxygenConcentrator, 'W',
                        "compressedSteel", 'X', new ItemStack(GCItems.canister, 1, 0), 'Y', GCItems.oxygenFan, 'Z',
                        GCItems.oxygenVent });

        // Handled by Galaxy Space
        // RecipeUtil.addRecipe(new ItemStack(GCBlocks.nasaWorkbench, 1), new Object[] {
        // "XZX", "UWU", "YVY", 'U',
        // Blocks.lever, 'V', Blocks.redstone_torch, 'W', "waferAdvanced", 'X',
        // "compressedSteel", 'Y',
        // "compressedSteel", 'Z', Blocks.crafting_table });

        // RecipeUtil.addRecipe(new ItemStack(GCItems.oxTankHeavy, 1,
        // GCItems.oxTankHeavy.getMaxDamage()), new Object[]
        // { "ZZZ", "XXX", "YYY", 'X', new ItemStack(GCItems.canister, 1, 0), 'Y',
        // "compressedSteel", 'Z', new
        // ItemStack(Blocks.wool, 1, 14) });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.oxygenFan, 1),
                new Object[] { "Z Z", " Y ", "ZXZ", 'X', Items.redstone, 'Y', "waferBasic", 'Z', "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.oxygenConcentrator, 1),
                new Object[] { "ZWZ", "WYW", "ZXZ", 'W', "compressedTin", 'X', GCItems.oxygenVent, 'Y',
                        new ItemStack(GCItems.canister, 1, 0), 'Z', "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.steelPickaxe, 1),
                new Object[] { "YYY", " X ", " X ", 'Y', "compressedSteel", 'X', Items.stick });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.steelAxe, 1),
                new Object[] { "YY ", "YX ", " X ", 'Y', "compressedSteel", 'X', Items.stick });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.steelAxe, 1),
                new Object[] { " YY", " XY", " X ", 'Y', "compressedSteel", 'X', Items.stick });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.steelHoe, 1),
                new Object[] { " YY", " X ", " X ", 'Y', "compressedSteel", 'X', Items.stick });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.steelHoe, 1),
                new Object[] { "YY ", " X ", " X ", 'Y', "compressedSteel", 'X', Items.stick });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.steelSpade, 1),
                new Object[] { " Y ", " X ", " X ", 'Y', "compressedSteel", 'X', Items.stick });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.steelSword, 1),
                new Object[] { " Y ", " Y ", " X ", 'Y', "compressedSteel", 'X', Items.stick });

        RecipeUtil
                .addRecipe(new ItemStack(GCItems.steelBoots, 1), new Object[] { "X X", "X X", 'X', "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.steelChestplate, 1),
                new Object[] { "X X", "XXX", "XXX", 'X', "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.steelLeggings, 1),
                new Object[] { "XXX", "X X", "X X", 'X', "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.steelHelmet, 1),
                new Object[] { "XXX", "X X", 'X', "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.flagPole, 2, 0),
                new Object[] { "X", "X", "X", 'X', "compressedSteel" });

        CraftingManager.getInstance().getRecipeList().add(
                new ShapelessOreRecipe(
                        new ItemStack(GCItems.oxygenVent, 1),
                        "compressedTin",
                        "compressedTin",
                        "compressedTin",
                        "compressedSteel"));

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.airLockFrame, 4, 0),
                new Object[] { "XXX", "YZY", "XXX", 'X', "compressedAluminum", 'Y', "compressedSteel", 'Z',
                        GCItems.oxygenConcentrator });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.airLockFrame, 1, 1),
                new Object[] { "YYY", "WZW", "YYY", 'W', meteoricIronPlate, 'Y', "compressedSteel", 'Z',
                        new ItemStack(GCItems.basicItem, 1, 13) });

        // Disable oil extractor:
        // RecipeUtil.addRecipe(new ItemStack(GCItems.oilExtractor), new Object[] { "X
        // ", " XY", "ZYY", 'X',
        // "compressedSteel", 'Y', "compressedBronze", 'Z', Items.redstone });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.basicItem, 1, 20),
                new Object[] { "WVW", "YXY", "YZY", 'X', "compressedSteel", 'Y', "compressedBronze", 'Z', "waferBasic",
                        'W', Items.redstone, 'V', GCItems.oxygenVent });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.oilCanister, 1, GCItems.oilCanister.getMaxDamage()),
                new Object[] { "WXW", "WYW", "WZW", 'X', "compressedSteel", 'Y', Blocks.glass, 'Z',
                        new ItemStack(GCItems.canister, 1, 0), 'W', "compressedTin" });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.refinery),
                new Object[] { " Z ", "WZW", "XYX", 'X', "compressedSteel", 'Y', Blocks.furnace, 'Z',
                        new ItemStack(GCItems.canister, 1, 1), 'W', Blocks.stone });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.oxygenCompressor, 1, 0),
                new Object[] { "XWX", "WZW", "XYX", 'W', "compressedAluminum", 'X', "compressedSteel", 'Y',
                        "compressedBronze", 'Z', GCItems.oxygenConcentrator });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.oxygenCompressor, 1, 4),
                new Object[] { "XVX", "WZW", "XYX", 'V', GCItems.oxygenFan, 'W', "compressedAluminum", 'X',
                        "compressedSteel", 'Y', Blocks.redstone_torch, 'Z', GCItems.oxygenConcentrator });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.fuelLoader),
                new Object[] { "XXX", "XZX", "WYW", 'W', "compressedAluminum", 'X', "compressedSteel", 'Y',
                        "waferBasic", 'Z', new ItemStack(GCItems.canister, 1, 0) });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.basicItem, 2, 0),
                new Object[] { "XXX", "YYY", "ZZZ", 'X', Blocks.glass, 'Y', "waferSolar", 'Z',
                        new ItemStack(GCBlocks.aluminumWire, 1, 0) });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.basicItem, 1, 1),
                new Object[] { "XXX", "YYY", "XXX", 'X', new ItemStack(GCItems.basicItem, 1, 0), 'Y',
                        new ItemStack(GCBlocks.aluminumWire, 1, 0) });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.solarPanel, 1, 0),
                new Object[] { "XYX", "XZX", "VWV", 'V', new ItemStack(GCBlocks.aluminumWire, 1, 0), 'W', "waferBasic",
                        'X', "compressedSteel", 'Y', new ItemStack(GCItems.basicItem, 1, 1), 'Z', GCItems.flagPole });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.solarPanel, 1, 4),
                new Object[] { "XYX", "XZX", "VWV", 'V', new ItemStack(GCBlocks.aluminumWire, 1, 1), 'W',
                        "waferAdvanced", 'X', "compressedSteel", 'Y', new ItemStack(GCItems.basicItem, 1, 1), 'Z',
                        GCItems.flagPole });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.cargoLoader, 1, 0),
                new Object[] { "XWX", "YZY", "XXX", 'W', Blocks.hopper, 'X', "compressedSteel", 'Y',
                        "compressedAluminum", 'Z', Blocks.chest });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.cargoLoader, 1, 4),
                new Object[] { "XXX", "YZY", "XWX", 'W', Blocks.hopper, 'X', "compressedSteel", 'Y',
                        "compressedAluminum", 'Z', Blocks.chest });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.glowstoneTorch, 4),
                new Object[] { "Y", "X", 'X', Items.stick, 'Y', Items.glowstone_dust });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.basicItem, 1, 19),
                new Object[] { " X ", "YUY", "ZWZ", 'U', Items.repeater, 'W', "waferBasic", 'X', "compressedAluminum",
                        'Y', "compressedIron", 'Z', Items.redstone });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.wrench),
                new Object[] { "  Y", " X ", "X  ", 'X', "compressedBronze", 'Y', "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(Blocks.lit_pumpkin),
                new Object[] { "P  ", "T  ", "   ", 'P', new ItemStack(Blocks.pumpkin), 'T',
                        new ItemStack(GCBlocks.unlitTorch) });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.brightLamp),
                new Object[] { "XYX", "YZY", "XYX", 'X', deshIngot, 'Y', Items.glowstone_dust, 'Z',
                        new ItemStack(GCItems.battery, 1, 0) });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.spinThruster),
                new Object[] { "   ", "YWZ", "PXP", 'W', "waferAdvanced", 'X', meteoricIronIngot, 'Y',
                        new ItemStack(GCItems.fuelCanister, 1, 1), 'Z', new ItemStack(GCItems.rocketEngine, 1, 0), 'P',
                        "compressedSteel" });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.screen),
                new Object[] { "XYX", "YGY", "XYX", 'X', "compressedSteel", 'Y', "waferBasic", 'G', Blocks.glass });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.telemetry),
                new Object[] { "XFX", "XWX", "YYY", 'W', "waferBasic", 'X', "compressedTin", 'Y', "compressedCopper",
                        'F', new ItemStack(GCItems.basicItem, 1, 19) });

        RecipeUtil.addBlockRecipe(
                new ItemStack(GCBlocks.basicBlock, 1, 9),
                "ingotCopper",
                new ItemStack(GCItems.basicItem, 1, 3));

        RecipeUtil.addBlockRecipe(
                new ItemStack(GCBlocks.basicBlock, 1, 10),
                "ingotTin",
                new ItemStack(GCItems.basicItem, 1, 4));

        RecipeUtil.addBlockRecipe(
                new ItemStack(GCBlocks.basicBlock, 1, 11),
                "ingotAluminum",
                new ItemStack(GCItems.basicItem, 1, 5));

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.basicBlock, 1, 12),
                new Object[] { "YYY", "YYY", "YYY", 'Y', meteoricIronIngot });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.basicItem, 9, 3),
                new Object[] { "X", 'X', new ItemStack(GCBlocks.basicBlock, 1, 9) });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.basicItem, 9, 4),
                new Object[] { "X", 'X', new ItemStack(GCBlocks.basicBlock, 1, 10) });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.basicItem, 9, 5),
                new Object[] { "X", 'X', new ItemStack(GCBlocks.basicBlock, 1, 11) });

        RecipeUtil.addRecipe(
                new ItemStack(GCItems.meteoricIronIngot, 9, 0),
                new Object[] { "X", 'X', new ItemStack(GCBlocks.basicBlock, 1, 12) });

        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.cheeseBlock, 1),
                new Object[] { "YYY", "YXY", "YYY", 'X', Items.milk_bucket, 'Y', GCItems.cheeseCurd });

        // Tin Stairs 1
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.tinStairs1, 4),
                new Object[] { "  X", " XX", "XXX", 'X', new ItemStack(GCBlocks.basicBlock, 1, 4) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.tinStairs1, 4),
                new Object[] { "X  ", "XX ", "XXX", 'X', new ItemStack(GCBlocks.basicBlock, 1, 4) });

        // Tin Stairs 2
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.tinStairs2, 4),
                new Object[] { "  X", " XX", "XXX", 'X', new ItemStack(GCBlocks.basicBlock, 1, 3) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.tinStairs2, 4),
                new Object[] { "X  ", "XX ", "XXX", 'X', new ItemStack(GCBlocks.basicBlock, 1, 3) });

        // Moon Stone Stairs
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.moonStoneStairs, 4),
                new Object[] { "  X", " XX", "XXX", 'X', new ItemStack(GCBlocks.blockMoon, 1, 4) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.moonStoneStairs, 4),
                new Object[] { "X  ", "XX ", "XXX", 'X', new ItemStack(GCBlocks.blockMoon, 1, 4) });

        // Moon Dungeon Brick Stairs
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.moonBricksStairs, 4),
                new Object[] { "  X", " XX", "XXX", 'X', new ItemStack(GCBlocks.blockMoon, 1, 14) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.moonBricksStairs, 4),
                new Object[] { "X  ", "XX ", "XXX", 'X', new ItemStack(GCBlocks.blockMoon, 1, 14) });

        // Slab Block
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.slabGCHalf, 6, 0),
                new Object[] { "XXX", 'X', new ItemStack(GCBlocks.basicBlock, 1, 4) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.slabGCHalf, 6, 1),
                new Object[] { "XXX", 'X', new ItemStack(GCBlocks.basicBlock, 1, 3) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.slabGCHalf, 6, 2),
                new Object[] { "XXX", 'X', new ItemStack(GCBlocks.blockMoon, 1, 4) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.slabGCHalf, 6, 3),
                new Object[] { "XXX", 'X', new ItemStack(GCBlocks.blockMoon, 1, 14) });

        // Wall Block
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.wallGC, 6, 0),
                new Object[] { "XXX", "XXX", 'X', new ItemStack(GCBlocks.basicBlock, 1, 4) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.wallGC, 6, 1),
                new Object[] { "XXX", "XXX", 'X', new ItemStack(GCBlocks.basicBlock, 1, 3) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.wallGC, 6, 2),
                new Object[] { "XXX", "XXX", 'X', new ItemStack(GCBlocks.blockMoon, 1, 4) });

        // Dungeon Brick Wall
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.wallGC, 6, 3),
                new Object[] { "XXX", "XXX", 'X', new ItemStack(GCBlocks.blockMoon, 1, 14) });

        CraftingManager.getInstance().getRecipeList().add(
                new ShapelessOreRecipe(
                        new ItemStack(GCItems.basicItem, 1, 15),
                        new ItemStack(GCItems.canister, 1, 0),
                        Items.apple,
                        Items.apple));

        CraftingManager.getInstance().getRecipeList().add(
                new ShapelessOreRecipe(
                        new ItemStack(GCItems.basicItem, 1, 16),
                        new ItemStack(GCItems.canister, 1, 0),
                        Items.carrot,
                        Items.carrot));

        CraftingManager.getInstance().getRecipeList().add(
                new ShapelessOreRecipe(
                        new ItemStack(GCItems.basicItem, 1, 17),
                        new ItemStack(GCItems.canister, 1, 0),
                        Items.melon,
                        Items.melon));

        CraftingManager.getInstance().getRecipeList().add(
                new ShapelessOreRecipe(
                        new ItemStack(GCItems.basicItem, 1, 18),
                        new ItemStack(GCItems.canister, 1, 0),
                        Items.potato,
                        Items.potato));

        CraftingManager.getInstance().getRecipeList()
                .add(new ShapelessOreRecipe(new ItemStack(GCItems.meteorChunk, 3), GCItems.meteoricIronRaw));

        for (int i = 3; i < 6; i++) {
            if (ItemBasic.names[i].contains("ingot")) {
                CompressorRecipes.addShapelessRecipe(
                        new ItemStack(GCItems.basicItem, 1, i + 3),
                        ItemBasic.names[i],
                        ItemBasic.names[i]);
            }
        }

        /*
         * // Support for all the spellings of Aluminum for (ItemStack stack : aluminumIngots) {
         * CompressorRecipes.addShapelessRecipe(new ItemStack(GCItems.basicItem, 1, 8), stack, stack); }
         */
        if (OreDictionary.getOres("ingotBronze").size() > 0) {
            CompressorRecipes.addShapelessRecipe(new ItemStack(GCItems.basicItem, 1, 10), "ingotBronze", "ingotBronze");
        }

        CompressorRecipes.addShapelessRecipe(
                new ItemStack(GCItems.basicItem, 1, 10),
                new ItemStack(GCItems.basicItem, 1, 6),
                new ItemStack(GCItems.basicItem, 1, 7));
        CompressorRecipes
                .addShapelessRecipe(new ItemStack(GCItems.basicItem, 1, 11), Items.iron_ingot, Items.iron_ingot);
        CompressorRecipes.addShapelessRecipe(new ItemStack(GCItems.meteoricIronIngot, 1, 1), meteoricIronIngot);
        CompressorRecipes.addRecipe(
                new ItemStack(GCItems.heavyPlatingTier1, 2, 0),
                "XYZ",
                "XYZ",
                'X',
                new ItemStack(GCItems.basicItem, 1, 9),
                'Y',
                new ItemStack(GCItems.basicItem, 1, 8),
                'Z',
                new ItemStack(GCItems.basicItem, 1, 10));
    }

    public static void setConfigurableRecipes() {
        final ItemStack solarPanels = new ItemStack(GCItems.basicItem, 9, 12);
        final ItemStack basicWafers = new ItemStack(GCItems.basicItem, 3, 13);
        final ItemStack advancedWafers = new ItemStack(GCItems.basicItem, 1, 14);

        CircuitFabricatorRecipes.removeRecipe(solarPanels);
        CircuitFabricatorRecipes.removeRecipe(basicWafers);
        CircuitFabricatorRecipes.removeRecipe(advancedWafers);
        final ArrayList<ItemStack> silicons = OreDictionary.getOres(ConfigManagerCore.otherModsSilicon);
        final int siliconCount = silicons.size();
        for (int j = 0; j <= siliconCount; j++) {
            ItemStack silicon;
            if (j == 0) {
                silicon = new ItemStack(GCItems.basicItem, 1, 2);
            } else {
                silicon = silicons.get(j - 1);
                if (silicon.getItem() == GCItems.basicItem && silicon.getItemDamage() == 2) {
                    continue;
                }
            }
            CircuitFabricatorRecipes.addRecipe(
                    solarPanels,
                    new ItemStack[] { new ItemStack(Items.diamond), silicon, silicon, new ItemStack(Items.redstone),
                            new ItemStack(Items.dye, 1, 4) });
            CircuitFabricatorRecipes.addRecipe(
                    basicWafers,
                    new ItemStack[] { new ItemStack(Items.diamond), silicon, silicon, new ItemStack(Items.redstone),
                            new ItemStack(Blocks.redstone_torch) });
            CircuitFabricatorRecipes.addRecipe(
                    advancedWafers,
                    new ItemStack[] { new ItemStack(Items.diamond), silicon, silicon, new ItemStack(Items.redstone),
                            new ItemStack(Items.repeater) });
        }

        CompressorRecipes.removeRecipe(new ItemStack(GCItems.basicItem, 1, 9));
        boolean steelDone = false;
        if (OreDictionary.getOres("ingotSteel").size() > 0) {
            CompressorRecipes.addShapelessRecipe(new ItemStack(GCItems.basicItem, 1, 9), "ingotSteel", "ingotSteel");
            steelDone = true;
        }
        if (!ConfigManagerCore.hardMode || !steelDone) {
            CompressorRecipes.addShapelessRecipe(
                    new ItemStack(GCItems.basicItem, 1, 9),
                    Items.coal,
                    new ItemStack(GCItems.basicItem, 1, 11),
                    Items.coal);
        } else {
            CompressorRecipes.addShapelessAdventure(
                    new ItemStack(GCItems.basicItem, 1, 9),
                    Items.coal,
                    new ItemStack(GCItems.basicItem, 1, 11),
                    Items.coal);
        }
    }

    private static void addBuildCraftCraftingRecipes() {
        // boolean refineryDone = false;
        // boolean newBCAPI = false;
        // try
        // {
        // Class<?> clazz =
        // Class.forName("buildcraft.api.recipes.IRefineryRecipeManager");
        // Method[] mzz = clazz.getMethods();
        // for (Method m : mzz)
        // {
        // if (m.getName().equals("addRecipe"))
        // {
        // if (m.getParameterTypes()[0].equals(String.class))
        // {
        // newBCAPI = true;
        // break;
        // }
        // }
        // }
        //
        // if (newBCAPI)
        // {
        // //Newer Buildcraft API versions
        // BuildcraftRecipeRegistry.refinery.addRecipe("buildcraft:fuel", new
        // FluidStack(GalacticraftCore.gcFluidOil, 1), new
        // FluidStack(FluidRegistry.getFluid("fuel"), 1), 120, 1);
        // refineryDone = true;
        // }
        // else
        // {
        // //Older Buildcraft API versions
        // BuildcraftRecipes.refinery.addRecipe(new
        // FluidStack(GalacticraftCore.gcFluidOil, 1), new
        // FluidStack(FluidRegistry.getFluid("fuel"), 1), 120, 1);
        // refineryDone = true;
        // }
        // }
        // catch (Exception e) { }
        //
        // if (refineryDone)
        // GCLog.info("Successfully added GC oil to Buildcraft Refinery recipes.");

        try {
            final Class<?> clazz = Class.forName("buildcraft.BuildCraftTransport");

            final Object pipeItemsStone = clazz.getField("pipeItemsStone").get(null);
            final Object pipeItemsCobblestone = clazz.getField("pipeItemsCobblestone").get(null);
            final Object pipeFluidsCobblestone = clazz.getField("pipeFluidsCobblestone").get(null);
            final Object pipeFluidsStone = clazz.getField("pipeFluidsStone").get(null);
            final Object pipePowerStone = clazz.getField("pipePowerStone").get(null);
            final Object pipePowerGold = clazz.getField("pipePowerGold").get(null);

            RecipeUtil.addRecipe(
                    new ItemStack(GCBlocks.sealableBlock, 1, EnumEnclosedBlock.BC_ITEM_COBBLESTONEPIPE.getMetadata()),
                    new Object[] { "XYX", 'Y', pipeItemsCobblestone, 'X', new ItemStack(GCBlocks.basicBlock, 1, 4) });
            RecipeUtil.addRecipe(
                    new ItemStack(GCBlocks.sealableBlock, 1, EnumEnclosedBlock.BC_ITEM_STONEPIPE.getMetadata()),
                    new Object[] { "XYX", 'Y', pipeItemsStone, 'X', new ItemStack(GCBlocks.basicBlock, 1, 4) });
            RecipeUtil.addRecipe(
                    new ItemStack(GCBlocks.sealableBlock, 1, EnumEnclosedBlock.BC_FLUIDS_COBBLESTONEPIPE.getMetadata()),
                    new Object[] { "XYX", 'Y', pipeFluidsCobblestone, 'X', new ItemStack(GCBlocks.basicBlock, 1, 4) });
            RecipeUtil.addRecipe(
                    new ItemStack(GCBlocks.sealableBlock, 1, EnumEnclosedBlock.BC_FLUIDS_STONEPIPE.getMetadata()),
                    new Object[] { "XYX", 'Y', pipeFluidsStone, 'X', new ItemStack(GCBlocks.basicBlock, 1, 4) });
            RecipeUtil.addRecipe(
                    new ItemStack(GCBlocks.sealableBlock, 1, EnumEnclosedBlock.BC_POWER_STONEPIPE.getMetadata()),
                    new Object[] { "XYX", 'Y', pipePowerStone, 'X', new ItemStack(GCBlocks.basicBlock, 1, 4) });
            RecipeUtil.addRecipe(
                    new ItemStack(GCBlocks.sealableBlock, 1, EnumEnclosedBlock.BC_POWER_GOLDPIPE.getMetadata()),
                    new Object[] { "XYX", 'Y', pipePowerGold, 'X', new ItemStack(GCBlocks.basicBlock, 1, 4) });

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void addIndustrialCraft2Recipes() {
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.sealableBlock, 1, EnumEnclosedBlock.IC2_COPPER_CABLE.getMetadata()),
                new Object[] { "XYX", 'Y', RecipeUtil.getIndustrialCraftItem("insulatedCopperCableItem"), 'X',
                        new ItemStack(GCBlocks.basicBlock, 1, 4) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.sealableBlock, 1, EnumEnclosedBlock.IC2_GOLD_CABLE.getMetadata()),
                new Object[] { "XYX", 'Y', RecipeUtil.getIndustrialCraftItem("insulatedGoldCableItem"), 'X',
                        new ItemStack(GCBlocks.basicBlock, 1, 4) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.sealableBlock, 1, 4),
                new Object[] { "XYX", 'Y', RecipeUtil.getIndustrialCraftItem("insulatedIronCableItem"), 'X',
                        new ItemStack(GCBlocks.basicBlock, 1, 4) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.sealableBlock, 1, EnumEnclosedBlock.IC2_GLASS_FIBRE_CABLE.getMetadata()),
                new Object[] { "XYX", 'Y', RecipeUtil.getIndustrialCraftItem("glassFiberCableItem"), 'X',
                        new ItemStack(GCBlocks.basicBlock, 1, 4) });
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.sealableBlock, 1, EnumEnclosedBlock.IC2_LV_CABLE.getMetadata()),
                new Object[] { "XYX", 'Y', RecipeUtil.getIndustrialCraftItem("insulatedTinCableItem"), 'X',
                        new ItemStack(GCBlocks.basicBlock, 1, 4) });

        // try
        // {
        // Class<?> clazz = Class.forName("ic2.core.Ic2Items");
        //
        // Object copperDustObject =
        // clazz.getField("crushedCopperOre").get(null);
        // ItemStack copperDustItemStack = (ItemStack) copperDustObject;
        // Class<?> clazz2 =
        // Class.forName("ic2.api.recipe.RecipeInputItemStack");
        // Object o = clazz2.getConstructor(ItemStack.class).newInstance(new
        // ItemStack(GCCoreBlocks.blockMoon, 1, 0));
        // Method addRecipe =
        // Class.forName("ic2.api.recipe.IMachineRecipeManager").getMethod("addRecipe",
        // Class.forName("ic2.api.recipe.IRecipeInput"), NBTTagCompound.class,
        // ItemStack[].class);
        // addRecipe.invoke(Class.forName("ic2.api.recipe.Recipes").getField("macerator").get(null),
        // o, null, new ItemStack[] { new
        // ItemStack(copperDustItemStack.getItem(), 2,
        // copperDustItemStack.getItemDamage()) });
        //
        // Object tinDustObject = clazz.getField("crushedTinOre").get(null);
        // ItemStack tinDustItemStack = (ItemStack) tinDustObject;
        // o = clazz2.getConstructor(ItemStack.class).newInstance(new
        // ItemStack(GCCoreBlocks.blockMoon, 1, 1));
        // addRecipe.invoke(Class.forName("ic2.api.recipe.Recipes").getField("macerator").get(null),
        // o, null, new ItemStack[] { new ItemStack(tinDustItemStack.getItem(),
        // 2, tinDustItemStack.getItemDamage()) });
        // }
        // catch (Throwable e)
        // {
        // e.printStackTrace();
        // } TODO IC2 recipes
    }

    private static void addAppEngRecipes() {
        RecipeUtil.addRecipe(
                new ItemStack(GCBlocks.sealableBlock, 1, EnumEnclosedBlock.ME_CABLE.getMetadata()),
                new Object[] { "XYX", 'Y',
                        AEApi.instance().definitions().parts().cableGlass().stack(AEColor.Transparent, 1), 'X',
                        new ItemStack(GCBlocks.basicBlock, 1, 4) });
    }

    private static void addExNihiloRecipes() {
        try {
            final Class<?> registry = Class.forName("exnihilo.registries.HeatRegistry");
            final Method m = registry.getMethod("register", Block.class, float.class);
            m.invoke(null, GCBlocks.unlitTorchLit, 0.1F);
            for (final Block torch : GCBlocks.otherModTorchesLit) {
                m.invoke(null, torch, 0.1F);
            }
            GCLog.info("Successfully added space torches as heat sources for Ex Nihilo crucibles etc");
        } catch (final Throwable e) {}
    }
}
