package micdoodle8.mods.galacticraft.core.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.GalacticraftModels;
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData;

/**
 * The model of the Galacticraft equipment, if RenderPlayerAPI / Smart Moving are not installed.
 * <p>
 * This is no longer the player's own model:
 * {@link micdoodle8.mods.galacticraft.core.client.render.entities.GCPlayerRenderer} keeps one instance of it and draws
 * the equipment on top of whatever model the player is actually rendered with. It is also used as the thermal armor
 * model.
 * <p>
 * The player's own limb positions (holding both hands overhead when holding a rocket, and so on) are adjusted by
 * {@code ModelBipedMixin} instead.
 */
public class ModelPlayerGC extends ModelBiped {

    public static final ResourceLocation oxygenMaskTexture = new ResourceLocation(
            GalacticraftCore.ASSET_PREFIX,
            "textures/model/oxygen.png");
    public static final ResourceLocation playerTexture = new ResourceLocation(
            GalacticraftCore.ASSET_PREFIX,
            "textures/model/player.png");
    public static final ResourceLocation frequencyModuleTexture = new ResourceLocation(
            GalacticraftCore.ASSET_PREFIX,
            "textures/model/frequencyModule.png");

    public ModelRenderer[] parachute = new ModelRenderer[3];
    public ModelRenderer[] parachuteStrings = new ModelRenderer[4];
    public ModelRenderer[][] tubes = new ModelRenderer[2][7];
    public ModelRenderer[] greenOxygenTanks = new ModelRenderer[2];
    public ModelRenderer[] orangeOxygenTanks = new ModelRenderer[2];
    public ModelRenderer[] redOxygenTanks = new ModelRenderer[2];
    public ModelRenderer[] blueOxygenTanks = new ModelRenderer[2];
    public ModelRenderer[] violetOxygenTanks = new ModelRenderer[2];
    public ModelRenderer[] grayOxygenTanks = new ModelRenderer[2];
    public ModelRenderer oxygenMask;

    private boolean usingParachute;
    private PlayerGearData gearData;

    private final IModelCustom frequencyModule;

    public ModelPlayerGC(float var1) {
        super(var1);

        this.oxygenMask = new ModelRenderer(this, 0, 0);
        this.oxygenMask.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 1);
        this.oxygenMask.setRotationPoint(0.0F, 0.0F + 0.0F, 0.0F);

        this.parachute[0] = new ModelRenderer(this, 0, 0).setTextureSize(512, 256);
        this.parachute[0].addBox(-20.0F, -45.0F, -20.0F, 10, 2, 40, var1);
        this.parachute[0].setRotationPoint(15.0F, 4.0F, 0.0F);
        this.parachute[1] = new ModelRenderer(this, 0, 42).setTextureSize(512, 256);
        this.parachute[1].addBox(-20.0F, -45.0F, -20.0F, 40, 2, 40, var1);
        this.parachute[1].setRotationPoint(0.0F, 0.0F, 0.0F);
        this.parachute[2] = new ModelRenderer(this, 0, 0).setTextureSize(512, 256);
        this.parachute[2].addBox(-20.0F, -45.0F, -20.0F, 10, 2, 40, var1);
        this.parachute[2].setRotationPoint(11F, -11, 0.0F);

        this.parachuteStrings[0] = new ModelRenderer(this, 100, 0).setTextureSize(512, 256);
        this.parachuteStrings[0].addBox(-0.5F, 0.0F, -0.5F, 1, 40, 1, var1);
        this.parachuteStrings[0].setRotationPoint(0.0F, 0.0F, 0.0F);
        this.parachuteStrings[1] = new ModelRenderer(this, 100, 0).setTextureSize(512, 256);
        this.parachuteStrings[1].addBox(-0.5F, 0.0F, -0.5F, 1, 40, 1, var1);
        this.parachuteStrings[1].setRotationPoint(0.0F, 0.0F, 0.0F);
        this.parachuteStrings[2] = new ModelRenderer(this, 100, 0).setTextureSize(512, 256);
        this.parachuteStrings[2].addBox(-0.5F, 0.0F, -0.5F, 1, 40, 1, var1);
        this.parachuteStrings[2].setRotationPoint(0.0F, 0.0F, 0.0F);
        this.parachuteStrings[3] = new ModelRenderer(this, 100, 0).setTextureSize(512, 256);
        this.parachuteStrings[3].addBox(-0.5F, 0.0F, -0.5F, 1, 40, 1, var1);
        this.parachuteStrings[3].setRotationPoint(0.0F, 0.0F, 0.0F);

