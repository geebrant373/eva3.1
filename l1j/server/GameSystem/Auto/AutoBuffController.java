package l1j.server.GameSystem.Auto;

import static l1j.server.server.model.skill.L1SkillId.CURSE_PARALYZE;
import static l1j.server.server.model.skill.L1SkillId.CURSE_PARALYZE2;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;
import static l1j.server.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static l1j.server.server.model.skill.L1SkillId.MOB_BASILL;
import static l1j.server.server.model.skill.L1SkillId.MOB_COCA;
import static l1j.server.server.model.skill.L1SkillId.MOB_RANGESTUN_18;
import static l1j.server.server.model.skill.L1SkillId.MOB_RANGESTUN_19;
import static l1j.server.server.model.skill.L1SkillId.MOB_SHOCKSTUN_30;
import static l1j.server.server.model.skill.L1SkillId.SHOCK_STUN;
import static l1j.server.server.model.skill.L1SkillId.STATUS_POISON_PARALYZED;

import java.util.ArrayList;
import java.util.Collection;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillDelay;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Skills;

public class AutoBuffController implements Runnable {

	private static AutoBuffController _instance;

	public static AutoBuffController getInstance() {
		if (_instance == null) {
			_instance = new AutoBuffController();
		}
		return _instance;
	}

	public AutoBuffController() {
		GeneralThreadPool.getInstance().execute(this);
	}

	private Collection<L1PcInstance> list = null;

