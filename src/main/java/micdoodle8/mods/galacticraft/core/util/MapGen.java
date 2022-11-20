package micdoodle8.mods.galacticraft.core.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.layer.GenLayer;
import org.apache.commons.io.FileUtils;

@SuppressWarnings("unused")
public class MapGen {
    public boolean calculatingMap = false;
    private int ix = 0;
    private int iz = 0;
    private int biomeMapx0 = 0;
    private int biomeMapz0 = 0;
    private int biomeMapz00;
    private int biomeMapCx;
    private int biomeMapCz;
    private int biomeMapFactor;
    private WorldChunkManager biomeMapWCM;
    private static GenLayer biomeMapGenLayer;
    public File biomeMapFile;
    private byte[] biomeAndHeightArray = null;
    private int biomeMapSizeX;
    private int biomeMapSizeZ;
    private Random rand = new Random();
    // private WeakReference<World> biomeMapWorld;
    private int[] heights = null;
    private double[] heighttemp = null;
    private WorldType field_147435_p = WorldType.DEFAULT;
    private BiomeGenBase[] biomesGrid = null; // Memory efficient to keep re-using the same one.
    private BiomeGenBase[] biomesGridHeights = null;
    private int[] biomeCount = null;

    public MapGen(World world, int sx, int sz, int cx, int cz, int scale, File file) {
        this.biomeMapCx = cx >> 4;
        this.biomeMapCz = cz >> 4;
        if (file.exists()) {
            // try {
            // this.sendToClient(FileUtils.readFileToByteArray(file));
            // } catch (IOException e) { e.printStackTrace(); }
            return;
        }

        this.biomeMapFile = file;
        this.calculatingMap = true;
        this.biomeMapSizeX = sx;
        this.biomeMapSizeZ = sz;
        this.biomeMapFactor = scale;
        final int limitX = this.biomeMapSizeX * this.biomeMapFactor / 32;
        final int limitZ = this.biomeMapSizeZ * this.biomeMapFactor / 32;
        this.biomeMapz00 = -limitZ;
        this.biomeMapx0 = -limitX;
        this.biomeMapz0 = this.biomeMapz00;
        this.ix = 0;
        this.iz = 0;
        this.biomeMapWCM = world.getWorldChunkManager();
        // this.biomeMapWorld = new WeakReference<World>(world);
        try {
            final Field bil = this.biomeMapWCM
                    .getClass()
                    .getDeclaredField(VersionUtil.getNameDynamic(VersionUtil.KEY_FIELD_BIOMEINDEXLAYER));
            bil.setAccessible(true);
            MapGen.biomeMapGenLayer = (GenLayer) bil.get(this.biomeMapWCM);
        } catch (final Exception e) {
        }
        if (MapGen.biomeMapGenLayer == null) {
            this.calculatingMap = false;
            GCLog.debug("Failed to get gen layer from World Chunk Manager.");
            return;
        }

        GCLog.debug("Starting map generation " + file.getName() + " top left " + (this.biomeMapCx - limitX) * 16 + ","
                + (this.biomeMapCz - limitZ) * 16);
        this.field_147435_p = world.getWorldInfo().getTerrainType();
        this.initialise(world.getSeed());
    }

