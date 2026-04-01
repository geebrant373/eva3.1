package l1j.server.NpcStatusDamage;

public enum NpcStatusDamageType {
	SHORT_DMG(0, "ShortDmg(Str)"),
	SHORT_HIT(1, "ShortHit(Str)"),
	LONG_DMG(2, "LongDmg(Dex)"),
	LONG_HIT(3, "LongHit(Dex)"),
	MAGIC_DMG(4, "MagicDmg(Int)"),
	MAGIC_HIT(5, "MagicHit(Int)");
	int m_val;
	String m_name;
	NpcStatusDamageType(int val, String name){
		m_val = val;
		m_name = name;
	}
	
	public int to_val(){
		return m_val;
	}
	public String to_name(){
		return m_name;
	}
	public static NpcStatusDamageType from_name(String name){
		for(NpcStatusDamageType f_type : values()){
			if(f_type.to_name().equals(name))
				return f_type;
		}
		return null;
	}
	public static NpcStatusDamageType from_int(int val){
		for(NpcStatusDamageType f_type : values()){
			if(f_type.to_val() == val)
				return f_type;
		}
		return null;
	}
}
