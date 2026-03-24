/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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

import static l1j.server.server.model.skill.L1SkillId.ERASE_MAGIC;
import static l1j.server.server.model.skill.L1SkillId.EXP_POTION;
import static l1j.server.server.model.skill.L1SkillId.LIFE_MAAN;
import static l1j.server.server.model.skill.L1SkillId.POLLUTE_WATER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL2;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL3;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_MITHRIL_POWDER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_WATER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_WATER_OF_EVA;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.ExchangeItem.ExchangeItemInfo;
import l1j.server.ExchangeItem.ExchangeItemLoader;
import l1j.server.GameSystem.CrockSystem;
import l1j.server.GameSystem.Antaras.AntarasRaidSystem;
import l1j.server.MJTemplate.MJRnd;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ForceItem;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MapFixKeyTable;
import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.WantedTeleportTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1GuardianInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.item.function.BluePotion;
import l1j.server.server.model.item.function.BravePotion;
import l1j.server.server.model.item.function.WisdomPotion;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_Board;
import l1j.server.server.serverpackets.S_ChangeName;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharTitle;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DRAGONPERL;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_Drawal;
import l1j.server.server.serverpackets.S_DropList;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_Letter;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SearchAdenaTrade;
import l1j.server.server.serverpackets.S_SearchAdenaTrade3;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShowPolyList;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Sound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Teleport;
import l1j.server.server.serverpackets.S_UseMap;
import l1j.server.server.serverpackets.S_UserCommands2;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.templates.L1Drop;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.L1SpawnUtil;
import l1j.server.server.utils.SQLUtil;
import server.LineageClient;

//Referenced classes of package l1j.server.server.clientpackets:
//ClientBasePacket

public class C_ItemUSe extends ClientBasePacket {

	private static final String C_ITEM_USE = "[C] C_ItemUSe";
	private static Logger _log = Logger.getLogger(C_ItemUSe.class.getName());

	private static Random _random = new Random();

	Calendar currentDate = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd h:mm:ss a");
	String time = dateFormat.format(currentDate.getTime());

	@SuppressWarnings("unused")
	public C_ItemUSe(byte abyte0[], LineageClient client) throws Exception {
		super(abyte0);
		int itemObjid = readD();

		L1PcInstance pc = client.getActiveChar();
		if (pc == null || pc.isGhost() || isTwoLogin(pc)) {
			return;
		}
		if (pc.getMapId() == 701) {
			return;
		}
		
		L1ItemInstance l1iteminstance = pc.getInventory().getItem(itemObjid);
		L1ItemInstance useItem = pc.getInventory().getItem(itemObjid);
		final ExchangeItemInfo ex_Info = ExchangeItemLoader.getInstance()
				.getExchangeItemInfo(l1iteminstance.getItemId());
		if (ex_Info != null) {
			if (!pc.getInventory().consumeItem(l1iteminstance, ex_Info.get_use_count())) {
				pc.sendPackets(
						String.format("\ubcc0\ud658\ud560 %s\uc758 \uac2f\uc218\uac00 \ubaa8\uc790\ub78d\ub2c8\ub2e4.",
								ex_Info.get_item_name()));
				return;
			}
			if (MJRnd.isWinning(1000000, ex_Info.calc_probability(pc))) {
				final L1ItemInstance ex_item = ItemTable.getInstance().createItem(ex_Info.get_success_item_id());
				ex_item.setCount(ex_Info.get_success_item_count());
				pc.getInventory().storeTradeItem(ex_item);
				pc.sendPackets(String.format(
						"%s\uc758 \ubaa8\uc2b5\uc774 \ucc2c\ub780\ud788 \ube5b\ub098\uba70 \ubcc0\ud658\uc5d0 \uc131\uacf5\ud558\uc5ec %s\ub97c \ud68d\ub4dd \ud558\uc600\uc2b5\ub2c8\ub2e4.",
						ex_Info.get_item_name(), ex_Info.get_success_item_name()));
				return;
			}
			pc.sendPackets(String.format(
					"%s\uc758 \ubaa8\uc2b5\uc774 \uc5b4\ub461\uac8c \ube5b\ub098\uba70 \ubcc0\ud658\uc5d0 \uc2e4\ud328\ud558\uc5ec \uc99d\ubc1c \ud558\uc600\uc2b5\ub2c8\ub2e4.",
					ex_Info.get_item_name()));
		}
		if (l1iteminstance == null) {
			return;
		}
		
		if (useItem.getItem().getUseType() == -1) { // none:사용할 수 없는 아이템
			pc.sendPackets(new S_ServerMessage(74, useItem.getLogName())); // \f1%0은 사용할 수 없습니다.
			return;
		}
		if (pc.isTeleport()) { // 텔레포트 처리중
			return;
		}
		
		// 존재버그 관련 추가
		L1PcInstance jonje = L1World.getInstance().getPlayer(pc.getName());
		if (jonje == null && pc.getAccessLevel() != 200) {
			pc.sendPackets(new S_SystemMessage("존재버그 강제종료! 재접속하세요"));
			client.kick();
			return;
		}

		if (!pc.getMap().isUsableItem()) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1 여기에서는 사용할 수 없습니다.
			return;
		}

		int itemId;
		try {
			itemId = useItem.getItem().getItemId();
		} catch (Exception e) {
			return;
		}

		if (useItem.isWorking()) { // 키워드미인증시 젤데이사용금지 220108수정
			if (pc.getCurrentHp() > 0) {
				useItem.clickItem(pc, this);
			}
			return;
		}
		
		int l = 0;
		int spellsc_objid = 0;
		int spellsc_x = 0;
		int spellsc_y = 0;

		int use_type = useItem.getItem().getUseType();
		if (itemId == 41029 // 소환공의 조각
				|| itemId == 40317 || itemId == 41036 || itemId == L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_DOWN
				|| itemId == L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_DOWN
				|| itemId == L1ItemId.LOWER_TIKAL_PRESENT_PIECE_DOWN
				|| itemId == L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_DOWN || itemId == L1ItemId.TIMECRACK_CORE
				|| itemId == 45108 || itemId == 40964 || itemId == 41030 || itemId == 40925 || itemId == 40926
				|| itemId == 40927 // 정화·신비적인 일부
				|| itemId == 40928 || itemId == 1999 || itemId == 40929) {
			l = readD();
		} else if (use_type == 30 || itemId == 40870 || itemId == 40879) { // spell_buff
			spellsc_objid = readD();
		} else if (use_type == 5 || use_type == 17) { // spell_long spell_short
			spellsc_objid = readD();
			spellsc_x = readH();
			spellsc_y = readH();
		} else {
			l = readC();
		}

