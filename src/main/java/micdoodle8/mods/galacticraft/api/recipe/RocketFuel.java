package micdoodle8.mods.galacticraft.api.recipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class RocketFuel {
    private final int fluidId;
    private final int maxTier;

    public RocketFuel(int fluidId, int maxTier) {
        this.fluidId = fluidId;
        this.maxTier = maxTier;
    }

    public int getMaxTier() {
        return maxTier;
    }

    public boolean isFluidEqual(FluidStack fluid) {
        return fluid.getFluidID() == fluidId;
    }

    public boolean isFluidEqual(Fluid fluid) {
        return fluid.getID() == fluidId;
    }
}
