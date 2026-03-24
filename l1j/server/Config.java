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
 * http://www.gnu.org/copyleft/gpl.html1
 */
package l1j.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.MJTemplate.MJArrangeHelper.MJArrangeParseeFactory;
import l1j.server.MJTemplate.MJArrangeHelper.MJArrangeParser;
import l1j.server.server.utils.IntRange;
import l1j.server.server.utils.PerformanceTimer;

public final class Config {
	private static final Logger _log = Logger.getLogger(Config.class.getName());
	public static String[] LANGUAGE_CODE_ARRAY = { "UTF8", "EUC-KR", "UTF8", "BIG5", "SJIS", "GBK" };
	public static int 자동사냥충전시간;
	public static int 자동사냥법사공격스킬발동확률;
	public static int 자동사냥트리플발동확률;
	public static boolean 자동사냥아이템픽업;
	public static int 자동사냥뮨;
	/** Debug/release mode */
	public static final boolean DEBUG = false;
	public static boolean STANDBY_SERVER = false;
	public static boolean CHECK_AUTO;
	public static int 자동인증시간;
	
	public static Integer[] 용갑인챈;
	public static int 용갑;
	public static int 배틀존입장레벨;
	public static boolean 배틀존작동유무;
	public static String 배틀존아이템;
	public static String 배틀존아이템갯수;
	public static String 배틀존_OPEN_TIME = null;
	
	public static int 인형합성확률1단계;
	public static int 인형합성확률2단계;
	public static int 인형합성확률3단계;
	public static int 인형합성확률4단계;
	public static int 인형합성확률5단계;
	
	public static int 인형합성비용1단계;
	public static int 인형합성비용2단계;
	public static int 인형합성비용3단계;
	public static int 인형합성비용4단계;
	public static int 인형합성비용5단계;
	
	public static int systime;
	public static String sys1;
	public static String sys2;
	public static String sys3;
	public static String sys4;
	public static String sys5;
	public static String sys6;
	public static String sys7;
	public static String sys8;
	public static String sys9;
	public static String sys10;
	public static String sys11;
	public static String sys12;
	public static String sys13;
	public static String sys14;
	public static String sys15;
	public static String sys16;
	
	/** Thread pools size */
	public static int THREAD_P_EFFECTS;
	public static int THREAD_P_GENERAL;
	public static int AI_MAX_THREAD;
	public static int THREAD_P_TYPE_GENERAL;
	public static int THREAD_P_SIZE_GENERAL;
	public static double WANTED_ADENA_CHARGE; // 현상금 회수율
	public static int WANTED_ADENA_52;
	public static int WANTED_ADENA_55;
	public static int WANTED_ADENA_60;
	public static int WANTED_ADENA_65;
	public static int WANTED_ADENA_70;
	public static int WANTED_ADENA_75;
	public static int WANTED_ADENA_80;
	public static double 데스나이트메테오;
	public static double 데스나이트헬파이어;
	public static double 데스나이트선버스트;
	public static double 데스나이트경험치;
	public static double 무료인형경험치;
	public static double IMMUNE_TO_HARM_PC;
	public static double IMMUNE_TO_HARM_NPC;
	public static double IMMUNE_TO_HARM_MAGIC;
	public static int EnchantChanceAccessory;
	public static int BlassEnchantChanceAccessory;
	public static int Master_Enchant;
	public static int Master_ArmorEnchant;
	public static int 랭킹동상x좌표;
	public static int 랭킹동상y좌표;
	
	public static int 게일추가대미지;
	
	public static int FEATHER_TIME; 
	public static int useritem;
	public static int usercount;
	public static int USERITEM1_TIME; 
	public static int useritem1;
	public static int usercount1;
	public static boolean 전체선물작동유무;
	public static int ROBOT_TEL_PERCENT;
	public static boolean START_AUTO_ROBOT;
	public static int 봇시작레벨;
	public static int 봇물약기본회복량;
	public static int 봇물약랜덤회복량;
	public static int ROBOT_LEVEL_RANGE;
	public static boolean ROBOT_DIE_MESSAGE;
	
	public static int SANGATOP_45_RANGE;
	public static int SANGATOP_45_ADD_HIT;
	public static int SANGATOP_45_ADD_DMG;
	
	public static int HAWDOONG_RANGE;
	public static int HAWDOONG_ADD_HIT;
	public static int HAWDOONG_ADD_DMG;
	
	public static int BONDUN_16_RANGE;
	public static int BONDUN_16_ADD_HIT;
	public static int BONDUN_16_ADD_DMG;
	
	public static int BONDUN_7_RANGE;
	public static int BONDUN_7_ADD_HIT;
	public static int BONDUN_7_ADD_DMG;
	
	public static int GIGAM_12_RANGE;
	public static int GIGAM_12_ADD_HIT;
	public static int GIGAM_12_ADD_DMG;
	
	public static int OMAN_18_RANGE;
	public static int OMAN_18_ADD_HIT;
	public static int OMAN_18_ADD_DMG;
	
	public static int OMAN_9_RANGE;
	public static int OMAN_9_ADD_HIT;
	public static int OMAN_9_ADD_DMG;
	
	public static int TOP_RANGE;
	public static int TOP_ADD_HIT;
	public static int TOP_ADD_DMG;
	
	public static int GYULGE_12_RANGE;
	public static int GYULGE_12_ADD_HIT;
	public static int GYULGE_12_ADD_DMG;
	
	public static int ETC_RANGE;
	public static int ETC_ADD_HIT;
	public static int ETC_ADD_DMG;
	
	public static int FEATHER_NUM;
	public static int FEATHER_NUM1;
	public static int FEATHER_NUM2;
	public static int FEATHER_NUM3;
	
	public static int 잊섬1봇카운트;
	public static int 잊섬2봇카운트;
	public static int 화둥봇카운트;
	public static int 용계봇카운트;
	public static int 풍둥봇카운트;
	public static int 용던1봇카운트;
	public static int 용던2봇카운트;
	public static int 용던3봇카운트;
	public static int 용던4봇카운트;
	public static int 용던5봇카운트;
	public static int 용던6봇카운트;
	public static int 용던7봇카운트;
	public static int 본던1봇카운트;
	public static int 본던2봇카운트;
	public static int 본던3봇카운트;
	public static int 본던4봇카운트;
	public static int 본던5봇카운트;
	public static int 본던6봇카운트;
	public static int 본던7봇카운트;
	public static int 기감1봇카운트;
	public static int 기감2봇카운트;
	public static int 상아탑4봇카운트;
	public static int 상아탑5봇카운트;
	public static int 상아탑6봇카운트;
	public static int 상아탑7봇카운트;
	public static int 오만1봇카운트;
	public static int 오만2봇카운트;
	public static int 오만3봇카운트;
	public static int 오만4봇카운트;
	public static int 오만5봇카운트;
	public static int 오만6봇카운트;
	public static int 오만7봇카운트;
	public static int 오만8봇카운트;
	public static int 오만9봇카운트;
	public static int 오만10봇카운트;
	public static int 오만정상봇카운트;
	public static int 지배1봇카운트;
	public static int 지배2봇카운트;
	public static int 지배3봇카운트;
	public static int 지배4봇카운트;
	public static int 지배5봇카운트;
	public static int 지배6봇카운트;
	public static int 지배7봇카운트;
	public static int 지배8봇카운트;
	public static int 지배9봇카운트;
	public static int 지배10봇카운트;
	public static int 지배정상봇카운트;
	public static int 지배결계1봇카운트;
	public static int 지배결계2봇카운트;
	public static int 엘모어봇카운트;
	public static int 보스봇카운트;
	public static int 오만보스봇카운트;
	public static int 지배보스봇카운트;
	
	public static int ORIM_ACCESS_ENCHANT_SCROLL_USE_LEVEL; // 오림의장신구마법주문서 사용가능할
															// 레벨
	public static int ORIM_ACCESS_ENCHANT_SCROLL_CHANCE; // 오림의장신구마법주문서 성공확률
	public static int ORIM_ACCESS_ENCHANT_SCROLL_DECREASE_CHANCE; // 오림의장신구마법주문서
																	// 인챈당 감소확률

	public static double UNCANNY_DODGE_DECREASE_RATE_BY_NPC;
	public static double UNCANNY_DODGE_DECREASE_RATE_BY_PC;
	public static int DOUBLE_BRAKE_EXERCISE_PROB;
	public static double DOUBLE_BRAKE_WEAPON_INCREASE_DMG_RATE;
	
	public static double PER_BURNING_SPIRIT_DMG_RATE;
	public static int BURNING_SPIRIT_EXERCISE_PROB;
	public static double PER_ELEMENTAL_FIRE_DMG_RATE;
	public static int ELEMENTAL_FIRE_EXERCISE_PROB;
	
	public static boolean KEYWORD_USE;
	

	public static boolean CHECK_SPELL_EARTH_BIND;
	
	public static double MOVE_SPEED_VALUE;
	public static double ATTACK_SPEED_VALUE;
	
	public static double 펫추가대미지;
	public static double 서먼추가대미지;
	public static double 자동패널티배율;
	public static double AC_HIT_PCPC;
	public static double AC_HIT_NPCPC;
	public static double MR_MAGIC_DMG;
	public static double 인원배율;
	public static int EnchantChanceRun0;
	public static int EnchantChanceRun1;
	public static int EnchantChanceRun2;
	public static int EnchantChanceRun3;
	public static int EnchantChanceRun4;
	public static int EnchantChanceRun5;
	public static int EnchantChanceRun6;
	public static int EnchantChanceRun7;
	public static int EnchantChanceRun8;
	public static int EnchantChanceRun9;
	public static int BlassEnchantChanceRun;
	public static int 랭커변신가능순위;
	
	public static double MR_SKILL_REDUC200;
	public static double MR_SKILL_REDUC190;
	public static double MR_SKILL_REDUC180;
	public static double MR_SKILL_REDUC170;
	public static double MR_SKILL_REDUC160;
	public static double MR_SKILL_REDUC150;
	public static double MR_SKILL_REDUC140;
	public static double MR_SKILL_REDUC130;
	public static double MR_SKILL_REDUC120;
	public static double MR_SKILL_REDUC110;
	public static double MR_SKILL_REDUC100;
	public static double MR_SKILL_REDUC90;
	public static double MR_SKILL_REDUC80;
	public static double MR_SKILL_REDUC70;
	public static double MR_SKILL_REDUC60;
	public static double MR_SKILL_REDUC50;
	public static double MR_SKILL_REDUC40;
	public static double MR_SKILL_REDUC30;
	public static double MR_SKILL_REDUC20;
	public static double MR_SKILL_REDUC10;
	public static int RANKTIME;
	
	public static int MANAGER_LOG_SAVE_DELAY;

	public static int 바운스어택수치;
	public static int 솔리드캐리지수치;
	
	public static int 발라가호확률;
	public static int 린드가호확률;
	public static int 파푸가호확률;
	public static double 발라이펙트대미지1;
	public static double 발라이펙트대미지2;
	public static double 발라이펙트대미지3;
	public static double 발라이펙트대미지4;
	
	public static double 린드이펙트대미지1;
	public static double 린드이펙트대미지2;
	public static double 린드이펙트대미지3;
	public static double 린드이펙트대미지4;
	
	public static double 파푸이펙트대미지1;
	public static double 파푸이펙트대미지2;
	public static double 파푸이펙트대미지3;
	public static double 파푸이펙트대미지4;
	
	public static int 무기디지즈확률1;
	public static int 무기디지즈확률2;
	public static int 무기디지즈확률3;
	public static int 무기디지즈확률4;
	public static int 무기디지즈확률5;
	public static int 무기디지즈확률6;
	public static int 무기디지즈지속시간;
	public static int 빨갱이회복량;
	public static int 주홍이회복량;
	public static int 맑갱이회복량;
	public static int 낮은3렙캐릭;
	public static int 낮은2렙캐릭;
	public static int 낮은1렙캐릭;
	public static int 동렙캐릭;
	public static int 높은1렙캐릭;
	public static int 높은2렙캐릭;
	public static int 높은3렙캐릭;
	/** ADD_CPMW_SYSTEM */
	public static String boardTitleAdena;
	public static int ADENASHOP_LEVEL;
	public static int MAX_SELL_ADENA;
	public static int MIN_SELL_ADENA;
	public static int MIN_SELL_CASH;
	public static int MAX_SELL_CASH;
	public static double ADD_ADENA_BY_DOLL_RATE;
	public static int ADD_ADENA_BY_DOLL_ID;
	public static double ADD_EXP_BY_DOLL_RATE;
	public static int ADD_EXP_BY_DOLL_ID;
	public static int MOVE_ADD_INRERVAL;
	public static int 빨갱이;
	public static int 주홍이;
	public static int 맑갱이;
	public static int ATTACK_ADD_INRERVAL;
	public static int 용던본던시간;
	public static int 기감시간;
	public static int 몽섬시간;
	public static int 라던시간;
	public static int 개미던전시간;
	public static int 그림자신전시간;
	public static int 기본엠흡;
	public static int 바포엠흡;
	public static int 기감렙제;
	public static int 테베렙제;
	public static int 포그면역수치;
	public static int 디지즈면역수치;
	public static int 커스면역수치;
	public static int 웨폰브레이크면역수치;
	public static int 커스블라인드면역수치;
	public static int 아이스면역수치;
	public static int 아데나시세비율;
	public static int 중립혈엠블럼;
	public static int 중립혈아이디;
	public static int 중립혈레벨제한;
	public static int 드슬변신랭킹;
	public static int 지배순반제작확률;
	
	public static int 무기안전0일때1업;
	public static int 무기안전0일때2업;
	public static int 무기안전0일때3업;
	public static int 무기안전6일때1업;
	public static int 무기안전6일때2업;
	public static int 무기안전6일때3업;
	
	public static int 방어구안전0일때1업;
	public static int 방어구안전0일때2업;
	public static int 방어구안전0일때3업;
	public static int 방어구안전4일때1업;
	public static int 방어구안전4일때2업;
	public static int 방어구안전4일때3업;
	
	/** Server control */
	public static String 서버이름;
	public static int GAME_SERVER_TYPE;
	public static String GAME_SERVER_HOST_NAME;
	public static int GAME_SERVER_PORT;
	public static String DB_DRIVER;
	public static String DB_URL;
	public static String DB_LOGIN;
	public static String DB_PASSWORD;
	public static String TIME_ZONE;
	public static int CLIENT_LANGUAGE;
	public static boolean HOSTNAME_LOOKUPS;
	public static int AUTOMATIC_KICK;
	public static boolean AUTO_CREATE_ACCOUNTS;
	public static short MAX_ONLINE_USERS;
	public static boolean CACHE_MAP_FILES;
	public static boolean LOAD_V2_MAP_FILES;
	public static boolean CHECK_MOVE_INTERVAL;
	public static boolean CHECK_ATTACK_INTERVAL;
	public static boolean CHECK_SPELL_INTERVAL;
	public static short INJUSTICE_COUNT;
	public static int JUSTICE_COUNT;
	public static int CHECK_STRICTNESS;
	public static byte LOGGING_WEAPON_ENCHANT;
	public static byte LOGGING_ARMOR_ENCHANT;
	public static boolean LOGGING_CHAT_NORMAL;
	public static boolean LOGGING_CHAT_WHISPER;
	public static boolean LOGGING_CHAT_SHOUT;
	public static boolean LOGGING_CHAT_WORLD;
	public static boolean LOGGING_CHAT_CLAN;
	public static boolean LOGGING_CHAT_PARTY;
	public static boolean LOGGING_CHAT_COMBINED;
	public static boolean LOGGING_CHAT_CHAT_PARTY;

