package micdoodle8.mods.galacticraft.core.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class PlayerGearData {

    private final EntityPlayer player;
    private int mask;
    private int gear;
    private int leftTank;
    private int rightTank;
    private final int[] thermalPadding;
    private ResourceLocation parachute;
    private int frequencyModule;
    private boolean renderMask;
    private boolean renderGear;
    private boolean renderLeftTank;
    private boolean renderRightTank;
    private boolean renderFrequencyModule;
    private final boolean[] renderThermalPadding;

    public PlayerGearData(EntityPlayer player) {
        this(player, -1, -1, -1, -1, -1, new int[] { -1, -1, -1, -1 });
    }

    public PlayerGearData(EntityPlayer player, int mask, int gear, int leftTank, int rightTank, int frequencyModule,
            int[] thermalPadding) {
        this(
                player,
                mask,
                gear,
                leftTank,
                rightTank,
                frequencyModule,
                thermalPadding,
                true,
                true,
                true,
                true,
                true,
                new boolean[] { true, true, true, true });
    }

    public PlayerGearData(EntityPlayer player, int mask, int gear, int leftTank, int rightTank, int frequencyModule,
            int[] thermalPadding, boolean renderMask, boolean renderGear, boolean renderLeftTank,
            boolean renderRightTank, boolean renderFrequencyModule, boolean[] renderThermalPadding) {
        this.player = player;
        this.mask = mask;
        this.gear = gear;
        this.leftTank = leftTank;
        this.rightTank = rightTank;
        this.frequencyModule = frequencyModule;
        this.thermalPadding = thermalPadding;
        this.renderMask = renderMask;
        this.renderGear = renderGear;
        this.renderLeftTank = renderLeftTank;
        this.renderRightTank = renderRightTank;
        this.renderFrequencyModule = renderFrequencyModule;
        this.renderThermalPadding = renderThermalPadding;
    }

    public int getMask() {
        return this.mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public int getGear() {
        return this.gear;
    }

    public void setGear(int gear) {
        this.gear = gear;
    }

    public int getLeftTank() {
        return this.leftTank;
    }

    public void setLeftTank(int leftTank) {
        this.leftTank = leftTank;
    }

    public int getRightTank() {
        return this.rightTank;
    }

    public void setRightTank(int rightTank) {
        this.rightTank = rightTank;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public ResourceLocation getParachute() {
        return this.parachute;
    }

    public void setParachute(ResourceLocation parachute) {
        this.parachute = parachute;
    }

    public int getFrequencyModule() {
        return this.frequencyModule;
    }

    public void setFrequencyModule(int frequencyModule) {
        this.frequencyModule = frequencyModule;
    }

    public int getThermalPadding(int slot) {
        if (slot >= 0 && slot < this.thermalPadding.length) {
            return this.thermalPadding[slot];
        }

        return -1;
    }

    public void setThermalPadding(int slot, int thermalPadding) {
        if (slot >= 0 && slot < this.thermalPadding.length) {
            this.thermalPadding[slot] = thermalPadding;
        }
    }

    public boolean getRenderMask() {
        return renderMask;
    }

    public boolean getRenderGear() {
        return renderGear;
    }

    public boolean getRenderLeftTank() {
        return renderLeftTank;
    }

    public boolean getRenderRightTank() {
        return renderRightTank;
    }

    public boolean getRenderFrequencyModule() {
        return renderFrequencyModule;
    }

    public boolean getRenderThermalPadding(int slot) {
        if (slot >= 0 && slot < this.renderThermalPadding.length) {
            return this.renderThermalPadding[slot];
        }
        return false;
    }

    public void setRenderMask(boolean renderMask) {
        this.renderMask = renderMask;
    }

    public void setRenderGear(boolean renderGear) {
        this.renderGear = renderGear;
    }

    public void setRenderLeftTank(boolean renderLeftTank) {
        this.renderLeftTank = renderLeftTank;
    }

    public void setRenderRightTank(boolean renderRightTank) {
        this.renderRightTank = renderRightTank;
    }

    public void setRenderFrequencyModule(boolean renderFrequencyModule) {
        this.renderFrequencyModule = renderFrequencyModule;
    }

    public void setRenderThermalPadding(int slot, boolean render) {
        if (slot >= 0 && slot < this.renderThermalPadding.length) {
            this.renderThermalPadding[slot] = render;
        }
    }

    @Override
    public int hashCode() {
        return this.player.getGameProfile().getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerGearData) {
            return ((PlayerGearData) obj).player.getGameProfile().getName()
                    .equals(this.player.getGameProfile().getName());
        }

        return false;
    }
}
