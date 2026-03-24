/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model;

import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
//import static l1j.server.server.model.skill.L1SkillId.DARK_BLIND;
import static l1j.server.server.model.skill.L1SkillId.ARMOR_BRAKE;
import static l1j.server.server.model.skill.L1SkillId.BOUNCE_ATTACK;
import static l1j.server.server.model.skill.L1SkillId.BURNING_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.DOUBLE_BRAKE;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_FIRE;
import static l1j.server.server.model.skill.L1SkillId.ENCHANT_VENOM;
import static l1j.server.server.model.skill.L1SkillId.FEATHER_BUFF_A;
import static l1j.server.server.model.skill.L1SkillId.FEATHER_BUFF_B;
import static l1j.server.server.model.skill.L1SkillId.FREEZING_BLIZZARD;
import static l1j.server.server.model.skill.L1SkillId.ICE_LANCE;
import static l1j.server.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static l1j.server.server.model.skill.L1SkillId.MOB_BASILL;
import static l1j.server.server.model.skill.L1SkillId.MOB_COCA;
import static l1j.server.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static l1j.server.server.model.skill.L1SkillId.SOUL_OF_FLAME;
import static l1j.server.server.model.skill.L1SkillId.SPECIAL_COOKING;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CURSE_BARLOG;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CURSE_YAHEE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_MITHRIL_POWDER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_WATER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_WATER_OF_EVA;
import static l1j.server.server.model.skill.L1SkillId.UNCANNY_DODGE;

import java.util.Random;

import l1j.server.Config;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.NpcStatusDamage.NpcStatusDamageInfo;
import l1j.server.NpcStatusDamage.NpcStatusDamageType;
import l1j.server.server.ActionCodes;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.datatables.AccessoryBalanceTable;
import l1j.server.server.datatables.ArmorBalanceTable;
import l1j.server.server.datatables.CharacterBalance;
import l1j.server.server.datatables.CharactersAcTable;
import l1j.server.server.datatables.CharactersAcTable.CharactersAc;
import l1j.server.server.datatables.CharactersReducTable;
import l1j.server.server.datatables.CharactersReducTable.CharactersReduc;
import l1j.server.server.datatables.Map_Event;
import l1j.server.server.datatables.MonsterBalance;
import l1j.server.server.datatables.SpecialMapTable;
import l1j.server.server.datatables.WeaponNpcBalanceTable;
import l1j.server.server.datatables.WeaponPcBalanceTable;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.gametime.GameTimeClock;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.poison.L1ParalysisPoison;
import l1j.server.server.model.poison.L1SilencePoison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_AttackCritical;
import l1j.server.server.serverpackets.S_AttackMissPacket;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_AttackPacketForNpc;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_UseArrowSkill;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1SpecialMap;
import l1j.server.server.types.Point;
import l1j.server.server.utils.CalcStat;
import l1j.server.server.utils.CommonUtil;

public class L1Attack {

	private L1PcInstance _pc = null;

	private L1Character _target = null;

	private L1PcInstance _targetPc = null;

	private L1NpcInstance _npc = null;

	private L1NpcInstance _targetNpc = null;

	private final int _targetId;

	private int _targetX;

	private int _targetY;

	private int _statusDamage = 0;

	private static final Random _random = new Random();

	private int _hitRate = 0;

	private int _calcType;

	private static final int PC_PC = 1;

	private static final int PC_NPC = 2;

	private static final int NPC_PC = 3;

	private static final int NPC_NPC = 4;

	private boolean _isHit = false;

	private int _damage = 0;

	private int _drainMana = 0;

	/** 조우의 돌골렘 **/

	private int _drainHp = 0;

	/** 조우의 돌골렘 **/

	private int _attckGrfxId = 0;

	private int _attckActId = 0;

	// 공격자가 플레이어의 경우의 무기 정보
	private L1ItemInstance weapon = null;

	private int _weaponId = 0;

	private int _weaponType = 0;

	private int _weaponType1 = 0;

	private int _weaponAddHit = 0;

	private int _weaponAddDmg = 0;

	private int _weaponSmall = 0;

	private int _weaponLarge = 0;

	private int _weaponRange = 1;

	private int _weaponBless = 1;

	private int _weaponEnchant = 0;

	private int _weaponMaterial = 0;

	private int _weaponAttrEnchantLevel = 0;

	private int _weaponDoubleDmgChance = 0;

	private int _attackType = 0;

	private boolean _발라근거리가호 = false;
	private boolean _발라원거리가호 = false;

	private L1ItemInstance _arrow = null;

	private L1ItemInstance _sting = null;

	private int _leverage = 10; // 1/10배로 표현한다.

	public void setLeverage(int i) {
		_leverage = i;
	}

	private int getLeverage() {
		return _leverage;
	}

	private static final int[] strHit = new int[128];
	
	// 공격자가 플레이어의 경우의 스테이터스에 의한 보정
	static {
		for (int str = 0; str <= 7; str++) {
			strHit[str] = -2;
		}
		strHit[8] = -1;
		strHit[9] = -1;
		strHit[10] = 0;
		strHit[11] = 0;
		strHit[12] = 1;
		strHit[13] = 1;
		strHit[14] = 2;
		strHit[15] = 2;
		strHit[16] = 3;
		strHit[17] = 3;
		strHit[18] = 4;
		strHit[19] = 4;
		strHit[20] = 4;
		strHit[21] = 5;
		strHit[22] = 5;
		strHit[23] = 5;
		strHit[24] = 6;
		strHit[25] = 6;
		strHit[26] = 6;
		strHit[27] = 7;
		strHit[28] = 7;
		strHit[29] = 7;
		strHit[30] = 8;
		strHit[31] = 8;
		strHit[32] = 8;
		strHit[33] = 9;
		strHit[34] = 9;
		strHit[35] = 9;
		strHit[36] = 10;
		strHit[37] = 10;
		strHit[38] = 10;
		strHit[39] = 11;
		strHit[40] = 11;
		strHit[41] = 11;
		strHit[42] = 12;
		strHit[43] = 12;
		strHit[44] = 12;
		strHit[45] = 13;
		strHit[46] = 13;
		strHit[47] = 13;
		strHit[48] = 14;
		strHit[49] = 14;
		strHit[50] = 14;
		strHit[51] = 15;
		strHit[52] = 15;
		strHit[53] = 15;
		strHit[54] = 16;
		strHit[55] = 16;
		strHit[56] = 16;
		strHit[57] = 17;
		strHit[58] = 17;
		strHit[59] = 18;
		strHit[60] = 19;
		strHit[61] = 20;
		strHit[62] = 21;
		strHit[63] = 22;
		strHit[64] = 23;
		strHit[65] = 24;
		strHit[66] = 25;
		strHit[67] = 26;
		strHit[68] = 27;
		strHit[69] = 28;
		strHit[70] = 29;
		strHit[71] = 30;
		strHit[72] = 31;
		strHit[73] = 32;
		strHit[74] = 33;
		strHit[75] = 34;
		strHit[76] = 35;
		strHit[77] = 36;
		strHit[78] = 37;
		strHit[79] = 38;
		strHit[80] = 39;
		strHit[81] = 40;
		strHit[82] = 41;
		strHit[83] = 42;
		strHit[84] = 43;
		strHit[85] = 44;
		strHit[86] = 45;
		strHit[87] = 46;
		strHit[88] = 47;
		strHit[89] = 48;
		strHit[90] = 49;
		strHit[91] = 50;
		strHit[92] = 51;
		strHit[93] = 52;
		strHit[94] = 53;
		strHit[95] = 54;
		strHit[96] = 55;
		strHit[97] = 56;
		strHit[98] = 57;
		strHit[99] = 58;
		strHit[100] = 59;
		strHit[101] = 60;
		strHit[102] = 61;
	}

	private static final int[] dexHit = new int[128];

	static {
		// DEX 데미지 보정
		for (int dex = 0; dex <= 6; dex++) {
			// 0~11는 0
			dexHit[dex] = -2;
		}
		dexHit[7] = 0;
		dexHit[8] = 0;
		dexHit[9] = 0;
		dexHit[10] = 0;
		dexHit[11] = 1;
		dexHit[12] = 1;
		dexHit[13] = 1;
		dexHit[14] = 2;
		dexHit[15] = 2;
		dexHit[16] = 2;
		dexHit[17] = 3;
		dexHit[18] = 3;
		dexHit[19] = 4;
		dexHit[20] = 5;
		dexHit[21] = 5;
		dexHit[22] = 6;
		dexHit[23] = 6;
		dexHit[24] = 7;
		dexHit[25] = 8;
		dexHit[26] = 9;
		dexHit[27] = 10;
		dexHit[28] = 11;
		dexHit[29] = 12;
		dexHit[30] = 13;
		dexHit[31] = 14;
		dexHit[32] = 15;
		dexHit[33] = 16;
		dexHit[34] = 16;
		dexHit[35] = 16;
		dexHit[36] = 17;
		dexHit[37] = 17;
		dexHit[38] = 17;
		dexHit[39] = 18;
		dexHit[40] = 18;
		dexHit[41] = 18;
		dexHit[42] = 19;
		dexHit[43] = 19;
		dexHit[44] = 19;
		dexHit[45] = 19;
		dexHit[46] = 20;
		dexHit[47] = 20;
		dexHit[48] = 21;
		dexHit[49] = 21;
		dexHit[50] = 21;
		dexHit[51] = 22;
		dexHit[52] = 22;
		dexHit[53] = 22;
		dexHit[54] = 23;
		dexHit[55] = 23;
		dexHit[56] = 23;
		dexHit[57] = 24;
		dexHit[58] = 24;
		dexHit[59] = 24;
		dexHit[60] = 25;
		dexHit[61] = 25;
		dexHit[62] = 25;
		dexHit[63] = 26;
		dexHit[64] = 26;
		dexHit[65] = 26;
		dexHit[66] = 27;
		dexHit[67] = 27;
		dexHit[68] = 27;
		dexHit[69] = 28;
		dexHit[70] = 28;
		dexHit[71] = 29;
		dexHit[72] = 30;
		dexHit[73] = 31;
		dexHit[74] = 32;
		dexHit[75] = 33;
		dexHit[76] = 34;
		dexHit[77] = 35;
		dexHit[78] = 36;
		dexHit[79] = 37;
		dexHit[80] = 38;
		dexHit[81] = 39;
		dexHit[82] = 40;
		dexHit[83] = 41;
		dexHit[84] = 42;
		dexHit[85] = 43;
		dexHit[86] = 44;
		dexHit[87] = 45;
		dexHit[88] = 46;
		dexHit[89] = 47;
		dexHit[90] = 48;
		dexHit[91] = 49;
		dexHit[92] = 50;
		dexHit[93] = 51;
		dexHit[94] = 52;
		dexHit[95] = 53;
		dexHit[96] = 54;
		dexHit[97] = 55;
		dexHit[98] = 56;
		dexHit[99] = 57;
		dexHit[100] = 58;
		dexHit[101] = 59;
		dexHit[102] = 60;
	}

	private static final int[] strDmg = new int[128];

	static {
		// STR 데미지 보정
		for (int str = 0; str <= 8; str++) {
			// 1~8는 -2
			strDmg[str] = -2;
		}
		for (int str = 9; str <= 10; str++) {
			// 9~10는 -1
			strDmg[str] = -1;
		}
		strDmg[11] = 0;
		strDmg[12] = 0;
		strDmg[13] = 1;
		strDmg[14] = 1;
		strDmg[15] = 2;
		strDmg[16] = 2;
		strDmg[17] = 3;
		strDmg[18] = 3;
		strDmg[19] = 4;
		strDmg[20] = 4;
		strDmg[21] = 5;
		strDmg[22] = 5;
		strDmg[23] = 6;
		strDmg[24] = 6;
		strDmg[25] = 6;
		strDmg[26] = 7;
		strDmg[27] = 7;
		strDmg[28] = 7;
		strDmg[29] = 8;
		strDmg[30] = 8;
		strDmg[31] = 9;
		strDmg[32] = 9;
		strDmg[33] = 10;
		strDmg[34] = 11;
		int dmg = 12;
		for (int str = 35; str <= 127; str++) { // 35~127은 4마다＋1
			if (str % 4 == 1) {
				dmg++;
			}
			strDmg[str] = dmg;
		}
	}

	private static final int[] dexDmg = new int[128];

	static {
		// DEX 데미지 보정
		for (int dex = 0; dex <= 14; dex++) {
			// 0~14는 0
			dexDmg[dex] = 0;
		}
		dexDmg[15] = 1;
		dexDmg[16] = 2;
		dexDmg[17] = 3;
		dexDmg[18] = 4;
		dexDmg[19] = 4;
		dexDmg[20] = 4;
		dexDmg[21] = 5;
		dexDmg[22] = 5;
		dexDmg[23] = 5;
		dexDmg[24] = 6;
		dexDmg[25] = 6;
		dexDmg[26] = 6;
		dexDmg[27] = 7;
		dexDmg[28] = 7;
		dexDmg[29] = 7;
		dexDmg[30] = 8;
		dexDmg[31] = 8;
		dexDmg[32] = 8;
		dexDmg[33] = 9;
		dexDmg[34] = 9;
		dexDmg[35] = 9;
		int dmg = 10;
		for (int dex = 36; dex <= 127; dex++) { // 36~127은 4마다＋1 //#
			if (dex % 4 == 1) {
				dmg++;
			}

			dexDmg[dex] = dmg;
		}
	}

	public void setActId(int actId) {
		_attckActId = actId;
	}

	public void setGfxId(int gfxId) {
		_attckGrfxId = gfxId;
	}

	public int getActId() {
		return _attckActId;
	}

	public int getGfxId() {
		return _attckGrfxId;
	}

	public L1Attack(L1Character attacker, L1Character target) {
		if (attacker instanceof L1PcInstance) {
			_pc = (L1PcInstance) attacker;
			if (target instanceof L1PcInstance) {
				_targetPc = (L1PcInstance) target;
				_calcType = PC_PC;
			} else if (target instanceof L1NpcInstance) {
				_targetNpc = (L1NpcInstance) target;
				_calcType = PC_NPC;
			}
			// 무기 정보의 취득
			weapon = _pc.getWeapon();
			if (weapon != null) {
				_weaponId = weapon.getItem().getItemId();
				_weaponType = weapon.getItem().getType1();
				_weaponAddHit = weapon.getItem().getHitModifier() + weapon.getHitByMagic();
				_weaponAddDmg = weapon.getItem().getDmgModifier() + weapon.getDmgByMagic();
				_weaponType1 = weapon.getItem().getType();
				_weaponSmall = weapon.getItem().getDmgSmall();
				_weaponLarge = weapon.getItem().getDmgLarge();
				_weaponRange = weapon.getItem().getRange();
				_weaponBless = weapon.getItem().getBless();
				if (_weaponType != 20 && _weaponType != 62) {
					_weaponEnchant = weapon.getEnchantLevel() - weapon.get_durability(); // 손상분
																							// 마이너스
				} else {
					_weaponEnchant = weapon.getEnchantLevel();
				}
				_weaponMaterial = weapon.getItem().getMaterial();
				if (_weaponType == 20) { // 화살의 취득
					_arrow = _pc.getInventory().getArrow();
					if (_arrow != null) {
						_weaponBless = _arrow.getItem().getBless();
						_weaponMaterial = _arrow.getItem().getMaterial();
					}
				}
				if (_weaponType == 62) { // 스팅의 취득
					_sting = _pc.getInventory().getSting();
					if (_sting != null) {
						_weaponBless = _sting.getItem().getBless();
						_weaponMaterial = _sting.getItem().getMaterial();
					}
				}
				_weaponDoubleDmgChance = weapon.getItem().getDoubleDmgChance();
				_weaponAttrEnchantLevel = weapon.getAttrEnchantLevel();
			}
			// 스테이터스에 의한 추가 데미지 보정
			if (_weaponType == 20) { // 활의 경우는 DEX치 참조
				_statusDamage = dexDmg[_pc.getAbility().getTotalDex()];
			} else {
				_statusDamage = strDmg[_pc.getAbility().getTotalStr()];
			}
		} else if (attacker instanceof L1NpcInstance) {
			_npc = (L1NpcInstance) attacker;
			if (target instanceof L1PcInstance) {
				_targetPc = (L1PcInstance) target;
				_calcType = NPC_PC;
			} else if (target instanceof L1NpcInstance) {
				_targetNpc = (L1NpcInstance) target;
				_calcType = NPC_NPC;
			}
		}
		_target = target;
		_targetId = target.getId();
		_targetX = target.getX();
		_targetY = target.getY();
	}

