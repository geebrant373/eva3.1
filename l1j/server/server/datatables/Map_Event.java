
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

public final class Map_Event {
	private class MapData {
		public double Mapevent_add_exp = 1;
		public double add_Npc_Pc_dmg = 1;
		public double add_Pc_Npc_reduc = 1;
		public double adena_rate = 1;
		public double add_Npc_Pc_hit = 1;
	}

	private static Logger _log = Logger.getLogger(Map_Event.class.getName());

	private static Map_Event _instance;

	/**
	 * Key에 MAP ID, Value에 텔레포트 가부 플래그가 격납되는 HashMap
	 */
	private final Map<Integer, MapData> _maps = new HashMap<Integer, MapData>();

	/**
	 * 새롭고 MapsTable 오브젝트를 생성해, MAP의 텔레포트 가부 플래그를 읽어들인다.
	 */
	private Map_Event() {
		loadMapsFromDatabase();
	}

	/**
	 * MAP의 텔레포트 가부 플래그를 데이타베이스로부터 읽어들여, HashMap _maps에 격납한다.
	 */
	private void loadMapsFromDatabase() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM Map_Event");
			MapData data = null;
			for (rs = pstm.executeQuery(); rs.next();) {
				data = new MapData();
				int mapId = rs.getInt("Mapid");
				data.Mapevent_add_exp = rs.getDouble("exp_rate");
				data.add_Npc_Pc_dmg = rs.getDouble("Npc_Pc_dmg");
				data.add_Pc_Npc_reduc = rs.getDouble("Pc_Npc_reduc");
				data.add_Npc_Pc_hit = rs.getDouble("Npc_Pc_hit");
				data.adena_rate = rs.getDouble("adena_rate");
				

				_maps.put(new Integer(mapId), data);
			}

			_log.config("Maps " + _maps.size());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	private void loadMaps(Map<Integer, MapData> maps) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM mapids");
			MapData data = null;
			for (rs = pstm.executeQuery(); rs.next();) {
				data = new MapData();
				int mapId = rs.getInt("Mapid");
				data.Mapevent_add_exp = rs.getDouble("exp_rate");
				data.add_Npc_Pc_dmg = rs.getDouble("Npc_Pc_dmg");
				data.add_Pc_Npc_reduc = rs.getDouble("Pc_Npc_reduc");
				data.add_Npc_Pc_hit = rs.getDouble("Npc_Pc_hit");
				data.adena_rate = rs.getDouble("adena_rate");
				

				_maps.put(new Integer(mapId), data);
			}

			_log.config("Maps " + _maps.size());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	/**
	 * MapsTable의 인스턴스를 돌려준다.
	 * 
	 * @return MapsTable의 인스턴스
	 */
	public static Map_Event getInstance() {
		if (_instance == null) {
			_instance = new Map_Event();
		}
		return _instance;
	}

	
	
	public void reload() {
		PerformanceTimer timer = new PerformanceTimer();
		Map_Event oldInstance = _instance;
		_instance = new Map_Event();
		if (oldInstance != null)
			oldInstance._maps.clear();
		System.out.print("reloading " + _log.getName().substring(_log.getName().lastIndexOf(".") + 1) + "...");
		System.out.println("OK! " + timer.elapsedTimeMillis() + "ms");

	

	//	System.out.println("OK! " + timer.elapsedTimeMillis() + "ms");
	}
	/**
	 * 맵의 드롭 배율을 돌려준다
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * @return 드롭 배율
	 */
	public double getadena_rate(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return  map.adena_rate;
	}
	public double getMapevent_add_exp(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return  map.Mapevent_add_exp;
	}

	public double getadd_Npc_Pc_dmg(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.add_Npc_Pc_dmg;
	}
	public double getadd_Npc_Pc_hit(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return  map.add_Npc_Pc_hit;
	}
	public double getadd_Pc_Npc_reduc(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return  map.add_Pc_Npc_reduc;
	}


	

}
