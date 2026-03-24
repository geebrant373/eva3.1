package l1j.server.server.model.Instance;

import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;
import static l1j.server.server.model.skill.L1SkillId.POLLUTE_WATER;
import static l1j.server.server.model.skill.L1SkillId.SHOCK_STUN;

import java.awt.Robot;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javolution.util.FastTable;
import l1j.server.Config;
import l1j.server.SpecialEventHandler;
import l1j.server.AutoHuntSystem.AutoHuntController;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.MJTemplate.MJSqlHelper.Executors.Selector;
import l1j.server.MJTemplate.MJSqlHelper.Executors.Updator;
import l1j.server.MJTemplate.MJSqlHelper.Handler.Handler;
import l1j.server.MJTemplate.MJSqlHelper.Handler.SelectorHandler;
import l1j.server.Warehouse.ClanWarehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.Account;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.command.executor.L1HpBar;
import l1j.server.server.datatables.CharacterSlotItemTable;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.CharactersReducTable;
import l1j.server.server.datatables.CharactersReducTable.CharactersReduc;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.AHRegeneration;
import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.AcceleratorChecker.ACT_TYPE;
import l1j.server.server.model.Instance.L1PcInstance.Autoportion;
import l1j.server.server.model.Beginner;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.HalloweenRegeneration;
import l1j.server.server.model.HpRegenerationByDoll;
import l1j.server.server.model.L1Astar;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1ChatParty;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1EquipmentSlot;
import l1j.server.server.model.L1ExcludingList;
import l1j.server.server.model.L1GroundInventory;
import l1j.server.server.model.L1HateList;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Karma;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Node;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PartyRefresh;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.MpDecreaseByScales;
import l1j.server.server.model.MpRegenerationByDoll;
import l1j.server.server.model.SHRegeneration;
import l1j.server.server.model.classes.L1ClassFeature;
import l1j.server.server.model.gametime.GameTimeCarrier;
import l1j.server.server.model.item.function.MagicDoll;
import l1j.server.server.model.monitor.L1PcAutoUpdate;
import l1j.server.server.model.monitor.L1PcExpMonitor;
import l1j.server.server.model.monitor.L1PcGhostMonitor;
import l1j.server.server.model.monitor.L1PcHellMonitor;
import l1j.server.server.model.monitor.L1PcInvisDelay;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.monitor.Logger.ItemActionType;
import l1j.server.server.serverpackets.S_BlueMessage;
import l1j.server.server.serverpackets.S_CastleMaster;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharTitle;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_Exp;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_Lawful;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharPack;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_bonusstats;
import l1j.server.server.serverpackets.ServerBasePacket;
import server.message.ServerMessage;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1PriceTemp;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.types.Point;
import l1j.server.server.utils.CalcStat;
import l1j.server.server.utils.CommonUtil;
import manager.LinAllManager;
import manager.LinAllManagerInfoThread;
import server.LineageClient;

// Referenced classes of package l1j.server.server.model:
// L1Character, L1DropTable, L1Object, L1ItemInstance,
// L1World
public class L1PcInstance extends L1Character {
	private static final long serialVersionUID = 1L;
	private static Random _random = new Random();
	public static final int CLASSID_PRINCE = 0;
	public static final int CLASSID_PRINCESS = 1;
	public static final int CLASSID_KNIGHT_MALE = 61;
	public static final int CLASSID_KNIGHT_FEMALE = 48;
	public static final int CLASSID_ELF_MALE = 138;
	public static final int CLASSID_ELF_FEMALE = 37;
	public static final int CLASSID_WIZARD_MALE = 734;
	public static final int CLASSID_WIZARD_FEMALE = 1186;
	public static final int CLASSID_DARKELF_MALE = 2786;
	public static final int CLASSID_DARKELF_FEMALE = 2796;
	public L1ItemInstance _fishingRod = null;
	public static final int REGENSTATE_NONE = 4;
	public static final int REGENSTATE_MOVE = 2;
	public static final int REGENSTATE_ATTACK = 1;
	public L1ItemInstance TradeChaItem = null;
	public int fishX = 0;
	public int fishY = 0;
	public boolean safe_poly;
	public FastTable<L1PriceTemp> SellPriclist = null;
	public FastTable<L1PriceTemp> BuyPriclist = null;
	public int Craft_Npcid;
	public int Craft_Orderid;
	public boolean 기운축복 = false;
	public boolean autoattack = false;
	public boolean 로테_시작 = false;
	public boolean _attackClick = false;
	public short dragonmapid;
	public byte system = -1;
	public int dx = 0;
	public int dy = 0;
	public short dm = 0;
	public short fdmap = 0;
	public int dh = 0;
	public int[] trade_ids = null;
	public int[] trade_sell_ids;
	public boolean 파이널번 = false;
	public boolean RootMent = true;// 루팅 멘트]
	public boolean RootMent1 = true;// 루팅 멘트]
	private long lastFightMsgTime = 0;
	public String[] autoPotionIdx;
	public boolean isAutoPotion = false;
	public int autoPotionPercent = 0;
	public String autoPotionName;

	public List<L1ItemInstance> _자동물약초이스리스트 = new ArrayList<L1ItemInstance>();
	public List<L1ItemInstance> _자동구입초이스리스트 = new ArrayList<L1ItemInstance>();
	public List<L1ItemInstance> _자동판매초이스리스트 = new ArrayList<L1ItemInstance>();
	public ArrayList<Integer> _자동버프리스트 = new ArrayList<Integer>();
	public ArrayList<Integer> _자동물약리스트 = new ArrayList<Integer>();
	public ArrayList<Integer> _자동구입리스트 = new ArrayList<Integer>();
	public ArrayList<Integer> _자동판매리스트 = new ArrayList<Integer>();
	private boolean _자동버프세이프티존사용;
	private boolean _자동버프전투시사용;
	private boolean _자동버프사용;
	private boolean _자동물약사용;
	private boolean _자동구입사용;
	private boolean _자동판매사용;
	private int _자동물약퍼센트 = 30;

	public boolean EquipChange = false;
	public int AttackSpeedCheck2 = 0;
    public int MoveSpeedCheck = 0;
    public long AttackSpeed2;
    public long MoveSpeed;
    
	private long _EquipChange = 0;

	public long getEquipChange() {
		return _EquipChange;
	}

	public void setEquipChange(long l) {
		_EquipChange = l;
	}
	
	private int _autoPotion;

	public int getAutoPotion() {
		return _autoPotion;
	}

	public void setAutoPotion(int i) {
		_autoPotion = i;
	}
	
	private int _autoPotion_percent;

	public int getAutoPotion_Percent() {
		return _autoPotion_percent;
	}

	public void setAutoPotion_Percent(int i) {
		_autoPotion_percent = i;
	}
	
	private long _autoDropTime;

	public long getAutoDropTime() {
		return _autoDropTime;
	}

	public void setAutoDropTime(long l) {
		_autoDropTime = l;
	}
	
	private boolean _alarm_pvp = false;

	public boolean isAlarmPvp() {
		return _alarm_pvp;
	}

	public void setAlarmPvp(boolean flag) {
		_alarm_pvp = flag;
	}

	private long _PvpArlamTime;

	public long getPvpArlamTime() {
		return _PvpArlamTime;
	}

	public void setPvpArlamTime(long l) {
		_PvpArlamTime = l;
	}

	private boolean _alarm_potion = false;

	public boolean isAlarmPotion() {
		return _alarm_potion;
	}

	public void setAlarmPotion(boolean flag) {
		_alarm_potion = flag;
	}

	private long _PotionArlamTime;

	public long getPotionArlamTime() {
		return _PotionArlamTime;
	}

	public void setPotionArlamTime(long l) {
		_PotionArlamTime = l;
	}

	private boolean _alarm_weight = false;

	public boolean isAlarmWeight() {
		return _alarm_weight;
	}

	public void setAlarmWeight(boolean flag) {
		_alarm_weight = flag;
	}

	private long _WeightArlamTime;

	public long getWeightArlamTime() {
		return _WeightArlamTime;
	}

	public void setWeightArlamTime(long l) {
		_WeightArlamTime = l;
	}

	private boolean _alarm_hp = false;

	public boolean isAlarmHp() {
		return _alarm_hp;
	}

	public void setAlarmHp(boolean flag) {
		_alarm_hp = flag;
	}

	private long _HpArlamTime;

	public long getHpArlamTime() {
		return _HpArlamTime;
	}

	public void setHpArlamTime(long l) {
		_HpArlamTime = l;
	}
	
    public Timestamp getLastLoginTime() {
        return _lastLoginTime;
    }

    public void setLastLoginTime(Timestamp time) {
        _lastLoginTime = time;
    }
    
	public ArrayList<Integer> get_자동버프리스트() {
		return _자동버프리스트;
	}

	public void set_자동버프리스트(final ArrayList<Integer> _자동버프리스트) {
		this._자동버프리스트 = _자동버프리스트;
	}

	public int check_자동버프리스트(int skillid) {
		if (_자동버프리스트.contains(skillid)) {
			_자동버프리스트.remove((Object) skillid);
			return 0;
		}
		_자동버프리스트.add(skillid);
		return 1;
	}

	public void remove_자동버프리스트(int skillid) {
		if (_자동버프리스트.contains(skillid)) {
			_자동버프리스트.remove((Object) skillid);
		}
	}

	public boolean is_자동버프리스트(int skillid) {
		return _자동버프리스트.contains(skillid);
	}

	public int[] trade_buy_ids;
	/** 수배설정 **/
	private int _wanted;

	public int getWanted() {
		return this._wanted;
	}

	public void setWanted(int i) {
		this._wanted = i;
	}

	private int[] _slot1;

	private int[] _slot2;

	public int[] getSlot1() {
		return this._slot1;
	}

	public void setSlot1(int[] slot) {
		this._slot1 = slot;
	}

	public int[] getSlot2() {
		return this._slot2;
	}

	public void setSlot2(int[] slot) {
		this._slot2 = slot;
	}

	private int _HuntPrice;

	public int getHuntPrice() {
		return this._HuntPrice;
	}

	public void setHuntPrice(int i) {
		this._HuntPrice = i;
	}

	/** 수배관련시스템 **/
	private L1ClassFeature _classFeature = null;
	private L1EquipmentSlot _equipSlot;
	private String _accountName;
	private Account _account;
	private int _classId;
	private int _type;
	private int _exp;
	private short _accessLevel;

	private short _baseMaxHp = 0;
	private short _baseMaxMp = 0;
	private int _baseAc = 0;

	private int _baseBowDmgup = 0;
	private int _baseDmgup = 0;
	private int _baseHitup = 0;
	private int _baseBowHitup = 0;

	private int _baseMagicHitup = 0; // 베이스 스탯에 의한 마법 명중
	private int _baseMagicCritical = 0; // 베이스 스탯에 의한 마법 치명타(%)
	private int _baseMagicDmg = 0; // 베이스 스탯에 의한 마법 데미지
	private int _baseMagicDecreaseMp = 0; // 베이스 스탯에 의한 마법 데미지

	private int _PVPDamage = 0;
	private int _PVPDamageReduction = 0;
	private int _HitupByArmor = 0; // 방어용 기구에 의한 근접무기 명중율
	private int _bowHitupByArmor = 0; // 방어용 기구에 의한 활의 명중율
	private int _DmgupByArmor = 0; // 방어용 기구에 의한 근접무기 추타율
	private int _bowDmgupByArmor = 0; // 방어용 기구에 의한 활의 추타율

	private int _bowHitupBydoll = 0; // 인형에 의한 원거리 공격성공률
	private int _bowDmgupBydoll = 0; // 인형에 의한 원거리 추타율
	private int _HitupBydoll = 0; // 인형에 의한 근거리 공격성공률
	private int _Hitup_magic = 0; // 마법 적중
	private int _Hitup_skill = 0; // 기술 적중
	private int _Hitup_spirit = 0; // 정령 적중
	// * 허수아비데미지 변수부분
	private boolean _isrewardMon = false;

	private boolean _dmgScarecrow = false;

	public boolean getScarecrow() {
		return _dmgScarecrow;
	}

	public void setScarecrow(boolean C) {
		_dmgScarecrow = C;
	}

	/**
	 * /** 트리플
	 **/
	public boolean TRIPLE = false;
	public boolean isTRIPLE = false;

	private int _PKcount;
	private int _clanid;
	private String clanname;
	private int _clanRank;
	private byte _sex;
	private int _returnstat;
	private short _hpr = 0;
	private short _trueHpr = 0;
	private short _mpr = 0;
	private short _trueMpr = 0;

	private int _advenHp;
	private int _advenMp;
	private int _highLevel;
	public boolean _PinkName = false;
	private boolean _ghost = false;
	private boolean _isReserveGhost = false;
	private boolean _isShowTradeChat = true;
	private boolean _isCanWhisper = true;
	private boolean _isFishing = false;
	private boolean _isFishingReady = false;

	public boolean _isShowFang = false;

	public int _getLive = 0; // 생존의외침

	private int _rankLevel;

	public int getRankLevel() {
		return _rankLevel;
	}

	public void setRankLevel(int i) {
		_rankLevel = i;
	}

	private int _classranklevel;

	public int getclassRankLevel() {
		return _classranklevel;
	}

	public void setclassRankLevel(int i) {
		_classranklevel = i;
	}

	private int _bapodmg;

	public int getBapodmg() {
		return _bapodmg;
	}

	public void setBapodmg(int i) {
		_bapodmg = i;
	}

	public int LawfulAC = 0;

	public int LawfulMR = 0;

	public int LawfulSP = 0;

	public int LawfulAT = 0;

	private int _nbapoLevel;

	private int _obapoLevel;

	public int getNBapoLevel() {
		return _nbapoLevel;
	}

	public void setNBapoLevell(int i) {
		_nbapoLevel = i;
	}

	public int getOBapoLevel() {
		return _obapoLevel;
	}

	public void setOBapoLevell(int i) {
		_obapoLevel = i;
	}

	private boolean petRacing = false; // 펫레이싱
	private int petRacingLAB = 1; // 현재 LAB
	private int petRacingCheckPoint = 162; // 현재구간
	private boolean isHaunted = false;
	private boolean isDeathMatch = false;
	private boolean _isShowWorldChat = true;
	private boolean _gm;
	private boolean _monitor;
	private boolean _gmInvis;
	private boolean _isTeleport = false;
	private boolean _isDrink = false;
	private boolean _isGres = false;
	private boolean _isPinkName = false;
	private boolean _banned;
	private boolean _gresValid;
	private boolean _tradeOk;
	private boolean _mpRegenActiveByDoll;
	private boolean _mpDecreaseActiveByScales;
	private boolean _AHRegenActive;
	private boolean _SHRegenActive;
	private boolean _HalloweenRegenActive;
	private boolean _hpRegenActiveByDoll;
	private boolean _rpActive;
	public boolean FafuArmor = false;
	public boolean MaanDodge = false;
	public boolean MaanMagicIm = false;
	public boolean MaanMagicDmg = false;
	public boolean MaanAddDmg = false;

	private int invisDelayCounter = 0;
	private Object _invisTimerMonitor = new Object();

	private int _ghostSaveLocX = 0;
	private int _ghostSaveLocY = 0;
	private short _ghostSaveMapId = 0;
	private int _ghostSaveHeading = 0;

	private ScheduledFuture<?> _ghostFuture;
	private ScheduledFuture<?> _hellFuture;
	private ScheduledFuture<?> _autoUpdateFuture;
	private ScheduledFuture<?> _expMonitorFuture;

	private Timestamp _lastPk;
	private Timestamp _deleteTime;
    private Timestamp _lastLoginTime;
    
	private int _weightReduction = 0;
	private int _hasteItemEquipped = 0;
	private int _damageReductionByArmor = 0;

	private final L1ExcludingList _excludingList = new L1ExcludingList();
	private final AcceleratorChecker _acceleratorChecker = new AcceleratorChecker(this);
	private ArrayList<Integer> skillList = new ArrayList<Integer>();

	private int _teleportY = 0;
	private int _teleportX = 0;
	private short _teleportMapId = 0;
	private int _teleportHeading = 0;

	private int _tempCharGfxAtDead;
	private int _fightId;
	private byte _chatCount = 0;
	private long _oldChatTimeInMillis = 0L;

	private int _elfAttr;
	private int _expRes;

	private int _onlineStatus;
	private int _로테이션;
	private int _homeTownId;
	private int _contribution;
	private int _food;
	private int _hellTime;
	private int _partnerId;
	private long _fishingTime = 0;
	private int _dessertId = 0;
	private int _callClanId;
	private int _callClanHeading;

	private int _currentWeapon;
	private final L1Karma _karma = new L1Karma();
	private final L1PcInventory _inventory;
	private final L1Inventory _tradewindow;

	private L1ItemInstance _weapon;
	private L1ItemInstance _armor;
	private L1Party _party;
	private L1ChatParty _chatParty;

	private int _cookingId = 0;
	private int _partyID;
	private int _partyType;
	private int _tradeID;
	private int _tempID;
	private int _ubscore;

	private L1Quest _quest;

	private HpRegenerationByDoll _hpRegenByDoll;
	private MpRegenerationByDoll _mpRegenByDoll;
	private MpDecreaseByScales _mpDecreaseByScales;
	private AHRegeneration _AHRegen;
	private SHRegeneration _SHRegen;
	private HalloweenRegeneration _HalloweenRegen;
	private L1PartyRefresh _rp;
	private static Timer _regenTimer = new Timer(true);

	public void Delay(int delayTime) throws Exception {

		int mdelayTime;
		mdelayTime = delayTime;
		Robot robot = new Robot();
		robot.delay(mdelayTime);
	} // 로봇다이 멘트

	/** 로봇 멘트 관련 **/
	private boolean _isRobot = false;

	public boolean isRobot() {
		return _isRobot;
	}

	public void setRobot(boolean flag) {
		_isRobot = flag;
	}

	private boolean _isTradingInPrivateShop = false;
	private boolean _isPrivateShop = false;
	private int _partnersPrivateShopItemCount = 0;

	private final ArrayList<L1BookMark> _bookmarks;
	private ArrayList<L1PrivateShopSellList> _sellList = new ArrayList<L1PrivateShopSellList>();
	private ArrayList<L1PrivateShopBuyList> _buyList = new ArrayList<L1PrivateShopBuyList>();

	private final Map<Integer, L1NpcInstance> _petlist = new HashMap<Integer, L1NpcInstance>();
	private final Map<Integer, L1DollInstance> _dolllist = new HashMap<Integer, L1DollInstance>();
	private final Map<Integer, L1FollowerInstance> _followerlist = new HashMap<Integer, L1FollowerInstance>();

	private byte[] _shopChat;
	private LineageClient _netConnection;
	private static Logger _log = Logger.getLogger(L1PcInstance.class.getName());
	private long lastSavedTime = System.currentTimeMillis();
	private long lastSavedTime_inventory = System.currentTimeMillis();

	private int adFeature = 1;

	public L1PcInstance() {
		super();
		_accessLevel = 0;
		_currentWeapon = 0;
		_inventory = new L1PcInventory(this);
		_tradewindow = new L1Inventory();
		_bookmarks = new ArrayList<L1BookMark>();
		_quest = new L1Quest(this);
		_equipSlot = new L1EquipmentSlot(this);
	}

	private boolean _isAutoClanjoin = false;//무인가입
	
