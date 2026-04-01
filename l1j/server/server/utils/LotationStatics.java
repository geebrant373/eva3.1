package l1j.server.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class LotationStatics {
	public static LotationStatics getInstance() {
		if (_instance == null) {
			_instance = new LotationStatics();
		}
		return _instance;
	}

	private static LotationStatics _instance;
	private static Logger _log = Logger.getLogger(LotationStatics.class.getName());

	/*public static void Reload() {
		_instance.ReloadData();
	}*/

	// COMMON Func.
	/*public void ReloadData() {
		LotationStatics oldInstance = _instance;
		_instance = new LotationStatics();
		oldInstance.MapEventList.clear();
	}*/

	
	public static void reload() {
		synchronized (_instance) {
			LotationStatics oldInstance = _instance;
			_instance = new LotationStatics();
			oldInstance.MapEventList.clear();
		}
	}
	private LotationStatics() {
		ReadTable_MapOpenTime();
	}

	// Common Data Checker
	public static String ShowDebug(String command) {
		String res = "";
		LotationStatics datas = LotationStatics.getInstance();
		switch (command) {
		case "맵이벤":
			res += "\\f1[MapEventList] Count:" + datas.MapEventList.size() + "\n";
			for (int i = 0; i < datas.MapEventList.size(); i++) {
				res += "[" + datas.MapEventList.get(i).Id + ":";
				for (int j = 0; j < datas.MapEventList.get(i).Maps.length; j++) {
					res += datas.MapEventList.get(i).Maps[j];
					if (j < datas.MapEventList.get(i).Maps.length - 1)
						res += ",";
				}
				res += "]";
			}
			break;
		}
		return res;
	}

	public static boolean IsMapEventMapId(int id) {
		ArrayList<MapEventInfo> list = _instance.MapEventList;
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).Maps.length; j++)
				if (list.get(i).Maps[j] == id)
					return true;
		}
		return false;
	}

	// -------------------------------------------------------------------------------------------------------
	// 로테이션 시스템 추가
	// -------------------------------------------------------------------------------------------------------
	public static class MapEventInfo {
		public String Id;
		public String Type; // 맵오픈 타입
		public int[] Maps;

		public int[] Day;
		public int[] SpawnHour;
		public int[] SpawnMinute;
		public int[] NearTime;

		public int DuringTime;
		public boolean isDO;
		public String Notice;
		public int EntryNpc;
		public int[] EntryNpcLoc;
	}

	private ArrayList<MapEventInfo> MapEventList = new ArrayList<MapEventInfo>();

	public ArrayList<MapEventInfo> GetMapInfoList() {
		return MapEventList;
	}

	private void ReadTable_MapOpenTime() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_map");
			rs = pstm.executeQuery();

			while (rs.next()) {
				MapEventInfo temp = new MapEventInfo();
				temp.Id = rs.getString("id");
				temp.Type = rs.getString("type");
				String text = rs.getString("info");
				temp.Notice = rs.getString("notice");
				temp.EntryNpc = Integer.parseInt(rs.getString("entrynpc"));

				StringTokenizer s = new StringTokenizer(text, "\r\n");
				int line = 0;
				while (s.hasMoreElements()) {
					String temp2 = "";
					StringTokenizer values = new StringTokenizer(s.nextToken(), "위크: 타임유지맵NPC좌표");
					StringTokenizer mdata;
					while (values.hasMoreElements())
						temp2 += values.nextToken();

					// LINE 단위로 읽음
					switch (line) {
					case 0: // 맵리스트
						StringTokenizer Maps = new StringTokenizer(temp2, ",");
						ArrayList<String> StrList = new ArrayList<String>();
						while (Maps.hasMoreElements()) {
							String map = Maps.nextToken();
							StrList.add(new String(map));
						}
						temp.Maps = new int[StrList.size()];
						for (int i = 0; i < StrList.size(); i++)
							temp.Maps[i] = Integer.parseInt(StrList.get(i));
						break;
					case 1: // 요일
						StringTokenizer Day = new StringTokenizer(temp2, ",");
						ArrayList<Integer> list = new ArrayList<Integer>();
						while (Day.hasMoreElements()) {
							String day = Day.nextToken();
							list.add(new Integer(GetWeekToInt(day)));
						}
						temp.Day = new int[list.size()];
						for (int i = 0; i < list.size(); i++)
							temp.Day[i] = list.get(i);
						break;
					case 2: // 시간
						mdata = new StringTokenizer(temp2, ",");
						ArrayList<Integer> Hourlist = new ArrayList<Integer>();
						ArrayList<Integer> Minutelist = new ArrayList<Integer>();
						while (mdata.hasMoreElements()) {
							String Times = mdata.nextToken();
							StringTokenizer Hours = new StringTokenizer(Times, "시");
							String Hour = Hours.nextToken();
							StringTokenizer Minutes = new StringTokenizer(Hours.nextToken(), "분");
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
						}
						break;
					case 3: // 유지타임
						mdata = new StringTokenizer(temp2, "초");
						temp.DuringTime = Integer.parseInt(mdata.nextToken().trim());
						break;
					case 4: // 좌표
						mdata = new StringTokenizer(temp2, ",");
						temp.EntryNpcLoc = new int[3];
						temp.EntryNpcLoc[0] = Integer.parseInt(mdata.nextToken().trim());
						temp.EntryNpcLoc[1] = Integer.parseInt(mdata.nextToken().trim());
						temp.EntryNpcLoc[2] = Integer.parseInt(mdata.nextToken().trim());
						break;
					}
					line++;
				}
				MapEventList.add(temp);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private int GetWeekToInt(String day) {
		if (day.equalsIgnoreCase("일")) {
			return 0;
		} else if (day.equalsIgnoreCase("월")) {
			return 1;
		} else if (day.equalsIgnoreCase("화")) {
			return 2;
		} else if (day.equalsIgnoreCase("수")) {
			return 3;
		} else if (day.equalsIgnoreCase("목")) {
			return 4;
		} else if (day.equalsIgnoreCase("금")) {
			return 5;
		} else if (day.equalsIgnoreCase("토")) {
			return 6;
		}
		return 0;
	}
}
