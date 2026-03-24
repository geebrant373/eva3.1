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
package l1j.server.server.TimeController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.FishExpTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ServerMessage;

public class FishingTimeController implements Runnable {
	
	public static final int SLEEP_TIME = 300;
	
	private static FishingTimeController _instance;
	
	private final List<L1PcInstance> _fishingList = new ArrayList<L1PcInstance>();

	private static Random _random = new Random(System.nanoTime());
	
	public static FishingTimeController getInstance() {
		if (_instance == null) {
			_instance = new FishingTimeController();
		}
		return _instance;
	}

	public void run() {
		try {
			fishing();
		} catch (Exception e1) {
		}
	}

	public void addMember(L1PcInstance pc) {
		if (pc == null || _fishingList.contains(pc)) {
			return;
		}
		_fishingList.add(pc);
	}

	public void removeMember(L1PcInstance pc) {
		if (pc == null || !_fishingList.contains(pc)) {
			return;
		}
		_fishingList.remove(pc);
	}

	private void fishing() {
		if (_fishingList.size() > 0) {
			long currentTime = System.currentTimeMillis();
			L1PcInstance pc = null;
			for (int i = 0; i < _fishingList.size(); i++) {
				pc = _fishingList.get(i);
				if (pc == null)
					continue;
				if (pc.isFishing()) {
					long time = pc.getFishingTime();
					if (currentTime > (time + 1000)) {
						if (pc.getInventory().checkItem(241295, 1) || pc.getInventory().checkItem(141295, 1) || pc.getInventory().consumeItem(41295, 1)) {
							if(pc._fishingRod.getItemId() == 41294){
								successFishing(pc, 49494, "어망주머니(를)"); // 베리아나
								pc.setFishingTime(System.currentTimeMillis() + 60000);
							} else {
								successFishing(pc, 49494, "어망주머니(를)"); // 베리아나
								pc.setFishingTime(System.currentTimeMillis() + 120000);
							}
						} else {
							endFishing(pc);
						}
					}
				}
			}
		}
	}

	private void addFishExp(L1PcInstance pc) {
		try {
			int addExp = ExpTable.getNeedExpNextLevel(pc.getLevel());
			int fishCount = FishExpTable.getInstance().getFishCountByLevel(pc.getLevel());

			int realExp = addExp / fishCount;
			if (pc.getInventory().checkItem(241295)) {
				realExp *= 2.5;
			}
			pc.addExp(realExp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void endFishing(L1PcInstance pc) {
		// TODO Auto-generated method stub
		pc.setFishingTime(0);
		pc.setFishingReady(false);
		pc.setFishing(false);
		pc.sendPackets(new S_CharVisualUpdate(pc));
		pc._fishingRod = null;
		Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
		removeMember(pc);
	}

	private void successFishing(L1PcInstance pc, int itemid, String message) {
		int chance = _random.nextInt(10000) + 1; //100%
		if (pc == null) {
			return;
		}
		if (pc.getInventory().getSize() > (180 - 16)) {
			pc.sendPackets(new S_ServerMessage(263));
			return;
		}

		if (chance < Config.낚시성공확률) {
			if (pc != null) {
				L1ItemInstance item = pc.getInventory().storeItem(itemid, 1);
				if (pc.getLevel() < Config.LIMITLEVEL) {
					addFishExp(pc);
				}
				if (item != null) {
					pc.sendPackets(new S_ServerMessage(1185, message));
				}
			}
		} else {
			pc.sendPackets(new S_ServerMessage(1136));
		}
		
	}
}
