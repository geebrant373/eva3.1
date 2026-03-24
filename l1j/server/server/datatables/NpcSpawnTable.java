/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

//Referenced classes of package l1j.server.server:
//MobTable, IdFactory

public class NpcSpawnTable {
	private static Logger _log = Logger.getLogger(NpcSpawnTable.class.getName());
	private static NpcSpawnTable _instance;
	private Map<Integer, L1Spawn> _spawntable = new HashMap<Integer, L1Spawn>();
	private int _highestId;
	
	public static NpcSpawnTable getInstance() {
		if (_instance == null) {
			_instance = new NpcSpawnTable();
		}
		return _instance;
	}

	private NpcSpawnTable() {
		fillNpcSpawnTable();
	}

	 public void reload1() {
		  PerformanceTimer timer = new PerformanceTimer();
		  System.out.print("loading " + _log.getName().substring(_log.getName().lastIndexOf(".") + 1) + "...");
		  NpcSpawnTable oldInstance = _instance;
		  oldInstance._spawntable.clear();
		  fillNpcSpawnTable();

		  System.out.println("OK! " + timer.get() + " ms");
		 }
	
	private void fillNpcSpawnTable() {
		int spawnCount = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_npc");
			rs = pstm.executeQuery();
			do {
				if (!rs.next()) {break;}
				
				int npcTemplateid = rs.getInt("npc_templateid");
				
				if (Config.ALT_HALLOWEENEVENT == false && npcTemplateid == 4200001) {					
						continue;					
				}
				if (Config.ALT_FANTASYEVENT == false && npcTemplateid == 4200500){
						continue;
				}
				if (Config.GAME_SERVER_TYPE == 0
					&& (npcTemplateid == 4200103 || npcTemplateid == 4200104)){
					continue;					
				}
				
				L1Npc l1npc = NpcTable.getInstance().getTemplate(npcTemplateid);
				L1Spawn l1spawn;				
				if (l1npc == null) {
					_log.warning("mob data for id:" + npcTemplateid	+ " missing in npc table");
					l1spawn = null;
				} else {
					if (rs.getInt("count") == 0) {
						continue;
					}
					l1spawn = new L1Spawn(l1npc);
					l1spawn.setId(rs.getInt("id"));
					l1spawn.setAmount(rs.getInt("count"));
					l1spawn.setLocX(rs.getInt("locx"));
					l1spawn.setLocY(rs.getInt("locy"));
					l1spawn.setRandomx(rs.getInt("randomx"));
					l1spawn.setRandomy(rs.getInt("randomy"));
					l1spawn.setLocX1(0);
					l1spawn.setLocY1(0);
					l1spawn.setLocX2(0);
					l1spawn.setLocY2(0);
					l1spawn.setHeading(rs.getInt("heading"));
					l1spawn.setMinRespawnDelay(rs.getInt("respawn_delay"));
					l1spawn.setMapId(rs.getShort("mapid"));
					l1spawn.setMovementDistance(rs.getInt("movement_distance"));
					l1spawn.setName(l1npc.get_name());
					l1spawn.init();
					spawnCount += l1spawn.getAmount();

					_spawntable.put(new Integer(l1spawn.getId()), l1spawn);
					if (l1spawn.getId() > _highestId) {
						_highestId = l1spawn.getId();
					}
				}

			} while (true);
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
		_log.config("NPC 배치 리스트 " + _spawntable.size() + "건 로드");
		_log.fine("총NPC수 " + spawnCount + "건");
	}

	public void storeSpawn(L1PcInstance pc, L1Npc npc) {
		Connection con = null;
		PreparedStatement pstm = null;

		try {
			int count = 1;
			String note = npc.get_name();

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO spawnlist_npc SET location=?,count=?,npc_templateid=?,locx=?,locy=?,heading=?,mapid=?");
			pstm.setString(1, note);
			pstm.setInt(2, count);
			pstm.setInt(3, npc.get_npcId());
			pstm.setInt(4, pc.getX());
			pstm.setInt(5, pc.getY());
			pstm.setInt(6, pc.getMoveState().getHeading());
			pstm.setInt(7, pc.getMapId());
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public L1Spawn getTemplate(int i) {
		return _spawntable.get(i);
	}

	public void addNewSpawn(L1Spawn l1spawn) {
		_highestId++;
		l1spawn.setId(_highestId);
		_spawntable.put(l1spawn.getId(), l1spawn);
	}
	
	public void removeSpawn(L1NpcInstance paramL1NpcInstance) {
	    Connection con = null;
	    PreparedStatement pstm = null;
	    ResultSet rs = null;
	    try {
	        // 데이터베이스 연결
	        con = L1DatabaseFactory.getInstance().getConnection();

	        // 스폰 정보 조회
	        pstm = con.prepareStatement(
	            "SELECT id FROM spawnlist_npc WHERE npc_templateid=? AND mapid=? AND locx=? AND locy=?"
	        );
	        pstm.setInt(1, paramL1NpcInstance.getNpcId());
	        pstm.setInt(2, paramL1NpcInstance.getMapId());
	        pstm.setInt(3, paramL1NpcInstance.getX());
	        pstm.setInt(4, paramL1NpcInstance.getY());
	        rs = pstm.executeQuery();

	        if (rs.next()) {
	            int id = rs.getInt("id");

	            // 내부 스폰 테이블에서 제거
	            this._spawntable.remove(Integer.valueOf(id));

	            // Statement 닫기
	            SQLUtil.close(pstm);
	            pstm = null; // 리소스 초기화

	            // 스폰 정보 삭제
	            pstm = con.prepareStatement(
	                "DELETE FROM spawnlist_npc WHERE npc_templateid=? AND mapid=? AND locx=? AND locy=?"
	            );
	            pstm.setInt(1, paramL1NpcInstance.getNpcId());
	            pstm.setInt(2, paramL1NpcInstance.getMapId());
	            pstm.setInt(3, paramL1NpcInstance.getX());
	            pstm.setInt(4, paramL1NpcInstance.getY());
	            pstm.executeUpdate();
	        }
	    } catch (Exception e) {
	        // 예외 발생 시 로깅
	        e.printStackTrace();
	    } finally {
	        // 리소스 정리
	        SQLUtil.close(rs);
	        SQLUtil.close(pstm); // PreparedStatement 닫기
	        SQLUtil.close(con);  // Connection 닫기
	    }
	}

}
