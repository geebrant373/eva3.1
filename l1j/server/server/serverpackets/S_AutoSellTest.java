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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.templates.L1Item;

public class S_AutoSellTest extends ServerBasePacket {

    public S_AutoSellTest(L1PcInstance pc, int npcId) {
        pc._자동판매초이스리스트 = new ArrayList<>();
        Map<Integer, Integer> npcItemPriceMap = getItemsWithPrice(npcId);
        L1ItemInstance dummy = new L1ItemInstance();
        L1Item template = null;
        // 자동판매 조건 필터
		for (L1ItemInstance item : pc.getInventory().getItems()) {
			if (!npcItemPriceMap.containsKey(item.getItem().getItemId()))
				continue;
			if (item.getBless() == 129)
				continue;
			if (pc.getInventory().checkEquipped(item.getItem().getItemId()))
				continue;
			if (item.getEnchantLevel() > 0)
				continue;
			if (item.getItemId() == L1ItemId.ADENA)
				continue;

			pc._자동판매초이스리스트.add(item);
		}

        int size = pc._자동판매초이스리스트.size();
        writeC(Opcodes.S_OPCODE_SHOWSHOPBUYLIST);
        writeD(-1); // NPC ObjId 대신 -1 사용
        writeH(size);

        for (L1ItemInstance item : pc._자동판매초이스리스트) {
            int price = npcItemPriceMap.get(item.getItem().getItemId());
            writeD(item.getId());              // 오브젝트 ID
            writeH(item.get_gfxid());          // 아이템 아이콘
            writeD(price);                     // 아이템 가격
            writeS(item.getViewName());        // 아이템 이름
            
            template = ItemTable
    				.getInstance().getTemplate(item.getItemId());
    		
    		if (template == null) {
    			writeC(0);
    		} else {
    			dummy.setItem(template);
    			byte[] status = dummy.getStatusBytes();
    			if (status != null) {
    				writeC(status.length);
    				for (byte b : status) {
    					writeC(b);
    				}
    			} 
    		}
        }
        writeH(0x07);// 0x00:kaimo 0x01:pearl 0x07:adena
        pc.setTempShipID(789223);
    }

    private Map<Integer, Integer> getItemsWithPrice(int npcId) {
        Map<Integer, Integer> map = new HashMap<>();
        String sql = "SELECT item_id, purchasing_price FROM shop WHERE npc_id = ? AND purchasing_price > 0";

        try (Connection con = L1DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, npcId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("item_id"), rs.getInt("purchasing_price"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    @Override
    public byte[] getContent() throws IOException {
        return getBytes();
    }
}
