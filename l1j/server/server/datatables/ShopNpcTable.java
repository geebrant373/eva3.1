/*
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html Author: ChrisLiu.2007.07.20
 */

package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.utils.SQLUtil;

public class ShopNpcTable {

  private static final long serialVersionUID = 1L;

  private static Logger _log = Logger.getLogger(ShopNpcTable.class.getName());

  private static ShopNpcTable _instance;

  private final Map<Integer, L1Shop> _npcShops = new HashMap<Integer, L1Shop>();

  private static Random _random = new Random(System.nanoTime());

  public static ShopNpcTable getInstance() {
    if (_instance == null) {
      _instance = new ShopNpcTable();
    }
    return _instance;
  }

  public static void reloding() {
    try {
      ShopNpcTable oldInstance = _instance;
      _instance = new ShopNpcTable();
      if (oldInstance != null && oldInstance._npcShops != null)
        oldInstance._npcShops.clear();
    } catch (Exception e) {
    }
  }

  private ShopNpcTable() {
    loadShops();
  }

  private ArrayList<Integer> enumNpcIds() {
    ArrayList<Integer> ids = new ArrayList<Integer>();

    Connection con = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      con = L1DatabaseFactory.getInstance().getConnection();
      pstm = con.prepareStatement("SELECT DISTINCT npc_id FROM shop_npc_item");
      rs = pstm.executeQuery();
      while (rs.next()) {
        ids.add(rs.getInt("npc_id"));
      }
    } catch (SQLException e) {
      _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
    } finally {
      SQLUtil.close(rs, pstm, con);
    }
    return ids;
  }

  private L1Shop loadShop(int npcId, ResultSet rs) throws SQLException {
    List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();
    List<L1ShopItem> purchasingList = new ArrayList<L1ShopItem>();
    L1ShopItem item = null;
    while (rs.next()) {
      int itemId = rs.getInt("item_id");
      int sellingPrice = rs.getInt("selling_price");
      int purchasingPrice = rs.getInt("purchasing_price");
      int count = rs.getInt("count");
      int enchant = rs.getInt("enchant");
      int bless = rs.getInt("bless");
      switch (itemId) {
        case 62: // 무양
          sellingPrice += (_random.nextInt(5) + 1) * 100000;
          break;
        case 81: // 흑이
          sellingPrice += (_random.nextInt(3) + 1) * 100000;
          break;
        case 188: // 라헤비
          sellingPrice += (_random.nextInt(5) + 1) * 10000;
          break;
        case 40074:
        case 40087:
          sellingPrice -= (_random.nextInt(5) + 1) * 1000;
          count -= (_random.nextInt(5) + 1) * 3;
          break;
        case 140074:
        case 140087:
        case 240074:
        case 240087:
          sellingPrice -= (_random.nextInt(5) + 1) * 10000;
          count -= (_random.nextInt(5) + 1) * 1;
          break;
      }
      if (0 <= sellingPrice) {
        item = new L1ShopItem(itemId, sellingPrice, 1, enchant, bless);
        item.setCount(count);
        sellingList.add(item);
      }
      if (0 <= purchasingPrice) {
        item = new L1ShopItem(itemId, purchasingPrice, 1, enchant, bless);
        purchasingList.add(item);
      }
    }
    return new L1Shop(npcId, sellingList, purchasingList);
  }

  private void loadShops() {
    Connection con = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      con = L1DatabaseFactory.getInstance().getConnection();
      pstm = con.prepareStatement("SELECT * FROM shop_npc_item WHERE npc_id=?");
      L1Shop shop = null;
      for (int npcId : enumNpcIds()) {
        pstm.setInt(1, npcId);
        rs = pstm.executeQuery();
        shop = loadShop(npcId, rs);
        _npcShops.put(npcId, shop);
        rs.close();
      }
    } catch (SQLException e) {
      _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
    } finally {
      SQLUtil.close(rs, pstm, con);
    }
  }

  public L1Shop get(int npcId) {
    return _npcShops.get(npcId);
  }
}
