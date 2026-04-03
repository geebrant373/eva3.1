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
package l1j.server.server.model.skill;

public class L1SkillId {
	public static final int SKILLS_BEGIN = 1;

	/*
	 * Regular Magic Lv1-10
	 */
	// 1단계 일반마법
	public static final int HEAL = 1; // E: LESSER_HEAL
	public static final int LIGHT = 2;
	public static final int SHIELD = 3;
	public static final int ENERGY_BOLT = 4;
	public static final int TELEPORT = 5;
	public static final int ICE_DAGGER = 6;
	public static final int WIND_CUTTER = 7; // E: WIND_SHURIKEN
	public static final int HOLY_WEAPON = 8;

	// 2단계 일반마법
	public static final int CURE_POISON = 9;
	public static final int CHILL_TOUCH = 10;
	public static final int CURSE_POISON = 11;
	public static final int ENCHANT_WEAPON = 12;
	public static final int DETECTION = 13;
	public static final int DECREASE_WEIGHT = 14;
	public static final int FIRE_ARROW = 15;
	public static final int STALAC = 16;

	// 3단계 일반마법
	public static final int LIGHTNING = 17;
	public static final int TURN_UNDEAD = 18;
	public static final int EXTRA_HEAL = 19; // E: HEAL
	public static final int CURSE_BLIND = 20;
	public static final int BLESSED_ARMOR = 21;
	public static final int FROZEN_CLOUD = 22;
	public static final int WEAK_ELEMENTAL = 23; // E: REVEAL_WEAKNESS

	// 4단계 일반마법 	// none = 24
	public static final int FIREBALL = 25;
	public static final int PHYSICAL_ENCHANT_DEX = 26; // E: ENCHANT_DEXTERITY
	public static final int WEAPON_BREAK = 27;
	public static final int VAMPIRIC_TOUCH = 28;
	public static final int SLOW = 29;
	public static final int EARTH_JAIL = 30;
	public static final int COUNTER_MAGIC = 31;
	public static final int MEDITATION = 32;

	// 5단계 일반마법
	public static final int CURSE_PARALYZE = 10033;
	public static final int EMPIER = 33;
	public static final int CALL_LIGHTNING = 34;
	public static final int GREATER_HEAL = 35;
	public static final int TAMING_MONSTER = 36; // E: TAME_MONSTER
	public static final int REMOVE_CURSE = 37;
	public static final int CONE_OF_COLD = 38;
	public static final int MANA_DRAIN = 39;
	public static final int DARKNESS = 40;

	// 6단계 일반마법
	public static final int CREATE_ZOMBIE = 41;
	public static final int PHYSICAL_ENCHANT_STR = 42; // E: ENCHANT_MIGHTY
	public static final int HASTE = 43;
	public static final int CANCELLATION = 44; // E: CANCEL MAGIC
	public static final int ERUPTION = 45;
	public static final int SUNBURST = 46;
	public static final int WEAKNESS = 47;
	public static final int BLESS_WEAPON = 48;

	// 7단계 일반마법
	public static final int HEAL_ALL = 49; // E: HEAL_PLEDGE
	public static final int ICE_LANCE = 50;
	public static final int SUMMON_MONSTER = 51;
	public static final int HOLY_WALK = 52;
	public static final int TORNADO = 53;
	public static final int GREATER_HASTE = 54;
	public static final int BERSERKERS = 55;
	public static final int DISEASE = 56;

	// 8단계 일반마법
	public static final int FULL_HEAL = 57;
	public static final int FIRE_WALL = 58;
	public static final int BLIZZARD = 59;
	public static final int INVISIBILITY = 60;
	public static final int RESURRECTION = 61;
	public static final int EARTHQUAKE = 62;
	public static final int LIFE_STREAM = 63;
	public static final int SILENCE = 64;

	// 9단계 일반마법
	public static final int LIGHTNING_STORM = 65;
	public static final int FOG_OF_SLEEPING = 66;
	public static final int SHAPE_CHANGE = 67; // E: POLYMORPH
	public static final int IMMUNE_TO_HARM = 68;
	public static final int MASS_TELEPORT = 69;
	public static final int FIRE_STORM = 70;
	public static final int DECAY_POTION = 71;
	public static final int COUNTER_DETECTION = 72;

