package se.fredsfursten.eithonfixesplugin;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import se.fredsfursten.plugintools.ConfigurableFormat;
import se.fredsfursten.plugintools.PluginConfig;
import se.fredsfursten.eithonfixesplugin.Fixes;

public final class EithonFixesPlugin extends JavaPlugin implements Listener {
	private static File EithonFixestorageFile;
	private static PluginConfig configuration;

	@Override
	public void onEnable() {
		if (configuration == null) {
			configuration = new PluginConfig(this, "config.yml");
		} else {
			configuration.load();
		}
		ConfigurableFormat.enable(getPluginConfig());

		EithonFixestorageFile = new File(getDataFolder(), "EithonFixes.bin");
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



	public static File getStorageFile()
	{
		return EithonFixestorageFile;
	}

	public static FileConfiguration getPluginConfig()
	{
		return configuration.getFileConfiguration();
	}
	
	public static void reloadConfiguration()
	{
		configuration.load();
	}
}
