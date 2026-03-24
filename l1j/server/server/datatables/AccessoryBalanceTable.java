package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class AccessoryBalanceTable {
	private static AccessoryBalanceTable _instance;
	private Map<Integer, ArrayList<L1AccessoryBalance>> _list = new HashMap<Integer, ArrayList<L1AccessoryBalance>>();

	public static AccessoryBalanceTable getInstance() {
		if (_instance == null) {
			_instance = new AccessoryBalanceTable();
		}
		return _instance;
	}

	private AccessoryBalanceTable() {
		loadWeaponBalance();
	}

	public static void reload() {
		AccessoryBalanceTable oldInstance = _instance;
		_instance = new AccessoryBalanceTable();
		oldInstance._list.clear();
	}

	private void loadWeaponBalance() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		ArrayList<L1AccessoryBalance> subList = null;
		L1AccessoryBalance wb = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM accessory_balance");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int itemId = rs.getInt("itemId");
				wb = new L1AccessoryBalance();
				wb.setEnchant(rs.getInt("enchantLevel"));
				wb.setAddDmg(rs.getInt("damage"));
				wb.setAddReduction(rs.getInt("reduction"));
				wb.setAddMagicdmg(rs.getInt("magicdmg"));
				
				subList = _list.get(Integer.valueOf(itemId));
				if (subList == null) {
					subList = new ArrayList<L1AccessoryBalance>();
					_list.put(Integer.valueOf(itemId), subList);
				}
				subList.add(wb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
	}

	public int getItemBalanceDmg(int itemid, int enchant) {
		ArrayList<L1AccessoryBalance> subList = _list.get(Integer.valueOf(itemid));
		if (subList != null) {
			for (L1AccessoryBalance wb : subList) {
				if (wb.getEnchant() == enchant) {
					return wb.getAddDmg();
				}
			}
		}
		return 0;
	}

	public int getItemBalanceReduction(int itemid, int enchant) {
		ArrayList<L1AccessoryBalance> subList = _list.get(Integer.valueOf(itemid));
		if (subList != null) {
			for (L1AccessoryBalance wb : subList) {
				if (wb.getEnchant() == enchant) {
					return wb.getAddReduction();
				}
			}
		}
		return 0;
	}

	public int getItemBalanceMagicdmg(int itemid, int enchant) {
		ArrayList<L1AccessoryBalance> subList = _list.get(Integer.valueOf(itemid));
		if (subList != null) {
			for (L1AccessoryBalance wb : subList) {
				if (wb.getEnchant() == enchant) {
					return wb.getAddMagicdmg();
				}
			}
		}
		return 0;
	}
	
	public class L1AccessoryBalance {
		private int _enchant;
		private int _dmg;
		private int _reduction;
		private int _magicdmg;
		
		public L1AccessoryBalance() {
		}

		public int getEnchant() {
			return this._enchant;
		}

		public void setEnchant(int i) {
			this._enchant = i;
		}

		public int getAddDmg() {
			return this._dmg;
		}

		public void setAddDmg(int i) {
			this._dmg = i;
		}

		public int getAddReduction() {
			return this._reduction;
		}

		public void setAddReduction(int i) {
			this._reduction = i;
		}
		
		public int getAddMagicdmg() {
			return this._magicdmg;
		}

		public void setAddMagicdmg(int i) {
			this._magicdmg = i;
		}
	}
}
