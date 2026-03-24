package l1j.server.server.serverpackets;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;

public class S_SabuTell extends ServerBasePacket {

	private static final String S_SabuTell = "[S] S_SabuTell";
	private byte[] _byte = null;

	public S_SabuTell(L1PcInstance pc) {
		if (pc.ÅÚ´ë±â() || pc.isTeleport() || pc.isDead()
				) {
			return;
		}
		if (pc.getTelType() != 4) { // »ª½ºÅÜ
			pc.getSkillEffectTimerSet().setSkillEffect(
					L1SkillId.ABSOLUTE_BARRIER, 500);
			pc.setTeleport(true);
		}
		pc.ÅÚ´ë±â(true);
		// if(pc.getTelType() == 1 || pc.getTelType() == 4 || pc.getTelType() ==
		// 10){
		
	}

	public S_SabuTell(L1PcInstance pc, int time) {
		if (pc.ÅÚ´ë±â() || pc.isTeleport() || pc.isDead()) {
			return;
		}
		if (pc.getTelType() != 4) { // »ª½ºÅÜ
		
			pc.setTeleport(true);
		}
		pc.ÅÚ´ë±â(true);
		// if(pc.getTelType() == 1 || pc.getTelType() == 4 || pc.getTelType() ==
		// 10){
		
	}

	// ¼øÁÜ °ü·Ã ÅÚ
	public S_SabuTell(L1PcInstance pc, boolean ck) {
		if (pc.ÅÚ´ë±â() || pc.isTeleport() || pc.isDead()) {
			return;
		}
		if (pc.getTelType() != 4) { // »ª½ºÅÜ
		
			pc.setTeleport(true);
		}
		pc.ÅÚ´ë±â(true);
		
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_SabuTell;
	}
}
