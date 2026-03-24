package l1j.server.GameSystem.Auto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;

public class AutoSellItemController implements Runnable {
	private static AutoSellItemController _instance;

	public static AutoSellItemController getInstance() {
		if (_instance == null) {
			_instance = new AutoSellItemController();
		}
		return _instance;
	}

	public AutoSellItemController() {
		GeneralThreadPool.getInstance().execute(this);
	}

	private Collection<L1PcInstance> list = null;
	public void run() {
		try {
			for (;;) {
				this.list = L1World.getInstance().getAllPlayers();
				for (L1PcInstance pc : this.list) {
					if (pc != null) {
						try {
							doAutoSellAction(pc);
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

	static class SimpleSellingItemFormat {
		int itemId;
		int needCount;
		int enchant;
		int price;
	}

	private void doAutoSellAction(L1PcInstance pc) {
		ArrayList<Integer> _판매리스트 = pc.get_자동판매리스트();
		if (!pc.is_자동판매사용()) {
			return;
		}
		if (_판매리스트 == null || _판매리스트.isEmpty()) {
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
		if (pc.getInventory().checkItem(L1ItemId.ADENA, 1800000000)) {
			return;
		}
		if (!sell_items(pc))
			return;
	}

	private boolean sell_items(L1PcInstance pc) {
	    L1ShopItem shopItem;
	    L1Shop shop = ShopTable.getInstance().get(70037); // 만물상인
	    List<Integer> autoSellItem = new ArrayList<>();
	    ArrayList<Integer> _판매리스트 = pc.get_자동판매리스트();
	    ArrayList<SimpleSellingItemFormat> selling_formats = new ArrayList<SimpleSellingItemFormat>();
	    L1PcInventory inv = pc.getInventory();

	    int totalPrice = 0;

	    for (int i = 0; i < _판매리스트.size(); i++) {
	        int itemId = _판매리스트.get(i);

	        // 인챈트 0짜리만 개수 체크
	        int count = inv.countItems(itemId, 0); 
	        if (count <= 0) {
	            continue;
	        }

	        try {
	            SimpleSellingItemFormat f = new SimpleSellingItemFormat();
	            f.needCount = count;
	            f.itemId = itemId;
	            shopItem = shop.getBuyExceptionEnchantItem(itemId);
	            f.enchant = 0; // 인챈트 0 고정
	            try {
	            	 f.price = shopItem.getPrice() * f.needCount;
	            } catch (Exception e) {
	            	
	            }

	            // 인챈트 0인 것만 소비
	            boolean consumed = inv.consumeItem(f.itemId, f.needCount, 0);
	            if (!consumed) {
	            	System.out.println("실패하면 다음 아이템으로 넘어가기");
	                // 실패하면 다음 아이템으로 넘어가기
	                continue;
	            }

	            totalPrice += f.price;
	            selling_formats.add(f);
	            autoSellItem.add(f.itemId);

	            NumberFormat nf = NumberFormat.getInstance(Locale.KOREA);
	            pc.sendPackets(new S_SystemMessage(
	                shopItem.getItem().getName()
	                + " (" + f.needCount + ") 자동판매 되었습니다. "));
	            pc.sendPackets(new S_SystemMessage("판매 금액: " + nf.format(f.price) + " 아데나"));

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    // 모든 아이템 처리 후 아데나 지급
	    if (totalPrice > 0) {
	        inv.storeItem(L1ItemId.ADENA, totalPrice);
	    }
	    return true;
	}


	public int getItemCount(L1PcInstance pc, int itemid) {
		if (pc.getInventory().checkItem(itemid)) {
			return pc.getInventory().checkItemCount(itemid);
		}
		return 0;
	}
	
	public int getEnchantLevel(L1PcInstance pc, int itemid) {
		if (pc.getInventory().checkItem(itemid)) {
			return pc.getInventory().getEnchantCount(itemid);
		}
		return 0;
	}
}