package micdoodle8.mods.galacticraft.core.command;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommandGCEnergyUnits extends CommandBase {

    @Override
    public String getCommandUsage(ICommandSender var1) {
        StringBuilder options = new StringBuilder(" [gJ");
        if (EnergyConfigHandler.isBuildcraftLoaded()) {
            options.append("|MJ");
        }
        if (EnergyConfigHandler.isIndustrialCraft2Loaded()) {
            options.append("|EU");
        }
        if (EnergyConfigHandler.isMekanismLoaded()) {
            options.append("|J");
        }
        options.append("|RF");
        return "/" + this.getCommandName()
                + options.append("]")
                         .toString();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "gcenergyunits";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        final EntityPlayerMP playerBase = PlayerUtil
                .getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), true);
        if (playerBase == null) {
            return;
        }

        if (astring.length == 1) {
            final String param = astring[0].toLowerCase();
            if (param.length() <= 2) {
                int paramvalue = 0;
                if ("gj".equals(param)) {
                    paramvalue = 1;
                } else if ("mj".equals(param) && EnergyConfigHandler.isBuildcraftLoaded()) {
                    paramvalue = 2;
                } else if ("eu".equals(param) && EnergyConfigHandler.isIndustrialCraft2Loaded()) {
                    paramvalue = 3;
                } else if ("j".equals(param) && EnergyConfigHandler.isMekanismLoaded()) {
                    paramvalue = 4;
                } else if ("rf".equals(param)) {
                    paramvalue = 5;
                }

                if (paramvalue > 0) {
                    GalacticraftCore.packetPipeline.sendTo(
                            new PacketSimple(EnumSimplePacket.C_UPDATE_ENERGYUNITS, new Object[] { paramvalue }),
                            playerBase);
                    return;
                }
            }

            throw new WrongUsageException(
                    GCCoreUtil.translateWithFormat(
                            "commands.gcenergyunits.invalidUnits",
                            this.getCommandUsage(icommandsender)));
        }

        throw new WrongUsageException(
                GCCoreUtil.translateWithFormat("commands.gcenergyunits.noUnits", this.getCommandUsage(icommandsender)));
    }

    public static void handleParamClientside(int param) {
        if (param == 1) {
            EnergyConfigHandler.displayEnergyUnitsBC = false;
            EnergyConfigHandler.displayEnergyUnitsIC2 = false;
            EnergyConfigHandler.displayEnergyUnitsMek = false;
            EnergyConfigHandler.displayEnergyUnitsRF = false;
            return;
        }

        if (param == 2 && EnergyConfigHandler.isBuildcraftLoaded()) {
            EnergyConfigHandler.displayEnergyUnitsBC = true;
            EnergyConfigHandler.displayEnergyUnitsIC2 = false;
            EnergyConfigHandler.displayEnergyUnitsMek = false;
            EnergyConfigHandler.displayEnergyUnitsRF = false;
            return;
        }

        if (param == 3 && EnergyConfigHandler.isIndustrialCraft2Loaded()) {
            EnergyConfigHandler.displayEnergyUnitsBC = false;
            EnergyConfigHandler.displayEnergyUnitsIC2 = true;
            EnergyConfigHandler.displayEnergyUnitsMek = false;
            EnergyConfigHandler.displayEnergyUnitsRF = false;
            return;
        }

        if (param == 4 && EnergyConfigHandler.isMekanismLoaded()) {
            EnergyConfigHandler.displayEnergyUnitsBC = false;
            EnergyConfigHandler.displayEnergyUnitsIC2 = false;
            EnergyConfigHandler.displayEnergyUnitsMek = true;
            EnergyConfigHandler.displayEnergyUnitsRF = false;
            return;
        }

        if (param == 5) {
            EnergyConfigHandler.displayEnergyUnitsBC = false;
            EnergyConfigHandler.displayEnergyUnitsIC2 = false;
            EnergyConfigHandler.displayEnergyUnitsMek = false;
            EnergyConfigHandler.displayEnergyUnitsRF = true;
            return;
        }
    }
}