	public static boolean 자동사냥;
	public static int AUTOSAVE_INTERVAL;
	public static int AUTOSAVE_INTERVAL_INVENTORY;
	public static int SKILLTIMER_IMPLTYPE;
	public static int NPCAI_IMPLTYPE;
	public static boolean TELNET_SERVER;
	public static int TELNET_SERVER_PORT;
	public static int PC_RECOGNIZE_RANGE;
	public static boolean CHARACTER_CONFIG_IN_SERVER_SIDE;
	public static boolean ALLOW_2PC;
	public static int LEVEL_DOWN_RANGE;
	public static boolean SEND_PACKET_BEFORE_TELEPORT;
	public static boolean DETECT_DB_RESOURCE_LEAKS;
	public static int 경험치물약;
	public static boolean 낚시장소;
	public static boolean 다엘생성;
	public static boolean 자동물약;
	/** Rate control */
	public static double 룬착용추가경험치1;
	public static double 룬착용추가경험치2;
	public static double 룬착용추가경험치3;
	public static double 룬착용추가경험치4;
	public static double 룬착용추가경험치5;
	public static double 룬착용추가경험치6;
	public static double 룬착용추가경험치7;
	public static double 룬착용추가경험치8;
	public static int AinHasad_dell;
	public static int 스냅퍼최대인챈;

	public static int 룸티스최대인챈;
	public static int 룬최대인챈;
	public static int 장신구최대인챈;
	public static int nomal_orim;
	public static int Bless_orim;
	public static int 일층지배확률;
	public static int 이층지배확률;
	public static int 삼층지배확률;
	public static int 사층지배확률;
	public static int 오층지배확률;
	public static int 육층지배확률;
	public static int 칠층지배확률;
	public static int 팔층지배확률;
	public static int 구층지배확률;
	public static int 십층지배확률;
	public static int 축오림제작확률;
	public static int MAX_ARMOR;
	public static int MAX_WEAPON;
	public static double RATE_XP;
	public static double RATE_LAWFUL;
	public static double RATE_KARMA;
	public static double RATE_DROP_ADENA;
	public static double RATE_DROP_ITEMS;
	public static int ENCHANT_CHANCE_WEAPON;
	public static int ENCHANT_CHANCE_ARMOR;
	public static double RATE_WEIGHT_LIMIT;
	public static double RATE_WEIGHT_LIMIT_PET;
	public static double RATE_SHOP_SELLING_PRICE;
	public static double RATE_SHOP_PURCHASING_PRICE;
	public static int RATE_FISHING_EXP;
	public static int CREATE_CHANCE_DIARY;
	public static int CREATE_CHANCE_RECOLLECTION;
	public static int CREATE_CHANCE_MYSTERIOUS;
	public static int CREATE_CHANCE_PROCESSING;
	public static int CREATE_CHANCE_PROCESSING_DIAMOND;
	public static int CREATE_CHANCE_DANTES;
	public static int CREATE_CHANCE_ANCIENT_AMULET;
	public static int CREATE_CHANCE_HISTORY_BOOK;

	public static double RATE_7_DMG_RATE;// 인첸추타 외부화
	public static int RATE_7_DMG_PER;
	public static double RATE_8_DMG_RATE;
	public static int RATE_8_DMG_PER;
	public static double RATE_9_DMG_RATE;
	public static int RATE_9_DMG_PER;
	public static double RATE_10_DMG_RATE;
	public static int RATE_10_DMG_PER;
	public static double RATE_11_DMG_RATE;
	public static int RATE_11_DMG_PER;
	public static double RATE_12_DMG_RATE;
	public static int RATE_12_DMG_PER;
	public static double RATE_13_DMG_RATE;
	public static int RATE_13_DMG_PER;
	public static double RATE_14_DMG_RATE;
	public static int RATE_14_DMG_PER;
	public static double RATE_15_DMG_RATE;
	public static int RATE_15_DMG_PER;
	public static double RATE_16_DMG_RATE;
	public static int RATE_16_DMG_PER;
	public static double RATE_17_DMG_RATE;
	public static int RATE_17_DMG_PER;
	public static double RATE_18_DMG_RATE;
	public static int RATE_18_DMG_PER;

	public static double adddmg7to1;
	public static double adddmg7to2;
	public static double adddmg7to3;
	public static double adddmg8to1;
	public static double adddmg8to2;
	public static double adddmg8to3;
	public static double adddmg9to1;
	public static double adddmg9to2;
	public static double adddmg9to3;
	public static double adddmg10to1;
	public static double adddmg10to2;
	public static double adddmg10to3;
	public static double adddmg11to1;
	public static double adddmg11to2;
	public static double adddmg11to3;
	public static double adddmg12to1;
	public static double adddmg12to2;
	public static double adddmg12to3;
	public static double adddmg13to1;
	public static double adddmg13to2;
	public static double adddmg13to3;
	public static double adddmg14to1;
	public static double adddmg14to2;
	public static double adddmg14to3;
	public static double adddmg15to1;
	public static double adddmg15to2;
	public static double adddmg15to3;

	public static double TRIPLE_DMG;
	public static int 낚시성공확률;
	
	/** AltSettings control */
	public static short GLOBAL_CHAT_LEVEL;
	public static short WHISPER_CHAT_LEVEL;
	public static byte AUTO_LOOT;
	public static int LOOTING_RANGE;
	public static boolean ALT_NONPVP;
	public static boolean ALT_ATKMSG;
	public static boolean CHANGE_TITLE_BY_ONESELF;
	public static int MAX_CLAN_MEMBER;
	public static boolean CLAN_ALLIANCE;
	public static int MAX_PT;
	public static int MAX_CHAT_PT;
	public static boolean SIM_WAR_PENALTY;
	public static boolean GET_BACK;
	public static String ALT_ITEM_DELETION_TYPE;
	public static int ALT_ITEM_DELETION_TIME;
	public static int ALT_ITEM_DELETION_RANGE;
	public static int 경험치지급단;
	public static int 무기5;
	public static int 무기6;
	public static int 무기7;
	public static int 무기8;
	public static int 무기9;
	public static int 무기10;
	public static int 무기11;
	public static int 무기12;
	public static int 무기13;
	public static int 무기14;
	public static int 무기안전0;
	public static int 무기안전1;
	public static int 무기안전2;
	public static int 무기안전3;
	public static int 무기안전4;
	public static int 무기안전5;
	public static int 무기안전6;
	public static int 무기안전7;
	public static int 무기안전8;
	public static int 무기안전9;
	public static int 무기안전10;
	public static int 무기안전11;
	public static int 무기안전12;
	public static int 무기안전13;
	public static int 무기안전14;
	public static int 무기안전15;

	public static int 방어구4;
	public static int 방어구5;
	public static int 방어구6;
	public static int 방어구7;
	public static int 방어구8;
	public static int 방어구9;
	public static int 방어구10;
	public static int 방어구11;
	public static int 방어구12;
	public static int 방어구13;
	public static int 방어구14;
	public static int 방어구안전0;
	public static int 방어구안전1;
	public static int 방어구안전2;
	public static int 방어구안전3;
	public static int 방어구안전4;
	public static int 방어구안전5;
	public static int 방어구안전6;
	public static int 방어구안전7;
	public static int 방어구안전8;

	public static int 방어구안전사0;
	public static int 방어구안전사1;
	public static int 방어구안전사2;
	public static int 방어구안전사3;
	public static int 방어구안전사4;
	public static int 방어구안전사5;
	public static int 방어구안전사6;
	public static int 방어구안전사7;
	public static int 방어구안전사8;
	public static int 방어구안전사9;
	public static int 방어구안전사10;
	public static int 악세인첸1;
	public static int 악세인첸2;
	public static int 악세인첸3;
	public static int 악세인첸4;
	public static int 악세인첸5;
	public static int 악세인첸6;
	public static int 악세인첸7;
	public static int 악세인첸8;
	public static int 악세인첸9;

	public static boolean 수배작동유무;

	public static int 수배1단;
	public static int 수배2단;
	public static int 수배3단;
	public static int npcdmg;
	public static int wing;
	public static double 인형축복;
	public static double weaponbless;
	// -- 몬스터 레벨별 차등데미지 외부화
	public static double MONSTER_DAMAGE_1; // 몹렙이 10 ~ 49
	public static double MONSTER_DAMAGE_2; // 몹렙이 50 ~ 69
	public static double MONSTER_DAMAGE_3; // 몹렙이 70 ~ 79
	public static double MONSTER_DAMAGE_4; // 몹렙이 80 이상
	public static double MONSTER_DAMAGE_5; // 몹렙이 80 이상
	public static double MONSTER_DAMAGE_6; // 몹렙이 80 이상

	public static boolean PickUpItem;
	public static String PickUpItem_Id;
	public static boolean PickUpItem_UserName;

	public static boolean HunterEvent_Doll;
	public static String HunterEvent_Doll_Id;
	public static String HunterEvent_Doll_MapId;
	public static String HunterEvent_Doll_MapId1;
	public static String HunterEvent_Doll_MapId2;
	public static String HunterEvent_Doll_MapId3;
	public static String HunterEvent_Doll_MapId4;
	public static String HunterEvent_Doll_MapId5;
	public static String HunterEvent_Doll_MapId6;
	public static String HunterEvent_Doll_MapId7;
	public static String HunterEvent_Doll_MapId8;
	public static String HunterEvent_Doll_MapId9;
	public static String HunterEvent_Doll_MapId10;
	public static int HunterEvent_Doll_MonsterId;
	public static int HunterEvent_Doll_1;
	public static int HunterEvent_Doll_11;
	public static int HunterEvent_Doll_12;
	public static int HunterEvent_Doll_13;
	public static int HunterEvent_Doll_14;
	public static int HunterEvent_Doll_15;
	
	public static int 전투메시지딜레이;
	public static int ALLBUFF_POLYID;
	public static boolean ASBUGCHECK_ALLBUF;

	public static int 근거리pc메테오대미지;
	public static int 원거리pc메테오대미지;
	public static int 근거리npc메테오대미지;
	public static int 원거리npc메테오대미지;
	
	public static int 근거리헬파이어대미지;
	public static int 근거리선버스트대미지;
	
	public static int 원거리헬파이어대미지;
	public static int 원거리선버스트대미지;
	
	
	/** 속성강화 주문서 관련 **/
	public static int ENCHANT_CHANCE_WATER;
	public static int ENCHANT_CHANCE_FIRE;
	public static int ENCHANT_CHANCE_WIND;
	public static int ENCHANT_CHANCE_EARTH;
	public static int ATTR_ENCHANT_LEVEL;

	/****** 이벤트 ******/
	public static boolean ALT_HALLOWEENEVENT; // 할로윈
	public static boolean ALT_HALLOWEENEVENT2009; // 할로윈(2009년)
	public static boolean ALT_FANTASYEVENT; // 환상
	public static boolean ALT_CHUSEOKEVENT; // 추석(09.09.24)
	public static boolean ALT_FEATURE;

	public static boolean ALT_WHO_COMMAND;
	public static boolean ALT_REVIVAL_POTION;
	public static int ALT_WAR_TIME;
	public static int ALT_WAR_TIME_UNIT;
	public static int ALT_WAR_INTERVAL;
	public static int ALT_WAR_INTERVAL_UNIT;
	public static int ALT_RATE_OF_DUTY;
	public static boolean SPAWN_HOME_POINT;
	public static int SPAWN_HOME_POINT_RANGE;
	public static int SPAWN_HOME_POINT_COUNT;
	public static int SPAWN_HOME_POINT_DELAY;
	public static boolean INIT_BOSS_SPAWN;
	public static int ELEMENTAL_STONE_AMOUNT;
	public static int HOUSE_TAX_INTERVAL;
	public static int MAX_DOLL_COUNT;
	public static boolean RETURN_TO_NATURE;
	public static int MAX_NPC_ITEM;
	public static int MAX_PERSONAL_WAREHOUSE_ITEM;
	public static int MAX_CLAN_WAREHOUSE_ITEM;
	public static boolean DELETE_CHARACTER_AFTER_7DAYS;
	public static int GMCODE;
	public static int DELETE_DB_DAYS;

	/** CharSettings control */

	public static int PRINCE_MAX_HP;
	public static int PRINCE_MAX_MP;
	public static int KNIGHT_MAX_HP;
	public static int KNIGHT_MAX_MP;
	public static int ELF_MAX_HP;
	public static int ELF_MAX_MP;
	public static int WIZARD_MAX_HP;
	public static int WIZARD_MAX_MP;
	public static int DARKELF_MAX_HP;
	public static int DARKELF_MAX_MP;
	public static int DRAGONKNIGHT_MAX_HP;
	public static int DRAGONKNIGHT_MAX_MP;
	public static int BLACKWIZARD_MAX_HP;
	public static int BLACKWIZARD_MAX_MP;

	public static int PRINCE_ADD_DAMAGEPC;
	public static int KNIGHT_ADD_DAMAGEPC;
	public static int ELF_ADD_DAMAGEPC;
	public static int WIZARD_ADD_DAMAGEPC;
	public static int DARKELF_ADD_DAMAGEPC;
	public static int DRAGONKNIGHT_ADD_DAMAGEPC;
	public static int BLACKWIZARD_ADD_DAMAGEPC;
	public static int LIMITLEVEL;
	public static int LV50_EXP;
	public static int LV51_EXP;
	public static int LV52_EXP;
	public static int LV53_EXP;
	public static int LV54_EXP;
	public static int LV55_EXP;
	public static int LV56_EXP;
	public static int LV57_EXP;
	public static int LV58_EXP;
	public static int LV59_EXP;
	public static int LV60_EXP;
	public static int LV61_EXP;
	public static int LV62_EXP;
	public static int LV63_EXP;
	public static int LV64_EXP;
	public static int LV65_EXP;
	public static int LV66_EXP;
	public static int LV67_EXP;
	public static int LV68_EXP;
	public static int LV69_EXP;
	public static int LV70_EXP;
	public static int LV71_EXP;
	public static int LV72_EXP;
	public static int LV73_EXP;
	public static int LV74_EXP;
	public static int LV75_EXP;
	public static int LV76_EXP;
	public static int LV77_EXP;
	public static int LV78_EXP;
	public static int LV79_EXP;
	public static int LV80_EXP;
	public static int LV81_EXP;
	public static int LV82_EXP;
	public static int LV83_EXP;
	public static int LV84_EXP;
	public static int LV85_EXP;
	public static int LV86_EXP;
	public static int LV87_EXP;
	public static int LV88_EXP;
	public static int LV89_EXP;
	public static int LV90_EXP;
	public static int LV91_EXP;
	public static int LV92_EXP;
	public static int LV93_EXP;
	public static int LV94_EXP;
	public static int LV95_EXP;
	public static int LV96_EXP;
	public static int LV97_EXP;
	public static int LV98_EXP;
	public static int LV99_EXP;
	
	public static String 기감_OPEN_TIME = null;
	public static String 본던_OPEN_TIME = null;
	public static String 용던_OPEN_TIME = null;
	public static String 잊혀진섬_OPEN_TIME = null;
	public static String 오만의탑정상_OPEN_TIME = null;
	public static String 테베라스_OPEN_TIME = null;
	public static String 상아탑8층_OPEN_TIME = null;
	public static String 던전초기화시간 = null;
	
	// skillpro.start
	public static double TURN_UNDEAD;
	public static int SHOCK_STUN;
	public static int 스턴렙차;
	public static int 아머브레이크;
	public static int 카운터배리어;

	// skillpro.end

	@Annotations.Configure(file = "./config/Neoserver.xml", key = "Metice_Buff")
	public static int[] Metice_Buff = { 21,26,42,48,54,79,148,151,164 };
	
	@Annotations.Configure(file = "./config/Neoserver.xml", key = "Zendor_Buff")
	public static int[] Zendor_Buff = { 26, 42, 48 };

	@Annotations.Configure(file = "./config/Neoserver.xml", key = "Newhunter")
	public static int[] Newhunter = { 32864, 32936, 630, 49 };

	@Annotations.Configure(file = "./config/Neoserver.xml", key = "KillDeath")
	public static int killdeath = 45;

	@Annotations.Configure(file = "./config/Neoserver.xml", key = "ipCount")
	public static int ipCount = 5;

	@Annotations.Configure(file = "./config/Neoserver.xml", key = "Wblesschance")
	public static double Wblesschance = 1.0;

	@Annotations.Configure(file = "./config/Neoserver.xml", key = "scarecrow")
	public static int scarecrow = 10;

