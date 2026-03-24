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
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.utils.SQLUtil;

public class S_BoardRead_AdenaTraid extends ServerBasePacket {

	private static final String S_BoardRead_ServerInfo = "[C] S_BoardRead_ServerInfo";

	private static Logger _log = Logger.getLogger(S_BoardRead_AdenaTraid.class.getName());

	private byte[] _byte = null;

	public S_BoardRead_AdenaTraid(int number) {
		buildPacket(number);
	}

	private void buildPacket(int number) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM board_adena WHERE id=?");
			pstm.setInt(1, number);
			rs = pstm.executeQuery();
			DecimalFormat comentPrice = new DecimalFormat("###,###");
			while (rs.next()) {
				writeC(Opcodes.S_OPCODE_BOARDREAD);
				writeD(number);
				writeS(rs.getString(2)); // id
				writeS(rs.getString(3)); // name
				writeS(rs.getString(4)); // date
				/////////////////////////////////////////////////////////
				writeS("\r\n"
						+ "\r\n"
						+ "   현재상태: 거래중 "
						+ "\r\n"
						+ "   물품번호: " + number
						+ "\r\n"
						+ "\r\n"
						+ "   아덴수량: " +  comentPrice.format(rs.getInt(7)) 
						+ "\r\n"
						+ "   판매금액: " + comentPrice.format(rs.getInt(8))
						+ "\r\n"
						+ "\r\n"
						+ "   예금주: " + rs.getString(13)
						+ "\r\n"
						+ "   은행이름: " + rs.getString(14)
						+ "\r\n"
						+ "   계좌번호: " + rs.getString(15)
						+ "\r\n"); // date
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
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
		return S_BoardRead_ServerInfo;
	}
}
