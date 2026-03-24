package l1j.server.server.serverpackets;

public class KeyPacket extends ServerBasePacket{
	private byte[] _byte = null;
//	12 00 37 E4 82 E9 61 E8  26 E3 48 16 7F 01 00 00  
//	00 00
	public KeyPacket(){
		byte[] _byte1 = {  
				(byte) 0x3c,
				(byte) 0x65, (byte) 0xb6, (byte) 0xbd, (byte) 0x65,
				(byte) 0xcc,
				(byte) 0xd0, (byte) 0x7e, (byte) 0x53, (byte) 0x2e, (byte) 0xfa,
				(byte) 0xc1 };
		for(int i=0;i<_byte1.length; i++){
			writeC(_byte1[i]);
		}

	}


	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}
}
