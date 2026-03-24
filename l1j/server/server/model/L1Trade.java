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
package l1j.server.server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.BuffSystem;
//비스킷 추가, 비스킷 수정 주사위 주사위 주사위
import l1j.server.GameSystem.biscuitGame.Akduk1GameSystem;
import l1j.server.GameSystem.biscuitGame.Akduk2GameSystem;
import l1j.server.GameSystem.biscuitGame.Akduk3GameSystem;
import l1j.server.GameSystem.biscuitGame.Akduk4GameSystem;
//비스킷 추가, 비스킷 수정 주사위 주사위 주사위
import l1j.server.Warehouse.PrivateWarehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.model.Instance.L1BuffNpcInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TradeAddItem;
import l1j.server.server.serverpackets.S_TradeStatus;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.utils.SQLUtil;
import manager.LinAllManager;

// Referenced classes of package l1j.server.server.model:
// L1Trade

public class L1Trade {
	private static L1Trade _instance;

	public L1Trade() {}
	public static L1Trade getInstance() {
		if (_instance == null) {
			_instance = new L1Trade();
		}
		return _instance;
	}

	private boolean TradeChaCheck(L1PcInstance pc, L1PcInstance partner, int itemid) {
		if (partner.getNetConnection().getAccount().countCharacters() >= partner.getNetConnection().getAccount().getCharSlot()) {
			pc.sendPackets(new S_SystemMessage("거래 대상에게 빈 캐릭터 슬롯이 없습니다."));
			partner.sendPackets(new S_SystemMessage("빈 캐릭터 슬롯이 없습니다. 캐릭터 슬롯을 확보하세요."));
			TradeCancel(pc);
			return false;
			
		} else if (pc.getNetConnection().getAccount().countCharacters() >= pc.getNetConnection().getAccount().getCharSlot()) {
			partner.sendPackets(new S_SystemMessage("거래 대상에게 빈 캐릭터 슬롯이 없습니다."));
			pc.sendPackets(new S_SystemMessage("빈 캐릭터 슬롯이 없습니다. 캐릭터 슬롯을 확보하세요."));
			TradeCancel(pc);
			return false;
		}      
		if (pc.getNetConnection() == null || partner.getNetConnection() == null) {
			 pc.sendPackets(new S_SystemMessage("거래 대상 캐릭터가 비정상 접속중입니다."));
			 partner.sendPackets(new S_SystemMessage("거래 대상 캐릭터가 비정상 접속중입니다."));
			 TradeCancel(pc);
			 return false;
		}
		
		
			    
		if (itemid == 100904) {		//하급 
			if (pc.getLevel() >= 70){
				pc.sendPackets(new S_SystemMessage("상급 캐릭터 교환증을 이용하세요."));
				return false;
			}
		} else {
			if (pc.getLevel() < 70){
				pc.sendPackets(new S_SystemMessage("하급 캐릭터 교환장을 이용하세요."));
				return false;
			}
		}
		return true;
	}	
	
