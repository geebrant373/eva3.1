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

package l1j.server.server.serverpackets;

import java.util.List;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ShopNpcTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1ShopItem;

public class S_PrivateShopNpc extends ServerBasePacket {

	public S_PrivateShopNpc(L1PcInstance pc, int objId, int type) {
		L1NpcInstance npc = (L1NpcInstance) L1World.getInstance().findObject(objId);

		if (npc == null) {
			return;
		}

		writeC(Opcodes.S_OPCODE_PRIVATESHOPLIST);
		writeC(type);
		writeD(objId);

		int npcId = npc.getNpcTemplate().get_npcId();
		L1Shop shop = ShopNpcTable.getInstance().get(npcId);
		List<L1ShopItem> shopItems = shop.getSellingItems();

		int size = shopItems.size();
		pc.setPartnersPrivateShopItemCount(size);
		writeH(size);
		L1ItemInstance dummy = new L1ItemInstance();
		for (int i = 0; i < size; i++) {
			L1ShopItem shopItem = shopItems.get(i);
			L1Item item = shopItem.getItem();
			dummy.setItem(item);
			if (dummy != null) {
				dummy.setEnchantLevel(shopItem.getEnchant());
				dummy.setBless(shopItem.getBless());
				dummy.setCount(shopItem.getCount());
				dummy.setIdentified(true);

				writeC(i);
				writeC(dummy.getBless());
				writeH(dummy.get_gfxid());
				writeD(dummy.getCount());
				writeD(shopItem.getPrice());
				writeS(dummy.getNumberedName(dummy.getCount()));
				byte[] status = dummy.getStatusBytes();
				if (status != null) {
					writeC(status.length);
					writeByte(status);
				}
			}
		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
