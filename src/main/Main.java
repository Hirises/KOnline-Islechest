package main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import commands.CommandTabCompleter;
import commands.UserCommands;
import util.FileManager;

public class Main extends JavaPlugin
{
	public static JavaPlugin plugin;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		
		registerCommands();
		registerEvents();
		
		SetDefault();
		FileManager.readChestSize();
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "플러그인이 활성화되었습니다");
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "플러그인이 비활성화되었습니다");
	}
	
	private void registerCommands() {
		getCommand("섬창고").setExecutor(new UserCommands());
		
		getCommand("섬창고").setTabCompleter(new CommandTabCompleter());
	}
	
	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new UserCommands(), plugin);
	}
	
	private void SetDefault() {
		FileManager.chestSize[0] = 1;
		FileManager.chestSize[1] = 11;
		FileManager.chestSize[2] = 21;
		FileManager.chestSize[3] = 31;
		FileManager.chestSize[4] = 41;
		FileManager.chestSize[5] = 51;
	}
}
