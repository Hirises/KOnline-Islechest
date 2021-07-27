package main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import commands.CommandTabCompleter;
import commands.UserCommands;
import util.FileReader;

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
		FileReader.readChestInfor();
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "플러그인이 활성화되었습니다");
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "플러그인이 비홝성화되었습니다");
	}
	
	private void registerCommands() {
		getCommand("섬창고").setExecutor(new UserCommands());
		
		getCommand("섬창고").setTabCompleter(new CommandTabCompleter());
	}
	
	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new UserCommands(), plugin);
	}
	
	private void SetDefault() {
		FileReader.chestSize[0] = 1;
		FileReader.chestSize[1] = 11;
		FileReader.chestSize[2] = 21;
		FileReader.chestSize[3] = 31;
		FileReader.chestSize[4] = 41;
		FileReader.chestSize[5] = 51;
	}
}
