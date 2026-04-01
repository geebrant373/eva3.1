package l1j.server.server.datatables;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;


public class SpawnBossTable {
	private static SpawnBossTable _instance;

	public static SpawnBossTable getInstance() {
		if (_instance == null) {
			_instance = new SpawnBossTable();
		}
		return _instance;
	}

	private static Logger _log = Logger.getLogger(SpawnBossTable.class.getName());

	private ArrayList<BossTemp> bosslist = new ArrayList<BossTemp>();

	private Random rnd = new Random(System.nanoTime());

	public ArrayList<BossTemp> getlist() {
		return bosslist;
	}
	
	public static void reload() {
		SpawnBossTable oldInstance = _instance;
		_instance = new SpawnBossTable();
		oldInstance.bosslist.clear();
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("reloading " + _log.getName().substring(_log.getName().lastIndexOf(".") + 1) + "...");
		System.out.println("OK! " + timer.elapsedTimeMillis() + "ms");
	}
	
	

	private SpawnBossTable() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		FileOutputStream fos;
		BufferedOutputStream bos;
		
		try {
			
			
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_boss_new");
			rs = pstm.executeQuery();

			while (rs.next()) {
				BossTemp temp = new BossTemp();
				temp.npcid = rs.getInt("npcid");
				String text = rs.getString("spawn_time");
				temp.rndLoc = rs.getInt("rndXY");
				temp.rndTime = rs.getInt("random_time");
				temp.Groupid = rs.getInt("groupid");
				temp.DeleteTime = rs.getInt("DeleteTime");
				temp.isMent = rs.getInt("is_ment") == 0 ? false : true;
				temp.Ment = rs.getString("ment");

				StringTokenizer s = new StringTokenizer(text, "\n");
				int number = 0;
				while (s.hasMoreElements()) {
					String temp2 = "";
					StringTokenizer values = new StringTokenizer(s.nextToken());
					while (values.hasMoreElements()) {// 공백제거
						temp2 += values.nextToken();
					}
				//	 System.out.println("temp2 = " + temp2);

				 // 스폰시간
						StringTokenizer mdata = new StringTokenizer(temp2, "/");
						ArrayList<Integer> Hourlist = new ArrayList<Integer>();
						ArrayList<Integer> Minutelist = new ArrayList<Integer>();
						while (mdata.hasMoreElements()) {
							String Times = mdata.nextToken();
							StringTokenizer Hours = new StringTokenizer(Times, ":");
							String Hour = Hours.nextToken();
							StringTokenizer Minutes = new StringTokenizer(Hours.nextToken());
							String Minute = Minutes.nextToken();
							Hourlist.add(Integer.parseInt(Hour.trim()));
							Minutelist.add(Integer.parseInt(Minute.trim()));
						}

						temp.SpawnHour = new int[Hourlist.size()];
						temp.SpawnMinute = new int[Hourlist.size()];
						for (int i = 0; i < Hourlist.size(); i++) {
							int Hour = Hourlist.get(i);
							int Minute = Minutelist.get(i);
							temp.SpawnHour[i] = Hour;
							temp.SpawnMinute[i] = Minute;
							 //System.out.println("Hour = " + Hour);
							 //System.out.println("Minute = " + Minute);
						}
						
					 // 스폰좌표
					
						
						temp.SpawnLoc = new int[3];
						temp.SpawnLoc[0] = rs.getInt("loc_x");
						temp.SpawnLoc[1] = rs.getInt("loc_y");
						temp.SpawnLoc[2] = rs.getInt("map_id");
						// System.out.println("스폰좌표x = " + temp.SpawnLoc[0]);
						// System.out.println("스폰좌표y = " + temp.SpawnLoc[1]);
						// System.out.println("스폰좌표m = " + temp.SpawnLoc[2]);
					

					number++;
				}
				bosslist.add(temp);
			}

			
			
			/*for (BossTemp temp : bosslist) {
				temp.NearTime = new int[temp.SpawnHour.length];
				for (int i = 0; i < temp.SpawnHour.length; i++) {
					if (temp.rndTime != 0) {
						int Hour = temp.SpawnHour[i];
						int rndtime = rnd.nextInt(temp.rndTime) + 1;
						int Minute = temp.SpawnMinute[i] + rndtime;
						if (Minute >= 120) {
							Hour += 2;
							Minute -= 120;
						} else if (Minute >= 60) {
							Hour++;
							Minute -= 60;
						}
						StringBuffer NewText = new StringBuffer();
						NewText.append(Hour);
						NewText.append(Minute < 10 ? "0" + Minute : Minute);
						temp.NearTime[i] = Integer.parseInt(NewText.toString().trim());
						// System.out.println("temp.NearTime[i] = " + temp.NearTime[i]);
					} else {
						StringBuffer NewText = new StringBuffer();
						NewText.append(temp.SpawnHour[i]);
						NewText.append(temp.SpawnMinute[i] < 10 ? "0" + temp.SpawnMinute[i] : temp.SpawnMinute[i]);
						temp.NearTime[i] = Integer.parseInt(NewText.toString().trim());
						// System.out.println("temp.NearTime[i] = " + temp.NearTime[i]);
						
					}
				}
			}*/
		
		} catch (SQLException e) {
			e.printStackTrace();
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
 
	/*
	 * 1~6 0 
	 * 스폰데이: 월,화,수,목,금,토,일 
	 * 스폰타임: 2시 00분, 5시 30분 
	 * 랜덤타임: 0분 
	 * 삭제타임: 3600초 
	 * 스폰좌표: 32726, 32832, 603 
	 * 랜덤범위: 0 
	 * 그룹스폰: 0 
	 * YN메세지: 1 
	 * 스폰멘트: 1
	 */
	public static class BossTemp {
		public int npcid;
		public boolean isSpawn;
		public int[] Day;
		public int[] SpawnHour;
		public int[] SpawnMinute;
		public int[] NearTime;
		public int rndTime;
		public int DeleteTime;
		public int[] SpawnLoc;
		public int rndLoc;
		public int Groupid;
		
		public boolean isMent;
		public String Ment;
		public int timeM;
	}
}