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

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.BoardAdenaTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1BoardAdena;

public class S_SearchAdenaTrade extends ServerBasePacket {
	public S_SearchAdenaTrade(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(pc.getId());
		writeS("searchtrade");
		writeC(0);
		writeH(6);
		//물품번호 저장에 필요한 배열.
		pc.trade_ids = new int[6];
		for(int i = 0; i < 6; i++) {
			pc.trade_ids[i] = 0;
		}
		
		int count = 0;
		for(L1BoardAdena board : BoardAdenaTable.getInstance().getBoardAdenaList()) {
			if(board == null) {
				continue;
			}
			if(board.getType() == 2) { //판매완료 물품 제외
				continue;
			}
			if(pc.getId() == board.getChaId()) {
				pc.trade_ids[count] = board.getTradeNumber();
				count++;
				if(count > 4) { //판매 물품표현은 최대 5개 0~4
					break;
				}
			}
			if(pc.getId() == board.getTradeId()) { //구매진행물품
				pc.trade_ids[5] = board.getTradeNumber();
			}
		}
		
		L1BoardAdena board = null;
		for(int tradenumber : pc.trade_ids) {
			board = BoardAdenaTable.getInstance().getBoardAdena(tradenumber);
			if(board == null) {
				writeS(" ");
			}else {
				String type = board.getType() == 0 ? "판매중" : board.getType() == 1 ? "거래중" : "";
				writeS("물품번호:" + tradenumber + "[" + type + "]");
			}
		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
