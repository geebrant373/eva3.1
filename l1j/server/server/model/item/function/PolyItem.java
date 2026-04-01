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

import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ShowPolyList;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class PolyItem extends L1ItemInstance{
	
	public PolyItem(L1Item item){
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet){
		if(cha instanceof L1PcInstance){
			L1PcInstance pc = (L1PcInstance)cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = this.getItemId();
			if (itemId == 41154 // 어둠의 비늘
					|| itemId == 41155 //랭킹변신 주문서
					|| itemId == 451000 // 드래곤슬레이어변신카드 1회용
					|| itemId == 41156 // 배덕자의 비늘
					|| itemId == 41157// 증오의 비늘
					|| itemId == 41143 // 러버 얼간이 변신 일부
					|| itemId == 41144 // 라바본아챠 변신 일부
					|| itemId == 41145) {  //라버본나이트
				usePolyItem(pc, itemId);
				pc.getInventory().removeItem(useItem, 1);
			} else if (itemId == L1ItemId.SHARNA_POLYSCROLL_LV30 // 샤르나의 변신 주문서 (레벨 30)
					|| itemId == L1ItemId.SHARNA_POLYSCROLL_LV40 // 샤르나의 변신 주문서 (레벨 40)
					|| itemId == L1ItemId.SHARNA_POLYSCROLL_LV52		//샤르나의 변신 주문서 (레벨 52)
					|| itemId == L1ItemId.SHARNA_POLYSCROLL_LV55		//샤르나의 변신 주문서 (레벨 55)
					|| itemId == L1ItemId.SHARNA_POLYSCROLL_LV60		//샤르나의 변신 주문서 (레벨 60)
					|| itemId == L1ItemId.SHARNA_POLYSCROLL_LV65  //샤르나의 변신 주문서 (레벨 65)
					|| itemId == L1ItemId.SHARNA_POLYSCROLL_LV70) { //샤르나의 변신 주문서 (레벨 70)
				useLevelPolyScroll(pc, itemId);
				pc.getInventory().removeItem(useItem, 1);	
			} else if (itemId == L1ItemId.POLYSCROLL_ARC){
				pc.sendPackets(new S_ShowPolyList(pc.getId(),"archmonlist"));
				if (!pc.isArchShapeChange()) {
					pc.setArchShapeChange(true);
					pc.setArchPolyType(true);
				}
				pc.getInventory().removeItem(useItem, 1);
			} else if (itemId == L1ItemId.POLYBOOK_ARC){
				pc.sendPackets(new S_ShowPolyList(pc.getId(),"archmonlist"));
				if (!pc.isArchShapeChange()) {
					pc.setArchShapeChange(true);
					pc.setArchPolyType(false);
				}
			} 
			else if (itemId == 451001 /* && itemId == 455555 */){
				usePolyItem(pc, itemId);
			}
					
		}
	}

	private void usePolyItem(L1PcInstance pc, int itemId) {
		
		int polyId = 0;
		int time = 0;
		if (itemId == 41154) { // 어둠의 비늘
			polyId = 3101;
			time = 600;
		} else if (itemId == 451001) { // 랭킹변신주문서-부적으로 0310업뎃
		
		} if (pc.isWizard()) {
			if (pc.get_sex() == 0) {
				polyId = 20787;
				time = 7200;					
							 	
					//L1PolyMorph.doPoly(pc, polyId, time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
			}else {
				polyId = 20789;
				time = 7200;
				
				}		
					//L1PolyMorph.doPoly(pc, polyId, time, L1PolyMorph.MORPH_BY_ITEMMAGIC);	
				}
			if (pc.isCrown()) {
				if (pc.get_sex() == 0) {
					polyId = 20775;
					time = 7200;
					//L1PolyMorph.doPoly(pc, polyId, time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
				} else {
					polyId = 20777;
					time = 7200;
				}
					//L1PolyMorph.doPoly(pc, polyId, time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
				
			}  if (pc.isKnight()) {
				if (pc.get_sex() == 0) {
					polyId = 20779;
					time = 7200;
				//	L1PolyMorph.doPoly(pc, polyId, time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
				} else {
					polyId = 20781;
					time = 7200;
				}
				//L1PolyMorph.doPoly(pc, polyId, time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
				
			} if (pc.isElf()) {
				if (pc.get_sex() == 0) {
					polyId = 20783;
					time = 7200;
				//	L1PolyMorph.doPoly(pc, polyId, time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
				} 							
				else {
					polyId = 20785;
					time = 7200;			
				}
				L1PolyMorph.doPoly(pc, polyId, time, L1PolyMorph.MORPH_BY_ITEMMAGIC);				
			
			} if (pc.isDarkelf()) {
				if (pc.get_sex() == 0) {
					polyId = 20791;
					time = 7200;
					L1PolyMorph.doPoly(pc, polyId, time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
				} 			
				
				else {
					
					polyId = 20793;
					time = 7200;
					
				}
					L1PolyMorph.doPoly(pc, polyId, time, L1PolyMorph.MORPH_BY_ITEMMAGIC);

			
		}  else if (itemId == 41155) { // 랭킹변신주문서 소모성
			if (pc.isCrown()) {
				if (pc.get_sex() == 0) {
					polyId = 20775;
					time = 1800;
				} else {
					polyId = 20777;
					time = 1800;
				}
			} else if (pc.isKnight()) {
				if (pc.get_sex() == 0) {
					polyId = 20779;
					time = 1800;
				} else {
					polyId = 20781;
					time = 1800;
				}
			} else if (pc.isElf()) {
				if (pc.get_sex() == 0) {
					polyId = 20783;
					time = 1800;
				} else {
					polyId = 20785;
					time = 1800;
				}

			} else if (pc.isWizard()) {
				if (pc.get_sex() == 0) {
					polyId = 20787;
					time = 1800;
				} else {
					polyId = 20789;
					time = 1800;
				}

			} else if (pc.isDarkelf()) {
				if (pc.get_sex() == 0) {
					polyId = 20791;
					time = 1800;
				} else {
					polyId = 20793;
					time = 1800;
				}
			}
		} else if (itemId == 41156) { // 배덕자의 비늘
			polyId = 3888;
			time = 600;
		} else if (itemId == 41157) { // 증오의 비늘 플래데스변신
			polyId = 13858;
			time = 1800;
		} else if (itemId == 41143) {
			polyId = 6086;
			time = 1800;
		} else if (itemId == 41144) {
			polyId = 6087;
			time = 1800;
		} else if (itemId == 41145) {
			polyId = 6088;
			time = 1800;
		}
		 else if (itemId == 451000) { //드래곤 슬레이어 변신 카드 1회용
				polyId = 14491;
				time = 1800;
		}
		
		// else if (itemId == 451001) { //드래곤 슬레이어 변신 카드 무제한
		//		polyId = 14491;
		//		time = 1800;
	//		}
			/*
			 * else if (itemId == 455555) { //운영자 polyId = 1080; time = 1800; }
			 */
		L1PolyMorph.doPoly(pc, polyId, time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
		
		
	}

	private void useLevelPolyScroll(L1PcInstance pc, int itemId) {
		int polyId = 0;
		if (itemId == L1ItemId.SHARNA_POLYSCROLL_LV30) { // 30
			if(pc.isCrown()) {
				if (pc.get_sex() == 0) {
					polyId = 6822;
				} else {
					polyId = 6823;
				}
			} else if (pc.isKnight()) {
				if (pc.get_sex() == 0) {
					polyId = 6824;
				} else {
					polyId = 6825;
				}
			} else if (pc.isElf()) {
				if (pc.get_sex() == 0) {
					polyId = 6826;
				} else {
					polyId = 6827;
				}
			} else if (pc.isWizard()) {
				if (pc.get_sex() == 0) {
					polyId = 6828;
				} else {
					polyId = 6829;
				}
			} else if (pc.isDarkelf()) {
				if (pc.get_sex() == 0) {
					polyId = 6830;
				} else {
					polyId = 6831;
				}
			}
		} else if (itemId == L1ItemId.SHARNA_POLYSCROLL_LV40) { // 40
			if(pc.isCrown()) {
				if (pc.get_sex() == 0) {
					polyId = 6832;
				} else {
					polyId = 6833;
				}
			} else if (pc.isKnight()) {
				if (pc.get_sex() == 0) {
					polyId = 6834;
				} else {
					polyId = 6835;
				}
			} else if (pc.isElf()) {
				if (pc.get_sex() == 0) {
					polyId = 6836;
				} else {
					polyId = 6837;
				}
			} else if (pc.isWizard()) {
				if (pc.get_sex() == 0) {
					polyId = 6838;
				} else {
					polyId = 6839;
				}
			} else if (pc.isDarkelf()) {
				if (pc.get_sex() == 0) {
					polyId = 6840;
				} else {
					polyId = 6841;
				}
			}
		} else if (itemId == L1ItemId.SHARNA_POLYSCROLL_LV52) { // 52
			if(pc.isCrown()) {
				if (pc.get_sex() == 0) {
					polyId = 6842;
				} else {
					polyId = 6843;
				}
			} else if (pc.isKnight()) {
				if (pc.get_sex() == 0) {
					polyId = 6844;
				} else {
					polyId = 6845;
				}
			} else if (pc.isElf()) {
				if (pc.get_sex() == 0) {
					polyId = 6846;
				} else {
					polyId = 6847;
				}
			} else if (pc.isWizard()) {
				if (pc.get_sex() == 0) {
					polyId = 6848;
				} else {
					polyId = 6849;
				}
			} else if (pc.isDarkelf()) {
				if (pc.get_sex() == 0) {
					polyId = 6850;
				} else {
					polyId = 6851;
				}
			}
		} else if (itemId == L1ItemId.SHARNA_POLYSCROLL_LV55) { // 55
			if(pc.isCrown()) {
				if (pc.get_sex() == 0) {
					polyId = 6852;
				} else {
					polyId = 6853;
				}
			} else if (pc.isKnight()) {
				if (pc.get_sex() == 0) {
					polyId = 6854;
				} else {
					polyId = 6855;
				}
			} else if (pc.isElf()) {
				if (pc.get_sex() == 0) {
					polyId = 6856;
				} else {
					polyId = 6857;
				}
			} else if (pc.isWizard()) {
				if (pc.get_sex() == 0) {
					polyId = 6858;
				} else {
					polyId = 6859;
				}
			} else if (pc.isDarkelf()) {
				if (pc.get_sex() == 0) {
					polyId = 6860;
				} else {
					polyId = 6861;
				}
			}
		} else if (itemId == L1ItemId.SHARNA_POLYSCROLL_LV60) { // 60
			if(pc.isCrown()) {
				if (pc.get_sex() == 0) {
					polyId = 6862;
				} else {
					polyId = 6863;
				}
			} else if (pc.isKnight()) {
				if (pc.get_sex() == 0) {
					polyId = 6864;
				} else {
					polyId = 6865;
				}
			} else if (pc.isElf()) {
				if (pc.get_sex() == 0) {
					polyId = 6866;
				} else {
					polyId = 6867;
				}
			} else if (pc.isWizard()) {
				if (pc.get_sex() == 0) {
					polyId = 6868;
				} else {
					polyId = 6869;
				}
			} else if (pc.isDarkelf()) {
				if (pc.get_sex() == 0) {
					polyId = 6870;
				} else {
					polyId = 6871;
				}
			}
		} else if (itemId == L1ItemId.SHARNA_POLYSCROLL_LV65) { // 65
			if(pc.isCrown()) {
				if (pc.get_sex() == 0) {
					polyId = 6872;
				} else {
					polyId = 6873;
				}
			} else if (pc.isKnight()) {
				if (pc.get_sex() == 0) {
					polyId = 6874;
				} else {
					polyId = 6875;
				}
			} else if (pc.isElf()) {
				if (pc.get_sex() == 0) {
					polyId = 6876;
				} else {
					polyId = 6877;
				}
			} else if (pc.isWizard()) {
				if (pc.get_sex() == 0) {
					polyId = 6878;
				} else {
					polyId = 6879;
				}
			} else if (pc.isDarkelf()) {
				if (pc.get_sex() == 0) {
					polyId = 6880;
				} else {
					polyId = 6881;
				}
			}
		} else if (itemId == L1ItemId.SHARNA_POLYSCROLL_LV70) { // 70
			if(pc.isCrown()) {
				if (pc.get_sex() == 0) {
					polyId = 6882;
				} else {
					polyId = 6883;
				}
			} else if (pc.isKnight()) {
				if (pc.get_sex() == 0) {
					polyId = 6884;
				} else {
					polyId = 6885;
				}
			} else if (pc.isElf()) {
				if (pc.get_sex() == 0) {
					polyId = 6886;
				} else {
					polyId = 6887;
				}
			} else if (pc.isWizard()) {
				if (pc.get_sex() == 0) {
					polyId = 6888;
				} else {
					polyId = 6889;
				}
			} else if (pc.isDarkelf()) {
				if (pc.get_sex() == 0) {
					polyId = 6890;
				} else {
					polyId = 6891;
				}
			}
		}
		L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_ITEMMAGIC);
	}
}

