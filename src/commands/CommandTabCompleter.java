package commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String arg, String[] add)	//Tab 자동완성
	{
		switch(arg) {
			case "섬창고":
				String argument = "열기";
				
				if(add.length == 1 && argument.startsWith(add[0])) {
					return Arrays.asList(argument);
				}
				
				return null;
			case "islechest":
				List<String> arguments = Arrays.asList("reload", "save");
				ArrayList<String> output = new ArrayList<>();
				
				if(add.length == 1) {
					for(String targetStr : arguments) {
						if(targetStr.startsWith(add[0])) {
							output.add(targetStr);
						}
					}
				}
				
				return output;
		}
		return null;
	}
}