	private boolean TradeCha(L1PcInstance pc, L1PcInstance tradingPartner, L1ItemInstance item) {
		if (!TradeChaCheck(pc, tradingPartner, item.getItemId()))
			return false;

		String title = null;
		switch(pc.getType()) {
		case 0:title = "군주";break;
		case 1:title = "기사";break;
		case 2:title = "엘프";break;
		case 3:title = "마법사";break;
		case 4:title = "다크엘프";break;
		}
		String chatText = "거래 대상 캐릭터 - 클래스: [" + title + "]  레벨: [" + pc.getLevel()
		+ "]  엘릭서 사용: [" + pc.getAbility().getElixirCount() + "]  캐릭터 거래 완료 후 자동으로 접속이 종료됩니다.";

		S_ChatPacket s_chatpacket = new S_ChatPacket(tradingPartner, chatText, Opcodes.S_OPCODE_NORMALCHAT, 2);
		if (!tradingPartner.getExcludingList().contains(tradingPartner.getName())) {
			tradingPartner.sendPackets(s_chatpacket);
		}
		chatText = "캐릭터 거래시 거래 완료 후 자동으로 접속이 종료됩니다. 아데나와 교환시 창고에 저장됩니다.";
		S_ChatPacket s_chatpacket1 = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_NORMALCHAT, 2);
		if (!pc.getExcludingList().contains(pc.getName())) {
			pc.sendPackets(s_chatpacket1);
		}
		return true;
	}
	
	public void TradeAddItem(L1PcInstance player, int itemid, int itemcount) {
		L1Object trading_partner = L1World.getInstance().findObject(player.getTradeID());
		L1ItemInstance item = player.getInventory().getItem(itemid);
		if (item != null && trading_partner != null) {
			if(trading_partner instanceof L1PcInstance){
				L1PcInstance tradepc = (L1PcInstance)trading_partner;
				if (!item.isEquipped()) {
					if (item.getCount() < itemcount || 0 >= itemcount) {
						player.sendPackets(new S_TradeStatus(1));
						tradepc.sendPackets(new S_TradeStatus(1));
						player.setTradeOk(false);
						tradepc.setTradeOk(false);
						player.setTradeID(0);
						tradepc.setTradeID(0);
						return;
					}
					/* 캐릭터 교환서 */
					if (item.getItemId() == L1ItemId.CHA_TRADE_SCROLL_ROW 
							|| item.getItemId() == L1ItemId.CHA_TRADE_SCROLL_HIGH) {
						if (!TradeCha(player, tradepc, item)) {
							return;
						}
						List<?> player_tradelist = player.getTradeWindowInventory().getItems();
						int player_tradecount = player.getTradeWindowInventory().getSize();
						L1ItemInstance pcItem = null;
						for (int cnt = 0; cnt < player_tradecount; cnt++) {
							pcItem = (L1ItemInstance) player_tradelist.get(cnt);
							if (pcItem.getItemId() == L1ItemId.CHA_TRADE_SCROLL_ROW 
									|| pcItem.getItemId() == L1ItemId.CHA_TRADE_SCROLL_HIGH) {
								player.sendPackets(new S_SystemMessage("캐릭터 교환증은 하나만 올릴수 있습니다."));
								return;
							}
						}	
						String title = null;
						switch(player.getType()) {
						case 0:title = "군주";break;
						case 1:title = "기사";break;
						case 2:title = "엘프";break;
						case 3:title = "마법사";break;
						case 4:title = "다크엘프";break;
						}
						player.getInventory().tradeItem(item, itemcount, player.getTradeWindowInventory());
						player.sendPackets(new S_TradeAddItem(item, itemcount, 0, title, player.getLevel()));
						tradepc.sendPackets(new S_TradeAddItem(item, itemcount, 1, title, player.getLevel()));
					} else {
						player.getInventory().tradeItem(item, itemcount, player.getTradeWindowInventory());
						player.sendPackets(new S_TradeAddItem(item, itemcount, 0));
						tradepc.sendPackets(new S_TradeAddItem(item, itemcount, 1));	
					}
				}
			}
			
			// 비스킷 추가, 비스킷 수정 주사위 주사위 주사위
			else if (trading_partner instanceof L1BuffNpcInstance) {
				L1BuffNpcInstance tradenpc = (L1BuffNpcInstance) trading_partner;
				if (item.getCount() < itemcount || 0 >= itemcount) { // 허상버그
					// 관련
					// 추가
					player.sendPackets(new S_TradeStatus(1));
					player.setTradeOk(false);
					tradenpc.setTradeOk(false);
					player.setTradeID(0);
					tradenpc.setTradeID(0);
					return;
				}
				// 비스킷 추가, 비스킷 수정 딜러거래
				if (item.getItemId() != 40308 || itemcount > 5000000) { 
//				if (item.getItemId() != 1100001 || itemcount > 400) {
					player.sendPackets(new S_TradeStatus(1));
					player.setTradeOk(false);
					tradenpc.setTradeOk(false);
					player.setTradeID(0);
					tradenpc.setTradeID(0);
					Broadcaster.broadcastPacket(tradenpc, new S_NpcChatPacket(
							tradenpc, "아데나만 올려주세요. 그리고 500만 아데나까지 가능.", 0));
					return;
				}

				if (item.getItemId() == 40308 && itemcount != 100000
						&& tradenpc.getNpcTemplate().get_npcId() == 7000067) {
					player.sendPackets(new S_TradeStatus(1));
					player.setTradeOk(false);
					tradenpc.setTradeOk(false);
					player.setTradeID(0);
					tradenpc.setTradeID(0);
					Broadcaster.broadcastPacket(tradenpc, new S_NpcChatPacket(
							tradenpc, "비용은 10만아덴 입니다.", 0));
				} else if (item.getItemId() == 40308
						&& itemcount != 200000
						&& tradenpc.getNpcTemplate().get_npcId() == 7000070) {
					player.sendPackets(new S_TradeStatus(1));
					player.setTradeOk(false);
					tradenpc.setTradeOk(false);
					player.setTradeID(0);
					tradenpc.setTradeID(0);
					Broadcaster.broadcastPacket(tradenpc, new S_NpcChatPacket(
							tradenpc, "비용은 20만아덴 입니다.", 0));
				} else {
					player.getInventory().tradeItem(item, itemcount,
							player.getTradeWindowInventory());
					player.sendPackets(new S_TradeAddItem(item,
							itemcount, 0));
				}
			}
			// 비스킷 추가, 비스킷 수정 주사위 주사위 주사위
		}
	}
	private void chaTradeAccount(String accountName, String chaName) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE characters SET account_name=? WHERE char_name=?");
			pstm.setString(1, accountName);
			pstm.setString(2, chaName);
			pstm.execute();
		} catch (Exception e) {

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	private void chaTradeOk(L1PcInstance player, L1PcInstance trading_partner, boolean chaChk1, boolean chaChk2 ) {
		if (chaChk1 || chaChk2) {
			player.getNetConnection().kick();
			player.getNetConnection().close();
			player.sendPackets(new S_Disconnect());
			trading_partner.getNetConnection().kick();
			trading_partner.getNetConnection().close();
			trading_partner.sendPackets(new S_Disconnect());
		}
		if (chaChk1) {
			chaTradeAccount(trading_partner.getAccountName(), player.getName() );
		}
		if (chaChk2) {
			chaTradeAccount(player.getAccountName(), trading_partner.getName());
		}
	}
	
	private boolean chaTradeItemChk(List<?> pc_tradelist, int listcount) {
		L1ItemInstance l1iteminstance = null;
		for (int cnt = 0; cnt < listcount; cnt++) {
			l1iteminstance = (L1ItemInstance) pc_tradelist.get(cnt);
			if (l1iteminstance.getItemId() == L1ItemId.CHA_TRADE_SCROLL_ROW 
					|| l1iteminstance.getItemId() == L1ItemId.CHA_TRADE_SCROLL_HIGH) {
				return true;
			}
		}
		return false;
	}
	public void TradeOK(L1PcInstance pc) {
		try	{
			int cnt;
			boolean chaChk1 = false;
			boolean chaChk2 = false;
			L1Object trading_partner = L1World.getInstance().findObject(pc.getTradeID());
			if (trading_partner != null) {
				if(trading_partner instanceof L1PcInstance){
					L1PcInstance tradepc = (L1PcInstance)trading_partner;
					List<?> player_tradelist = pc.getTradeWindowInventory().getItems();
					int player_tradecount = pc.getTradeWindowInventory().getSize();

					List<?> trading_partner_tradelist = tradepc.getTradeWindowInventory().getItems();
					int trading_partner_tradecount = tradepc.getTradeWindowInventory().getSize();
					chaChk1 = chaTradeItemChk(player_tradelist,player_tradecount);
					chaChk2 = chaTradeItemChk(trading_partner_tradelist,trading_partner_tradecount);
					L1ItemInstance pcitem = null;
					for (cnt = 0; cnt < player_tradecount; cnt++) {
						pcitem = (L1ItemInstance) player_tradelist.get(0);
						if (chaChk2 && 
								!(pcitem.getItemId() == L1ItemId.CHA_TRADE_SCROLL_ROW 
								|| pcitem.getItemId() == L1ItemId.CHA_TRADE_SCROLL_HIGH)) {
							PrivateWarehouse warehouse = WarehouseManager.getInstance().getPrivateWarehouse(tradepc.getAccountName());
							if(warehouse == null) return;
							if (warehouse.checkAddItemToWarehouse(pcitem, player_tradecount) == L1Inventory.SIZE_OVER) {
								tradepc.sendPackets(new S_ServerMessage(75)); // \f1상대가 물건을 너무 가지고 있어 거래할 수 없습니다.
								break;
							}
							LinAllManager.getInstance().TradeAppend(pcitem.getName(), tradepc.getName(), pc.getName());
							pc.getTradeWindowInventory().tradeItem(pcitem, pcitem.getCount(), warehouse);					
							/** 로그파일저장 **/
							try {
								LoggerInstance.getInstance().addTrade(true, pc, (L1PcInstance)trading_partner, pcitem, pcitem.getCount());
							} catch (Exception e) {}
						} else {
							if ((pcitem.getCount() >= 100 
									&& pcitem.getItemId() == 40087
									|| pcitem.getItemId() == 40074) 
									|| pcitem.getItemId() == 40308 && pcitem.getCount() >= 10000000
									|| pcitem.getItemId() == 41159 && pcitem.getCount() >= 500
											|| pcitem.getItemId() == 430023 && pcitem.getCount() >= 500//0225갬블추가
									|| pcitem.getCount() >= 1000 && pcitem.getItemId() != 40308) {
							}							
							pc.getTradeWindowInventory().tradeItem(pcitem, pcitem.getCount(), tradepc.getInventory());
							/** 로그파일저장 **/
							try {
								LoggerInstance.getInstance().addTrade(true, pc, (L1PcInstance)trading_partner, pcitem, pcitem.getCount());
							} catch (Exception e) {}
							LinAllManager.getInstance().TradeAppend(pcitem.getName(), tradepc.getName(), pc.getName());
						}
						/*if (pc.getSkillEffectTimerSet().hasSkillEffect(6524)) { 
							pc.sendPackets(new S_SystemMessage("\\fY리스후 30초간은 교환을 하실수 없습니다."));
							return;
					    }*/
					}
					L1ItemInstance tradepcitem = null;
					for (cnt = 0; cnt < trading_partner_tradecount; cnt++) {
						tradepcitem = (L1ItemInstance) trading_partner_tradelist.get(0);
						if  (chaChk1 && 
								!(tradepcitem.getItemId() == L1ItemId.CHA_TRADE_SCROLL_ROW 
								|| tradepcitem.getItemId() == L1ItemId.CHA_TRADE_SCROLL_HIGH)) {
							PrivateWarehouse warehouse = WarehouseManager.getInstance().getPrivateWarehouse(pc.getAccountName());
							if (warehouse == null) return;
							if (warehouse.checkAddItemToWarehouse(tradepcitem, trading_partner_tradecount) == L1Inventory.SIZE_OVER) {
								pc.sendPackets(new S_ServerMessage(75)); // \f1상대가 물건을 너무 가지고 있어 거래할 수 없습니다.
								break;
							}
							tradepc.getTradeWindowInventory().tradeItem(tradepcitem, tradepcitem.getCount(), warehouse);
							/** 로그파일저장 **/
							try {
								LoggerInstance.getInstance().addTrade(true, pc, (L1PcInstance)trading_partner, tradepcitem, tradepcitem.getCount());
							} catch (Exception e) {}							
						} else {
							if ((tradepcitem.getCount() >= 100 
									&& tradepcitem.getItemId() == 40087
									|| tradepcitem.getItemId() == 40074)
									|| tradepcitem.getItemId() == 40308 && tradepcitem.getCount() >= 10000000
									|| tradepcitem.getItemId() == 41159 && tradepcitem.getCount() >= 500
									|| tradepcitem.getCount() >= 1000 && tradepcitem.getItemId() != 40308) {
							}													
							tradepc.getTradeWindowInventory().tradeItem(tradepcitem, tradepcitem.getCount(), pc.getInventory());
							/** 로그파일저장 **/
							try {
								LoggerInstance.getInstance().addTrade(true, pc, (L1PcInstance)trading_partner, tradepcitem, tradepcitem.getCount());
							} catch (Exception e) {}
						}
						/*if (tradepc.getSkillEffectTimerSet().hasSkillEffect(6524)) { 
							tradepc.sendPackets(new S_SystemMessage("\\fY리스후 30초간은 교환을 하실수 없습니다."));
							return;
					    }*/
					}
					/** 2012.01.12 SOCOOL 매입상점 */
					if (tradepc.isAutoshop()) {							
						for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
							if (player.isGm() && !player.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_MENT)) {
								if (pcitem.getEnchantLevel() > 0) {
									//player.sendPackets(new S_SystemMessage("\\fT" + pc.getName()+ "님이 +" + pcitem.getEnchantLevel() + " " + pcitem.getName() + "을 매입시켰습니다."));
								} else {
									//player.sendPackets(new S_SystemMessage("\\fT" + pc.getName()+ "님이 " + pcitem.getName() + "을 " + pcitem.getCount() + "개 매입시켰습니다."));
								}
							}
						}
					}
				
			
					pc.sendPackets(new S_TradeStatus(0));
					tradepc.sendPackets(new S_TradeStatus(0));
					pc.setTradeOk(false);
					tradepc.setTradeOk(false);
					pc.setTradeID(0);
					tradepc.setTradeID(0);
					pc.getLight().turnOnOffLight();
					tradepc.getLight().turnOnOffLight();
					if (chaChk1) {
						if (tradepc.getInventory().checkItem(L1ItemId.CHA_TRADE_SCROLL_ROW, 1) ) {
							tradepc.getInventory().consumeItem(L1ItemId.CHA_TRADE_SCROLL_ROW, 1);
						}
						if (tradepc.getInventory().checkItem(L1ItemId.CHA_TRADE_SCROLL_HIGH, 1) ) {
							tradepc.getInventory().consumeItem(L1ItemId.CHA_TRADE_SCROLL_HIGH, 1);
						}
					}
					if (chaChk2) {
						if (pc.getInventory().checkItem(L1ItemId.CHA_TRADE_SCROLL_ROW, 1) ) {
							pc.getInventory().consumeItem(L1ItemId.CHA_TRADE_SCROLL_ROW, 1);
						}
						if (pc.getInventory().checkItem(L1ItemId.CHA_TRADE_SCROLL_HIGH, 1) ) {
							pc.getInventory().consumeItem(L1ItemId.CHA_TRADE_SCROLL_HIGH, 1);
						}
					}
					chaTradeOk(pc, tradepc, chaChk1, chaChk2);

				}
				// 비스킷 추가, 비스킷 수정 주사위 주사위 주사위
				 else if (trading_partner instanceof L1BuffNpcInstance) {
					L1BuffNpcInstance tradenpc = (L1BuffNpcInstance) trading_partner;
					List<?> player_tradelist = pc.getTradeWindowInventory()
							.getItems();
					int player_tradecount = pc.getTradeWindowInventory()
							.getSize();

					L1ItemInstance pcitem = null;
					for (cnt = 0; cnt < player_tradecount; cnt++) {
						pcitem = (L1ItemInstance) player_tradelist.get(0);
						L1Castle castle = CastleTable.getInstance()
								.getCastleTable(4);
						L1Castle castle2 = CastleTable.getInstance()
								.getCastleTable(6);
						int money = castle.getPublicReadyMoney()
								+ pcitem.getCount() / 3;
						int money2 = castle2.getPublicReadyMoney()
								+ pcitem.getCount() / 10;
						castle.setPublicReadyMoney(money);
						castle2.setPublicReadyMoney(money2);
						pc.getTradeWindowInventory().consumeItem(
								pcitem.getItemId(), pcitem.getCount());
					}
					pc.sendPackets(new S_TradeStatus(0));
					pc.setTradeOk(false);
					tradenpc.setTradeOk(false);
					pc.setTradeID(0);
					tradenpc.setTradeID(0);
					pc.getLight().turnOnOffLight();
					tradenpc.getLight().turnOnOffLight();
					new BuffSystem(tradenpc, pc);

					
					if (tradenpc.getNpcTemplate().get_npcId() == 41922) { // 주사위(딜러)
						Akduk2GameSystem gambling = new Akduk2GameSystem();
						gambling.Gambling(pc, pcitem.getCount());
					} else if (tradenpc.getNpcTemplate().get_npcId() == 41923) { // 묵찌빠
						Akduk4GameSystem gambling = new Akduk4GameSystem();
						gambling.Gambling(pc, pcitem.getCount());
					} else if (tradenpc.getNpcTemplate().get_npcId() == 41918) { // 소막(딜러)
						Akduk1GameSystem gambling = new Akduk1GameSystem();
						gambling.Gambling(pc, pcitem.getCount());
					} else if (tradenpc.getNpcTemplate().get_npcId() == 41919) { // 소막2(딜러)
						Akduk3GameSystem gambling = new Akduk3GameSystem();
						gambling.Gambling(pc, pcitem.getCount());
					}
					// 비스킷 추가, 비스킷 수정 주사위 주사위 주사위
				}
			}
		} catch (Exception e) { 
			System.out.println("오류");
		}
	}
	/**원본**/
	/*public void TradeCancel(L1PcInstance pc) {
		int cnt;
		L1Object trading_partner = L1World.getInstance().findObject(pc.getTradeID());
		if (trading_partner != null) {
			if(trading_partner instanceof L1PcInstance){
				L1PcInstance tradepc = (L1PcInstance)trading_partner;
				List<?> player_tradelist = pc.getTradeWindowInventory().getItems();
				int player_tradecount = pc.getTradeWindowInventory().getSize();

				List<?> trading_partner_tradelist = tradepc.getTradeWindowInventory().getItems();
				int trading_partner_tradecount = tradepc.getTradeWindowInventory().getSize();
				L1ItemInstance pcitem = null;
				for (cnt = 0; cnt < player_tradecount; cnt++) {
					pcitem = (L1ItemInstance) player_tradelist.get(0);
					pc.getTradeWindowInventory().tradeItem(pcitem, pcitem.getCount(), pc.getInventory());
				}
				L1ItemInstance tradepcitem = null;
				for (cnt = 0; cnt < trading_partner_tradecount; cnt++) {
					tradepcitem = (L1ItemInstance) trading_partner_tradelist.get(0);
					tradepc.getTradeWindowInventory().tradeItem(tradepcitem, tradepcitem.getCount(), tradepc.getInventory());
				}

				if (tradepc == null || tradepc.getTradeID() != pc.getId()) {
					return;
				}
				pc.saveInventory();
				pc.sendPackets(new S_TradeStatus(1));
				tradepc.sendPackets(new S_TradeStatus(1));
				pc.setTradeOk(false);
				tradepc.saveInventory();
				tradepc.setTradeOk(false);
				pc.setTradeID(0);
				tradepc.setTradeID(0);
				/**원본**/
				
	public void TradeCancel(L1PcInstance pc) {
		int cnt;
		L1Object trading_partner = L1World.getInstance().findObject(pc.getTradeID());
		if (trading_partner != null) {
			if(trading_partner instanceof L1PcInstance){
				L1PcInstance tradepc = (L1PcInstance)trading_partner;
				List<?> player_tradelist = pc.getTradeWindowInventory().getItems();
				int player_tradecount = pc.getTradeWindowInventory().getSize();

				List<?> trading_partner_tradelist = tradepc.getTradeWindowInventory().getItems();
				int trading_partner_tradecount = tradepc.getTradeWindowInventory().getSize();
				L1ItemInstance pcitem = null;
				for (cnt = 0; cnt < player_tradecount; cnt++) {
					pcitem = (L1ItemInstance) player_tradelist.get(0);
					pc.getTradeWindowInventory().tradeItem(pcitem, pcitem.getCount(), pc.getInventory());
					/** 로그파일저장 **/
					try {
						LoggerInstance.getInstance().addTrade(false, pc, (L1PcInstance)trading_partner, pcitem, pcitem.getCount());
					} catch (Exception e) {}
				}
				L1ItemInstance tradepcitem = null;
				for (cnt = 0; cnt < trading_partner_tradecount; cnt++) {
					tradepcitem = (L1ItemInstance) trading_partner_tradelist.get(0);
					tradepc.getTradeWindowInventory().tradeItem(tradepcitem, tradepcitem.getCount(), tradepc.getInventory());
					/** 로그파일저장 **/
					try {
						LoggerInstance.getInstance().addTrade(false, tradepc, (L1PcInstance)trading_partner, tradepcitem, tradepcitem.getCount());
					} catch (Exception e) {}
				}

				pc.sendPackets(new S_TradeStatus(1));
				tradepc.sendPackets(new S_TradeStatus(1));
				pc.setTradeOk(false);
				tradepc.setTradeOk(false);
				pc.setTradeID(0);
				tradepc.setTradeID(0);	
			}else if(trading_partner instanceof L1BuffNpcInstance){
				L1BuffNpcInstance tradenpc = (L1BuffNpcInstance)trading_partner;
				List<?> player_tradelist = pc.getTradeWindowInventory().getItems();
				int player_tradecount = pc.getTradeWindowInventory().getSize();

				L1ItemInstance pcitem = null;
				for (cnt = 0; cnt < player_tradecount; cnt++) {
					pcitem = (L1ItemInstance) player_tradelist.get(0);
					pc.getTradeWindowInventory().tradeItem(pcitem, pcitem.getCount(), pc.getInventory());
				}
				pc.sendPackets(new S_TradeStatus(1));
				pc.setTradeOk(false);
				tradenpc.setTradeOk(false);
				pc.setTradeID(0);
				tradenpc.setTradeID(0);
				
			}
		}
	}
}