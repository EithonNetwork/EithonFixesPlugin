package se.fredsfursten.eithonfixesplugin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.Misc;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.UserDoesNotExistException;

public class Fixes {
	private static Fixes singleton = null;
	private static String giveCommandFormat;
	private static String takeCommandFormat;

	private JavaPlugin plugin = null;

	private Fixes() {
		giveCommandFormat = EithonFixesPlugin.getPluginConfig().getString("GiveCommand");
		takeCommandFormat = EithonFixesPlugin.getPluginConfig().getString("TakeCommand");
	}

	static Fixes get()
	{
		if (singleton == null) {
			singleton = new Fixes();
		}
		return singleton;
	}

	void enable(JavaPlugin plugin){
		this.plugin = plugin;
		Plugin ess = plugin.getServer().getPluginManager().getPlugin("Economy");
		if (ess != null && ess.isEnabled()) {
			plugin.getLogger().info("Succesfully hooked into Essentials economy!");
		}	
	}

	void disable() {
	}

	@SuppressWarnings("deprecation")
	public void buy(Player buyingPlayer, String item, double price, int amount)
	{
		double totalPrice = amount*price;
		double balance;
		boolean hasEnough;
		String playerName = buyingPlayer.getName();
		try {
			balance = Economy.getMoney(playerName);
			hasEnough = Economy.hasEnough(playerName, totalPrice);
		} catch (UserDoesNotExistException e) {
			buyingPlayer.sendMessage(String.format("Could not find a user named \"%s\".", playerName));
			return;
		}
		if (!hasEnough) {
			buyingPlayer.sendMessage(String.format(
					"You need %.2f to buy %d %s. You have %.2f.",
					totalPrice, amount, item, balance));
			return;
		}

		String takeCommand = getTakeCommand(buyingPlayer, totalPrice);
		if (takeCommand == null) return;

		String giveCommand = getGiveCommand(buyingPlayer, item, amount);
		if (giveCommand == null) return;

		Misc.executeCommand(takeCommand);
		Misc.executeCommand(giveCommand);
		
		buyingPlayer.sendMessage(String.format(
				"You successfully purchased %d item(s) of %s.", amount, item));
	}

	private String getGiveCommand(Player buyingPlayer, String item, int amount) {
		String giveCommand;
		try {
			giveCommand = String.format(giveCommandFormat, buyingPlayer.getName(), item, amount);
		} catch (Exception e) {
			buyingPlayer.sendMessage(String.format(
					"The GiveCommand (%s) from config.yml is not correctly formatted.",
					giveCommandFormat));
			return null;
		}
		return giveCommand;
	}

	private String getTakeCommand(Player buyingPlayer, double price) {
		String takeCommand;
		try {
			takeCommand = String.format(takeCommandFormat, buyingPlayer.getName(), price);
		} catch (Exception e) {
			buyingPlayer.sendMessage(String.format(
					"The TakeCommand (%s) from config.yml is not correctly formatted.",
					takeCommandFormat));
			return null;			
		}
		return takeCommand;
	}

	@SuppressWarnings("deprecation")
	public void balance(CommandSender sender) {
		Player player = (Player)sender;
		String playerName = player.getName();
		double balance;
		try {
			balance = Economy.getMoney(playerName);
		} catch (UserDoesNotExistException e) {
			sender.sendMessage(String.format("Could not find a user named \"%s\".", playerName));
			return;
		}
		sender.sendMessage(String.format("Your balance is %.2f E-Coins.", balance));
	}
}
