package micdoodle8.mods.galacticraft.core.tile;

import java.lang.ref.WeakReference;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil;
import micdoodle8.mods.galacticraft.core.util.RelativeCoordinatePacker;

public class TileEntityArclamp extends TileEntity {

    // Direction offsets indexed by side: [side][x, y, z]
    private static final int[][] SIDE_OFFSETS = { { 0, -1, 0 }, // 0: Down
            { 0, 1, 0 }, // 1: Up
            { 0, 0, -1 }, // 2: North
            { 0, 0, 1 }, // 3: South
            { -1, 0, 0 }, // 4: West
            { 1, 0, 0 } // 5: East
    };

    private int ticks = 0;
    private int sideRear = 0;
    public int facing = 0;
    private final LongSet airToRestore = new LongOpenHashSet();
    private boolean isActive = false;
    private AxisAlignedBB thisAABB;
    private Vec3 thisPos;
    private int facingSide = 0;
    public boolean updateClientFlag;

    // Internal chunk cache for block access optimization using WeakReference to allow garbage collection
    private static WeakReference<Chunk> chunkCached = new WeakReference<>(null);
    private static int chunkCacheDim = Integer.MAX_VALUE;
    private static int chunkCacheX = 1876000; // outside the world edge
    private static int chunkCacheZ = 1876000; // outside the world edge

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (this.worldObj.isRemote) {
            return;
        }

        boolean initialLight = false;
        if (this.updateClientFlag) {
            GalacticraftCore.packetPipeline.sendToDimension(
                    new PacketSimple(
                            EnumSimplePacket.C_UPDATE_ARCLAMP_FACING,
                            new Object[] { this.xCoord, this.yCoord, this.zCoord, this.facing }),
                    this.worldObj.provider.dimensionId);
            this.updateClientFlag = false;
        }

        if (RedstoneUtil.isBlockReceivingRedstone(this.worldObj, this.xCoord, this.yCoord, this.zCoord)) {
            if (this.isActive) {
                this.isActive = false;
                this.revertAir();
                this.markDirty();
            }
        } else if (!this.isActive) {
            this.isActive = true;
            initialLight = true;
        }

