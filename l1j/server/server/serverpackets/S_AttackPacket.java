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

import java.util.Random;
import java.util.logging.Logger;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_AttackPacket extends ServerBasePacket {
	private static final String S_ATTACK_PACKET = "[S] S_AttackPacket";
	
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(S_AttackPacket.class.getName());

	private byte[] _byte = null;
	private static final Random _random = new Random();
	public S_AttackPacket(L1PcInstance pc, int objid, int type) {
		buildpacket(pc, objid, type, 0);
	}
	public S_AttackPacket(L1PcInstance pc, int objid, int type, int attacktype) {
		buildpacket(pc, objid, type, attacktype);
	}
	private void buildpacket(L1PcInstance pc, int objid, int type, int attacktype) {
		int alttype = (_random.nextInt(6));
		writeC(Opcodes.S_OPCODE_ATTACKPACKET);
		if (pc.getGfxId().getTempCharGfx() == 14491 || pc.getGfxId().getTempCharGfx() == 19500) {
			if (pc.getCurrentWeapon() == 50) { // 양검
				switch (alttype) {
				case 0: // alt attack
					type = 32;
					break;
				case 1: // alt attack
					type = 32;
					break;
				default:
					type = 51;
					break;
				}
			} else if (pc.getCurrentWeapon() == 46) { // 단검
				switch (alttype) {
				case 0: // alt attack
					type = 33;
					break;
				case 1: // alt attack
					type = 34;
					break;
				default:
					type = 47;
					break;
				}
			} else if (pc.getCurrentWeapon() == 4) { // 한손검
				switch (alttype) {
				case 0: // alt attack
					type = 34;
					break;
				case 1:
					type = 33;
					break;
				default:
					type = 5;
					break;
				}
			} else if (pc.getCurrentWeapon() == 24) { // 창
				switch (alttype) {
				case 0: // alt attack
					type = 35;
					break;
				default:
					type = 25;
					break;
				}
			} else if (pc.getCurrentWeapon() == 11) { // 도끼
				switch (alttype) {
				case 0: // alt attack
					type = 36;
					break;
				default:
					type = 12;
					break;
				}
			} else if (pc.getCurrentWeapon() == 54 || pc.getCurrentWeapon() == 58) { // 이도류 크로우
				switch (alttype) {
				case 0: // alt attack
				case 1:
				case 2:
					type = 37;
					break;
				default:
					type = 55;
					break;
				}
			} else if (pc.getCurrentWeapon() == 40) { // 지팡이
				switch (alttype) {
				case 0: // alt attack
					type = 38;
					break;
				default:
					type = 41;
					break;
				}
			}
		} else if (pc.getGfxId().getTempCharGfx() == 16008) {
			switch (alttype) {
			case 0: // alt attack
				type = 30;
				break;
			default:
				break;
			}
		} else if (pc.getGfxId().getTempCharGfx() == 16053) {
			switch (alttype) {
			case 0: // alt attack
				type = 30;
				break;
			case 1:
				type = 31;
			default:
				break;
			}
		} else if (pc.getGfxId().getTempCharGfx() == 16027 || pc.getGfxId().getTempCharGfx() == 16040) {
			switch (alttype) {
			case 0: // alt attack
				type = 18;
				break;
			case 1:
				type = 1;
				break;
			default:
				type = 30;
				break;
			}
		} else if (pc.getGfxId().getTempCharGfx() == 16056 || pc.getGfxId().getTempCharGfx() == 16284) {
			switch (alttype) {
			case 0: // alt attack
			case 1:
				type = 30;
				break;
			default:
				break;
			}
		}
		writeC(type);
		writeD(pc.getId());
		writeD(objid);
		writeC(0x01); // damage
		writeC(0x00);
		writeC(pc.getMoveState().getHeading());
		writeH(0x0000); // target x
		writeH(0x0000); // target y
		writeC(attacktype); // 0:none 2:크로우 4:이도류 0x08:CounterMirror
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}

		return _byte;
	}

	@Override
	public String getType() {
		return S_ATTACK_PACKET;
	}
}