	@Override
	public void run() {
		while (true) {
			try {
				list = L1World.getInstance().getAllPlayers();
				for (L1PcInstance pc : list) {
					if (pc == null) {
						continue;
					} 
					try {
						doAutoBuffAction(pc);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					Thread.sleep(300);
				} catch (Exception e) {
				}
			}
		}
	}

	private void doAutoBuffAction(L1PcInstance pc) {
		if (!pc.is_자동버프사용()) {
			return;
		}
		if (!pc.is_자동버프세이프티존사용()) {
			return;
		}

		if (pc.isTeleport() || pc.isDead()) {
			return;
		}
		if (pc.isFishing()) {
			return;
		}
		if (pc.isPrivateShop() || pc.isAutoClanjoin()) {
			return;
		}
		if (pc.isPinkName() && !pc.is_자동버프전투시사용()) {
			return;
		}

		if (!pc.getMap().isUsableSkill()) {
			return;
		}

		if (pc.getCurrentMp() == 0) {
			return;
		}
		
		if (pc.getSkillEffectTimerSet().hasSkillEffect(SHOCK_STUN)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(MOB_SHOCKSTUN_30)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(MOB_RANGESTUN_19)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(MOB_RANGESTUN_18)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(MOB_COCA)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(MOB_BASILL)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(FOG_OF_SLEEPING)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(CURSE_PARALYZE)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(CURSE_PARALYZE2)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_POISON_PARALYZED)
				) {
			return;
		}

		ArrayList<Integer> _버프리스트 = new ArrayList<Integer>();

		_버프리스트 = pc.get_자동버프리스트();
		if (_버프리스트 == null || _버프리스트.isEmpty()) {
			return;
		}
		for (int skillId : _버프리스트) {
			if (!SkillsTable.getInstance().spellCheck(pc.getId(), skillId)) {
				continue;
			}

			if (pc.getType() == 0) { //군주				
				switch(skillId) {		
				case L1SkillId.LIGHT: 		
				case L1SkillId.SHIELD: 		
				case L1SkillId.HOLY_WEAPON: 		
				case L1SkillId.ENCHANT_WEAPON: 		
				case L1SkillId.DECREASE_WEIGHT: 		
				case L1SkillId.GLOWING_AURA: 		
				case L1SkillId.SHINING_AURA: 		
				case L1SkillId.BRAVE_AURA: 		
					L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
					if (skillId == L1SkillId.HOLY_WEAPON) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ENCHANT_WEAPON)
								|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESS_WEAPON)) {
							continue;
						}
					} else if (skillId == L1SkillId.ENCHANT_WEAPON) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESS_WEAPON)) {
							continue;
						}
					} else if (skillId == L1SkillId.SHIELD) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IRON_SKIN)
								|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHINING_AURA)) {
							continue;
						}
					} else if (skillId == L1SkillId.SHINING_AURA) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IRON_SKIN)) {
							continue;
						}
					} else if (skillId == L1SkillId.HASTE) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HASTE)
								|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GREATER_HASTE)) {
							continue;
						}
					}
					if (!pc.getSkillEffectTimerSet().hasSkillEffect(skillId)) {
						if (skill.getMpConsume() <= pc.getCurrentMp()) {
							if (skillId == L1SkillId.HOLY_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && pc.getWeapon().equals(item)) {
										pc.sendPackets(
												new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.HOLY_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.ENCHANT_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && pc.getWeapon().equals(item)) {
										pc.sendPackets(
												new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.ENCHANT_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.BLESS_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && item.getItem().getType2() == 1) {
										pc.sendPackets(new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.BLESS_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.BLESSED_ARMOR) {
								if (pc != null && !pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESSED_ARMOR)) {
									pc.getAC().addAc(-3);
									pc.sendPackets(new S_OwnCharAttrDef(pc));
									pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.BLESSED_ARMOR, 20*60*1000);
								}
							}
						new L1SkillUse().handleCommands(pc, skillId, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_NORMAL);
						L1SkillDelay.onSkillUse(pc, skill.getReuseDelay());
						}
					}
					break;	
				}		
			} else if (pc.getType() == 1) {	//기사	
				switch(skillId) {		
				case L1SkillId.LIGHT: 		
				case L1SkillId.SHIELD: 		
				case L1SkillId.HOLY_WEAPON: 		
				case L1SkillId.REDUCTION_ARMOR: 		
				case L1SkillId.BOUNCE_ATTACK: 		
				case L1SkillId.SOLID_CARRIAGE: 		
				case L1SkillId.COUNTER_BARRIER: 		
					L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
					if (pc.isSkillDelay()) {
						continue;
					}
					if (skillId == L1SkillId.COUNTER_BARRIER) {// 댄싱블레이즈 검착용 체
						L1ItemInstance weapon = pc.getWeapon();
						if (weapon == null) {
							continue;
						}
						if (weapon.getItem().getType1() != 50) {
							continue;
						}
					}
					if (skillId == L1SkillId.HOLY_WEAPON) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ENCHANT_WEAPON)
								|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESS_WEAPON)) {
							continue;
						}
					} else if (skillId == L1SkillId.ENCHANT_WEAPON) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESS_WEAPON)) {
							continue;
						}
					} else if (skillId == L1SkillId.SHIELD) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IRON_SKIN)) {
							continue;
						}
					} else if (skillId == L1SkillId.HASTE) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HASTE)
								|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GREATER_HASTE)) {
							continue;
						}
					}
					if (!pc.getSkillEffectTimerSet().hasSkillEffect(skillId)) {
						if (skill.getMpConsume() <= pc.getCurrentMp()) {
							if (skillId == L1SkillId.HOLY_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && pc.getWeapon().equals(item)) {
										pc.sendPackets(
												new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.HOLY_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.ENCHANT_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && pc.getWeapon().equals(item)) {
										pc.sendPackets(
												new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.ENCHANT_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.BLESS_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && item.getItem().getType2() == 1) {
										pc.sendPackets(new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.BLESS_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.BLESSED_ARMOR) {
								if (pc != null && !pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESSED_ARMOR)) {
									pc.getAC().addAc(-3);
									pc.sendPackets(new S_OwnCharAttrDef(pc));
									pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.BLESSED_ARMOR, 20*60*1000);
								}
							}
						new L1SkillUse().handleCommands(pc, skillId, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_NORMAL);	
						L1SkillDelay.onSkillUse(pc, skill.getReuseDelay());
						}
					}	
					break;	
				}		
			} else if (pc.getType() == 2) {	//요정		
				switch(skillId) {
				case L1SkillId.LIGHT:
				case L1SkillId.SHIELD:
				case L1SkillId.HOLY_WEAPON:
				case L1SkillId.ENCHANT_WEAPON:
				case L1SkillId.DECREASE_WEIGHT:
				case L1SkillId.BLESSED_ARMOR:
				case L1SkillId.PHYSICAL_ENCHANT_DEX:
				case L1SkillId.PHYSICAL_ENCHANT_STR:
				case L1SkillId.HASTE:
				case L1SkillId.BLESS_WEAPON:

				case L1SkillId.RESIST_MAGIC:
				case L1SkillId.COUNTER_MIRROR:
				case L1SkillId.CLEAR_MIND:
				case L1SkillId.ELEMENTAL_PROTECTION:
					
				case L1SkillId.FIRE_WEAPON:
				case L1SkillId.FIRE_BLESS:
				case L1SkillId.BURNING_WEAPON:
				case L1SkillId.ELEMENTAL_FIRE:
				case L1SkillId.SOUL_OF_FLAME:
				case L1SkillId.ADDITIONAL_FIRE:

				case L1SkillId.NATURES_TOUCH:
				case L1SkillId.AQUA_PROTECTER:
					
				case L1SkillId.WIND_SHOT:
				case L1SkillId.WIND_WALK:
				case L1SkillId.STORM_EYE:
				case L1SkillId.STORM_SHOT:
					
				case L1SkillId.EARTH_SKIN:
				case L1SkillId.EARTH_BLESS:
				case L1SkillId.IRON_SKIN:
				case L1SkillId.EXOTIC_VITALIZE:
					L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
					if (pc.isSkillDelay()) {
						continue;
					}
					//System.out.println("스킬 = " + skillId);
					
					if (skillId == L1SkillId.HOLY_WEAPON) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ENCHANT_WEAPON)
								|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESS_WEAPON)) {
							continue;
						}
					} else if (skillId == L1SkillId.ENCHANT_WEAPON) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESS_WEAPON)) {
							continue;
						}
					} else if (skillId == L1SkillId.SHIELD) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IRON_SKIN)) {
							continue;
						}
					} else if (skillId == L1SkillId.HASTE) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HASTE)
								|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GREATER_HASTE)) {
							continue;
						}
					}
					if (!pc.getSkillEffectTimerSet().hasSkillEffect(skillId)) {
						if (skill.getMpConsume() <= pc.getCurrentMp()) {
							if (skillId == L1SkillId.HOLY_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && pc.getWeapon().equals(item)) {
										pc.sendPackets(
												new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.HOLY_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.ENCHANT_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && pc.getWeapon().equals(item)) {
										pc.sendPackets(
												new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.ENCHANT_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.BLESS_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && item.getItem().getType2() == 1) {
										pc.sendPackets(new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.BLESS_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.BLESSED_ARMOR) {
								if (pc != null && !pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESSED_ARMOR)) {
									pc.getAC().addAc(-3);
									pc.sendPackets(new S_OwnCharAttrDef(pc));
									pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.BLESSED_ARMOR, 20*60*1000);
								}
							}
							new L1SkillUse().handleCommands(pc, skillId, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_NORMAL);
							L1SkillDelay.onSkillUse(pc, skill.getReuseDelay());
						}
					}
					break;	
				}		
			} else if (pc.getType() == 3) {	// 마법사		
				switch(skillId) {		
				case L1SkillId.LIGHT: 		
				case L1SkillId.SHIELD: 		
				case L1SkillId.HOLY_WEAPON: 		
				case L1SkillId.ENCHANT_WEAPON: 		
				case L1SkillId.DECREASE_WEIGHT:  		
				case L1SkillId.BLESSED_ARMOR: 		
				case L1SkillId.PHYSICAL_ENCHANT_DEX: 		
				case L1SkillId.PHYSICAL_ENCHANT_STR: 		
				case L1SkillId.HASTE: 		
				case L1SkillId.BLESS_WEAPON: 		
				case L1SkillId.HOLY_WALK: 		
				case L1SkillId.GREATER_HASTE: 		
				case L1SkillId.IMMUNE_TO_HARM:
				case L1SkillId.ADVANCE_SPIRIT: 
					L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
					if (pc.isSkillDelay()) {
						continue;
					}
					if (skillId == L1SkillId.HOLY_WEAPON) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ENCHANT_WEAPON)
								|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESS_WEAPON)) {
							continue;
						}
					} else if (skillId == L1SkillId.ENCHANT_WEAPON) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESS_WEAPON)) {
							continue;
						}
					} else if (skillId == L1SkillId.HASTE) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HASTE)
								|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GREATER_HASTE)) {
							continue;
						}
					}
					if (!pc.getSkillEffectTimerSet().hasSkillEffect(skillId)) {
						if (skill.getMpConsume() <= pc.getCurrentMp()) {
							if (skillId == L1SkillId.HOLY_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && pc.getWeapon().equals(item)) {
										pc.sendPackets(
												new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.HOLY_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.ENCHANT_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && pc.getWeapon().equals(item)) {
										pc.sendPackets(
												new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.ENCHANT_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.BLESS_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && item.getItem().getType2() == 1) {
										pc.sendPackets(new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.BLESS_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.BLESSED_ARMOR) {
								if (pc != null && !pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESSED_ARMOR)) {
									pc.getAC().addAc(-3);
									pc.sendPackets(new S_OwnCharAttrDef(pc));
									pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.BLESSED_ARMOR, 20*60*1000);
								}
							}
							new L1SkillUse().handleCommands(pc, skillId, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_NORMAL);
							L1SkillDelay.onSkillUse(pc, skill.getReuseDelay());
						}
					}
					break;	
				}		
			} else if (pc.getType() == 4) {	 //다크엘프		
				switch(skillId) {		
				case L1SkillId.LIGHT: 		
				case L1SkillId.SHIELD: 		
				case L1SkillId.HOLY_WEAPON: 		
				case L1SkillId.ENCHANT_WEAPON: 		
				case L1SkillId.DECREASE_WEIGHT:  		
				case L1SkillId.ENCHANT_VENOM: 		
				case L1SkillId.SHADOW_ARMOR: 		
				case L1SkillId.MOVING_ACCELERATION: 		
				case L1SkillId.BURNING_SPIRIT: 		
				case L1SkillId.DOUBLE_BRAKE: 		
				case L1SkillId.VENOM_RESIST:
				case L1SkillId.UNCANNY_DODGE: 		
				case L1SkillId.SHADOW_FANG: 		
				case L1SkillId.DRESS_MIGHTY: 		
				case L1SkillId.DRESS_DEXTERITY: 		
				case L1SkillId.DRESS_EVASION: 	
					L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
					if (pc.isSkillDelay()) {
						continue;
					}
					if (skillId == L1SkillId.HOLY_WEAPON) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ENCHANT_WEAPON)
								|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESS_WEAPON)
								|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHADOW_FANG)) {
							continue;
						}
					} else if (skillId == L1SkillId.ENCHANT_WEAPON) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESS_WEAPON)
								|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHADOW_FANG)) {
							continue;
						}
					} else if (skillId == L1SkillId.BLESS_WEAPON) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHADOW_FANG)) {
							continue;
						}
					} else if (skillId == L1SkillId.DRESS_MIGHTY) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PHYSICAL_ENCHANT_STR)) {
							continue;
						}
					} else if (skillId == L1SkillId.DRESS_DEXTERITY) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PHYSICAL_ENCHANT_DEX)) {
							continue;
						}
					} else if (skillId == L1SkillId.SHIELD) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHADOW_ARMOR)) {
							continue;
						}
					} else if (skillId == L1SkillId.SHADOW_ARMOR) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESSED_ARMOR)) {
							continue;
						}
					}

					if (!pc.getSkillEffectTimerSet().hasSkillEffect(skillId)) {	
						if (skill.getMpConsume() <= pc.getCurrentMp()) {
							if (skillId == L1SkillId.HOLY_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && pc.getWeapon().equals(item)) {
										pc.sendPackets(
												new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.HOLY_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.ENCHANT_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && pc.getWeapon().equals(item)) {
										pc.sendPackets(
												new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.ENCHANT_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.BLESS_WEAPON) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && item.getItem().getType2() == 1) {
										pc.sendPackets(new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillWeaponEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.BLESS_WEAPON, 20*60*1000);
									}
								}
							} else if (skillId == L1SkillId.BLESSED_ARMOR) {
								if (pc != null && !pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLESSED_ARMOR)) {
									pc.getAC().addAc(-3);
									pc.sendPackets(new S_OwnCharAttrDef(pc));
									pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.BLESSED_ARMOR, 20*60*1000);
								}
							} else if (skillId == L1SkillId.SHADOW_FANG) {
								for (L1ItemInstance item : pc.getInventory().getItems()) {
									if (item != null && item.getItem().getType2() == 2 && item.getItem().getType() == 2) {
										pc.sendPackets(new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
										item.setSkillArmorEnchant(pc, skillId, skill.getBuffDuration() * 1000);
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.SHADOW_FANG, 20*60*1000);
									}
								}
							}
							new L1SkillUse().handleCommands(pc, skillId, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_NORMAL);
							L1SkillDelay.onSkillUse(pc, skill.getReuseDelay());
						}	
					}
					break;	
				}		
			}
		}
	}
}