package l1j.server.server.serverpackets;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.utils.SQLUtil;

//TODO 중개 거래 게시판
public class S_AuctionSystemBoard extends ServerBasePacket {

	private static final String S_AuctionSystemBoard = "[S] S_AuctionSystemBoard";

	private static Logger _log = Logger.getLogger(S_AuctionSystemBoard.class.getName());

	private byte[] _byte = null;

	public S_AuctionSystemBoard(L1NpcInstance board) {
		buildPacket(board, 0);
	}

	public S_AuctionSystemBoard(L1NpcInstance board, int number) {
		buildPacket(board, number);
	}

	public S_AuctionSystemBoard(int number) {
		buildPacket(number);
	}
	
	/**구버전에 맞게 패킷 변경 by 명월이*/
	private void buildPacket(L1NpcInstance board, int number) {
		int count = 0;
		String[][] db = null;
		int[] id = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			db = new String[8][4];
			id = new int[8];
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM Auction order by id desc");
			rs = pstm.executeQuery();
			while (rs.next() && count < 8) {
				if (rs.getInt(1) <= number || number == 0) {
					id[count] = rs.getInt(1);
					db[count][0] = rs.getString(2);
					db[count][1] = rs.getString(15);
					db[count][2] = rs.getString(8);
					db[count][3] = Integer.toString(rs.getInt(5));
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
		writeC(0);
		writeD(board.getId());
		writeC(0xFF); 
		writeC(0xFF); 
		writeC(0xFF); 
		writeC(0x7F);
		writeH(count);
		writeH(300);
		for (int i = 0; i < count; ++i) {
			writeD(id[i]);
			writeS(db[i][0]);
			writeS(db[i][1]);
			writeS(db[i][3]+"아데나 "+db[i][2]);
		}
	}
	private void buildPacket(int number) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM Auction WHERE id=?");
			pstm.setInt(1, number);
			rs = pstm.executeQuery();
			while (rs.next()) {
				writeC(Opcodes.S_OPCODE_BOARDREAD);
				writeD(number);
				writeS(rs.getString(2));
				writeS("" + rs.getString(5) + " " + rs.getString(4));
				writeS(rs.getString(8));
				writeS("▣내용 클릭시 팅길수 있습니다▣"
						+ "\n\n" + rs.getString(4) + " : " + rs.getString(5) + " 개"
						+ "\n판매 금액 : " + rs.getString(6) + " 원" + ""
						+ "\n\n▣구매 방법▣"
						+ "\n\n.구매신청 (게시번호) 입력후"
						+ "\n편지 내용 확인.");
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
//	public void buildPacket(int number) {
//		Connection con = null;
//		PreparedStatement pstm = null;
//		ResultSet rs = null;
//		try {
//			con = L1DatabaseFactory.getInstance().getConnection();
//			pstm = con.prepareStatement("SELECT * FROM Auction WHERE id=?");
//			pstm.setInt(1, number);
//			rs = pstm.executeQuery();
//			while (rs.next()) {
//				writeC(Opcodes.S_OPCODE_BOARDREAD);
//				writeD(number);
//				writeS(rs.getString(2));
//				writeS("" + rs.getString(5) + " " + rs.getString(4));
//				writeS(rs.getString(15));
//				writeS("▣내용 클릭시 팅길수 있습니다▣"
//						+ "\n\n" + rs.getString(4) + " : " + rs.getString(5) + " 개"
//						+ "\n판매 금액 : " + rs.getString(6) + " 원" + ""
//						+ "\n\n▣구매 방법▣"
//						+ "\n\n.구매신청 (게시번호) 입력후"
//						+ "\n편지 내용 확인.");
//				System.out.println(rs.getString(3));
//			}
//		} catch (SQLException e) {
//			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
//		} finally {
//			SQLUtil.close(rs);
//			SQLUtil.close(pstm);
//			SQLUtil.close(con);
		
	
	/**구버전에 맞게 패킷 변경 by 명월이*/

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_AuctionSystemBoard;
	}

}