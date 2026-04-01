package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

public class CharacterBalance {
	private static Logger _log = Logger.getLogger(CharacterBalance.class.getName());

	private static CharacterBalance _instance;

	public static CharacterBalance getInstance() {
		if (_instance == null) {
			_instance = new CharacterBalance();
		}
		return _instance;
	}

	private Map<Integer, ArrayList<L1PvpDmg>> _list = new HashMap<Integer, ArrayList<L1PvpDmg>>();

	public void reload() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("reloading " + _log.getName().substring(_log.getName().lastIndexOf(".") + 1) + "...");

		Map<Integer, ArrayList<L1PvpDmg>> list = new HashMap<Integer, ArrayList<L1PvpDmg>>();
		loadList(list);
		_list = list;

		System.out.println("OK! " + timer.elapsedTimeMillis() + "ms");
	}

	private CharacterBalance() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("loading " + _log.getName().substring(_log.getName().lastIndexOf(".") + 1) + "...");

		loadList(_list);

		System.out.println("OK! " + timer.elapsedTimeMillis() + "ms");
	}

	private void loadList(Map<Integer, ArrayList<L1PvpDmg>> list) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		ArrayList<L1PvpDmg> edlist = null;
		L1PvpDmg ed = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM characters_balance");
			rs = pstm.executeQuery();
			while (rs.next()) {
				ed = new L1PvpDmg();
				int type = rs.getInt("클래스타입");
				edlist = list.get(type);

				if (edlist == null) {
					edlist = new ArrayList<L1PvpDmg>();
					list.put(type, edlist);
				}

				double dmg_rate = rs.getInt("대미지배율") * 0.01;
				double magic_dmg_rate = rs.getInt("마법대미지배율") * 0.01;
				
				ed.setTargetType(rs.getInt("대상클래스타입"));
				ed.setDmg(rs.getInt("추가대미지"));
				ed.setHit(rs.getInt("추가명중"));
				ed.setMagicDmg(rs.getInt("마법추가대미지"));
				ed.setMagicHit(rs.getInt("마법추가명중"));
				ed.setDmgRate(dmg_rate);
				ed.setMagicDmgRate(magic_dmg_rate);

				edlist.add(ed);
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
	}

	public int getDmg(int type, int target_type) {
		ArrayList<L1PvpDmg> edlist = _list.get(type);
		if (edlist != null) {
			for (L1PvpDmg ed : edlist) {
				if (ed.getTargetType() == target_type) {
					return ed.getDmg();
				}
			}
		}

		return 0;
	}

	public int getHit(int type, int target_type) {
		ArrayList<L1PvpDmg> edlist = _list.get(type);
		if (edlist != null) {
			for (L1PvpDmg ed : edlist) {
				if (ed.getTargetType() == target_type) {
					return ed.getHit();
				}
			}
		}

		return 0;
	}

	public int getMagicDmg(int type, int target_type) {
		ArrayList<L1PvpDmg> edlist = _list.get(type);
		if (edlist != null) {
			for (L1PvpDmg ed : edlist) {
				if (ed.getTargetType() == target_type) {
					return ed.getMagicDmg();
				}
			}
		}

		return 0;
	}

	public int getMagicHit(int type, int target_type) {
		ArrayList<L1PvpDmg> edlist = _list.get(type);
		if (edlist != null) {
			for (L1PvpDmg ed : edlist) {
				if (ed.getTargetType() == target_type) {
					return ed.getMagicHit();
				}
			}
		}

		return 0;
	}
	
	public double getDmgRate(int type, int target_type) {
		double rate = 0;
		ArrayList<L1PvpDmg> edlist = _list.get(type);
		if (edlist != null) {
			for (L1PvpDmg ed : edlist) {
				if (ed.getTargetType() == target_type) {
					rate += ed.getDmgRate();
					break;
				}
			}
		}
		return rate;
	}
	
	public double getMagicDmgRate(int type, int target_type) {
		double rate = 0;
		ArrayList<L1PvpDmg> edlist = _list.get(type);
		if (edlist != null) {
			for (L1PvpDmg ed : edlist) {
				if (ed.getTargetType() == target_type) {
					rate += ed.getMagicDmgRate();
					break;
				}
			}
		}

		return rate;
	}

	public class L1PvpDmg {
		private int _target;
		private int _dmg;
		private int _hit;
		private int _magicdmg;
		private int _magichit;

		public int getTargetType() {
			return _target;
		}

		public void setTargetType(int i) {
			_target = i;
		}

		public int getDmg() {
			return _dmg;
		}

		public void setDmg(int i) {
			_dmg = i;
		}

		public int getHit() {
			return _hit;
		}

		public void setHit(int i) {
			_hit = i;
		}

		public int getMagicDmg() {
			return _magicdmg;
		}

		public void setMagicDmg(int i) {
			_magicdmg = i;
		}

		public int getMagicHit() {
			return _magichit;
		}

		public void setMagicHit(int i) {
			_magichit = i;
		}

		private double _rate;

		public double getDmgRate() {
			return _rate;
		}

		public void setDmgRate(double i) {
			_rate = i;
		}
		
		private double _magicrate;

		public double getMagicDmgRate() {
			return _magicrate;
		}

		public void setMagicDmgRate(double i) {
			_magicrate = i;
		}
	}
}
