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

import java.util.Random;

import l1j.server.Config;
import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class EnchantWeapon extends Enchant {

	private static Random _random = new Random();

	public EnchantWeapon(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			
			if(Config.KEYWORD_USE) {
				if(!pc.getNetConnection().keyword) {
					pc.sendPackets("키워드 인증 후에 사용이 가능합니다.");
					return;
				}
			}
			
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = this.getItemId();
			L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(packet.readD());
			if (pc.getLastEnchantItemid() == l1iteminstance1.getId()) {
				pc.setLastEnchantItemid(l1iteminstance1.getId(), l1iteminstance1);
				return;
			}
			if (l1iteminstance1 == null || l1iteminstance1.getItem().getType2() != 1) {
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
			}
			if (l1iteminstance1.getBless() >= 128) { // 봉인템
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
			}
			int safe_enchant = l1iteminstance1.getItem().get_safeenchant();
			if (safe_enchant < 0) { // 강화 불가
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
			}
			int weaponId = l1iteminstance1.getItem().getItemId();

			if (itemId == L1ItemId.WIND_ENCHANT_WEAPON_SCROLL || itemId == L1ItemId.EARTH_ENCHANT_WEAPON_SCROLL
					|| itemId == L1ItemId.WATER_ENCHANT_WEAPON_SCROLL || itemId == L1ItemId.FIRE_ENCHANT_WEAPON_SCROLL) {
				AttrEnchant(pc, l1iteminstance1, itemId);
				pc.getInventory().removeItem(useItem, 1);
				return;
			}

			if (weaponId >= 246 && weaponId <= 249) { // 강화 불가
				if (itemId == L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON) {// 시련의
																		// 스크롤
				} else {
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
																// 않았습니다.
					return;
				}
			}
			if (itemId == L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON) {
				// 시련의 스크롤
				if (weaponId >= 246 && weaponId <= 249) { // 강화 불가
				} else {
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
																// 않았습니다.
					return;
				}
			}
			/** 환상의 무기 마법 주문서 **/
			if (weaponId >= 413000 && weaponId <= 413007) { // 이외에 강화 불가
				if (itemId == L1ItemId.SCROLL_OF_ENCHANT_FANTASY_WEAPON) {// 환상의무기마법주문서
				} else {
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
																// 않았습니다.
					return;
				}
			}
			if (itemId == L1ItemId.SCROLL_OF_ENCHANT_FANTASY_WEAPON) {// 환상의무기마법주문서
				if (weaponId >= 413000 && weaponId <= 413007) { // 이외에 강화 불가
				} else {
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
																// 않았습니다.
					return;
				}
			}
			/** 환상의 무기 마법 주문서 **/

			/** 창천 무기 마법 주문서 **/
			if (weaponId >= 411000 && weaponId <= 411035) {
				if (itemId == L1ItemId.CHANGCHUN_ENCHANT_WEAPON_SCROLL) {
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
			}
			if (itemId == L1ItemId.CHANGCHUN_ENCHANT_WEAPON_SCROLL) {
				if (weaponId >= 411000 && weaponId <= 411035) {
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
			}
			/** 창천 무기 마법 주문서 **/
			int enchant_level = l1iteminstance1.getEnchantLevel();

			if (Config.GAME_SERVER_TYPE == 1 && enchant_level >= safe_enchant + 3
					&& (itemId != L1ItemId.WIND_ENCHANT_WEAPON_SCROLL || itemId != L1ItemId.EARTH_ENCHANT_WEAPON_SCROLL
							|| itemId != L1ItemId.WATER_ENCHANT_WEAPON_SCROLL || itemId != L1ItemId.FIRE_ENCHANT_WEAPON_SCROLL)) {
				pc.sendPackets(new S_SystemMessage("테스트서버에서는 안전인챈+3 이상은 인챈하실수 없습니다."));
				return;
			}

			if (enchant_level >= Config.MAX_WEAPON) { // 인챈트 제한
				pc.sendPackets(new S_SystemMessage("\\fW무기는 현재 +" + Config.MAX_WEAPON + "이상은 인챈할수 없습니다."));
				return;
			}

			if (itemId == L1ItemId.C_SCROLL_OF_ENCHANT_WEAPON) { // c-dai
				pc.getInventory().removeItem(useItem, 1);
				if (enchant_level < -6) {
					// -7이상은 할 수 없다.
					FailureEnchant(pc, l1iteminstance1);
				} else {
					SuccessEnchant(pc, l1iteminstance1, -1);
				}
			} else if (itemId == 430633) { // 장인의 무기 마법 주문서
				if (!(l1iteminstance1.getItem().getMaterial() == 9 || l1iteminstance1.getItem().getMaterial() == 18)) {
					if (enchant_level == 9) {
						int rnd = _random.nextInt(100);
						if (rnd <= Config.Master_Enchant) {
							SuccessEnchant(pc, l1iteminstance1, 1);
							if (enchant_level >= 9) { // 무기 +10 성공하였을때 알림
								L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("어느 아덴 용사 님께서 [" + l1iteminstance1.getLogName() + "] 인첸트에 성공하였습니다."));
								L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"어느 아덴 용사 님께서 [" + l1iteminstance1.getLogName() + "] 인첸트에 성공하였습니다"));
							}
						} else {
							pc.sendPackets(new S_ServerMessage(1310));
						// 인챈트: 강렬하게 빛났지만 아무 일도 없었습니다.
						}
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "$18299"));
						pc.sendPackets(new S_SystemMessage("$18299"));
						// 인챈트 +9 무기만 사용 가능
					}
				} else {
					pc.sendPackets(new S_ServerMessage(1294));
					// 인챈트: 해당 강화 주문서 사용 불가
				}
			} else if (enchant_level < safe_enchant) {
				pc.getInventory().removeItem(useItem, 1);
				SuccessEnchant(pc, l1iteminstance1, RandomELevel(l1iteminstance1, itemId));
			} else {
				pc.getInventory().removeItem(useItem, 1);

				int rnd = _random.nextInt(100) + 1;
				int enchant_chance_wepon = 0;

				if (l1iteminstance1.getItem().get_safeenchant() != 0 && enchant_level >= 5 && enchant_level < 6) {
					enchant_chance_wepon = Config.무기5;
				} else if (l1iteminstance1.getItem().get_safeenchant() != 0 && enchant_level >= 6 && enchant_level < 7) {
					enchant_chance_wepon = Config.무기6;
				} else if (l1iteminstance1.getItem().get_safeenchant() != 0 && enchant_level >= 7 && enchant_level < 8) {
					enchant_chance_wepon = Config.무기7;
				} else if (l1iteminstance1.getItem().get_safeenchant() != 0 && enchant_level >= 8 && enchant_level < 9) {
					enchant_chance_wepon = Config.무기8;
				} else if (l1iteminstance1.getItem().get_safeenchant() != 0 && enchant_level >= 9 && enchant_level <= 10) {
					enchant_chance_wepon = Config.무기9;
				} else if (l1iteminstance1.getItem().get_safeenchant() != 0 && enchant_level >= 10 && enchant_level <= 11) {
					enchant_chance_wepon = Config.무기10;
				} else if (l1iteminstance1.getItem().get_safeenchant() != 0 && enchant_level >= 11 && enchant_level <= 12) {
					enchant_chance_wepon = Config.무기11;
				} else if (l1iteminstance1.getItem().get_safeenchant() != 0 && enchant_level >= 12 && enchant_level <= 13) {
					enchant_chance_wepon = Config.무기12;
				} else if (l1iteminstance1.getItem().get_safeenchant() != 0 && enchant_level >= 13 && enchant_level <= 14) {
					enchant_chance_wepon = Config.무기13;
				} else if (l1iteminstance1.getItem().get_safeenchant() != 0 && enchant_level >= 14 && enchant_level <= 15) {
					enchant_chance_wepon = Config.무기14;
				} else {
					if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 0 && enchant_level < 1) {
						enchant_chance_wepon = Config.무기안전0;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 1 && enchant_level < 2) {
						enchant_chance_wepon = Config.무기안전1;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 2 && enchant_level < 3) {
						enchant_chance_wepon = Config.무기안전2;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 3 && enchant_level < 4) {
						enchant_chance_wepon = Config.무기안전3;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 4 && enchant_level < 5) {
						enchant_chance_wepon = Config.무기안전4;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 5 && enchant_level < 6) {
						enchant_chance_wepon = Config.무기안전5;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 6 && enchant_level < 7) {
						enchant_chance_wepon = Config.무기안전6;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 7 && enchant_level < 8) {
						enchant_chance_wepon = Config.무기안전7;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 8 && enchant_level < 9) {
						enchant_chance_wepon = Config.무기안전8;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 9 && enchant_level < 10) {
						enchant_chance_wepon = Config.무기안전9;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 10 && enchant_level < 11) {
						enchant_chance_wepon = Config.무기안전10;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 11 && enchant_level < 12) {
						enchant_chance_wepon = Config.무기안전11;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 12 && enchant_level < 13) {
						enchant_chance_wepon = Config.무기안전12;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 13 && enchant_level < 14) {
						enchant_chance_wepon = Config.무기안전13;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 14 && enchant_level < 15) {
						enchant_chance_wepon = Config.무기안전14;
					} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 15 && enchant_level < 16) {
						enchant_chance_wepon = Config.무기안전15;
					} else {
						enchant_chance_wepon = 90 / ((enchant_level - safe_enchant + 1) * 2) / (enchant_level / 9 != 0 ? 1 * 2 : 1)
								+ Config.ENCHANT_CHANCE_WEAPON;
					}
				}
				if (pc.isGm()) {
					pc.sendPackets(new S_SystemMessage("\\fY성공확률: [ " + enchant_chance_wepon + " ]"));
					pc.sendPackets(new S_SystemMessage("\\fY찬스: [ " + rnd + " ]"));
					if (enchant_chance_wepon < rnd) {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
					} else {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:성공"));
					}
				}

				// System.out.println(enchant_chance_wepon);
				// 7 == 22
				// 8 == 15
				// 9 == 5

				if (rnd < enchant_chance_wepon) {
					int randomEnchantLevel = RandomELevel(l1iteminstance1, itemId);
					SuccessEnchant(pc, l1iteminstance1, randomEnchantLevel);
				} else if (enchant_level >= 9 && rnd < (enchant_chance_wepon * 2)) {
					// \f1%0이%2과 강렬하게%1 빛났습니다만, 다행히 무사하게 살았습니다.
					pc.sendPackets(new S_ServerMessage(160, l1iteminstance1.getLogName(), "$245", "$248"));
				} else {
					FailureEnchant(pc, l1iteminstance1);
				}
			}
		}
	}
}