	public boolean isAutoClanjoin() {
		return _isAutoClanjoin;
	}

	public void setAutoClanjoin(boolean flag) {
		_isAutoClanjoin = flag;
	}
	
	private long _ThunderWanddelayCheck;

	public long getThunderWanddelayCheck() {
		return _ThunderWanddelayCheck;
	}

	public void setThunderWandelayCheck(long ThunderWand) {
		_ThunderWanddelayCheck = ThunderWand;
	}

	public long attack_time_count = 0;

	public long getAttack_Time_count() {
		return attack_time_count;
	}

	public void setAttack_Time_count(long num) {
		this.attack_time_count = num;
	}

	public long attack_time_bug = 0;

	public long getAttack_Time_bug() {
		return attack_time_bug;
	}

	public void setAttack_Time_bug(long num) {
		this.attack_time_bug = num;
	}

	public long attack_time = 0;

	public long getAttack_Time() {
		return attack_time;
	}

	public void setAttack_Time(long num) {
		this.attack_time = num;
	}

	public int getadFeature() {
		return adFeature;
	}

	public void setadFeature(int count) {
		this.adFeature = count;
	}

	public long getlastSavedTime() {
		return lastSavedTime;
	}

	public long getlastSavedTime_inventory() {
		return lastSavedTime_inventory;
	}

	public void setlastSavedTime(long stime) {
		this.lastSavedTime = stime;
	}

	public void setlastSavedTime_inventory(long stime) {
		this.lastSavedTime_inventory = stime;
	}

	public void setSkillMastery(int skillid) {
		if (!skillList.contains(skillid)) {
			skillList.add(skillid);
		}
	}

	public void removeSkillMastery(int skillid) {
		if (skillList.contains((Object) skillid)) {
			skillList.remove((Object) skillid);
		}
	}

	public boolean isSkillMastery(int skillid) {
		return skillList.contains(skillid);
	}

	public void clearSkillMastery() {
		skillList.clear();
	}

	private int birth;

	public void setBirthday(int birth) {
		this.birth = birth;
	}

	public int getBirthday() {
		return birth;
	}

	public short getHpr() {
		return _hpr;
	}

	public void addHpr(int i) {
		_trueHpr += i;
		_hpr = (short) Math.max(0, _trueHpr);
	}

	public short getMpr() {
		return _mpr;
	}

	public void addMpr(int i) {
		_trueMpr += i;
		_mpr = (short) Math.max(0, _trueMpr);
	}

	private String _characterName = "";

	public void setCharacterName(String name) {
		_characterName = name;
	}

	public String getCharacterName() {
		return _characterName;
	}

	public void startRP() {
		final int INTERVAL = 25000;
		if (!_rpActive) {
			_rp = new L1PartyRefresh(this);
			_regenTimer.scheduleAtFixedRate(_rp, INTERVAL, INTERVAL);
			_rpActive = true;
		}
	}

	public void startHpRegenerationByDoll() {
		final int INTERVAL_BY_DOLL = 64000;
		boolean isExistHprDoll = false;

		for (L1DollInstance doll : getDollList().values()) {
			if (doll.isHpRegeneration()) {
				isExistHprDoll = true;
			}
		}
		if (!_hpRegenActiveByDoll && isExistHprDoll) {
			_hpRegenByDoll = new HpRegenerationByDoll(this);
			_regenTimer.scheduleAtFixedRate(_hpRegenByDoll, INTERVAL_BY_DOLL, INTERVAL_BY_DOLL);
			_hpRegenActiveByDoll = true;
		}
	}

	public void startAHRegeneration() {
		final int INTERVAL = 600000;
		if (!_AHRegenActive) {
			_AHRegen = new AHRegeneration(this);
			_regenTimer.scheduleAtFixedRate(_AHRegen, INTERVAL, INTERVAL);
			_AHRegenActive = true;
		}
	}

	public void startSHRegeneration() {
		final int INTERVAL = 1800000;
		if (!_SHRegenActive) {
			_SHRegen = new SHRegeneration(this);
			_regenTimer.scheduleAtFixedRate(_SHRegen, INTERVAL, INTERVAL);
			_SHRegenActive = true;
		}
	}

	public void startHalloweenRegeneration() {
		final int INTERVAL = 900000;
		if (!_HalloweenRegenActive) {
			_HalloweenRegen = new HalloweenRegeneration(this);
			_regenTimer.scheduleAtFixedRate(_HalloweenRegen, INTERVAL, INTERVAL);
			_HalloweenRegenActive = true;
		}
	}


	public void stopHpRegenerationByDoll() {
		if (_hpRegenActiveByDoll) {
			_hpRegenByDoll.cancel();
			_hpRegenByDoll = null;
			_hpRegenActiveByDoll = false;
		}
	}

	public void startMpRegenerationByDoll() {
		final int INTERVAL_BY_DOLL = 15000;// 엠틱시간15초 겜블수정0515
		boolean isExistMprDoll = false;

		for (L1DollInstance doll : getDollList().values()) {

			if (doll.isMpRegeneration()) {
				isExistMprDoll = true;
			}
		}
		if (!_mpRegenActiveByDoll && isExistMprDoll) {
			_mpRegenByDoll = new MpRegenerationByDoll(this);
			_regenTimer.scheduleAtFixedRate(_mpRegenByDoll, INTERVAL_BY_DOLL, INTERVAL_BY_DOLL);
			_mpRegenActiveByDoll = true;
		}
	}

	public void startMpDecreaseByScales() {
		final int INTERVAL_BY_SCALES = 4000;
		_mpDecreaseByScales = new MpDecreaseByScales(this);
		_regenTimer.scheduleAtFixedRate(_mpDecreaseByScales, INTERVAL_BY_SCALES, INTERVAL_BY_SCALES);
		_mpDecreaseActiveByScales = true;
	}

	/*
	 * public void stopMpRegeneration() { if (_mpRegenActive) { _mpRegen.cancel();
	 * _mpRegen = null; _mpRegenActive = false; } }
	 */
	public void stopRP() {
		if (_rpActive) {
			_rp.cancel();
			_rp = null;
			_rpActive = false;
		}
	}

	public void stopMpRegenerationByDoll() {
		if (_mpRegenActiveByDoll) {
			_mpRegenByDoll.cancel();
			_mpRegenByDoll = null;
			_mpRegenActiveByDoll = false;
		}
	}

	public void stopMpDecreaseByScales() {
		if (_mpDecreaseActiveByScales) {
			_mpDecreaseByScales.cancel();
			_mpDecreaseByScales = null;
			_mpDecreaseActiveByScales = false;
		}
	}

	public void stopAHRegeneration() {
		if (_AHRegenActive) {
			_AHRegen.cancel();
			_AHRegen = null;
			_AHRegenActive = false;
		}
	}

	public void stopSHRegeneration() {
		if (_SHRegenActive) {
			_SHRegen.cancel();
			_SHRegen = null;
			_SHRegenActive = false;
		}
	}

	public void stopHalloweenRegeneration() {
		if (_HalloweenRegenActive) {
			_HalloweenRegen.cancel();
			_HalloweenRegen = null;
			_HalloweenRegenActive = false;
		}
	}

	public void startObjectAutoUpdate() {
		final long INTERVAL_AUTO_UPDATE = 300;
		getNearObjects().removeAllKnownObjects();
		_autoUpdateFuture = GeneralThreadPool.getInstance().pcScheduleAtFixedRate(new L1PcAutoUpdate(getId()), 0L,
				INTERVAL_AUTO_UPDATE);
	}

	public void stopEtcMonitor() {

		if (_autoUpdateFuture != null) {
			_autoUpdateFuture.cancel(true);
			_autoUpdateFuture = null;
		}
		if (_expMonitorFuture != null) {
			_expMonitorFuture.cancel(true);
			_expMonitorFuture = null;
		}

		if (_ghostFuture != null) {
			_ghostFuture.cancel(true);
			_ghostFuture = null;
		}

		if (_hellFuture != null) {
			_hellFuture.cancel(true);
			_hellFuture = null;
		}

	}

	public void stopEquipmentTimer() {
		List<L1ItemInstance> allItems = this.getInventory().getItems();
		for (L1ItemInstance item : allItems) {
			if (item.isEquipped() && item.getRemainingTime() > 0) {
				item.stopEquipmentTimer();
			}
		}
	}

	public void onChangeExp() {
		int level = ExpTable.getLevelByExp(getExp());
		int char_level = getLevel();
		int gap = level - char_level;
		if (gap == 0) {
			// sendPackets(new S_OwnCharStatus(this));
			sendPackets(new S_Exp(this));
			return;
		}

		if (gap > 0) {
			levelUp(gap);
		} else if (gap < 0) {
			levelDown(gap);
		}
	}

	@Override
	public void onPerceive(L1PcInstance pc) {
		if (isGhost()) {
			return;
		}
		pc.getNearObjects().addKnownObject(this);
		pc.sendPackets(new S_OtherCharPacks(this));

		if (pc._ispinknameauto) {
			sendPackets(new S_PinkName(pc.getId(), 3600 * 1000));
		}
		if (isPinkName())
			pc.sendPackets(new S_PinkName(getId(),
					getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.STATUS_PINK_NAME)));