    public void writeOutputFile(boolean flag) {
        try {
            if (!this.biomeMapFile.exists() || this.biomeMapFile.canWrite() && this.biomeMapFile.canRead()) {
                FileUtils.writeByteArrayToFile(this.biomeMapFile, this.biomeAndHeightArray);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        if (flag) {
            this.sendToClient(this.biomeAndHeightArray);
        }

        this.biomeAndHeightArray = null;
    }

    private void sendToClient(byte[] toSend) {
        try {
            GalacticraftCore.packetPipeline.sendToAll(new PacketSimple(
                    EnumSimplePacket.C_SEND_OVERWORLD_IMAGE,
                    new Object[] {this.biomeMapCx << 4, this.biomeMapCz << 4, toSend}));
        } catch (final Exception ex) {
            System.err.println("Error sending map image to player.");
            ex.printStackTrace();
        }
    }

    public boolean BiomeMapOneTick() {
        final int limit = Math.min(this.biomeMapFactor, 16);
        if (this.biomeAndHeightArray == null) {
            this.biomeAndHeightArray = new byte[this.biomeMapSizeX * this.biomeMapSizeZ * 2];
            this.heights = new int[256];
            this.heighttemp = new double[825];
            this.biomeCount = new int[limit * limit];
        }
        int multifactor = this.biomeMapFactor >> 4;
        if (multifactor < 1) {
            multifactor = 1;
        }
        int imagefactor = 16 / this.biomeMapFactor;
        if (imagefactor < 1) {
            imagefactor = 1;
        }
        this.biomeMapOneChunk(
                this.biomeMapCx + this.biomeMapx0,
                this.biomeMapCz + this.biomeMapz0,
                this.ix,
                this.iz,
                this.biomeMapFactor,
                limit);
        this.biomeMapz0 += multifactor;
        this.iz += imagefactor;
        if (this.iz > this.biomeMapSizeZ - imagefactor) {
            this.iz = 0;
            if (this.ix % 25 == 8) {
                GCLog.debug("Finished map column " + this.ix + " at " + (this.biomeMapCx + this.biomeMapx0) + ","
                        + (this.biomeMapCz + this.biomeMapz0));
            }
            this.ix += imagefactor;
            this.biomeMapz0 = this.biomeMapz00;
            this.biomeMapx0 += multifactor;
            if (this.biomeMapx0 > -this.biomeMapz00 * 4) {
                this.biomeMapx0 += this.biomeMapz00 * 8;
            }
            return this.ix > this.biomeMapSizeX - imagefactor;
        }
        return false;
    }

    private void biomeMapOneChunk(int x0, int z0, int ix, int iz, int factor, int limit) {
        // IntCache.resetIntCache();
        // int[] biomesGrid = biomeMapGenLayer.getInts(x0 << 4, z0 << 4, 16, 16);
        // TODO: For some reason getInts() may not work in Minecraft 1.7.2, gives a
        // banded result where part of the
        // array is 0
        this.biomesGrid = this.biomeMapWCM.getBiomeGenAt(this.biomesGrid, x0 << 4, z0 << 4, 16, 16, false);
        if (this.biomesGrid == null) {
            return;
        }
        this.getHeightMap(x0, z0);
        final int halfFactor = limit * limit / 2;
        final ArrayList<Integer> cols = new ArrayList<>();
        for (int j = 0; j < this.biomeCount.length; j++) {
            this.biomeCount[j] = 0;
        }
        for (int x = 0; x < 16; x += factor) {
            final int izstore = iz;
            for (int z = 0; z < 16; z += factor) {
                cols.clear();
                int maxcount = 0;
                int maxindex = -1;
                int biome = -1;
                int lastcol = -1;
                int idx = 0;
                int avgHeight = 0;
                int divisor = 0;
                // TODO: start in centre instead of top left
                BIOMEDONE:
                for (int xx = 0; xx < limit; xx++) {
                    final int hidx = (xx + x << 4) + z;
                    for (int zz = 0; zz < limit; zz++) {
                        final int height = this.heights[hidx + zz];
                        avgHeight += height;
                        divisor++;
                        final BiomeGenBase theBiome = this.biomesGrid[xx + x + (zz + z << 4)];
                        if (theBiome != null) {
                            biome = theBiome.biomeID;
                        } else {
                            biome = 9;
                        }
                        if (biome != lastcol) {
                            idx = cols.indexOf(biome);
                            if (idx == -1) {
                                idx = cols.size();
                                cols.add(biome);
                            }
                            lastcol = biome;
                        }
                        this.biomeCount[idx]++;
                        if (this.biomeCount[idx] > maxcount) {
                            maxcount = this.biomeCount[idx];
                            maxindex = idx;
                            if (maxcount > halfFactor) {
                                break BIOMEDONE;
                            }
                        }
                    }
                }
                // Clear the array for next time
                for (int j = cols.size() - 1; j >= 0; j--) {
                    this.biomeCount[j] = 0;
                }

                final int arrayIndex = (ix * this.biomeMapSizeZ + iz) * 2;
                this.biomeAndHeightArray[arrayIndex] = (byte) cols.get(maxindex);
                this.biomeAndHeightArray[arrayIndex + 1] = (byte) ((avgHeight + (divisor + 1) / 2) / divisor);
                iz++;
            }
            iz = izstore;
            ix++;
        }
    }

    public void getHeightMap(int cx, int cz) {
        this.rand.setSeed(cx * 341873128712L + cz * 132897987541L);
        this.biomesGridHeights =
                this.biomeMapWCM.getBiomesForGeneration(this.biomesGridHeights, cx * 4 - 2, cz * 4 - 2, 10, 10);
        this.func_147423_a(cx * 4, 0, cz * 4);

        final double d0 = 0.125D;
        final double d9 = 0.25D;

        for (int xx = 0; xx < 4; ++xx) {
            final int xa = xx * 5;
            final int xb = xa + 5;

            for (int zz = 0; zz < 4; ++zz) {
                final int aa = (xa + zz) * 33;
                final int ab = aa + 33;
                final int ba = (xb + zz) * 33;
                final int bb = ba + 33;

                for (int yy = 2; yy < 18; ++yy) {
                    double d1 = this.heighttemp[aa + yy];
                    double d2 = this.heighttemp[ab + yy];
                    double d3 = this.heighttemp[ba + yy];
                    double d4 = this.heighttemp[bb + yy];
                    final double d5 = (this.heighttemp[aa + yy + 1] - d1) * d0;
                    final double d6 = (this.heighttemp[ab + yy + 1] - d2) * d0;
                    final double d7 = (this.heighttemp[ba + yy + 1] - d3) * d0;
                    final double d8 = (this.heighttemp[bb + yy + 1] - d4) * d0;

                    for (int y = 0; y < 8; ++y) {
                        double d10 = d1;
                        double d11 = d2;
                        final double d12 = (d3 - d1) * d9;
                        final double d13 = (d4 - d2) * d9;

                        final int truey = yy * 8 + y;
                        for (int x = 0; x < 4; ++x) {
                            final int idx = x + xx * 4 << 4 | zz * 4;
                            final double d16 = (d11 - d10) * d9;
                            double d15 = d10 - d16;

                            for (int z = 0; z < 4; ++z) {
                                if ((d15 += d16) > 0.0D) {
                                    this.heights[idx + z] = truey;
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    static double[] noiseField3;
    static double[] noiseField1;
    static double[] noiseField2;
    static double[] noiseField4;
    private NoiseGeneratorOctaves noiseGen1;
    private NoiseGeneratorOctaves noiseGen2;
    private NoiseGeneratorOctaves noiseGen3;
    public NoiseGeneratorOctaves noiseGen4;

    public void initialise(long seed) {
        this.rand = new Random(seed);
        this.noiseGen1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGen2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGen3 = new NoiseGeneratorOctaves(this.rand, 8);
        this.noiseGen4 = new NoiseGeneratorOctaves(this.rand, 16);
    }

    private void func_147423_a(int cx, int cy, int cz) {
        noiseField4 = this.noiseGen4.generateNoiseOctaves(noiseField4, cx, cz, 5, 5, 200.0D, 200.0D, 0.5D);
        noiseField3 = this.noiseGen3.generateNoiseOctaves(
                noiseField3, cx, cy, cz, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
        noiseField1 =
                this.noiseGen1.generateNoiseOctaves(noiseField1, cx, cy, cz, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        noiseField2 =
                this.noiseGen2.generateNoiseOctaves(noiseField2, cx, cy, cz, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        int l = 2;
        int i1 = 0;
        final boolean amplified = this.field_147435_p == WorldType.AMPLIFIED;

        for (int xx = 0; xx < 5; ++xx) {
            for (int zz = 0; zz < 5; ++zz) {
                float f = 0.0F;
                float f1 = 0.0F;
                float f2 = 0.0F;
                final BiomeGenBase biomegenbase = this.biomesGridHeights[xx + 22 + zz * 10];

                for (int x = -2; x <= 2; ++x) {
                    final int baseIndex = xx + x + 22 + zz * 10;
                    for (int z = -2; z <= 2; ++z) {
                        final BiomeGenBase biomegenbase1 = this.biomesGridHeights[baseIndex + z * 10];
                        float f3 = biomegenbase1.rootHeight;
                        float f4 = biomegenbase1.heightVariation;

                        if (amplified && f3 > 0.0F) {
                            f3 = 1.0F + f3 + f3;
                            f4 = 1.0F + f4 * 4.0F;
                        }

                        float f5 = MapUtil.parabolicField[x + 12 + z * 5] / (f3 + 2.0F);

                        if (biomegenbase1.rootHeight > biomegenbase.rootHeight) {
                            f5 /= 2.0F;
                        }

                        f += f4 * f5;
                        f1 += f3 * f5;
                        f2 += f5;
                    }
                }

                f /= f2;
                f1 /= f2;
                f = f * 0.9F + 0.1F;
                f1 = f1 / 2.0F - 0.125F;
                double d12 = noiseField4[i1] / 8000.0D;

                if (d12 < 0.0D) {
                    d12 = -d12 * 0.3D;
                }

                d12 = d12 * 3.0D - 2.0D;

                if (d12 < 0.0D) {
                    d12 /= 2.0D;

                    if (d12 < -1.0D) {
                        d12 = -1.0D;
                    }

                    d12 /= 1.4D;
                    d12 /= 2.0D;
                } else {
                    if (d12 > 1.0D) {
                        d12 = 1.0D;
                    }

                    d12 /= 8.0D;
                }

                ++i1;
                double d13 = f1;
                final double d14 = f / 6.0D;
                d13 += d12 * 0.2D;
                d13 = d13 * 8.5D / 8.0D;
                final double d5 = 8.5D + d13 * 4.0D;

                for (int j2 = 2; j2 < 19; ++j2) {
                    double d6 = (j2 - d5) / d14;
                    if (d6 < 0.0D) {
                        d6 *= 4.0D;
                    }

                    final double d7 = noiseField1[l] / 512.0D;
                    final double d8 = noiseField2[l] / 512.0D;
                    final double d9 = (noiseField3[l] / 10.0D + 1.0D) / 2.0D;
                    this.heighttemp[l] = MathHelper.denormalizeClamp(d7, d8, d9) - d6;
                    ++l;
                }
                l += 16;
            }
        }
    }
}
