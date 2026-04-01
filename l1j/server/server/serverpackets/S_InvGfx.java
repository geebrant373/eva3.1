package l1j.server.server.serverpackets;

import java.io.IOException;


import l1j.server.server.Opcodes;

import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;


public class S_InvGfx extends ServerBasePacket {
	public S_InvGfx(L1PcInstance pc, L1Inventory inventory) {
		int size = inventory.getSize();
		writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
		writeD(pc.getId());
		writeH(size);
		writeC(3); // 개인 창고
		for(int i = 0; i < size; i++){
			L1ItemInstance item = inventory.getItems().get(i);
			writeD(item.getId());
			writeC(0);
			writeH(item.get_gfxid());
			writeC(item.getBless());
			writeD(item.getCount());
			writeC(item.isIdentified() ? 1 : 0);
			writeS(item.getViewName());
		}
	}
	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}
}
