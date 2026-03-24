/**
 * 무인 엔피씨 상점 시작 명령어
 * by - Eva Team.
 */
package l1j.server.server.command.executor;

import java.util.logging.Logger;

import l1j.server.GameSystem.ShopNpcSystem;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1NpcShopSwitch implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1NpcShopSwitch.class.getName());

	private L1NpcShopSwitch() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1NpcShopSwitch();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			boolean power = ShopNpcSystem.getInstance().isPower();

			if(power)
				pc.sendPackets(new S_SystemMessage("엔피씨 상점을 이미 실행중입니다."));
			
			else
				pc.sendPackets(new S_SystemMessage("엔피씨 무인상점 시작합니다."));
			
				ShopNpcSystem.getInstance().npcShopStart();
			
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("영자상점 커멘드 에러"));
		}
	}
}
