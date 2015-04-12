package se.fredsfursten.eithonfixesplugin;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.ConfigurableFormat;
import se.fredsfursten.plugintools.PluginConfig;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.UserDoesNotExistException;

public class Fixes {
	private static Fixes singleton = null;
	private ConfigurableFormat giveCommandFormat;
	private ConfigurableFormat takeCommandFormat;
	private ConfigurableFormat youNeedMoreMoneyFormat;
	private ConfigurableFormat successfulPurchaseFormat;
	private ConfigurableFormat currentBalanceFormat;
	private List<String> penaltyOnDeathWorlds;
	private double costOfDeath;
	private ConfigurableFormat penaltyOnDeathMessage;

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
		this.penaltyOnDeathWorlds = config.getStringList("PenaltyOnDeathWorlds");
		this.costOfDeath = config.getDouble("CostOfDeath", 30.0);
		this.penaltyOnDeathMessage = new ConfigurableFormat(config, "PenaltyOnDeathMessage", 1,
				"You death has resulted in a penalty of %.2f.");
		this.giveCommandFormat = new ConfigurableFormat(config, "GiveCommand", 3,
				"give %s %s %d");
		this.takeCommandFormat = new ConfigurableFormat(config, "TakeCommand", 2,
				"eco take %s %f");
		this.youNeedMoreMoneyFormat = new ConfigurableFormat(config, "YouNeedMoreMoneyMessage", 4,
				"You need %.2f to buy %d %s. You have %.2f.");
		this.successfulPurchaseFormat = new ConfigurableFormat(config, "SuccessfulPurchaseMessage", 2,
				"You successfully purchased %d item(s) of %s.");
		this.currentBalanceFormat = new ConfigurableFormat(config, "CurrentBalanceMessage", 1,
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
						this.youNeedMoreMoneyFormat.getFormat(), totalPrice, amount, item, balance));
			} catch (Exception e) {
				this.youNeedMoreMoneyFormat.reportFailure(buyingPlayer, e);
			}
			return;
		}

		this.takeCommandFormat.execute(buyingPlayer.getName(), totalPrice);
		this.giveCommandFormat.execute(buyingPlayer.getName(), item, amount);

		try {
			buyingPlayer.sendMessage(String.format(this.successfulPurchaseFormat.getFormat(), amount, item));
		} catch (Exception e) {
			this.successfulPurchaseFormat.reportFailure(buyingPlayer, e);
		}
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
			sender.sendMessage(String.format(this.currentBalanceFormat.getFormat(), balance));
		} catch (Exception e) {
			this.currentBalanceFormat.reportFailure(sender, e);
		}
	}

	public void playerDied(Player player) {
		for (String penaltyWorld : this.penaltyOnDeathWorlds) {
			if (penaltyWorld.equalsIgnoreCase(player.getWorld().getName())) {
				this.takeCommandFormat.execute(player.getName(), this.costOfDeath);
				this.penaltyOnDeathMessage.sendMessage(player, this.costOfDeath);
				break;
			}
		}
	}
}