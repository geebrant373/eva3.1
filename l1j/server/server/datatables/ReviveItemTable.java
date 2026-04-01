package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import l1j.server.L1DatabaseFactory;

public class ReviveItemTable {

	private static ReviveItemTable _instance;

	private HashMap<Integer, L1ReviveItem> _reviveItems = new HashMap<Integer, L1ReviveItem>(); // itemId,로스트템.

	public static ReviveItemTable getInstance() {
		if (_instance == null) {
			_instance = new ReviveItemTable();
		}
		return _instance;
	}

	private ReviveItemTable() {
		loadReviveItems();
	}

	public static void reload() {
		ReviveItemTable oldInstance = _instance;
		_instance = new ReviveItemTable();
		oldInstance._reviveItems.clear();
	}

	private void loadReviveItems() {
		try (Connection con = L1DatabaseFactory.getInstance().getConnection();
				PreparedStatement pstm = con.prepareStatement("SELECT * FROM revive_items");
				ResultSet rs = pstm.executeQuery()) {
			while (rs.next()) {
				int itemId = rs.getInt("consum_id");
				L1ReviveItem reviveItem = new L1ReviveItem(itemId, rs.getInt("store_id"), rs.getInt("chance"), rs.getInt("is_ment") == 0 ? false : true, rs.getString("ment"));
				_reviveItems.put(itemId, reviveItem);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public L1ReviveItem getReviveItem(int itemId) {
		return _reviveItems.get(itemId);
	}

	public class L1ReviveItem {
		public int itemId;
		public int storeId;
		public int chance;
		public boolean isMent;
		public String Ment;
		

		public L1ReviveItem(int itemId, int storeId, int chance, boolean isMent, String Ment) {
			this.itemId = itemId;
			this.storeId = storeId;
			this.chance = chance;
			this.isMent = isMent;
			this.Ment = Ment;
		}
	}
}
