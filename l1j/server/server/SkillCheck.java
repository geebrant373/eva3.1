package l1j.server.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import l1j.server.server.model.Instance.L1PcInstance;

public class SkillCheck {
	private HashMap<Integer, List<Integer>> _SkillCheck = new HashMap<Integer, List<Integer>>();

	private static SkillCheck _instance;

	private SkillCheck() {
	}

	public static SkillCheck getInstance() {
		if (_instance == null) {
			_instance = new SkillCheck();
		}
		return _instance;
	}

	public void AddSkill(int objid, List<Integer> skillList) {
		_SkillCheck.put(objid, skillList);
	}

	public boolean AddSkill(int objid, int skillId) {
		List<Integer> skillList = _SkillCheck.get(objid);
		if (skillList == null) {
			_SkillCheck.put(objid, new ArrayList<Integer>());
			skillList = _SkillCheck.get(objid);
		}

		for (int Id : skillList) {
			if (Id == skillId) {
				return false;
			}
		}

		skillList.add(skillId);

		return true;
	}

	public List<Integer> CheckSkill(L1PcInstance pc) {
		List<Integer> skillList = _SkillCheck.get(pc.getId());
		if (skillList == null || skillList.size() <= 0) {
			return null;
		}
		return skillList;
	}

	public void addSkillCheck(int objId, int skillId) {
	    List<Integer> list = _SkillCheck.get(objId);
	    if (list == null) {
	        list = new ArrayList<>();
	        _SkillCheck.put(objId, list);
	    }

	    if (!list.contains(skillId)) {
	        list.add(skillId);
	    }
	}
	
	public boolean CheckSkill(L1PcInstance pc, int skillId) {
		List<Integer> skillList = _SkillCheck.get(pc.getId());
		if (skillList == null) {
			return false;
		}

		for (int Id : skillList) {
			if (Id == skillId) {
				return true;	
			}
		}

		return false;
	}

	public void DelSkill(int objid, int skillId) {
		List<Integer> skillList = _SkillCheck.get(objid);

		if (skillList != null) {
			skillList.remove((Integer) skillId);
		}
	}

	public void QuitDelSkill(L1PcInstance pc) {
		_SkillCheck.remove(pc.getId());
	}

	public void sendAllSkillList(L1PcInstance pc) {
		List<Integer> skillList = _SkillCheck.get(pc.getId());
		if (skillList == null || skillList.size() <= 0) {
			return;
		}

		//SC_AVAILABLE_SPELL_NOTI noti = SC_AVAILABLE_SPELL_NOTI.newInstance();
		//for (Integer spellId : skillList) {
	//		noti.appendNewSpell(spellId, true);
	//	}
	//	pc.sendPackets(noti, MJEProtoMessages.SC_AVAILABLE_SPELL_NOTI, true);
	}
}
