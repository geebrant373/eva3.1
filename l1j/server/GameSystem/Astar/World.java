package l1j.server.GameSystem.Astar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import l1j.server.GameSystem.Astar.share.TimeLine;
import l1j.server.server.model.map.L1V1Map;
import l1j.server.server.model.map.L1WorldMap;

public final class World {

	static private Map<Integer, l1j.server.GameSystem.Astar.bean.Map> list;

	static public void init() {
		TimeLine.start("월드맵 불러오기....안해");
		list = new HashMap<Integer, l1j.server.GameSystem.Astar.bean.Map>();

		try {
			File f = new File("Sabu/maps/Cache");
			// 폴더가 존재할경우
			if (f.isDirectory()) {
				// 캐쉬파일로부터 맵 로딩
				read(false);
				// 폴더가 존재하지 않을경우
			} else {
				System.out.println("캐쉬 폴더가 존재하지 않습니다.");
				// 폴더생성
				f.mkdir();
				// txt파일로부터 맵 로딩
				read(true);
				// 캐쉬파일 작성
				writeCache();
			}
		} catch (Exception e) {
			System.out.printf("%s : init()\r\n", World.class.toString());
			System.out.println(e);
		}

		TimeLine.end();
	}

	static private void writeCache() throws Exception {
		try {

			System.out.println("캐쉬 파일을 생성하고 있습니다.");
			BufferedOutputStream bw = null;
			for (l1j.server.GameSystem.Astar.bean.Map m : list.values()) {
				bw = new BufferedOutputStream(new FileOutputStream(
						String.format("Sabu/maps/Cache/%d.data", +m.mapid)));
				bw.write(m.data);
				bw.close();
			}
			System.out.println(" (완료)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static private void read(boolean type) throws Exception {
		// text로부터 읽는거 알림용
		try {

			if (type)
				System.out.println("Text 파일에서 월드맵 정보를 추출하고 있습니다.");

			String maps;
			StringTokenizer st1;
			BufferedReader lnrr = new BufferedReader(new FileReader(
					"Sabu/maps/maps.csv"));
			byte[] temp = new byte[22149121];
			while ((maps = lnrr.readLine()) != null) {
				if (maps.startsWith("#")) {
					continue;
				}
				st1 = new StringTokenizer(maps, ",");
				int readID = Integer.parseInt(st1.nextToken());
				int x1 = Integer.parseInt(st1.nextToken());
				int x2 = Integer.parseInt(st1.nextToken());
				int y1 = Integer.parseInt(st1.nextToken());
				int y2 = Integer.parseInt(st1.nextToken());
				int size = Integer.parseInt(st1.nextToken());
				if (type)
					readText(temp, readID, size, x1, x2, y1, y2);
				else
					readCache(readID, x1, x2, y1, y2, size);
			}
			temp = null;
			st1 = null;
			lnrr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static private void readText(byte[] temp, int readID, int size, int x1,
			int x2, int y1, int y2) throws Exception {

		int TotalSize = -1;
		String line;
		StringTokenizer st = null;

		BufferedReader lnr = new BufferedReader(new FileReader(String.format(
				"Sabu/maps/Text/%d.txt", readID)));
		while ((line = lnr.readLine()) != null) {

			st = new StringTokenizer(line, ",");

			for (int i = 0; i < size; ++i) {
				int t = 0;
				try {
					t = Integer.parseInt(st.nextToken());
				} catch (Exception e) {
					System.out.println(readID);
				}
				if (Byte.MAX_VALUE < t) {
					temp[++TotalSize] = Byte.MAX_VALUE;
				} else {
					temp[++TotalSize] = (byte) t;
				}
			}
		}
		byte[] MAP = new byte[TotalSize - 1];
		try {
			System.arraycopy(temp, 0, MAP, 0, MAP.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		l1j.server.GameSystem.Astar.bean.Map m = new l1j.server.GameSystem.Astar.bean.Map();
		m.mapid = readID;
		m.locX1 = x1;
		m.locX2 = x2;
		m.locY1 = y1;
		m.locY2 = y2;
		m.size = size;
		m.data = MAP;
		m.data_size = m.data.length;
		m.dataDynamic = new byte[m.data_size];
		m.isdoor = new boolean[m.data_size][8];
		list.put(m.mapid, m);
		try {
			lnr.close();
		} catch (Exception e) {
		}
	}

	static private void readCache(int readID, int x1, int x2, int y1, int y2,
			int size) throws Exception {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				String.format("Sabu/maps/Cache/%d.data", readID)));
		byte[] data = new byte[bis.available()];
		bis.read(data, 0, data.length);
		l1j.server.GameSystem.Astar.bean.Map m = new l1j.server.GameSystem.Astar.bean.Map();
		m.mapid = readID;
		m.locX1 = x1;
		m.locX2 = x2;
		m.locY1 = y1;
		m.locY2 = y2;
		m.size = size;
		m.data = data;
		m.data_size = data.length;
		// System.out.println("x1>"+x1+" x2>"+x2+" y1>"+y1+" y2>"+y2+" > "+m.data_size);
		m.dataDynamic = new byte[m.data_size];
		m.isdoor = new boolean[m.data_size][8];
		list.put(m.mapid, m);
		bis.close();
		bis = null;
	}

	static public boolean get_map(int map) {
		return L1WorldMap.getInstance().getMapCK((short) map);
	}

	static public void cloneMap(int targetId, int newId) {
	}

	static public void resetMap(int targetId, int resetId) {

	}


	private static final byte BITFLAG_IS_DOOR_IMPASSABLE_X = (byte) 0x80;
	private static final byte BITFLAG_IS_DOOR_IMPASSABLE_Y = (byte) 0x40;

	static public void 문이동(int x, int y, int map, boolean h, boolean flag) {
		L1V1Map m = (L1V1Map) L1WorldMap.getInstance().getMap((short) map);
		if (m != null) {
			if (!m.isInMap(x, y)) {
				return;
			}
			if (flag) {
				synchronized (m._doorMap) {
					m._doorMap[x - m.getX()][y - m.getY()] = 0;
				}
			} else {
				byte setBit = BITFLAG_IS_DOOR_IMPASSABLE_Y;
				if (h) {
					setBit = BITFLAG_IS_DOOR_IMPASSABLE_X;
				}
				synchronized (m._doorMap) {
					m._doorMap[x - m.getX()][y - m.getY()] = setBit;
				}
			}
		}
	}

	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	static public boolean 문이동(int x, int y, int map, int h) {
		if (h < 0 || h > 7) {
			return false;
		}
		L1V1Map m = (L1V1Map) L1WorldMap.getInstance().getMap((short) map);
		if (m != null) {
			int newX = x + HEADING_TABLE_X[h];
			int newY = y + HEADING_TABLE_Y[h];
			if (x > newX) {
				int doorTile1 = accessDoorTile(newX, y, m);
				int doorTile2 = accessDoorTile(newX, newY, m);
				if (((doorTile1 & BITFLAG_IS_DOOR_IMPASSABLE_X) != 0) || ((doorTile2 & BITFLAG_IS_DOOR_IMPASSABLE_X) != 0))
					return true;
			} else if (x < newX) {
				int doorTileOld = accessDoorTile(x, y, m);
				int doorTileNew = accessDoorTile(newX - 1, newY, m);
				if (((doorTileOld & BITFLAG_IS_DOOR_IMPASSABLE_X) != 0) || ((doorTileNew & BITFLAG_IS_DOOR_IMPASSABLE_X) != 0))
					return true;
			}
			if (y < newY) {
				int doorTile1 = accessDoorTile(x, newY, m);
				int doorTile2 = accessDoorTile(newX, newY, m);
				if (((doorTile1 & BITFLAG_IS_DOOR_IMPASSABLE_Y) != 0) || ((doorTile2 & BITFLAG_IS_DOOR_IMPASSABLE_Y) != 0))
					return true;
			} else if (y > newY) {
				int doorTileOld = accessDoorTile(x, y, m);
				int doorTileNew = accessDoorTile(newX, newY + 1, m);
				if (((doorTileOld & BITFLAG_IS_DOOR_IMPASSABLE_Y) != 0) || ((doorTileNew & BITFLAG_IS_DOOR_IMPASSABLE_Y) != 0))
					return true;
			}
		}
		return false;
	}

	private static int accessDoorTile(int x, int y, L1V1Map m) {
		if (!m.isInMap(x, y)) {
			return 0;
		}
		synchronized (m._doorMap) {
			return m._doorMap[x - m.getX()][y - m.getY()];
		}
	}

	static public boolean isMapdynamic(int x, int y, int map) {
		L1V1Map m = (L1V1Map) L1WorldMap.getInstance().getMap((short) map);
		return !m.isPassable(x, y);
	}

	static public boolean isThroughObject(int x, int y, int map, int dir) {
		L1V1Map m = (L1V1Map) L1WorldMap.getInstance().getMap((short) map);
		if (map >= 10010 && map <= 10100) {
			switch (dir) {
			case 0:
				if (m.accessTile(x, y - 1) != 0) {
					return true;
				} else {
					return false;
				}
			case 1:
				if (m.accessTile(x + 1, y - 1) != 0) {
					return true;
				} else {
					return false;
				}
			case 2:
				if (m.accessTile(x + 1, y) != 0) {
					return true;
				} else {
					return false;
				}
			case 3:
				if (m.accessTile(x + 1, y + 1) != 0) {
					return true;
				} else {
					return false;
				}
			case 4:
				if (m.accessTile(x, y + 1) != 0) {
					return true;
				} else {
					return false;
				}
			case 5:
				if (m.accessTile(x - 1, y + 1) != 0) {
					return true;
				} else {
					return false;
				}
			case 6:
				if (m.accessTile(x - 1, y) != 0) {
					return true;
				} else {
					return false;
				}
			case 7:
				if (m.accessTile(x - 1, y - 1) != 0) {
					return true;
				} else {
					return false;
				}
			}
		}
		// return m.isPassable(x, y, dir);
		switch (dir) {
		case 0:
			return (m.accessTile(x, y) & 2) > 0;
		case 1:
			return ((m.accessTile(x, y) & 2) > 0 && (m.accessTile(x, y - 1) & 1) > 0)
					|| ((m.accessTile(x, y) & 1) > 0 && (m.accessTile(x + 1, y) & 2) > 0);
		case 2:
			return (m.accessTile(x, y) & 1) > 0;
		case 3:
			return ((m.accessTile(x, y + 1) & 2) > 0 && (m.accessTile(x, y + 1) & 1) > 0)
					|| ((m.accessTile(x, y) & 1) > 0 && (m.accessTile(x + 1,
							y + 1) & 2) > 0);
		case 4:
			return (m.accessTile(x, y + 1) & 2) > 0;
		case 5:
			return ((m.accessTile(x, y + 1) & 2) > 0 && (m.accessTile(x - 1,
					y + 1) & 1) > 0)
					|| ((m.accessTile(x - 1, y) & 1) > 0 && (m.accessTile(
							x - 1, y + 1) & 2) > 0);
		case 6:
			return (m.accessTile(x - 1, y) & 1) > 0;
		case 7:
			return ((m.accessTile(x, y) & 2) > 0 && (m.accessTile(x - 1, y - 1) & 1) > 0)
					|| ((m.accessTile(x - 1, y) & 1) > 0 && (m.accessTile(
							x - 1, y) & 2) > 0);
		default:
			break;
		}
		return false;
	}

	static public boolean isThroughAttack(int x, int y, int map, int dir) {
		L1V1Map m = (L1V1Map) L1WorldMap.getInstance().getMap((short) map);
		return m.isArrowPassable(x, y, dir);
	}

}
