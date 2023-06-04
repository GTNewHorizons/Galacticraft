package micdoodle8.mods.galacticraft.planets.asteroids;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.command.CommandGCAstroMiner;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric;
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe;
import micdoodle8.mods.galacticraft.core.util.RecipeUtil;
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets;
import micdoodle8.mods.galacticraft.planets.IPlanetsModule;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.TeleportTypeAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityEntryPod;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityGrapple;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntitySmallAsteroid;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityTier3Rocket;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.player.AsteroidsPlayerHandler;
import micdoodle8.mods.galacticraft.planets.asteroids.event.AsteroidsEventHandler;
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.ContainerAstroMinerDock;
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.ContainerShortRangeTelepad;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.asteroids.network.PacketSimpleAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.recipe.CanisterRecipes;
import micdoodle8.mods.galacticraft.planets.asteroids.recipe.RecipeManagerAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.schematic.SchematicAstroMiner;
import micdoodle8.mods.galacticraft.planets.asteroids.schematic.SchematicTier3Rocket;
import micdoodle8.mods.galacticraft.planets.asteroids.tick.AsteroidsTickHandlerServer;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityBeamReceiver;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityBeamReflector;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBaseSingle;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityShortRangeTelepad;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityTelepadFake;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityTreasureChestAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.ChunkProviderAsteroids;
import micdoodle8.mods.galacticraft.planets.mars.MarsModule;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.RecipeSorter;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import gregtech.api.util.GT_ModHandler;

public class AsteroidsModule implements IPlanetsModule {

    public static Planet planetAsteroids;
    private File GCPlanetsSource;

    public static final String ASSET_PREFIX = "galacticraftasteroids";
    public static final String TEXTURE_PREFIX = AsteroidsModule.ASSET_PREFIX + ":";

    public static AsteroidsPlayerHandler playerHandler;
    public static Fluid fluidMethaneGas;
    public static Fluid fluidOxygenGas;
    public static Fluid fluidNitrogenGas;
    public static Fluid fluidLiquidMethane;
    public static Fluid fluidLiquidOxygen;
    public static Fluid fluidLiquidNitrogen;
    public static Fluid fluidLiquidArgon;
    public static Fluid fluidAtmosphericGases;
    // public static Fluid fluidCO2Gas;

