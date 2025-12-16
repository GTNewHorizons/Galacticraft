package micdoodle8.mods.galacticraft.core.util;

/**
 * Packs relative coordinates into an int with optional side bits. Layout: [6 side bits][8 Y][9 Z][9 X]
 * <p>
 * Valid range: X/Z: -256 to +255 (9-bit signed), Y: -128 to +127 (8-bit signed).
 * Values outside this range will silently wrap due to masking.
 */
public final class RelativeCoordinatePacker {

    private static final int XZ_MASK = 0x1FF;
    private static final int Y_MASK = 0xFF;
    private static final int SIDE_MASK = 0x3F;
    private static final int SIDE_SHIFT = 26;
    public static final int COORD_ONLY_MASK = 0x3FFFFFF;

    private RelativeCoordinatePacker() {}

    public static int pack(int relX, int relY, int relZ) {
        return (relX & XZ_MASK) | ((relZ & XZ_MASK) << 9) | ((relY & Y_MASK) << 18);
    }

    public static int unpackX(int packed) {
        return (packed << 23) >> 23;
    }

    public static int unpackZ(int packed) {
        return (packed << 14) >> 23;
    }

    public static int unpackY(int packed) {
        return (packed << 6) >> 24;
    }

    public static int getSideBits(int packed) {
        return (packed >>> SIDE_SHIFT) & SIDE_MASK;
    }

    public static int withSideBit(int packed, int side) {
        return packed | (1 << (SIDE_SHIFT + side));
    }

    public static int coordOnly(int packed) {
        return packed & COORD_ONLY_MASK;
    }
}
