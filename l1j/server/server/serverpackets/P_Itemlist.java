package l1j.server.server.serverpackets;

import java.io.IOException;
import java.util.Collection;

import l1j.server.server.clientpackets.P_connect.itemdata;

public class P_Itemlist extends ServerBasePacket {
	private static final String S_ADD_SKILL = "[S] S_AddSkill";

	private byte[] _byte = null;

	public P_Itemlist(Collection<itemdata> itemlist) {
		writeC(119);
		writeC(119);
		writeC(119);
		writeC(itemlist.size());
		for (itemdata data : itemlist) {
			writeS(data.name);
			writeS(data.pcname);
			writeC(data.blass);
			writeD(data.price);
			writeS(data.loc);
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

}
