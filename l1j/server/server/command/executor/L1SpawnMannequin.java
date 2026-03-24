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
package l1j.server.server.command.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class L1SpawnMannequin implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1SpawnMannequin.class.getName());

	private L1SpawnMannequin() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1SpawnMannequin();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int classtype = Integer.parseInt(st.nextToken());

			int npcid = 5000000 + classtype;
			if(npcid > 5000100) {
				pc.sendPackets(new S_SystemMessage("허상으로 새울수 있는 엔피씨 수를 초과하였습니다."));
				return;
			}

			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcid);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(pc.getMapId());

			npc.getLocation().set(pc.getX(), pc.getY(), pc.getMapId());
			npc.getLocation().forward(5);

			npc.setHomeX(pc.getX());
			npc.setHomeY(pc.getY());
			npc.getMoveState().setHeading(pc.getMoveState().getHeading());

			npc.setNameId(npc.getNameId());
			npc.setTitle("");
			npc.setTempLawful(npc.getLawful());

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc); 
			insertMannequin(pc, npc.getNameId(), npcid, npc.getLawful());
			
			npc.getLight().turnOnOffLight();

		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName
					+ "[타입] 를 입력해 주세요. "));
		}
	}

	public void insertMannequin(L1PcInstance pc, String name, int npcid, int lawful) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO spawnlist_mannequin SET name=?, npc_id=?, lawful=?, locx=?, locy=?, mapid=?, heading=?");

			pstm.setString(1, name);
			pstm.setInt(2, npcid);
			pstm.setInt(3, lawful);
			pstm.setInt(4, pc.getX());
			pstm.setInt(5, pc.getY());
			pstm.setInt(6, pc.getMapId());
			pstm.setInt(7, pc.getMoveState().getHeading());
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

	}
}
