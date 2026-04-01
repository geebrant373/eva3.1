package l1j.server.server.command.executor;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;


import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.SpawnTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1NpcDown2 implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1NpcDown2.class.getName());

	public static L1CommandExecutor getInstance() {
		return new L1NpcDown2();
	}

	public void execute(L1PcInstance pc, String cmdName, String poby) {
		try {
			StringTokenizer token = new StringTokenizer(poby);
			int mapId = Integer.parseInt(token.nextToken());

			ArrayList<Integer> ids = new ArrayList<Integer>();
			int removeCount = 0;
			for (L1Spawn l1spawn : SpawnTable.getInstance().getSpawns()) {
				if ((l1spawn != null) && (l1spawn.getMapId() == mapId)) {
					l1spawn.setSpawnType(100);
					ids.add(Integer.valueOf(l1spawn.getId()));
				}
			}
			for (L1Object l1object : L1World.getInstance().getObject()) {
				if ((l1object != null) && (l1object.getMapId() == mapId) && (((l1object instanceof L1MonsterInstance))))
					
																				 {
					L1MonsterInstance npc = (L1MonsterInstance)l1object;
					npc.setReSpawn(false);
					npc.deleteMe();
					removeCount++;
				}
			}

			for (int id : ids) {				
				SpawnTable.getInstance().removeSpawn(id);
			}
			ids.clear();
			ids = null;

			int spawnCount = SpawnTable.getInstance().spawnMonsters(mapId);
			pc.sendPackets(new S_SystemMessage(MapsTable.getInstance().locationname(mapId)+" / "+ mapId+"¹ø ¸Ê"  + " : »èÁ¦: " + removeCount	+ " / ¹èÄ¡: " + spawnCount));
		
		} catch (Exception e) {
		
			pc.sendPackets(new S_SystemMessage("."+cmdName+" <¸Ê ¹øÈ£> "));
		}
	}
}