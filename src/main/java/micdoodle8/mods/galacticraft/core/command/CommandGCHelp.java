package micdoodle8.mods.galacticraft.core.command;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class CommandGCHelp extends CommandBase {

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "/" + this.getCommandName();
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
        return "gchelp";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        final EntityPlayerMP playerBase = PlayerUtil
                .getPlayerBaseServerFromPlayerUsername(icommandsender.getCommandSenderName(), true);
        if (playerBase == null) {
            return;
        }
        playerBase.addChatMessage(new ChatComponentTranslation("gui.message.help1").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE)).appendSibling(new ChatComponentText(" wiki." + GalacticraftCore.PREFIX + "com/wiki/Galacticraft").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentTranslation("gui.message.clicklink").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW)))).setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://wiki." + GalacticraftCore.PREFIX + "com/wiki/Galacticraft")))));
    }
}
