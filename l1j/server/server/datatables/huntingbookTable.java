package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class huntingbookTable {
  private static Logger _log = Logger.getLogger(huntingbookTable.class.getName());
  
  private static huntingbookTable _instance;
  
  public static huntingbookTable getInstance() {
    if (_instance == null)
      _instance = new huntingbookTable(); 
    return _instance;
  }
  
  public static void reload() {
    huntingbookTable oldInstance = _instance;
    _instance = new huntingbookTable();
    oldInstance._huntingList.clear();
  }
  
  private ConcurrentHashMap<Integer, telTemp> _huntingList = new ConcurrentHashMap<>();
  
  private huntingbookTable() {
    Connection con = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      con = L1DatabaseFactory.getInstance().getConnection();
      pstm = con.prepareStatement("SELECT * FROM huntingbook");
      rs = pstm.executeQuery();
      while (rs.next()) {
        telTemp temp = new telTemp();
        temp.id = rs.getInt("tel_id");
        temp.locX = rs.getInt("locx");
        temp.locY = rs.getInt("locy");
        temp.mapid = rs.getInt("mapid");
        temp.isWanted = (rs.getInt("isWanTed") == 1);
        this._huntingList.put(Integer.valueOf(temp.id), temp);
      } 
    } catch (SQLException e) {
      e.printStackTrace();
      _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
    } finally {
      SQLUtil.close(rs);
      SQLUtil.close(pstm);
      SQLUtil.close(con);
    } 
  }
  
  public telTemp getHuntingTemp(int id) {
    return this._huntingList.get(Integer.valueOf(id));
  }
  
  public class telTemp {
    public int id;
    
    public int locX;
    
    public int locY;
    
    public int mapid;
    
    public boolean isWanted;
  }
}