	// 10단계 일반마법
	public static final int CREATE_MAGICAL_WEAPON = 73;
	public static final int METEOR_STRIKE = 74;
	public static final int GREATER_RESURRECTION = 75;
	public static final int MASS_SLOW = 76;
	public static final int DISINTEGRATE = 77; // E: DESTROY
	public static final int ABSOLUTE_BARRIER = 78;
	public static final int ADVANCE_SPIRIT = 79;
	public static final int FREEZING_BLIZZARD = 80;

	// none = 81 - 86
	/*
	 * Knight skills
	 */
	public static final int SHOCK_STUN = 87; // E: STUN_SHOCK
	public static final int REDUCTION_ARMOR = 88;
	public static final int BOUNCE_ATTACK = 89;
	public static final int SOLID_CARRIAGE = 90;
	public static final int COUNTER_BARRIER = 91;

	// none = 92-96
	/*
	 * Dark Spirit Magic
	 */
	public static final int BLIND_HIDING = 97;
	public static final int ENCHANT_VENOM = 98;
	public static final int SHADOW_ARMOR = 99;
	public static final int BRING_STONE = 100;
	public static final int MOVING_ACCELERATION = 101; // E: PURIFY_STONE
	public static final int BURNING_SPIRIT = 102;
	public static final int ARMOR_BRAKE = 103;
	public static final int VENOM_RESIST = 104;
	public static final int DOUBLE_BRAKE = 105;  
	public static final int UNCANNY_DODGE = 106;
	public static final int SHADOW_FANG = 107;
	public static final int FINAL_BURN = 108;
	public static final int DRESS_MIGHTY = 109;
	public static final int DRESS_DEXTERITY = 110;
	public static final int DRESS_EVASION = 111;
	//public static final int ARMOR_BRAKE = 112;//220112 수정
	// none = 112
	/*
	 * Royal Magic
	 */
	public static final int TRUE_TARGET = 113;
	public static final int GLOWING_AURA = 114;
	public static final int SHINING_AURA = 115;
	public static final int CALL_CLAN = 116; // E: CALL_PLEDGE_MEMBER
	public static final int BRAVE_AURA = 117;
	public static final int RUN_CLAN = 118;

	// unknown = 119 - 120
	// none = 121 - 128
	/*
	 * Spirit Magic
	 */
	public static final int RESIST_MAGIC = 129;
	public static final int BODY_TO_MIND = 130;
	public static final int TELEPORT_TO_MOTHER = 131;
	public static final int TRIPLE_ARROW = 132;
	public static final int ELEMENTAL_FALL_DOWN = 133;
	public static final int COUNTER_MIRROR = 134;

	// none = 135 - 136
	public static final int CLEAR_MIND = 137;
	public static final int RESIST_ELEMENTAL = 138;

	// none = 139 - 144
	public static final int RETURN_TO_NATURE = 145;
	public static final int BLOODY_SOUL = 146; // E: BLOOD_TO_SOUL
	public static final int ELEMENTAL_PROTECTION = 147; // E:PROTECTION_FROM_ELEMENTAL
	
