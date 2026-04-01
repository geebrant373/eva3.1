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
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server:
// IdFactory

public class BoardTable {

	private static Logger _log = Logger.getLogger(BoardTable.class.getName());

	private static BoardTable _instance;

	private BoardTable() {
	}

	public static BoardTable getInstance() {
		if (_instance == null) {
			_instance = new BoardTable();
		}
		return _instance;
	}

	public void writeTopic(L1PcInstance pc, String date, String title, String content, int id) {
		int count = 0;

		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM board ORDER BY board_id DESC");
			rs = pstm1.executeQuery();
			while (rs.next()) {
				if(rs.getInt(6) == id){
					if(count < rs.getInt(1));{
						count = rs.getInt(1);
					}
				}
			}
			pstm2 = con.prepareStatement("INSERT INTO board SET id=?, name=?, date=?, title=?, content=?, board_id=?");
			pstm2.setInt(1, (count + 1));
			pstm2.setString(2, pc.getName());
			pstm2.setString(3, date);
			pstm2.setString(4, title);
			pstm2.setString(5, content);
			pstm2.setInt(6, id);
			pstm2.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	public void writeTopic4(L1PcInstance pc, String date, String title, String content, int id) {
		int count = 0;

		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM board_notice ORDER BY board_id DESC");
			rs = pstm1.executeQuery();
			while (rs.next()) {
				if(rs.getInt(6) == id){
					if(count < rs.getInt(1));{
						count = rs.getInt(1);
					}
				}
			}
			pstm2 = con.prepareStatement("INSERT INTO board_notice SET id=?, name=?, date=?, title=?, content=?, board_id=?");
			pstm2.setInt(1, (count + 1));
			pstm2.setString(2, pc.getName());
			pstm2.setString(3, date);
			pstm2.setString(4, title);
			pstm2.setString(5, content);
			pstm2.setInt(6, id);
			pstm2.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}
	
	public void writeTopic5(L1PcInstance pc, String date, String title, String content, int id) {
		int count = 0;

		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM board_notice1 ORDER BY board_id DESC");
			rs = pstm1.executeQuery();
			while (rs.next()) {
				if(rs.getInt(6) == id){
					if(count < rs.getInt(1));{
						count = rs.getInt(1);
					}
				}
			}
			pstm2 = con.prepareStatement("INSERT INTO board_notice1 SET id=?, name=?, date=?, title=?, content=?, board_id=?");
			pstm2.setInt(1, (count + 1));
			pstm2.setString(2, pc.getName());
			pstm2.setString(3, date);
			pstm2.setString(4, title);
			pstm2.setString(5, content);
			pstm2.setInt(6, id);
			pstm2.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}
	
	public void writeTopic6(L1PcInstance pc, String date, String title, String content, int id) {
		int count = 0;

		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM board_notice2 ORDER BY board_id DESC");
			rs = pstm1.executeQuery();
			while (rs.next()) {
				if(rs.getInt(6) == id){
					if(count < rs.getInt(1));{
						count = rs.getInt(1);
					}
				}
			}
			pstm2 = con.prepareStatement("INSERT INTO board_notice2 SET id=?, name=?, date=?, title=?, content=?, board_id=?");
			pstm2.setInt(1, (count + 1));
			pstm2.setString(2, pc.getName());
			pstm2.setString(3, date);
			pstm2.setString(4, title);
			pstm2.setString(5, content);
			pstm2.setInt(6, id);
			pstm2.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}
	
	public void writeDragonKey(L1PcInstance pc, L1ItemInstance key, String date, int id) {
		int count = 0;

		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM board ORDER BY board_id DESC");
			rs = pstm1.executeQuery();
			while (rs.next()) {
				if(rs.getInt(6) == id){
					if(count < rs.getInt(1));{
						count = rs.getInt(1);
					}
				}
			}
			pstm2 = con.prepareStatement("INSERT INTO board SET id=?, name=?, date=?, board_id=?, remaining_time=?, item_id=?");
			pstm2.setInt(1, (count + 1));
			pstm2.setString(2, pc.getName());
			pstm2.setString(3, date);
			pstm2.setInt(4, id);
			pstm2.setTimestamp(5, key.getEndTime());
			pstm2.setInt(6, key.getId());
			pstm2.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	public void deleteTopic(int number) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM board WHERE id=?");
			pstm.setInt(1, number);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	/**
	 * 등록된 사람이 있나 찾는다 (드래곤 키)
	 * @param name
	 * @param npcid
	 * @return
	 */
	public boolean checkExistName(String name, int npcid) {
		boolean result = true;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT name FROM board WHERE board_id=? AND name=?");
			pstm.setInt(1, npcid);
			pstm.setString(2, name);
			rs = pstm.executeQuery();
			result = rs.next();
		} catch (SQLException e) {
			_log.warning("could not check existing charname:" + e.getMessage());
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}
	
	/**
	 * 게시판에서 해당 키 내용을 삭제한다
	 * @param id (아이템 id)
	 */
	public void delDayExpire(int id) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM board WHERE item_id=?");
			pstm.setInt(1, id);
			pstm.execute();
			
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		
	}
	public void deleteAdena(int number) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM board_adena WHERE id=?");
			pstm.setInt(1, number);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}

