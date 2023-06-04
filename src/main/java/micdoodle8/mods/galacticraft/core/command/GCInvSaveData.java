package micdoodle8.mods.galacticraft.core.command;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;

public class GCInvSaveData extends WorldSavedData {

    public static final String SAVE_ID = "GCInv_savefile";

    public GCInvSaveData() {
        super(SAVE_ID);
    }

    public GCInvSaveData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound filedata) {
        for (final Object obj : filedata.func_150296_c()) {
            if (obj instanceof NBTTagList entry) {
                final String name = entry.toString(); // TODO See if this is equivilent to 1.6's getName function
                final ItemStack[] saveinv = new ItemStack[6];
                if (entry.tagCount() > 0) {
                    for (int j = 0; j < entry.tagCount(); j++) {
                        final NBTTagCompound obj1 = entry.getCompoundTagAt(j);

                        if (obj1 != null) {
                            final int i = obj1.getByte("Slot") & 7;
                            if (i >= 6) {
                                System.out.println("GCInv error retrieving savefile: slot was outside range 0-5");
                                return;
                            }
                            saveinv[i] = ItemStack.loadItemStackFromNBT(obj1);
                        }
                    }
                }
                CommandGCInv.savedata.put(name.toLowerCase(), saveinv);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound toSave) {
        for (final String name : CommandGCInv.savedata.keySet()) {
            final NBTTagList par1NBTTagList = new NBTTagList();
            final ItemStack[] saveinv = CommandGCInv.savedata.get(name);

            for (int i = 0; i < 6; i++) {
                if (saveinv[i] != null) {
                    final NBTTagCompound nbttagcompound = new NBTTagCompound();
                    nbttagcompound.setByte("Slot", (byte) (i + 200));
                    saveinv[i].writeToNBT(nbttagcompound);
                    par1NBTTagList.appendTag(nbttagcompound);
                }
            }
            toSave.setTag(name, par1NBTTagList);
        }
    }
}
