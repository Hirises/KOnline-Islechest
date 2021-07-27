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

import util.FileManager;

public class UserCommands implements CommandExecutor, Listener
{
	private static final HashMap<String, Player> openChestTask = new HashMap<>();	//명령어 실행 Task  <fromPlayerUUID, toPlayer>

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] add)
	{		
		if(arg.equalsIgnoreCase("섬창고")) {
			if(add.length > 0 && add[0].equalsIgnoreCase("열기")) {
				Player runPlayer = null;
				UUID fromPlayerUUID = null;
				if(sender instanceof Player) {	//플레이어가 실행하지 않았다면 취소
					runPlayer = (Player)sender;
				}else {
					return false;
				}
				
				if(add.length == 1) {	//섬창고 열기 -------------------------------------------------------
					fromPlayerUUID = runPlayer.getUniqueId();
					
					openChestTask.put(fromPlayerUUID.toString(), runPlayer);
					ASkyBlockAPI.getInstance().calculateIslandLevel(fromPlayerUUID);
					return true;
				}else if(runPlayer.isOp() && add.length == 2) {	//섬창고 열기 <플레이어 이름>    (OP만 가능)-------
					Player fromPlayer = Bukkit.getPlayer(add[1]);	 //플레이어 찾기
					if(fromPlayer == null) {
						return false;
					}
					fromPlayerUUID = fromPlayer.getUniqueId();
					
					openChestTask.put(fromPlayerUUID.toString(), runPlayer);
					ASkyBlockAPI.getInstance().calculateIslandLevel(fromPlayerUUID);
					return true;
				}
			}
		}
		return false;
	}
	
	@EventHandler
	public void onCalculateIslandLevel(IslandPreLevelEvent event) {	//섬레벨 계산이 끝나면
		UUID fromPlayerUUID = event.getPlayer();
		if(openChestTask.containsKey(fromPlayerUUID.toString())) {	//명령어 실행 Task에 플레이어가 있다면
			openOwnIslandChest(fromPlayerUUID, openChestTask.get(fromPlayerUUID.toString()), event.getLongLevel());
			openChestTask.remove(fromPlayerUUID.toString());
		}
	}
	
	private boolean openOwnIslandChest(UUID fromPlayerUUID, Player toPlayer, long islandLevel) {		//상자 열기
		if(ASkyBlockAPI.getInstance().isCoop(Bukkit.getPlayer(fromPlayerUUID))) {	//Coop이면 중지
			return false;
		}
		
		UUID islandOwnerUUID = ASkyBlockAPI.getInstance().inTeam(fromPlayerUUID) ? ASkyBlockAPI.getInstance().getTeamLeader(fromPlayerUUID) : fromPlayerUUID;	//섬이름, 주인 가져오기
		String islandName = ASkyBlockAPI.getInstance().getIslandName(islandOwnerUUID);
		
		Inventory islandChest = null;	//인벤토리 가져오기
		if(FileManager.islandChest.containsKey(islandName + '/' + islandOwnerUUID)) {
			islandChest = checkChestSize(FileManager.islandChest.get(islandName + '/' +  islandOwnerUUID), fromPlayerUUID, islandLevel);
		}else {
			islandChest = Bukkit.createInventory(null, calculateChestSize(islandLevel), islandName + "섬 창고");	//없으면 생성
			FileManager.islandChest.put(islandName + '/' +  islandOwnerUUID, islandChest);	//저장
		}
		
		toPlayer.openInventory(islandChest);
		
		return true;
	}
	
	private Inventory checkChestSize(Inventory targetInven, UUID islandOwnerUUID, long islandLevel) {	//상자크기 갱신 처리
		Inventory outputInven = targetInven;
		int targetSize = calculateChestSize(islandLevel);
		
		if(targetSize > targetInven.getSize()) {	//목표 크기보다 작으면 늘림
			outputInven = Bukkit.createInventory(null, targetSize, ASkyBlockAPI.getInstance().getIslandName(islandOwnerUUID) + "섬 창고");
			outputInven.setContents(targetInven.getContents());
			FileManager.islandChest.put(ASkyBlockAPI.getInstance().getIslandName(islandOwnerUUID) + '/' +  islandOwnerUUID, outputInven);	//저장
		}
		
		return outputInven;
	}
	
	private int calculateChestSize(long islandLevel) {	//목표 상자 크기 계산
		int size = 0;
		
		for(int i = 0; i < 6; i++) {
			if(FileManager.chestSize[i] <= islandLevel) {
				size += 9;
			}
		}
		
		return size;
	}
}
