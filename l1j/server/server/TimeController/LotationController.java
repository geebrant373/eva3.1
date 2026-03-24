package l1j.server.server.TimeController;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1NpcDeleteTimer1;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.EtcUtils;
import l1j.server.server.utils.EtcUtils.MESSAGE_TYPE;
import l1j.server.server.utils.L1SpawnUtil;
import l1j.server.server.utils.LotationStatics;
import l1j.server.server.utils.LotationStatics.MapEventInfo;

public class LotationController implements Runnable {
	private static Logger _log = Logger.getLogger(LotationController.class.getName());

	private static LotationController _instance;

	public static LotationController getInstance() {
		if (_instance == null)
			_instance = new LotationController();
		return _instance;
	}

	private Date day = new Date(System.currentTimeMillis());

	public LotationController() {
		GeneralThreadPool.getInstance().execute(this);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				MapEventUpdate();
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}

	// -- MAP EVENT UPDATE --
	private ArrayList<MapEventInfo> MapEventInfos;

	private void MapEventUpdate() {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int minute = Calendar.getInstance().get(Calendar.MINUTE);

		MapEventInfos = LotationStatics.getInstance().GetMapInfoList();

		for (MapEventInfo temp : MapEventInfos) {
			boolean isDay = false;
			for (int i : temp.Day)
				if (i == day.getDay()) {
					isDay = true;
					break;
				}
			if (!isDay)
				continue;
			if (temp.isDO) {
				boolean ck = false;
				for (int Minute_temp : temp.SpawnMinute) {
					if (minute == Minute_temp) {
						ck = true;
						break;
					}
				}
				if (ck)
					continue;
				else
					temp.isDO = false;
			}

			for (int i = 0; i < temp.SpawnHour.length; i++) {
				if (hour == temp.SpawnHour[i] && minute == temp.SpawnMinute[i]) {
					temp.isDO = true;
					GeneralThreadPool.getInstance().execute(new MapThread(temp));
				}
			}
		}
	}

	int CloseBeforeMinute = 1; // 닫히기 몇분전에 공지합니다.
	int TeleporterDisapearBeforeMinute = 1; // 텔레포터가 몇분전에 미리 사라집니다.
	// 맵이벤트 진행

	class MapThread implements Runnable {
		MapEventInfo temp;

		public MapThread(MapEventInfo _temp) {
			temp = _temp;
		}

		public void run() {
			
			try {
				
				
				EtcUtils.SetBroadcastMessage("[" + temp.Notice + "]  개방되었습니다.", MESSAGE_TYPE.BOTH);
				// OPEN MAP
			//	
				CharacterTable.clear로테이션시작();
				로테이션잊섬_타임시작();
				// 텔레포터 NPC 생성 ( 오픈시간동안 생성 )
				L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(temp.EntryNpc);

				L1Location loc = new L1Location(temp.EntryNpcLoc[0], temp.EntryNpcLoc[1], temp.EntryNpcLoc[2]);
				npc.setLocation(loc);
				npc.getLocation().forward(4);
				npc.setHomeX(npc.getX());
				npc.setHomeY(npc.getY());

				L1SpawnUtil.spawn로테(npc, temp.EntryNpc, 0,
						(temp.DuringTime - (TeleporterDisapearBeforeMinute * 60)) * 1000);
				
				// L1World.getInstance().storeObject(npc);
				// L1World.getInstance().addVisibleObject(npc);

				 L1NpcDeleteTimer1 timer = new L1NpcDeleteTimer1(npc, ( temp.DuringTime - (		 TeleporterDisapearBeforeMinute * 60 ) ) * 1000 );
				 timer.begin1();
				
				Thread.sleep((temp.DuringTime - (CloseBeforeMinute * 60)) * 1000);
				EtcUtils.SetBroadcastMessage("[" + temp.Notice + "] 종료시간이 " + CloseBeforeMinute + "분 남았습니다.",
						MESSAGE_TYPE.BOTH);

				Thread.sleep(CloseBeforeMinute * 1000 * 60);
				EtcUtils.SetBroadcastMessage("[" + temp.Notice + "]  종료되었습니다.", MESSAGE_TYPE.BOTH);
				//RejectMaps(temp.Maps);
				CharacterTable.clear로테이션();
				// AFTER 5 MINUTES MORE REJECT PLAYERS FROM MAPS.
				//Thread.sleep(5 * 1000 * 60);
				//RejectMaps(temp.Maps);
				로테이션잊섬_타임종료();
				
				로테이션잊섬종료();
				로테이션테베종료();
				close(); // 추가(꼭여기에하삼)
				

				/** 종료 **/
				End();

			} catch (Exception e) {
			}
		}
	}
	
	private void RejectMaps(int[] Maps) {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			int myMapId = c.getMap().getId();
			for (int i = 0; i < Maps.length; i++) {
				if (myMapId == Maps[i])
					RejectMap(c);
			}
		}
	}

	private void RejectMap(L1PcInstance c) {
		c.stopHpRegenerationByDoll();
		c.stopMpRegenerationByDoll();
		L1Teleport.teleport(c, 33431, 32794, (short) 4, 4, true);
		c.sendPackets(new S_SystemMessage("현 시간부로 개방이 종료되었습니다."));
	}
	/** 아덴마을로 팅기게 **/
	private void 로테이션잊섬종료() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			switch (c.getMap().getId()) {
			case 70: // 
				c.stopHpRegenerationByDoll();
				c.stopMpRegenerationByDoll();
				L1Teleport.teleport(c, 33442, 32818, (short)4, c.getMoveState().getHeading(), true);	
				c.sendPackets(new S_SystemMessage("잊혀진 섬이 종료 되었습니다."));
				break;
			default:
				break;
			}
		}
	}
	
	public static void 로테이션잊섬_타임시작() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			/**if (!c.로테_시작) {
				c.로테_시작 = true;
				
			*/	
			c.set로테시작(1);
		    CharacterTable.update로테이션(c);
				System.out.println(c.get로테시작());
				
			//}
		}
	}
	public static void 로테이션잊섬_타임종료() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			
				//c.로테_시작 = false;
		    	c.set로테시작(0);
			    CharacterTable.update로테이션(c);
				
		}
	}
	/** 아덴마을로 팅기게 **/
	private void 로테이션테베종료() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			switch (c.getMap().getId()) {
			case 780: // mapid
			case 781:
			case 782:
				c.stopHpRegenerationByDoll();
				c.stopMpRegenerationByDoll();
				L1Teleport.teleport(c, 33442, 32818, (short)4, c.getMoveState().getHeading(), true);	
				c.sendPackets(new S_SystemMessage("테베라스가 종료 되었습니다."));
				break;
			default:
				break;
			}
		}
	}

	/** 캐릭터가 죽었다면 종료시키기 **/
	private void close() {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc.getMap().getId() == 72 && pc.isDead()) {
				pc.stopHpRegenerationByDoll();
				pc.stopMpRegenerationByDoll();
				pc.sendPackets(new S_Disconnect());
			}
		}
	}

	/** 종료 **/
	private void End() {
		로테이션잊섬종료();
		close(); //추가
		L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, " 로테이션이 닫혔습니다."));
		L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, " 로테이션이 닫혔습니다."));
		L1World.getInstance().broadcastServerMessage("\\fW로테이션이 닫혔습니다.");
	
	}
}
