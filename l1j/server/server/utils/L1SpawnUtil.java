/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.GameSystem.Antaras.AntarasRaid;
import l1j.server.GameSystem.Antaras.AntarasRaidSystem;
import l1j.server.GameSystem.biscuitGame.BiscuitLastabardController;
import l1j.server.server.ActionCodes;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCPack;

public class L1SpawnUtil {
	private static Logger _log = Logger.getLogger(L1SpawnUtil.class.getName());

	public static void spawn(L1PcInstance pc, int npcId, int randomRange, int timeMillisToDelete, boolean isUsePainwand) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(pc.getMapId());
			if (randomRange == 0) {
				npc.getLocation().set(pc.getLocation());
				npc.getLocation().forward(pc.getMoveState().getHeading());
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(pc.getX() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					npc.setY(pc.getY() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					npc.getLocation().set(pc.getLocation());
					npc.getLocation().forward(pc.getMoveState().getHeading());
				}
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(pc.getMoveState().getHeading());
			if (isUsePainwand) {
				if (npc instanceof L1MonsterInstance) {
					L1MonsterInstance mon = (L1MonsterInstance) npc;
					mon.set_storeDroped(2);
				}
			}

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
			if (0 < timeMillisToDelete) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, timeMillisToDelete);
				timer.begin();
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 엔피씨를 스폰한다
	 * 
	 * @param x
	 * @param y
	 * @param map
	 * @param npcId
	 * @param randomRange
	 * @param timeMillisToDelete
	 * @param movemap
	 *            (이동시킬 맵을 설정한다 - 안타레이드)
	 */

	public static L1NpcInstance spawnnpc(int x, int y, short map, int npcId, int randomRange, int timeMillisToDelete, int movemap) {
		int heading = 5;
		if (npcId == 7000044 || npcId == 100325 || npcId == 100326 || (npcId == 100213 && x == 33094 && y == 33401) || npcId == 100563
				|| npcId == 100646 || npcId == 100692)
			heading = 6;
		else if (npcId == 100430 || npcId == 100709 || npcId == 100710)
			heading = 4;
		else {
			heading = 0;
		}
		return spawn4(x, y, map, heading, npcId, randomRange, timeMillisToDelete, movemap, false);
	}

	public static L1NpcInstance spawn4(int x, int y, short map, int heading, int npcId, int randomRange, int timeMillisToDelete, int movemap,
			boolean level) {
		L1NpcInstance npc = null;
		try {
			if (level) {
				npc = NpcTable.getInstance().newNpcInstance(npcId + 1000000);
			} else {
				npc = NpcTable.getInstance().newNpcInstance(npcId);
			}

			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(map);
			if (randomRange == 0) {
				npc.getLocation().set(x, y, map);
				/**
				 * 용땅도포함~~~ 하딘 관련 NPC가 아닐 경우에만 적용 일단 주석 처리 해봄.
				 **/
				/*
				 * if(npcId != 4212013 && !(npcId >= 5000038 && npcId <=
				 * 5000093)) npc.getLocation().forward(5);
				 */
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(x + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					npc.setY(y + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);
				if (tryCount >= 50) {
					npc.getLocation().set(x, y, map);
					// npc.getLocation().forward(5);
				}
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(heading);
			if (npcId == 4500101 || npcId == 4500102 || npcId == 4500103 || npcId == 4212015 || npcId == 4212016 || npcId == 4500107
					|| npcId == 100011 || npcId == 910008 || npcId == 4038060) {
				L1FieldObjectInstance fobj = (L1FieldObjectInstance) npc;
				fobj.setMoveMapId(movemap);
			}
			if (npc.getNpcId() == 5000091) {
				L1DoorInstance door = (L1DoorInstance) npc;
				door.setDoorId(npc.getNpcTemplate().get_npcId());
				door.setDirection(0);
				door.setLeftEdgeLocation(door.getX());
				door.setRightEdgeLocation(door.getX());

				door.setOpenStatus(ActionCodes.ACTION_Close);
				door.isPassibleDoor(false);
				door.setPassable(1);
				// door.setOpenStatus(ActionCodes.ACTION_Close);
				// door.isPassibleDoor(false);
				// door.setPassable(L1DoorInstance.PASS);
			}

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);
			// 안타라스의 경우 튀어 나오게끔..
			if (npcId == 4212015 || npcId == 4212016 || npcId == 4038000 || npcId == 4200010 || npcId == 4200011 || npcId == 4039000
					|| npcId == 4039006 || npcId == 4039007 || npcId == 100011) { // 안타
																					// 파푸
				// npc.broadcastPacket(new S_DoActionGFX(npc.getId(), 11));
				// npc.setActionStatus(11);
				npc.broadcastPacket(new S_NPCPack(npc));
				npc.broadcastPacket(new S_DoActionGFX(npc.getId(), 11));
				// npc.setActionStatus(3);
				// npc.broadcastPacket(new S_NPCPack(npc));
				// npc.broadcastPacket(new S_NPCPack(npc));
			} else if (npcId == 100586 || npcId == 100587) {
				npc.broadcastPacket(new S_NPCPack(npc));
				npc.broadcastPacket(new S_DoActionGFX(npc.getId(), 4));
			}
			if (npc.getNpcId() == 4038000 || npc.getNpcId() == 4200010 || npc.getNpcId() == 4200011) {
				AntarasRaid ar = AntarasRaidSystem.getInstance().getAR(map);
				ar.setanta(npc);
			}

			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
			if (0 < timeMillisToDelete) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, timeMillisToDelete);
				timer.begin();
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		return npc;
	}

	public static void spawn2(int x, int y, short map, int npcId, int randomRange, int timeMillisToDelete, int movemap) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(map);
			if (randomRange == 0) {
				npc.getLocation().set(x, y, map);
				npc.getLocation().forward(5);
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(x + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					npc.setY(y + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					npc.getLocation().set(x, y, map);
					npc.getLocation().forward(5);
				}
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(5);

			if (npcId == 4212015) {
				L1FieldObjectInstance fobj = (L1FieldObjectInstance) npc;
				fobj.setMoveMapId(movemap);
			}

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
			if (0 < timeMillisToDelete) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, timeMillisToDelete);
				timer.begin();
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	public static void spawn(L1NpcInstance pc, int npcId, int randomRange, int timeMillisToDelete) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(pc.getMapId());
			if (randomRange == 0) {
				npc.getLocation().set(pc.getLocation());
				npc.getLocation().forward(pc.getMoveState().getHeading());
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(pc.getX() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					npc.setY(pc.getY() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					npc.getLocation().set(pc.getLocation());
					npc.getLocation().forward(pc.getMoveState().getHeading());
				}
			}
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(pc.getMoveState().getHeading());

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시

			if (0 < timeMillisToDelete) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, timeMillisToDelete);
				timer.begin();
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	public static void spawn로테(L1NpcInstance pc, int npcId, int randomRange, int timeMillisToDelete) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(pc.getMapId());
			if (randomRange == 0) {
				npc.getLocation().set(pc.getLocation());
				npc.getLocation().forward(pc.getMoveState().getHeading());
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(pc.getX() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					npc.setY(pc.getY() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					npc.getLocation().set(pc.getLocation());
					npc.getLocation().forward(pc.getMoveState().getHeading());
				}
			}
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(pc.getMoveState().getHeading());
			npc.로테_시작1 = true;
			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시

			if (0 < timeMillisToDelete) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, timeMillisToDelete);
				timer.begin();
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	public static L1NpcInstance Gmspawn(int npcId, int x, int y, short mapid, int heading, int timeMinToDelete) {
		L1NpcInstance npc = null;
		try {
			npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(mapid);
			npc.setX(x);
			npc.setY(y);
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(heading);

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			if (0 < timeMinToDelete) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, timeMinToDelete);
				timer.begin();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return npc;
	}

	/** 라스타바드 레이드 전용 스폰 */
	public static void spawnLastabard(int x, int y, short MapId, int Heading, int npcId, int randomRangeX, int randomRangeY, int stage) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(MapId);
			if (randomRangeX == 0 && randomRangeY == 0) {
				npc.getLocation().set(x, y, MapId);
				npc.getLocation().forward(Heading);
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(x + (int) (Math.random() * randomRangeX) - (int) (Math.random() * randomRangeX));
					npc.setY(y + (int) (Math.random() * randomRangeY) - (int) (Math.random() * randomRangeY));
					if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);
				if (tryCount >= 50) {
					npc.getLocation().forward(Heading);
				}
			}
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(Heading);

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			BiscuitLastabardController biscuitLastabard = BiscuitLastabardController.getInstance();

			if (stage == 530) {
				biscuitLastabard.add(npc, 530);
			} else if (stage == 532) {
				biscuitLastabard.add(npc, 532);
			} else if (stage == 5311) {
				biscuitLastabard.add(npc, 5311);
			} else if (stage == 5312) {
				biscuitLastabard.add(npc, 5312);
			} else if (stage == 5313) {
				biscuitLastabard.add(npc, 5313);
			} else if (stage == 5331) {
				biscuitLastabard.add(npc, 5331);
			} else if (stage == 5332) {
				biscuitLastabard.add(npc, 5332);
			} else if (stage == 5333) {
				biscuitLastabard.add(npc, 5333);
			}

			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시

		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1SpawnUtil[]Error7", e);
		}
	}

}
