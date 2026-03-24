package l1j.server.autoportion;

import static l1j.server.server.model.skill.L1SkillId.POLLUTE_WATER;

import java.util.Collection;

import l1j.server.Config;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;

public class AutoPotionSystem extends Thread {

	private static AutoPotionSystem _instance;

	public static AutoPotionSystem getInstance() {
		if (_instance == null) {
			_instance = new AutoPotionSystem();
			_instance.start();
		}
		return _instance;
	}

	public AutoPotionSystem() {
		super("server.controller.AutoPotionSystem");
	}

	private Collection<L1PcInstance> list = null;

	@Override
	public void run() {
		while (true) {
			try {
				list = L1World.getInstance().getAllPlayers();
				for (L1PcInstance pc : list) {
					if (pc == null || (pc.getNetConnection() == null && !pc.noPlayerCK) || pc.isDead()
							|| pc.getSkillEffectTimerSet().hasSkillEffect(33) == true // 커스
							|| pc.getSkillEffectTimerSet().hasSkillEffect(50) == true // 아이스
							|| pc.getSkillEffectTimerSet().hasSkillEffect(66) == true // 포그
							|| pc.getSkillEffectTimerSet().hasSkillEffect(71) == true // 디케이포션
							|| pc.getSkillEffectTimerSet().hasSkillEffect(78) == true // 앱솔
							|| pc.getSkillEffectTimerSet().hasSkillEffect(87) == true // 쇼크스턴
							|| pc.getSkillEffectTimerSet().hasSkillEffect(157) == true // 어스바인드
							|| pc.getSkillEffectTimerSet().hasSkillEffect(208) == true // 본
							|| pc.isAutoPotion() == false) {
						continue;
					}
					
					if(pc.isParalyzed())
						continue;

					int MaxHp = pc.getMaxHp();
					int CurHp = pc.getCurrentHp();
					double Hp = ((double) CurHp / (double) MaxHp) * 100;
					int effect = 0;
					int heal = 0;

					switch (pc.getHealItemNum()) {
					case 40010: // 체력회복제
						heal = Config.빨갱이회복량;
						effect = 189;
						if (pc.getHealDelay() == 0) {
							pc.setHealDelay(1);
						} else {
							pc.setHealDelay(0);
						}
						break;
					case 40011: // 고급 체력 회복제
						heal = Config.주홍이회복량;
						effect = 194;
						if (pc.getHealDelay() == 0) {
							pc.setHealDelay(1);
						} else {
							pc.setHealDelay(0);
						}
						break;
					case 40012: // 강력 체력 회복제
						heal = Config.맑갱이회복량;
						effect = 197;
						if (pc.getHealDelay() == 0) {
							pc.setHealDelay(1);
						} else {
							pc.setHealDelay(0);
						}
						break;
					}

					if (pc.getHealDelay() == 0) {
						if (Hp < pc.getHealVal()) {// 물약 회복 구간 퍼센테이지.
							if (pc.getInventory().checkItem(pc.getHealItemNum(), 1)) {
								pc.getInventory().consumeItem(pc.getHealItemNum(), 1);
								UseHeallingPotion(pc, heal, effect);
							} else {
								pc.sendPackets(new S_SystemMessage("물약이 부족하여 자동물약 회복을 종료합니다."));
								pc.setAutoPotion(false);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					Thread.sleep(495);// 물약 회복 딜레이 디비와같이 500밀리세컨드로 맞춤. 빠르다싶으면
										// 늘리면됨.
					list = null;
				} catch (Exception e) {
				}
			}
		}
	}

	private void UseHeallingPotion(L1PcInstance pc, int heal, int gfxid) {
		
		// 앱솔루트베리어의 해제
		pc.cancelAbsoluteBarrier();

		pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));

		if (pc.getSkillEffectTimerSet().hasSkillEffect(POLLUTE_WATER)) { // 포르트워타중은
			// 회복량1/2배
			heal *= 0.5;
		}

		if (pc.isGm()) {
			pc.sendPackets(new S_SystemMessage("힐량 : " + heal));
		}

		pc.setCurrentHp(pc.getCurrentHp() + heal);

	}

}