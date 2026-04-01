package l1j.server.AutoHuntSystem;

import static l1j.server.server.model.skill.L1SkillId.DETECTION;
import static l1j.server.server.model.skill.L1SkillId.ENCHANT_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.EXTRA_HEAL;
import static l1j.server.server.model.skill.L1SkillId.GREATER_HASTE;
import static l1j.server.server.model.skill.L1SkillId.HASTE;
import static l1j.server.server.model.skill.L1SkillId.HEAL;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;

import l1j.server.server.SkillCheck;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Skills;

public class AutoHuntSkillUse {
	private L1PcInstance owner = null;
	private static final int MAGIC_PENALTY = 1000;
	private int trycount = 0;
	
	public AutoHuntSkillUse(L1PcInstance pc) {
		owner = pc;
	}
	
	public void toUseSkills() {
		int[] skillIds = null;
		if (owner.isCrown()) {
			skillIds = new int[] { L1SkillId.BRAVE_AURA, L1SkillId.SHINING_AURA};
		} else if (owner.isKnight()) {
			skillIds = new int[] { L1SkillId.REDUCTION_ARMOR, L1SkillId.BOUNCE_ATTACK, L1SkillId.COUNTER_BARRIER };
		} else if (owner.isElf()) {
			if (owner.getWeapon() == null) {
				return;
			}
			if (owner.getWeapon().getItem().getType1() == 20) {// 활
				if (owner.getElfAttr() == 4) {
					skillIds = new int[] { L1SkillId.PHYSICAL_ENCHANT_STR,L1SkillId.PHYSICAL_ENCHANT_DEX, L1SkillId.CLEAR_MIND };
				} else if (owner.getElfAttr() == 8) {
					skillIds = new int[] { L1SkillId.PHYSICAL_ENCHANT_STR, L1SkillId.PHYSICAL_ENCHANT_DEX, L1SkillId.CLEAR_MIND, L1SkillId.STORM_SHOT };
				} else if (owner.getElfAttr() == 1) {
					skillIds = new int[] { L1SkillId.PHYSICAL_ENCHANT_STR, L1SkillId.PHYSICAL_ENCHANT_DEX, L1SkillId.CLEAR_MIND, L1SkillId.IRON_SKIN };
				} else {
					skillIds = new int[] { L1SkillId.PHYSICAL_ENCHANT_STR, L1SkillId.PHYSICAL_ENCHANT_DEX, L1SkillId.CLEAR_MIND };
				}
			} else {
				if (owner.getElfAttr() == 1) {
					skillIds = new int[] { L1SkillId.PHYSICAL_ENCHANT_STR, L1SkillId.PHYSICAL_ENCHANT_DEX,
							L1SkillId.SHIELD, L1SkillId.CLEAR_MIND,
							L1SkillId.IRON_SKIN};
				} else if (owner.getElfAttr() == 2) {
					skillIds = new int[] { L1SkillId.PHYSICAL_ENCHANT_STR, L1SkillId.PHYSICAL_ENCHANT_DEX,
							L1SkillId.SHIELD, L1SkillId.CLEAR_MIND,
							L1SkillId.BURNING_WEAPON, L1SkillId.ELEMENTAL_FIRE, L1SkillId.ADDITIONAL_FIRE,
							L1SkillId.SOUL_OF_FLAME};
				} else {
					skillIds = new int[] { L1SkillId.PHYSICAL_ENCHANT_STR, L1SkillId.PHYSICAL_ENCHANT_DEX, L1SkillId.CLEAR_MIND };
				}
			}
		} else if (owner.isDarkelf()) {
			skillIds = new int[] { L1SkillId.DRESS_MIGHTY, L1SkillId.SHADOW_ARMOR, L1SkillId.ENCHANT_VENOM, L1SkillId.BURNING_SPIRIT,
					L1SkillId.MOVING_ACCELERATION, L1SkillId.DRESS_DEXTERITY, L1SkillId.DRESS_EVASION, L1SkillId.VENOM_RESIST,
					L1SkillId.DOUBLE_BRAKE, L1SkillId.UNCANNY_DODGE };
		} else if (owner.isWizard()) {
			skillIds = new int[] { L1SkillId.HOLY_WALK, L1SkillId.PHYSICAL_ENCHANT_STR, L1SkillId.PHYSICAL_ENCHANT_DEX, L1SkillId.ADVANCE_SPIRIT };
		}
		L1SkillUse _skilluse;
		L1Skills _skill;
		for(int i = 0; i < skillIds.length; i++) {
			try {	
				long current = System.currentTimeMillis();
				if(current - owner.getAutoSkillDelay() < 0) {
					break;
				}
				if(!SkillCheck.getInstance().CheckSkill(owner, skillIds[i])) {
					continue;
				}
				
				if (owner.get_자동마나퍼센트() > owner.getCurrentMpPercent()) {
					continue;
				}
				
				_skill = SkillsTable.getInstance().getTemplate(skillIds[i]);
				boolean needShadowFang = owner.isDarkelf() && !owner._isShowFang;
				
				for (L1ItemInstance item : owner.getInventory().getItems()) {
					if (item == null) continue;
					/*if(SkillCheck.getInstance().CheckSkill(owner, L1SkillId.BLESS_WEAPON)) {
						if (!item._isRunningWeapon && owner.getWeapon() != null && owner.getWeapon().equals(item)) {
							owner.sendPackets(new S_ServerMessage(
									161,
									String.valueOf(item.getLogName()).trim(),
									"$245", "$247"));
	
							item.setSkillWeaponEnchant(
									owner,
									L1SkillId.BLESS_WEAPON,
									_skill.getBuffDuration() * 1000);
							owner.sendPackets(new S_SkillSound(owner.getId(), 2176));
							owner.broadcastPacket(new S_SkillSound(owner.getId(), 2176));
							return;
						}
					}*/
					if(SkillCheck.getInstance().CheckSkill(owner, L1SkillId.BLESSED_ARMOR)) {
						if (!item._isRunningArmor
								&& item.getItem().getType2() == 2
								&& item.getItem().getType() == 2) {
	
							owner.sendPackets(new S_ServerMessage(
									161,
									String.valueOf(item.getLogName()).trim(),
									"$245", "$247"));
							item.setSkillArmorEnchant(
									owner,
									L1SkillId.BLESSED_ARMOR,
									_skill.getBuffDuration() * 1000);
	
							owner.sendPackets(new S_SkillSound(owner.getId(), 748));
							owner.broadcastPacket(new S_SkillSound(owner.getId(), 748));
							return;
						}
					}
					if(SkillCheck.getInstance().CheckSkill(owner, L1SkillId.SHADOW_FANG)) {
						if (needShadowFang && item.getItem().getType2() == 1) {
							item.setSkillWeaponEnchant(
									owner,
									L1SkillId.SHADOW_FANG,
									_skill.getBuffDuration() * 1000);
							owner.sendPackets(new S_SkillSound(owner.getId(), 8955));
							owner._isShowFang = true;
							needShadowFang = false;
						}
					}
				}
				if(owner.getSkillEffectTimerSet().hasSkillEffect(skillIds[i])) {
					continue;				
				}
				
				if(!isHPMPConsume(_skill, skillIds[i])) {
					return;
				} else if(!isItemConsume(_skill)){
					return;
				}
				if(skillIds[i] == L1SkillId.BLESS_WEAPON) {
					if(owner.getWeapon() == null) continue;
				}
				if(skillIds[i] == L1SkillId.COUNTER_BARRIER) {
					if (owner.getWeapon() == null || owner.getWeapon().getItem().getType() != 3) {
						continue;
					}
				}

				_skilluse = new L1SkillUse();
				_skilluse.handleCommands(owner, skillIds[i], owner.getId(), owner.getX(), owner.getY(), null, 0, L1SkillUse.TYPE_NORMAL);				
				owner.setAutoSkillDelay(System.currentTimeMillis() + _skill.getReuseDelay() + MAGIC_PENALTY);
			}catch(Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void toUseHealingMagic() {
		if (!owner.isWizard())
			return;
		int _hp = (int) (((double) owner.getCurrentHp() / (double) owner.getMaxHp()) * 100);
		L1SkillUse _skilluse;
		L1Skills _skill;
		if (_hp <= 50) {
			if (owner.get_자동마나퍼센트() > owner.getCurrentMpPercent()) {
				return;
			}
			if (!SkillCheck.getInstance().CheckSkill(owner, L1SkillId.IMMUNE_TO_HARM))
				return;
			if (owner.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IMMUNE_TO_HARM)) {
				return;
			}
			long current = System.currentTimeMillis();
			if (current - owner.getAutoSkillDelay() < 0)
				return;
			_skill = SkillsTable.getInstance().getTemplate(L1SkillId.IMMUNE_TO_HARM);
			_skilluse = new L1SkillUse();
			_skilluse.handleCommands(owner, L1SkillId.IMMUNE_TO_HARM, owner.getId(), owner.getX(), owner.getY(), null, 0,
					L1SkillUse.TYPE_NORMAL);
			owner.setAutoSkillDelay(System.currentTimeMillis() + _skill.getReuseDelay() + MAGIC_PENALTY);
		}
	}
	
	public void toTripleArrow(L1Character target) {			
		if(!SkillCheck.getInstance().CheckSkill(owner, L1SkillId.TRIPLE_ARROW)) 
			return;
		if (owner.get_자동마나퍼센트() > owner.getCurrentMpPercent()) {
			return;
		}
		L1Skills _skill = SkillsTable.getInstance().getTemplate(L1SkillId.TRIPLE_ARROW);
		long current = System.currentTimeMillis();
		if(current - owner.getAutoSkillDelay() < 0) 
			return;
		
		if(!isHPMPConsume(_skill, L1SkillId.TRIPLE_ARROW)) {
			return;
		}
		L1SkillUse _skilluse = new L1SkillUse();
		_skilluse.handleCommands(owner, L1SkillId.TRIPLE_ARROW, target.getId(), target.getX(), target.getY(), null, 0, L1SkillUse.TYPE_NORMAL);
		owner.setAutoSkillDelay(System.currentTimeMillis() + _skill.getReuseDelay() + MAGIC_PENALTY);
	}
	
	public void toBloodSoul(L1PcInstance pc) {
		L1Skills _skill = SkillsTable.getInstance().getTemplate(L1SkillId.BLOODY_SOUL);
		long current = System.currentTimeMillis();
		if (!pc.getAutosoul()) {
			return;
		}
		if (current - pc.getAutoSkillDelay() < 0) {
			return;
		}
		
		if (!isHPMPConsume(_skill, L1SkillId.BLOODY_SOUL)) {
			return;
		}
		
		if (pc.getCurrentHpPercent() < 50) {
			return;
		}
		
		if (pc.getCurrentMpPercent() > 90) {
			return;
		}
		L1SkillUse _skilluse = new L1SkillUse();
		_skilluse.handleCommands(pc, L1SkillId.BLOODY_SOUL, pc.getId(), pc.getX(), pc.getY(), null, 0,
				L1SkillUse.TYPE_NORMAL);
		pc.setAutoSkillDelay(System.currentTimeMillis() + _skill.getReuseDelay() + MAGIC_PENALTY);
	}
	
	public void toTurnUndead(L1PcInstance pc) {
		try {
			L1SkillUse _skilluse;
			L1Character target = pc.getAutoTarget();
			AutoHuntItemUse itemuse = new AutoHuntItemUse(owner);
			
			if(!SkillCheck.getInstance().CheckSkill(owner, L1SkillId.TURN_UNDEAD)) {
				return;
			}
			if (target == null || trycount == 2) {
				pc.getAutoTargetList().clear();
				pc.setAutoStatus(0);
				trycount = 0;
				itemuse.toUseScroll(40100);
				return;
			}
			if (!pc.glanceCheck(target.getX(), target.getY())) {
				return;
			}
			if (pc.getCurrentMpPercent() < pc.get_자동마나퍼센트()) {
				return;
			}
			if (!isundead(target)) {
				pc.getAutoTargetList().clear();
				pc.setAutoStatus(0);
				trycount = 0;
				itemuse.toUseScroll(40100);
				return;
			}
			pc.delInvis();
			_skilluse = new L1SkillUse();
			_skilluse.handleCommands(pc, L1SkillId.TURN_UNDEAD, target.getId(), target.getX(), target.getY(), null, 0, L1SkillUse.TYPE_NORMAL);
			trycount++;
		} catch (Exception e) {
		}
	}
	
	public void toSunBurst(L1PcInstance pc, L1Character target) {
		if (!SkillCheck.getInstance().CheckSkill(pc, L1SkillId.SUNBURST)) {
			return;
		}
			
		L1Skills _skill = SkillsTable.getInstance().getTemplate(L1SkillId.SUNBURST);
		long current = System.currentTimeMillis();
		if (current - pc.getAutoSkillDelay() < 0) {
			return;
		}
		if (owner.get_자동마나퍼센트() > owner.getCurrentMpPercent()) {
			return;
		}
		L1SkillUse _skilluse = new L1SkillUse();
		_skilluse.handleCommands(pc, L1SkillId.SUNBURST, target.getId(), target.getX(), target.getY(), null, 0,
				L1SkillUse.TYPE_NORMAL);
		pc.setAutoSkillDelay(System.currentTimeMillis() + _skill.getReuseDelay() + MAGIC_PENALTY);
	}
	
	public void toIceSpike(L1PcInstance pc, L1Character target) {
		if (!SkillCheck.getInstance().CheckSkill(pc, L1SkillId.FREEZING_BLIZZARD)) {
			return;
		}
			
		L1Skills _skill = SkillsTable.getInstance().getTemplate(L1SkillId.FREEZING_BLIZZARD);
		long current = System.currentTimeMillis();
		if (current - pc.getAutoSkillDelay() < 0) {
			return;
		}
		
		if (pc.getCurrentMpPercent() < pc.get_자동마나퍼센트()) {
			return;
		}
		L1SkillUse _skilluse = new L1SkillUse();
		_skilluse.handleCommands(pc, L1SkillId.FREEZING_BLIZZARD, target.getId(), target.getX(), target.getY(), null, 0,
				L1SkillUse.TYPE_NORMAL);
		pc.setAutoSkillDelay(System.currentTimeMillis() + _skill.getReuseDelay() + MAGIC_PENALTY);
	}
	
	public void autoAttackSkill(L1PcInstance pc, L1Character target, int skillid) {
		if (!SkillCheck.getInstance().CheckSkill(pc, skillid)) {
			return;
		}
			
		L1Skills _skill = SkillsTable.getInstance().getTemplate(skillid);
		long current = System.currentTimeMillis();
		if (current - pc.getAutoSkillDelay() < 0) {
			return;
		}
		
		if (pc.getCurrentMpPercent() < pc.get_자동마나퍼센트()) {
			return;
		}
		L1SkillUse _skilluse = new L1SkillUse();
		_skilluse.handleCommands(pc, skillid, target.getId(), target.getX(), target.getY(), null, 0, L1SkillUse.TYPE_NORMAL);
		pc.setAutoSkillDelay(System.currentTimeMillis() + _skill.getReuseDelay() + MAGIC_PENALTY);
		if (skillid == L1SkillId.CHILL_TOUCH) {
			target.getSkillEffectTimerSet().setSkillEffect(L1SkillId.CHILL_TOUCH, -1);
		}
	}
	
	public void toMeteorStrike(L1PcInstance pc, L1Character target) {
		if (!SkillCheck.getInstance().CheckSkill(pc, L1SkillId.METEOR_STRIKE)) {
			return;
		}
			
		L1Skills _skill = SkillsTable.getInstance().getTemplate(L1SkillId.METEOR_STRIKE);
		long current = System.currentTimeMillis();
		if (current - pc.getAutoSkillDelay() < 0) {
			return;
		}
		
		if (owner.get_자동마나퍼센트() > owner.getCurrentMpPercent()) {
			return;
		}
		L1SkillUse _skilluse = new L1SkillUse();
		_skilluse.handleCommands(pc, L1SkillId.METEOR_STRIKE, target.getId(), target.getX(), target.getY(), null, 0,
				L1SkillUse.TYPE_NORMAL);
		pc.setAutoSkillDelay(System.currentTimeMillis() + _skill.getReuseDelay() + MAGIC_PENALTY);
	}
	
	private boolean isItemConsume(L1Skills _skill) {
		int itemConsume = _skill.getItemConsumeId();
		int itemConsumeCount = _skill.getItemConsumeCount();
		if (itemConsume == 0) {
			return true;
		}
		if (itemConsume == 40318) { 
//			if (owner.getInventory().checkItem(30079, itemConsumeCount)
//					&& owner.getLevel() < 56) {
//				return true;
//			}
		} else if (itemConsume == 40321) { 
//			if (owner.getInventory().checkItem(30080, itemConsumeCount)
//					&& owner.getLevel() < 56) {
//				return true;
//			}
		} else if (itemConsume == 210035) { 
//			if (owner.getInventory().checkItem(30081, itemConsumeCount)
//					&& owner.getLevel() < 56) {
//				return true;
//			}
		} else if (itemConsume == 210038) { 
//			if (owner.getInventory().checkItem(30082, itemConsumeCount)
//					&& owner.getLevel() < 56) {
//				return true;
//			}
		} else if (itemConsume == 40319) { 
//			if (owner.getInventory().checkItem(30078, itemConsumeCount)
//					&& owner.getLevel() < 56) {
//				return true;
//			}
		}
		if (!owner.getInventory().checkItem(itemConsume, itemConsumeCount)) {
			return false;
		}
		return true;
	}
	
	private boolean isHPMPConsume(L1Skills _skill, int _skillId) {		
		int _hpConsume;
		int _mpConsume;
		_mpConsume = _skill.getMpConsume();
		_hpConsume = _skill.getHpConsume();
		int currentMp = 0;
		int currentHp = 0;

		currentMp = owner.getCurrentMp();
		currentHp = owner.getCurrentHp();

		if (owner.getAbility().getTotalInt() > 12
				&& _skillId > L1SkillId.HOLY_WEAPON && _skillId <= L1SkillId.FREEZING_BLIZZARD) {
			_mpConsume--;
		}
		if (owner.getAbility().getTotalInt() > 13 && _skillId > L1SkillId.STALAC
				&& _skillId <= L1SkillId.FREEZING_BLIZZARD) {
			_mpConsume--;
		}
		if (owner.getAbility().getTotalInt() > 14
				&& _skillId > L1SkillId.WEAK_ELEMENTAL
				&& _skillId <= L1SkillId.FREEZING_BLIZZARD) {
			_mpConsume--;
		}
		if (owner.getAbility().getTotalInt() > 15
				&& _skillId > L1SkillId.MEDITATION && _skillId <= L1SkillId.FREEZING_BLIZZARD) {
			_mpConsume--;
		}
		if (owner.getAbility().getTotalInt() > 16 && _skillId > L1SkillId.DARKNESS
				&& _skillId <= L1SkillId.FREEZING_BLIZZARD) {
			_mpConsume--;
		}
		if (owner.getAbility().getTotalInt() > 17
				&& _skillId > L1SkillId.BLESS_WEAPON && _skillId <= L1SkillId.FREEZING_BLIZZARD) {
			_mpConsume--;
		}

		if (owner.getAbility().getTotalInt() > 12
				&& _skillId >= L1SkillId.SHOCK_STUN && _skillId <= L1SkillId.COUNTER_BARRIER) {
			_mpConsume -= (owner.getAbility().getTotalInt() - 12);
		}
		
		if (owner.isCrown()) {
			if (owner.getAbility().getBaseInt() >= 11) {
				_mpConsume--;
			}
			if (owner.getAbility().getBaseInt() >= 13) {
				_mpConsume--;
			}
		} else if (owner.isKnight()) {
			if (owner.getAbility().getBaseInt() >= 9) {
				_mpConsume--;
			}
			if (owner.getAbility().getBaseInt() >= 11) {
				_mpConsume--;
			}
		} else if (owner.isDarkelf()) {
			if (owner.getAbility().getBaseInt() >= 13) {
				_mpConsume--;
			}
			if (owner.getAbility().getBaseInt() >= 15) {
				_mpConsume--;
			}
		}

		if (_skillId == PHYSICAL_ENCHANT_DEX
				&& owner.getInventory().checkEquipped(20013)) {
			_mpConsume /= 2;
		}
		if (_skillId == HASTE
				&& owner.getInventory().checkEquipped(20013)) {
			_mpConsume /= 2;
		}
		if (_skillId == HEAL && owner.getInventory().checkEquipped(20014)) {
			_mpConsume /= 2;
		}
		if (_skillId == EXTRA_HEAL
				&& owner.getInventory().checkEquipped(20014)) {
			_mpConsume /= 2;
		}
		if (_skillId == ENCHANT_WEAPON
				&& owner.getInventory().checkEquipped(20015)) {
			_mpConsume /= 2;
		}
		if (_skillId == DETECTION
				&& owner.getInventory().checkEquipped(20015)) {
			_mpConsume /= 2;
		}
		if (_skillId == PHYSICAL_ENCHANT_STR
				&& owner.getInventory().checkEquipped(20015)) {
			_mpConsume /= 2;
		}
		if (_skillId == HASTE
				&& owner.getInventory().checkEquipped(20008)) {
			_mpConsume /= 2;
		}
		if (_skillId == GREATER_HASTE
				&& owner.getInventory().checkEquipped(20023)) {
			_mpConsume /= 2;
		}

		if (0 < _skill.getMpConsume()) {
			_mpConsume = Math.max(_mpConsume, 1);
		}

		if (currentHp < _hpConsume + 1) {
			return false;
		} else if (currentMp < _mpConsume) {
			return false;
		}
		return true;
	}
	
	private boolean isundead(L1Character mob) {
		L1NpcInstance mon = (L1NpcInstance) mob;
		int und = mon.getNpcTemplate().get_undead();
		return und == 1 ? true : false;
	}
}
