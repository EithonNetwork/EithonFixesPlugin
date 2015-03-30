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
	private static String youNeedMoreMoneyFormat;
	private static String successfulPurchaseFormat;
	private static String currentBalanceFormat;

	private JavaPlugin plugin = null;

	private Fixes() {
		giveCommandFormat = EithonFixesPlugin.getPluginConfig().getString("GiveCommand");
		if (giveCommandFormat == null) {
			giveCommandFormat = "give %s %s %d";
		}
		takeCommandFormat = EithonFixesPlugin.getPluginConfig().getString("TakeCommand");
		if (takeCommandFormat == null) {
			takeCommandFormat = "eco take %s %f";
		}
		youNeedMoreMoneyFormat = EithonFixesPlugin.getPluginConfig().getString("YouNeedMoreMoneyMessage");
		if (youNeedMoreMoneyFormat == null) {
			youNeedMoreMoneyFormat = "You need %.2f to buy %d %s. You have %.2f.";
		}
		successfulPurchaseFormat = EithonFixesPlugin.getPluginConfig().getString("SuccessfulPurchaseMessage");
		if (successfulPurchaseFormat == null) {
			successfulPurchaseFormat = "You successfully purchased %d item(s) of %s.";
		}
		currentBalanceFormat = EithonFixesPlugin.getPluginConfig().getString("CurrentBalanceMessage");
		if (currentBalanceFormat == null) {
			currentBalanceFormat = "Your balance is %.2f E-Coins.";
		}
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
			try {
				buyingPlayer.sendMessage(String.format(
						youNeedMoreMoneyFormat, totalPrice, amount, item, balance));
			} catch (Exception e) {
				Misc.configurableFormatFailed(buyingPlayer, "YouNeedMoreMoneyMessage", youNeedMoreMoneyFormat, 4, e);			
			}
			return;
		}

		String takeCommand = getTakeCommand(buyingPlayer, totalPrice);
		if (takeCommand == null) return;

		String giveCommand = getGiveCommand(buyingPlayer, item, amount);
		if (giveCommand == null) return;

		Misc.executeCommand(takeCommand);
		Misc.executeCommand(giveCommand);

		try {
			buyingPlayer.sendMessage(String.format(successfulPurchaseFormat, amount, item));
		} catch (Exception e) {
			Misc.configurableFormatFailed(buyingPlayer, "SuccessfulPurchaseMessage", successfulPurchaseFormat, 2, e);
		}
	}

	private String getGiveCommand(Player buyingPlayer, String item, int amount) {
		String giveCommand;
		try {
			giveCommand = String.format(giveCommandFormat, buyingPlayer.getName(), item, amount);
		} catch (Exception e) {
			Misc.configurableFormatFailed(buyingPlayer, "GiveCommand", giveCommandFormat, 3, e);
			return null;
		}
		return giveCommand;
	}

	private String getTakeCommand(Player buyingPlayer, double price) {
		String takeCommand;
		try {
			takeCommand = String.format(takeCommandFormat, buyingPlayer.getName(), price);
		} catch (Exception e) {
			Misc.configurableFormatFailed(buyingPlayer, "TakeCommand", takeCommandFormat, 2, e);
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
		try {
			sender.sendMessage(String.format(currentBalanceFormat, balance));
		} catch (Exception e) {
			Misc.configurableFormatFailed(sender, "CurrentBalanceMessage", currentBalanceFormat, 1, e);
		}
	}
}