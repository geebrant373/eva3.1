package l1j.server.DollBonusEventSystem;

import java.sql.ResultSet;
import java.sql.SQLException;

import l1j.server.MJTemplate.MJArrangeHelper.MJArrangeParseeFactory;
import l1j.server.MJTemplate.MJArrangeHelper.MJArrangeParser;
import l1j.server.server.model.Instance.L1PcInstance;

public class DollBonusEventInfo {
	static DollBonusEventInfo newInstance(ResultSet rs) throws SQLException {
		DollBonusEventInfo pInfo = newInstance();
		pInfo._map_id = rs.getInt("map_id");
		pInfo._map_name = rs.getString("map_name");
		pInfo._trans_id = rs.getString("trans_id");
		pInfo._min_probability = rs.getString("min_probability");
		pInfo._max_probability = rs.getString("max_probability");
		pInfo._is_loggin = rs.getBoolean("is_loggin");
		pInfo._dollids = rs.getString("dollids");
		pInfo._non_trans_id = rs.getString("non_trans_id");
		return pInfo;
	}
	
	public static DollBonusEventInfo newInstance() {
		return new DollBonusEventInfo();
	}
	
	private boolean _is_loggin;
	private int _map_id;
	private String _map_name;
	private String _trans_id;
	private String _min_probability;
	private String _max_probability;
	private String _dollids;
	private String _non_trans_id;
	
	private DollBonusEventInfo() {
	}

	public int get_map_id() {
		return _map_id;
	}
	
	public String get_map_name() {
		return _map_name;
	}
	
	public String get_trans_id() {
		return _trans_id;
	}
	
	public String get_min_probability() {
		return _min_probability;
	}
	
	public String get_max_probability() {
		return _max_probability;
	}
	
	public String get_dollids() {
		return _dollids;
	}
	
	public String get_non_trans_id() {
		return _non_trans_id;
	}
	
	void print() {
	}
	
	public int trans_npc(int i) {
		String[] transe_id = (String[]) MJArrangeParser.parsing(get_trans_id(), ",", MJArrangeParseeFactory.createStringArrange()).result();
		int npcid = 0;
		if (get_trans_id() != null) {
			if (transe_id[i] != null)
				npcid = Integer.parseInt(transe_id[i]);
			else
				npcid = Integer.parseInt(transe_id[0]);
		}
		return npcid;
	}
	public int calc_probability(L1PcInstance pc, int i) {
		int probability = 0;
		String[] drop_min_chance = (String[]) MJArrangeParser.parsing(_min_probability, ",", MJArrangeParseeFactory.createStringArrange()).result();
		String[] drop_max_chance = (String[]) MJArrangeParser.parsing(_max_probability, ",", MJArrangeParseeFactory.createStringArrange()).result();
		int min_chance = Integer.parseInt(drop_min_chance[i]);
		int max_chance = Integer.parseInt(drop_max_chance[i]);
		if (min_chance == max_chance) {
			probability = Integer.parseInt(drop_max_chance[i]);
		}else
			probability = (int) (Math.random() * (max_chance - min_chance)) + min_chance;
		
		if (_is_loggin) {
			if (pc.isGm()) {
				System.out.println(String.format("¸Ê ¾ÆÀÌµð: %d, ÀÌ¸§: %s, º¯°æ È®·ü:(%d)", _map_id, _map_name, probability));
			}
		}
		
		return probability;
	}
	
	public boolean isMap(L1PcInstance pc) {
		if (pc.getMapId() == _map_id) {
			return true;
		}
		return false;
	}
}
