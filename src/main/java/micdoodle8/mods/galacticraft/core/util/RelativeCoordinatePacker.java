package micdoodle8.mods.galacticraft.core.util;

/**
 * Packs relative coordinates into an int with optional side bits. Layout: [6 side bits][8 Y][9 Z][9 X]
 * <p>
 * Valid range: X/Z: -256 to +255 (9-bit signed), Y: -128 to +127 (8-bit signed). Values outside this range will
 * silently wrap due to masking.
 */
public final class RelativeCoordinatePacker {

    // Bit widths for each field
    private static final int X_BITS = 9;
    private static final int Z_BITS = 9;
    private static final int Y_BITS = 8;
    private static final int SIDE_BITS = 6;

    // Bit positions where each field starts
    private static final int Z_SHIFT = X_BITS;
    private static final int Y_SHIFT = X_BITS + Z_BITS;
    private static final int SIDE_SHIFT = X_BITS + Z_BITS + Y_BITS;

    // Masks to isolate each field
    private static final int XZ_MASK = (1 << X_BITS) - 1;
    private static final int Y_MASK = (1 << Y_BITS) - 1;
    private static final int SIDE_MASK = (1 << SIDE_BITS) - 1;

    // Mask for coordinate-only portion (excludes side bits)
    public static final int COORD_ONLY_MASK = (1 << SIDE_SHIFT) - 1;

    // Sign extension: shift left to put sign bit at bit 31, then arithmetic shift right
    private static final int X_SIGN_LEFT = 32 - X_BITS;
    private static final int Z_SIGN_LEFT = 32 - (X_BITS + Z_BITS);
    private static final int Y_SIGN_LEFT = 32 - (X_BITS + Z_BITS + Y_BITS);
    private static final int XZ_SIGN_RIGHT = 32 - X_BITS;
    private static final int Y_SIGN_RIGHT = 32 - Y_BITS;

    private RelativeCoordinatePacker() {}

    public static int pack(int relX, int relY, int relZ) {
        return (relX & XZ_MASK) | ((relZ & XZ_MASK) << Z_SHIFT) | ((relY & Y_MASK) << Y_SHIFT);
    }

    public static int unpackX(int packed) {
        return (packed << X_SIGN_LEFT) >> XZ_SIGN_RIGHT;
    }

    public static int unpackZ(int packed) {
        return (packed << Z_SIGN_LEFT) >> XZ_SIGN_RIGHT;
    }

    public static int unpackY(int packed) {
        return (packed << Y_SIGN_LEFT) >> Y_SIGN_RIGHT;
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
