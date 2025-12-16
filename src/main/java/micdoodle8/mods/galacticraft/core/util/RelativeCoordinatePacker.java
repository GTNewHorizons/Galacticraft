package micdoodle8.mods.galacticraft.core.util;

/**
 * Packs relative coordinates into an int with optional side bits. Layout: [8 unused][6 side bits][6 Y][6 Z][6 X]
 * <p>
 * Valid range for each coordinate: -32 to +31 (6-bit signed). Values outside this range will silently wrap due to
 * masking.
 */
public final class RelativeCoordinatePacker {

    private static final int COORD_MASK = 0x3F;
    private static final int SIDE_SHIFT = 18;
    public static final int COORD_ONLY_MASK = 0x3FFFF;

    private RelativeCoordinatePacker() {}

    public static int pack(int relX, int relY, int relZ) {
        return (relX & COORD_MASK) | ((relZ & COORD_MASK) << 6) | ((relY & COORD_MASK) << 12);
    }

    public static int unpackX(int packed) {
        return (packed << 26) >> 26;
    }

    public static int unpackZ(int packed) {
        return (packed << 20) >> 26;
    }

    public static int unpackY(int packed) {
        return (packed << 14) >> 26;
    }

    public static int getSideBits(int packed) {
        return (packed >>> SIDE_SHIFT) & COORD_MASK;
    }

    public static int withSideBit(int packed, int side) {
        return packed | (1 << (SIDE_SHIFT + side));
    }

    public static int coordOnly(int packed) {
        return packed & COORD_ONLY_MASK;
    }
}
