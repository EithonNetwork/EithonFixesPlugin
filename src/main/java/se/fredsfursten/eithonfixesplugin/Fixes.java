package se.fredsfursten.eithonfixesplugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.Misc;

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
	}

	void disable() {
	}

	public void buy(Player buyingPlayer, String item, double price, int amount)
	{
		String takeCommand;
		try {
			takeCommand = String.format(takeCommandFormat, buyingPlayer.getName(), price*amount);
		} catch (Exception e) {
			buyingPlayer.sendMessage(String.format(
					"Could not buy, because the TakeCommand (%s) from config.yml is not correctly formatted.",
					takeCommandFormat));
			return;			
		}
		
		String giveCommand;
		try {
			giveCommand = String.format(giveCommandFormat, buyingPlayer.getName(), item, amount);
		} catch (Exception e) {
			buyingPlayer.sendMessage(String.format(
					"Could not buy, because the GiveCommand (%s) from config.yml is not correctly formatted.",
					giveCommandFormat));
			return;
		}
		Misc.executeCommand(takeCommand);
		Misc.executeCommand(giveCommand);
	}
}
