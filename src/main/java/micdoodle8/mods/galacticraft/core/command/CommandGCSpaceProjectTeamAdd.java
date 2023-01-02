package micdoodle8.mods.galacticraft.core.command;

import java.util.List;
import java.util.UUID;
import micdoodle8.mods.galacticraft.api.spaceprojects.SpaceProject;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandGCSpaceProjectTeamAdd extends CommandBase {

    @Override
    public String getCommandName() {
        return "spinvite";
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
    public String getCommandUsage(ICommandSender var1) {
        return "/" + this.getCommandName() + " <User1 Joining User2> <User2's Name>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] astring) {
        String username_0 = null;
        String username_1 = null;

        if (astring.length > 1) {
            username_0 = astring[0];
            username_1 = astring[1];

            String formatted_username_0 = EnumChatFormatting.BLUE + username_0 + EnumChatFormatting.RESET;
            String formatted_username_1 = EnumChatFormatting.BLUE + username_1 + EnumChatFormatting.RESET;

            UUID uuid_0 = SpaceProject.getUUIDFromUsername(username_0);
            UUID uuid_1 = SpaceProject.getUUIDFromUsername(username_1);

            if (uuid_1.equals("") && uuid_0.equals("")) {
                if (username_0.equals(username_1)) {
                    sender.addChatMessage(
                            new ChatComponentText("User " + formatted_username_0 + " has no space project network."));
                } else {
                    sender.addChatMessage(new ChatComponentText("User " + formatted_username_0 + " and "
                            + formatted_username_1 + " have no space project networks."));
                }
                return;
            }

            if (uuid_0.equals("")) {
                sender.addChatMessage(
                        new ChatComponentText("User " + formatted_username_0 + " has no space project network."));
                return;
            }

            if (uuid_1.equals("")) {
                sender.addChatMessage(
                        new ChatComponentText("User " + formatted_username_1 + " has no space project network."));
                return;
            }

            if (uuid_0.equals(uuid_1)) {
                SpaceProject.joinUserNetwork(uuid_0, uuid_1);
                sender.addChatMessage(new ChatComponentText(
                        "User " + formatted_username_0 + " has rejoined their own space project network."));
                return;
            }

            SpaceProject.joinUserNetwork(uuid_0, uuid_1);

            sender.addChatMessage(new ChatComponentText(
                    "Success! " + formatted_username_0 + " has joined " + formatted_username_1 + "."));
            sender.addChatMessage(
                    new ChatComponentText("To undo this simply join your own network again with /spinvite "
                            + formatted_username_0 + " " + formatted_username_0 + "."));
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return par2ArrayOfStr.length <= 2 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPlayers()) : null;
    }

    protected String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }
}