	public static final int FIRE_WEAPON = 148;
	public static final int WIND_SHOT = 149;
	public static final int WIND_WALK = 150;
	public static final int EARTH_SKIN = 151;
	public static final int ENTANGLE = 152;
	public static final int ERASE_MAGIC = 153;
	public static final int LESSER_ELEMENTAL = 154; // E:SUMMON_LESSER_ELEMENTAL
	public static final int FIRE_BLESS = 155; // E: BLESS_OF_FIRE
	public static final int STORM_EYE = 156; // E: EYE_OF_STORM
	public static final int EARTH_BIND = 157;
	public static final int NATURES_TOUCH = 158;
	public static final int EARTH_BLESS = 159; // E: BLESS_OF_EARTH
	public static final int AQUA_PROTECTER = 160;
	public static final int AREA_OF_SILENCE = 161;
	public static final int GREATER_ELEMENTAL = 162; // E:SUMMON_GREATER_ELEMENTAL
	public static final int BURNING_WEAPON = 163;
	public static final int NATURES_BLESSING = 164;
	public static final int CALL_OF_NATURE = 165; // E: NATURES_MIRACLE
	public static final int STORM_SHOT = 166;
	public static final int WIND_SHACKLE = 167;
	public static final int IRON_SKIN = 168;
	public static final int EXOTIC_VITALIZE = 169;
	public static final int WATER_LIFE = 170;
	public static final int ELEMENTAL_FIRE = 171;
	public static final int STORM_WALK = 172;
	public static final int POLLUTE_WATER = 173;
	public static final int STRIKER_GALE = 174;
	public static final int SOUL_OF_FLAME = 175;
	public static final int ADDITIONAL_FIRE = 176;
	public static final int SKILLS_END = 176;

	/*
	 * Status
	 */
	public static final int STATUS_BEGIN = 1000;
	public static final int STATUS_BRAVE = 1000;
	public static final int STATUS_HASTE = 1001;
	public static final int STATUS_BLUE_POTION = 1002;
	public static final int STATUS_UNDERWATER_BREATH = 1003;
	public static final int STATUS_WISDOM_POTION = 1004;

	public static final int STATUS_POISON = 1006;
	public static final int STATUS_POISON_SILENCE = 1007;
	public static final int STATUS_POISON_PARALYZING = 1008;
	public static final int STATUS_POISON_PARALYZED = 1009;
	public static final int STATUS_CURSE_PARALYZING = 1010;
	public static final int STATUS_CURSE_PARALYZED = 1011;
	public static final int STATUS_FLOATING_EYE = 1012;
	public static final int STATUS_HOLY_WATER = 1013;
	public static final int STATUS_HOLY_MITHRIL_POWDER = 1014;
	public static final int STATUS_HOLY_WATER_OF_EVA = 1015;
	public static final int STATUS_ELFBRAVE = 1016;
	public static final int STATUS_CANCLEEND = 1016;
	public static final int STATUS_CURSE_BARLOG = 1017;
	public static final int STATUS_CURSE_YAHEE = 1018;
	public static final int STATUS_PET_FOOD = 1019;
	public static final int STATUS_PINK_NAME = 1020;
	public static final int STATUS_TIKAL_BOSSJOIN = 1021;
	public static final int STATUS_TIKAL_BOSSDIE = 1022;
	public static final int STATUS_CHAT_PROHIBITED = 1023;
	public static final int STATUS_COMA_3 = 1024;
	public static final int STATUS_COMA_5 = 1025;
	public static final int FEATHER_BUFF_A = 1026;
	public static final int FEATHER_BUFF_B = 1027;
	public static final int FEATHER_BUFF_C = 1028;
	public static final int FEATHER_BUFF_D = 1029;
	public static final int  SELL_ADENA = 12000;
	public static final int 자동사냥 = 151544;
	
	public static final int BUFF_CRAY = 1026; // 크레이버프
	public static final int BUFF_SAMUEL = 1027; // 저주받은 무녀 사무엘 버프
	public static final int STATUS_DRAGONMAAN_EARTH = 7671; // 지룡의 마안
	public static final int STATUS_DRAGONMAAN_WATER = 7672; // 수룡의 마안
	public static final int STATUS_DRAGONMAAN_FIRE = 7673; // 화룡의 마안
	public static final int STATUS_DRAGONMAAN_WIND = 7674; // 풍룡의 마안
	public static final int STATUS_DRAGONMAAN_BIRTH = 7675; // 탄생의 마안
	public static final int STATUS_DRAGONMAAN_SHAPE = 7676; // 형상의 마안
	public static final int STATUS_DRAGONMAAN_LIFE = 7677; // 생명의 마안
	
//	public static final int STATUS_END = 1023;
	public static final int GMSTATUS_BEGIN = 2000;
	public static final int GMSTATUS_INVISIBLE = 2000;
	public static final int GMSTATUS_HPBAR = 2001;
	public static final int GMSTATUS_SHOWTRAPS = 2002;
	public static final int GMSTATUS_END = 2002;
	public static final int COOKING_NOW = 2999;
	public static final int COOKING_BEGIN = 3000;

