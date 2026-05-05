package micdoodle8.mods.galacticraft.core.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Loader;
import ganymedes01.etfuturum.client.model.ModelPlayer;
import ganymedes01.etfuturum.client.skins.DelegatedModelBiped;
import ganymedes01.etfuturum.client.skins.NewRenderPlayer;
import micdoodle8.mods.galacticraft.api.item.IHoldableItem;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GCDelegatedModelBiped extends DelegatedModelBiped {

    public static final ModelBiped modelThermalPadding = new ModelBiped(0.25F);
    public static final ModelBiped modelThermalPaddingHelmet = new ModelBiped(0.9F);

    public static final ResourceLocation oxygenMaskTexture = new ResourceLocation(
            GalacticraftCore.ASSET_PREFIX,
            "textures/model/oxygen.png");
    public static final ResourceLocation playerTexture = new ResourceLocation(
            GalacticraftCore.ASSET_PREFIX,
            "textures/model/player.png");
    public static final ResourceLocation frequencyModuleTexture = new ResourceLocation(
            GalacticraftCore.ASSET_PREFIX,
            "textures/model/frequencyModule.png");

    private static final ModelBiped BASE_MODEL_BIPED = new ModelBiped(0.0F);

    private static final ResourceLocation thermalPaddingTexture0;
    private static final ResourceLocation thermalPaddingTexture1;

    private static Boolean isSmartRenderLoaded = null;

    public static boolean flagThermalOverride = false;

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

    private boolean usingParachute;boolean wearingMask = false;
    private boolean wearingGear = false;
    private boolean wearingLeftTankGreen = false;
    private boolean wearingLeftTankOrange = false;
    private boolean wearingLeftTankRed = false;
    private boolean wearingLeftTankBlue = false;
    private boolean wearingLeftTankViolet = false;
    private boolean wearingRightTankGreen = false;
    private boolean wearingLeftTankGray = false;
    private boolean wearingRightTankOrange = false;
    private boolean wearingRightTankRed = false;
    private boolean wearingRightTankBlue = false;
    private boolean wearingRightTankViolet = false;
    private boolean wearingRightTankGray = false;
    private boolean wearingFrequencyModule = false;

    private final IModelCustom frequencyModule;

    static {
        thermalPaddingTexture0 = new ResourceLocation(
                AsteroidsModule.ASSET_PREFIX,
                "textures/misc/thermalPadding_0.png");
        thermalPaddingTexture1 = new ResourceLocation(
                AsteroidsModule.ASSET_PREFIX,
                "textures/misc/thermalPadding_1.png");
    }

    public GCDelegatedModelBiped(ModelPlayer modelBiped) {
        super(modelBiped);
        float var1 = 0.0F;

        GCLog.info("Creating delegated model biped!");

        this.oxygenMask = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.oxygenMask.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 1);
        this.oxygenMask.setRotationPoint(0.0F, 0.0F + 0.0F, 0.0F);

        this.parachute[0] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0).setTextureSize(512, 256);
        this.parachute[0].addBox(-20.0F, -45.0F, -20.0F, 10, 2, 40, var1);
        this.parachute[0].setRotationPoint(15.0F, 4.0F, 0.0F);
        this.parachute[1] = new ModelRenderer(BASE_MODEL_BIPED, 0, 42).setTextureSize(512, 256);
        this.parachute[1].addBox(-20.0F, -45.0F, -20.0F, 40, 2, 40, var1);
        this.parachute[1].setRotationPoint(0.0F, 0.0F, 0.0F);
        this.parachute[2] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0).setTextureSize(512, 256);
        this.parachute[2].addBox(-20.0F, -45.0F, -20.0F, 10, 2, 40, var1);
        this.parachute[2].setRotationPoint(11F, -11, 0.0F);

        this.parachuteStrings[0] = new ModelRenderer(BASE_MODEL_BIPED, 100, 0).setTextureSize(512, 256);
        this.parachuteStrings[0].addBox(-0.5F, 0.0F, -0.5F, 1, 40, 1, var1);
        this.parachuteStrings[0].setRotationPoint(0.0F, 0.0F, 0.0F);
        this.parachuteStrings[1] = new ModelRenderer(BASE_MODEL_BIPED, 100, 0).setTextureSize(512, 256);
        this.parachuteStrings[1].addBox(-0.5F, 0.0F, -0.5F, 1, 40, 1, var1);
        this.parachuteStrings[1].setRotationPoint(0.0F, 0.0F, 0.0F);
        this.parachuteStrings[2] = new ModelRenderer(BASE_MODEL_BIPED, 100, 0).setTextureSize(512, 256);
        this.parachuteStrings[2].addBox(-0.5F, 0.0F, -0.5F, 1, 40, 1, var1);
        this.parachuteStrings[2].setRotationPoint(0.0F, 0.0F, 0.0F);
        this.parachuteStrings[3] = new ModelRenderer(BASE_MODEL_BIPED, 100, 0).setTextureSize(512, 256);
        this.parachuteStrings[3].addBox(-0.5F, 0.0F, -0.5F, 1, 40, 1, var1);
        this.parachuteStrings[3].setRotationPoint(0.0F, 0.0F, 0.0F);

        this.tubes[0][0] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[0][0].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][0].setRotationPoint(2F, 3F, 5.8F);
        this.tubes[0][0].setTextureSize(128, 64);
        this.tubes[0][0].mirror = true;
        this.tubes[0][1] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[0][1].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][1].setRotationPoint(2F, 2F, 6.8F);
        this.tubes[0][1].setTextureSize(128, 64);
        this.tubes[0][1].mirror = true;
        this.tubes[0][2] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[0][2].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][2].setRotationPoint(2F, 1F, 6.8F);
        this.tubes[0][2].setTextureSize(128, 64);
        this.tubes[0][2].mirror = true;
        this.tubes[0][3] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[0][3].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][3].setRotationPoint(2F, 0F, 6.8F);
        this.tubes[0][3].setTextureSize(128, 64);
        this.tubes[0][3].mirror = true;
        this.tubes[0][4] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[0][4].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][4].setRotationPoint(2F, -1F, 6.8F);
        this.tubes[0][4].setTextureSize(128, 64);
        this.tubes[0][4].mirror = true;
        this.tubes[0][5] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[0][5].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][5].setRotationPoint(2F, -2F, 5.8F);
        this.tubes[0][5].setTextureSize(128, 64);
        this.tubes[0][5].mirror = true;
        this.tubes[0][6] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[0][6].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[0][6].setRotationPoint(2F, -3F, 4.8F);
        this.tubes[0][6].setTextureSize(128, 64);
        this.tubes[0][6].mirror = true;

        this.tubes[1][0] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[1][0].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][0].setRotationPoint(-2F, 3F, 5.8F);
        this.tubes[1][0].setTextureSize(128, 64);
        this.tubes[1][0].mirror = true;
        this.tubes[1][1] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[1][1].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][1].setRotationPoint(-2F, 2F, 6.8F);
        this.tubes[1][1].setTextureSize(128, 64);
        this.tubes[1][1].mirror = true;
        this.tubes[1][2] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[1][2].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][2].setRotationPoint(-2F, 1F, 6.8F);
        this.tubes[1][2].setTextureSize(128, 64);
        this.tubes[1][2].mirror = true;
        this.tubes[1][3] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[1][3].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][3].setRotationPoint(-2F, 0F, 6.8F);
        this.tubes[1][3].setTextureSize(128, 64);
        this.tubes[1][3].mirror = true;
        this.tubes[1][4] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[1][4].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][4].setRotationPoint(-2F, -1F, 6.8F);
        this.tubes[1][4].setTextureSize(128, 64);
        this.tubes[1][4].mirror = true;
        this.tubes[1][5] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[1][5].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][5].setRotationPoint(-2F, -2F, 5.8F);
        this.tubes[1][5].setTextureSize(128, 64);
        this.tubes[1][5].mirror = true;
        this.tubes[1][6] = new ModelRenderer(BASE_MODEL_BIPED, 0, 0);
        this.tubes[1][6].addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, var1);
        this.tubes[1][6].setRotationPoint(-2F, -3F, 4.8F);
        this.tubes[1][6].setTextureSize(128, 64);
        this.tubes[1][6].mirror = true;

        this.greenOxygenTanks[0] = new ModelRenderer(BASE_MODEL_BIPED, 4, 0);
        this.greenOxygenTanks[0].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.greenOxygenTanks[0].setRotationPoint(2F, 2F, 3.8F);
        this.greenOxygenTanks[0].mirror = true;
        this.greenOxygenTanks[1] = new ModelRenderer(BASE_MODEL_BIPED, 4, 0);
        this.greenOxygenTanks[1].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.greenOxygenTanks[1].setRotationPoint(-2F, 2F, 3.8F);
        this.greenOxygenTanks[1].mirror = true;

        this.orangeOxygenTanks[0] = new ModelRenderer(BASE_MODEL_BIPED, 16, 0);
        this.orangeOxygenTanks[0].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.orangeOxygenTanks[0].setRotationPoint(2F, 2F, 3.8F);
        this.orangeOxygenTanks[0].mirror = true;
        this.orangeOxygenTanks[1] = new ModelRenderer(BASE_MODEL_BIPED, 16, 0);
        this.orangeOxygenTanks[1].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.orangeOxygenTanks[1].setRotationPoint(-2F, 2F, 3.8F);
        this.orangeOxygenTanks[1].mirror = true;

        this.redOxygenTanks[0] = new ModelRenderer(BASE_MODEL_BIPED, 28, 0);
        this.redOxygenTanks[0].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.redOxygenTanks[0].setRotationPoint(2F, 2F, 3.8F);
        this.redOxygenTanks[0].mirror = true;
        this.redOxygenTanks[1] = new ModelRenderer(BASE_MODEL_BIPED, 28, 0);
        this.redOxygenTanks[1].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.redOxygenTanks[1].setRotationPoint(-2F, 2F, 3.8F);
        this.redOxygenTanks[1].mirror = true;

        this.blueOxygenTanks[0] = new ModelRenderer(BASE_MODEL_BIPED, 40, 0);
        this.blueOxygenTanks[0].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.blueOxygenTanks[0].setRotationPoint(2F, 2F, 3.8F);
        this.blueOxygenTanks[0].mirror = true;
        this.blueOxygenTanks[1] = new ModelRenderer(BASE_MODEL_BIPED, 40, 0);
        this.blueOxygenTanks[1].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.blueOxygenTanks[1].setRotationPoint(-2F, 2F, 3.8F);
        this.blueOxygenTanks[1].mirror = true;

        this.violetOxygenTanks[0] = new ModelRenderer(BASE_MODEL_BIPED, 52, 0);
        this.violetOxygenTanks[0].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.violetOxygenTanks[0].setRotationPoint(2F, 2F, 3.8F);
        this.violetOxygenTanks[0].mirror = true;
        this.violetOxygenTanks[1] = new ModelRenderer(BASE_MODEL_BIPED, 52, 0);
        this.violetOxygenTanks[1].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.violetOxygenTanks[1].setRotationPoint(-2F, 2F, 3.8F);
        this.violetOxygenTanks[1].mirror = true;

        this.grayOxygenTanks[0] = new ModelRenderer(BASE_MODEL_BIPED, 4, 10);
        this.grayOxygenTanks[0].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.grayOxygenTanks[0].setRotationPoint(2F, 2F, 3.8F);
        this.grayOxygenTanks[0].mirror = true;
        this.grayOxygenTanks[1] = new ModelRenderer(BASE_MODEL_BIPED, 4, 10);
        this.grayOxygenTanks[1].addBox(-1.5F, 0F, -1.5F, 3, 7, 3, var1);
        this.grayOxygenTanks[1].setRotationPoint(-2F, 2F, 3.8F);
        this.grayOxygenTanks[1].mirror = true;

        this.frequencyModule = GalacticraftModels.getFrequencyModule();
    }

    @Override
    public void postSetRotationAngles(float par1, float par2, float par3, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity par7Entity) {
        this.usingParachute = false;
        this.wearingMask = false;
        this.wearingGear = false;
        this.wearingLeftTankGreen = false;
        this.wearingLeftTankOrange = false;
        this.wearingLeftTankRed = false;
        this.wearingLeftTankBlue = false;
        this.wearingLeftTankViolet = false;
        this.wearingRightTankGreen = false;
        this.wearingLeftTankGray = false;
        this.wearingRightTankOrange = false;
        this.wearingRightTankRed = false;
        this.wearingRightTankBlue = false;
        this.wearingRightTankViolet = false;
        this.wearingRightTankGray = false;
        this.wearingFrequencyModule = false;

        final EntityPlayer player = (EntityPlayer) par7Entity;
        final PlayerGearData gearData = ClientProxyCore.playerItemData.get(player.getCommandSenderName());

        if (gearData != null) {
            this.usingParachute = gearData.getParachute() != null;
            this.wearingMask = gearData.getMask() > -1 && gearData.getRenderMask();
            this.wearingGear = gearData.getGear() > -1 && gearData.getRenderGear();
            this.wearingLeftTankGreen = gearData.getLeftTank() == 0 && gearData.getRenderLeftTank();
            this.wearingLeftTankOrange = gearData.getLeftTank() == 1 && gearData.getRenderLeftTank();
            this.wearingLeftTankRed = gearData.getLeftTank() == 2 && gearData.getRenderLeftTank();
            this.wearingLeftTankBlue = gearData.getLeftTank() == 3 && gearData.getRenderLeftTank();
            this.wearingLeftTankViolet = gearData.getLeftTank() == 4 && gearData.getRenderLeftTank();
            this.wearingLeftTankGray = gearData.getLeftTank() == Integer.MAX_VALUE && gearData.getRenderLeftTank();
            this.wearingRightTankGreen = gearData.getRightTank() == 0 && gearData.getRenderRightTank();
            this.wearingRightTankOrange = gearData.getRightTank() == 1 && gearData.getRenderRightTank();
            this.wearingRightTankRed = gearData.getRightTank() == 2 && gearData.getRenderRightTank();
            this.wearingRightTankBlue = gearData.getRightTank() == 3 && gearData.getRenderRightTank();
            this.wearingRightTankViolet = gearData.getRightTank() == 4 && gearData.getRenderRightTank();
            this.wearingRightTankGray = gearData.getRightTank() == Integer.MAX_VALUE && gearData.getRenderRightTank();
            this.wearingFrequencyModule = gearData.getFrequencyModule() > -1 && gearData.getRenderFrequencyModule();
        } else {
            final String id = player.getGameProfile().getName();

            if (!ClientProxyCore.gearDataRequests.contains(id)) {
                GalacticraftCore.packetPipeline.sendToServer(
                        new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_GEAR_DATA, new Object[] { id }));
                ClientProxyCore.gearDataRequests.add(id);
            }
        }

        final ItemStack currentItemStack = player.inventory.getCurrentItem();

        if (!par7Entity.onGround && par7Entity.worldObj.provider instanceof IGalacticraftWorldProvider
                && par7Entity.ridingEntity == null
                && (currentItemStack == null || !(currentItemStack.getItem() instanceof IHoldableItem))) {
            final float speedModifier = 0.1162F * 2;

            final float angularSwingArm = MathHelper.cos(par1 * (speedModifier / 2));
            final float rightMod = model.heldItemRight != 0 ? 1 : 2;
            model.bipedRightArm.rotateAngleX -= MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * rightMod
                    * par2
                    * 0.5F;
            model.bipedLeftArm.rotateAngleX -= MathHelper.cos(par1 * 0.6662F) * 2.0F * par2 * 0.5F;
            model.bipedRightArm.rotateAngleX += -angularSwingArm * 4.0F * par2 * 0.5F;
            model.bipedLeftArm.rotateAngleX += angularSwingArm * 4.0F * par2 * 0.5F;
            model.bipedLeftLeg.rotateAngleX -= MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
            model.bipedLeftLeg.rotateAngleX += MathHelper.cos(par1 * 0.1162F * 2 + (float) Math.PI) * 1.4F * par2;
            model.bipedRightLeg.rotateAngleX -= MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
            model.bipedRightLeg.rotateAngleX += MathHelper.cos(par1 * 0.1162F * 2) * 1.4F * par2;
        }

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
            model.bipedLeftArm.rotateAngleX += (float) Math.PI;
            model.bipedLeftArm.rotateAngleZ += (float) Math.PI / 10;
            model.bipedRightArm.rotateAngleX += (float) Math.PI;
            model.bipedRightArm.rotateAngleZ -= (float) Math.PI / 10;
        }

        if (player.inventory.getCurrentItem() != null
                && player.inventory.getCurrentItem().getItem() instanceof IHoldableItem) {
            final IHoldableItem holdableItem = (IHoldableItem) player.inventory.getCurrentItem().getItem();

            if (holdableItem.shouldHoldLeftHandUp(player)) {
                model.bipedLeftArm.rotateAngleX = 0;
                model.bipedLeftArm.rotateAngleZ = 0;

                model.bipedLeftArm.rotateAngleX += (float) Math.PI + 0.3;
                model.bipedLeftArm.rotateAngleZ += (float) Math.PI / 10;
            }

            if (holdableItem.shouldHoldRightHandUp(player)) {
                model.bipedRightArm.rotateAngleX = 0;
                model.bipedRightArm.rotateAngleZ = 0;

                model.bipedRightArm.rotateAngleX += (float) Math.PI + 0.3;
                model.bipedRightArm.rotateAngleZ -= (float) Math.PI / 10;
            }

            if (player.onGround && holdableItem.shouldCrouch(player)) {
                model.bipedBody.rotateAngleX = 0.5F;
                model.bipedRightLeg.rotationPointZ = 4.0F;
                model.bipedLeftLeg.rotationPointZ = 4.0F;
                model.bipedRightLeg.rotationPointY = 9.0F;
                model.bipedLeftLeg.rotationPointY = 9.0F;
                model.bipedHead.rotationPointY = 1.0F;
                model.bipedHeadwear.rotationPointY = 1.0F;
            }
        }

        this.greenOxygenTanks[0].rotateAngleX = model.bipedBody.rotateAngleX;
        this.greenOxygenTanks[0].rotateAngleY = model.bipedBody.rotateAngleY;
        this.greenOxygenTanks[0].rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.greenOxygenTanks[1].rotateAngleX = model.bipedBody.rotateAngleX;
        this.greenOxygenTanks[1].rotateAngleY = model.bipedBody.rotateAngleY;
        this.greenOxygenTanks[1].rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.orangeOxygenTanks[0].rotateAngleX = model.bipedBody.rotateAngleX;
        this.orangeOxygenTanks[0].rotateAngleY = model.bipedBody.rotateAngleY;
        this.orangeOxygenTanks[0].rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.orangeOxygenTanks[1].rotateAngleX = model.bipedBody.rotateAngleX;
        this.orangeOxygenTanks[1].rotateAngleY = model.bipedBody.rotateAngleY;
        this.orangeOxygenTanks[1].rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.redOxygenTanks[0].rotateAngleX = model.bipedBody.rotateAngleX;
        this.redOxygenTanks[0].rotateAngleY = model.bipedBody.rotateAngleY;
        this.redOxygenTanks[0].rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.redOxygenTanks[1].rotateAngleX = model.bipedBody.rotateAngleX;
        this.redOxygenTanks[1].rotateAngleY = model.bipedBody.rotateAngleY;
        this.redOxygenTanks[1].rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.blueOxygenTanks[0].rotateAngleX = model.bipedBody.rotateAngleX;
        this.blueOxygenTanks[0].rotateAngleY = model.bipedBody.rotateAngleY;
        this.blueOxygenTanks[0].rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.blueOxygenTanks[1].rotateAngleX = model.bipedBody.rotateAngleX;
        this.blueOxygenTanks[1].rotateAngleY = model.bipedBody.rotateAngleY;
        this.blueOxygenTanks[1].rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.violetOxygenTanks[0].rotateAngleX = model.bipedBody.rotateAngleX;
        this.violetOxygenTanks[0].rotateAngleY = model.bipedBody.rotateAngleY;
        this.violetOxygenTanks[0].rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.violetOxygenTanks[1].rotateAngleX = model.bipedBody.rotateAngleX;
        this.violetOxygenTanks[1].rotateAngleY = model.bipedBody.rotateAngleY;
        this.violetOxygenTanks[1].rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.grayOxygenTanks[0].rotateAngleX = model.bipedBody.rotateAngleX;
        this.grayOxygenTanks[0].rotateAngleY = model.bipedBody.rotateAngleY;
        this.grayOxygenTanks[0].rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.grayOxygenTanks[1].rotateAngleX = model.bipedBody.rotateAngleX;
        this.grayOxygenTanks[1].rotateAngleY = model.bipedBody.rotateAngleY;
        this.grayOxygenTanks[1].rotateAngleZ = model.bipedBody.rotateAngleZ;

        final List<Entity> entitiesInAABB = player.worldObj.getEntitiesWithinAABBExcludingEntity(
                player,
                AxisAlignedBB.getBoundingBox(
                        player.posX - 20,
                        0,
                        player.posZ - 20,
                        player.posX + 20,
                        200,
                        player.posZ + 20));

        for (Entity entity : entitiesInAABB) {
            if (entity instanceof EntityTieredRocket ship) {
                if (ship.riddenByEntity != null && !ship.riddenByEntity.equals(player)
                        && (ship.getLaunched() || ship.timeUntilLaunch < 390)) {
                    model.bipedRightArm.rotateAngleZ -= (float) (Math.PI / 8) + MathHelper.sin(par3 * 0.9F) * 0.2F;
                    model.bipedRightArm.rotateAngleX = (float) Math.PI;
                    break;
                }
            }
        }
    }

    @Override
    public void preRender(Entity var1, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float var7) {
        final Class<EntityClientPlayerMP> entityClass = EntityClientPlayerMP.class;
        final NewRenderPlayer render = (NewRenderPlayer) RenderManager.instance.getEntityClassRenderObject(entityClass);
        final ModelBiped modelBipedMain = render.modelBipedMain;
        final EntityPlayer player = (EntityPlayer) var1;
        final PlayerGearData gearData = ClientProxyCore.playerItemData.get(player.getCommandSenderName());

        if (var1 instanceof AbstractClientPlayer && model.equals(modelBipedMain)) {
            if (gearData != null) {
                if (wearingMask) {
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(oxygenMaskTexture);
                    GL11.glPushMatrix();
                    GL11.glScalef(1.05F, 1.05F, 1.05F);
                    this.oxygenMask.rotateAngleY = model.bipedHead.rotateAngleY;
                    this.oxygenMask.rotateAngleX = model.bipedHead.rotateAngleX;
                    this.oxygenMask.render(var7);
                    GL11.glScalef(1F, 1F, 1F);
                    GL11.glPopMatrix();
                }

                //

                if (wearingFrequencyModule) {
                    FMLClientHandler.instance().getClient().renderEngine
                            .bindTexture(frequencyModuleTexture);
                    GL11.glPushMatrix();
                    GL11.glRotatef(180, 1, 0, 0);

                    GL11.glRotatef((float) (model.bipedHeadwear.rotateAngleY * (-180.0F / Math.PI)), 0, 1, 0);
                    GL11.glRotatef((float) (model.bipedHeadwear.rotateAngleX * (180.0F / Math.PI)), 1, 0, 0);
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

                FMLClientHandler.instance().getClient().renderEngine.bindTexture(playerTexture);

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
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(gearData.getParachute());

                    this.parachute[0].render(var7);
                    this.parachute[1].render(var7);
                    this.parachute[2].render(var7);

                    this.parachuteStrings[0].render(var7);
                    this.parachuteStrings[1].render(var7);
                    this.parachuteStrings[2].render(var7);
                    this.parachuteStrings[3].render(var7);
                }
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(render.getEntityTexture((AbstractClientPlayer) player));
        }
    }

    @Override
    public void postRender(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
        if (isSmartRenderLoaded == null) {
            isSmartRenderLoaded = Loader.isModLoaded("SmartRender");
        }

        EntityLivingBase par1EntityLivingBase = (EntityLivingBase) p_78088_1_;
        final NewRenderPlayer thisInst = (NewRenderPlayer) RenderManager.instance.getEntityClassRenderObject(p_78088_1_.getClass());

        if (GCDelegatedModelBiped.thermalPaddingTexture0 != null && !isSmartRenderLoaded) {
            final PlayerGearData gearData = ClientProxyCore.playerItemData
                    .get(par1EntityLivingBase.getCommandSenderName());

            if (gearData != null && !GCDelegatedModelBiped.flagThermalOverride) {
                ModelBiped modelBiped;

                for (int i = 0; i < 4; ++i) {
                    if (i == 0) {
                        modelBiped = modelThermalPaddingHelmet;
                    } else {
                        modelBiped = modelThermalPadding;
                    }

                    final int padding = gearData.getThermalPadding(i);

                    // Padding sub-type 0 is standard Thermal Armor. See PacketSimple handling of
                    // C_UPDATE_GEAR_SLOT for how the sub-type gets set
                    if (padding == 0 && !par1EntityLivingBase.isInvisible()) {
                        GL11.glColor4f(1, 1, 1, 1);
                        Minecraft.getMinecraft().renderEngine.bindTexture(GCDelegatedModelBiped.thermalPaddingTexture1);
                        modelBiped.bipedHead.showModel = i == 0 && gearData.getRenderThermalPadding(i);
                        modelBiped.bipedHeadwear.showModel = i == 0 && gearData.getRenderThermalPadding(i);
                        modelBiped.bipedBody.showModel = (i == 1 || i == 2) && gearData.getRenderThermalPadding(i);
                        modelBiped.bipedRightArm.showModel = i == 1 && gearData.getRenderThermalPadding(i);
                        modelBiped.bipedLeftArm.showModel = i == 1 && gearData.getRenderThermalPadding(i);
                        modelBiped.bipedRightLeg.showModel = (i == 2 || i == 3)
                                && gearData.getRenderThermalPadding(i);
                        modelBiped.bipedLeftLeg.showModel = (i == 2 || i == 3)
                                && gearData.getRenderThermalPadding(i);

                        modelBiped.onGround = thisInst.mainModel.onGround;
                        modelBiped.isRiding = thisInst.mainModel.isRiding;
                        modelBiped.isChild = thisInst.mainModel.isChild;
                        if (thisInst.mainModel instanceof ModelBiped) {
                            modelBiped.heldItemLeft = ((ModelBiped) thisInst.mainModel).heldItemLeft;
                            modelBiped.heldItemRight = ((ModelBiped) thisInst.mainModel).heldItemRight;
                            modelBiped.isSneak = ((ModelBiped) thisInst.mainModel).isSneak;
                            modelBiped.aimedBow = ((ModelBiped) thisInst.mainModel).aimedBow;
                        }
                        modelBiped.setLivingAnimations(par1EntityLivingBase, p_78088_2_, p_78088_3_, 0.0F);
                        modelBiped.render(par1EntityLivingBase, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);

                        // Start alpha render
                        GL11.glDisable(GL11.GL_LIGHTING);
                        Minecraft.getMinecraft().renderEngine.bindTexture(GCDelegatedModelBiped.thermalPaddingTexture0);
                        GL11.glEnable(GL11.GL_ALPHA_TEST);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        final float time = par1EntityLivingBase.ticksExisted / 10.0F;
                        final float sTime = (float) Math.sin(time) * 0.5F + 0.5F;

                        float r = 0.2F * sTime;
                        float g = 1.0F * sTime;
                        float b = 0.2F * sTime;

                        if (par1EntityLivingBase.worldObj.provider instanceof IGalacticraftWorldProvider) {
                            final float modifier = ((IGalacticraftWorldProvider) par1EntityLivingBase.worldObj.provider)
                                    .getThermalLevelModifier();

                            if (modifier > 0) {
                                b = g;
                                g = r;
                            } else if (modifier < 0) {
                                r = g;
                                g = b;
                            }
                        }

                        GL11.glColor4f(r, g, b, 0.4F * sTime);
                        modelBiped.render(par1EntityLivingBase, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
                        GL11.glColor4f(1, 1, 1, 1);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glEnable(GL11.GL_ALPHA_TEST);
                        GL11.glEnable(GL11.GL_LIGHTING);
                    }
                }
            }
        }
    }
}
