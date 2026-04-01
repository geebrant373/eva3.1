package l1j.server.GameSystem.Boss;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.TimeController.TebeController;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.SpawnBossTable;
import l1j.server.server.datatables.SpawnBossTable.BossTemp;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1MobGroupSpawn;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import manager.LinAllManager;

public class BossSpawnTimeController implements Runnable {
	private static Logger _log = Logger.getLogger(BossSpawnTimeController.class.getName());

	private static BossSpawnTimeController _instance;

	private Random rnd = new Random(System.nanoTime());
	private int _time = 0;
	private int _timeM = 0;
	private Date day = new Date(System.currentTimeMillis());

	public static BossSpawnTimeController getInstance() {
		if (_instance == null)
			_instance = new BossSpawnTimeController();
		return _instance;
	}

	ArrayList<BossTemp> bosslist = null;

	private boolean isNow = false;

	public BossSpawnTimeController() {
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

		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int minute = Calendar.getInstance().get(Calendar.MINUTE);

		bosslist = SpawnBossTable.getInstance().getlist();
		for (BossTemp temp : bosslist) {

			if (temp.isSpawn) {
				boolean ck = false;
				for (int Minute_temp : temp.SpawnMinute) {
					if (minute == Minute_temp) {
						ck = true;
					}
				}
				if (ck)
					continue;
				else
					temp.isSpawn = false;
			}

			for (int i = 0; i < temp.SpawnHour.length; i++) {
				if (hour == temp.SpawnHour[i] && minute == temp.SpawnMinute[i]) {
					temp.isSpawn = true;
					GeneralThreadPool.getInstance().execute(new BossThread(temp));
				}
			}
		}
	}

	/** 보스 스폰 처리 */
	class BossThread implements Runnable {
		BossTemp temp;

		public BossThread(BossTemp _temp) {
			temp = _temp;
		}

		public void run() {
			try {
				int npcId = temp.npcid;
				int rndtime = rnd.nextInt(temp.rndTime) + 1;
				if (rndtime > 0) {
					Thread.sleep(rndtime * 1000);
				}
				if (npcId == 45957) {
					for (int i=450;i<=496;i++) {
						for (L1Object obj : L1World.getInstance().getVisibleObjects(i).values()) { 
							if (obj instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) obj;
								L1Teleport.teleport(pc, 33435, 32800, (short) 4, pc.getMoveState().getHeading(), true);
								pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "마을로 강제 이동 됩니다."));
								pc.sendPackets(new S_SystemMessage("마을로 강제 이동 됩니다."));
							}
						}
					}
					for (int i=530;i<=533;i++) {
						for (L1Object obj : L1World.getInstance().getVisibleObjects(i).values()) { 
							if (obj instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) obj;
								L1Teleport.teleport(pc, 33435, 32800, (short) 4, pc.getMoveState().getHeading(), true);
								pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "마을로 강제 이동 됩니다."));
								pc.sendPackets(new S_SystemMessage("마을로 강제 이동 됩니다."));
							}
						}
					}
				}
				if (temp.npcid == 9000014) {
					TebeController.getInstance().isgameStart = true;
					TebeController.getInstance().테베시간 = temp.DeleteTime;
				}
				StoreBoss(temp.npcid, temp.SpawnLoc, temp.rndTime, temp.rndLoc, temp.Groupid, temp.isMent, temp.Ment,
						temp.DeleteTime);
				LinAllManager.getInstance().BossAppend(NpcTable.getInstance().findNpcIdByName(temp.npcid));
			} catch (Exception e) {

			}

		}
	}

	public void StoreBoss(int npcid, int[] Loc, int rndTime, int rndXY, int groupid, boolean isMent, String ment,
			int deleteTime) {
		try {
			L1Npc template = NpcTable.getInstance().getTemplate(npcid);
			if (template == null) {
				_log.warning("Boss mob data for id:" + npcid + " missing in npc table");
				System.out.println("보스스폰 컨트롤러 보스 npcid " + npcid + "가 존재하지 않습니다.");
				return;
			}
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcid);

			npc.setId(ObjectIdFactory.getInstance().nextId());

			L1Location loc = new L1Location(Loc[0], Loc[1], Loc[2]);

			/*
			 * if (rndXY != 0) { loc.randomLocation(rndXY, false); }
			 */

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
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\f2" + ment));
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3" + ment));
			}

			if (0 < deleteTime) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, deleteTime * 1000);
				timer.begin();
			} else {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, 3600 * 1000);
				timer.begin();
			}

			int guardRange = 10;        
			int guardIntervalMs = 5000;
			GeneralThreadPool.getInstance().execute(new BossRangeGuard(npc, loc.getX(), loc.getY(), (short) loc.getMapId(), guardRange, guardIntervalMs));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 보스가 스폰 위치에서 지정 반경 밖으로 나갔을 때 강제 복귀시키는 검사기 */
	class BossRangeGuard implements Runnable {
	    private final L1NpcInstance npc;
	    private final int spawnX;
	    private final int spawnY;
	    private final short mapId;
	    private final int maxRange;
	    private final int intervalMs;

	    public BossRangeGuard(L1NpcInstance npc, int spawnX, int spawnY, short mapId, int maxRange, int intervalMs) {
	        this.npc = npc;
	        this.spawnX = spawnX;
	        this.spawnY = spawnY;
	        this.mapId = mapId;
	        this.maxRange = maxRange;
	        this.intervalMs = intervalMs;
	    }

	    @Override
	    public void run() {
	        try {
	            while (true) {
	                if (npc == null) break;
	                if (npc.isDead()) break;
	                if (L1World.getInstance().findObject(npc.getId()) == null) break;

	                int dx = npc.getX() - spawnX;
	                int dy = npc.getY() - spawnY;
	                double dist = Math.sqrt(dx * dx + dy * dy);

	                if (dist > maxRange) {
	                    npc.setX(spawnX);
	                    npc.setY(spawnY);
	                    npc.setMap((short) mapId);

	                    npc.setHomeX(spawnX);
	                    npc.setHomeY(spawnY);

	                    npc.setHeading(0);
	                    npc.nearTeleport(spawnX, spawnY);
	                }

	                Thread.sleep(intervalMs);
	            }
	        } catch (InterruptedException ie) {
	        } catch (Exception e) {
	            _log.log(Level.WARNING, "BossRangeGuard exception", e);
	        }
	    }
	}

}