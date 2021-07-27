package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import main.Main;
import util.FileManager;

public class AdditionalCommands implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] add)
	{
		switch(arg) {
			case "islechest":
				if(add.length == 1) {
					switch(add[0]) {
						case "reload":
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
							
							Main.SetDefault();
							FileManager.readChestSize();
							
							sender.sendMessage("플러그인 파일을 다시 로드했습니다.");
							
							return true;
						case "save":
							
							
							
							return true;
					}
				}
				break;
		}
		return false;
	}

}