	/** 1차요리 효과 (노멀) */
	public static final int COOKING_1_0_N = 3000;
	public static final int COOKING_1_1_N = 3001;
	public static final int COOKING_1_2_N = 3002;
	public static final int COOKING_1_3_N = 3003;
	public static final int COOKING_1_4_N = 3004;
	public static final int COOKING_1_5_N = 3005;
	public static final int COOKING_1_6_N = 3006;
	public static final int COOKING_1_7_N = 3007;

	/** 2차요리 효과 (노멀) */
	public static final int COOKING_1_8_N = 3008;
	public static final int COOKING_1_9_N = 3009;
	public static final int COOKING_1_10_N = 3010;
	public static final int COOKING_1_11_N = 3011;
	public static final int COOKING_1_12_N = 3012;
	public static final int COOKING_1_13_N = 3013;
	public static final int COOKING_1_14_N = 3014;
	public static final int COOKING_1_15_N = 3015;

	/** 3차요리 효과 (노멀) */
	public static final int COOKING_1_16_N = 3016;
	public static final int COOKING_1_17_N = 3017;
	public static final int COOKING_1_18_N = 3018;
	public static final int COOKING_1_19_N = 3019;
	public static final int COOKING_1_20_N = 3020;
	public static final int COOKING_1_21_N = 3021;
	public static final int COOKING_1_22_N = 3022;
	public static final int COOKING_1_23_N = 3023;

	/** 1차요리 효과 (환상) */
	public static final int COOKING_1_0_S = 3050;
	public static final int COOKING_1_1_S = 3051;
	public static final int COOKING_1_2_S = 3052;
	public static final int COOKING_1_3_S = 3053;
	public static final int COOKING_1_4_S = 3054;
	public static final int COOKING_1_5_S = 3055;
	public static final int COOKING_1_6_S = 3056;
	public static final int COOKING_1_7_S = 3057;

	/** 2차요리 효과 (환상) */
	public static final int COOKING_1_8_S = 3058;
	public static final int COOKING_1_9_S = 3059;
	public static final int COOKING_1_10_S = 3060;
	public static final int COOKING_1_11_S = 3061;
	public static final int COOKING_1_12_S = 3062;
	public static final int COOKING_1_13_S = 3063;
	public static final int COOKING_1_14_S = 3064;
	public static final int COOKING_1_15_S = 3065;

	/** 3차요리 효과 (환상) */
	public static final int COOKING_1_16_S = 3066;
	public static final int COOKING_1_17_S = 3067;
	public static final int COOKING_1_18_S = 3068;
	public static final int COOKING_1_19_S = 3069;
	public static final int COOKING_1_20_S = 3070;
	public static final int COOKING_1_21_S = 3071;
	public static final int COOKING_1_22_S = 3072;
	public static final int COOKING_1_23_S = 3073;

	public static final int SPECIAL_COOKING = 3074;
	public static final int COOKING_END = 3075;

	public static final int STATUS_FREEZE = 10071;
	public static final int CURSE_PARALYZE2 = 10101;

	public static final int STATUS_SPOT1 = 20072;
	public static final int STATUS_SPOT2 = 20073;
	public static final int STATUS_SPOT3 = 20074;

	public static final int STATUS_IGNITION = 20075;
	public static final int STATUS_QUAKE = 20076;
	public static final int STATUS_SHOCK = 20077;
	public static final int STATUS_BALANCE = 20078;

	public static final int STATUS_FRUIT = 20079;
	public static final int STATUS_OVERLAP = 20080;
	public static final int EXP_POTION = 20081;
	public static final int STATUS_BLUE_POTION2 = 20082;
	public static final int STATUS_DESHOCK = 20083;
	public static final int STATUS_CUBE = 20084;
	public static final int STATUS_CASHSCROLL = 6993;
	public static final int STATUS_CASHSCROLL2 = 6994;
	public static final int STATUS_CASHSCROLL3 = 6995;

