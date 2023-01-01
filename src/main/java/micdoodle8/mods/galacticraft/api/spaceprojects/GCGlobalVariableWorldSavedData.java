package micdoodle8.mods.galacticraft.api.spaceprojects;

import static micdoodle8.mods.galacticraft.api.spaceprojects.GCGlobalVariableStorage.*;

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

    private static final String DATA_NAME = "GalaxySpace_GlobalVariableWorldSavedData";

    private static final String GlobalSpaceElevatorNameNBTTag = "GalaxySpace_GlobalSpaceElevatorName_MapNBTTag";
    private static final String GlobalSpaceElevatorTeamNBTTag = "GalaxySpace_GlobalSpaceElevatorTeam_MapNBTTag";

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
            byte[] ba = nbtTagCompound.getByteArray(GlobalSpaceElevatorNameNBTTag);
            InputStream byteArrayInputStream = new ByteArrayInputStream(ba);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object data = objectInputStream.readObject();
            GlobalSpaceProjectName = (HashMap<String, String>) data;
        } catch (IOException | ClassNotFoundException exception) {
            System.out.println(GlobalSpaceElevatorNameNBTTag + " FAILED");
            exception.printStackTrace();
        }

        try {
            byte[] ba = nbtTagCompound.getByteArray(GlobalSpaceElevatorTeamNBTTag);
            InputStream byteArrayInputStream = new ByteArrayInputStream(ba);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object data = objectInputStream.readObject();
            GlobalSpaceProjectTeam = (HashMap<String, String>) data;
        } catch (IOException | ClassNotFoundException exception) {
            System.out.println(GlobalSpaceElevatorTeamNBTTag + " FAILED");
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
            nbtTagCompound.setByteArray(GlobalSpaceElevatorNameNBTTag, data);
        } catch (Exception exception) {
            System.out.println(GlobalSpaceElevatorNameNBTTag + " SAVE FAILED");
            exception.printStackTrace();
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(GlobalSpaceProjectTeam);
            objectOutputStream.flush();
            byte[] data = byteArrayOutputStream.toByteArray();
            nbtTagCompound.setByteArray(GlobalSpaceElevatorTeamNBTTag, data);
        } catch (IOException exception) {
            System.out.println(GlobalSpaceElevatorTeamNBTTag + " SAVE FAILED");
            exception.printStackTrace();
        }
    }
}
