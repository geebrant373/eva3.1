package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class CharactersMrTable {
	private static CharactersMrTable _instance;

	public static CharactersMrTable getInstance() {
		if (_instance == null) {
			_instance = new CharactersMrTable();
		}
		return _instance;
	}

	private Map<Integer, CharactersMr> _list = new HashMap<Integer, CharactersMr>();

	private CharactersMrTable() {
		loadTable(_list);
	}
	
	public void reload() {
		Map<Integer, CharactersMr> list = new HashMap<Integer, CharactersMr>();
		loadTable(list);
		_list = list;
	}

	private void loadTable(Map<Integer, CharactersMr> list) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		CharactersMr ca = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from characters_mr_table");
			rs = pstm.executeQuery();
			while (rs.next()) {
				ca = new CharactersMr();

				int ac = rs.getInt("mr");
				int dodge = rs.getInt("dodge");
				double dmgDecrease = rs.getInt("dmg_decrease_rate") * 0.01;

				ca.setDodge(dodge);
				ca.setDmgDecrease(dmgDecrease);

				list.put(ac, ca);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public CharactersMr getCharactersMr(int ac) {
		return _list.get(ac) != null ? _list.get(ac) : null;
	}

	public class CharactersMr {
		private int dodge;
		private double dmgDecrease;

		public int getDodge() {
			return dodge;
		}

		public void setDodge(int dodge) {
			this.dodge = dodge;
		}

		public double getDmgDecrease() {
			return dmgDecrease;
		}

		public void setDmgDecrease(double dmgDecrease) {
			this.dmgDecrease = dmgDecrease;
		}
	}
}
