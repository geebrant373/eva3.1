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

import java.text.DecimalFormat;
import java.util.Random;

import l1j.server.Config;
import l1j.server.MJTemplate.MJRnd;
import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.datatables.AccessoryEnchantInformationTable;
import l1j.server.server.datatables.AccessoryEnchantInformationTable1;
import l1j.server.server.datatables.ArmorEnchantTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Armor;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class EnchantArmor extends Enchant {

	private static Random _random = new Random();

	public EnchantArmor(L1Item item) {
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
			if (l1iteminstance1 == null || l1iteminstance1.getItem().getType2() != 2) {
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
			}
			if (l1iteminstance1.getBless() >= 128) { // 봉인템
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
			}
			int safe_enchant = ((L1Armor) l1iteminstance1.getItem()).get_safeenchant();

			if (safe_enchant < 0) { // 강화 불가
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
			}
			int armorId = l1iteminstance1.getItem().getItemId();
			int armortype = l1iteminstance1.getItem().getType();
			
			if (armorId >= 420100 && armorId <= 420115) {
				if (itemId == 90000032) {
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
			}
			if (itemId == 90000032) {
				if (armorId >= 420100 && armorId <= 420115) {
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
			}
			/** 환상의 갑옷 마법 주문서 **/
			if (armorId >= 423000 && armorId <= 423008) {
				if (itemId == L1ItemId.SCROLL_OF_ENCHANT_FANTASY_ARMOR) {
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
			}
			if (itemId == L1ItemId.SCROLL_OF_ENCHANT_FANTASY_ARMOR) {
				if (armorId >= 423000 && armorId <= 423008) {
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
			}
			/** 환상의 갑옷 마법 주문서 **/

			/** 창천의 갑옷 마법 주문서 **/
			if (armorId >= 422000 && armorId <= 422020) {
				if (itemId == L1ItemId.CHANGCHUN_ENCHANT_ARMOR_SCROLL) {
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
			}
			if (armorId >= 422000 && armorId <= 422020) {
				if (armorId >= 22041 && armorId <= 22061) {
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
			}
			/** 창천의 갑옷 마법 주문서 **/

			/**
			 * 룬 인첸 보호주문서
			 */
			if (itemId == 5370632 || itemId == 5370634) {
				if (armortype == 14) {
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
			}
			if (armortype == 14) {
				if (itemId == 5370632 || itemId == 5370634) {
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
			}
			/** 장신구 강화 주문서 */
			if (itemId == L1ItemId.ACCESSORY_ENCHANT_SCROLL || itemId == L1ItemId.ORIM_ACCESSORY_ENCHANT_SCROLL
					|| itemId == L1ItemId.ORIM_ACCESSORY_ENCHANT_SCROLL_B || itemId == 5000500 || itemId == 5000501) {
				if (armortype >= 8 && armortype <= 12) {
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
			}
			if (armortype >= 8 && armortype <= 12) {
				if (itemId == L1ItemId.ACCESSORY_ENCHANT_SCROLL || itemId == L1ItemId.ORIM_ACCESSORY_ENCHANT_SCROLL
						|| itemId == L1ItemId.ORIM_ACCESSORY_ENCHANT_SCROLL_B || itemId == 5000500 || itemId == 5000501) {
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
			}

			/** 장신구 강화 주문서 */

			int enchant_level = l1iteminstance1.getEnchantLevel();

			/** 용의 티셔츠 전용 강화 주문서 */
			if (itemId == 430042 || itemId == 1430042 || itemId == 2430042) {
				if (enchant_level >= Config.MAX_ARMOR) {
					pc.sendPackets(new S_SystemMessage("용의 티셔츠는" + Config.MAX_ARMOR + "까지만 현재 인첸트 됩니다"));
					return;
				}
				if (armorId == 91140 || armorId == 91150 || armorId == 91160 || armorId == 91170 || armorId == 9114 || armorId == 9115
						|| armorId == 9116 || armorId == 9117) {
				} else {
					pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
					return;
				}
			}

			if (armorId == 91140 || armorId == 91150 || armorId == 91160 || armorId == 91170 || armorId == 9114 || armorId == 9115 || armorId == 9116
					|| armorId == 9117) {
				if (enchant_level >= Config.MAX_ARMOR) {
					pc.sendPackets(new S_SystemMessage("용의 티셔츠는" + Config.MAX_ARMOR + "까지만 현재 인첸트 됩니다"));
					return;
				}
				if (itemId == 430042 || itemId == 1430042 || itemId == 2430042) {
				} else {
					pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
					return;
				}
			}

			/*
			 * if (armorId == 200852 || armorId == 200851 || armorId == 200853
			 * || armorId == 200854 || armorId ==30030 || armorId ==30031 ||
			 * armorId ==30032 || armorId ==30033) { if (enchant_level >=
			 * Config.MAX_ARMOR) { pc.sendPackets(new S_SystemMessage("용의 티셔츠는"+
			 * Config.MAX_ARMOR +"까지만 현재 인첸트 됩니다")); return; } if (itemId ==
			 * 430042 || itemId == 1430042 || itemId == 2430042) { } else {
			 * pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다.")); return;
			 * } }
			 */
			if (enchant_level >= Config.장신구최대인챈) {
				if (armortype >= 8 && armortype <= 12) { // 강도 : 특
					pc.sendPackets(new S_SystemMessage("\\fW장신구는 " + Config.장신구최대인챈 + "이상은 인챈트 할 수 없습니다."));
					return;
				}
			}
			if (!(armortype >= 8 && armortype <= 12)) {
				if (enchant_level >= Config.MAX_ARMOR) {
					pc.sendPackets(new S_SystemMessage("\\f:모든 방어구는 현재 +" + Config.MAX_ARMOR + "이상은 인챈할 수 없습니다."));
					return;
				}
			}
			/**
			 * 룬인첸 보호주문서
			 */
			if (itemId == 5370632 || itemId == 5370634) {
				if ((armorId == 21156)) {
					if (enchant_level >= Config.룬최대인챈) {
						pc.sendPackets(new S_SystemMessage("\\fW축복의 룬은 " + Config.룬최대인챈 + "이상은 인챈트 할 수 없습니다."));
						return;
					}
				} else {
					pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
					return;
				}

			}
			if ((armorId == 21156)) {
				if (itemId == 5370632 || itemId == 5370634) {
					if (enchant_level >= Config.룬최대인챈) {
						pc.sendPackets(new S_SystemMessage("\\fW축복의 룬은 " + Config.룬최대인챈 + "이상은 인챈트 할 수 없습니다."));
						return;
					}
				} else {
					pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
					return;
				}
			}
			/** 룸티스 강화 주문서 **/
			if (itemId == 5000500 || itemId == 5000550) {
				if ((armorId >= 427110 && armorId <= 427115)) {
					if (enchant_level >= Config.룸티스최대인챈) {
						pc.sendPackets(new S_SystemMessage("\\fW룸티스 귀걸이는 " + Config.룸티스최대인챈 + "이상은 인챈트 할 수 없습니다."));
						return;
					}
				} else {
					pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
					return;
				}

			}
			if ((armorId >= 427110 && armorId <= 427115)) {
				if (itemId == 5000500 || itemId == 5000550) {
					if (enchant_level >= Config.룸티스최대인챈) {
						pc.sendPackets(new S_SystemMessage("\\fW룸티스 귀걸이는 " + Config.룸티스최대인챈 + "이상은 인챈트 할 수 없습니다."));
						return;
					}
				} else {
					pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
					return;
				}
				/** 스냅퍼의 반지 강화 주문서 */
				if (itemId == 5000501 || itemId == 5000551) {
					if (armorId >= 427116 && armorId <= 427123) {
						if (enchant_level >= Config.스냅퍼최대인챈) {
							pc.sendPackets(new S_SystemMessage("\\fW스냅퍼반지는 " + Config.스냅퍼최대인챈 + "이상은 인챈트 할 수 없습니다."));
							return;
						}
					} else {
						pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
						return;
					}
				}
				if (armorId >= 5000501 && armorId <= 21253) {
					if (itemId == 60417 || itemId == 5000551) {
						if (enchant_level >= Config.스냅퍼최대인챈) {
							pc.sendPackets(new S_SystemMessage("\\fW스냅퍼반지는 " + Config.스냅퍼최대인챈 + "이상은 인챈트 할 수 없습니다."));
							return;
						}
					} else {
						pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
						return;
					}

				}
			}

			if (itemId == 7323 || itemId == 7324 || itemId == 430014) {
				if (armorId >= 23093 && armorId <= 23098) {
					pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
					return;
				}
			}

			if (Config.GAME_SERVER_TYPE == 1 && enchant_level >= safe_enchant + 3) {
				pc.sendPackets(new S_SystemMessage("테스트서버에서는 안전인챈+3 이상은 인챈하실수 없습니다."));
				return;
			}

			if (itemId == L1ItemId.C_SCROLL_OF_ENCHANT_ARMOR || itemId == 2430042) { // c-zel
				pc.getInventory().removeItem(useItem, 1);
				int rnd = _random.nextInt(100) + 1;
				if (safe_enchant == 0 && rnd <= 30) {
					FailureEnchant(pc, l1iteminstance1);
					return;
				}
				if (enchant_level < -6) {
					// -7이상은 할 수 없다.
					FailureEnchant(pc, l1iteminstance1);
				} else {
					SuccessEnchant(pc, l1iteminstance1, -1);
				}
			} else if (itemId == 90000032) { // 용갑 강화 주문서
				if (l1iteminstance1 != null && l1iteminstance1.getItem().getType2() == 2 && ( l1iteminstance1.getItem().getItemId() >= 420100 &&
							 l1iteminstance1.getItem().getItemId() <= 420115) ) {
					if (enchant_level >= Config.용갑) {
						pc.sendPackets(new S_SystemMessage("더 이상 인챈이 불가능합니다."));
						return;
					}
					int enchant = enchant_level < 0 ? 0
							: enchant_level >= Config.용갑인챈.length ? Config.용갑인챈.length - 1 : enchant_level;
					if (MJRnd.isWinning(100, Config.용갑인챈[enchant])) {
						SuccessEnchant(pc, l1iteminstance1, 1);
						pc.sendPackets(new S_SystemMessage("순간 강렬하게 빛을 내어 인챈트에 성공 하였습니다."));
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_SystemMessage("한 순간 강렬하게 빛났지만 인챈트에 실패 하였습니다."));
						FailureEnchant(pc, l1iteminstance1);
						pc.getInventory().removeItem(useItem, 1);
					}
				}
			} else if (itemId == 1430633) { // 장인의 방어구 마법 주문서
				if (!(l1iteminstance1.getItem().getMaterial() == 9 || l1iteminstance1.getItem().getMaterial() == 18)) {
					if (enchant_level == 8) {
						int rnd = _random.nextInt(100);
						if (rnd <= Config.Master_ArmorEnchant)
							SuccessEnchant(pc, l1iteminstance1, 1);
						else
							pc.sendPackets(new S_ServerMessage(1310));
						// 인챈트: 강렬하게 빛났지만 아무 일도 없었습니다.
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "인챈트 +8 방어구만 사용 가능"));
						pc.sendPackets(new S_SystemMessage("인챈트 +8 방어구만 사용 가능"));
						// 인챈트 +8 방어구만 사용 가능
					}
				} else {
					pc.sendPackets(new S_ServerMessage(1294));
					// 인챈트: 해당 강화 주문서 사용 불가
				}
			} else if (enchant_level < safe_enchant) {
				pc.getInventory().removeItem(useItem, 1);
				SuccessEnchant(pc, l1iteminstance1, RandomELevel(l1iteminstance1, itemId));
			} else if (itemId == L1ItemId.ACCESSORY_ENCHANT_SCROLL) {
				pc.getInventory().removeItem(useItem, 1);
				int rnd5 = _random.nextInt(100) + 1;
				int acceChance3 = AccessoryEnchantInformationTable1.getInstance().getChance(armorId, enchant_level);

				if (safe_enchant == 0 && rnd5 <= acceChance3) {
					SuccessEnchant(pc, l1iteminstance1, +1);
				} else {
					FailureEnchant(pc, l1iteminstance1);
					pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
				}
			} else if (itemId == 5000500) {
				pc.getInventory().removeItem(useItem, 1);
				int rnd1 = _random.nextInt(100) + 1;
				int acceChance = AccessoryEnchantInformationTable.getInstance().getChance(armorId, enchant_level);

				if (safe_enchant == 0 && rnd1 <= acceChance) {
					SuccessEnchant(pc, l1iteminstance1, +1);
				} else {
					FailureEnchant(pc, l1iteminstance1);
					pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
				}
			} else if (itemId == 5370632) {// 룬 보호
				pc.getInventory().removeItem(useItem, 1);
				int rnd18 = _random.nextInt(100) + 1;
				if (l1iteminstance1.getEnchantLevel() == 0) {
					if (rnd18 <= Config.EnchantChanceRun0) {
						SuccessEnchant(pc, l1iteminstance1, +1);
					} else {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
					}
				} else if (l1iteminstance1.getEnchantLevel() == 1) {
					if (rnd18 <= Config.EnchantChanceRun1) {
						SuccessEnchant(pc, l1iteminstance1, +1);
					} else {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
					}
				} else if (l1iteminstance1.getEnchantLevel() == 2) {
					if (rnd18 <= Config.EnchantChanceRun2) {
						SuccessEnchant(pc, l1iteminstance1, +1);
					} else {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
					}
				} else if (l1iteminstance1.getEnchantLevel() == 3) {
					if (rnd18 <= Config.EnchantChanceRun3) {
						SuccessEnchant(pc, l1iteminstance1, +1);
					} else {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
					}
				} else if (l1iteminstance1.getEnchantLevel() == 4) {
					if (rnd18 <= Config.EnchantChanceRun4) {
						SuccessEnchant(pc, l1iteminstance1, +1);
					} else {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
					}
				} else if (l1iteminstance1.getEnchantLevel() == 5) {
					if (rnd18 <= Config.EnchantChanceRun5) {
						SuccessEnchant(pc, l1iteminstance1, +1);
					} else {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
					}
				} else if (l1iteminstance1.getEnchantLevel() == 6) {
					if (rnd18 <= Config.EnchantChanceRun6) {
						SuccessEnchant(pc, l1iteminstance1, +1);
					} else {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
					}
				} else if (l1iteminstance1.getEnchantLevel() == 7) {
					if (rnd18 <= Config.EnchantChanceRun7) {
						SuccessEnchant(pc, l1iteminstance1, +1);
					} else {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
					}
				} else if (l1iteminstance1.getEnchantLevel() == 8) {
					if (rnd18 <= Config.EnchantChanceRun8) {
						SuccessEnchant(pc, l1iteminstance1, +1);
					} else {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
					}
				} else if (l1iteminstance1.getEnchantLevel() == 9) {
					if (rnd18 <= Config.EnchantChanceRun9) {
						SuccessEnchant(pc, l1iteminstance1, +1);
					} else {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
					}
				}
			} else if (itemId == 5370634) {// 축복받은 룬 보호
				pc.getInventory().removeItem(useItem, 1);
				int rnd18 = _random.nextInt(100) + 1;
				int acceChance18 = Config.BlassEnchantChanceRun;// AccessoryEnchantInformationTable.getInstance().getChance(armorId,enchant_level);

				if (safe_enchant == 0 && rnd18 <= acceChance18) {
					SuccessEnchant(pc, l1iteminstance1, +1);
				} else {
					pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
				}
			} else if (itemId == 5000501) {
				pc.getInventory().removeItem(useItem, 1);
				int rnd7 = _random.nextInt(100) + 1;
				int acceChance = AccessoryEnchantInformationTable.getInstance().getChance(armorId, enchant_level);

				if (safe_enchant == 0 && rnd7 <= acceChance) {
					SuccessEnchant(pc, l1iteminstance1, +1);
				} else {
					FailureEnchant(pc, l1iteminstance1);
					pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
				}

			} else if (itemId == L1ItemId.ORIM_ACCESSORY_ENCHANT_SCROLL || itemId == L1ItemId.ORIM_ACCESSORY_ENCHANT_SCROLL_B) { // 오림의신구강화주문서
				int rnd = _random.nextInt(1000000) + 1;

				if (Config.ORIM_ACCESS_ENCHANT_SCROLL_USE_LEVEL != 0) {
					if (enchant_level < Config.ORIM_ACCESS_ENCHANT_SCROLL_USE_LEVEL) {
						pc.sendPackets(new S_SystemMessage("더 이상 강화를 진행할 수 없습니다. [사용가능인챈 : +" + Config.ORIM_ACCESS_ENCHANT_SCROLL_USE_LEVEL + "이하]"));
						return;
					}
				}
				int master_enchant_scroll_chance = Config.ORIM_ACCESS_ENCHANT_SCROLL_CHANCE;
				master_enchant_scroll_chance -= (enchant_level - Config.ORIM_ACCESS_ENCHANT_SCROLL_USE_LEVEL)
						* Config.ORIM_ACCESS_ENCHANT_SCROLL_DECREASE_CHANCE;

				if (pc.isGm()) {
					DecimalFormat format = new DecimalFormat(".##");
					pc.sendPackets(new S_SystemMessage("\\fT인챈트 - 레벨: " + enchant_level + " / 성공확률: "
							+ format.format(master_enchant_scroll_chance / 10000) + " >= 랜덤: " + format.format(rnd / 10000)));
				}

				pc.getInventory().removeItem(useItem, 1);
				if (rnd < master_enchant_scroll_chance) {
					SuccessEnchant(pc, l1iteminstance1, RandomELevel(l1iteminstance1, itemId));
					pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_ENCHANTLVL);
				} else {
					if (useItem.getBless() % 128 == 0)
						pc.sendPackets(new S_ServerMessage(1310, l1iteminstance1.getLogName()));
					else {
						if (enchant_level > 0) {
							SuccessEnchant(pc, l1iteminstance1, -1);
						} else {
							pc.sendPackets(new S_ServerMessage(1310, l1iteminstance1.getLogName()));
						}
						pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_ENCHANTLVL);
					}
				}
			} else {
				pc.getInventory().removeItem(useItem, 1);
				int rnd = _random.nextInt(100) + 1;
				int enchant_chance_armor = 0;
				int enchant_level_tmp;
				if (safe_enchant == 0) { // 뼈, 브락크미스릴용 보정
					enchant_level_tmp = 2;
				} else {
					enchant_level_tmp = 1;
				}
				if (armortype >= 8 && armortype <= 12) {
					if (enchant_level >= 0 && enchant_level < 1) {
						enchant_chance_armor = Config.악세인첸1;
					} else if (enchant_level >= 1 && enchant_level < 2) {
						enchant_chance_armor = Config.악세인첸2;
					} else if (enchant_level >= 2 && enchant_level < 3) {
						enchant_chance_armor = Config.악세인첸3;
					} else if (enchant_level >= 3 && enchant_level < 4) {
						enchant_chance_armor = Config.악세인첸4;
					} else if (enchant_level >= 4 && enchant_level < 5) {
						enchant_chance_armor = Config.악세인첸5;
					} else if (enchant_level >= 5 && enchant_level < 6) {
						enchant_chance_armor = Config.악세인첸6;
					} else if (enchant_level >= 6 && enchant_level < 7) {
						enchant_chance_armor = Config.악세인첸7;
					} else if (enchant_level >= 7 && enchant_level < 8) {
						enchant_chance_armor = Config.악세인첸8;
					} else if (enchant_level >= 8) {
						enchant_chance_armor = Config.악세인첸9;
					}
				} else {
					int chance = 0;
					chance = ArmorEnchantTable.getInstance().getChance(armorId, enchant_level);
					if (chance != 0) {
						enchant_chance_armor = chance;
					} else {
						if (l1iteminstance1.getItem().get_safeenchant() == 4 && enchant_level >= 4 && enchant_level < 5) {
							enchant_chance_armor = Config.방어구4;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 4 && enchant_level >= 5 && enchant_level < 6) {
							enchant_chance_armor = Config.방어구5;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 4 && enchant_level >= 6 && enchant_level < 7) {
							enchant_chance_armor = Config.방어구6;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 4 && enchant_level >= 7 && enchant_level < 8) {
							enchant_chance_armor = Config.방어구7;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 4 && enchant_level >= 8 && enchant_level < 9) {
							enchant_chance_armor = Config.방어구8;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 4 && enchant_level >= 9 && enchant_level <= 10) {
							enchant_chance_armor = Config.방어구9;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 4 && enchant_level >= 10 && enchant_level <= 11) {
							enchant_chance_armor = Config.방어구10;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 4 && enchant_level >= 11 && enchant_level <= 12) {
							enchant_chance_armor = Config.방어구11;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 4 && enchant_level >= 12 && enchant_level <= 13) {
							enchant_chance_armor = Config.방어구12;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 4 && enchant_level >= 13 && enchant_level <= 14) {
							enchant_chance_armor = Config.방어구13;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 4 && enchant_level >= 14 && enchant_level <= 15) {
							enchant_chance_armor = Config.방어구14;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 6 && enchant_level >= 6 && enchant_level < 7) {
							enchant_chance_armor = Config.방어구안전사0;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 6 && enchant_level >= 7 && enchant_level < 8) {
							enchant_chance_armor = Config.방어구안전사1;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 6 && enchant_level >= 8 && enchant_level < 9) {
							enchant_chance_armor = Config.방어구안전사2;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 6 && enchant_level >= 9 && enchant_level < 10) {
							enchant_chance_armor = Config.방어구안전사3;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 6 && enchant_level >= 10 && enchant_level < 11) {
							enchant_chance_armor = Config.방어구안전사4;
						} else if (l1iteminstance1.getItem().get_safeenchant() == 6 && enchant_level >= 11 && enchant_level < 12) {
							enchant_chance_armor = Config.방어구안전사5;
						} else {
							if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 0 && enchant_level < 1) {
								enchant_chance_armor = Config.방어구안전0;
								// System.out.println("00");
							} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 1 && enchant_level < 2) {
								enchant_chance_armor = Config.방어구안전1;
								// System.out.println("11");
							} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 2 && enchant_level < 3) {
								enchant_chance_armor = Config.방어구안전2;
								// System.out.println("22");
							} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 3 && enchant_level < 4) {
								enchant_chance_armor = Config.방어구안전3;
								// System.out.println("33");
							} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 4 && enchant_level < 5) {
								enchant_chance_armor = Config.방어구안전4;
								// System.out.println("44");
							} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 5 && enchant_level < 6) {
								enchant_chance_armor = Config.방어구안전5;
								// System.out.println("55");
							} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 6 && enchant_level < 7) {
								enchant_chance_armor = Config.방어구안전6;
								// System.out.println("66");
							} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 7 && enchant_level < 8) {
								enchant_chance_armor = Config.방어구안전7;
								// System.out.println("77");
							} else if (l1iteminstance1.getItem().get_safeenchant() == 0 && enchant_level >= 8 && enchant_level < 9) {
								enchant_chance_armor = Config.방어구안전8;
								// System.out.println("88");
							} else {
								enchant_chance_armor = 90 / ((enchant_level - safe_enchant + 1) * 2) / (enchant_level / 7 != 0 ? 1 * 2 : 1)
										/ (enchant_level_tmp) + Config.ENCHANT_CHANCE_ARMOR;
							}
						}
					}
				}
				if (pc.isGm()) {
					pc.sendPackets(new S_SystemMessage("\\fY성공확률: [ " + enchant_chance_armor + " ]"));
					pc.sendPackets(new S_SystemMessage("\\fY찬스: [ " + rnd + " ]"));
					if (enchant_chance_armor < rnd) {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:실패"));
					} else {
						pc.sendPackets(new S_SystemMessage("\\fX인첸:성공"));
					}
				}

				if (rnd < enchant_chance_armor) {
					int randomEnchantLevel = RandomELevel(l1iteminstance1, itemId);
					SuccessEnchant(pc, l1iteminstance1, randomEnchantLevel);
				} else if (enchant_level >= 9 && rnd < (enchant_chance_armor * 2)) {
					String item_name_id = l1iteminstance1.getName();
					String pm = "";
					String msg = "";
					if (enchant_level > 0) {
						pm = "+";
					}
					msg = (new StringBuilder()).append(pm + enchant_level).append(" ").append(item_name_id).toString();
					// \f1%0이%2과 강렬하게%1 빛났습니다만, 다행히 무사하게 살았습니다.
					pc.sendPackets(new S_ServerMessage(160, msg, "$252", "$248"));
				} else {
					if (itemId == L1ItemId.ORIM_ACCESSORY_ENCHANT_SCROLL) {
						int rnddd = _random.nextInt(100);
						if (rnddd < 55) {
							String item_name_id = l1iteminstance1.getName();
							String pm = "";
							String msg = "";
							if (enchant_level > 0) {
								pm = "+";
							}
							msg = (new StringBuilder()).append(pm + enchant_level).append(" ").append(item_name_id).toString();

							pc.sendPackets(new S_ServerMessage(4056, msg));
							return;
						}
					} /**
						 * else if (itemId ==
						 * L1ItemId.ORIM_ACCESSORY_ENCHANT_SCROLL_B) { String
						 * item_name_id = l1iteminstance1.getName(); String pm =
						 * ""; String msg = ""; if (enchant_level > 0) { pm =
						 * "+"; } msg = (new StringBuilder()).append(pm +
						 * enchant_level) .append(" "
						 * ).append(item_name_id).toString(); pc.sendPackets(new
						 * S_ServerMessage(4056, msg)); return; }
						 */
					else if (itemId == 600406) {
						int rnddd = _random.nextInt(100);
						if (rnddd < 55) {
							String item_name_id = l1iteminstance1.getName();
							String pm = "";
							String msg = "";
							if (enchant_level > 0) {
								pm = "+";
							}
							msg = (new StringBuilder()).append(pm + enchant_level).append(" ").append(item_name_id).toString();

							pc.sendPackets(new S_ServerMessage(4056, msg));
							return;
						}
					} else if (itemId == 5000550) {
						String item_name_id = l1iteminstance1.getName();
						String pm = "";
						String msg = "";
						if (enchant_level > 0) {
							pm = "+";
						}
						msg = (new StringBuilder()).append(pm + enchant_level).append(" ").append(item_name_id).toString();
						pc.sendPackets(new S_ServerMessage(4056, msg));
						return;
					}
					// 스냅퍼 보호 주문서
					else if (itemId == 5000551) {
						String item_name_id = l1iteminstance1.getName();
						String pm = "";
						String msg = "";
						if (enchant_level > 0) {
							pm = "+";
						}
						msg = (new StringBuilder()).append(pm + enchant_level).append(" ").append(item_name_id).toString();
						pc.sendPackets(new S_ServerMessage(4056, msg));
						return;
					}
					FailureEnchant(pc, l1iteminstance1);
				}
			}
		}
	}

}
