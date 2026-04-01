package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class AttrEnchantControlTable {
	private static AttrEnchantControlTable _instance;

	public static AttrEnchantControlTable getInstance() {
		if (_instance == null) {
			_instance = new AttrEnchantControlTable();
		}
		return _instance;
	}

	private Map<Integer, ArrayList<AttrEnchantControl>> _list = new HashMap<Integer, ArrayList<AttrEnchantControl>>();

	private AttrEnchantControlTable() {
		loadTable(_list);
	}

	public void reload() {
		Map<Integer, ArrayList<AttrEnchantControl>> list = new HashMap<Integer, ArrayList<AttrEnchantControl>>();
		loadTable(list);
		_list = list;
	}

	private void loadTable(Map<Integer, ArrayList<AttrEnchantControl>> list) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		ArrayList<AttrEnchantControl> arcList = null;
		AttrEnchantControl arc = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM attr_enchant_control");
			rs = pstm.executeQuery();
			while (rs.next()) {
				arc = new AttrEnchantControl();
				int safeEnchant = rs.getInt("safe_enchant");

				arcList = list.get(safeEnchant);
				if (arcList == null) {
					arcList = new ArrayList<AttrEnchantControl>();
					list.put(safeEnchant, arcList);
				}

				arc.setEnchantLevel(rs.getInt("tr_enchant_level"));
				arc.setMaxAttrEnchant(rs.getInt("max_attr_enchant_level"));

				arcList.add(arc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
	}

	public AttrEnchantControl getAttrEnchantControl(int safe, int enchant) {
		ArrayList<AttrEnchantControl> llist = _list.get(safe);
		if (llist == null)
			return null;

		for (AttrEnchantControl ar : llist) {
			if (ar.getEnchantLevel() == enchant)
				return ar;
		}

		return null;
	}

	public class AttrEnchantControl {
		private int enchantLevel;
		private int maxAttrEnchant;

		public int getEnchantLevel() {
			return enchantLevel;
		}

		public void setEnchantLevel(int enchantLevel) {
			this.enchantLevel = enchantLevel;
		}

		public int getMaxAttrEnchant() {
			return maxAttrEnchant;
		}

		public void setMaxAttrEnchant(int maxAttrEnchant) {
			this.maxAttrEnchant = maxAttrEnchant;
		}
	}
}
