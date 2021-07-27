package commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import main.Main;
import util.FileManager;

public class AdditionalCommands implements CommandExecutor
{

	private static boolean isRunReset = false;	//리셋 확인용
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] add)
	{
		switch(arg) {
			case "islechest":
				if(add.length == 1) {
					boolean isOP = false;	//권한확인 (콘솔이거나, op거나)
					if(sender instanceof ConsoleCommandSender) {
						isOP = true;
					}else if(sender instanceof Player) {
						Player player = (Player)sender;
						isOP = player.isOp();
					}
					
					if(!isOP) {
						return false;
					}
					
					switch(add[0]) {
						case "reload":
							Main.SetDefault();
							FileManager.readChestSize();
							FileManager.readChestContents();
							
							sender.sendMessage("플러그인 파일을 다시 로드했습니다.");
							
							return true;
						case "save":
							FileManager.saveChestContents();
							
							sender.sendMessage("플러그인 파일을 저장했습니다.");
							return true;
						case "reset":
							if(isRunReset) {
								File f = new File(Main.plugin.getDataFolder().getAbsolutePath() + "\\chestsize.yml");
								YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(f);
								
								yamlFile.set("chestsize_9", 1);
								yamlFile.set("chestsize_18", 11);
								yamlFile.set("chestsize_27", 21);
								yamlFile.set("chestsize_36", 31);
								yamlFile.set("chestsize_45", 41);
								yamlFile.set("chestsize_54", 51);
								
								try
								{
									yamlFile.save(f);
								} catch (IOException e)
								{
									Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "chestsize.yml 파일을 초기화하는 중 오류가 발생했습니다.");
									e.printStackTrace();
									return false;
								}
								
								f = new File(Main.plugin.getDataFolder().getAbsolutePath() + "\\chestcontents.yml");
								yamlFile = YamlConfiguration.loadConfiguration(f);
								f.delete();
								try
								{
									f.createNewFile();
								} catch (IOException e)
								{
									Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "chestcontents.yml 파일을 초기화하는 중 오류가 발생했습니다.");
									e.printStackTrace();
								}
								
								Main.SetDefault();
								FileManager.readChestSize();
								FileManager.readChestContents();
								
								sender.sendMessage("플러그인 파일을 리셋했습니다.");
								
								isRunReset = false;
							}else {
								isRunReset = true;
								
								sender.sendMessage(ChatColor.RED + "정말로 플러그인 파일을 리셋하시겠습니까? 리셋후에는 복원 할 수 없습니다. 리셋을 원하시면 3초내에 다시 입력해주세요");
								
								Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable()
								{
									@Override
									public void run()
									{
										isRunReset = false;
									}
								}, 60);
							}
							return true;
					}
				}
				break;
		}
		return false;
	}

}
