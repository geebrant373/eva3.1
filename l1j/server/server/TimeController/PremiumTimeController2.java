package l1j.server.server.TimeController;

import l1j.server.Config;

import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class PremiumTimeController2 implements Runnable {

	public static final int SLEEP_TIME = Config.USERITEM1_TIME * 60000; // 원본 600초

	private static PremiumTimeController2 _instance;

	public static PremiumTimeController2 getInstance() {
		if (_instance == null) {
			_instance = new PremiumTimeController2();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			checkPremiumTime();
		} catch (Exception e1) {
		}
	}

	private void checkPremiumTime() {// 일정시간 깃털지급
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (!pc.isAutoClanjoin() && !pc.isPrivateShop() && !pc.noPlayerCK && pc != null && !pc.isDead()) {
				int FN4 = Config.useritem1;// 아이템번호
				int FN5 = Config.usercount1;// 갯수
				pc.getInventory().storeItem(FN4, FN5);
				pc.sendPackets(new S_SystemMessage("알림: 드래곤의 다이아몬드 (" + FN5 + ") 획득 하셨습니다."));
			}
		}
	}
}