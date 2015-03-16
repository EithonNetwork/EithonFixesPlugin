package se.fredsfursten.eithonfixesplugin;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Commands {
	private static Commands singleton = null;
	private static final String BUY_COMMAND = "/eithonfixes buy <player> <item> <price> <amount>";

	private JavaPlugin plugin = null;

	private Commands() {
	}

	static Commands get()
	{
		if (singleton == null) {
			singleton = new Commands();
		}
		return singleton;
	}

	void enable(JavaPlugin plugin){
		this.plugin = plugin;
	}

	void disable() {
	}

	@SuppressWarnings("deprecation")
	void buyCommand(CommandSender sender, String[] args)
	{
		if (sender instanceof Player) {
			if (!verifyPermission((Player) sender, "eithonfixes.buy")) return;
		}
		if (!arrayLengthIsWithinInterval(args, 5, 5)) {
			sender.sendMessage(BUY_COMMAND);
			return;
		}

		Player buyingPlayer = null;
		try {
			UUID id = UUID.fromString(args[1]);
			buyingPlayer = Bukkit.getPlayer(id);
		} catch (Exception e) {
		}
		if (buyingPlayer == null) buyingPlayer = Bukkit.getPlayer(args[1]);
		if (buyingPlayer == null) {
			sender.sendMessage(String.format("Unknown player: %s", args[1]));
			return;
		}

		String item = args[2];
		double pricePerItem;
		try {
			pricePerItem = Float.parseFloat(args[3]);
		} catch (Exception e) {
			sender.sendMessage(BUY_COMMAND);
			return;			
		}
		int amount;
		try {
			amount = Integer.parseInt(args[3]);
		} catch (Exception e) {
			sender.sendMessage(BUY_COMMAND);
			return;			
		}

		Fixes.get().buy(buyingPlayer, item, pricePerItem, amount);
	}

	private boolean verifyPermission(Player player, String permission)
	{
		if (player.hasPermission(permission)) return true;
		player.sendMessage("You must have permission " + permission);
		return false;
	}

	private boolean arrayLengthIsWithinInterval(Object[] args, int min, int max) {
		return (args.length >= min) && (args.length <= max);
	}
}
