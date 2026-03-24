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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.BoardTable;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.templates.L1BoardPost;
import l1j.server.server.utils.SQLUtil;

public class S_Board extends ServerBasePacket {

	private static final String S_BOARD = "[S] S_Board";

	private static Logger _log = Logger.getLogger(S_Board.class.getName());

	private static final int TOPIC_LIMIT = 8;
	
	private byte[] _byte = null;

	public S_Board(L1NpcInstance board) {
		if(board.getNpcId()==4212014) {
			buildPacket2(board, 0);
		} else if (board.getNpcId()==500001) {
			buildPacket3(board, 0);
		} else if (board.getNpcId()==900001609) {
			buildPacket4(board, 0);
		} else if (board.getNpcId()==900001610) {
			buildPacket5(board, 0);
		} else if (board.getNpcId()==80006) {
			buildPacket6(board, 0);
		} else if (board.getNpcId()==900009643) {
			buildPacket7(board, 0);
		} else if (board.getNpcId()==900009644) {
			buildPacket사냥터정보(board, 0);
		} else if (board.getNpcId()==900009645) {
			buildPacket보스정보(board, 0);
		} else if (board.getNpcId()==900009646) {
			buildPacket무기정보(board, 0);
		} else if (board.getNpcId()==900009647) {
			buildPacket방어구정보(board, 0);
		} else if (board.getNpcId()==900009648) {
			buildPacket인첸율정보(board, 0);
		} else
			buildPacket(board, 0);
	}

	public S_Board(L1NpcInstance board, int number) {
		if(board.getNpcId()==4212014) {
			buildPacket2(board, number);
		} else if (board.getNpcId()==500001) {
			buildPacket3(board, number);
		} else if (board.getNpcId()==900001609) {
			buildPacket4(board, number);
		} else if (board.getNpcId()==900001610) {
			buildPacket5(board, number);
		} else if (board.getNpcId()==80006) {
			buildPacket6(board, number);
		} else if (board.getNpcId()==900009643) {
			buildPacket7(board, number);
		} else if (board.getNpcId()==900009644) {
			buildPacket사냥터정보(board, number);
		} else if (board.getNpcId()==900009645) {
			buildPacket보스정보(board, number);
		} else if (board.getNpcId()==900009646) {
			buildPacket무기정보(board, number);
		} else if (board.getNpcId()==900009647) {
			buildPacket방어구정보(board, number);
		} else if (board.getNpcId()==900009648) {
			buildPacket인첸율정보(board, number);
		} else
			buildPacket(board, number);
	}

	private void buildPacket(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.index(number, TOPIC_LIMIT);
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0);
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}

	private void buildPacket2(L1NpcInstance board, int number) {// 드래곤키 알림 게시판
		int count = 0;
		long a = 0;
		String[][] db = null;
		int[] id = null;
		int[] time = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			db = new String[8][2];
			id = new int[8];
			time = new int[8];
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM board order by id desc");
			rs = pstm.executeQuery();
			while (rs.next() && count < 8){
				if(board.getNpcId() != rs.getInt(6)){
					continue;
				}
				a = rs.getTimestamp(7).getTime() - System.currentTimeMillis();
				if(a < 0){
					BoardTable.getInstance().delDayExpire(rs.getInt(8));
					continue;
				}
				if (rs.getInt("id") <= number || number == 0) {
					id[count] = rs.getInt(1);
					db[count][0] = rs.getString(2);// 이름
					db[count][1] = rs.getString(3);// 날짜
					time[count] = (int) a/60000*60;
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
		writeC(1);// type
		writeD(board.getId());
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0x7F); // ?
		writeC(count);
		for (int i = 0; i < count; ++i) {
			writeD(id[i]);
			writeS(db[i][0]);// 이름
			writeS(db[i][1]);// 날짜
			writeD(time[i]);
		}
	}
	
	private void buildPacket3(L1NpcInstance board,int number) {
		int count = 0;
		String[][] db = null;
		int[] id = null;
		db = new String[9][3];
		id = new int[9];
		while (count < 9) {
			id[count] = count + 1;
			db[count][0] = "";// Ranking
			db[count][1] = "";
			count++;
		}
		db[0][2] = "--------- 전     체";
		db[1][2] = "--------- 군     주";
		db[2][2] = "--------- 기     사";
		db[3][2] = "--------- 요     정";
		db[4][2] = "--------- 법     사";
		db[5][2] = "--------- 다     엘";
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0);
		writeD(board.getId());
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0x7F); // ?
		writeH(9);
		writeH(300);
		for (int i = 0; i < 8; ++i) {
			writeD(id[i]);
			writeS(db[i][0]);
			writeS(db[i][1]);
			writeS(db[i][2]);
		}
	}
	
	private void buildPacket4(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM(number, TOPIC_LIMIT);
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0); // DragonKeybbs = 1
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	
	private void buildPacket5(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM1(number, TOPIC_LIMIT);
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0);
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	
	private void buildPacket6(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM2(number, TOPIC_LIMIT);
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0);
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	
	private void buildPacket7(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM3(number, TOPIC_LIMIT);
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0);
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	
	private void buildPacket사냥터정보(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM사냥터정보(number, TOPIC_LIMIT);
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0);
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	
	private void buildPacket보스정보(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM보스정보(number, TOPIC_LIMIT);
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0);
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	
	private void buildPacket무기정보(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM무기정보(number, TOPIC_LIMIT);
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0);
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	
	private void buildPacket방어구정보(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM방어구정보(number, TOPIC_LIMIT);
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0);
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
	}
	
	private void buildPacket인첸율정보(L1NpcInstance board, int number) {
		List<L1BoardPost> topics = L1BoardPost.indexGM인첸율정보(number, TOPIC_LIMIT);
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0);
		writeD(board.getId());
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
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
		return S_BOARD;
	}
}
