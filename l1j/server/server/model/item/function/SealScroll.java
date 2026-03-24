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
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ItemStatus;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class SealScroll extends L1ItemInstance {

	public SealScroll(L1Item item) {
		super(item);
	}

	private static Random _random = new Random();

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = this.getItemId();
			L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(packet.readD());
			int targetItem = l1iteminstance1.getItemId();
			if (itemId == 50020) { // 봉인줌서
				if (l1iteminstance1.getBless() >= 0 && l1iteminstance1.getBless() <= 3) {
					int Bless = 0;
					switch (l1iteminstance1.getBless()) {
					case 0:
						Bless = 128;
						break; // 축
					case 1:
						Bless = 129;
						break; // 보통
					case 2:
						Bless = 130;
						break; // 저주
					case 3:
						Bless = 131;
						break; // 미확인
					}
					l1iteminstance1.setBless(Bless);
					pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_BLESS);
					pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_BLESS);
					pc.getInventory().removeItem(useItem, 1);
				} else
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
																// 않았습니다.
			} else if (itemId == 45110) { // 봉인해제줌서
				if (l1iteminstance1.getBless() >= 1 && l1iteminstance1.getBless() <= 3) {
					int Bless = 0;
					switch (l1iteminstance1.getBless()) {
					case 1:
						Bless = 0;
						break; // 보통 아이템
					case 2:
						Bless = 0;
						break; // 저주 아이템
					case 3:
						Bless = 0;
						break; // 미확인 아이템
					}
					int rnd = _random.nextInt(100) + 1;
					if (rnd < 10) {
						l1iteminstance1.setBless(Bless);
						switch (l1iteminstance1.getItem().getType2()) {
						case 1:
							pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_BLESS);
							pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_BLESS);
							pc.getInventory().removeItem(useItem, 1);
							break;
						case 2:
							pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_BLESS);
							pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_BLESS);
							pc.getInventory().removeItem(useItem, 1);
							break;
						}
					} else {
						pc.getInventory().removeItem(useItem, 1);
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				}
			} else if (itemId == 50021) { // 봉인해제줌서
				if (l1iteminstance1.getBless() >= 128 && l1iteminstance1.getBless() <= 131) {
					int Bless = 0;
					switch (l1iteminstance1.getBless()) {
					case 128:
						Bless = 0;
						break;
					case 129:
						Bless = 1;
						break;
					case 130:
						Bless = 2;
						break;
					case 131:
						Bless = 3;
						break;
					}
					l1iteminstance1.setBless(Bless);
					pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_BLESS);
					pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_BLESS);
					pc.getInventory().removeItem(useItem, 1);
				} else
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
																// 않았습니다.
			} else if (itemId == 501003) { // 축복받은 주문서

				if (l1iteminstance1 == null || l1iteminstance1.getItem().getType2() == 0) {
					pc.sendPackets(new S_SystemMessage("\\fY무기와 방어구만 사용가능합니다."));
					return;
				}
				// 1:helm, 2:armor, 3:T, 4:cloak, 5:glove, 6:boots, 7:shield, 8:amulet,9:ring, 10:belt, 11:ring2, 12:earring
				if (l1iteminstance1.getItem().getType2() == 2) {
					if (l1iteminstance1.getItem().getType() >= 9 && l1iteminstance1.getItem().getType() <= 13) {
						pc.sendPackets(new S_SystemMessage("귀걸이,벨트,가더,반지에는 축복 주문서를 사용할 수 없습니다."));
						return;
					}
				}
				if (l1iteminstance1.getItem().getType2() == 1) {
					if (l1iteminstance1.getEnchantLevel() <= 7) {
						pc.sendPackets(new S_SystemMessage("\\fY+8인첸부터 축복 주문서 사용 가능합니다."));
						return;
					}
				} else if (l1iteminstance1.getItem().getType2() == 2) {
					if (l1iteminstance1.getItem().get_safeenchant() == 0) {
						if (l1iteminstance1.getEnchantLevel() <= 2) {
							pc.sendPackets(new S_SystemMessage("\\fY+3인첸부터 축복 주문서 사용 가능합니다."));
							return;
						}
					} else if (l1iteminstance1.getItem().get_safeenchant() == 4) {
						if (l1iteminstance1.getEnchantLevel() <= 6) {
							pc.sendPackets(new S_SystemMessage("\\fY+7인첸부터 축복 주문서 사용 가능합니다."));
							return;
						}
					} else if (l1iteminstance1.getItem().get_safeenchant() == 6) {
						if (l1iteminstance1.getEnchantLevel() <= 7) {
							pc.sendPackets(new S_SystemMessage("\\fY+8인첸부터 축복 주문서 사용 가능합니다."));
							return;
						}
					}
				}
				if (l1iteminstance1.getBless() >= 128) { // 이미축일경우
					pc.sendPackets(new S_ServerMessage(79)); // 아무것도 일어나지 않았습니다.
					return;
				}
				
				int ran = _random.nextInt(100) + 1;
				if (ran <= Config.weaponbless) {// 10프로 확률로 축복인첸에 성공한다.
					if (l1iteminstance1.getItem().getType2() == 1) {
						switch(l1iteminstance1.getEnchantLevel()) {
						case 7:
							l1iteminstance1.set_bless_level(2);
							break;
						case 8:
							l1iteminstance1.set_bless_level(3);
							break;
						case 9:
							l1iteminstance1.set_bless_level(4);
							break;
						case 10:
							l1iteminstance1.set_bless_level(5);
							break;
						}
					} else if (l1iteminstance1.getItem().getType2() == 2) {
						if (l1iteminstance1.getItem().get_safeenchant() == 0) {
							switch(l1iteminstance1.getEnchantLevel()) {
							case 3:
								l1iteminstance1.set_bless_level(1);
								break;
							case 4:
								l1iteminstance1.set_bless_level(2);
								break;
							case 5:
								l1iteminstance1.set_bless_level(3);
								break;
							}
						} else if (l1iteminstance1.getItem().get_safeenchant() == 4) {
							switch(l1iteminstance1.getEnchantLevel()) {
							case 7:
								l1iteminstance1.set_bless_level(1);
								break;
							case 8:
								l1iteminstance1.set_bless_level(2);
								break;
							case 9:
								l1iteminstance1.set_bless_level(3);
								break;
							}
						} else if (l1iteminstance1.getItem().get_safeenchant() == 6) {
							switch(l1iteminstance1.getEnchantLevel()) {
							case 8:
								l1iteminstance1.set_bless_level(1);
								break;
							case 9:
								l1iteminstance1.set_bless_level(2);
								break;
							case 10:
								l1iteminstance1.set_bless_level(3);
								break;
							}
						}
					}
					l1iteminstance1.setBless(0);
					pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_BLESS);
					pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_BLESS);
					pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
					pc.getInventory().removeItem(useItem, 1);
					pc.sendPackets(new S_SystemMessage("\\fY축복 받은 주문서 인첸에 성공하였습니다."));
				} else {
					pc.sendPackets(new S_SystemMessage("\\fY축복 받은 주문서 인첸에 실패하였습니다."));
					pc.getInventory().removeItem(useItem, 1);
				}
			} else if (itemId == 90000034) { // 인형 축복받은 주문서
				if (targetItem == 743 || targetItem == 744 || targetItem == 41248 || targetItem == 41249 || targetItem == 41250
						 || targetItem == 41916 || targetItem == 430000 || targetItem == 430001 || targetItem == 430002
						 || targetItem == 430003 || targetItem == 430004 || targetItem == 430500 || targetItem == 430505
						 || targetItem == 430506 || targetItem == 447016 || targetItem == 4370598 || targetItem == 4370599
						 || targetItem == 4370600 || targetItem == 5370600 || targetItem == 5370601 || targetItem == 5370602
						 || targetItem == 5370603 || targetItem == 5370604 || targetItem == 5370605 || targetItem == 5370606
						 || targetItem == 5370607 || targetItem == 5370608) {
					if (l1iteminstance1.getBless() == 0) { // 이미축일경우
						pc.sendPackets(new S_ServerMessage(79)); // 아무것도 일어나지 않았습니다.
						return;
					}
					
					int ran = _random.nextInt(100) + 1;
					if (ran <= Config.인형축복) {
						l1iteminstance1.setBless(0);
						pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_BLESS);
						pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_BLESS);
						pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
						pc.getInventory().removeItem(useItem, 1);
						pc.sendPackets(new S_SystemMessage("\\fY인형에 축복의 기운이 스며듭니다."));
					} else {
						pc.sendPackets(new S_SystemMessage("\\fY축복이 실패되었습니다."));
						pc.getInventory().removeItem(useItem, 1);
					}
				} else {
					pc.sendPackets(new S_SystemMessage("\\fY인형에만 사용 할 수 있습니다."));
				}
			}
		}
	}
}
