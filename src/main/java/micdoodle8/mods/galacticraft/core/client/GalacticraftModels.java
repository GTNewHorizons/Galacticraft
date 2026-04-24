package micdoodle8.mods.galacticraft.core.client;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import micdoodle8.mods.galacticraft.planets.mars.MarsModule;

@SideOnly(Side.CLIENT)
public final class GalacticraftModels {

    private GalacticraftModels() {}

    private static IModelCustom aluminumWire;
    private static IModelCustom aluminumWireHeavy;
    private static IModelCustom buggy;
    private static IModelCustom buggyWheelLeft;
    private static IModelCustom buggyWheelRight;
    private static IModelCustom cargoRocket;
    private static IModelCustom chamber;
    private static IModelCustom frequencyModule;
    private static IModelCustom landingBalloon;
    private static IModelCustom meteorChunk;
    private static IModelCustom teleporter;
    private static IModelCustom thruster;

    public static IModelCustom getAluminumWire() {
        if (aluminumWire == null) {
            aluminumWire = loadModel(GalacticraftCore.ASSET_PREFIX, "models/aluminumWire.obj");
        }
        return aluminumWire;
    }

    public static IModelCustom getAluminumWireHeavy() {
        if (aluminumWireHeavy == null) {
            aluminumWireHeavy = loadModel(GalacticraftCore.ASSET_PREFIX, "models/aluminumWireHeavy.obj");
        }
        return aluminumWireHeavy;
    }

    public static IModelCustom getBuggy() {
        if (buggy == null) {
            buggy = loadModel(GalacticraftCore.ASSET_PREFIX, "models/buggy.obj");
        }
        return buggy;
    }

    public static IModelCustom getBuggyWheelLeft() {
        if (buggyWheelLeft == null) {
            buggyWheelLeft = loadModel(GalacticraftCore.ASSET_PREFIX, "models/buggyWheelLeft.obj");
        }
        return buggyWheelLeft;
    }

    public static IModelCustom getBuggyWheelRight() {
        if (buggyWheelRight == null) {
            buggyWheelRight = loadModel(GalacticraftCore.ASSET_PREFIX, "models/buggyWheelRight.obj");
        }
        return buggyWheelRight;
    }

    public static IModelCustom getCargoRocket() {
        if (cargoRocket == null) {
            cargoRocket = loadModel(MarsModule.ASSET_PREFIX, "models/cargoRocket.obj");
        }
        return cargoRocket;
    }

    public static IModelCustom getChamber() {
        if (chamber == null) {
            chamber = loadModel(MarsModule.ASSET_PREFIX, "models/chamber.obj");
        }
        return chamber;
    }

    public static IModelCustom getFrequencyModule() {
        if (frequencyModule == null) {
            frequencyModule = loadModel(GalacticraftCore.ASSET_PREFIX, "models/frequencyModule.obj");
        }
        return frequencyModule;
    }

    public static IModelCustom getLandingBalloon() {
        if (landingBalloon == null) {
            landingBalloon = loadModel(MarsModule.ASSET_PREFIX, "models/landingBalloon.obj");
        }
        return landingBalloon;
    }

    public static IModelCustom getMeteorChunk() {
        if (meteorChunk == null) {
            meteorChunk = loadModel(GalacticraftCore.ASSET_PREFIX, "models/meteorChunk.obj");
        }
        return meteorChunk;
    }

    public static IModelCustom getTeleporter() {
        if (teleporter == null) {
            teleporter = loadModel(AsteroidsModule.ASSET_PREFIX, "models/teleporter.obj");
        }
        return teleporter;
    }

    public static IModelCustom getThruster() {
        if (thruster == null) {
            thruster = loadModel(GalacticraftCore.ASSET_PREFIX, "models/thruster.obj");
        }
        return thruster;
    }

    private static IModelCustom loadModel(String prefix, String modelName) {
        return AdvancedModelLoader.loadModel(new ResourceLocation(prefix, modelName));
    }
}
