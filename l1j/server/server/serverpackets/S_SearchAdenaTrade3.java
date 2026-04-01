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
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

import javolution.util.FastTable;
import l1j.server.server.GMCommands;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.BoardAdenaTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1BoardAdena;

public class S_SearchAdenaTrade3 extends ServerBasePacket {
	private static Logger _log = Logger.getLogger(GMCommands.class.getName());	
	
	private FastTable<L1BoardAdena> sellitemlist = new FastTable<L1BoardAdena>();
	
	public S_SearchAdenaTrade3(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(pc.getId());
		writeS("searchtrade3");
		writeC(0);
		writeH(30);
		
		for(L1BoardAdena board : BoardAdenaTable.getInstance().getBoardAdenaList()) {
			if(board == null) {
				continue;
			}
			if(board.getType() == 0) {
				sellitemlist.add(board);
			}
		}
		
		Collections.sort(sellitemlist, new Comparator<L1BoardAdena>() {
			@Override
			public int compare(L1BoardAdena temp1, L1BoardAdena temp2) {
				if(temp1.getSellCount() > temp2.getSellCount()) {
					return 1;
				}else if(temp1.getSellCount() < temp2.getSellCount()) {
					return -1;
				}else {
					return 0;
				}
			}
		});
		
		DecimalFormat priceformat = new DecimalFormat("#,###,###,###");
		int count = 0;
		for(L1BoardAdena board : sellitemlist) {
			writeS("물품번호:" + board.getTradeNumber());
			writeS("판매금액:" + priceformat.format(board.getSellCount()));
			writeS("아덴수량:" + priceformat.format(board.getAdenaCount()));
			count++;
			if(count >= 10) {
				break;
			}
		}
		
		int nulCount = 10 - count;
		if(nulCount != 0) {
			for(int i = 0; i < nulCount; i++) {
				writeS(" ");
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
