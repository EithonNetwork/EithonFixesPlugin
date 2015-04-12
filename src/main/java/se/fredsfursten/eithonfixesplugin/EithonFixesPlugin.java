package se.fredsfursten.eithonfixesplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import se.fredsfursten.plugintools.Misc;
import se.fredsfursten.plugintools.PluginConfig;

public final class EithonFixesPlugin extends JavaPlugin implements Listener {
	private static PluginConfig configuration;

	@Override
	public void onEnable() {
		Misc.enable(this);
		getServer().getPluginManager().registerEvents(this, this);		
		Fixes.get().enable(this);
		Commands.get().enable(this);
	}

	@Override
	public void onDisable() {
		Fixes.get().disable();
		Commands.get().disable();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage("Incomplete command...");
			return false;
		}

		String command = args[0].toLowerCase();
		if (command.equals("buy")) {
			Commands.get().buyCommand(sender, args);
		} else if (command.equals("balance")) {
				Commands.get().balanceCommand(sender, args);
		} else {
			sender.sendMessage("Could not understand command.");
			return false;
		}
		return true;
	}
	
	public static void reloadConfiguration()
	{
		configuration.load();
	}
}
