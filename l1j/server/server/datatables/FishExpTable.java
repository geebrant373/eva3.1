package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class FishExpTable {
	private static FishExpTable _instance;
	
	public static FishExpTable getInstance() {
		if(_instance == null) {
			_instance = new FishExpTable();
		}
		return _instance;
	}
	
	private Map<Integer, Integer> _fishExpList = new HashMap<Integer, Integer>();
	
	private FishExpTable() {
		loadTable(_fishExpList);
	}
	
	public void reload() {
		Map<Integer, Integer> fishExpList = new HashMap<Integer, Integer>();
		loadTable(fishExpList);
		_fishExpList = fishExpList;
	}
	
	private void loadTable(Map<Integer, Integer> fishExpList) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from fish_exp");
			rs = pstm.executeQuery();
			while(rs.next()) {
				int level = rs.getInt("level");
				int count =rs.getInt("fish_count");
				
				fishExpList.put(level, count);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public int getFishCountByLevel(int level) {
		return _fishExpList.get(level) != null ? _fishExpList.get(level) : 1;
	}
}
