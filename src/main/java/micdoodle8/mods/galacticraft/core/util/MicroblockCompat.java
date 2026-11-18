package micdoodle8.mods.galacticraft.core.util;

import net.minecraft.block.Block;

import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;

/** Only load after {@link CompatibilityManager#isFMPMicroblockLoaded()}. */
public final class MicroblockCompat {

    private MicroblockCompat() {}

    /** The name is the saved material ID; never change it. */
    public static void registerMaterial(Block block, int meta, String name) {
        MicroMaterialRegistry.registerMaterial(new BlockMicroMaterial(block, meta), name);
    }
}
