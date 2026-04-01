package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1AddRewardMon;
import l1j.server.server.utils.SQLUtil;

public class AddRewardMonTable {
	
	public static AddRewardMonTable _instance;
	
	public Map<Integer, L1AddRewardMon> _list = new HashMap<Integer, L1AddRewardMon>();
	
	public static AddRewardMonTable getInstance() {
		if (_instance == null) {
			_instance = new AddRewardMonTable();
		}
		return _instance;
	}
	
	public static void reload() {
		AddRewardMonTable oldInstance = _instance;
		_instance = new AddRewardMonTable();
		oldInstance._list.clear();
	}
	
	private AddRewardMonTable(){
		loadGiveMonster();
	}
	
	private void loadGiveMonster(){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM add_reward_mon");
			rs = pstm.executeQuery();
			while(rs.next()) {
				L1AddRewardMon CM = new L1AddRewardMon();
				int npcid = rs.getInt("npc_id");
				CM.setNpcName(rs.getString("npc_name"));
				CM.setGiveItem(true);
				CM.setItemId(rs.getInt("item_id"));
				CM.setItemCount(rs.getInt("item_count"));
				_list.put(npcid, CM);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			SQLUtil.close(rs, pstm, con);
		}
	}
	
	public L1AddRewardMon getAddRewardMon(int npcid){
		return _list.get(npcid);
	}
	
	public boolean isRewardMon(int npcid){
		Set<Integer> keys = _list.keySet();
		int givemon;
		boolean OK = false;
		for (Iterator<Integer> iterator = keys.iterator(); iterator.hasNext();) {
			givemon = iterator.next();
			if(givemon == npcid){
				OK = true;
				break;
			}
		}
		return OK;
	}
}