	/* ■■■■■■■■■■■■■■■■ 명중 판정 ■■■■■■■■■■■■■■■■ */

	public boolean calcHit() {
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			if (_weaponRange != -1) {
				if (_pc.getLocation().getTileLineDistance(_target.getLocation()) > _weaponRange + 1) {
					_isHit = false;
					return _isHit;
				}
				if (_weaponType1 == 17) {
					_isHit = true;
					return _isHit;
				}
			} else {
				if (!_pc.getLocation().isInScreen(_target.getLocation())) {
					_isHit = false;
					return _isHit;
				}
			}
			if (_pc instanceof L1RobotInstance && _pc.isElf()) {
				if (!_pc.getLocation().isInScreen(_target.getLocation())) {
					_isHit = false;
					return _isHit;
				}
			}
			if (!(_pc instanceof L1RobotInstance) && _weaponType == 20 && _weaponId != 190 && _weaponId != 9100
					&& _weaponId != 450009 && _arrow == null) {
				return _isHit = false; // 화살이 없는 경우는 미스
			} else if (_weaponType == 62 && _sting == null) {
				return _isHit = false; // 스팅이 없는 경우는 미스
			} else if (!CharPosUtil.glanceCheck(_pc, _targetX, _targetY)) {
				return _isHit = false; // 공격자가 플레이어의 경우는 장애물 판정
			} else if (_weaponId == 247 || _weaponId == 248 || _weaponId == 249) {
				return _isHit = false; // 시련의 검B~C 공격 무효
			} else if (_calcType == PC_PC) {
				if (CharPosUtil.getZoneType(_pc) == 1 || CharPosUtil.getZoneType(_targetPc) == 1) {
					return _isHit = false;
				}
				return _isHit = calcPcPcHit();
			} else if (_calcType == PC_NPC) {
				return _isHit = calcPcNpcHit();
			}
		} else if (_calcType == NPC_PC) {
			return _isHit = calcNpcPcHit();
		} else if (_calcType == NPC_NPC) {
			return _isHit = calcNpcNpcHit();
		}
		return _isHit;
	}

	private boolean critical;

	public boolean isCritical() {
		return critical;
	}

	public void setCritical(boolean critical) {
		this.critical = critical;
	}

	// ●●●● 플레이어로부터 플레이어에의 명중 판정 ●●●●
	/*
	 * PC에의 명중율 =(PC의 Lv＋클래스 보정＋STR 보정＋DEX 보정＋무기 보정＋DAI의 매수/2＋마법 보정)×0.68－10 이것으로
	 * 산출된 수치는 자신이 최대 명중(95%)을 주는 일을 할 수 있는 상대측 PC의 AC 거기로부터 상대측 PC의 AC가 1좋아질 때마다
	 * 자명중율로부터 1당겨 간다 최소 명중율5% 최대 명중율95%
	 */
	private boolean calcPcPcHit() {
		if (_pc.getAbility().getTotalStr() > 39) {
			_hitRate += strHit[39];
		} else {
			_hitRate += strHit[_pc.getAbility().getTotalStr()];
		}
		
		if (_pc.getAbility().getTotalDex() > 39) {
			_hitRate += dexHit[39];
		} else {
			_hitRate += dexHit[_pc.getAbility().getTotalDex()];
		}
		
		_hitRate = (90 + _weaponAddHit + _weaponEnchant / 2);
		
		/** 스탯 + 무기에 따른 공성 **/
		_hitRate += PchitAdd();
		if (_targetPc.getAC().getAc() < 0)
			_hitRate += (int) ((-10 + _targetPc.getAC().getAc()) * 0.66D);
		else
			_hitRate -= (int) ((10 - _targetPc.getAC().getAc()) * 0.66D);
		
		_hitRate += WeaponPcBalanceTable.getInstance().getItemBalanceHit(_weaponId, _weaponEnchant);
		if (_targetPc.MaanDodge) {
			int rnd = _random.nextInt(100) + 1;
			if (rnd <= 10)
				_hitRate -= 6;
		}
		try {
			CharactersAc ca = CharactersAcTable.getInstance().getCharactersAc(_targetPc.getAC().getAc());
			if (ca != null) {
				_hitRate -= ca.getDodge();
			}
			CharactersReduc cr = CharactersReducTable.getInstance().getCharactersReduc(_targetPc.getLevel());
			if (cr != null) {
				_hitRate -= cr.getDodge();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (_weaponType == 20 || _weaponType == 62) {
			_hitRate += _pc.getBowHitup();
		}
		
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(UNCANNY_DODGE)) {
			_hitRate -= 5;
		}
 
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)) {
			_hitRate = 0;
		}
			
		int rnd = _random.nextInt(100) + 1;
		try {
			_hitRate += CharacterBalance.getInstance().getHit(_pc.getType(), _targetPc.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isShortDistance() && _targetPc.getSkillEffectTimerSet().hasSkillEffect(UNCANNY_DODGE)) {
			_hitRate -= (_hitRate * Config.UNCANNY_DODGE_DECREASE_RATE_BY_PC);
		}
		
		if (_weaponType == 20 && _hitRate > rnd) {
			return calcErEvasion();
		}
		
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)
				|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
				|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(FREEZING_BLIZZARD)
				|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)) {
			_hitRate = 0;
		}
		
		// 미스이팩 넣기
		if (_hitRate >= rnd) {
			return true;
		} else {
			_pc.sendPackets(new S_SkillSound(_targetPc.getId(), 20130));// 이펙트
			return false;
		}
	}

	// ●●●● 플레이어로부터 NPC 에의 명중 판정 ●●●●
	private boolean calcPcNpcHit() {
		/** SPR체크 **/
		if (_pc.AttackSpeedCheck2 >= 1) {
			if (_pc.AttackSpeedCheck2 == 1) {
				_pc.AttackSpeed2 = System.currentTimeMillis();
				_pc.sendPackets(new S_SystemMessage("\\fY[체크시작]"));
			}
			_pc.AttackSpeedCheck2++;
			if (_pc.AttackSpeedCheck2 >= 12) {
				_pc.AttackSpeedCheck2 = 0;
				double k = (System.currentTimeMillis() - _pc.AttackSpeed2) / 10D;
				String s = String.format("%.0f", k);
				_pc.AttackSpeed2 = 0;
				_pc.sendPackets(new S_SystemMessage("-----------------------------------------"));
				_pc.sendPackets(new S_SystemMessage("해당변신은 " + s + "이 공속으로 적절한값입니다."));
				_pc.sendPackets(new S_SystemMessage("-----------------------------------------"));
			}
		}
		
		/** SPR체크 **/
		// NPC에의 명중율
		// =(PC의 Lv＋클래스 보정＋STR 보정＋DEX 보정＋무기 보정＋DAI의 매수/2＋마법 보정)×5－{NPC의 AC×(-5)}

		_hitRate += _pc.getLevel();

		// 명중치 공식 수정본
		if (_pc.isDarkelf() || _pc.isKnight()) {
			_hitRate += _pc.getLevel() / 3;
		} else if (_pc.isElf() || _pc.isCrown()) {
			_hitRate += _pc.getLevel() / 5;
		}
		// 명중치 공식 수정본

		if (_weaponType != 20 && _weaponType != 62) {
			_hitRate += CalcStat.근거리명중(_pc.getAbility().getTotalStr()) * 2;
			_hitRate += _weaponAddHit + _pc.getHitup() + _pc.getHitupByArmor() + (_weaponEnchant / 2);
		} else {
			_hitRate += CalcStat.원거리명중(_pc.getAbility().getTotalDex()) * 2;
			_hitRate += _weaponAddHit + _pc.getBowHitup() + _pc.getBowHitupByArmor() + _pc.getBowHitupByDoll()
					+ (_weaponEnchant / 2);
		}

		_hitRate -= MonsterBalance.getInstance().getCharacterDodge(_targetNpc.getNpcTemplate().get_npcId());

		int npcId = _targetNpc.getNpcTemplate().get_npcId();
		if (npcId >= 45912 && npcId <= 45915 && !_pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_WATER)) {
			_hitRate = 0;
		}
		if (npcId == 45916 && !_pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
			_hitRate = 0;
		}
		if (npcId == 45941 && !_pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
			_hitRate = 0;
		}

		if (!_pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CURSE_BARLOG) && (npcId == 45752 || npcId == 45753)) {
			_hitRate = 0;
		}
		if (!_pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CURSE_YAHEE)
				&& (npcId == 45675 || npcId == 81082 || npcId == 45625 || npcId == 45674 || npcId == 45685)) {
			_hitRate = 0;
		}
		if (npcId >= 46068 && npcId <= 46091 && _pc.getGfxId().getTempCharGfx() == 6035) {
			_hitRate = 0;
		}
		if (npcId >= 46092 && npcId <= 46106 && _pc.getGfxId().getTempCharGfx() == 6034) {
			_hitRate = 0;
		}
		int rnd = _random.nextInt(100) + 1;

		_hitRate += WeaponNpcBalanceTable.getInstance().getItemBalanceHit(_weaponId, _weaponEnchant);
		
		try {
			_hitRate += CharacterBalance.getInstance().getHit(_pc.getType(), 10);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (_hitRate == 0) { // 미스 이팩트 처리
			_pc.sendPackets(new S_SkillSound(_targetNpc.getId(), 20130));
			System.out.println("미스");
		}

		return _hitRate >= rnd;
	}

	// ●●●● NPC 로부터 플레이어에의 명중 판정 ●●●●
	private boolean calcNpcPcHit() {
		double mapevent_Npc_Pc_Hit = Map_Event.getInstance().getadd_Npc_Pc_hit(_npc.getMapId()); // 맵이벤트
		_hitRate += _npc.getLevel();

		if (_npc instanceof L1PetInstance) {
			_hitRate += ((L1PetInstance) _npc).getHitByWeapon();
		}
		if (mapevent_Npc_Pc_Hit == 0) {
			mapevent_Npc_Pc_Hit = 1;
		}
		double status = 0;
		int level = Math.max(_npc.getLevel(), 2);
		if (this._npc.getNpcTemplate().getBowActId() > 0) {
			NpcStatusDamageInfo eInfo = NpcStatusDamageInfo.find_npc_status_info(NpcStatusDamageType.LONG_HIT, level);
			if (eInfo != null)
				status = this._npc.getAbility().getTotalDex() * eInfo.get_increase_dmg();
			else
				status = this._npc.getAbility().getTotalDex();
		} else {
			NpcStatusDamageInfo eInfo = NpcStatusDamageInfo.find_npc_status_info(NpcStatusDamageType.SHORT_HIT, level);
			if (eInfo != null)
				status = this._npc.getAbility().getTotalStr() * eInfo.get_increase_dmg();
			else
				status = this._npc.getAbility().getTotalStr();
		}

		if (status <= 0)
			status = 1;

		_hitRate += status + _npc.getLevel();
		_hitRate += _npc.getHitup();
		_hitRate += (_hitRate * mapevent_Npc_Pc_Hit);

		if (_targetPc.MaanDodge) {
			int rnd = _random.nextInt(100) + 1;
			if (rnd <= 10)
				_hitRate -= 6;
		}

		if (_targetPc.getAC().getAc() < 0) {
			_hitRate -= (int) (_targetPc.getAC().getAc() * Config.AC_HIT_NPCPC/* 1.5 */) * -1;
		} else {
			_hitRate -= (int) (_targetPc.getAC().getAc() * Config.AC_HIT_NPCPC/* 1.5 */);
		}

		try {
			CharactersAc ca = CharactersAcTable.getInstance().getCharactersAc(_targetPc.getAC().getAc());
			if (ca != null) {
				_hitRate -= ca.getDodge();
			}
			CharactersReduc cr = CharactersReducTable.getInstance().getCharactersReduc(_targetPc.getLevel());
			if (cr != null) {
				_hitRate -= cr.getDodge();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			_hitRate += CharacterBalance.getInstance().getHit(10, _targetPc.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}

		_hitRate += MonsterBalance.getInstance().getCharacterHitRate(_npc.getNpcTemplate().get_npcId());

		if (isShortDistance() && _targetPc.getSkillEffectTimerSet().hasSkillEffect(UNCANNY_DODGE)) {
			_hitRate -= (_hitRate * Config.UNCANNY_DODGE_DECREASE_RATE_BY_NPC);
		}

		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) {
			_hitRate = 0;
		}

		int rnd = _random.nextInt(100) + 1;
		// NPC의 공격 레인지가 10이상의 경우로, 2이상 떨어져 있는 경우활공격으로 간주한다
		if (_npc.getNpcTemplate().get_ranged() >= 10 && _hitRate > rnd
				&& _npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) >= 3) {
			return calcErEvasion();
		}
		return _hitRate >= rnd;
	}

	// ●●●● NPC 로부터 NPC 에의 명중 판정 ●●●●
	private boolean calcNpcNpcHit() {
		int target_ac = 10 - _targetNpc.getAC().getAc();
		int attacker_lvl = _npc.getNpcTemplate().get_level();

		if (target_ac != 0) {
			_hitRate = (100 / target_ac * attacker_lvl); // 피공격자 AC = 공격자 Lv
			// 의 때 명중율 100%
		} else {
			_hitRate = 100 / 1 * attacker_lvl;
		}

		if (_npc instanceof L1PetInstance) { // 펫은 LV1마다 추가 명중+2
			_hitRate += _npc.getLevel() * 2;
			_hitRate += ((L1PetInstance) _npc).getHitByWeapon();
		}

		if (_hitRate < attacker_lvl) {
			_hitRate = attacker_lvl; // 최저 명중율=Lｖ％
		}

		try {
			_hitRate += CharacterBalance.getInstance().getHit(10, 10);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (_hitRate > 95) {
			_hitRate = 95; // 최고 명중율은 95%
		}
		if (_hitRate < 5) {
			_hitRate = 5; // 공격자 Lv가 5 미만때는 명중율 5%
		}

		int rnd = _random.nextInt(100) + 1;
		return _hitRate >= rnd;
	}

	// ●●●● ER에 의한 회피 판정 ●●●●
	private boolean calcErEvasion() {
		int er = _targetPc.getEr();

		int rnd = _random.nextInt(130) + 1;
		return er < rnd;
	}

	/* ■■■■■■■■■■■■■■■ 데미지 산출 ■■■■■■■■■■■■■■■ */

	public int calcDamage() {
		try {
			// System.out.println("//"+_targetPc.getName());
			switch (_calcType) {
			case PC_PC:
				_damage = calcPcPcDamage();
				try {
					_damage += CharacterBalance.getInstance().getDmg(_pc.getType(), _targetPc.getType());
					_damage *= CharacterBalance.getInstance().getDmgRate(_pc.getType(), _targetPc.getType());
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 피격 모션 처리를 어디에 해야 할 까?
				if (_targetPc.getGfxId().getTempCharGfx() == 16074 || _targetPc.getGfxId().getTempCharGfx() == 16053
						|| _targetPc.getGfxId().getTempCharGfx() == 14491
						|| _targetPc.getGfxId().getTempCharGfx() == 16056
						|| _targetPc.getGfxId().getTempCharGfx() == 16284
						|| _targetPc.getGfxId().getTempCharGfx() == 16002
						|| _targetPc.getGfxId().getTempCharGfx() == 16040
						|| _targetPc.getGfxId().getTempCharGfx() == 16027
						|| _targetPc.getGfxId().getTempCharGfx() == 16014
						|| _targetPc.getGfxId().getTempCharGfx() == 16008
						|| _targetPc.getGfxId().getTempCharGfx() == 15986) {
					_targetPc.sendPackets(new S_DoActionGFX(_targetPc.getId(), ActionCodes.ACTION_Damage));
					Broadcaster.broadcastPacket(_targetPc,
							new S_DoActionGFX(_targetPc.getId(), ActionCodes.ACTION_Damage));
				}
				break;
			case PC_NPC:
				_damage = calcPcNpcDamage();

				try {
					_damage += CharacterBalance.getInstance().getDmg(_pc.getType(), 10);
					_damage *= CharacterBalance.getInstance().getDmgRate(_pc.getType(), 10);
				} catch (Exception e) {
					e.printStackTrace();
				}

				_damage -= MonsterBalance.getInstance().getCharacterReduc(_targetNpc.getNpcTemplate().get_npcId());

				break;
			case NPC_PC:
				_damage = calcNpcPcDamage();

				try {
					_damage += CharacterBalance.getInstance().getDmg(10, _targetPc.getType());
					_damage *= CharacterBalance.getInstance().getDmgRate(10, _targetPc.getType());
				} catch (Exception e) {
					e.printStackTrace();
				}

				_damage += MonsterBalance.getInstance().getCharacterBalance(_npc.getNpcTemplate().get_npcId());

				// 피격 모션 처리를 어디에 해야 할 까?
				if (_targetPc.getGfxId().getTempCharGfx() == 16074 || _targetPc.getGfxId().getTempCharGfx() == 16053
						|| _targetPc.getGfxId().getTempCharGfx() == 14491
						|| _targetPc.getGfxId().getTempCharGfx() == 16056
						|| _targetPc.getGfxId().getTempCharGfx() == 16284
						|| _targetPc.getGfxId().getTempCharGfx() == 16002
						|| _targetPc.getGfxId().getTempCharGfx() == 16040
						|| _targetPc.getGfxId().getTempCharGfx() == 16027
						|| _targetPc.getGfxId().getTempCharGfx() == 16014
						|| _targetPc.getGfxId().getTempCharGfx() == 16008
						|| _targetPc.getGfxId().getTempCharGfx() == 15986) {
					_targetPc.sendPackets(new S_DoActionGFX(_targetPc.getId(), ActionCodes.ACTION_Damage));
					Broadcaster.broadcastPacket(_targetPc,
							new S_DoActionGFX(_targetPc.getId(), ActionCodes.ACTION_Damage));
				}
				if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(IMMUNE_TO_HARM)) {
					_damage /= Config.IMMUNE_TO_HARM_NPC;
				}
				break;
			case NPC_NPC:
				_damage = calcNpcNpcDamage();

				try {
					_damage += CharacterBalance.getInstance().getDmg(10, 10);
					_damage *= CharacterBalance.getInstance().getDmgRate(10, 10);
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (_damage < 0) {
			_damage = 0;
		}
		return _damage;

	}

	// ●●●● 플레이어로부터 플레이어에의 데미지 산출 ●●●●
	public int calcPcPcDamage() {
		int weaponMaxDamage = _weaponSmall + _weaponAddDmg;

		int weaponDamage = 0;

		int doubleChance = _random.nextInt(120) + 1;
		if (_target != null) {
			int chance1 = _random.nextInt(100);
			for (L1ItemInstance item : _targetPc.getInventory().getItems()) {
				if (item.isEquipped()) {
					if (item.getItemId() >= 420104 && item.getItemId() <= 420107) {
						if (chance1 < Config.파푸가호확률) {
							int basehp = (int) Config.파푸이펙트대미지1;
							if (item.getEnchantLevel() == 7)
								basehp = (int) Config.파푸이펙트대미지2;
							if (item.getEnchantLevel() == 8)
								basehp = (int) Config.파푸이펙트대미지3;
							if (item.getEnchantLevel() == 9)
								basehp = (int) Config.파푸이펙트대미지4;
							_targetPc.setCurrentHp(_targetPc.getCurrentHp() + basehp);
							_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2187));
							Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(), 2187));
						}
						break;
					} else if (item.getItemId() >= 420108 && item.getItemId() <= 420111) {
						if (chance1 < Config.린드가호확률) {
							int basemp = (int) Config.린드이펙트대미지1;
							if (item.getEnchantLevel() == 7)
								basemp = (int) Config.린드이펙트대미지2;
							if (item.getEnchantLevel() == 8)
								basemp = (int) Config.린드이펙트대미지3;
							if (item.getEnchantLevel() == 9)
								basemp = (int) Config.린드이펙트대미지4;
							_targetPc.setCurrentMp(_targetPc.getCurrentMp() + basemp);
							_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2188));
							Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(), 2188));
						}
						break;
					} else if (item.getItemId() == 9204) {
						if (chance1 < 6) {
							_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 50);
							_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 13429));
							Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(), 13429));
						}
						break;
					} else if (item.getItemId() == 21255) {
						if (chance1 < 4) {
							_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 31);
							_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2183));
							Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(), 2183));
						}
						break;
					}
				}
			}
		}
		// 파푸가호
		if (_weaponType == 58 && doubleChance <= _weaponDoubleDmgChance) { // 크로우
																			// 더블
			weaponDamage = weaponMaxDamage;
			_attackType = 2; // 이게 이팩트군

			// _pc.sendPackets(new S_SkillSound(_targetId, 13416));
			// Broadcaster.broadcastPacket(_pc, new
			// S_SkillSound(_targetPc.getId(), 13416));

		} else if (_weaponType == 0) { // 맨손
			weaponDamage = 0;
		} else {
			weaponDamage = _random.nextInt(_weaponSmall) + 1 + _weaponAddDmg;
		}

		if (_발라원거리가호 || _발라근거리가호) {
			weaponDamage = weaponMaxDamage * 2;
			_발라근거리가호 = false;
			_발라원거리가호 = false;
		}
		if (_pc.getSkillEffectTimerSet().hasSkillEffect(SOUL_OF_FLAME)) {
			if (_weaponType != 20 && _weaponType != 62) {
				weaponDamage = weaponMaxDamage;
			}
		}

		int weaponTotalDamage = weaponDamage + _weaponEnchant;

		if (_pc.getSkillEffectTimerSet().hasSkillEffect(DOUBLE_BRAKE) && (_weaponType == 54 || _weaponType == 58)) {
			if ((_random.nextInt(1000000) + 1) <= Config.DOUBLE_BRAKE_EXERCISE_PROB) {
				weaponTotalDamage *= Config.DOUBLE_BRAKE_WEAPON_INCREASE_DMG_RATE;
			}
		}

		if (_pc.getSkillEffectTimerSet().hasSkillEffect(ARMOR_BRAKE) && (_weaponType == 54 || _weaponType == 58)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				weaponTotalDamage *= 1.8;
			}
		}

		double dmg = weaponTotalDamage + _statusDamage;

		if (_random.nextInt(11) == 1) {
			setCritical(true);

			dmg *= 1.1;
		}

		if (_weaponType != 20 && _weaponType != 62) {
			dmg += _pc.getDmgup() + _pc.getDmgupByArmor();
		} else {
			dmg += _pc.getBowDmgup() + _pc.getBowDmgupByArmor() + _pc.getBowDmgupByDoll();
		}

		if (_weaponType == 20) { // 활
			if (_arrow != null) {
				int add_dmg = _arrow.getItem().getDmgSmall();
				if (add_dmg == 0) {
					add_dmg = 1;
				}
				dmg += _random.nextInt(add_dmg) + 1;
			} else if (_weaponId == 190 || _weaponId == 9100 || _weaponId == 450009) { // 사이하의
																						// 활,
																						// 가이아
				// 격노
				dmg += _random.nextInt(15) + 1;
			}
		} else if (_weaponType == 62) { // 암 토토 렛
			int add_dmg = _sting.getItem().getDmgSmall();
			if (add_dmg == 0) {
				add_dmg = 1;
			}
			dmg = dmg + _random.nextInt(add_dmg) + 1;
		}

		/** 악몽의장궁 진노의크로스보우 리뉴얼 6115 **/
		if (_pc.getInventory().checkEquipped(450009) || _pc.getInventory().checkEquipped(66)
				|| _pc.getInventory().checkEquipped(450008) || _pc.getInventory().checkEquipped(450010)) {// 아이템번호 디비에
																											// 맞게 변경
			if (_pc.getLawful() < -32760) {
				dmg += 13;
			}
			if (_pc.getLawful() >= -32760 && _pc.getLawful() < -25000) {
				dmg += 10;
			}
			if (_pc.getLawful() >= -25000 && _pc.getLawful() < -15000) {
				dmg += 7;
			}
			if (_pc.getLawful() >= -15000 && _pc.getLawful() < 0) {
				dmg += 2;
			}
			if (_pc.getLawful() >= 0) {
				dmg += 1;
			}
		}
		// dmg = calcBuffDamage(dmg); 중복으로인한주석처리
		/** 진노의크로우 리뉴얼 6127 **/
		if (_pc.getInventory().checkEquipped(6127)) {// 아이템번호 디비에 맞게 변경
			if (_pc.getLawful() < -32760) {
				dmg += 8;
			}
			if (_pc.getLawful() >= -32760 && _pc.getLawful() < -25000) {
				dmg += 6;
			}
			if (_pc.getLawful() >= -25000 && _pc.getLawful() < -15000) {
				dmg += 4;
			}
			if (_pc.getLawful() >= -15000 && _pc.getLawful() < 0) {
				dmg += 2;
			}
			if (_pc.getLawful() >= 0) {
				dmg += 1;
			}
		}
		// dmg = calcBuffDamage(dmg); 중복으로인한주석처리
		/** 붉은 기사의 대검 리뉴얼 4150133 **/
		if (_pc.getInventory().checkEquipped(4150133)) {// 아이템번호 디비에 맞게 변경
			if (_pc.getLawful() < -32760) {
				dmg += 8;
			}
			if (_pc.getLawful() >= -32760 && _pc.getLawful() < -25000) {
				dmg += 6;
			}
			if (_pc.getLawful() >= -25000 && _pc.getLawful() < -15000) {
				dmg += 4;
			}
			if (_pc.getLawful() >= -15000 && _pc.getLawful() < 0) {
				dmg += 2;
			}
			if (_pc.getLawful() >= 0) {
				dmg += 1;
			}
		}
		dmg = calcBuffDamage(dmg);
		// 80렙부터 1렙당 추가 데미지 1씩..데미지를 바꾸고싶으면 뒤에숫자 2을 바꾸세요
		dmg += Math.max(0, _pc.getLevel() - 80) * 2; // 렙당데미지추가2

		dmg = calcPcDefenseByAcTable(dmg);
		// //데미지 버그 350이상은 나올수없다.
		// if (_calcType == PC_PC || _calcType == PC_NPC) {
		// if (dmg > 100) {//데미지 조절부분
		// dmg = 200;
		// }
		// }
		

		int 발동찬스 = _random.nextInt(100);
		int basedmg = 0;
		if (발동찬스 <= Config.발라가호확률) {
			for (L1ItemInstance item : _pc.getInventory().getItems()) {
				if (item.isEquipped()) {
					if (item.getItemId() >= 420112 && item.getItemId() <= 420115) {
						basedmg = (int) Config.발라이펙트대미지1;
						if (item.getEnchantLevel() == 7)
							basedmg = (int) Config.발라이펙트대미지2;
						if (item.getEnchantLevel() == 8)
							basedmg = (int) Config.발라이펙트대미지3;
						if (item.getEnchantLevel() == 9)
							basedmg = (int) Config.발라이펙트대미지4;
						dmg += basedmg;
						_pc.sendPackets(new S_SkillSound(_pc.getId(), 15841));
						Broadcaster.broadcastPacket(_pc, new S_SkillSound(_pc.getId(), 15841));
						_발라근거리가호 = true;
					}
				}
			}
		}
		if (_weaponType == 0) { // 맨손
			dmg = (_random.nextInt(5) + 4) / 4;
		} else if (_weaponType == 46) {
			dmg += 2;
		} else if (_weaponType == 50) {
			dmg += 4;
		}
		if (_weaponType1 == 17) {
			dmg = WeaponSkill.getKiringkuDamage(_pc, _target);
		}
		switch (_weaponId) {
		case 2:
		case 200002:
			dmg = WeaponSkill.getDiceDaggerDamage(_pc, _targetPc, weapon);
			break;
		case 13:
		case 44:
			WeaponSkill.getPoisonSword(_pc, _targetPc);
			break;
		case 47:
			WeaponSkill.getSilenceSword(_pc, _targetPc);
			break;
		case 134:
		case 54:
		case 450011:
			dmg += WeaponSkill.getKurtSwordDamage(_pc, _targetPc);
			break;
		case 58:
			dmg += WeaponSkill.getDeathKnightSwordDamage(_pc, _targetPc);
			break;
		case 76:
			dmg += WeaponSkill.getRondeDamage(_pc, _targetPc);
			break;
		case 121:
			dmg += WeaponSkill.getIceQueenStaffDamage(_pc, _target);
			break;
		case 124:
			dmg += WeaponSkill.getBaphometStaffDamage(_pc, _target);
			바포엠흡();
			break;
		case 450008:
			dmg += WeaponSkill.get4차StaffDamage(_pc, _target);
			break;
		case 126:
		case 127:
			calcStaffOfMana();
			break;
		case 203:
			dmg += WeaponSkill.getBarlogSwordDamage(_pc, _target);
			break;
		case 204:
		case 100204:
			WeaponSkill.giveFettersEffect(_pc, _targetPc);
			break;
		case 1136:
			dmg += WeaponSkill.getNightmareBowDamage(_pc, _target, _weaponEnchant);
			break;
		case 205:
			// case 450009:
			dmg += WeaponSkill.getMoonBowDamage(_pc, _target, _weaponEnchant);
			break;

		case 256:
			dmg += WeaponSkill.getEffectSwordDamage(_pc, _target, 2750);
			break;
		case 412000:
			dmg += WeaponSkill.getEffectSwordDamage(_pc, _target, 10);
			break;
		case 412001:
			calcDrainOfHp(dmg);
			break;
		case 412004:
			dmg += WeaponSkill.getIceSpearDamage(_pc, _target);
			break;
		case 412005:
			dmg += WeaponSkill.geTornadoAxeDamage(_pc, _target);
			break;
		case 412002:
		case 450009:
			calcDrainOfMana();
			break;
		case 412003:
			WeaponSkill.getDiseaseWeapon(_pc, _target, 412003);
			break;
		case 413101:
		case 413102:
		case 413104:
		case 413105:
			WeaponSkill.getDiseaseWeapon(_pc, _target, 413101);
			break;
		case 415010:
		case 415011:
		case 415012:
		case 415013:
			dmg += WeaponSkill.getChaserDamage(_pc, _target, 6985);
			break;
		case 415015:
		case 415016:
			dmg += WeaponSkill.getChaserDamage(_pc, _target, 7179);
			break;
		case 413103:
			// case 450009:

			calcStaffOfMana();
			WeaponSkill.getDiseaseWeapon(_pc, _target, 413101);
			break;
		case 317: // 파괴의 크로우
		case 316: // 파괴의 이도류
			dmg += WeaponSkill.getVenomBladesDamage(_pc, _target, _weaponEnchant);
			break;
		case 84:
		case 10084:
		case 164:
		case 100164:
		case 100189:
		case 59:
		case 61:
		case 86:
			dmg += 10;
			break;
		case 189:
			dmg += 4;
			break;

		}

		for (L1DollInstance doll : _pc.getDollList().values()) {
			dmg += doll.attackPixieDamage(_pc, _targetPc);
			dmg += doll.getDamageByDoll();
			doll.attackPoisonDamage(_pc, _targetPc);
		}

		dmg += 룸티스검귀추가데미지();

		if (_pc.MaanAddDmg) {
			int rnd = _random.nextInt(100) + 1;
			if (rnd <= 15)
				dmg += 5;
		}
		dmg -= _targetPc.getDamageReductionByArmor(); // 방어용 기구에 의한 데미지 경감

		if (_pc.getWeapon() != null && _targetPc.getWeapon() != null) {
			if (_pc.getWeapon().getItem().getType() == 4) {
				dmg -= _targetPc.getAddBowReduction();
			}
		}
		
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE)) {
			dmg += Config.게일추가대미지;
		}
		
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(SPECIAL_COOKING)) { // 스페셜요리에
																					// 의한
																					// 데미지
																					// 경감
			dmg -= 5;
		}
		dmg -= 룸티스붉귀데미지감소();
		dmg -= 스냅퍼체반데미지감소();
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(REDUCTION_ARMOR)) {
			int targetPcLvl = _targetPc.getLevel();
			if (targetPcLvl < 50) {
				targetPcLvl = 50;
			}
			dmg -= (targetPcLvl - 50) / 5 + 1;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(FEATHER_BUFF_A)) {
			dmg -= 3;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(FEATHER_BUFF_B)) {
			dmg -= 2;
		}
		// 룸티스 붉은빛 확률적 데미지감소처리
		if (_targetPc.getInventory().checkEquipped(500040)) {// 반역자의방패
			int chance = _random.nextInt(100);
			L1ItemInstance item = _targetPc.getInventory().findEquippedItemId(500040);
			int enchant = item.getEnchantLevel();
			if (chance <= 2 + (enchant * 2)) {
				dmg -= 20;
				_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 6320));
				_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 6320));
			}
		}
		/** 아머브레이크 */
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ARMOR_BRAKE)) { // 아머브레이크
			if (_weaponType != 20 && _weaponType != 62) {
				dmg *= 1.58;
			}
		}
		/** 아머브레이크 */
		/** 개인 밸런스 추가 */
		if (_pc.getAddDamageRate() >= CommonUtil.random(100)) {
			dmg += _pc.getAddDamage();
		}

		if (_targetPc.getAddReductionRate() >= CommonUtil.random(100)) {
			dmg -= _targetPc.getAddReduction();
		}

		if (_pc.getBapodmg() > 0) {
			dmg += _pc.getBapodmg();
		}

		// 무기 벨런스 처리
		if (_weaponType == 20) {
			dmg -= 15;
		}

		if (_weaponType == 46) {
			dmg -= 5;
		}

		if (_weaponId == 317 || _weaponId == 316) { // 파이 파크
			dmg -= 10;
		}

		if (_weaponId == 318) { // 파장
			dmg -= 4;
		}
		if (_weaponId == 205) { // 달장
			dmg -= 3;
		}

		// 인챈당 대미지 추타
		/*int randomAdddmg = (_random.nextInt(3));
		if (_weaponEnchant == 7) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg7to1;
				break;
			case 1:
				dmg += Config.adddmg7to2;
				break;
			case 2:
				dmg += Config.adddmg7to3;
				break;
			}
		} else if (_weaponEnchant == 8) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg8to1;
				break;
			case 1:
				dmg += Config.adddmg8to2;
				break;
			case 2:
				dmg += Config.adddmg8to3;
				break;
			}
		} else if (_weaponEnchant == 9) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg9to1;
				break;
			case 1:
				dmg += Config.adddmg9to2;
				break;
			case 2:
				dmg += Config.adddmg9to3;
				break;
			}
		} else if (_weaponEnchant == 10) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg10to1;
				break;
			case 1:
				dmg += Config.adddmg10to2;
				break;
			case 2:
				dmg += Config.adddmg10to3;
				break;
			}
		} else if (_weaponEnchant == 11) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg11to1;
				break;
			case 1:
				dmg += Config.adddmg11to2;
				break;
			case 2:
				dmg += Config.adddmg11to3;
				break;
			}
		} else if (_weaponEnchant == 12) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg12to1;
				break;
			case 1:
				dmg += Config.adddmg12to2;
				break;
			case 2:
				dmg += Config.adddmg12to3;
				break;
			}
		} else if (_weaponEnchant == 13) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg13to1;
				break;
			case 1:
				dmg += Config.adddmg13to2;
				break;
			case 2:
				dmg += Config.adddmg13to3;
				break;
			}
		} else if (_weaponEnchant == 14) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg14to1;
				break;
			case 1:
				dmg += Config.adddmg14to2;
				break;
			case 2:
				dmg += Config.adddmg14to3;
				break;
			}
		} else if (_weaponEnchant == 15) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg15to1;
				break;
			case 1:
				dmg += Config.adddmg15to2;
				break;
			case 2:
				dmg += Config.adddmg15to3;
				break;
			}
		}*/

		// 캐릭터 간 대미지 외부화 처리

		if (_calcType == PC_PC && _weaponId != 0) {
			if (_pc.isCrown()) {
				dmg += Config.PRINCE_ADD_DAMAGEPC;
			} else if (_pc.isKnight()) {
				dmg += Config.KNIGHT_ADD_DAMAGEPC;
			} else if (_pc.isElf()) {
				dmg += Config.ELF_ADD_DAMAGEPC;
			} else if (_pc.isDarkelf()) {
				dmg += Config.DARKELF_ADD_DAMAGEPC;
			} else if (_pc.isWizard()) {
				dmg += Config.WIZARD_ADD_DAMAGEPC;
			}
		}

		if (_weaponId == 9100) {// 가이아의격노 데미지리덕션
			int targetReduc = _targetPc.getDamageReductionByArmor();
			if (targetReduc > weapon.getEnchantLevel() + 9) {
				targetReduc = weapon.getEnchantLevel() + 9;
			}
			if (targetReduc > 0) {
				dmg += targetReduc;
			} else {
				dmg += 0;
			}
		}

		if (_calcType == PC_PC) {
			/** PVP추가 대미지 **/
			if (_pc.getPVPDamage() > 0) {
				int PVP = _pc.getPVPDamage();
				dmg += PVP;

			}

			/** PVP대미지 감소 **/
			if (_pc.getPVPDamageReduction() > 0) {
				int PVP = _pc.getPVPDamageReduction();
				dmg -= PVP;

			}
		}

		/*double dmgRate = 1;
		try {
			switch (_weaponEnchant) {
			case 7:
				dmgRate = Config.RATE_7_DMG_RATE;
				break;
			case 8:
				dmgRate = Config.RATE_8_DMG_RATE;
				break;
			case 9:
				dmgRate = Config.RATE_9_DMG_RATE;
				break;
			case 10:
				dmgRate = Config.RATE_10_DMG_RATE;
				break;
			case 11:
				dmgRate = Config.RATE_11_DMG_RATE;
				break;
			case 12:
				dmgRate = Config.RATE_12_DMG_RATE;
				break;
			case 13:
				dmgRate = Config.RATE_13_DMG_RATE;
				break;
			case 14:
				dmgRate = Config.RATE_14_DMG_RATE;
				break;
			case 15:
				dmgRate = Config.RATE_15_DMG_RATE;
				break;
			case 16:
				dmgRate = Config.RATE_16_DMG_RATE;
				break;
			case 17:
				dmgRate = Config.RATE_17_DMG_RATE;
				break;
			case 18:
				dmgRate = Config.RATE_18_DMG_RATE;
				break;
			}
		} catch (Exception e) {
		}

		dmg *= dmgRate;*/

		if (_pc.isTRIPLE) {
			dmg = dmg * Config.TRIPLE_DMG;
		}

		if (_targetPc.getInventory().checkEquipped(20395)) {
			//
			int chance = _random.nextInt(100) + 1;
			int chanceHp = _random.nextInt(20) + 1;

			boolean type = false;

			L1ItemInstance item = _targetPc.getInventory().checkEquippedItem(20395);
			if (item != null) {
				if (item.getEnchantLevel() == 0) {
					if (chance <= 2) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 5 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 1) {
					if (chance <= 4) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 10 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 2) {
					if (chance <= 6) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 20 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 3) {
					if (chance <= 8) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 30 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 4) {
					if (chance <= 10) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 40 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 5) {
					if (chance <= 15) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 50 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 6) {
					if (chance <= 20) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 60 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 7) {
					if (chance <= 25) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 70 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 8) {
					if (chance <= 30) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 80 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 9) {
					if (chance <= 40) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 100 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 10) {
					if (chance <= 50) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 150 + chanceHp);
						type = true;
					}
				}

				if (type) {
					_targetPc.sendPackets(new S_SkillSound(_targetId, 2187));// 파푸가호
					_targetPc.broadcastPacket(new S_SkillSound(_targetId, 2187));
				}

			}
		}

		if (_pc.getWeapon() != null && _pc.getWeapon().getEnchantLevel() >= 9) {
			WeaponSkill.getDiseaseWeapon(_pc, _target);
		}
		
		for (L1ItemInstance armor : _targetPc.getEquipSlot().getArmors()) {
			if (AccessoryBalanceTable.getInstance().getItemBalanceReduction(armor.getItemId(), armor.getEnchantLevel()) != 0) {
				if (armor.getItem().getType() >= 8 && armor.getItem().getType() <= 13) {
					//_pc.sendPackets(new S_SystemMessage("악세사리 = " + armor.getName() + ", 적용전 대미지 = " + dmg));
					dmg -= AccessoryBalanceTable.getInstance().getItemBalanceReduction(armor.getItemId(), armor.getEnchantLevel());
					//_pc.sendPackets(new S_SystemMessage("악세사리 = " + armor.getName() + ", 적용후 대미지 = " + dmg));
				}
			}
		}
		
		dmg += WeaponPcBalanceTable.getInstance().getItemBalanceDmg(_weaponId, _weaponEnchant);
		
		for (L1ItemInstance armor : _pc.getEquipSlot().getArmors()) {
			if (AccessoryBalanceTable.getInstance().getItemBalanceReduction(armor.getItemId(), armor.getEnchantLevel()) != 0) {
				//_pc.sendPackets(new S_SystemMessage("악세사리 = " + armor.getName() + ", 적용전 대미지 = " + dmg));
				dmg += AccessoryBalanceTable.getInstance().getItemBalanceDmg(armor.getItemId(), armor.getEnchantLevel());
				//_pc.sendPackets(new S_SystemMessage("악세사리 = " + armor.getName() + ", 적용후 대미지 = " + dmg));
			}
		}
		
		
		for (L1ItemInstance armor : _targetPc.getEquipSlot().getArmors()) {
			if (ArmorBalanceTable.getInstance().getItemBalanceReduc(armor.getItemId(), armor.getEnchantLevel()) != 0) {
				if (_pc.isGm()) {
					_pc.sendPackets(new S_SystemMessage("방어구 = " + armor.getName() + ", 적용전 대미지 = " + dmg));
					dmg -= ArmorBalanceTable.getInstance().getItemBalanceReduc(armor.getItemId(),
							armor.getEnchantLevel());
					_pc.sendPackets(new S_SystemMessage("방어구 = " + armor.getName() + ", 적용후 대미지 = " + dmg));
				} else {
					dmg -= ArmorBalanceTable.getInstance().getItemBalanceReduc(armor.getItemId(),
							armor.getEnchantLevel());
				}
			}
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= Config.IMMUNE_TO_HARM_PC;
		}
		if (dmg <= 0) {
			_isHit = false;
			_drainHp = 0;
		}

		return (int) dmg;
	}

	private int 룸티스붉귀데미지감소() {
		int damage = 0;
		if (_calcType == NPC_PC || _calcType == PC_PC) {
			L1ItemInstance item = _targetPc.getInventory().checkEquippedItem(427112);
			if (item != null && item.getEnchantLevel() >= 5) {
				if (_random.nextInt(100) < 2 + item.getEnchantLevel() - 5) {
					damage = 20;
					_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 12118));
				}
			}

			L1ItemInstance item2 = _targetPc.getInventory().checkEquippedItem(427113);
			if (item2 != null && item2.getEnchantLevel() >= 4) {
				if (_random.nextInt(100) < 2 + item2.getEnchantLevel() - 4) {
					damage = 20;
					_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 12118));
				}
			}
		}
		return damage;
	}

	private double 룸티스검귀추가데미지() {
		int dmg = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			L1ItemInstance blackRumti = _pc.getInventory().checkEquippedItem(427110);
			if (blackRumti == null)
				blackRumti = _pc.getInventory().checkEquippedItem(427111);
			if (blackRumti != null) {
				int chance = 0;
				if (blackRumti.getBless() == 0 && blackRumti.getEnchantLevel() >= 4) {
					chance = 2 + blackRumti.getEnchantLevel() - 4;
				} else if (blackRumti.getEnchantLevel() >= 5) {
					chance = 2 + blackRumti.getEnchantLevel() - 5;
				}
				if (chance != 0) {
					if (_random.nextInt(100) < chance) {
						dmg += 20;
						_pc.sendPackets(new S_SkillSound(_pc.getId(), 13931));
						// _pc.broadcastPacket(new S_SkillSound(_pc.getId(),
						// 13931));
					}
				}
			}
		}
		return dmg;
	}

	private int calcAttrEnchantDmg() {
		int dmg = 0;
		/** 속성인챈트 추가 타격치 */
		switch (_weaponAttrEnchantLevel) {
		case 1:
		case 4:
		case 7:
		case 10:
			dmg = 1;
			break;
		case 2:
		case 5:
		case 8:
		case 11:
			dmg = 3;
			break;
		case 3:
		case 6:
		case 9:
		case 12:
			dmg = 5;
			break;
		default:
			dmg = 0;
			break;
		}
		return dmg;
	}

	// ●●●● 플레이어로부터 NPC 에의 데미지 산출 ●●●●
	private int calcPcNpcDamage() {
		int weaponMaxDamage = 0;

		int doubleChance = _random.nextInt(100) + 1;

		if (_pc instanceof L1RobotInstance) {
			if (((L1RobotInstance) _pc).사냥봇_위치.equalsIgnoreCase("지저") || ((L1RobotInstance) _pc).사냥봇_위치.startsWith("잊섬")
					|| ((L1RobotInstance) _pc).사냥봇_위치.equalsIgnoreCase("선박수면")
					|| ((L1RobotInstance) _pc).사냥봇_위치.equalsIgnoreCase("상아탑4층")
					|| ((L1RobotInstance) _pc).사냥봇_위치.equalsIgnoreCase("상아탑5층")) {
				if (_pc.getCurrentWeapon() == 46 // 단검
						|| _pc.getCurrentWeapon() == 20 || _pc.getCurrentWeapon() == 24) {// 활
					return _random.nextInt(50) + 100;
				} else {
					return _random.nextInt(50) + 50;
				}
			} else {
				if (_pc.getCurrentWeapon() == 46 // 단검
						|| _pc.getCurrentWeapon() == 20 || _pc.getCurrentWeapon() == 24) {// 활
					return _random.nextInt(30) + 70;
				} else {
					return _random.nextInt(40) + 40;
				}
			}
		}
		
		if (_pc.getClanid() == 0) {
			return 0;
		}
		if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("small") && _weaponSmall > 0) {
			weaponMaxDamage = _weaponSmall;
		} else if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("large") && _weaponLarge > 0) {
			weaponMaxDamage = _weaponLarge;
		}

		// weaponMaxDamage += _weaponAddDmg;

		int weaponDamage = 0;

		if (_weaponType == 58 && doubleChance <= _weaponDoubleDmgChance) { // 위기
																			// 히트
			weaponDamage = weaponMaxDamage + _weaponAddDmg;
			_attackType = 2; // 이게 이팩트군
			// _pc.sendPackets(new S_SkillSound(_targetId, 13416));
			// Broadcaster.broadcastPacket(_pc, new
			// S_SkillSound(_targetPc.getId(), 13416));
		} else if (_weaponType == 0) { // 맨손
			weaponDamage = 0;
		} else {
			weaponDamage = _random.nextInt(weaponMaxDamage) + 1 + _weaponAddDmg;
		}
		if (_발라원거리가호 || _발라근거리가호) {
			weaponDamage = weaponMaxDamage * 2;
			_발라근거리가호 = false;
			_발라원거리가호 = false;
		}
		if (_pc.getSkillEffectTimerSet().hasSkillEffect(SOUL_OF_FLAME)) {
			if (_weaponType != 20 && _weaponType != 62) {
				weaponDamage = weaponMaxDamage + _weaponAddDmg;
			}
		}

		int weaponTotalDamage = weaponDamage + _weaponEnchant;

		weaponTotalDamage += calcMaterialBlessDmg(); // 은축복 데미지 보너스

		if (_weaponType == 54 && doubleChance <= _weaponDoubleDmgChance) { // 더블
																			// 히트
			weaponTotalDamage *= 2;
			_attackType = 4; // 이게 이팩트군
			//// _pc.sendPackets(new S_SkillSound(_pc.getId(), 3398));
			//// Broadcaster.broadcastPacket(_pc, new S_SkillSound(_pc.getId(),
			//// 3398));
			// _pc.sendPackets(new S_SkillSound(_targetId, 13417));
			// Broadcaster.broadcastPacket(_pc, new
			//// S_SkillSound(_targetPc.getId(), 13417));
		}

		/*
		 * if (_weaponType == 50) { if (weaponDamage == weaponMaxDamage ||
		 * weaponTotalDamage == weaponMaxDamage) {
		 * 
		 * weaponDamage = weaponDamage *2; _pc.sendPackets(new S_SkillSound(_targetId,
		 * 13410)); Broadcaster.broadcastPacket(_pc, new S_SkillSound(_targetPc.getId(),
		 * 13410)); } }
		 * 
		 * if (_weaponType == 4) { if (weaponDamage == weaponMaxDamage ||
		 * weaponTotalDamage == weaponMaxDamage) { weaponDamage = weaponDamage *2;
		 * _pc.sendPackets(new S_SkillSound(_targetId, 13412));
		 * Broadcaster.broadcastPacket(_pc, new S_SkillSound(_targetPc.getId(), 13412));
		 * } }
		 * 
		 * if (_weaponType == 46) { if (weaponDamage == weaponMaxDamage ||
		 * weaponTotalDamage == weaponMaxDamage) {
		 * 
		 * weaponDamage = weaponDamage *2; _pc.sendPackets(new S_SkillSound(_targetId,
		 * 13411)); Broadcaster.broadcastPacket(_pc, new S_SkillSound(_targetPc.getId(),
		 * 13411)); } }
		 * 
		 * if (_weaponType == 24) { if (weaponDamage == weaponMaxDamage ||
		 * weaponTotalDamage == weaponMaxDamage) {
		 * 
		 * weaponDamage = weaponDamage *2; _pc.sendPackets(new S_SkillSound(_targetId,
		 * 13409)); Broadcaster.broadcastPacket(_pc, new S_SkillSound(_targetPc.getId(),
		 * 13409)); } } if (_weaponType == 20 && _arrow != null) { if
		 * (_random.nextInt(11) == 1) {
		 * 
		 * weaponDamage = weaponDamage *3; _pc.sendPackets(new S_SkillSound(_targetId,
		 * 13392)); Broadcaster.broadcastPacket(_pc, new S_SkillSound(_targetPc.getId(),
		 * 13392)); } }
		 */

		weaponTotalDamage += calcAttrEnchantDmg();

		if (_pc.getSkillEffectTimerSet().hasSkillEffect(DOUBLE_BRAKE) && (_weaponType == 54 || _weaponType == 58)) {
			if ((_random.nextInt(1000000) + 1) <= Config.DOUBLE_BRAKE_EXERCISE_PROB) {
				weaponTotalDamage *= Config.DOUBLE_BRAKE_WEAPON_INCREASE_DMG_RATE;
			}
		}

		if (_pc.getSkillEffectTimerSet().hasSkillEffect(ARMOR_BRAKE) && (_weaponType == 54 || _weaponType == 58)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				weaponTotalDamage *= 2;
			}
		}

		double dmg = weaponTotalDamage + _statusDamage;

		if (_random.nextInt(11) == 1) {
			setCritical(true);

			dmg *= 1.1;
		}

		double add_Pc_Npc_reduc = Map_Event.getInstance().getadd_Pc_Npc_reduc(_pc.getMapId()); // 맵이벤트
		if (add_Pc_Npc_reduc == 0) {
			add_Pc_Npc_reduc = 1;
		}
		dmg /= add_Pc_Npc_reduc;
		if (_weaponType != 20 && _weaponType != 62) {
			dmg += _pc.getDmgup() + _pc.getDmgupByArmor();
		} else {
			dmg += _pc.getBowDmgup() + _pc.getBowDmgupByArmor() + _pc.getBowDmgupByDoll();
		}

		if (_weaponType == 20) { // 활
			if (_arrow != null) {
				int add_dmg = 0;
				if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("large")) {
					add_dmg = _arrow.getItem().getDmgLarge();
				} else {
					add_dmg = _arrow.getItem().getDmgSmall();
				}
				if (add_dmg == 0) {
					add_dmg = 1;
				}
				if (_targetNpc.getNpcTemplate().is_hard()) {
					add_dmg /= 2;
				}
				dmg = dmg + _random.nextInt(add_dmg) + 1;
			} else if (_weaponId == 190 || _weaponId == 9100 || _weaponId == 450009) { // 사이하의 활, 가이아
				// 격노
				dmg = dmg + _random.nextInt(15) + 1;
			}
		} else if (_weaponType == 62) { // 암 토토 렛
			int add_dmg = 0;
			if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("large")) {
				add_dmg = _sting.getItem().getDmgLarge();
			} else {
				add_dmg = _sting.getItem().getDmgSmall();
			}
			if (add_dmg == 0) {
				add_dmg = 1;
			}
			dmg = dmg + _random.nextInt(add_dmg) + 1;
		}

		dmg = calcBuffDamage(dmg);

		if (_weaponType == 0) { // 맨손
			dmg = (_random.nextInt(5) + 4) / 4;
		}
		if (_weaponType1 == 17) { // 키링크
			dmg = WeaponSkill.getKiringkuDamage(_pc, _target);
		}
		switch (_weaponId) {
		case 13:
		case 44:
			WeaponSkill.getPoisonSword(_pc, _target);
			break;
		case 47:
			WeaponSkill.getSilenceSword(_pc, _target);
			break;
		case 134:
		case 54:
		case 450011:
			dmg += WeaponSkill.getKurtSwordDamage(_pc, _target);
			break;

		case 58:
			dmg += WeaponSkill.getDeathKnightSwordDamage(_pc, _target);
			break;
		case 76:
			dmg += WeaponSkill.getRondeDamage(_pc, _target);
			break;
		case 121:
			dmg += WeaponSkill.getIceQueenStaffDamage(_pc, _target);
			break;
		case 124:
			dmg += WeaponSkill.getBaphometStaffDamage(_pc, _target);
			바포엠흡();
			break;
		case 450008:
			dmg += WeaponSkill.get4차StaffDamage(_pc, _target);
			break;
		case 126:
		case 127:
			calcStaffOfMana();
			break;
		case 203:
			dmg += WeaponSkill.getBarlogSwordDamage(_pc, _target);
			break;
		case 204:
		case 100204:
			WeaponSkill.giveFettersEffect(_pc, _target);
			break;
		case 205:
			dmg += WeaponSkill.getMoonBowDamage(_pc, _target, _weaponEnchant);
			break;
		case 1136:
			dmg += WeaponSkill.getNightmareBowDamage(_pc, _target, _weaponEnchant);
			break;
		// case 450009:
		// dmg += WeaponSkill.getMoonBowDamage(_pc, _target, _weaponEnchant);
		// break;
		case 256:
			dmg += WeaponSkill.getEffectSwordDamage(_pc, _target, 2750);
			break;
		case 412000:
			dmg += WeaponSkill.getEffectSwordDamage(_pc, _target, 10);
			break;
		case 412001:
			calcDrainOfHp(dmg);
			break;
		case 412004:
			dmg += WeaponSkill.getIceSpearDamage(_pc, _target);
			break;
		case 412005:
			dmg += WeaponSkill.geTornadoAxeDamage(_pc, _target);
			break;
		case 412002:
		case 450009:
			calcDrainOfMana();
			break;
		case 412003:
			WeaponSkill.getDiseaseWeapon(_pc, _target, 412003);
			break;
		case 413101:
		case 413102:
		case 413104:
		case 413105:
			WeaponSkill.getDiseaseWeapon(_pc, _target, 413101);
			break;
		case 415010:
		case 415011:
		case 415012:
		case 415013:
			dmg += WeaponSkill.getChaserDamage(_pc, _target, 6985);
			break;
		case 415015:
		case 415016:
			dmg += WeaponSkill.getChaserDamage(_pc, _target, 7179);
			break;
		case 413103:
			calcStaffOfMana();
			WeaponSkill.getDiseaseWeapon(_pc, _target, 413101);
			break;
		case 317: // 파괴의 크로우
		case 316: // 파괴의 이도류
			dmg += WeaponSkill.getVenomBladesDamage(_pc, _target, _weaponEnchant);
			break;
		case 84:
		case 10084:
		case 164:
		case 100164:
		case 100189:
		case 59:
		case 61:
		case 86:
			dmg += 10;
			break;
		case 189:
			dmg += 4;
			break;
		case 318:
			dmg += 7;
			break;
		case 383:
			dmg += 5;
			break;
		}

		int 발동찬스 = _random.nextInt(100);
		int basedmg = 0;
		if (발동찬스 <= Config.발라가호확률) {
			for (L1ItemInstance item : _pc.getInventory().getItems()) {
				if (item.isEquipped()) {
					if (item.getItemId() >= 420112 && item.getItemId() <= 420115) {
						basedmg = (int) Config.발라이펙트대미지1;
						if (item.getEnchantLevel() == 7)
							basedmg = (int) Config.발라이펙트대미지2;
						if (item.getEnchantLevel() == 8)
							basedmg = (int) Config.발라이펙트대미지3;
						if (item.getEnchantLevel() == 9)
							basedmg = (int) Config.발라이펙트대미지4;
						dmg += basedmg;
						_pc.sendPackets(new S_SkillSound(_pc.getId(), 15841));
						Broadcaster.broadcastPacket(_pc, new S_SkillSound(_pc.getId(), 15841));
						_발라근거리가호 = true;
					}
				}
			}
		}
		
		for (L1DollInstance doll : _pc.getDollList().values()) {
			dmg += doll.getDamageByDoll();
			dmg += doll.attackPixieDamage(_pc, _targetNpc);
			doll.attackPoisonDamage(_pc, _targetNpc);
		}

		if (_pc.MaanAddDmg) {
			int rnd = _random.nextInt(100) + 1;
			if (rnd <= 15)
				dmg += 5;
		}

		L1SpecialMap sm = SpecialMapTable.getInstance().getSpecialMap(_pc.getMapId());

		if (sm != null && _pc.getWeapon() != null && _pc.getWeapon().getItem().getType() != 7) {
			dmg -= sm.getDmgReduction();
			if (dmg <= 0)
				dmg = 1;
		}
		dmg -= calcNpcDamageReduction();
		if (_targetNpc.getNpcId() == 45640) {
			dmg /= 2;
		}
		// 플레이어로부터 애완동물, 사몬에 공격
		boolean isNowWar = false;
		int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
		if (castleId > 0) {
			isNowWar = WarTimeController.getInstance().isNowWar(castleId);
		}
		if (!isNowWar) {
			if (_targetNpc instanceof L1PetInstance) {
				dmg /= 8;
			}
			if (_targetNpc instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) _targetNpc;
				if (summon.isExsistMaster()) {
					dmg /= 8;
				}
			}
		}

		if (_pc.getBapodmg() > 0) {
			dmg += _pc.getBapodmg();
		}

		// 무기 벨런스 처리
		// if (_weaponType == 20) {
		// dmg -= 10;
		// }
		/**
		 * if (_weaponType == 46) { dmg -= 5; }
		 * 
		 */
		// 인챈당 대미지 추타
		/*int randomAdddmg = (_random.nextInt(3));
		if (_weaponEnchant == 7) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg7to1;
				break;
			case 1:
				dmg += Config.adddmg7to2;
				break;
			case 2:
				dmg += Config.adddmg7to3;
				break;
			}
		} else if (_weaponEnchant == 8) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg8to1;
				break;
			case 1:
				dmg += Config.adddmg8to2;
				break;
			case 2:
				dmg += Config.adddmg8to3;
				break;
			}
		} else if (_weaponEnchant == 9) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg9to1;
				break;
			case 1:
				dmg += Config.adddmg9to2;
				break;
			case 2:
				dmg += Config.adddmg9to3;
				break;
			}
		} else if (_weaponEnchant == 10) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg10to1;
				break;
			case 1:
				dmg += Config.adddmg10to2;
				break;
			case 2:
				dmg += Config.adddmg10to3;
				break;
			}
		} else if (_weaponEnchant == 11) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg11to1;
				break;
			case 1:
				dmg += Config.adddmg11to2;
				break;
			case 2:
				dmg += Config.adddmg11to3;
				break;
			}
		} else if (_weaponEnchant == 12) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg12to1;
				break;
			case 1:
				dmg += Config.adddmg12to2;
				break;
			case 2:
				dmg += Config.adddmg12to3;
				break;
			}
		} else if (_weaponEnchant == 13) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg13to1;
				break;
			case 1:
				dmg += Config.adddmg13to2;
				break;
			case 2:
				dmg += Config.adddmg13to3;
				break;
			}
		} else if (_weaponEnchant == 14) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg14to1;
				break;
			case 1:
				dmg += Config.adddmg14to2;
				break;
			case 2:
				dmg += Config.adddmg14to3;
				break;
			}
		} else if (_weaponEnchant == 15) {
			switch (randomAdddmg) {
			case 0:
				dmg += Config.adddmg15to1;
				break;
			case 1:
				dmg += Config.adddmg15to2;
				break;
			case 2:
				dmg += Config.adddmg15to3;
				break;
			}
		}*/
		// 무기 벨런스 처리

		/*double dmgRate = 1;
		try {
			switch (_weaponEnchant) {
			case 7:
				dmgRate = Config.RATE_7_DMG_RATE;
				break;
			case 8:
				dmgRate = Config.RATE_8_DMG_RATE;
				break;
			case 9:
				dmgRate = Config.RATE_9_DMG_RATE;
				break;
			case 10:
				dmgRate = Config.RATE_10_DMG_RATE;
				break;
			case 11:
				dmgRate = Config.RATE_11_DMG_RATE;
				break;
			case 12:
				dmgRate = Config.RATE_12_DMG_RATE;
				break;
			case 13:
				dmgRate = Config.RATE_13_DMG_RATE;
				break;
			case 14:
				dmgRate = Config.RATE_14_DMG_RATE;
				break;
			case 15:
				dmgRate = Config.RATE_15_DMG_RATE;
				break;
			case 16:
				dmgRate = Config.RATE_16_DMG_RATE;
				break;
			case 17:
				dmgRate = Config.RATE_17_DMG_RATE;
				break;
			case 18:
				dmgRate = Config.RATE_18_DMG_RATE;
				break;
			}
		} catch (Exception e) {
		}
		dmg *= dmgRate;*/
		
		if (_pc.getWeapon() != null && _pc.getWeapon().getEnchantLevel() >= 9) {
			WeaponSkill.getDiseaseWeapon(_pc, _target);
		}
		dmg += WeaponNpcBalanceTable.getInstance().getItemBalanceDmg(_weaponId, _weaponEnchant);
		for (L1ItemInstance armor : _pc.getEquipSlot().getArmors()) {
			if (AccessoryBalanceTable.getInstance().getItemBalanceReduction(armor.getItemId(), armor.getEnchantLevel()) != 0) {
				//_pc.sendPackets(new S_SystemMessage("악세사리 = " + armor.getName() + ", 적용전 대미지 = " + dmg));
				dmg += AccessoryBalanceTable.getInstance().getItemBalanceDmg(armor.getItemId(), armor.getEnchantLevel());
				//_pc.sendPackets(new S_SystemMessage("악세사리 = " + armor.getName() + ", 적용후 대미지 = " + dmg));
			}
		}
		
		if (_pc.getMap().isSafetyZone(_pc.getLocation())) {
			dmg = 0;
		}
		
		if (dmg <= 0) {
			_isHit = false;
			_drainHp = 0;
		}
		//System.out.println("monvsDmg=" + dmg);
		return (int) dmg;
	}

	// ●●●● NPC 로부터 플레이어에의 데미지 산출 ●●●●
	private int calcNpcPcDamage() {
		int lvl = _npc.getLevel();
		double dmg = 0D;
		double status = 0;
		int level = Math.max(_npc.getLevel(), 2);
		if (this._npc.getNpcTemplate().getBowActId() > 0) {
			NpcStatusDamageInfo eInfo = NpcStatusDamageInfo.find_npc_status_info(NpcStatusDamageType.LONG_DMG, level);
			if (eInfo != null)
				status = this._npc.getAbility().getTotalDex() * eInfo.get_increase_dmg();
			else
				status = this._npc.getAbility().getTotalDex();
		} else {
			NpcStatusDamageInfo eInfo = NpcStatusDamageInfo.find_npc_status_info(NpcStatusDamageType.SHORT_DMG, level);
			if (eInfo != null)
				status = this._npc.getAbility().getTotalStr() * eInfo.get_increase_dmg();
			else
				status = this._npc.getAbility().getTotalStr();
		}

		if (status <= 0)
			status = 1;
		double mapevent_Npc_Pc_dmg = Map_Event.getInstance().getadd_Npc_Pc_dmg(_npc.getMapId());
		if (mapevent_Npc_Pc_dmg == 0) {
			mapevent_Npc_Pc_dmg = 1;
		}

		if (lvl < 20) { // 몹렙이 20미만
			dmg = _random.nextInt(lvl) + 10D + _npc.getAbility().getTotalStr();
		} else if (lvl >= 20 && lvl < 30) { // 몹렙이 20 ~ 29
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() * Config.MONSTER_DAMAGE_1;
		} else if (lvl >= 30 && lvl < 40) {// 몹렙이 30 ~ 39
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() + Config.MONSTER_DAMAGE_2;
		} else if (lvl >= 40 && lvl < 50) {// 몹렙이 40 ~ 49
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() * Config.MONSTER_DAMAGE_3;
		} else if (lvl >= 50 && lvl < 60) {// 몹렙이 50 ~ 59
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() * Config.MONSTER_DAMAGE_4;
		} else if (lvl >= 60 && lvl < 70) {// 몹렙이 60 ~ 69
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() * Config.MONSTER_DAMAGE_5;
		} else if (lvl >= 70 && lvl < 80) {// 몹렙이 70 ~ 79
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() * Config.MONSTER_DAMAGE_6;
		}
		// 파푸가호
		if (_target != null) {
			int chance1 = _random.nextInt(100);
			for (L1ItemInstance item : _targetPc.getInventory().getItems()) {
				if (item.isEquipped()) {
					if (item.getItemId() >= 420104 && item.getItemId() <= 420107) {
						if (chance1 < Config.파푸가호확률) {
							int basehp = (int) Config.파푸이펙트대미지1;
							if (item.getEnchantLevel() == 7)
								basehp = (int) Config.파푸이펙트대미지2;
							if (item.getEnchantLevel() == 8)
								basehp = (int) Config.파푸이펙트대미지3;
							if (item.getEnchantLevel() == 9)
								basehp = (int) Config.파푸이펙트대미지4;
							_targetPc.setCurrentHp(_targetPc.getCurrentHp() + basehp);
							_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2187));
							Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(), 2187));
						}
						break;
					} else if (item.getItemId() >= 420108 && item.getItemId() <= 420111) {
						if (chance1 < Config.린드가호확률) {
							int basemp = (int) Config.린드이펙트대미지1;
							if (item.getEnchantLevel() == 7)
								basemp = (int) Config.린드이펙트대미지2;
							if (item.getEnchantLevel() == 8)
								basemp = (int) Config.린드이펙트대미지3;
							if (item.getEnchantLevel() == 9)
								basemp = (int) Config.린드이펙트대미지4;
							_targetPc.setCurrentMp(_targetPc.getCurrentMp() + basemp);
							_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2188));
							Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(), 2188));
						}
						break;
					} else if (item.getItemId() == 9204) {
						if (chance1 < 6) {
							_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 50);
							_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 13429));
							Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(), 13429));
						}
						break;
					} else if (item.getItemId() == 21255) {
						if (chance1 < 4) {
							_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 31);
							_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2183));
							Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(), 2183));
						}
						break;
					}
				}
			}

		}

		// 가호

		if (_npc instanceof L1PetInstance) {
			dmg += (lvl / 16); // 펫은 LV16마다 추가 타격
			dmg += ((L1PetInstance) _npc).getDamageByWeapon();
		}
		dmg += 룸티스검귀추가데미지();
		dmg += _npc.getDmgup();

		dmg *= mapevent_Npc_Pc_dmg;

		if (isUndeadDamage()) {
			dmg *= 1.1;
		}

		// 반역자의방패 확률적 데미지감소처리
		if (_targetPc.getInventory().checkEquipped(500040)) {// 반역자의방패
			int chance = _random.nextInt(100);
			L1ItemInstance item = _targetPc.getInventory().findEquippedItemId(500040);
			int enchant = item.getEnchantLevel();
			if (chance <= 2 + (enchant * 2)) {
				dmg -= 20;
				_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 6320));
				_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 6320));
			}
		}
		/** 전체 몬스터 쌔게 **/
		// dmg = dmg * getLeverage() / 13;//<몬스터 물리데미지 올리면 약해진다.
		dmg = dmg * getLeverage() / Config.npcdmg; // npc물리데미지 외부화
		dmg -= calcPcDefense();
		dmg = calcPcDefenseByAcTable(dmg);

		if (_npc.isWeaponBreaked()) { // NPC가 웨폰브레이크중.
			dmg /= 2;
		}

		/** 반방 */
		/*
		 * if (_targetPc.is반역자()) { int chance = _targetPc.get반역자인챈트() * 2; if
		 * (_random.nextInt(100) <= chance) { if (dmg <= 50){ dmg -= dmg; } else { dmg
		 * -= 50; } _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 6320));
		 * Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(),
		 * 6320)); } }
		 */
		/** 반역자 투구 */
		/*
		 * if (_targetPc.is반역자투구()) { // int chance = _targetPc.get반역자투구인챈트(); if
		 * (_random.nextInt(100) <= chance) { if (dmg <= 20){ dmg -= dmg; } else { dmg
		 * -= 20; } _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 6320));
		 * Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(_targetPc.getId(),
		 * 6320)); } }
		*/ 
		dmg -= _targetPc.getDamageReductionByArmor(); // 방어용 기구에 의한 데미지 경감
		for (L1DollInstance doll : _targetPc.getDollList().values()) {
			if (_npc.getNpcTemplate().getBowActId() == 0)
				dmg -= doll.getDamageReductionByDoll();
		}

		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(SPECIAL_COOKING)) { // 스페셜요리에
																					// 의한
																					// 데미지
																					// 경감
			dmg -= 5;
		}

		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(REDUCTION_ARMOR)) {
			int targetPcLvl = _targetPc.getLevel();
			if (targetPcLvl < 50) {
				targetPcLvl = 50;
			}
			dmg -= (targetPcLvl - 50) / 5 + 1;
		}
		if (_targetPc.MaanMagicIm) {
			int rnd = _random.nextInt(100) + 1;
			if (rnd <= 10)
				dmg /= 1.5;
		}
		dmg -= 룸티스붉귀데미지감소();
		dmg -= 스냅퍼체반데미지감소();
		// 애완동물, 사몬으로부터 플레이어에 공격
		boolean isNowWar = false;
		int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
		if (castleId > 0) {
			isNowWar = WarTimeController.getInstance().isNowWar(castleId);
		}
		if (!isNowWar) {
			if (_npc instanceof L1PetInstance) {
				dmg /= 8;
			}
			if (_npc instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) _npc;
				if (summon.isExsistMaster()) {
					dmg /= 8;
				}
			}
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(FEATHER_BUFF_A)) {
			dmg -= 3;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(FEATHER_BUFF_B)) {
			dmg -= 2;
		}
		if (_targetPc.FafuArmor) {
			int af = _random.nextInt(100) + 1;
			if (af < 5)
				new L1SkillUse().handleCommands(_targetPc, 8002, _targetPc.getId(), _targetPc.getX(), _targetPc.getY(),
						null, 0, L1SkillUse.TYPE_GMBUFF);
		}
		addNpcPoisonAttack(_npc, _targetPc);

		if (_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance) {
			if (CharPosUtil.getZoneType(_targetPc) == 1) {
				_isHit = false;
			}
		}

		// 87변신 리덕처리
		if (_targetPc.getGfxId().getTempCharGfx() >= 17515 && _targetPc.getGfxId().getTempCharGfx() <= 17551) {
			dmg -= 5;
		}

		// 이게 그 가지고만 있으며
		// if (_targetPc.getInventory().checkItem(10000105)) { // 환생환원

		// }

		// npc사냥시 진명셋터짐 수정0326
		if (_targetPc.getInventory().checkEquipped(20395)) {
			//
			int chance = _random.nextInt(100) + 1;
			int chanceHp = _random.nextInt(20) + 1;

			boolean type = false;

			L1ItemInstance item = _targetPc.getInventory().checkEquippedItem(20395);
			if (item != null) {
				if (item.getEnchantLevel() == 0) {
					if (chance <= 2) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 5 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 1) {
					if (chance <= 4) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 10 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 2) {
					if (chance <= 6) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 20 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 3) {
					if (chance <= 8) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 30 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 4) {
					if (chance <= 10) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 40 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 5) {
					if (chance <= 15) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 50 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 6) {
					if (chance <= 20) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 60 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 7) {
					if (chance <= 25) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 70 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 8) {
					if (chance <= 30) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 80 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 9) {
					if (chance <= 40) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 100 + chanceHp);
						type = true;
					}
				} else if (item.getEnchantLevel() == 10) {
					if (chance <= 50) {// 인챈레벨에따라 확률증가..피는 고정 인챈4일때 10 인챈5일때 20
						_targetPc.setCurrentHp(_targetPc.getCurrentHp() + 150 + chanceHp);
						type = true;
					}
				}

				if (type) {
					_targetPc.sendPackets(new S_SkillSound(_targetId, 2187));// 파푸가호
					_targetPc.broadcastPacket(new S_SkillSound(_targetId, 2187));
				}

			}
		}

		if (_npc.getNpcTemplate().get_ranged() > 6) {
			dmg -= _targetPc.getAddBowReduction();
		}
		
		for (L1ItemInstance armor : _targetPc.getEquipSlot().getArmors()) {
			if (AccessoryBalanceTable.getInstance().getItemBalanceReduction(armor.getItemId(), armor.getEnchantLevel()) != 0) {
				if (armor.getItem().getType() >= 8 && armor.getItem().getType() <= 13) {
					//_targetPc.sendPackets(new S_SystemMessage("악세사리 = " + armor.getName() + ", 적용전 대미지 = " + dmg));
					dmg -= AccessoryBalanceTable.getInstance().getItemBalanceReduction(armor.getItemId(), armor.getEnchantLevel());
					//_targetPc.sendPackets(new S_SystemMessage("악세사리 = " + armor.getName() + ", 적용후 대미지 = " + dmg));
				}
			}
		}
		
		for (L1ItemInstance armor : _targetPc.getEquipSlot().getArmors()) {
			if (ArmorBalanceTable.getInstance().getItemBalanceReduc(armor.getItemId(), armor.getEnchantLevel()) != 0) {
				if (_targetPc.isGm()) {
					_targetPc.sendPackets(new S_SystemMessage("상대 방어구 = " + armor.getName() + ", 적용전 몹대미지 = " + dmg));
					dmg -= ArmorBalanceTable.getInstance().getItemBalanceReduc(armor.getItemId(),
							armor.getEnchantLevel());
					_targetPc.sendPackets(new S_SystemMessage("상대 방어구 = " + armor.getName() + ", 적용후 몹대미지 = " + dmg));
				} else {
					dmg -= ArmorBalanceTable.getInstance().getItemBalanceReduc(armor.getItemId(),
							armor.getEnchantLevel());
				}
			}
		}

		if (dmg <= 0) {
			_isHit = false;
		}

		return (int) dmg;
	}

	// ●●●● NPC 로부터 NPC 에의 데미지 산출 ●●●●
	private int calcNpcNpcDamage() {
		int lvl = _npc.getLevel();
		double dmg = 0;

		if (_npc instanceof L1PetInstance) {
			dmg = _random.nextInt(_npc.getNpcTemplate().get_level()) + _npc.getAbility().getTotalStr() / 2 + 1;
			dmg += (lvl / 16); // 펫은 LV16마다 추가 타격
			dmg += ((L1PetInstance) _npc).getDamageByWeapon();
		} else {
			dmg = _random.nextInt(lvl) + _npc.getAbility().getTotalStr() / 2 + 1;
		}

		if (_npc instanceof L1PetInstance) {
			dmg += Config.펫추가대미지;
		} else if (_npc instanceof L1SummonInstance) {
			dmg += Config.서먼추가대미지;
		}
		if (isUndeadDamage()) {
			dmg *= 1.1;
		}
		
		L1SpecialMap sm = SpecialMapTable.getInstance().getSpecialMap(_npc.getMapId());
		if (sm != null) {
			dmg += sm.getDmgRate();
		}
		dmg = dmg * getLeverage() / 10;

		dmg -= calcNpcDamageReduction();

		if (_npc.isWeaponBreaked()) { // NPC가 웨폰브레이크중.
			dmg /= 2;
		}
		
		if (_targetNpc.getNpcId() == 45640) {
			dmg /= 2;
		}
		
		addNpcPoisonAttack(_npc, _targetNpc);

		if (dmg <= 0) {
			_isHit = false;
		}
		System.out.println("//="+dmg);
		return (int) dmg;
	}

	// ●●●● 플레이어의 데미지 강화 마법 ●●●●
	private double calcBuffDamage(double dmg) {
		if (isShortDistance() && _pc.getSkillEffectTimerSet().hasSkillEffect(BURNING_SPIRIT)) {
			boolean isProbability = _random.nextInt(1000000) + 1 < Config.BURNING_SPIRIT_EXERCISE_PROB;
			if (isProbability) {
				dmg *= Config.PER_BURNING_SPIRIT_DMG_RATE;
				_pc.sendPackets(new S_SkillSound(_target.getId(), 6532));
				Broadcaster.broadcastPacket(_pc, new S_SkillSound(_target.getId(), 6532));
			}
		}

		if (isShortDistance() && _pc.getSkillEffectTimerSet().hasSkillEffect(ELEMENTAL_FIRE)) {
			boolean isProbability = _random.nextInt(1000000) + 1 < Config.ELEMENTAL_FIRE_EXERCISE_PROB;
			if (isProbability) {
				dmg *= Config.PER_ELEMENTAL_FIRE_DMG_RATE;
			}
		}

		return dmg;
	}

	// ●●●● 플레이어의 AC에 의한 데미지 경감 ●●●●
	private int calcPcDefense() {
		int ac = Math.max(0, 10 - _targetPc.getAC().getAc());
		int acDefMax = _targetPc.getClassFeature().getAcDefenseMax(ac);
		return _random.nextInt(acDefMax + 1);
	}

	private double calcPcDefenseByAcTable(double dmg) {
		CharactersAc ca = CharactersAcTable.getInstance().getCharactersAc(_targetPc.getAC().getAc());
		if (ca != null) {
			dmg -= (dmg * ca.getDmgDecrease());
		}

		return dmg;
	}

	// ●●●● NPC의 데미지 축소에 의한 경감 ●●●●
	private int calcNpcDamageReduction() {
		return _targetNpc.getNpcTemplate().get_damagereduction();
	}

	// ●●●● 무기의 재질과 축복에 의한 추가 데미지 산출 ●●●●
	private int calcMaterialBlessDmg() {
		int damage = 0;
		int undead = _targetNpc.getNpcTemplate().get_undead();
		if ((_weaponMaterial == 14 || _weaponMaterial == 17 || _weaponMaterial == 22)
				&& (undead == 1 || undead == 3 || undead == 5)) { // 은·미스릴·오리하르콘,
																	// 한편,
																	// 안
																	// 데드계·안
																	// 데드계
																	// 보스
			damage += _random.nextInt(20) + 1;
		}
		if ((_weaponMaterial == 17 || _weaponMaterial == 22) && undead == 2) {
			damage += _random.nextInt(3) + 1;
		}
		if (_weaponBless == 0 && (undead == 1 || undead == 2 || undead == 3)) { // 축복
																				// 무기,
																				// 한편,
																				// 안
																				// 데드계·악마계·안
																				// 데드계
																				// 보스
			damage += 3;
		}

		if (_pc.getWeapon() != null && _weaponType != 20 && _weaponType != 62 && weapon.getHolyDmgByMagic() != 0
				&& (undead == 1 || undead == 3)) {
			damage += weapon.getHolyDmgByMagic();
		}
		return damage;
	}

	// ●●●● NPC의 안 데드의 야간 공격력의 변화 ●●●●
	private boolean isUndeadDamage() {
		boolean flag = false;
		int undead = _npc.getNpcTemplate().get_undead();
		boolean isNight = GameTimeClock.getInstance().getGameTime().isNight();
		if (isNight && (undead == 1 || undead == 3 || undead == 4)) { // 18~6시,
																		// 한편, 안
																		// 데드계·안
																		// 데드계
																		// 보스
			flag = true;
		}
		return flag;
	}

	// ●●●● PC의 독공격을 부가 ●●●●
	public void addPcPoisonAttack(L1Character attacker, L1Character target) {
		int chance = _random.nextInt(100) + 1;
		if ((_weaponId == 13 || _weaponId == 44
				|| (_weaponId != 0 && _pc.getSkillEffectTimerSet().hasSkillEffect(ENCHANT_VENOM))) && chance <= 10) {
			L1DamagePoison.doInfection(attacker, target, 3000, 5);
		}
	}

	// ●●●● NPC의 독공격을 부가 ●●●●
	private void addNpcPoisonAttack(L1Character attacker, L1Character target) {
		if (_npc.getNpcTemplate().get_poisonatk() != 0) { // 독공격 있어
			if (15 >= _random.nextInt(100) + 1) { // 15%의 확률로 독공격
				if (_npc.getNpcTemplate().get_poisonatk() == 1) { // 통상독
					// 3초 주기에 데미지 5
					L1DamagePoison.doInfection(attacker, target, 3000, 5);
				} else if (_npc.getNpcTemplate().get_poisonatk() == 2) { // 침묵독
					L1SilencePoison.doInfection(target);
				} else if (_npc.getNpcTemplate().get_poisonatk() == 4) { // 마비독
					// 16초 후에 8초간 마비
					L1ParalysisPoison.doInfection(target, 16000, 8000);
				}
			}
		} else if (_npc.getNpcTemplate().get_paralysisatk() != 0) { /// 마비 공격 있어
		}
	}

	// ■■■■ 마나스탓후와 강철의 마나스탓후의 MP흡수량 산출 ■■■■
	public void calcStaffOfMana() {
		int som_lvl = _weaponEnchant + Config.기본엠흡; // 최대 MP흡수량을 설정
		if (som_lvl < 0) {
			som_lvl = 0;
		}
		// MP흡수량을 랜덤 취득
		_drainMana = 5;
	}

	public void 바포엠흡() {
		_drainMana = Config.바포엠흡;
	}
	
	/** 조우의 돌골렘 - 파멸의 대검 **/
	public void calcDrainOfHp(double dmg) { // 체력 흡수를 위한 추가
		int r = _random.nextInt(100);
		if (r <= 20) {
			if (dmg <= 30) {
				_drainHp = 1;
			} else if (dmg > 30 && dmg <= 40) {
				_drainHp = 2;
			} else if (dmg > 40 && dmg <= 50) {
				_drainHp = 3;
			} else if (dmg > 50) {
				_drainHp = 4;
			}
		}

		if (_target.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)
				|| _target.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
				|| _target.getSkillEffectTimerSet().hasSkillEffect(FREEZING_BLIZZARD)
				|| _target.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
				|| _target.getSkillEffectTimerSet().hasSkillEffect(MOB_BASILL)
				|| _target.getSkillEffectTimerSet().hasSkillEffect(MOB_COCA)) {
			_drainHp = 0;
		}
	}

	/** 조우의 돌골렘 - 마력의 단검 **/
	public void calcDrainOfMana() { // 마나 흡수를 위한 추가
		_drainMana = 1;
		if (_target.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)
				|| _target.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
				|| _target.getSkillEffectTimerSet().hasSkillEffect(FREEZING_BLIZZARD)
				|| _target.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
				|| _target.getSkillEffectTimerSet().hasSkillEffect(MOB_BASILL)
				|| _target.getSkillEffectTimerSet().hasSkillEffect(MOB_COCA)) {
			_drainMana = 0;
		}
	}

	/* ■■■■■■■■■■■■■■ 공격 모션 송신 ■■■■■■■■■■■■■■ */

	public void action() {
		try {
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				actionPc();
			} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
				actionNpc();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ●●●● 플레이어의 공격 모션 송신 ●●●●
	private void actionPc() {
		_pc.getMoveState().setHeading(CharPosUtil.targetDirection(_pc, _targetX, _targetY)); // 방향세트
		if (_weaponType == 20) {
			if (_arrow != null) {
				_pc.getInventory().removeItem(_arrow, 1);
				if (_pc.getGfxId().getTempCharGfx() == 17535) {
					_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 17539, _targetX, _targetY, _isHit));
					Broadcaster.broadcastPacket(_pc,
							new S_UseArrowSkill(_pc, _targetId, 17539, _targetX, _targetY, _isHit));
				} else if (_pc.getGfxId().getTempCharGfx() == 7968 || _pc.getGfxId().getTempCharGfx() == 14491) {
					if (_target.getGfxId().getTempCharGfx() == 16074 || _target.getGfxId().getTempCharGfx() == 16053
							|| _target.getGfxId().getTempCharGfx() == 14491
							|| _target.getGfxId().getTempCharGfx() == 16056
							|| _target.getGfxId().getTempCharGfx() == 16284
							|| _target.getGfxId().getTempCharGfx() == 16002
							|| _target.getGfxId().getTempCharGfx() == 16040
							|| _target.getGfxId().getTempCharGfx() == 16027
							|| _target.getGfxId().getTempCharGfx() == 16014
							|| _target.getGfxId().getTempCharGfx() == 16008
							|| _target.getGfxId().getTempCharGfx() == 15986) {
						_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 7972, _targetX, _targetY, false));
						Broadcaster.broadcastPacket(_pc,
								new S_UseArrowSkill(_pc, _targetId, 7972, _targetX, _targetY, false));
					} else {
						_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 7972, _targetX, _targetY, _isHit));
						Broadcaster.broadcastPacket(_pc,
								new S_UseArrowSkill(_pc, _targetId, 7972, _targetX, _targetY, _isHit));
					}
				} else {
					if (_target.getGfxId().getTempCharGfx() == 16074 || _target.getGfxId().getTempCharGfx() == 16053
							|| _target.getGfxId().getTempCharGfx() == 14491
							|| _target.getGfxId().getTempCharGfx() == 16056
							|| _target.getGfxId().getTempCharGfx() == 16284
							|| _target.getGfxId().getTempCharGfx() == 16002
							|| _target.getGfxId().getTempCharGfx() == 16040
							|| _target.getGfxId().getTempCharGfx() == 16027
							|| _target.getGfxId().getTempCharGfx() == 16014
							|| _target.getGfxId().getTempCharGfx() == 16008
							|| _target.getGfxId().getTempCharGfx() == 15986) {
						_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 66, _targetX, _targetY, false));
						// Broadcaster.broadcastPacket(_pc, new
						// S_UseArrowSkill(_pc, _targetId, 66, _targetX,
						// _targetY, false));
					} else {
						_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 66, _targetX, _targetY, _isHit));
						Broadcaster.broadcastPacket(_pc,
								new S_UseArrowSkill(_pc, _targetId, 66, _targetX, _targetY, _isHit));
					}
				}
				if (_isHit) {
					if (_target.getGfxId().getTempCharGfx() == 16074 || _target.getGfxId().getTempCharGfx() == 16053
							|| _target.getGfxId().getTempCharGfx() == 14491
							|| _target.getGfxId().getTempCharGfx() == 16056
							|| _target.getGfxId().getTempCharGfx() == 16284
							|| _target.getGfxId().getTempCharGfx() == 16002
							|| _target.getGfxId().getTempCharGfx() == 16040
							|| _target.getGfxId().getTempCharGfx() == 16027
							|| _target.getGfxId().getTempCharGfx() == 16014
							|| _target.getGfxId().getTempCharGfx() == 16008
							|| _target.getGfxId().getTempCharGfx() == 15986) {
						// _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId,
						// 66, _targetX, _targetY, false));
						Broadcaster.broadcastPacket(_pc,
								new S_UseArrowSkill(_pc, _targetId, 66, _targetX, _targetY, false));
					} else {
						Broadcaster.broadcastPacketExceptTargetSight(_target,
								new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
					}
				}
			} else if (_weaponId == 190 || _weaponId == 9100 || _weaponId == 450009) {
				_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 2349, _targetX, _targetY, _isHit));
				Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 2349, _targetX, _targetY, _isHit));
				if (_isHit) {
					Broadcaster.broadcastPacketExceptTargetSight(_target,
							new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
				}
			}

			if (_isHit) {
				if (isCritical()) {
					_pc.sendPackets(new S_AttackCritical(_pc, _targetId, 99));
					Broadcaster.broadcastPacket(_pc, new S_AttackCritical(_pc, _targetId, 99));
				}
			}

		} else if (_weaponType == 62 && _sting != null) {
			_pc.getInventory().removeItem(_sting, 1);
			if (_pc.getGfxId().getTempCharGfx() == 7968) {
				_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 7972, _targetX, _targetY, _isHit));
				Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 7972, _targetX, _targetY, _isHit));
			} else {
				_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 2989, _targetX, _targetY, _isHit));
				Broadcaster.broadcastPacket(_pc, new S_UseArrowSkill(_pc, _targetId, 2989, _targetX, _targetY, _isHit));
			}
			if (_isHit) {
				Broadcaster.broadcastPacketExceptTargetSight(_target,
						new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
			}
		} else {
			if (_isHit) {
				// 추가 런닝 변신 피격처리 근거리
				if (_target.getGfxId().getTempCharGfx() == 16074 || _target.getGfxId().getTempCharGfx() == 16053
						|| _target.getGfxId().getTempCharGfx() == 14491 || _target.getGfxId().getTempCharGfx() == 16056
						|| _target.getGfxId().getTempCharGfx() == 16284 || _target.getGfxId().getTempCharGfx() == 16002
						|| _target.getGfxId().getTempCharGfx() == 16040 || _target.getGfxId().getTempCharGfx() == 16027
						|| _target.getGfxId().getTempCharGfx() == 16014 || _target.getGfxId().getTempCharGfx() == 16008
						|| _target.getGfxId().getTempCharGfx() == 15986) {
					_pc.sendPackets(new S_AttackPacket(_pc, 0, ActionCodes.ACTION_Attack, _attackType));
					Broadcaster.broadcastPacket(_pc,
							new S_AttackPacket(_pc, 0, ActionCodes.ACTION_Attack, _attackType));
				} else {
					_pc.sendPackets(new S_AttackPacket(_pc, _targetId, ActionCodes.ACTION_Attack, _attackType));
					Broadcaster.broadcastPacket(_pc,
							new S_AttackPacket(_pc, _targetId, ActionCodes.ACTION_Attack, _attackType));

					if (isCritical() && weapon != null) {
						int criType = weapon.getItem().getType();

						_pc.sendPackets(new S_AttackCritical(_pc, _targetId, criType));
						Broadcaster.broadcastPacket(_pc, new S_AttackCritical(_pc, _targetId, criType));
					}

					Broadcaster.broadcastPacketExceptTargetSight(_target,
							new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _pc);
				}
			} else {
				if (_targetId > 0) {
					_pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
					Broadcaster.broadcastPacket(_pc, new S_AttackMissPacket(_pc, _targetId));
				} else {
					_pc.sendPackets(new S_AttackPacket(_pc, 0, ActionCodes.ACTION_Attack));
					Broadcaster.broadcastPacket(_pc, new S_AttackPacket(_pc, 0, ActionCodes.ACTION_Attack));
				}
			}
		}
	}

	// ●●●● NPC의 공격 모션 송신 ●●●●
	private void actionNpc() {
		int _npcObjectId = _npc.getId();
		int bowActId = 0;
		int actId = 0;

		_npc.getMoveState().setHeading(CharPosUtil.targetDirection(_npc, _targetX, _targetY)); // 방향세트

		// 타겟과의 거리가 2이상 있으면 원거리 공격
		boolean isLongRange = (_npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) > 1);
		bowActId = _npc.getNpcTemplate().getBowActId();

		if (getActId() > 0) {
			actId = getActId();
		} else {
			actId = ActionCodes.ACTION_Attack;
		}

		if (isLongRange && bowActId > 0) {
			if (_target.getGfxId().getTempCharGfx() == 16074 || _target.getGfxId().getTempCharGfx() == 16053
					|| _target.getGfxId().getTempCharGfx() == 14491 || _target.getGfxId().getTempCharGfx() == 16056
					|| _target.getGfxId().getTempCharGfx() == 16284 || _target.getGfxId().getTempCharGfx() == 16002
					|| _target.getGfxId().getTempCharGfx() == 16040 || _target.getGfxId().getTempCharGfx() == 16027
					|| _target.getGfxId().getTempCharGfx() == 16014 || _target.getGfxId().getTempCharGfx() == 16008
					|| _target.getGfxId().getTempCharGfx() == 15986) {
				Broadcaster.broadcastPacket(_npc,
						new S_UseArrowSkill(_npc, _targetId, bowActId, _targetX, _targetY, false));
			} else {
				Broadcaster.broadcastPacket(_npc,
						new S_UseArrowSkill(_npc, _targetId, bowActId, _targetX, _targetY, _isHit));
			}
		} else {
			if (_isHit) {
				if (getGfxId() > 0) {
					Broadcaster.broadcastPacket(_npc,
							new S_UseAttackSkill(_target, _npcObjectId, getGfxId(), _targetX, _targetY, actId));
					Broadcaster.broadcastPacketExceptTargetSight(_target,
							new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _npc);
				} else {
					// 추가 런닝 변신 피격처리 근거리
					if (_target.getGfxId().getTempCharGfx() == 16074 || _target.getGfxId().getTempCharGfx() == 16053
							|| _target.getGfxId().getTempCharGfx() == 14491
							|| _target.getGfxId().getTempCharGfx() == 16056
							|| _target.getGfxId().getTempCharGfx() == 16284
							|| _target.getGfxId().getTempCharGfx() == 16002
							|| _target.getGfxId().getTempCharGfx() == 16040
							|| _target.getGfxId().getTempCharGfx() == 16027
							|| _target.getGfxId().getTempCharGfx() == 16014
							|| _target.getGfxId().getTempCharGfx() == 16008
							|| _target.getGfxId().getTempCharGfx() == 15986) {
						Broadcaster.broadcastPacket(_npc, new S_AttackPacketForNpc(_target, _npcObjectId, 0, actId));
					} else {
						Broadcaster.broadcastPacket(_npc, new S_AttackPacketForNpc(_target, _npcObjectId, actId));
						Broadcaster.broadcastPacketExceptTargetSight(_target,
								new S_DoActionGFX(_targetId, ActionCodes.ACTION_Damage), _npc);
					}
				}
			} else {
				if (getGfxId() > 0) {
					Broadcaster.broadcastPacket(_npc,
							new S_UseAttackSkill(_target, _npcObjectId, getGfxId(), _targetX, _targetY, actId, 0));
				} else {
					Broadcaster.broadcastPacket(_npc, new S_AttackMissPacket(_npc, _targetId, actId));
				}
			}
		}
	}
	/* ■■■■■■■■■■■■■■■ 계산 결과 반영 ■■■■■■■■■■■■■■■ */

	public void commit() {
		if (_isHit) {
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				commitPc();
			} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
				commitNpc();
			}
		}

		// 데미지치 및 명중율 확인용 메세지
		if (!Config.ALT_ATKMSG) {
			return;
		}
		if (Config.ALT_ATKMSG) {
			if ((_calcType == PC_PC || _calcType == PC_NPC) && !_pc.isGm()) {
				return;
			}
			if ((_calcType == PC_PC || _calcType == NPC_PC) && !_targetPc.isGm()) {
				return;
			}
		}

	}

	// ●●●● 플레이어에 계산 결과를 반영 ●●●●
	private void commitPc() {
		if (_calcType == PC_PC) {
			if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(FREEZING_BLIZZARD)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(MOB_BASILL) // 바실얼리기데미지0
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(MOB_COCA)) { // 코카얼리기데미지0
				_damage = 0;
				_drainMana = 0;
				_drainHp = 0;
			}
			if (_drainMana > 0 && _targetPc.getCurrentMp() > 0) {
				if (_drainMana > _targetPc.getCurrentMp()) {
					_drainMana = _targetPc.getCurrentMp();
				}
				short newMp = (short) (_targetPc.getCurrentMp() - _drainMana);
				_targetPc.setCurrentMp(newMp);
				newMp = (short) (_pc.getCurrentMp() + _drainMana);
				_pc.setCurrentMp(newMp);
			}

			/** 조우의 돌골렘 **/

			if (_drainHp > 0 && _targetPc.getCurrentHp() > 0) {
				if (_drainHp > _targetPc.getCurrentHp()) {
					_drainHp = _targetPc.getCurrentHp();
				}
				short newHp = (short) (_targetPc.getCurrentHp() - _drainHp);
				_targetPc.setCurrentHp(newHp);
				newHp = (short) (_pc.getCurrentHp() + _drainHp);
				_pc.setCurrentHp(newHp);
			}
			/** 조우의 돌골렘 **/
			_targetPc.receiveDamage(_pc, _damage, false);
		} else if (_calcType == NPC_PC) {
			if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(FREEZING_BLIZZARD)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(MOB_BASILL) // 바실얼리기데미지0
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(MOB_COCA)) { // 코카얼리기데미지0
				_damage = 0;
			}

			_targetPc.receiveDamage(_npc, _damage, false);
		}
	}

	// ●●●● NPC에 계산 결과를 반영 ●●●●
	private void commitNpc() {
		if (_calcType == PC_NPC) {
			if (_targetNpc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(FREEZING_BLIZZARD)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(MOB_BASILL) // 바실얼리기데미지0
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(MOB_COCA)) { // 코카얼리기데미지0
				_damage = 0;
				_drainMana = 0;
				_drainHp = 0;
			}

			if (_drainMana > 0 && _targetNpc.getCurrentMp() > 0) {
				if (_drainMana > _targetNpc.getCurrentMp()) {
					_drainMana = _targetNpc.getCurrentMp();
				}
				short newMp = (short) (_targetNpc.getCurrentMp() - _drainMana);
				_targetNpc.setCurrentMp(newMp);
				newMp = (short) (_pc.getCurrentMp() + _drainMana);
				_pc.setCurrentMp(newMp);
			}
			
			/** 조우의 돌골렘 **/

			if (_drainHp > 0) {
				int newHp = _pc.getCurrentHp() + _drainHp;
				_pc.setCurrentHp(newHp);
			}
			/** 조우의 돌골렘 **/

			damageNpcWeaponDurability(); // 무기를 손상시킨다.

			_targetNpc.receiveDamage(_pc, _damage);
		} else if (_calcType == NPC_NPC) {
			if (_targetNpc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(FREEZING_BLIZZARD)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(MOB_BASILL) // 바실얼리기데미지0
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(MOB_COCA)) { // //코카얼리기데미지0
				_damage = 0;
			}
			_targetNpc.receiveDamage(_npc, _damage);
		}
	}

	/* ■■■■■■■■■■■■■■■ 카운터 바리어 ■■■■■■■■■■■■■■■ */

	// ■■■■ 카운터 바리어시의 공격 모션 송신 ■■■■
	public void actionCounterBarrier() {
		if (_calcType == PC_PC) {
			_pc.getMoveState().setHeading(CharPosUtil.targetDirection(_pc, _targetX, _targetY)); // 방향세트
			_pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
			Broadcaster.broadcastPacket(_pc, new S_AttackMissPacket(_pc, _targetId));
			_pc.sendPackets(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
			Broadcaster.broadcastPacket(_pc, new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
			_pc.sendPackets(new S_SkillSound(_targetId, 20990)); // 4395
			Broadcaster.broadcastPacket(_pc, new S_SkillSound(_targetId, 20990));

		} else if (_calcType == NPC_PC) {
			int actId = 0;
			_npc.getMoveState().setHeading(CharPosUtil.targetDirection(_npc, _targetX, _targetY)); // 방향세트
			if (getActId() > 0) {
				actId = getActId();
			} else {
				actId = ActionCodes.ACTION_Attack;
			}
			if (getGfxId() > 0) {
				Broadcaster.broadcastPacket(_npc,
						new S_UseAttackSkill(_target, _npc.getId(), getGfxId(), _targetX, _targetY, actId, 0));
			} else {
				Broadcaster.broadcastPacket(_npc, new S_AttackMissPacket(_npc, _targetId, actId));
			}
			Broadcaster.broadcastPacket(_npc, new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
			Broadcaster.broadcastPacket(_npc, new S_SkillSound(_targetId, 20990));// 4395
		}
	}

	// ■■■■ 모탈바디 발동시의 공격 모션 송신 ■■■■
	public void actionMortalBody() {
		if (_calcType == PC_PC) {
			_pc.getMoveState().setHeading(CharPosUtil.targetDirection(_pc, _targetX, _targetY)); // 방향세트
			S_UseAttackSkill packet = new S_UseAttackSkill(_pc, _target.getId(), 6519, _targetX, _targetY,
					ActionCodes.ACTION_Attack, false);
			_pc.sendPackets(packet);
			Broadcaster.broadcastPacket(_pc, packet);
			_pc.sendPackets(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
			Broadcaster.broadcastPacket(_pc, new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
		} else if (_calcType == NPC_PC) {
			_npc.getMoveState().setHeading(CharPosUtil.targetDirection(_npc, _targetX, _targetY)); // 방향세트
			Broadcaster.broadcastPacket(_npc, new S_SkillSound(_target.getId(), 6519));
			Broadcaster.broadcastPacket(_npc, new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
		}
	}

	// ■■■■ 상대의 공격에 대해서 카운터 바리어가 유효한가를 판별 ■■■■
	public boolean isShortDistance() {
		boolean isShortDistance = true;
		if (_calcType == PC_PC) {
			if (_weaponType == 20 || _weaponType == 62) { // 활이나 간트렛트
				isShortDistance = false;
			}
		} else if (_calcType == NPC_PC) {
			boolean isLongRange = (_npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) > 1);
			int bowActId = _npc.getNpcTemplate().getBowActId();
			// 거리가 2이상, 공격자의 활의 액션 ID가 있는 경우는 원공격
			if (isLongRange && bowActId > 0) {
				isShortDistance = false;
			}
		}
		return isShortDistance;
	}

	// ■■■■ 카운터 바리어의 데미지를 반영 ■■■■
	public void commitCounterBarrier() {
		int damage = calcCounterBarrierDamage();
		if (damage == 0) {
			return;
		}
		if (_calcType == PC_PC) {
			_pc.receiveDamage(_targetPc, damage, false);
		} else if (_calcType == NPC_PC) {
			_npc.receiveDamage(_targetPc, damage);
		}
	}

	// ■■■■ 모탈바디의 데미지를 반영 ■■■■
	public void commitMortalBody() {
		int damage = 30;
		if (damage == 0) {
			return;
		}
		if (_calcType == PC_PC) {
			_pc.receiveDamage(_targetPc, damage, false);
		} else if (_calcType == NPC_PC) {
			_npc.receiveDamage(_targetPc, damage);
		}
	}

	// ●●●● 카운터 바리어의 데미지를 산출 ●●●●
	private int calcCounterBarrierDamage() {
		int damage = 0;
		L1ItemInstance weapon = null;
		weapon = _targetPc.getWeapon();
		if (weapon != null) {
			if (weapon.getItem().getType() == 3) {
				damage = (weapon.getItem().getDmgLarge() + weapon.getEnchantLevel() + weapon.getItem().getDmgModifier())
						* 2 + 50;
			}
		}
		return damage;
	}

	/*
	 * 무기를 손상시킨다. 대NPC의 경우, 손상 확률은10%로 한다. 축복 무기는3%로 한다.
	 */
	private void damageNpcWeaponDurability() {
		int chance = 3;
		int bchance = 1;

		/*
		 * 손상하지 않는 NPC, 맨손, 손상하지 않는 무기 사용, SOF중의 경우 아무것도 하지 않는다.
		 */
		if (_calcType != PC_NPC || _targetNpc.getNpcTemplate().is_hard() == false || _weaponType == 0
				|| weapon.getItem().get_canbedmg() == 0 || _pc.getSkillEffectTimerSet().hasSkillEffect(SOUL_OF_FLAME)) {
			return;
		}

		if (_weaponBless == 0) { // 축 무기는 손상안함
			return;
		}
		// 통상의 무기·저주해진 무기
		if ((_weaponBless == 1 || _weaponBless == 2) && ((_random.nextInt(100) + 1) < chance)) {
			// \f1당신의%0가 손상했습니다.
			_pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
			_pc.getInventory().receiveDamage(weapon);
		}

		// 축복된 무기
		/*
		 * if (_weaponBless == 0 && ((_random.nextInt(100) + 1) < bchance)) { //
		 * \f1당신의%0가 손상했습니다. _pc.sendPackets(new S_ServerMessage(268,
		 * weapon.getLogName())); _pc.getInventory().receiveDamage(weapon); }
		 */
	}

	public int calcDamage(boolean flag) {
		switch (_calcType) {
		case PC_PC:
			_damage = calcPcPcDamage();
			break;
		case PC_NPC:
			_damage = calcPcNpcDamage();
			break;
		case NPC_PC:
			_damage = calcNpcPcDamage();
			break;
		case NPC_NPC:
			_damage = calcNpcNpcDamage();
			break;
		default:
			break;
		}
		if (flag)
			_damage /= 2;
		return _damage;
	}

	private int 스냅퍼체반데미지감소() {
		int damage = 0;
		if (_calcType == NPC_PC || _calcType == PC_PC) {
			L1ItemInstance item = _targetPc.getInventory().checkEquippedItem(21248);
			if (item != null && item.getEnchantLevel() >= 7) {
				if (_random.nextInt(100) < 1 + item.getEnchantLevel() - 7) {
					damage = 20;
					_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 12118));
				}
			}

			L1ItemInstance item2 = _targetPc.getInventory().checkEquippedItem(21252);
			if (item2 != null && item2.getEnchantLevel() >= 6) {
				if (_random.nextInt(100) < 1 + item2.getEnchantLevel() - 6) {
					damage = 20;
					_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 12118));
				}
			}
		}
		return damage;
	}
	
	/** 스탯 + 무기에 따른 공성 **/
	private int PchitAdd() {
		int value = 0;
		if (_pc instanceof L1RobotInstance) {
			return 10;
		}
		
		if (_pc.getAbility().getTotalStr() > 59) {
			value += (strHit[58]);
		} else {
			value += (strHit[_pc.getAbility().getTotalStr() - 1]);
		}
		
		if (_pc.getAbility().getTotalDex() > 60) {
			value += (dexHit[59]);
		} else {
			value += (dexHit[_pc.getAbility().getTotalDex() - 1]);
		}

		if (_weaponType != 20) {
			value += _weaponAddHit + _pc.getHitup() + (_weaponEnchant / 2);
		} else {
			value += _weaponAddHit + _pc.getBowHitup() + (_weaponEnchant / 2);
		}
		return value;
	}

	/** 타겟PC 회피 스킬에 대한 연산 **/
	private int toPcSkillHit() {
		int value = 0;
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(UNCANNY_DODGE)) {
			value -= 5;// 2
		}
		return value;
	}

	/** Hit 최종 연산 **/
	private int hitRateCal(int AD, int DD, int fumble, int critical) {
		System.out.println("AD="+AD);
		System.out.println("DD="+DD);
		System.out.println("fumble="+fumble);
		System.out.println("critical="+critical);
		if (AD <= fumble) {
			_hitRate = 0;
		} else if (AD >= critical) {
			_hitRate = 100;
		} else {
			if (AD > DD) {
				_hitRate = 100;
			} else if (AD <= DD) {
				_hitRate = 0;
			}
		}
		return _hitRate;
	}

	/** 타겟PC DD 연산 **/
	private int toPcDD(int dv) {
		if (_targetPc.getAC().getAc() >= 0) {
			return 10 - _targetPc.getAC().getAc();
		} else {
			return _random.nextInt(dv) + 1;
		}
	}
}