	/** 데이터베이스 풀 관련 */
	public static int min;
	public static int max;
	public static boolean run;

	/** Configuration files */
	public static final String Neoserver1_FILE = "./config/Neoserver.xml";
	public static final String Neoserver_FILE = "./config/Neoserver.properties";
	public static final String SERVER_CONFIG_FILE = "./config/server.properties";
	public static final String RATES_CONFIG_FILE = "./config/rates.properties";
	public static final String HUNT_CONFIG_FILE = "./config/hunt.properties";
	public static final String BOT_SETTINGS_FILE = "./config/botsettings.properties";
	public static final String ALT_SETTINGS_FILE = "./config/altsettings.properties";
	public static final String CHAR_SETTINGS_CONFIG_FILE = "./config/charsettings.properties";
	public static final String ADD_CPMW_SYSTEM = "./config/cpmwaddsystem.properties";
	public static final String SKILLPRO = "./config/skillpro.properties";
	public static final String AUTOHUNT_SETTINGS_CONFIG_FILE = "./config/Autohuntsettings.properties";
	public static boolean shutdown = false;
	// 로그 표현할것인지
	public static boolean LOGGER = true;
	// 패킷 표현 할것인지
	public static boolean PACKET = false;

	/** 그 외의 설정 */

	// NPC로부터 들이마실 수 있는 MP한계
	public static final int MANA_DRAIN_LIMIT_PER_NPC = 40;

	// 1회의 공격으로 들이마실 수 있는 MP한계(SOM, 강철 SOM)
	public static final int MANA_DRAIN_LIMIT_PER_SOM_ATTACK = 9;