        if (this.isActive) {
            // Test for first tick after placement
            if (this.thisAABB == null) {
                initialLight = true;
                final int side = this.getBlockMetadata();
                this.sideRear = side;
                switch (side) {
                    case 0 -> { // Down
                        this.facingSide = this.facing + 2;
                        this.thisAABB = AxisAlignedBB.getBoundingBox(
                                this.xCoord - 20,
                                this.yCoord - 8,
                                this.zCoord - 20,
                                this.xCoord + 20,
                                this.yCoord + 20,
                                this.zCoord + 20);
                    }
                    case 1 -> { // Up
                        this.facingSide = this.facing + 2;
                        this.thisAABB = AxisAlignedBB.getBoundingBox(
                                this.xCoord - 20,
                                this.yCoord - 20,
                                this.zCoord - 20,
                                this.xCoord + 20,
                                this.yCoord + 8,
                                this.zCoord + 20);
                    }
                    case 2 -> { // North
                        this.facingSide = this.facing;
                        if (this.facing > 1) {
                            this.facingSide = 7 - this.facing;
                        }
                        this.thisAABB = AxisAlignedBB.getBoundingBox(
                                this.xCoord - 20,
                                this.yCoord - 20,
                                this.zCoord - 8,
                                this.xCoord + 20,
                                this.yCoord + 20,
                                this.zCoord + 20);
                    }
                    case 3 -> { // South
                        this.facingSide = this.facing;
                        if (this.facing > 1) {
                            this.facingSide += 2;
                        }
                        this.thisAABB = AxisAlignedBB.getBoundingBox(
                                this.xCoord - 20,
                                this.yCoord - 20,
                                this.zCoord - 20,
                                this.xCoord + 20,
                                this.yCoord + 20,
                                this.zCoord + 8);
                    }
                    case 4 -> { // West
                        this.facingSide = this.facing;
                        this.thisAABB = AxisAlignedBB.getBoundingBox(
                                this.xCoord - 8,
                                this.yCoord - 20,
                                this.zCoord - 20,
                                this.xCoord + 20,
                                this.yCoord + 20,
                                this.zCoord + 20);
                    }
                    case 5 -> { // East
                        this.facingSide = this.facing;
                        if (this.facing > 1) {
                            this.facingSide = 5 - this.facing;
                        }
                        this.thisAABB = AxisAlignedBB.getBoundingBox(
                                this.xCoord - 20,
                                this.yCoord - 20,
                                this.zCoord - 20,
                                this.xCoord + 8,
                                this.yCoord + 20,
                                this.zCoord + 20);
                    }
                    default -> {
                        return;
                    }
                }
            }

            if (initialLight || this.ticks % 100 == 0) {
                this.lightArea();
            }

            if (this.worldObj.rand.nextInt(20) == 0) {
                final List<Entity> moblist = this.worldObj
                        .getEntitiesWithinAABBExcludingEntity(null, this.thisAABB, IMob.mobSelector);

                if (!moblist.isEmpty()) {
                    for (final Entity entry : moblist) {
                        if (!(entry instanceof EntityCreature e)) {
                            continue;
                        }
                        final Vec3 vecNewTarget = RandomPositionGenerator
                                .findRandomTargetBlockAwayFrom(e, 16, 7, this.thisPos);
                        if (vecNewTarget == null) {
                            continue;
                        }
                        final PathNavigate nav = e.getNavigator();
                        if (nav == null) {
                            continue;
                        }
                        Vec3 vecOldTarget = null;
                        if (nav.getPath() != null && !nav.getPath().isFinished()) {
                            vecOldTarget = nav.getPath().getPosition(e);
                        }
                        final double distanceNew = vecNewTarget.squareDistanceTo(this.xCoord, this.yCoord, this.zCoord);

                        if (distanceNew > e.getDistanceSq(this.xCoord, this.yCoord, this.zCoord)
                                && (vecOldTarget == null || distanceNew
                                        > vecOldTarget.squareDistanceTo(this.xCoord, this.yCoord, this.zCoord))) {
                            e.getNavigator()
                                    .tryMoveToXYZ(vecNewTarget.xCoord, vecNewTarget.yCoord, vecNewTarget.zCoord, 0.3D);
                            // System.out.println("Debug: Arclamp repelling entity:
                            // "+e.getClass().getSimpleName());
                        }
                    }
                }
            }
        }

