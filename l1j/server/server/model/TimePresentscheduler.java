package l1j.server.server.model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class TimePresentscheduler {

    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public static void start() {
        long initialDelay = 0; // 서버 시작 후 바로 실행 (필요시 조절 가능)
        long period = 3 * 60 * 60; // 3시간 (초 단위)

        scheduler.scheduleAtFixedRate(() -> {
            try {
            	givepresent();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, initialDelay, period, TimeUnit.SECONDS);
    }

    private static void givepresent() {
    	for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
    		if (!pc.isAutoClanjoin() && !pc.isPrivateShop() && !pc.noPlayerCK && pc != null && !pc.isDead()) {
    			pc.sendPackets(new S_SystemMessage("자동시간 충전석이 지급되었습니다."));
    			pc.getInventory().storeItem(875640508, 1);
    		}
    	}
    }
}
