package l1j.server.GameSystem;

import java.util.ArrayList;
import java.util.Random;

import l1j.server.server.ActionCodes;
import l1j.server.server.GameServerSetting;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.ShopNpcSpawnTable;
import l1j.server.server.datatables.ShopNpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1ShopNpcInstance;
import l1j.server.server.model.gametime.BaseTime;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.gametime.TimeListener;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.templates.L1NpcShop;

public class ShopNpcSystem implements TimeListener {

	private static ShopNpcSystem _instance;
	private boolean _power = false;
	private static Random _random = new Random(System.nanoTime());
	private static ArrayList<L1ShopNpcInstance> ShopNpcList = new ArrayList<L1ShopNpcInstance>();

	private GeneralThreadPool _threadPool = GeneralThreadPool.getInstance();

	public static ShopNpcSystem getInstance() {
		if (_instance == null) {
			_instance = new ShopNpcSystem();
			RealTimeClock.getInstance().addListener(_instance);
		}
		return _instance;
	}

	static class NpcShopTimer implements Runnable {

		public NpcShopTimer() {
		}

		@Override
		public void run() {
			try {
				ArrayList<L1NpcShop> list = ShopNpcSpawnTable.getInstance().getList();
				for (int i = 0; i < list.size(); i++) {
					L1NpcShop shop = list.get(i);
					L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(shop.getNpcId());
					npc.setId(ObjectIdFactory.getInstance().nextId());
					npc.setMap(shop.getMapId());

					npc.getLocation().set(shop.getX(), shop.getY(), shop.getMapId());
					npc.getLocation().forward(5);

					npc.setHomeX(npc.getX());
					npc.setHomeY(npc.getY());
					npc.getMoveState().setHeading(shop.getHeading());

					npc.setNameId(npc.getName());
					npc.setTitle(npc.getTitle());

					L1ShopNpcInstance obj = (L1ShopNpcInstance) npc;

					obj.setShopName(shop.getShopName());

					L1World.getInstance().storeObject(npc);
					L1World.getInstance().addVisibleObject(npc);

					npc.getLight().turnOnOffLight();
					add_ShopNpc(obj);
					Thread.sleep(30);

					obj.setState(1);

					Broadcaster.broadcastPacket(npc, new S_DoActionShop(npc.getId(), ActionCodes.ACTION_Shop, shop.getShopName().getBytes()));
					GameServerSetting.getInstance().set_fakePlayerNum(GameServerSetting.getInstance().get_fakePlayerNum() + 1);
					Thread.sleep(10);
				}
				list.clear();

			} catch (Exception exception) {
				return;
			}
		}
	}

	@Override
	public void onMonthChanged(BaseTime time) {
	}

	@Override
	public void onDayChanged(BaseTime time) {
		
	}

	@Override
	public void onHourChanged(BaseTime time) {//한시간에한번갱신
		if (isPower())
			ShopNpcTable.reloding();
	}

	@Override
	public void onMinuteChanged(BaseTime time) {
	}

	public void npcShopStart() {
		NpcShopTimer ns = new NpcShopTimer();
		_threadPool.execute(ns);
		_power = true;
	}

	public boolean isPower() {
		return _power;
	}

	private L1ShopNpcInstance[] ShopNpc_list() {
		return ShopNpcList.toArray(new L1ShopNpcInstance[ShopNpcList.size()]);
	}

	private static void add_ShopNpc(L1ShopNpcInstance shop) {
		if (!ShopNpcList.contains(shop))
			ShopNpcList.add(shop);
		;
	}

	public L1ShopNpcInstance getShopNpc(String name) {
		L1ShopNpcInstance[] npc = ShopNpc_list();
		for (int i = 0; i < npc.length; i++) {
			if (npc[i].getNpcTemplate().get_name().equalsIgnoreCase(name))
				return npc[i];
		}
		return null;
	}

	public void remove(L1ShopNpcInstance npc) {
		ShopNpcList.remove(npc);
	}
}
