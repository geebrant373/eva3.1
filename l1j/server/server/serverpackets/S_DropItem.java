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

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1ItemInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_DropItem extends ServerBasePacket {

	private static final String _S__OB_DropItem = "[S] S_DropItem";

	private byte[] _byte = null;

	public S_DropItem(L1ItemInstance item) {
		buildPacket(item);
	}

	private void buildPacket(L1ItemInstance item) {
		// int addbyte = 0;
		// int addbyte1 = 1;
		// int addbyte2 = 13;
		// int setting = 4;
		writeC(Opcodes.S_OPCODE_SHOWOBJ);
		writeH(item.getX());
		writeH(item.getY());
		writeD(item.getId());
		writeH(item.getItem().getGroundGfxId());
		writeC(0);
		writeC(0);
		if (item.isNowLighting()) {
			writeC(item.getItem().getLightRange());
		} else {
			writeC(0);
		}
		writeC(0);
		writeD(item.getCount());
		writeC(0);
		writeC(0);

		/***********************************************************************
		 ***********************************************************************/
		StringBuffer sb = null;
		sb = new StringBuffer();
		if (item.isIdentified()) {
			if (item.getItem().getType2() == 1
					|| item.getItem().getType2() == 2) {
				switch (item.getAttrEnchantLevel()) {// by사부
				case 1:   //1~5화 6~10 수 11~15 풍 16~20지
					sb.append("$6115");
					break; 
				case 2:
					sb.append("$6116");
					break;
				case 3:
					sb.append("$6117");
					break;
				case 4:
					sb.append("$14361");
					break;
				case 5:
					sb.append("$14365 ");
					break;
				case 6:
					sb.append("$6118");
					break;
				case 7:
					sb.append("$6119");
					break;
				case 8:
					sb.append("$6120");
					break;
				case 9:
					sb.append("$14362");
					break;
				case 10:
					sb.append("$14366 ");
					break;
				case 11:
					sb.append("$6121");
					break;
				case 12:
					sb.append("$6122");
					break;
				case 13:
					sb.append("$6123");
					break;
				case 14:
					sb.append("$14363");
					break;
				case 15:
					sb.append("$14367 ");
					break;
				case 16:
					sb.append("$6124");
					break;
				case 17:
					sb.append("$6125");
					break;
				case 18:
					sb.append("$6126");
					break;
				case 19:
					sb.append("$14364");
					break;
				case 20:
					sb.append("$14368 ");
					break;
			

				default:
					sb.append(" ");
					break;
				// 주석안할경우 ALT눌렀을때나 마우스올려놨을때나 인첸표시
				// 주석할경우 ALT눌렀을때만 인첸표시 (본섭)
				}
				// 인첸 +0 일때도 표기되게 하실분은 밑에 if (item.getEnchantLevel() >= 0) {로 교체
				// by사부
				if (item.getEnchantLevel() > 0) {
					sb.append("+" + item.getEnchantLevel() + " ");
				} else if (item.getEnchantLevel() < 0) {
					sb.append(String.valueOf(item.getEnchantLevel()) + " ");
				}
			}
		}
		sb.append(item.getItem().getNameId());
		int itemId = item.getItem().getItemId();
		int isId = item.isIdentified() ? 1 : 0;
		if (item.getCount() > 1) {
			sb.append(" (" + item.getCount() + ")");
		} else {
			/*
			 * } else if ((itemId == 40006 || itemId == 40007 || itemId == 40008
			 * || itemId == 40009 || itemId == 140006 || itemId == 140008) &&
			 * isId == 1) { sb.append(" (" + item.getChargeCount() + ")");
			 */
			if (item.getItem().getLightRange() != 0 && item.isNowLighting()) {
				sb.append(" ($10)");
			}
		}


		writeS(sb.toString());// by사부
		/***********************************************************************
		 ***********************************************************************/
		


		writeC(0);
		writeD(0);
		writeD(0);
		writeC(255);
		writeC(0);
		writeC(0);
		writeC(0);
		writeH(65535);
		// writeD(0x401799a);
		writeD(0);
		writeC(8);
		writeC(0);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}
	@Override
	public String getType() {
		return _S__OB_DropItem;
	}

}
