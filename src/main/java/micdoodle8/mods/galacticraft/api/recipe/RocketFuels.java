package micdoodle8.mods.galacticraft.api.recipe;

import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import micdoodle8.mods.galacticraft.api.entity.IFuelable;

/**
 * Handles which fluids can be used to fuel an {@link IFuelable} object. This is used by the Fuel Loader but you can use
 * this for other things too.
 * 
 * @since 3.0.70-GTNH
 * @author glowredman
 */
public class RocketFuels {

    private static final SetMultimap<Class<? extends IFuelable>, String> FUEL_MAP = HashMultimap.create();

    /**
     * Allow a fluid to be used as fuel
     * 
     * @param fluid must be either of type {@link String}, {@link Fluid} or {@link FluidStack}
     * @return {@code true} if the fuel map changed
     * @since 3.0.70-GTNH
     * @author glowredman
     */
    public static boolean addFuel(@Nonnull Class<? extends IFuelable> fuelableClass, @Nonnull Object fluid) {
        return FUEL_MAP.put(fuelableClass, getFluidName(fluid));
    }

    /**
     * Remove a fluid from the fuelable's allowed-list of fuels
     * 
     * @param fluid must be either of type {@link String}, {@link Fluid} or {@link FluidStack}
     * @return {@code true} if the fuel map changed
     * @since 3.0.70-GTNH
     * @author glowredman
     */
    public static boolean removeFuel(@Nonnull Class<? extends IFuelable> fuelableClass, @Nonnull Object fluid) {
        return FUEL_MAP.remove(fuelableClass, getFluidName(fluid));
    }

    /**
     * Remove all fluids from the fuelable's allowed-list of fuels
     * 
     * @return the values that were removed (possibly empty).
     * @since 3.0.70-GTNH
     * @author glowredman
     */
    public static Set<String> removeFuelable(@Nonnull Class<? extends IFuelable> fuelableClass) {
        return FUEL_MAP.removeAll(fuelableClass);
    }

    /**
     * Check if the given fuel can be used for this fuelable
     * 
     * @param fluid must be either of type {@link String}, {@link Fluid} or {@link FluidStack}
     * @return {@code true} if it is usable
     * @since 3.0.70-GTNH
     * @author glowredman
     */
    public static boolean isCorrectFuel(@Nonnull IFuelable fuelable, @Nonnull Object fluid) {
        return FUEL_MAP.containsEntry(fuelable.getClass(), getFluidName(fluid));
    }

    /**
     * Check if the given fuel can be used for any fuelable
     * 
     * @param fluid must be either of type {@link String}, {@link Fluid} or {@link FluidStack}
     * @return {@code true} if it is usable
     * @since 3.0.70-GTNH
     * @author glowredman
     */
    public static boolean isValidFuel(@Nonnull Object fluid) {
        return FUEL_MAP.containsValue(getFluidName(fluid));
    }

    private static String getFluidName(Object fluid) {
        if (fluid instanceof String fluidName) {
            return fluidName;
        }
        if (fluid instanceof Fluid fluidObj) {
            return fluidObj.getName();
        }
        if (fluid instanceof FluidStack fluidStack) {
            return fluidStack.getFluid().getName();
        }
        throw new IllegalArgumentException(fluid + " is not an instace of String, FLuid or FluidStack!");
    }

    private RocketFuels() {}
}
