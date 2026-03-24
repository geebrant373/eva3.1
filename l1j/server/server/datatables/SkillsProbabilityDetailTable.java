package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import l1j.server.L1DatabaseFactory;
import l1j.server.MJTemplate.MJRnd;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.eSkillsProbabilityDetailCalcType;
import l1j.server.server.utils.SQLUtil;

public class SkillsProbabilityDetailTable {

	private static SkillsProbabilityDetailTable _instance;

	public static SkillsProbabilityDetailTable getInstance() {
		if (_instance == null) {
			_instance = new SkillsProbabilityDetailTable();
		}
		return _instance;
	}

	private ArrayList<SkillsProbabilityDetail> _list = new ArrayList<SkillsProbabilityDetail>();

	private SkillsProbabilityDetailTable() {
		loadTalbe(_list);
	}

	public void reload() {
		ArrayList<SkillsProbabilityDetail> list = new ArrayList<SkillsProbabilityDetail>();
		loadTalbe(list);
		_list = list;
	}

	private void loadTalbe(ArrayList<SkillsProbabilityDetail> list) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		SkillsProbabilityDetail sprobDetail = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from skills_probability_detail");

			rs = pstm.executeQuery();

			while (rs.next()) {
				sprobDetail = new SkillsProbabilityDetail();

				sprobDetail.setSkill_id(rs.getInt("skill_id"));
				sprobDetail.setSkill_name(rs.getString("skill_name"));
				sprobDetail.setCrown_basic_probability(rs.getInt("crown_basic_probability"));
				sprobDetail.setKnight_basic_probability(rs.getInt("knight_basic_probability"));
				sprobDetail.setElf_basic_probability(rs.getInt("elf_basic_probability"));
				sprobDetail.setWizard_basic_probability(rs.getInt("wizard_basic_probability"));
				sprobDetail.setDark_elf_basic_probability(rs.getInt("dark_elf_basic_probability"));
				
				sprobDetail.setDefence_type(eSkillsProbabilityDetailCalcType.fromString(rs.getString("defence_resist")));
				sprobDetail.setDefence_resist_variation(rs.getInt("defence_resist_variation"));
				sprobDetail.setOffence_type(eSkillsProbabilityDetailCalcType.fromString(rs.getString("offence_hit")));
				sprobDetail.setOffence_hit_variation(rs.getInt("offence_hit_variation"));
				
				sprobDetail.setLevel_by_variation(rs.getInt("level_by_variation"));
				sprobDetail.setInt_by_variation(rs.getInt("int_by_variation"));
				sprobDetail.setTarget_mr_decrease(rs.getInt("target_mr_decrease"));
				
				list.add(sprobDetail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
	}
	
	public SkillsProbabilityDetail getSkillProb(int skillid) {
		for(SkillsProbabilityDetail ss : _list) {
			if(ss.getSkill_id() == skillid)
				return ss;
		}
		return null;
	}

	public class SkillsProbabilityDetail {
		private int skill_id;
		private String skill_name;
		private int int_by_variation;
		private int target_mr_decrease;
		private int level_by_variation;
		private eSkillsProbabilityDetailCalcType defence_type;
		private int defence_resist_variation;
		private eSkillsProbabilityDetailCalcType offence_type;
		private int offence_hit_variation;
		private int crown_basic_probability;
		private int knight_basic_probability;
		private int elf_basic_probability;
		private int wizard_basic_probability;
		private int dark_elf_basic_probability;

		public int getSkill_id() {
			return skill_id;
		}

		public String getSkill_name() {
			return skill_name;
		}

		public int getInt_by_variation() {
			return int_by_variation;
		}

		public int getTarget_mr_decrease() {
			return target_mr_decrease;
		}

		public int getLevel_by_variation() {
			return level_by_variation;
		}

		public eSkillsProbabilityDetailCalcType getDefence_type() {
			return defence_type;
		}

		public int getDefence_resist_variation() {
			return defence_resist_variation;
		}

		public eSkillsProbabilityDetailCalcType getOffence_type() {
			return offence_type;
		}

		public int getOffence_hit_variation() {
			return offence_hit_variation;
		}

		public int getCrown_basic_probability() {
			return crown_basic_probability;
		}

		public int getKnight_basic_probability() {
			return knight_basic_probability;
		}

		public int getElf_basic_probability() {
			return elf_basic_probability;
		}

		public int getWizard_basic_probability() {
			return wizard_basic_probability;
		}

		public int getDark_elf_basic_probability() {
			return dark_elf_basic_probability;
		}

		public void setSkill_id(int skill_id) {
			this.skill_id = skill_id;
		}

		public void setSkill_name(String skill_name) {
			this.skill_name = skill_name;
		}

		public void setInt_by_variation(int int_by_variation) {
			this.int_by_variation = int_by_variation;
		}

		public void setTarget_mr_decrease(int target_mr_decrease) {
			this.target_mr_decrease = target_mr_decrease;
		}

		public void setLevel_by_variation(int level_by_variation) {
			this.level_by_variation = level_by_variation;
		}

		public void setDefence_type(eSkillsProbabilityDetailCalcType defence_type) {
			this.defence_type = defence_type;
		}

		public void setDefence_resist_variation(int defence_resist_variation) {
			this.defence_resist_variation = defence_resist_variation;
		}

		public void setOffence_type(eSkillsProbabilityDetailCalcType offence_type) {
			this.offence_type = offence_type;
		}

		public void setOffence_hit_variation(int offence_hit_variation) {
			this.offence_hit_variation = offence_hit_variation;
		}

		public void setCrown_basic_probability(int crown_basic_probability) {
			this.crown_basic_probability = crown_basic_probability;
		}

		public void setKnight_basic_probability(int knight_basic_probability) {
			this.knight_basic_probability = knight_basic_probability;
		}

		public void setElf_basic_probability(int elf_basic_probability) {
			this.elf_basic_probability = elf_basic_probability;
		}

		public void setWizard_basic_probability(int wizard_basic_probability) {
			this.wizard_basic_probability = wizard_basic_probability;
		}

		public void setDark_elf_basic_probability(int dark_elf_basic_probability) {
			this.dark_elf_basic_probability = dark_elf_basic_probability;
		}

		public boolean skillProbResult(L1Character attacker, L1Character target) {
			int chance = MJRnd.next(1, 1000000);
			int customProb = 250000;

			if (attacker instanceof L1PcInstance) {
				L1PcInstance owner = (L1PcInstance) attacker;
				if (owner.isCrown())
					customProb = this.crown_basic_probability;
				if (owner.isKnight())
					customProb = this.knight_basic_probability;
				if (owner.isElf())
					customProb = this.elf_basic_probability;
				if (owner.isWizard())
					customProb = this.wizard_basic_probability;
				if (owner.isDarkelf())
					customProb = this.dark_elf_basic_probability;
				
				if(target instanceof L1PcInstance) {
					L1PcInstance tr = (L1PcInstance) target;
					customProb += CharacterBalance.getInstance().getMagicHit(owner.getType(), tr.getType());
				} else if(target instanceof L1MonsterInstance) {
					customProb += CharacterBalance.getInstance().getMagicHit(owner.getType(), 10);
				}
			} else if (attacker instanceof L1MonsterInstance) {
				if(target instanceof L1PcInstance) {
					L1PcInstance tr = (L1PcInstance) target;
					customProb += CharacterBalance.getInstance().getMagicHit(10, tr.getType());
				} else if(target instanceof L1MonsterInstance) {
					customProb += CharacterBalance.getInstance().getMagicHit(10, 10);
				}
			}
			
			if (this.offence_hit_variation != 0) {
				switch (this.offence_type) {
				case FREEZE:
					customProb += attacker.getHitup_spirit() * this.offence_hit_variation;
					break;
				case HOLD:
					customProb += attacker.getHitup_skill() * this.offence_hit_variation;
					break;
				case NONE:
					break;
				case SLEEP:
					customProb += attacker.getHitup_spirit() * this.offence_hit_variation;
					break;
				case STONE:
					customProb += attacker.getHitup_spirit() * this.offence_hit_variation;
					break;
				case STUN:
					customProb += attacker.getHitup_skill() * this.offence_hit_variation;
					break;
				default:
					break;
				}
			}

			if (this.level_by_variation != 0) {
				int carryLevel = attacker.getLevel() - target.getLevel();
				customProb += (this.level_by_variation * carryLevel);
			}

			if (this.int_by_variation != 0) {
				int carrtIntel = attacker.getAbility().getTotalInt();
				customProb += (this.int_by_variation * carrtIntel);
			}

			if (this.target_mr_decrease != 0) {
				int targetMr = target.getResistance().getMr();
				customProb -= (this.target_mr_decrease * targetMr);
			}

			if (this.defence_resist_variation != 0) {
				switch (this.defence_type) {
				case FREEZE:
					customProb -= target.getResistance().getFreeze() * this.defence_resist_variation;
					break;
				case HOLD:
					customProb -= target.getResistance().getHold() * this.defence_resist_variation;
					break;
				case NONE:
					break;
				case SLEEP:
					customProb -= target.getResistance().getSleep() * this.defence_resist_variation;
					break;
				case STONE:
					customProb -= target.getResistance().getPetrifaction() * this.defence_resist_variation;
					break;
				case STUN:
					customProb -= target.getResistance().getStun() * this.defence_resist_variation;
					break;
				default:
					break;
				}
			}

			if(customProb <= 0) {
				customProb = 5000;
			}
			
			if(customProb > 1000000) {
				customProb = 980000;
			}
			
			boolean result = chance < customProb;
			
			if (attacker instanceof L1PcInstance) {
				L1PcInstance gm = (L1PcInstance) attacker;
				if (gm.isGm()) {
					gm.sendPackets(String.format("%s [%s -> %s] 성공확률 %d < 계산된확률 %d (결과:%s)", this.skill_name, attacker.getName(), target.getName(), chance,
							customProb, (result ? "성공" : "실패")));
				}
			}
			
			if (target instanceof L1PcInstance) {
				L1PcInstance gm = (L1PcInstance) target;
				if (gm.isGm()) {
					gm.sendPackets(String.format("%s [%s -> %s] 성공확률 %d < 계산된확률 %d (결과:%s)", this.skill_name, attacker.getName(), target.getName(), chance,
							customProb, (result ? "성공" : "실패")));
				}
			}

			return result;
		}

	}
}