	public static final int DRAGON_EMERALD_NO = 7785;
	public static final int DRAGON_EMERALD_YES = 7786;

	public static final int STATUS_BLUE_POTION3 = 22004;

	public static final int MOB_SLOW_18 = 30000;			//슬로우 18번모션
	public static final int MOB_SLOW_1 = 30001;				//슬로우 1번모션
	public static final int MOB_CURSEPARALYZ_19 = 30002;	//커스 19번모션
	public static final int MOB_COCA = 30003;				//코카트리스 얼리기공격
	public static final int MOB_BASILL = 30004;				//바실리스크 얼리기에볼
	public static final int MOB_RANGESTUN_19 = 30005;		//범위스턴 19번모션
	public static final int MOB_RANGESTUN_18 = 30006;		//범위스턴 18번모션
	public static final int MOB_CURSEPARALYZ_18 = 30007;	//커스 18번모션
	public static final int MOB_DISEASE_30 = 30008;			//디지즈 30번모션
	public static final int MOB_WEAKNESS_1 = 30009;			//위크니스 1번모션
	public static final int MOB_DISEASE_1 = 30079;			//디지즈 1번모션
	public static final int MOB_SHOCKSTUN_30 = 30081;		//쇼크스턴 30번모션
	public static final int MOB_WINDSHACKLE_1 = 30084;		//윈드셰클 1번모션


	//꼭 리스후 저장 되어야 할 버프 icon[19] = 11; // 아이템으로 사용해야 하는 버프들 예) 토파즈
	
	public static final int STR_STATUS_EFFECT_BM_CHINA_ITEM1 = 7101; // TOPAZ 5231
	public static final int STR_STATUS_EFFECT_BM_CHINA_ITEM2 = 7102; // EMERALD 4768
	public static final int STR_STATUS_EFFECT_BM_CHINA_ITEM3 = 7103; // 
	public static final int STR_STATUS_EFFECT_BM_CHINA_ITEM4 = 7104; // 
	public static final int STR_STATUS_EFFECT_BM_CHINA_ITEM5 = 7105; // 
	public static final int STR_STATUS_EFFECT_BM_CHINA_ITEM6 = 7106; // 
	public static final int STR_STATUS_EFFECT_BM_CHINA_ITEM7 = 7107; // 
	public static final int STR_STATUS_EFFECT_BM_CHINA_ITEM8 = 7108; // 
	public static final int STR_STATUS_EFFECT_BM_CHINA_ITEM9 = 7109; // 
//	public static final int STR_STATUS_EFFECT_BM_JAPAN_ITEM6 = 7250; // 
//	public static final int STR_STATUS_EFFECT_BM_JAPAN_ITEM7 = 7251; // 
//	public static final int STR_STATUS_EFFECT_BM_JAPAN_ITEM8 = 7252; //  
//	public static final int STR_STATUS_EFFECT_BM_JAPAN_ITEM9 = 7253; // 
//	public static final int STR_STATUS_EFFECT_BM_JAPAN_ITEM10 = 7254; // 
//	public static final int STR_STATUS_EFFECT_BM_JAPAN_ITEM11 = 7255; // 
//	public static final int STR_STATUS_EFFECT_BM_JAPAN_ITEM12 = 7256; // 
// 13 14 중복불가 BM_JAPAN_ITEM 들과도 중복 안됨 고로 사용 불가능
//	public static final int STR_STATUS_EFFECT_BM_JAPAN_ITEM13 = 7478; // 
//	public static final int STR_STATUS_EFFECT_BM_JAPAN_ITEM14 = 7479; // 
//	public static final int STR_STATUS_EFFECT_BM_CHINA_POTION2 = 7544; // 안타룬 5441
//	public static final int STR_STATUS_EFFECT_BM_CHINA_POTION3 = 7545; // 발라룬 5443
//	public static final int STR_STATUS_EFFECT_BM_CHINA_POTION4 = 7546; // 린드룬 5445
//	public static final int STR_STATUS_EFFECT_BM_CHINA_POTION5 = 7547; // 파푸룬 5447
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF1 = 8939; //RANK ★ 7093
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF2 = 8940; //RANK ★★ 7094
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF3 = 8941; // RANK ★★★ 7095
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF4 = 8942; // RANK ★★★★ 7096
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF5 = 8943; // VIP 5646
	// 대미지 감소 3 콘 1  hp 50
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF6 = 8944; // 안타룬 5441
	// 추타 3 힘 1  hp 50
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF7 = 8945; // 발라룬 5443
	// 활추타3 덱1 hp 30 mp 20
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF8 = 8946; // 린드룬 5445
	// sp1 인트1 hp20 mp30
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF9 = 8947; // 파푸룬 5447
	// PVP 대미지 리덕션 2 HP 30 Ac -1 
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF10 = 8948; // 고정 버프 8170
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF11 = 8949; // 포이즌 on
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF12 = 8950; // 포이즌 off
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF13 = 8951; //DRAGON RUNE OFF
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF14 = 8952; //클랜버프
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF15 = 8953; //클랜버프 종료
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF16 = 8954; //콤보 
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF17 = 8955; //쉐도우 팽
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF18 = 8956;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF19 = 8957;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF20 = 7000;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF21 = 7085;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF22 = 7002;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF23 = 7003;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF24 = 8962;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF25 = 8963;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF26 = 8967;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF27 = 8968;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF28 = 8969;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF29 = 8970;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF30 = 8971;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF31 = 8972;
	public static final int STR_STATUS_EFFECT_TAIWAN_STONE_BUFF32 = 8973;

