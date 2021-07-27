package util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import main.Main;

public class FileReader
{
	public static final HashMap<String, Inventory> islandChest = new HashMap<>();	//인벤 저장용 변수
	public static final int[] chestSize = new int[6];	//상자 크기 저장용
	
	public static void readChestInfor() {
		Bukkit.getConsoleSender().sendMessage(Main.plugin.getDataFolder().getAbsolutePath() + "\\chestsize.yml");
		File f = new File(Main.plugin.getDataFolder().getAbsolutePath() + "/chestsize.yml");
		if(!f.exists()) {
			try
			{
				createNewChestSizeFile(f);
			} catch (IOException e)
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "chestsize.yml 파일을 새로 생성하는 중 오류가 발생했습니다. " + e.getMessage());
				return;
			}
		}
		YamlConfiguration File = YamlConfiguration.loadConfiguration(f);
		
		chestSize[0] = File.getInt("chestsize_9");
		chestSize[1] = File.getInt("chestsize_18");
		chestSize[2] = File.getInt("chestsize_27");
		chestSize[3] = File.getInt("chestsize_36");
		chestSize[4] = File.getInt("chestsize_45");
		chestSize[5] = File.getInt("chestsize_54");
	}
	
	private static void createNewChestSizeFile(File f) throws IOException {
		Main.plugin.saveConfig();
		f.createNewFile();
		YamlConfiguration File = YamlConfiguration.loadConfiguration(f);
		
		File.set("chestsize_9", 1);
		File.set("chestsize_18", 11);
		File.set("chestsize_27", 21);
		File.set("chestsize_36", 31);
		File.set("chestsize_45", 41);
		File.set("chestsize_54", 51);
		
		File.save(f);
	}
}
