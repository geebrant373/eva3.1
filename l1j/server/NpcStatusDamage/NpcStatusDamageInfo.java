package l1j.server.NpcStatusDamage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import l1j.server.MJTemplate.MJSqlHelper.Executors.Selector;
import l1j.server.MJTemplate.MJSqlHelper.Handler.FullSelectorHandler;
import l1j.server.server.utils.IntRange;

public class NpcStatusDamageInfo {
	private static HashMap<Integer, ArrayList<NpcStatusDamageInfo>> m_npc_damges;
	
	public static void do_load(){
		HashMap<Integer, ArrayList<NpcStatusDamageInfo>> npc_dmgs = new HashMap<Integer, ArrayList<NpcStatusDamageInfo>>();
		Selector.exec("select * from npc_status_dmg", new FullSelectorHandler(){
			@Override
			public void result(ResultSet rs) throws Exception {
				while(rs.next()){
					NpcStatusDamageInfo o = newInstance(rs);
					ArrayList<NpcStatusDamageInfo> list = npc_dmgs.get(o.get_attack_type().to_val());
					if(list == null){
						list = new ArrayList<NpcStatusDamageInfo>();
						npc_dmgs.put(o.get_attack_type().to_val(), list);
					}
					list.add(o);
				}
			}
		});
		m_npc_damges = npc_dmgs;
	}
	
	public static NpcStatusDamageInfo find_npc_status_info(NpcStatusDamageType type, int level){
		ArrayList<NpcStatusDamageInfo> list = m_npc_damges.get(type.to_val());
		if(list == null)
			return null;
		
		for(NpcStatusDamageInfo fInfo : list){
			if(IntRange.includes(level, fInfo.get_level_min(), fInfo.get_level_max()))
				return fInfo;
		}
		return null;
	}

	private static NpcStatusDamageInfo newInstance(ResultSet rs) throws SQLException{
		return newInstance()
				.set_attack_type(NpcStatusDamageType.from_name(rs.getString("attack_type")))
				.set_level_min(rs.getInt("level_min"))
				.set_level_max(rs.getInt("level_max"))
				.set_increase_dmg(rs.getDouble("increase_dmg"));
	}

	private static NpcStatusDamageInfo newInstance(){
		return new NpcStatusDamageInfo();
	}

	private NpcStatusDamageType m_attack_type;
	private int m_level_min;
	private int m_level_max;
	private double m_increase_dmg;
	private NpcStatusDamageInfo(){}

	public NpcStatusDamageInfo set_attack_type(NpcStatusDamageType attack_type){
		m_attack_type = attack_type;
		return this;
	}
	public NpcStatusDamageInfo set_level_min(int level_min){
		m_level_min = level_min;
		return this;
	}
	public NpcStatusDamageInfo set_level_max(int level_max){
		m_level_max = level_max;
		return this;
	}
	public NpcStatusDamageInfo set_increase_dmg(double increase_dmg){
		m_increase_dmg = increase_dmg;
		return this;
	}
	public NpcStatusDamageType get_attack_type(){
		return m_attack_type;
	}
	public int get_level_min(){
		return m_level_min;
	}
	public int get_level_max(){
		return m_level_max;
	}
	public double get_increase_dmg(){
		return m_increase_dmg;
	}
}

