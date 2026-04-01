
package l1j.server.server.command.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Recall implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Recall.class.getName());

	private L1Recall() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Recall();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			Collection<L1PcInstance> targets = null;
			if (arg.equalsIgnoreCase("전체")) {
				targets = L1World.getInstance().getAllPlayers();
			} else {
				targets = new ArrayList<L1PcInstance>();
				L1PcInstance tg = L1World.getInstance().getPlayer(arg);
				if (tg == null) {
					pc.sendPackets(new S_SystemMessage("그러한 캐릭터는 없습니다. "));
					return;
				}
				targets.add(tg);
			}

			for (L1PcInstance target : targets) {	
				if (target.isPrivateShop() || target.isAutoClanjoin()){
					pc.sendPackets(new S_SystemMessage(target.getName()+" 캐릭은 개인상점모드입니다."));
					return;
				}
				L1Teleport.teleportToTargetFront(target, pc, 2);
				pc.sendPackets(new S_SystemMessage((new StringBuilder())
						.append(target.getName()).append(" 를 소환했습니다. ")
						.toString()));
				target.sendPackets(new S_SystemMessage("게임 마스터에 소환되었습니다. "));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName
					+ " [전체, 캐릭터명]으로 입력해 주세요. "));
		}
	}
}
