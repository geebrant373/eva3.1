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
package l1j.server.server.serverpackets;

import java.io.IOException;
import java.util.ArrayList;

import l1j.server.Warehouse.PrivateWarehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_AutoPotionList extends ServerBasePacket {
	public S_AutoPotionList(L1PcInstance pc) {
		
		pc._자동물약초이스리스트 = new ArrayList<L1ItemInstance>();

		for (L1ItemInstance item : pc.getInventory().getItems()) {
			if (item.getItem().getType2() == 0) {
				if (pc.getType() == 0) { // 군주
					if (item.getItemId() == 40010
							|| item.getItemId() == 40011
							|| item.getItemId() == 40012
							|| item.getItemId() == 40013
							//|| item.getItemId() == 437011
							|| item.getItemId() == 437004 // 전투 강화의 주문서
							|| item.getItemId() == 31117 // 버프 물약 : 근거리
							|| item.getItemId() == 40018 // 강화 속도향상 물약
							|| item.getItemId() == 40031 // 악마의 피
							|| item.getItemId() == 40015 // 마력 회복 물약
							|| item.getItemId() == 40017 // 해독제
							|| item.getItemId() == 40507
							|| item.getItemId() == 437010
							|| item.getItemId() == 437011
							|| item.getItemId() == 1437011
							|| item.getItemId() == 436017
							|| item.getItemId() == 436019
							|| item.getItemId() == 436022
							|| item.getItemId() == 41292
							|| item.getItemId() == 40879
							) {
						pc._자동물약초이스리스트.add(item);
					}
				} else if (pc.getType() == 1) { // 기사
					if (item.getItemId() == 40010
							|| item.getItemId() == 40011
							|| item.getItemId() == 40012
							|| item.getItemId() == 40013
							//|| item.getItemId() == 437011
							|| item.getItemId() == 437004 // 전투 강화의 주문서
							|| item.getItemId() == 31117 // 버프 물약 : 근거리
							|| item.getItemId() == 40018 // 강화 속도향상 물약
							|| item.getItemId() == 41415 // 강화 용기의 물약
							|| item.getItemId() == 40014
							|| item.getItemId() == 40015 // 마력 회복 물약
							|| item.getItemId() == 40017 // 해독제
							|| item.getItemId() == 40507
							|| item.getItemId() == 437010
							|| item.getItemId() == 437011
							|| item.getItemId() == 1437011
							|| item.getItemId() == 436017
							|| item.getItemId() == 436019
							|| item.getItemId() == 436022
							|| item.getItemId() == 41292
							|| item.getItemId() == 40879
							) {
						pc._자동물약초이스리스트.add(item);
					}
				} else if (pc.getType() == 2) { // 요정
					if (item.getItemId() == 40010
							|| item.getItemId() == 40011
							|| item.getItemId() == 40012
							|| item.getItemId() == 40013
							//|| item.getItemId() == 437011
							|| item.getItemId() == 437004 // 전투 강화의 주문서
							|| item.getItemId() == 31118 // 버프 물약 : 원거리
							|| item.getItemId() == 40018 // 강화 속도향상 물약
							|| item.getItemId() == 40068 // 엘븐 와퍼
							|| item.getItemId() == 40015 // 마력 회복 물약
							|| item.getItemId() == 40016 // 지혜의 물약
							|| item.getItemId() == 40017 // 해독제
							|| item.getItemId() == 40507
							|| item.getItemId() == 437010
							|| item.getItemId() == 437011
							|| item.getItemId() == 1437011
							|| item.getItemId() == 436017
							|| item.getItemId() == 436019
							|| item.getItemId() == 436022
							|| item.getItemId() == 41292
							|| item.getItemId() == 40879
							) {
						pc._자동물약초이스리스트.add(item);
					}
				} else if (pc.getType() == 3 ) { // 법사
					if (item.getItemId() == 40010
							|| item.getItemId() == 40011
							|| item.getItemId() == 40012
							|| item.getItemId() == 40013
							//|| item.getItemId() == 437011
							|| item.getItemId() == 437003 // 마력 증강의 주문서
							|| item.getItemId() == 31117 // 버프 물약 : 근거리
							|| item.getItemId() == 40018 // 강화 속도향상 물약
							|| item.getItemId() == 40016 // 지혜의 물약
							|| item.getItemId() == 40015 // 마력 회복 물약
							|| item.getItemId() == 40017 // 해독제
							|| item.getItemId() == 40507
							|| item.getItemId() == 437010
							|| item.getItemId() == 437011
							|| item.getItemId() == 1437011
							|| item.getItemId() == 436017
							|| item.getItemId() == 436019
							|| item.getItemId() == 436022
							|| item.getItemId() == 41292
							|| item.getItemId() == 40879
							) {
						pc._자동물약초이스리스트.add(item);
					}
				} else if (pc.getType() == 4) { // 다엘
					if (item.getItemId() == 40010
							|| item.getItemId() == 40011
							|| item.getItemId() == 40012
							|| item.getItemId() == 40013
							//|| item.getItemId() == 437011
							|| item.getItemId() == 437004 // 전투 강화의 주문서
							|| item.getItemId() == 31117 // 버프 물약 : 근거리
							|| item.getItemId() == 40018 // 강화 속도향상 물약
							|| item.getItemId() == 40015 // 마력 회복 물약
							|| item.getItemId() == 40017 // 해독제
							|| item.getItemId() == 40507
							|| item.getItemId() == 437010
							|| item.getItemId() == 437011
							|| item.getItemId() == 1437011
							|| item.getItemId() == 436017
							|| item.getItemId() == 436019
							|| item.getItemId() == 436022
							|| item.getItemId() == 41292
							|| item.getItemId() == 40879
							) {
						pc._자동물약초이스리스트.add(item);
					}
				}
			}
		}
		int size = pc._자동물약초이스리스트.size();
		if (size > 0) {
			writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
			writeD(-1);
			writeH(size);
			writeC(3); // 개인 창고
			for (L1ItemInstance item : pc._자동물약초이스리스트) {
				writeD(item.getId());
				writeC(0);
				writeH(item.get_gfxid());
				writeC(item.getBless());
				writeD(item.getCount());
				writeC(item.isIdentified() ? 1 : 0);
				writeS(item.getViewName());
			}
			pc.setTempShipID(789221);
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}

}
