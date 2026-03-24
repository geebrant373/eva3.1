package l1j.server.server.serverpackets;

import java.util.HashMap;




import l1j.server.MJCTSystem.MJCTSpell;
import l1j.server.MJCTSystem.Loader.MJCTSpellLoader;
import l1j.server.server.Opcodes;

public class S_InventoryIcon extends ServerBasePacket {
	public static final int SHOW_INVEN_BUFFICON = 110;
	
	public static final int TYPE_EFF_NONE 									= 0x00;
	public static final int TYPE_EFF_PERCENT 								= 0x01;
	public static final int TYPE_EFF_MINUTE 								= 0x02;
	public static final int TYPE_EFF_PERCENT_ORC_SERVER 					= 0x03;
	public static final int TYPE_EFF_EINHASAD_COOLTIME_MINUTE 				= 0x04;
	public static final int TYPE_EFF_LEGACY_TIME 							= 0x05;
	public static final int TYPE_EFF_VARIABLE_VALUE 						= 0x06;
	public static final int TYPE_EFF_DAY_HOUR_MIN 							= 0x07;
	public static final int TYPE_EFF_AUTO_DAY_HOUR_MIN_SEC 					= 0x08;
	public static final int TYPE_EFF_NSERVICE_TOPPING 						= 0x09;
	public static final int TYPE_EFF_UNLIMIT 								= 0x0A;
	public static final int TYPE_EFF_CUSTOM 								= 0x0B;
	public static final int TYPE_EFF_COUNT 									= 0x0C;
	
	private static final HashMap<Integer, StrMessageId> _skillIdToMsgIds = new HashMap<Integer, StrMessageId>(8);
	static{

	}
	
	private static final StrMessageId _dummyId = new StrMessageId(0,0,0);
	
	static class StrMessageId{
		int tooltipId;
		int newId;
		int endId;
		
		StrMessageId(int t, int n, int e){
			tooltipId 		= t;
			newId 		= n;
			endId 		= e;
		}
	}
	
	public static S_InventoryIcon on(int priority, int type, int skillId, int showType,long time, boolean isGood, int invGfx, int tooltip, int startId, int endId){
		S_InventoryIcon s = new S_InventoryIcon();
		s.writeC(0x08);			// noti_type(new = 1, reset = 2, end = 3)
		s.writeC(type);
		s.writeC(0x10);			// spell_id
		s.writeBit(skillId);
		s.writeC(0x18);			// duration
		s.writeBit(time);
		s.writeC(0x20);			// duration_show_type
		s.writeBit(showType);
		s.writeC(0x28);			// on_icon_id	
		/*if (skillId == 23069)
			s.writeBit(3069);
		else*/
			s.writeBit(invGfx);
		s.writeC(0x30);			// off_icon_id
		s.writeC(0x00);
		s.writeC(0x38);			// icon_priority
		s.writeC(priority);
		s.writeC(0x40);			// tooltip_str_id
		s.writeBit(tooltip);
		s.writeC(0x48);			// new_str_id
		s.writeBit(startId);
		s.writeC(0x50);			// end_str_id
		s.writeBit(endId);
		s.writeC(0x58);			// is_good
		s.writeC(isGood ? 1 : 0);
		s.writeC(0x60);			// overlap_buff_icon
		s.writeC(0x00);
		s.writeC(0x68); 		// main_tooltip_str_id
		s.writeC(0x00);
		s.writeC(0x70);			// buff_icon_priority
		s.writeC(0x00);
		return s;
	}
	
	public static S_InventoryIcon on(int type, int skillId, long time, boolean isGood, int invGfx){
		StrMessageId sid = _skillIdToMsgIds.get(skillId);
		if(sid == null)
			sid = _dummyId;		
		return on(3, type, skillId, TYPE_EFF_AUTO_DAY_HOUR_MIN_SEC, time, isGood, invGfx, sid.tooltipId, sid.newId, sid.endId);		
	}
	
