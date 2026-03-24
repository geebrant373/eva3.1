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
package l1j.server.server.clientpackets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import l1j.server.Config;
import l1j.server.ParseHelper.ArrangeParseeFactory;
import l1j.server.ParseHelper.ArrangeParser;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.monitor.Logger.ItemActionType;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import manager.LinAllManager;
import server.LineageClient;
import server.system.autoshop.AutoShopManager;

public class C_PickUpItem extends ClientBasePacket {

	private static final String C_PICK_UP_ITEM = "[C] C_PickUpItem";
	private Random _random = new Random();

	public C_PickUpItem(byte decrypt[], LineageClient client) throws Exception {
		super(decrypt);
		int x = readH();
		int y = readH();
		int objectId = readD();
		int pickupCount = readD();
		L1PcInstance pc = client.getActiveChar();

		if (pc.getOnlineStatus() != 1) {
			pc.sendPackets(new S_Disconnect());
			return;
		}
		if (isTwoLogin(pc))
			return;
		if (pc.isGhost() || pc.isDead()) {
			return;
		}
		if (pc.isInvisble()) {
			return;
		} // 인비지 상태
		if (pc.isInvisDelay()) {
			return;
		} // 인비지디레이 상태
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {
			return;
		}
		L1Inventory groundInventory = L1World.getInstance().getInventory(x, y, pc.getMapId());
		L1Object object = groundInventory.getItem(objectId);

		if (object != null && !pc.isDead()) {
			L1ItemInstance item = (L1ItemInstance) object;
			// 토글시에 아이템 삭제시간을 초기화 한다.
			item.init_DeleteItemTime();

			if (item.getItemOwner() != null) {
				if (item.getItemOwner().isInParty()) {
					if (!item.getItemOwner().getParty().isMember(pc)) {
						pc.sendPackets(new S_ServerMessage(623));
						return;
					}
				} else {
					if (item.getItemOwner().getId() != pc.getId()) {
						pc.sendPackets(new S_ServerMessage(623));
						return;
					}
				}
			}
			if (objectId != item.getId()) {
				pc.sendPackets(new S_Disconnect());
				return;
			}
			if (!item.isStackable() && pickupCount != 1) {
				pc.sendPackets(new S_Disconnect());
				return;
			}
			if (pickupCount <= 0 || item.getCount() <= 0) {
				pc.sendPackets(new S_Disconnect());
				groundInventory.deleteItem(item);
				return;
			}
			if (pickupCount > item.getCount()) {
				pickupCount = item.getCount();
			}

			if (((item.getCount() >= 100) && ((item.getItemId() == 41159)
					|| (item.getItemId() == 40087) || (item.getItemId() == 40074)))
					|| (item.getEnchantLevel() > 0)
					|| ((item.getCount() >= 1000000) && (item.getItemId() == 40308))
					|| ((item.getCount() >= 1000) && (item.getItemId() != 40308)))
				LinAllManager.getInstance().PicupAppend(item.getLogName(), item.getName(), item.getCount(), 0);
			
			if (item.getItem().getItemId() == L1ItemId.ADENA) {
				L1ItemInstance inventoryItem = pc.getInventory().findItemId(L1ItemId.ADENA);
				int inventoryItemCount = 0;
				if (inventoryItem != null) {
					inventoryItemCount = inventoryItem.getCount();
				}
				// 주운 후에 2 G를 초과하지 않게 체크
				if ((long) inventoryItemCount + (long) pickupCount > 2000000000L) {
					pc.sendPackets(new S_ServerMessage(166, // \f1%0이%4%1%3%2
							"소지하고 있는 아데나", "2,000,000,000을 초과하므로 주울 수 없습니다."));
					return;
				}
			}
			// 용량 중량 확인 및 메세지 송신
			if (pc.getInventory().checkAddItem(item, pickupCount) == L1Inventory.OK) {
				if (item.getX() != 0 && item.getY() != 0) {
					if (pc.isInParty()) {
						// 자동분배 타입인가?
						if (pc.getParty().getLeader().getPartyType() == 1 && item.isDropMobId() != 0) {
							List<L1PcInstance> _membersList = new ArrayList<L1PcInstance>();
							_membersList.add(pc);
							for (L1PcInstance realUser : L1World.getInstance().getVisiblePlayer(pc, 50)) {
								if (pc.getParty().isMember(realUser) && pc.getId() != realUser.getId()) {
									_membersList.add(realUser);
								}
							}
							// 랜덤으로 누구 한테 갈껀지 ㅋ
							int luckuyNum = _random.nextInt(_membersList.size());
							L1PcInstance luckyUser = _membersList.get(luckuyNum);

							// 아데나 인가?
							if (item.getItemId() == L1ItemId.ADENA) {
								int divAden = pickupCount / _membersList.size();
								if (_membersList.size() > 1) {
									int modNum = pickupCount % _membersList.size();
									if (modNum == 0) {
										for (int row = 0; row < _membersList.size(); row++) {
											groundInventory.tradeItem(item, divAden,
													_membersList.get(row).getInventory());
										}
									} else {
										if (pickupCount < _membersList.size()) {
											groundInventory.tradeItem(item, pickupCount, pc.getInventory());
										} else {
											for (int row = 0; row < _membersList.size(); row++) {
												if (pc.getId() == _membersList.get(row).getId()) {
													groundInventory.tradeItem(item, divAden + modNum,
															pc.getInventory());
												} else {
													groundInventory.tradeItem(item, divAden,
															_membersList.get(row).getInventory());
												}
											}
										}
									}
								} else {
									groundInventory.tradeItem(item, pickupCount, pc.getInventory());
								}

							}
							// 아니면 다른 아이템인가?
							else {
								groundInventory.tradeItem(item, pickupCount, luckyUser.getInventory());
								if (item.isDropMobId() != 0) {
									L1Npc npc = NpcTable.getInstance().getTemplate(item.isDropMobId());
									for (L1PcInstance partymember : pc.getParty().getMembers()) {
										if (!partymember.getSkillEffectTimerSet()
												.hasSkillEffect(L1SkillId.STATUS_MENT)) {
											partymember.sendPackets(new S_ServerMessage(813, npc.get_name(),
													item.getLogName(), luckyUser.getName()));
										}
									}
									item.setDropMobId(0);
								}
							}
						}
						// 아니면 그냥인가?
						else {
							groundInventory.tradeItem(item, pickupCount, pc.getInventory());
							if (item.isDropMobId() != 0) {
								L1Npc npc = NpcTable.getInstance().getTemplate(item.isDropMobId());
								for (L1PcInstance partymember : pc.getParty().getMembers()) {
									if (!partymember.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_MENT)) {
										partymember.sendPackets(new S_ServerMessage(813, npc.get_name(),
												item.getLogName(), pc.getName()));
									}
								}
								item.setDropMobId(0);
							}
						}
						pc.getLight().turnOnOffLight();
					} else { // 파티가아닐시
						groundInventory.tradeItem(item, pickupCount, pc.getInventory());
						pc.getLight().turnOnOffLight();
					}
					pc.sendPackets(new S_DoActionGFX(objectId, ActionCodes.ACTION_Pickup));
					if (!pc.isGmInvis()) {
						Broadcaster.broadcastPacket(pc, new S_DoActionGFX(objectId, ActionCodes.ACTION_Pickup));
					}
				}
			}
			
			if (!item.isGiveItem()) {
				String[] pickup_item = (String[]) ArrangeParser.parsing(Config.PickUpItem_Id, ",", ArrangeParseeFactory.createStringArrange()).result(); 
				for (int i = 0; i < pickup_item.length; i++) {
					if (item.getItemId() == Integer.parseInt(pickup_item[i])) {
						String men = "";
						if (Config.PickUpItem_UserName)
							men = "" + pc.getName() + " 님께서";
						else
							men = "누군가가";

						String itemName = item.getViewName();
						if (itemName == null)
							itemName = item.getName();
						String message = String.format("" + men + "\\fH %s\\f2을(를) 획득하였습니다.", itemName);
						L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, message));
						L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(message));
					}
				}
			}
			
			item.setGiveItem(false);
			LoggerInstance.getInstance().addItemAction(ItemActionType.Pickup, pc, item, (int) pickupCount);
		}
	}

	private boolean isTwoLogin(L1PcInstance c) {
boolean bool = false;
		
		for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
			if (target.noPlayerCK)continue;
			if (c.getId() != target.getId() && (!target.isPrivateShop() && !target.isAutoClanjoin())) {
				if (c.getNetConnection().getAccountName().equalsIgnoreCase(target.getNetConnection().getAccountName())) {
					bool = true;
					break;
				}
			}
		}
		return bool;
	}

	@Override
	public String getType() {
		return C_PICK_UP_ITEM;
	}
}
