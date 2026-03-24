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
package l1j.server.server.serverpackets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;

public class S_AutoSellList extends ServerBasePacket {
	public S_AutoSellList(L1PcInstance pc) {
		pc._자동판매초이스리스트 = new ArrayList<L1ItemInstance>();
		Set<Integer> npcItems = getItemsForNPC(70037);
		for (L1ItemInstance item : pc.getInventory().getItems()) {
			if (npcItems.contains(item.getItem().getItemId())) {
				if (item.getBless() == 129) {
					continue;
				}
				if (pc.getInventory().checkEquipped(item.getItem().getItemId())) {
					continue;
				}
				if (item.getEnchantLevel() > 0) {
					continue;
				}
				if (item.getItemId() == L1ItemId.ADENA) {
					continue;
				}
				pc._자동판매초이스리스트.add(item);
			}
		}
		int size = pc._자동판매초이스리스트.size();
		if (size > 0) {
			writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
			writeD(-1);
			writeH(size);
			writeC(3); // 개인 창고
			for (L1ItemInstance item : pc._자동판매초이스리스트) {
				writeD(item.getId());
				writeC(0);
				writeH(item.get_gfxid());
				writeC(item.getBless());
				writeD(item.getCount());
				writeC(item.isIdentified() ? 1 : 0);
				writeS(item.getViewName());
			}
			pc.setTempShipID(789223);
		}
	}

	public Set<Integer> getItemsForNPC(int npcId) {
	    Set<Integer> itemIds = new HashSet<>();
	    String sql = "SELECT item_id FROM shop WHERE npc_id = ? AND purchasing_price > 0";

	    try (Connection con = L1DatabaseFactory.getInstance().getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, npcId);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                itemIds.add(rs.getInt("item_id"));
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return itemIds;
	}
	
	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}

}