	public static S_InventoryIcon onUnLimit(int type, int skillId, long time, boolean isGood, int invGfx){
		StrMessageId sid = _skillIdToMsgIds.get(skillId);
		if(sid == null)
			sid = _dummyId;		
		return on(3, type, skillId, TYPE_EFF_UNLIMIT, time, isGood, invGfx, sid.tooltipId, sid.newId, sid.endId);		
	}
	
	public static S_InventoryIcon on(int type, int skillId, long time, boolean isGood){
		MJCTSpell sp = MJCTSpellLoader.getInstance().get(skillId);
		int ico = 0;
		if(sp != null)
			ico = sp.xicon;
		
		StrMessageId sid = _skillIdToMsgIds.get(skillId);
		if(sid == null)
			sid = _dummyId;
		
		return on(3, type, skillId, TYPE_EFF_AUTO_DAY_HOUR_MIN_SEC, time, isGood, ico, sid.tooltipId, sid.newId, sid.endId);
	}
	
	public static S_InventoryIcon onUnLimit(int priority, int type, int skillId, long time, boolean isGood){
		MJCTSpell sp = MJCTSpellLoader.getInstance().get(skillId);
		int ico = 0;
		if(sp != null)
			ico = sp.xicon;
		
		StrMessageId sid = _skillIdToMsgIds.get(skillId);
		if(sid == null)
			sid = _dummyId;
		
		return on(priority, type, skillId, TYPE_EFF_UNLIMIT, time, isGood, ico, sid.tooltipId, sid.newId, sid.endId);
	}
	
	public static S_InventoryIcon icoNew(int skillId, long time, boolean isGood){
		return on(1, skillId, time, isGood);
	}
	
	public static S_InventoryIcon icoNew(int skillId, int tooltip, long time, boolean isGood){
		return on(3, 1, skillId, TYPE_EFF_AUTO_DAY_HOUR_MIN_SEC, time, isGood, skillId, tooltip, 0, 0);
	}
	
	public static S_InventoryIcon iconNewUnLimit(int skillId, boolean isGood){
		return onUnLimit(3, 1, skillId, TYPE_EFF_UNLIMIT, isGood);
	}
	
	public static S_InventoryIcon iconNewUnLimitAndPriority(int priority, int skillId, boolean isGood){
		return onUnLimit(priority, 1, skillId, TYPE_EFF_UNLIMIT, isGood);
	}
	
	public static S_InventoryIcon iconNewUnLimit(int skillId, int tooltip, boolean isGood){
		return on(3, 1, skillId, TYPE_EFF_UNLIMIT, 1, isGood, skillId, tooltip, 0, 0);
	}
	
	public static S_InventoryIcon iconNewUnLimitAndPriority(int priority, int skillId, int tooltip, boolean isGood){
		return on(priority, 1, skillId, TYPE_EFF_UNLIMIT, 1, isGood, skillId, tooltip, 0, 0);
	}
	
	public static S_InventoryIcon icoReset(int skillId, int tooltip, long time, boolean isGood){
		return on(3, 2, skillId, TYPE_EFF_AUTO_DAY_HOUR_MIN_SEC, time, isGood, skillId, tooltip, 0, 0);
	}
	
	public static S_InventoryIcon icoReset(int skillId, long time, boolean isGood){
		return on(2, skillId, time, isGood);
	}
	
	public static S_InventoryIcon icoEnd(int skillId){
		S_InventoryIcon s = new S_InventoryIcon();
		s.writeC(0x08);
		s.writeC(0x03);
		s.writeC(0x10);
		s.writeBit(skillId);
		s.writeC(0x30);
		s.writeC(0x00);
		s.writeC(0x50);
		s.writeC(0x00);
		s.writeH(0x00);
		return s;
	}
	
	public S_InventoryIcon(){
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeH(SHOW_INVEN_BUFFICON);
	}
	
	public byte[] getContent() {
		return getBytes();
	}
}