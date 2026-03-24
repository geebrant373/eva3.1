package l1j.server.EventSystem;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.L1SpawnUtil;

public class EventSystemSpawner implements Runnable {
	private EventSystemInfo _EventInfo;
	private int _fsFlg;

	public EventSystemSpawner(EventSystemInfo EventInfo, int startState) {
		_EventInfo = EventInfo;
		_fsFlg 	= startState;
	}
	
	public EventSystemSpawner(int i, int startState) {
		_EventInfo = EventSystemLoader.getInstance().getEventSystemInfo(i);
		_fsFlg 	= startState;
	}

	@Override
	public void run() {
		try {
			if ((_fsFlg & EventSystemTimeController.FS_START) > 0) {
				EventSystemTimeController.getInstance().setFlag(_EventInfo);
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, String.format("지금부터 %s 이벤트가 시작됩니다.", _EventInfo.get_event_name())));
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(String.format("지금부터 %s 이벤트가 시작됩니다.", _EventInfo.get_event_name())));
				if (_EventInfo.get_spawn_loc() != null) {
					int npcid = _EventInfo.get_npc_id();
					String[] spawn = _EventInfo.get_spawn_loc().split(",");
					int x = Integer.parseInt(spawn[0]);
					int y = Integer.parseInt(spawn[1]);
					int mapid = Integer.parseInt(spawn[2]);
					int heading = Integer.parseInt(spawn[3]);
					L1SpawnUtil.Gmspawn(npcid, x, y, (short)mapid, heading, _EventInfo.get_event_end_time() * 1000);
				}
			} else if ((_fsFlg & EventSystemTimeController.FS_END) > 0) {
				EventSystemTimeController.getInstance().delFlag(_EventInfo);
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, String.format("%s 이벤트가 종료 되었습니다.", _EventInfo.get_event_name())));
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(String.format("%s 이벤트가 종료 되었습니다.", _EventInfo.get_event_name())));
				if (_EventInfo.is_mapout())
					EventSystemTimeController.getInstance().MapOut(_EventInfo.get_event_map_id());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ((_fsFlg & EventSystemTimeController.FS_START) > 0) {
				_fsFlg = EventSystemTimeController.FS_END;
				EventSystemTimeController.getInstance().initScs(_EventInfo);
				EventSystemTimeController._scs.put(_EventInfo.get_id(), GeneralThreadPool.getInstance().schedule(this,
				_EventInfo.get_event_end_time() * 1000));
			} else if ((_fsFlg & EventSystemTimeController.FS_END) > 0) {
				_fsFlg = EventSystemTimeController.FS_NONE;
				synchronized (EventSystemTimeController._lock) {
					EventSystemTimeController._scs.put(_EventInfo.get_id(), null);
				}
			}
		}
	}
}