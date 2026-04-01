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

package l1j.server.server.model.item.function;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.TimeController.FishingTimeController;
import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_BlueMessage;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.types.Point;

@SuppressWarnings("serial")
public class Fishing extends L1ItemInstance {

	public Fishing(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			int itemId = this.getItemId();
			pc._fishingRod = pc.getInventory().findItemId(itemId);
			
			if (Config.STANDBY_SERVER) {
				pc.sendPackets(new S_SystemMessage("오픈대기 상태에서는 낚시를 할 수 없습니다."), true);
				return;
			}
			if (pc.isFishing()) {
				pc.sendPackets(new S_SystemMessage("낚시: 진행중"), true);
			} else {
				if (Config.낚시장소) {
					startFishing(pc, itemId, packet.readH(), packet.readH());
				} else {
					startFishingInTown(pc, itemId, packet.readH(), packet.readH());
				}
				
			}
		}
	}

	
	private void startFishing(L1PcInstance pc, int itemId, int fishX, int fishY) {
		if (pc.getMapId() != 4 ) {//5302
			pc.sendPackets(new S_BlueMessage(1416, "\\f=이 곳에서는 낚시가 불가능 합니다."));
			return;
		}
	

		if (pc.getLocation().getTileDistance(new Point(fishX, fishY)) > 15) {
			pc.sendPackets(new S_BlueMessage(1416, "\\f=조금 더 가까이에서 던져주세요."));
			return;
		}
		int gab = 0;
		int heading = pc.getMoveState().getHeading(); // ● 방향: (0.좌상)(1.상)(
														// 2.우상)(3.오른쪽)(4.우하)(5.하)(6.좌하)(7.좌)
		switch (heading) {
		case 0: // 상좌
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX(), pc.getY() - 5);
			break;
		case 1: // 상
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX() + 5, pc.getY() - 5);
			break;
		case 2: // 우상
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX() + 5, pc.getY() - 5);
			break;
		case 3: // 오른쪽
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX() + 5, pc.getY() + 5);
			break;
		case 4: // 우하
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX(), pc.getY() + 5);
			break;
		case 5: // 하
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX() - 5, pc.getY() + 5);
			break;
		case 6: // 좌하
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX() - 5, pc.getY());
			break;
		case 7: // 좌
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX() - 5, pc.getY() - 5);
			break;
		}
		int fishGab = pc.getMap().getOriginalTile(fishX, fishY);
		if (gab == 28 && fishGab == 28) {
			if (pc.getInventory().checkItem(241295, 1) || pc.getInventory().checkItem(141295, 1) || pc.getInventory().consumeItem(41295, 1)) { // 먹이 
				pc.setFishing(true);
				pc.setFishingItem(this);
				pc.sendPackets(new S_Fishing(pc.getId(), ActionCodes.ACTION_Fishing, fishX, fishY), true);
				Broadcaster.broadcastPacket(pc, new S_Fishing(pc.getId(), ActionCodes.ACTION_Fishing, fishX, fishY), true);
				pc.fishX = fishX;
				pc.fishY = fishY;
				long time = System.currentTimeMillis() + 120000;
				if (itemId == 41294) {// 짧고 가벼운 낚싯대
					time = System.currentTimeMillis() + 60000;
				}
				pc.setFishingTime(time);
				FishingTimeController.getInstance().addMember(pc);
			} else {
				// 낚시를 하기 위해서는 먹이가 필요합니다.
				pc.sendPackets(new S_ServerMessage(1137), true);
			}
		} else {
			// 여기에 낚싯대를 던질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(1138), true);
		}
	}
	
	private void startFishingInTown(L1PcInstance pc, int itemId, int fishX, int fishY) { 
		int gab = 0;
		int heading = pc.getMoveState().getHeading(); // ● 방향: (0.좌상)(1.상)(
														// 2.우상)(3.오른쪽)(4.우하)(5.하)(6.좌하)(7.좌)
		switch (heading) {
		case 0: // 상좌
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX(), pc.getY() - 5);
			break;
		case 1: // 상
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX() + 5, pc.getY() - 5);
			break;
		case 2: // 우상
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX() + 5, pc.getY() - 5);
			break;
		case 3: // 오른쪽
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX() + 5, pc.getY() + 5);
			break;
		case 4: // 우하
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX(), pc.getY() + 5);
			break;
		case 5: // 하
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX() - 5, pc.getY() + 5);
			break;
		case 6: // 좌하
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX() - 5, pc.getY());
			break;
		case 7: // 좌
			gab = L1WorldMap.getInstance().getMap((short) 4)
					.getOriginalTile(pc.getX() - 5, pc.getY() - 5);
			break;
		}
		int px = 0;
		int py = 0;
		int x = 33446;// 잡아둔좌표 기준점 33417 32831
		int y = 32790;
		px = 4;
		py = 5;
		if ((fishX >= x - px && fishX <= x + px) && (fishY >= y - py && fishY <= y + py)) {
			if (pc.getInventory().checkItem(241295, 1) || pc.getInventory().checkItem(141295, 1) || pc.getInventory().consumeItem(41295, 1)) { // 먹이 
				pc.setFishing(true);
				pc.setFishingItem(this);
				pc.sendPackets(new S_Fishing(pc.getId(), ActionCodes.ACTION_Fishing, fishX, fishY), true);
				Broadcaster.broadcastPacket(pc, new S_Fishing(pc.getId(), ActionCodes.ACTION_Fishing, fishX, fishY), true);
				pc.fishX = fishX;
				pc.fishY = fishY;
				long time = System.currentTimeMillis() + 120000;
				if (itemId == 41294) {// 짧고 가벼운 낚싯대
					time = System.currentTimeMillis() + 60000;
				}
				pc.setFishingTime(time);
				FishingTimeController.getInstance().addMember(pc);
			} else {
				// 낚시를 하기 위해서는 먹이가 필요합니다.
				pc.sendPackets(new S_ServerMessage(1137), true);
			}
		} else {
			// 여기에 낚싯대를 던질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(1138), true);
		}
	}
}
