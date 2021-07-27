package util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import main.Main;

public class FileManager
{
	public static final HashMap<String, Inventory> islandChest = new HashMap<>();	//상자 저장
	public static final int[] chestSize = new int[6];	//상자 레벨별 크기 (index:0 부터 오름차순)
	
	public static void readChestSize() {	//레벨별 상자 크기 읽기
		File f = new File(Main.plugin.getDataFolder().getAbsolutePath() + "\\chestsize.yml");
		
		if(!f.exists()) {	//존재하지 않으면 생성
			try
			{
				createNewChestSizeFile(f);
			} catch (IOException e)
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "chestsize.yml 파일을 새로 생성하는 중 오류가 발생했습니다.");
				e.printStackTrace();
				return;
			}
		}
		
		YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(f);
		
		chestSize[0] = yamlFile.getInt("chestsize_9");
		chestSize[1] = yamlFile.getInt("chestsize_18");
		chestSize[2] = yamlFile.getInt("chestsize_27");
		chestSize[3] = yamlFile.getInt("chestsize_36");
		chestSize[4] = yamlFile.getInt("chestsize_45");
		chestSize[5] = yamlFile.getInt("chestsize_54");
	}
	
	private static void createNewChestSizeFile(File f) throws IOException {	//기본 chestsize.yml 파일 생성
		Main.plugin.saveConfig();
		f.createNewFile();
		YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(f);
		
		yamlFile.set("chestsize_9", 1);
		yamlFile.set("chestsize_18", 11);
		yamlFile.set("chestsize_27", 21);
		yamlFile.set("chestsize_36", 31);
		yamlFile.set("chestsize_45", 41);
		yamlFile.set("chestsize_54", 51);
		
		yamlFile.save(f);
	}
}
