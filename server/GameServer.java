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
package server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.DollBonusEventSystem.DollBonusEventLoader;
import l1j.server.EventSystem.EventSystemLoader;
import l1j.server.EventSystem.EventSystemTimeController;
import l1j.server.ExchangeItem.ExchangeItemLoader;
import l1j.server.GameSystem.Auto.AutoBuffController;
import l1j.server.GameSystem.Auto.AutoBuyItemController;
import l1j.server.GameSystem.Auto.AutoPotionController;
import l1j.server.GameSystem.Auto.AutoSellItemController;
import l1j.server.GameSystem.CrockSystem;
import l1j.server.GameSystem.HomeTownController;
import l1j.server.GameSystem.ShopNpcSystem;
import l1j.server.GameSystem.Boss.BossSpawnTimeController;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.GameSystem.Robot.Robot_Fish;
import l1j.server.GameSystem.Robot.Robot_Hunt;
import l1j.server.GameSystem.Robot.Robot_Location;
import l1j.server.GameSystem.Timernpc.NewNpcSpawnTable;
import l1j.server.GameSystem.Timernpc.NpcTimeController;
import l1j.server.autoportion.AutoPotionSystem;
import l1j.server.GameSystem.MiniGame.BattleZone;
import l1j.server.MJ3SEx.MJSprBoundary;
import l1j.server.MJ3SEx.Loader.SpriteInformationLoader;
import l1j.server.NpcStatusDamage.NpcStatusDamageInfo;
import l1j.server.TowerOfDominance.DominanceDataLoader;
import l1j.server.TowerOfDominance.BossController.DominanceTimeController;
import l1j.server.server.model.DungeonTimer;
import l1j.server.server.GMCommands;
import l1j.server.server.GMCommandsConfig;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.LoopTimer;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.Shutdown;
import l1j.server.server.TimeController.DungeonQuitController;
import l1j.server.server.TimeController.PremiumTimeController;
import l1j.server.server.TimeController.PremiumTimeController2;
import l1j.server.server.TimeController.TebeController;
import l1j.server.server.TimeController.AdenaBoardController;
import l1j.server.server.TimeController.AuctionTimeController;
import l1j.server.server.TimeController.AutoDollController;
import l1j.server.server.TimeController.FishingTimeController;
import l1j.server.server.TimeController.HouseTaxTimeController;
import l1j.server.server.TimeController.InvenTimeController;
import l1j.server.server.TimeController.LightTimeController;
import l1j.server.server.TimeController.LotationController;
import l1j.server.server.TimeController.NpcChatTimeController;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.datatables.AccessoryBalanceTable;
import l1j.server.server.datatables.AccessoryEnchantInformationTable;
import l1j.server.server.datatables.AccessoryEnchantInformationTable1;
import l1j.server.server.datatables.AddRewardMonTable;
import l1j.server.server.datatables.ArmorBalanceTable;
import l1j.server.server.datatables.AttrEnchantControlTable;
import l1j.server.server.datatables.BoardAdenaTable;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.CharacterBalance;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.CharactersAcTable;
import l1j.server.server.datatables.CharactersMrTable;
import l1j.server.server.datatables.CharactersReducTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.CraftListTable;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.DropEventTable;
import l1j.server.server.datatables.DropItemTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.EvaSystemTable;
import l1j.server.server.datatables.FishExpTable;
import l1j.server.server.datatables.ForceItem;
import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.GetBackRestartTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.LightSpawnTable;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.MobGroupTable;
import l1j.server.server.datatables.ModelSpawnTable;
import l1j.server.server.datatables.MonsterBalance;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.datatables.NpcChatTable;
import l1j.server.server.datatables.NpcShopAdenTypeTable;
import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.PetTypeTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ResolventTable;
import l1j.server.server.datatables.ShopNpcSpawnTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.SkillsDmgTable;
import l1j.server.server.datatables.SkillsProbabilityDetailTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SoldierTable;
import l1j.server.server.datatables.SpawnBossTable;
import l1j.server.server.datatables.SpawnTable;
import l1j.server.server.datatables.SpecialMapTable;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.datatables.UBSpawnTable;
import l1j.server.server.datatables.WantedTeleportTable;
import l1j.server.server.datatables.WeaponMagicNpcBalanceTable;
import l1j.server.server.datatables.WeaponMagicPcBalanceTable;
import l1j.server.server.datatables.WeaponNpcBalanceTable;
import l1j.server.server.datatables.WeaponPcBalanceTable;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.DungeonResetScheduler;
import l1j.server.server.model.ElementalStoneGenerator;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1NpcRegenerationTimer;
import l1j.server.server.model.L1Sys;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.GameTimeClock;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.utils.L1SpawnUtil;
import l1j.server.server.utils.LotationStatics;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.TimeController.ItemDeleteController;
import server.system.autoshop.AutoShopManager;
import server.threads.pc.AutoSaveThread;
import server.threads.pc.CharacterQuickCheckThread;
import server.threads.pc.DollObserverThread;
import server.threads.pc.ExpMonitorController;
import server.threads.pc.HpMpRegenThread;
import server.threads.pc.ItemLimitTimeThread;
import server.threads.pc.PremiumAinThread;
import server.threads.pc.SpeedHackThread;
public class GameServer/* extends Thread*/ {
	private static Logger _log = Logger.getLogger(GameServer.class.getName());
	private static GameServer _instance;
	private boolean serverExit;
	private GameServer() {
		//super("GameServer");
	}

