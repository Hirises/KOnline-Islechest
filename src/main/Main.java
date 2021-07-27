package main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import commands.AdditionalCommands;
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
		FileManager.readChestSize();	//파일 읽어오기
		FileManager.readChestContents();
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "플러그인이 활성화되었습니다");
	}
	
	@Override
	public void onDisable()
	{
		FileManager.saveChestContents();
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "플러그인이 비활성화되었습니다");
	}
	
	private void registerCommands() {
		getCommand("섬창고").setExecutor(new UserCommands());
		getCommand("islechest").setExecutor(new AdditionalCommands());
		
		getCommand("섬창고").setTabCompleter(new CommandTabCompleter());
		getCommand("islechest").setTabCompleter(new CommandTabCompleter());
	}
	
	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new UserCommands(), plugin);
	}
	
	public static void SetDefault() {	//기본값으로 설정
		FileManager.chestSize[0] = 1;
		FileManager.chestSize[1] = 11;
		FileManager.chestSize[2] = 21;
		FileManager.chestSize[3] = 31;
		FileManager.chestSize[4] = 41;
		FileManager.chestSize[5] = 51;
	}
}
