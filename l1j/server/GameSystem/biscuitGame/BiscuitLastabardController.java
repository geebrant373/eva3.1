package l1j.server.GameSystem.biscuitGame;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.Lastabard.LastabardData;
import l1j.server.GameSystem.Lastabard.LastabardSpawn;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.L1SpawnUtil;
import l1j.server.server.utils.NumberUtil;
import l1j.server.server.utils.SQLUtil;

public class BiscuitLastabardController implements Runnable {
	private static Logger _log = Logger.getLogger(BiscuitLastabardController.class.getName());
	private Map<Integer, LastabardSpawn> spawnTable = new HashMap<Integer, LastabardSpawn>();
	private static BiscuitLastabardController _instance;

	public static BiscuitLastabardController getInstance() {
		if (_instance == null) {
			_instance = new BiscuitLastabardController();
		}
		return _instance;
	}
	
	// 비스킷, 텔레포트
	private boolean door5300 = false;
	private boolean door5311 = false;
	private boolean door5312 = false;
	private boolean door5313 = false;
	private boolean door5320 = false;
	private boolean door5331 = false;
	private boolean door5332 = false;
	private boolean door5333 = false;

	public boolean isDoor5300() {
		return door5300;
	}

	public void setDoor5300(boolean door5300) {
		this.door5300 = door5300;
	}

	public boolean isDoor5311() {
		return door5311;
	}

	public void setDoor5311(boolean door5311) {
		this.door5311 = door5311;
	}

	public boolean isDoor5312() {
		return door5312;
	}

	public void setDoor5312(boolean door5312) {
		this.door5312 = door5312;
	}

	public boolean isDoor5313() {
		return door5313;
	}

	public void setDoor5313(boolean door5313) {
		this.door5313 = door5313;
	}

	public boolean isDoor5320() {
		return door5320;
	}

	public void setDoor5320(boolean door5320) {
		this.door5320 = door5320;
	}

	public boolean isDoor5331() {
		return door5331;
	}

	public void setDoor5331(boolean door5331) {
		this.door5331 = door5331;
	}

	public boolean isDoor5332() {
		return door5332;
	}

	public void setDoor5332(boolean door5332) {
		this.door5332 = door5332;
	}

	public boolean isDoor5333() {
		return door5333;
	}

	public void setDoor5333(boolean door5333) {
		this.door5333 = door5333;
	}

	/* 스테이지 */
	private boolean stage5300 = false;
	private int stage5311 = 0;
	private int stage5312 = 0;
	private int stage5313 = 0;
	private boolean stage5320 = false;
	private int stage5331 = 0;
	private int stage5332 = 0;
	private int stage5333 = 0;
	
	private boolean first530Room = false;
	private boolean first531Room = false;
	private boolean second531Room = false;
	private boolean third531Room = false;
	private boolean first532Room = false;
	private boolean first533Room = false;
	private boolean second533Room = false;
	private boolean third533Room = false;
	
	/* 다음 시간 */
	private int nextClock5300 = -1;
	private int nextClock5311 = -1;
	private int nextClock5312 = -1;
	private int nextClock5313 = -1;
	private int nextClock5320 = -1;
	private int nextClock5331 = -1;
	private int nextClock5332 = -1;
	private int nextClock5333 = -1;
		
