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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1BoardAdena;
import l1j.server.server.utils.SQLUtil;


/**
 * 아데나 유저 거래 게시판
 * @author 흑영
 *
 */
public class BoardAdenaTable {

	private static Logger _log = Logger.getLogger(BoardAdenaTable.class.getName());

	private static BoardAdenaTable _instance;
	
	public static BoardAdenaTable getInstance() {
		if (_instance == null) {
			_instance = new BoardAdenaTable();
		}
		return _instance;
	}
	
	private Calendar timestampToCalendar(Timestamp ts) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts.getTime());
		return cal;
	}
	
	private Map<Integer, L1BoardAdena> _boards = new ConcurrentHashMap<Integer, L1BoardAdena>();
	
	private BoardAdenaTable() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM board_adena");
			rs = pstm.executeQuery();
			L1BoardAdena board = null;
			while(rs.next()) {
				board = new L1BoardAdena();
				board.setId(rs.getInt("id"));
				board.setName(rs.getString("name"));
				board.setDate(rs.getString("date"));
				board.setTitle(rs.getString("title"));
				board.setContent(rs.getString("content"));
				board.setType(rs.getInt("type"));
				board.setTradeNumber(rs.getInt("trade_number"));
				board.setChaId(rs.getInt("cha_id"));
				board.setChaName(rs.getString("cha_name"));
				board.setChaAccountName(rs.getString("cha_account_name"));
				board.setTradeId(rs.getInt("trade_id"));
				board.setTradeName(rs.getString("trade_name"));
				board.setTradeAccountName(rs.getString("trade_account_name"));
				board.setAdenaCount(rs.getInt("adena_count"));
				board.setSellCount(rs.getInt("sell_count"));
				board.setAdenaTime(timestampToCalendar((Timestamp) rs.getObject("add_time")));
				
				_boards.put(board.getTradeNumber(), board);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	/**
	 * 테이블 등록
	 */
	public void writeBoardAdena(L1BoardAdena board) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO board_adena SET id=?, name=?, date=?, title=?, content=?"
					+ ",type=?,trade_number=?,cha_id=?,cha_name=?, cha_account_name=?, trade_id=?, trade_name=?, trade_account_name=?, adena_count=?,sell_count=?, add_time=?");
			
			pstm.setInt(1, board.getId());
			pstm.setString(2, board.getName());
			pstm.setString(3, board.getDate());
			pstm.setString(4, board.getTitle());
			pstm.setString(5, board.getContent());
			
			pstm.setInt(6, board.getType());
			pstm.setInt(7, board.getTradeNumber());
			
			pstm.setInt(8, board.getChaId());
			pstm.setString(9, board.getChaName());
			pstm.setString(10, board.getChaAccountName());
			
			pstm.setInt(11, board.getTradeId());
			pstm.setString(12, board.getTradeName());
			pstm.setString(13, board.getTradeAccountName());
			
			pstm.setInt(14, board.getAdenaCount());
			pstm.setInt(15, board.getSellCount());
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String fm = formatter.format(board.getAdenaTime().getTime()); 
			pstm.setString(16, fm);
			
			pstm.execute();
			
			_boards.put(board.getTradeNumber(), board); //리스트 등록
			
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	/**
	 * 테이블 삭제
	 */
	public void deleteBoardAdena(int number) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM board_adena WHERE trade_number=?");
			pstm.setInt(1, number);
			pstm.execute();
			
			_boards.remove(number); //리스트에서 삭제
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	/**
	 * 테이블 업데이트
	 */
	public void updateBoardAdena(L1BoardAdena board) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE board_adena"
					+ " SET id=?, name=?, content=?, type=?, cha_id=?, cha_name=?, cha_account_name=?, trade_id=?, trade_name=?, trade_account_name=?, add_time=?"
					+ " WHERE trade_number=?");
			
			pstm.setInt(1, board.getId());
			pstm.setString(2, board.getName());
			pstm.setString(3, board.getContent());
			pstm.setInt(4, board.getType());
			pstm.setInt(5, board.getChaId());
			pstm.setString(6, board.getChaName());
			pstm.setString(7, board.getChaAccountName());
			
			pstm.setInt(8, board.getTradeId());
			pstm.setString(9, board.getTradeName());
			pstm.setString(10, board.getTradeAccountName());
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String fm = formatter.format(board.getAdenaTime().getTime()); 
			pstm.setString(11, fm);
			
			pstm.setInt(12, board.getTradeNumber());
			pstm.execute();
			
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public L1BoardAdena[] getBoardAdenaList() {
		return _boards.values().toArray(new L1BoardAdena[_boards.size()]);
	}
	
	public L1BoardAdena getBoardAdena(int tradenumber) {
		return _boards.get(tradenumber);
	}
	
	public void addBoardAdena(L1BoardAdena board) {
		_boards.put(board.getTradeNumber(), board);
	}
	
	public void removeBoardAdena(int tradenumber) {
		_boards.remove(tradenumber);
	}
}
