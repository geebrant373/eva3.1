package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1BoardAdena;
import l1j.server.server.utils.SQLUtil;

public class BoardBuyTable {
  private static Logger _log = Logger.getLogger(BoardBuyTable.class.getName());
  
  private static BoardBuyTable _instance;
  
  public static BoardBuyTable getInstance() {
    if (_instance == null)
      _instance = new BoardBuyTable(); 
    return _instance;
  }
  
  private Map<Integer, L1BoardAdena> _boards = new ConcurrentHashMap<>();
  
  private BoardBuyTable() {
    Connection con = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      con = L1DatabaseFactory.getInstance().getConnection();
      pstm = con.prepareStatement("SELECT * FROM board_gm_buy");
      rs = pstm.executeQuery();
      L1BoardAdena board = null;
      while (rs.next()) {
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
        this._boards.put(Integer.valueOf(board.getTradeNumber()), board);
      } 
    } catch (SQLException e) {
      _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
    } finally {
      SQLUtil.close(rs);
      SQLUtil.close(pstm);
      SQLUtil.close(con);
    } 
  }
  
  public void writeBoardAdena(L1BoardAdena board) {
    Connection con = null;
    PreparedStatement pstm = null;
    try {
      con = L1DatabaseFactory.getInstance().getConnection();
      pstm = con.prepareStatement("INSERT INTO board_gm_buy SET id=?, name=?, date=?, title=?, content=?,type=?,trade_number=?,cha_id=?,cha_name=?, cha_account_name=?, trade_id=?, trade_name=?, trade_account_name=?, adena_count=?,sell_count=?");
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
      pstm.execute();
      this._boards.put(Integer.valueOf(board.getTradeNumber()), board);
    } catch (SQLException e) {
      _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
    } finally {
      SQLUtil.close(pstm);
      SQLUtil.close(con);
    } 
  }
  
  public void deleteBoardAdena(int number) {
    Connection con = null;
    PreparedStatement pstm = null;
    try {
      con = L1DatabaseFactory.getInstance().getConnection();
      pstm = con.prepareStatement("DELETE FROM board_gm_buy WHERE trade_number=?");
      pstm.setInt(1, number);
      pstm.execute();
      this._boards.remove(Integer.valueOf(number));
    } catch (SQLException e) {
      _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
    } finally {
      SQLUtil.close(pstm);
      SQLUtil.close(con);
    } 
  }
  
  public void updateBoardAdena(L1BoardAdena board) {
    Connection con = null;
    PreparedStatement pstm = null;
    try {
      con = L1DatabaseFactory.getInstance().getConnection();
      pstm = con.prepareStatement("UPDATE board_gm_buy SET id=?, name=?, content=?, type=?, cha_id=?, cha_name=?, cha_account_name=?, trade_id=?, trade_name=?, trade_account_name=? WHERE trade_number=?");
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
      pstm.setInt(11, board.getTradeNumber());
      pstm.execute();
    } catch (SQLException e) {
      _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
    } finally {
      SQLUtil.close(pstm);
      SQLUtil.close(con);
    } 
  }
  
  public L1BoardAdena[] getBoardAdenaList() {
    return (L1BoardAdena[])this._boards.values().toArray((Object[])new L1BoardAdena[this._boards.size()]);
  }
  
  public L1BoardAdena getBoardAdena(int tradenumber) {
    return this._boards.get(Integer.valueOf(tradenumber));
  }
  
  public void addBoardAdena(L1BoardAdena board) {
    this._boards.put(Integer.valueOf(board.getTradeNumber()), board);
  }
  
  public void removeBoardAdena(int tradenumber) {
    this._boards.remove(Integer.valueOf(tradenumber));
  }
}
