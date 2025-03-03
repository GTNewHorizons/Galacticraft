package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import cpw.mods.fml.common.Loader;
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;

public class EntityEvolvedZombie extends EntityZombie implements IEntityBreathable {

    public EntityEvolvedZombie(World par1World) {
        super(par1World);
        this.tasks.taskEntries.clear();
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBreakDoor(this));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.36F, false));
        this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityVillager.class, 0.36F, true));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 0.36F));
        this.tasks.addTask(5, new EntityAIMoveThroughVillage(this, 0.36F, false));
        this.tasks.addTask(6, new EntityAIWander(this, 0.36F));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
                .setBaseValue(ConfigManagerCore.hardMode ? 1.06F : 0.96F);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
                .setBaseValue(ConfigManagerCore.hardMode ? 5.0D : 3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange)
                .setBaseValue(ConfigManagerCore.hardMode ? 20.0D : 16.0D);
    }

    @Override
    public boolean canBreath() {
        return true;
    }

    public IAttribute getReinforcementsAttribute() {
        return EntityZombie.field_110186_bp;
    }

    @Override
    protected void jump() {
        this.motionY = 0.48D / WorldUtil.getGravityFactor(this);
        if (this.motionY < 0.24D) {
            this.motionY = 0.24D;
        }

        if (this.isPotionActive(Potion.jump)) {
            this.motionY += (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
        }

        if (this.isSprinting()) {
            final float f = this.rotationYaw * 0.017453292F;
            this.motionX -= MathHelper.sin(f) * 0.2F;
            this.motionZ += MathHelper.cos(f) * 0.2F;
        }

        this.isAirBorne = true;
        ForgeHooks.onLivingJump(this);
    }

    @Override
    protected void dropRareDrop(int p_70600_1_) {
        switch (this.rand.nextInt(16)) {
            case 0:
            case 1:
            case 2:
                // Dehydrated carrot
                this.entityDropItem(new ItemStack(GCItems.basicItem, 1, 16), 0.0F);
                break;
            case 3:
            case 4:
                this.dropItem(GCItems.meteoricIronRaw, 1);
                break;
            case 5:
            case 6:
                // Dehydrated potato
                this.entityDropItem(new ItemStack(GCItems.basicItem, 1, 18), 0.0F);
                break;
            case 7:
            case 8:
                // Oxygen tank half empty or less
                this.entityDropItem(new ItemStack(GCItems.oxTankMedium, 1, 901 + this.rand.nextInt(900)), 0.0F);
                break;
            case 9:
                this.dropItem(GCItems.oxMask, 1);
                break;
            case 10:
                this.dropItem(GCItems.oxygenVent, 1);
                break;
            case 11:
            case 12:
                this.dropItem(Items.carrot, 1);
                break;
            case 13:
            case 14:
            case 15:
                if (ConfigManagerCore.challengeMobDropsAndSpawning) {
                    this.dropItem(Items.melon_seeds, 1);
                }
                break;
        }
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        super.dropFewItems(p_70628_1_, p_70628_2_);
        final Item item = this.getDropItem();

        // Less rotten flesh than vanilla
        int j = this.rand.nextInt(2);

        if (item != null) {
            if (p_70628_2_ > 0) {
                j += this.rand.nextInt(p_70628_2_ + 1);
            }

            for (int k = 0; k < j; ++k) {
                this.dropItem(item, 1);
            }
        }

        // Drop copper ingot as semi-rare drop if player hit and if dropping rotten
        // flesh (50% chance)
        if (p_70628_1_ && ConfigManagerCore.challengeMobDropsAndSpawning && j > 0 && this.rand.nextInt(6) == 0) {
            if (!Loader.isModLoaded(Constants.MOD_ID_NEW_HORIZONS_CORE_MOD)) {
                this.entityDropItem(new ItemStack(GCItems.basicItem, 1, 3), 0.0F);
            }
        }
    }
}