		if (isInParty() && getParty().isMember(pc)) {
			pc.sendPackets(new S_HPMeter(this));
		}
		if (isPrivateShop()) {
			pc.sendPackets(new S_DoActionShop(getId(), ActionCodes.ACTION_Shop, getShopChat()));
		}
	}

	private void removeOutOfRangeObjects() {
		for (L1Object known : getNearObjects().getKnownObjects()) {
			if (known == null) {
				continue;
			}
			if (Config.PC_RECOGNIZE_RANGE == -1) {
				if (!getLocation().isInScreen(known.getLocation())) {
					getNearObjects().removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			} else {
				if (getLocation().getTileLineDistance(known.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
					getNearObjects().removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			}
		}
	}

	public void updateObject() {
		GeneralThreadPool.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				updateObject0();
			}
		});
	}

	private final Object updateSync = new Object();

	public void updateObject0() {
		synchronized (updateSync) {
			removeOutOfRangeObjects();// 추가
			ArrayList<L1Object> _Vlist = null;
			_Vlist = L1World.getInstance().getVisibleObjects(this, Config.PC_RECOGNIZE_RANGE);

			for (L1Object visible : _Vlist) {
				if (visible == null)
					continue;

				try {
					if (!getNearObjects().knownsObject(visible)) {
						visible.onPerceive(this);
					} else {
						if (visible instanceof L1NpcInstance) {
							L1NpcInstance npc = (L1NpcInstance) visible;
							if (getLocation().isInScreen(npc.getLocation()) && npc.getHiddenStatus() != 0) {
								npc.approachPlayer(this);
							}
						}
						// 클라이언트 죽는 현상 때문에 임시 주석처리
						// if (visible instanceof L1DoorInstance) { // 4층 케이나방 1차문수정 220108
						// L1DoorInstance door = (L1DoorInstance) visible;
						// door.onPerceive(this);
						// }
					}

					if (visible instanceof L1PcInstance) {
						if (_attackClick) {
							sendPackets(new S_PinkName(visible.getId(), 30));
						}
					}

					if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GMSTATUS_HPBAR)
							&& L1HpBar.isHpBarTarget(visible)) {
						sendPackets(new S_HPMeter((L1Character) visible));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	//
	// public void updateObject() {
	//
	//
	// for (L1Object visible : L1World.getInstance().getVisibleObjects(this,
	// Config.PC_RECOGNIZE_RANGE)) {
	// if (!getNearObjects().knownsObject(visible)) {
	// visible.onPerceive(this);
	// } else {
	// if (visible instanceof L1NpcInstance) {
	// L1NpcInstance npc = (L1NpcInstance) visible;
	// if (getLocation().isInScreen(npc.getLocation()) && npc.getHiddenStatus() !=
	// 0) {
	// npc.approachPlayer(this);
	// }
	// }
	// if (visible instanceof L1DoorInstance) { // 4층 케이나방 1차문수정 220108
	// L1DoorInstance door = (L1DoorInstance) visible;
	// door.onPerceive(this);
	// }
	// }
	//
	// if (visible instanceof L1PcInstance) {
	// if (_attackClick) {
	// sendPackets(new S_PinkName(visible.getId(), 30));
	// }
	// }
	//
	// if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GMSTATUS_HPBAR) &&
	// L1HpBar.isHpBarTarget(visible)) {
	// sendPackets(new S_HPMeter((L1Character) visible));
	// }
	// }
	// }

	private void sendVisualEffect() {
		int poisonId = 0;
		if (getPoison() != null) {
			poisonId = getPoison().getEffectId();
		}
		if (getParalysis() != null) {
			poisonId = getParalysis().getEffectId();
		}
		if (poisonId != 0) {
			sendPackets(new S_Poison(getId(), poisonId));
			Broadcaster.broadcastPacket(this, new S_Poison(getId(), poisonId));
		}
	}

	public void sendVisualEffectAtLogin() {

		sendVisualEffect();
	}

	public void sendCastleMaster() {
		if (getClanid() != 0) {
			L1Clan clan = L1World.getInstance().getClan(getClanname());
			if (clan != null) {
				if (isCrown() && getId() == clan.getLeaderId() && clan.getCastleId() != 0) {
					sendPackets(new S_CastleMaster(clan.getCastleId(), getId()));
				}
			}
		}
	}

	public void sendVisualEffectAtTeleport() {
		if (isDrink()) {
			//sendPackets(new S_Liquor(getId()));
		}
		sendVisualEffect();
	}

	@Override
	public void setCurrentHp(int i) {
		if (getCurrentHp() == i)
			return;
		// if (isGm())
		// i = getMaxHp();
		super.setCurrentHp(i);
		sendPackets(new S_HPUpdate(getCurrentHp(), getMaxHp()));
		if (isInParty())
			getParty().updateMiniHP(this);
	}

	@Override
	public void setCurrentMp(int i) {
		if (getCurrentMp() == i)
			return;
		if (isGm())
			i = getMaxMp();
		super.setCurrentMp(i);
		sendPackets(new S_MPUpdate(getCurrentMp(), getMaxMp()));
	}

	@Override
	public L1PcInventory getInventory() {
		return _inventory;
	}

	public L1Inventory getTradeWindowInventory() {
		return _tradewindow;
	}

	public boolean isGmInvis() {
		return _gmInvis;
	}

	public void setGmInvis(boolean flag) {
		_gmInvis = flag;
	}

/*	public int getCurrentWeapon() {
		return _currentWeapon;
	}

	public void setCurrentWeapon(int i) {
		_currentWeapon = i;
	}*/

	public int getType() {
		return _type;
	}

	public void setType(int i) {
		_type = i;
	}

	public short getAccessLevel() {
		return _accessLevel;
	}

	public void setAccessLevel(short i) {
		_accessLevel = i;
	}

	public int getClassId() {
		return _classId;
	}

	public void setClassId(int i) {
		_classId = i;
		_classFeature = L1ClassFeature.newClassFeature(i);
	}

	public L1ClassFeature getClassFeature() {
		return _classFeature;
	}

	@Override
	public synchronized int getExp() {
		return _exp;
	}

	@Override
	public synchronized void setExp(int i) {
		_exp = i;
	}

	public synchronized int getReturnStat() {
		return _returnstat;
	}

	public synchronized void setReturnStat(int i) {
		_returnstat = i;
	}

	private L1PcInstance getStat() {
		return null;
	}

	public void reduceCurrentHp(double d, L1Character l1character) {
		getStat().reduceCurrentHp(d, l1character);
	}

	private void notifyPlayersLogout(List<L1PcInstance> playersArray) {
		for (L1PcInstance player : playersArray) {
			if (player.getNearObjects().knownsObject(this)) {
				player.getNearObjects().removeKnownObject(this);
				player.sendPackets(new S_RemoveObject(this));
			}
		}
	}

	public void logout() {
		L1World world = L1World.getInstance();
		notifyPlayersLogout(getNearObjects().getKnownPlayers());
		world.removeVisibleObject(this);
		world.removeObject(this);
		notifyPlayersLogout(world.getRecognizePlayer(this));
		stopEquipmentTimer();
		try {
            if (!(noPlayerCK || isPrivateShop())) {
            	LinAllManager.getInstance().LogLogOutAppend(getName(), getNetConnection().getHostname());
             }
        } catch (Exception e) {
        }
		_inventory.clearItems();
		WarehouseManager w = WarehouseManager.getInstance();
		w.delPrivateWarehouse(this.getAccountName());
		w.delElfWarehouse(this.getAccountName());
		w.delPackageWarehouse(this.getAccountName());

		getNearObjects().removeAllKnownObjects();
		CharacterSlotItemTable.getInstance().updateCharSlotItems(this);
		stopHalloweenRegeneration();
		stopAHRegeneration();
		stopHpRegenerationByDoll();
		stopMpRegenerationByDoll();
		stopSHRegeneration();
		stopMpDecreaseByScales();
		Stoptatat();
		stopEtcMonitor();
		EndAutoController();
		setDead(true);
		setNetConnection(null);
	}

	public LineageClient getNetConnection() {
		return _netConnection;
	}

	public void setNetConnection(LineageClient clientthread) {
		_netConnection = clientthread;
	}

	public boolean isInParty() {
		return getParty() != null;
	}

	public L1Party getParty() {
		return _party;
	}

	public void setParty(L1Party p) {
		_party = p;
	}

	public boolean isInChatParty() {
		return getChatParty() != null;
	}

	public L1ChatParty getChatParty() {
		return _chatParty;
	}

	public void setChatParty(L1ChatParty cp) {
		_chatParty = cp;
	}

	public int getPartyID() {
		return _partyID;
	}

	public void setPartyID(int partyID) {
		_partyID = partyID;
	}

	public int getPartyType() {
		return _partyType;
	}

	public void setPartyType(int partyType) {
		_partyType = partyType;
	}

	public int getTradeID() {
		return _tradeID;
	}

	public void setTradeID(int tradeID) {
		_tradeID = tradeID;
	}

	public void setTradeOk(boolean tradeOk) {
		_tradeOk = tradeOk;
	}

	public boolean getTradeOk() {
		return _tradeOk;
	}

	public int getTempID() {
		return _tempID;
	}

	public void setTempID(int tempID) {
		_tempID = tempID;
	}

	public boolean isTeleport() {
		return _isTeleport;
	}

	public void setTeleport(boolean flag) {
		_isTeleport = flag;
	}

	public boolean isDrink() {
		return _isDrink;
	}

	public void setDrink(boolean flag) {
		_isDrink = flag;
	}

	public boolean isGres() {
		return _isGres;
	}

	public void setGres(boolean flag) {
		_isGres = flag;
	}

	public boolean _ispinknameauto;

	public boolean isPinkName() {
		return _isPinkName;
	}

	public void setPinkName(boolean flag) {
		_isPinkName = flag;
	}

	public ArrayList<L1PrivateShopSellList> getSellList() {
		return _sellList;
	}

	public ArrayList<L1PrivateShopBuyList> getBuyList() {
		return _buyList;
	}

	public void setShopChat(byte[] chat) {
		_shopChat = chat;
	}

	public byte[] getShopChat() {
		return _shopChat;
	}

	public boolean isPrivateShop() {
		return _isPrivateShop;
	}

	public void setPrivateShop(boolean flag) {
		_isPrivateShop = flag;
	}

	public int getLive() {
		return _getLive;
	}

	public void addLive(int Live) {
		_getLive += Live;
	} // 생존의외침

	public void setLive(int Live) {
		_getLive = Live;
	}

	public boolean isTradingInPrivateShop() {
		return _isTradingInPrivateShop;
	}

	public void setTradingInPrivateShop(boolean flag) {
		_isTradingInPrivateShop = flag;
	}

	public int getPartnersPrivateShopItemCount() {
		return _partnersPrivateShopItemCount;
	}

	public void setPartnersPrivateShopItemCount(int i) {
		_partnersPrivateShopItemCount = i;
	}

	/** 캐릭별 추가 데미지 */
	private int _AddDamage = 0;
	private int _AddDamageRate = 0;
	private int _AddReduction = 0;
	private int _AddBowReduction = 0;
	private int _AddReductionRate = 0;

	public int getAddDamage() {
		return _AddDamage;
	}

	public void setAddDamage(int addDamage) {
		_AddDamage = addDamage;
	}

	public int getAddDamageRate() {
		return _AddDamageRate;
	}

	public void setAddDamageRate(int addDamageRate) {
		_AddDamageRate = addDamageRate;
	}

	public int getAddReduction() {
		return _AddReduction;
	}

	public void setAddReduction(int addReduction) {
		_AddReduction = addReduction;
	}

	public int getAddBowReduction() {
		return _AddBowReduction;
	}

	public void setAddBowReduction(int addBowReduction) {
		_AddBowReduction = addBowReduction;
	}
	
	public int getAddReductionRate() {
		return _AddReductionRate;
	}

	public void setAddReductionRate(int addReductionRate) {
		_AddReductionRate = addReductionRate;
	}

	public void sendPackets(ServerBasePacket serverbasepacket) {
		if (getNetConnection() == null) {
			return;
		}

		try {
			getNetConnection().sendPacket(serverbasepacket);
		} catch (Exception e) {
		}
	}

	public void sendPackets(String s) {
		sendPackets(new S_SystemMessage(s), true);
	}

	@Override
	public void onAction(L1PcInstance attacker) {
		if (attacker == null) {
			return;
		}
		if (isTeleport()) {
			return;
		}
		if (CharPosUtil.getZoneType(this) == 1 || CharPosUtil.getZoneType(attacker) == 1) {
			L1Attack attack_mortion = new L1Attack(attacker, this); 
			attack_mortion.action();
			return;
		}

		if (checkNonPvP(this, attacker) == true) {
			L1Attack attack_mortion = new L1Attack(attacker, this);
			attack_mortion.action();
			return;
		}
		
		if (getCurrentHp() > 0 && !isDead()) {
			attacker.delInvis();
			boolean isCounterBarrier = false;
			boolean isMortalBody = false;
			L1Attack attack = new L1Attack(attacker, this);
			L1Magic magic = null;
			
			if (attack.calcHit()) {
				if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
					magic = new L1Magic(this, attacker);
					boolean isProbability = magic.calcProbabilityMagic(L1SkillId.COUNTER_BARRIER);
					boolean isShortDistance = attack.isShortDistance();
					if (isProbability && isShortDistance) {
						isCounterBarrier = true;
					}
				}
				if (!isCounterBarrier && !isMortalBody) {
					attacker.setPetTarget(this);
					attack.calcDamage();
					attack.addPcPoisonAttack(attacker, this);
				}
			}
			if (isCounterBarrier) {
				attack.actionCounterBarrier();
				attack.commitCounterBarrier();
			} else if (isMortalBody) {
				attack.actionMortalBody();
				attack.commitMortalBody();
			} else {
				attack.action();
				attack.commit();
			}
		}
	}

	public boolean checkNonPvP(L1PcInstance pc, L1Character target) {
		L1PcInstance targetpc = null;

		if (target instanceof L1PcInstance)
			targetpc = (L1PcInstance) target;
		else if (target instanceof L1PetInstance)
			targetpc = (L1PcInstance) ((L1PetInstance) target).getMaster();
		else if (target instanceof L1SummonInstance)
			targetpc = (L1PcInstance) ((L1SummonInstance) target).getMaster();

		if (targetpc == null)
			return false;

		if (!Config.ALT_NONPVP) {
			if (getMap().isCombatZone(getLocation()))
				return false;

			for (L1War war : L1World.getInstance().getWarList()) {
				if (pc.getClanid() != 0 && targetpc.getClanid() != 0) {
					boolean same_war = war.CheckClanInSameWar(pc.getClanname(), targetpc.getClanname());

					if (same_war == true)
						return false;
				}
			}

			if (target instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) target;
				if (isInWarAreaAndWarTime(pc, targetPc))
					return false;
			}
			return true;
		}

		return false;
	}

	public boolean AttackCheckUseSKill = false;
	public int AttackCheckUseSKillDelay = 0;

	AutoAttack at = null;

	public void startatat() {
		synchronized (this) {
			if (at == null) {
				at = new AutoAttack();
				at.start();
			}
		}
	}

	// ddd
	public void Stoptatat() {
		synchronized (this) {
			if (at != null) {
				at = null;
				target = null;
				run = false;
			}

		}
	}

	class AutoAttack implements Runnable {
		public void start() {
			GeneralThreadPool.getInstance().execute(AutoAttack.this);
		}

		public void run() {
			try {
				if (!run) {
					at = null;
					target = null;
					return;
				}

				if (AttackCheckUseSKill) {
					GeneralThreadPool.getInstance().schedule(AutoAttack.this, AttackCheckUseSKillDelay);
					return;
				}

				autoattack();
				GeneralThreadPool.getInstance().schedule(AutoAttack.this,
						getAcceleratorChecker().getRightInterval(ACT_TYPE.ATTACK) + 50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public L1Object target = null;
	private int range = 1;
	public boolean run = false;

	protected void autoattack() {
		try {
			if (target == null) {
				run = false;
				return;
			}
			if (getNetConnection() == null) {
				run = false;
				return;
			}
			L1Object cktarget = L1World.getInstance().findObject(target.getId());
			if (cktarget == null) {
				run = false;
				return;
			}

			if (cktarget instanceof L1Character) {
				L1Character ttr = (L1Character) cktarget;
				if (ttr != null) {
					if (ttr.isDead()) {
						target = null;
						run = false;
						return;
					}
				}
			}

			if (isGmInvis() || isGhost() || isDead() || isTeleport() || isInvisDelay()) {
				run = false;
				return;
			}

			if (isInvisble()) {
				run = false;
				return;
			}
			if (isInvisDelay()) {
				run = false;
				return;
			}

			if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN)
					|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)
					|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ICE_LANCE)
					|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)
					|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CURSE_PARALYZE)
					|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED)
					|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_POISON_PARALYZED)) {
				run = false;
				return;
			}

			if (getCurrentWeapon() == 20 || getCurrentWeapon() == 62) {
				range = 13;
			} else {
				range = 1;
			}

			if (getLocation().getTileLineDistance(new Point(target.getX(), target.getY())) > range) {
				run = false;
				return;
			}
			if (target instanceof L1Character) {
				if (((L1Character) target).isDead()) {
					run = false;
					return;
				}
			}

			if (target instanceof L1NpcInstance) {
				int hiddenStatus = ((L1NpcInstance) target).getHiddenStatus();
				if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK
						|| hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY) {
					run = false;
					return;
				}
			}

			if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {
				getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.ABSOLUTE_BARRIER);
				startHpRegenerationByDoll();
				startMpRegenerationByDoll();
			}

			if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MEDITATION)) {
				getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.MEDITATION);
			}

			delInvis();
			setRegenState(REGENSTATE_ATTACK);
			L1Character cha = null;
			if (target instanceof L1Character) {
				cha = (L1Character) target;
			}
			if (cha != null && target != null && !cha.isDead()) {
				target.onAction(this);
			} else {
				run = false;
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isInWarAreaAndWarTime(L1PcInstance pc, L1PcInstance target) {
		int castleId = L1CastleLocation.getCastleIdByArea(pc);
		int targetCastleId = L1CastleLocation.getCastleIdByArea(target);
		if (castleId != 0 && targetCastleId != 0 && castleId == targetCastleId) {
			if (WarTimeController.getInstance().isNowWar(castleId)) {
				return true;
			}
		}
		return false;
	}

	public void setPetTarget(L1Character target) {
		Object[] petList = getPetList().values().toArray();
		L1PetInstance pets = null;
		L1SummonInstance summon = null;
		for (Object pet : petList) {
			if (pet instanceof L1PetInstance) {
				pets = (L1PetInstance) pet;
				pets.setMasterTarget(target);
			} else if (pet instanceof L1SummonInstance) {
				summon = (L1SummonInstance) pet;
				summon.setMasterTarget(target);
			}
		}
	}

	public void delInvis() {
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INVISIBILITY)) {
			getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.INVISIBILITY);
			sendPackets(new S_Invis(getId(), 0));
			Broadcaster.broadcastPacket(this, new S_Invis(getId(), 0));
			// broadcastPacket(new S_OtherCharPacks(this));
		}
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLIND_HIDING)) {
			getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.BLIND_HIDING);
			sendPackets(new S_Invis(getId(), 0));
			Broadcaster.broadcastPacket(this, new S_Invis(getId(), 0));
			// broadcastPacket(new S_OtherCharPacks(this));
		}
	}

	public void delBlindHiding() {
		getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.BLIND_HIDING);
		sendPackets(new S_Invis(getId(), 0));
		Broadcaster.broadcastPacket(this, new S_Invis(getId(), 0));
		// broadcastPacket(new S_OtherCharPacks(this));
	}

	public void receiveDamage(L1Character attacker, int damage, int attr) {
		Random random = new Random();
		int player_mr = getResistance().getEffectedMrBySkill();
		int rnd = random.nextInt(100) + 1;
		if (player_mr >= rnd) {
			damage /= 2;
		}
		receiveDamage(attacker, damage, false);
	}

	public void receiveManaDamage(L1Character attacker, int mpDamage) {
		if (mpDamage > 0 && !isDead()) {
			delInvis();
			if (attacker instanceof L1PcInstance) {
				L1PinkName.onAction(this, attacker);
			}
			if (attacker instanceof L1PcInstance && ((L1PcInstance) attacker).isPinkName()) {
				L1GuardInstance guard = null;
				for (L1Object object : L1World.getInstance().getVisibleObjects(attacker)) {
					if (object instanceof L1GuardInstance) {
						guard = (L1GuardInstance) object;
						guard.setTarget(((L1PcInstance) attacker));
					}
				}
			}

			int newMp = getCurrentMp() - mpDamage;
			this.setCurrentMp(newMp);
		}
	}

	public long _oldTime = 0;

	public void receiveDamage(L1Character attacker, double damage, boolean isMagicDamage) {
		try {
			if (getCurrentHp() > 0 && !isDead() && !isGhost()) {
				if (attacker != this && !getNearObjects().knownsObject(attacker)
						&& attacker.getMapId() == this.getMapId()) {
					attacker.onPerceive(this);
				}
				if (isMagicDamage == true) {
					long nowTime = System.currentTimeMillis();
					long interval = nowTime - _oldTime;

					if (damage <= 0) {
						damage = damage;
					} else {
						if (2000 > interval && interval >= 1900) {
							damage = (damage * (100 - (10 / 3))) / 100;
						} else if (1900 > interval && interval >= 1800) {
							damage = (damage * (100 - 2 * (10 / 3))) / 100;
						} else if (1800 > interval && interval >= 1700) {
							damage = (damage * (100 - 3 * (10 / 3))) / 100;
						} else if (1700 > interval && interval >= 1600) {
							damage = (damage * (100 - 4 * (10 / 3))) / 100;
						} else if (1600 > interval && interval >= 1500) {
							damage = (damage * (100 - 5 * (10 / 3))) / 100;
						} else if (1500 > interval && interval >= 1400) {
							damage = (damage * (100 - 6 * (10 / 3))) / 100;
						} else if (1400 > interval && interval >= 1300) {
							damage = (damage * (100 - 7 * (10 / 3))) / 100;
						} else if (1300 > interval && interval >= 1200) {
							damage = (damage * (100 - 8 * (10 / 3))) / 100;
						} else if (1200 > interval && interval >= 1100) {
							damage = (damage * (100 - 9 * (10 / 3))) / 100;
						} else if (1100 > interval && interval >= 1000) {
							damage = (damage * (100 - 10 * (10 / 3))) / 100;
						} else if (1000 > interval && interval >= 900) {
							damage = (damage * (100 - 11 * (10 / 3))) / 100;
						} else if (900 > interval && interval >= 800) {
							damage = (damage * (100 - 12 * (10 / 3))) / 100;
						} else if (800 > interval && interval >= 700) {
							damage = (damage * (100 - 13 * (10 / 3))) / 100;
						} else if (700 > interval && interval >= 600) {
							damage = (damage * (100 - 14 * (10 / 3))) / 100;
						} else if (600 > interval && interval >= 500) {
							damage = (damage * (100 - 15 * (10 / 3))) / 100;
						} else if (500 > interval && interval >= 400) {
							damage = (damage * (100 - 16 * (10 / 3))) / 100;
						} else if (400 > interval && interval >= 300) {
							damage = (damage * (100 - 17 * (10 / 3))) / 100;
						} else if (300 > interval && interval >= 200) {
							damage = (damage * (100 - 18 * (10 / 3))) / 100;
						} else if (200 > interval && interval >= 100) {
							damage = (damage * (100 - 19 * (10 / 3))) / 100;
						} else if (100 > interval && interval >= 0) {
							damage = (damage * (100 - 20 * (10 / 3))) / 100;
						} else {
							damage = damage;
						}
						if (damage < 1) {
							damage = 0;
						}
						_oldTime = nowTime;
					}
				}
				if (damage > 0) {
					if (this instanceof L1RobotInstance) {
						L1RobotInstance bot = (L1RobotInstance) this;

						if (bot.이동딜레이 == 0) {
							int sleepTime = bot.calcSleepTime(L1RobotInstance.DMG_MOTION_SPEED);
							bot.이동딜레이 = sleepTime;
						}
						if (attacker instanceof L1PcInstance) {
							if (!isParalyzed() && !isSleeped()) {
								if (bot.텔사냥 && bot.getMap().isTeleportable()) {

									int percent = (int) (Math.random() * 100); // 0 ~ 100 사이
									if (percent != 0 && Config.ROBOT_TEL_PERCENT >= percent) {
										bot.랜덤텔();
									}

								}

								else {
									if (!bot.타격귀환무시) {
										if (bot.사냥봇_위치.startsWith("오만")) {
											bot.딜레이(60000 + _random.nextInt(60000));
										}

										bot.귀환();
									}
								}
							}
						} else if (attacker instanceof L1MonsterInstance) {
							if (attacker.getMaxHp() >= 6000)
								bot.귀환();
						}
					}
					delInvis();
					
					if (attacker instanceof L1PcInstance && this instanceof L1PcInstance) {
					    long now = System.currentTimeMillis();
					    String locName = MapsTable.getInstance().locationname(getMapId());
					    if (now - lastFightMsgTime > Config.전투메시지딜레이 * 1000) {
					    	L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, 
									"[" + getName() + " VS " + attacker.getName() + "]" + " \\fT" + locName + "에서 전투중."));
							L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(
									"[" + getName() + " VS " + attacker.getName() + "]" +  " \\fT" + locName + "에서 전투중."));
					        lastFightMsgTime = now;
					    }
					}
					if (attacker instanceof L1PcInstance) {
						L1PinkName.onAction(this, attacker);
					}
					
					if (attacker instanceof L1PcInstance && ((L1PcInstance) attacker).isPinkName()) {
						for (L1Object object : L1World.getInstance().getVisibleObjects(attacker)) {
							if (object instanceof L1GuardInstance) {
								L1GuardInstance guard = (L1GuardInstance) object;
								guard.setTarget(((L1PcInstance) attacker));
							}
						}
					}
					if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)) {
						getSkillEffectTimerSet().removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
					}
				}
				if (getInventory().checkEquipped(145) || getInventory().checkEquipped(149)) {
					damage *= 1.5;
				}

				int newHp = getCurrentHp() - (int) (damage);

				if (newHp > getMaxHp()) {
					newHp = getMaxHp();
				}
				if (newHp <= 0) {
					if (isGm()) {
						this.setCurrentHp(getMaxHp());
					} else {
						if (isDeathMatch()) {
							if (getMapId() == 5153) {
								try {
									this.setCurrentHp(getMaxHp());
									save();
									beginGhost(getX(), getY(), (short) getMapId(), true);
									sendPackets(new S_ServerMessage(1271));
								} catch (Exception e) {
									_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
								}
								return;
							}
						} else {
							death(attacker);
							if (attacker instanceof L1PcInstance) {
								L1PcInstance atk = (L1PcInstance) attacker;
								if (CharPosUtil.getZoneType(L1PcInstance.this) == 0) {
									if (getLevel() >= Config.killdeath) {
										attacker.setKills(attacker.getKills() + 1);
										setDeaths(getDeaths() + 1);
									}
								}
								for (L1PcInstance listner : L1World.getInstance().getAllPlayers()) {
									listner.sendPackets(new S_ChatPacket(atk, getName(), Opcodes.S_OPCODE_MSG, 99));
								}
							}
						}
					}
				}
				if (newHp > 0) {
					this.setCurrentHp(newHp);
				}
			} else if (!isDead()) {
				System.out.println("[L1PcInstance] 경고：플레이어의 HP감소 처리가 올바르게 행해지지 않은 개소가 있습니다.※혹은 최초부터 HP0");
				death(attacker);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void death(L1Character lastAttacker) {
		synchronized (this) {
			if (isDead()) {
				return;
			}
			
			L1PcInstance player = null;
			if (lastAttacker instanceof L1PcInstance) {
				player = (L1PcInstance) lastAttacker;
			}
			
			LinAllManagerInfoThread.PvPCount += 1;
			if (player != null) {
				if (lastAttacker instanceof L1PcInstance) {
					String locName = MapsTable.getInstance().locationname(getMapId());
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3[ 승리 : " + player.getName() + " \\f3] [ 패배 : " + getName()+"\\f3] [사망위치 : " + locName + "]"));
					L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\f3[ 승리 : " + player.getName() + " \\f3] [ 패배 : " + getName()+"\\f3] [사망위치 : " + locName + "]"));
				}
				if (player.getMapId() != 5153) {
					if (L1PcInstance.this.getHuntPrice() != 0) {
						int wantedAdena = (int) (getHuntPrice() * Config.WANTED_ADENA_CHARGE);
						player.getInventory().storeItem(40308, wantedAdena);
						L1PcInstance.this.setWanted(0);
						L1PcInstance.this.addDmgup(-3);
						L1PcInstance.this.addBowDmgup(-3);
						L1PcInstance.this.getAbility().addSp(-3);
						L1PcInstance.this.sendPackets((ServerBasePacket) new S_SPMR(L1PcInstance.this));
						player.sendPackets((ServerBasePacket) new S_SystemMessage(
								"\\fY수배범[" + L1PcInstance.this.getName() + "]의 현상금" + wantedAdena + "아데나를 획득 합니다."));
					}
				}
				
				if (getLawful() >= 0 && isPinkName() == false) {
					boolean isChangePkCount = false;
					if (player.getLawful() < 30000) {
						player.set_PKcount(player.get_PKcount() + 1);
						isChangePkCount = true;
						player.setLastPk();
					}

					int lawful;

					if (player.getLevel() < 50) {
						lawful = -1 * (int) ((Math.pow(player.getLevel(), 2) * 4));
					} else {
						lawful = -1 * (int) ((Math.pow(player.getLevel(), 3) * 0.08));
					}
					if ((player.getLawful() - 1000) < lawful) {
						lawful = player.getLawful() - 1000;
					}

					if (lawful <= -32768) {
						lawful = -32768;
					}
					player.setLawful(lawful);
					S_Lawful s_lawful = new S_Lawful(player.getId(), player.getLawful());
					player.sendPackets(s_lawful);
					Broadcaster.broadcastPacket(player, s_lawful);
				} else {
					if (isPinkName()) {
						getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_PINK_NAME);
						setPinkName(false);
					}
				}
			}
			setDead(true);
			setActionStatus(ActionCodes.ACTION_Die);
		}
		GeneralThreadPool.getInstance().execute(new Death(lastAttacker));

	}

	private class Death implements Runnable {
		L1Character _lastAttacker;

		Death(L1Character cha) {
			_lastAttacker = cha;
		}

		public void run() {
			L1Character lastAttacker = _lastAttacker;
			_lastAttacker = null;
			setCurrentHp(0);
			setGresValid(false);

			while (isTeleport()) {
				try {
					Thread.sleep(300);
				} catch (Exception e) {
				}
			}

			int targetobjid = getId();
			getMap().setPassable(getLocation(), true);

			int tempchargfx = 0;
			if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
				tempchargfx = getGfxId().getTempCharGfx();
				setTempCharGfxAtDead(tempchargfx);
				setCurrentSprite(tempchargfx);
			} else {
				setTempCharGfxAtDead(getClassId());
				setCurrentSprite(getClassId());
			}

			L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(L1PcInstance.this, L1SkillId.CANCELLATION, getId(), getX(), getY(), null, 0,
					L1SkillUse.TYPE_LOGIN);

			if (tempchargfx == 5727 || tempchargfx == 5730 || tempchargfx == 5733 || tempchargfx == 5736) {
				tempchargfx = 0;
			}
			if (tempchargfx != 0) {
				sendPackets(new S_ChangeShape(getId(), tempchargfx));
				Broadcaster.broadcastPacket(L1PcInstance.this, new S_ChangeShape(getId(), tempchargfx));
			} else {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}

			sendPackets(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
			Broadcaster.broadcastPacket(L1PcInstance.this, new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
			if (lastAttacker != L1PcInstance.this) {
				if (CharPosUtil.getZoneType(L1PcInstance.this) != 0) {
					L1PcInstance player = null;
					if (lastAttacker instanceof L1PcInstance) {
						player = (L1PcInstance) lastAttacker;
					} else if (lastAttacker instanceof L1PetInstance) {
						player = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
					} else if (lastAttacker instanceof L1SummonInstance) {
						player = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
					}
					if (player != null) {
						if (!isInWarAreaAndWarTime(L1PcInstance.this, player)) {
							return;
						}
					}
				}

				boolean sim_ret = simWarResult(lastAttacker);
				if (sim_ret == true) {
					return;
				}
			}

			if (!getMap().isEnabledDeathPenalty()) {
				return;
			}
			
			L1PcInstance fightPc = null;
			if (lastAttacker instanceof L1PcInstance) {
				fightPc = (L1PcInstance) lastAttacker;
			}
			if (fightPc != null) {
				if (getFightId() == fightPc.getId() && fightPc.getFightId() == getId()) {
					setFightId(0);
					sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
					fightPc.setFightId(0);
					fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
					return;
				}
			}

			if (getInventory().checkItem(46173)) { // 가호처리
				graciaGahoSystem(1);
				return;
			} else {
				if (!getAutoHunt()) {
					deathPenalty();
				} else {
					if (!(lastAttacker instanceof L1PcInstance)) {
						deathPenalty();
					}
				}
			}
			setGresValid(true);
			
			if (getExpRes() == 0) {
				setExpRes(1);
			}
			
			if (lastAttacker instanceof L1GuardInstance) {
				if (get_PKcount() > 0) {
					set_PKcount(get_PKcount() - 1);
				}
				setLastPk(null);
			}

			Random random = new Random();
			int count = 1;
			int skillcount = 1;
			int drop = random.nextInt(100) + 1;
			if (!getAutoHunt()) {
				if (getLawful() < 0) {
					count = random.nextInt(3) + 1;
					skillcount = random.nextInt(3) + 1;
					caoPenaltyResult(count);
					caoPenaltySkill(skillcount);
				} else {
					if (drop < 50) {
						caoPenaltyResult(count);
						caoPenaltySkill(skillcount);	
					}
				}
			} else {
				if (!(lastAttacker instanceof L1PcInstance)) {
					if (getLawful() < 0) {
						count = random.nextInt(3) + 1;
						skillcount = random.nextInt(3) + 1;
						caoPenaltyResult(count);
						caoPenaltySkill(skillcount);
					} else {
						if (drop < 50) {
							caoPenaltyResult(count);
							caoPenaltySkill(skillcount);	
						}
					}
				}
			}

			boolean castle_ret = castleWarResult();
			if (castle_ret == true) {
				return;
			}
		}
	}

	private void graciaGahoSystem(int count) {
		for (int i = 0; i < count; i++) {
			L1ItemInstance item = getInventory().getItemByItemId(46173);

			if (item != null) {
				if (item.isStackable()) { // 봉인이 끝난 아이템
					getInventory().removeItem(item);
				} else {
					getInventory().removeItem(item);
					spawnGroundItem(46174, 1);
				}
				sendPackets(new S_ServerMessage(638, item.getLogName()));
				// %0를 잃었습니다
			}
		}
	}

	private void spawnGroundItem(int itemId, int count) {
		L1Item temp = ItemTable.getInstance().getTemplate(itemId);

		if (temp == null) {
			return;
		}
		for (int i = 0; i < count; i++) {
			if (temp.isStackable()) {
				L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
				item.setEnchantLevel(0);
				item.setCount(1);
				L1GroundInventory ground = L1World.getInstance().getInventory(getX(), getY(), getMapId());
				if (ground.checkAddItem(item, 1) == L1Inventory.OK) {
					ground.storeItem(item);
				}
			} else {
				L1ItemInstance item = null;
				for (int createCount = 0; createCount < 1; createCount++) {
					item = ItemTable.getInstance().createItem(itemId);
					item.setEnchantLevel(0);
					L1GroundInventory ground = L1World.getInstance().getInventory(getX(), getY(), getMapId());
					if (ground.checkAddItem(item, 1) == L1Inventory.OK) {
						ground.storeItem(item);
					}
				}
			}
		}
	}

	private boolean _isDragonDia;

	public boolean isDragonDia() {
		return _isDragonDia;
	}

	public void setDragonDia(boolean dia) {
		_isDragonDia = dia;
	}

	private boolean _isautobuy = false;

	public boolean isAutoBuy() {
		return _isautobuy;
	}

	public void setAutoBuy(boolean dia) {
		_isautobuy = dia;
	}
	private void caoPenaltyResult(int count) {
		for (int i = 0; i < count; i++) {
			L1ItemInstance item = getInventory().CaoPenalty();
			LinAllManagerInfoThread.PenaltyCount += 1;
			if (item != null) {
				if (item.getBless() > 3) {
					getInventory().removeItem(item, item.isStackable() ? item.getCount() : 1);
					LinAllManager.getInstance().PenaltyAppend(item.getLogName(), getName(), count, 1);
					/** 파일로그저장 **/
                    LoggerInstance.getInstance().addItemAction(ItemActionType.del, this, item, count);
				} else {
					getInventory().tradeItem(item, item.isStackable() ? item.getCount() : 1,
							L1World.getInstance().getInventory(getX(), getY(), getMapId()));
					sendPackets(new S_ServerMessage(638, item.getLogName()));
					LinAllManager.getInstance().PenaltyAppend(item.getLogName(), getName(), count, 0);
					/** 파일로그저장 **/
                    LoggerInstance.getInstance().addItemAction(ItemActionType.del, this, item, count);
				}
			} else {
			}
		}
	}

	private void caoPenaltySkill(int count) {
		int l = 0;
		int lv1 = 0;
		int lv2 = 0;
		int lv3 = 0;
		int lv4 = 0;
		int lv5 = 0;
		int lv6 = 0;
		int lv7 = 0;
		int lv8 = 0;
		int lv9 = 0;
		int lv10 = 0;
		Random random = new Random();
		int lostskilll = 0;
		for (int i = 0; i < count; i++) {
			if (isCrown()) {
				lostskilll = random.nextInt(16) + 1;
			} else if (isKnight()) {
				lostskilll = random.nextInt(8) + 1;
			} else if (isElf()) {
				lostskilll = random.nextInt(48) + 1;
			} else if (isDarkelf()) {
				lostskilll = random.nextInt(23) + 1;
			} else if (isWizard()) {
				lostskilll = random.nextInt(80) + 1;
			}

			if (!SkillsTable.getInstance().spellCheck(getId(), lostskilll)) {
				return;
			}

			L1Skills l1skills = null;
			l1skills = SkillsTable.getInstance().getTemplate(lostskilll);
			if (l1skills.getSkillLevel() == 1) {
				lv1 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 2) {
				lv2 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 3) {
				lv3 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 4) {
				lv4 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 5) {
				lv5 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 6) {
				lv6 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 7) {
				lv7 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 8) {
				lv8 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 9) {
				lv9 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 10) {
				lv10 |= l1skills.getId();
			}

			SkillsTable.getInstance().spellLost(getId(), lostskilll);
			l = lv1 + lv2 + lv3 + lv4 + lv5 + lv6 + lv7 + lv8 + lv9 + lv10;
		}
		if (l > 0) {
			sendPackets(new S_DelSkill(lv1, lv2, lv3, lv4, lv5, lv6, lv7, lv8, lv9, lv10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0));
		}
	}

	public boolean castleWarResult() {
		if (getClanid() != 0 && isCrown()) {
			L1Clan clan = L1World.getInstance().getClan(getClanname());
			for (L1War war : L1World.getInstance().getWarList()) {
				int warType = war.GetWarType();
				boolean isInWar = war.CheckClanInWar(getClanname());
				boolean isAttackClan = war.CheckAttackClan(getClanname());
				if (getId() == clan.getLeaderId() && warType == 1 && isInWar && isAttackClan) {
					String enemyClanName = war.GetEnemyClanName(getClanname());
					if (enemyClanName != null) {
						war.CeaseWar(getClanname(), enemyClanName);
					}
					break;
				}
			}
		}

		int castleId = 0;
		boolean isNowWar = false;
		castleId = L1CastleLocation.getCastleIdByArea(this);
		if (castleId != 0) {
			isNowWar = WarTimeController.getInstance().isNowWar(castleId);
		}
		return isNowWar;
	}

	public boolean simWarResult(L1Character lastAttacker) {
		if (getClanid() == 0) {
			return false;
		}
		if (Config.SIM_WAR_PENALTY) {
			return false;
		}
		L1PcInstance attacker = null;
		String enemyClanName = null;
		boolean sameWar = false;

		if (lastAttacker instanceof L1PcInstance) {
			attacker = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			attacker = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			attacker = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
		} else {
			return false;
		}
		L1Clan clan = null;
		for (L1War war : L1World.getInstance().getWarList()) {
			clan = L1World.getInstance().getClan(getClanname());

			int warType = war.GetWarType();
			boolean isInWar = war.CheckClanInWar(getClanname());
			if (attacker != null && attacker.getClanid() != 0) {
				sameWar = war.CheckClanInSameWar(getClanname(), attacker.getClanname());
			}

			if (getId() == clan.getLeaderId() && warType == 2 && isInWar == true) {
				enemyClanName = war.GetEnemyClanName(getClanname());
				if (enemyClanName != null) {
					war.CeaseWar(getClanname(), enemyClanName);
				}
			}

			if (warType == 2 && sameWar) {
				return true;
			}
		}
		return false;
	}

	public void resExp() {
		int oldLevel = getLevel();
		int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		double ratio;

		if (oldLevel < 45)
			ratio = 0.05;
		else if (oldLevel >= 49)
			ratio = 0.025;
		else
			ratio = 0.05 - (oldLevel - 44) * 0.005;

		exp = (int) (needExp * ratio);

		if (exp == 0)
			return;

		addExp(exp);
	}

	public void resExpToTemple() {
		int oldLevel = getLevel();
		int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		double ratio;

		if (oldLevel < 45)
			ratio = 0.05;
		else if (oldLevel >= 45 && oldLevel < 49)
			ratio = 0.05 - (oldLevel - 44) * 0.005;
		else if (oldLevel >= 49 && oldLevel < 52)
			ratio = 0.025;
		else if (oldLevel == 52)
			ratio = 0.026;
		else if (oldLevel > 52 && oldLevel < 74)
			ratio = 0.026 + (oldLevel - 52) * 0.001;
		else if (oldLevel >= 74 && oldLevel < 79)
			ratio = 0.048 - (oldLevel - 73) * 0.0005;
		else
			/* if (oldLevel >= 79) */ ratio = 0.0499; // 79렙부터 4.9%복구

		exp = (int) (needExp * ratio);
		if (exp == 0)
			return;

		addExp(exp);
	}

	public void deathPenalty() {
		int oldLevel = getLevel();
		int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;

		if (oldLevel >= 1 && oldLevel < 49)
			exp = 0;
		// else if (oldLevel >= 11 && oldLevel < 45) exp = (int) (needExp *
		// 0.1);
		// else if (oldLevel == 45) exp = (int) (needExp * 0.09);
		// else if (oldLevel == 46) exp = (int) (needExp * 0.08);
		// else if (oldLevel == 47) exp = (int) (needExp * 0.07);
		// else if (oldLevel == 48) exp = (int) (needExp * 0.06);
		else if (oldLevel >= 50)
			exp = (int) (needExp * 0.10);

		if (exp == 0)
			return;

		addExp(-exp);
	}

	public int getEr() {
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE)) {
			return 0;
		}

		int er = 0;
		if (isKnight()) {
			er = getLevel() / 4;
		} else if (isCrown() || isElf()) {
			er = getLevel() / 8;
		} else if (isDarkelf()) {
			er = getLevel() / 6;
		} else if (isWizard()) {
			er = getLevel() / 10;
		}

		er += (getAbility().getTotalDex() - 8) / 2;

		int BaseEr = CalcStat.calcBaseEr(getType(), getAbility().getBaseDex());

		er += BaseEr;

		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRESS_EVASION)) {
			er += 12;
		}
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SOLID_CARRIAGE)) {
			er += 15;
		}

		return er;
	}

	public L1BookMark getBookMark(String name) {
		L1BookMark element = null;
		for (int i = 0; i < _bookmarks.size(); i++) {
			element = _bookmarks.get(i);
			if (element.getName().equalsIgnoreCase(name)) {
				return element;
			}
		}
		return null;
	}

	public L1BookMark getBookMark(int id) {
		L1BookMark element = null;
		for (int i = 0; i < _bookmarks.size(); i++) {
			element = _bookmarks.get(i);
			if (element.getId() == id) {
				return element;
			}
		}
		return null;
	}

	public int getBookMarkSize() {
		return _bookmarks.size();
	}

	public void addBookMark(L1BookMark book) {
		_bookmarks.add(book);
	}

	public void removeBookMark(L1BookMark book) {
		_bookmarks.remove(book);
	}

	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	public void setWeapon(L1ItemInstance weapon) {
		_weapon = weapon;
	}

	public L1ItemInstance getArmor() {
		return _armor;
	}

	public void setArmor(L1ItemInstance armor) {
		_armor = armor;
	}

	public L1Quest getQuest() {
		return _quest;
	}

	public boolean isCrown() {
		return (getClassId() == CLASSID_PRINCE || getClassId() == CLASSID_PRINCESS);
	}

	public boolean isKnight() {
		return (getClassId() == CLASSID_KNIGHT_MALE || getClassId() == CLASSID_KNIGHT_FEMALE);
	}

	public boolean isElf() {
		return (getClassId() == CLASSID_ELF_MALE || getClassId() == CLASSID_ELF_FEMALE);
	}

	public boolean isWizard() {
		return (getClassId() == CLASSID_WIZARD_MALE || getClassId() == CLASSID_WIZARD_FEMALE);
	}

	public boolean isDarkelf() {
		return (getClassId() == CLASSID_DARKELF_MALE || getClassId() == CLASSID_DARKELF_FEMALE);
	}

	public String getAccountName() {
		return _accountName;
	}

	public void setAccountName(String s) {
		_accountName = s;
	}

	public boolean isAddrewardMon() {
		return _isrewardMon;
	}

	public void setAddrewardMon(boolean flag) {
		_isrewardMon = flag;
	}

	public short getBaseMaxHp() {
		return _baseMaxHp;
	}

	public void addBaseMaxHp(short i) {
		i += _baseMaxHp;
		if (i >= 32767) {
			i = 32767;
		} else if (i < 1) {
			i = 1;
		}
		addMaxHp(i - _baseMaxHp);
		_baseMaxHp = i;
	}

	public short getBaseMaxMp() {
		return _baseMaxMp;
	}

	public void addBaseMaxMp(short i) {
		i += _baseMaxMp;
		if (i >= 32767) {
			i = 32767;
		} else if (i < 0) {
			i = 0;
		}
		addMaxMp(i - _baseMaxMp);
		_baseMaxMp = i;
	}

	public int getBaseAc() {
		return _baseAc;
	}

	public int getBaseDmgup() {
		return _baseDmgup;
	}

	public int getBaseBowDmgup() {
		return _baseBowDmgup;
	}

	public int getBaseHitup() {
		return _baseHitup;
	}

	public int getBaseBowHitup() {
		return _baseBowHitup;
	}

	public void setBaseMagicHitUp(int i) {
		_baseMagicHitup = i;
	}

	public int getBaseMagicHitUp() {
		return _baseMagicHitup;
	}

	public void setBaseMagicCritical(int i) {
		_baseMagicCritical = i;
	}

	public int getBaseMagicCritical() {
		return _baseMagicCritical;
	}

	public void setBaseMagicDmg(int i) {
		_baseMagicDmg = i;
	}

	public int getBaseMagicDmg() {
		return _baseMagicDmg;
	}

	public void setBaseMagicDecreaseMp(int i) {
		_baseMagicDecreaseMp = i;
	}

	public int getBaseMagicDecreaseMp() {
		return _baseMagicDecreaseMp;
	}

	public int getAdvenHp() {
		return _advenHp;
	}

	public void setAdvenHp(int i) {
		_advenHp = i;
	}

	public int getAdvenMp() {
		return _advenMp;
	}

	public void setAdvenMp(int i) {
		_advenMp = i;
	}

	public int getHighLevel() {
		return _highLevel;
	}

	public void setHighLevel(int i) {
		_highLevel = i;
	}

	public int getElfAttr() {
		return _elfAttr;
	}

	public void setElfAttr(int i) {
		_elfAttr = i;
	}

	public int getExpRes() {
		return _expRes;
	}

	public void setExpRes(int i) {
		_expRes = i;
	}

	public int getPartnerId() {
		return _partnerId;
	}

	public void setPartnerId(int i) {
		_partnerId = i;
	}

	public int getOnlineStatus() {
		return _onlineStatus;
	}

	public void setOnlineStatus(int i) {
		_onlineStatus = i;
	}

	public int get로테시작() {
		return _로테이션;
	}

	public void set로테시작(int i) {
		_로테이션 = i;
	}

	public int getHomeTownId() {
		return _homeTownId;
	}

	public void setHomeTownId(int i) {
		_homeTownId = i;
	}

	public int getContribution() {
		return _contribution;
	}

	public void setContribution(int i) {
		_contribution = i;
	}

	public int getHellTime() {
		return _hellTime;
	}

	public void setHellTime(int i) {
		_hellTime = i;
	}

	public boolean isBanned() {
		return _banned;
	}

	public void setBanned(boolean flag) {
		_banned = flag;
	}

	public int get_food() {
		return _food;
	}

	public void set_food(int i) {
		_food = i;
	}

	public L1EquipmentSlot getEquipSlot() {
		return _equipSlot;
	}

	public static L1PcInstance load(String charName) {
		L1PcInstance result = null;
		try {
			result = CharacterTable.getInstance().loadCharacter(charName);
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		return result;
	}

	public void save() throws Exception {
		if (isGhost()) {
			return;
		}
		CharacterTable.getInstance().storeCharacter(this);
	}

	public void saveInventory() {
		for (L1ItemInstance item : getInventory().getItems()) {
			if (item != null)
				getInventory().saveItem(item, item.getRecordingColumns() != 0 ? L1PcInventory.COL_SAVE_ALL : 0);
		}
	}

	public void setRegenState(int state) {
		setHpRegenState(state);
		setMpRegenState(state);
	}

	public void setHpRegenState(int state) {
		if (_HpcurPoint < state)
			return;

		this._HpcurPoint = state;
		// _mpRegen.setState(state);
		// _hpRegen.setState(state);
	}

	public void setMpRegenState(int state) {
		if (_MpcurPoint < state)
			return;

		this._MpcurPoint = state;
		// _mpRegen.setState(state);
		// _hpRegen.setState(state);
	}

	public double getMaxWeight() {
		int str = getAbility().getTotalStr();
		int con = getAbility().getTotalCon();
		int basestr = ability.getBaseStr();
		int basecon = ability.getBaseCon();
		int maxWeight = CalcStat.getMaxWeight(str, con);
		double plusWeight = 0;
		// 베이스스탯에 의한 수치(최대 무게수지 + 1 당 0.04)
		double baseWeight = CalcStat.calcBaseWeight(getType(), basestr, basecon);
		// 방어구에 의한 수치
		double armorWeight = getWeightReduction();
		// 인형에 의한 수치
		int dollWeight = 0;
		for (L1DollInstance doll : getDollList().values()) {
			dollWeight = doll.getWeightReductionByDoll();
		}
		// 마법에 의한 수치
		int magicWeight = 0;
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECREASE_WEIGHT)) {
			magicWeight = 3;
		}
		baseWeight = Math.ceil(maxWeight * (1 + (baseWeight * 0.04))) - maxWeight;
		plusWeight = Math.ceil(maxWeight * (1 + ((armorWeight + magicWeight + dollWeight) * 0.02))) - maxWeight;
		maxWeight += plusWeight + baseWeight;
		maxWeight *= Config.RATE_WEIGHT_LIMIT;

		return maxWeight;
	}

	public boolean isUgdraFruit() {
		return getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_FRUIT);
	}

	public boolean isFastMovable() {
		return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.HOLY_WALK)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MOVING_ACCELERATION)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.WIND_WALK));
	}

	public boolean isBrave() {
		return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BRAVE));
	}

	public boolean isElfBrave() {
		return getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_ELFBRAVE);
	}

	public boolean isDragonPearl() {
		return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGONPERL));
	}

	public boolean isThirdSpeed() {
		return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGONPERL));
	}

	public boolean isHaste() {
		return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HASTE)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.HASTE)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GREATER_HASTE)
				|| getMoveState().getMoveSpeed() == 1);
	}

	public boolean isInvisDelay() {
		return (invisDelayCounter > 0);
	}

	public void addInvisDelayCounter(int counter) {
		synchronized (_invisTimerMonitor) {
			invisDelayCounter += counter;
		}
	}

	public void beginInvisTimer() {
		final long DELAY_INVIS = 1000L;
		addInvisDelayCounter(1);
		GeneralThreadPool.getInstance().pcSchedule(new L1PcInvisDelay(getId()), DELAY_INVIS);
	}

	public synchronized void addExp(int exp) {
		_exp += exp;
		if (_exp > ExpTable.MAX_EXP) {
			_exp = ExpTable.MAX_EXP;
		}
	}

	public synchronized void addContribution(int contribution) {
		_contribution += contribution;
	}

	public void beginExpMonitor() {
		final long INTERVAL_EXP_MONITOR = 500;
		_expMonitorFuture = GeneralThreadPool.getInstance().pcScheduleAtFixedRate(new L1PcExpMonitor(getId()), 0L,
				INTERVAL_EXP_MONITOR);
	}

	private void levelUp(int gap) {
		resetLevel();
		
		String BloodName = getClanname();
		if (getLevel() > Config.중립혈레벨제한 && BloodName.equalsIgnoreCase("중립")) {
			try {
				L1Clan clan = L1World.getInstance().findClan("중립");
				L1PcInstance clanMember[] = clan.getOnlineClanMember();
				String player_name = getName();
				String clan_name = getClanname();
				for (int i = 0; i < clanMember.length; i++) {
					clanMember[i].sendPackets(new S_ServerMessage(ServerMessage.LEAVE_CLAN, player_name, clan_name));
				}
				ClearPlayerClanData(clan);
				clan.removeClanMember(player_name);
				L1Teleport.teleport(this, this.getX(), this.getY(), this.getMapId(), this.getHeading(), false);
				save();
				saveInventory();
			} catch (Exception e) {
			}
		}
		
		if (getLevel() == 99 && Config.ALT_REVIVAL_POTION) {
			try {
				L1Item l1item = ItemTable.getInstance().getTemplate(43000);
				if (l1item != null) {
					getInventory().storeItem(43000, 1);
					sendPackets(new S_ServerMessage(403, l1item.getName()));
				} else {
					sendPackets(new S_SystemMessage("환생의 물약 입수에 실패했습니다."));
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				sendPackets(new S_SystemMessage("환생의 물약 입수에 실패했습니다."));
			}
		}

		for (int i = 0; i < gap; i++) {
			short randomHp = CalcStat.calcStatHp(getType(), getBaseMaxHp(), getAbility().getCon());
			short randomMp = CalcStat.calcStatMp(getType(), getBaseMaxMp(), getAbility().getWis());
			addBaseMaxHp(randomHp);
			addBaseMaxMp(randomMp);
		}

		this.setCurrentHp(getBaseMaxHp());
		this.setCurrentMp(getBaseMaxMp());
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();
		if (getLevel() > getHighLevel() && getReturnStat() == 0) {
			setHighLevel(getLevel());
		}

		try {
			save();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

		if (getLevel() >= Config.LIMITLEVEL) {
			sendPackets(new S_SystemMessage("서버 제한 레벨에 도달하여 이후 사냥하셔도 경험치를 획득이 불가능 합니다."));
		}
		if (getLevel() >= 51 && getLevel() - 50 > getAbility().getBonusAbility() && getAbility().getAmount() < 210) {
			sendPackets(new S_bonusstats(getId(), 1));
		}
		if (getLevel() >= 13) {
			if (getMapId() == 69) {
				L1Teleport.teleport(this, 33082, 33389, (short) 4, 5, true);
			} else if (getMapId() == 68) {
				L1Teleport.teleport(this, 32574, 32941, (short) 0, 5, true);
			}
		}
		if (getLevel() >= 52) { // 지정 레벨
			if (getMapId() == 777) { // 버림받은 사람들의 땅(그림자의 신전)
				L1Teleport.teleport(this, 34043, 32184, (short) 4, 5, true); // 상아의
																				// 탑전
			} else if (getMapId() == 778 || getMapId() == 779) { // 버림받은 사람들의
																	// 땅(욕망의 동굴)
				L1Teleport.teleport(this, 32608, 33178, (short) 4, 5, true); // WB
			}
		}
		if (getLevel() >= Config.Newhunter[3] || isGm()) { // 초보자 사냥터 맵, 레벨제한
			if (getMapId() == Config.Newhunter[2]) {
				L1Teleport.teleport(this, 33440, 32799, (short) 4, 5, true);
				sendPackets(new S_SystemMessage("초보자 사냥터 레벨 제한 " + Config.Newhunter[3] + " 레벨에 도달하셨습니다."));
			}
		}
		if (getLevel() >= 49 && getAinHasad() == 0) {
			setAinHasad(2500000);
			sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, getAinHasad()));
		}

		CheckStatus();
		sendPackets(new S_OwnCharStatus(this));
		sendPackets(new S_SPMR(this));
	}

	private void levelDown(int gap) {
		resetLevel();

		for (int i = 0; i > gap; i--) {
			short randomHp = CalcStat.calcStatHp(getType(), 0, getAbility().getCon());
			short randomMp = CalcStat.calcStatMp(getType(), 0, getAbility().getWis());
			addBaseMaxHp((short) -randomHp);
			addBaseMaxMp((short) -randomMp);
		}
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();
		if (Config.LEVEL_DOWN_RANGE != 0) {
			if (getHighLevel() - getLevel() >= Config.LEVEL_DOWN_RANGE) {
				sendPackets(new S_ServerMessage(64));
				sendPackets(new S_Disconnect());
				_log.info(String.format("레벨 다운의 허용 범위를 넘었기 때문에 %s를 강제 절단 했습니다.", getName()));
			}
		}
		try {
			save();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		sendPackets(new S_OwnCharStatus(this));
	}

	public void beginGameTimeCarrier() {
		new GameTimeCarrier(this).start();
	}

	public boolean isGhost() {
		return _ghost;
	}

	private void setGhost(boolean flag) {
		_ghost = flag;
	}

	public boolean isReserveGhost() {
		return _isReserveGhost;
	}

	private void setReserveGhost(boolean flag) {
		_isReserveGhost = flag;
	}

	public void beginGhost(int locx, int locy, short mapid, boolean canTalk) {
		beginGhost(locx, locy, mapid, canTalk, 0);
	}

	public void beginGhost(int locx, int locy, short mapid, boolean canTalk, int sec) {
		if (isGhost()) {
			return;
		}
		setGhost(true);
		_ghostSaveLocX = getX();
		_ghostSaveLocY = getY();
		_ghostSaveMapId = getMapId();
		_ghostSaveHeading = getMoveState().getHeading();
		L1Teleport.teleport(this, locx, locy, mapid, 5, true);
		if (sec > 0) {
			_ghostFuture = GeneralThreadPool.getInstance().pcSchedule(new L1PcGhostMonitor(getId()), sec * 1000);
		}
	}

	public void makeReadyEndGhost() {
		setReserveGhost(true);
		L1Teleport.teleport(this, _ghostSaveLocX, _ghostSaveLocY, _ghostSaveMapId, _ghostSaveHeading, true);
	}

	public void DeathMatchEndGhost() {
		endGhost();
		L1Teleport.teleport(this, 32614, 32735, (short) 4, 5, true);
	}

	public void endGhost() {
		setGhost(false);
		setReserveGhost(false);
	}

	public void beginHell(boolean isFirst) {
		if (getMapId() != 666) {
			int locx = 32701;
			int locy = 32777;
			short mapid = 666;
			L1Teleport.teleport(this, locx, locy, mapid, 5, false);
		}

		if (isFirst) {
			if (get_PKcount() <= 10) {
				setHellTime(180);
			} else {
				setHellTime(300 * (get_PKcount() - 100) + 300);
			}
			sendPackets(new S_BlueMessage(552, String.valueOf(get_PKcount()), String.valueOf(getHellTime() / 60)));
		} else {
			sendPackets(new S_BlueMessage(637, String.valueOf(getHellTime())));
		}
		if (_hellFuture == null) {
			_hellFuture = GeneralThreadPool.getInstance().pcScheduleAtFixedRate(new L1PcHellMonitor(getId()), 0L,
					1000L);
		}
	}

	public void endHell() {
		if (_hellFuture != null) {
			_hellFuture.cancel(false);
			_hellFuture = null;
		}
		int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_ORCISH_FOREST);
		L1Teleport.teleport(this, loc[0], loc[1], (short) loc[2], 5, true);
		try {
			save();
		} catch (Exception ignore) {
		}
	}

	@Override
	public void setPoisonEffect(int effectId) {
		sendPackets(new S_Poison(getId(), effectId));
		if (!isGmInvis() && !isGhost() && !isInvisble()) {
			Broadcaster.broadcastPacket(this, new S_Poison(getId(), effectId));
		}
	}

	@Override
	public void healHp(int pt) {
		super.healHp(pt);
		sendPackets(new S_HPUpdate(this));
	}

	@Override
	public int getKarma() {
		return _karma.get();
	}

	@Override
	public void setKarma(int i) {
		_karma.set(i);
	}

	public void addKarma(int i) {
		synchronized (_karma) {
			_karma.add(i);
			sendPackets(new S_PacketBox(this, S_PacketBox.KARMA));
		}
	}

	public int getKarmaLevel() {
		return _karma.getLevel();
	}

	public int getKarmaPercent() {
		return _karma.getPercent();
	}

	public Timestamp getLastPk() {
		return _lastPk;
	}

	public void setLastPk(Timestamp time) {
		_lastPk = time;
	}

	public void setLastPk() {
		_lastPk = new Timestamp(System.currentTimeMillis());
	}

	public boolean isWanted() {
		if (_lastPk == null) {
			return false;
		} else if (System.currentTimeMillis() - _lastPk.getTime() > 24 * 3600 * 1000) {
			setLastPk(null);
			return false;
		}
		return true;
	}

	public Timestamp getDeleteTime() {
		return _deleteTime;
	}

	public void setDeleteTime(Timestamp time) {
		_deleteTime = time;
	}

	public int getWeightReduction() {
		return _weightReduction;
	}

	public void addWeightReduction(int i) {
		_weightReduction += i;
	}

	public int getHasteItemEquipped() {
		return _hasteItemEquipped;
	}

	public void addHasteItemEquipped(int i) {
		_hasteItemEquipped += i;
	}

	public void removeHasteSkillEffect() {
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SLOW))
			getSkillEffectTimerSet().removeSkillEffect(L1SkillId.SLOW);
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MASS_SLOW))
			getSkillEffectTimerSet().removeSkillEffect(L1SkillId.MASS_SLOW);
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ENTANGLE))
			getSkillEffectTimerSet().removeSkillEffect(L1SkillId.ENTANGLE);
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.HASTE))
			getSkillEffectTimerSet().removeSkillEffect(L1SkillId.HASTE);
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GREATER_HASTE))
			getSkillEffectTimerSet().removeSkillEffect(L1SkillId.GREATER_HASTE);
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HASTE))
			getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_HASTE);
	}

	public void resetBaseDmgup() {
		int newBaseDmgup = 0;
		int newBaseBowDmgup = 0;
		int newBaseStatDmgup = CalcStat.calcBaseDmgup(getType(), getAbility().getBaseStr());
		int newBaseStatBowDmgup = CalcStat.calcBaseBowDmgup(getType(), getAbility().getBaseDex());
		if (isKnight() || isDarkelf()) {
			newBaseDmgup = getLevel() / 10;
			newBaseBowDmgup = 0;

		} else if (isElf()) {
			newBaseDmgup = 0;
			newBaseBowDmgup = getLevel() / 10;
		}
		addDmgup((newBaseDmgup + newBaseStatDmgup) - _baseDmgup);
		addBowDmgup((newBaseBowDmgup + newBaseStatBowDmgup) - _baseBowDmgup);
		_baseDmgup = newBaseDmgup + newBaseStatDmgup;
		_baseBowDmgup = newBaseBowDmgup + newBaseStatBowDmgup;
	}

	public void resetBaseHitup() {
		int newBaseHitup = 0;
		int newBaseBowHitup = 0;
		int newBaseStatHitup = CalcStat.calcBaseHitup(getType(), getAbility().getBaseStr());
		int newBaseStatBowHitup = CalcStat.calcBaseBowHitup(getType(), getAbility().getBaseDex());

		if (isCrown()) {
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		} else if (isKnight()) {
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (isElf()) {
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		} else if (isDarkelf()) {
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		}
		addHitup((newBaseHitup + newBaseStatHitup) - _baseHitup);
		addBowHitup((newBaseBowHitup + newBaseStatBowHitup) - _baseBowHitup);
		_baseHitup = newBaseHitup + newBaseStatHitup;
		_baseBowHitup = newBaseBowHitup + newBaseStatBowHitup;
	}

	public void resetBaseAc() {
		int newAc = CalcStat.calcAc(getLevel(), getAbility().getDex());
		int newbaseAc = CalcStat.calcBaseAc(getType(), getAbility().getBaseDex());
		this.ac.addAc((newAc + newbaseAc) - _baseAc);
		_baseAc = newAc + newbaseAc;
	}

	public void resetBaseMr() {
		// int newBaseMr = CalcStat.calcBaseMr(getType(),
		// getAbility().getBaseWis());
		int newMr = 0;

		if (isCrown())
			newMr = 10;
		else if (isElf())
			newMr = 25;
		else if (isWizard())
			newMr = 15;
		else if (isDarkelf())
			newMr = 10;
		newMr += CalcStat.calcStatMr(getAbility().getTotalWis());
		newMr += getLevel() / 2;
		resistance.setBaseMr(newMr);
	}

	public void resetLevel() {
		setLevel(ExpTable.getLevelByExp(_exp));
		updateLevel();
	}

	public void updateLevel() {
		final int lvlTable[] = new int[] { 30, 25, 20, 16, 14, 12, 11, 10, 9, 3, 2 };

		int regenLvl = Math.min(10, getLevel());
		if (30 <= getLevel() && isKnight()) {
			regenLvl = 11;
		}

		synchronized (this) {
			setHpregenMax(lvlTable[regenLvl - 1] * 4);
		}
	}

	public void refresh() {
		CheckChangeExp();
		resetLevel();
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseMr();
		resetBaseAc();
		setBaseMagicDecreaseMp(CalcStat.calcBaseDecreaseMp(getType(), getAbility().getBaseInt()));
		setBaseMagicHitUp(CalcStat.calcBaseMagicHitUp(getType(), getAbility().getBaseInt()));
		setBaseMagicCritical(CalcStat.calcBaseMagicCritical(getType(), getAbility().getBaseInt()));
		setBaseMagicDmg(CalcStat.calcBaseMagicDmg(getType(), getAbility().getBaseInt()));
	}

	public void checkChatInterval() {
		long nowChatTimeInMillis = System.currentTimeMillis();
		if (_chatCount == 0) {
			_chatCount++;
			_oldChatTimeInMillis = nowChatTimeInMillis;
			return;
		}

		long chatInterval = nowChatTimeInMillis - _oldChatTimeInMillis;
		if (chatInterval > 2000) {
			_chatCount = 0;
			_oldChatTimeInMillis = 0;
		} else {
			if (_chatCount >= 3) {
				getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED, 120 * 1000);
				sendPackets(new S_SkillIconGFX(36, 120));
				sendPackets(new S_ServerMessage(153));
				_chatCount = 0;
				_oldChatTimeInMillis = 0;
			}
			_chatCount++;
		}
	}

	// 범위외가 된 인식이 끝난 오브젝트를 제거(버경)
	private void removeOutOfRangeObjects(int distance) {
		try {
			List<L1Object> known = getNearObjects().getKnownObjects();
			for (int i = 0; i < known.size(); i++) {
				if (known.get(i) == null) {
					continue;
				}

				L1Object obj = known.get(i);
				if (!getLocation().isInScreen(obj.getLocation())) { // 범위외가 되는
																	// 거리
					getNearObjects().removeKnownObject(obj);
					sendPackets(new S_RemoveObject(obj));
				}
			}
		} catch (Exception e) {
			System.out.println("removeOutOfRangeObjects 에러 : " + e);
		}
	}

	// 오브젝트 인식 처리(버경)
	public void UpdateObject() {
		try {
			if (this == null)
				return;
			try {
				removeOutOfRangeObjects(17);
			} catch (Exception e) {
				System.out.println("removeOutOfRangeObjects(17) 에러 : " + e);
			}

			// 화면내의 오브젝트 리스트를 작성
			ArrayList<L1Object> visible2 = L1World.getInstance().getVisibleObjects(this);
			L1NpcInstance npc = null;
			for (L1Object visible : visible2) {
				if (this == null) {
					break;
				}
				if (visible == null) {
					continue;
				}
				if (!getNearObjects().knownsObject(visible)) {
					visible.onPerceive(this);
				} else {
					if (visible instanceof L1NpcInstance) {
						npc = (L1NpcInstance) visible;
						if (npc.getHiddenStatus() != 0) {
							npc.approachPlayer(this);
						}
					}

				}
			}
		} catch (Exception e) {
			System.out.println("UpdateObject() 에러 : " + e);
		}
	}

	public void CheckChangeExp() {
		int level = ExpTable.getLevelByExp(getExp());
		int char_level = CharacterTable.getInstance().PcLevelInDB(getId());
		if (char_level == 0) { // 0이라면..에러겟지?
			return; // 그럼 그냥 리턴
		}
		int gap = level - char_level;
		if (gap == 0) {
			// sendPackets(new S_OwnCharStatus(this));
			sendPackets(new S_Exp(this));
			return;
		}

		// 레벨이 변화했을 경우
		if (gap > 0) {
			levelUp(gap);
		} else if (gap < 0) {
			levelDown(gap);
		}
	}

	public void CheckStatus() {
		if (!getAbility().isNormalAbility(getClassId(), getLevel(), getHighLevel(), getAbility().getBaseAmount())
				&& !isGm()) {
			SpecialEventHandler.getInstance().ReturnStats(this);
		}
	}

	public void cancelAbsoluteBarrier() { // 아브소르트바리아의 해제
		if (this.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) {
			this.getSkillEffectTimerSet().killSkillEffectTimer(ABSOLUTE_BARRIER);
			this.startHpRegenerationByDoll();
			this.startMpRegenerationByDoll();
		}
	}

	public int get_PKcount() {
		return _PKcount;
	}

	public void set_PKcount(int i) {
		_PKcount = i;
	}

	public int getClanid() {
		return _clanid;
	}

	public void setClanid(int i) {
		_clanid = i;
	}

	public String getClanname() {
		return clanname;
	}

	public void setClanname(String s) {
		clanname = s;
	}

	public L1Clan getClan() {
		return L1World.getInstance().getClan(getClanname());
	}

	public int getClanRank() {
		return _clanRank;
	}

	public void setClanRank(int i) {
		_clanRank = i;
	}

	public byte get_sex() {
		return _sex;
	}

	public void set_sex(int i) {
		_sex = (byte) i;
	}

	public boolean isGm() {
		return _gm;
	}

	public void setGm(boolean flag) {
		_gm = flag;
	}

	public boolean isMonitor() {
		return _monitor;
	}

	public void setMonitor(boolean flag) {
		_monitor = flag;
	}

	/**
	 * 2011.06.21 고정수 마안 딜레이 추가
	 */
	private Timestamp _MaanDelay = null;

	public Timestamp getMaanDelay() {
		return _MaanDelay;
	}

	public void setMaanDelay(Timestamp MaanDelay) {
		_MaanDelay = MaanDelay;
	}

	public int getDamageReductionByArmor() {
		int addReduc = 0;

		CharactersReduc cr = CharactersReducTable.getInstance().getCharactersReduc(getLevel());
		if (cr != null) {
			addReduc = cr.getReduc();
		}

		return _damageReductionByArmor + addReduc;
	}

	public void addDamageReductionByArmor(int i) {
		_damageReductionByArmor += i;
	}

	public int getBowHitupByArmor() {
		return _bowHitupByArmor;
	}

	public void addBowHitupByArmor(int i) {
		_bowHitupByArmor += i;
	}

	public int getBowDmgupByArmor() {
		return _bowDmgupByArmor;
	}

	public void addBowDmgupByArmor(int i) {
		_bowDmgupByArmor += i;
	}

	public int getHitupByArmor() {
		return _HitupByArmor;
	}

	public void addHitupByArmor(int i) {
		_HitupByArmor += i;
	}

	public int getDmgupByArmor() {
		return _DmgupByArmor;
	}

	public void addDmgupByArmor(int i) {
		_DmgupByArmor += i;
	}

	public int getBowHitupByDoll() {
		return _bowHitupBydoll;
	}

	public void addBowHitupByDoll(int i) {
		_bowHitupBydoll += i;
	}

	public int getHitupByDoll() {
		return _HitupBydoll;
	}

	public void addHitupByDoll(int i) {
		_HitupBydoll += i;
	}

	public int getBowDmgupByDoll() {
		return _bowDmgupBydoll;
	}

	public void addBowDmgupByDoll(int i) {
		_bowDmgupBydoll += i;
	}

	// 마법적중
	public int getHitup_magic() {
		return _Hitup_magic;
	}

	public int addHitup_magic(int i) {
		return _Hitup_magic += i;
	}

	// 기술적중
	public int getHitup_skill() {
		return _Hitup_skill;
	}

	public int addHitup_skill(int i) {
		return _Hitup_skill += i;
	}

	// 정령 적중
	public int getHitup_spirit() {
		return _Hitup_spirit;
	}

	public int addHitup_spirit(int i) {
		return _Hitup_spirit += i;
	}

	public int getPVPDamage() { // PVP추가 대미지
		return _PVPDamage;
	}

	public int addPVPDamage(int i) {
		return _PVPDamage += i;
	}

	public int getPVPDamageReduction() { // PVP대미지 감소
		return _PVPDamageReduction;
	}

	public int addPVPDamageReduction(int i) {
		return _PVPDamageReduction += i;
	}

	private int _PVPMagicDamageReduction = 0;

	public int getPVPMagicDamageReduction() { // PVP마법대미지 감소
		return _PVPMagicDamageReduction;
	}

	public int addPVPMagicDamageReduction(int i) {
		return _PVPMagicDamageReduction += i;
	}

	private void setGresValid(boolean valid) {
		_gresValid = valid;
	}

	public boolean isGresValid() {
		return _gresValid;
	}

	public long getFishingTime() {
		return _fishingTime;
	}

	public void setFishingTime(long i) {
		_fishingTime = i;
	}

	public boolean isFishing() {
		return _isFishing;
	}

	public boolean isFishingReady() {
		return _isFishingReady;
	}

	public void setFishing(boolean flag) {
		_isFishing = flag;
	}

	public void setFishingReady(boolean flag) {
		_isFishingReady = flag;
	}

	public int getCookingId() {
		return _cookingId;
	}

	public void setCookingId(int i) {
		_cookingId = i;
	}

	public int getDessertId() {
		return _dessertId;
	}

	public void setDessertId(int i) {
		_dessertId = i;
	}

	public L1ExcludingList getExcludingList() {
		return _excludingList;
	}

	public AcceleratorChecker getAcceleratorChecker() {
		return _acceleratorChecker;
	}

	public int getTeleportX() {
		return _teleportX;
	}

	public void setTeleportX(int i) {
		_teleportX = i;
	}

	public int getTeleportY() {
		return _teleportY;
	}

	public void setTeleportY(int i) {
		_teleportY = i;
	}

	public short getTeleportMapId() {
		return _teleportMapId;
	}

	public void setTeleportMapId(short i) {
		_teleportMapId = i;
	}

	public int getTeleportHeading() {
		return _teleportHeading;
	}

	public void setTeleportHeading(int i) {
		_teleportHeading = i;
	}

	public int getTempCharGfxAtDead() {
		return _tempCharGfxAtDead;
	}

	public void setTempCharGfxAtDead(int i) {
		_tempCharGfxAtDead = i;
	}

	public boolean isCanWhisper() {
		return _isCanWhisper;
	}

	public void setCanWhisper(boolean flag) {
		_isCanWhisper = flag;
	}

	public boolean isShowTradeChat() {
		return _isShowTradeChat;
	}

	public void setShowTradeChat(boolean flag) {
		_isShowTradeChat = flag;
	}

	public boolean isShowWorldChat() {
		return _isShowWorldChat;
	}

	public void setShowWorldChat(boolean flag) {
		_isShowWorldChat = flag;
	}

	public int getFightId() {
		return _fightId;
	}

	public void setFightId(int i) {
		_fightId = i;
	}

	public boolean isPetRacing() {
		return petRacing;
	}

	public void setPetRacing(boolean Petrace) {
		this.petRacing = Petrace;
	}

	public int getPetRacingLAB() {
		return petRacingLAB;
	}

	public void setPetRacingLAB(int lab) {
		this.petRacingLAB = lab;
	}

	public int getPetRacingCheckPoint() {
		return petRacingCheckPoint;
	}

	public void setPetRacingCheckPoint(int p) {
		this.petRacingCheckPoint = p;
	}

	public void setHaunted(boolean i) {
		this.isHaunted = i;
	}

	public boolean isHaunted() {
		return isHaunted;
	}

	public void setDeathMatch(boolean i) {
		this.isDeathMatch = i;
	}

	public boolean isDeathMatch() {
		return isDeathMatch;
	}

	public int getCallClanId() {
		return _callClanId;
	}

	public void setCallClanId(int i) {
		_callClanId = i;
	}

	public int getCallClanHeading() {
		return _callClanHeading;
	}

	public void setCallClanHeading(int i) {
		_callClanHeading = i;
	}

	private boolean _isSummonMonster = false;

	public void setSummonMonster(boolean SummonMonster) {
		_isSummonMonster = SummonMonster;
	}

	public boolean isSummonMonster() {
		return _isSummonMonster;
	}

	private boolean _isShapeChange = false;

	public void setShapeChange(boolean isShapeChange) {
		_isShapeChange = isShapeChange;
	}

	public boolean isShapeChange() {
		return _isShapeChange;
	}

	private boolean _isArchShapeChange = false;

	public void setArchShapeChange(boolean isArchShapeChange) {
		_isArchShapeChange = isArchShapeChange;
	}

	public boolean isArchShapeChange() {
		return _isArchShapeChange;
	}

	private boolean _isArchPolyType = true; // t 1200 f -1

	public void setArchPolyType(boolean isArchPolyType) {
		_isArchPolyType = isArchPolyType;
	}

	public boolean isArchPolyType() {
		return _isArchPolyType;
	}

	public int getUbScore() {
		return _ubscore;
	}

	public void setUbScore(int i) {
		_ubscore = i;
	}

	private int _girandungeon;

	/** 기란던전 입장되어 있던 시간값을 가져 온다. 단위 : 1분 */
	public int getGdungeonTime() {
		return _girandungeon;
	}

	public void setGdungeonTime(int i) {
		_girandungeon = i;
	}

	private int _timeCount = 0;

	public int getTimeCount() {
		return _timeCount;
	}

	public void setTimeCount(int i) {
		_timeCount = i;
	}

	private boolean _isPointUser;

	/** 계정 시간이 남은 Pc 인지 판단한다 */
	public boolean isPointUser() {
		return _isPointUser;
	}

	public void setPointUser(boolean i) {
		_isPointUser = i;
	}

	private long _limitPointTime;

	public long getLimitPointTime() {
		return _limitPointTime;
	}

	public void setLimitPointTime(long i) {
		_limitPointTime = i;
	}

	private int _safecount = 0;

	public int getSafeCount() {
		return _safecount;
	}

	public void setSafeCount(int i) {
		_safecount = i;
	}

	private Timestamp _logoutTime;

	public Timestamp getLogOutTime() {
		return _logoutTime;
	}

	public void setLogOutTime(Timestamp t) {
		_logoutTime = t;
	}

	public void setLogOutTime() {
		_logoutTime = new Timestamp(System.currentTimeMillis());
	}

	public boolean noPlayerCK;

	private long _quiztime = 0;

	public long getQuizTime() {
		return _quiztime;
	}

	public void setQuizTime(long l) {
		_quiztime = l;
	}

	public int _fishingX = 0;
	public int _fishingY = 0;
	public int _fishingmap = 0;
	public boolean GMCommand_Clanmark = false;
	// 나이
	private int _age;

	public int getAge() {
		return _age;
	}

	public void setAge(int i) {
		_age = i;
	}

	private int _vipLevel;

	public int getVipLevel() {
		return _vipLevel;
	}

	public void setVipLevel(int i) {
		_vipLevel = i;
	}

	private boolean eventDmg;

	public boolean isEventDmg() {
		return eventDmg;
	}

	public boolean setEventDmg(boolean i) {
		return eventDmg = i;
	}

	// 고정버프
	private int _memberShip;

	public int getMemberShip() {
		return _memberShip;
	}

	public void setMemberShip(int MemberShip) {
		_memberShip = MemberShip;
	}

	// 콤보시스템
	private int comboCount;

	public int getComboCount() {
		return this.comboCount;
	}

	public void setComboCount(int comboCount) {
		this.comboCount = comboCount;
	}

	private int _ainhasad;

	public int getAinHasad() {
		return _ainhasad;
	}

	public void calAinHasad(int i) {
		int calc = _ainhasad + i;
		if (calc >= 2500000)
			calc = 2500000;
		_ainhasad = calc;
	}

	public void setAinHasad(int i) {
		_ainhasad = i;
	}

	/** 인챈트 버그 예외 처리 */
	private int _enchantitemid = 0;

	public int getLastEnchantItemid() {
		return _enchantitemid;
	}

	public void setLastEnchantItemid(int i, L1ItemInstance item) {
		if (getLastEnchantItemid() == i && i != 0) {
			sendPackets(new S_Disconnect());
			getInventory().removeItem(item, item.getCount());
			return;
		}
		_enchantitemid = i;
	}

	/** 캐릭터에, pet, summon monster, tame monster, created zombie 를 추가한다. */
	public void addPet(L1NpcInstance npc) {
		_petlist.put(npc.getId(), npc);
	}

	/** 캐릭터로부터, pet, summon monster, tame monster, created zombie 를 삭제한다. */
	public void removePet(L1NpcInstance npc) {
		_petlist.remove(npc.getId());
	}

	/** 캐릭터의 애완동물 리스트를 돌려준다. */
	public Map<Integer, L1NpcInstance> getPetList() {
		return _petlist;
	}

	/** 캐릭터에 doll을 추가한다. */
	public void addDoll(L1DollInstance doll) {
		_dolllist.put(doll.getId(), doll);
	}

	/** 캐릭터로부터 dool을 삭제한다. */
	public void removeDoll(L1DollInstance doll) {
		_dolllist.remove(doll.getId());
	}

	/** 캐릭터의 doll 리스트를 돌려준다. */
	public Map<Integer, L1DollInstance> getDollList() {
		return _dolllist;
	}

	public ArrayList<L1DollInstance> getDollList1() {
		ArrayList<L1DollInstance> doll = new ArrayList<L1DollInstance>();
		synchronized (_dolllist) {
			doll.addAll(_dolllist.values());
		}
		return doll;
	}

	/** 캐릭터에 이벤트 NPC(케릭터를 따라다니는)를 추가한다. */
	public void addFollower(L1FollowerInstance follower) {
		_followerlist.put(follower.getId(), follower);
	}

	/** 캐릭터로부터 이벤트 NPC(케릭터를 따라다니는)를 삭제한다. */
	public void removeFollower(L1FollowerInstance follower) {
		_followerlist.remove(follower.getId());
	}

	/** 캐릭터의 이벤트 NPC(케릭터를 따라다니는) 리스트를 돌려준다. */
	public Map<Integer, L1FollowerInstance> getFollowerList() {
		return _followerlist;
	}

	public void ClearPlayerClanData(L1Clan clan) throws Exception {
		ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());
		if (clanWarehouse != null)
			clanWarehouse.unlock(getId());
		setClanid(0);
		setClanname("");
		setTitle("");
		if (this != null) {
			sendPackets(new S_CharTitle(getId(), ""));
			Broadcaster.broadcastPacket(this, new S_CharTitle(getId(), ""));
		}
		setClanRank(0);
		save();
	}

	private int _HpregenMax = 0;

	public int getHpregenMax() {
		return _HpregenMax;
	}

	public void setHpregenMax(int num) {
		this._HpregenMax = num;
	}

	private int _HpregenPoint = 0;

	public int getHpregenPoint() {
		return _HpregenPoint;
	}

	public void setHpregenPoint(int num) {
		this._HpregenPoint = num;
	}

	public void addHpregenPoint(int num) {
		this._HpregenPoint += num;
	}

	private int _HpcurPoint = 4;

	public int getHpcurPoint() {
		return _HpcurPoint;
	}

	public void setHpcurPoint(int num) {
		this._HpcurPoint = num;
	}

	private int _MpregenMax = 0;

	public int getMpregenMax() {
		return _MpregenMax;
	}

	public void setMpregenMax(int num) {
		this._MpregenMax = num;
	}

	private int _MpregenPoint = 0;

	public int getMpregenPoint() {
		return _MpregenPoint;
	}

	public void setMpregenPoint(int num) {
		this._MpregenPoint = num;
	}

	public void addMpregenPoint(int num) {
		this._MpregenPoint += num;
	}

	private int _MpcurPoint = 4;

	public int getMpcurPoint() {
		return _MpcurPoint;
	}

	public void setMpcurPoint(int num) {
		this._MpcurPoint = num;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////// 스피드핵 방지
	///////////////////////////////////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////

	private int hackTimer = -1;
	private int hackCKtime = -1;
	private int hackCKcount = 0;

	public int get_hackTimer() {
		return hackTimer;
	}

	public void increase_hackTimer() {
		if (hackTimer < 0)
			return;
		hackTimer++;
	}

	public void init_hackTimer() {
		hackTimer = 0;
		// 스피드 핵 관련 쓰레드에 추가
	}

	public void calc_hackTimer() {
		if (hackTimer < 0)
			return;
		else if (hackTimer <= 30) {
			_netConnection.close();
			this.logout();
		} else if (hackTimer <= 58) {
			if (hackCKtime < 0 || hackCKtime < hackTimer - 1 || hackCKtime > hackTimer + 1) {
				hackCKtime = hackTimer;
				hackCKcount = 1;
			} else {
				hackCKcount++;
				System.out.println("스핵 의심 사용자 : " + this.getName());
				if (hackCKcount == 3) {
					_netConnection.close();
					this.logout();
				}
			}
		} else {
			hackCKtime = -1;
			hackCKcount = 0;
		}
		hackTimer = 0;
	}

	private int _old_lawful;
	private int _old_exp;

	public boolean isReturnStatus = false;
	public boolean isReturnStatus_START = false;
	public boolean isReturnStatus_LEVELUP = false;

	public boolean isStun() {
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN)) {
			return true;
		}
		return false;
	}

	public boolean isFreeze() {
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_FREEZE)) {
			return true;
		}
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ICE_LANCE)) {
			return true;
		}
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.FREEZING_BLIZZARD)) {
			return true;
		}
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
			return true;
		}
		return false;
	}

	public int getold_lawful() {
		return this._old_lawful;
	}

	public void setold_lawful(int value) {
		this._old_lawful = value;
	}

	public int getold_exp() {
		return this._old_exp;
	}

	public void setold_exp(int value) {
		this._old_exp = value;
	}

	public void bkteleport() {
		int nx = getX();
		int ny = getY();
		int aaa = getMoveState().getHeading();
		switch (aaa) {
		case 1:
			nx += -1;
			ny += 1;
			break;
		case 2:
			nx += -1;
			ny += 0;
			break;
		case 3:
			nx += -1;
			ny += -1;
			break;
		case 4:
			nx += 0;
			ny += -1;
			break;
		case 5:
			nx += 1;
			ny += -1;
			break;
		case 6:
			nx += 1;
			ny += 0;
			break;
		case 7:
			nx += 1;
			ny += 1;
			break;
		case 0:
			nx += 0;
			ny += 1;
			break;
		default:
			break;
		}
		L1Teleport.teleport(this, nx, ny, getMapId(), aaa, false);
	}

	private int _adenaBuyCount;

	public int getAdenaBuyCount() {
		return _adenaBuyCount;
	}

	public void setAdenaBuyCount(int i) {
		_adenaBuyCount = i;
	}

	private int _adenaSellCount;

	public int getAdenaSellCount() {
		return _adenaSellCount;
	}

	public void setAdenaSellCount(int i) {
		_adenaSellCount = i;
	}

	private String _bankName;

	public String getBankName() {
		return _bankName;
	}

	public void setBankName(String i) {
		_bankName = i;
	}

	private String _bankNumber;

	public String getBankNumber() {
		return _bankNumber;
	}

	public void setBankNumber(String i) {
		_bankNumber = i;
	}

	private String _realName;

	public String getRealName() {
		return _realName;
	}

	public void setRealName(String i) {
		_realName = i;
	}

	private int _attrMsgType;

	public int getAttrMsgType() {
		return _attrMsgType;
	}

	public void setAttrMsgType(int i) {
		_attrMsgType = i;
	}

	private boolean _isAdenaTrade = false;

	public boolean isAdenaTrade() {
		return _isAdenaTrade;
	}

	public void setAdenaTrade(boolean flag) {
		_isAdenaTrade = flag;
	}

	// by 악덕
	/** 2011.04.24 SOCOOL 무인 캐릭 여부 */
	public boolean noPlayer = false;

	public boolean isNoPlayer() {
		return noPlayer;
	}

	public void setNoPlayer(boolean noPlayer) {
		this.noPlayer = noPlayer;
	}

	/** 매입상점 여부 */
	private int _shopStep = 0;

	public int getShopStep() {
		return _shopStep;
	}

	public void setShopStep(int _shopStep) {
		this._shopStep = _shopStep;
	}

	private boolean autoshop = false;

	public boolean isAutoshop() {
		return autoshop;
	}

	public void setAutoshop(boolean autoshop) {
		this.autoshop = autoshop;
	}

	/** 매입상점 **/
	/** 혈맹가입군주여부 */
	private boolean autoKing = false;

	public boolean isAutoKing() {
		return autoKing;
	}

	public void setAutoKing(boolean autoKing) {
		this.autoKing = autoKing;
	}

	/** 혈맹가입군주유저호칭 */
	private String autoKingTitle = "";

	public String getAutoKingTitle() {
		return autoKingTitle;
	}

	public void setAutoKingTitle(String autoKingTitle) {
		this.autoKingTitle = autoKingTitle;
	}

	private int _TelType = 0;

	public int getTelType() {
		return _TelType;
	}

	public void setTelType(int i) {
		_TelType = i;
	}

	private boolean _텔대기 = false;

	public boolean 텔대기() {
		return _텔대기;
	}

	public void 텔대기(boolean flag) {
		_텔대기 = flag;
	}

	public boolean is오만텔() {
		int _오만1층 = 101;
		int _오만2층 = 102;
		int _오만3층 = 103;
		int _오만4층 = 104;
		int _오만5층 = 105;
		int _오만6층 = 106;
		int _오만7층 = 107;
		int _오만8층 = 108;
		int _오만9층 = 109;
		int _오만10층 = 111;
		int _테베라스 = 781;
		int _오만정상 = 200;
		int _결계 = 410;
		
		if ((getMapId() == _오만1층) && getInventory().checkItem(5370617)) {// 1층
			return true;
		}
		if ((getMapId() == _오만2층) && getInventory().checkItem(5370618)) {// 2층
			return true;
		}
		if ((getMapId() == _오만3층) && getInventory().checkItem(5370619)) {// 3층
			return true;
		}
		if ((getMapId() == _오만4층) && getInventory().checkItem(5370620)) {// 4층
			return true;
		}
		if ((getMapId() == _오만5층) && getInventory().checkItem(5370621)) {// 5층
			return true;
		}
		if ((getMapId() == _오만6층) && getInventory().checkItem(5370622)) {// 6층
			return true;
		}
		if ((getMapId() == _오만7층) && getInventory().checkItem(5370623)) {// 7층
			return true;
		}
		if ((getMapId() == _오만8층) && getInventory().checkItem(5370624)) {// 8층
			return true;
		}
		if ((getMapId() == _오만9층) && getInventory().checkItem(5370625)) {// 9층
			return true;
		}
		if ((getMapId() == _오만10층) && getInventory().checkItem(5370626)) {// 10층
			return true;
		}
		if ((getMapId() == _테베라스) && getInventory().checkItem(120288)) {// 10층
			return true;
		}
		if ((getMapId() == _오만정상) && getInventory().checkItem(120288)) {// 10층
			return true;
		}
		if ((getMapId() == _결계) && getInventory().checkItem(120288)) {// 10층
			return true;
		}
		return false;
	}

	public void sendPackets(ServerBasePacket serverbasepacket, boolean clear) {
		try {
			if ((getMapId() == 2699 || getMapId() == 2100)
					&& serverbasepacket.getType().equalsIgnoreCase("[S] S_OtherCharPacks")) {
			} else
				sendPackets(serverbasepacket);
			if (clear) {
				serverbasepacket.clear();
				serverbasepacket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private L1ItemInstance _fishingitem;

	public L1ItemInstance getFishingItem() {
		return _fishingitem;
	}

	public void setFishingItem(L1ItemInstance item) {
		_fishingitem = item;
	}

	public String getClassName() {
		if (isCrown()) {
			return "군주";
		} else if (isKnight()) {
			return "기사";
		} else if (isElf()) {
			return "요정";
		} else if (isWizard()) {
			return "마법사";
		} else if (isDarkelf()) {
			return "다크엘프";
		}

		return "직업명";
	}

	private boolean _isautodoll;

	public boolean isAutoDoll() {
		return this._isautodoll;
	}

	public void setAutoDoll(boolean i) {
		this._isautodoll = i;
	}

	public long lastSpellUseMillis = 0L;
	private L1DollInstance _doll;

	public L1DollInstance getMagicDoll() {
		return _doll;
	}

	public void setMagicDoll(L1DollInstance doll) {
		_doll = doll;
	}

	private HashMap<Integer, Integer> m_private_porbability = null;

	public void load_private_probability() {
		final HashMap<Integer, Integer> probabilities = new HashMap<Integer, Integer>();
		Selector.exec("select * from characters_private_probability where object_id=?", new SelectorHandler() {
			@Override
			public void handle(PreparedStatement pstm) throws Exception {
				pstm.setInt(1, getId());
			}

			@Override
			public void result(ResultSet rs) throws Exception {
				while (rs.next()) {
					probabilities.put(rs.getInt("skill_id"), rs.getInt("probability"));
				}
			}
		});
		m_private_porbability = probabilities;
	}

	public void add_private_probability(final int skill_id, final int probability) {
		if (m_private_porbability != null)
			m_private_porbability.put(skill_id, probability);
		Updator.exec(
				"insert into characters_private_probability set object_id=?, skill_id=?, probability=? on duplicate key update skill_id=?, probability=?",
				new Handler() {
					@Override
					public void handle(PreparedStatement pstm) throws Exception {
						int idx = 0;
						pstm.setInt(++idx, getId());
						pstm.setInt(++idx, skill_id);
						pstm.setInt(++idx, probability);
						pstm.setInt(++idx, skill_id);
						pstm.setInt(++idx, probability);
					}
				});

	}

	public int get_private_probability(int skill_id) {
		return m_private_porbability != null && m_private_porbability.containsKey(skill_id)
				? m_private_porbability.get(skill_id)
				: 0;
	}

	public void truncate_private_probability() {
		if (m_private_porbability != null)
			m_private_porbability.clear();
	}

	public Account getAccount() {
		return _account;
	}

	public void setAccount(Account _account) {
		this._account = _account;
	}

	// 비스킷 추가, 비스킷 수정 주사위 주사위 주사위
	/** 소막 및 주사위 게임 */
	// by 악덕
	private boolean _isGambling = false;

	public boolean isGambling() {
		return _isGambling;
	}

	public void setGambling(boolean flag) {
		_isGambling = flag;
	}

	private int _gamblingmoney = 0;

	public int getGamblingMoney() {
		return _gamblingmoney;
	}

	public void setGamblingMoney(int i) {
		_gamblingmoney = i;
	}

	// //##########겜블 소막 주사위 묵찌빠############
	private boolean _isGambling1 = false;

	public boolean isGambling1() {
		return _isGambling1;
	}

	public void setGambling1(boolean flag) {
		_isGambling1 = flag;
	}

	private int _gamblingmoney1 = 0;

	public int getGamblingMoney1() {
		return _gamblingmoney1;
	}

	public void setGamblingMoney1(int i) {
		_gamblingmoney1 = i;
	}

	// //##########겜블 소막 주사위 묵찌빠############
	private boolean _isGambling3 = false;

	public boolean isGambling3() {
		return _isGambling3;
	}

	public void setGambling3(boolean flag) {
		_isGambling3 = flag;
	}

	private int _gamblingmoney3 = 0;

	public int getGamblingMoney3() {
		return _gamblingmoney3;
	}

	public void setGamblingMoney3(int i) {
		_gamblingmoney3 = i;
	}

	// //##########겜블 소막 주사위 묵찌빠############
	private boolean _isGambling4 = false;

	public boolean isGambling4() {
		return _isGambling4;
	}

	public void setGambling4(boolean flag) {
		_isGambling4 = flag;
	}

	private int _gamblingmoney4 = 0;

	public int getGamblingMoney4() {
		return _gamblingmoney4;
	}

	public void setGamblingMoney4(int i) {
		_gamblingmoney4 = i;
	}

	/** 소막 및 주사위 및 묵 찌 빠 게임 */
	// /////추사위 추가, 주사위 수정 주사위 주사위 주사위

	private boolean _autotell;

	public boolean getAutoTell() {
		return _autotell;
	}

	public void setAutoTell(boolean i) {
		_autotell = i;
	}

	private boolean _autopickup = true;

	public boolean getAutoPickup() {
		return _autopickup;
	}

	public void setAutoPickup(boolean i) {
		_autopickup = i;
	}
	
	private boolean _autoHuntStatus;

	public boolean getAutoHuntStatus() {
		return _autoHuntStatus;
	}

	public void setAutoHuntStatus(boolean i) {
		_autoHuntStatus = i;
	}

	private int _autoStatus;

	public int getAutoStatus() {
		return _autoStatus;
	}

	public void setAutoStatus(int i) {
		_autoStatus = i;
	}

	private L1HateList _autoTargetList = new L1HateList();

	public L1HateList getAutoTargetList() {
		return _autoTargetList;
	}

	public void setAutoTargetList(L1HateList attackList) {
		this._autoTargetList = attackList;
	}

	public void addAutoTargetList(L1Character mon) {
		if (_autoTargetList.containsKey(mon)) {
			return;
		}
		_autoTargetList.add(mon, 0);
	}

	public void removeAutoTargetList(L1Character mon) {
		if (mon == null || !_autoTargetList.containsKey(mon))
			return;
		_autoTargetList.remove(mon);
	}

	private L1Character _autoTarget;

	public L1Character getAutoTarget() {
		return _autoTarget;
	}

	public void setAutoTarget(L1Character mon) {
		_autoTarget = mon;
	}

	private boolean _isAutoSetting;

	public boolean isAutoSetting() {
		return _isAutoSetting;
	}

	public void setAutoSetting(boolean b) {
		_isAutoSetting = b;
	}

	private int _autox;

	public int getAutoX() {
		return _autox;
	}

	public void setAutoX(int i) {
		_autox = i;
	}

	private int _autoy;

	public int getAutoY() {
		return _autoy;
	}

	public void setAutoY(int i) {
		_autoy = i;
	}

	private int _automapid;

	public int getAutoMapid() {
		return _automapid;
	}

	public void setAutoMapid(int i) {
		_automapid = i;
	}

	private int _autoPolyId;

	public int getAutoPolyID() {
		return _autoPolyId;
	}

	public void setAutoPolyID(int i) {
		_autoPolyId = i;
	}

	private int _autoLocX;

	public int getAutoLocX() {
		return _autoLocX;
	}

	public void setAutoLocX(int i) {
		_autoLocX = i;
	}

	private int _autoLocY;

	public int getAutoLocY() {
		return _autoLocY;
	}

	public void setAutoLocY(int i) {
		_autoLocY = i;
	}

	private long _autoSkillDelay;

	public long getAutoSkillDelay() {
		return _autoSkillDelay;
	}

	public void setAutoSkillDelay(long i) {
		_autoSkillDelay = i;
	}

	private int attackRange;

	public void setAttackRang(int attackRange) {
		this.attackRange = attackRange;
	}

	public int getAttackRang() {
		return attackRange;
	}


	private L1Astar _autoAStar = new L1Astar();

	public L1Astar getAutoAstar() {
		return _autoAStar;
	}

	public void setAutoAstar(L1Astar a) {
		_autoAStar = a;
	}

	private List<int[]> _autoPath = new ArrayList<>();

	public List<int[]> getAutoPath() {
		return _autoPath;
	}

	public void setAutoPath(List<int[]> i) {
		_autoPath = i;
	}

	private L1Node _autoTail;

	public L1Node getAutoTail() {
		return _autoTail;
	}

	public void setAutoTail(L1Node node) {
		_autoTail = node;
	}

	public int _autoCurrentPath;

	private int _autoMoveCount = CommonUtil.random(50, 200);

	public int getAutoMoveCount() {
		return _autoMoveCount;
	}

	public void setAutoMoveCount(int i) {
		_autoMoveCount = i;
	}

	private boolean _autoDead;

	public boolean isAutoDead() {
		return _autoDead;
	}

	public void setAutoDead(boolean b) {
		_autoDead = b;
	}

	private long _autoTimeMove;

	public long getAutoTimeMove() {
		return _autoTimeMove;
	}

	public void setAutoTimeMove(long time) {
		_autoTimeMove = time;
	}

	private int _autoDeadTime = 5;

	public int getAutoDeadTime() {
		return _autoDeadTime;
	}

	public void setAutoDeadTime(int i) {
		_autoDeadTime = i;
	}

	private int _autoReadyTime = 5;

	public int getAutoReadyTime() {
		return _autoReadyTime;
	}

	public void setAutoReadyTime(int i) {
		_autoReadyTime = i;
	}

	private int _autoTeleportTime;

	public int getAutoTeleportTime() {
		return _autoTeleportTime;
	}

	public void setAutoTeleportime(int i) {
		_autoTeleportTime = i;
	}

	private long _autoAiTime;

	public long getAutoAiTime() {
		return _autoAiTime;
	}

	public void setAutoAiTime(long l) {
		_autoAiTime = l;
	}

	private int teleport_x;

	public int get_teleport_x() {
		return teleport_x;
	}

	public void set_teleport_x(int i) {
		this.teleport_x = i;
	}

	private int teleport_y;

	public int get_teleport_y() {
		return teleport_y;
	}

	public void set_teleport_y(int i) {
		this.teleport_y = i;
	}

	private int teleport_map;

	public int get_teleport_map() {
		return teleport_map;
	}

	public void set_teleport_map(int i) {
		this.teleport_map = i;
	}

	private long _autoTimeAttack;

	public long getAutoTimeAttack() {
		return _autoTimeAttack;
	}

	public void setAutoTimeAttack(long time) {
		_autoTimeAttack = time;
	}

	public ArrayList<Integer> get_자동판매리스트() {
		return this._자동판매리스트;
	}

	public void set_자동판매리스트(final ArrayList<Integer> _자동판매리스트) {
		this._자동판매리스트 = _자동판매리스트;
	}

	public void add_자동판매리스트(final int itemId) {
		if (!this._자동판매리스트.contains(itemId)) {
			this._자동판매리스트.add(itemId);
		}
	}

	public boolean check_자동판매리스트(final int itemId) {
		return this._자동판매리스트.contains(itemId);
	}

	public ArrayList<Integer> get_자동구입리스트() {
		return this._자동구입리스트;
	}

	public void set_자동구입리스트(final ArrayList<Integer> _자동구입리스트) {
		this._자동구입리스트 = _자동구입리스트;
	}

	public boolean check_자동구입리스트(final int itemId) {
		return this._자동구입리스트.contains(itemId);
	}

	public ArrayList<Integer> get_자동물약리스트() {
		return this._자동물약리스트;
	}

	public void set_자동물약리스트(final ArrayList<Integer> _자동물약리스트) {
		this._자동물약리스트 = _자동물약리스트;
	}

	public boolean check_자동물약리스트(final int itemId) {
		return this._자동물약리스트.contains(itemId);
	}

	public boolean is_자동버프세이프티존사용() {
		return this._자동버프세이프티존사용;
	}

	public void set_자동버프세이프티존사용(final boolean _자동버프세이프티존사용) {
		this._자동버프세이프티존사용 = _자동버프세이프티존사용;
	}

	public boolean is_자동버프전투시사용() {
		return this._자동버프전투시사용;
	}

	public void set_자동버프전투시사용(final boolean _자동버프전투시사용) {
		this._자동버프전투시사용 = _자동버프전투시사용;
	}

	public boolean is_자동버프사용() {
		return this._자동버프사용;
	}

	public void set_자동버프사용(final boolean _자동버프사용) {
		this._자동버프사용 = _자동버프사용;
	}

	public boolean is_자동판매사용() {
		return this._자동판매사용;
	}

	public void set_자동판매사용(final boolean _자동판매사용) {
		this._자동판매사용 = _자동판매사용;
	}

	public boolean is_자동구입사용() {
		return this._자동구입사용;
	}

	public void set_자동구입사용(final boolean _자동구입사용) {
		this._자동구입사용 = _자동구입사용;
	}

	public boolean is_자동물약사용() {
		return this._자동물약사용;
	}

	public void set_자동물약사용(final boolean _자동물약사용) {
		this._자동물약사용 = _자동물약사용;
	}

	public boolean 자동물약 = false;

	public boolean PartyRootMent = true;

	public int get_자동물약퍼센트() {
		return this._자동물약퍼센트;
	}

	public void set_자동물약퍼센트(int _자동물약퍼센트) {
		this._자동물약퍼센트 = _자동물약퍼센트;
	}

	private boolean _라이트켬;

	public boolean is_라이트켬() {
		return _라이트켬;
	}

	public void set_라이트켬(boolean _라이트켬) {
		this._라이트켬 = _라이트켬;
	}

	private int tempshopid;

	public int getTempShopId() {
		return tempshopid;
	}

	public void setTempShipID(int i) {
		tempshopid = i;
	}

	private boolean _물약자동구매;

	public boolean is_물약자동구매() {
		return _물약자동구매;
	}

	public void set_물약자동구매(boolean flag) {
		_물약자동구매 = flag;
	}
	
	// 자동 사냥
	private boolean isAutoHunt = false;

	public boolean getAutoHunt() {
		return isAutoHunt;
	}

	public void setAutoHunt(boolean flag) {
		isAutoHunt = flag;
	}

	
	public void broadcastRemoveAllKnownObjects() {
		for (L1Object known : getKnownObjects()) {
			if (known == null) {
				continue;
			}

			sendPackets(new S_RemoveObject(known));
		}
	}
	
	public boolean isNonAction(L1Character c) {
		return (c.getSkillEffectTimerSet().hasSkillEffect(SHOCK_STUN))
				|| (c.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND))
				|| (c.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ICE_LANCE))
				|| (c.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_FREEZE))
				|| (c.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZING)) || (c.isParalyzed())
				|| (c.isSleeped());
	}
	
	/** 슬롯체인지 **/
	private List<Integer> _slotItemOne = new ArrayList<Integer>();
	private List<Integer> _slotItemTwo = new ArrayList<Integer>();

	public void addSlotItem(int slotNum, int itemobjid, boolean flag) {
		if (flag) {
			if (slotNum == 0) {
				_slotItemOne.clear();
				for (L1ItemInstance item : getInventory().getItems()) {
					if (item.isEquipped()) {
						_slotItemOne.add(item.getId());
					}
				}
			} else if (slotNum == 1) {
				_slotItemTwo.clear();
				for (L1ItemInstance item : getInventory().getItems()) {
					if (item.isEquipped()) {
						_slotItemTwo.add(item.getId());
					}
				}
			}
		} else {
			if (slotNum == 0) {
				_slotItemOne.add(itemobjid);
			} else if (slotNum == 1) {
				_slotItemTwo.add(itemobjid);
			}
		}
	}

	public List<Integer> getSlotItems(int slotNum) {
		if (slotNum == 0) {
			return _slotItemOne;
		} else if (slotNum == 1) {
			return _slotItemTwo;
		}
		return null;
	}

	public void getChangeSlot(int slotNum) {
		if (slotNum == 0) {
			for (L1ItemInstance item : this.getInventory().getItems()) {
				if (!_slotItemOne.contains(item.getId())) {
					if (item.isEquipped()) {
						getInventory().setEquipped(item, false);
					} else {

					}
				}
			}
			for (L1ItemInstance item : this.getInventory().getItems()) {
				if (_slotItemOne.contains(item.getId())) {
					if (item.isEquipped()) {
						if (item.getItem().getType2() == 1) {
							if (!L1PolyMorph.isEquipableWeapon(getCurrentSpriteId(), item.getItem().getType())) {
								getInventory().setEquipped(item, false);
							}
						}
					} else {
						if (item.getItem().getType2() == 1) {
							if (!L1PolyMorph.isEquipableWeapon(getCurrentSpriteId(), item.getItem().getType())) {
								continue;
							}
						}
						getInventory().setEquipped(item, true);
					}
				}
			}
		} else if (slotNum == 1) {
			for (L1ItemInstance item : this.getInventory().getItems()) {
				if (!_slotItemTwo.contains(item.getId())) {
					if (item.isEquipped()) {
						getInventory().setEquipped(item, false);
					} else {

					}
				}
			}
			for (L1ItemInstance item : this.getInventory().getItems()) {
				if (_slotItemTwo.contains(item.getId())) {
					if (item.isEquipped()) {
						if (item.getItem().getType2() == 1) {
							if (!L1PolyMorph.isEquipableWeapon(getCurrentSpriteId(), item.getItem().getType())) {
								getInventory().setEquipped(item, false);
							}
						}
					} else {
						if (item.getItem().getType2() == 1) {
							if (!L1PolyMorph.isEquipableWeapon(getCurrentSpriteId(), item.getItem().getType())) {
								continue;
							}
						}
						getInventory().setEquipped(item, true);
					}
				}
			}
		}
	}

	private int slotNumber = 0;

	public int getSlotNumber() {
		return slotNumber;
	}

	public void setSlotNumber(int i) {
		slotNumber = i;
	}
	
