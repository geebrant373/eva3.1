package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.random.RandomGenerator;
import l1j.server.random.RandomGeneratorFactory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

public class DropEventTable {
	private static Logger _log = Logger.getLogger(DropEventTable.class.getName());
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	private static DropEventTable _instance;
	private static Map<Integer, ArrayList<DropEventData>> _dropEvents = new HashMap<Integer, ArrayList<DropEventData>>();

	public static DropEventTable getInstance() {
		if (_instance == null) {
			_instance = new DropEventTable();
		}
		return _instance;
	}

	private DropEventTable() {
		load();
	}

	private void loadDropEvents(Map<Integer, ArrayList<DropEventData>> dropEvents) {
		Connection con = null;
		PreparedStatement pstm = null;	
		ResultSet rs = null;	
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM drop_event");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int mapId = rs.getInt("map_id");
				L1Map pobyMap = L1WorldMap.getInstance().getMap((short) mapId);
				if (pobyMap == null) {
					System.out.println("[DropEventData] - 존재하지 않는 맵번호: " + mapId);
					continue;
				}
				int itemId = rs.getInt("item_id");
				L1Item pobyItem = ItemTable.getInstance().getTemplate(itemId);
				if (pobyItem == null) {
					System.out.println("[DropEventData] - 존재하지 않는 아이템번호: " + itemId);
					continue;
				}

				DropEventData data = new DropEventData();
				data._itemId = rs.getInt("item_id");
				data._bless = rs.getInt("bless");
				data._min = rs.getInt("min");
				data._max = rs.getInt("max");
				data._chance = (int) (rs.getFloat("chance") * 1.0F);

				ArrayList<DropEventData> dataList = (ArrayList<DropEventData>) dropEvents.get(Integer.valueOf(mapId));
				if (dataList == null) {
					dataList = new ArrayList<DropEventData>();				
					dropEvents.put(new Integer(mapId), dataList);
				}
				dataList.add(data);

				dropEvents.put(new Integer(mapId), dataList);			
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void load() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("loading " + _log.getName().substring(_log.getName().lastIndexOf(".") + 1) + "...");
		loadDropEvents(_dropEvents);
		System.out.println("OK! " + timer.get() + " ms");
	}

	public void reload() {

		Map<Integer, ArrayList<DropEventData>> dropEvents = new HashMap<Integer, ArrayList<DropEventData>>();
		loadDropEvents(dropEvents);
		_dropEvents = dropEvents;
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("reloading " + _log.getName().substring(_log.getName().lastIndexOf(".") + 1) + "...");
		System.out.println("OK! " + timer.elapsedTimeMillis() + "ms");


	}

	public boolean isDropEvent(int mapId) {
		return _dropEvents.get(mapId) != null;
	}

	public void storeEventItem(L1NpcInstance npc) {
		if ((npc.getNpcTemplate().getTransformId() != -1) || (npc.getNpcTemplate().get_atkspeed() == 0)
				|| (npc.getNpcTemplate().getBossType() > 0)) {			
			return;
		}
		try {
			int mapId = npc.getBaseMapId();
			for (DropEventData data : _dropEvents.get(Integer.valueOf(mapId))) {
				
				int chance = data._chance;
				int randomChance = _random.nextInt(1000000);
				
				if (chance == 0 || chance < randomChance) {
					continue;
				}
				
				int itemCount = data._min;				
				int addCount = data._max - data._min + 1;
				if (addCount > 1) {
					itemCount += _random.nextInt(addCount);
				}
						
				if (itemCount <= 0) continue;
				if (itemCount > 2000000000) itemCount = 2000000000;				
				L1ItemInstance item = npc.getInventory().findItemId(data._itemId, data._bless);
				if (item == null) {
					item = ItemTable.getInstance().createItem(data._itemId);
					item.setBless(data._bless);
					item.setCount(itemCount);
					npc.getInventory().storeItem(item);
				} else {
					item.setCount(itemCount);
				}					
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private class DropEventData {
		int _itemId;
		int _bless;
		int _min;
		int _max;
		int _chance;

		private DropEventData() {
		}
	}
}