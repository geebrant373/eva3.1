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
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class PolyScroll extends L1ItemInstance{
	
	public PolyScroll(L1Item item){
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet){
		if(cha instanceof L1PcInstance){
			L1PcInstance pc = (L1PcInstance)cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			
			if (pc.getMapId() == 5153 || pc.getMapId() == 5001) {
				pc.sendPackets(new S_SystemMessage("현재맵에서는 변신할 수 없습니다."));
				return;
			}
			
			int itemId = this.getItemId();
			if (usePolyScroll(pc, itemId, packet.readS())) {
				pc.getInventory().removeItem(useItem, 1);
			} else {
				pc.sendPackets(new S_ServerMessage(181)); // \f1 그러한 monster에게는 변신할 수 없습니다.
			}
		}
	}
	
	private boolean usePolyScroll(L1PcInstance pc, int item_id, String s) {
		//System.out.println("S="+s);
		int time = 0;
		if (item_id == 40088 || item_id == 40096) { // 변신 스크롤, 상아의 탑의 변신 스크롤
			time = 1800;
		} else if (item_id == 140088) { // 축복된 변신 스크롤
			time = 2100;
		}
		if(s.equalsIgnoreCase("ranking class polymorph")) {
			if(pc.getRankLevel() <= 40 || pc.getclassRankLevel() <= 3) {
				if (pc.isCrown()) {
					if (pc.get_sex() == 0)
						s = "rangking prince male";
					else
						s = "rangking prince female";
				} else if (pc.isKnight()) {
					if (pc.get_sex() == 0)
						s = "rangking knight male";
					else
						s = "rangking knight female";
				} else if (pc.isElf()) {
					if (pc.get_sex() == 0)
						s = "rangking elf male";
					else
						s = "rangking elf female";
				} else if (pc.isWizard()) {
					if (pc.get_sex() == 0)
						s = "rangking wizard male";
					else
						s = "rangking wizard female";
				} else if (pc.isDarkelf()) {
					if (pc.get_sex() == 0)
						s = "rangking darkelf male";
					else
						s = "rangking darkelf female";
				} 
			}
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
			} else 	if (poly.getPolyId() == 16074 || poly.getPolyId() == 16053 || poly.getPolyId() == 14491
					|| poly.getPolyId() == 16056 || poly.getPolyId() == 16284 || poly.getPolyId() == 16002
					|| poly.getPolyId() == 16040 || poly.getPolyId() == 16027 || poly.getPolyId() == 16014 
					|| poly.getPolyId() == 16008 || poly.getPolyId() == 15986) {
				return false;
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