	public void run() {
		fillSpawnTable();	
		
		while (true) {
			try {
				
				pcChk(531);
				pcChk(533);
				
				// 530맵 케이나 보스
				if (!stage5300) {
					spawnMonster(530, 1);
					nextClock5300 = -1;
					stage5300 = true;
					first530Room = true;
				} else {
					check(530);
					if (first530Room) {
						first530();
					}
				}
				
				// 531맵 1번방
				if(stage5311 == 1){
					ListClear(5311);
					stage5311 = 2;
					spawnMonster(531, 1);
					first531Room = true;
				} else if (stage5311 == 2) {
					if (first531Room) {
						first531();
					}
				}
				
				// 531맵 2번방
				if(stage5312 == 1){
					ListClear(5312);
					stage5312 = 2;
					spawnMonster(531, 2);
					second531Room = true;
				} else if (stage5312 == 2) {
					check(5312);
					if (second531Room) {
						second531();
					}
				}
				
				// 531맵 3번방
				if(stage5313 == 1){
					ListClear(5313);
					stage5313 = 2;
					spawnMonster(531, 3);
					third531Room = true;
				} else if (stage5313 == 2) {
					check(5313);
					if (third531Room) {
						third531();
					}
				}
				
				
				// 532맵 이데아 보스
				if (!stage5320) {
					spawnMonster(532, 1);
					nextClock5320 = -1;
					stage5320 = true;
					first532Room = true;
				} else {
					check(532);
					if (first532Room) {
						first532();
					}
				}
				
				// 533맵 1번방
				if(stage5331 == 1){
					ListClear(5331);
					stage5331 = 2;
					spawnMonster(533, 1);
					first533Room = true;
				} else if (stage5331 == 2) {
					if (first533Room) {
						first533();
					}
				}
				
				// 533맵 2번방
				if(stage5332 == 1){
					ListClear(5332);
					stage5332 = 2;
					spawnMonster(533, 2);
					second533Room = true;
				} else if (stage5332 == 2) {
					check(5332);
					if (second533Room) {
						second533();
					}
				}
				
				// 531맵 3번방
				if(stage5333 == 1){
					ListClear(5333);
					stage5333 = 2;
					spawnMonster(533, 3);
					third533Room = true;
				} else if (stage5333 == 2) {
					check(5333);
					if (third533Room) {
						third533();
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				_log.log(Level.SEVERE, "BiscuitLastabardController에서 에러발생", e);
			} finally {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}
	}
	
	private void pcChk(int mapId) {
		for ( L1Object obj : L1World.getInstance().getVisibleObjects(mapId).values() ) {
			if (obj instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) obj;
				// 531맵
				if (pc.getMapId() == 531) {
					if (pc.getX() >= 32730 && pc.getX() <= 32742 && pc.getY() >= 32734 && pc.getY() <= 32757) {
						int i = 0;
						for (L1PcInstance pcList : _pcList5311) {
							if (pcList.getName().equals(pc.getName())) {
								i++;
							}
						}
						if (i == 0) {
							addPc(pc, 5311);
						}
					}
					if (pc.getX() >= 32794 && pc.getX() <= 32806 && pc.getY() >= 32731 && pc.getY() <= 32743) {
						int i = 0;
						
						for(int j = 0; j < _pcList5311.size(); j++) {
							L1PcInstance pcList5311 = _pcList5311.get(j);
							if(pcList5311.getName().equals(pc.getName()) || pc == null || !_pcList5311.contains(pc)) {
								_pcList5311.remove(j);
								break;
							}
						}
						
						for (L1PcInstance pcList : _pcList5312) {
							if (pcList.getName().equals(pc.getName())) {
								i++;
							}
						}
						if (i == 0) {
							addPc(pc, 5312);
						}
					}
					if (pc.getX() >= 32765 && pc.getX() <= 32783 && pc.getY() >= 32822 && pc.getY() <= 32836) {
						int i = 0;
						
						for(int j = 0; j < _pcList5312.size(); j++) {
							L1PcInstance pcList5312 = _pcList5312.get(j);
							if(pcList5312.getName().equals(pc.getName()) || pc == null || !_pcList5312.contains(pc)) {
								_pcList5312.remove(j);
								break;
							}
						}
						
						for (L1PcInstance pcList : _pcList5313) {
							if (pcList.getName().equals(pc.getName())) {
								i++;
							}
						}
						if (i == 0) {
							addPc(pc, 5313);
						}
					}
				}

				// 533맵
				if (pc.getMapId() == 533) {
					if (pc.getX() >= 32845 && pc.getX() <= 32873 && pc.getY() >= 32926 && pc.getY() <= 32938) {
						int i = 0;
						for (L1PcInstance pcList : _pcList5331) {
							if (pcList.getName().equals(pc.getName())) {
								i++;
							}
						}
						if (i == 0) {
							addPc(pc, 5331);
						}
					}
					if (pc.getX() >= 32805 && pc.getX() <= 32827 && pc.getY() >= 32866 && pc.getY() <= 32882) {
						int i = 0;
						
						for(int j = 0; j < _pcList5331.size(); j++) {
							L1PcInstance pcList5331 = _pcList5331.get(j);
							if(pcList5331.getName().equals(pc.getName()) || pc == null || !_pcList5331.contains(pc)) {
								_pcList5331.remove(j);
								break;
							}
						}
						
						for (L1PcInstance pcList : _pcList5332) {
							if (pcList.getName().equals(pc.getName())) {
								i++;
							}
						}
						if (i == 0) {
							addPc(pc, 5332);
						}
					}
					if (pc.getX() >= 32744 && pc.getX() <= 32770 && pc.getY() >= 32836 && pc.getY() <= 32854) {
						int i = 0;
						
						for(int j = 0; j <= _pcList5332.size(); j++) {
							L1PcInstance pcList5332 = _pcList5332.get(j);
							if(pcList5332.getName().equals(pc.getName()) || pc == null || !_pcList5332.contains(pc)) {
								_pcList5332.remove(j);
								break;
							}
						}
						
						for (L1PcInstance pcList : _pcList5333) {
							if (pcList.getName().equals(pc.getName())) {
								i++;
							}
						}
						if (i == 0) {
							addPc(pc, 5333);
						}
					}
				}
			}
		}
	}
	
	private void openDoor(int mapId, int doorId) {
		L1DoorInstance door = null;
		for (L1Object object : L1World.getInstance().getVisibleObjects(mapId).values()) {
			if (object instanceof L1DoorInstance) {
				door = (L1DoorInstance) object;
				if (door.getDoorId() == doorId) {
					if (door.getOpenStatus() == ActionCodes.ACTION_Close) {
						door.open();
					}
				}
			}
		}
	}

	private void closeDoor(int mapRoomId) {
		
		int mapId = Integer.parseInt(String.valueOf(mapRoomId).substring(0, 3));
		
		L1DoorInstance door = null;
		for (L1Object object : L1World.getInstance().getVisibleObjects(mapId).values()) {
			if (object instanceof L1DoorInstance) {
				door = (L1DoorInstance) object;
				if (mapRoomId == 530 && door.getDoorId() == 4047) {
					if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
						door.close();
					}
				} else if (mapRoomId == 532 && door.getDoorId() == 4051) {
					if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
						door.close();
					}
				} else if (mapRoomId == 5311 && door.getDoorId() == 4048) {
					if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
						door.close();
					}
				} else if (mapRoomId == 5312 && door.getDoorId() == 4049) {
					if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
						door.close();
					}
				} else if (mapRoomId == 5313 && door.getDoorId() == 4050) {
					if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
						door.close();
					}
				} else if (mapRoomId == 5331 && door.getDoorId() == 4052) {
					if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
						door.close();
					}
				} else if (mapRoomId == 5332 && door.getDoorId() == 4053) {
					if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
						door.close();
					}
				} else if (mapRoomId == 5333 && door.getDoorId() == 4054) {
					if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
						door.close();
					}
				}
				
			}
		}
	}

	/** 좌표 맵 방향 모르겟고 갯수 방현재방지역 */
	private void spawnMonster(int mapId, int stage) {
		if (mapId == 530) {
		//	L1SpawnUtil.spawnLastabard(32862, 32840, (short) 530, 0, 45955, 0, 0, 530); // 대법관 케이나 0521 릴차단
		} else if (mapId == 532) {
			L1SpawnUtil.spawnLastabard(32789, 32812, (short) 532, 0, 45959, 0, 0, 532); // 대법관 이데아
		} else if (mapId == 531) {
			if (stage == 1) {
				L1SpawnUtil.spawnLastabard(32757, 32744, (short) 531, 0, 45956, 0, 0, 5311); // 대법관 비아타스
				for (int i = 0; i < 4; i++) {
					L1SpawnUtil.spawnLastabard(32757, 32744, (short) 531, 0, 46002, 16, 6, 5311); // 장로 수행원 
				}
				for (int i = 0; i < 4; i++) {
					L1SpawnUtil.spawnLastabard(32757, 32744, (short) 531, 0, 46012, 16, 6, 5311); // 브랏디나이트 
				}
				for (int i = 0; i < 4; i++) {
					L1SpawnUtil.spawnLastabard(32757, 32744, (short) 531, 0, 46005, 16, 6, 5311); // 라스타바드 근위병 
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32757, 32744, (short) 531, 0, 45990, 16, 6, 5311); // 라스타바드 근위병 
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32757, 32744, (short) 531, 0, 46001, 16, 6, 5311); // 브랏드아사신 
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32757, 32744, (short) 531, 0, 46006, 16, 6, 5311); // 팬텀 나이트 
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32757, 32744, (short) 531, 0, 45993, 16, 6, 5311); // 다크 위저드 
				}
			} else if (stage == 2) {
				L1SpawnUtil.spawnLastabard(32791, 32786, (short) 531, 0, 45957, 0, 0, 5312); // 대법관 바로메스
				for (int i = 0; i < 4; i++) {
					L1SpawnUtil.spawnLastabard(32791, 32786, (short) 531, 0, 46002, 20, 6, 5312); // 장로 수행원
				}
				for (int i = 0; i < 4; i++) {
					L1SpawnUtil.spawnLastabard(32791, 32786, (short) 531, 0, 46012, 20, 6, 5312); // 브랏디나이트
				}
				for (int i = 0; i < 4; i++) {
					L1SpawnUtil.spawnLastabard(32791, 32786, (short) 531, 0, 46005, 20, 6, 5312); // 라스타바드 근위병
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32791, 32786, (short) 531, 0, 45990, 20, 6, 5312); // 라스타바드 근위병
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32791, 32786, (short) 531, 0, 46001, 20, 6, 5312); // 브랏드아사신
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32791, 32786, (short) 531, 0, 46006, 20, 6, 5312); // 팬텀 나이트
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32791, 32786, (short) 531, 0, 45993, 20, 6, 5312); // 다크 위저드
				}
			} else if (stage == 3) {
				L1SpawnUtil.spawnLastabard(32845, 32857, (short) 531, 0, 45958, 0, 0, 5313); // 대법관 엔디아스
				for (int i = 0; i < 4; i++) {
					L1SpawnUtil.spawnLastabard(32829, 32857, (short) 531, 0, 46002, 14, 13, 5313); // 장로 수행원
				}
				for (int i = 0; i < 4; i++) {
					L1SpawnUtil.spawnLastabard(32829, 32857, (short) 531, 0, 46012, 14, 13, 5313); // 브랏디나이트
				}
				for (int i = 0; i < 4; i++) {
					L1SpawnUtil.spawnLastabard(32829, 32857, (short) 531, 0, 46005, 14, 13, 5313); // 라스타바드 근위병
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32829, 32857, (short) 531, 0, 45990, 14, 13, 5313); // 라스타바드 근위병
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32829, 32857, (short) 531, 0, 46001, 14, 13, 5313); // 브랏드아사신
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32829, 32857, (short) 531, 0, 46006, 14, 13, 5313); // 팬텀 나이트
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32829, 32857, (short) 531, 0, 45993, 14, 13, 5313); // 다크 위저드
				}
			}
		} else if (mapId == 533) {
			if (stage == 1) {
				L1SpawnUtil.spawnLastabard(32859, 32897, (short) 533, 0, 45960, 0, 0, 5331); // 대법관 티아메스
				for (int i = 0; i < 5; i++) {
					L1SpawnUtil.spawnLastabard(32851, 32897, (short) 533, 0, 46002, 18, 11, 5331); // 장로 수행원
				}
				for (int i = 0; i < 5; i++) {
					L1SpawnUtil.spawnLastabard(32859, 32897, (short) 533, 0, 46012, 18, 11, 5331); // 브랏디나이트
				}
				for (int i = 0; i < 5; i++) {
					L1SpawnUtil.spawnLastabard(32859, 32897, (short) 533, 0, 46005, 18, 11, 5331); // 라스타바드 근위병
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32859, 32897, (short) 533, 0, 45990, 18, 11, 5331); // 라스타바드 근위병
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32859, 32897, (short) 533, 0, 45996, 18, 11, 5331); // 다크 위저드
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32859, 32897, (short) 533, 0, 46001, 18, 11, 5331); // 브랏드아사신
				}
			} else if (stage == 2) {
				L1SpawnUtil.spawnLastabard(32789, 32891, (short) 533, 0, 45961, 0, 0, 5332); // 대법관 라미아스
				for (int i = 0; i < 5; i++) {
					L1SpawnUtil.spawnLastabard(32777, 32891, (short) 533, 0, 46002, 12, 10, 5332); // 장로 수행원
				}
				for (int i = 0; i < 5; i++) {
					L1SpawnUtil.spawnLastabard(32777, 32891, (short) 533, 0, 46012, 12, 10, 5332); // 브랏디나이트
				}
				for (int i = 0; i < 5; i++) {
					L1SpawnUtil.spawnLastabard(32777, 32891, (short) 533, 0, 46005, 12, 10, 5332); // 라스타바드 근위병
				}
				for (int i = 0; i < 5; i++) {
					L1SpawnUtil.spawnLastabard(32777, 32891, (short) 533, 0, 46010, 12, 10, 5332); // 어둠의 복수자
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32777, 32891, (short) 533, 0, 45990, 12, 10, 5332); // 라스타바드 근위병
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32777, 32891, (short) 533, 0, 45996, 12, 10, 5332); // 다크 위저드
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32777, 32891, (short) 533, 0, 46001, 12, 10, 5332); // 브랏드아사신
				}
			} else if (stage == 3) {
				L1SpawnUtil.spawnLastabard(32753, 32811, (short) 533, 0, 45962, 0, 0, 5333); // 대법관 바로드
				for (int i = 0; i < 5; i++) {
					L1SpawnUtil.spawnLastabard(32753, 32811, (short) 533, 0, 46002, 12, 12, 5333); // 장로 수행원
				}
				for (int i = 0; i < 5; i++) {
					L1SpawnUtil.spawnLastabard(32753, 32811, (short) 533, 0, 46012, 12, 12, 5333); // 브랏디나이트
				}
				for (int i = 0; i < 5; i++) {
					L1SpawnUtil.spawnLastabard(32753, 32811, (short) 533, 0, 46005, 12, 12, 5333); // 라스타바드 근위병
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32753, 32811, (short) 533, 0, 45990, 12, 12, 5333); // 라스타바드 근위병
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32753, 32811, (short) 533, 0, 45996, 12, 12, 5333); // 다크 위저드
				}
				for (int i = 0; i < 7; i++) {
					L1SpawnUtil.spawnLastabard(32753, 32811, (short) 533, 0, 46001, 12, 12, 5333); // 브랏드아사신
				}
			}
		}

	}
	
	/** 몹 새롭게 셋팅할때마다 마을로 이동 */
	private void pcTel(L1PcInstance pc) {
		switch(pc.getMapId()){
		case 534:
			L1Teleport.teleport(pc, 32733, 32872, (short) 468, 5, true); // 장로회의장
			break;
		default:
			// 각 층의 휴식층으로 귀환
			int[] loc = Getback.GetBack_Location(pc, true);
			L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
		}
	}

	private void check(int mapId) {
		
		// 케이나 시간지나면 531맵의 1번방도 동일하게 종료
		if (mapId == 530) {
			if (nextClock5300 > -1) {
				nextClock5300++;
			}
			if (nextClock5300 == 300) {
				closeDoor(530);
				setDoor5300(false);
			}
			if (nextClock5300 > 1500) {
				stage5300 = false;
				stage5311 = 0;
				reset(531, 1); // 531맵의 1번방 몹 다 삭제
				closeDoor(5311);
				setDoor5311(false);
				for (L1PcInstance pcList : _pcList5311) {
					pcTel(pcList);
				}
				_pcList5311.clear();
				ListClear(530);
				ListClear(5311);
			}
		}
		
		// 531맵의 2번방
		if (mapId == 5312) {
			if (nextClock5312 > -1) {
				nextClock5312++;
			}
			if (nextClock5312 > 2100) {
				stage5312 = 0;
				reset(531, 2); // 531맵의 2번방 몹 다 삭제
				for (L1PcInstance pcList : _pcList5312) {
					pcTel(pcList);
				}
				_pcList5312.clear();
				ListClear(5312);
				closeDoor(5311);
				closeDoor(5312);
				setDoor5311(false);
				setDoor5312(false);
			}
		}
		
		// 531맵의 3번방
		if (mapId == 5313) {
			if (nextClock5313 > -1) {
				nextClock5313++;
			}
			if (nextClock5313 > 2700) {
				stage5313 = 0;
				reset(531, 3); // 531맵의 2번방 몹 다 삭제
				for (L1PcInstance pcList : _pcList5313) {
					pcTel(pcList);
				}
				_pcList5313.clear();
				ListClear(5313);
				closeDoor(5312);
				closeDoor(5313);
				setDoor5312(false);
				setDoor5313(false);
			}
		}
		
		// 이데아 시간지나면 533맵의 1번방도 동일하게 종료
		if (mapId == 532) {
			if (nextClock5320 > -1) {
				nextClock5320++;
			}
			if (nextClock5320 == 300) {
				closeDoor(532);
				setDoor5320(false);
			}
			if (nextClock5320 > 1500) {
				stage5320 = false;
				stage5331 = 0;
				reset(533, 1); // 533맵의 1번방 몹 다 삭제
				closeDoor(5331);
				setDoor5331(false);
				for (L1PcInstance pcList : _pcList5331) {
					pcTel(pcList);
				}
				_pcList5331.clear();
				ListClear(532);
				ListClear(5331);
			}
		}
		
		// 533맵의 2번방
		if (mapId == 5332) {
			if (nextClock5332 > -1) {
				nextClock5332++;
			}
			if (nextClock5332 > 2100) {
				stage5332 = 0;
				reset(533, 2); // 533맵의 2번방 몹 다 삭제
				for (L1PcInstance pcList : _pcList5332) {
					pcTel(pcList);
				}
				_pcList5332.clear();
				ListClear(5332);
				closeDoor(5331);
				closeDoor(5332);
				setDoor5331(false);
				setDoor5332(false);
			}
		}
		
		// 533맵의 3번방
		if (mapId == 5333) {
			if (nextClock5333 > -1) {
				nextClock5333++;
			}
			if (nextClock5333 > 2700) {
				stage5333 = 0;
				reset(533, 3); // 533맵의 3번방 몹 다 삭제
				for (L1PcInstance pcList : _pcList5333) {
					pcTel(pcList);
				}
				_pcList5333.clear();
				ListClear(5333);
				closeDoor(5332);
				closeDoor(5333);
				setDoor5332(false);
				setDoor5333(false);
			}
		}
		
		if (mapId == 530 || mapId == 5312 || mapId == 5313) {
			if ((nextClock5300 % 30 == 0 && nextClock5300 != -1) || (nextClock5312 % 30 == 0 && nextClock5312 != -1) || (nextClock5313 % 30 == 0 && nextClock5313 != -1)) {
				mobChk(531);
			}
		}
		
		if (mapId == 532 || mapId == 5332 || mapId == 5333) {
			if ((nextClock5320 % 30 == 0 && nextClock5320 != -1) || (nextClock5332 % 30 == 0 && nextClock5332 != -1) || (nextClock5333 % 30 == 0 && nextClock5333 != -1)) {
				mobChk(533);
			}
		}
		
	}
	
	private void mobChk(int mapId) {
		for ( L1Object obj : L1World.getInstance().getVisibleObjects(mapId).values() ) {
			if (obj instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) obj;
				countMob(pc, mapId);
			}
		}
	}

	/** 남은몹 멘트알리미 */
	private void countMob(L1PcInstance pc, int mapId) {
		if (mapId == 531) {
			if(first531Room){
				pc.sendPackets(new S_SystemMessage("\\fT현재1번방의 남은 몬스터는 [ " + _list5311.size() + " ]마리 입니다."));
			}
			if(second531Room){
				pc.sendPackets(new S_SystemMessage("\\fT현재2번방의 남은 몬스터는 [ " + _list5312.size() + " ]마리 입니다."));
			}
			if(third531Room){
				pc.sendPackets(new S_SystemMessage("\\fT현재3번방의 남은 몬스터는 [ " + _list5313.size() + " ]마리 입니다."));
			}
		} else if (mapId == 533) {
			if(first533Room){
				pc.sendPackets(new S_SystemMessage("\\fT현재1번방의 남은 몬스터는 [ " + _list5331.size() + " ]마리 입니다."));
			}
			if(second533Room){
				pc.sendPackets(new S_SystemMessage("\\fT현재2번방의 남은 몬스터는 [ " + _list5332.size() + " ]마리 입니다."));
			}
			if(third533Room){
				pc.sendPackets(new S_SystemMessage("\\fT현재3번방의 남은 몬스터는 [ " + _list5333.size() + " ]마리 입니다."));
			}
		}
	}
	
	private void first530() {
		int mobCnt = _list530.size();
		
		if (mobCnt > 0) {
			for (int i = mobCnt - 1; i >= 0; i--) {
				L1NpcInstance npc = _list530.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 530);
				}
			}
		} else {
			first530Room = false;
			openDoor(530, 4047);	
			setDoor5300(true);
			nextClock5300 = 0;
			stage5311 = 1;
		}
	}
	
	private void first531() {
		int mobCnt = _list5311.size();
		
		if (mobCnt > 0) {
			for (int i = mobCnt - 1; i >= 0; i--) {
				L1NpcInstance npc = _list5311.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 5311);
				}
			}
		} else {
			first531Room = false;
			openDoor(531, 4048);
			setDoor5311(true);
			nextClock5312 = 0;
			stage5312 = 1;
		}
	}
	
	private void second531() {
		int mobCnt = _list5312.size();
		
		if (mobCnt > 0) {
			for (int i = mobCnt - 1; i >= 0; i--) {
				L1NpcInstance npc = _list5312.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 5312);
				}
			}
		} else {
			second531Room = false;
			openDoor(531, 4049);
			setDoor5312(true);
			nextClock5313 = 0;
			stage5313 = 1;
		}
	}
	
	private void third531() {
		int mobCnt = _list5313.size();
		
		if (mobCnt > 0) {
			for (int i = mobCnt - 1; i >= 0; i--) {
				L1NpcInstance npc = _list5313.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 5313);
				}
			}
		} else {
			third531Room = false;
			openDoor(531, 4050);
			setDoor5313(true);
		}
	}
	
	private void first532() {
		int mobCnt = _list532.size();
		
		if (mobCnt > 0) {
			for (int i = mobCnt - 1; i >= 0; i--) {
				L1NpcInstance npc = _list532.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 532);
				}
			}
		} else {
			first532Room = false;
			openDoor(532, 4051);
			setDoor5320(true);
			nextClock5320 = 0;
			stage5331 = 1;
		}
	}

	private void first533() {
		int mobCnt = _list5331.size();
		
		if (mobCnt > 0) {
			for (int i = mobCnt - 1; i >= 0; i--) {
				L1NpcInstance npc = _list5331.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 5331);
				}
			}
		} else {
			first533Room = false;
			openDoor(533, 4052);
			setDoor5331(true);
			nextClock5332 = 0;
			stage5332 = 1;
		}
	}

	private void second533() {
		int mobCnt = _list5332.size();
		
		if (mobCnt > 0) {
			for (int i = mobCnt - 1; i >= 0; i--) {
				L1NpcInstance npc = _list5332.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 5332);
				}
			}
		} else {
			second533Room = false;
			openDoor(533, 4053);
			setDoor5332(true);
			nextClock5333 = 0;
			stage5333 = 1;
		}
	}

	private void third533() {
		int mobCnt = _list5333.size();
		
		if (mobCnt > 0) {
			for (int i = mobCnt - 1; i >= 0; i--) {
				L1NpcInstance npc = _list5333.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 5333);
				}
			}
		} else {
			third533Room = false;
			openDoor(533, 4054);
			setDoor5333(true);
		}
	}
	
	private void reset(int mapId, int roomNum) {
		
		for (L1Object obj : L1World.getInstance().getVisibleObjects(mapId).values()) {
			if (obj instanceof L1MonsterInstance) {				
				L1MonsterInstance mon = (L1MonsterInstance) obj;
				if (mapId == 531) {
					if (roomNum == 1) {
						for (int i = _list5311.size() - 1; i >= 0; i--) {
							L1NpcInstance npc = _list5311.get(i);
							if (npc.getId() == mon.getId()) {
								mon.deleteMe();
							}
						}
					} else if (roomNum == 2) {
						for (int i = _list5312.size() - 1; i >= 0; i--) {
							L1NpcInstance npc = _list5312.get(i);
							if (npc.getId() == mon.getId()) {
								mon.deleteMe();
							}
						}
					} else if (roomNum == 3) {
						for (int i = _list5313.size() - 1; i >= 0; i--) {
							L1NpcInstance npc = _list5313.get(i);
							if (npc.getId() == mon.getId()) {
								mon.deleteMe();
							}
						}
					}
				} else if (mapId == 533) {
					if (roomNum == 1) {
						for (int i = _list5331.size() - 1; i >= 0; i--) {
							L1NpcInstance npc = _list5331.get(i);
							if (npc.getId() == mon.getId()) {
								mon.deleteMe();
							}
						}
					} else if (roomNum == 2) {
						for (int i = _list5332.size() - 1; i >= 0; i--) {
							L1NpcInstance npc = _list5332.get(i);
							if (npc.getId() == mon.getId()) {
								mon.deleteMe();
							}
						}
					} else if (roomNum == 3) {
						for (int i = _list5333.size() - 1; i >= 0; i--) {
							L1NpcInstance npc = _list5333.get(i);
							if (npc.getId() == mon.getId()) {
								mon.deleteMe();
							}
						}
					}
				}
			}
		}
		
	}

	/** 몬스터 리스트 */
	private final ArrayList<L1NpcInstance> _list5311 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list5312 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list5313 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list5331 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list5332 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list5333 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list530 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list532 = new ArrayList<L1NpcInstance>();

	private final ArrayList<L1PcInstance> _pcList5311 = new ArrayList<L1PcInstance>();
	private final ArrayList<L1PcInstance> _pcList5312 = new ArrayList<L1PcInstance>();
	private final ArrayList<L1PcInstance> _pcList5313 = new ArrayList<L1PcInstance>();
	private final ArrayList<L1PcInstance> _pcList5331 = new ArrayList<L1PcInstance>();
	private final ArrayList<L1PcInstance> _pcList5332 = new ArrayList<L1PcInstance>();
	private final ArrayList<L1PcInstance> _pcList5333 = new ArrayList<L1PcInstance>();
	
	public void addPc(L1PcInstance pc, int type) {
		switch (type) {
		case 5311:
			if (pc == null || _pcList5311.contains(pc)) {
				return;
			}
			_pcList5311.add(pc);
			break;
		case 5312:
			if (pc == null || _pcList5312.contains(pc)) {
				return;
			}
			_pcList5312.add(pc);
			break;
		case 5313:
			if (pc == null || _pcList5313.contains(pc)) {
				return;
			}
			_pcList5313.add(pc);
			break;
		case 5331:
			if (pc == null || _pcList5331.contains(pc)) {
				return;
			}
			_pcList5331.add(pc);
			break;
		case 5332:
			if (pc == null || _pcList5332.contains(pc)) {
				return;
			}
			_pcList5332.add(pc);
			break;
		case 5333:
			if (pc == null || _pcList5333.contains(pc)) {
				return;
			}
			_pcList5333.add(pc);
			break;
		}
	}
	
	private void removePc(L1PcInstance pc, int type) {
		switch (type) {
		case 5311:
			if (pc == null || !_pcList5311.contains(pc)) {
				return;
			}
			_pcList5311.remove(pc);
			break;
			
		case 5312:
			if (pc == null || !_pcList5312.contains(pc)) {
				return;
			}
			_pcList5312.remove(pc);
			break;
			
		case 5313:
			if (pc == null || !_pcList5313.contains(pc)) {
				return;
			}
			_pcList5313.remove(pc);
			break;
			
		case 5331:
			if (pc == null || !_pcList5331.contains(pc)) {
				return;
			}
			_pcList5331.remove(pc);
			break;
			
		case 5332:
			if (pc == null || !_pcList5332.contains(pc)) {
				return;
			}
			_pcList5332.remove(pc);
			break;
			
		case 5333:
			if (pc == null || !_pcList5333.contains(pc)) {
				return;
			}
			_pcList5333.remove(pc);
			break;
		}

	}

	public void add(L1NpcInstance npc, int type) {
		switch (type) {
		case 5311:
			if (npc == null || _list5311.contains(npc)) {
				return;
			}
			_list5311.add(npc);
			break;
			
		case 5312:
			if (npc == null || _list5312.contains(npc)) {
				return;
			}
			_list5312.add(npc);
			break;
			
		case 5313:
			if (npc == null || _list5313.contains(npc)) {
				return;
			}
			_list5313.add(npc);
			break;
			
		case 5331:
			if (npc == null || _list5331.contains(npc)) {
				return;
			}
			_list5331.add(npc);
			break;
			
		case 5332:
			if (npc == null || _list5332.contains(npc)) {
				return;
			}
			_list5332.add(npc);
			break;
			
		case 5333:
			if (npc == null || _list5333.contains(npc)) {
				return;
			}
			_list5333.add(npc);
			break;
			
		case 530:
			if (npc == null || _list530.contains(npc)) {
				return;
			}
			_list530.add(npc);
			break;
			
		case 532:
			if (npc == null || _list532.contains(npc)) {
				return;
			}
			_list532.add(npc);
			break;
			
		}
	}

	private void remove(L1NpcInstance npc, int type) {
		switch (type) {
		case 5311:
			if (npc == null || !_list5311.contains(npc)) {
				return;
			}
			_list5311.remove(npc);
			break;
			
		case 5312:
			if (npc == null || !_list5312.contains(npc)) {
				return;
			}
			_list5312.remove(npc);
			break;
			
		case 5313:
			if (npc == null || !_list5313.contains(npc)) {
				return;
			}
			_list5313.remove(npc);
			break;
			
		case 5331:
			if (npc == null || !_list5331.contains(npc)) {
				return;
			}
			_list5331.remove(npc);
			break;
			
		case 5332:
			if (npc == null || !_list5332.contains(npc)) {
				return;
			}
			_list5332.remove(npc);
			break;
			
		case 5333:
			if (npc == null || !_list5333.contains(npc)) {
				return;
			}
			_list5333.remove(npc);
			break;
			
		case 530:
			if (npc == null || !_list530.contains(npc)) {
				return;
			}
			_list530.remove(npc);
			break;
			
		case 532:
			if (npc == null || !_list532.contains(npc)) {
				return;
			}
			_list532.remove(npc);
			break;
		}

	}

	private void ListClear(int type) {
		switch (type) {
		case 5311:
			_list5311.clear();
			break;
			
		case 5312:
			_list5312.clear();
			break;
			
		case 5313:
			_list5313.clear();
			break;
			
		case 5331:
			_list5331.clear();
			break;
			
		case 5332:
			_list5332.clear();
			break;
			
		case 5333:
			_list5333.clear();
			break;
			
		case 530:
			_list530.clear();
			break;
			
		case 532:
			_list532.clear();
			break;
		}
	}
	
	private void fillSpawnTable() {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int spawnCount = 0;
		
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_lastabard WHERE mapid NOT IN ('531', '533') AND (mapid, npc_templateid) NOT IN (('530', '45955'), ('532', '45959'))");
			rs = pstm.executeQuery();

			LastabardSpawn spawnDat;
			L1Npc npcTemplate;
			double amount_rate;
			int npcTemplateId, count;
			
			while (rs.next()) {
				if (rs.getInt("count") == 0) continue;
				
				npcTemplateId = rs.getInt("npc_templateid");
				npcTemplate = NpcTable.getInstance().getTemplate(npcTemplateId);
				
				if (npcTemplate == null) {
					_log.warning("[Lastabard] missing mob data for id:" + npcTemplateId  + " in npc table");
					continue;
				}
				
				amount_rate = MapsTable.getInstance().getMonsterAmount(rs.getShort("mapid"));
				count = calcCount(npcTemplate, rs.getInt("count"), amount_rate);
				
				if (count == 0) continue;

				spawnDat = new LastabardSpawn(npcTemplate);
				spawnDat.setId(rs.getInt("id"));
				spawnDat.setAmount(count);
				spawnDat.setGroupId(rs.getInt("group_id"));
				spawnDat.setLocX(rs.getInt("locx"));
				spawnDat.setLocY(rs.getInt("locy"));
				spawnDat.setRandomx(rs.getInt("randomx"));
				spawnDat.setRandomy(rs.getInt("randomy"));
				spawnDat.setLocX1(rs.getInt("locx1"));
				spawnDat.setLocY1(rs.getInt("locy1"));
				spawnDat.setLocX2(rs.getInt("locx2"));
				spawnDat.setLocY2(rs.getInt("locy2"));
				spawnDat.setHeading(rs.getInt("heading"));
				spawnDat.setMinRespawnDelay(rs.getInt("min_respawn_delay"));
				spawnDat.setMaxRespawnDelay(rs.getInt("max_respawn_delay"));
				spawnDat.setMapId(rs.getShort("mapid"));
				spawnDat.setRespawnScreen(rs.getBoolean("respawn_screen"));
				spawnDat.setMovementDistance(rs.getInt("movement_distance"));
				spawnDat.setRest(rs.getBoolean("rest"));
				//spawnDat.setSpawnType(rs.getInt("near_spawn"));
				spawnDat.setName(npcTemplate.get_name());
				spawnDat.setDoorId(rs.getInt("spawnlist_door"));
				spawnDat.setCountMapId(rs.getInt("count_map"));

				if (count > 1 && spawnDat.getLocX1() == 0) {
					// 다수의 고정 스폰몹은  개체수 * 6 의 범위스폰 (범위 30 이하)
					int range = Math.min(count * 6, 30);
					spawnDat.setLocX1(spawnDat.getLocX() - range);
					spawnDat.setLocY1(spawnDat.getLocY() - range);
					spawnDat.setLocX2(spawnDat.getLocX() + range);
					spawnDat.setLocY2(spawnDat.getLocY() + range);
				}

				// start the spawning
				spawnDat.init(true);
				spawnCount += spawnDat.getAmount();

				spawnTable.put(new Integer(spawnDat.getId()), spawnDat);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (SecurityException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (ClassNotFoundException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.fine("[Lastabard] 총 " + spawnCount + "마리");
	}
	
	private static int calcCount(L1Npc npc, int count, double rate) {
		if (rate == 0) 							return 0;
		if (rate == 1 || npc.isAmountFixed()) 	return count;
		else 									return NumberUtil.randomRound((count * rate));
	}
	
}
