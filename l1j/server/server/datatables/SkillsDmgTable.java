package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class SkillsDmgTable{
	private static SkillsDmgTable _instance;
	public static SkillsDmgTable getInstance() {
		if (_instance == null) {
			_instance = new SkillsDmgTable();
		}
		return _instance;
	}
	
	private ConcurrentHashMap<Integer, SkillsDmgTemp> _skilldmglist = new ConcurrentHashMap<Integer, SkillsDmgTemp>();
	
	public static void reload() {
		SkillsDmgTable oldInstance = _instance;
		_instance = new SkillsDmgTable();
		oldInstance._skilldmglist.clear();
	}
	
	public SkillsDmgTable(){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM skills_dmg");
			rs = pstm.executeQuery();
			while (rs.next()) {
				SkillsDmgTemp temp = new SkillsDmgTemp();
				temp.skillid = rs.getInt("스킬번호");
				temp.name = rs.getString("스킬명");
				temp.dmg = rs.getInt("기본대미지");
				temp.rnd_dmg = rs.getInt("랜덤대미지");
				temp.Sp_stan = rs.getInt("기준스펠");
				temp.Sp_dmg = rs.getInt("스펠대미지");
				temp.Mr_stan = rs.getInt("기준마방");
				temp.Mr_dmg = rs.getInt("마방대미지");
				temp.mon_dmg = rs.getInt("몹기본대미지");
				temp.mon_rnd_dmg = rs.getInt("몹랜덤대미지");
				_skilldmglist.put(temp.skillid, temp);
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
		if(_skilldmglist.get(skillid) != null) {
			return true;
		}
		return false;
	}
	
	public SkillsDmgTemp getskills(int skillid) {
		return _skilldmglist.get(skillid);
	}
	
	public class SkillsDmgTemp{
		public int skillid;
		public String name;
		public int dmg;
		public int rnd_dmg;
		public int Sp_stan;
		public int Sp_dmg;
		public int Mr_stan;
		public int Mr_dmg;
		public int mon_dmg;
		public int mon_rnd_dmg;
	}
}