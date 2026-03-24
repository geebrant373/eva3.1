package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import l1j.server.L1DatabaseFactory;

public class WantedTeleportTable {
    private static WantedTeleportTable _instance;

	private final Map<Integer, HashMap<Integer, Integer>> _list = new HashMap<Integer, HashMap<Integer, Integer>>();
	
    private final Set<Integer> _wantedTeleportMaps = new HashSet<>();

    private WantedTeleportTable() {
        load();
    }

	public static void reload() {
		WantedTeleportTable oldInstance = _instance;
		_instance = new WantedTeleportTable();
		oldInstance._list.clear();
	}
	
    public static WantedTeleportTable getInstance() {
        if (_instance == null) {
            _instance = new WantedTeleportTable();
        }
        return _instance;
    }

    private void load() {
        _wantedTeleportMaps.clear();
        try (Connection con = L1DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstm = con.prepareStatement("SELECT mapid FROM WantedTeleportMaps");
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                _wantedTeleportMaps.add(rs.getInt("mapid"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isWantedTeleportMap(int mapId) {
        return _wantedTeleportMaps.contains(mapId);
    }
}