        this.tubes[0][0] = new ModelRenderer(this, 0, 0);
        this.tubes[0][0].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][0].setRotationPoint(2F, 3F, 5.8F);
        this.tubes[0][0].setTextureSize(128, 64);
        this.tubes[0][0].mirror = true;
        this.tubes[0][1] = new ModelRenderer(this, 0, 0);
        this.tubes[0][1].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][1].setRotationPoint(2F, 2F, 6.8F);
        this.tubes[0][1].setTextureSize(128, 64);
        this.tubes[0][1].mirror = true;
        this.tubes[0][2] = new ModelRenderer(this, 0, 0);
        this.tubes[0][2].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][2].setRotationPoint(2F, 1F, 6.8F);
        this.tubes[0][2].setTextureSize(128, 64);
        this.tubes[0][2].mirror = true;
        this.tubes[0][3] = new ModelRenderer(this, 0, 0);
        this.tubes[0][3].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][3].setRotationPoint(2F, 0F, 6.8F);
        this.tubes[0][3].setTextureSize(128, 64);
        this.tubes[0][3].mirror = true;
        this.tubes[0][4] = new ModelRenderer(this, 0, 0);
        this.tubes[0][4].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][4].setRotationPoint(2F, -1F, 6.8F);
        this.tubes[0][4].setTextureSize(128, 64);
        this.tubes[0][4].mirror = true;
        this.tubes[0][5] = new ModelRenderer(this, 0, 0);
        this.tubes[0][5].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][5].setRotationPoint(2F, -2F, 5.8F);
        this.tubes[0][5].setTextureSize(128, 64);
        this.tubes[0][5].mirror = true;
        this.tubes[0][6] = new ModelRenderer(this, 0, 0);
        this.tubes[0][6].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][6].setRotationPoint(2F, -3F, 4.8F);
        this.tubes[0][6].setTextureSize(128, 64);
        this.tubes[0][6].mirror = true;

        this.tubes[1][0] = new ModelRenderer(this, 0, 0);
        this.tubes[1][0].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][0].setRotationPoint(-2F, 3F, 5.8F);
        this.tubes[1][0].setTextureSize(128, 64);
        this.tubes[1][0].mirror = true;
        this.tubes[1][1] = new ModelRenderer(this, 0, 0);
        this.tubes[1][1].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][1].setRotationPoint(-2F, 2F, 6.8F);
        this.tubes[1][1].setTextureSize(128, 64);
        this.tubes[1][1].mirror = true;
        this.tubes[1][2] = new ModelRenderer(this, 0, 0);
        this.tubes[1][2].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][2].setRotationPoint(-2F, 1F, 6.8F);
        this.tubes[1][2].setTextureSize(128, 64);
        this.tubes[1][2].mirror = true;
        this.tubes[1][3] = new ModelRenderer(this, 0, 0);
        this.tubes[1][3].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][3].setRotationPoint(-2F, 0F, 6.8F);
        this.tubes[1][3].setTextureSize(128, 64);
        this.tubes[1][3].mirror = true;
        this.tubes[1][4] = new ModelRenderer(this, 0, 0);
        this.tubes[1][4].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][4].setRotationPoint(-2F, -1F, 6.8F);
        this.tubes[1][4].setTextureSize(128, 64);
        this.tubes[1][4].mirror = true;
        this.tubes[1][5] = new ModelRenderer(this, 0, 0);
        this.tubes[1][5].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][5].setRotationPoint(-2F, -2F, 5.8F);
        this.tubes[1][5].setTextureSize(128, 64);
        this.tubes[1][5].mirror = true;
        this.tubes[1][6] = new ModelRenderer(this, 0, 0);
        this.tubes[1][6].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][6].setRotationPoint(-2F, -3F, 4.8F);
        this.tubes[1][6].setTextureSize(128, 64);
        this.tubes[1][6].mirror = true;

        this.greenOxygenTanks[0] = new ModelRenderer(this, 4, 0);
        this.greenOxygenTanks[0].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.greenOxygenTanks[0].setRotationPoint(2F, 2F, 3.8F);
        this.greenOxygenTanks[0].mirror = true;
        this.greenOxygenTanks[1] = new ModelRenderer(this, 4, 0);
        this.greenOxygenTanks[1].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.greenOxygenTanks[1].setRotationPoint(-2F, 2F, 3.8F);
        this.greenOxygenTanks[1].mirror = true;

        this.orangeOxygenTanks[0] = new ModelRenderer(this, 16, 0);
        this.orangeOxygenTanks[0].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.orangeOxygenTanks[0].setRotationPoint(2F, 2F, 3.8F);
        this.orangeOxygenTanks[0].mirror = true;
        this.orangeOxygenTanks[1] = new ModelRenderer(this, 16, 0);
        this.orangeOxygenTanks[1].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.orangeOxygenTanks[1].setRotationPoint(-2F, 2F, 3.8F);
        this.orangeOxygenTanks[1].mirror = true;

        this.redOxygenTanks[0] = new ModelRenderer(this, 28, 0);
        this.redOxygenTanks[0].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.redOxygenTanks[0].setRotationPoint(2F, 2F, 3.8F);
        this.redOxygenTanks[0].mirror = true;
        this.redOxygenTanks[1] = new ModelRenderer(this, 28, 0);
        this.redOxygenTanks[1].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.redOxygenTanks[1].setRotationPoint(-2F, 2F, 3.8F);
        this.redOxygenTanks[1].mirror = true;

        this.blueOxygenTanks[0] = new ModelRenderer(this, 40, 0);
        this.blueOxygenTanks[0].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.blueOxygenTanks[0].setRotationPoint(2F, 2F, 3.8F);
        this.blueOxygenTanks[0].mirror = true;
        this.blueOxygenTanks[1] = new ModelRenderer(this, 40, 0);
        this.blueOxygenTanks[1].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.blueOxygenTanks[1].setRotationPoint(-2F, 2F, 3.8F);
        this.blueOxygenTanks[1].mirror = true;

        this.violetOxygenTanks[0] = new ModelRenderer(this, 52, 0);
        this.violetOxygenTanks[0].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.violetOxygenTanks[0].setRotationPoint(2F, 2F, 3.8F);
        this.violetOxygenTanks[0].mirror = true;
        this.violetOxygenTanks[1] = new ModelRenderer(this, 52, 0);
        this.violetOxygenTanks[1].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.violetOxygenTanks[1].setRotationPoint(-2F, 2F, 3.8F);
        this.violetOxygenTanks[1].mirror = true;

        this.grayOxygenTanks[0] = new ModelRenderer(this, 4, 10);
        this.grayOxygenTanks[0].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.grayOxygenTanks[0].setRotationPoint(2F, 2F, 3.8F);
        this.grayOxygenTanks[0].mirror = true;
        this.grayOxygenTanks[1] = new ModelRenderer(this, 4, 10);
        this.grayOxygenTanks[1].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.grayOxygenTanks[1].setRotationPoint(-2F, 2F, 3.8F);
        this.grayOxygenTanks[1].mirror = true;

        this.frequencyModule = GalacticraftModels.getFrequencyModule();
    }

    /**
     * Sets the gear data of the player about to be rendered. Must be called before {@link #setRotationAngles} so that
     * the parachute is posed correctly.
     */
    public void setGearData(PlayerGearData gearData) {
        this.gearData = gearData;
        this.usingParachute = gearData != null && gearData.getParachute() != null;
    }

    /**
     * Renders the equipment the player is wearing. The player's own model is expected to have been drawn already, and
     * the caller is responsible for rebinding the player's skin afterwards.
     */
    public void renderGear(Entity var1, float var7) {
        if (this.gearData == null) {
            return;
        }

        final boolean wearingMask = this.gearData.getMask() > -1 && this.gearData.getRenderMask();
        final boolean wearingGear = this.gearData.getGear() > -1 && this.gearData.getRenderGear();
        final boolean wearingLeftTankGreen = this.gearData.getLeftTank() == 0 && this.gearData.getRenderLeftTank();
        final boolean wearingLeftTankOrange = this.gearData.getLeftTank() == 1 && this.gearData.getRenderLeftTank();
        final boolean wearingLeftTankRed = this.gearData.getLeftTank() == 2 && this.gearData.getRenderLeftTank();
        final boolean wearingLeftTankBlue = this.gearData.getLeftTank() == 3 && this.gearData.getRenderLeftTank();
        final boolean wearingLeftTankViolet = this.gearData.getLeftTank() == 4 && this.gearData.getRenderLeftTank();
        final boolean wearingLeftTankGray = this.gearData.getLeftTank() == Integer.MAX_VALUE
                && this.gearData.getRenderLeftTank();
        final boolean wearingRightTankGreen = this.gearData.getRightTank() == 0 && this.gearData.getRenderRightTank();
        final boolean wearingRightTankOrange = this.gearData.getRightTank() == 1 && this.gearData.getRenderRightTank();
        final boolean wearingRightTankRed = this.gearData.getRightTank() == 2 && this.gearData.getRenderRightTank();
        final boolean wearingRightTankBlue = this.gearData.getRightTank() == 3 && this.gearData.getRenderRightTank();
        final boolean wearingRightTankViolet = this.gearData.getRightTank() == 4 && this.gearData.getRenderRightTank();
        final boolean wearingRightTankGray = this.gearData.getRightTank() == Integer.MAX_VALUE
                && this.gearData.getRenderRightTank();
        final boolean wearingFrequencyModule = this.gearData.getFrequencyModule() > -1
                && this.gearData.getRenderFrequencyModule();

        if (wearingMask) {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.oxygenMaskTexture);
            GL11.glPushMatrix();
            GL11.glScalef(1.05F, 1.05F, 1.05F);
            this.oxygenMask.rotateAngleY = this.bipedHead.rotateAngleY;
            this.oxygenMask.rotateAngleX = this.bipedHead.rotateAngleX;
            this.oxygenMask.render(var7);
            GL11.glScalef(1F, 1F, 1F);
            GL11.glPopMatrix();
        }

        //

        if (wearingFrequencyModule) {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.frequencyModuleTexture);
            GL11.glPushMatrix();
            GL11.glRotatef(180, 1, 0, 0);

            GL11.glRotatef((float) (this.bipedHeadwear.rotateAngleY * (-180.0F / Math.PI)), 0, 1, 0);
            GL11.glRotatef((float) (this.bipedHeadwear.rotateAngleX * (180.0F / Math.PI)), 1, 0, 0);
            GL11.glScalef(0.3F, 0.3F, 0.3F);
            GL11.glTranslatef(-1.1F, 1.2F, 0);
            this.frequencyModule.renderPart("Main");
            GL11.glTranslatef(0, 1.2F, 0);
            GL11.glRotatef((float) (Math.sin(var1.ticksExisted * 0.05) * 50.0F), 1, 0, 0);
            GL11.glRotatef((float) (Math.cos(var1.ticksExisted * 0.1) * 50.0F), 0, 1, 0);
            GL11.glTranslatef(0, -1.2F, 0);
            this.frequencyModule.renderPart("Radar");
            GL11.glPopMatrix();
        }

        //

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.playerTexture);

        if (wearingGear) {
            for (int i = 0; i < 7; i++) {
                for (int k = 0; k < 2; k++) {
                    this.tubes[k][i].render(var7);
                }
            }
        }

        //

        if (wearingLeftTankGray) {
            this.grayOxygenTanks[0].render(var7);
        }

        //

        if (wearingLeftTankViolet) {
            this.violetOxygenTanks[0].render(var7);
        }

        //

        if (wearingLeftTankBlue) {
            this.blueOxygenTanks[0].render(var7);
        }

        //

        if (wearingLeftTankRed) {
            this.redOxygenTanks[0].render(var7);
        }

        //

        if (wearingLeftTankOrange) {
            this.orangeOxygenTanks[0].render(var7);
        }

        //

        if (wearingLeftTankGreen) {
            this.greenOxygenTanks[0].render(var7);
        }

        //

        if (wearingRightTankGray) {
            this.grayOxygenTanks[1].render(var7);
        }

        //

        if (wearingRightTankViolet) {
            this.violetOxygenTanks[1].render(var7);
        }

        //

        if (wearingRightTankBlue) {
            this.blueOxygenTanks[1].render(var7);
        }

        //

        if (wearingRightTankRed) {
            this.redOxygenTanks[1].render(var7);
        }

        //

        if (wearingRightTankOrange) {
            this.orangeOxygenTanks[1].render(var7);
        }

        //

        if (wearingRightTankGreen) {
            this.greenOxygenTanks[1].render(var7);
        }

        //

        if (this.usingParachute) {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(this.gearData.getParachute());

            this.parachute[0].render(var7);
            this.parachute[1].render(var7);
            this.parachute[2].render(var7);

            this.parachuteStrings[0].render(var7);
            this.parachuteStrings[1].render(var7);
            this.parachuteStrings[2].render(var7);
            this.parachuteStrings[3].render(var7);
        }
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6,
            Entity par7Entity) {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);

        if (this.usingParachute) {
            this.parachute[0].rotateAngleZ = (float) (30F * (Math.PI / 180F));
            this.parachute[2].rotateAngleZ = (float) -(30F * (Math.PI / 180F));
            this.parachuteStrings[0].rotateAngleZ = (float) (155F * (Math.PI / 180F));
            this.parachuteStrings[0].rotateAngleX = (float) (23F * (Math.PI / 180F));
            this.parachuteStrings[0].setRotationPoint(-9.0F, -7.0F, 2.0F);
            this.parachuteStrings[1].rotateAngleZ = (float) (155F * (Math.PI / 180F));
            this.parachuteStrings[1].rotateAngleX = (float) -(23F * (Math.PI / 180F));
            this.parachuteStrings[1].setRotationPoint(-9.0F, -7.0F, 2.0F);
            this.parachuteStrings[2].rotateAngleZ = (float) -(155F * (Math.PI / 180F));
            this.parachuteStrings[2].rotateAngleX = (float) (23F * (Math.PI / 180F));
            this.parachuteStrings[2].setRotationPoint(9.0F, -7.0F, 2.0F);
            this.parachuteStrings[3].rotateAngleZ = (float) -(155F * (Math.PI / 180F));
            this.parachuteStrings[3].rotateAngleX = (float) -(23F * (Math.PI / 180F));
            this.parachuteStrings[3].setRotationPoint(9.0F, -7.0F, 2.0F);
        }

        this.greenOxygenTanks[0].rotateAngleX = this.bipedBody.rotateAngleX;
        this.greenOxygenTanks[0].rotateAngleY = this.bipedBody.rotateAngleY;
        this.greenOxygenTanks[0].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.greenOxygenTanks[1].rotateAngleX = this.bipedBody.rotateAngleX;
        this.greenOxygenTanks[1].rotateAngleY = this.bipedBody.rotateAngleY;
        this.greenOxygenTanks[1].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.orangeOxygenTanks[0].rotateAngleX = this.bipedBody.rotateAngleX;
        this.orangeOxygenTanks[0].rotateAngleY = this.bipedBody.rotateAngleY;
        this.orangeOxygenTanks[0].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.orangeOxygenTanks[1].rotateAngleX = this.bipedBody.rotateAngleX;
        this.orangeOxygenTanks[1].rotateAngleY = this.bipedBody.rotateAngleY;
        this.orangeOxygenTanks[1].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.redOxygenTanks[0].rotateAngleX = this.bipedBody.rotateAngleX;
        this.redOxygenTanks[0].rotateAngleY = this.bipedBody.rotateAngleY;
        this.redOxygenTanks[0].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.redOxygenTanks[1].rotateAngleX = this.bipedBody.rotateAngleX;
        this.redOxygenTanks[1].rotateAngleY = this.bipedBody.rotateAngleY;
        this.redOxygenTanks[1].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.blueOxygenTanks[0].rotateAngleX = this.bipedBody.rotateAngleX;
        this.blueOxygenTanks[0].rotateAngleY = this.bipedBody.rotateAngleY;
        this.blueOxygenTanks[0].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.blueOxygenTanks[1].rotateAngleX = this.bipedBody.rotateAngleX;
        this.blueOxygenTanks[1].rotateAngleY = this.bipedBody.rotateAngleY;
        this.blueOxygenTanks[1].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.violetOxygenTanks[0].rotateAngleX = this.bipedBody.rotateAngleX;
        this.violetOxygenTanks[0].rotateAngleY = this.bipedBody.rotateAngleY;
        this.violetOxygenTanks[0].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.violetOxygenTanks[1].rotateAngleX = this.bipedBody.rotateAngleX;
        this.violetOxygenTanks[1].rotateAngleY = this.bipedBody.rotateAngleY;
        this.violetOxygenTanks[1].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.grayOxygenTanks[0].rotateAngleX = this.bipedBody.rotateAngleX;
        this.grayOxygenTanks[0].rotateAngleY = this.bipedBody.rotateAngleY;
        this.grayOxygenTanks[0].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.grayOxygenTanks[1].rotateAngleX = this.bipedBody.rotateAngleX;
        this.grayOxygenTanks[1].rotateAngleY = this.bipedBody.rotateAngleY;
        this.grayOxygenTanks[1].rotateAngleZ = this.bipedBody.rotateAngleZ;
    }
}
