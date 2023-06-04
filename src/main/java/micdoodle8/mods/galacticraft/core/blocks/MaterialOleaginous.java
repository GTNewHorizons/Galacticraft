package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;

import micdoodle8.mods.galacticraft.core.util.JavaUtil;

// This avoids water and oil mixing, by being a different material
public class MaterialOleaginous extends MaterialLiquid {

    private final Class<BlockLiquid> blockLiquidName = BlockLiquid.class;
    private final Class<BlockStaticLiquid> blockLiquidStaticName = BlockStaticLiquid.class;
    private final Class<BlockDynamicLiquid> blockLiquidDynamicName = BlockDynamicLiquid.class;

    public MaterialOleaginous(MapColor color) {
        super(color);
        this.setNoPushMobility();
    }

    // Water and other liquids cannot displace oil, but solid blocks can
    @Override
    public boolean blocksMovement() {
        return JavaUtil.instance
                .isCalledBy(this.blockLiquidStaticName, this.blockLiquidName, this.blockLiquidDynamicName);
    }
}
