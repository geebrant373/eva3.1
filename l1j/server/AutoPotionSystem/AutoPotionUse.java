package l1j.server.AutoPotionSystem;

import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.DECAY_POTION;

import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.function.HealingPotion;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_Sound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;

public class AutoPotionUse {
	private L1PcInstance owner = null;

	public AutoPotionUse(L1PcInstance pc) {
		owner = pc;
	}

	public void toUseItem() {
		useHealPotion();
	}

	private void useHealPotion() { 
		
		if (owner.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
			owner.sendPackets(new S_ServerMessage(698));
			return;
		}
		if (owner.isNonAction(owner))
			return;

		cancelAbsoluteBarrier(owner);

		int itemId = owner.getAutoPotion();
		L1ItemInstance item = owner.getInventory().findItemId(itemId);
		if (item == null) { 
			return;
		}
		
		if (owner.isDead())
			return;
		if (owner.isInvisble())
			return;
		
		if (!isUseCheck(item)) { 
			return;
		}
		
		int percent = (int) Math.round(((double) owner.getCurrentHp() / (double) owner.getMaxHp()) * 100);
		if (percent > owner.getAutoPotion_Percent()) {
			return;
		}
		
		if (owner.getInventory().countItems(itemId) < 50 && owner.getInventory().countItems(itemId) > 0) {
			if (owner.isAlarmPotion()) {
				if (owner.getPotionArlamTime() == 0) {
					owner.setPotionArlamTime(System.currentTimeMillis());
				} else {
					if (System.currentTimeMillis() >= owner.getPotionArlamTime() + 2000) {
						owner.setPotionArlamTime(0);
						int sound_num = owner.get_sex() == 0 ? 21238 : 21237;
						owner.sendPackets(new S_Sound(21237));
					}
				}
			}
		} 
		//HealingPotion.clickItem(owner, item); 
	}

	private boolean isUseCheck(L1ItemInstance item) {
		int delay_id = 0;
		if (item.getItem().getType2() == 0) {
			if (item.getItem() instanceof L1EtcItem) {
				delay_id = ((L1EtcItem) item.getItem()).get_delayid();
			}
		}
		if (delay_id != 0) {
			if (owner.hasItemDelay(delay_id) == true) {
				return false;
			}
		}
		return true;
	}
	
	private void cancelAbsoluteBarrier(L1PcInstance pc) { // 아브소르트바리아의 해제
		if (pc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(ABSOLUTE_BARRIER);
		}
	}
}


