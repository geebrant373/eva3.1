/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.model.item.function;

import static l1j.server.server.model.skill.L1SkillId.DECAY_POTION;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Item;

public class CurePotion extends L1ItemInstance {
	 private static final long serialVersionUID = 1L;
	 
	public CurePotion(L1Item item) {
		super(item);
	}

	public static void checkCondition(L1PcInstance pc, L1ItemInstance item) {
		int itemId = item.getItemId();
		
		useCurePotion(pc, itemId);
		pc.getInventory().removeItem(item, 1);
	}
	
	private static void useCurePotion(L1PcInstance pc, int item_id) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) { // 디케이포션 상태
			pc.sendPackets(new S_ServerMessage(698), true); // \f1마력에 의해 아무것도 마실수가 없습니다.
			return;
		}
		
		if (pc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CURSE_PARALYZE)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CURSE_PARALYZE2)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MOB_CURSEPARALYZ_18)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MOB_CURSEPARALYZ_19)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MOB_RANGESTUN_18)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MOB_SHOCKSTUN_30)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MOB_BASILL)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN)) {
			return;
		}
		pc.cancelAbsoluteBarrier();

		pc.sendPackets(new S_SkillSound(pc.getId(), 192), true);
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 192), true);
		pc.curePoison();
	}
}