	public static GameServer getInstance() {
		if (_instance == null) {
			synchronized (GameServer.class) {
				if(_instance == null)
					_instance = new GameServer();
			}
		}
		return _instance;
	}

	public void setServerExit(boolean flag){
		serverExit = flag;
	}
	public boolean getServerExit(){
		return serverExit;
	}
	public void initialize() throws Exception {
		serverExit = false;
		showGameServerSetting();

		ObjectIdFactory.createInstance();
		L1WorldMap.createInstance();  // FIXME 부실하다
		initTime();
		//MJCTSpellLoader.getInstance();
		SpecialMapTable.getInstance();
		AccessoryEnchantInformationTable.getInstance();
		AccessoryEnchantInformationTable1.getInstance();
		NpcStatusDamageInfo.do_load();
		ExchangeItemLoader.getInstance();
		//CharacterBalance.getInstance();
		DollBonusEventLoader.getInstance();
		BoardAdenaTable.getInstance(); //유저 거래 게시판
		AdenaBoardController.getInstance();
		CharacterTable.getInstance().loadAllCharName(); // FIXME 굳이 메모리에 띄워둘 필요가 있나
		CharacterTable.clearOnlineStatus();
		WantedTeleportTable.getInstance();
		//CharacterTable.clear로테이션();
		/**mjSpr관련*/
		SpriteInformationLoader.getInstance().loadSpriteInformation();
		MJSprBoundary.do_load();
		// TODO change following code to be more effective
		DominanceDataLoader.getInstance();
		DominanceTimeController.getInstance();
		// UB타임 콘트롤러
		//UbTimeController.getInstance();
		Robot_Location.setRLOC();
		Robot_Fish.getInstance();
		// 전쟁 타임 콘트롤러
		WarTimeController.getInstance();
		// 정령의 돌 타임 컨트롤러
		if (Config.ELEMENTAL_STONE_AMOUNT > 0) {
			ElementalStoneGenerator.getInstance();
		}
		// 배틀존
		if (Config.배틀존작동유무) {
			BattleZone battleZone = BattleZone.getInstance();
			GeneralThreadPool.getInstance().execute(battleZone);
		}
		//제작테이블
				CraftListTable.getInstance();
				//스킬데미지 테이블
				SkillsDmgTable.getInstance();
				//헌터북
				// huntingbookTable.getInstance();
		// 홈 타운
		HomeTownController.getInstance();

		// 아지트 경매 타임 콘트롤러
		AuctionTimeController.getInstance();

		// 아지트 세금 타임 콘트롤러
		HouseTaxTimeController.getInstance();

		// 생존의외침 타임 컨트롤러
		//LiveTimeController.getInstance();

		AutoDollController.getInstance();
		
		// 낚시 타임 콘트롤러
		FishingTimeController fishingTimeController = FishingTimeController.getInstance();
		GeneralThreadPool.getInstance().scheduleAtFixedRate(fishingTimeController, 0, FishingTimeController.SLEEP_TIME); // #

		InvenTimeController invenTimeController = InvenTimeController.getInstance();
		GeneralThreadPool.getInstance().scheduleAtFixedRate(invenTimeController, 0, InvenTimeController.SLEEP_TIME);
		
		// 고무 타임 컨트롤러
	//	GomuSystem.getInstance().start();
		
		// 고무 타임 컨트롤러
//		GomuSystem1.getInstance().start();
		
		NpcChatTimeController.getInstance();

		NpcTable.getInstance();
		ShopNpcSpawnTable.getInstance();
		//L1DeleteItemOnGround deleteitem = new L1DeleteItemOnGround();
		//deleteitem.initialize();

		if (!NpcTable.getInstance().isInitialized()) {
			throw new Exception("[GameServer] Could not initialize the npc table");
		}

		SpawnTable.getInstance();
		MobGroupTable.getInstance();
		SkillsTable.getInstance();
		PolyTable.getInstance();
		ItemTable.getInstance();
		DropTable.getInstance();
		DropEventTable.getInstance();
		DropItemTable.getInstance();
		ShopTable.getInstance();
		NPCTalkDataTable.getInstance();
		L1World.getInstance();
		L1WorldTraps.getInstance();
		Dungeon.getInstance();
		NpcSpawnTable.getInstance();
		IpTable.getInstance();
		MapsTable.getInstance();
		UBSpawnTable.getInstance();
		PetTable.getInstance();
		ClanTable.getInstance();
		CastleTable.getInstance();
		L1CastleLocation.setCastleTaxRate(); // CastleTable 초기화 다음 아니면 안 된다
		GetBackRestartTable.getInstance();
		DoorSpawnTable.getInstance();
		GeneralThreadPool.getInstance();
		L1NpcRegenerationTimer.getInstance();	
//		ChatLogTable.getInstance();
//		WeaponSkillTable.getInstance();
		NpcActionTable.load();
		GMCommandsConfig.load();
		Getback.loadGetBack();
		PetTypeTable.load();
		L1TreasureBox.load();
		SprTable.getInstance();
		//RaceTable.getInstance();
		ResolventTable.getInstance();
		ForceItem.getInstance();
		FurnitureSpawnTable.getInstance();
		NpcChatTable.getInstance();
//		CrockController.getInstance().start();
		SoldierTable.getInstance();
//		L1BugBearRace.getInstance();
		WeaponPcBalanceTable.getInstance(); // 무기데미지 pc
		WeaponNpcBalanceTable.getInstance(); // 무기데미지 npc
		AccessoryBalanceTable.getInstance();
		WeaponMagicPcBalanceTable.getInstance();
		WeaponMagicNpcBalanceTable.getInstance();
		SpawnBossTable.getInstance();
		BossSpawnTimeController.getInstance();
		NewNpcSpawnTable.getInstance();
		NpcTimeController.getInstance();
		// 라스타바드 던전
		/*LastabardController.start();
		BiscuitLastabardController biscuitLastabardController = BiscuitLastabardController.getInstance();
	    GeneralThreadPool.getInstance().execute(biscuitLastabardController);*/
	    MonsterBalance.getInstance();
	    FishExpTable.getInstance();
	    
		// 던전 타이머
		DungeonTimer dungeontimer = DungeonTimer.getInstance();
		GeneralThreadPool.getInstance().scheduleAtFixedRate(dungeontimer, 0, DungeonTimer.SleepTime);
	 		
	    /** 시장갱신 및 시장스타트 컨트롤러 추가 */
	    boolean power = ShopNpcSystem.getInstance().isPower();
	    if(!power) {
	    	ShopNpcSystem.getInstance().npcShopStart();
	    }
		/*BiscuitBuffTimeController biscuitBuffTimeController = BiscuitBuffTimeController.getInstance();
		GeneralThreadPool.getInstance().execute(biscuitBuffTimeController);*/
	    
	    LoopTimer looptimer = LoopTimer.getInstance();
		GeneralThreadPool.getInstance().scheduleAtFixedRate(looptimer, 0, LoopTimer.SLEEP_TIME); 
		DungeonResetScheduler.start();
		//GeneralThreadPool.getInstance().execute(GludioDungeonController.getInstance());
		//GeneralThreadPool.getInstance().execute(ForgottenIsleController.getInstance());
		//GeneralThreadPool.getInstance().execute(OmanTopFloorController.getInstance());
		GeneralThreadPool.getInstance().execute(TebeController.getInstance());
		//GeneralThreadPool.getInstance().execute(IvoryTowerController.getInstance());
	    GeneralThreadPool.getInstance().execute(DungeonQuitController.getInstance());
	    CharactersMrTable.getInstance();
	    CharactersAcTable.getInstance();
	    CharactersReducTable.getInstance();
	    PremiumTimeController premiumTimeController = PremiumTimeController.getInstance();
		GeneralThreadPool.getInstance().scheduleAtFixedRate(premiumTimeController, 0, PremiumTimeController.SLEEP_TIME); // #
		
		PremiumTimeController2 premiumTimeController2 = PremiumTimeController2.getInstance();
		GeneralThreadPool.getInstance().scheduleAtFixedRate(premiumTimeController2, 0, PremiumTimeController2.SLEEP_TIME); // #
			
		LotationStatics.getInstance();
		LotationController.getInstance();
		// 유령의집, 데스매치
		//GeneralThreadPool.getInstance().execute(DeathMatch.getInstance());
		//GeneralThreadPool.getInstance().execute(GhostHouse.getInstance());
		//GeneralThreadPool.getInstance().execute(PetRacing.getInstance());
		L1Sys.getInstance();
		L1Sys l1Sys = L1Sys.getInstance();
		GeneralThreadPool.getInstance().execute(l1Sys);
		// 횃불
		LightSpawnTable.getInstance();
		LightTimeController.start();

		// 월드내에 모형 넣기(던전내 횟불 등등)
		ModelSpawnTable.getInstance().ModelInsertWorld();
		
		EventSystemLoader.getInstance();
		EventSystemTimeController.getInstance().Start();
		AutoPotionController.getInstance();
		AutoBuffController.getInstance();
		AutoBuyItemController.getInstance();
		AutoSellItemController.getInstance();
		ArmorBalanceTable.getInstance();
		AutoPotionSystem.getInstance();//220102추가
		// 공성 시간지정 타이머
//		WarSetTime.start();

		// 게임 공지
		//NoticeSystem.start();

		// 시간의 균열
		CrockSystem.getInstance();
		EvaSystemTable.getInstance();
		if (Config.ALT_HALLOWEENEVENT != true) {
			Halloween();
		}
		//보스 한입만 상자
		AddRewardMonTable.getInstance();
		// 버경표 삭제
		//RaceTicket();
//		MapFixKeyTable.getInstance();

//		MiniClient Mini = MiniClient.getInstance();
//		Mini.start();
		//케릭터 자동저장 스케줄러 해당 시간에 맞게 전체 유저를 읽어서 저장시킨다.
		//CharacterAutoSaveController chaSave = new CharacterAutoSaveController(Config.AUTOSAVE_INTERVAL * 1000);
		//chaSave.start();


		//CharacterQuitCheckController quick = new CharacterQuitCheckController(10000);
		//quick.start();
		//케릭터가 가진 인형의 Action을 전송한다.
		//DollobserverController dollAction = new DollobserverController(15000);
		//dollAction.start();

		//HpMpRegenController regen = new HpMpRegenController(1000);
		//regen.start();
	
		AutoSaveThread.getInstance();
		DollObserverThread.getInstance();
		HpMpRegenThread.getInstance();
		SpeedHackThread.getInstance();
		PremiumAinThread.getInstance();
		CharacterQuickCheckThread.getInstance();
		//AutoUpdateThread.getInstance();
		//ExpMonitorThread.getInstance();
//		UserRankingController.getInstance(); //랭킹 시스템
		ExpMonitorController.getInstance();
		AttrEnchantControlTable.getInstance();
		CharacterBalance.getInstance();
		NpcShopAdenTypeTable.getInstance();
		SkillsProbabilityDetailTable.getInstance();
		
		ItemDeleteController idel = new ItemDeleteController(60000);
		idel.start();
		//GCThread.getInstance();
		ItemLimitTimeThread.get();
		
		if (firstHerotype() == 0) {
			spawnStatue(5137, firstHero());
		} else if (firstHerotype() == 1) {
			spawnStatue(5143, firstHero());
		} else if (firstHerotype() == 2) {
			spawnStatue(5156, firstHero());
		} else if (firstHerotype() == 3) {
			spawnStatue(5139, firstHero());
		} else if (firstHerotype() == 4) {
			spawnStatue(5160, firstHero());
		}
		
		// 가비지 컬렉터 실행 (Null) 객체의 해제
		System.out.println("[GameServer] 로딩 완료!");
		System.out.println("=================================================");
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

		//this.start();
	}

