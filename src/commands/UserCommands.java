package commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.events.IslandPreLevelEvent;

import util.FileReader;

public class UserCommands implements CommandExecutor, Listener
{
	private static final HashMap<String, Player> openChestTask = new HashMap<>();	//fromPlayerUUID, toPlayer

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] add)
	{
		Player fromPlayer = null;
		UUID fromPlayerUUID = null;
		if(sender instanceof Player) {
			fromPlayer = (Player)sender;
			fromPlayerUUID = fromPlayer.getUniqueId();
		}else {
			return false;
		}
		
		if(arg.equalsIgnoreCase("섬창고")) {
			if(add.length > 0 && add[0].equalsIgnoreCase("열기")) {
				if(add.length == 1) {
					openChestTask.put(fromPlayerUUID.toString(), fromPlayer);
					ASkyBlockAPI.getInstance().calculateIslandLevel(fromPlayerUUID);
				}else if(fromPlayer.isOp() && add.length == 2) {
					return true;
				}
			}
		}
		return false;
	}
	
	private int calculateChestSize(UUID uuid, long islandLevel) {	//상자크기 계산
		int size = 0;
		
		for(int i = 0; i < 6; i++) {
			if(FileReader.chestSize[i] <= islandLevel) {
				size += 9;
			}
		}
		return size;
	}
	
	private Inventory checkChestSize(Inventory inputInven, UUID uuid, long islandLevel) {	//상자크기 갱신 처리
		Inventory outputInven = inputInven;
		int targetSize = calculateChestSize(uuid, islandLevel);
		
		if(targetSize > inputInven.getSize()) {
			outputInven = Bukkit.createInventory(null, targetSize, "섬 창고");
			outputInven.setContents(inputInven.getContents());
			FileReader.islandChest.put(ASkyBlockAPI.getInstance().getIslandName(uuid), outputInven);	//저장
		}
		
		return outputInven;
	}
	
	@EventHandler
	public void onCalculateIslandLevel(IslandPreLevelEvent event) {
		UUID fromPlayerUUID = event.getPlayer();
		if(openChestTask.containsKey(fromPlayerUUID.toString())) {
			openOwnIslandChest(fromPlayerUUID, openChestTask.get(fromPlayerUUID.toString()), event.getLongLevel());
			openChestTask.remove(fromPlayerUUID.toString());
		}
	}
	
	private boolean openOwnIslandChest(UUID fromPlayerUUID, Player toPlayer, long islandLevel) {		
		if(ASkyBlockAPI.getInstance().isCoop(Bukkit.getPlayer(fromPlayerUUID))) {	//Coop이면 중지
			return false;
		}
		
		UUID islandOwner = ASkyBlockAPI.getInstance().inTeam(fromPlayerUUID) ? ASkyBlockAPI.getInstance().getTeamLeader(fromPlayerUUID) : fromPlayerUUID;	//섬이름, 주인 가져오기
		String islandName = ASkyBlockAPI.getInstance().getIslandName(islandOwner);
		
		Inventory islandChest = null;	//인벤토리 가져오기
		if(FileReader.islandChest.containsKey(islandName)) {
			islandChest = checkChestSize(FileReader.islandChest.get(islandName), fromPlayerUUID, islandLevel);
		}else {
			islandChest = Bukkit.createInventory(null, calculateChestSize(islandOwner, islandLevel), "섬 창고");	//없으면 생성
			FileReader.islandChest.put(islandName, islandChest);	//저장
		}
		
		toPlayer.openInventory(islandChest);
		
		return true;
	}

}
