package l1j.server.AutoPotionSystem;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.monitor.L1PcMonitor;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_SystemMessage;

public class AutoPotionController extends L1PcMonitor {
	public AutoPotionController(int oId) {
		super(oId);
	}

	@Override
	public void execTask(L1PcInstance pc) {
		try {
			if (owner != pc)
				owner = pc;

			getSource();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private L1PcInstance owner;

	private void getSource() {
		if (owner == null) {
			removeAuto("자동 물약 : OFF"); 
			return;
		}

		AutoPotionUse itemuse = new AutoPotionUse(owner);
		itemuse.toUseItem(); 
	}

	public void removeAuto(String ment) {
		owner.toCharacterRefresh();
		if (owner != null) {
			owner.resetAuto();
			owner.sendPackets(new S_SystemMessage(ment));
		}
		owner.EndAutoController();
	}

}
