package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.ArmorBalanceTable.L1ArmorBalance;
import l1j.server.server.utils.SQLUtil;

public class ArmorBalanceTable {
	private static ArmorBalanceTable _instance;
	private Map<Integer, ArrayList<L1ArmorBalance>> _list = new HashMap<Integer, ArrayList<L1ArmorBalance>>();

	public static ArmorBalanceTable getInstance() {
		if (_instance == null) {
			_instance = new ArmorBalanceTable();
		}
		return _instance;
	}

	private ArmorBalanceTable() {
		loadArmorBalance();
	}

	public static void reload() {
		ArmorBalanceTable oldInstance = _instance;
		_instance = new ArmorBalanceTable();
		oldInstance._list.clear();
	}

	private void loadArmorBalance() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		ArrayList<L1ArmorBalance> subList = null;
		L1ArmorBalance wb = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM armor_balance");
			rs = pstm.executeQuery();
			int count = 0;
			while (rs.next()) {
				int itemId = rs.getInt("ITEM_ID");
				wb = new L1ArmorBalance();
				wb.setEnchant(rs.getInt("ITEM_ENCHANT"));
				wb.setAddReduc(rs.getInt("ADD_REDUC"));

				subList = _list.get(Integer.valueOf(itemId));
				if (subList == null) {
					subList = new ArrayList<L1ArmorBalance>();
					_list.put(Integer.valueOf(itemId), subList);
				}
				subList.add(wb);
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
	}

	public int getItemBalanceReduc(int itemid, int enchant) {
		ArrayList<L1ArmorBalance> subList = _list.get(Integer.valueOf(itemid));
		if (subList != null) {
			for (L1ArmorBalance wb : subList) {
				if (wb.getEnchant() == enchant) {
					return wb.getAddReduc();
				}
			}
		}
		return 0;
	}

	public class L1ArmorBalance {
		private int _enchant;
		private int _reduc;

		public L1ArmorBalance() {
		}

		public int getEnchant() {
			return this._enchant;
		}

		public void setEnchant(int i) {
			this._enchant = i;
		}

		public int getAddReduc() {
			return this._reduc;
		}

		public void setAddReduc(int i) {
			this._reduc = i;
		}
	}
}