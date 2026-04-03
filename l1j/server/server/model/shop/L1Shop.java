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
package l1j.server.server.model.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcShopAdenTypeTable;
import l1j.server.server.datatables.NpcShopAdenTypeTable.L1AdenType;
import l1j.server.server.datatables.TownTable;
import l1j.server.server.model.L1BugBearRace;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1TaxCalculator;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.utils.IntRange;

public class L1Shop {
	private final int _npcId;
	private final List<L1ShopItem> _sellingItems;
	private final List<L1ShopItem> _purchasingItems;

	public L1Shop(int npcId, List<L1ShopItem> sellingItems, List<L1ShopItem> purchasingItems) {
		if (sellingItems == null || purchasingItems == null) {
			throw new NullPointerException();
		}
		_npcId = npcId;
		_sellingItems = sellingItems;
		_purchasingItems = purchasingItems;
	}

	public void sellItems(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int npcid = getNpcId();
		L1AdenType at = NpcShopAdenTypeTable.getInstance().getNpcAdenType(npcid);
		if (at != null) {
			if (!ensurePremiumSell(pc, orderList, at)) {
				return;
			}
			sellPremiumItems(pc.getInventory(), orderList, at);
		} else {
			// 세율 안들어가는 NPC
			if (getNpcId() == 70068 || getNpcId() == 70020 || getNpcId() == 70056 || getNpcId() == 70051 || getNpcId() == 70055
					|| getNpcId() == 4213002 || getNpcId() == 70017 || getNpcId() == 4200105) {
				if (!NoTaxEnsureSell(pc, orderList)) {
					return;
				}
				NoTaxSellItems(pc.getInventory(), orderList);
				return;
			}
			// 보스코인 상점
			if (getNpcId() == 900001619) {
				if (!BossCoinEnsureSell(pc, orderList)) {
					return;
				}
				BossCoinSellItems(pc.getInventory(), orderList);
				return;
			}
			// 고대의 금화 상인 (트릭)
			if (getNpcId() == 4208001) {
				if (!AGEnsureSell(pc, orderList)) {
					return;
				}
				AGSellItems(pc.getInventory(), orderList);
				return;
			}
			// 프리미엄 상점
			if (getNpcId() == 4220000 || getNpcId() == 4220001 || getNpcId() == 4220002 || getNpcId() == 4220003 || getNpcId() == 4220700) {
				if (!ensurePremiumSell(pc, orderList)) {
					return;
				}
				sellPremiumItems(pc.getInventory(), orderList);
				return;
			}
			// 무인엔피씨 상점
			if (getNpcId() >= 8100000 && getNpcId() <= 8100500) {
				if (!NoTaxEnsureSell(pc, orderList)) {
					return;
				}
				for (L1ShopBuyOrder order : orderList.getList()) {
					int amount = order.getCount();
					if (amount <= 0)
						return;
				}
				NpcShopSellItems(pc.getInventory(), orderList);
				return;

			}
			// 전쟁물자 상인(징표)
			if (getNpcId() == 4200104) {
				if (!ensureMarkSell(pc, orderList)) {
					return;
				}
				sellMarkItems(pc.getInventory(), orderList);
				return;
			}
			// 행 베리 상인(베리)
			if (getNpcId() == 46200) {
				if (!ensureMarkSell2(pc, orderList)) {
					return;
				}
				sellBerryItems(pc.getInventory(), orderList);
				return;
			}
			// 인형 코인
			if (getNpcId() == 46198) {
				if (!ensureMarkSell1(pc, orderList)) {
					return;
				}
				sellGCoinItems(pc.getInventory(), orderList);
				return;
			}
			
			if (getNpcId() == 146198) {
				if (!ensurePaybackSell(pc, orderList)) {
					return;
				}
				sellPaybackCoinItems(pc.getInventory(), orderList);
				return;
			}
			
			// 그 외
			if (!ensureSell(pc, orderList)) {
				return;
			} else {
				sellItems(pc.getInventory(), orderList);
				payTax(orderList);
			}
		}
	}

