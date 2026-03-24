package l1j.server.server.serverpackets;

import java.util.concurrent.atomic.AtomicInteger;

import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_AttackCritical extends ServerBasePacket {

    private static final String S_AttackCritical = "[S] S_AttackCritical";

    private byte[] _byte = null;

    private static AtomicInteger _sequentialNumber = new AtomicInteger(0);

    /** 활이 아닌 경우 **/
    public S_AttackCritical(L1PcInstance pc, int objid, int type) {
    	int autoNum = _sequentialNumber.incrementAndGet();
    	
    	
    	L1Object o = L1World.getInstance().findObject(objid);
    	
    	if(o == null) {
    		return;
    	}
    	
    	int x = o.getX();
    	int y = o.getY();
    	
        writeC(Opcodes.S_OPCODE_ATTACKPACKET);
        writeC(1);
        
        writeD(0);
        writeD(objid);
        writeC(1);
        writeC(0x00);
        writeC(pc.getMoveState().getHeading());
        writeD(autoNum); // 번호가 겹치지 않게 보낸다
        

        int gfx = 13414;
        
        switch (type) {
	        //한손검
	        case 1:
	            gfx = 13411;
	            break;
	        //단검
	        case 2:
	            gfx = 13412;
	            break;
	        case 3:
	            //양손검
	            gfx = 13410;
	            break;
	        case 4:
	            break;
	        //도끼
	        case 6:
	            gfx = 13414;
	            break;
	        case 7:
	            //지팡이
	            gfx = 13413;
	            break;
	        //크로우
	        case 11:
	            gfx = 13416;
	            break;
	        case 12:
	            //이도류
	            gfx = 13417;
	            break;
	        //양손도끼
	        case 15:
	            gfx = 13415;
	            break;
	        case 99:
	            gfx = 13415;
	            break;
	    }
        
        writeH(gfx);
        writeC(0);
        writeH(x);
        writeH(y);
        writeH(x);
        writeH(y);
        writeD(0);
    }

    /** 활 모션 **/
    public S_AttackCritical(L1Character cha, int targetobj, int x, int y, int type, boolean isHit) {
        int gfxid = 0;
        int aid = 1;
        // 오크 궁수에만 변경
        if (cha.getTempCharGfx() == 3860 || cha.getTempCharGfx() == 7959) {
            aid = 21;
        }
        writeC(Opcodes.S_OPCODE_ATTACKPACKET);
        writeC(aid);
        writeD(cha.getId());
        writeD(targetobj);
        writeC(isHit ? 6 : 0);
        writeC(0x00);
        writeC(cha.getMoveState().getHeading());
        writeD(_sequentialNumber.incrementAndGet());
        if (type == 20) {
            gfxid = 13392;
        } else if (type == 62) {
            gfxid = 13398;
        }
        writeH(gfxid);
        writeC(127); 
        writeH(x);
        writeH(y);
        writeH(cha.getX());
        writeH(cha.getY());
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
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
        return S_AttackCritical;
    }
}
