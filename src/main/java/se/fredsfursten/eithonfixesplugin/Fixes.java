package se.fredsfursten.eithonfixesplugin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.ConfigurableFormat;
import se.fredsfursten.plugintools.Misc;
import se.fredsfursten.plugintools.PluginConfig;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.UserDoesNotExistException;

public class Fixes {
	private static Fixes singleton = null;
	private static ConfigurableFormat giveCommandFormat;
	private static ConfigurableFormat takeCommandFormat;
	private static ConfigurableFormat youNeedMoreMoneyFormat;
	private static ConfigurableFormat successfulPurchaseFormat;
	private static ConfigurableFormat currentBalanceFormat;

	private Fixes() {
	}

	static Fixes get()
	{
		if (singleton == null) {
			singleton = new Fixes();
		}
		return singleton;
	}

	void enable(JavaPlugin plugin){
		PluginConfig config = PluginConfig.get(plugin);
		giveCommandFormat = new ConfigurableFormat(config, "GiveCommand", 3,
				"give %s %s %d");
		takeCommandFormat = new ConfigurableFormat(config, "TakeCommand", 2,
				"eco take %s %f");
		youNeedMoreMoneyFormat = new ConfigurableFormat(config, "YouNeedMoreMoneyMessage", 4,
				"You need %.2f to buy %d %s. You have %.2f.");
		successfulPurchaseFormat = new ConfigurableFormat(config, "SuccessfulPurchaseMessage", 2,
				"You successfully purchased %d item(s) of %s.");
		currentBalanceFormat = new ConfigurableFormat(config, "CurrentBalanceMessage", 1,
				"Your balance is %.2f E-Coins.");
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
						youNeedMoreMoneyFormat.getFormat(), totalPrice, amount, item, balance));
			} catch (Exception e) {
				youNeedMoreMoneyFormat.reportFailure(buyingPlayer, e);
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
			buyingPlayer.sendMessage(String.format(successfulPurchaseFormat.getFormat(), amount, item));
		} catch (Exception e) {
			successfulPurchaseFormat.reportFailure(buyingPlayer, e);
		}
	}

	private String getGiveCommand(Player buyingPlayer, String item, int amount) {
		String giveCommand;
		try {
			giveCommand = String.format(giveCommandFormat.getFormat(), buyingPlayer.getName(), item, amount);
		} catch (Exception e) {
			giveCommandFormat.reportFailure(buyingPlayer, e);
			return null;
		}
		return giveCommand;
	}

	private String getTakeCommand(Player buyingPlayer, double price) {
		String takeCommand;
		try {
			takeCommand = String.format(takeCommandFormat.getFormat(), buyingPlayer.getName(), price);
		} catch (Exception e) {
			takeCommandFormat.reportFailure(buyingPlayer, e);
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
			sender.sendMessage(String.format(currentBalanceFormat.getFormat(), balance));
		} catch (Exception e) {
			currentBalanceFormat.reportFailure(sender, e);
		}
	}
}