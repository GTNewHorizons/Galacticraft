package micdoodle8.mods.galacticraft.core.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import cpw.mods.fml.common.IWorldGenerator;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;

public class OverworldGenerator implements IWorldGenerator {

    private final int amountPerChunk;
    private final int maxGenerateLevel;
    private final int minGenerateLevel;
    private final int amountPerVein;
    private final Block oreBlock;
    private final int metadata;

    public OverworldGenerator(Block oreBlock, int metadata, int amountPerChunk, int minGenLevel, int maxGenLevel,
            int amountPerVein) {
        this.oreBlock = oreBlock;
        this.metadata = metadata;
        this.amountPerChunk = amountPerChunk;
        this.minGenerateLevel = minGenLevel;
        this.maxGenerateLevel = maxGenLevel;
        this.amountPerVein = amountPerVein;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
            IChunkProvider chunkProvider) {
        if (!(world.provider instanceof IGalacticraftWorldProvider)) {
            for (int i = 0; i < this.amountPerChunk; i++) {
                final int x = chunkX * 16 + random.nextInt(16);
                final int z = chunkZ * 16 + random.nextInt(16);
                final int y = random.nextInt(Math.max(this.maxGenerateLevel - this.minGenerateLevel, 0))
                        + this.minGenerateLevel;
                this.generateOre(world, random, x, y, z);
            }
        }
    }

    private boolean generateOre(World par1World, Random par2Random, int par3, int par4, int par5) {
        final float var6 = par2Random.nextFloat() * (float) Math.PI;
        final double var7 = par3 + 8 + MathHelper.sin(var6) * this.amountPerVein / 8.0F;
        final double var9 = par3 + 8 - MathHelper.sin(var6) * this.amountPerVein / 8.0F;
        final double var11 = par5 + 8 + MathHelper.cos(var6) * this.amountPerVein / 8.0F;
        final double var13 = par5 + 8 - MathHelper.cos(var6) * this.amountPerVein / 8.0F;
        final double var15 = par4 + par2Random.nextInt(3) - 2;
        final double var17 = par4 + par2Random.nextInt(3) - 2;

        for (int var19 = 0; var19 <= this.amountPerVein; ++var19) {
            final double var20 = var7 + (var9 - var7) * var19 / this.amountPerVein;
            final double var22 = var15 + (var17 - var15) * var19 / this.amountPerVein;
            final double var24 = var11 + (var13 - var11) * var19 / this.amountPerVein;
            final double var26 = par2Random.nextDouble() * this.amountPerVein / 16.0D;
            final double var28 = (MathHelper.sin(var19 * (float) Math.PI / this.amountPerVein) + 1.0F) * var26 + 1.0D;
            final double var30 = (MathHelper.sin(var19 * (float) Math.PI / this.amountPerVein) + 1.0F) * var26 + 1.0D;
            final int var32 = MathHelper.floor_double(var20 - var28 / 2.0D);
            final int var33 = MathHelper.floor_double(var22 - var30 / 2.0D);
            final int var34 = MathHelper.floor_double(var24 - var28 / 2.0D);
            final int var35 = MathHelper.floor_double(var20 + var28 / 2.0D);
            final int var36 = MathHelper.floor_double(var22 + var30 / 2.0D);
            final int var37 = MathHelper.floor_double(var24 + var28 / 2.0D);

            for (int var38 = var32; var38 <= var35; ++var38) {
                final double var39 = (var38 + 0.5D - var20) / (var28 / 2.0D);

                if (var39 * var39 < 1.0D) {
                    for (int var41 = var33; var41 <= var36; ++var41) {
                        final double var42 = (var41 + 0.5D - var22) / (var30 / 2.0D);

                        if (var39 * var39 + var42 * var42 < 1.0D) {
                            for (int var44 = var34; var44 <= var37; ++var44) {
                                final double var45 = (var44 + 0.5D - var24) / (var28 / 2.0D);

                                final Block block = par1World.getBlock(var38, var41, var44);
                                if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0D
                                        && block.isReplaceableOreGen(par1World, var38, var41, var44, Blocks.stone)) {
                                    par1World.setBlock(var38, var41, var44, this.oreBlock, this.metadata, 2);
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
