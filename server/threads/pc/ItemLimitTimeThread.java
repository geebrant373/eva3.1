package server.threads.pc;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

public class ItemLimitTimeThread implements Runnable {

	private static ItemLimitTimeThread _instance;

	public static ItemLimitTimeThread get() {
		if (_instance == null) {
			_instance = new ItemLimitTimeThread();
		}
		return _instance;
	}

	public ItemLimitTimeThread() {
		GeneralThreadPool.getInstance().schedule(this, 1000);
	}

	@Override
	public void run() {
		try {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if (pc == null || pc.getNetConnection() == null)
					continue;
				pc.getInventory().checkEndTime();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		GeneralThreadPool.getInstance().schedule(this, 1000);
	}
}