    private Fluid registerFluid(String fluidName, int density, int viscosity, int temperature, boolean gaseous) {
        Fluid returnFluid = FluidRegistry.getFluid(fluidName);
        if (returnFluid == null) {
            FluidRegistry.registerFluid(
                    new Fluid(fluidName).setDensity(density).setViscosity(viscosity).setTemperature(temperature)
                            .setGaseous(gaseous));
            returnFluid = FluidRegistry.getFluid(fluidName);
        }
        return returnFluid;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        this.GCPlanetsSource = event.getSourceFile();
        playerHandler = new AsteroidsPlayerHandler();
        MinecraftForge.EVENT_BUS.register(playerHandler);
        FMLCommonHandler.instance().bus().register(playerHandler);
        final AsteroidsEventHandler eventHandler = new AsteroidsEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance().bus().register(eventHandler);
        RecipeSorter.register(
                "galacticraftmars:canisterRecipe",
                CanisterRecipes.class,
                RecipeSorter.Category.SHAPELESS,
                "after:minecraft:shapeless");

        AsteroidsModule.fluidMethaneGas = this.registerFluid("methane", 1, 11, 295, true);
        AsteroidsModule.fluidAtmosphericGases = this.registerFluid("atmosphericgases", 1, 13, 295, true);
        AsteroidsModule.fluidLiquidMethane = this.registerFluid("liquidmethane", 450, 120, 109, false);
        // Data source for liquid methane:
        // http://science.nasa.gov/science-news/science-at-nasa/2005/25feb_titan2/
        AsteroidsModule.fluidLiquidOxygen = this.registerFluid("liquidoxygen", 1141, 140, 90, false);
        AsteroidsModule.fluidOxygenGas = this.registerFluid("oxygen", 1, 13, 295, true);
        AsteroidsModule.fluidLiquidNitrogen = this.registerFluid("liquidnitrogen", 808, 130, 90, false);
        AsteroidsModule.fluidNitrogenGas = this.registerFluid("nitrogen", 1, 12, 295, true);
        this.registerFluid("carbondioxide", 2, 20, 295, true);
        this.registerFluid("hydrogen", 1, 1, 295, true);
        this.registerFluid("argon", 1, 4, 295, true);
        AsteroidsModule.fluidLiquidArgon = this.registerFluid("liquidargon", 900, 100, 87, false);
        this.registerFluid("helium", 1, 1, 295, true);

        // AsteroidsModule.fluidCO2Gas = FluidRegistry.getFluid("carbondioxide");

        AsteroidBlocks.initBlocks();
        AsteroidBlocks.registerBlocks();
        AsteroidBlocks.setHarvestLevels();
        AsteroidBlocks.oreDictRegistration();

        AsteroidsItems.initItems();

        FluidContainerRegistry.registerFluidContainer(
                new FluidContainerData(
                        new FluidStack(AsteroidsModule.fluidMethaneGas, 1000),
                        new ItemStack(AsteroidsItems.methaneCanister, 1, 1),
                        new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY)));
        FluidContainerRegistry.registerFluidContainer(
                new FluidContainerData(
                        new FluidStack(AsteroidsModule.fluidLiquidOxygen, 1000),
                        new ItemStack(AsteroidsItems.canisterLOX, 1, 1),
                        new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY)));
        FluidContainerRegistry.registerFluidContainer(
                new FluidContainerData(
                        new FluidStack(AsteroidsModule.fluidLiquidNitrogen, 1000),
                        new ItemStack(AsteroidsItems.canisterLN2, 1, 1),
                        new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY)));
    }

    @Override
    public void init(FMLInitializationEvent event) {
        this.registerMicroBlocks();
        SchematicRegistry.registerSchematicRecipe(new SchematicTier3Rocket());
        SchematicRegistry.registerSchematicRecipe(new SchematicAstroMiner());

        GalacticraftCore.packetPipeline.addDiscriminator(7, PacketSimpleAsteroids.class);

        final AsteroidsTickHandlerServer eventHandler = new AsteroidsTickHandlerServer();
        FMLCommonHandler.instance().bus().register(eventHandler);
        MinecraftForge.EVENT_BUS.register(eventHandler);

        this.registerEntities();

        RecipeManagerAsteroids.loadRecipes();

        AsteroidsModule.planetAsteroids = new Planet("asteroids").setParentSolarSystem(GalacticraftCore.solarSystemSol);
        AsteroidsModule.planetAsteroids
                .setDimensionInfo(ConfigManagerAsteroids.dimensionIDAsteroids, WorldProviderAsteroids.class)
                .setTierRequired(3);
        AsteroidsModule.planetAsteroids
                .setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.375F, 1.375F))
                .setRelativeOrbitTime(45.0F).setPhaseShift((float) (Math.random() * (2 * Math.PI)));
        AsteroidsModule.planetAsteroids.setBodyIcon(
                new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/asteroid.png"));

        GalaxyRegistry.registerPlanet(AsteroidsModule.planetAsteroids);
        GalacticraftRegistry.registerTeleportType(WorldProviderAsteroids.class, new TeleportTypeAsteroids());

        // Handled by Galaxy Space
        final HashMap<Integer, ItemStack> input = new HashMap<>();
        /*
         * input.put(1, new ItemStack(AsteroidsItems.heavyNoseCone)); input.put(2, new
         * ItemStack(AsteroidsItems.basicItem, 1, 0)); input.put(3, new ItemStack(AsteroidsItems.basicItem, 1, 0));
         * input.put(4, new ItemStack(AsteroidsItems.basicItem, 1, 0)); input.put(5, new
         * ItemStack(AsteroidsItems.basicItem, 1, 0)); input.put(6, new ItemStack(AsteroidsItems.basicItem, 1, 0));
         * input.put(7, new ItemStack(AsteroidsItems.basicItem, 1, 0)); input.put(8, new
         * ItemStack(AsteroidsItems.basicItem, 1, 0)); input.put(9, new ItemStack(AsteroidsItems.basicItem, 1, 0));
         * input.put(10, new ItemStack(AsteroidsItems.basicItem, 1, 0)); input.put(11, new
         * ItemStack(AsteroidsItems.basicItem, 1, 0)); input.put(12, new ItemStack(GCItems.rocketEngine, 1, 1));
         * input.put(13, new ItemStack(AsteroidsItems.basicItem, 1, 2)); input.put(14, new
         * ItemStack(AsteroidsItems.basicItem, 1, 2)); input.put(15, new ItemStack(AsteroidsItems.basicItem, 1, 1));
         * input.put(16, new ItemStack(GCItems.rocketEngine, 1, 1)); input.put(17, new
         * ItemStack(AsteroidsItems.basicItem, 1, 2)); input.put(18, new ItemStack(AsteroidsItems.basicItem, 1, 2));
         * input.put(19, null); input.put(20, null); input.put(21, null); GalacticraftRegistry.addT3RocketRecipe(new
         * NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.tier3Rocket, 1, 0), input)); HashMap<Integer, ItemStack>
         * input2 = new HashMap<Integer, ItemStack>(input); input2.put(19, new ItemStack(Blocks.chest)); input2.put(20,
         * null); input2.put(21, null); GalacticraftRegistry.addT3RocketRecipe(new NasaWorkbenchRecipe(new
         * ItemStack(AsteroidsItems.tier3Rocket, 1, 1), input2)); input2 = new HashMap<Integer, ItemStack>(input);
         * input2.put(19, null); input2.put(20, new ItemStack(Blocks.chest)); input2.put(21, null);
         * GalacticraftRegistry.addT3RocketRecipe(new NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.tier3Rocket, 1,
         * 1), input2)); input2 = new HashMap<Integer, ItemStack>(input); input2.put(19, null); input2.put(20, null);
         * input2.put(21, new ItemStack(Blocks.chest)); GalacticraftRegistry.addT3RocketRecipe(new
         * NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.tier3Rocket, 1, 1), input2)); input2 = new HashMap<Integer,
         * ItemStack>(input); input2.put(19, new ItemStack(Blocks.chest)); input2.put(20, new ItemStack(Blocks.chest));
         * input2.put(21, null); GalacticraftRegistry.addT3RocketRecipe(new NasaWorkbenchRecipe(new
         * ItemStack(AsteroidsItems.tier3Rocket, 1, 2), input2)); input2 = new HashMap<Integer, ItemStack>(input);
         * input2.put(19, new ItemStack(Blocks.chest)); input2.put(20, null); input2.put(21, new
         * ItemStack(Blocks.chest)); GalacticraftRegistry.addT3RocketRecipe(new NasaWorkbenchRecipe(new
         * ItemStack(AsteroidsItems.tier3Rocket, 1, 2), input2)); input2 = new HashMap<Integer, ItemStack>(input);
         * input2.put(19, null); input2.put(20, new ItemStack(Blocks.chest)); input2.put(21, new
         * ItemStack(Blocks.chest)); GalacticraftRegistry.addT3RocketRecipe(new NasaWorkbenchRecipe(new
         * ItemStack(AsteroidsItems.tier3Rocket, 1, 2), input2)); input2 = new HashMap<Integer, ItemStack>(input);
         * input2.put(19, new ItemStack(Blocks.chest)); input2.put(20, new ItemStack(Blocks.chest)); input2.put(21, new
         * ItemStack(Blocks.chest)); GalacticraftRegistry.addT3RocketRecipe(new NasaWorkbenchRecipe(new
         * ItemStack(AsteroidsItems.tier3Rocket, 1, 3), input2));
         */

        for (int i = 1; i <= 8; i++) {
            input.put(i, new ItemStack(MarsItems.marsItemBasic, 1, 3));
        }
        input.put(9, new ItemStack(GCItems.flagPole));
        input.put(10, new ItemStack(GCItems.flagPole));
        for (int i = 11; i <= 13; i++) {
            input.put(i, new ItemStack(AsteroidsItems.basicItem));
        }
        for (int i = 14; i <= 17; i++) {
            input.put(i, new ItemStack(AsteroidsItems.orionDrive));
        }
        if (GalacticraftCore.isGalaxySpaceLoaded) {
            input.put(18, GT_ModHandler.getModItem(Constants.MOD_ID_GALAXYSPACE, "item.RocketControlComputer", 1, 102));
        }
        input.put(19, new ItemStack(GCItems.basicItem, 1, 14));
        input.put(20, new ItemStack(GCItems.basicItem, 1, 14));
        for (int i = 21; i <= 23; i++) {
            input.put(i, new ItemStack(GCItems.heavyPlatingTier1));
        }
        input.put(24, RecipeUtil.getChestItemStack(1, 1));
        input.put(25, RecipeUtil.getChestItemStack(1, 1));
        input.put(26, new ItemStack(AsteroidsItems.basicItem, 1, 8));
        input.put(27, new ItemStack(AsteroidBlocks.beamReceiver));
        input.put(28, GT_ModHandler.getModItem(Constants.MOD_ID_GREGTECH, "gt.metaitem.01", 1, 32603));
        input.put(29, GT_ModHandler.getModItem(Constants.MOD_ID_GREGTECH, "gt.metaitem.01", 1, 32603));
        GalacticraftRegistry
                .addAstroMinerRecipe(new NasaWorkbenchRecipe(new ItemStack(AsteroidsItems.astroMiner, 1, 0), input));
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        try {
            final ZipFile zf = new ZipFile(this.GCPlanetsSource);
            final Pattern assetENUSLang = Pattern.compile("assets/(.*)/lang/(?:.+/|)([\\w_-]+).lang");
            for (final ZipEntry ze : Collections.list(zf.entries())) {
                if (!ze.getName().contains("galacticraftasteroids/lang")) {
                    continue;
                }
                final Matcher matcher = assetENUSLang.matcher(ze.getName());
                if (matcher.matches()) {
                    final String lang = matcher.group(2);
                    LanguageRegistry.instance()
                            .injectLanguage(lang, StringTranslate.parseLangFile(zf.getInputStream(ze)));
                    if ("en_US".equals(lang) && event.getSide() == Side.SERVER) {
                        StringTranslate.inject(zf.getInputStream(ze));
                    }
                }
            }
            zf.close();
        } catch (final Exception e) {}
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandGCAstroMiner());
        ChunkProviderAsteroids.reset();
    }

    @Override
    public void serverInit(FMLServerStartedEvent event) {
        AsteroidsTickHandlerServer.restart();
    }

    @Override
    public void getGuiIDs(List<Integer> idList) {
        idList.add(GuiIdsPlanets.MACHINE_ASTEROIDS);
    }

    @Override
    public Object getGuiElement(Side side, int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (side == Side.SERVER) {
            final TileEntity tile = world.getTileEntity(x, y, z);

            switch (ID) {
                case GuiIdsPlanets.MACHINE_ASTEROIDS:
                    if (tile instanceof TileEntityShortRangeTelepad) {
                        return new ContainerShortRangeTelepad(player.inventory, (TileEntityShortRangeTelepad) tile);
                    }
                    if (tile instanceof TileEntityMinerBase) {
                        return new ContainerAstroMinerDock(player.inventory, (TileEntityMinerBase) tile);
                    }

                    break;
            }
        }

        return null;
    }

    private void registerEntities() {
        this.registerCreatures();
        this.registerNonMobEntities();
        this.registerTileEntities();
    }

    private void registerCreatures() {}

    private void registerNonMobEntities() {
        MarsModule.registerGalacticraftNonMobEntity(EntitySmallAsteroid.class, "SmallAsteroidGC", 150, 3, true);
        MarsModule.registerGalacticraftNonMobEntity(EntityGrapple.class, "GrappleHookGC", 150, 1, true);
        MarsModule.registerGalacticraftNonMobEntity(EntityTier3Rocket.class, "Tier3RocketGC", 150, 1, false);
        MarsModule.registerGalacticraftNonMobEntity(EntityEntryPod.class, "EntryPodAsteroids", 150, 1, true);
        MarsModule.registerGalacticraftNonMobEntity(EntityAstroMiner.class, "AstroMiner", 80, 1, true);
    }

    private void registerMicroBlocks() {
        try {
            final Class<?> clazz = Class.forName("codechicken.microblock.MicroMaterialRegistry");
            if (clazz != null) {
                Method registerMethod = null;
                final Method[] methodz = clazz.getMethods();
                for (final Method m : methodz) {
                    if (m.getName().equals("registerMaterial")) {
                        registerMethod = m;
                        break;
                    }
                }
                final Class<?> clazzbm = Class.forName("codechicken.microblock.BlockMicroMaterial");
                registerMethod.invoke(
                        null,
                        clazzbm.getConstructor(Block.class, int.class).newInstance(AsteroidBlocks.blockBasic, 0),
                        "tile.asteroidsBlock.asteroid0");
                registerMethod.invoke(
                        null,
                        clazzbm.getConstructor(Block.class, int.class).newInstance(AsteroidBlocks.blockBasic, 1),
                        "tile.asteroidsBlock.asteroid1");
                registerMethod.invoke(
                        null,
                        clazzbm.getConstructor(Block.class, int.class).newInstance(AsteroidBlocks.blockBasic, 2),
                        "tile.asteroidsBlock.asteroid2");
                registerMethod.invoke(
                        null,
                        clazzbm.getConstructor(Block.class, int.class).newInstance(AsteroidBlocks.blockDenseIce, 0),
                        "tile.denseIce");
            }
        } catch (final Exception e) {}
    }

    private void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityBeamReflector.class, "Beam Reflector");
        GameRegistry.registerTileEntity(TileEntityBeamReceiver.class, "Beam Receiver");
        GameRegistry.registerTileEntity(TileEntityShortRangeTelepad.class, "Short Range Telepad");
        GameRegistry.registerTileEntity(TileEntityTelepadFake.class, "Fake Short Range Telepad");
        GameRegistry.registerTileEntity(TileEntityTreasureChestAsteroids.class, "Asteroids Treasure Chest");
        GameRegistry.registerTileEntity(TileEntityMinerBaseSingle.class, "Astro Miner Base Builder");
        GameRegistry.registerTileEntity(TileEntityMinerBase.class, "Astro Miner Base");
    }

    @Override
    public Configuration getConfiguration() {
        return ConfigManagerAsteroids.config;
    }

    @Override
    public void syncConfig() {
        ConfigManagerAsteroids.syncConfig(false, false);
    }
}
