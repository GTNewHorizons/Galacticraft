package micdoodle8.mods.galacticraft.core.blocks;

import java.util.Arrays;
import java.util.List;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class BlockStairsGC extends BlockStairs {

    private IIcon[] tinSideIcon;

    public enum StairsCategoryGC {

        TIN1("stone"),
        TIN2("stone"),
        MOON_STONE("stone"),
        MOON_BRICKS("stone"),
        MARS_COBBLESTONE("stone"),
        MARS_BRICKS("stone");

        private final List<String> values;
        private final String type;

        StairsCategoryGC(String type) {
            this.type = type;
            this.values = Arrays.asList(type);
        }
    }

    private final StairsCategoryGC category;

    public BlockStairsGC(String name, Block model, StairsCategoryGC cat) {
        super(model, 0);
        this.category = cat;
        this.setBlockName(name);
        this.useNeighborBrightness = true;
    }

    @Override
    public CreativeTabs getCreativeTabToDisplayOn() {
        if (this.category == StairsCategoryGC.TIN1 || this.category == StairsCategoryGC.TIN2
                || this.category == StairsCategoryGC.MOON_STONE
                || this.category == StairsCategoryGC.MARS_COBBLESTONE
                || this.category == StairsCategoryGC.MOON_BRICKS
                || this.category == StairsCategoryGC.MARS_BRICKS) {
            return GalacticraftCore.galacticraftBlocksTab;
        }
        return null;
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        if (this.category == StairsCategoryGC.TIN1) // Tin Decoration
        {
            this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_4");
        } else if (this.category == StairsCategoryGC.TIN2) // Tin Decoration
        {
            this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_2");
        } else if (this.category == StairsCategoryGC.MOON_STONE) // Moon Stone
        {
            this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "bottom");
        } else if (this.category == StairsCategoryGC.MOON_BRICKS) // Moon Dungeon Bricks
        {
            this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX_MOON + "brick");
        }

        if (GalacticraftCore.isPlanetsLoaded) {
            try {
                final String prefix = (String) Class.forName("micdoodle8.mods.galacticraft.planets.mars.MarsModule")
                        .getField("TEXTURE_PREFIX").get(null);
                if (this.category == StairsCategoryGC.MARS_COBBLESTONE) // Mars Cobblestone
                {
                    this.blockIcon = par1IconRegister.registerIcon(prefix + "cobblestone");
                } else if (this.category == StairsCategoryGC.MARS_BRICKS) // Mars Dungeon Bricks
                {
                    this.blockIcon = par1IconRegister.registerIcon(prefix + "brick");
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        this.tinSideIcon = new IIcon[2];
        this.tinSideIcon[0] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_1");
        this.tinSideIcon[1] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "deco_aluminium_4");
    }

    public boolean isWoodCategory(String block) {
        final String type = StairsCategoryGC.valueOf(block).type;

        return "wood".equals(type);
    }

    public boolean isStoneCategory(String block) {
        final String type = StairsCategoryGC.valueOf(block).type;

        return "stone".equals(type);
    }

    public static int getWoodCategoryAmount() {
        int woodCatNo = 0;

        for (final StairsCategoryGC cat : StairsCategoryGC.values()) {
            if (cat.values.contains("wood")) {
                ++woodCatNo;
            }
        }
        return woodCatNo;
    }

    public static int getStoneCategoryAmount() {
        int woodCatNo = 0;

        for (final StairsCategoryGC cat : StairsCategoryGC.values()) {
            if (cat.values.contains("stone")) {
                ++woodCatNo;
            }
        }
        return woodCatNo;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (this.category == StairsCategoryGC.TIN2) // Tin Decoration
        {
            switch (meta) {
                case 0:
                case 8:
                    return switch (side) {
                        case 0 -> this.tinSideIcon[1]; // BOTTOM
                        case 1 -> this.blockIcon; // TOP
                        case 2 -> this.tinSideIcon[0]; // Z-
                        case 3 -> this.tinSideIcon[0]; // Z+
                        case 4 -> this.blockIcon; // X-
                        case 5 -> this.tinSideIcon[0]; // X+
                        default -> this.blockIcon;
                    };
                case 1:
                case 9:
                    return switch (side) {
                        case 0 -> this.tinSideIcon[1]; // BOTTOM
                        case 1 -> this.blockIcon; // TOP
                        case 2 -> this.tinSideIcon[0]; // Z-
                        case 3 -> this.tinSideIcon[0]; // Z+
                        case 4 -> this.tinSideIcon[0]; // X-
                        case 5 -> this.blockIcon; // X+
                        default -> this.blockIcon;
                    };
                case 2:
                case 10:
                    return switch (side) {
                        case 0 -> this.tinSideIcon[1]; // BOTTOM
                        case 1 -> this.blockIcon; // TOP
                        case 2 -> this.blockIcon; // Z-
                        case 3 -> this.tinSideIcon[0]; // Z+
                        case 4 -> this.tinSideIcon[0]; // X-
                        case 5 -> this.tinSideIcon[0]; // X+
                        default -> this.blockIcon;
                    };
                case 3:
                case 11:
                    return switch (side) {
                        case 0 -> this.tinSideIcon[1]; // BOTTOM
                        case 1 -> this.blockIcon; // TOP
                        case 2 -> this.tinSideIcon[0]; // Z-
                        case 3 -> this.blockIcon; // Z+
                        case 4 -> this.tinSideIcon[0]; // X-
                        case 5 -> this.tinSideIcon[0]; // X+
                        default -> this.blockIcon;
                    };
                case 4:
                case 12:
                    return switch (side) {
                        case 0 -> this.blockIcon; // BOTTOM
                        case 1 -> this.tinSideIcon[1]; // TOP
                        case 2 -> this.tinSideIcon[0]; // Z-
                        case 3 -> this.tinSideIcon[0]; // Z+
                        case 4 -> this.blockIcon; // X-
                        case 5 -> this.tinSideIcon[0]; // X+
                        default -> this.blockIcon;
                    };
                case 5:
                case 13:
                    return switch (side) {
                        case 0 -> this.blockIcon; // BOTTOM
                        case 1 -> this.tinSideIcon[1]; // TOP
                        case 2 -> this.tinSideIcon[0]; // Z-
                        case 3 -> this.tinSideIcon[0]; // Z+
                        case 4 -> this.tinSideIcon[0]; // X-
                        case 5 -> this.blockIcon; // X+
                        default -> this.blockIcon;
                    };
                case 6:
                case 14:
                    return switch (side) {
                        case 0 -> this.blockIcon; // BOTTOM
                        case 1 -> this.tinSideIcon[1]; // TOP
                        case 2 -> this.blockIcon; // Z-
                        case 3 -> this.tinSideIcon[0]; // Z+
                        case 4 -> this.tinSideIcon[0]; // X-
                        case 5 -> this.tinSideIcon[0]; // X+
                        default -> this.blockIcon;
                    };
                case 7:
                case 15:
                    return switch (side) {
                        case 0 -> this.blockIcon; // BOTTOM
                        case 1 -> this.tinSideIcon[1]; // TOP
                        case 2 -> this.tinSideIcon[0]; // Z-
                        case 3 -> this.blockIcon; // Z+
                        case 4 -> this.tinSideIcon[0]; // X-
                        case 5 -> this.tinSideIcon[0]; // X+
                        default -> this.blockIcon;
                    };
                default:
                    break;
            }
        }
        return this.blockIcon;
    }
}