	private boolean ensureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPriceTaxIncluded();
		if (!IntRange.includes(price, 0, 2000000000)) {
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}
		if (!pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
			pc.sendPackets(new S_ServerMessage(189));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}

		return true;
	}

	private void sellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(L1ItemId.ADENA, orderList.getTotalPriceTaxIncluded())) {
			throw new IllegalStateException("구입에 필요한 아데나를 소비 할 수 없습니다.");
		}
		L1ItemInstance item = null;

		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			int enchant = order.getItem().getEnchant();
			int bless = order.getItem().getBless();
			item = ItemTable.getInstance().createItem(itemId);
			if (getSellingItems().contains(item)) {
				return;
			}
			item.setCount(amount);
			item.setEnchantLevel(enchant);
			item.setIdentified(true);
			// 배당을 측정하기 위한 추가 부분
			if (getNpcId() == 70035 || getNpcId() == 70041 || getNpcId() == 70042) {
				int[] ticket = L1BugBearRace.getInstance().getTicketInfo(order.getOrderNumber());
				item.setSecondId(ticket[0]);
				item.setRoundId(ticket[1]);
				item.setTicketId(ticket[2]);
				L1BugBearRace.getInstance().addBetting(order.getOrderNumber(), amount);

			}
			inv.storeItem(item);
		}
	}

	private boolean NoTaxEnsureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		if (!IntRange.includes(price, 0, 2000000000)) {
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}
		if (!pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
			pc.sendPackets(new S_ServerMessage(189));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}

	private void NoTaxSellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(L1ItemId.ADENA, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 아데나를 소비 할 수 없습니다.");
		}
		L1ItemInstance item = null;
		Random random = new Random();
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			int bless = order.getItem().getBless();
			item = ItemTable.getInstance().createItem(itemId);
			if (getSellingItems().contains(item)) {
				return;
			}
			item.setCount(amount);
			item.setBless(bless);
			item.setIdentified(true);

			if (_npcId == 70068 || _npcId == 70020 || _npcId == 70056) {
				item.setIdentified(false);
				int chance = random.nextInt(150) + 1;
				if (chance <= 15) {
					item.setEnchantLevel(-2);
				} else if (chance >= 16 && chance <= 30) {
					item.setEnchantLevel(-1);
				} else if (chance >= 31 && chance <= 89) {
					item.setEnchantLevel(0);
				} else if (chance >= 90 && chance <= 141) {
					item.setEnchantLevel(random.nextInt(2) + 1);
				} else if (chance >= 142 && chance <= 147) {
					item.setEnchantLevel(random.nextInt(3) + 3);
				} else if (chance >= 148 && chance <= 149) {
					item.setEnchantLevel(6);
				} else if (chance == 150) {
					item.setEnchantLevel(7);
				}
			}

			if (_npcId == 4200105) {
				item.setIdentified(false);
				int chance = random.nextInt(150) + 1;
				if (chance <= 140) {
					item.setBless(0);
				} else if (chance == 150) {
					item.setBless(1);
				}
			}
			inv.storeItem(item);
		}
	}

	private void BossCoinSellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(90000001, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 보스코인을 소비 할 수 없습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			if (getSellingItems().contains(item)) {
				return;
			}
			item.setCount(amount);
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}

	private boolean BossCoinEnsureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		if (!IntRange.includes(price, 0, 2000000000)) {
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}
		if (!pc.getInventory().checkItem(90000001, price)) {
			pc.sendPackets(new S_SystemMessage("보스코인이 부족합니다."));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}
	
	private void AGSellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(49026, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 아데나를 소비 할 수 없습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			if (getSellingItems().contains(item)) {
				return;
			}
			item.setCount(amount);
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}

	private boolean AGEnsureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		if (!IntRange.includes(price, 0, 2000000000)) {
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}
		if (!pc.getInventory().checkItem(49026, price)) {
			pc.sendPackets(new S_ServerMessage(189));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}

	private void sellMarkItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(L1ItemId.TEST_MARK, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 징표를 소비할 수 없습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}

	private void sellBerryItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(L1ItemId.BERRY, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 베리를 소비할 수 없습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}

	private void sellGCoinItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(L1ItemId.G_COIN, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 퍼플 코인을 소비할 수 없습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}

	private void sellPaybackCoinItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(145067, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 페이백 코인을 소비할 수 없습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}
	
	private boolean ensureMarkSell2(L1PcInstance pc, L1ShopBuyOrderList orderList) {

		int price = orderList.getTotalPrice();

		if (!pc.getInventory().checkItem(L1ItemId.BERRY, price)) {
			pc.sendPackets(new S_SystemMessage("베리가 부족합니다."));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}

	public L1ShopItem getBuyExceptionEnchantItem(int itemid) {
		for (L1ShopItem a : _purchasingItems) {
			if (a.getEnchant() > 0) {
				return null;
			}
			if (a.getItemId() == itemid) {
				return a;
			}
		}
		return null;
	}
	
	private boolean ensureMarkSell1(L1PcInstance pc, L1ShopBuyOrderList orderList) {

		int price = orderList.getTotalPrice();

		if (!pc.getInventory().checkItem(L1ItemId.G_COIN, price)) {
			pc.sendPackets(new S_SystemMessage("퍼플 코인이 부족합니다."));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}

	private boolean ensurePaybackSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {

		int price = orderList.getTotalPrice();

		if (!pc.getInventory().checkItem(145067, price)) {
			pc.sendPackets(new S_SystemMessage("페이백 코인이 부족합니다."));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}
	
	private boolean ensureMarkSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {

		int price = orderList.getTotalPrice();

		if (!pc.getInventory().checkItem(L1ItemId.TEST_MARK, price)) {
			pc.sendPackets(new S_SystemMessage("징표가 부족합니다."));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}

	private void sellPremiumItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(41159, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 신비한 깃털을 소비할 수 없었습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}

	private boolean ensurePremiumSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		if (!IntRange.includes(price, 0, 100000000)) {
			pc.sendPackets(new S_SystemMessage("신비한 날개깃털은 한번에 1억개 이상 사용할수 없습니다."));
			return false;
		}
		if (!pc.getInventory().checkItem(41159, price)) {
			pc.sendPackets(new S_SystemMessage("신비한 날개깃털이 부족합니다."));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}

	private void NpcShopSellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(L1ItemId.ADENA, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 아데나를 소비 할 수 없습니다.");
		}
		
		L1ItemInstance item = null;
		boolean[] isRemoveFromList = new boolean[8];
		for (L1ShopBuyOrder order : orderList.getList()) {
			int orderid = order.getOrderNumber();
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			int enchant = order.getItem().getEnchant();
			int remaindcount = getSellingItems().get(orderid).getCount();
			int bless = order.getItem().getBless();
			if (remaindcount < amount)
				return;

			item = ItemTable.getInstance().createItem(itemId);
			if (getSellingItems().contains(item)) {
				return;
			}

			item.setCount(amount);
			item.setBless(bless);
			item.setIdentified(true);
			item.setEnchantLevel(enchant);

			if (remaindcount == amount)
				isRemoveFromList[orderid] = true;
			else
				_sellingItems.get(orderid).setCount(remaindcount - amount);

			inv.storeItem(item);

			for (int i = 7; i >= 0; i--) {
				if (isRemoveFromList[i]) {
					_sellingItems.remove(i);
				}
			}
		}
	}

	private void payTax(L1ShopBuyOrderList orderList) {
		payCastleTax(orderList);
		//payTownTax(orderList);
		//payDiadTax(orderList);
	}

	private void payCastleTax(L1ShopBuyOrderList orderList) {
		L1TaxCalculator calc = orderList.getTaxCalculator();
		int price = orderList.getTotalPrice();
		int castleId = L1CastleLocation.getCastleIdByNpcid(_npcId);
		int castleTax = calc.calcCastleTaxPrice(price);
		int nationalTax = calc.calcNationalTaxPrice(price);
		//System.out.println("castleTax="+castleTax);
		//System.out.println("nationalTax="+nationalTax);
		if (castleId == L1CastleLocation.ADEN_CASTLE_ID || castleId == L1CastleLocation.DIAD_CASTLE_ID) {
			castleTax += nationalTax;
			nationalTax = 0;
		}

		if (castleId != 0 && castleTax > 0) {
			L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);
			synchronized (castle) {
				int money = castle.getPublicReadyMoney();
				if (2000000000 > money) {
					money += castleTax;
					castle.setPublicReadyMoney(money);
					CastleTable.getInstance().updateCastle(castle);
				}
			}
			if (nationalTax > 0) {
				L1Castle aden = CastleTable.getInstance().getCastleTable(L1CastleLocation.ADEN_CASTLE_ID);
				synchronized (aden) {
					int money = aden.getPublicReadyMoney();
					if (2000000000 > money) {
						money += nationalTax;
						aden.setPublicReadyMoney(money);
						CastleTable.getInstance().updateCastle(aden);
					}
				}
			}
		}
	}

	private void payDiadTax(L1ShopBuyOrderList orderList) {
		L1Castle castle = CastleTable.getInstance().getCastleTable(L1CastleLocation.DIAD_CASTLE_ID);
		L1TaxCalculator calc = orderList.getTaxCalculator();
		int price = orderList.getTotalPrice();
		int diadTax = calc.calcDiadTaxPrice(price);

		if (diadTax <= 0) {
			return;
		}
		synchronized (castle) {
			int money = castle.getPublicReadyMoney();
			if (2000000000 > money) {
				money = money + diadTax;
				castle.setPublicReadyMoney(money);
				CastleTable.getInstance().updateCastle(castle);
			}
		}
	}

	private void payTownTax(L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		if (!L1World.getInstance().isProcessingContributionTotal()) {
			int town_id = L1TownLocation.getTownIdByNpcid(_npcId);
			if (town_id >= 1 && town_id <= 10) {
				TownTable.getInstance().addSalesMoney(town_id, price);
			}
		}
	}

	public void buyItems(L1ShopSellOrderList orderList) {
	    L1PcInventory inv = orderList.getPc().getInventory();
	    int totalPrice = 0;
	    List<L1ItemInstance> toRemove = new ArrayList<>();

	    for (L1ShopSellOrder order : orderList.getList()) {
	        L1ItemInstance item = inv.getItem(order.getItem().getTargetId());
	        if (item != null && item.getItem().getBless() < 128) {
	            int count = Math.min(item.getCount(), order.getCount());
	            toRemove.add(item);
	            totalPrice += order.getItem().getAssessedPrice() * order.getDividend() * count;
	        }
	    }

	    totalPrice = IntRange.ensure(totalPrice, 0, 2000000000);
	    if (totalPrice <= 0) return;

	    // 모든 연산이 문제없으면 여기서 실제 처리 시작
	    for (int i = 0; i < toRemove.size(); i++) {
	        L1ItemInstance item = toRemove.get(i);
	        L1ShopSellOrder order = orderList.getList().get(i);
	        inv.removeItem(item, order.getCount());
	    }

	    int currencyId = (getNpcId() == 46198) ? 45067 : L1ItemId.ADENA;
	    inv.storeItem(currencyId, totalPrice);
	    orderList.getPc().saveInventory();
	}

	private L1ShopItem getPurchasingItem(int itemId) {
		for (L1ShopItem shopItem : _purchasingItems) {
			if (shopItem.getItemId() == itemId) {
				return shopItem;
			}
		}
		return null;
	}

	private L1ShopItem getPurchasingItem(int itemId, int enchant) {
		for (L1ShopItem shopItem : _purchasingItems) {
			if (shopItem.getItemId() == itemId && shopItem.getEnchant() == enchant) {
				return shopItem;
			}
		}
		return null;
	}
	
	private boolean isPurchaseableItem(L1ItemInstance item) {
		if (item == null || item.isEquipped() || item.getBless() >= 128) {
			return false;
		}
		return true;
	}

	public L1AssessedItem assessItem(L1ItemInstance item) {
		L1ShopItem shopItem = getPurchasingItem(item.getItemId(), item.getEnchantLevel());
		if (shopItem == null) {
			return null;
		}
		return new L1AssessedItem(item.getId(), getAssessedPrice(shopItem));
	}

	private int getAssessedPrice(L1ShopItem item) {
		return (int) (item.getPrice() * Config.RATE_SHOP_PURCHASING_PRICE / item.getPackCount());
	}

	public List<L1AssessedItem> assessItems(L1PcInventory inv) {
		List<L1AssessedItem> result = new ArrayList<L1AssessedItem>();
		for (L1ShopItem item : _purchasingItems) {
			for (L1ItemInstance targetItem : inv.findItemsId(item.getItemId())) {
				if (!isPurchaseableItem(targetItem)) {
					continue;
				}
				if (item.getEnchant() == targetItem.getEnchantLevel()) { // 인챈트가 같은 아이템만
					result.add(new L1AssessedItem(targetItem.getId(), getAssessedPrice(item)));
				}
			}
		}
		return result;
	}

	public List<L1AssessedItem> assessTickets(L1PcInventory inv) {
		List<L1AssessedItem> result = new ArrayList<L1AssessedItem>();
		for (L1ShopItem item : _purchasingItems) {
			for (L1ItemInstance targetItem : inv.findItemsId(item.getItemId())) {
				if (!isPurchaseableItem(targetItem)) {
					continue;
				}
				float dividend = L1BugBearRace.getInstance().getTicketPrice(targetItem);

				result.add(new L1AssessedItem(targetItem.getId(), (int) (getAssessedPrice(item) * dividend)));
			}
		}
		return result;
	}

	public int getNpcId() {
		return _npcId;
	}

	public List<L1ShopItem> getSellingItems() {
		return _sellingItems;
	}
	
	public List<L1ShopItem> getPurchasingItems() {
		return _purchasingItems;
	}

	public L1ShopBuyOrderList newBuyOrderList() {
		return new L1ShopBuyOrderList(this);
	}

	public L1ShopSellOrderList newSellOrderList(L1PcInstance pc) {
		return new L1ShopSellOrderList(this, pc);
	}

	private void sellPremiumItems(L1PcInventory inv, L1ShopBuyOrderList orderList, L1AdenType at) {
		int aden_type = at.getAdenType();

		if (!inv.consumeItem(aden_type, orderList.getTotalPrice())) {
			inv.getOwner().sendPackets(new S_SystemMessage("구입에 필요한 " + at.getAdenName() + " 을(를) 소비할 수 없었습니다."));
			return;
		}

		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			int enchantLevel = order.getItem().getEnchant();
			item = ItemTable.getInstance().createItem(itemId);

			item.setCount(amount);
			item.setEnchantLevel(enchantLevel);
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}

	private boolean ensurePremiumSell(L1PcInstance pc, L1ShopBuyOrderList orderList, L1AdenType at) {
		int price = orderList.getTotalPrice();
		int FeatherCount = at.getMaxUse();
		int aden_type = at.getAdenType();
		// 오버플로우 체크
		if (!IntRange.includes(price, 0, FeatherCount)) {
			pc.sendPackets(new S_ChatPacket(pc, at.getAdenName() + "은 한번에 " + FeatherCount + "개 이상 사용할 수 없습니다."));
			return false;
		}

		if (!pc.getInventory().checkItem(aden_type, price)) {
			pc.sendPackets(new S_ChatPacket(pc, at.getAdenName() + "이(가) 부족합니다."));
			return false;
		}

		// 중량 체크
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			// 아이템이 너무 무거워, 더 이상 가질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		// 개수 체크
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			// \f1한사람의 캐릭터가 가지고 걸을 수 있는 아이템은 최대 180개까지입니다.
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}
	
	public L1ShopItem getSellingItem(int itemId) {
		for (L1ShopItem shopItem : _sellingItems) {
			if (shopItem.getItemId() == itemId) {
				return shopItem;
			}
		}
		return null;
	}

	public boolean isSellingItem(int itemid) {
		for (L1ShopItem a : _sellingItems) {
			if (a.getItemId() == itemid) {
				return true;
			}
		}
		return false;
	}
	
	public L1ShopItem getBuyItem(int itemid) {
		for (L1ShopItem a : _purchasingItems) {
			if (a.getItemId() == itemid) {
				return a;
			}
		}
		return null;
	}
}
