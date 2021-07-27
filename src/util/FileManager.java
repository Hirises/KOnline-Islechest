package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import main.Main;

public class FileManager
{
	public static final HashMap<String, Inventory> islandChest = new HashMap<>();	//상자 저장
	public static final int[] chestSize = new int[6];	//상자 레벨별 크기 (index:0 부터 오름차순)
	
	public static void readChestContents() {	//상자 내용물 읽기
		File f = new File(Main.plugin.getDataFolder().getAbsolutePath() + "\\chestcontents.yml");
		
		if(!f.exists()) {	//존재하지 않으면 생성
			try
			{
				f.createNewFile();
			} catch (IOException e)
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "chestcontents.yml 파일을 새로 생성하는 중 오류가 발생했습니다.");
				e.printStackTrace();
				return;
			}
		}
		
		YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(f);
		
		islandChest.clear();
		Set<String> keySet = yamlFile.getKeys(false);
		for(String key : keySet) {
			islandChest.put(key, UnpacktoInven(yamlFile.getString(key)));
		}
	}
	
	public static void saveChestContents()  {
		File f = new File(Main.plugin.getDataFolder().getAbsolutePath() + "\\chestcontents.yml");
		
		if(!f.exists()) {	//존재하지 않으면 생성
			try
			{
				f.createNewFile();
			} catch (IOException e)
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "chestcontents.yml 파일을 새로 생성하는 중 오류가 발생했습니다.");
				e.printStackTrace();
				return;
			}
		}
		
		YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(f);
		
		for(String key : islandChest.keySet()) {
			yamlFile.set(key, PacktoInven(islandChest.get(key)));
		}
		
		try
		{
			yamlFile.save(f);
		} catch (IOException e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "chestcontents.yml 파일을 저장하는 중 오류가 발생했습니다.");
			e.printStackTrace();
		}
	}
	
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
	
	private static String PacktoInven(Inventory input) {	//inven -> string
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            dataOutput.writeInt(input.getSize());
            
            for (int i = 0; i < input.getSize(); i++) {
                dataOutput.writeObject(input.getItem(i));
            }
            
            dataOutput.close();
            String output = Base64Coder.encodeLines(outputStream.toByteArray());
            return output + '/' + input.getTitle();
        } catch (Exception e) {
        	Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "창고 내용물을 저장하기위해 인코딩하는 과정에서 오류가 발생했습니다.");
            e.printStackTrace();
            return null;
        }
    }
	
	private static Inventory UnpacktoInven(String data) { //string -> inven
		String invenName = data.split("/")[1];
		data = data.split("/")[0];
		
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt(), invenName);
    
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            
            dataInput.close();
            return inventory;
        } catch (Exception e) {
        	Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "창고 내용물을 읽기위해 디코딩하는 과정에서 오류가 발생했습니다.");
            e.printStackTrace();
            return null;
        }
    }
}
