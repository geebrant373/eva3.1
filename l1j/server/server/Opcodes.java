/**
 * 
 * 					Eva Team - http://eva.pe.kr 
 * 				    
 * 					Member	- 샤샤 (Shax2)				 	 
 * 							- 코드 (Shyeon0111)
 * 							- 뮨법사 (Cr7016)
 * 							- 똥개 (Zinasura)
 * 							- 마스터 (Babuboss)
 * 
 */

package l1j.server.server;

public class Opcodes {

	public Opcodes() {}

	public static final int C_OPCODE_EXIT_GHOST = 0;
	public static final int C_OPCODE_RETURNTOLOGIN = 1;
	public static final int C_OPCODE_SELECT_CHARACTER = 5;
	public static final int C_OPCODE_HIRESOLDIER = 7;
	public static final int C_OPCODE_BOOKMARKDELETE = 8;
	public static final int C_OPCODE_DROPITEM = 10;//
	public static final int C_OPCODE_SHOP_N_WAREHOUSE = 11;//
	public static final int C_OPCODE_SELECTTARGET = 13;//
	public static final int C_OPCODE_NOTICECLICK = 14 ;//
	public static final int C_OPCODE_SETCASTLESECURITY = 15;//
	public static final int C_OPCODE_CLAN = 16;////
	public static final int C_OPCODE_FIX_WEAPON_LIST = 18;//
	public static final int C_OPCODE_USESKILL = 19;
	public static final int C_OPCODE_TRADEADDCANCEL = 21;
	public static final int C_OPCODE_CHANGEPASS = 22;
	public static final int C_OPCODE_DEPOSIT = 24;
	public static final int C_OPCODE_TRADE = 25;
	public static final int C_OPCODE_MOVE_CHECK = 26;
	public static final int C_OPCODE_ENTERPORTAL = 27;
	public static final int C_OPCODE_DRAWAL = 28;
	public static final int C_OPCODE_SECOND_PLEDGE = 31;
	public static final int C_OPCODE_RANK = 31;//
	public static final int C_OPCODE_TRADEADDOK = 32;//
	public static final int C_OPCODE_PLEDGE = 33;//
	public static final int C_OPCODE_QUITGAME = 35;//
	public static final int C_OPCODE_BANCLAN = 36;//
	public static final int C_OPCODE_WAREHOUSEPASSWORD = 37;//
	public static final int C_OPCODE_CASH_SHOP = 38;//
	public static final int C_OPCODE_TITLE = 39;//
	public static final int C_OPCODE_PICKUPITEM = 41;//
	public static final int C_OPCODE_BASERESET = 42;//
	public static final int C_OPCODE_CREATE_CHARACTER = 43;//
	public static final int C_OPCODE_DOOR = 44;//
	public static final int C_OPCODE_PETMENU = 45;//
	public static final int C_OPCODE_CLIENTVERSION = 46; //
	public static final int C_OPCODE_CREATECLAN = 48;//
	public static final int C_OPCODE_RESTART = 50;//
	public static final int C_OPCODE_USEITEM = 51;//
	public static final int C_OPCODE_SKILLBUYOK = 52;//
	public static final int C_OPCODE_UNKOWN1 = 53;//
	public static final int C_OPCODE_NPCTALK = 55;//
	public static final int C_OPCODE_TELEPORT = 56;//
	public static final int C_OPCODE_SHIP = 58;//
	public static final int C_OPCODE_CHANGEWARTIME = 102;//
	public static final int C_OPCODE_USEPETITEM = 60;//
	public static final int C_OPCODE_SKILLBUY = 63;//
	public static final int C_OPCODE_ADDBUDDY = 64;//
	public static final int C_OPCODE_BOARDWRITE = 65;//
	public static final int C_OPCODE_BOARDBACK = 66;//
	public static final int C_OPCODE_FISHCLICK = 67;//
	public static final int C_OPCODE_LEAVECLANE = 69;//
	public static final int C_OPCODE_LOGINTOSERVEROK = 70;//
	public static final int C_OPCODE_BUDDYLIST = 71;//
	public static final int C_OPCODE_MOVECHAR = 73;//
	public static final int C_OPCODE_ATTR = 74;//
	public static final int C_OPCODE_BOARDDELETE = 75;//
	public static final int C_OPCODE_DELEXCLUDE = 76;//
	public static final int C_OPCODE_EXCLUDE = 76;//
	public static final int C_OPCODE_CHATGLOBAL = 77;//
	public static final int C_OPCODE_PROPOSE = 78;//
	public static final int C_OPCODE_TRADEADDITEM = 79;//
	public static final int C_OPCODE_CASTLESECURITY = 81;//
	public static final int C_OPCODE_SHOP = 82;//
	public static final int C_OPCODE_CHAT = 83;//
	public static final int C_OPCODE_PUTSOLDIER = 84;//
	public static final int C_OPCODE_LEAVEPARTY = 85;//
	public static final int C_OPCODE_PARTY = 86;//
	public static final int C_OPCODE_REPORT = 87; // 
	public static final int C_OPCODE_BOARDREAD = 88;//
	public static final int C_OPCODE_CALL = 89;//
	public static final int C_OPCODE_WAR = 91;//
	public static final int C_OPCODE_CHECKPK = 92;//
	public static final int C_OPCODE_CHANGEHEADING = 93;//
	public static final int C_OPCODE_AMOUNT = 94;//
	public static final int C_OPCODE_WHO = 95;//
	public static final int C_OPCODE_FIGHT = 96;//
	public static final int C_OPCODE_NPCACTION = 97;//
	public static final int C_OPCODE_CHARACTERCONFIG = 100;//
	public static final int C_OPCODE_ATTACK = 101;//
	public static final int C_OPCODE_SELECTWARTIME = 102;//
	public static final int C_OPCODE_BOARD = 103;//
	public static final int C_OPCODE_PRIVATESHOPLIST = 104;//
	public static final int C_OPCODE_LOGINPACKET = 105;//
	public static final int C_OPCODE_SELECTLIST = 106;//
	public static final int C_OPCODE_MAIL = 107;//
	public static final int C_OPCODE_EXTCOMMAND = 108;//
	public static final int C_OPCODE_DELETECHAR = 110;//
	public static final int C_OPCODE_DELBUDDY = 112;//
	public static final int C_OPCODE_ARROWATTACK = 113;//
	public static final int C_OPCODE_EMBLEM = 114;//
	public static final int C_OPCODE_BANPARTY = 115;//
	public static final int C_OPCODE_CHATWHISPER = 116;//
	public static final int C_OPCODE_SMS = 117;//
	public static final int C_OPCODE_PUTHIRESOLDIER = 118;//
	public static final int C_OPCODE_BOOKMARK = 119;//
	public static final int C_OPCODE_PUTBOWSOLDIER = 120;//
	public static final int C_OPCODE_KEEPALIVE = 121;//
	public static final int C_OPCODE_TAXRATE = 122;//
	public static final int C_OPCODE_GIVEITEM = 124;//
	public static final int C_OPCODE_JOINCLAN = 125;//
	public static final int C_OPCODE_DELETEINVENTORYITEM = 126;//
	public static final int C_OPCODE_RESTART_AFTER_DIE = 127;//
	public static final int C_OPCODE_CREATEPARTY = 130;//
	public static final int C_OPCODE_CHATPARTY = 131;//

	
	/**3.2C ServerPacket*/
	public static final int S_OPCODE_COMMONNEWS2 = 0;
	public static final int S_OPCODE_USEMAP = 71;
	public static final int S_LETTER = 90;

