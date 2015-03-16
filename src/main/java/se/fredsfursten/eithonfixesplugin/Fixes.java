package se.fredsfursten.eithonfixesplugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.Misc;

public class Fixes {
	private static Fixes singleton = null;
	private static String buyCommand;
	private static String takeCommand;

	private JavaPlugin plugin = null;

	private Fixes() {
		buyCommand = EithonFixesPlugin.getPluginConfig().getString("BuyCommand");
		takeCommand = EithonFixesPlugin.getPluginConfig().getString("TakeCommand");
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
	}

	void disable() {
	}

	public void buy(Player buyingPlayer, String item, double price, int amount)
	{
		String command = String.format(takeCommand, buyingPlayer.getName(), price*amount);
		command = String.format(buyCommand, buyingPlayer.getName(), item, amount);
		Misc.executeCommand(command);
	}
}
