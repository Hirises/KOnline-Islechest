package commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.wasteofplastic.askyblock.ASkyBlockAPI;

import util.FileReader;

public class UserCommands implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] add)
	{
		Player senderPlayer = null;
		UUID uuid = null;
		if(sender instanceof Player) {
			senderPlayer = (Player)sender;
			uuid = senderPlayer.getUniqueId();
		}else {
			return false;
		}
		
		if(arg.equalsIgnoreCase("섬창고")) {
			if(add.length > 0 && add[0].equalsIgnoreCase("열기")) {
				if(add.length == 1) {
					openOwnIslandChest(senderPlayer, uuid);	//일반유저
				}else if(senderPlayer.isOp() && add.length == 2) {
					return true;
				}
			}
		}
		return false;
	}
	
	private int calculateChestSize(UUID uuid) {	//상자크기 계산
		ASkyBlockAPI.getInstance().calculateIslandLevel(uuid);
		long islandLevel = ASkyBlockAPI.getInstance().getLongIslandLevel(uuid);
		int size = 0;
		
		for(int i = 0; i < 6; i++) {
			if(FileReader.chestSize[i] <= islandLevel) {
				size += 9;
			}
		}
		return size;
	}
	
	private Inventory checkChestSize(Inventory inputInven, UUID uuid) {	//상자크기 갱신 처리
		Inventory outputInven = inputInven;
		int targetSize = calculateChestSize(uuid);
		
		if(targetSize > inputInven.getSize()) {
			outputInven = Bukkit.createInventory(null, targetSize, "섬 창고");
			outputInven.setContents(inputInven.getContents());
		}
		
		return outputInven;
	}
	
	private boolean openOwnIslandChest(Player sender, UUID uuid) {
		Bukkit.broadcastMessage(uuid + "//");
		Bukkit.broadcastMessage(ASkyBlockAPI.getInstance().hasIsland(uuid) + "");
		
		if(ASkyBlockAPI.getInstance().isCoop(sender)) {	//Coop이면 중지
			return false;
		}
		
		UUID islandOwner = ASkyBlockAPI.getInstance().inTeam(uuid) ? ASkyBlockAPI.getInstance().getTeamLeader(uuid) : uuid;	//섬이름, 주인 가져오기
		String islandName = ASkyBlockAPI.getInstance().getIslandName(islandOwner);
		
		Inventory islandChest = null;	//인벤토리 가져오기
		if(FileReader.islandChest.containsKey(islandName)) {
			islandChest = checkChestSize(FileReader.islandChest.get(islandName), uuid);
		}else {
			islandChest = Bukkit.createInventory(null, calculateChestSize(islandOwner), "섬 창고");	//없으면 생성
			FileReader.islandChest.put(islandName, islandChest);
		}
		
		sender.openInventory(islandChest);
		
		return true;
	}

}
