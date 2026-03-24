package l1j.server.DollBonusEventSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class DollBonusEventLoader {
	private static DollBonusEventLoader _instance;
	public static DollBonusEventLoader getInstance() {
		if(_instance == null)
			_instance = new DollBonusEventLoader();
		return _instance;
	}
	public static void reload() {
		if(_instance != null) {
			_instance = new DollBonusEventLoader();
		}
	}
	
	private HashMap<Integer, DollBonusEventInfo> _bonusitem;
	private DollBonusEventLoader() {
		load();
	}
	
	private void load() {
		final HashMap<Integer, DollBonusEventInfo> bonus = new HashMap<Integer, DollBonusEventInfo>(256);
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM doll_bonus_event_system");
			rs = pstm.executeQuery();
			while (rs.next()) {
				DollBonusEventInfo pInfo = DollBonusEventInfo.newInstance(rs);
				if (pInfo == null)
					continue;
				bonus.put(pInfo.get_map_id(), pInfo);
			}
			_bonusitem = bonus;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public DollBonusEventInfo getBonusItemInfo(int mapid) {
		if (_bonusitem.containsKey(mapid)) {
			return _bonusitem.get(mapid);
		}
		return null;
	}
}
