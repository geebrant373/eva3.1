package l1j.server.TowerOfDominance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class DominanceDataLoader {
	static private Random _random = new Random(System.nanoTime());

	public static DominanceDataLoader _instance;

	public Map<Integer, DominanceBoss> _list = new HashMap<Integer, DominanceBoss>();

	static private List<DominanceBoss> list;

	public static DominanceDataLoader getInstance() {
		if (_instance == null) {
			_instance = new DominanceDataLoader();
		}
		return _instance;
	}

	public static void reload() {
		DominanceDataLoader oldInstance = _instance;
		_instance = new DominanceDataLoader();
		oldInstance._list.clear();
	}

	private DominanceDataLoader() {
		loadQuestMonster();
	}

	private void loadQuestMonster() {
		if (list == null)
			list = new ArrayList<DominanceBoss>();
		synchronized (list) {
			list.clear();
			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("SELECT * FROM tower_of_dominance");
				rs = pstm.executeQuery();
				while (rs.next()) {
					int rnd_time_min = rs.getInt("rnd_time_min");
					
					DominanceBoss DT = new DominanceBoss();
					String spawn_time = rs.getString("spawn_time");
					DT.setBossNum(rs.getInt("boss_num"));
					DT.setBossName(rs.getString("npc_name"));
					DT.setNpcId(rs.getInt("npc_id"));
					DT.setMapX(rs.getInt("map_x"));
					DT.setMapY(rs.getInt("map_y"));
					DT.setMapId(rs.getInt("map_id"));
					DT.setMentuse(rs.getBoolean("ment_use"));
					DT.setMent(rs.getString("ment"));
					DT.setAllEffect(rs.getBoolean("send_effect_use"));
					DT.setEffectNum(rs.getInt("effect_id"));
					DT.setRandomSpawn(rs.getInt("spawn_fail_per"));
					DT.setRndMinuteTime(rnd_time_min);
					
					String[] spawn_yoil = new String[] { "ÀüÃ¼" };
					try {
						StringTokenizer stt = new StringTokenizer(rs.getString("spawn_day"), ",");// |
						spawn_yoil = new String[stt.countTokens()];
						for (int i = 0; stt.hasMoreTokens(); ++i)
							spawn_yoil[i] = stt.nextToken();
					} catch (Exception e) {
					}
					
					DT.setYoil(spawn_yoil);
					
					if (spawn_time.length() > 0) {
						String[] spawn = spawn_time.split(",");

						int[][] time = new int[spawn.length][2];
						int[][] real_time = new int[spawn.length][2];

						int rnd_spawn_minute;

						for (int i = 0; i < spawn.length; i++) {
							String boss_time = spawn[i];
							String[] boss_result = boss_time.split(":");
							int boss_h = Integer.valueOf(boss_result[0]);
							int boss_m = Integer.valueOf(boss_result[1]);
							rnd_spawn_minute = rnd_time_min == 0 ? 0 : _random.nextInt(rnd_time_min);
							real_time[i][0] = boss_h;
							real_time[i][1] = boss_m;

							time[i][0] = boss_h;
							time[i][1] = (boss_m + rnd_spawn_minute);

							while (time[i][1] >= 60) {
								time[i][1] -= 60;
								time[i][0] += 1;
							}

							if (time[i][0] >= 24) {
								time[i][0] -= 24;
							}
						}

						DT.setBossTime(time);
					}

					list.add(DT);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				SQLUtil.close(rs, pstm, con);
			}
		}
	}

	static public List<DominanceBoss> getList() {
		synchronized (list) {
			return new ArrayList<DominanceBoss>(list);
		}
	}
	
	static public int getSize() {
		return list.size();
	}

	static public DominanceBoss find(int bossnum) {
		synchronized (list) {
			for (DominanceBoss b : list) {
				if (b.getBossNum() == bossnum)
					return b;
			}
			return null;
		}
	}

}