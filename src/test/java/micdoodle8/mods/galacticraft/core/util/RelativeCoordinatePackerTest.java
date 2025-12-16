package micdoodle8.mods.galacticraft.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RelativeCoordinatePackerTest {

    @Test
    void testPackUnpackRoundTrip() {
        int[][] coords = { { 0, 0, 0 }, { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 }, { -1, 0, 0 }, { 0, -1, 0 }, { 0, 0, -1 },
                { -1, -1, -1 }, { 31, 31, 31 }, { -32, -32, -32 }, { -15, 10, -20 }, };
        for (int[] c : coords) {
            int packed = RelativeCoordinatePacker.pack(c[0], c[1], c[2]);
            assertEquals(c[0], RelativeCoordinatePacker.unpackX(packed), "X for " + c[0]);
            assertEquals(c[1], RelativeCoordinatePacker.unpackY(packed), "Y for " + c[1]);
            assertEquals(c[2], RelativeCoordinatePacker.unpackZ(packed), "Z for " + c[2]);
        }
    }

    @Test
    void testSideBitsDoNotCorruptCoordinates() {
        // Reproduces the original bug: coords must survive adding side bits
        int packed = RelativeCoordinatePacker.pack(1, 20, 0);
        for (int side = 0; side < 6; side++) {
            packed = RelativeCoordinatePacker.withSideBit(packed, side);
        }
        assertEquals(1, RelativeCoordinatePacker.unpackX(packed));
        assertEquals(20, RelativeCoordinatePacker.unpackY(packed));
        assertEquals(0, RelativeCoordinatePacker.unpackZ(packed));
        assertEquals(0x3F, RelativeCoordinatePacker.getSideBits(packed));
    }

    @Test
    void testCoordOnlyForHashSet() {
        // Same coord reached from different sides should match in hash set
        int packed1 = RelativeCoordinatePacker.withSideBit(RelativeCoordinatePacker.pack(5, -3, 10), 0);
        int packed2 = RelativeCoordinatePacker.withSideBit(RelativeCoordinatePacker.pack(5, -3, 10), 3);
        assertEquals(RelativeCoordinatePacker.coordOnly(packed1), RelativeCoordinatePacker.coordOnly(packed2));
    }
}
