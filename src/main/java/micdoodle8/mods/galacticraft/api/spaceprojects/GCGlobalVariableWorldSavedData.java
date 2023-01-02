package micdoodle8.mods.galacticraft.api.spaceprojects;

import static micdoodle8.mods.galacticraft.api.spaceprojects.GCGlobalVariableStorage.*;

import com.google.common.collect.HashBiMap;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.world.WorldEvent;

public class GCGlobalVariableWorldSavedData extends WorldSavedData {

    public static GCGlobalVariableWorldSavedData INSTANCE;

    private static final String DATA_NAME = "Galacticraft_GlobalVariableWorldSavedData";

    private static final String GLOBAL_SPACE_PROJECT_NAME_NBT_TAG = "Galacticraft_GlobalSpaceProjectName_MapNBTTag";
    private static final String GLOBAL_SPACE_PROJECT_TEAM_NBT_TAG = "Galacticraft_GlobalSpaceProjectTeam_MapNBTTag";

    private static void loadInstance(World world) {
        ISpaceProject.clearGlobalSpaceElevatorInformationMap();

        MapStorage storage = world.mapStorage;
        INSTANCE = (GCGlobalVariableWorldSavedData) storage.loadData(GCGlobalVariableWorldSavedData.class, DATA_NAME);
        if (INSTANCE == null) {
            INSTANCE = new GCGlobalVariableWorldSavedData();
            storage.setData(DATA_NAME, INSTANCE);
        }
        INSTANCE.markDirty();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.world.isRemote && event.world.provider.dimensionId == 0) {
            loadInstance(event.world);
        }
    }

    public GCGlobalVariableWorldSavedData() {
        super(DATA_NAME);
    }

    public GCGlobalVariableWorldSavedData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        try {
            byte[] ba = nbtTagCompound.getByteArray(GLOBAL_SPACE_PROJECT_NAME_NBT_TAG);
            InputStream byteArrayInputStream = new ByteArrayInputStream(ba);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object data = objectInputStream.readObject();
            GlobalSpaceProjectName = (HashBiMap<String, String>) data;
        } catch (IOException | ClassNotFoundException exception) {
            System.out.println(GLOBAL_SPACE_PROJECT_NAME_NBT_TAG + " FAILED");
            exception.printStackTrace();
        }

        try {
            byte[] ba = nbtTagCompound.getByteArray(GLOBAL_SPACE_PROJECT_TEAM_NBT_TAG);
            InputStream byteArrayInputStream = new ByteArrayInputStream(ba);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object data = objectInputStream.readObject();
            GlobalSpaceProjectTeam = (HashMap<String, String>) data;
        } catch (IOException | ClassNotFoundException exception) {
            System.out.println(GLOBAL_SPACE_PROJECT_TEAM_NBT_TAG + " FAILED");
            exception.printStackTrace();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(GlobalSpaceProjectName);
            objectOutputStream.flush();
            byte[] data = byteArrayOutputStream.toByteArray();
            nbtTagCompound.setByteArray(GLOBAL_SPACE_PROJECT_NAME_NBT_TAG, data);
        } catch (Exception exception) {
            System.out.println(GLOBAL_SPACE_PROJECT_NAME_NBT_TAG + " SAVE FAILED");
            exception.printStackTrace();
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(GlobalSpaceProjectTeam);
            objectOutputStream.flush();
            byte[] data = byteArrayOutputStream.toByteArray();
            nbtTagCompound.setByteArray(GLOBAL_SPACE_PROJECT_TEAM_NBT_TAG, data);
        } catch (IOException exception) {
            System.out.println(GLOBAL_SPACE_PROJECT_TEAM_NBT_TAG + " SAVE FAILED");
            exception.printStackTrace();
        }
    }
}