		if (pc.getCurrentHp() > 0) {
			int delay_id = 0;
			if (useItem.getItem().getType2() == 0) { // 종별：그 외의 아이템
				delay_id = ((L1EtcItem) useItem.getItem()).get_delayid();
			}
			if (delay_id != 0) { // 지연 설정 있어
				if (pc.hasItemDelay(delay_id) == true) {
					return;
				}
			}
			
			// 재사용 체크
			boolean isDelayEffect = false;
						
			if (l1iteminstance.getItem().getType2() == 0) {
				int delayEffect = ((L1EtcItem) l1iteminstance.getItem()).get_delayEffect();
				if (delayEffect > 0) {
					isDelayEffect = true;
					Timestamp lastUsed = l1iteminstance.getLastUsed();
					if (lastUsed != null) {
						Calendar cal = Calendar.getInstance();
						if ((cal.getTimeInMillis() - lastUsed.getTime()) / 1000 <= delayEffect) {
							pc.sendPackets(
									new S_SystemMessage(
											((delayEffect - (cal.getTimeInMillis() - lastUsed.getTime()) / 1000) / 60)
													+ "분 "
													+ ((delayEffect
															- (cal.getTimeInMillis() - lastUsed.getTime()) / 1000) % 60)
									+ "초 후에 사용할 수 있습니다. "));
							return;
						}
					}
				}
			}
			
			L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(l);
			
			_log.finest("request item use (obj) = " + itemObjid + " action = " + l);
			if (useItem.getItem().getType2() == 0) { // 종별：그 외의 아이템
				int item_minlvl = ((L1EtcItem) useItem.getItem()).getMinLevel();
				int item_maxlvl = ((L1EtcItem) useItem.getItem()).getMaxLevel();

				if (item_minlvl != 0 && item_minlvl > pc.getLevel() && !pc.isGm()) {
					pc.sendPackets(new S_ServerMessage(318, String.valueOf(item_minlvl)));
					// 이 아이템은%0레벨 이상이 되지 않으면 사용할 수 없습니다.
					return;
				} else if (item_maxlvl != 0 && item_maxlvl < pc.getLevel() && !pc.isGm()) {
					pc.sendPackets(new S_ServerMessage(673, String.valueOf(item_maxlvl)));
					// 이 아이템은%d레벨 이상만 사용할 수 있습니다.
					return;
				}
				
				if ((itemId == 40576 && !pc.isElf()) || (itemId == 40577 && !pc.isWizard()) // 영혼의 결정의 파편(흑)
						|| (itemId == 40578 && !pc.isKnight())) { // 영혼의 결정의
																	// 파편(빨강)
					pc.sendPackets(new S_ServerMessage(264)); // \f1당신의 클래스에서는 이
																// 아이템은 사용할 수
																// 없습니다.
					return;
				}
				if (itemId == 40003) {
					for (L1ItemInstance lightItem : pc.getInventory().getItems()) {
						if (lightItem.getItem().getItemId() == 40002) {
							lightItem.setRemainingTime(useItem.getItem().getLightFuel());
							pc.sendPackets(new S_ItemName(lightItem));
							pc.sendPackets(new S_ServerMessage(230));
							break;
						}
					}
					pc.getInventory().removeItem(useItem, 1);
					/** 여기부터 추가 하기 */
				} else if (itemId == 252531) {
					L1Object target = L1World.getInstance().findObject(spellsc_objid);
					if (target != null) {
						if (target instanceof L1MonsterInstance) {
							L1MonsterInstance mon = (L1MonsterInstance) target;
							ArrayList<L1Drop> dropList = DropTable.getInstance().getDropList(mon.getNpcId());
							if (dropList == null) {
								pc.sendPackets(new S_SystemMessage("아이템 드랍정보가 존재하지 않습니다."));
								return;
							}

							pc._자동판매초이스리스트.clear();

							String droptext = "";
							for (int i = 0; i < dropList.size(); i++) {
								L1Drop drop = dropList.get(i);
								if (drop != null) {
									
									int itemid = drop.getItemid();
									L1Item temp = ItemTable.getInstance().getTemplate(itemid);
									if (temp != null) {
										L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
										if (item != null) {
											pc._자동판매초이스리스트.add(item);
										}
									}
								}
							}
							pc.sendPackets(new S_DropList(pc));
						} else {
							pc.sendPackets(new S_SystemMessage("몬스터에게만 사용 가능합니다."));
						}
					}
				} else if (itemId == 430027) {
					pc.sendPackets(new S_Letter(l1iteminstance, true));
					l1iteminstance.setItemId(itemId + 1);
					pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_ITEMID);
					pc.getInventory().saveItem(l1iteminstance, L1PcInventory.COL_ITEMID);
				} else if (itemId == 430028) {
					pc.sendPackets(new S_Letter(l1iteminstance, true));
					l1iteminstance.setItemId(itemId + 1);
					pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_ITEMID);
					pc.getInventory().saveItem(l1iteminstance, L1PcInventory.COL_ITEMID);
				} else if (itemId == 400074) {
					if (pc.getInventory().checkItem(40308, 100000000)) {
						pc.getInventory().consumeItem(40308, 100000000);
						pc.getInventory().storeItem(400075, 1);
						pc.sendPackets(new S_SystemMessage("\\fY1억아데나 수표를 얻었습니다."));
					} else {
						pc.sendPackets(new S_SystemMessage("100,000,000원 아데나가 부족합니다."));
					}
				} else if (itemId == 400075) {
					pc.getInventory().storeItem(40308, 100000000);
					pc.sendPackets(new S_SystemMessage("1억 아데나로 환전 되었습니다."));
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 45073) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "telbook7"));

				} else if (itemId == 50135) { // 이름변경
					pc.sendPackets(new S_SystemMessage(".이름변경 변경할닉네임"));
				} else if (itemId == 100903 || itemId == 100904) { // 캐릭터교환증표
					// 설명서
					pc.sendPackets(new S_UserCommands2(1), true);

				} else if (itemId == 45072) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "telbook6"));
				} else if (itemId == 45070) {
					pc.setDeaths(0);
					pc.setKills(0);
					pc.sendPackets(new S_SystemMessage("\\fY캐릭터 전적이 초기화 되었습니다."));
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 50111) { // 캐릭교환 인형
					if (useItem.getTradeCha() != 0) {
						pc.sendPackets(new S_Message_YN(2360, pc.getName()));
						pc.TradeChaItem = useItem;
					} else {
						pc.sendPackets(new S_SystemMessage(".캐릭등록 [캐릭명] 명령어를 사용해주세요."));
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, ".캐릭등록 [캐릭명] 명령어를 사용해주세요."));
						pc.sendPackets(new S_SystemMessage(".캐릭등록 [캐릭명] 명령어를 사용해주세요."));
					}
				} else if (itemId == 45097) {
					/*for (L1Object obj : L1World.getInstance().getObject()) {
						if (obj instanceof L1NpcInstance) {
							L1NpcInstance npc = (L1NpcInstance) obj;
							if (npc.getNpcId() == 900009643) { 
								pc.sendPackets(new S_Board(npc));
								break;
							}
						}
					}*/
					ArrayList<String> onelevelresult = new ArrayList<String>();
					onelevelresult.add("0");
					onelevelresult.add("2");
					onelevelresult.add("3");
					onelevelresult.add("4");
					onelevelresult.add("5");
					onelevelresult.add("6");
					onelevelresult.add("7");
					onelevelresult.add("8");
					onelevelresult.add("9");
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "serverInfo", (String[]) onelevelresult.toArray(new String[onelevelresult.size()])));
				} else if (itemId == 5370629) {
					{
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tradeinfo"));// 구매판매매뉴얼
					}
				} else if (itemId == 5370630) {
					{
						pc.sendPackets(new S_SearchAdenaTrade3(pc));
					}
				} else if (itemId == 5370631) {
					{
						pc.sendPackets(new S_SearchAdenaTrade(pc));
					}
				}

				else if (itemId == 45109) { // 오만의 탑 이동 기억책

					pc.sendPackets(new S_ShowPolyList(pc.getId(), "jinlist"));
				} else if (itemId == 430634) { // 오만의 탑 이동 기억책
					pc.sendPackets(new S_ShowPolyList(pc.getId(), "jinlist"));
				} else if (itemId == 52064) { // 오만의 탑 이동 기억책
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "telbook3"));
				} else if (itemId == 52065) { // 조우의 이동 기억책
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "telbook4"));
				} else if (itemId == 52063) { // 조우의 이동 기억책
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "book01"));
				} else if (itemId == 45071) { // 보스 이동 기억책
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "telbook7"));
				} else if (itemId == 90000007) {
					pc.getInventory().storeItem(41248, 1);
					pc.getInventory().storeItem(41250, 1);
					pc.getInventory().storeItem(41916, 1);
					pc.getInventory().storeItem(430000, 1);
					pc.getInventory().storeItem(430002, 1);
					pc.getInventory().storeItem(430003, 1);
					pc.getInventory().storeItem(430004, 1);
					pc.getInventory().storeItem(430505, 1);
					pc.getInventory().storeItem(4370599, 1);
					pc.getInventory().storeItem(4370600, 1);
					pc.getInventory().storeItem(744, 1);
					pc.getInventory().storeItem(743, 1);
					pc.getInventory().storeItem(447016, 1);
					pc.getInventory().storeItem(5370600, 1);
					pc.getInventory().storeItem(5370601, 1);
					pc.getInventory().storeItem(5370602, 1);
					pc.getInventory().storeItem(5370603, 1);
					pc.getInventory().storeItem(5370604, 1);
				} else if (itemId == 437011) {
					useDragonPearl(pc);
					pc.getInventory().removeItem(useItem, 1);
					pc.sendPackets(new S_ServerMessage(1065));
				} else if (itemId == 1437011) {
					useDragonPearl(pc);
					pc.sendPackets(new S_ServerMessage(1065));
				} else if (itemId == 45068) {
					if (pc.getSkillEffectTimerSet().hasSkillEffect(71) == true) { // 디케이포션 상태
						pc.sendPackets(new S_ServerMessage(698, "")); // 마력에 의해 아무것도 마실 수가 없습니다.
						return;
					}
					if (pc.getSkillEffectTimerSet().hasSkillEffect(20081)) {
						pc.getSkillEffectTimerSet().removeSkillEffect(20081);
					}
					if (pc.getSkillEffectTimerSet().hasSkillEffect(7289)) {
						pc.getSkillEffectTimerSet().removeSkillEffect(7289);
					}
					pc.getSkillEffectTimerSet().setSkillEffect(7289, 1800 * 1000);
					pc.sendPackets(new S_SkillSound(pc.getId(), 7289));
					pc.sendPackets(new S_SkillSound(pc.getId(), 195));
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 45104) { // 카베
					L1World.getInstance().broadcastPacketToAll(
							new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "어느 아덴 용사가 월드보스상자에서 기술서(카운터 배리어)를 획득하였습니다. "));
					L1World.getInstance().broadcastPacketToAll(
							new S_SystemMessage("어느 아덴 용사가 월드보스상자에서 기술서(카운터 배리어)를 획득하였습니다. "));
				} else if (itemId == 45105) { // 디스
					L1World.getInstance().broadcastPacketToAll(
							new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "어느 아덴 용사가 월드보스상자에서 마법서(디스인티그레이트)를 획득하였습니다. "));
					L1World.getInstance().broadcastPacketToAll(
							new S_SystemMessage("어느 아덴 용사가 월드보스상자에서 마법서(디스인티그레이트)를 획득하였습니다. "));
				} else if (itemId == 45106) { // 아머
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
							"어느 아덴 용사가 월드보스상자에서 흑정령의 수정(아머 브레이크)를 획득하였습니다. "));
					L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("어느 아덴 용사가 월드보스상자에서 흑정령의 수정(아머 브레이크)를 획득하였습니다. "));
				} else if (itemId == 61) { // 집행검
					L1World.getInstance().broadcastPacketToAll(
							new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "어느 아덴월드 용사가 집행검을 손에 넣었습니다. "));
					L1World.getInstance().broadcastPacketToAll(
							new S_SystemMessage("어느 아덴월드 용사가 집행검을 손에 넣었습니다. "));
				} else if (itemId == 134) { // 수결지
					L1World.getInstance().broadcastPacketToAll(
							new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "어느 아덴월드 용사가 수정 결정체 지팡이를 손에 넣었습니다. "));
					L1World.getInstance().broadcastPacketToAll(
							new S_SystemMessage("어느 아덴월드 용사가 수정 결정체 지팡이를 손에 넣었습니다. "));
				} else if (itemId == 86) { // 붉이
					L1World.getInstance().broadcastPacketToAll(
							new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "어느 아덴월드 용사가 붉은 그림자의 이도류를 손에 넣었습니다. "));
					L1World.getInstance().broadcastPacketToAll(
							new S_SystemMessage("어느 아덴월드 용사가 붉은 그림자의 이도류를 손에 넣었습니다. "));
				} else if (itemId == 12) { // 바칼
					L1World.getInstance().broadcastPacketToAll(
							new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "어느 아덴월드 용사가 바람칼날의 단검을 손에 넣었습니다. "));
					L1World.getInstance().broadcastPacketToAll(
							new S_SystemMessage("어느 아덴월드 용사가 바람칼날의 단검을 손에 넣었습니다. "));
				} else if (itemId == 160) { // 야수왕의 크로우
					L1World.getInstance().broadcastPacketToAll(
							new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "어느 아덴월드 용사가 야수왕의 크로우을 손에 넣었습니다. "));
					L1World.getInstance().broadcastPacketToAll(
							new S_SystemMessage("어느 아덴월드 용사가 야수왕의 크로우을 손에 넣었습니다. "));
				} else if (itemId == 46176) { // 고정 버프 코인
					if (pc.getMemberShip() >= 1) {
						pc.sendPackets(new S_SystemMessage("이미 고정 버프를 사용중 입니다."));
						return;
					} else {
						pc.getAC().addAc(-2);
						pc.addMaxHp(30);
						pc.addMaxMp(30);
						pc.setMemberShip(1);
						pc.sendPackets(new S_SkillSound(pc.getId(), 8948));
						pc.sendPackets(new S_OwnCharStatus(pc));
					}
				} else if (itemId == 46172) { // VIP
					if (pc.getVipLevel() >= 2 && pc.getWanted() < 1) {
						pc.sendPackets(new S_SystemMessage("이미 VIP 버프를 사용중 입니다."));
						return;
					} else {
						pc.setVipLevel(2);
						pc.getAC().addAc(-3);
						pc.addMaxHp(50);
						pc.addMaxMp(50);
						pc.addDmgup(3);
						pc.addHitup(3);
						pc.addBowDmgup(3);
						pc.getResistance().addMr(10);
						pc.getAbility().addSp(2);
						pc.sendPackets(new S_SkillSound(pc.getId(), 8943));
						pc.sendPackets(new S_OwnCharStatus(pc));
						pc.sendPackets(new S_SPMR(pc));
					}
				} else if (itemId == 42017) {
					if (pc.getWanted() ==0  && WantedTeleportTable.getInstance().isWantedTeleportMap(303)) {
						pc.sendPackets(new S_SystemMessage("\\fT수배 상태에서만 사냥터로 이동 가능합니다."));
						return;
					}
					if (pc.getAccount().getDreamIslandTime() >= Config.몽섬시간) {
						pc.sendPackets(new S_SystemMessage("몽환의 섬 사냥시간이 소진되었습니다."));
						return;
					}
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					}
					L1Teleport.teleport(pc, 32636, 32818, (short) 303, pc.getMoveState().getHeading(), true);
					pc.sendPackets(new S_SystemMessage("몽환의 섬으로 이동하였습니다."));
					doTeleport(pc);
					pc.getInventory().consumeItem(42017, 1);
				} else if (itemId == 45096) { // 코마 버프
					int[] allBuffSkill = { 1025 };
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
								L1SkillUse.TYPE_GMBUFF);
					}
					pc.getInventory().removeItem(useItem, 1);
					return;
				} else if (itemId == 430030) {// 자동칼질
					if (!pc.autoattack) {
						pc.autoattack = true;
						pc.sendPackets(new S_SystemMessage("자동칼질이 활성화 되었습니다."));
					} else {
						pc.autoattack = false;
						pc.Stoptatat();
						pc.sendPackets(new S_SystemMessage("자동칼질이 중지 되었습니다."));
					}
				} else if (itemId == 42039) {// 오만1층
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						// pc.sendPackets(new S_ServerMessage(647));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					}

					else {
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 32800, 32800, (short) 110, pc.getMoveState().getHeading(), true);
						pc.getInventory().removeItem(useItem, 1);
					}
				} else if (itemId == 42038) { // 오만2층
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					} else {
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 32800, 32800, (short) 120, pc.getMoveState().getHeading(), true);
						pc.getInventory().removeItem(useItem, 1);
					}
				} else if (itemId == 42037) { // 오만3층
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					} else {
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 32800, 32800, (short) 130, pc.getMoveState().getHeading(), true);
						pc.getInventory().removeItem(useItem, 1);
					}
				} else if (itemId == 42036) { // 오만4층
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					} else {
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 32800, 32800, (short) 140, pc.getMoveState().getHeading(), true);
						pc.getInventory().removeItem(useItem, 1);
					}
				} else if (itemId == 42035) { // 오만5층
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					} else {
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 32796, 32796, (short) 150, pc.getMoveState().getHeading(), true);
						pc.getInventory().removeItem(useItem, 1);
					}
				} else if (itemId == 42033) { // 오만6층
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					} else {
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 32720, 32821, (short) 160, pc.getMoveState().getHeading(), true);
						pc.getInventory().removeItem(useItem, 1);
					}
				} else if (itemId == 42032) { // 오만7층
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					} else {
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 32720, 32821, (short) 170, pc.getMoveState().getHeading(), true);
						pc.getInventory().removeItem(useItem, 1);
					}
				} else if (itemId == 42031) { // 오만8층
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					} else {
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 32724, 32822, (short) 180, pc.getMoveState().getHeading(), true);
						pc.getInventory().removeItem(useItem, 1);
					}
				} else if (itemId == 42030) { // 오만9층
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					} else {
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 32722, 32827, (short) 190, pc.getMoveState().getHeading(), true);
						pc.getInventory().removeItem(useItem, 1);
					}
				} else if (itemId == 42029) { // 오만100층
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					} else {
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 32731, 32856, (short) 200, pc.getMoveState().getHeading(), true);
						pc.getInventory().removeItem(useItem, 1);
					}
				} else if (itemId == 42013) { // 라스타바드
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					} else {
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 32722, 32795, (short) 536, pc.getMoveState().getHeading(), true);
						// 32722, 32795, (short) 536변경 4층입구
						// 32725,32851, (short) 453기존1층 마수군왕집무실
						pc.getInventory().removeItem(useItem, 1);
					}
				} else if (itemId == 46175) { // 기란 마을 귀환 부적
					if (pc.getMapId() == 70 || pc.getMapId() == 200 || pc.getMapId() == 5153) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					}
					int loc = (_random.nextInt(7));
					switch (loc) {
					case 0:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33442, 32818, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 1:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33425, 32825, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 2:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33419, 32809, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 3:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33426, 32799, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 4:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33435, 32802, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 5:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33447, 32811, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 6:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33429, 32808, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 7:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33444, 32827, (short) 4, pc.getMoveState().getHeading(), true);
					default:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33419, 32809, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					}
				} else if (itemId == 5370628) { // 오렌 마을 귀환 부적
					if (pc.getMapId() == 70 || pc.getMapId() == 200 || pc.getMapId() == 5153) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					}
					int loc = (_random.nextInt(7));
					switch (loc) {
					case 0:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 34059, 32275, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 1:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 34054, 32271, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 2:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 34045, 32280, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 3:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 34053, 32285, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 4:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 34052, 32294, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 5:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 34043, 32280, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 6:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 34046, 32263, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 7:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 34055, 32267, (short) 4, pc.getMoveState().getHeading(), true);
					default:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 34069, 32279, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					}
				}

				else if (itemId == 5370633) { // 후원 상점 귀환 주문서
					if (pc.getMapId() == 70) {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(647));
						return;
					}
					int loc = (_random.nextInt(16));
					switch (loc) {
					case 0:

						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33531, 32853, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 1:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33533, 32849, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 2:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33536, 32846, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 3:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33538, 32852, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 4:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33539, 32858, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 5:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33536, 32860, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 6:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33526, 32855, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 7:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33528, 33858, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 8:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33540, 32877, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 9:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33539, 32868, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 10:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33534, 32869, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 11:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33534, 32874, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 12:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33535, 32879, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 13:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33539, 32881, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 14:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33545, 32873, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					case 15:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33539, 32866, (short) 4, pc.getMoveState().getHeading(), true);

					default:
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_SkillBuff));
						L1Teleport.teleport(pc, 33533, 32856, (short) 4, pc.getMoveState().getHeading(), true);
						break;
					}
				} else if (itemId == 90000030) {
					ArrayList<String> onelevelresult = new ArrayList<String>();
					switch(pc.getType()) {
					case 0 :
						if (pc.get_sex() == 0) {
							onelevelresult.add("23");
							onelevelresult.add("15");
							onelevelresult.add("17");
							onelevelresult.add("19");
							onelevelresult.add("21");
						} else {
							onelevelresult.add("24");
							onelevelresult.add("16");
							onelevelresult.add("18");
							onelevelresult.add("20");
							onelevelresult.add("22");
						}
						break;
					case 1 :
						if (pc.get_sex() == 0) {
							onelevelresult.add("15");
							onelevelresult.add("17");
							onelevelresult.add("19");
							onelevelresult.add("21");
							onelevelresult.add("23");
						} else {
							onelevelresult.add("16");
							onelevelresult.add("18");
							onelevelresult.add("20");
							onelevelresult.add("22");
							onelevelresult.add("24");
						}
						break;
					case 2 :
						if (pc.get_sex() == 0) {
							onelevelresult.add("19");
							onelevelresult.add("17");
							onelevelresult.add("15");
							onelevelresult.add("21");
							onelevelresult.add("23");
						} else {
							onelevelresult.add("20");
							onelevelresult.add("18");
							onelevelresult.add("16");
							onelevelresult.add("22");
							onelevelresult.add("24");
						}
						break;
					case 3 :
						if (pc.get_sex() == 0) {
							onelevelresult.add("21");
							onelevelresult.add("19");
							onelevelresult.add("17");
							onelevelresult.add("15");
							onelevelresult.add("23");
						} else {
							onelevelresult.add("22");
							onelevelresult.add("20");
							onelevelresult.add("18");
							onelevelresult.add("16");
							onelevelresult.add("24");
						}
						break;
					case 4 :
						if (pc.get_sex() == 0) {
							onelevelresult.add("17");
							onelevelresult.add("21");
							onelevelresult.add("19");
							onelevelresult.add("15");
							onelevelresult.add("23");
						} else {
							onelevelresult.add("18");
							onelevelresult.add("22");
							onelevelresult.add("20");
							onelevelresult.add("16");
							onelevelresult.add("24");
						}
						break;
					}
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polyRanker", (String[]) onelevelresult.toArray(new String[onelevelresult.size()])));
				} else if (itemId == 90000031) {
					ArrayList<String> onelevelresult = new ArrayList<String>();
					onelevelresult.add("14");
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "polyDS", (String[]) onelevelresult.toArray(new String[onelevelresult.size()])));
				} else if (itemId == 46165 || itemId == 46169) { // 드래곤의 에메랄드
					if (pc.getSkillEffectTimerSet().hasSkillEffect(7102)) {
						pc.getSkillEffectTimerSet().killSkillEffectTimer(7102);
					}
					if (pc.getAinHasad() <= 1500000) {
						pc.calAinHasad(1000000);
						pc.sendPackets(new S_SkillSound(pc.getId(), 7102));
						pc.getSkillEffectTimerSet().setSkillEffect(7102, 1800 * 1000);
						pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc.getAinHasad()));
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_SystemMessage("사용 불가: 아인하사드의 축복 최대 충전량 초과"));
					}
				} else if (itemId == 46167 || itemId == 46170) { // 드래곤의 토파즈
					if (itemId == 46170 && pc.getLevel() > 52) {
						pc.sendPackets(new S_SystemMessage("초보 드래곤의 토파즈를 사용할 수 없습니다."));
						return;
					}
					if (pc.getSkillEffectTimerSet().hasSkillEffect(7101)) {
						pc.getSkillEffectTimerSet().killSkillEffectTimer(7101);
					}
					pc.sendPackets(new S_SkillSound(pc.getId(), 7101));
					pc.getSkillEffectTimerSet().setSkillEffect(7101, 1800 * 1000);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == L1ItemId.INCRESE_HP_SCROLL || itemId == L1ItemId.INCRESE_MP_SCROLL
						|| itemId == L1ItemId.INCRESE_ATTACK_SCROLL || itemId == L1ItemId.CHUNSANG_HP_SCROLL
						|| itemId == L1ItemId.CHUNSANG_MP_SCROLL || itemId == L1ItemId.CHUNSANG_ATTACK_SCROLL) {
					useCashScroll(pc, itemId);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 40858) { // liquor(술)
					pc.setDrink(true);
					//pc.sendPackets(new S_Liquor(pc.getId()));
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == L1ItemId.EXP_POTION || itemId == L1ItemId.EXP_POTION2) { // 천상의물약
					UseExpPotion(pc, itemId);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == L1ItemId.POTION_OF_CURE_POISON || itemId == 40507) { // 시션 일부, 엔트의 가지
					if (pc.getSkillEffectTimerSet().hasSkillEffect(71) == true) { // 디케이포션
																					// 상태
						pc.sendPackets(new S_ServerMessage(698)); // 마력에 의해 아무것도
																	// 마실 수가
																	// 없습니다.
					} else {
						pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
						pc.sendPackets(new S_SkillSound(pc.getId(), 192));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 192));
						if (itemId == L1ItemId.POTION_OF_CURE_POISON) {
							pc.getInventory().removeItem(useItem, 1);
						} else if (itemId == 40507) {
							pc.getInventory().removeItem(useItem, 1);
						}
						pc.curePoison();
					}
				} else if (itemId == 40014 || itemId == 41415 || itemId == 140014) {
					if (pc.isKnight()) {
						BravePotion.checkCondition(pc, l1iteminstance);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
						// \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40031) {
					if (pc.isCrown()) {
						BravePotion.checkCondition(pc, l1iteminstance);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
						// \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40068 || itemId == 140068) {
					if (pc.isElf()) {
						BravePotion.checkCondition(pc, l1iteminstance);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
						// \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40016 || itemId == 140016) {
					if (pc.isWizard()) {
						WisdomPotion.checkCondition(pc, l1iteminstance);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
						// \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40015 || itemId == 40736 || itemId == 41142 || itemId == 140015) {
					BluePotion.checkCondition(pc, l1iteminstance);
				} else if (itemId == 40066 || itemId == 41413) { // 떡, 월병
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
																		// 회복해 갈
																		// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp() + (7 + _random.nextInt(6))); // 7~12
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 40067 || itemId == 41414) { // 쑥떡, 복떡
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
																		// 회복해 갈
																		// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp() + (15 + _random.nextInt(16))); // 15~30
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 410002) { // 빛나는 나뭇잎
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
																		// 회복해 갈
																		// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp() + 44);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 40735) { // 용기의 코인
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
																		// 회복해 갈
																		// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp() + 60);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 40042) { // 스피릿 일부
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
																		// 회복해 갈
																		// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp() + 50);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41404) { // 쿠쟈크의 영약
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
																		// 회복해 갈
																		// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp() + (80 + _random.nextInt(21))); // 80~100
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41412) { // 금의 톨즈
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
																		// 회복해 갈
																		// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp() + (5 + _random.nextInt(16))); // 5~20
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 210118) {
					if (pc.getLevel() > Config.중립혈레벨제한) {
						pc.sendPackets(new S_SystemMessage("중립혈맹 가입 레벨 제한됩니다."));
						return;
					}
					if (pc.getClanid() == 0) {
						L1Clan clan = L1World.getInstance().getClan("중립");
						L1PcInstance clanMember[] = clan.getOnlineClanMember();
						for (int cnt = 0; cnt < clanMember.length; cnt++) {
							clanMember[cnt].sendPackets(new S_ServerMessage(94, pc.getName()));
						}
						pc.setClanid(Config.중립혈아이디);
						pc.setClanname("중립");
						pc.setTitle("\\f:신규중립혈맹");
						pc.sendPackets(new S_CharTitle(pc.getId(), "\\f:신규중립혈맹"));
						pc.setClanRank(L1Clan.CLAN_RANK_PUBLIC);
						pc.save(); // DB에 캐릭터 정보를 기입한다
						clan.addClanMember(pc.getName(), pc.getClanRank(), pc.getOnlineStatus(), pc);
						pc.sendPackets(new S_SystemMessage("[메티스]:신규 중립 혈맹에 가입 되었습니다."));
						pc.getInventory().removeItem(l1iteminstance, 1);
						L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
					} else {
						pc.sendPackets(new S_SystemMessage("당신은 이미 혈맹에 가입하였습니다."));
					}
				} else if (itemId == 875640508) {
					if (pc.getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.자동사냥시간) >= 60 * 60 * 24) {
						pc.sendPackets(new S_SystemMessage("\\f3더 이상 자동사냥 시간을 누적할 수 없습니다."));
						return;
					}
					pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.자동사냥시간, (pc.getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.자동사냥시간) * 1000) + (1000 * 60 * 60 * 1));
					pc.getInventory().consumeItem(875640508, 1);
					pc.sendPackets(new S_SystemMessage("자동사냥 시간이 충전되었습니다."));
				} else if (itemId == 40317) { // 숫돌
					// 무기나 방어용 기구의 경우만
					if (l1iteminstance1.getItem().getType2() != 0 && l1iteminstance1.get_durability() > 0) {
						String msg0;
						pc.getInventory().recoveryDamage(l1iteminstance1);
						msg0 = l1iteminstance1.getLogName();
						if (l1iteminstance1.get_durability() == 0) {
							pc.sendPackets(new S_ServerMessage(464, msg0)); // %0%s는
																			// 신품
																			// 같은
																			// 상태가
																			// 되었습니다.
						} else {
							pc.sendPackets(new S_ServerMessage(463, msg0)); // %0
																			// 상태가
																			// 좋아졌습니다.
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_DOWN
						|| itemId == L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_DOWN) { // 오시리스의
																					// 조각
																					// (하)
					int itemId2 = l1iteminstance1.getItem().getItemId();
					if (itemId == L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_DOWN
							&& itemId2 == L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_UP) {
						if (pc.getInventory().checkItem(L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_UP)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(L1ItemId.CLOSE_LOWER_OSIRIS_PRESENT, 1);
						}
					} else if (itemId == L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_DOWN
							&& itemId2 == L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_UP) {
						if (pc.getInventory().checkItem(L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_UP)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(L1ItemId.CLOSE_HIGHER_OSIRIS_PRESENT, 1);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId == L1ItemId.LOWER_TIKAL_PRESENT_PIECE_DOWN
						|| itemId == L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_DOWN) { // 오시리스의
																					// 조각
																					// (하)
					int itemId2 = l1iteminstance1.getItem().getItemId();
					if (itemId == L1ItemId.LOWER_TIKAL_PRESENT_PIECE_DOWN
							&& itemId2 == L1ItemId.LOWER_TIKAL_PRESENT_PIECE_UP) {
						if (pc.getInventory().checkItem(L1ItemId.LOWER_TIKAL_PRESENT_PIECE_UP)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(L1ItemId.CLOSE_LOWER_TIKAL_PRESENT, 1);
						}
					} else if (itemId == L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_DOWN
							&& itemId2 == L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_UP) {
						if (pc.getInventory().checkItem(L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_UP)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(L1ItemId.CLOSE_HIGHER_TIKAL_PRESENT, 1);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId == 90000006) { // 랭커 10위 진입 주문서
					if (pc.getLevel() >= Config.LIMITLEVEL) {// 경험치
						pc.sendPackets(new S_SystemMessage("레벨제한으로 더이상 경험치 획득이 불가능합니다"));
						return;
					}
					pc.setExp(use10RankingExpPotion());
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 49027) { // 자동사냥 아이템
					if (pc._AutoController != null) {
						pc.EndAutoController();
						pc.sendPackets("자동 사냥을 종료합니다.");
					} else {
						pc.StartAutoController();
						pc.sendPackets("자동 사냥을 시작합니다.");
					}
				} else if (itemId == 3005265) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "autosystem"));
				} else if (itemId == L1ItemId.ANCIENT_ROYALSEAL) { // 태고의 옥쇄
					if (client.getAccount().getCharSlot() < 8) {
						client.getAccount().setCharSlot(client, client.getAccount().getCharSlot() + 1);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				}

				else if (itemId == L1ItemId.TIMECRACK_CORE) { // 균열의 핵
					int itemId2 = l1iteminstance1.getItem().getItemId();
					if (itemId2 == L1ItemId.CLOSE_LOWER_OSIRIS_PRESENT) {
						if (pc.getInventory().checkItem(L1ItemId.CLOSE_LOWER_OSIRIS_PRESENT)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(L1ItemId.OPEN_LOWER_OSIRIS_PRESENT, 1);
						}
					} else if (itemId2 == L1ItemId.CLOSE_HIGHER_OSIRIS_PRESENT) {
						if (pc.getInventory().checkItem(L1ItemId.CLOSE_HIGHER_OSIRIS_PRESENT)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(L1ItemId.OPEN_HIGHER_OSIRIS_PRESENT, 1);
						}
					} else if (itemId2 == L1ItemId.CLOSE_LOWER_TIKAL_PRESENT) {
						if (pc.getInventory().checkItem(L1ItemId.CLOSE_LOWER_TIKAL_PRESENT)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(L1ItemId.OPEN_LOWER_TIKAL_PRESENT, 1);
						}
					} else if (itemId2 == L1ItemId.CLOSE_HIGHER_TIKAL_PRESENT) {
						if (pc.getInventory().checkItem(L1ItemId.CLOSE_HIGHER_TIKAL_PRESENT)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(L1ItemId.OPEN_HIGHER_TIKAL_PRESENT, 1);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId == 40097 || itemId == 40119 || itemId == 140119 || itemId == 140329) { // 해주스크롤,
																											// 원주민의 토템
					L1Item template = null;
					for (L1ItemInstance eachItem : pc.getInventory().getItems()) {
						if (eachItem.getItem().getBless() != 2) {
							continue;
						}
						if (!eachItem.isEquipped() && (itemId == 40119 || itemId == 40097)) {
							// n해주는 장비 하고 있는 것 밖에 해주 하지 않는다
							continue;
						}
						int id_normal = eachItem.getItemId() - 200000;
						template = ItemTable.getInstance().getTemplate(id_normal);
						if (template == null) {
							continue;
						}
						if (pc.getInventory().checkItem(id_normal) && template.isStackable()) {
							pc.getInventory().storeItem(id_normal, eachItem.getCount());
							pc.getInventory().removeItem(eachItem, eachItem.getCount());
						} else {
							eachItem.setItem(template);
							pc.getInventory().updateItem(eachItem, L1PcInventory.COL_ITEMID);
							pc.getInventory().saveItem(eachItem, L1PcInventory.COL_ITEMID);
							eachItem.setBless(eachItem.getBless() - 1);
							pc.getInventory().updateItem(eachItem, L1PcInventory.COL_BLESS);
							pc.getInventory().saveItem(eachItem, L1PcInventory.COL_BLESS);
						}
					}
					pc.getInventory().removeItem(useItem, 1);
					pc.sendPackets(new S_ServerMessage(155)); // \f1누군가가 도와 준 것 같습니다.
				} else if (itemId == 46164) {
					L1Object target = L1World.getInstance().findObject(spellsc_objid);
					if (target != null) {
						if (target instanceof L1NpcInstance) {
							L1NpcInstance npc = (L1NpcInstance) target;
							pc.sendPackets(new S_SystemMessage("ID:" + npc.getNpcId() + " GfxId:"
									+ npc.getNpcTemplate().get_gfxid() + " 이름:" + npc.getName()));
						}
					}
				} else if (itemId == 46163) {
					L1Object target = L1World.getInstance().findObject(spellsc_objid);
					if (target != null && target instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) target;
						NpcSpawnTable.getInstance().removeSpawn(npc);
						npc.setRespawn(false);
						new L1NpcDeleteTimer(npc, 2 * 1000).begin();
						pc.sendPackets(new S_SystemMessage(npc.getNpcId() + npc.getName() + "을(를) 2초 뒤에 삭제 합니다."));

					}
				}

				else if (itemId == 450108) {
					pc.setLawful(32767);
					pc.sendPackets(new S_SystemMessage("라우풀 성향의 기운이 몸 속으로 스며듭니다."));
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 450109) {
					pc.setLawful(-32700);
					pc.sendPackets(new S_SystemMessage("카오틱 성향의 기운이 몸 속으로 스며듭니다."));
					pc.getInventory().removeItem(useItem, 1);
					pc.save();
				} else if (itemId == 3000392) { // 랭킹변신줌
					usePolyRangking(pc, itemId);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 42077) {
					if (pc.getMapId() == 70 || pc.getMapId() == 200) {
						pc.sendPackets(new S_SystemMessage("해당맵에서 사용할 수 없습니다."));
						return;
					}
					if (pc.getAccount().getLastabardTime() >= Config.라던시간) {
						pc.sendPackets(new S_SystemMessage("라스타바드 사냥시간이 소진되었습니다."));
						return;
					}
					L1Teleport.teleport(pc, 32740, 32877, (short) 530, 5, false);
					doTeleport(pc);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41036) { // 풀
					int diaryId = l1iteminstance1.getItem().getItemId();
					if (diaryId >= 41038 && 41047 >= diaryId) {
						if ((_random.nextInt(99) + 1) <= Config.CREATE_CHANCE_DIARY) {
							createNewItem(pc, diaryId + 10, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(158, l1iteminstance1.getName())); // \f1%0이 증발하고
																									// 있지 않게
																									// 되었습니다.
						}
						pc.getInventory().removeItem(l1iteminstance1, 1);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId == 40964) { // 흑마법 가루
					int historybookId = l1iteminstance1.getItem().getItemId();
					if (historybookId >= 41011 && 41018 >= historybookId) {
						if ((_random.nextInt(100) + 1) <= Config.CREATE_CHANCE_HISTORY_BOOK) {
							createNewItem(pc, historybookId + 8, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(158, l1iteminstance1.getName()));
						}
						pc.getInventory().removeItem(l1iteminstance1, 1);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40925) { // 정화의 일부
					int earingId = l1iteminstance1.getItem().getItemId();
					if (earingId >= 40987 && 40989 >= earingId) { // 저주해진 블랙 귀 링
						if (_random.nextInt(100) < Config.CREATE_CHANCE_RECOLLECTION) {
							createNewItem(pc, earingId + 186, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(158, l1iteminstance1.getName())); // \f1%0이 증발하고
																									// 있지 않게
																									// 되었습니다.
						}
						pc.getInventory().removeItem(l1iteminstance1, 1);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId >= 40926 && 40929 >= itemId) {
					// 신비적인 일부(1~4 단계)
					int earing2Id = l1iteminstance1.getItem().getItemId();
					int potion1 = 0;
					int potion2 = 0;
					if (earing2Id >= 41173 && 41184 >= earing2Id) {
						// 귀 링류
						if (itemId == 40926) {
							potion1 = 247;
							potion2 = 249;
						} else if (itemId == 40927) {
							potion1 = 249;
							potion2 = 251;
						} else if (itemId == 40928) {
							potion1 = 251;
							potion2 = 253;
						} else if (itemId == 40929) {
							potion1 = 253;
							potion2 = 255;
						}
						if (earing2Id >= (itemId + potion1) && (itemId + potion2) >= earing2Id) {
							if ((_random.nextInt(99) + 1) < Config.CREATE_CHANCE_MYSTERIOUS) {
								createNewItem(pc, (earing2Id - 12), 1);
								pc.getInventory().removeItem(l1iteminstance1, 1);
								pc.getInventory().removeItem(useItem, 1);
							} else {
								pc.sendPackets(new S_ServerMessage(160, l1iteminstance1.getName()));
								// \f1%0이%2 강렬하게%1 빛났습니다만, 다행히 무사하게 살았습니다.
								pc.getInventory().removeItem(useItem, 1);
							}
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																		// 일어나지
																		// 않았습니다.
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId == 1999) {
					int percent = ForceItem.getInstance().getSuccPercent(l1iteminstance1.getItem().getItemId());
					int SuccItem = ForceItem.getInstance().getSuccItem(l1iteminstance1.getItem().getItemId());
					int chance = _random.nextInt(100) + 1;
					if (pc.기운축복) {
						percent = 100;
						pc.기운축복 = false;
					}
					if (l1iteminstance1.getItem().getItemId() >= 8100
							&& l1iteminstance1.getItem().getItemId() <= 8217) {
						if (percent > chance) {
							L1ItemInstance SuccItem2 = pc.getInventory().storeItem(SuccItem, 1);
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_SystemMessage("" + SuccItem2.getName() + "획득"));
							pc.sendPackets(new S_SystemMessage("" + SuccItem2.getName() + "은(는) 새 생명이 부여되었습니다."));
							L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
									"누군가가 " + l1iteminstance1.getName() + "에 새 생명을 부여 하였습니다."));
							L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("누군가가 " + l1iteminstance1.getName() + "에 새 생명을 부여 하였습니다."));
						} else {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(
									new S_SystemMessage("" + l1iteminstance1.getName() + "은(는) 기운을 흡수하지 못하고 소멸하였습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("해당 아이템에는 사용할 수 없습니다."));
					}
				} else if (itemId == 2999) { // 축 나뭇잎
					int percent = ForceItem.getInstance().getSuccPercent(l1iteminstance1.getItem().getItemId());
					int SuccItem = ForceItem.getInstance().getSuccItem(l1iteminstance1.getItem().getItemId());
					int chance = _random.nextInt(100) + 1;
					int chance2 = _random.nextInt(100) + 1;
					if (pc.기운축복) {
						percent = 100;
						pc.기운축복 = false;
					}
					if (l1iteminstance1.getItem().getItemId() >= 8100
							&& l1iteminstance1.getItem().getItemId() <= 8217) {
						if (percent > chance) {
							L1ItemInstance SuccItem2 = pc.getInventory().storeItem(SuccItem, 1);
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_SystemMessage("" + SuccItem2.getName() + "획득"));
							pc.sendPackets(new S_SystemMessage("" + SuccItem2.getName() + "은(는) 새 생명이 부여되었습니다."));
							L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
									"누군가가 " + l1iteminstance1.getName() + "에 새 생명을 부여 하였습니다."));
							L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("누군가가 " + l1iteminstance1.getName() + "에 새 생명을 부여 하였습니다."));
						} else {
							if (20 > chance2) {
								pc.getInventory().removeItem(useItem, 1);
								pc.sendPackets(new S_SystemMessage(
										"" + l1iteminstance1.getName() + "은(는) 기운을 흡수하지 못하였으나 다행히 소멸하지 않았습니다."));
							} else {
								pc.getInventory().removeItem(l1iteminstance1, 1);
								pc.getInventory().removeItem(useItem, 1);
								pc.sendPackets(new S_SystemMessage(
										"" + l1iteminstance1.getName() + "은(는) 기운을 흡수하지 못하고 소멸하였습니다."));
							}
						}
					} else {
						pc.sendPackets(new S_SystemMessage("해당 아이템에는 사용할 수 없습니다."));
					}
				} else if (itemId == 88888889) { // 통합 스킬북(기사)
					if (!pc.isKnight()) {
						pc.sendPackets(new S_SystemMessage("기사만 배울 수 있습니다."));
						return;
					}
					int skillNum;
					int skillLevel;
					for (skillLevel = 11; skillLevel <= 12; skillLevel++) {
						skillNum = (skillLevel == 11) ? 6 : 0;
						for (; skillNum < 8; skillNum++) {
							통합기술서(pc);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("기사 스킬을 배웠습니다."));
				} else if (itemId == 88888890) { // 통합 스킬북(군주)
					if (!pc.isCrown()) {
						pc.sendPackets(new S_SystemMessage("군주만 배울 수 있습니다."));
						return;
					}
					int skillNum;
					for (skillNum = 0; skillNum < 6; skillNum++) {
						통합군주마법서(pc);
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("군주 스킬을 배웠습니다."));
				} else if (itemId == 88888891) { // 통합 스킬북(다크엘프)
					if (!pc.isDarkelf()) {
						pc.sendPackets(new S_SystemMessage("다크엘프만 배울 수 있습니다."));
						return;
					}
					int skillNum = 0;
					int skillLevel;
					for (skillLevel = 13; skillLevel <= 14; skillLevel++) {
						for (; skillNum < 8; skillNum++) {
							통합흑정령마법(pc);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("다크엘프 스킬을 배웠습니다."));
				} else if (itemId == 88888892) { // 통합 공통 스킬북(요정)
					if (!pc.isElf()) {
						pc.sendPackets(new S_SystemMessage("요정만 배울 수 있습니다."));
						return;
					}
					int skillNum = 0;
					int skillEndnum = 0;
					int skillLevel;
					for (skillLevel = 17; skillLevel <= 19; skillLevel++) {
						if (skillLevel == 17) {
							skillEndnum = 6;
						} else if (skillLevel == 18) {
							skillEndnum = 2;
						} else if (skillLevel == 19) {
							skillEndnum = 3;
						}
						for (; skillNum < skillEndnum; skillNum++) {
							통합공통정령마법(pc);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("요정 공통 스킬을 배웠습니다."));
				} else if (itemId == 88888897) { // 통합 불 스킬북(요정)
					if (!pc.isElf()) {
						pc.sendPackets(new S_SystemMessage("요정만 배울 수 있습니다."));
						return;
					}
					if (pc.getElfAttr() != 2) {
						pc.sendPackets(new S_SystemMessage("불 속성이 아닙니다."));
						return;
					}
					int skillNum = 0;
					int skillEndnum = 8;
					int skillLevel;
					for (skillLevel = 19; skillLevel <= 22; skillLevel++) {
						skillNum = (skillLevel == 19) ? 3 : 0;
						for (; skillNum < skillEndnum; skillNum++) {
							통합불정령마법(pc);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("요정 불 스킬을 배웠습니다."));
				} else if (itemId == 88888898) { // 통합 바람 스킬북(요정)
					if (!pc.isElf()) {
						pc.sendPackets(new S_SystemMessage("요정만 배울 수 있습니다."));
						return;
					}
					if (pc.getElfAttr() != 8) {
						pc.sendPackets(new S_SystemMessage("바람 속성이 아닙니다."));
						return;
					}
					int skillNum = 0;
					int skillEndnum = 8;
					int skillLevel;
					for (skillLevel = 19; skillLevel <= 22; skillLevel++) {
						skillNum = (skillLevel == 19) ? 3 : 0;
						for (; skillNum < skillEndnum; skillNum++) {
							통합바람정령마법(pc);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("요정 바람 스킬을 배웠습니다."));
				} else if (itemId == 88888899) { // 통합 물 스킬북(요정)
					if (!pc.isElf()) {
						pc.sendPackets(new S_SystemMessage("요정만 배울 수 있습니다."));
						return;
					}
					if (pc.getElfAttr() != 4) {
						pc.sendPackets(new S_SystemMessage("물 속성이 아닙니다."));
						return;
					}
					int skillNum = 0;
					int skillEndnum = 8;
					int skillLevel;
					for (skillLevel = 19; skillLevel <= 22; skillLevel++) {
						skillNum = (skillLevel == 19) ? 3 : 0;
						for (; skillNum < skillEndnum; skillNum++) {
							통합물정령마법(pc);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("요정 물 스킬을 배웠습니다."));
				} else if (itemId == 88888900) { // 통합 땅 스킬북(요정)
					if (!pc.isElf()) {
						pc.sendPackets(new S_SystemMessage("요정만 배울 수 있습니다."));
						return;
					}
					if (pc.getElfAttr() != 1) {
						pc.sendPackets(new S_SystemMessage("땅 속성이 아닙니다."));
						return;
					}
					int skillNum = 0;
					int skillEndnum = 8;
					int skillLevel;
					for (skillLevel = 19; skillLevel <= 22; skillLevel++) {
						skillNum = (skillLevel == 19) ? 3 : 0;
						for (; skillNum < skillEndnum; skillNum++) {
							통합땅정령마법(pc);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("요정 땅 스킬을 배웠습니다."));
				} else if (itemId == 88888893) { // 통합 스킬북(마법사)
					if (!pc.isWizard()) {
						pc.sendPackets(new S_SystemMessage("마법사만 배울 수 있습니다."));
						return;
					}
					int skillNum = 0;
					int skillLevel;
					for (skillLevel = 1; skillLevel <= 10; skillLevel++) {
						for (; skillNum < 8; skillNum++) {
							통합법사마법서(pc);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("1~10단계(기본) 마법을 배웠습니다."));
				} else if (itemId == 88888894) { // 통합 1단계 스킬북
					int skillNum;
					for (skillNum = 0; skillNum < 8; skillNum++) {
						통합1단계마법서(pc);
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("1단계 마법을 배웠습니다."));
				} else if (itemId == 88888895) { // 통합 1~2단계 스킬북
					int skillNum = 0;
					int skillLevel;
					for (skillLevel = 1; skillLevel <= 2; skillLevel++) {
						for (; skillNum < 8; skillNum++) {
							통합2단계마법서(pc);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("1~2단계 마법을 배웠습니다."));
				} else if (itemId == 88888896) { // 통합 1~6단계 스킬북
					if (!pc.isElf()) {
						pc.sendPackets(new S_SystemMessage("요정만 배울 수 있습니다."));
						return;
					}
					int skillNum = 0;
					int skillLevel;
					for (skillLevel = 1; skillLevel <= 6; skillLevel++) {
						for (; skillNum < 8; skillNum++) {
							통합6단계마법서(pc);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("1~6단계 마법을 배웠습니다."));
				} else if (itemId == 90000000) {
					if (Config.STANDBY_SERVER) {
						pc.sendPackets("오픈대기 상태에서는 경험치 물약을 복용할 수 없습니다.");
						return;
					}
					if (pc.getLevel() < 50) {
						pc.sendPackets("50레벨 이상부터 사용가능합니다.");
						return;
					}
					if (pc.getLevel() > Config.LIMITLEVEL) {
						pc.sendPackets("" + Config.LIMITLEVEL + "레벨 이하만 사용이 가능합니다.");
						return;
					}
					double addexp = 0;
					if (pc.getLevel() >= 50 && pc.getLevel() <= 52) {
						addexp = ExpTable.getExpByLevel(pc.getLevel()) * 0.003;
					} else if (pc.getLevel() >= 53 && pc.getLevel() <= 55) {
						addexp = ExpTable.getExpByLevel(pc.getLevel()) * 0.001;
					} else if (pc.getLevel() >= 56 && pc.getLevel() <= 58) {
						addexp = ExpTable.getExpByLevel(pc.getLevel()) * 0.0005;
					} else if (pc.getLevel() >= 59 && pc.getLevel() <= 61) {
						addexp = ExpTable.getExpByLevel(pc.getLevel()) * 0.0003;
					} else if (pc.getLevel() >= 62 && pc.getLevel() <= 64) {
						addexp = ExpTable.getExpByLevel(pc.getLevel()) * 0.00005;
					} else if (pc.getLevel() >= 65 && pc.getLevel() <= 67) {
						addexp = ExpTable.getExpByLevel(pc.getLevel()) * 0.00002;
					} else if (pc.getLevel() >= 68 && pc.getLevel() <= 70) {
						addexp = ExpTable.getExpByLevel(pc.getLevel()) * 0.00001;
					} else if (pc.getLevel() >= 71 && pc.getLevel() <= 73) {
						addexp = ExpTable.getExpByLevel(pc.getLevel()) * 0.000005;
					} else if (pc.getLevel() >= 74 && pc.getLevel() <= 76) {
						addexp = ExpTable.getExpByLevel(pc.getLevel()) * 0.000002;
					} else if (pc.getLevel() >= 77 && pc.getLevel() <= 90) {
						addexp = ExpTable.getExpByLevel(pc.getLevel()) * 0.000001;
					} else {
						addexp = ExpTable.getExpByLevel(pc.getLevel()) * 0.0000005;
					}
					pc.setExp(pc.getExp() + (int) addexp);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 41029) { // 소환구 조각
					int dantesId = l1iteminstance1.getItem().getItemId();
					if (dantesId >= 41030 && 41034 >= dantesId) { // 소환공의 코어· 각
																	// 단계
						if ((_random.nextInt(99) + 1) < Config.CREATE_CHANCE_DANTES) {
							createNewItem(pc, dantesId + 1, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(158, l1iteminstance1.getName())); // \f1%0이 증발하고
																									// 있지 않게
																									// 되었습니다.
						}
						pc.getInventory().removeItem(l1iteminstance1, 1);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
					// 스펠 스크롤
				} else if ((itemId >= 40859 && itemId <= 40898) && itemId != 40863) { // 40863은 텔레포트 스크롤로서 처리된다
					if (spellsc_objid == pc.getId() && useItem.getItem().getUseType() != 30) {
						// spell_buff
						pc.sendPackets(new S_ServerMessage(281)); // \f1마법이 무효가
																	// 되었습니다.
						return;
					}
					pc.getInventory().removeItem(useItem, 1);
					if (spellsc_objid == 0 && useItem.getItem().getUseType() != 0
							&& useItem.getItem().getUseType() != 26 && useItem.getItem().getUseType() != 27) {
						return;
						// 타겟이 없는 경우에 handleCommands송가 되기 (위해)때문에 여기서 return
						// handleCommands 쪽으로 판단＆처리해야 할 부분일지도 모른다
					}
					pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
					int skillid = itemId - 40858;
					L1SkillUse l1skilluse = new L1SkillUse();
					l1skilluse.handleCommands(client.getActiveChar(), skillid, spellsc_objid, spellsc_x, spellsc_y,
							null, 0, L1SkillUse.TYPE_SPELLSC);

				} else if (itemId >= 40373 && itemId <= 40384 // 지도 각종
						|| itemId >= 40385 && itemId <= 40390) {
					pc.sendPackets(new S_UseMap(pc, useItem.getId(), useItem.getItem().getItemId()));
				} else if (itemId == 40314 || itemId == 40316) { // 펫의 아뮤렛트
					if (pc.getInventory().checkItem(41160)) { // 소환의 피리
						if (withdrawPet(pc, itemObjid)) {
							pc.getInventory().consumeItem(41160, 1);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId == 40315) { // 펫의 피리
					pc.sendPackets(new S_Sound(437));
					Broadcaster.broadcastPacket(pc, new S_Sound(437));
					Object[] petList = pc.getPetList().values().toArray();
					for (Object petObject : petList) {
						if (petObject instanceof L1PetInstance) { // 펫
							L1PetInstance pet = (L1PetInstance) petObject;
							pet.call();
						}
					}
				} else if (itemId == 40493) { // 매직 플룻
					pc.sendPackets(new S_Sound(165));
					Broadcaster.broadcastPacket(pc, new S_Sound(165));
					L1GuardianInstance guardian = null;
					for (L1Object visible : pc.getNearObjects().getKnownObjects()) {
						if (visible instanceof L1GuardianInstance) {
							guardian = (L1GuardianInstance) visible;
							if (guardian.getNpcTemplate().get_npcId() == 70850) { // 빵
								if (createNewItem(pc, 88, 1)) {
									pc.getInventory().removeItem(useItem, 1);
								}
							}
						}
					}
				} else if (itemId == 40325) {
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3237 + _random.nextInt(2);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId == 40326) {
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3229 + _random.nextInt(3);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId == 40327) {
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3204 + _random.nextInt(4);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId == 40328) {
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3204 + _random.nextInt(6);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId >= 51093 && itemId <= 51097) {
					//클래스 변경물약 
					if (pc.getClanid() != 0) {
						pc.sendPackets(new S_ChatPacket(pc,"혈맹을 먼저 탈퇴하여 주시기 바랍니다."));
						return;
					} else if (itemId == 51093 && pc.getType() == 0) { // 자네 군주?
						pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 군주 클래스 입니다."));
						return;
					} else if (itemId == 51094 && pc.getType() == 1) { // 자네 기사?
						pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 기사 클래스 입니다."));
						return;
					} else if (itemId == 51095 && pc.getType() == 2) { // 자네 요정?
						pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 요정 클래스 입니다."));
						return;
					} else if (itemId == 51096 && pc.getType() == 3) { // 자네 마법사?
						pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 마법사 클래스 입니다."));
						return;
					} else if (itemId == 51097 && pc.getType() == 4) { // 자네 다크엘프?
						pc.sendPackets(new S_ChatPacket(pc,"당신은 이미 다크엘프 클래스 입니다."));
						return;
					}
					int[] Mclass = new int[] { 0, 61, 138, 734, 2786 };
					int[] Wclass = new int[] { 1, 48, 37, 1186, 2796 };
					if (itemId == 51093 && pc.getType() != 0 && pc.get_sex() == 0) {
						pc.setType(0);
						pc.setClassId(Mclass[pc.getType()]);
					} else if (itemId == 51093 && pc.getType() != 0 && pc.get_sex() == 1) {//군주
						pc.setType(0);
						pc.setClassId(Wclass[pc.getType()]);
					} else if (itemId == 51094 && pc.getType() != 1 && pc.get_sex() == 0) { // 변경: 기사
						pc.setType(1);
						pc.setClassId(Mclass[pc.getType()]);
					} else if (itemId == 51094 && pc.getType() != 1 && pc.get_sex() == 1) {
						pc.setType(1);
						pc.setClassId(Wclass[pc.getType()]);
					} else if (itemId == 51095 && pc.getType() != 2 && pc.get_sex() == 0) { // 변경: 요정
						pc.setType(2);
						pc.setClassId(Mclass[pc.getType()]);
					} else if (itemId == 51095 && pc.getType() != 2 && pc.get_sex() == 1) {
						pc.setType(2);
						pc.setClassId(Wclass[pc.getType()]);
					} else if (itemId == 51096 && pc.getType() != 3 && pc.get_sex() == 0) { // 변경: 마법사
						pc.setType(3);
						pc.setClassId(Mclass[pc.getType()]);
					} else if (itemId == 51096 && pc.getType() != 3 && pc.get_sex() == 1) {
						pc.setType(3);
						pc.setClassId(Wclass[pc.getType()]);
					} else if (itemId == 51097 && pc.getType() != 4 && pc.get_sex() == 0) { // 변경: 다크엘프
						pc.setType(4);
						pc.setClassId(Mclass[pc.getType()]);
					} else if (itemId == 51097 && pc.getType() != 4 && pc.get_sex() == 1) {
						pc.setType(4);
						pc.setClassId(Wclass[pc.getType()]);
					}
					if (pc.getWeapon() != null)
						pc.getInventory().setEquipped(pc.getWeapon(), false, false, false);
					pc.getInventory().takeoffEquip(945);
					pc.sendPackets(new S_CharVisualUpdate(pc));
					for (L1ItemInstance armor : pc.getInventory().getItems()) {
						for (int type = 0; type <= 12; type++) {
							if (armor != null) {
								pc.getInventory().setEquipped(armor, false, false, false);
							}
						}
					}
					pc.sendPackets(new S_DelSkill(255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
							255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255));
					deleteSpell(pc);
					pc.setTempCharGfx(pc.getClassId());
					pc.sendPackets(new S_ChangeShape(pc.getId(), pc.getClassId()));
					Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), pc.getClassId()));
					pc.getInventory().removeItem(l1iteminstance, 1);
					try {
						pc.save();
					} catch (Exception e) { }
					pc.sendPackets(new S_SystemMessage("클래스변경으로 자동접속종료됩니다."));
					L1Teleport.teleport(pc, 32723 + _random.nextInt(10), 32851 + _random.nextInt(10), (short) 5166, 5, true);
					StatInitialize(pc);
					Thread.sleep(500);
					pc.sendPackets(new S_Disconnect());
				} else if (itemId == 700079) {
					for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 10)) {
						if (obj instanceof L1MonsterInstance) { // 몬스터라면
							L1NpcInstance npc = (L1NpcInstance) obj;
							npc.receiveDamage(pc, 5000000); // 대미지
							if (npc.getCurrentHp() <= 0) {
							} else {
							}
						} else if (obj instanceof L1PcInstance) { // pc라면
							L1PcInstance Player = (L1PcInstance) obj;
							Player.receiveDamage(Player, 0, false);
							if (Player.getCurrentHp() <= 0) {
							} else {
							}
						}
					}
				} else if (itemId == 1430047) { // 강제 칼질
					if (pc._attackClick) {
						pc._attackClick = false;
						for (L1PcInstance other : L1World.getInstance().getVisiblePlayer(pc)) {
							if (pc.getId() == other.getId())
								continue;
							pc.sendPackets(new S_PinkName(other.getId(), 0));

						}
						pc.sendPackets(new S_SystemMessage("PK모드 활성화가 종료 되었습니다."));
					} else {
						pc._attackClick = true;
						pc.sendPackets(new S_SystemMessage("PK모드 활성화가 시작 되었습니다."));
					}
				} else if (itemId == 700078) { // 메티스 축복
					int objid = pc.getId();
					pc.sendPackets(new S_SkillSound(objid, 4856)); // 3944
					Broadcaster.broadcastPacket(pc, new S_SkillSound(objid, 4856));
					for (L1PcInstance tg : L1World.getInstance().getVisiblePlayer(pc)) {
						if (tg.getCurrentHp() == 0 && tg.isDead()) {
							tg.sendPackets(new S_SystemMessage("GM이 부활을 해주었습니다. "));
							Broadcaster.broadcastPacket(tg, new S_SkillSound(tg.getId(), 3944));
							tg.sendPackets(new S_SkillSound(tg.getId(), 3944));
							// 축복된 부활 스크롤과 같은 효과
							tg.setTempID(objid);
							tg.sendPackets(new S_Message_YN(322, "")); // 또 부활하고 싶습니까? (Y/N)
						} else {
							// tg.sendPackets(new S_SystemMessage("GM이 HP,MP를 회복해주었습니다."));
							Broadcaster.broadcastPacket(tg, new S_SkillSound(tg.getId(), 832));
							tg.sendPackets(new S_SkillSound(tg.getId(), 832));
							tg.setCurrentHp(tg.getMaxHp());
							tg.setCurrentMp(tg.getMaxMp());
						}
					}

				} else if (itemId == 40328) {
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3204 + _random.nextInt(6);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						// \f1 아무것도 일어나지 않았습니다.
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 4370604) {

					int gfxid = 3204 + _random.nextInt(6);
					pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));
					pc.getInventory().consumeItem(40318, 1);

				} else if (itemId == L1ItemId.CHARACTER_REPAIR_SCROLL) {
					Connection connection = null;
					connection = L1DatabaseFactory.getInstance().getConnection();
					PreparedStatement preparedstatement = connection.prepareStatement(
							"UPDATE characters SET LocX=33087, LocY=33399, MapID=4 WHERE account_name=?");
					preparedstatement.setString(1, client.getAccountName());
					preparedstatement.execute();
					preparedstatement.close();
					connection.close();
					pc.getInventory().removeItem(useItem, 1);
					pc.sendPackets(new S_SystemMessage("모든 케릭터의 좌표가 정상적으로 복구 되었습니다."));
				} else if (itemId >= 40903 && itemId <= 40908) { // 각종 약혼 반지
					L1PcInstance partner = null;
					boolean partner_stat = false;
					if (pc.getPartnerId() != 0) { // 결혼중
						partner = (L1PcInstance) L1World.getInstance().findObject(pc.getPartnerId());
						if (partner != null && partner.getPartnerId() != 0 && pc.getPartnerId() == partner.getId()
								&& partner.getPartnerId() == pc.getId()) {
							partner_stat = true;
						}
					} else {
						pc.sendPackets(new S_ServerMessage(662)); // \f1당신은 결혼하지
																	// 않았습니다.
						return;
					}

					if (useItem.getChargeCount() <= 0) {
						return;
					}
					if (pc.getMapId() == 666) {
						return;
					}
					if (partner_stat) {
						boolean castle_area = L1CastleLocation.checkInAllWarArea(partner.getX(), partner.getY(),
								partner.getMapId());

						if ((partner.getMapId() == 0 || partner.getMapId() == 4 || partner.getMapId() == 304)
								&& castle_area == false) {
							useItem.setChargeCount(useItem.getChargeCount() - 1);
							pc.getInventory().updateItem(useItem, L1PcInventory.COL_CHARGE_COUNT);
							L1Teleport.teleport(pc, partner.getX(), partner.getY(), partner.getMapId(), 5, true);
						} else {
							pc.sendPackets(new S_ServerMessage(547)); // \f1당신의
																		// 파트너는
																		// 지금
																		// 당신이 갈
																		// 수 없는
																		// 곳에서
																		// 플레이중입니다.
						}
					} else {
						pc.sendPackets(new S_ServerMessage(546)); // \f1당신의 파트너는
																	// 지금 플레이를
																	// 하고 있지
																	// 않습니다.
					}
				} else if (itemId == 40555) { // 비밀의 방의 키
					// 오림 방
					if (pc.isKnight() && (pc.getX() >= 32806 && pc.getX() <= 32814)
							&& (pc.getY() >= 32798 && pc.getY() <= 32807) && pc.getMapId() == 13) {
						L1Teleport.teleport(pc, 32815, 32810, (short) 13, 5, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId == 40417) { // 정령의결정
					if ((pc.getX() >= 32667 && pc.getX() <= 32673) && (pc.getY() >= 32978 && pc.getY() <= 32984)
							&& pc.getMapId() == 440) {
						L1Teleport.teleport(pc, 32922, 32812, (short) 430, 5, true);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId == 40566) { // 신비적인 쉘
					// 상아의 탑의 마을의 남쪽에 있는 매직 스퀘어의 좌표
					if (pc.isElf() && (pc.getX() >= 33971 && pc.getX() <= 33975)
							&& (pc.getY() >= 32324 && pc.getY() <= 32328) && pc.getMapId() == 4
							&& !pc.getInventory().checkItem(40548)) { // 망령의 봉투
						boolean found = false;
						L1MonsterInstance mob = null;
						for (L1Object obj : L1World.getInstance().getVisibleObjects(4).values()) {
							if (obj instanceof L1MonsterInstance) {
								mob = (L1MonsterInstance) obj;
								if (mob != null) {
									if (mob.getNpcTemplate().get_npcId() == 45300) {
										found = true;
										break;
									}
								}
							}
						}
						if (found) {
							pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																		// 일어나지
																		// 않았습니다.
						} else {
							L1SpawnUtil.spawn(pc, 45300, 0, 0, false); // 고대인의
																		// 망령
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
					}
				} else if (itemId == 40557) {
					if (pc.getX() == 32620 && pc.getY() == 32641 && pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45883) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45883, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40563) {
					if (pc.getX() == 32730 && pc.getY() == 32426 && pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45884) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45884, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40561) {
					if (pc.getX() == 33046 && pc.getY() == 32806 && pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45885) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45885, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40560) {
					if (pc.getX() == 32580 && pc.getY() == 33260 && pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45886) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45886, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40562) {
					if (pc.getX() == 33447 && pc.getY() == 33476 && pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45887) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45887, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40559) {
					if (pc.getX() == 34215 && pc.getY() == 33195 && pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45888) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45888, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40558) {
					if (pc.getX() == 33513 && pc.getY() == 32890 && pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45889) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45889, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40572) {
					if (pc.getX() == 32778 && pc.getY() == 32738 && pc.getMapId() == 21) {
						L1Teleport.teleport(pc, 32781, 32728, (short) 21, 5, true);
					} else if (pc.getX() == 32781 && pc.getY() == 32728 && pc.getMapId() == 21) {
						L1Teleport.teleport(pc, 32778, 32738, (short) 21, 5, true);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40009) {// 추방막대
					int chargeCount = useItem.getChargeCount();
					if (chargeCount <= 0) {
						pc.sendPackets(new S_ServerMessage(79));// \f1 아무것도 일어나지
																// 않았습니다.
						return;
					}

					L1Object target = L1World.getInstance().findObject(spellsc_objid);
					if (target != null) {
						int heding = CharPosUtil.targetDirection(pc, spellsc_x, spellsc_y);
						pc.getMoveState().setHeading(heding);
						pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_Wand));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, ActionCodes.ACTION_Wand));

						if (target instanceof L1PcInstance) {
							L1PcInstance cha = (L1PcInstance) target;
							if (cha.getLevel() <= 60) {
								if (!L1CastleLocation.checkInAllWarArea(cha.getX(), cha.getY(), cha.getMapId())
										&& (CharPosUtil.getZoneType(cha) == 0 || CharPosUtil.getZoneType(cha) == -1)) {
									L1Teleport.teleport(cha, pc.getLocation(), pc.getMoveState().getHeading(), false);
								}
							}
							if (cha.getSkillEffectTimerSet().hasSkillEffect(ERASE_MAGIC)) {
								cha.getSkillEffectTimerSet().removeSkillEffect(ERASE_MAGIC);
							}
						}
					}

					useItem.setChargeCount(useItem.getChargeCount() - 1);
					pc.getInventory().updateItem(useItem, L1PcInventory.COL_CHARGE_COUNT);

				} else if (itemId == 31116) { // 버프물약(근거리)
					int[] allBuffSkill = { 114 };
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
								L1SkillUse.TYPE_GMBUFF);
					}
					pc.getInventory().removeItem(useItem, 1);
					return;

				} else if (itemId == 1430635) { // 버프물약(근거리)
					int[] allBuffSkill = { 115 };
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
								L1SkillUse.TYPE_GMBUFF);
					}
					pc.getInventory().removeItem(useItem, 1);
					return;

				} else if (itemId == 1430636) { // 버프물약(근거리)
					int[] allBuffSkill = { 117 };
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
								L1SkillUse.TYPE_GMBUFF);
					}
					pc.getInventory().removeItem(useItem, 1);
					return;

				} else if (itemId == 31117) { // 버프물약(근거리)
					int[] allBuffSkill = { 148, 26, 42, 54, 48, 151 };
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
								L1SkillUse.TYPE_GMBUFF);
					}
					pc.getInventory().removeItem(useItem, 1);
					return;
				} else if (itemId == 31118) { // 버프물약(원거리)
					int[] allBuffSkill = { 26, 42, 54, 48, 166 };// 스톰샷
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
								L1SkillUse.TYPE_GMBUFF);
					}
					pc.getInventory().removeItem(useItem, 1);
					return;
				} else if (itemId == 1430634
						) { // 무제한버프근거리
					int[] allBuffSkill = { 26, 42, 48, 54, 79, 117, 148, 168 };
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
								L1SkillUse.TYPE_GMBUFF);
					}

					return;
				} else if (itemId == 1530634) { // 무제한버프원
					int[] allBuffSkill = { 26, 42, 48, 54, 79, 117, 149, 166, 168 };// 스톰샷
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
								L1SkillUse.TYPE_GMBUFF);
					}

					return;
				} else if (itemId == L1ItemId.ICECAVE_KEY) {
					L1Object t = L1World.getInstance().findObject(spellsc_objid);
					L1DoorInstance door = (L1DoorInstance) t;
					if (pc.getLocation().getTileLineDistance(door.getLocation()) > 3) {
						return;
					}
					if (door.getDoorId() >= 5000 && door.getDoorId() <= 5009) {
						if (door != null && door.getOpenStatus() == ActionCodes.ACTION_Close) {
							door.open();
							pc.getInventory().removeItem(useItem, 1);
						}
					}
				} else if (itemId == 4370603) { // 인형코인조각
					if (!pc.getInventory().checkItem(4370603, 30)) {
						pc.sendPackets(new S_SystemMessage("이터널코인조각 30개가 부족합니다."));
						return;
					} else {
						pc.getInventory().consumeItem(4370603, 30);
						pc.getInventory().storeItem(45067, 1);
						pc.sendPackets(new S_SystemMessage("이터널코인을 얻었습니다."));
					}

				}

				else if (itemId >= 40289 && itemId <= 40297) { // 오만의 탑11~91층 부적
					useToiTeleportAmulet(pc, itemId, useItem);
				} else if (itemId >= 5370617 && itemId <= 5370626) { // 오만의 탑11~91층
					useToiTeleportAmulet1(pc, itemId, useItem);
				} else if (itemId >= 40280 && itemId <= 40288) {
					// 봉인된 오만의 탑 11~91층 부적
					pc.getInventory().removeItem(useItem, 1);
					L1ItemInstance item = pc.getInventory().storeItem(itemId + 9, 1);
					if (item != null) {
						pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
					}
				} else if (itemId == 40070) {
					pc.sendPackets(new S_ServerMessage(76, useItem.getLogName()));
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41301) { // 샤이닝렛드핏슈
					int chance = _random.nextInt(10);
					if (chance >= 0 && chance < 5) {
						UseHeallingPotion(pc, 15, 189);
					} else if (chance >= 5 && chance < 9) {
						createNewItem(pc, 40019, 1);
					} else if (chance >= 9) {
						int gemChance = _random.nextInt(3);
						if (gemChance == 0) {
							createNewItem(pc, 40045, 1);
						} else if (gemChance == 1) {
							createNewItem(pc, 40049, 1);
						} else if (gemChance == 2) {
							createNewItem(pc, 40053, 1);
						}
					}
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41302) { // 샤이닝그린핏슈
					int chance = _random.nextInt(3);
					if (chance >= 0 && chance < 5) {
						UseHeallingPotion(pc, 15, 189);
					} else if (chance >= 5 && chance < 9) {
						createNewItem(pc, 40018, 1);
					} else if (chance >= 9) {
						int gemChance = _random.nextInt(3);
						if (gemChance == 0) {
							createNewItem(pc, 40047, 1);
						} else if (gemChance == 1) {
							createNewItem(pc, 40051, 1);
						} else if (gemChance == 2) {
							createNewItem(pc, 40055, 1);
						}
					}
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41303) { // 샤이닝브르핏슈
					int chance = _random.nextInt(3);
					if (chance >= 0 && chance < 5) {
						UseHeallingPotion(pc, 15, 189);
					} else if (chance >= 5 && chance < 9) {
						createNewItem(pc, 40015, 1);
					} else if (chance >= 9) {
						int gemChance = _random.nextInt(3);
						if (gemChance == 0) {
							createNewItem(pc, 40046, 1);
						} else if (gemChance == 1) {
							createNewItem(pc, 40050, 1);
						} else if (gemChance == 2) {
							createNewItem(pc, 40054, 1);
						}
					}
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41304) { // 샤이닝화이트핏슈
					int chance = _random.nextInt(3);
					if (chance >= 0 && chance < 5) {
						UseHeallingPotion(pc, 15, 189);
					} else if (chance >= 5 && chance < 9) {
						createNewItem(pc, 40021, 1);
					} else if (chance >= 9) {
						int gemChance = _random.nextInt(3);
						if (gemChance == 0) {
							createNewItem(pc, 40044, 1);
						} else if (gemChance == 1) {
							createNewItem(pc, 40048, 1);
						} else if (gemChance == 2) {
							createNewItem(pc, 40052, 1);
						}
					}
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 40615) { // 그림자의 신전 2층의 열쇠
					if ((pc.getX() >= 32701 && pc.getX() <= 32705) && (pc.getY() >= 32894 && pc.getY() <= 32898)
							&& pc.getMapId() == 522) { // 그림자의 신전 1F
						L1Teleport.teleport(pc, ((L1EtcItem) useItem.getItem()).get_locx(),
								((L1EtcItem) useItem.getItem()).get_locy(), ((L1EtcItem) useItem.getItem()).get_mapid(),
								5, true);
					} else {
						// \f1 아무것도 일어나지 않았습니다.
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40616 || itemId == 40782 || itemId == 40783) { // 그림자의 신전 3층의 열쇠
					if ((pc.getX() >= 32698 && pc.getX() <= 32702) && (pc.getY() >= 32894 && pc.getY() <= 32898)
							&& pc.getMapId() == 523) { // 그림자의 신전 2층
						L1Teleport.teleport(pc, ((L1EtcItem) useItem.getItem()).get_locx(),
								((L1EtcItem) useItem.getItem()).get_locy(), ((L1EtcItem) useItem.getItem()).get_mapid(),
								5, true);
					} else {
						// \f1 아무것도 일어나지 않았습니다.
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40692) { // 완성된 보물의 지도
					if (pc.getInventory().checkItem(40621)) {
						// \f1 아무것도 일어나지 않았습니다.
						pc.sendPackets(new S_ServerMessage(79));
					} else if ((pc.getX() >= 32856 && pc.getX() <= 32858) && (pc.getY() >= 32857 && pc.getY() <= 32858)
							&& pc.getMapId() == 443) { // 해적섬의 지하 감옥 3층
						L1Teleport.teleport(pc, ((L1EtcItem) useItem.getItem()).get_locx(),
								((L1EtcItem) useItem.getItem()).get_locy(), ((L1EtcItem) useItem.getItem()).get_mapid(),
								5, true);
					} else {
						// \f1 아무것도 일어나지 않았습니다.
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 41146) { // 드로몬드의 초대장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei001"));
				} else if (itemId == 41209) { // 포피레아의 의뢰서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei002"));
				} else if (itemId == 41210) { // 연마재
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei003"));
				} else if (itemId == 41211) { // 허브
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei004"));
				} else if (itemId == 41212) { // 특제 캔디
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei005"));
				} else if (itemId == 41213) { // 티미의 바스켓
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei006"));
				} else if (itemId == 41214) { // 운의 증거
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei012"));
				} else if (itemId == 41215) { // 지의 증거
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei010"));
				} else if (itemId == 41216) { // 력의 증거
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei011"));
				} else if (itemId == 41222) { // 마슈르
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei008"));
				} else if (itemId == 41223) { // 무기의 파편
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei007"));
				} else if (itemId == 41224) { // 배지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei009"));
				} else if (itemId == 41225) { // 케스킨의 발주서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei013"));
				} else if (itemId == 41226) { // 파고의 약
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei014"));
				} else if (itemId == 41227) { // 알렉스의 소개장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei033"));
				} else if (itemId == 41228) { // 율법박사의 부적
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei034"));
				} else if (itemId == 41229) { // 스켈리턴의 머리
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei025"));
				} else if (itemId == 41230) { // 지난에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei020"));
				} else if (itemId == 41231) { // 맛티에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei021"));
				} else if (itemId == 41233) { // 케이이에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei019"));
				} else if (itemId == 41234) { // 뼈가 들어온 봉투
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei023"));
				} else if (itemId == 41235) { // 재료표
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei024"));
				} else if (itemId == 41236) { // 본아챠의 뼈
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei026"));
				} else if (itemId == 41237) { // 스켈리턴 스파이크의 뼈
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei027"));
				} else if (itemId == 41239) { // 브트에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei018"));
				} else if (itemId == 41240) { // 페다에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei022"));
				} else if (itemId == 41060) { // 노나메의 추천서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "nonames"));
				} else if (itemId == 41061) { // 조사단의 증서：에르프 지역 두다마라카메
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kames"));
				} else if (itemId == 41062) { // 조사단의 증서：인간 지역 네르가바크모
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bakumos"));
				} else if (itemId == 41063) { // 조사단의 증서：정령 지역 두다마라브카
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bukas"));
				} else if (itemId == 41064) { // 조사단의 증서：오크 지역 네르가후우모
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "huwoomos"));
				} else if (itemId == 41065) { // 조사단의 증서：조사단장 아트바노아
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noas"));
				} else if (itemId == 41356) { // 파룸의 자원 리스트
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rparum3"));
				} else if (itemId == 40701) { // 작은 보물의 지도
					if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 1) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "firsttmap"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 2) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "secondtmapa"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 3) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "secondtmapb"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 4) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "secondtmapc"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 5) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapd"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 6) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmape"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 7) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapf"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 8) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapg"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 9) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmaph"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 10) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapi"));
					}
				} else if (itemId == 40663) { // 아들의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "sonsletter"));
				} else if (itemId == 40630) { // 디에고의 낡은 일기
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "diegodiary"));
				} else if (itemId == 41340) { // 용병단장 티온
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tion"));
				} else if (itemId == 41317) { // 랄슨의 추천장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rarson"));
				} else if (itemId == 41318) { // 쿠엔의 메모
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kuen"));
				} else if (itemId == 41329) { // 박제의 제작 의뢰서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "anirequest"));
				} else if (itemId == 41346) { // 로빈훗드의 메모 1
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "robinscroll"));
				} else if (itemId == 41347) { // 로빈훗드의 메모 2
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "robinscroll2"));
				} else if (itemId == 41348) { // 로빈훗드의 소개장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "robinhood"));
				} else if (itemId == 41007) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "erisscroll"));
				} else if (itemId == 41009) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "erisscroll2"));
				} else if (itemId == 41019) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory1"));
				} else if (itemId == 41020) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory2"));
				} else if (itemId == 41021) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory3"));
				} else if (itemId == 41022) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory4"));
				} else if (itemId == 41023) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory5"));
				} else if (itemId == 41024) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory6"));
				} else if (itemId == 41025) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory7"));
				} else if (itemId == 41026) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory8"));
				} else if (itemId == 210087) { // 프로켈의 첫 번째 지령서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "first_p"));
				} else if (itemId == 210093) { // 실레인의 첫 번째 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "silrein1lt"));
				} else if (itemId == L1ItemId.TIKAL_CALENDAR) {
					if (CrockSystem.getInstance().isOpen())
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tcalendaro"));
					else
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tcalendarc"));
				} else if (itemId == 41208) { // 져 가는 영혼
					if ((pc.getX() >= 32844 && pc.getX() <= 32845) && (pc.getY() >= 32693 && pc.getY() <= 32694)
							&& pc.getMapId() == 550) { // 배의 묘지:지상층
						L1Teleport.teleport(pc, ((L1EtcItem) useItem.getItem()).get_locx(),
								((L1EtcItem) useItem.getItem()).get_locy(), ((L1EtcItem) useItem.getItem()).get_mapid(),
								5, true);
					} else {
						// \f1 아무것도 일어나지 않았습니다.
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40700) { // 실버 플룻
					pc.sendPackets(new S_Sound(10));
					Broadcaster.broadcastPacket(pc, new S_Sound(10));
					if ((pc.getX() >= 32619 && pc.getX() <= 32623) && (pc.getY() >= 33120 && pc.getY() <= 33124)
							&& pc.getMapId() == 440) { // 해적 시마마에반매직 스퀘어 좌표
						boolean found = false;
						L1MonsterInstance mon = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1MonsterInstance) {
								mon = (L1MonsterInstance) obj;
								if (mon != null) {
									if (mon.getNpcTemplate().get_npcId() == 45875) {
										found = true;
										break;
									}
								}
							}

						}
						if (found) {
						} else {
							L1SpawnUtil.spawn(pc, 45875, 0, 0, false);
						}
					}
				} else if (itemId == 67001 || itemId == 67002) {
					long curtime = System.currentTimeMillis() / 1000;
					if (pc.getEquipChange() + 2 > curtime) { 
						return;
					}
					for (L1ItemInstance item : pc.getInventory().getItems()) {
						if (item.getItemId() == 20077 || item.getItemId() == 120077 || item.getItemId() == 20062) { // 투망일경우
							if (pc.isInvisble()) {
								pc.getInventory().setEquipped(item, false);
								pc.beginInvisTimer();
							} else {
								pc.getInventory().setEquipped(item, false);
							}
						}
					}

					if (!pc.EquipChange) { // 2번슬롯
						pc.setSlotNumber(1);
						pc.getChangeSlot(1);
						pc.EquipChange = true;
						pc.sendPackets(new S_SkillSound(pc.getId(), 14994));
						pc.sendPackets(new S_SkillSound(pc.getId(), 14996));
						pc.sendPackets(new S_SystemMessage("장비 셋트 2번으로 변경되었습니다."));
					} else {
						pc.setSlotNumber(0);
						pc.getChangeSlot(0);
						pc.EquipChange = false;
						pc.sendPackets(new S_SkillSound(pc.getId(), 14994));
						pc.sendPackets(new S_SkillSound(pc.getId(), 14996));
						pc.sendPackets(new S_SystemMessage("장비 셋트 1번으로 변경되었습니다."));
					}
					pc.setEquipChange(curtime);
				} else if (itemId == 90000002) {
					ArrayList<Integer> New물약리스트 = new ArrayList<Integer>();
					for (int i = 0; i < pc.get_자동물약리스트().size(); i++) {
						if (pc.get_자동물약리스트().get(i) == 40010) {
							pc.get_자동물약리스트().remove(i);
						} else if (pc.get_자동물약리스트().get(i) == 40011) {
							pc.get_자동물약리스트().remove(i);
						} else if (pc.get_자동물약리스트().get(i) == 40012) {
							pc.get_자동물약리스트().remove(i);
						}
					}
					New물약리스트 = pc.get_자동물약리스트();
					New물약리스트.add(40010);
					pc.set_자동물약리스트(New물약리스트);
					pc.sendPackets(new S_SystemMessage("자동사냥 물약이 체력 회복제로 선택되었습니다."));
				} else if (itemId == 90000003) {
					ArrayList<Integer> New물약리스트 = new ArrayList<Integer>();
					for (int i = 0; i < pc.get_자동물약리스트().size(); i++) {
						if (pc.get_자동물약리스트().get(i) == 40010) {
							pc.get_자동물약리스트().remove(i);
						} else if (pc.get_자동물약리스트().get(i) == 40011) {
							pc.get_자동물약리스트().remove(i);
						} else if (pc.get_자동물약리스트().get(i) == 40012) {
							pc.get_자동물약리스트().remove(i);
						}
					}
					New물약리스트 = pc.get_자동물약리스트();
					New물약리스트.add(40011);
					pc.set_자동물약리스트(New물약리스트);
					pc.sendPackets(new S_SystemMessage("자동사냥 물약이 고급 체력 회복제로 선택되었습니다."));
				} else if (itemId == 90000004) {
					ArrayList<Integer> New물약리스트 = new ArrayList<Integer>();
					for (int i = 0; i < pc.get_자동물약리스트().size(); i++) {
						if (pc.get_자동물약리스트().get(i) == 40010) {
							pc.get_자동물약리스트().remove(i);
						} else if (pc.get_자동물약리스트().get(i) == 40011) {
							pc.get_자동물약리스트().remove(i);
						} else if (pc.get_자동물약리스트().get(i) == 40012) {
							pc.get_자동물약리스트().remove(i);
						}
					}
					New물약리스트 = pc.get_자동물약리스트();
					New물약리스트.add(40012);
					pc.set_자동물약리스트(New물약리스트);
					pc.sendPackets(new S_SystemMessage("자동사냥 물약이 강력 체력 회복제로 선택되었습니다."));
				} else if (itemId == 41121) {
					if (pc.getQuest().get_step(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END
							|| pc.getInventory().checkItem(41122, 1)) {
						pc.sendPackets(new S_ServerMessage(79));
					} else {
						createNewItem(pc, 41122, 1);
					}
				} else if (itemId == 41130) {
					if (pc.getQuest().get_step(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END
							|| pc.getInventory().checkItem(41131, 1)) {
						pc.sendPackets(new S_ServerMessage(79));
					} else {
						createNewItem(pc, 41131, 1);
					}
				} else if (itemId == 42501) { // 스톰 워크
					L1Teleport.teleport(pc, spellsc_x, spellsc_y, pc.getMapId(), pc.getMoveState().getHeading(), true,
							L1Teleport.CHANGE_POSITION);
				} else if (itemId == 50101) { // 위치막대
					IdentMapWand(pc, spellsc_x, spellsc_y);
				} else if (itemId == 50102) { // 위치변경막대
					MapFixKeyWand(pc, spellsc_x, spellsc_y);
				} else if (itemId == 430116) { // 드키
					if (AntarasRaidSystem.getInstance().startRaid(pc)) {
						L1World.getInstance().broadcastServerMessage(
								"강철 길드 난쟁이: 으...드래곤의 울부짖음이 여기까지 들리오. 필시 누군가 드래곤 포탈을 연 것이 확실하오! 준비된 드래곤 슬레이어에게 영광과 축복을!");
						pc.getInventory().removeItem(useItem, 1);
					}
				} else if (itemId == L1ItemId.CHANGING_PETNAME_SCROLL) {
					if (l1iteminstance1.getItem().getItemId() == 40314
							|| l1iteminstance1.getItem().getItemId() == 40316) {
						L1Pet petTemplate = PetTable.getInstance().getTemplate(l1iteminstance1.getId());
						L1Npc l1npc = NpcTable.getInstance().getTemplate(petTemplate.get_npcid());
						petTemplate.set_name(l1npc.get_name());
						PetTable.getInstance().storePet(petTemplate);
						L1ItemInstance item = pc.getInventory().getItem(l1iteminstance1.getId());
						pc.getInventory().updateItem(item);
						pc.getInventory().removeItem(useItem, 1);
						pc.sendPackets(new S_ServerMessage(1322, l1npc.get_name()));
						pc.sendPackets(new S_ChangeName(petTemplate.get_objid(), l1npc.get_name()));
						Broadcaster.broadcastPacket(pc, new S_ChangeName(petTemplate.get_objid(), l1npc.get_name()));
					} else {
						pc.sendPackets(new S_ServerMessage(1164));
					}
				} else if (itemId == 41260) { // 신
					for (L1Object object : L1World.getInstance().getVisibleObjects(pc, 3)) {
						if (object instanceof L1EffectInstance) {
							if (((L1NpcInstance) object).getNpcTemplate().get_npcId() == 81170) {
								pc.sendPackets(new S_ServerMessage(1162)); // 벌써
																			// 주위에
																			// 모닥불이
																			// 있습니다.
								return;
							}
						}
					}
					int[] loc = new int[2];
					loc = CharPosUtil.getFrontLoc(pc.getX(), pc.getY(), pc.getMoveState().getHeading());
					L1EffectSpawn.getInstance().spawnEffect(81170, 600000, loc[0], loc[1], pc.getMapId());
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41345) { // 산성의 유액
					L1DamagePoison.doInfection(pc, pc, 3000, 5);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41315) { // 성수
					if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
						return;
					}
					if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
						pc.getSkillEffectTimerSet().removeSkillEffect(STATUS_HOLY_MITHRIL_POWDER);
					}
					pc.getSkillEffectTimerSet().setSkillEffect(STATUS_HOLY_WATER, 900 * 1000);
					pc.sendPackets(new S_SkillSound(pc.getId(), 190));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 190));
					pc.sendPackets(new S_ServerMessage(1141));
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41316) { // 신성한 미스리르파우다
					if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
						return;
					}
					if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_WATER)) {
						pc.getSkillEffectTimerSet().removeSkillEffect(STATUS_HOLY_WATER);
					}
					pc.getSkillEffectTimerSet().setSkillEffect(STATUS_HOLY_MITHRIL_POWDER, 900 * 1000);
					pc.sendPackets(new S_SkillSound(pc.getId(), 190));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 190));
					pc.sendPackets(new S_ServerMessage(1142));
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41354) { // 신성한 에바의 물
					if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_WATER)
							|| pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
																	// 일어나지
																	// 않았습니다.
						return;
					}
					pc.getSkillEffectTimerSet().setSkillEffect(STATUS_HOLY_WATER_OF_EVA, 900 * 1000);
					pc.sendPackets(new S_SkillSound(pc.getId(), 190));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 190));
					pc.sendPackets(new S_ServerMessage(1140));
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == L1ItemId.CHANGING_SEX_POTION) { // 성전환 물약
					int[] MALE_LIST = new int[] { 0, 61, 138, 734, 2786, 6658, 6671 };
					int[] FEMALE_LIST = new int[] { 1, 48, 37, 1186, 2796, 6661, 6650 };
					if (pc.get_sex() == 0) {
						pc.set_sex(1);
						pc.setClassId(FEMALE_LIST[pc.getType()]);
					} else {
						pc.set_sex(0);
						pc.setClassId(MALE_LIST[pc.getType()]);
					}
					pc.getGfxId().setTempCharGfx(pc.getClassId());
					pc.sendPackets(new S_ChangeShape(pc.getId(), pc.getClassId()));
					Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), pc.getClassId()));
					pc.getInventory().removeItem(useItem, 1);
					/************************** 안타라스 리뉴얼 New System *****************************/
					/*
					 * } else if (itemId == L1ItemId.DRAGON_KEY){ if(useItem.getEndTime().getTime()
					 * < System.currentTimeMillis()){ pc.getInventory().removeItem(useItem);
					 * pc.sendPackets(new S_SystemMessage("사용 시간이 지나 삭제 합니다."));// 만약의 버그를 대비
					 * return; } AntarasRaidSystem.getInstance().startRaid(pc);
					 */
				} else if (itemId == L1ItemId.DRAGON_JEWEL_BOX) {
					int[] DRAGONSCALE = new int[] { 40393, 40394, 40395, 40396 };
					int bonus = _random.nextInt(100) + 1;
					int rullet = _random.nextInt(100) + 1;
					L1ItemInstance bonusitem = null;
					pc.getInventory().storeItem(L1ItemId.DRAGON_DIAMOND, 1);
					pc.sendPackets(new S_ServerMessage(403, "$7969"));
					pc.getInventory().removeItem(useItem, 1);
					if (bonus <= 3) {
						bonusitem = pc.getInventory().storeItem(DRAGONSCALE[rullet % DRAGONSCALE.length], 1);
						pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
					} else if (bonus >= 4 && bonus <= 8) {
						bonusitem = pc.getInventory().storeItem(L1ItemId.DRAGON_PEARL, 1);
						pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
					} else if (bonus >= 9 && bonus <= 15) {
						pc.getInventory().storeItem(L1ItemId.DRAGON_EMERALD, 1);
						pc.sendPackets(new S_ServerMessage(403, "$7969"));
					} else if (bonus >= 16 && bonus <= 25) {
						pc.getInventory().storeItem(L1ItemId.DRAGON_EMERALD, 1);
						pc.sendPackets(new S_ServerMessage(403, "$7969"));
					} else {
					}
				} else if (itemId == L1ItemId.DRAGON_EMERALD_BOX) {
					pc.getInventory().storeItem(L1ItemId.DRAGON_EMERALD, 1);
					pc.sendPackets(new S_ServerMessage(403, "$11518"));
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 437010 || itemId == 46171) {
					if (itemId == 46171) {
						if (pc.getLevel() > 52) {
							pc.sendPackets(new S_SystemMessage("초보 드래곤의 다이아몬드를 사용할 수 없습니다."));
							return;
						}
					}
					if (pc.getAinHasad() <= 1500000) {
						pc.calAinHasad(1000000);
						pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc.getAinHasad()));
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_SystemMessage("사용 불가: 아인하사드의 축복 최대 충전량 초과.")); // 사용 불가: 아인하사드의 축복 최대 충전량
																							// 초과
						return;
					}
				} else if (itemId == 430110) {
				    pc.cancelAbsoluteBarrier();

							L1SkillUse l1Skilluse = new L1SkillUse();
							l1Skilluse.handleCommands(pc, LIFE_MAAN, pc.getId(), pc.getX(), pc.getY(), null, 0,
									L1SkillUse.TYPE_GMBUFF);
				} else if (itemId == L1ItemId.FORTUNE_COOKIE) {
					pc.getInventory().removeItem(useItem, 1);
					pc.getInventory().storeItem(437022, 1);
					pc.sendPackets(new S_ServerMessage(403, "$8540"));
					int chance = _random.nextInt(42);
					if (chance == 0) {
						pc.getInventory().storeItem(437027, 1);
					} else if (chance >= 1 && chance <= 10) {

						pc.getInventory().storeItem(437028, 1);
					} else if (chance >= 11 && chance <= 40) {
						pc.getInventory().storeItem(437029, 1);
					} else if (chance == 41) {
						pc.getInventory().storeItem(437030, 1);
					}
					pc.sendPackets(new S_ServerMessage(403, "$8539"));

				} else if (itemId == 440001) { // 기감 시간 충전 주문서 추가
					int gTime = pc.getGdungeonTime() % 1000;
					int setTime = 0;
					int calgtime = gTime - 59;
					if (calgtime > 0) {
						setTime = pc.getGdungeonTime() - 59;
						pc.getInventory().removeItem(useItem, 1);
						pc.setGdungeonTime(setTime);
						pc.sendPackets(new S_SystemMessage("기란던젼 시간 충전 주문서(1시간)을  사용 하였습니다."));
					} else {
						pc.sendPackets(new S_SystemMessage("사용시간이 1시간 이하로 사용 할 수없습니다."));
					}
				} else if (itemId == 440002) {
					int gTime = pc.getGdungeonTime() % 1000;
					int setTime = 0;
					int calgtime = gTime - 119;
					if (calgtime > 0) {
						setTime = pc.getGdungeonTime() - 119;
						pc.getInventory().removeItem(useItem, 1);
						pc.setGdungeonTime(setTime);
						pc.sendPackets(new S_SystemMessage("기란던젼 시간 충전 주문서(2시간)을  사용 하였습니다."));
					} else {
						pc.sendPackets(new S_SystemMessage("사용시간이 2시간 이하로 사용 할 수없습니다."));
					}
				} else if (itemId == 440003) {
					int gTime = pc.getGdungeonTime() % 1000;
					int setTime = 0;
					int calgtime = gTime - 179;
					if (calgtime > 0) {
						setTime = pc.getGdungeonTime() - 179;
						pc.getInventory().removeItem(useItem, 1);
						pc.setGdungeonTime(setTime);
						pc.sendPackets(new S_SystemMessage("기란던젼 시간 충전 주문서(3시간)을  사용 하였습니다."));
					} else {
						pc.sendPackets(new S_SystemMessage("사용시간이 3시간 이하로 사용 할 수없습니다."));
					}
				} else {
					int locX = ((L1EtcItem) useItem.getItem()).get_locx();
					int locY = ((L1EtcItem) useItem.getItem()).get_locy();
					short mapId = ((L1EtcItem) useItem.getItem()).get_mapid();
					if (pc.getWanted() ==0 && WantedTeleportTable.getInstance().isWantedTeleportMap(mapId)) {
						pc.sendPackets(new S_SystemMessage("\\fT수배 상태에서만 사냥터로 이동 가능합니다."));
						return;
					}
					if (locX != 0 && locY != 0) {
						if (pc.getMap().isEscapable() || pc.isGm()) {
							L1Teleport.teleport(pc, locX, locY, mapId, pc.getMoveState().getHeading(), true);
							pc.getInventory().removeItem(useItem, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(647));
						}
						if (pc.getMapId() == 36 || pc.getMapId() == 13 || pc.getMapId() >= 49 && pc.getMapId() <= 51
								 || pc.getMapId() >= 522 && pc.getMapId() <= 524) {
							doTeleport(pc);
						}
						pc.cancelAbsoluteBarrier();
					} else {
						if (useItem.getCount() < 1) {
							pc.sendPackets(new S_ServerMessage(329, useItem.getLogName()));
						} else {
							pc.sendPackets(new S_ServerMessage(74, useItem.getLogName()));
						}
					}
				}
			}
			// 효과 지연이 있는 경우는 현재 시간을 세트
			if (isDelayEffect) {
				if (itemId == 410008 || itemId == 40414 || itemId == 700012 || itemId == 30043 || itemId == 30045
				/* || itemId == 702 */ || itemId == 30026 || itemId == 4100131 || itemId == 4100132) {
					int chargeCount = l1iteminstance.getChargeCount();
					Timestamp ts = new Timestamp(System.currentTimeMillis());
					l1iteminstance.setChargeCount(l1iteminstance.getChargeCount() - 1);
					if (chargeCount <= 1) {
						pc.getInventory().removeItem(l1iteminstance, 1);
					} else {
						l1iteminstance.setLastUsed(ts);
						pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_CHARGE_COUNT);
						pc.getInventory().saveItem(l1iteminstance, L1PcInventory.COL_CHARGE_COUNT);
					}
				} else {
					Timestamp ts = new Timestamp(System.currentTimeMillis());
					l1iteminstance.setLastUsed(ts);
					pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_DELAY_EFFECT);
					pc.getInventory().saveItem(l1iteminstance, L1PcInventory.COL_DELAY_EFFECT);
				}
			}
			L1ItemDelay.onItemUse(pc, useItem); // 아이템 지연 개시
		}
	}

	public static void useDragonPearl(L1PcInstance pc) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(71)) {
			pc.sendPackets(new S_ServerMessage(698));
			return;
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGONPERL)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.STATUS_DRAGONPERL);
			pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONPERL, 0, 0));
			Broadcaster.broadcastPacket(pc, new S_DRAGONPERL(pc.getId(), 0));
			pc.sendPackets(new S_DRAGONPERL(pc.getId(), 0));
		}
		pc.cancelAbsoluteBarrier();
		int time = 600 * 1000;
		int stime = ((time / 1000) / 4) - 2;
		pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_DRAGONPERL, time);
		pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONPERL, stime, 8));
		pc.sendPackets(new S_DRAGONPERL(pc.getId(), 8));
		Broadcaster.broadcastPacket(pc, new S_DRAGONPERL(pc.getId(), 8));
		pc.sendPackets(new S_SkillSound(pc.getId(), 197));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 197));
		pc.sendPackets(new S_Drawal(pc.getId(), 0)); // 일단주석
	}
	
	private void UseHeallingPotion(L1PcInstance pc, int healHp, int gfxid) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(71) == true) { // 디케이포션 상태
			pc.sendPackets(new S_ServerMessage(698)); // 마력에 의해 아무것도 마실 수가 없습니다.
			return;
		}

		// 앱솔루트베리어의 해제
		pc.cancelAbsoluteBarrier();

		pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));
		pc.sendPackets(new S_ServerMessage(77)); // \f1기분이 좋아졌습니다.
		healHp *= (_random.nextGaussian() / 5.0D) + 1.0D;
		if (pc.getSkillEffectTimerSet().hasSkillEffect(POLLUTE_WATER)) { // 포르트워타중은 회복량1/2배
			healHp /= 2;
		}
		pc.setCurrentHp(pc.getCurrentHp() + healHp);
	}

	// 천상의 물약
	private void UseExpPotion(L1PcInstance pc, int item_id) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(71) == true) { // 디케이포션 상태
			pc.sendPackets(new S_ServerMessage(698, "")); // 마력에 의해 아무것도 마실 수가 없습니다.
			return;
		}
		pc.cancelAbsoluteBarrier();

		int time = 0;
		if (item_id == L1ItemId.EXP_POTION || item_id == L1ItemId.EXP_POTION2) { // 경험치 상승 물약
			time = 1800;
		}

		if (pc.getSkillEffectTimerSet().hasSkillEffect(20081)) {
			pc.getSkillEffectTimerSet().removeSkillEffect(20081);
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(7289)) {
			pc.getSkillEffectTimerSet().removeSkillEffect(7289);
		}
		pc.getSkillEffectTimerSet().setSkillEffect(EXP_POTION, time * 1000);
		pc.sendPackets(new S_SkillSound(pc.getId(), 7013));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7013));
		pc.sendPackets(new S_ServerMessage(1313));
	}

	private void useCashScroll(L1PcInstance pc, int item_id) {
		int time = 3600;
		int scroll = 0;

		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CASHSCROLL)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_CASHSCROLL);
			pc.addHpr(-4);
			pc.addMaxHp(-50);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CASHSCROLL2)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_CASHSCROLL2);
			pc.addMpr(-4);
			pc.addMaxMp(-40);
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CASHSCROLL3)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_CASHSCROLL3);
			pc.addDmgup(-3);
			pc.addHitup(-3);
			pc.getAbility().addSp(-3);
			pc.sendPackets(new S_SPMR(pc));
		}

		if (item_id == L1ItemId.INCRESE_HP_SCROLL || item_id == L1ItemId.CHUNSANG_HP_SCROLL) {
			scroll = 6993;
			pc.addHpr(4);
			pc.addMaxHp(50);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		} else if (item_id == L1ItemId.INCRESE_MP_SCROLL || item_id == L1ItemId.CHUNSANG_MP_SCROLL) {
			scroll = 6994;
			pc.addMpr(4);
			pc.addMaxMp(40);
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		} else if (item_id == L1ItemId.INCRESE_ATTACK_SCROLL || item_id == L1ItemId.CHUNSANG_ATTACK_SCROLL) {
			scroll = 6995;
			pc.addDmgup(3);
			pc.addHitup(3);
			pc.getAbility().addSp(3);
			pc.sendPackets(new S_SPMR(pc));
		}

		pc.sendPackets(new S_SkillSound(pc.getId(), scroll));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), scroll));
		pc.getSkillEffectTimerSet().setSkillEffect(scroll, time * 1000);
	}

	private boolean createNewItem(L1PcInstance pc, int item_id, int count) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		if (item != null) {
			item.setCount(count);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else { // 가질 수 없는 경우는 지면에 떨어뜨리는 처리의 캔슬은 하지 않는다(부정 방지)
				L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0를 손에 넣었습니다.
			return true;
		} else {
			return false;
		}
	}

	private void useToiTeleportAmulet(L1PcInstance pc, int itemId, L1ItemInstance item) {
		boolean isTeleport = true;
		if (pc.getWanted() ==0 && (WantedTeleportTable.getInstance().isWantedTeleportMap(101) || WantedTeleportTable.getInstance().isWantedTeleportMap(102) || WantedTeleportTable.getInstance().isWantedTeleportMap(103)
				 || WantedTeleportTable.getInstance().isWantedTeleportMap(104) || WantedTeleportTable.getInstance().isWantedTeleportMap(105) || WantedTeleportTable.getInstance().isWantedTeleportMap(106)
				 || WantedTeleportTable.getInstance().isWantedTeleportMap(107) || WantedTeleportTable.getInstance().isWantedTeleportMap(108) || WantedTeleportTable.getInstance().isWantedTeleportMap(109)
				 || WantedTeleportTable.getInstance().isWantedTeleportMap(200))) {
			pc.sendPackets(new S_SystemMessage("\\fT수배 상태에서만 사냥터로 이동 가능합니다."));
			return;
		}
		if (itemId == 40289 || itemId == 40293) { // 11,51Famulet
			if (pc.getX() >= 32816 && pc.getX() <= 32821 && pc.getY() >= 32778 && pc.getY() <= 32783
					&& pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40290 || itemId == 40294) { // 21,61Famulet
			if (pc.getX() >= 32815 && pc.getX() <= 32820 && pc.getY() >= 32815 && pc.getY() <= 32820
					&& pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40291 || itemId == 40295) { // 31,71Famulet
			if (pc.getX() >= 32779 && pc.getX() <= 32784 && pc.getY() >= 32778 && pc.getY() <= 32783
					&& pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40292 || itemId == 40296) { // 41,81Famulet
			if (pc.getX() >= 32779 && pc.getX() <= 32784 && pc.getY() >= 32815 && pc.getY() <= 32820
					&& pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40297) { // 91Famulet
			if (pc.getX() >= 32706 && pc.getX() <= 32710 && pc.getY() >= 32909 && pc.getY() <= 32913
					&& pc.getMapId() == 190) {
				isTeleport = true;
			}
		}
		if (isTeleport) {
			if (pc.getMapId() == 70 || pc.getMapId() == 200 || pc.getMapId() == 5153) {
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				pc.sendPackets(new S_ServerMessage(647));
				return;
			}
			L1Teleport.teleport(pc, item.getItem().get_locx(), item.getItem().get_locy(), item.getItem().get_mapid(), 5,
					true);
		} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
		}
	}

	private void useToiTeleportAmulet1(L1PcInstance pc, int itemId, L1ItemInstance item) {
		boolean isTeleport = true;
		if (pc.getWanted() ==0 && (WantedTeleportTable.getInstance().isWantedTeleportMap(101) || WantedTeleportTable.getInstance().isWantedTeleportMap(102) || WantedTeleportTable.getInstance().isWantedTeleportMap(103)
				 || WantedTeleportTable.getInstance().isWantedTeleportMap(104) || WantedTeleportTable.getInstance().isWantedTeleportMap(105) || WantedTeleportTable.getInstance().isWantedTeleportMap(106)
				 || WantedTeleportTable.getInstance().isWantedTeleportMap(107) || WantedTeleportTable.getInstance().isWantedTeleportMap(108) || WantedTeleportTable.getInstance().isWantedTeleportMap(109)
				 || WantedTeleportTable.getInstance().isWantedTeleportMap(200))) {
			pc.sendPackets(new S_SystemMessage("\\fT수배 상태에서만 사냥터로 이동 가능합니다."));
			return;
		}
		if (itemId == 5370617) { // 11,51Famulet
			if (pc.getX() >= 32704 && pc.getX() <= 32895 && pc.getY() >= 32704 && pc.getY() <= 32895
					&& pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40290 || itemId == 40294) { // 21,61Famulet
			if (pc.getX() >= 32815 && pc.getX() <= 32820 && pc.getY() >= 32815 && pc.getY() <= 32820
					&& pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40291 || itemId == 40295) { // 31,71Famulet
			if (pc.getX() >= 32779 && pc.getX() <= 32784 && pc.getY() >= 32778 && pc.getY() <= 32783
					&& pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40292 || itemId == 40296) { // 41,81Famulet
			if (pc.getX() >= 32779 && pc.getX() <= 32784 && pc.getY() >= 32815 && pc.getY() <= 32820
					&& pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40297) { // 91Famulet
			if (pc.getX() >= 32706 && pc.getX() <= 32710 && pc.getY() >= 32909 && pc.getY() <= 32913
					&& pc.getMapId() == 190) {
				isTeleport = true;
			}
		}
		
		if (isTeleport) {
		    if (pc.getMapId() == 70 || pc.getMapId() == 200 || pc.getMapId() == 5153) {
		        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		        pc.sendPackets(new S_ServerMessage(647));
		        return;
		    }
		    L1Teleport.teleport(pc, item.getItem().get_locx(), item.getItem().get_locy(), item.getItem().get_mapid(), 5, true);

			if (pc.getMapId() == 101 && pc.getInventory().checkItem(5370617)) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배1층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배1층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배2층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배2층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배3층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배3층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배4층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배4층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배5층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배5층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배6층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배6층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배7층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배7층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배8층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배8층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배9층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배9층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배정상층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배정상층버프);
				}
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.오만지배1층버프, 1000 * 60 * 60 * 24);
				pc.addMaxHp(100);
				pc.addDmgup(5);
				pc.addBowDmgup(5);
				pc.getAbility().addSp(5);
				pc.addDamageReductionByArmor(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_SystemMessage("\\fT(HP+100,추가대미지+5,SP+5,대미지감소+2)"));
				pc.sendPackets(new S_SystemMessage("\\fT오만의 탑 지배버프가 발동되었습니다."));
			}
			if (pc.getMapId() == 102 && pc.getInventory().checkItem(5370618)) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배1층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배1층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배2층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배2층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배3층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배3층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배4층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배4층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배5층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배5층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배6층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배6층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배7층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배7층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배8층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배8층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배9층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배9층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배정상층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배정상층버프);
				}
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.오만지배2층버프, 1000 * 60 * 60 * 24);
				pc.addMaxHp(100);
				pc.addDmgup(5);
				pc.addBowDmgup(5);
				pc.getAbility().addSp(5);
				pc.addDamageReductionByArmor(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_SystemMessage("\\fT(HP+100,추가대미지+5,SP+5,대미지감소+2)"));
				pc.sendPackets(new S_SystemMessage("\\fT오만의 탑 지배버프가 발동되었습니다."));
			}
			if (pc.getMapId() == 103 && pc.getInventory().checkItem(5370619)) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배1층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배1층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배2층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배2층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배3층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배3층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배4층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배4층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배5층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배5층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배6층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배6층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배7층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배7층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배8층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배8층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배9층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배9층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배정상층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배정상층버프);
				}
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.오만지배3층버프, 1000 * 60 * 60 * 24);
				pc.addMaxHp(100);
				pc.addDmgup(5);
				pc.addBowDmgup(5);
				pc.getAbility().addSp(5);
				pc.addDamageReductionByArmor(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_SystemMessage("\\fT(HP+100,추가대미지+5,SP+5,대미지감소+2)"));
				pc.sendPackets(new S_SystemMessage("\\fT오만의 탑 지배버프가 발동되었습니다."));
			}
			if (pc.getMapId() == 104 && pc.getInventory().checkItem(5370620)) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배1층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배1층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배2층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배2층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배3층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배3층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배4층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배4층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배5층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배5층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배6층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배6층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배7층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배7층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배8층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배8층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배9층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배9층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배정상층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배정상층버프);
				}
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.오만지배4층버프, 1000 * 60 * 60 * 24);
				pc.addMaxHp(100);
				pc.addDmgup(5);
				pc.addBowDmgup(5);
				pc.getAbility().addSp(5);
				pc.addDamageReductionByArmor(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_SystemMessage("\\fT(HP+100,추가대미지+5,SP+5,대미지감소+2)"));
				pc.sendPackets(new S_SystemMessage("\\fT오만의 탑 지배버프가 발동되었습니다."));
			}
			if (pc.getMapId() == 105 && pc.getInventory().checkItem(5370621)) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배1층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배1층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배2층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배2층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배3층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배3층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배4층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배4층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배5층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배5층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배6층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배6층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배7층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배7층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배8층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배8층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배9층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배9층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배정상층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배정상층버프);
				}
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.오만지배5층버프, 1000 * 60 * 60 * 24);
				pc.addMaxHp(100);
				pc.addDmgup(5);
				pc.addBowDmgup(5);
				pc.getAbility().addSp(5);
				pc.addDamageReductionByArmor(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_SystemMessage("\\fT(HP+100,추가대미지+5,SP+5,대미지감소+2)"));
				pc.sendPackets(new S_SystemMessage("\\fT오만의 탑 지배버프가 발동되었습니다."));
			}
			if (pc.getMapId() == 106 && pc.getInventory().checkItem(5370622)) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배1층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배1층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배2층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배2층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배3층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배3층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배4층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배4층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배5층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배5층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배6층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배6층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배7층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배7층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배8층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배8층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배9층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배9층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배정상층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배정상층버프);
				}
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.오만지배6층버프, 1000 * 60 * 60 * 24);
				pc.addMaxHp(100);
				pc.addDmgup(5);
				pc.addBowDmgup(5);
				pc.getAbility().addSp(5);
				pc.addDamageReductionByArmor(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_SystemMessage("\\fT(HP+100,추가대미지+5,SP+5,대미지감소+2)"));
				pc.sendPackets(new S_SystemMessage("\\fT오만의 탑 지배버프가 발동되었습니다."));
			}
			if (pc.getMapId() == 107 && pc.getInventory().checkItem(5370623)) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배1층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배1층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배2층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배2층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배3층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배3층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배4층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배4층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배5층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배5층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배6층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배6층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배7층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배7층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배8층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배8층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배9층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배9층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배정상층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배정상층버프);
				}
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.오만지배7층버프, 1000 * 60 * 60 * 24);
				pc.addMaxHp(100);
				pc.addDmgup(5);
				pc.addBowDmgup(5);
				pc.getAbility().addSp(5);
				pc.addDamageReductionByArmor(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_SystemMessage("\\fT(HP+100,추가대미지+5,SP+5,대미지감소+2)"));
				pc.sendPackets(new S_SystemMessage("\\fT오만의 탑 지배버프가 발동되었습니다."));
			}
			if (pc.getMapId() == 108 && pc.getInventory().checkItem(5370624)) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배1층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배1층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배2층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배2층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배3층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배3층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배4층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배4층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배5층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배5층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배6층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배6층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배7층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배7층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배8층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배8층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배9층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배9층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배정상층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배정상층버프);
				}
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.오만지배8층버프, 1000 * 60 * 60 * 24);
				pc.addMaxHp(100);
				pc.addDmgup(5);
				pc.addBowDmgup(5);
				pc.getAbility().addSp(5);
				pc.addDamageReductionByArmor(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_SystemMessage("\\fT(HP+100,추가대미지+5,SP+5,대미지감소+2)"));
				pc.sendPackets(new S_SystemMessage("\\fT오만의 탑 지배버프가 발동되었습니다."));
			}
			if (pc.getMapId() == 109 && pc.getInventory().checkItem(5370625)) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배1층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배1층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배2층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배2층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배3층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배3층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배4층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배4층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배5층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배5층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배6층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배6층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배7층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배7층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배8층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배8층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배9층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배9층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배정상층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배정상층버프);
				}
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.오만지배9층버프, 1000 * 60 * 60 * 24);
				pc.addMaxHp(100);
				pc.addDmgup(5);
				pc.addBowDmgup(5);
				pc.getAbility().addSp(5);
				pc.addDamageReductionByArmor(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_SystemMessage("\\fT(HP+100,추가대미지+5,SP+5,대미지감소+2)"));
				pc.sendPackets(new S_SystemMessage("\\fT오만의 탑 지배버프가 발동되었습니다."));
			}
			if (pc.getMapId() == 111 && pc.getInventory().checkItem(5370626)) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배1층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배1층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배2층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배2층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배3층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배3층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배4층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배4층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배5층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배5층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배6층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배6층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배7층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배7층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배8층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배8층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배9층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배9층버프);
				} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.오만지배정상층버프)) {
					pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.오만지배정상층버프);
				}
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.오만지배정상층버프, 1000 * 60 * 60 * 24);
				pc.addMaxHp(100);
				pc.addDmgup(5);
				pc.addBowDmgup(5);
				pc.getAbility().addSp(5);
				pc.addDamageReductionByArmor(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_SystemMessage("\\fT(HP+100,추가대미지+5,SP+5,대미지감소+2)"));
				pc.sendPackets(new S_SystemMessage("\\fT오만의 탑 지배버프가 발동되었습니다."));
			}
		} else {
		    pc.sendPackets(new S_ServerMessage(79));
		}
	}

	private void usePolyRangking(L1PcInstance pc, int itemId) {
		int polyid = 0;
		if (itemId == 3000392) {
			if (pc.isCrown()) {
				if (pc.get_sex() == 0)
					polyid = 13715;
				else
					polyid = 13717;
			} else if (pc.isKnight()) {
				if (pc.get_sex() == 0)
					polyid = 15115;
				else
					polyid = 13721;
			} else if (pc.isElf()) {
				if (pc.get_sex() == 0)
					polyid = 13723;
				else
					polyid = 13725;
			} else if (pc.isWizard()) {
				if (pc.get_sex() == 0)
					polyid = 13727;
				else
					polyid = 13729;
			} else if (pc.isDarkelf()) {
				if (pc.get_sex() == 0)
					polyid = 13731;
				else
					polyid = 13733;
			}
			L1PolyMorph.doPoly(pc, polyid, 3600, L1PolyMorph.MORPH_BY_ITEMMAGIC);
		}
	}

	private boolean withdrawPet(L1PcInstance pc, int itemObjectId) {
		if (!pc.getMap().isTakePets()) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1 여기에서는 사용할 수 없습니다.
			return false;
		}

		int petCost = 0;
		Object[] petList = pc.getPetList().values().toArray();
		for (Object pet : petList) {
			if (pet instanceof L1PetInstance) {
				if (((L1PetInstance) pet).getItemObjId() == itemObjectId) { // 이미 꺼내고 있는 애완동물
					return false;
				}
			}
			petCost += ((L1NpcInstance) pet).getPetcost();
		}
		int charisma = pc.getAbility().getTotalCha();
		if (pc.isCrown()) { // CROWN
			charisma += 6;
		} else if (pc.isElf()) { // ELF
			charisma += 12;
		} else if (pc.isWizard()) { // WIZ
			charisma += 6;
		} else if (pc.isDarkelf()) { // DE
			charisma += 6;
		}

		charisma -= petCost;
		int petCount = charisma / 6;
		if (petCount <= 0) {
			pc.sendPackets(new S_ServerMessage(489)); // 물러가려고 하는 애완동물이 너무 많습니다.
			return false;
		}

		L1Pet l1pet = PetTable.getInstance().getTemplate(itemObjectId);
		if (l1pet != null) {
			L1Npc npcTemp = NpcTable.getInstance().getTemplate(l1pet.get_npcid());
			L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
			pet.setPetcost(6);
			pet.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_PET_FOOD, pet.getFoodTime() * 1000);
		}
		return true;
	}

	private void IdentMapWand(L1PcInstance pc, int locX, int locY) {
		pc.sendPackets(new S_SystemMessage("Gab :" + pc.getMap().getOriginalTile(locX, locY) + ",x :" + locX + ",y :"
				+ locY + ", mapId :" + pc.getMapId()));
		if (pc.getMap().isCloseZone(locX, locY)) {
			pc.sendPackets(new S_EffectLocation(locX, locY, 10));
			Broadcaster.broadcastPacket(pc, new S_EffectLocation(locX, locY, 10));
			pc.sendPackets(new S_SystemMessage("벽으로 인식중"));
		}
	}

	private void MapFixKeyWand(L1PcInstance pc, int locX, int locY) {
		String key = new StringBuilder().append(pc.getMapId()).append(locX).append(locY).toString();
		if (!pc.getMap().isCloseZone(locX, locY)) {
			if (!MapFixKeyTable.getInstance().isLockey(key)) {
				MapFixKeyTable.getInstance().storeLocFix(locX, locY, pc.getMapId());
				pc.sendPackets(new S_EffectLocation(locX, locY, 1815));
				Broadcaster.broadcastPacket(pc, new S_EffectLocation(locX, locY, 1815));
				pc.sendPackets(new S_SystemMessage("key추가 ,x :" + locX + ",y :" + locY + ", mapId :" + pc.getMapId()));
			}
		} else {
			pc.sendPackets(new S_SystemMessage("선택좌표는 벽이 아닙니다."));

			if (MapFixKeyTable.getInstance().isLockey(key)) {
				MapFixKeyTable.getInstance().deleteLocFix(locX, locY, pc.getMapId());
				pc.sendPackets(new S_EffectLocation(locX, locY, 10));
				Broadcaster.broadcastPacket(pc, new S_EffectLocation(locX, locY, 10));
				pc.sendPackets(new S_SystemMessage("key삭제 ,x :" + locX + ",y :" + locY + ", mapId :" + pc.getMapId()));
			}
		}
	}

	private boolean CheckEffects(L1PcInstance pc) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(7671) || pc.getSkillEffectTimerSet().hasSkillEffect(7672)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(7673) || pc.getSkillEffectTimerSet().hasSkillEffect(7674)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(7675) || pc.getSkillEffectTimerSet().hasSkillEffect(7676)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(7677)) {
			pc.sendPackets(new S_ServerMessage(1594));
			return false;
		}
		return true;
	}

	private void UseArmor(L1PcInstance activeChar, L1ItemInstance armor) {
		boolean equipeSpace;
		int type = armor.getItem().getType();
		L1PcInventory pcInventory = activeChar.getInventory();
		if (type == 9) {
			equipeSpace = (pcInventory.getTypeEquipped(2, 9) <= 1);
		} else {
			equipeSpace = (pcInventory.getTypeEquipped(2, type) <= 0);
		}
		if (!armor.isEquipped()) {
			if (!equipeSpace) {
				L1ItemInstance oldArmor = pcInventory.getItemEquipped(2, type);
				if (oldArmor != null) {
					if ((oldArmor.getItemId() == 20077 || oldArmor.getItemId() == 20062
							|| oldArmor.getItemId() == 120077) && activeChar.isInvisble()) {
						activeChar.beginInvisTimer();
						activeChar.getInventory().setEquipped(oldArmor, false);
					}
					pcInventory.setEquipped(oldArmor, false);
				}
			} else if (type == 7) {
				if (activeChar.getWeapon() != null)
					if (activeChar.getWeapon().getItem().isTwohandedWeapon() && armor.getItem().getUseType() != 13) {
						activeChar.sendPackets((ServerBasePacket) new S_ServerMessage(129));
						return;
					}
				L1ItemInstance oldArmor = pcInventory.getItemEquipped(2, 13);
				if (oldArmor != null)
					pcInventory.setEquipped(oldArmor, false);
			} else if (type == 13) {
				L1ItemInstance oldArmor = pcInventory.getItemEquipped(2, 7);
				if (oldArmor != null)
					pcInventory.setEquipped(oldArmor, false);
			}
			int polyid = activeChar.getTempCharGfx();
			if (!L1PolyMorph.isEquipableArmor(polyid, type))
				return;
			if (type == 7 && activeChar.getWeapon() != null)
				if (activeChar.getWeapon().getItem().isTwohandedWeapon() && armor.getItem().getUseType() != 13) {
					activeChar.sendPackets((ServerBasePacket) new S_ServerMessage(129));
					return;
				}
			if (type == 9 && armor.getItem().getItemId() == 20291
					&& pcInventory.getTypeAndItemIdEquipped(2, 9, 20291) >= 1) {
				activeChar.sendPackets((ServerBasePacket) new S_SystemMessage("해당 아이템은 한 개만 착용 할 수 있습니다."));
				return;
			}
			pcInventory.setEquipped(armor, true);
		} else {
			if (armor.getItem().getBless() == 2) {
				activeChar.sendPackets((ServerBasePacket) new S_ServerMessage(150));
				return;
			}
			pcInventory.setEquipped(armor, false);
		}
		activeChar.setCurrentHp(activeChar.getCurrentHp());
		activeChar.setCurrentMp(activeChar.getCurrentMp());
		activeChar.sendPackets((ServerBasePacket) new S_OwnCharAttrDef(activeChar));
		activeChar.sendPackets((ServerBasePacket) new S_OwnCharStatus(activeChar));
		activeChar.sendPackets((ServerBasePacket) new S_SPMR(activeChar));
	}

	private void UseWeapon(L1PcInstance activeChar, L1ItemInstance weapon) {
		L1PcInventory pcInventory = activeChar.getInventory();
		if (activeChar.getWeapon() == null || !activeChar.getWeapon().equals(weapon)) {
			int weapon_type = weapon.getItem().getType();
			int polyid = activeChar.getTempCharGfx();
			if (!L1PolyMorph.isEquipableWeapon(polyid, weapon_type))
				return;
			if (weapon.getItem().isTwohandedWeapon() && pcInventory.getTypeEquipped(2, 7) >= 1) {
				activeChar.sendPackets((ServerBasePacket) new S_ServerMessage(128));
				return;
			}
		}

		if (activeChar.getWeapon() != null) {
			if (activeChar.getWeapon().getItem().getBless() == 2) {
				activeChar.sendPackets((ServerBasePacket) new S_ServerMessage(150));
				return;
			}
			if (activeChar.getWeapon().equals(weapon)) {
				pcInventory.setEquipped(activeChar.getWeapon(), false, false, false);
				return;
			}
			pcInventory.setEquipped(activeChar.getWeapon(), false, false, true);
		}
		if (weapon.getItemId() == 200002)
			activeChar.sendPackets((ServerBasePacket) new S_ServerMessage(149, weapon.getLogName()));
		pcInventory.setEquipped(weapon, true, false, false);
	}

	private void 통합기술서(L1PcInstance pc) {
		String s = "";
		int l2 = 0;
		int i3 = 0;
		L1Skills l1skills = null;
		for (int skillId = 87; skillId <= 91; skillId++) {
			l1skills = SkillsTable.getInstance().getTemplate(skillId);
			s = l1skills.getName();
			int l6 = l1skills.getSkillLevel();
			int i7 = l1skills.getId();
			switch (l6) {
			case 11:
				l2 = i7;
				break;
			case 12:
				i3 = i7;
				break;
			}
			pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, l2, i3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0));
			SkillsTable.getInstance().spellMastery(pc.getId(), skillId, s, 0, 0);
		}
	}

	private void 통합군주마법서(L1PcInstance pc) {
		String s = "";
		int l3 = 0;
		L1Skills l1skills = null;
		for (int skillId = 113; skillId <= 118; skillId++) {
			l1skills = SkillsTable.getInstance().getTemplate(skillId);
			s = l1skills.getName();
			int l6 = l1skills.getSkillLevel();
			int i7 = l1skills.getId();
			switch (l6) {
			case 15:
				l3 = i7;
				break;
			}
			pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, l3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0));
			SkillsTable.getInstance().spellMastery(pc.getId(), skillId, s, 0, 0);
		}
	}

	private void 통합흑정령마법(L1PcInstance pc) {
		String s = "";
		int j3 = 0;
		int k3 = 0;
		L1Skills l1skills = null;
		for (int skillId = 97; skillId <= 111; skillId++) {
			l1skills = SkillsTable.getInstance().getTemplate(skillId);
			s = l1skills.getName();
			int l6 = l1skills.getSkillLevel();
			int i7 = l1skills.getId();
			switch (l6) {
			case 13:
				j3 = i7;
				break;
			case 14:
				k3 = i7;
				break;
			}
			pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, j3, k3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0));
			SkillsTable.getInstance().spellMastery(pc.getId(), skillId, s, 0, 0);
		}
	}

	private void 통합공통정령마법(L1PcInstance pc) {
		String s = "";
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		L1Skills l1skills = null;
		for (int skillId = 129; skillId <= 165; skillId++) {
			if (skillId == L1SkillId.COUNTER_MIRROR || skillId == L1SkillId.FIRE_WEAPON
					|| skillId == L1SkillId.WIND_SHOT || skillId == L1SkillId.WIND_WALK
					|| skillId == L1SkillId.EARTH_SKIN || skillId == L1SkillId.ENTANGLE
					|| skillId == L1SkillId.FIRE_BLESS || skillId == L1SkillId.STORM_EYE
					|| skillId == L1SkillId.EARTH_BIND || skillId == L1SkillId.EARTH_BLESS
					|| skillId == L1SkillId.AQUA_PROTECTER || skillId == L1SkillId.BURNING_WEAPON
					|| skillId == L1SkillId.NATURES_BLESSING)
				l1skills = SkillsTable.getInstance().getTemplate(skillId);
			s = l1skills.getName();
			int l6 = l1skills.getSkillLevel();
			int i7 = l1skills.getId();
			switch (l6) {
			case 17:
				j4 = i7;
				break;
			case 18:
				k4 = i7;
				break;
			case 19:
				l4 = i7;
				break;
			case 20:
				i5 = i7;
				break;
			case 21:
				j5 = i7;
				break;
			}
			pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, j4, k4, l4, i5, j5, 0, 0, 0,
					0, 0, 0, 0));
			SkillsTable.getInstance().spellMastery(pc.getId(), skillId, s, 0, 0);
		}
	}

	private void 통합불정령마법(L1PcInstance pc) {
		String s = "";
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		L1Skills l1skills = null;
		for (int skillId = 148; skillId <= 176; skillId++) {
			if (skillId == L1SkillId.WIND_SHOT || skillId == L1SkillId.WIND_WALK || skillId == L1SkillId.EARTH_SKIN
					|| skillId == L1SkillId.ENTANGLE || skillId == L1SkillId.ERASE_MAGIC
					|| skillId == L1SkillId.LESSER_ELEMENTAL || skillId == L1SkillId.STORM_EYE
					|| skillId == L1SkillId.EARTH_BIND || skillId == L1SkillId.NATURES_TOUCH
					|| skillId == L1SkillId.EARTH_BLESS || skillId == L1SkillId.AQUA_PROTECTER
					|| skillId == L1SkillId.AREA_OF_SILENCE || skillId == L1SkillId.GREATER_ELEMENTAL
					|| skillId == L1SkillId.NATURES_BLESSING || skillId == L1SkillId.CALL_OF_NATURE
					|| skillId == L1SkillId.STORM_SHOT || skillId == L1SkillId.WIND_SHACKLE
					|| skillId == L1SkillId.IRON_SKIN || skillId == L1SkillId.EXOTIC_VITALIZE
					|| skillId == L1SkillId.WATER_LIFE || skillId == L1SkillId.STORM_WALK
					|| skillId == L1SkillId.POLLUTE_WATER || skillId == L1SkillId.STRIKER_GALE
					|| skillId == L1SkillId.SOUL_OF_FLAME) {
				continue;
			}
			l1skills = SkillsTable.getInstance().getTemplate(skillId);
			s = l1skills.getName();
			int l6 = l1skills.getSkillLevel();
			int i7 = l1skills.getId();
			switch (l6) {
			case 19:
				l4 = i7;
				break;
			case 20:
				i5 = i7;
				break;
			case 21:
				j5 = i7;
				break;
			case 22:
				k5 = i7;
				break;
			}
			pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, l4, i5, j5, k5, 0, 0, 0,
					0, 0, 0));
			SkillsTable.getInstance().spellMastery(pc.getId(), skillId, s, 0, 0);
		}
	}

	private void 통합바람정령마법(L1PcInstance pc) {
		String s = "";
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		L1Skills l1skills = null;
		for (int skillId = 148; skillId <= 176; skillId++) {
			if (skillId == L1SkillId.FIRE_WEAPON || skillId == L1SkillId.EARTH_SKIN || skillId == L1SkillId.ENTANGLE
					|| skillId == L1SkillId.ERASE_MAGIC || skillId == L1SkillId.LESSER_ELEMENTAL
					|| skillId == L1SkillId.FIRE_BLESS || skillId == L1SkillId.EARTH_BIND
					|| skillId == L1SkillId.NATURES_TOUCH || skillId == L1SkillId.EARTH_BLESS
					|| skillId == L1SkillId.AQUA_PROTECTER || skillId == L1SkillId.AREA_OF_SILENCE
					|| skillId == L1SkillId.GREATER_ELEMENTAL || skillId == L1SkillId.BURNING_WEAPON
					|| skillId == L1SkillId.NATURES_BLESSING || skillId == L1SkillId.CALL_OF_NATURE
					|| skillId == L1SkillId.IRON_SKIN || skillId == L1SkillId.EXOTIC_VITALIZE
					|| skillId == L1SkillId.WATER_LIFE || skillId == L1SkillId.ELEMENTAL_FIRE
					|| skillId == L1SkillId.POLLUTE_WATER || skillId == L1SkillId.SOUL_OF_FLAME
					|| skillId == L1SkillId.ADDITIONAL_FIRE || skillId == L1SkillId.STRIKER_GALE) {
				continue;
			}
			l1skills = SkillsTable.getInstance().getTemplate(skillId);
			s = l1skills.getName();
			int l6 = l1skills.getSkillLevel();
			int i7 = l1skills.getId();
			switch (l6) {
			case 19:
				l4 = i7;
				break;
			case 20:
				i5 = i7;
				break;
			case 21:
				j5 = i7;
				break;
			case 22:
				k5 = i7;
				break;
			}
			pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, l4, i5, j5, k5, 0, 0, 0,
					0, 0, 0));
			SkillsTable.getInstance().spellMastery(pc.getId(), skillId, s, 0, 0);
		}
	}

	private void 통합물정령마법(L1PcInstance pc) {
		String s = "";
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		L1Skills l1skills = null;
		for (int skillId = 148; skillId <= 176; skillId++) {
			if (skillId == L1SkillId.FIRE_WEAPON || skillId == L1SkillId.WIND_SHOT || skillId == L1SkillId.WIND_WALK
					|| skillId == L1SkillId.EARTH_SKIN || skillId == L1SkillId.ENTANGLE
					|| skillId == L1SkillId.ERASE_MAGIC || skillId == L1SkillId.LESSER_ELEMENTAL
					|| skillId == L1SkillId.FIRE_BLESS || skillId == L1SkillId.STORM_EYE
					|| skillId == L1SkillId.EARTH_BIND || skillId == L1SkillId.EARTH_BLESS
					|| skillId == L1SkillId.AREA_OF_SILENCE || skillId == L1SkillId.GREATER_ELEMENTAL
					|| skillId == L1SkillId.BURNING_WEAPON || skillId == L1SkillId.NATURES_BLESSING
					|| skillId == L1SkillId.CALL_OF_NATURE || skillId == L1SkillId.STORM_SHOT
					|| skillId == L1SkillId.WIND_SHACKLE || skillId == L1SkillId.IRON_SKIN
					|| skillId == L1SkillId.EXOTIC_VITALIZE || skillId == L1SkillId.ELEMENTAL_FIRE
					|| skillId == L1SkillId.STORM_WALK || skillId == L1SkillId.STRIKER_GALE
					|| skillId == L1SkillId.SOUL_OF_FLAME || skillId == L1SkillId.ADDITIONAL_FIRE) {
				continue;
			}
			l1skills = SkillsTable.getInstance().getTemplate(skillId);
			s = l1skills.getName();
			int l6 = l1skills.getSkillLevel();
			int i7 = l1skills.getId();
			switch (l6) {
			case 19:
				l4 = i7;
				break;
			case 20:
				i5 = i7;
				break;
			case 21:
				j5 = i7;
				break;
			case 22:
				k5 = i7;
				break;
			}
			pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, l4, i5, j5, k5, 0, 0, 0,
					0, 0, 0));
			SkillsTable.getInstance().spellMastery(pc.getId(), skillId, s, 0, 0);
		}
	}

	private void 통합땅정령마법(L1PcInstance pc) {
		String s = "";
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		L1Skills l1skills = null;
		for (int skillId = 148; skillId <= 176; skillId++) {
			if (skillId == L1SkillId.FIRE_WEAPON || skillId == L1SkillId.WIND_SHOT || skillId == L1SkillId.WIND_WALK
					|| skillId == L1SkillId.ERASE_MAGIC || skillId == L1SkillId.LESSER_ELEMENTAL
					|| skillId == L1SkillId.FIRE_BLESS || skillId == L1SkillId.STORM_EYE
					|| skillId == L1SkillId.EARTH_BIND || skillId == L1SkillId.NATURES_TOUCH
					|| skillId == L1SkillId.AQUA_PROTECTER || skillId == L1SkillId.AREA_OF_SILENCE
					|| skillId == L1SkillId.GREATER_ELEMENTAL || skillId == L1SkillId.BURNING_WEAPON
					|| skillId == L1SkillId.NATURES_BLESSING || skillId == L1SkillId.CALL_OF_NATURE
					|| skillId == L1SkillId.STORM_SHOT || skillId == L1SkillId.WIND_SHACKLE
					|| skillId == L1SkillId.WATER_LIFE || skillId == L1SkillId.ELEMENTAL_FIRE
					|| skillId == L1SkillId.STORM_WALK || skillId == L1SkillId.POLLUTE_WATER
					|| skillId == L1SkillId.STRIKER_GALE || skillId == L1SkillId.SOUL_OF_FLAME
					|| skillId == L1SkillId.ADDITIONAL_FIRE) {
				continue;
			}
			l1skills = SkillsTable.getInstance().getTemplate(skillId);
			s = l1skills.getName();
			int l6 = l1skills.getSkillLevel();
			int i7 = l1skills.getId();
			switch (l6) {
			case 19:
				l4 = i7;
				break;
			case 20:
				i5 = i7;
				break;
			case 21:
				j5 = i7;
				break;
			case 22:
				k5 = i7;
				break;
			}
			pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, l4, i5, j5, k5, 0, 0, 0,
					0, 0, 0));
			SkillsTable.getInstance().spellMastery(pc.getId(), skillId, s, 0, 0);
		}
	}

	private void 통합1단계마법서(L1PcInstance pc) {
		String s = "";
		int level1 = 0;
		L1Skills l1skills = null;
		for (int skillId = 1; skillId <= 8; skillId++) {
			l1skills = SkillsTable.getInstance().getTemplate(skillId);
			s = l1skills.getName();
			int l6 = l1skills.getSkillLevel();
			int i7 = l1skills.getId();
			switch (l6) {
			case 1:
				level1 = i7;
				break;
			}
			pc.sendPackets(new S_AddSkill(level1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0));
			SkillsTable.getInstance().spellMastery(pc.getId(), skillId, s, 0, 0);
		}
	}

	private void 통합2단계마법서(L1PcInstance pc) {
		String s = "";
		int level1 = 0;
		int level2 = 0;
		L1Skills l1skills = null;
		for (int skillId = 1; skillId <= 16; skillId++) {
			l1skills = SkillsTable.getInstance().getTemplate(skillId);
			s = l1skills.getName();
			int l6 = l1skills.getSkillLevel();
			int i7 = l1skills.getId();
			switch (l6) {
			case 1:
				level1 = i7;
				break;
			case 2:
				level2 = i7;
				break;
			}
			pc.sendPackets(new S_AddSkill(level1, level2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0));
			SkillsTable.getInstance().spellMastery(pc.getId(), skillId, s, 0, 0);
		}
	}

	private void 통합6단계마법서(L1PcInstance pc) {
		String s = "";
		int level1 = 0;
		int level2 = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		L1Skills l1skills = null;
		for (int skillId = 1; skillId <= 48; skillId++) {
			l1skills = SkillsTable.getInstance().getTemplate(skillId);
			s = l1skills.getName();
			int l6 = l1skills.getSkillLevel();
			int i7 = l1skills.getId();
			switch (l6) {
			case 1:
				level1 = i7;
				break;
			case 2:
				level2 = i7;
				break;
			case 3:
				l = i7;
				break;
			case 4:
				i1 = i7;
				break;
			case 5:
				j1 = i7;
				break;
			case 6:
				k1 = i7;
				break;
			}
			pc.sendPackets(new S_AddSkill(level1, level2, l, i1, j1, k1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0));
			SkillsTable.getInstance().spellMastery(pc.getId(), skillId, s, 0, 0);
		}
	}

	private void 통합법사마법서(L1PcInstance pc) {
		String s = "";
		int level1 = 0;
		int level2 = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		L1Skills l1skills = null;
		for (int skillId = 1; skillId <= 80; skillId++) {
			if (skillId == L1SkillId.INVISIBILITY || skillId == L1SkillId.SHAPE_CHANGE
					|| skillId == L1SkillId.IMMUNE_TO_HARM || skillId == L1SkillId.METEOR_STRIKE
					|| skillId == L1SkillId.ABSOLUTE_BARRIER || skillId == L1SkillId.DISINTEGRATE) {
				continue;
			}
			l1skills = SkillsTable.getInstance().getTemplate(skillId);
			s = l1skills.getName();
			int l6 = l1skills.getSkillLevel();
			int i7 = l1skills.getId();
			switch (l6) {
			case 1:
				level1 = i7;
				break;
			case 2:
				level2 = i7;
				break;
			case 3:
				l = i7;
				break;
			case 4:
				i1 = i7;
				break;
			case 5:
				j1 = i7;
				break;
			case 6:
				k1 = i7;
				break;
			case 7:
				l1 = i7;
				break;
			case 8:
				i2 = i7;
				break;
			case 9:
				j2 = i7;
				break;
			case 10:
				k2 = i7;
				break;
			}
			pc.sendPackets(new S_AddSkill(level1, level2, l, i1, j1, k1, l1, i2, j2, k2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0));
			SkillsTable.getInstance().spellMastery(pc.getId(), skillId, s, 0, 0);
		}
	}

	private int use10RankingExpPotion() {
		int rankexp = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT exp FROM characters ORDER BY exp DESC LIMIT 1 OFFSET 10;");
			rs = pstm.executeQuery();

			if (rs.next()) {
				rankexp = rs.getInt("exp");
			}
		} catch (SQLException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		System.out.println("rankexp="+rankexp);
		return rankexp;
	}
	
	private static void doTeleport(L1PcInstance pc) {
		int newmap = pc.getMapId();
		int setTimer1 = Config.기감시간 - pc.getAccount().getGiranPrisonTime(); 
		int setTimer2 = Config.몽섬시간 - pc.getAccount().getDreamIslandTime();
		int setTimer3 = Config.라던시간 - pc.getAccount().getLastabardTime();
		int setTimer4 = Config.용던본던시간 - pc.getAccount().getDragonGludioTime();
		int setTimer5 = Config.개미던전시간 - pc.getAccount().getAntDundeonTime();
		int setTimer6 = Config.그림자신전시간 - pc.getAccount().getShadowTempleTime();
		if (pc.noPlayerCK) {
			return;
		}
		switch (newmap) {
		case 49:
		case 50:
		case 51:
			pc.sendPackets(new S_SystemMessage("\\fT남은 사냥시간은 " + setTimer5 + "분 입니다."));
			break;
		case 522:
		case 523:
		case 524:
			pc.sendPackets(new S_SystemMessage("\\fT남은 사냥시간은 " + setTimer6 + "분 입니다."));
			break;
		case 36:
		case 13:
			pc.sendPackets(new S_SystemMessage("\\fT남은 사냥시간은 " + setTimer4 + "분 입니다."));
			break;
		case 53:
		case 54:
			pc.sendPackets(new S_SystemMessage("\\fT남은 사냥시간은 " + setTimer1 + "분 입니다."));
			break;
		case 303:
			pc.sendPackets(new S_SystemMessage("\\fT남은 사냥시간은 " + setTimer2 + "분 입니다."));
			break;
		case 530:
		case 531:
		case 532:
		case 533:
			pc.sendPackets(new S_SystemMessage("\\fT남은 사냥시간은 " + setTimer3 + "분 입니다."));
			break;
		default:
			break;
		}
	}
	
	private boolean isTwoLogin(L1PcInstance c) {// 중복체크 변경
		boolean bool = false;
		for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
			if (target.noPlayerCK)
				continue;
			if (c.getId() != target.getId() && (!target.isPrivateShop() && !target.isAutoClanjoin())) {
				if (c.getNetConnection().getAccountName()
						.equalsIgnoreCase(target.getNetConnection().getAccountName())) {
					bool = true;
					break;
				}
			}
		}
		return bool;
	}
	
	private void deleteSpell(L1PcInstance pc) {
		int player = pc.getId();
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=?");
			pstm.setInt(1, player);
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	private void StatInitialize(L1PcInstance pc) {
		L1SkillUse l1skilluse = new L1SkillUse();
		l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), null, 0,
				L1SkillUse.TYPE_LOGIN);
		pc.getInventory().takeoffEquip(945);
		pc.sendPackets(new S_CharVisualUpdate(pc));
		pc.setReturnStat(pc.getExp());
		pc.sendPackets(new S_SPMR(pc));
		pc.sendPackets(new S_OwnCharAttrDef(pc));
		pc.sendPackets(new S_OwnCharStatus2(pc));
		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.START));
		try {
			pc.save();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	public String getType() {
		return C_ITEM_USE;
	}
}