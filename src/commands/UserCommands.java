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
					sender.sendMessage("콘솔이나 커멘드에선 실행이 불가능합니다");
					return true;
				}
				
				if(add.length == 1) {	//섬창고 열기 -------------------------------------------------------
					fromPlayerUUID = runPlayer.getUniqueId();
					
					openChestTask.put(fromPlayerUUID.toString(), runPlayer);
					ASkyBlockAPI.getInstance().calculateIslandLevel(fromPlayerUUID);
					return true;
				}else if(add.length == 2) {	//섬창고 열기 <플레이어 이름>    (OP만 가능)-------
					if(runPlayer.isOp()) {
						sender.sendMessage("다른 플레이어의 섬 창고는 OP만 열 수 있습니다");
						return true;
					}

					Player fromPlayer = Bukkit.getPlayer(add[1]);	 //플레이어 찾기
					if(fromPlayer == null) {
						sender.sendMessage("존재하지 않는 플레이어 이름입니다");
						return true;
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
	
	private void openOwnIslandChest(UUID fromPlayerUUID, Player toPlayer, long islandLevel) {		//상자 열기
		if(ASkyBlockAPI.getInstance().isCoop(Bukkit.getPlayer(fromPlayerUUID))) {	//Coop이면 중지
			toPlayer.sendMessage("Coop 맴버는 섬 창고에 접근할 수 없습니다");
			return;
		}
		
		UUID islandOwnerUUID = ASkyBlockAPI.getInstance().inTeam(fromPlayerUUID) ? ASkyBlockAPI.getInstance().getTeamLeader(fromPlayerUUID) : fromPlayerUUID;	//섬이름, 주인 가져오기
		String islandName = ASkyBlockAPI.getInstance().getIslandName(islandOwnerUUID);
		
		Inventory islandChest = null;	//인벤토리 가져오기
		if(FileManager.islandChest.containsKey(islandName + '/' + islandOwnerUUID)) {
			islandChest = updateChest(FileManager.islandChest.get(islandName + '/' +  islandOwnerUUID), fromPlayerUUID, islandLevel);
		}else {
			islandChest = Bukkit.createInventory(null, calculateChestSize(islandLevel), islandName + "의 섬 창고");	//없으면 생성
			FileManager.islandChest.put(islandName + '/' +  islandOwnerUUID, islandChest);	//저장
		}
		
		toPlayer.openInventory(islandChest);
		
		return;
	}
	
	private Inventory updateChest(Inventory targetInven, UUID islandOwnerUUID, long islandLevel) {	//상자 갱신 처리
		Inventory outputInven = targetInven;
		int targetSize = calculateChestSize(islandLevel);
		
		if(targetSize > targetInven.getSize()) {	//목표 크기보다 작으면 늘림
			outputInven = Bukkit.createInventory(null, targetSize, ASkyBlockAPI.getInstance().getIslandName(islandOwnerUUID) + "의 섬 창고");
			outputInven.setContents(targetInven.getContents());
			FileManager.islandChest.put(ASkyBlockAPI.getInstance().getIslandName(islandOwnerUUID) + '/' +  islandOwnerUUID, outputInven);	//저장
		}else if(!targetInven.getTitle().equalsIgnoreCase(ASkyBlockAPI.getInstance().getIslandName(islandOwnerUUID) + "의 섬 창고")) {	//이름 갱신
			outputInven = Bukkit.createInventory(null, targetSize, ASkyBlockAPI.getInstance().getIslandName(islandOwnerUUID) + "의 섬 창고");
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
