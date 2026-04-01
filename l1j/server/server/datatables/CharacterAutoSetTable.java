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

package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.L1QueryUtil;
import l1j.server.server.utils.SQLUtil;


public class CharacterAutoSetTable {

	private static Logger _log = Logger.getLogger(CharacterAutoSetTable.class.getName());

	private static CharacterAutoSetTable _instance;


	public static CharacterAutoSetTable getInstance() {
		if (_instance == null) {
			_instance = new CharacterAutoSetTable();
		}
		return _instance;
	}
	// 확인 완료
	public void load(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_auto_set WHERE objid=?");
			pstm.setInt(1, pc.getId());
			rs = pstm.executeQuery();
			while (rs.next()) {
				pc.set_자동물약퍼센트(rs.getInt("Auto_Potion_Percent"));
				String potions = rs.getString("Auto_Potion_List");
				if (potions != null && !potions.isEmpty()) {			
					StringTokenizer list = new StringTokenizer(potions, ",");
					while (list.hasMoreElements()) {
						String s = list.nextToken();
						pc.get_자동물약리스트().add(Integer.valueOf(s));
					}
				}
				pc.set_자동물약사용(rs.getInt("Auto_Potion_Use") == 0 ? false : true);
				pc.set_자동버프사용(rs.getInt("Auto_Buff_Use") == 0 ? false : true);
				String buffs = rs.getString("Auto_Buff_List");
				if (buffs != null && !buffs.isEmpty()) {			
					StringTokenizer list = new StringTokenizer(buffs, ",");
					while (list.hasMoreElements()) {
						String s = list.nextToken();
						pc.get_자동버프리스트().add(Integer.valueOf(s));
					}
				}
				pc.set_자동버프전투시사용(rs.getInt("Auto_Buff_Fight_Use") == 0 ? false : true);
				pc.set_자동버프세이프티존사용(rs.getInt("Auto_Buff_Safety_Use") == 0 ? false : true);
				
				String sellitems = rs.getString("Auto_Sell_List");
				if ((sellitems != null) && (!sellitems.isEmpty())) {
					StringTokenizer list = new StringTokenizer(sellitems, ",");
					while (list.hasMoreElements()) {
						String s = list.nextToken();
						pc.get_자동판매리스트().add(Integer.valueOf(s));
					}
				}
			}
		}
		catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	// 확인 완료
	public void store(L1PcInstance pc) {
		Connection con = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sql = "INSERT INTO character_auto_set (objid, char_name, Auto_Potion_Percent, Auto_Potion_List, Auto_Potion_Use, Auto_Buff_Use, Auto_Buff_List, Auto_Buff_Fight_Use, Auto_Buff_Safety_Use, Auto_Sell_List) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE objid=?, char_name=?, Auto_Potion_Percent=?, Auto_Potion_List=?, Auto_Potion_Use=?, Auto_Buff_Use=?, Auto_Buff_List=?, Auto_Buff_Fight_Use=?, Auto_Buff_Safety_Use=?, Auto_Sell_List=?";

			ArrayList<Integer> 물약리스트 = pc.get_자동물약리스트();
			String 물약리스트1 = 물약리스트 == null ? null : 물약리스트.toString();
			if (물약리스트1 != null) {
				물약리스트1 = 물약리스트1.replace("[", "");
				물약리스트1 = 물약리스트1.replace("]", "");
				물약리스트1 = 물약리스트1.replace(" ", "");
			}
			ArrayList<Integer> 버프리스트 = pc.get_자동버프리스트();
			String 버프리스트1 = 버프리스트 == null ? null : 버프리스트.toString();
			if (버프리스트1 != null) {
				버프리스트1 = 버프리스트1.replace("[", "");
				버프리스트1 = 버프리스트1.replace("]", "");
				버프리스트1 = 버프리스트1.replace(" ", "");
			}
			ArrayList<Integer> 판매리스트 = pc.get_자동판매리스트();
			String 판매리스트1 = 판매리스트 == null ? null : 판매리스트.toString();
			if (판매리스트1 != null) {
				판매리스트1 = 판매리스트1.replace("[", "");
				판매리스트1 = 판매리스트1.replace("]", "");
				판매리스트1 = 판매리스트1.replace(" ", "");
			}
			L1QueryUtil.execute(con, sql
					, pc.getId(), pc.getName(), pc.get_자동물약퍼센트(), 물약리스트1, pc.is_자동물약사용() ? 1 : 0, pc.is_자동버프사용() ? 1 : 0, 버프리스트1, pc.is_자동버프전투시사용() ? 1 : 0, pc.is_자동버프세이프티존사용() ? 1 : 0, 판매리스트1
							, pc.getId(), pc.getName(), pc.get_자동물약퍼센트(), 물약리스트1, pc.is_자동물약사용() ? 1 : 0, pc.is_자동버프사용() ? 1 : 0, 버프리스트1, pc.is_자동버프전투시사용() ? 1 : 0, pc.is_자동버프세이프티존사용() ? 1 : 0, 판매리스트1);
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(con);
		}
	}
}
