package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_SearchAdenaTrade4 extends ServerBasePacket {
	public S_SearchAdenaTrade4(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(pc.getId());
		writeS("tradeinfo");
		writeC(0);
		writeH(30);
	}
	public byte[] getContent() {
		return getBytes();
	}
}
