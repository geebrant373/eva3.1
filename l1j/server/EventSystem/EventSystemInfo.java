package l1j.server.EventSystem;

import java.sql.ResultSet;
import java.sql.SQLException;

import l1j.server.ParseHelper.ArrangeParseeFactory;
import l1j.server.ParseHelper.ArrangeParser;
import l1j.server.server.model.Instance.L1PcInstance;

public class EventSystemInfo {
	static EventSystemInfo newInstance(ResultSet rs) throws SQLException {
		EventSystemInfo pInfo = newInstance();
		pInfo._get_id = rs.getInt("getid");
		pInfo._npc_id = rs.getInt("npcid");
		pInfo._event_name = rs.getString("event_name");
		pInfo._action_id = rs.getString("action_id");
		pInfo._action_name = rs.getString("action_name");
		pInfo._spawn_loc = rs.getString("spawn_loc");
		pInfo._event_map_id = rs.getInt("event_map_id");
		pInfo._teleport_x = rs.getInt("teleport_x");
		pInfo._teleport_y = rs.getInt("teleport_y");
		pInfo._drop_item = rs.getString("drop_item");
		pInfo._min_count = rs.getString("min_count");
		pInfo._max_count = rs.getString("max_count");
		pInfo._drop_min_chance = rs.getString("drop_min_chance");
		pInfo._drop_max_chance = rs.getString("drop_max_chance");
		pInfo._event_time = rs.getString("event_time");
		pInfo._event_end_time = rs.getInt("event_end_time");
		pInfo._is_mapout = MapOutType(rs.getString("mapout"));
		return pInfo;
	}
	
	public static boolean MapOutType(String type) {
		if (type.equalsIgnoreCase("true"))
			return true;
		else
			return false;
	}
	
	private static EventSystemInfo newInstance() {
		return new EventSystemInfo();
	}
	
	private boolean _is_event = false;
	private int _get_id;
	private int _npc_id;
	private String _event_name;
	private String _action_id;
	private String _action_name;
	private String _spawn_loc;
	private int _event_map_id;
	private int _teleport_x;
	private int _teleport_y;
	private String _drop_item;
	private String _min_count;
	private String _max_count;
	private String _drop_min_chance;	
	private String _drop_max_chance;	
	private String _event_time;
	private int _event_end_time;
	private boolean _is_mapout = false;
	
	private EventSystemInfo() {
	}
	
	public int get_id() {
		return _get_id;
	}

	public int get_npc_id() {
		return _npc_id;
	}
	
	public String get_event_name() {
		return _event_name;
	}
	
	public String get_action_id() {
		return _action_id;
	}
	
	public String get_action_name() {
		return _action_name;
	}
	
	public String get_spawn_loc() {
		return _spawn_loc;
	}
	
	public int get_event_map_id() {
		return _event_map_id;
	}
	
	public int get_teleport_x() {
		return _teleport_x;
	}
	
	public int get_teleport_y() {
		return _teleport_y;
	}
	
	public String get_drop_item() {
		return _drop_item;
	}
	
	public String get_min_count() {
		return _min_count;
	}
	
	public String get_max_count() {
		return _max_count;
	}
	
	public String get_drop_min_chance() {
		return _drop_min_chance;
	}
	
	public String get_drop_max_chance() {
		return _drop_max_chance;
	}
	
	public String get_event_time() {
		return _event_time;
	}
	
	public int get_event_end_time() {
		return _event_end_time;
	}
	
	public boolean is_mapout() {
		return _is_mapout;
	}
	
	public boolean set_mapout(boolean flag) {
		_is_mapout = flag;
		return _is_mapout;
	}
	
	public boolean is_event() {
		return _is_event;
	}
	
	public boolean set_event(boolean flag) {
		_is_event = flag;
		return _is_event;
	}
	
	void print() {
	}
	
	public int calc_probability(EventSystemInfo bonusInfo, int i, L1PcInstance pc) {
		int probability = 0;
		String[] drop_min_chance = (String[]) ArrangeParser.parsing(bonusInfo.get_drop_min_chance(), ",", ArrangeParseeFactory.createStringArrange()).result();
		String[] drop_max_chance = (String[]) ArrangeParser.parsing(bonusInfo.get_drop_max_chance(), ",", ArrangeParseeFactory.createStringArrange()).result();
		int min_chance = Integer.parseInt(drop_min_chance[i]);
		int max_chance = Integer.parseInt(drop_max_chance[i]);
		if (min_chance == max_chance) {
			probability = Integer.parseInt(drop_max_chance[i]);
		}else
			probability = (int) (Math.random() * (max_chance - min_chance)) + min_chance;
		return probability;
	}
	
	public int calc_count(EventSystemInfo bonusInfo, int i, L1PcInstance pc) {
		String[] drop_min_count = (String[]) ArrangeParser.parsing(bonusInfo.get_min_count(), ",", ArrangeParseeFactory.createStringArrange()).result();
		String[] drop_max_count = (String[]) ArrangeParser.parsing(bonusInfo.get_max_count(), ",", ArrangeParseeFactory.createStringArrange()).result();
		int min_count = Integer.parseInt(drop_min_count[i]);
		int max_count = Integer.parseInt(drop_max_count[i]);
		
		int count 		= 0;
		if (min_count == max_count)
			count 	= max_count;
		else {
			count = (int) (Math.random() * (max_count - min_count)) + min_count;
		}
		return count;
	}
	
	public boolean isMap(EventSystemInfo bonusInfo, L1PcInstance pc) {
		int mapId = bonusInfo.get_event_map_id();
		if (pc.getMapId() == mapId) {
			return true;
		}
		return false;
	}
}
