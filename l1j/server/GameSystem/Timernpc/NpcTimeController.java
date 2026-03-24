package l1j.server.GameSystem.Timernpc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.GameSystem.Timernpc.NewNpcSpawnTable.BossTemp;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.TimeController.TebeController;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1MobGroupSpawn;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.L1SpawnUtil;
import manager.LinAllManager;

public class NpcTimeController implements Runnable {
	private static Logger _log = Logger.getLogger(NpcTimeController.class.getName());

	private static NpcTimeController _instance;

	private Random rnd = new Random(System.nanoTime());
	private int _time = 0;
	private int _timeM = 0;
	private Date day = new Date(System.currentTimeMillis());

	public static NpcTimeController getInstance() {
		if (_instance == null)
			_instance = new NpcTimeController();
		return _instance;
	}

	ArrayList<BossTemp> bosslist = null;

	public NpcTimeController() {
		GeneralThreadPool.getInstance().execute(this);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				BossChack();
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}

	private void BossChack() {
	    Calendar cal = Calendar.getInstance();
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    int minute = cal.get(Calendar.MINUTE);
	    int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 (Sunday) to 7 (Saturday)

	    bosslist = NewNpcSpawnTable.getInstance().getlist();

	    for (BossTemp temp : bosslist) {
	        boolean isDay = false;
	        for (int i : temp.Day) {
	            if (i == currentDayOfWeek) {
	                isDay = true;
	                break;
	            }
	        }
	        if (!isDay) {
	            continue;
	        }

	        if (temp.isSpawn) {
	            boolean ck = false;
	            for (int Minute_temp : temp.SpawnMinute) {
	                if (minute == Minute_temp) {
	                    ck = true;
	                }
	            }
	            if (ck) continue;
	            else temp.isSpawn = false;
	        }

	        for (int i = 0; i < temp.SpawnHour.length; i++) {
	            if (hour == temp.SpawnHour[i] && minute == temp.SpawnMinute[i]) {
	                temp.isSpawn = true;
	                GeneralThreadPool.getInstance().execute(new BossThread(temp));
	            }
	        }
	    }
	}
	
	class BossThread implements Runnable {
		BossTemp temp;
		public BossThread(BossTemp _temp){
			temp = _temp;
		}
		
		public void run(){
			try{
				int rndtime = rnd.nextInt(temp.rndTime) + 1;
				if(rndtime > 0) {
					Thread.sleep(rndtime * 60 * 1000);
				}
				if (temp.npcid == 9000014) {
					TebeController.getInstance().isgameStart = true;
					TebeController.getInstance().테베시간 = temp.DeleteTime;
				}
				StoreBoss(temp.npcid, temp.SpawnLoc, temp.rndLoc, temp.Groupid, temp.isYn, temp.isMent, temp.Ment,temp.DeleteTime);
			}catch(Exception e){}
		}
	}

	public void StoreBoss(int npcid, int[] Loc, int rndXY, int groupid, boolean isYN, boolean isMent, String ment, int deleteTime) {
		try {
			L1Npc template = NpcTable.getInstance().getTemplate(npcid);
			if (template == null) {
				_log.warning("Npc data for id:" + npcid + " missing in npc table");
				System.out.println("타임엔피씨스폰 컨트롤러 보스 npcid " + npcid + "가 존재하지 않습니다.");
				return;
			}
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcid);
			
			npc.setId(ObjectIdFactory.getInstance().nextId());

			L1Location loc = new L1Location(Loc[0], Loc[1], Loc[2]);
			if (rndXY != 0) {
				loc.randomLocation(rndXY, false);
			}

			npc.setLocation(loc);
			npc.getLocation().forward(5);
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			if (groupid > 0)
				L1MobGroupSpawn.getInstance().doSpawn(npc, groupid, true, false);

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);
			
			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);

			if (isMent) {
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(ment));
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, ment));
			}

			if (0 < deleteTime) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, deleteTime * 1000);
				timer.begin();
			} else {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, 3600 * 1000);
				timer.begin();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}