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
 * Author: ChrisLiu.2007.07.20
 */

 
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1Mannequin;
import l1j.server.server.templates.L1NpcShop;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

public class ShopNpcSpawnTable {
	private static Logger _log = Logger.getLogger(ShopNpcSpawnTable.class.getName());

	private static ShopNpcSpawnTable _instance;
	
	private ArrayList<L1NpcShop> npcShoplist = new ArrayList<L1NpcShop>();
	
	private ArrayList<L1Mannequin> mannequinlist = new ArrayList<L1Mannequin>();

	public static ShopNpcSpawnTable getInstance() {
		if (_instance == null) {
			_instance = new ShopNpcSpawnTable();
		}
		return _instance;
	}

	private ShopNpcSpawnTable() {
		load();
		loadMannequin();
	}

	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM shop_npc_spawn");
			rs = pstm.executeQuery();
			do {
				if (!rs.next()) {
					break;
				}
				if (rs.getInt("count") == 0) {
					continue;
				}
				if (rs.getInt("locx") == 0) {
					continue;
				}
				if (rs.getInt("locy") == 0) {
					continue;
				}
				
				L1NpcShop shop = new L1NpcShop();

				shop.setNpcId(rs.getInt("npc_id"));
				shop.setX(rs.getInt("locx"));
				shop.setY(rs.getInt("locy"));
				shop.setMapId(rs.getShort("mapid"));
				shop.setHeading(rs.getInt("heading"));
				shop.setShopName(rs.getString("shop_name"));
				
				npcShoplist.add(shop);
				shop = null;
				
			} while (true);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (SecurityException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (IllegalArgumentException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public void loadMannequin() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_mannequin");
			rs = pstm.executeQuery();
			do {
				if (!rs.next()) {
					break;
				}
				if (rs.getInt("count") == 0) {
					continue;
				}
				if (rs.getInt("locx") == 0) {
					continue;
				}
				if (rs.getInt("locy") == 0) {
					continue;
				}
				
				L1Mannequin npc = new L1Mannequin();

				npc.setNpcid(rs.getInt("npc_id"));
				npc.setX(rs.getInt("locx"));
				npc.setY(rs.getInt("locy"));
				npc.setMapId(rs.getShort("mapid"));
				npc.setHeading(rs.getInt("heading"));
				
				mannequinlist.add(npc);
				npc = null;
				
			} while (true);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (SecurityException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (IllegalArgumentException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public ArrayList<L1NpcShop> getList(){
		return npcShoplist;
	}
	
	public ArrayList<L1Mannequin> getMannequinList(){
		return mannequinlist;
	}
	
}
