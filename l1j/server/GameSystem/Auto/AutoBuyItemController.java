package l1j.server.GameSystem.Auto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;

public class AutoBuyItemController implements Runnable {
	private static AutoBuyItemController _instance;

	public static AutoBuyItemController getInstance() {
		if (_instance == null) {
			_instance = new AutoBuyItemController();
		}
		return _instance;
	}

	public AutoBuyItemController() {
		GeneralThreadPool.getInstance().execute(this);
	}

	private Collection<L1PcInstance> list = null;
	private static final Map<Integer, Integer> cbuyaItemsMapi = new HashMap<>();
	static {
		cbuyaItemsMapi.put(40024, 500);
		cbuyaItemsMapi.put(40018, 10);
		cbuyaItemsMapi.put(40100, 500);
		cbuyaItemsMapi.put(40088, 10);
		cbuyaItemsMapi.put(41246, 5000);
		cbuyaItemsMapi.put(40319, 300);
		cbuyaItemsMapi.put(40321, 100);
		cbuyaItemsMapi.put(40318, 100);
		cbuyaItemsMapi.put(40068, 20);//엘븐 와퍼
		cbuyaItemsMapi.put(40079, 50);//귀환 주문서
		cbuyaItemsMapi.put(40124, 50);//혈맹 귀환 주문서
		cbuyaItemsMapi.put(40317, 20);//숫돌
	}
	private static final Map<String, Integer> caitemsMapi = new HashMap<>();
	static {
		caitemsMapi.put("신속 강력 체력 회복제", 40024);
		caitemsMapi.put("강화 속도향상 물약", 40018);
		caitemsMapi.put("순간이동 주문서", 40100);
		caitemsMapi.put("변신 주문서", 40088);
		caitemsMapi.put("결정체", 41246);
		caitemsMapi.put("정령옥", 40319);
		caitemsMapi.put("흑요석", 40321);
		caitemsMapi.put("마력의 돌", 40318);
		caitemsMapi.put("엘븐 와퍼", 40068);
		caitemsMapi.put("귀환 주문서", 40079);
		caitemsMapi.put("혈맹 귀환 주문서", 40124);
		caitemsMapi.put("숫돌", 40317);
	}
	public void run() {
		try {
			for (;;) {
				this.list = L1World.getInstance().getAllPlayers();
				for (L1PcInstance pc : this.list) {
					if (pc != null) {
						try {
							doAutoBuyAction(pc);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				try {
					Thread.sleep(300L);
				} catch (Exception localException4) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				Thread.sleep(300L);
			} catch (Exception localException3) {
			}
		}
	}

	static class SimplePurchasingItemFormat {
		int itemId;
		int needCount;
		int price;
	}

	private void doAutoBuyAction(L1PcInstance pc) {
		ArrayList<Integer> _구입리스트 = pc.get_자동구입리스트();
		if (!pc.is_자동구입사용()) {
			return;
		}
		if ((_구입리스트 == null) || (_구입리스트.isEmpty())) {
			return;
		}
		if (pc.getAutoHuntStatus()) {
			return;
		}
		if (pc.isDead()) {
			return;
		}
		if (pc.isFishing()) {
			return;
		}
		if (pc.isPrivateShop() || pc.isAutoClanjoin()) {
			return;
		}
		if (!buy_items(pc, cbuyaItemsMapi))
			return;
	}

	private boolean buy_items(L1PcInstance pc, Map<Integer, Integer> cbuyaItemsMapi) {
		L1ShopItem shopItem;
		L1Shop shop = ShopTable.getInstance().get(70030); // 자동구입하는 아이템의 가격을 가져온다.
		List<Integer> autoBuyItem = new ArrayList<>();
		List<String> autoBuyItemName = new ArrayList<>();
		ArrayList<Integer> _구입리스트 = pc.get_자동구입리스트();
		ArrayList<SimplePurchasingItemFormat> purcasing_formats = new ArrayList<SimplePurchasingItemFormat>();
		NumberFormat nf = NumberFormat.getInstance();
		L1PcInventory inv = pc.getInventory();
		nf.setGroupingUsed(true);

		int totalPrice = 0;

		for (int i = 0; i < _구입리스트.size(); i++) {
		    int itemId = _구입리스트.get(i);
		    if (pc.getInventory().checkItem(itemId)) {
		        continue;
		    }
		    try {
		        SimplePurchasingItemFormat f = new SimplePurchasingItemFormat();
		        f.needCount = getitemcount(itemId);
		        f.itemId = itemId;
		        autoBuyItem.add(f.itemId);
		        autoBuyItemName.add(searchItemName(itemId));
		        shopItem = shop.getSellingItem(itemId);
		        if (shopItem.getPackCount() > 0) {
		        	 f.price = shopItem.getPrice()/shopItem.getPackCount();
		        } else {
		        	f.price = shopItem.getPrice();
		        }
		        f.price *= f.needCount;
		        totalPrice += f.price;
		        purcasing_formats.add(f);
		    } catch (Exception e) {
		    }
		}
		if (totalPrice > 0 && purcasing_formats.size() > 0) {
			if (inv.consumeItem(L1ItemId.ADENA, totalPrice)) {
				for (SimplePurchasingItemFormat f : purcasing_formats) {
					inv.storeItem(f.itemId, f.needCount);
				}
			} else {
				pc.sendPackets(new S_SystemMessage("아데나 " + totalPrice + "가 부족하여 자동구입이 중단됩니다."));
				pc.set_자동구입사용(false);
				return false;
			}
		}
		// 자동구입한 아이템 리스트
		if (!autoBuyItem.isEmpty()) {
			ArrayList<String> result = new ArrayList<String>();
			result.add("자동구입한 아이템 리스트");
			for (int itemid : autoBuyItem) {
				result.add(searchItemName(itemid));
			}
			String formattedPrice = nf.format(totalPrice);
			result.add("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
			result.add("총 " + formattedPrice + " 아데나");
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "autoBuyItemList",
					(String[]) result.toArray(new String[autoBuyItemName.size()])));
		}
		return true;
	}

	public int getitemcount(int itemid) {
		if (cbuyaItemsMapi.containsKey(itemid)) {
			return cbuyaItemsMapi.get(itemid);
		} else {
			System.out.println("해당 아이템은 리스트에 없습니다.");
			return -1;
		}
	}

	public static String searchItemName(int itemId) {
		for (Map.Entry<String, Integer> entry : caitemsMapi.entrySet()) {
			if (entry.getValue().equals(itemId)) {
				return entry.getKey();
			}
		}
		return "아이디에 해당하는 아이템이 없습니다.";
	}
}