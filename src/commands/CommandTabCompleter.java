package commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String arg, String[] add)
	{
		if(arg.equalsIgnoreCase("섬창고")) {
			String arguments = "열기";
			if(add.length == 1 && arguments.startsWith(add[0])) {
				return Arrays.asList(arguments);
			}
		}
		return null;
	}
}