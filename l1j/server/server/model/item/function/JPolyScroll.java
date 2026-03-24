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

import static l1j.server.server.model.skill.L1SkillId.SHAPE_CHANGE;
import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class JPolyScroll extends L1ItemInstance{
	
	public JPolyScroll(L1Item item){
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet){
		if(cha instanceof L1PcInstance){
			L1PcInstance pc = (L1PcInstance)cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = this.getItemId();
			if (usePolyScroll(pc, itemId, packet.readS())) {
				if (!pc.getInventory().checkItem(23099)) {
					pc.getInventory().removeItem(useItem, 1);
				}
			} else {
				pc.sendPackets(new S_ServerMessage(181)); // \f1 그러한 monster에게는 변신할 수 없습니다.
			}
		}
	}
	
	private boolean usePolyScroll(L1PcInstance pc, int item_id, String s) {
		
		int time = 0;
		if (item_id == 45077) { // 변신 스크롤, 상아의 탑의 변신 스크롤
			time = 1800;
		} 
		L1PolyMorph poly = PolyTable.getInstance().getTemplate(s);
		if (poly != null || s.equals("")) {			
			if (s.equals("")) {
				if (pc.getGfxId().getTempCharGfx() == 6034
						|| pc.getGfxId().getTempCharGfx() == 6035) {
					return true;
				} else {
					pc.getSkillEffectTimerSet().removeSkillEffect(SHAPE_CHANGE);
					return true;
				}
			} else if (poly.getMinLevel() == 100){
				return false;
			} else if (poly.getMinLevel() <= pc.getLevel() || pc.isGm()) {
				L1PolyMorph.doPoly(pc, poly.getPolyId(), time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}

