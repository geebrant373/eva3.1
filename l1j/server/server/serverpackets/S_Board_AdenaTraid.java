/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;


public class S_Board_AdenaTraid extends ServerBasePacket {

	private static final String S_Board_ServerInfo = "[S] S_Board_ServerInfo";

	private static Logger _log = Logger.getLogger(S_Board_AdenaTraid.class.getName());

	private byte[] _byte = null;

	public S_Board_AdenaTraid(L1PcInstance pc, L1NpcInstance board) {
		buildPacket(pc, board, 0);
	}

	public S_Board_AdenaTraid(L1PcInstance pc, L1NpcInstance board, int number) {
		buildPacket(pc, board, number);
	}

	private void buildPacket(L1PcInstance pc, L1NpcInstance board, int number) {
		int count = 0;
		String[][] db = null;
		int[] id = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			db = new String[8][3];
			id = new int[8];
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM board_adena order by id desc");
			rs = pstm.executeQuery();
			while (rs.next() && count < 8) {
				if (rs.getInt("id") <= number || number == 0) {
					id[count] = rs.getInt(1);
					if (rs.getInt(1) == pc.getAdenaBuyCount() || rs.getInt(1) == pc.getAdenaSellCount()) {
						db[count][0] = rs.getString(2);
					} else {
						db[count][0] = "####";
					}
					db[count][1] = rs.getString(3);
					if (rs.getInt(12) == 0) {
						db[count][2] = "[판매중]"  +  rs.getString(11);
					} else if (rs.getString(10) != null && rs.getInt(12) == 1) {
						db[count][2] = "[구매중]"  +  rs.getString(11);
					} else if (rs.getInt(12) == 2) {
						db[count][2] = "[판매완료]"  +  rs.getString(11);
					} else {
						db[count][2] = "[판매취소]"  +  rs.getString(11);
					} 
					count++;
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0); // DragonKeybbs = 1
		writeD(board.getId());
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0x7F); // ?
		writeH(count);
		writeH(300);
		for (int i = 0; i < count; ++i) {
			writeD(id[i]);
			writeS(db[i][0]);
			writeS(db[i][1]);
			writeS(db[i][2]);
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_Board_ServerInfo;
	}
}
