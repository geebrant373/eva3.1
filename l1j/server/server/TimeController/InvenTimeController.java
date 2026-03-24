package l1j.server.server.TimeController;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SystemMessage;

public class InvenTimeController implements Runnable {

	public static final int SLEEP_TIME = 5000;

	private static InvenTimeController _instance;

	public static InvenTimeController getInstance() {
		if (_instance == null) {
			_instance = new InvenTimeController();
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

	private void checkPremiumTime() {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc.getInventory().checkItem(120288)) {
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.지배이반버프)) {
					pc.addDamageReductionByArmor(4);
					pc.addMaxHp(200);
					pc.addDmgup(5);
					pc.addBowDmgup(5);
					pc.getAbility().addSp(5);
					pc.sendPackets(new S_SPMR(pc));
					pc.sendPackets(new S_SystemMessage("\\fT지배이반효과가 발동되었습니다."));
					pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.지배이반버프, 1000 * 60 * 60 * 24 * 1);
				}
			} else {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.지배이반버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.지배이반버프);
					pc.sendPackets(new S_SystemMessage("\\fT지배이반효과가 사라졌습니다."));
				}
			}
		}
	}
}