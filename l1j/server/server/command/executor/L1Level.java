package l1j.server.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.IntRange;

public class L1Level implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Level.class.getName());

	private L1Level() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Level();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			String charName = tok.nextToken();
			int level = Integer.parseInt(tok.nextToken());
			int persent = 0;
			try {
				persent = Integer.parseInt(tok.nextToken());
			} catch (Exception e) {
				persent = 0;
			}
			L1PcInstance player = L1World.getInstance().getPlayer(charName);

			if (player == null) {
				pc.sendPackets(new S_SystemMessage(charName + "은(는) 월드상에 존재하지 않습니다."));
				return;
			}

			// if (level == player.getLevel()) {
			// return;
			// }
			if (!IntRange.includes(level, 1, 99)) {
				pc.sendPackets(new S_SystemMessage("레벨은 1-99의 범위에서 지정해 주세요"));
				return;
			}
			if (!IntRange.includes(persent, 0, 99)) {
				pc.sendPackets(new S_SystemMessage("퍼센트는 0-99의 범위에서 지정해 주세요"));
				
				return;
			}
			player.setExp(ExpTable.getExpByLevel(level));
			player.addExp((ExpTable.getNeedExpNextLevel(player.getLevel() + 1) / 100) * persent);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + " [캐릭명] [레벨] [퍼센트]"));
		}
	}
}
