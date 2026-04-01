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

import java.text.DecimalFormat;

import javolution.util.FastTable;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1PriceTemp;

public class S_SearchItem extends ServerBasePacket {
	public S_SearchItem(L1PcInstance pc, String searchname, FastTable<L1PriceTemp> selllist, FastTable<L1PriceTemp> buylist) {

		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(pc.getId());
		writeS("searchitem");
		writeC(0);
		writeH(21);
		writeS(searchname); // 검색명

		// \r\n

		DecimalFormat priceformat = new DecimalFormat("#,###,###,###");
		int sellcount = 0;
		// 판매목록
		for (int i = 0; i < selllist.size(); i++) {
			sellcount++;
			L1PriceTemp selltemp = selllist.get(i);
			if (selltemp != null) {
				if (selltemp.enchant == 0) {
					String itemName = selltemp.itemname;
					String price = "금액 : " + priceformat.format(selltemp.price) + "아데나";
					writeS(itemName);
					writeS(price);
				} else {
					String itemName = "+" + selltemp.enchant + " " + selltemp.itemname;
					String price = "금액 : " + priceformat.format(selltemp.price) + " 아데나";
					writeS(itemName);
					writeS(price);
				}
			}
			if (sellcount == 5) {
				break;
			}
		}

		sellcount = 5 - sellcount;
		if (sellcount != 0) {
			for (int i = 0; i < sellcount; i++) {
				writeS(" ");
				writeS(" ");
			}

		}

		// 구매목록
		int buycount = 0;
		for (int i = 0; i < buylist.size(); i++) {
			buycount++;
			L1PriceTemp buytemp = buylist.get(i);
			if (buytemp != null) {
				if (buytemp.enchant == 0) {
					String itemName = buytemp.itemname;
					String price = "금액 : " + priceformat.format(buytemp.price) + " 아데나";
					writeS(itemName);
					writeS(price);
				} else {
					String itemName = "+" + buytemp.enchant + " " + buytemp.itemname;
					String price = "금액 : " + priceformat.format(buytemp.price) + " 아데나";
					writeS(itemName);
					writeS(price);
				}
			}
			if (buycount == 5) {
				break;
			}
		}

		buycount = 5 - buycount;
		if (buycount != 0) {
			for (int i = 0; i < buycount; i++) {
				writeS(" ");
				writeS(" ");
			}

		}

	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