	private static int firstHerotype() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int type = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(
				"SELECT type FROM characters " +
				"WHERE level > 1 AND AccessLevel = 0 AND account_name != 'MJBOT' " +
				"ORDER BY Exp DESC LIMIT 1");
			rs = pstm.executeQuery();
			while (rs.next()) {
				type = rs.getInt(1);
			}
		} catch (SQLException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return type;
	}
	
	private void spawnStatue(int classid, String name) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE npc SET nameid = ?, gfxid = ? WHERE npcid = 900009622");
			pstm.setString(1, "[서버 랭킹 1위]^" + "<<"+name+">>");
			pstm.setInt(2, classid);
			pstm.executeUpdate();
		} catch (SQLException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		NpcTable.reload();
		L1SpawnUtil.spawn2(Config.랭킹동상x좌표, Config.랭킹동상y좌표, (short) 4, 900009622, 0, 0, 0); 
	}
	
	public static String firstHero() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String nameid = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(
				"SELECT char_name, Sex, Class FROM characters " +
				"WHERE level > 1 AND AccessLevel = 0 AND account_name != 'MJBOT' " +
				"ORDER BY Exp DESC LIMIT 1");
			rs = pstm.executeQuery();
			while (rs.next()) {
				nameid = rs.getString("char_name");
			}
		} catch (SQLException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return nameid;
	}
	
	public void RoobotSpawn() {
		GMCommands.huntBot = true;
		Robot_Hunt.getInstance().start_spawn();
	}
	
	private void initTime() {
		GameTimeClock.init(); // 게임 시간 시계
		RealTimeClock.init(); // 현재 시간 시계
	}

	private void showGameServerSetting() {
		System.out.println("[GameServer] Starting GameServer"); // (" + serverSocket.toString() + ")");

		double rateXp			= Config.RATE_XP;
		double rateLawful 		= Config.RATE_LAWFUL;
		double rateKarma 		= Config.RATE_KARMA;
		double rateDropItems 	= Config.RATE_DROP_ITEMS;
		double rateDropAdena 	= Config.RATE_DROP_ADENA;

		System.out.println("[GameServer] Exp: x" + rateXp + " / Lawful: x" + rateLawful + " / Adena: x" + rateDropAdena);
		System.out.println("[GameServer] Karma: x" + rateKarma + " / Item: x" + rateDropItems);
		System.out.println("[GameServer] Chatting Level: " + Config.GLOBAL_CHAT_LEVEL);
		System.out.println("[GameServer] Maximum User: " + Config.MAX_ONLINE_USERS + "인");

		System.out.print("[GameServer] PvP mode: ");
		if (Config.ALT_NONPVP) 	System.out.println("On");
		else 					System.out.println("Off");
		System.out.println("=================================================");
	}

	/**
	 * 온라인중의 플레이어 모두에 대해서 kick, 캐릭터 정보의 보존을 한다.
	 */
	public void disconnectAllCharacters() {
		Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();
		// 모든 캐릭터 끊기
		for (L1PcInstance pc : players) {
			if (!(pc instanceof L1RobotInstance)) {
				if (!AutoShopManager.getInstance().isExistAutoShop(pc.getId())) {
					try {
						pc.save();
						pc.saveInventory();
						pc.getNetConnection().setActiveChar(null);
						pc.getNetConnection().kick();
						pc.logout();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else{
					try {
						pc.save();
						pc.saveInventory();
						pc.logout();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public int saveAllCharInfo() {
		// exception 발생하면 -1 리턴, 아니면 저장한 인원 수 리턴
		int cnt = 0;
		try {
			for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){
				cnt++;
				pc.save();
				pc.saveInventory();
			}
		}
		catch(Exception e) { return -1; }

		return cnt;
	}

	/**
	 * 온라인중의 플레이어에 대해서 kick , 캐릭터 정보의 보존을 한다.
	 */
	public void disconnectChar(String name) {
		L1PcInstance pc = L1World.getInstance().getPlayer(name);
		L1PcInstance Player = pc;
		synchronized (pc) {
			pc.getNetConnection().kick();
			Player.logout();
			pc.getNetConnection().quitGame(Player);
		}
	}
	
	public static void disconnectChar(L1PcInstance pc) {
		if (pc.getNetConnection() != null) {
			pc.getNetConnection().kick();
		}
		pc.logout();
	}

	private class ServerShutdownThread extends Thread {
		private final int _secondsCount;

		public ServerShutdownThread(int secondsCount) {
			_secondsCount = secondsCount;
		}

		@Override
		public void run() {
			L1World world = L1World.getInstance();
			try {
				int secondsCount = _secondsCount;
				System.out.println("[GameServer] 잠시 후, 서버를 종료 합니다.");
				System.out.println("[GameServer] 안전한 장소에서 로그아웃 해 주세요.");
				world.broadcastServerMessage("잠시 후, 서버를 종료 합니다.");
				world.broadcastServerMessage("안전한 장소에서 로그아웃 해 주세요.");
				while (0 < secondsCount) {
					if (secondsCount <= 30) {
						System.out.println("[GameServer] 게임이 " + secondsCount + "초 후에 종료 됩니다. 게임을 중단해 주세요.");
						world.broadcastServerMessage("게임이 " + secondsCount + "초 후에 종료 됩니다. 게임을 중단해 주세요.");
					} else {
						if (secondsCount % 60 == 0) {
							System.out.println("[GameServer] 게임이 " + secondsCount / 60 + "분 후에 종료 됩니다.");
							world.broadcastServerMessage("게임이 " + secondsCount / 60 + "분 후에 종료 됩니다.");
						}
					}
					Thread.sleep(1000);
					secondsCount--;
				}
				shutdown();
			} catch (InterruptedException e) {
				System.out.println("[GameServer] 서버 종료가 중단되었습니다. 서버는 정상 가동중입니다.");
				world.broadcastServerMessage("서버 종료가 중단되었습니다. 서버는 정상 가동중입니다.");
				return;
			}
		}
	}

	private ServerShutdownThread _shutdownThread = null;

	public synchronized void shutdownWithCountdown(int secondsCount) {
		if (_shutdownThread != null) {
			// 이미 슛다운 요구를 하고 있다
			// TODO 에러 통지가 필요할지도 모른다
			return;
		}
		_shutdownThread = new ServerShutdownThread(secondsCount);
		GeneralThreadPool.getInstance().execute(_shutdownThread);
	}

	public void shutdown() {
		disconnectAllCharacters();
		try {
			LoggerInstance.getInstance().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public synchronized void abortShutdown() {
		if (_shutdownThread == null) {
			// 슛다운 요구를 하지 않았다
			// TODO 에러 통지가 필요할지도 모른다
			return;
		}

		_shutdownThread.interrupt();
		_shutdownThread = null;
	}

	public void Halloween() {
		Connection con = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm1 = null;
		PreparedStatement pstm2 = null;
		PreparedStatement pstm3 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_items WHERE item_id IN (20380, 21060, 256) AND enchantlvl < 8");
			pstm1 = con.prepareStatement("DELETE FROM character_elf_warehouse WHERE item_id IN (20380, 21060, 256) AND enchantlvl < 8");
			pstm2 = con.prepareStatement("DELETE FROM clan_warehouse WHERE item_id IN (20380, 21060, 256) AND enchantlvl < 8");
			pstm3 = con.prepareStatement("DELETE FROM character_warehouse WHERE item_id IN (20380, 21060, 256) AND enchantlvl < 8");
			pstm3.execute();
			pstm2.execute();
			pstm1.execute();
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(pstm3);
			SQLUtil.close(con);
		}
	}

	public void RaceTicket() {
		Connection con = null;
		PreparedStatement pstm = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_items WHERE item_id >= 8000000");
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
