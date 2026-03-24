package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class CharactersReducTable {
	private static CharactersReducTable _instance;

	public static CharactersReducTable getInstance() {
		if (_instance == null) {
			_instance = new CharactersReducTable();
		}
		return _instance;
	}

	private Map<Integer, CharactersReduc> _list = new HashMap<Integer, CharactersReduc>();

	private CharactersReducTable() {
		loadTable(_list);
	}
	
	public void reload() {
		Map<Integer, CharactersReduc> list = new HashMap<Integer, CharactersReduc>();
		loadTable(list);
		_list = list;
	}

	private void loadTable(Map<Integer, CharactersReduc> list) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		CharactersReduc ca = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from characters_reduc_table");
			rs = pstm.executeQuery();
			while (rs.next()) {
				ca = new CharactersReduc();

				int level = rs.getInt("level");
				int dodge = rs.getInt("dodge");
				int reduc = rs.getInt("reduc");

				ca.setDodge(dodge);
				ca.setReduc(reduc);

				list.put(level, ca);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public CharactersReduc getCharactersReduc(int level) {
		return _list.get(level) != null ? _list.get(level) : null;
	}

	public class CharactersReduc {
		private int dodge;
		private int reduc;

		public int getDodge() {
			return dodge;
		}

		public void setDodge(int dodge) {
			this.dodge = dodge;
		}

		public int getReduc() {
			return reduc;
		}

		public void setReduc(int reduc) {
			this.reduc = reduc;
		}

	}
}
