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

public class S_AutoBuyList extends ServerBasePacket {
	public S_AutoBuyList(L1PcInstance pc) {
		pc._자동구입초이스리스트 = new ArrayList<L1ItemInstance>();
		for (L1ItemInstance item : pc.getInventory().getItems()) {
			if (item.getItemId() == 40024 // 고대의 강력 체력 회복제
					|| item.getItemId() == 40018 // 강화 속도향상 물약
					|| item.getItemId() == 40068 // 엘븐 와퍼
					|| item.getItemId() == 41415 // 강화 용기의 물약
					|| item.getItemId() == 40031 // 악마의 피
					|| item.getItemId() == 40015 // 마력 회복 물약
					|| item.getItemId() == 40016 // 지혜의 물약
					|| item.getItemId() == 40017 // 해독제
					) {
				pc._자동구입초이스리스트.add(item);
			}
	}
		int size = pc._자동구입초이스리스트.size();
		if (size > 0) {
			writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
			writeD(-1);
			writeH(size);
			writeC(3); // 개인 창고
			for (L1ItemInstance item : pc._자동구입초이스리스트) {
				writeD(item.getId());
				writeC(0);
				writeH(item.get_gfxid());
				writeC(item.getBless());
				writeD(item.getCount());
				writeC(item.isIdentified() ? 1 : 0);
				writeS(item.getViewName());
			}
			pc.setTempShipID(789222);
		}
	}

	public Set<Integer> getItemsForNPC(int npcId) {
        Set<Integer> itemIds = new HashSet<>(); // 중복된 아이템을 저장하기 위해 HashSet으로 변경
        try {
            Connection con = L1DatabaseFactory.getInstance().getConnection();
            Statement statement = con.createStatement();
            String sql = "SELECT item_id FROM shop WHERE npc_id = " + npcId;
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                itemIds.add(rs.getInt("item_id"));
            }
            rs.close();
            statement.close();
            con.close();
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