	public static void load() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("reloading " + _log.getName().substring(_log.getName().lastIndexOf(".") + 1) + "...");
		System.out.println("OK! " + timer.elapsedTimeMillis() + "ms");
		// server.properties
		try {
			Properties serverSettings = new Properties();
			FileReader is = new FileReader(new File(SERVER_CONFIG_FILE));
			serverSettings.load(is);
			new ConfigLoader().load(Config.class);
			is.close();

			/** 데이터 베이스 풀 */
			min = Integer.parseInt(serverSettings.getProperty("min"));
			max = Integer.parseInt(serverSettings.getProperty("max"));
			run = Boolean.parseBoolean(serverSettings.getProperty("run"));
			RANKTIME = Integer.parseInt(serverSettings.getProperty("ranktime", "7200"));
			서버이름 = serverSettings.getProperty("ServerName", "서버");
			GAME_SERVER_TYPE = Integer.parseInt(serverSettings.getProperty("ServerType", "0"));

			GAME_SERVER_HOST_NAME = serverSettings.getProperty("GameserverHostname", "*");

			GAME_SERVER_PORT = Integer.parseInt(serverSettings.getProperty("GameserverPort", "2000"));
			WANTED_ADENA_CHARGE = Double.parseDouble(serverSettings.getProperty("WANTED_ADENA_CHARGE", "0.7"));

			WANTED_ADENA_52 = Integer.parseInt(serverSettings.getProperty("WANTED_ADENA_52", "500000"));
			WANTED_ADENA_55 = Integer.parseInt(serverSettings.getProperty("WANTED_ADENA_55", "500000"));
			WANTED_ADENA_60 = Integer.parseInt(serverSettings.getProperty("WANTED_ADENA_60", "500000"));
			WANTED_ADENA_65 = Integer.parseInt(serverSettings.getProperty("WANTED_ADENA_65", "500000"));
			WANTED_ADENA_70 = Integer.parseInt(serverSettings.getProperty("WANTED_ADENA_70", "500000"));
			WANTED_ADENA_75 = Integer.parseInt(serverSettings.getProperty("WANTED_ADENA_75", "500000"));
			WANTED_ADENA_80 = Integer.parseInt(serverSettings.getProperty("WANTED_ADENA_80", "500000"));

			MANAGER_LOG_SAVE_DELAY = Integer.parseInt(serverSettings.getProperty("MANAGER_LOG_SAVE_DELAY", "3600"));
			
			DB_DRIVER = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");

			DB_URL = serverSettings.getProperty("URL", "jdbc:mysql://localhost/l1jdb?useUnicode=true&characterEncoding=euckr");

			DB_LOGIN = serverSettings.getProperty("Login", "root");

			DB_PASSWORD = serverSettings.getProperty("Password", "");

			THREAD_P_TYPE_GENERAL = Integer.parseInt(serverSettings.getProperty("GeneralThreadPoolType", "0"), 10);

			THREAD_P_SIZE_GENERAL = Integer.parseInt(serverSettings.getProperty("GeneralThreadPoolSize", "0"), 10);

			CLIENT_LANGUAGE = Integer.parseInt(serverSettings.getProperty("ClientLanguage", "1"));

			TIME_ZONE = serverSettings.getProperty("TimeZone", "KST");

			HOSTNAME_LOOKUPS = Boolean.parseBoolean(serverSettings.getProperty("HostnameLookups", "false"));

			AUTOMATIC_KICK = Integer.parseInt(serverSettings.getProperty("AutomaticKick", "10"));

			AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(serverSettings.getProperty("AutoCreateAccounts", "true"));

			MAX_ONLINE_USERS = Short.parseShort(serverSettings.getProperty("MaximumOnlineUsers", "30"));

			CACHE_MAP_FILES = Boolean.parseBoolean(serverSettings.getProperty("CacheMapFiles", "false"));

			LOAD_V2_MAP_FILES = Boolean.parseBoolean(serverSettings.getProperty("LoadV2MapFiles", "false"));

			CHECK_MOVE_INTERVAL = Boolean.parseBoolean(serverSettings.getProperty("CheckMoveInterval", "false"));

			CHECK_ATTACK_INTERVAL = Boolean.parseBoolean(serverSettings.getProperty("CheckAttackInterval", "false"));

			CHECK_SPELL_INTERVAL = Boolean.parseBoolean(serverSettings.getProperty("CheckSpellInterval", "false"));
			
			CHECK_SPELL_EARTH_BIND = Boolean.parseBoolean(serverSettings.getProperty("CheckSpellEarthBind", "false"));
			 
			MOVE_SPEED_VALUE = Double.parseDouble(serverSettings.getProperty("MoveSpeedValue", "1.0"));
			ATTACK_SPEED_VALUE = Double.parseDouble(serverSettings.getProperty("AttackSpeedValue", "1.0"));

			INJUSTICE_COUNT = Short.parseShort(serverSettings.getProperty("InjusticeCount", "10"));

			JUSTICE_COUNT = Integer.parseInt(serverSettings.getProperty("JusticeCount", "4"));

			CHECK_STRICTNESS = Integer.parseInt(serverSettings.getProperty("CheckStrictness", "102"));

			LOGGING_WEAPON_ENCHANT = Byte.parseByte(serverSettings.getProperty("LoggingWeaponEnchant", "0"));

			LOGGING_ARMOR_ENCHANT = Byte.parseByte(serverSettings.getProperty("LoggingArmorEnchant", "0"));

			LOGGING_CHAT_NORMAL = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatNormal", "false"));

			LOGGING_CHAT_WHISPER = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatWhisper", "false"));

			LOGGING_CHAT_SHOUT = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatShout", "false"));

			LOGGING_CHAT_WORLD = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatWorld", "false"));

			LOGGING_CHAT_CLAN = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatClan", "false"));

			LOGGING_CHAT_PARTY = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatParty", "false"));

			LOGGING_CHAT_COMBINED = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatCombined", "false"));

			LOGGING_CHAT_CHAT_PARTY = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatChatParty", "false"));

			AUTOSAVE_INTERVAL = Integer.parseInt(serverSettings.getProperty("AutosaveInterval", "1200"), 10);

			AUTOSAVE_INTERVAL_INVENTORY = Integer.parseInt(serverSettings.getProperty("AutosaveIntervalOfInventory", "300"), 10);

			SKILLTIMER_IMPLTYPE = Integer.parseInt(serverSettings.getProperty("SkillTimerImplType", "1"));

			NPCAI_IMPLTYPE = Integer.parseInt(serverSettings.getProperty("NpcAIImplType", "1"));

			TELNET_SERVER = Boolean.parseBoolean(serverSettings.getProperty("TelnetServer", "false"));

			TELNET_SERVER_PORT = Integer.parseInt(serverSettings.getProperty("TelnetServerPort", "23"));

			PC_RECOGNIZE_RANGE = Integer.parseInt(serverSettings.getProperty("PcRecognizeRange", "20"));

			CHARACTER_CONFIG_IN_SERVER_SIDE = Boolean.parseBoolean(serverSettings.getProperty("CharacterConfigInServerSide", "true"));

			ALLOW_2PC = Boolean.parseBoolean(serverSettings.getProperty("Allow2PC", "true"));

			LEVEL_DOWN_RANGE = Integer.parseInt(serverSettings.getProperty("LevelDownRange", "0"));

			SEND_PACKET_BEFORE_TELEPORT = Boolean.parseBoolean(serverSettings.getProperty("SendPacketBeforeTeleport", "false"));

			DETECT_DB_RESOURCE_LEAKS = Boolean.parseBoolean(serverSettings.getProperty("EnableDatabaseResourceLeaksDetection", "false"));

			KEYWORD_USE = Boolean.parseBoolean(serverSettings.getProperty("KeyWordUse", "true"));
			
			랭킹동상x좌표 = Integer.parseInt(serverSettings.getProperty("Rankingx", "3"));
			랭킹동상y좌표 = Integer.parseInt(serverSettings.getProperty("Rankingy", "3"));
			다엘생성 = Boolean.parseBoolean(serverSettings.getProperty("CreateDarkElf", "false"));
			자동물약 = Boolean.parseBoolean(serverSettings.getProperty("isAutoPotion", "false"));
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + SERVER_CONFIG_FILE + " File.");
		}
		try {
			Properties AdenatradeSettings = new Properties();
			InputStream is = new FileInputStream(new File(ADD_CPMW_SYSTEM));
			AdenatradeSettings.load(is);
			is.close();
			ADENASHOP_LEVEL = Integer.parseInt(AdenatradeSettings.getProperty("shoplevel", "52"));
			MAX_SELL_ADENA = Integer.parseInt(AdenatradeSettings.getProperty("maxselladena", "1000000000"));
			MIN_SELL_ADENA = Integer.parseInt(AdenatradeSettings.getProperty("minselladena", "100000000"));
			MIN_SELL_CASH = Integer.parseInt(AdenatradeSettings.getProperty("minsellcash", "10000"));
			MAX_SELL_CASH = Integer.parseInt(AdenatradeSettings.getProperty("maxsellcash", "100000"));
			ADD_ADENA_BY_DOLL_RATE = Double.parseDouble(AdenatradeSettings.getProperty("addadenabydollrate", "2.0"));
			ADD_ADENA_BY_DOLL_ID = Integer.parseInt(AdenatradeSettings.getProperty("addadenabydollid", "400054"));
			ADD_EXP_BY_DOLL_RATE = Double.parseDouble(AdenatradeSettings.getProperty("addexpbydollrate", "2.0"));
			ADD_EXP_BY_DOLL_ID = Integer.parseInt(AdenatradeSettings.getProperty("addexpbydollid", "400054"));
			MOVE_ADD_INRERVAL = Integer.parseInt(AdenatradeSettings.getProperty("moveaddinterval", "-10"));
			ATTACK_ADD_INRERVAL = Integer.parseInt(AdenatradeSettings.getProperty("attackaddinterval", "0"));
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + ADD_CPMW_SYSTEM + " File.");
		}
		try {
			Properties AutohuntSettings = new Properties();
			InputStream is = new FileInputStream(new File(AUTOHUNT_SETTINGS_CONFIG_FILE));
			AutohuntSettings.load(is);
			is.close();
			자동사냥충전시간 = Integer.parseInt(AutohuntSettings.getProperty("autoHuntChargeTime", "60000"));
			자동사냥법사공격스킬발동확률 = Integer.parseInt(AutohuntSettings.getProperty("AttackSkillRate", "20"));
			자동사냥트리플발동확률 = Integer.parseInt(AutohuntSettings.getProperty("TrippleRate", "20"));
			자동사냥아이템픽업 = Boolean.parseBoolean(AutohuntSettings.getProperty("IsPickupItem", "true"));
			자동사냥뮨 = Integer.parseInt(AutohuntSettings.getProperty("ImmuneHPpercentage", "80"));
			자동패널티배율 = Double.parseDouble(AutohuntSettings.getProperty("AutoPenalyRate", "1.0"));
		} catch (Exception e) {
			_log.log(Level.SEVERE, "Config.에서 에러가 발생했습니다.", e);
			throw new Error("Failed to Load " + AUTOHUNT_SETTINGS_CONFIG_FILE + " File.");
		}
		// rates.properties
		try {
			Properties rateSettings = new Properties();
			FileReader is = new FileReader(new File(RATES_CONFIG_FILE));
			rateSettings.load(is);
			is.close();

			systime = Integer.parseInt(rateSettings.getProperty("systime", "30"));
			sys1 = rateSettings.getProperty("sys1", "");
			sys2 = rateSettings.getProperty("sys2", "");
			sys3 = rateSettings.getProperty("sys3", "");
			sys4 = rateSettings.getProperty("sys4", "");
			sys5 = rateSettings.getProperty("sys5", "");
			sys6 = rateSettings.getProperty("sys6", "");
			sys7 = rateSettings.getProperty("sys7", "");
			sys8 = rateSettings.getProperty("sys8", "");
			sys9 = rateSettings.getProperty("sys9", "");
			sys10 = rateSettings.getProperty("sys10", "");
			sys11 = rateSettings.getProperty("sys11", "");
			sys12 = rateSettings.getProperty("sys12", "");
			sys13 = rateSettings.getProperty("sys13", "");
			sys14 = rateSettings.getProperty("sys14", "");
			sys15 = rateSettings.getProperty("sys15", "");
			sys16 = rateSettings.getProperty("sys16", "");
			
			RATE_XP = Double.parseDouble(rateSettings.getProperty("RateXp", "1.0"));

			RATE_LAWFUL = Double.parseDouble(rateSettings.getProperty("RateLawful", "1.0"));

			RATE_KARMA = Double.parseDouble(rateSettings.getProperty("RateKarma", "1.0"));

			RATE_DROP_ADENA = Double.parseDouble(rateSettings.getProperty("RateDropAdena", "1.0"));

			RATE_DROP_ITEMS = Double.parseDouble(rateSettings.getProperty("RateDropItems", "1.0"));

			ENCHANT_CHANCE_WEAPON = Integer.parseInt(rateSettings.getProperty("EnchantChanceWeapon", "68"));

			ENCHANT_CHANCE_ARMOR = Integer.parseInt(rateSettings.getProperty("EnchantChanceArmor", "52"));

			RATE_WEIGHT_LIMIT = Double.parseDouble(rateSettings.getProperty("RateWeightLimit", "1"));

			RATE_WEIGHT_LIMIT_PET = Double.parseDouble(rateSettings.getProperty("RateWeightLimitforPet", "1"));

			RATE_SHOP_SELLING_PRICE = Double.parseDouble(rateSettings.getProperty("RateShopSellingPrice", "1.0"));

			RATE_SHOP_PURCHASING_PRICE = Double.parseDouble(rateSettings.getProperty("RateShopPurchasingPrice", "1.0"));

			RATE_FISHING_EXP = Integer.parseInt(rateSettings.getProperty("RateFishingExp", "25000"));

			CREATE_CHANCE_DIARY = Integer.parseInt(rateSettings.getProperty("CreateChanceDiary", "33"));

			CREATE_CHANCE_RECOLLECTION = Integer.parseInt(rateSettings.getProperty("CreateChanceRecollection", "90"));

			CREATE_CHANCE_MYSTERIOUS = Integer.parseInt(rateSettings.getProperty("CreateChanceMysterious", "90"));

			CREATE_CHANCE_PROCESSING = Integer.parseInt(rateSettings.getProperty("CreateChanceProcessing", "90"));

			CREATE_CHANCE_PROCESSING_DIAMOND = Integer.parseInt(rateSettings.getProperty("CreateChanceProcessingDiamond", "90"));

			CREATE_CHANCE_DANTES = Integer.parseInt(rateSettings.getProperty("CreateChanceDantes", "90"));

			CREATE_CHANCE_ANCIENT_AMULET = Integer.parseInt(rateSettings.getProperty("CreateChanceAncientAmulet", "90"));

			CREATE_CHANCE_HISTORY_BOOK = Integer.parseInt(rateSettings.getProperty("CreateChanceHistoryBook", "5"));

			ENCHANT_CHANCE_WATER = Integer.parseInt(rateSettings.getProperty("WaterEnchant", "5"));
			ENCHANT_CHANCE_EARTH = Integer.parseInt(rateSettings.getProperty("EarthEnchant", "5"));
			ENCHANT_CHANCE_WIND = Integer.parseInt(rateSettings.getProperty("WindEnchant", "5"));
			ENCHANT_CHANCE_FIRE = Integer.parseInt(rateSettings.getProperty("FireEnchant", "5"));
			ATTR_ENCHANT_LEVEL = Integer.parseInt(rateSettings.getProperty("AttrEnchantLevel", "5"));
			수배작동유무 = Boolean.parseBoolean(rateSettings.getProperty("Wantedrat", "false"));
			수배1단 = Integer.parseInt(rateSettings.getProperty("WantedONE", "20000000"));
			수배2단 = Integer.parseInt(rateSettings.getProperty("WantedToo", "40000000"));
			수배3단 = Integer.parseInt(rateSettings.getProperty("WantedThree", "60000000"));
			wing = Integer.parseInt(rateSettings.getProperty("wing", "100"));//0220수정
			npcdmg = Integer.parseInt(rateSettings.getProperty("npcdmg", "14"));
			MONSTER_DAMAGE_1 = Double.parseDouble(rateSettings.getProperty("MonsterDamage1", "1.2"));
			MONSTER_DAMAGE_2 = Double.parseDouble(rateSettings.getProperty("MonsterDamage2", "1.4"));
			MONSTER_DAMAGE_3 = Double.parseDouble(rateSettings.getProperty("MonsterDamage3", "1.6"));
			MONSTER_DAMAGE_4 = Double.parseDouble(rateSettings.getProperty("MonsterDamage4", "2.0"));
			MONSTER_DAMAGE_5 = Double.parseDouble(rateSettings.getProperty("MonsterDamage5", "2.0"));
			MONSTER_DAMAGE_6 = Double.parseDouble(rateSettings.getProperty("MonsterDamage6", "2.0"));
			EnchantChanceAccessory = Integer.parseInt(rateSettings.getProperty("EnchantChanceAccessory", "5"));
			BlassEnchantChanceAccessory = Integer.parseInt(rateSettings.getProperty("BlassEnchantChanceAccessory", "20"));

			EnchantChanceRun0 = Integer.parseInt(rateSettings.getProperty("EnchantChanceRun0", "5"));
			EnchantChanceRun1 = Integer.parseInt(rateSettings.getProperty("EnchantChanceRun1", "5"));
			EnchantChanceRun2 = Integer.parseInt(rateSettings.getProperty("EnchantChanceRun2", "5"));
			EnchantChanceRun3 = Integer.parseInt(rateSettings.getProperty("EnchantChanceRun3", "5"));
			EnchantChanceRun4 = Integer.parseInt(rateSettings.getProperty("EnchantChanceRun4", "5"));
			EnchantChanceRun5 = Integer.parseInt(rateSettings.getProperty("EnchantChanceRun5", "5"));
			EnchantChanceRun6 = Integer.parseInt(rateSettings.getProperty("EnchantChanceRun6", "5"));
			EnchantChanceRun7 = Integer.parseInt(rateSettings.getProperty("EnchantChanceRun7", "5"));
			EnchantChanceRun8 = Integer.parseInt(rateSettings.getProperty("EnchantChanceRun8", "5"));
			EnchantChanceRun9 = Integer.parseInt(rateSettings.getProperty("EnchantChanceRun9", "5"));
			BlassEnchantChanceRun = Integer.parseInt(rateSettings.getProperty("BlassEnchantChanceRun", "20"));

			weaponbless = Double.parseDouble(rateSettings.getProperty("weaponbless", "14.0"));
			인형축복 = Double.parseDouble(rateSettings.getProperty("dollbless", "14.0"));
			RATE_7_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_7_Dmg_Rate", "1.5"));
			RATE_8_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_8_Dmg_Rate", "1.5"));
			RATE_9_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_9_Dmg_Rate", "2.0"));
			RATE_10_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_10_Dmg_Rate", "2.0"));
			RATE_11_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_11_Dmg_Rate", "2.0"));
			RATE_12_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_12_Dmg_Rate", "2.0"));
			RATE_13_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_13_Dmg_Rate", "2.0"));
			RATE_14_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_14_Dmg_Rate", "2.0"));
			RATE_15_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_15_Dmg_Rate", "2.0"));
			RATE_16_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_16_Dmg_Rate", "2.5"));
			RATE_17_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_17_Dmg_Rate", "2.5"));
			RATE_18_DMG_RATE = Double.parseDouble(rateSettings.getProperty("Rate_18_Dmg_Rate", "2.5"));

			RATE_7_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_7_Dmg_Per", "5"));
			RATE_8_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_8_Dmg_Per", "10"));
			RATE_9_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_9_Dmg_Per", "20"));
			RATE_10_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_10_Dmg_Per", "30"));
			RATE_11_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_11_Dmg_Per", "40"));
			RATE_12_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_12_Dmg_Per", "50"));
			RATE_13_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_13_Dmg_Per", "60"));
			RATE_14_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_14_Dmg_Per", "70"));
			RATE_15_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_15_Dmg_Per", "80"));
			RATE_16_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_16_Dmg_Per", "90"));
			RATE_17_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_17_Dmg_Per", "90"));
			RATE_18_DMG_PER = Integer.parseInt(rateSettings.getProperty("Rate_18_Dmg_Per", "100"));

			adddmg7to1 = Double.parseDouble(rateSettings.getProperty("adddmg7to1", "2.5"));
			adddmg7to2 = Double.parseDouble(rateSettings.getProperty("adddmg7to2", "2.5"));
			adddmg7to3 = Double.parseDouble(rateSettings.getProperty("adddmg7to3", "2.5"));
			adddmg8to1 = Double.parseDouble(rateSettings.getProperty("adddmg8to1", "2.5"));
			adddmg8to2 = Double.parseDouble(rateSettings.getProperty("adddmg8to2", "2.5"));
			adddmg8to3 = Double.parseDouble(rateSettings.getProperty("adddmg8to3", "2.5"));
			adddmg9to1 = Double.parseDouble(rateSettings.getProperty("adddmg9to1", "2.5"));
			adddmg9to2 = Double.parseDouble(rateSettings.getProperty("adddmg9to2", "2.5"));
			adddmg9to3 = Double.parseDouble(rateSettings.getProperty("adddmg9to3", "2.5"));
			adddmg10to1 = Double.parseDouble(rateSettings.getProperty("adddmg10to1", "2.5"));
			adddmg10to2 = Double.parseDouble(rateSettings.getProperty("adddmg10to2", "2.5"));
			adddmg10to3 = Double.parseDouble(rateSettings.getProperty("adddmg10to3", "2.5"));
			adddmg11to1 = Double.parseDouble(rateSettings.getProperty("adddmg11to1", "2.5"));
			adddmg11to2 = Double.parseDouble(rateSettings.getProperty("adddmg11to2", "2.5"));
			adddmg11to3 = Double.parseDouble(rateSettings.getProperty("adddmg11to3", "2.5"));
			adddmg12to1 = Double.parseDouble(rateSettings.getProperty("adddmg12to1", "2.5"));
			adddmg12to2 = Double.parseDouble(rateSettings.getProperty("adddmg12to2", "2.5"));
			adddmg12to3 = Double.parseDouble(rateSettings.getProperty("adddmg12to3", "2.5"));
			adddmg13to1 = Double.parseDouble(rateSettings.getProperty("adddmg13to1", "2.5"));
			adddmg13to2 = Double.parseDouble(rateSettings.getProperty("adddmg13to2", "2.5"));
			adddmg13to3 = Double.parseDouble(rateSettings.getProperty("adddmg13to3", "2.5"));
			adddmg14to1 = Double.parseDouble(rateSettings.getProperty("adddmg14to1", "2.5"));
			adddmg14to2 = Double.parseDouble(rateSettings.getProperty("adddmg14to2", "2.5"));
			adddmg14to3 = Double.parseDouble(rateSettings.getProperty("adddmg14to3", "2.5"));
			adddmg15to1 = Double.parseDouble(rateSettings.getProperty("adddmg15to1", "2.5"));
			adddmg15to2 = Double.parseDouble(rateSettings.getProperty("adddmg15to2", "2.5"));
			adddmg15to3 = Double.parseDouble(rateSettings.getProperty("adddmg15to3", "2.5"));
			룬착용추가경험치1 = Double.parseDouble(rateSettings.getProperty("RuneAddExp1", "2.5"));
			룬착용추가경험치2 = Double.parseDouble(rateSettings.getProperty("RuneAddExp2", "2.5"));
			룬착용추가경험치3 = Double.parseDouble(rateSettings.getProperty("RuneAddExp3", "2.5"));
			룬착용추가경험치4 = Double.parseDouble(rateSettings.getProperty("RuneAddExp4", "2.5"));
			룬착용추가경험치5 = Double.parseDouble(rateSettings.getProperty("RuneAddExp5", "2.5"));
			룬착용추가경험치6 = Double.parseDouble(rateSettings.getProperty("RuneAddExp6", "2.5"));
			룬착용추가경험치7 = Double.parseDouble(rateSettings.getProperty("RuneAddExp7", "2.5"));
			룬착용추가경험치8 = Double.parseDouble(rateSettings.getProperty("RuneAddExp8", "2.5"));

			AinHasad_dell = Integer.parseInt(rateSettings.getProperty("ainHasad_dell", "2"));
			스냅퍼최대인챈 = Integer.parseInt(rateSettings.getProperty("SnapperMaxEnchant", "5"));
			룸티스최대인챈 = Integer.parseInt(rateSettings.getProperty("RoomteeceMaxEnchant", "5"));
			룬최대인챈 = Integer.parseInt(rateSettings.getProperty("runeMaxEnchant", "5"));
			장신구최대인챈 = Integer.parseInt(rateSettings.getProperty("acaccessoryMaxEnchant", "5"));
			nomal_orim = Integer.parseInt(rateSettings.getProperty("nomal_orim", "5"));
			Bless_orim = Integer.parseInt(rateSettings.getProperty("Bless_orim", "5"));
			일층지배확률 = Integer.parseInt(rateSettings.getProperty("jibae_1", "10"));
			이층지배확률 = Integer.parseInt(rateSettings.getProperty("jibae_2", "10"));
			삼층지배확률 = Integer.parseInt(rateSettings.getProperty("jibae_3", "10"));
			사층지배확률 = Integer.parseInt(rateSettings.getProperty("jibae_4", "10"));
			오층지배확률 = Integer.parseInt(rateSettings.getProperty("jibae_5", "10"));
			육층지배확률 = Integer.parseInt(rateSettings.getProperty("jibae_6", "10"));
			칠층지배확률 = Integer.parseInt(rateSettings.getProperty("jibae_7", "10"));
			팔층지배확률 = Integer.parseInt(rateSettings.getProperty("jibae_8", "10"));
			구층지배확률 = Integer.parseInt(rateSettings.getProperty("jibae_9", "10"));
			십층지배확률 = Integer.parseInt(rateSettings.getProperty("jibae_10", "10"));
			축오림제작확률 = Integer.parseInt(rateSettings.getProperty("bless_orim_craft", "10"));
			데스나이트메테오 = Double.parseDouble(rateSettings.getProperty("DollMeteo", "10"));
			데스나이트헬파이어 = Double.parseDouble(rateSettings.getProperty("DollHellfire", "10"));
			데스나이트선버스트 = Double.parseDouble(rateSettings.getProperty("DollSunburst", "10"));
			데스나이트경험치 = Double.parseDouble(rateSettings.getProperty("DeathknightAddExp", "10"));
			무료인형경험치 = Double.parseDouble(rateSettings.getProperty("FreeDollAddExp", "10"));
			MAX_ARMOR = Integer.parseInt(rateSettings.getProperty("MaxArmor", "10"));
			MAX_WEAPON = Integer.parseInt(rateSettings.getProperty("MaxWeapon", "12"));
			
			잊섬1봇카운트 = Integer.parseInt(rateSettings.getProperty("olvidado1", "9"));
			잊섬2봇카운트 = Integer.parseInt(rateSettings.getProperty("olvidado2", "9"));
			화둥봇카운트 = Integer.parseInt(rateSettings.getProperty("firenest", "9"));
		    용계봇카운트 = Integer.parseInt(rateSettings.getProperty("dragon", "9"));
			풍둥봇카운트 = Integer.parseInt(rateSettings.getProperty("windnest", "9"));
			용던1봇카운트 = Integer.parseInt(rateSettings.getProperty("dragondun1", "9"));
			용던2봇카운트 = Integer.parseInt(rateSettings.getProperty("dragondun2", "9"));
			용던3봇카운트 = Integer.parseInt(rateSettings.getProperty("dragondun3", "9"));
			용던4봇카운트 = Integer.parseInt(rateSettings.getProperty("dragondun4", "9"));
			용던5봇카운트 = Integer.parseInt(rateSettings.getProperty("dragondun5", "9"));
			용던6봇카운트 = Integer.parseInt(rateSettings.getProperty("dragondun6", "9"));
			용던7봇카운트 = Integer.parseInt(rateSettings.getProperty("dragondun7", "9"));
			본던1봇카운트 = Integer.parseInt(rateSettings.getProperty("masterdun1", "9"));
			본던2봇카운트 = Integer.parseInt(rateSettings.getProperty("masterdun2", "9"));
			본던3봇카운트 = Integer.parseInt(rateSettings.getProperty("masterdun3", "9"));
			본던4봇카운트 = Integer.parseInt(rateSettings.getProperty("masterdun4", "9"));
			본던5봇카운트 = Integer.parseInt(rateSettings.getProperty("masterdun5", "9"));
			본던6봇카운트 = Integer.parseInt(rateSettings.getProperty("masterdun6", "9"));
			본던7봇카운트 = Integer.parseInt(rateSettings.getProperty("masterdun7", "9"));
			기감1봇카운트 = Integer.parseInt(rateSettings.getProperty("gprison1", "9"));
			기감2봇카운트 = Integer.parseInt(rateSettings.getProperty("gprison2", "9"));
			상아탑4봇카운트 = Integer.parseInt(rateSettings.getProperty("sangatop4", "9"));
			상아탑5봇카운트 = Integer.parseInt(rateSettings.getProperty("sangatop5", "9"));
			상아탑6봇카운트 = Integer.parseInt(rateSettings.getProperty("sangatop6", "9"));
			상아탑7봇카운트 = Integer.parseInt(rateSettings.getProperty("sangatop7", "9"));
			오만1봇카운트 = Integer.parseInt(rateSettings.getProperty("omantop1", "9"));
			오만2봇카운트 = Integer.parseInt(rateSettings.getProperty("omantop2", "9"));
			오만3봇카운트 = Integer.parseInt(rateSettings.getProperty("omantop3", "9"));
			오만4봇카운트 = Integer.parseInt(rateSettings.getProperty("omantop4", "9"));
			오만5봇카운트 = Integer.parseInt(rateSettings.getProperty("omantop5", "9"));
			오만6봇카운트 = Integer.parseInt(rateSettings.getProperty("omantop6", "9"));
			오만7봇카운트 = Integer.parseInt(rateSettings.getProperty("omantop7", "9"));
			오만8봇카운트 = Integer.parseInt(rateSettings.getProperty("omantop8", "9"));
			오만9봇카운트 = Integer.parseInt(rateSettings.getProperty("omantop9", "9"));
			오만10봇카운트 = Integer.parseInt(rateSettings.getProperty("omantop10", "9"));
			오만정상봇카운트 = Integer.parseInt(rateSettings.getProperty("omantop1", "9"));
			지배1봇카운트 = Integer.parseInt(rateSettings.getProperty("jibetop1", "9"));
			지배2봇카운트 = Integer.parseInt(rateSettings.getProperty("jibetop2", "9"));
			지배3봇카운트 = Integer.parseInt(rateSettings.getProperty("jibetop3", "9"));
			지배4봇카운트 = Integer.parseInt(rateSettings.getProperty("jibetop4", "9"));
			지배5봇카운트 = Integer.parseInt(rateSettings.getProperty("jibetop5", "9"));
			지배6봇카운트 = Integer.parseInt(rateSettings.getProperty("jibetop6", "9"));
			지배7봇카운트 = Integer.parseInt(rateSettings.getProperty("jibetop7", "9"));
			지배8봇카운트 = Integer.parseInt(rateSettings.getProperty("jibetop8", "9"));
			지배9봇카운트 = Integer.parseInt(rateSettings.getProperty("jibetop9", "9"));
			지배10봇카운트 = Integer.parseInt(rateSettings.getProperty("jibetop10", "9"));
			지배정상봇카운트 = Integer.parseInt(rateSettings.getProperty("jibetopV", "9"));
			지배결계1봇카운트 = Integer.parseInt(rateSettings.getProperty("jibeline1", "9"));
			지배결계2봇카운트 = Integer.parseInt(rateSettings.getProperty("jibeline2", "9"));
			엘모어봇카운트 = Integer.parseInt(rateSettings.getProperty("elmofight", "9"));
			보스봇카운트 = Integer.parseInt(rateSettings.getProperty("bossfight", "9"));
			오만보스봇카운트 = Integer.parseInt(rateSettings.getProperty("Obossfight", "9"));
			지배보스봇카운트 = Integer.parseInt(rateSettings.getProperty("Jbossfight", "9"));
			인원배율 = Double.parseDouble(rateSettings.getProperty("whoRate", "1"));
			//사냥터별 봇 외부화
			SANGATOP_45_RANGE = Integer.parseInt(rateSettings.getProperty("sangatop45range", "0"));
			SANGATOP_45_ADD_HIT = Integer.parseInt(rateSettings.getProperty("sangatop45addHit", "0"));
			SANGATOP_45_ADD_DMG = Integer.parseInt(rateSettings.getProperty("sangatop45addDmg", "0"));
			
			HAWDOONG_RANGE = Integer.parseInt(rateSettings.getProperty("hwadoongrange", "0"));
			HAWDOONG_ADD_HIT = Integer.parseInt(rateSettings.getProperty("hwadoongaddHit", "0"));
			HAWDOONG_ADD_DMG = Integer.parseInt(rateSettings.getProperty("hwadoongaddDmg", "0"));
			
			BONDUN_16_RANGE = Integer.parseInt(rateSettings.getProperty("bondun16range", "0"));
			BONDUN_16_ADD_HIT = Integer.parseInt(rateSettings.getProperty("bondun16addHit", "0"));
			BONDUN_16_ADD_DMG = Integer.parseInt(rateSettings.getProperty("bondun16addDmg", "0"));
			
			BONDUN_7_RANGE = Integer.parseInt(rateSettings.getProperty("bondun7range", "0"));
			BONDUN_7_ADD_HIT = Integer.parseInt(rateSettings.getProperty("bondun7addHit", "0"));
			BONDUN_7_ADD_DMG = Integer.parseInt(rateSettings.getProperty("bondun7addDmg", "0"));
			
			GIGAM_12_RANGE = Integer.parseInt(rateSettings.getProperty("gigam12range", "0"));
			GIGAM_12_ADD_HIT = Integer.parseInt(rateSettings.getProperty("gigam12addHit", "0"));
			GIGAM_12_ADD_DMG = Integer.parseInt(rateSettings.getProperty("gigam12addDmg", "0"));
			
			OMAN_18_RANGE = Integer.parseInt(rateSettings.getProperty("oman18range", "0"));
			OMAN_18_ADD_HIT = Integer.parseInt(rateSettings.getProperty("oman18addHit", "0"));
			OMAN_18_ADD_DMG = Integer.parseInt(rateSettings.getProperty("oman18addDmg", "0"));
			
			OMAN_9_RANGE = Integer.parseInt(rateSettings.getProperty("oman9range", "0"));
			OMAN_9_ADD_HIT = Integer.parseInt(rateSettings.getProperty("oman9addHit", "0"));
			OMAN_9_ADD_DMG = Integer.parseInt(rateSettings.getProperty("oman9addDmg", "0"));
			
			TOP_RANGE = Integer.parseInt(rateSettings.getProperty("toprange", "0"));
			TOP_ADD_HIT = Integer.parseInt(rateSettings.getProperty("topaddHit", "0"));
			TOP_ADD_DMG = Integer.parseInt(rateSettings.getProperty("totaddDmg", "0"));
			
			GYULGE_12_RANGE = Integer.parseInt(rateSettings.getProperty("gyulge12range", "0"));
			GYULGE_12_ADD_HIT = Integer.parseInt(rateSettings.getProperty("gyulge12addHit", "0"));
			GYULGE_12_ADD_DMG = Integer.parseInt(rateSettings.getProperty("gyulge12addDmg", "0"));
			
			FEATHER_NUM = Integer.parseInt(rateSettings.getProperty("FeatherNum", "6"));
			FEATHER_NUM1 = Integer.parseInt(rateSettings.getProperty("FeatherNum1", "2"));
			FEATHER_NUM2 = Integer.parseInt(rateSettings.getProperty("FeatherNum2", "3"));
			FEATHER_NUM3 = Integer.parseInt(rateSettings.getProperty("FeatherNum3", "12"));
			useritem = Integer.parseInt(rateSettings.getProperty("useritem", "1"));
			usercount = Integer.parseInt(rateSettings.getProperty("usercount", "1"));
			useritem1 = Integer.parseInt(rateSettings.getProperty("useritem1", "1"));
			usercount1 = Integer.parseInt(rateSettings.getProperty("usercount1", "1"));
			전체선물작동유무 = Boolean.parseBoolean(rateSettings.getProperty("GiftItem", "true"));
			FEATHER_TIME = Integer.parseInt(rateSettings.getProperty("FeatherTime", "15"));
			USERITEM1_TIME = Integer.parseInt(rateSettings.getProperty("UseritemTime", "15"));
			ETC_RANGE = Integer.parseInt(rateSettings.getProperty("etcrange", "0"));
			ETC_ADD_HIT = Integer.parseInt(rateSettings.getProperty("etcaddHit", "0"));
			ETC_ADD_DMG = Integer.parseInt(rateSettings.getProperty("etcaddDmg", "0"));
			경험치물약 = Integer.parseInt(rateSettings.getProperty("exppotion", "0"));
			낚시성공확률 = Integer.parseInt(rateSettings.getProperty("fishSuccessRate", "1"));
			ORIM_ACCESS_ENCHANT_SCROLL_USE_LEVEL = Integer.parseInt(rateSettings.getProperty("ORIM_ACCESS_ENCHANT_SCROLL_USE_LEVEL", "8")); // 오림의장신구마법주문서 사용가능할 레벨
			ORIM_ACCESS_ENCHANT_SCROLL_CHANCE = Integer.parseInt(rateSettings.getProperty("ORIM_ACCESS_ENCHANT_SCROLL_CHANCE", "8")); // 오림의장신구마법주문서 성공확률
			ORIM_ACCESS_ENCHANT_SCROLL_DECREASE_CHANCE = Integer.parseInt(rateSettings.getProperty("ORIM_ACCESS_ENCHANT_SCROLL_DECREASE_CHANCE", "8")); // 오림의장신구마법주문서 인챈당 감소확률
			무기디지즈확률1 = Integer.parseInt(rateSettings.getProperty("weaponDisease1", "0"));
			무기디지즈확률2 = Integer.parseInt(rateSettings.getProperty("weaponDisease2", "0"));
			무기디지즈확률3 = Integer.parseInt(rateSettings.getProperty("weaponDisease3", "0"));
			무기디지즈확률4 = Integer.parseInt(rateSettings.getProperty("weaponDisease4", "0"));
			무기디지즈확률5 = Integer.parseInt(rateSettings.getProperty("weaponDisease5", "0"));
			무기디지즈확률6 = Integer.parseInt(rateSettings.getProperty("weaponDisease6", "0"));
			무기디지즈지속시간 = Integer.parseInt(rateSettings.getProperty("weaponDiseaseTime", "0"));
			인형합성확률1단계 = Integer.parseInt(rateSettings.getProperty("dollcraft1", "0"));
			인형합성확률2단계 = Integer.parseInt(rateSettings.getProperty("dollcraft2", "0"));
			인형합성확률3단계 = Integer.parseInt(rateSettings.getProperty("dollcraft3", "0"));
			인형합성확률4단계 = Integer.parseInt(rateSettings.getProperty("dollcraft4", "0"));
			인형합성확률5단계 = Integer.parseInt(rateSettings.getProperty("dollcraft5", "0"));
			
			인형합성비용1단계 = Integer.parseInt(rateSettings.getProperty("dollcraftAdena1", "0"));
			인형합성비용2단계 = Integer.parseInt(rateSettings.getProperty("dollcraftAdena2", "0"));
			인형합성비용3단계 = Integer.parseInt(rateSettings.getProperty("dollcraftAdena3", "0"));
			인형합성비용4단계 = Integer.parseInt(rateSettings.getProperty("dollcraftAdena4", "0"));
			인형합성비용5단계 = Integer.parseInt(rateSettings.getProperty("dollcraftAdena5", "0"));
			
			드슬변신랭킹 = Integer.parseInt(rateSettings.getProperty("dragonslayerpolyRanking", "0"));
			지배순반제작확률 = Integer.parseInt(rateSettings.getProperty("craftteleportRing", "15"));
			용갑 = Integer.parseInt(rateSettings.getProperty("DragonArmor", "1"));
			용갑인챈 = (Integer[]) MJArrangeParser
					.parsing(rateSettings.getProperty("DragonArmorRate", "30,20,15,13,10,8,6,4,2,1,1,1,1,1,1"), ",",
							MJArrangeParseeFactory.createIntArrange())
					.result();
			
			발라가호확률 = Integer.parseInt(rateSettings.getProperty("valablessRate", "1"));
			린드가호확률 = Integer.parseInt(rateSettings.getProperty("lindblessRate", "1"));
			파푸가호확률 = Integer.parseInt(rateSettings.getProperty("papublessRate", "1"));
			발라이펙트대미지1 = Double.parseDouble(rateSettings.getProperty("valaEffectDamage1", "1.0"));
			발라이펙트대미지2 = Double.parseDouble(rateSettings.getProperty("valaEffectDamage2", "1.0"));
			발라이펙트대미지3 = Double.parseDouble(rateSettings.getProperty("valaEffectDamage3", "1.0"));
			발라이펙트대미지4 = Double.parseDouble(rateSettings.getProperty("valaEffectDamage4", "1.0"));
			린드이펙트대미지1 = Double.parseDouble(rateSettings.getProperty("lindEffectDamage1", "1.0"));
			린드이펙트대미지2 = Double.parseDouble(rateSettings.getProperty("lindEffectDamage2", "1.0"));
			린드이펙트대미지3 = Double.parseDouble(rateSettings.getProperty("lindEffectDamage3", "1.0"));
			린드이펙트대미지4 = Double.parseDouble(rateSettings.getProperty("lindEffectDamage4", "1.0"));
			파푸이펙트대미지1 = Double.parseDouble(rateSettings.getProperty("papuEffectDamage1", "1.0"));
			파푸이펙트대미지2 = Double.parseDouble(rateSettings.getProperty("papuEffectDamage2", "1.0"));
			파푸이펙트대미지3 = Double.parseDouble(rateSettings.getProperty("papuEffectDamage3", "1.0"));
			파푸이펙트대미지4 = Double.parseDouble(rateSettings.getProperty("papuEffectDamage4", "1.0"));
			
			빨갱이회복량 = Integer.parseInt(rateSettings.getProperty("RedHpUpRate", "1"));
			주홍이회복량 = Integer.parseInt(rateSettings.getProperty("OrangeHpUpRate", "1"));
			맑갱이회복량 = Integer.parseInt(rateSettings.getProperty("WriteHpUpRate", "1"));
			
			무기안전0일때1업 = Integer.parseInt(rateSettings.getProperty("WeaponSafe0up1", "1"));
			무기안전0일때2업 = Integer.parseInt(rateSettings.getProperty("WeaponSafe0up2", "1"));
			무기안전0일때3업 = Integer.parseInt(rateSettings.getProperty("WeaponSafe0up3", "1"));
			무기안전6일때1업 = Integer.parseInt(rateSettings.getProperty("WeaponSafe6up1", "1"));
			무기안전6일때2업 = Integer.parseInt(rateSettings.getProperty("WeaponSafe6up2", "1"));
			무기안전6일때3업 = Integer.parseInt(rateSettings.getProperty("WeaponSafe6up3", "1"));
			
			방어구안전0일때1업 = Integer.parseInt(rateSettings.getProperty("ArmorSafe0up1", "1"));
			방어구안전0일때2업 = Integer.parseInt(rateSettings.getProperty("ArmorSafe0up2", "1"));
			방어구안전0일때3업 = Integer.parseInt(rateSettings.getProperty("ArmorSafe0up3", "1"));
			방어구안전4일때1업 = Integer.parseInt(rateSettings.getProperty("ArmorSafe4up1", "1"));
			방어구안전4일때2업 = Integer.parseInt(rateSettings.getProperty("ArmorSafe4up1", "1"));
			방어구안전4일때3업 = Integer.parseInt(rateSettings.getProperty("ArmorSafe4up1", "1"));
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + RATES_CONFIG_FILE + " File.");
		}

		/** hunt.properties **/
		try {
			Properties huntSettings = new Properties();
			FileReader is = new FileReader(new File(HUNT_CONFIG_FILE));
			huntSettings.load(is);
			is.close();
			기감_OPEN_TIME = huntSettings.getProperty("GiranJailOpenTime", "0200#0600#1000#1400#1800#2000");
			본던_OPEN_TIME = huntSettings.getProperty("GludioDungeonOpenTime", "0200#0600#1000#1400#1800#2000");
			용던_OPEN_TIME = huntSettings.getProperty("DragonDungeonOpenTime", "1100#1700#2200");
			잊혀진섬_OPEN_TIME = huntSettings.getProperty("ForgottenIsleOpenTime", "1000#1400");
			오만의탑정상_OPEN_TIME = huntSettings.getProperty("OmanTopFloorOpenTime", "1000#1400");
			테베라스_OPEN_TIME = huntSettings.getProperty("TebeOpenTime", "1000#1400");
			상아탑8층_OPEN_TIME = huntSettings.getProperty("IvoryTowerOpenTime", "1000#1400");
			던전초기화시간 = huntSettings.getProperty("DundeonInitTime", "1000");
			기감렙제 = Integer.parseInt(huntSettings.getProperty("GiranLevel", "0"));
			테베렙제 = Integer.parseInt(huntSettings.getProperty("TebeLevel", "0"));
			배틀존작동유무 = Boolean.parseBoolean(huntSettings.getProperty("BattleZone", "true"));
			배틀존아이템 = huntSettings.getProperty("BattleItem", "");
			배틀존아이템갯수 = huntSettings.getProperty("BattleCount", "");
			배틀존_OPEN_TIME = huntSettings.getProperty("BattleOpenTime", "1100#1700#2200");
			배틀존입장레벨 = Integer.parseInt(huntSettings.getProperty("BattleLevel", "55"));
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + HUNT_CONFIG_FILE + " File.");
		}
		
		// altsettings.properties
		try {
			Properties altSettings = new Properties();
			InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
			altSettings.load(is);
			is.close();

			GLOBAL_CHAT_LEVEL = Short.parseShort(altSettings.getProperty("GlobalChatLevel", "30"));

			WHISPER_CHAT_LEVEL = Short.parseShort(altSettings.getProperty("WhisperChatLevel", "7"));

			AUTO_LOOT = Byte.parseByte(altSettings.getProperty("AutoLoot", "2"));

			LOOTING_RANGE = Integer.parseInt(altSettings.getProperty("LootingRange", "3"));

			ALT_NONPVP = Boolean.parseBoolean(altSettings.getProperty("NonPvP", "true"));

			ALT_ATKMSG = Boolean.parseBoolean(altSettings.getProperty("AttackMessageOn", "true"));

			CHANGE_TITLE_BY_ONESELF = Boolean.parseBoolean(altSettings.getProperty("ChangeTitleByOneself", "false"));

			MAX_CLAN_MEMBER = Integer.parseInt(altSettings.getProperty("MaxClanMember", "0"));

			CLAN_ALLIANCE = Boolean.parseBoolean(altSettings.getProperty("ClanAlliance", "true"));

			MAX_PT = Integer.parseInt(altSettings.getProperty("MaxPT", "8"));

			MAX_CHAT_PT = Integer.parseInt(altSettings.getProperty("MaxChatPT", "8"));

			SIM_WAR_PENALTY = Boolean.parseBoolean(altSettings.getProperty("SimWarPenalty", "true"));

			GET_BACK = Boolean.parseBoolean(altSettings.getProperty("GetBack", "false"));

			ALT_ITEM_DELETION_TYPE = altSettings.getProperty("ItemDeletionType", "auto");

			ALT_ITEM_DELETION_TIME = Integer.parseInt(altSettings.getProperty("ItemDeletionTime", "10"));

			ALT_ITEM_DELETION_RANGE = Integer.parseInt(altSettings.getProperty("ItemDeletionRange", "5"));

			경험치지급단 = Integer.parseInt(altSettings.getProperty("Expreturn", "75"));
			ALT_HALLOWEENEVENT = Boolean.parseBoolean(altSettings.getProperty("HalloweenEvent", "true"));
			TRIPLE_DMG = Double.parseDouble(altSettings.getProperty("tripledmg", "0.8"));
			ALT_HALLOWEENEVENT2009 = Boolean.parseBoolean(altSettings.getProperty("HalloweenEvent2009", "true"));

			ALT_FANTASYEVENT = Boolean.parseBoolean(altSettings.getProperty("FantasyEvent", "true"));

			ALT_CHUSEOKEVENT = Boolean.parseBoolean(altSettings.getProperty("ChuSeokEvent", "true"));

			ALT_FEATURE = Boolean.parseBoolean(altSettings.getProperty("FeatureEvent", "true"));

			ALT_WHO_COMMAND = Boolean.parseBoolean(altSettings.getProperty("WhoCommand", "false"));
			무기5 = Integer.parseInt(altSettings.getProperty("LimWeapon5", "30"));
			무기6 = Integer.parseInt(altSettings.getProperty("LimWeapon6", "1"));
			무기7 = Integer.parseInt(altSettings.getProperty("LimWeapon7", "1"));
			무기8 = Integer.parseInt(altSettings.getProperty("LimWeapon8", "7"));
			무기9 = Integer.parseInt(altSettings.getProperty("LimWeapon9", "7"));
			무기10 = Integer.parseInt(altSettings.getProperty("LimWeapon10", "7"));
			무기11 = Integer.parseInt(altSettings.getProperty("LimWeapon11", "7"));
			무기12 = Integer.parseInt(altSettings.getProperty("LimWeapon12", "7"));
			무기13 = Integer.parseInt(altSettings.getProperty("LimWeapon13", "7"));
			무기14 = Integer.parseInt(altSettings.getProperty("LimWeapon14", "7"));
			무기안전0 = Integer.parseInt(altSettings.getProperty("Lim0Weapon0", "7"));
			무기안전1 = Integer.parseInt(altSettings.getProperty("Lim1Weapon1", "7"));
			무기안전2 = Integer.parseInt(altSettings.getProperty("Lim2Weapon2", "7"));
			무기안전3 = Integer.parseInt(altSettings.getProperty("Lim3Weapon3", "7"));
			무기안전4 = Integer.parseInt(altSettings.getProperty("Lim4Weapon4", "7"));
			무기안전5 = Integer.parseInt(altSettings.getProperty("Lim5Weapon5", "7"));
			무기안전6 = Integer.parseInt(altSettings.getProperty("Lim6Weapon6", "7"));
			무기안전7 = Integer.parseInt(altSettings.getProperty("Lim7Weapon7", "7"));
			무기안전8 = Integer.parseInt(altSettings.getProperty("Lim8Weapon8", "7"));

			무기안전9 = Integer.parseInt(altSettings.getProperty("Lim9Weapon9", "7"));
			무기안전10 = Integer.parseInt(altSettings.getProperty("Lim10Weapon10", "7"));
			무기안전11 = Integer.parseInt(altSettings.getProperty("Lim11Weapon11", "7"));
			무기안전12 = Integer.parseInt(altSettings.getProperty("Lim12Weapon12", "7"));
			무기안전13 = Integer.parseInt(altSettings.getProperty("Lim13Weapon13", "7"));
			무기안전14 = Integer.parseInt(altSettings.getProperty("Lim14Weapon14", "7"));
			무기안전15 = Integer.parseInt(altSettings.getProperty("Lim15Weapon15", "7"));

			방어구4 = Integer.parseInt(altSettings.getProperty("LimArmor4", "7"));
			방어구5 = Integer.parseInt(altSettings.getProperty("LimArmor5", "7"));
			방어구6 = Integer.parseInt(altSettings.getProperty("LimArmor6", "7"));

			방어구7 = Integer.parseInt(altSettings.getProperty("LimArmor7", "7"));
			방어구8 = Integer.parseInt(altSettings.getProperty("LimArmor8", "7"));
			방어구9 = Integer.parseInt(altSettings.getProperty("LimArmor9", "7"));
			방어구10 = Integer.parseInt(altSettings.getProperty("LimArmor10", "7"));
			방어구11 = Integer.parseInt(altSettings.getProperty("LimArmor11", "7"));
			방어구12 = Integer.parseInt(altSettings.getProperty("LimArmor12", "7"));
			방어구13 = Integer.parseInt(altSettings.getProperty("LimArmor13", "7"));
			방어구14 = Integer.parseInt(altSettings.getProperty("LimArmor14", "7"));

			방어구안전0 = Integer.parseInt(altSettings.getProperty("Lim0Armor0", "10"));
			방어구안전1 = Integer.parseInt(altSettings.getProperty("Lim1Armor1", "11"));
			방어구안전2 = Integer.parseInt(altSettings.getProperty("Lim2Armor2", "12"));
			방어구안전3 = Integer.parseInt(altSettings.getProperty("Lim3Armor3", "13"));
			방어구안전4 = Integer.parseInt(altSettings.getProperty("Lim4Armor4", "14"));
			방어구안전5 = Integer.parseInt(altSettings.getProperty("Lim5Armor5", "15"));
			방어구안전6 = Integer.parseInt(altSettings.getProperty("Lim6Armor6", "16"));
			방어구안전7 = Integer.parseInt(altSettings.getProperty("Lim7Armor7", "17"));
			방어구안전8 = Integer.parseInt(altSettings.getProperty("Lim8Armor8", "18"));

			방어구안전사0 = Integer.parseInt(altSettings.getProperty("Li1Armor", "20"));
			방어구안전사1 = Integer.parseInt(altSettings.getProperty("Li2Armor", "20"));
			방어구안전사2 = Integer.parseInt(altSettings.getProperty("Li3Armor", "20"));
			방어구안전사3 = Integer.parseInt(altSettings.getProperty("Li4Armor", "20"));
			방어구안전사4 = Integer.parseInt(altSettings.getProperty("Li5Armor", "20"));
			방어구안전사5 = Integer.parseInt(altSettings.getProperty("Li6Armor", "20"));
			방어구안전사6 = Integer.parseInt(altSettings.getProperty("Li7Armor", "20"));
			방어구안전사7 = Integer.parseInt(altSettings.getProperty("Li8Armor", "20"));
			방어구안전사8 = Integer.parseInt(altSettings.getProperty("Li9Armor", "20"));
			방어구안전사9 = Integer.parseInt(altSettings.getProperty("Li10Armor", "20"));
			방어구안전사10 = Integer.parseInt(altSettings.getProperty("Li11Armor", "20"));

			악세인첸1 = Integer.parseInt(altSettings.getProperty("Acc1", "1"));
			악세인첸2 = Integer.parseInt(altSettings.getProperty("Acc2", "2"));
			악세인첸3 = Integer.parseInt(altSettings.getProperty("Acc3", "3"));
			악세인첸4 = Integer.parseInt(altSettings.getProperty("Acc4", "4"));
			악세인첸5 = Integer.parseInt(altSettings.getProperty("Acc5", "5"));
			악세인첸6 = Integer.parseInt(altSettings.getProperty("Acc6", "6"));
			악세인첸7 = Integer.parseInt(altSettings.getProperty("Acc7", "7"));
			악세인첸8 = Integer.parseInt(altSettings.getProperty("Acc8", "8"));
			악세인첸9 = Integer.parseInt(altSettings.getProperty("Acc9", "9"));
			용던본던시간 = Integer.parseInt(altSettings.getProperty("DragonGludioTime", "2"));
			기감시간 = Integer.parseInt(altSettings.getProperty("GiranPrisonTime", "2"));
			몽섬시간 = Integer.parseInt(altSettings.getProperty("DreamIslandTime", "2"));
			라던시간 = Integer.parseInt(altSettings.getProperty("LastabardTime", "2"));
			개미던전시간 = Integer.parseInt(altSettings.getProperty("AntDundeonTime", "2"));
			그림자신전시간 = Integer.parseInt(altSettings.getProperty("ShadowTempleTime", "2"));
			기본엠흡 = Integer.parseInt(altSettings.getProperty("DefaultMpSteal", "9"));
			바포엠흡 = Integer.parseInt(altSettings.getProperty("bapoMpSteal", "9"));
			ALT_REVIVAL_POTION = Boolean.parseBoolean(altSettings.getProperty("RevivalPotion", "false"));
			
			Master_Enchant = Integer.parseInt(altSettings.getProperty("MasterEnchant", "5"));
			Master_ArmorEnchant = Integer.parseInt(altSettings.getProperty("MasterArmorEnchant", "5"));
			
			근거리pc메테오대미지 = Integer.parseInt(altSettings.getProperty("ShortPcMeteodmg", "2"));
			원거리pc메테오대미지 = Integer.parseInt(altSettings.getProperty("LongPcMeteodmg", "2"));
			근거리npc메테오대미지 = Integer.parseInt(altSettings.getProperty("ShortNpcMeteodmg", "2"));
			원거리npc메테오대미지 = Integer.parseInt(altSettings.getProperty("LongNpcMeteodmg", "2"));
			근거리헬파이어대미지 = Integer.parseInt(altSettings.getProperty("ShortHellfiredmg", "2"));
			근거리선버스트대미지 = Integer.parseInt(altSettings.getProperty("Shortsunburstdmg", "2"));
			원거리헬파이어대미지 = Integer.parseInt(altSettings.getProperty("LongHellfiredmg", "2"));
			원거리선버스트대미지 = Integer.parseInt(altSettings.getProperty("Longsunburstdmg", "2"));
			랭커변신가능순위 = Integer.parseInt(altSettings.getProperty("RankerRank", "2"));
			전투메시지딜레이 = Integer.parseInt(altSettings.getProperty("FightMessageDelay", "10"));
			String strWar;
			strWar = altSettings.getProperty("WarTime", "1h");
			if (strWar.indexOf("d") >= 0) {
				ALT_WAR_TIME_UNIT = Calendar.DATE;
				strWar = strWar.replace("d", "");
			} else if (strWar.indexOf("h") >= 0) {
				ALT_WAR_TIME_UNIT = Calendar.HOUR_OF_DAY;
				strWar = strWar.replace("h", "");
			} else if (strWar.indexOf("m") >= 0) {
				ALT_WAR_TIME_UNIT = Calendar.MINUTE;
				strWar = strWar.replace("m", "");
			}
			ALT_WAR_TIME = Integer.parseInt(strWar);
			strWar = altSettings.getProperty("WarInterval", "2d");
			if (strWar.indexOf("d") >= 0) {
				ALT_WAR_INTERVAL_UNIT = Calendar.DATE;
				strWar = strWar.replace("d", "");
			} else if (strWar.indexOf("h") >= 0) {
				ALT_WAR_INTERVAL_UNIT = Calendar.HOUR_OF_DAY;
				strWar = strWar.replace("h", "");
			} else if (strWar.indexOf("m") >= 0) {
				ALT_WAR_INTERVAL_UNIT = Calendar.MINUTE;
				strWar = strWar.replace("m", "");
			}
			ALT_WAR_INTERVAL = Integer.parseInt(strWar);

			SPAWN_HOME_POINT = Boolean.parseBoolean(altSettings.getProperty("SpawnHomePoint", "true"));
			자동사냥 = Boolean.parseBoolean(altSettings.getProperty("isAutoHunt", "true"));
			SPAWN_HOME_POINT_COUNT = Integer.parseInt(altSettings.getProperty("SpawnHomePointCount", "2"));

			SPAWN_HOME_POINT_DELAY = Integer.parseInt(altSettings.getProperty("SpawnHomePointDelay", "100"));

			SPAWN_HOME_POINT_RANGE = Integer.parseInt(altSettings.getProperty("SpawnHomePointRange", "8"));

			INIT_BOSS_SPAWN = Boolean.parseBoolean(altSettings.getProperty("InitBossSpawn", "true"));

			ELEMENTAL_STONE_AMOUNT = Integer.parseInt(altSettings.getProperty("ElementalStoneAmount", "300"));

			HOUSE_TAX_INTERVAL = Integer.parseInt(altSettings.getProperty("HouseTaxInterval", "10"));

			MAX_DOLL_COUNT = Integer.parseInt(altSettings.getProperty("MaxDollCount", "1"));

			RETURN_TO_NATURE = Boolean.parseBoolean(altSettings.getProperty("ReturnToNature", "false"));

			MAX_NPC_ITEM = Integer.parseInt(altSettings.getProperty("MaxNpcItem", "8"));

			MAX_PERSONAL_WAREHOUSE_ITEM = Integer.parseInt(altSettings.getProperty("MaxPersonalWarehouseItem", "100"));

			MAX_CLAN_WAREHOUSE_ITEM = Integer.parseInt(altSettings.getProperty("MaxClanWarehouseItem", "200"));

			DELETE_CHARACTER_AFTER_7DAYS = Boolean.parseBoolean(altSettings.getProperty("DeleteCharacterAfter7Days", "True"));

			GMCODE = Integer.parseInt(altSettings.getProperty("GMCODE", "9999"));

			DELETE_DB_DAYS = Integer.parseInt(altSettings.getProperty("DeleteDBDAY", "14"));

			PickUpItem = Boolean.parseBoolean(altSettings.getProperty("PickUpItem", "True"));
			PickUpItem_Id = altSettings.getProperty("PickUpItem_Id", "*");
			PickUpItem_UserName = Boolean.parseBoolean(altSettings.getProperty("PickUpItem_UserName", "True"));

			HunterEvent_Doll = Boolean.parseBoolean(altSettings.getProperty("HunterEvent_Doll", "True"));
			HunterEvent_Doll_Id = altSettings.getProperty("HunterEvent_Doll_Id", "4370602,4370603,4370605,4370606");
			HunterEvent_Doll_MapId = altSettings.getProperty("HunterEvent_Doll_MapId", "54,110,12861");
			HunterEvent_Doll_MapId1 = altSettings.getProperty("HunterEvent_Doll_MapId1", "54,110,12861");
			HunterEvent_Doll_MapId2 = altSettings.getProperty("HunterEvent_Doll_MapId2", "54,110,12861");
			HunterEvent_Doll_MapId3 = altSettings.getProperty("HunterEvent_Doll_MapId3", "54,110,12861");
			HunterEvent_Doll_MapId4 = altSettings.getProperty("HunterEvent_Doll_MapId4", "54,110,12861");
			HunterEvent_Doll_MapId5 = altSettings.getProperty("HunterEvent_Doll_MapId5", "54,110,12861");
			HunterEvent_Doll_MapId6 = altSettings.getProperty("HunterEvent_Doll_MapId6", "54,110,12861");
			HunterEvent_Doll_MapId7 = altSettings.getProperty("HunterEvent_Doll_MapId7", "54,110,12861");
			HunterEvent_Doll_MapId8 = altSettings.getProperty("HunterEvent_Doll_MapId8", "54,110,12861");
			HunterEvent_Doll_MapId9 = altSettings.getProperty("HunterEvent_Doll_MapId9", "54,110,12861");
			HunterEvent_Doll_MapId10 = altSettings.getProperty("HunterEvent_Doll_MapId10", "54,110,12861");

			HunterEvent_Doll_MonsterId = Integer.parseInt(altSettings.getProperty("HunterEvent_Doll_MonsterId", "5660"));
			HunterEvent_Doll_1 = Integer.parseInt(altSettings.getProperty("HunterEvent_Doll_1", "1000"));

			AC_HIT_PCPC = Double.parseDouble(altSettings.getProperty("AcHitPCPC", "1.5"));
			AC_HIT_NPCPC = Double.parseDouble(altSettings.getProperty("AcHitNPCPC", "1"));
			MR_MAGIC_DMG = Double.parseDouble(altSettings.getProperty("MrMagicDMG", "1"));
			
			펫추가대미지 = Double.parseDouble(altSettings.getProperty("PetAddedDmg", "1.5"));
			서먼추가대미지 = Double.parseDouble(altSettings.getProperty("SummonAddedDmg", "1.5"));
			
			ALLBUFF_POLYID = Integer.parseInt(altSettings.getProperty("Allbuff_PolyId", "5641"));
			ASBUGCHECK_ALLBUF = Boolean.parseBoolean(altSettings.getProperty("ASBugCheck_Allbuf", "True"));
			빨갱이 = Integer.parseInt(altSettings.getProperty("RedPotion", "15"));
			주홍이 = Integer.parseInt(altSettings.getProperty("OrangePotion", "45"));
			맑갱이 = Integer.parseInt(altSettings.getProperty("WhitePotion", "75"));
			아데나시세비율 = Integer.parseInt(altSettings.getProperty("adenaRate", "5"));
			낚시장소 = Boolean.parseBoolean(altSettings.getProperty("fishInPlace", "True"));
			중립혈엠블럼 = Integer.parseInt(altSettings.getProperty("NatualEmblemId", "5"));
			중립혈아이디 = Integer.parseInt(altSettings.getProperty("NatualClanId", "5"));
			중립혈레벨제한 = Integer.parseInt(altSettings.getProperty("NatualClanLevel", "5"));
			CHECK_AUTO = Boolean.parseBoolean(altSettings.getProperty("checkauto", "true"));
			자동인증시간 = Integer.parseInt(altSettings.getProperty("AutoCertTime", "5"));
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + ALT_SETTINGS_FILE + " File.");
		}

		try {
			Properties botSettings = new Properties();
			InputStream is = new FileInputStream(new File(BOT_SETTINGS_FILE));
			botSettings.load(is);
			is.close();
			봇시작레벨 = Integer.parseInt(botSettings.getProperty("botstartLev", "56"));
			if(봇시작레벨 <= 0){
				봇시작레벨 = 1;
			}
			else if(봇시작레벨 >= 100){
				봇시작레벨 = 99;
			}
			봇물약기본회복량 = Integer.parseInt(botSettings.getProperty("botposionbase", "45"));
			봇물약랜덤회복량 = Integer.parseInt(botSettings.getProperty("botposionrnd", "65"));
			ROBOT_LEVEL_RANGE = Integer.parseInt(botSettings.getProperty("botLevelRange", "5"));
			ROBOT_DIE_MESSAGE = Boolean.parseBoolean(botSettings.getProperty("bootDieMessage", "false"));
			START_AUTO_ROBOT = Boolean.parseBoolean(botSettings.getProperty("startAutoBoot", "false"));

		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + BOT_SETTINGS_FILE + " File.");
		}
		
		// charsettings.properties
		try {
			Properties charSettings = new Properties();
			InputStream is = new FileInputStream(new File(CHAR_SETTINGS_CONFIG_FILE));
			charSettings.load(is);
			is.close();
			IMMUNE_TO_HARM_PC = Double.parseDouble(charSettings.getProperty("Immune_ToHarm_Pc", "1.2"));
			IMMUNE_TO_HARM_NPC = Double.parseDouble(charSettings.getProperty("Immune_ToHarm_Npc", "1.2"));
			IMMUNE_TO_HARM_MAGIC = Double.parseDouble(charSettings.getProperty("Immune_ToHarm_Magic", "1.2"));
			PRINCE_MAX_HP = Integer.parseInt(charSettings.getProperty("PrinceMaxHP", "1000"));

			PRINCE_MAX_MP = Integer.parseInt(charSettings.getProperty("PrinceMaxMP", "800"));

			KNIGHT_MAX_HP = Integer.parseInt(charSettings.getProperty("KnightMaxHP", "1400"));

			KNIGHT_MAX_MP = Integer.parseInt(charSettings.getProperty("KnightMaxMP", "600"));

			ELF_MAX_HP = Integer.parseInt(charSettings.getProperty("ElfMaxHP", "1000"));

			ELF_MAX_MP = Integer.parseInt(charSettings.getProperty("ElfMaxMP", "900"));

			WIZARD_MAX_HP = Integer.parseInt(charSettings.getProperty("WizardMaxHP", "800"));

			WIZARD_MAX_MP = Integer.parseInt(charSettings.getProperty("WizardMaxMP", "1200"));

			DARKELF_MAX_HP = Integer.parseInt(charSettings.getProperty("DarkelfMaxHP", "1000"));

			DARKELF_MAX_MP = Integer.parseInt(charSettings.getProperty("DarkelfMaxMP", "900"));

			DRAGONKNIGHT_MAX_HP = Integer.parseInt(charSettings.getProperty("DragonknightMaxHP", "1000"));

			DRAGONKNIGHT_MAX_MP = Integer.parseInt(charSettings.getProperty("DragonknightMaxMP", "900"));

			BLACKWIZARD_MAX_HP = Integer.parseInt(charSettings.getProperty("BlackwizardMaxHP", "900"));

			BLACKWIZARD_MAX_MP = Integer.parseInt(charSettings.getProperty("BlackwizardMaxMP", "1100"));

			LIMITLEVEL = Integer.parseInt(charSettings.getProperty("LimitLevel", "99"));

			PRINCE_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("PrinceAddDamagePc", "0"));
			KNIGHT_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("KnightAddDamagePc", "0"));
			ELF_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("ElfAddDamagePc", "0"));
			WIZARD_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("WizardAddDamagePc", "0"));
			DARKELF_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("DarkelfAddDamagePc", "0"));
			DRAGONKNIGHT_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("DragonknightAddDamagePc", "0"));
			BLACKWIZARD_ADD_DAMAGEPC = Integer.parseInt(charSettings.getProperty("BlackwizardAddDamagePc", "0"));

			UNCANNY_DODGE_DECREASE_RATE_BY_NPC = Double.parseDouble(charSettings.getProperty("UNCANNY_DODGE_DECREASE_RATE_BY_NPC", "0.25"));
			UNCANNY_DODGE_DECREASE_RATE_BY_PC = Double.parseDouble(charSettings.getProperty("UNCANNY_DODGE_DECREASE_RATE_BY_PC", "0.15"));
			
			DOUBLE_BRAKE_EXERCISE_PROB = Integer.parseInt(charSettings.getProperty("DOUBLE_BRAKE_EXERCISE_PROB", "330000"));
			DOUBLE_BRAKE_WEAPON_INCREASE_DMG_RATE = Double.parseDouble(charSettings.getProperty("DOUBLE_BRAKE_WEAPON_INCREASE_DMG_RATE", "1.5"));
			
			PER_BURNING_SPIRIT_DMG_RATE = Double.parseDouble(charSettings.getProperty("PER_BURNING_SPIRIT_DMG_RATE", "1.5"));
			
			BURNING_SPIRIT_EXERCISE_PROB = Integer.parseInt(charSettings.getProperty("BURNING_SPIRIT_EXERCISE_PROB", "330000"));
			ELEMENTAL_FIRE_EXERCISE_PROB = Integer.parseInt(charSettings.getProperty("ELEMENTAL_FIRE_EXERCISE_PROB", "330000"));
			
			PER_ELEMENTAL_FIRE_DMG_RATE = Double.parseDouble(charSettings.getProperty("PER_ELEMENTAL_FIRE_DMG_RATE", "1.5"));
			
			LV50_EXP = Integer.parseInt(charSettings.getProperty("Lv50Exp", "1"));
			LV51_EXP = Integer.parseInt(charSettings.getProperty("Lv51Exp", "1"));
			LV52_EXP = Integer.parseInt(charSettings.getProperty("Lv52Exp", "1"));
			LV53_EXP = Integer.parseInt(charSettings.getProperty("Lv53Exp", "1"));
			LV54_EXP = Integer.parseInt(charSettings.getProperty("Lv54Exp", "1"));
			LV55_EXP = Integer.parseInt(charSettings.getProperty("Lv55Exp", "1"));
			LV56_EXP = Integer.parseInt(charSettings.getProperty("Lv56Exp", "1"));
			LV57_EXP = Integer.parseInt(charSettings.getProperty("Lv57Exp", "1"));
			LV58_EXP = Integer.parseInt(charSettings.getProperty("Lv58Exp", "1"));
			LV59_EXP = Integer.parseInt(charSettings.getProperty("Lv59Exp", "1"));
			LV60_EXP = Integer.parseInt(charSettings.getProperty("Lv60Exp", "1"));
			LV61_EXP = Integer.parseInt(charSettings.getProperty("Lv61Exp", "1"));
			LV62_EXP = Integer.parseInt(charSettings.getProperty("Lv62Exp", "1"));
			LV63_EXP = Integer.parseInt(charSettings.getProperty("Lv63Exp", "1"));
			LV64_EXP = Integer.parseInt(charSettings.getProperty("Lv64Exp", "1"));
			LV65_EXP = Integer.parseInt(charSettings.getProperty("Lv65Exp", "2"));
			LV66_EXP = Integer.parseInt(charSettings.getProperty("Lv66Exp", "2"));
			LV67_EXP = Integer.parseInt(charSettings.getProperty("Lv67Exp", "2"));
			LV68_EXP = Integer.parseInt(charSettings.getProperty("Lv68Exp", "2"));
			LV69_EXP = Integer.parseInt(charSettings.getProperty("Lv69Exp", "2"));
			LV70_EXP = Integer.parseInt(charSettings.getProperty("Lv70Exp", "4"));
			LV71_EXP = Integer.parseInt(charSettings.getProperty("Lv71Exp", "4"));
			LV72_EXP = Integer.parseInt(charSettings.getProperty("Lv72Exp", "4"));
			LV73_EXP = Integer.parseInt(charSettings.getProperty("Lv73Exp", "4"));
			LV74_EXP = Integer.parseInt(charSettings.getProperty("Lv74Exp", "4"));
			LV75_EXP = Integer.parseInt(charSettings.getProperty("Lv75Exp", "8"));
			LV76_EXP = Integer.parseInt(charSettings.getProperty("Lv76Exp", "8"));
			LV77_EXP = Integer.parseInt(charSettings.getProperty("Lv77Exp", "8"));
			LV78_EXP = Integer.parseInt(charSettings.getProperty("Lv78Exp", "8"));
			LV79_EXP = Integer.parseInt(charSettings.getProperty("Lv79Exp", "16"));
			LV80_EXP = Integer.parseInt(charSettings.getProperty("Lv80Exp", "32"));
			LV81_EXP = Integer.parseInt(charSettings.getProperty("Lv81Exp", "64"));
			LV82_EXP = Integer.parseInt(charSettings.getProperty("Lv82Exp", "128"));
			LV83_EXP = Integer.parseInt(charSettings.getProperty("Lv83Exp", "256"));
			LV84_EXP = Integer.parseInt(charSettings.getProperty("Lv84Exp", "512"));
			LV85_EXP = Integer.parseInt(charSettings.getProperty("Lv85Exp", "1024"));
			LV86_EXP = Integer.parseInt(charSettings.getProperty("Lv86Exp", "2048"));
			LV87_EXP = Integer.parseInt(charSettings.getProperty("Lv87Exp", "4096"));
			LV88_EXP = Integer.parseInt(charSettings.getProperty("Lv88Exp", "8192"));
			LV89_EXP = Integer.parseInt(charSettings.getProperty("Lv89Exp", "16384"));
			LV90_EXP = Integer.parseInt(charSettings.getProperty("Lv90Exp", "32768"));
			LV91_EXP = Integer.parseInt(charSettings.getProperty("Lv91Exp", "65536"));
			LV92_EXP = Integer.parseInt(charSettings.getProperty("Lv92Exp", "131072"));
			LV93_EXP = Integer.parseInt(charSettings.getProperty("Lv93Exp", "262144"));
			LV94_EXP = Integer.parseInt(charSettings.getProperty("Lv94Exp", "524288"));
			LV95_EXP = Integer.parseInt(charSettings.getProperty("Lv95Exp", "1048576"));
			LV96_EXP = Integer.parseInt(charSettings.getProperty("Lv96Exp", "2097152"));
			LV97_EXP = Integer.parseInt(charSettings.getProperty("Lv97Exp", "4194304"));
			LV98_EXP = Integer.parseInt(charSettings.getProperty("Lv98Exp", "8388608"));
			LV99_EXP = Integer.parseInt(charSettings.getProperty("Lv99Exp", "16777216"));
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + CHAR_SETTINGS_CONFIG_FILE + " File.");
		}
		// skillpro.properties
		try {
			Properties skillpro = new Properties();
			InputStream is = new FileInputStream(new File(SKILLPRO));
			skillpro.load(is);
			new ConfigLoader().load(Config.class);
			is.close();

			/** 데이터 베이스 풀 */

			SHOCK_STUN = Integer.parseInt(skillpro.getProperty("ShockStun", "60"));
			스턴렙차 = Integer.parseInt(skillpro.getProperty("STUNLEV", "0"));//220125수정
			아머브레이크 = Integer.parseInt(skillpro.getProperty("ARMOR_BRAKE", "38"));
			카운터배리어 = Integer.parseInt(skillpro.getProperty("COUNTER_BARRIER", "20"));
			TURN_UNDEAD = Double.parseDouble(skillpro.getProperty("Turnundead", "1.9"));
			MR_SKILL_REDUC200 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC200", "0.5"));
			MR_SKILL_REDUC190 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC190", "0.5"));
			MR_SKILL_REDUC180 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC180", "0.5"));
			MR_SKILL_REDUC170 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC170", "0.5"));
			MR_SKILL_REDUC160 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC160", "0.5"));
			MR_SKILL_REDUC150 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC150", "0.5"));
			MR_SKILL_REDUC140 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC140", "0.5"));
			MR_SKILL_REDUC130 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC130", "0.5"));
			MR_SKILL_REDUC120 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC120", "0.5"));
			MR_SKILL_REDUC110 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC110", "0.5"));
			MR_SKILL_REDUC100 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC100", "0.5"));
			MR_SKILL_REDUC90 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC90", "0.5"));
			MR_SKILL_REDUC80 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC80", "0.5"));
			MR_SKILL_REDUC70 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC70", "0.5"));
			MR_SKILL_REDUC60 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC60", "0.5"));
			MR_SKILL_REDUC50 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC50", "0.5"));
			MR_SKILL_REDUC40 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC40", "0.5"));
			MR_SKILL_REDUC30 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC30", "0.5"));
			MR_SKILL_REDUC20 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC20", "0.5"));
			MR_SKILL_REDUC10 = Double.parseDouble(skillpro.getProperty("MR_SKILL_REDUC10", "0.5"));
			바운스어택수치 = Integer.parseInt(skillpro.getProperty("BounceAttack", "1"));
			솔리드캐리지수치 = Integer.parseInt(skillpro.getProperty("SolidCarrage", "1"));
			
			포그면역수치 = Integer.parseInt(skillpro.getProperty("fog", "20"));
			디지즈면역수치 = Integer.parseInt(skillpro.getProperty("disease", "20"));
			커스면역수치 = Integer.parseInt(skillpro.getProperty("curse", "20"));
			웨폰브레이크면역수치 = Integer.parseInt(skillpro.getProperty("weaponbrake", "20"));
			커스블라인드면역수치 = Integer.parseInt(skillpro.getProperty("curseblind", "20"));
			아이스면역수치 = Integer.parseInt(skillpro.getProperty("icelance", "20"));
			게일추가대미지 = Integer.parseInt(skillpro.getProperty("galeAdddamage", "20"));
			낮은3렙캐릭 = Integer.parseInt(skillpro.getProperty("low3lv", "20"));
			낮은2렙캐릭 = Integer.parseInt(skillpro.getProperty("low2lv", "20"));
			낮은1렙캐릭 = Integer.parseInt(skillpro.getProperty("low1lv", "20"));
			동렙캐릭 = Integer.parseInt(skillpro.getProperty("samelv", "20"));
			높은1렙캐릭 = Integer.parseInt(skillpro.getProperty("high1lv", "20"));
			높은2렙캐릭 = Integer.parseInt(skillpro.getProperty("high2lv", "20"));
			높은3렙캐릭 = Integer.parseInt(skillpro.getProperty("high3lv", "20"));
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new Error("Failed to Load " + SKILLPRO + " File.");
		}
		validate();
	}

	private static void validate() {
		if (!IntRange.includes(Config.ALT_ITEM_DELETION_RANGE, 0, 5)) {
			throw new IllegalStateException("ItemDeletionRange의 값이 설정 가능 범위외입니다. ");
		}

		if (!IntRange.includes(Config.ALT_ITEM_DELETION_TIME, 1, 35791)) {
			throw new IllegalStateException("ItemDeletionTime의 값이 설정 가능 범위외입니다. ");
		}
	}

	public static boolean setParameterValue(String pName, String pValue) {
		// server.properties
		if (pName.equalsIgnoreCase("ServerType")) {
			GAME_SERVER_TYPE = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("GameserverHostname")) {
			GAME_SERVER_HOST_NAME = pValue;
		} else if (pName.equalsIgnoreCase("ranktime")) {
			RANKTIME = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("GameserverPort")) {
			GAME_SERVER_PORT = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Driver")) {
			DB_DRIVER = pValue;
		} else if (pName.equalsIgnoreCase("URL")) {
			DB_URL = pValue;
		} else if (pName.equalsIgnoreCase("Login")) {
			DB_LOGIN = pValue;
		} else if (pName.equalsIgnoreCase("Password")) {
			DB_PASSWORD = pValue;
		} else if (pName.equalsIgnoreCase("ClientLanguage")) {
			CLIENT_LANGUAGE = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("TimeZone")) {
			TIME_ZONE = pValue;
		} else if (pName.equalsIgnoreCase("AutomaticKick")) {
			AUTOMATIC_KICK = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("AutoCreateAccounts")) {
			AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(pValue);
		} else if (pName.equalsIgnoreCase("MaximumOnlineUsers")) {
			MAX_ONLINE_USERS = Short.parseShort(pValue);
		} else if (pName.equalsIgnoreCase("LoggingWeaponEnchant")) {
			LOGGING_WEAPON_ENCHANT = Byte.parseByte(pValue);
		} else if (pName.equalsIgnoreCase("LoggingArmorEnchant")) {
			LOGGING_ARMOR_ENCHANT = Byte.parseByte(pValue);
		} else if (pName.equalsIgnoreCase("CharacterConfigInServerSide")) {
			CHARACTER_CONFIG_IN_SERVER_SIDE = Boolean.parseBoolean(pValue);
		} else if (pName.equalsIgnoreCase("Allow2PC")) {
			ALLOW_2PC = Boolean.parseBoolean(pValue);
		} else if (pName.equalsIgnoreCase("LevelDownRange")) {
			LEVEL_DOWN_RANGE = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("SendPacketBeforeTeleport")) {
			SEND_PACKET_BEFORE_TELEPORT = Boolean.parseBoolean(pValue);

		} else if (pName.equalsIgnoreCase("Immune_ToHarm_Pc")) {
			IMMUNE_TO_HARM_PC = Double.parseDouble(pValue);

		} else if (pName.equalsIgnoreCase("Immune_ToHarm_Npc")) {
			IMMUNE_TO_HARM_NPC = Double.parseDouble(pValue);

		} else if (pName.equalsIgnoreCase("Immune_ToHarm_Magic")) {
			IMMUNE_TO_HARM_MAGIC = Double.parseDouble(pValue);

		} else if (pName.equalsIgnoreCase("Turnundead")) {
			TURN_UNDEAD = Integer.parseInt(pValue);

		} else if (pName.equalsIgnoreCase("weaponbless")) {
			weaponbless = Double.parseDouble(pValue);
		}
		// rates.properties
		else if (pName.equalsIgnoreCase("RateXp")) {
			RATE_XP = Double.parseDouble(pValue);
		} else if (pName.equalsIgnoreCase("RateLawful")) {
			RATE_LAWFUL = Double.parseDouble(pValue);
		} else if (pName.equalsIgnoreCase("RateKarma")) {
			RATE_KARMA = Double.parseDouble(pValue);
		} else if (pName.equalsIgnoreCase("RateDropAdena")) {
			RATE_DROP_ADENA = Double.parseDouble(pValue);
		} else if (pName.equalsIgnoreCase("RateDropItems")) {
			RATE_DROP_ITEMS = Double.parseDouble(pValue);
		} else if (pName.equalsIgnoreCase("EnchantChanceWeapon")) {
			ENCHANT_CHANCE_WEAPON = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("EnchantChanceArmor")) {
			ENCHANT_CHANCE_ARMOR = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Weightrate")) {
			RATE_WEIGHT_LIMIT = Byte.parseByte(pValue);
		} else if (pName.equalsIgnoreCase("WaterEnchant")) {
			ENCHANT_CHANCE_WATER = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("EarthEnchant")) {
			ENCHANT_CHANCE_EARTH = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("WindEnchant")) {
			ENCHANT_CHANCE_WIND = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("FireEnchant")) {
			ENCHANT_CHANCE_FIRE = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("SnapperMaxEnchant")) {
			스냅퍼최대인챈 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("RoomteeceMaxEnchant")) {
			룸티스최대인챈 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("acaccessoryMaxEnchant")) {
			장신구최대인챈 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("nomal_orim")) {
			nomal_orim = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Bless_orim")) {
			Bless_orim = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("jibae_1")) {
			일층지배확률 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("jibae_2")) {
			이층지배확률 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("jibae_3")) {
			삼층지배확률 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("jibae_4")) {
			사층지배확률 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("jibae_5")) {
			오층지배확률 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("jibae_6")) {
			육층지배확률 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("jibae_7")) {
			칠층지배확률 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("jibae_8")) {
			팔층지배확률 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("jibae_9")) {
			구층지배확률 = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("jibae_10")) {
			십층지배확률 = Integer.parseInt(pValue);
		}

		// altsettings.properties
		else if (pName.equalsIgnoreCase("GlobalChatLevel")) {
			GLOBAL_CHAT_LEVEL = Short.parseShort(pValue);
		} else if (pName.equalsIgnoreCase("WhisperChatLevel")) {
			WHISPER_CHAT_LEVEL = Short.parseShort(pValue);
		} else if (pName.equalsIgnoreCase("AutoLoot")) {
			AUTO_LOOT = Byte.parseByte(pValue);
		} else if (pName.equalsIgnoreCase("LOOTING_RANGE")) {
			LOOTING_RANGE = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("AltNonPvP")) {
			ALT_NONPVP = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("AttackMessageOn")) {
			ALT_ATKMSG = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("ChangeTitleByOneself")) {
			CHANGE_TITLE_BY_ONESELF = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("MaxClanMember")) {
			MAX_CLAN_MEMBER = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("ClanAlliance")) {
			CLAN_ALLIANCE = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("MaxPT")) {
			MAX_PT = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("MaxChatPT")) {
			MAX_CHAT_PT = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("SimWarPenalty")) {
			SIM_WAR_PENALTY = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("GetBack")) {
			GET_BACK = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("AutomaticItemDeletionTime")) {
			ALT_ITEM_DELETION_TIME = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("AutomaticItemDeletionRange")) {
			ALT_ITEM_DELETION_RANGE = Byte.parseByte(pValue);
		} else if (pName.equalsIgnoreCase("HalloweenEvent")) {
			ALT_HALLOWEENEVENT = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("HalloweenEvent2009")) {
			ALT_HALLOWEENEVENT2009 = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("FantasyEvent")) {
			ALT_FANTASYEVENT = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("ChuSeokEvent")) {
			ALT_CHUSEOKEVENT = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("HouseTaxInterval")) {
			HOUSE_TAX_INTERVAL = Integer.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("MaxDollCount")) {
			MAX_DOLL_COUNT = Integer.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("ReturnToNature")) {
			RETURN_TO_NATURE = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("MaxNpcItem")) {
			MAX_NPC_ITEM = Integer.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("MaxPersonalWarehouseItem")) {
			MAX_PERSONAL_WAREHOUSE_ITEM = Integer.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("MaxClanWarehouseItem")) {
			MAX_CLAN_WAREHOUSE_ITEM = Integer.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("DeleteCharacterAfter7Days")) {
			DELETE_CHARACTER_AFTER_7DAYS = Boolean.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("GMCODE")) {
			GMCODE = Integer.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("DeleteDBDAY")) {
			DELETE_DB_DAYS = Integer.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("Allbuff_PolyId")) {
			ALLBUFF_POLYID = Integer.valueOf(pValue);
		} else if (pName.equalsIgnoreCase("ASBugCheck_Allbuf")) {
			ASBUGCHECK_ALLBUF = Boolean.valueOf(pValue);
		}
		// charsettings.properties
		else if (pName.equalsIgnoreCase("PrinceMaxHP")) {
			PRINCE_MAX_HP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("PrinceMaxMP")) {
			PRINCE_MAX_MP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("KnightMaxHP")) {
			KNIGHT_MAX_HP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("KnightMaxMP")) {
			KNIGHT_MAX_MP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("ElfMaxHP")) {
			ELF_MAX_HP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("ElfMaxMP")) {
			ELF_MAX_MP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("WizardMaxHP")) {
			WIZARD_MAX_HP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("WizardMaxMP")) {
			WIZARD_MAX_MP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("DarkelfMaxHP")) {
			DARKELF_MAX_HP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("DarkelfMaxMP")) {
			DARKELF_MAX_MP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("DragonknightMaxHP")) {
			DRAGONKNIGHT_MAX_HP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("DragonknightMaxMP")) {
			DRAGONKNIGHT_MAX_MP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("BlackwizardMaxHP")) {
			BLACKWIZARD_MAX_HP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("BlackwizardMaxMP")) {
			BLACKWIZARD_MAX_MP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("LimitLevel")) {
			LIMITLEVEL = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("KillDeath")) {
			killdeath = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("PrinceAddDamagePc")) {
			PRINCE_ADD_DAMAGEPC = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("KnightAddDamagePc")) {
			KNIGHT_ADD_DAMAGEPC = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("ElfAddDamagePc")) {
			ELF_ADD_DAMAGEPC = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("WizardAddDamagePc")) {
			WIZARD_ADD_DAMAGEPC = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("DarkelfAddDamagePc")) {
			DARKELF_ADD_DAMAGEPC = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("DragonknightAddDamagePc")) {
			DRAGONKNIGHT_ADD_DAMAGEPC = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("BlackwizardAddDamagePc")) {
			BLACKWIZARD_ADD_DAMAGEPC = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv50Exp")) {
			LV50_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv51Exp")) {
			LV51_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv52Exp")) {
			LV52_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv53Exp")) {
			LV53_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv54Exp")) {
			LV54_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv55Exp")) {
			LV55_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv56Exp")) {
			LV56_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv57Exp")) {
			LV57_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv58Exp")) {
			LV58_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv59Exp")) {
			LV59_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv60Exp")) {
			LV60_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv61Exp")) {
			LV61_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv62Exp")) {
			LV62_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv63Exp")) {
			LV63_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv64Exp")) {
			LV64_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv65Exp")) {
			LV65_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv66Exp")) {
			LV66_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv67Exp")) {
			LV67_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv68Exp")) {
			LV68_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv69Exp")) {
			LV69_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv70Exp")) {
			LV70_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv71Exp")) {
			LV71_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv72Exp")) {
			LV72_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv73Exp")) {
			LV73_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv74Exp")) {
			LV74_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv75Exp")) {
			LV75_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv76Exp")) {
			LV76_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv77Exp")) {
			LV77_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv78Exp")) {
			LV78_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv79Exp")) {
			LV79_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv80Exp")) {
			LV80_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv81Exp")) {
			LV81_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv82Exp")) {
			LV82_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv83Exp")) {
			LV83_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv84Exp")) {
			LV84_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv85Exp")) {
			LV85_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv86Exp")) {
			LV86_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv87Exp")) {
			LV87_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv88Exp")) {
			LV88_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv89Exp")) {
			LV89_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv90Exp")) {
			LV90_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv91Exp")) {
			LV91_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv92Exp")) {
			LV92_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv93Exp")) {
			LV93_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv94Exp")) {
			LV94_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv95Exp")) {
			LV95_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv96Exp")) {
			LV96_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv97Exp")) {
			LV97_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv98Exp")) {
			LV98_EXP = Integer.parseInt(pValue);
		} else if (pName.equalsIgnoreCase("Lv99Exp")) {
			LV99_EXP = Integer.parseInt(pValue);
		} else {
			return false;
		}
		return true;
	}

	private Config() {
	}

	public final static int etc_arrow = 0;
	public final static int etc_wand = 1;
	public final static int etc_light = 2;
	public final static int etc_gem = 3;
	public final static int etc_potion = 6;
	public final static int etc_firecracker = 5;
	public final static int etc_food = 7;
	public final static int etc_scroll = 8;
	public final static int etc_questitem = 9;
	public final static int etc_spellbook = 10;
	public final static int etc_other = 12;
	public final static int etc_material = 13;
	public final static int etc_sting = 15;
	public final static int etc_treasurebox = 16;

	public static enum LOG {
		chat, error, system, badplayer, enchant, inventory, time
	}

	public static synchronized String YearMonthDate2() {
		try {
			int 년 = Year();
			String 년2;
			if (년 < 10) {
				년2 = "0" + 년;
			} else {
				년2 = Integer.toString(년);
			}
			int 월 = Month();
			String 월2 = null;
			if (월 < 10) {
				월2 = "0" + 월;
			} else {
				월2 = Integer.toString(월);
			}
			int 일 = Date();
			String 일2 = null;
			if (일 < 10) {
				일2 = "0" + 일;
			} else {
				일2 = Integer.toString(일);
			}
			return 년2 + 월2 + 일2;
		} catch (Exception e) {
		}

		return "000000";
	}

	public static synchronized int Year() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("KST"));
		return cal.get(Calendar.YEAR) - 2000;
	}

	public static synchronized int Month() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("KST"));
		return cal.get(Calendar.MONTH) + 1;
	}

	public static synchronized int Date() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("KST"));
		return cal.get(Calendar.DATE);
	}

	public static synchronized int Hour() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("KST"));
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	public static synchronized int Minute() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("KST"));
		return cal.get(Calendar.MINUTE);
	}

	public static synchronized String Time() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("KST"));
		int h = cal.get(Calendar.HOUR);
		int m = cal.get(Calendar.MINUTE);
		StringBuffer sb = new StringBuffer();
		if (h < 10) {
			sb.append("0");
		}
		sb.append(h);
		sb.append(":");
		if (m < 10) {
			sb.append("0");
		}
		sb.append(m);
		return sb.toString();
	}
}