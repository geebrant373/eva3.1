package l1j.server.EventSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class EventSystemLoader {
	private static EventSystemLoader _instance;
	public static EventSystemLoader getInstance() {
		if(_instance == null)
			_instance = new EventSystemLoader();
		return _instance;
	}
	public static void reload() {
		if(_instance != null) {
			_instance = new EventSystemLoader();
		}
	}
	
	private HashMap<Integer, EventSystemInfo> _event_system;
	private EventSystemLoader() {
		load();
	}
	
	private void load() {
		final HashMap<Integer, EventSystemInfo> bonus = new HashMap<Integer, EventSystemInfo>(256);
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM event_system");
			rs = pstm.executeQuery();
			while (rs.next()) {
				EventSystemInfo pInfo = EventSystemInfo.newInstance(rs);
				if (pInfo == null)
					continue;
				bonus.put(pInfo.get_id(), pInfo);
			}
			_event_system = bonus;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public EventSystemInfo getEventSystemInfo(L1PcInstance pc) {
		for (int i = 0; i < _event_system.size() + 1; i++) {
			EventSystemInfo info = _event_system.get(i);
			if (info == null)
				continue;
			if (!info.is_event())
				continue;
			if (pc.getMapId() != info.get_event_map_id())
				continue;
			return info;
		}
		return null;
	}
	
	public EventSystemInfo getEventSystemInfo() {
		for (int i = 0; i < _event_system.size() + 1; i++) {
			EventSystemInfo info = _event_system.get(i);
			if (info == null)
				continue;
			if (!info.is_event())
				continue;
			return info;
		}
		return null;
	}
	
	public void getEventSystemInfoCheck(L1PcInstance pc) {
		for (int i = 0; i < _event_system.size() + 1; i++) {
			EventSystemInfo info = _event_system.get(i);
			if (info == null)
				continue;
			if (!info.is_event())
				continue;
			if (info.get_event_name() == null)
				continue;
			pc.sendPackets(new S_SystemMessage("No." + info.get_id() + " ÀÌº¥Æ® : " + info.get_event_name()));
		}
	}
	
	public EventSystemInfo getEventSystemInfo(int i) {
		EventSystemInfo info = _event_system.get(i);
		return info;
	}
	
	public int getEventSystemSize() {
		return _event_system.size();
	}
}
