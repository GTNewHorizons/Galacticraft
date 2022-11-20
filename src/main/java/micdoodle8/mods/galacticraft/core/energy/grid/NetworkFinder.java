package micdoodle8.mods.galacticraft.core.energy.grid;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class NetworkFinder {
    public World worldObj;
    public BlockVec3 start;
    private final BlockVec3 toIgnore;

    private final Set<BlockVec3> iterated = new HashSet<>();
    public List<IConductor> found = new LinkedList<>();

    public NetworkFinder(World world, BlockVec3 location, BlockVec3 ignore) {
        this.worldObj = world;
        this.start = location;

        this.toIgnore = ignore;
    }

    private void loopAll(int x, int y, int z, int dirIn) {
        BlockVec3 obj = null;
        for (int dir = 0; dir < 6; dir++) {
            if (dir == dirIn) {
                continue;
            }
            switch (dir) {
                case 0:
                    obj = new BlockVec3(x, y - 1, z);
                    break;
                case 1:
                    obj = new BlockVec3(x, y + 1, z);
                    break;
                case 2:
                    obj = new BlockVec3(x, y, z - 1);
                    break;
                case 3:
                    obj = new BlockVec3(x, y, z + 1);
                    break;
                case 4:
                    obj = new BlockVec3(x - 1, y, z);
                    break;
                case 5:
                    obj = new BlockVec3(x + 1, y, z);
                    break;
            }

            if (!this.iterated.contains(obj)) {
                this.iterated.add(obj);

                final TileEntity tileEntity = this.worldObj.getTileEntity(obj.x, obj.y, obj.z);

                if (tileEntity instanceof IConductor) {
                    this.found.add((IConductor) tileEntity);
                    this.loopAll(obj.x, obj.y, obj.z, dir ^ 1);
                }
            }
        }
    }

    public List<IConductor> exploreNetwork() {
        if (this.start.getTileEntity(this.worldObj) instanceof IConductor) {
            this.iterated.add(this.start);
            this.iterated.add(this.toIgnore);
            this.found.add((IConductor) this.start.getTileEntity(this.worldObj));
            this.loopAll(this.start.x, this.start.y, this.start.z, 6);
        }

        return this.found;
    }
}