	public static final int ANTA_MAAN = 7671; // 지룡의 마안
	public static final int FAFU_MAAN = 7672; // 수룡의 마안
	public static final int VALA_MAAN = 7673; // 화룡의 마안
	public static final int LIND_MAAN = 7674; // 풍룡의 마안
	public static final int BIRTH_MAAN = 7675; // 탄생의 마안
	public static final int SHAPE_MAAN = 7676; // 형상의 마안
	public static final int LIFE_MAAN = 7677; // 생명의 마안

	public static final int AdenRateByDoll = 7680;
	public static final int 자동사냥시간 = 15234;
	public static final int 오만지배1층버프 = 17671;
	public static final int 오만지배2층버프 = 17672;
	public static final int 오만지배3층버프 = 17673;
	public static final int 오만지배4층버프 = 17674;
	public static final int 오만지배5층버프 = 17675;
	public static final int 오만지배6층버프 = 17676;
	public static final int 오만지배7층버프 = 17677;
	public static final int 오만지배8층버프 = 17678;
	public static final int 오만지배9층버프 = 17679;
	public static final int 오만지배정상층버프 = 17680;
	
	public static final int 지배이반버프 = 17780;
	
	public static final int BUYER_COOLTIME = 1033;
	//8600 ~ 8700 
	public static final int EMPTY22 = 7301; // 
	public static final int EXP_BOOSTER_30  = 7289; // EXP 3069 30%
	public static final int STR_STATUS_EFFECT_GOD_MAJO_POTION_SPELL = 7321; // 
	public static final int STR_STATUS_EFFECT_JBM_EXP_BOOSTER = 7495; // exp 종료 6768
	public static final int STR_STATUS_EFFECT_BM_CHINA_POTION1 = 7543; 
	public static final int STR_STATUS_EFFECT_GOD_MAJO_POTION_SPELL1 = 7683; // 2400<--해외
	
	public static final int STATUS_MENT = 7626; // 추가 멘트명령어

	public static final int STATUS_EXP_UP_II =7289; // 기원의 일부 II
	
	public static final int STATUS_DRAGONPERL = 999; // 드래곤진주
    public static final int WEAPON_KURTS = 8001;//커검
    public static final int RANKING_BUFF_1 = 80000;
	public static final int RANKING_BUFF_2 = 80001;
	public static final int RANKING_BUFF_3 = 80002;
	public static final int RANKING_BUFF_4 = 80003;
	public static final int SEAL_BUFF = 80014;
	public static final int rank_1_10_int = 3535;
	public static final int 린드가호딜레이 = 8178;
	public static final int DRAGONRAID_BUFF = 55005;
}
