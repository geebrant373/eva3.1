package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class MonsterBalance {
	public class Balance {
		int Damage = 0;
	}

	public class HitRate {
		int Hitrate = 0;
	}

	public class Reduc {
		int reduc = 0;
	}

	public class Dodge {
		int dodge = 0;
	}

	public class MagicDmg {
		int magicdmg = 0;
	}

	public class AddExp {
		int addexp = 0;
	}

	private static Logger _log = Logger.getLogger(MonsterBalance.class.getName());
	private static MonsterBalance _instance;

	private final Map<Integer, Balance> _idlist = new HashMap<Integer, Balance>();
	private final Map<Integer, HitRate> _idlist2 = new HashMap<Integer, HitRate>();
	private final Map<Integer, Reduc> _idlist3 = new HashMap<Integer, Reduc>();
	private final Map<Integer, Dodge> _idlist4 = new HashMap<Integer, Dodge>();

	private final Map<Integer, MagicDmg> _idlist5 = new HashMap<Integer, MagicDmg>();
	private final Map<Integer, AddExp> _idlist7 = new HashMap<Integer, AddExp>();

	public static MonsterBalance getInstance() {
		if (_instance == null) {
			_instance = new MonsterBalance();
		}
		return _instance;
	}

	private MonsterBalance() {
		characterBalance();
		characterHitrate();
		characterReduc();
		characterDodge();
		characterMagicDmg();
		characterAddExp();
	}

	public void characterBalance() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select id, addDmg from monster_balance");
			rs = pstm.executeQuery();
			Balance characterdamage = null;
			while (rs.next()) {
				characterdamage = new Balance();
				characterdamage.Damage = rs.getInt("addDmg");
				_idlist.put(rs.getInt("id"), characterdamage);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void characterHitrate() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select id, addHitRate from monster_balance");
			rs = pstm.executeQuery();
			HitRate characterhitrate = null;
			while (rs.next()) {
				characterhitrate = new HitRate();
				characterhitrate.Hitrate = rs.getInt("addHitRate");
				_idlist2.put(rs.getInt("id"), characterhitrate);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void characterReduc() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select id, addReduction from monster_balance");
			rs = pstm.executeQuery();
			Reduc characterreduc = null;
			while (rs.next()) {
				characterreduc = new Reduc();
				characterreduc.reduc = rs.getInt("addReduction");
				_idlist3.put(rs.getInt("id"), characterreduc);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void characterDodge() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select id, addDodge from monster_balance");
			rs = pstm.executeQuery();
			Dodge characterdodge = null;
			while (rs.next()) {
				characterdodge = new Dodge();
				characterdodge.dodge = rs.getInt("addDodge");
				_idlist4.put(rs.getInt("id"), characterdodge);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void characterMagicDmg() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select id, addMagicDmg from monster_balance");
			rs = pstm.executeQuery();
			MagicDmg charactermagicdmg = null;
			while (rs.next()) {
				charactermagicdmg = new MagicDmg();
				charactermagicdmg.magicdmg = rs.getInt("addMagicDmg");
				_idlist5.put(rs.getInt("id"), charactermagicdmg);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void characterAddExp() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select id, addExp from monster_balance");
			rs = pstm.executeQuery();
			AddExp charactermagicaddexp = null;
			while (rs.next()) {
				charactermagicaddexp = new AddExp();
				charactermagicaddexp.addexp = rs.getInt("addExp");
				_idlist7.put(rs.getInt("id"), charactermagicaddexp);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public double getCharacterHitRate(int Id) {
		HitRate characterhitrate = _idlist2.get(Id);
		if (characterhitrate == null) {
			return 0;
		}
		return characterhitrate.Hitrate;
	}

	public double getCharacterBalance(int Id) {
		Balance characterdamage = _idlist.get(Id);
		if (characterdamage == null) {
			return 0;
		}
		return characterdamage.Damage;
	}

	public double getCharacterReduc(int Id) {
		Reduc characterreduc = _idlist3.get(Id);
		if (characterreduc == null) {
			return 0;
		}
		return characterreduc.reduc;
	}

	public double getCharacterDodge(int Id) {
		Dodge characterdodge = _idlist4.get(Id);
		if (characterdodge == null) {
			return 0;
		}
		return characterdodge.dodge;
	}

	public double getCharacterMagicDmg(int Id) {
		MagicDmg charactermagicdmg = _idlist5.get(Id);
		if (charactermagicdmg == null) {
			return 0;
		}
		return charactermagicdmg.magicdmg;
	}

	public double getCharacterAddExp(int Id) {
		AddExp charactermagicaddexp = _idlist7.get(Id);
		if (charactermagicaddexp == null) {
			return 0;
		}
		return charactermagicaddexp.addexp;
	}

	public static void reload() {
		MonsterBalance oldInstance = _instance;
		_instance = new MonsterBalance();
		if (oldInstance != null)
			oldInstance._idlist.clear();
	}
}