	/**3.3C ServerPacket*/
	public static final int S_OPCODE_BLUEMESSAGE = 0;//
	public static final int S_OPCODE_BLESSOFEVA = 1;//
	public static final int S_OPCODE_NPCSHOUT = 3;//
	public static final int S_OPCODE_RESURRECTION = 4;//
	public static final int S_OPCODE_BOARDREAD = 5;//
	public static final int S_OPCODE_CASTLEMASTER = 6;//
	public static final int S_OPCODE_FIX_WEAPON_MENU = 7;//
	public static final int S_OPCODE_SELECTLIST = 7;//
	public static final int S_OPCODE_ADDSKILL = 8;//
	public static final int S_OPCODE_CHARVISUALUPDATE = 9;//
	public static final int S_OPCODE_NOTICE = 10;//
	public static final int S_OPCODE_CHARAMOUNT = 11;//
	public static final int S_OPCODE_PARALYSIS = 12;//
	public static final int S_OPCODE_REDMESSAGE = 13; //
	public static final int S_OPCODE_INPUTAMOUNT = 14;//
	public static final int S_OPCODE_SKILLSOUNDGFX = 15;//
	public static final int S_OPCODE_IDENTIFYDESC = 16;//
	public static final int S_OPCODE_EFFECTLOCATION = 18;//
	public static final int S_OPCODE_LETTER = 19;//
	public static final int S_OPCODE_SHOWRETRIEVELIST = 21;//
	public static final int S_OPCODE_HOUSELIST = 22;//
	public static final int S_OPCODE_SKILLBUY = 23;//
	public static final int S_OPCODE_MSG = 24;//
	public static final int S_OPCODE_SYSMSG = 24;//
	public static final int S_OPCODE_CURSEBLIND = 25;//
	public static final int S_OPCODE_INVLIST = 26;//
	public static final int S_OPCODE_CHARPACK = 27;//
	public static final int S_OPCODE_SHOWOBJ = 27;//
	public static final int S_OPCODE_DROPITEM = 27;//
	public static final int S_OPCODE_SERVERMSG = 29;//
	public static final int S_OPCODE_NEWCHARPACK = 31;//
	public static final int S_OPCODE_DELSKILL = 34;//
	public static final int S_OPCODE_UNKNOWN1 = 35;//
	public static final int S_OPCODE_WHISPERCHAT = 36;//
	public static final int S_OPCODE_DRAWAL = 37;//
	public static final int S_OPCODE_CHARLIST = 38;//
	public static final int S_OPCODE_EMBLEM = 39;//
	public static final int S_OPCODE_ATTACKPACKET = 40;//
	public static final int S_OPCODE_SPMR = 42;//
	public static final int S_OPCODE_OWNCHARSTATUS = 43;//
	public static final int S_OPCODE_RANGESKILLS = 44;//
	public static final int S_OPCODE_SHOWSHOPSELLLIST = 45;//
	public static final int S_OPCODE_INVIS = 47;//
	public static final int S_OPCODE_NORMALCHAT = 48;//
	public static final int S_OPCODE_SKILLHASTE = 49;//
	public static final int S_OPCODE_TAXRATE = 50;//
	public static final int S_OPCODE_WEATHER = 51;//
	public static final int S_OPCODE_HIRESOLDIER = 52;//
	public static final int S_OPCODE_WAR = 53;//
	public static final int S_OPCODE_TELEPORTLOCK = 54;//
	public static final int S_OPCODE_PINKNAME = 55;//
	public static final int S_OPCODE_ITEMSTATUS = 56;//
	public static final int S_OPCODE_ITEMAMOUNT = 56;//
	public static final int S_OPCODE_PRIVATESHOPLIST = 57;//
	public static final int S_OPCODE_DETELECHAROK = 58;//
	public static final int S_OPCODE_BOOKMARKS = 59;//
	public static final int S_OPCODE_INITPACKET = 60;//
	public static final int S_OPCODE_MOVEOBJECT = 62;//
	public static final int S_OPCODE_PUTSOLDIER = 63;
	public static final int S_OPCODE_TELEPORT = 64;
	public static final int S_OPCODE_STRUP = 65;
	public static final int S_OPCODE_LAWFUL = 66;//
	public static final int S_OPCODE_SELECTTARGET = 67;//
	public static final int S_OPCODE_ABILITY = 68;//
	public static final int S_OPCODE_HPMETER = 69;//
	public static final int S_OPCODE_ATTRIBUTE = 70;//
	public static final int S_OPCODE_SERVERVERSION = 72;//
	public static final int S_OPCODE_EXP = 73;//
	public static final int S_OPCODE_MPUPDATE = 74;//
	public static final int S_OPCODE_CHANGENAME = 75;//
	public static final int S_OPCODE_POLY = 76;//
	public static final int S_OPCODE_MAPID = 77;//
	public static final int S_OPCODE_ITEMCOLOR = 79;//
	public static final int S_OPCODE_OWNCHARATTRDEF = 80;//
	public static final int S_OPCODE_PACKETBOX = 82;//
	public static final int S_OPCODE_ACTIVESPELLS = 82;
	public static final int S_OPCODE_SKILLICONGFX = 82;
	public static final int S_OPCODE_UNKNOWN2 = 82;
	public static final int S_OPCODE_DELETEINVENTORYITEM = 83;//
	public static final int S_OPCODE_RESTART = 84;//
	public static final int S_OPCODE_PINGTIME = 85;//
	public static final int S_OPCODE_DEPOSIT = 86;//
	public static final int S_OPCODE_TRUETARGET = 88;//
	public static final int S_OPCODE_HOUSEMAP = 89;//
	public static final int S_OPCODE_CHARTITLE = 90;//
	public static final int S_OPCODE_DEXUP = 92;//
	public static final int S_OPCODE_CHANGEHEADING = 94;//
	public static final int S_OPCODE_BOARD = 96;//
	public static final int S_OPCODE_LIQUOR = 97;//
	public static final int S_OPCODE_TRADESTATUS = 99;//
	public static final int S_OPCODE_SPOLY = 100;//
	public static final int S_OPCODE_UNDERWATER = 101;//
	public static final int S_OPCODE_SKILLBRAVE = 102;//
	public static final int S_OPCODE_PUTHIRESOLDIER = 103;//
	public static final int S_OPCODE_POISON = 104 ;//
	public static final int S_OPCODE_DISCONNECT = 105;//
	public static final int S_OPCODE_NEWCHARWRONG = 106;//
	public static final int S_OPCODE_REMOVE_OBJECT = 107;//
	public static final int S_OPCODE_NPC_ATTACKPACKET = 108;//
	public static final int S_OPCODE_ADDITEM = 110;//
	public static final int S_OPCODE_TRADE = 111;//
	public static final int S_OPCODE_OWNCHARSTATUS2 = 112;//
	public static final int S_OPCODE_SHOWHTML = 113;//
	public static final int S_OPCODE_SKILLICONSHIELD = 114;//
	public static final int S_OPCODE_DOACTIONGFX = 115;//
	public static final int S_OPCODE_TRADEADDITEM = 116;//
	public static final int S_OPCODE_YES_NO = 117;//
	public static final int S_OPCODE_HPUPDATE = 118;//
	public static final int S_OPCODE_SHOWSHOPBUYLIST = 119;//
	public static final int S_OPCODE_GAMETIME = 120;//
	public static final int S_OPCODE_PETCTRL = 121;//
	public static final int S_OPCODE_RETURNEDSTAT = 121; //
	public static final int S_OPCODE_SOUND = 122;//
	public static final int S_OPCODE_LIGHT = 123;//
	public static final int S_OPCODE_LOGINRESULT = 124;//
	public static final int S_OPCODE_PUTBOWSOLDIERLIST = 125;//
	public static final int S_OPCODE_WARTIME = 126;//
	public static final int S_OPCODE_ITEMNAME = 127;//
	public static final int S_EXTENDED_PROTOBUF = 0xB4;	// 종합 패킷
	/** Server Packet 안쓰는 것들 **/
	public static final int S_OPCODE_HORUN = 0x1003; // 호런
	public static final int S_OPCODE_DRAGONPERL = 86; // 드래곤진주
	public static final int S_OPCODE_PETGUI = 0x1008; // v 스텟 초기화 길이
	public static final int S_OPCODE_REFRESH_CLAN = 0x1009; // 혈원수 변동이 있을때 오는 패킷
	public static final int S_OPCODE_SOLDIERGIVE = 82; // 선택한 용병 주기
	public static final int S_OPCODE_SOLDIERBUYLIST = 97; // 성 용병 구입 리스트창
	public static final int S_OPCODE_SOLDIERGIVELIST = 97; // 용병 주는 선택 리스트 고용한 용병을 배치
	public static final int S_OPCODE_SHORTOFMATERIAL = 0x1010;
	public static final int S_OPCODE_ALLIANCECHAT = 0x1000; // 동맹채팅
	public static final int S_OPCODE_HOTELENTER = 0x1002; // 엔피씨로 여관 진입시>>
	public static final int C_OPCODE_HORUNOK = 0x1012; // 호런 마법배우기
	public static final int C_OPCODE_WARTIMESET = 0x1016; // 공성시간 지정
	public static final int C_OPCODE_HORUN = 0x1019; // 호런 클릭
	public static final int C_OPCODE_HOTEL_ENTER = 0x1021; // 엔피씨로 여관 진입시
		/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * **/
			
}