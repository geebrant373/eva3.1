package l1j.server.server.TimeController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

public class DungeonQuitController implements Runnable {

	private static DungeonQuitController _instance;

	/** 게임오픈유무 **/
	public boolean isgameStart = false;

	/** 게임상태 **/
	public int Status = 0;// 진행 상태
	private final int 대기 = 0;// 진행
	private final int 오픈 = 1;
	private final int 진행 = 2;
	private final int 종료 = 3;//

	public static DungeonQuitController getInstance() {
		if (_instance == null) {
			_instance = new DungeonQuitController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				switch (Status) {
				case 대기:
					Thread.sleep(10000);
					if (isgameStart == false) {
						continue;
					}
					Status = 오픈;
					continue;
				case 오픈:
					L1World.getInstance().broadcastServerMessage("알림: 잠시 후 모든 인스턴스 던전 시간이 초기화 됩니다.");
					Thread.sleep(3000L);
					Status = 진행;
					continue;
				case 진행:
					Thread.sleep(5000L);
					safeUpdate("dreamisland");
                    safeUpdate("giranprison");
                    safeUpdate("lastabard");
                    safeUpdate("dragongludio");
                    safeUpdate("antdundeon");
                    safeUpdate("shadowtemple");
                    // 접속 캐릭터 메모리 초기화
                    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                    	if (pc == null) continue;
                        if (pc.getAccount() == null) continue;
                        pc.getAccount().setDreamIslandTime(0);
                        pc.getAccount().setGiranPrisonTime(0);
                        pc.getAccount().setLastabardTime(0);
                        pc.getAccount().setDragonGludioTime(0);
                        pc.getAccount().setAntDundeonTime(0);
                        pc.getAccount().setShadowTempleTime(0);
                    }
					Status = 종료;
					continue;
				case 종료:
					L1World.getInstance().broadcastServerMessage("알림: 모든 인스턴스 던전 시간이 초기화 되었습니다.");
					isgameStart = false;
					Status = 대기;
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updatedreamisland() throws SQLException {
		Connection cc = null;
		PreparedStatement p = null;
		try {
			cc = L1DatabaseFactory.getInstance().getConnection();
			p = cc.prepareStatement("update accounts set dreamisland=0");
			p.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			p.close();
			cc.close();
		}
	}

	public void updategiranprison() throws SQLException {
		Connection cc = null;
		PreparedStatement p = null;
		try {
			cc = L1DatabaseFactory.getInstance().getConnection();
			p = cc.prepareStatement("update accounts set giranprison=0");
			p.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			p.close();
			cc.close();
		}
	}
	
	public void updatelastabard() throws SQLException {
		Connection cc = null;
		PreparedStatement p = null;
		try {
			cc = L1DatabaseFactory.getInstance().getConnection();
			p = cc.prepareStatement("update accounts set lastabard=0"); 
			p.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			p.close();
			cc.close();
		}
	}
	
	private void safeUpdate(String columnName) throws SQLException {
	    Connection cc = null;
	    PreparedStatement p = null;
	    try {
	        cc = L1DatabaseFactory.getInstance().getConnection();
	        p = cc.prepareStatement("UPDATE accounts SET " + columnName + "=0");
	        p.execute();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (p != null) p.close();
	        if (cc != null) cc.close();
	    }
	}
}
