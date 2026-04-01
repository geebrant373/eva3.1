package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.WeaponMagicPcBalanceTable.L1WeaponBalance;
import l1j.server.server.utils.SQLUtil;

public class WeaponMagicPcBalanceTable {
	private static WeaponMagicPcBalanceTable _instance;
	private Map<Integer, ArrayList<L1WeaponBalance>> _list = new HashMap<Integer, ArrayList<L1WeaponBalance>>();

	public static WeaponMagicPcBalanceTable getInstance() {
		if (_instance == null) {
			_instance = new WeaponMagicPcBalanceTable();
		}
		return _instance;
	}

	private WeaponMagicPcBalanceTable() {
		loadWeaponBalance();
	}

	public static void reload() {
		WeaponMagicPcBalanceTable oldInstance = _instance;
		_instance = new WeaponMagicPcBalanceTable();
		oldInstance._list.clear();
	}

	private void loadWeaponBalance() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		ArrayList<L1WeaponBalance> subList = null;
		L1WeaponBalance wb = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM weapon_magic_damege_pc");
			rs = pstm.executeQuery();
			int count = 0;
			while (rs.next()) {
				int itemId = rs.getInt("itemId");
				wb = new L1WeaponBalance();
				wb.setEnchant(rs.getInt("enchantLevel"));
				wb.setAddDmg(rs.getInt("damage"));

				subList = _list.get(Integer.valueOf(itemId));
				if (subList == null) {
					subList = new ArrayList<L1WeaponBalance>();
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

	public int getItemBalanceDmg(int itemid, int enchant) {
		ArrayList<L1WeaponBalance> subList = _list.get(Integer.valueOf(itemid));
		if (subList != null) {
			for (L1WeaponBalance wb : subList) {
				if (wb.getEnchant() == enchant) {
					return wb.getAddDmg();
				}
			}
		}
		return 0;
	}

	public class L1WeaponBalance {
		private int _enchant;
		private int _dmg;

		public L1WeaponBalance() {
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
	}
}