private int _seletedOneDoll1 = 0;
	
	public void setSeletedOneDoll1(int i) {
		_seletedOneDoll1 = i;
	}

	public int getSeletedOneDoll1() {
		return _seletedOneDoll1;
	}
	
	private int _seletedOneDoll2 = 0;
	
	public void setSeletedOneDoll2(int i) {
		_seletedOneDoll2 = i;
	}

	public int getSeletedOneDoll2() {
		return _seletedOneDoll2;
	}
	
	private int _seletedOneDoll3 = 0;
	
	public void setSeletedOneDoll3(int i) {
		_seletedOneDoll3 = i;
	}

	public int getSeletedOneDoll3() {
		return _seletedOneDoll3;
	}
	
	private int _seletedOneDoll4 = 0;
	
	public void setSeletedOneDoll4(int i) {
		_seletedOneDoll4 = i;
	}

	public int getSeletedOneDoll4() {
		return _seletedOneDoll4;
	}
	
	private int _seletedTwoDoll1 = 0;
	
	public void setSeletedTwoDoll1(int i) {
		_seletedTwoDoll1 = i;
	}

	public int getSeletedTwoDoll1() {
		return _seletedTwoDoll1;
	}
	
	private int _seletedTwoDoll2 = 0;
	
	public void setSeletedTwoDoll2(int i) {
		_seletedTwoDoll2 = i;
	}

	public int getSeletedTwoDoll2() {
		return _seletedTwoDoll2;
	}
	
	private int _seletedTwoDoll3 = 0;
	
	public void setSeletedTwoDoll3(int i) {
		_seletedTwoDoll3 = i;
	}

	public int getSeletedTwoDoll3() {
		return _seletedTwoDoll3;
	}
	
	private int _seletedTwoDoll4 = 0;
	
	public void setSeletedTwoDoll4(int i) {
		_seletedTwoDoll4 = i;
	}

	public int getSeletedTwoDoll4() {
		return _seletedTwoDoll4;
	}
	
	private int _seletedThreeDoll1 = 0;
	
	public void setSeletedThreeDoll1(int i) {
		_seletedThreeDoll1 = i;
	}

	public int getSeletedThreeDoll1() {
		return _seletedThreeDoll1;
	}
	
	private int _seletedThreeDoll2 = 0;
	
	public void setSeletedThreeDoll2(int i) {
		_seletedThreeDoll2 = i;
	}

	public int getSeletedThreeDoll2() {
		return _seletedThreeDoll2;
	}
	
	private int _seletedThreeDoll3 = 0;
	
	public void setSeletedThreeDoll3(int i) {
		_seletedThreeDoll3 = i;
	}

	public int getSeletedThreeDoll3() {
		return _seletedThreeDoll3;
	}
	
	private int _seletedThreeDoll4 = 0;
	
	public void setSeletedThreeDoll4(int i) {
		_seletedThreeDoll4 = i;
	}

	public int getSeletedThreeDoll4() {
		return _seletedThreeDoll4;
	}
 
	private int _seletedFourDoll1 = 0;
	
	public void setSeletedFourDoll1(int i) {
		_seletedFourDoll1 = i;
	}

	public int getSeletedFourDoll1() {
		return _seletedFourDoll1;
	}
	
	private int _seletedFourDoll2 = 0;
	
	public void setSeletedFourDoll2(int i) {
		_seletedFourDoll2 = i;
	}

	public int getSeletedFourDoll2() {
		return _seletedFourDoll2;
	}
	
	private int _seletedFourDoll3 = 0;
	
	public void setSeletedFourDoll3(int i) {
		_seletedFourDoll3 = i;
	}

	public int getSeletedFourDoll3() {
		return _seletedFourDoll3;
	}
	
	private int _seletedFourDoll4 = 0;
	
	public void setSeletedFourDoll4(int i) {
		_seletedFourDoll4 = i;
	}

	public int getSeletedFourDoll4() {
		return _seletedFourDoll4;
	}
	
	private int _seletedFiveDoll1 = 0;
	
	public void setSeletedFiveDoll1(int i) {
		_seletedFiveDoll1 = i;
	}

	public int getSeletedFiveDoll1() {
		return _seletedFiveDoll1;
	}
	
	private int _seletedFiveDoll2 = 0;
	
	public void setSeletedFiveDoll2(int i) {
		_seletedFiveDoll2 = i;
	}

	public int getSeletedFiveDoll2() {
		return _seletedFiveDoll2;
	}
	
	private int _seletedFiveDoll3 = 0;
	
	public void setSeletedFiveDoll3(int i) {
		_seletedFiveDoll3 = i;
	}

	public int getSeletedFiveDoll3() {
		return _seletedFiveDoll3;
	}
	
	private int _seletedFiveDoll4 = 0;
	
	public void setSeletedFiveDoll4(int i) {
		_seletedFiveDoll4 = i;
	}

	public int getSeletedFiveDoll4() {
		return _seletedFiveDoll4;
	}
	
	int useddollId = 0;
	
	public int getusedDollId() {
		return useddollId;
	}
	
	public void setusedDollId(int id) {
		useddollId = id;
	}
	
	private boolean autoDollFlag = false; // 인형 자동 소환 플래그

	public void setAutoDollFlag(boolean flag) {
	    this.autoDollFlag = flag;
	}

	public boolean isAutoDollFlag() {
	    return this.autoDollFlag;
	}
	
	private boolean _dolleffect = true;

	public void setDollEffect(boolean flag) {
	    _dolleffect = flag;
	}

	public boolean isDollEffect() {
	    return _dolleffect;
	}
	
	 /** 배틀존 **/
    private int _DuelLine;

    public int get_DuelLine() {
        return _DuelLine;
    }

    public void set_DuelLine(int i) {
        _DuelLine = i;
    }
    
	private int _lastAutoCheckTime = 0;
	private int _autoCheckDuration;
	private int _autoCheckCount;
	private String _autoAuthCode;
	
	private int getAutoCheckDuration() {
		return _autoCheckDuration;
	}
	
	// 오토방지코드
	private void showAutoAuthDialog() {
	    ++_autoCheckCount;

	    Random random = new Random(System.nanoTime());
	    _autoAuthCode = String.format("%01d", random.nextInt(10));

	    sendPackets(new S_ChatPacket(this,
	        "오토방지: " + _autoAuthCode + " + 3 = ??",
	        Opcodes.S_OPCODE_NORMALCHAT, 2));
	    sendPackets(new S_SystemMessage("\\fY오토방지: " + _autoAuthCode + " + 3 = ??"));
		sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, String.format("\\fY오토방지: " + _autoAuthCode + " + 3 = ??")));
	    sendPackets(new S_SystemMessage(
	        "오토방지코드: " + _autoAuthCode + " + 3 = ?? 미입력시 압류 됩니다."));

	    if (_autoCheckCount == 1) {
	        _lastAutoCheckTime = (int)(System.currentTimeMillis() / 1000);
	    }
	}
	
	public boolean waitAutoAuth() {
		return _autoCheckCount > 0;
	}

	public String getAutoAuthCode() {// 스트링
		int temp1 = Integer.valueOf(_autoAuthCode);
		int code = temp1 + 3;
		return String.valueOf(code);
	}

	public void resetAutoInfo() {
		_autoCheckCount = 0;
		_lastAutoCheckTime = (int) (System.currentTimeMillis() / 1000);
		genAutoCheckDuration();
	}

	private void genAutoCheckDuration() {
		 Random random = new Random();
		 _autoCheckDuration = 3600 + random.nextInt(3601); // 3600~7200
	}
	
	public synchronized boolean checkAuto() {
	    if (_netConnection == null) {
	        return false;
	    }

	    int now = (int)(System.currentTimeMillis() / 1000);

	    if (_lastAutoCheckTime == 0) {
	        _lastAutoCheckTime = now;
	        genAutoCheckDuration();
	        return false;
	    }

	    if (_autoCheckCount > 0 && now > _lastAutoCheckTime + 120) {
	        L1Teleport.teleport(this, 32835, 32782, (short) 701, 5, true);
	        resetAutoInfo();
	        return true;
	    }

	    if (_autoCheckCount == 0 &&
	        _lastAutoCheckTime + getAutoCheckDuration() < now) {
	        showAutoAuthDialog();
	    }

	    return false;
	}

	private boolean _autoturnundead = false;

	public boolean getAutoTurnUndead() {
		return _autoturnundead;
	}

	public void setAutoTurnUndead(boolean i) {
		_autoturnundead = i;
	}

	private boolean _autobuypotion;

	public boolean getAutoBuyPotion() {
		return _autobuypotion;
	}

	public void setAutoBuyPotion(boolean i) {
		_autobuypotion = i;
	}

	private int _자동피퍼센트 = 80;

	public int get_자동피퍼센트() {
		return _자동피퍼센트;
	}

	public void set_자동피퍼센트(int 퍼센트) {
		_자동피퍼센트 = 퍼센트;
	}

	private int _자동마나퍼센트 = 50;

	public int get_자동마나퍼센트() {
		return _자동마나퍼센트;
	}

	public void set_자동마나퍼센트(int 퍼센트) {
		_자동마나퍼센트 = 퍼센트;
	}

	private int _자동귀환퍼센트 = 30;

	public int get_자동귀환퍼센트() {
		return _자동귀환퍼센트;
	}

	public void set_자동귀환퍼센트(int 퍼센트) {
		_자동귀환퍼센트 = 퍼센트;
	}
	
	private boolean _autoskill1 = false;

	public boolean getAutoskill1() {
		return _autoskill1;
	}

	public void setAutoskill1(boolean i) {
		_autoskill1 = i;
	}

	private boolean _autoskill2 = false;

	public boolean getAutoskill2() {
		return _autoskill2;
	}

	public void setAutoskill2(boolean i) {
		_autoskill2 = i;
	}

	private boolean _autoskill3 = false;

	public boolean getAutoskill3() {
		return _autoskill3;
	}

	public void setAutoskill3(boolean i) {
		_autoskill3 = i;
	}

	private boolean _autoskill4 = false;

	public boolean getAutoskill4() {
		return _autoskill4;
	}

	public void setAutoskill4(boolean i) {
		_autoskill4 = i;
	}

	private boolean _autoskill5 = false;

	public boolean getAutoskill5() {
		return _autoskill5;
	}

	public void setAutoskill5(boolean i) {
		_autoskill5 = i;
	}

	private boolean _autoskill6 = false;

	public boolean getAutoskill6() {
		return _autoskill6;
	}

	public void setAutoskill6(boolean i) {
		_autoskill6 = i;
	}

	private boolean _autoskill7 = false;

	public boolean getAutoskill7() {
		return _autoskill7;
	}

	public void setAutoskill7(boolean i) {
		_autoskill7 = i;
	}

	private boolean _autosoul = true;

	public boolean getAutosoul() {
		return _autosoul;
	}

	public void setAutosoul(boolean i) {
		_autosoul = i;
	}

	private boolean _autoTripple = true;

	public boolean getAutoTripple() {
		return _autoTripple;
	}

	public void setAutoTripple(boolean i) {
		_autoTripple = i;
	}
	
	public ScheduledFuture<?> _AutoController;

	public void StartAutoController() {
		resetAuto();
		setAutoPolyID(getCurrentSpriteId());
		setAutoHunt(true);
		setAutoMapid(getMapId());
		_AutoController = GeneralThreadPool.getInstance().scheduleAtFixedRate(new AutoHuntController(getId()), 0L, 10L);
	}

	public void EndAutoController() {
		if (_AutoController != null) {
			_AutoController.cancel(true);
			_AutoController = null;
			resetAuto();
			toCharacterRefresh();
			setAutoHunt(false);
		}
	}

	public void resetAuto() {
		setAutoStatus(0);
		setAutoMoveCount(0);
		setAutoSkillDelay(0);
		setAutoTarget(null);
		getAutoTargetList().clear();
		setAutoAiTime(0);
	}

	public void toCharacterRefresh() {
		getNearObjects().removeAllKnownObjects();
		sendPackets(new S_OtherCharPacks(this));
		sendPackets(new S_OwnCharPack(this));
		updateObject();
		sendPackets(new S_CharVisualUpdate(this));
	}
	
	Autoportion ap = null;
	public L1ItemInstance Item = null;
	public int dealaytime = 990;

	public void start() {
		synchronized (this) {
			if (ap == null) {
				ap = new Autoportion();
				ap.start();
			}
		}
	}

	public void stoppotion() {
		synchronized (this) {
			if (ap != null) {
				ap = null;

				autoPotionName = null;
				isAutoPotion = false;
			}
		}
	}

	class Autoportion implements Runnable {
		public void start() {
			GeneralThreadPool.getInstance().execute(Autoportion.this);
		}

		public void run() {
			try {
				if (!isAutoPotion || autoPotionPercent < 1 || autoPotionName == null || autoPotionName == ""
						|| Item == null || getNetConnection() == null) {
					ap = null;
					return;
				}
				autoportion();
				GeneralThreadPool.getInstance().schedule(Autoportion.this, dealaytime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void autoportion() {
		int healhp = 0;
		int effect = 0;
		if (getNetConnection() == null) {
			return;
		}

		if (Item == null) {
			return;
		}

		if (isDead()) {
			return;
		}
		if (isPinkName()) {
			return;
		}

		if (hasItemDelay(2)) {
			return;
		}

		if (!getInventory().checkItem(Item.getItemId())) {
			stoppotion();
			return;
		}

		if (getCurrentHp() > getMaxHp() * autoPotionPercent * 0.01) {
			return;
		}

		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ICE_LANCE)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CURSE_PARALYZE)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_POISON_PARALYZED)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION)) {
			return;
		}

		if (getParalysis() != null)
			return;

		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {
			getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.ABSOLUTE_BARRIER);
			startHpRegenerationByDoll();
			startMpRegenerationByDoll();
		}

		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MEDITATION)) {
			getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.MEDITATION);
		}

		switch (Item.getItemId()) {
		case 40010:
			healhp = Config.빨갱이회복량;
			effect = 189;
			break;
		case 40011:
			healhp = Config.주홍이회복량;
			effect = 194;
			break;
		case 40012:
			healhp = Config.맑갱이회복량;
			effect = 197;
			break;
		default:
			healhp = 10;
			effect = 189;
			break;
		}
		getInventory().removeItem(Item, 1);
		sendPackets(new S_SkillSound(getId(), effect));
		broadcastPacket(new S_SkillSound(getId(), effect));
		if (getSkillEffectTimerSet().hasSkillEffect(POLLUTE_WATER)) { // 포르트워타중은
																		// 회복량1/2배
			healhp /= 2;
		}
		setCurrentHp(getCurrentHp() + healhp);
	}
	
	public int dealaytime() {
	    switch (Item.getItemId()) {
	      case 40010:
	      case 40012:
	      case 40011:
	        return dealaytime = 990;
	      default:
	        return dealaytime = 990;
	    }
	  }
}
