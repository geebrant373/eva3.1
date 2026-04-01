package l1j.server.EventSystem;

import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ScheduledFuture;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillSound;

public class EventSystemTimeController extends Thread {
	/** fme is field map event flag.... **/
	public static HashMap<Integer, ScheduledFuture<?>>	_scs = new HashMap<Integer, ScheduledFuture<?>>(8);
	
	public static int FME_NONE = 0;
	
	public static int FS_NONE = 0;
	public static int FS_START = 1;
	public static int FS_END = 2;
	
	public static Object _lock = new Object();
	
	private static EventSystemTimeController _instance;

	private boolean _FieldTimeStart;

	public boolean getFieldTimeStart() {
		return _FieldTimeStart;
	}

	public void setFieldTimeStart(boolean FieldTime) {
		_FieldTimeStart = FieldTime;
		fmeFlag = FME_NONE;
	}

	public int fmeFlag;

	public static EventSystemTimeController getInstance() {
		if (_instance == null) {
			_instance = new EventSystemTimeController();
		}
		return _instance;
	}
	
	public void Start(){
		GeneralThreadPool.getInstance().execute(this);
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(1000);
				isEvent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void isEvent() {
		int event_size = EventSystemLoader.getInstance().getEventSystemSize();
		EventSystemInfo EventInfo;
		for (int i = 0; i < event_size; i++) {
			EventInfo = EventSystemLoader.getInstance().getEventSystemInfo(i + 1);
			if (EventInfo == null)
				continue;
			
			if (!EventInfo.is_event()) {
				if (EventInfo.is_mapout()) 
					MapOut(EventInfo.get_event_map_id());
			}
			
			if (isTime(EventInfo) && !EventInfo.is_event())
				GeneralThreadPool.getInstance().execute(new EventSystemSpawner(EventInfo, FS_START));
		}
	}

	public void setFlag(EventSystemInfo EventInfo) {
		synchronized (_lock) {
			fmeFlag |= EventInfo.get_id();
			EventInfo.set_event(true);
		}
	}

	public void delFlag(EventSystemInfo EventInfo) {
		synchronized (_lock) {
			if ((fmeFlag & EventInfo.get_id()) > 0)
				fmeFlag -= EventInfo.get_id();
			EventInfo.set_event(false);
		}
	}
	
	public void initScs(EventSystemInfo EventInfo){
		synchronized(_lock){
			ScheduledFuture<?> sf = _scs.get(EventInfo.get_id());
			if(sf != null)
				sf.cancel(true);
		}
	}
	
	public ScheduledFuture<?> getScs(int flg){
		return _scs.get(flg);
	}
	
	public void initScs(int flg){
		synchronized(_lock){
			ScheduledFuture<?> sf = _scs.get(flg);
			if(sf != null)
				sf.cancel(true);
		}
	}
	
	public int getFlag() {
		return fmeFlag;
	}

	/** 필드 열리는 시간 hour:시간 minute:분 24시간개념 **/
	private boolean isTime(EventSystemInfo EventInfo) {
		Calendar calender = Calendar.getInstance();
		int hour, minute, second;
		hour = calender.get(Calendar.HOUR_OF_DAY);
		minute = calender.get(Calendar.MINUTE);
		second = calender.get(Calendar.SECOND);
		if (EventInfo.get_event_time() == null)
			return false;
		if (EventInfo.get_event_time().length() > 0) {
			StringTokenizer stt = new StringTokenizer(EventInfo.get_event_time(), ",");
			while (stt.hasMoreTokens()) {
				String event_time = stt.nextToken();
				String event_h = event_time.substring(0, event_time.indexOf(":"));
				String event_m = event_time.substring(event_h.length() + 1, event_time.length());
				if ((hour == Integer.valueOf(event_h) 
						&& minute == Integer.valueOf(event_m) 
								&& second == 00)) {
					return true;
				}
			}
		}
		return false;
	}

	public void MapOut(int mapid) {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			if (c.getMapId() == mapid) {
				int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_GIRAN);
				c.sendPackets(new S_SkillSound(c.getId(), 18339));
				Broadcaster.broadcastPacket(c, new S_SkillSound(c.getId(), 18339));
				L1Teleport.teleport(c, loc[0], loc[1], (short) loc[2], c.getMoveState().getHeading(), true);
			}
		}
	}
}