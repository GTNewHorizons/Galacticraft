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

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil;

public class TileEntityArclamp extends TileEntity {

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
                switch (side) {
                    case 0:
                        this.sideRear = side; // Down
                        this.facingSide = this.facing + 2;
                        this.thisAABB = AxisAlignedBB.getBoundingBox(
                                this.xCoord - 20,
                                this.yCoord - 8,
                                this.zCoord - 20,
                                this.xCoord + 20,
                                this.yCoord + 20,
                                this.zCoord + 20);
                        break;
                    case 1:
                        this.sideRear = side; // Up
                        this.facingSide = this.facing + 2;
                        this.thisAABB = AxisAlignedBB.getBoundingBox(
                                this.xCoord - 20,
                                this.yCoord - 20,
                                this.zCoord - 20,
                                this.xCoord + 20,
                                this.yCoord + 8,
                                this.zCoord + 20);
                        break;
                    case 2:
                        this.sideRear = side; // North
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
                        break;
                    case 3:
                        this.sideRear = side; // South
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
                        break;
                    case 4:
                        this.sideRear = side; // West
                        this.facingSide = this.facing;
                        this.thisAABB = AxisAlignedBB.getBoundingBox(
                                this.xCoord - 8,
                                this.yCoord - 20,
                                this.zCoord - 20,
                                this.xCoord + 20,
                                this.yCoord + 20,
                                this.zCoord + 20);
                        break;
                    case 5:
                        this.sideRear = side; // East
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
                        break;
                    default:
                        return;
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
        final LongSet checked = new LongOpenHashSet();

        LongList currentLayer = new LongArrayList();
        LongList nextLayer = new LongArrayList();
        final long thisPackedCoord = CoordinatePacker.pack(this.xCoord, this.yCoord, this.zCoord);
        currentLayer.add(thisPackedCoord);
        final World world = this.worldObj;
        final int sideskip1 = this.sideRear;
        final int sideskip2 = this.facingSide ^ 1;
        for (int i = 0; i < 6; i++) {
            if (i != sideskip1 && i != sideskip2 && i != (sideskip1 ^ 1) && i != (sideskip2 ^ 1)) {
                final long neighborPackedCoord = getAdjacentPacked(thisPackedCoord, i);
                final Block b = getBlockAtPackedCoord(world, neighborPackedCoord);
                if (b != null && b.getLightOpacity() < 15) {
                    currentLayer.add(neighborPackedCoord);
                }
            }
        }

        long inFrontPacked = thisPackedCoord;
        for (int i = 0; i < 5; i++) {
            // Move in the facing direction and then in the direction opposite to sideSkip1
            inFrontPacked = getDoubleSidedPacked(inFrontPacked, this.facingSide, sideskip1 ^ 1);
            final Block b = getBlockAtPackedCoord(world, inFrontPacked);
            if (b != null && b.getLightOpacity() < 15) {
                currentLayer.add(inFrontPacked);
            }
        }

        int side;
        long sideBits;

        for (int count = 0; count < 14; count++) {
            LongIterator iter = currentLayer.longIterator();
            while (iter.hasNext()) {
                final long packedCoord = iter.nextLong();
                side = 0;
                sideBits = (packedCoord >> 60) & 0xF; // Extract the side bits from highest 4 bits
                boolean allAir = true;

                do {
                    // Skip the side which this was entered from
                    // and never go 'backwards'
                    if ((sideBits & (1 << side)) == 0) {
                        // Create adjacent packed coordinate with side info
                        final long sidePackedCoord = getAdjacentPacked(packedCoord, side);

                        if (!checked.contains(sidePackedCoord)) {
                            checked.add(sidePackedCoord);

                            // Get coordinates for block access
                            final int sideX = CoordinatePacker.unpackX(sidePackedCoord);
                            final int sideY = CoordinatePacker.unpackY(sidePackedCoord);
                            final int sideZ = CoordinatePacker.unpackZ(sidePackedCoord);

                            // Get block at the coordinates without loading chunks
                            final Block b = getBlockAtPackedCoord(world, sidePackedCoord);

                            if (b instanceof BlockAir) {
                                if (side != sideskip1 && side != sideskip2) {
                                    nextLayer.add(sidePackedCoord);
                                }
                            } else {
                                allAir = false;
                                if (b != null && b.getLightOpacity(world, sideX, sideY, sideZ) == 0
                                        && side != sideskip1
                                        && side != sideskip2) {
                                    nextLayer.add(sidePackedCoord);
                                }
                            }
                        }
                    }
                    side++;
                } while (side < 6);

                if (!allAir) {
                    // Get coordinates for current position
                    final int x = CoordinatePacker.unpackX(packedCoord);
                    final int y = CoordinatePacker.unpackY(packedCoord);
                    final int z = CoordinatePacker.unpackZ(packedCoord);

                    // Get block at current position
                    final Block id = getBlockAtPackedCoord(world, packedCoord);

                    if (Blocks.air == id) {
                        world.setBlock(x, y, z, brightAir, 0, 2);
                        // Since airToRestore is now a LongSet, we just add the packed coordinate
                        this.airToRestore.add(packedCoord);
                        this.markDirty();
                    } else if (id == breatheableAirID) {
                        world.setBlock(x, y, z, brightBreatheableAir, 0, 2);
                        // Since airToRestore is now a LongSet, we just add the packed coordinate
                        this.airToRestore.add(packedCoord);
                        this.markDirty();
                    }
                }
            }

            currentLayer = nextLayer;
            nextLayer = new LongArrayList(); // Use LongArrayList for packed longs
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

    /**
     * Creates a packed coordinate for a position adjacent to the given packed coordinate in the specified direction.
     * Also embeds side information in the high bits for efficient traversal.
     *
     * @param packedCoord The original packed coordinate
     * @param side        The side (0-5) to move toward
     * @return A new packed coordinate with side information
     */
    private long getAdjacentPacked(long packedCoord, int side) {
        int x = CoordinatePacker.unpackX(packedCoord);
        int y = CoordinatePacker.unpackY(packedCoord);
        int z = CoordinatePacker.unpackZ(packedCoord);

        // Extract existing side bits to preserve them
        final long existingSideBits = packedCoord & 0xF000000000000000L;

        // Store side info in high bits (4 bits): which side this position came from
        // Use 4 of the 12 bits allocated for y and side info, leaving 8 bits for y coordinates
        // Use bit pattern: (1 << (side ^ 1)) to mark the opposite side as done
        // This creates a bitmask with the bit for the opposite side set to 1
        final long newSideBits = ((long) (1 << (side ^ 1)) + ((long) side << 6)) << 60;

        // Combine with existing side bits (preserving any previously set sides)
        long sideBits = existingSideBits | newSideBits;

        switch (side) {
            case 0:
                y--;
                break;
            case 1:
                y++;
                break;
            case 2:
                z--;
                break;
            case 3:
                z++;
                break;
            case 4:
                x--;
                break;
            case 5:
                x++;
                break;
        }

        // Pack coordinates according to the new scheme (26 bits x, 26 bits z, 8 bits y)
        // and combine with the side bits in the highest 4 bits
        return CoordinatePacker.pack(x, y, z) | sideBits;
    }

    /**
     * Gets the block at the specified packed coordinate position without forcing chunk load. - Borrowed and adapted
     * from BlockVec3
     *
     * @param world       The world
     * @param packedCoord The packed coordinate
     * @return The block at the position, or null if the coordinate is invalid or chunk isn't loaded
     */
    private Block getBlockAtPackedCoord(World world, long packedCoord) {
        // Clear the high bits (where side information is stored) to get only coordinate data
        final long coordBits = packedCoord & 0x0FFFFFFFFFFFFFFFL;

        int x = CoordinatePacker.unpackX(coordBits);
        int y = CoordinatePacker.unpackY(coordBits);
        int z = CoordinatePacker.unpackZ(coordBits);

        if (y < 0 || y >= 256) {
            return null;
        }

        final int chunkx = x >> 4;
        final int chunkz = z >> 4;
        try {
            if (world.getChunkProvider().chunkExists(chunkx, chunkz)) {
                // In a typical inner loop, 80% of the time consecutive calls to
                // this will be within the same chunk
                Chunk cached = chunkCached.get();
                if (chunkCacheX == chunkx && chunkCacheZ == chunkz
                        && chunkCacheDim == world.provider.dimensionId
                        && cached != null
                        && cached.isChunkLoaded) {
                    return cached.getBlock(x & 15, y, z & 15);
                }

                Chunk chunk = world.getChunkFromChunkCoords(chunkx, chunkz);
                chunkCached = new WeakReference<>(chunk);
                chunkCacheDim = world.provider.dimensionId;
                chunkCacheX = chunkx;
                chunkCacheZ = chunkz;
                return chunk.getBlock(x & 15, y, z & 15);
            }
            // Chunk doesn't exist - meaning, it is not loaded
            return Blocks.bedrock;
        } catch (final Throwable throwable) {
            final CrashReport crashreport = CrashReport
                    .makeCrashReport(throwable, "Arclamp thread: Exception getting block type in world");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Requested block coordinates");
            crashreportcategory.addCrashSection("Location", CrashReportCategory.getLocationInfo(x, y, z));
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Creates a packed coordinate that is moved in two directions from the original coordinate. Useful for creating
     * coordinates that are moved in multiple directions.
     *
     * @param packedCoord The original packed coordinate
     * @param firstSide   The first direction to move in
     * @param secondSide  The second direction to move in
     * @return A new packed coordinate moved in both directions with side info
     */
    private long getDoubleSidedPacked(long packedCoord, int firstSide, int secondSide) {
        int x = CoordinatePacker.unpackX(packedCoord);
        int y = CoordinatePacker.unpackY(packedCoord);
        int z = CoordinatePacker.unpackZ(packedCoord);

        // Apply first side movement
        switch (firstSide) {
            case 0:
                y--;
                break;
            case 1:
                y++;
                break;
            case 2:
                z--;
                break;
            case 3:
                z++;
                break;
            case 4:
                x--;
                break;
            case 5:
                x++;
                break;
        }

        // Apply second side movement
        switch (secondSide) {
            case 0:
                y--;
                break;
            case 1:
                y++;
                break;
            case 2:
                z--;
                break;
            case 3:
                z++;
                break;
            case 4:
                x--;
                break;
            case 5:
                x++;
                break;
        }

        // Extract existing side bits to preserve them
        long existingSideBits = packedCoord & 0xF000000000000000L;

        // Store first side info in high bits (4 bits)
        long newSideBits = ((long) (1 << (firstSide ^ 1)) + ((long) firstSide << 6)) << 60;

        // Combine with existing side bits
        long sideBits = existingSideBits | newSideBits;

        return CoordinatePacker.pack(x, y, z) | sideBits;
    }

}
