package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class SkillsChanceTable {
  private static SkillsChanceTable _instance;
  
  public static SkillsChanceTable getInstance() {
    if (_instance == null)
      _instance = new SkillsChanceTable(); 
    return _instance;
  }
  
  private ConcurrentHashMap<Integer, SkillsChanceTemp> _skillchancelist = new ConcurrentHashMap<>();
  
  public static void reload() {
    SkillsChanceTable oldInstance = _instance;
    _instance = new SkillsChanceTable();
    oldInstance._skillchancelist.clear();
  }
  
  public SkillsChanceTable() {
    Connection con = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      con = L1DatabaseFactory.getInstance().getConnection();
      pstm = con.prepareStatement("SELECT * FROM skills_chance");
      rs = pstm.executeQuery();
      while (rs.next()) {
        SkillsChanceTemp temp = new SkillsChanceTemp();
        temp.skillid = rs.getInt("스킬번호");
        temp.name = rs.getString("스킬명");
        temp.chance = rs.getInt("기본확률");
        temp.Int_stan = rs.getInt("기준인트");
        temp.Int_chance = rs.getInt("인트확률");
        temp.Mr_stan = rs.getInt("기준마방");
        temp.Mr_chance = rs.getInt("마방확률");
        temp.level_chance = rs.getInt("레벨확률");
        temp.regi_name = rs.getString("내성종류");
        temp.regi_chance = rs.getInt("내성확률");
        this._skillchancelist.put(Integer.valueOf(temp.skillid), temp);
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      SQLUtil.close(rs);
      SQLUtil.close(pstm);
      SQLUtil.close(con);
    } 
  }
  
  public boolean isSkills(int skillid) {
    if (this._skillchancelist.get(Integer.valueOf(skillid)) != null)
      return true; 
    return false;
  }
  
  public SkillsChanceTemp getskills(int skillid) {
    return this._skillchancelist.get(Integer.valueOf(skillid));
  }
  
  public class SkillsChanceTemp {
    public int skillid;
    
    public String name;
    
    public int chance;
    
    public int Int_stan;
    
    public int Int_chance;
    
    public int Mr_stan;
    
    public int Mr_chance;
    
    public int level_chance;
    
    public String regi_name;
    
    public int regi_chance;
  }
}
