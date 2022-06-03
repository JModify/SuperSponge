package me.modify.supersponge.commands;

import com.modify.fundamentum.text.ColorUtil;
import com.modify.fundamentum.util.PlugDebugger;
import me.modify.supersponge.SuperSponge;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SuperSpongeCommand extends BukkitCommand {

    public SuperSpongeCommand() {
        super("supersponge");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {

        int length = args.length;

        if (length == 1) {

            if (args[0].equalsIgnoreCase("reload")) {

                if (!sender.hasPermission("supersponge.reload")) {
                    sender.sendMessage(ColorUtil.format("&4&l(!) &r&cInsufficient permissions."));
                    return true;
                }

                SuperSponge.getInstance().getDataManager().reloadConfigurations();
                sender.sendMessage(ColorUtil.format("&2&l(✓) &aPlugin successfully reloaded."));

            } else if (args[0].equalsIgnoreCase("debug")) {

                if (!sender.hasPermission("supersponge.debug")) {
                    sender.sendMessage(ColorUtil.format("&4&l(!) &r&cInsufficient permissions."));
                    return true;
                }

                PlugDebugger debugger = SuperSponge.getInstance().getDebugger();
                if (debugger.isDebugMode()) {
                    debugger.setDebugMode(false);
                    sender.sendMessage(ColorUtil.format("&2&l(✓) &aPlugin debug mode disabled."));
                    return true;
                }

                sender.sendMessage(ColorUtil.format("&2&l(✓) &aPlugin debug mode enabled."));
                debugger.setDebugMode(true);
            } else {
                sendSyntaxMessage(sender);
                return true;
            }


        } else if (length == 2) {


        } else if (length == 3) {

            if (args[0].equalsIgnoreCase("give")) {

                if (!sender.hasPermission("supersponge.give")) {
                    sender.sendMessage(ColorUtil.format("&4&l(!) &r&cInsufficient permissions."));
                    return true;
                }

                Player player = Bukkit.getPlayer(args[1]);

                if (player == null) {
                    sender.sendMessage(ColorUtil.format("&4&l(!) &r&cPlayer '" + args[1] + "' is not online. "));
                    return true;
                }

                int quantity = -1;
                try {
                    quantity = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ColorUtil.format("&4&l(!) &r&cSuper sponge quantity must be an integer."));
                    return true;
                }

                ItemStack item;
                try {
                    item = SuperSponge.getInstance().getSuperSpongeManager().getSuperSpongeItem(quantity);
                } catch (InvalidConfigurationException e) {
                    sender.sendMessage(ColorUtil.format("&4&l(!) &r&cUnexpected exception occurred while giving super sponge to user. Check console log."));
                    e.printStackTrace();
                    return true;
                }

                player.getInventory().addItem(item);
                sender.sendMessage(ColorUtil.format("&2&l(✓) &aSuper sponge(s) successfully given to the target player."));
            } else {
                sendSyntaxMessage(sender);
                return true;
            }
        } else {
            sendSyntaxMessage(sender);
            return true;
        }

        return false;
    }

    private void sendSyntaxMessage(CommandSender sender) {
        sender.sendMessage(ColorUtil.format("&4&l(!) Invalid usage. Valid syntax:"));
        sender.sendMessage(ColorUtil.format("&c/supersponge give <player> <quantity>"));
        sender.sendMessage(ColorUtil.format("&c/supersponge reload"));
    }
}
