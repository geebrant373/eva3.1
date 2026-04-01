package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.L1QueryUtil;
import l1j.server.server.utils.SQLUtil;

public class CharacterAutoHuntTable {
	private static Logger _log = Logger.getLogger(CharacterAutoHuntTable.class.getName());
	private static CharacterAutoHuntTable _instance;

	public static CharacterAutoHuntTable getInstance() {
		if (_instance == null) {
			_instance = new CharacterAutoHuntTable();
		}
		return _instance;
	}

	public void load(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_auto_hunt WHERE objid=?");
			pstm.setInt(1, pc.getId());
			rs = pstm.executeQuery();
			while (rs.next()) {
				pc.set_자동귀환퍼센트(rs.getInt("Auto_Return_Percent"));
				pc.setAutoTell(rs.getInt("Auto_Teleport_Use") != 0);
				pc.setAutoBuyPotion(rs.getInt("Auto_Buy_item_Use") != 0);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			System.out.println("■SQL접속 오류■ : 스크린샷을 찍어 주시길 바랍니다.");
		} catch (Exception e1) {
			System.out.println("■오류■ : 스크린샷을 찍어 주시길 바랍니다.");
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void store(L1PcInstance pc) {
		Connection con = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sql = "INSERT INTO character_auto_hunt (objid, char_name, Auto_Return_Percent, Auto_Teleport_Use, Auto_Buy_item_Use) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE objid=?, char_name=?, Auto_Return_Percent=?, Auto_Teleport_Use=?, Auto_Buy_item_Use=?";

			L1QueryUtil.execute(con, sql,
					new Object[] { Integer.valueOf(pc.getId()), pc.getName(), Integer.valueOf(pc.get_자동귀환퍼센트()),
							Integer.valueOf(pc.getAutoTell() ? 1 : 0), Integer.valueOf(pc.getAutoBuyPotion() ? 1 : 0),
							Integer.valueOf(pc.getId()), pc.getName(), Integer.valueOf(pc.get_자동귀환퍼센트()),
							Integer.valueOf(pc.getAutoTell() ? 1 : 0),
							Integer.valueOf(pc.getAutoBuyPotion() ? 1 : 0) });
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(con);
		}
	}
}
