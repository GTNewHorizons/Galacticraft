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
    private static IModelCustom support;
    private static IModelCustom fork;
    private static IModelCustom dish;
    private static IModelCustom pod;
    private static IModelCustom rocketT3;
    private static IModelCustom grapple;
    private static IModelCustom lampMetal;
    private static IModelCustom lampLight;
    private static IModelCustom lampBase;
    private static IModelCustom screenModel0;
    private static IModelCustom screenModel1;
    private static IModelCustom screenModel2;
    private static IModelCustom screenModel3;
    private static IModelCustom screenModel4;
    private static IModelCustom astroMiner;
    private static IModelCustom astroMinerFrontLaser;
    private static IModelCustom astroMinerBottomLaser;
    private static IModelCustom astroMinerCenterLaser;
    private static IModelCustom astroMinerLeftLaserGuard;
    private static IModelCustom astroMinerRightLaserGuard;
    private static IModelCustom beamReceiver;
    private static IModelCustom telepad;
    private static IModelCustom beamReflector;
    private static IModelCustom bubble;
    private static IModelCustom walkway;


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

    public static IModelCustom getDishSupport(){
        if (support == null){
            support = loadModel(GalacticraftCore.ASSET_PREFIX, "models/telesupport.obj");
        }
        return support;
    }

    public static IModelCustom getDishFork(){
        if (fork == null){
            fork = loadModel(GalacticraftCore.ASSET_PREFIX, "models/telefork.obj");
        }
        return fork;
    }

    public static IModelCustom getDish(){
        if (dish == null){
            dish = loadModel(GalacticraftCore.ASSET_PREFIX, "models/teledish.obj");
        }
        return dish;
    }

    public static IModelCustom getPod(){
        if (pod == null){
            pod = loadModel(AsteroidsModule.ASSET_PREFIX, "models/pod.obj");
        }
        return pod;
    }

    public static IModelCustom getRocketT3(){
        if (rocketT3 == null){
            rocketT3 = loadModel(AsteroidsModule.ASSET_PREFIX, "models/tier3rocket.obj");
        }
        return rocketT3;
    }

    public static IModelCustom getGrapple(){
        if (grapple == null){
            grapple = loadModel(AsteroidsModule.ASSET_PREFIX, "models/grapple.obj");
        }
        return grapple;
    }

    public static IModelCustom getLampBase(){
        if (lampBase == null){
            lampBase = loadModel(GalacticraftCore.ASSET_PREFIX, "models/arclampMetal.obj");
        }
        return lampBase;
    }

    public static IModelCustom getLampMetal(){
        if (lampMetal == null){
            lampMetal = loadModel(GalacticraftCore.ASSET_PREFIX, "models/arclampLight.obj");
        }
        return lampMetal;
    }

    public static IModelCustom getLampLight(){
        if (lampLight == null){
            lampLight = loadModel(GalacticraftCore.ASSET_PREFIX, "models/arclampBase.obj");
        }
        return lampLight;
    }

    public static IModelCustom getWholeScreen(){
        if (screenModel0 == null){
            screenModel0 = loadModel(GalacticraftCore.ASSET_PREFIX, "models/screenWhole.obj");
        }
        return screenModel0;
    }

    public static IModelCustom getScreenOQuarter(){
        if (screenModel4 == null){
            screenModel4 = loadModel(GalacticraftCore.ASSET_PREFIX,"models/screen0Quarters.obj");
        }
        return screenModel4;
    }

    public static IModelCustom getScreen1Quarter(){
        if (screenModel3 == null){
            screenModel3 = loadModel(GalacticraftCore.ASSET_PREFIX,"models/screen1Quarters.obj");
        }
        return screenModel3;
    }

    public static IModelCustom getScreen2Quarter(){
        if (screenModel2 == null){
            screenModel2 = loadModel(GalacticraftCore.ASSET_PREFIX,"models/screen2Quarters.obj");
        }
        return screenModel2;
    }

    public static IModelCustom getScreen3Quarter(){
        if (screenModel1 == null){
            screenModel1 = loadModel(GalacticraftCore.ASSET_PREFIX,"models/screen3Quarters.obj");
        }
        return screenModel1;
    }
    
    public static IModelCustom getAstroMiner(){
        if (astroMiner == null){
            astroMiner = loadModel(AsteroidsModule.ASSET_PREFIX, "models/astroMiner.obj");
        }
        return astroMiner;
    }
    
    public static IModelCustom getAstroMinerFrontLaser(){
        if (astroMinerFrontLaser == null){
            astroMinerFrontLaser = loadModel(AsteroidsModule.ASSET_PREFIX, "models/astroMinerLaserFront.obj");
        }
        return astroMinerFrontLaser;
    }

    public static IModelCustom getAstroMinerBottomLaser(){
        if (astroMinerBottomLaser == null){
            astroMinerBottomLaser = loadModel(AsteroidsModule.ASSET_PREFIX, "models/astroMinerLaserBottom.obj");
        }
        return astroMinerBottomLaser;
    }

    public static IModelCustom getAstroMinerCenterLaser(){
        if (astroMinerCenterLaser == null){
            astroMinerCenterLaser = loadModel(AsteroidsModule.ASSET_PREFIX, "models/astroMinerLaserCenter.obj");
        }
        return astroMinerCenterLaser;
    }

    public static IModelCustom getAstroMinerLeftLaserGuard(){
        if (astroMinerLeftLaserGuard == null){
            astroMinerLeftLaserGuard = loadModel(AsteroidsModule.ASSET_PREFIX, "models/astroMinerLeftGuard.obj");
        }
        return astroMinerLeftLaserGuard;
    }

    public static IModelCustom getAstroMinerRightLaserGuard(){
        if (astroMinerRightLaserGuard == null){
            astroMinerRightLaserGuard = loadModel(AsteroidsModule.ASSET_PREFIX, "models/astroMinerRightGuard.obj");
        }
        return astroMinerRightLaserGuard;
    }

    public static IModelCustom getBeamReceiver(){
        if (beamReceiver == null){
            beamReceiver = loadModel(AsteroidsModule.ASSET_PREFIX, "models/receiver.obj");
        }
        return beamReceiver;
    }

    public static IModelCustom getTelepad(){
        if (telepad == null){
            telepad = loadModel(AsteroidsModule.ASSET_PREFIX, "models/minerbase.obj");
        }
        return telepad;
    }

    public static IModelCustom getBeamReflector(){
        if (beamReflector == null){
            beamReflector = loadModel(AsteroidsModule.ASSET_PREFIX, "models/reflector.obj");
        }
        return beamReflector;
    }

    public static IModelCustom getBubble(){
        if (bubble == null){
            bubble = loadModel(GalacticraftCore.ASSET_PREFIX, "models/sphere.obj");
        }
        return bubble;
    }

    public static IModelCustom getWalkway(){
        if (walkway == null){
            walkway = loadModel(AsteroidsModule.ASSET_PREFIX, "models/walkway.obj");
        }
        return walkway;
    }

    private static IModelCustom loadModel(String prefix, String modelName) {
        return AdvancedModelLoader.loadModel(new ResourceLocation(prefix, modelName));
    }
}