        this.ticks++;
    }

    @Override
    public void validate() {
        super.validate();
        this.thisPos = Vec3.createVectorHelper(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D);
        this.ticks = 0;
        this.thisAABB = null;
        if (this.worldObj.isRemote) {
            GalacticraftCore.packetPipeline.sendToServer(
                    new PacketSimple(
                            EnumSimplePacket.S_REQUEST_ARCLAMP_FACING,
                            new Object[] { this.xCoord, this.yCoord, this.zCoord }));
        } else {
            this.isActive = true;
        }
    }

    @Override
    public void invalidate() {
        if (!this.worldObj.isRemote) {
            this.revertAir();
        }
        this.isActive = false;
        super.invalidate();
    }

    public void lightArea() {
        final Block breatheableAirID = GCBlocks.breatheableAir;
        final Block brightAir = GCBlocks.brightAir;
        final Block brightBreatheableAir = GCBlocks.brightBreatheableAir;
        final IntSet checked = new IntOpenHashSet();
        final int baseX = this.xCoord, baseY = this.yCoord, baseZ = this.zCoord;

        IntList currentLayer = new IntArrayList();
        IntList nextLayer = new IntArrayList();
        final World world = this.worldObj;
        final int sideskip1 = this.sideRear;
        final int sideskip2 = this.facingSide ^ 1;

        // Add initial neighbors
        for (int i = 0; i < 6; i++) {
            if (i != sideskip1 && i != sideskip2 && i != (sideskip1 ^ 1) && i != (sideskip2 ^ 1)) {
                final int[] offset = SIDE_OFFSETS[i];
                final Block b = getBlockSafe(world, baseX + offset[0], baseY + offset[1], baseZ + offset[2]);
                if (b != null && b.getLightOpacity() < 15) {
                    currentLayer.add(RelativeCoordinatePacker.pack(offset[0], offset[1], offset[2]));
                }
            }
        }

        // Add positions in front of lamp
        final int[] facingOffset = SIDE_OFFSETS[this.facingSide];
        final int[] rearOppositeOffset = SIDE_OFFSETS[sideskip1 ^ 1];
        int frontX = 0, frontY = 0, frontZ = 0;
        for (int i = 0; i < 5; i++) {
            frontX += facingOffset[0] + rearOppositeOffset[0];
            frontY += facingOffset[1] + rearOppositeOffset[1];
            frontZ += facingOffset[2] + rearOppositeOffset[2];
            final Block b = getBlockSafe(world, baseX + frontX, baseY + frontY, baseZ + frontZ);
            if (b != null && b.getLightOpacity() < 15) {
                currentLayer.add(RelativeCoordinatePacker.pack(frontX, frontY, frontZ));
            }
        }

        for (int count = 0; count < 14; count++) {
            IntIterator iter = currentLayer.intIterator();
            while (iter.hasNext()) {
                final int packed = iter.nextInt();
                final int sideBits = RelativeCoordinatePacker.getSideBits(packed);
                final int rx = RelativeCoordinatePacker.unpackX(packed);
                final int ry = RelativeCoordinatePacker.unpackY(packed);
                final int rz = RelativeCoordinatePacker.unpackZ(packed);
                boolean allAir = true;

                for (int side = 0; side < 6; side++) {
                    if ((sideBits & (1 << side)) != 0) continue;

                    final int[] offset = SIDE_OFFSETS[side];
                    final int nrx = rx + offset[0];
                    final int nry = ry + offset[1];
                    final int nrz = rz + offset[2];

                    final int neighborCoord = RelativeCoordinatePacker.pack(nrx, nry, nrz);
                    if (!checked.contains(RelativeCoordinatePacker.coordOnly(neighborCoord))) {
                        checked.add(RelativeCoordinatePacker.coordOnly(neighborCoord));

                        final int absX = baseX + nrx, absY = baseY + nry, absZ = baseZ + nrz;
                        final Block b = getBlockSafe(world, absX, absY, absZ);

                        if (b instanceof BlockAir) {
                            if (side != sideskip1 && side != sideskip2) {
                                nextLayer.add(RelativeCoordinatePacker.withSideBit(neighborCoord, side ^ 1));
                            }
                        } else {
                            allAir = false;
                            if (b != null && b.getLightOpacity(world, absX, absY, absZ) == 0
                                    && side != sideskip1
                                    && side != sideskip2) {
                                nextLayer.add(RelativeCoordinatePacker.withSideBit(neighborCoord, side ^ 1));
                            }
                        }
                    }
                }

                if (!allAir) {
                    final int absX = baseX + rx, absY = baseY + ry, absZ = baseZ + rz;
                    final Block id = getBlockSafe(world, absX, absY, absZ);

                    if (Blocks.air == id) {
                        world.setBlock(absX, absY, absZ, brightAir, 0, 2);
                        this.airToRestore.add(CoordinatePacker.pack(absX, absY, absZ));
                        this.markDirty();
                    } else if (id == breatheableAirID) {
                        world.setBlock(absX, absY, absZ, brightBreatheableAir, 0, 2);
                        this.airToRestore.add(CoordinatePacker.pack(absX, absY, absZ));
                        this.markDirty();
                    } else if (id == brightAir || id == brightBreatheableAir) {
                        // Already lit - ensure it's tracked for revert
                        this.airToRestore.add(CoordinatePacker.pack(absX, absY, absZ));
                    }
                }
            }

            IntList temp = currentLayer;
            currentLayer = nextLayer;
            temp.clear();
            nextLayer = temp;
            if (currentLayer.isEmpty()) {
                break;
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        this.facing = nbt.getInteger("Facing");
        this.updateClientFlag = true;

        this.airToRestore.clear();

        // Use the same NBT format structure, just convert to packed longs internally
        final NBTTagList airBlocks = nbt.getTagList("AirBlocks", 10);
        if (airBlocks.tagCount() > 0) {
            for (int j = airBlocks.tagCount() - 1; j >= 0; j--) {
                final NBTTagCompound tag = airBlocks.getCompoundTagAt(j);
                if (tag != null) {
                    long packedCoord = CoordinatePacker
                            .pack(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
                    this.airToRestore.add(packedCoord);
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger("Facing", this.facing);

        // Write using the same NBT format, just unpack from longs when writing
        final NBTTagList airBlocks = new NBTTagList();

        LongIterator iterator = this.airToRestore.longIterator();
        while (iterator.hasNext()) {
            final long packedCoord = iterator.nextLong();
            final NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("x", CoordinatePacker.unpackX(packedCoord));
            tag.setInteger("y", CoordinatePacker.unpackY(packedCoord));
            tag.setInteger("z", CoordinatePacker.unpackZ(packedCoord));
            airBlocks.appendTag(tag);
        }

        nbt.setTag("AirBlocks", airBlocks);
    }

    public void facingChanged() {
        this.facing -= 2;
        if (this.facing < 0) {
            this.facing = 1 - this.facing;
            // facing sequence: 0 - 3 - 1 - 2
        }

        GalacticraftCore.packetPipeline.sendToDimension(
                new PacketSimple(
                        EnumSimplePacket.C_UPDATE_ARCLAMP_FACING,
                        new Object[] { this.xCoord, this.yCoord, this.zCoord, this.facing }),
                this.worldObj.provider.dimensionId);
        this.thisAABB = null;
        this.revertAir();
        this.markDirty();
    }

    private void revertAir() {
        final Block brightAir = GCBlocks.brightAir;
        final Block brightBreatheableAir = GCBlocks.brightBreatheableAir;

        LongIterator iterator = this.airToRestore.longIterator();
        while (iterator.hasNext()) {
            final long packedCoord = iterator.nextLong();

            // Unpack coordinates
            final int x = CoordinatePacker.unpackX(packedCoord);
            final int y = CoordinatePacker.unpackY(packedCoord);
            final int z = CoordinatePacker.unpackZ(packedCoord);

            // Get the block at these coordinates
            final Block b = this.worldObj.getBlock(x, y, z);

            if (b == brightAir) {
                this.worldObj.setBlock(x, y, z, Blocks.air, 0, 2);
            } else if (b == brightBreatheableAir) {
                this.worldObj.setBlock(x, y, z, GCBlocks.breatheableAir, 0, 2);
                // No block update - not necessary for changing air to air, also must not
                // trigger a sealer edge check
            }
        }

        this.airToRestore.clear();
    }

    public boolean getEnabled() {
        return !RedstoneUtil.isBlockReceivingRedstone(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
    }

    /** Gets block without forcing chunk load. Uses chunk cache for efficiency. */
    private Block getBlockSafe(World world, int x, int y, int z) {
        if (y < 0 || y >= 256) return null;

        final int chunkx = x >> 4;
        final int chunkz = z >> 4;
        try {
            if (!world.getChunkProvider().chunkExists(chunkx, chunkz)) {
                // Chunk doesn't exist - meaning, it is not loaded
                return Blocks.bedrock;
            }
            // In a typical inner loop, 80% of the time consecutive calls to this will be within the same chunk

            final Chunk cached = chunkCached.get();
            if (chunkCacheX == chunkx && chunkCacheZ == chunkz
                    && chunkCacheDim == world.provider.dimensionId
                    && cached != null
                    && cached.isChunkLoaded) {
                return cached.getBlock(x & 15, y, z & 15);
            }
            final Chunk chunk = world.getChunkFromChunkCoords(chunkx, chunkz);
            chunkCached = new WeakReference<>(chunk);
            chunkCacheDim = world.provider.dimensionId;
            chunkCacheX = chunkx;
            chunkCacheZ = chunkz;
            return chunk.getBlock(x & 15, y, z & 15);
        } catch (final Throwable throwable) {
            final CrashReport crashreport = CrashReport
                    .makeCrashReport(throwable, "Arclamp: Exception getting block type in world");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Requested block coordinates");
            crashreportcategory.addCrashSection("Location", CrashReportCategory.getLocationInfo(x, y, z));
            throw new ReportedException(crashreport);
        }
    }
}
