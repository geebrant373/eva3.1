/**package l1j.server;

import java.util.ArrayList;

import l1j.server.server.ActionCodes;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcShopSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.ShopPointTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1NpcShopInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1NpcShop;

public class GmShopSystem{
	private static GmShopSystem _instance;
	
	public static GmShopSystem getInstance() {
		if (_instance == null) {
			_instance = new GmShopSystem();
		}
		return _instance;
	}
	
	private static ArrayList<L1NpcInstance> _shops = new ArrayList<L1NpcInstance>();
	
	private GmShopSystem() {
		
	}
	
	public void GmShopAllSpwan(L1PcInstance pc) {
		try {
			for(int i = 0; i < _shops.size(); i++) {
				L1NpcInstance npc = _shops.get(i);
				if(npc == null) {
					continue;
				}
				npc.deleteMe();
				_shops.remove(npc);
			}
			
			int shop_x = 0, shop_y = 0, shop_m = 0;
			
			for(L1NpcShop shop : NpcShopSpawnTable.getInstance().getList()) {
				shop_x = shop.getX();
				shop_y = shop.getY();
				shop_m = shop.getMapId();
				//지정된 좌표가 등록되어있는가.
				boolean ck1 = false;
				for(ShopPointTable.S_Point temp : ShopPointTable.getInstance().getSlist()) {
					if(temp.LocX == shop_x && temp.LocY == shop_y && temp.LocM == shop_m) {
						ck1 = true;
						break;
					}
				}
				//좌표가 등록되어있다.
				if(ck1) {
					//해당 좌표에 유저가 존재하는지 체크한다.
					if(UseCheck(shop_x, shop_y, shop_m)) { //유저가 존재하지 않는다 바로 스폰.
						SpawnShop(shop, shop_x,shop_y,shop_m);
					}else { //유저가 존재한다. 스폰가능한 좌표를 찾는다.
						for(ShopPointTable.S_Point temp : ShopPointTable.getInstance().getSlist()) {
							if(UseCheck(temp.LocX, temp.LocY, temp.LocM)) {
								//스폰
								SpawnShop(shop, temp.LocX,temp.LocY,temp.LocM);
								break;
							}
						}
					}
				}else { //좌표가 존재하지 않는다. 스폰가능한 좌표를 찾는다.
					for(ShopPointTable.S_Point temp : ShopPointTable.getInstance().getSlist()) {
						if(UseCheck(temp.LocX, temp.LocY, temp.LocM)) {
							//스폰
							SpawnShop(shop, temp.LocX,temp.LocY,temp.LocM);
							break;
						}
					}
				}
				
			}
			
			pc.sendPackets(new S_SystemMessage("영자상점 " + _shops.size() + "명이 스폰되었습니다."));
		}catch (Exception e) {

		}
	}
	
	public void SpawnShop(L1NpcShop shop, int x, int y, int mapid) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(shop.getNpcId());
			
			npc.setId(IdFactory.getInstance().nextId());
			npc.setMap((short) mapid);
			npc.getLocation().set(x, y, mapid);
			npc.setHomeX(x);
			npc.setHomeY(y);
			npc.setHeading(shop.getHeading());
			
			npc.setName(shop.getName());
			npc.setTitle(shop.getTitle());
			
			L1NpcShopInstance obj = (L1NpcShopInstance) npc;
			obj.setShopName(shop.getShopName());
			
			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);
			
			obj.setState(1);
			npc.broadcastPacket(new S_DoActionShop(npc.getId(), ActionCodes.ACTION_Shop, shop.getShopName().getBytes()));
			_shops.add(npc);
		}catch (Exception e) {

		}
	}
	
	public boolean UseCheck(int x, int y, int mapid) {
		for(L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if(pc.getMapId() == mapid) {
				if(pc.getX() == x && pc.getY() == y) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void GmShopNameSpwan(L1PcInstance pc, String name) {
		try {
			for(L1NpcInstance npc : _shops) {
				if(npc == null) {
					continue;
				}
				if(npc.getName().equalsIgnoreCase(name)) {
					_shops.remove(npc);
					npc.deleteMe();
					break;
				}
			}
			
			L1NpcShop shop = NpcShopSpawnTable.getInstance().getShopNpc(name);
			
			int shop_x = 0, shop_y = 0, shop_m = 0;
			shop_x = shop.getX();
			shop_y = shop.getY();
			shop_m = shop.getMapId();
			//지정된 좌표가 등록되어있는가.
			boolean ck1 = false;
			for(ShopPointTable.S_Point temp : ShopPointTable.getInstance().getSlist()) {
				if(temp.LocX == shop_x && temp.LocY == shop_y && temp.LocM == shop_m) {
					ck1 = true;
					break;
				}
			}
			//좌표가 등록되어있다.
			if(ck1) {
				//해당 좌표에 유저가 존재하는지 체크한다.
				if(UseCheck(shop_x, shop_y, shop_m)) { //유저가 존재하지 않는다 바로 스폰.
					SpawnShop(shop, shop_x,shop_y,shop_m);
					pc.sendPackets(new S_SystemMessage(name + " 영자상점이 스폰되었습니다."));
				}else { //유저가 존재한다. 스폰가능한 좌표를 찾는다.
					for(ShopPointTable.S_Point temp : ShopPointTable.getInstance().getSlist()) {
						if(UseCheck(temp.LocX, temp.LocY, temp.LocM)) {
							//스폰
							SpawnShop(shop, temp.LocX,temp.LocY,temp.LocM);
							pc.sendPackets(new S_SystemMessage(name + " 영자상점이 스폰되었습니다."));
							break;
						}
					}
				}
			}else { //좌표가 존재하지 않는다. 스폰가능한 좌표를 찾는다.
				for(ShopPointTable.S_Point temp : ShopPointTable.getInstance().getSlist()) {
					if(UseCheck(temp.LocX, temp.LocY, temp.LocM)) {
						//스폰
						SpawnShop(shop, temp.LocX,temp.LocY,temp.LocM);
						pc.sendPackets(new S_SystemMessage(name + " 영자상점이 스폰되었습니다."));
						break;
					}
				}
			}
//			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(shop.getNpcId());
//			npc.setId(IdFactory.getInstance().nextId());
//			npc.setMap(shop.getMapId());
//			npc.getLocation().set(shop.getX(), shop.getY(), shop.getMapId());
//			//npc.getLocation().forward(5);
//			npc.setHomeX(npc.getX());
//			npc.setHomeY(npc.getY());
//			npc.setHeading(shop.getHeading());
//			npc.setName(shop.getName());
//			npc.setTitle(shop.getTitle());
//			
//			L1NpcShopInstance obj = (L1NpcShopInstance) npc;
//			obj.setShopName(shop.getShopName());
//			
//			L1World.getInstance().storeObject(npc);
//			L1World.getInstance().addVisibleObject(npc);
//			
//			obj.setState(1);
//			npc.broadcastPacket(new S_DoActionShop(npc.getId(), ActionCodes.ACTION_Shop, shop.getShopName().getBytes()));
//			_shops.add(npc);
			
//			pc.sendPackets(new S_SystemMessage(name + " 영자상점이 스폰되었습니다."));
		}catch (Exception e) {

		}
	}
	
	public void GmShopNameDelete(L1PcInstance pc, String name) {
		try {
			for(L1NpcInstance npc : _shops) {
				if(npc == null) {
					continue;
				}
				if(npc.getName().equalsIgnoreCase(name)) {
					_shops.remove(npc);
					npc.deleteMe();
					break;
				}
			}
			pc.sendPackets(new S_SystemMessage(name + " 영자상점이 삭제되었습니다."));
		}catch (Exception e) {

		}
	}
	
	public void GmShopAllDelete(L1PcInstance pc) {
		try {
			for(int i = 0; i < _shops.size(); i++) {
				L1NpcInstance npc = _shops.get(i);
				if(npc == null) {
					continue;
				}
				npc.deleteMe();
				_shops.remove(npc);
			}
			pc.sendPackets(new S_SystemMessage("영자상점이 모두 삭제되었습니다."));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<L1NpcInstance> getGmShop(){
		return _shops;
	}
}*/
