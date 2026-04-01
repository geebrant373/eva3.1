package l1j.server.server.clientpackets;

import static l1j.server.server.model.skill.L1SkillId.ADDITIONAL_FIRE;
import static l1j.server.server.model.skill.L1SkillId.BERSERKERS;
import static l1j.server.server.model.skill.L1SkillId.CLEAR_MIND;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_0_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_0_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_14_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_14_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_16_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_16_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_22_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_22_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_6_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_6_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_8_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_8_S;
import static l1j.server.server.model.skill.L1SkillId.DECAY_POTION;
import static l1j.server.server.model.skill.L1SkillId.DECREASE_WEIGHT;
import static l1j.server.server.model.skill.L1SkillId.DISEASE;
import static l1j.server.server.model.skill.L1SkillId.DRAGON_EMERALD_NO;
import static l1j.server.server.model.skill.L1SkillId.DRAGON_EMERALD_YES;
import static l1j.server.server.model.skill.L1SkillId.DRESS_EVASION;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_FALL_DOWN;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_FIRE;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_PROTECTION;
import static l1j.server.server.model.skill.L1SkillId.ERASE_MAGIC;
import static l1j.server.server.model.skill.L1SkillId.EXP_POTION;
import static l1j.server.server.model.skill.L1SkillId.FEATHER_BUFF_A;
import static l1j.server.server.model.skill.L1SkillId.FEATHER_BUFF_B;
import static l1j.server.server.model.skill.L1SkillId.FEATHER_BUFF_C;
import static l1j.server.server.model.skill.L1SkillId.FEATHER_BUFF_D;
import static l1j.server.server.model.skill.L1SkillId.NATURES_TOUCH;
import static l1j.server.server.model.skill.L1SkillId.POLLUTE_WATER;
import static l1j.server.server.model.skill.L1SkillId.RESIST_ELEMENTAL;
import static l1j.server.server.model.skill.L1SkillId.RESIST_MAGIC;
import static l1j.server.server.model.skill.L1SkillId.SHAPE_CHANGE;
import static l1j.server.server.model.skill.L1SkillId.SILENCE;
import static l1j.server.server.model.skill.L1SkillId.SOUL_OF_FLAME;
import static l1j.server.server.model.skill.L1SkillId.SPECIAL_COOKING;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BLUE_POTION;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BLUE_POTION2;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BLUE_POTION3;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BRAVE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL2;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL3;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CHAT_PROHIBITED;
import static l1j.server.server.model.skill.L1SkillId.STATUS_COMA_3;
import static l1j.server.server.model.skill.L1SkillId.STATUS_COMA_5;
import static l1j.server.server.model.skill.L1SkillId.STATUS_DRAGONMAAN_BIRTH;
import static l1j.server.server.model.skill.L1SkillId.STATUS_DRAGONMAAN_EARTH;
import static l1j.server.server.model.skill.L1SkillId.STATUS_DRAGONMAAN_FIRE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_DRAGONMAAN_LIFE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_DRAGONMAAN_SHAPE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_DRAGONMAAN_WATER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_DRAGONMAAN_WIND;
import static l1j.server.server.model.skill.L1SkillId.STATUS_ELFBRAVE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_EXP_UP_II;
import static l1j.server.server.model.skill.L1SkillId.STATUS_FRUIT;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HASTE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_TIKAL_BOSSDIE;
import static l1j.server.server.model.skill.L1SkillId.STRIKER_GALE;
import static l1j.server.server.model.skill.L1SkillId.STR_STATUS_EFFECT_BM_CHINA_ITEM1;
import static l1j.server.server.model.skill.L1SkillId.STR_STATUS_EFFECT_BM_CHINA_ITEM2;
import static l1j.server.server.model.skill.L1SkillId.VENOM_RESIST;
import static l1j.server.server.model.skill.L1SkillId.WEAKNESS;
import static l1j.server.server.model.skill.L1SkillId.WIND_SHACKLE;
import static l1j.server.server.model.skill.L1SkillId.자동사냥시간;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.SpecialEventHandler;
import l1j.server.GameSystem.CrockSystem;
import l1j.server.server.ActionCodes;
import l1j.server.server.SkillCheck;
import l1j.server.server.TimeController.LiveTimeController;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.datatables.CharacterSlotItemTable;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.GetBackRestartTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Cooking;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_Bookmarks;
import l1j.server.server.serverpackets.S_CharacterConfig;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ElfIcon;
import l1j.server.server.serverpackets.S_Emblem;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_InvList;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_MapID;
import l1j.server.server.serverpackets.S_OwnCharPack;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_UnityIcon;
import l1j.server.server.serverpackets.S_Unknown1;
import l1j.server.server.serverpackets.S_War;
import l1j.server.server.serverpackets.S_Weather;
import l1j.server.server.serverpackets.S_bonusstats;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1GetBackRestart;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.SQLUtil;
import manager.LinAllManager;
import server.GameServer;
import server.LineageClient;
import server.system.autoshop.AutoShop;
import server.system.autoshop.AutoShopManager;

public class C_SelectCharacter extends ClientBasePacket {
	private static final String C_LOGIN_TO_SERVER = "[C] C_LoginToServer";
	private static Logger _log = Logger.getLogger(C_SelectCharacter.class.getName());

	private static void print_message(LineageClient clnt, String message, boolean is_kick) throws Exception {
		System.out.println("─────────────────────────────────");
		System.out.println(message);
		System.out.println("─────────────────────────────────");
		if (is_kick) {
			clnt.kick();
			clnt.close();
		}
	}

	private int[] omanLocX = { 32781, 32818, 32818, 32818 };
	private int[] omanLocY = { 32781, 32781, 32818, 32781 };

	Random ran = new Random();

	public C_SelectCharacter(byte abyte0[], LineageClient client) throws FileNotFoundException, Exception {
		super(abyte0);
		String charName = readS();
		String login = client.getAccountName();

		if (client.getActiveChar() != null) { // restart 상태가 아니란 소린가?
			client.kick();
			client.close();
			return;
		}

		AutoShopManager shopManager = AutoShopManager.getInstance();
		AutoShop shopPlayer = shopManager.getShopPlayer(charName);
		if (shopPlayer != null) {
			shopPlayer.logout();
			shopManager.remove(shopPlayer);
			shopPlayer = null;
		}

		L1PcInstance pc = L1PcInstance.load(charName);
		long sysTime = System.currentTimeMillis();

		/** 2캐릭 버그 방지 Start */
		L1PcInstance OtherPc = L1World.getInstance().getPlayer(charName);
		if (OtherPc != null) {
			boolean isPrivateShop = OtherPc.isPrivateShop();
			boolean isAutoclanjoin = OtherPc.isAutoClanjoin();
			GameServer.disconnectChar(OtherPc);
			OtherPc = null;

			if (isPrivateShop == false && isAutoclanjoin==false) {
				print_message(client, String.format("동일 ID의 중복 접속이므로 (%s:%s)의 접속을 강제 종료합니다.", client.getIp(), charName),
						true);
				return;
			}
		}
		serchPc(pc, client);
		Collection<L1PcInstance> pcs = L1World.getInstance().getAllPlayers();
		for (L1PcInstance bugpc : pcs) {
			if (bugpc.getAccountName().equals(client.getAccountName())) {
				if ((!bugpc.isPrivateShop() && !bugpc.isAutoClanjoin()) || bugpc.getNetConnection() != null) {
					print_message(client, String.format("동일 Account의 중복 접속이므로 (%s)의 접속을 강제 종료합니다.", client.getIp()),
							true);
					GameServer.disconnectChar(bugpc);
				}
			}
		}
		pcs = null;
		/** 2캐릭 버그 방지 End */

		String accountName = client.getAccountName();
		if (pc == null || !accountName.equals(pc.getAccountName())) {
			_log.info("로그인 요청 무효: char:" + charName + " account:" + accountName + " host:" + client.getHostname());
			client.kick();
			client.close();
			return;
		}
		pc.setAccount(client.getAccount());
		pc.setAccountName(client.getAccount().getName());
		System.out.println("캐릭터 접속: char:" + charName + " account:" + accountName + " host:" + client.getHostname());

		int currentHpAtLoad = pc.getCurrentHp();
		int currentMpAtLoad = pc.getCurrentMp();

		/** 로그파일저장 **/
		LoggerInstance.getInstance()
				.addConnection("접속 캐릭=" + charName + "	계정=" + login + "	IP=" + client.getHostname());
		pc.clearSkillMastery();
		pc.setOnlineStatus(1);
		CharacterTable.updateOnlineStatus(pc);

		L1World.getInstance().storeObject(pc);

		pc.setNetConnection(client);

		client.setActiveChar(pc);

		// if(pc.isGm())ChatMonitorChannel.getInstance().join(pc);

		/*
		 * ############### Point System ############### int at =
		 * client.getAccount().getAccountTime() * 60000; if(at > 0) { long la =
		 * client.getAccount().getLastActive().getTime(); long sum = la + at;
		 * pc.setLimitPointTime(sum/1000); pc.setPointUser(true); } ###############
		 * Point System ###############
		 */

		pc.sendPackets(new S_Unknown1());
		items(pc);
		bookmarks(pc);
		skills(pc);
		// restart처가 getback_restart 테이블로 지정되고 있으면(자) 이동시킨다
		GetBackRestartTable gbrTable = GetBackRestartTable.getInstance();
		L1GetBackRestart[] gbrList = gbrTable.getGetBackRestartTableList();
		for (L1GetBackRestart gbr : gbrList) {
			if (pc.getMapId() == gbr.getArea()) {
				pc.setX(gbr.getLocX());
				pc.setY(gbr.getLocY());
				pc.setMap(gbr.getMapId());
				break;
			}
		}

		L1Map map = L1WorldMap.getInstance().getMap(pc.getMapId());
		// altsettings.properties로 GetBack가 true라면 거리에 이동시킨다
		int tile = map.getTile(pc.getX(), pc.getY());
		if (Config.GET_BACK || !map.isInMap(pc.getX(), pc.getY()) || tile == 0 || tile == 4 || tile == 12) {
			int[] loc = Getback.GetBack_Location(pc, true);
			pc.setX(loc[0]);
			pc.setY(loc[1]);
			pc.setMap((short) loc[2]);
		}

		if (pc.getMapId() == 101 || pc.getMapId() == 72 || pc.getMapId() == 508) {// 오만의 탑 귀환 설정일경우 1층으로 세팅되어있음
			int rnd = ran.nextInt(omanLocX.length);
			pc.setX(omanLocX[rnd]);
			pc.setY(omanLocY[rnd]);
			pc.setMap((short) 101);
		}

		for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
			if (player.isGm()) {
				player.sendPackets(new S_SystemMessage("\\fT접속>>캐릭명: [" + pc.getName() + "] 계정: ["
						+ client.getAccountName() + "] IP:" + client.getIp()));
			}

		}
		// 전쟁중의 기내에 있었을 경우, 성주 혈맹이 아닌 경우는 귀환시킨다.
		int castle_id = L1CastleLocation.getCastleIdByArea(pc);
		if (0 < castle_id) {
			if (WarTimeController.getInstance().isNowWar(castle_id)) {
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if (clan != null) {
					if (clan.getCastleId() != castle_id) {
						// 성주 크란은 아니다
						int[] loc = new int[3];
						loc = L1CastleLocation.getGetBackLoc(castle_id);
						pc.setX(loc[0]);
						pc.setY(loc[1]);
						pc.setMap((short) loc[2]);
					}
				} else {
					// 크란에 소속해 없는 경우는 귀환
					int[] loc = new int[3];
					loc = L1CastleLocation.getGetBackLoc(castle_id);
					pc.setX(loc[0]);
					pc.setY(loc[1]);
					pc.setMap((short) loc[2]);
				}
			}
		}

		LinAllManager.getInstance().LogConnectAppend(pc.getName(), client.getHostname());

		L1World.getInstance().addVisibleObject(pc);

		pc.beginGameTimeCarrier();
		pc.load_private_probability();
		S_OwnCharStatus s_owncharstatus = new S_OwnCharStatus(pc);
		pc.sendPackets(s_owncharstatus);

		S_MapID s_mapid = new S_MapID(pc.getMapId(), pc.getMap().isUnderwater());
		pc.sendPackets(s_mapid);

		S_OwnCharPack s_owncharpack = new S_OwnCharPack(pc);
		pc.sendPackets(s_owncharpack);

		buff(client, pc);
		vipSystem(pc); // vip 버프
		runbuff(pc);
		MenberShip(pc);

		RankingSystem(pc);
		pc.sendPackets(new S_Weather(L1World.getInstance().getWeather()));

		// 본섭은 위 아래 패킷 사이에 S_OPCODE_CASTLEMASTER 패킷이 온다 (총 9개)
		pc.sendCastleMaster();

		pc.sendPackets(new S_SPMR(pc));

		pc.sendVisualEffectAtLogin(); // 독, 수중 등의 시각 효과를 표시
		pc.sendPackets(new S_ReturnedStat(pc, 4));

		if (pc.getLevel() >= 49)
			hasadbuff(pc);

		pc.getLight().turnOnOffLight();

		// 존재버그 관련 추가
		L1PcInstance jonje = L1World.getInstance().getPlayer(pc.getName());
		if (jonje == null) {
			pc.sendPackets(new S_SystemMessage("존재버그 강제종료! 재접속하세요"));
			client.kick();
			return;
		}
		if (pc.getCurrentHp() > 0) {
			pc.setDead(false);
			pc.setActionStatus(0);
		} else {
			pc.setDead(true);
			pc.setActionStatus(ActionCodes.ACTION_Die);
		}

		if (pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getAbility().getBonusAbility()
				&& pc.getAbility().getAmount() < 150) {
			pc.sendPackets(new S_bonusstats(pc.getId(), 1));
		}
		CharacterSlotItemTable.getInstance().selectCharSlot(pc, 0); // 1번슬롯
		CharacterSlotItemTable.getInstance().selectCharSlot(pc, 1); // 2번슬롯
		pc.sendPackets(new S_OwnCharStatus2(pc));

		if (pc.getReturnStat() != 0) {
			SpecialEventHandler.getInstance().ReturnStats(pc);
		}

		pc.sendPackets(new S_PacketBox(pc, S_PacketBox.KARMA));
		// pc.sendPackets(new S_PacketBox(S_PacketBox.LOGIN_UNKNOWN1)); // 버프쯤 되는듯?
		// pc.sendPackets(new S_PacketBox(S_PacketBox.LOGIN_UNKNOWN2));

		if (Config.CHARACTER_CONFIG_IN_SERVER_SIDE) {
			pc.sendPackets(new S_CharacterConfig(pc.getId()));
		}

		// 생존의외침
		pc.setLive(0);
		if (pc.get_food() == 225) {
			LiveTimeController.getInstance().addMember(pc);
		}

		serchSummon(pc);
		
		WarTimeController.getInstance().checkCastleWar(pc);

		if (pc.getClanid() != 0) { // 크란 소속중
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			L1World.getInstance().broadcastPacketToAll(new S_Emblem(pc.getClan().getEmblemId()));
			if (clan != null) {
				if (pc.getClanid() == clan.getClanId() && // 크란을 해산해, 재차, 동명의 크란이 창설되었을 때의 대책
						pc.getClanname().toLowerCase().equals(clan.getClanName().toLowerCase())) {
					for (L1PcInstance clanMember : clan.getOnlineClanMember()) {
						if (clanMember.getId() != pc.getId()) {
							// 지금, 혈맹원의%0%s가 게임에 접속했습니다.
							clanMember.sendPackets(new S_ServerMessage(843, pc.getName()));
						}
					}

					// 전전쟁 리스트를 취득
					for (L1War war : L1World.getInstance().getWarList()) {
						boolean ret = war.CheckClanInWar(pc.getClanname());
						if (ret) { // 전쟁에 참가중
							String enemy_clan_name = war.GetEnemyClanName(pc.getClanname());
							if (enemy_clan_name != null) {
								// 당신의 혈맹이 현재_혈맹과 교전중입니다.
								pc.sendPackets(new S_War(8, pc.getClanname(), enemy_clan_name));
							}
							break;
						}
					}
				} else {
					pc.setClanid(0);
					pc.setClanname("");
					pc.setClanRank(0);
					pc.save(); // DB에 캐릭터 정보를 기입한다
				}
			}
		}

		if (pc.getPartnerId() != 0) { // 결혼중
			L1PcInstance partner = (L1PcInstance) L1World.getInstance().findObject(pc.getPartnerId());
			if (partner != null && partner.getPartnerId() != 0) {
				if (pc.getPartnerId() == partner.getId() && partner.getPartnerId() == pc.getId()) {
					pc.sendPackets(new S_ServerMessage(548)); // 당신의 파트너는 지금 게임중입니다.
					partner.sendPackets(new S_ServerMessage(549)); // 당신의 파트너는 방금 로그인했습니다.
				}
			}
		}
		if (currentHpAtLoad > pc.getCurrentHp()) {
			pc.setCurrentHp(currentHpAtLoad);
		}
		if (currentMpAtLoad > pc.getCurrentMp()) {
			pc.setCurrentMp(currentMpAtLoad);
		}
		
		if (pc.getNetConnection().getAccount().getDragonRaid() != null) {
			if (sysTime <= pc.getNetConnection().getAccount().getDragonRaid().getTime()) {
				long BloodTime = pc.getNetConnection().getAccount().getDragonRaid().getTime() - sysTime;
				pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.DRAGONRAID_BUFF);
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.DRAGONRAID_BUFF, (int) BloodTime);
				pc.sendPackets(new S_PacketBox(S_PacketBox.드래곤레이드버프, (int) BloodTime / 1000));
			}
		}
		pc.startObjectAutoUpdate();
		client.CharReStart(false);
		pc.beginExpMonitor();
		pc.save(); // DB에 캐릭터 정보를 기입한다
		
		try {
			if (!pc.get_자동물약리스트().isEmpty()) {
				pc.sendPackets(new S_SystemMessage("자동물약 설정을 불러왔습니다."));
				for (int id : pc.get_자동물약리스트()) {
					L1Item _item = ItemTable.getInstance().getTemplate(id);
					pc.sendPackets(
							new S_SystemMessage("자동물약: " + (_item == null ? "알수없음" : _item.getName()) + "(무한 소모)"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if (!pc.get_자동판매리스트().isEmpty()) {
				pc.sendPackets(new S_SystemMessage("\\f:자동판매 목록이 저장되어 있습니다."));
				pc.sendPackets(new S_SystemMessage("\\f:[시작] 버튼만 누르시면 기존설정대로 실행됩니다."));
			} else {
				pc.sendPackets(new S_SystemMessage("\\f:저장되어 있는 자동판매 목록이 없습니다."));
			}
			for (Iterator<Integer> item = pc.get_자동판매리스트().iterator(); item.hasNext();) {
				int id = ((Integer) item.next()).intValue();
				L1Item _item = ItemTable.getInstance().getTemplate(id);
				pc.sendPackets(
						new S_SystemMessage("\\f:자동판매: " + (_item == null ? "알수없음" : _item.getName()) + "(자동판매)"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		pc.set_자동물약사용(false);
		pc.set_자동버프사용(false);

		pc.sendPackets(new S_OwnCharStatus(pc));

		if (pc.getHellTime() > 0) {
			pc.beginHell(false);
		}

		if (pc.getWanted() > 0) {
			pc.addDmgup(3);
			pc.addBowDmgup(3);
			pc.getAbility().addSp(3);
			pc.sendPackets((ServerBasePacket) new S_SPMR(pc));
			pc.sendPackets(new S_SkillSound(pc.getId(), 8943)); // 별4
		}
		
		if (CrockSystem.getInstance().isContinuationTime())
			pc.sendPackets(new S_ServerMessage(1466));
		pc.setadFeature(1);
		pc.CheckStatus();
		
		// 시작시 특정렙
		if (pc.getLevel() < Config.경험치지급단) {
			pc.addExp((ExpTable.getExpByLevel(Config.경험치지급단) - 1) - pc.getExp()
					+ ((ExpTable.getExpByLevel(Config.경험치지급단) - 1) / 1000));
		}
	}

	private void CheckTwoLogin(L1PcInstance c) {
		for (L1PcInstance target : L1World.getInstance().getAllPlayersToArray()) {
			int count = 0;
			if (c.getId() == target.getId()) {
				count++;
				if (count > 1) {
					c.getNetConnection().kick();
					c.getNetConnection().close();
					target.getNetConnection().kick();
					target.getNetConnection().close();
				}
			}
			if (c.getId() != target.getId()) {
				if (c.getAccountName().equalsIgnoreCase(target.getAccountName())) {
					if (!AutoShopManager.getInstance().isExistAutoShop(target.getId())) {
						c.getNetConnection().kick();
						c.getNetConnection().close();
						target.getNetConnection().kick();
						target.getNetConnection().close();
					} else {
						// 팅궈내도 무인은 안팅겨지므로 ;;
						target.getNetConnection().kick();
						// target.getNetConnection().close();
					}
				}
			}

		}
	}

	private void RankingSystem(L1PcInstance pc) {
		// TODO Auto-generated method stub
		if (pc.isGm()) {
			pc.sendPackets(new S_SkillSound(pc.getId(), 7000)); // 별4
		}
		if (pc.getLevel() >= 51) {
			if (pc.getRankLevel() >= 1 && pc.getRankLevel() <= 5) {
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.RANKING_BUFF_1, 1000 * 60 * 60 * 24 * 2);
				pc.getAC().addAc(-3);
				pc.addMaxHp(200);
				pc.addPVPDamageReduction(2);
				pc.addDmgup(2);
				pc.addBowDmgup(2);
				pc.sendPackets(new S_SkillSound(pc.getId(), 8942)); // 별4
			} else if (pc.getRankLevel() >= 6 && pc.getRankLevel() <= 10) {
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.RANKING_BUFF_2, 1000 * 60 * 60 * 24 * 2);
				pc.getAC().addAc(-2);
				pc.addMaxHp(150);
				pc.addPVPDamageReduction(1);
				pc.addDmgup(1);
				pc.addBowDmgup(1);
				pc.sendPackets(new S_SkillSound(pc.getId(), 8941)); // 별3
			} else if (pc.getRankLevel() >= 11 && pc.getRankLevel() <= 20) {
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.RANKING_BUFF_3, 1000 * 60 * 60 * 24 * 2);
				pc.getAC().addAc(-1);
				pc.addMaxHp(100);
				pc.addPVPDamageReduction(1);
				pc.sendPackets(new S_SkillSound(pc.getId(), 8940)); // 별2
			} else if (pc.getRankLevel() >= 21 && pc.getRankLevel() <= 30) {
				pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.RANKING_BUFF_4, 1000 * 60 * 60 * 24 * 2);
				pc.getAC().addAc(-1);
				pc.addMaxHp(70);
				pc.sendPackets(new S_SkillSound(pc.getId(), 8939)); // 별1
			}
		}
	}

	private void MenberShip(L1PcInstance pc) {
		// TODO Auto-generated method stub
		if (pc.getMemberShip() >= 1) {
			pc.getAC().addAc(-2);
			pc.addMaxHp(30);
			pc.addMaxMp(30);
			pc.sendPackets(new S_SkillSound(pc.getId(), 8948));
		}
	}

	private void runbuff(L1PcInstance pc) {
		List<L1ItemInstance> items = pc.getInventory().getItems();
		for (L1ItemInstance item : items) {
			if (item.getItemId() == 21156 && (item.isEquipped())) {
				pc.sendPackets(new S_SkillSound(pc.getId(), 8944));
			}
		}
	}

	private void vipSystem(L1PcInstance pc) {
		// TODO Auto-generated method stub
		if (pc.getVipLevel() >= 2) {
			pc.getAC().addAc(-3);
			pc.addMaxHp(50);
			pc.addMaxMp(50);
			pc.addDmgup(3);
			pc.addHitup(3);
			pc.addBowDmgup(3);
			pc.getAbility().addSp(2);
			pc.getResistance().addMr(10);
			pc.sendPackets(new S_SkillSound(pc.getId(), 8943));
			pc.sendPackets(new S_OwnCharStatus(pc));
			pc.sendPackets(new S_SPMR(pc));

		}
	}

	private void hasadbuff(L1PcInstance pc) {
		if (pc.getAinHasad() >= 2000000) {
			pc.setAinHasad(2000000);
			pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc.getAinHasad()));
			return;
		}

		int temp = (int) ((System.currentTimeMillis() - pc.getLogOutTime().getTime()) / 900000);
		int sum = pc.getAinHasad() + (temp * 10000);
		if (sum >= 2000000)
			pc.setAinHasad(2000000);
		else
			pc.setAinHasad(sum);

		pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc.getAinHasad()));
	}

	private void items(L1PcInstance pc) {
		// DB로부터 캐릭터와 창고의 아이템을 읽어들인다
		CharacterTable.getInstance().restoreInventory(pc);
		pc.sendPackets(new S_InvList(pc));
	}

	private void bookmarks(L1PcInstance pc) {

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_teleport WHERE char_id=? ORDER BY name ASC");
			pstm.setInt(1, pc.getId());
			rs = pstm.executeQuery();

			L1BookMark bookmark = null;
			while (rs.next()) {
				bookmark = new L1BookMark();
				bookmark.setId(rs.getInt("id"));
				bookmark.setCharId(rs.getInt("char_id"));
				bookmark.setName(rs.getString("name"));
				bookmark.setLocX(rs.getInt("locx"));
				bookmark.setLocY(rs.getInt("locy"));
				bookmark.setMapId(rs.getShort("mapid"));
				bookmark.setRandomX(rs.getShort("randomX"));
				bookmark.setRandomY(rs.getShort("randomY"));
				S_Bookmarks s_bookmarks = new S_Bookmarks(bookmark.getName(), bookmark.getMapId(), bookmark.getId());
				pc.addBookMark(bookmark);
				pc.sendPackets(s_bookmarks);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void skills(L1PcInstance pc) {
		List<Integer> skillIdList = new ArrayList<Integer>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_skills WHERE char_obj_id=?");
			pstm.setInt(1, pc.getId());
			rs = pstm.executeQuery();

			int i = 0;
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
			int lv11 = 0;
			int lv12 = 0;
			int lv13 = 0;
			int lv14 = 0;
			int lv15 = 0;
			int lv16 = 0;
			int lv17 = 0;
			int lv18 = 0;
			int lv19 = 0;
			int lv20 = 0;
			int lv21 = 0;
			int lv22 = 0;
			int lv23 = 0;
			int lv24 = 0;
			int lv25 = 0;
			int lv26 = 0;
			int lv27 = 0;
			int lv28 = 0;
			L1Skills l1skills = null;
			while (rs.next()) {
				int skillId = rs.getInt("skill_id");
				l1skills = SkillsTable.getInstance().getTemplate(skillId);
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
				if (l1skills.getSkillLevel() == 11) {
					lv11 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 12) {
					lv12 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 13) {
					lv13 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 14) {
					lv14 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 15) {
					lv15 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 16) {
					lv16 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 17) {
					lv17 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 18) {
					lv18 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 19) {
					lv19 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 20) {
					lv20 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 21) {
					lv21 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 22) {
					lv22 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 23) {
					lv23 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 24) {
					lv24 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 25) {
					lv25 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 26) {
					lv26 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 27) {
					lv27 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 28) {
					lv28 |= l1skills.getId();
				}

				i = lv1 + lv2 + lv3 + lv4 + lv5 + lv6 + lv7 + lv8 + lv9 + lv10 + lv11 + lv12 + lv13 + lv14 + lv15 + lv16
						+ lv17 + lv18 + lv19 + lv20 + lv21 + lv22 + lv23 + lv24 + lv25 + lv26 + lv27 + lv28;

				pc.setSkillMastery(skillId);
				skillIdList.add(skillId);
			}
			if (i > 0) {
				pc.sendPackets(new S_AddSkill(lv1, lv2, lv3, lv4, lv5, lv6, lv7, lv8, lv9, lv10, lv11, lv12, lv13, lv14,
						lv15, lv16, lv17, lv18, lv19, lv20, lv21, lv22, lv23, lv24, lv25, lv26, lv27, lv28));
			}
			SkillCheck.getInstance().AddSkill(pc.getId(), skillIdList);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void serchSummon(L1PcInstance pc) {
		for (L1SummonInstance summon : L1World.getInstance().getAllSummons()) {
			if (summon.getMaster().getId() == pc.getId()) {
				summon.setMaster(pc);
				pc.addPet(summon);
				for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(summon)) {
					visiblePc.sendPackets(new S_SummonPack(summon, visiblePc));
				}
			}
		}
	}

	private void buff(LineageClient clientthread, L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_buff WHERE char_obj_id=?");
			pstm.setInt(1, pc.getId());
			rs = pstm.executeQuery();
			int icon[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0 };

			while (rs.next()) {
				int skillid = rs.getInt("skill_id");
				int remaining_time = rs.getInt("remaining_time");

				if (skillid >= COOKING_1_0_N && skillid <= COOKING_1_6_N
						|| skillid >= COOKING_1_8_N && skillid <= COOKING_1_14_N
						|| skillid >= COOKING_1_16_N && skillid <= COOKING_1_22_N
						|| skillid >= COOKING_1_0_S && skillid <= COOKING_1_6_S
						|| skillid >= COOKING_1_8_S && skillid <= COOKING_1_14_S
						|| skillid >= COOKING_1_16_S && skillid <= COOKING_1_22_S) { // 요리(디저트는 제외하다)
					L1Cooking.eatCooking(pc, skillid, remaining_time);
					continue;
				}

				int time = (int) ((System.currentTimeMillis() - pc.getLogOutTime().getTime()) / 1000);

				switch (skillid) {
				case DECREASE_WEIGHT:
					icon[0] = remaining_time / 16;
					break;
				case WEAKNESS:// 위크니스 //
					icon[4] = remaining_time / 4;
					pc.addDmgup(-5);
					pc.addHitup(-1);
					break;
				case BERSERKERS:// 버서커스 //
					icon[7] = remaining_time / 4;
					pc.getAC().addAc(10);
					pc.addDmgup(5);
					pc.addHitup(2);
					break;
				case DISEASE:// 디지즈 //
					icon[5] = remaining_time / 4;
					pc.addDmgup(-6);
					pc.getAC().addAc(12);
					break;
				case SILENCE:
					icon[2] = remaining_time / 4;
					break;
				case SHAPE_CHANGE:
					int poly_id = rs.getInt("poly_id");
					L1PolyMorph.doPoly(pc, poly_id, remaining_time, L1PolyMorph.MORPH_BY_LOGIN);
					continue;

				case DECAY_POTION:
					icon[1] = remaining_time / 4;
					break;
				case VENOM_RESIST:// 베놈 레지스트 //
					icon[3] = remaining_time / 4;
					break;
				case DRESS_EVASION:// 드레스 이베이젼 //
					icon[6] = remaining_time / 4;
					break;
				case RESIST_MAGIC:// 레지스트 매직
					pc.getResistance().addMr(10);
					pc.sendPackets(new S_ElfIcon(remaining_time / 16, 0, 0, 0));
					break;
				case ELEMENTAL_FALL_DOWN:
					icon[12] = remaining_time / 4;
					int playerAttr = pc.getElfAttr();
					int i = -50;
					switch (playerAttr) {
					case 0:
						pc.sendPackets(new S_ServerMessage(79));
						break;
					case 1:
						pc.getResistance().addEarth(i);
						pc.setAddAttrKind(1);
						break;
					case 2:
						pc.getResistance().addFire(i);
						pc.setAddAttrKind(2);
						break;
					case 4:
						pc.getResistance().addWater(i);
						pc.setAddAttrKind(4);
						break;
					case 8:
						pc.getResistance().addWind(i);
						pc.setAddAttrKind(8);
						break;
					default:
						break;
					}
					break;
				case 자동사냥시간:
					break;
				case CLEAR_MIND:// 클리어 마인드
					pc.getAbility().addAddedWis((byte) 3);
					pc.resetBaseMr();
					pc.sendPackets(new S_ElfIcon(0, remaining_time / 16, 0, 0));
					break;
				case RESIST_ELEMENTAL:// 레지스트 엘리멘탈
					pc.getResistance().addAllNaturalResistance(10);
					pc.sendPackets(new S_ElfIcon(0, 0, remaining_time / 16, 0));
					break;
				case ELEMENTAL_PROTECTION:// 프로텍션 프롬 엘리멘탈
					int attr = pc.getElfAttr();
					if (attr == 1) {
						pc.getResistance().addEarth(50);
					} else if (attr == 2) {
						pc.getResistance().addFire(50);
					} else if (attr == 4) {
						pc.getResistance().addWater(50);
					} else if (attr == 8) {
						pc.getResistance().addWind(50);
					}
					pc.sendPackets(new S_ElfIcon(0, 0, 0, remaining_time / 16));
					break;
				case ERASE_MAGIC:
					icon[10] = remaining_time / 4;
					break;
				case NATURES_TOUCH:// 네이쳐스 터치 //
					icon[8] = remaining_time / 4;
					break;
				case WIND_SHACKLE:
					icon[9] = remaining_time / 4;
					break;
				case ELEMENTAL_FIRE:
					icon[13] = remaining_time / 4;
					break;
				case POLLUTE_WATER:// 폴루트 워터 //
					icon[16] = remaining_time / 4;
					break;
				case STRIKER_GALE:// 스트라이커 게일 //
					icon[14] = remaining_time / 4;
					break;
				case SOUL_OF_FLAME:// 소울 오브 프레임 //
					icon[15] = remaining_time / 4;
					break;
				case ADDITIONAL_FIRE:
					icon[11] = remaining_time / 16;
					break;
				case STATUS_BRAVE:
					pc.sendPackets(new S_SkillBrave(pc.getId(), 1, remaining_time));
					Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 1, 0));
					pc.getMoveState().setBraveSpeed(1);
					break;
				case STATUS_HASTE:
					pc.sendPackets(new S_SkillHaste(pc.getId(), 1, remaining_time));
					Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 1, 0));
					pc.getMoveState().setMoveSpeed(1);
					break;
				case STATUS_BLUE_POTION:
				case STATUS_BLUE_POTION2:
				case STATUS_BLUE_POTION3:
					pc.sendPackets(new S_SkillIconGFX(34, remaining_time));
					break;
				case STATUS_ELFBRAVE:
					pc.sendPackets(new S_SkillBrave(pc.getId(), 3, remaining_time));
					Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 3, 0));
					pc.getMoveState().setBraveSpeed(1);
					break;
				case STATUS_CHAT_PROHIBITED:
					pc.sendPackets(new S_SkillIconGFX(36, remaining_time));
					break;
				case STATUS_TIKAL_BOSSDIE:
					icon[20] = (remaining_time + 8) / 16;
					new L1SkillUse().handleCommands(clientthread.getActiveChar(), skillid, pc.getId(), pc.getX(),
							pc.getY(), null, remaining_time, L1SkillUse.TYPE_LOGIN);
					break;
				case STATUS_COMA_3:// 코마 3
					icon[31] = (remaining_time + 16) / 32;
					icon[32] = 40;
					new L1SkillUse().handleCommands(clientthread.getActiveChar(), skillid, pc.getId(), pc.getX(),
							pc.getY(), null, remaining_time, L1SkillUse.TYPE_LOGIN);
					break;
				case STATUS_COMA_5:// 코마 5
					icon[31] = (remaining_time + 16) / 32;
					icon[32] = 41;
					new L1SkillUse().handleCommands(clientthread.getActiveChar(), skillid, pc.getId(), pc.getX(),
							pc.getY(), null, remaining_time, L1SkillUse.TYPE_LOGIN);
					break;
				case SPECIAL_COOKING:
					if (pc.getSkillEffectTimerSet().hasSkillEffect(SPECIAL_COOKING)) {
						if (pc.getSkillEffectTimerSet().getSkillEffectTimeSec(SPECIAL_COOKING) < remaining_time) {
							pc.getSkillEffectTimerSet().setSkillEffect(SPECIAL_COOKING, remaining_time * 1000);
						}
					}
					continue;
				case STATUS_CASHSCROLL:// 체력증강주문서 //
					icon[18] = remaining_time / 16;
					pc.addHpr(4);
					pc.addMaxHp(50);
					pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
					if (pc.isInParty()) {
						pc.getParty().updateMiniHP(pc);
					}
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
					break;
				case STATUS_CASHSCROLL2:// 마력증강주문서 //
					icon[18] = remaining_time / 16;
					icon[19] = 1;
					pc.addMpr(4);
					pc.addMaxMp(40);
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
					break;
				case STATUS_CASHSCROLL3:
					icon[18] = remaining_time / 16;
					icon[19] = 2;
					pc.addDmgup(3);
					pc.addHitup(3);
					pc.getAbility().addSp(3);
					pc.sendPackets(new S_SPMR(pc));
					break;
				case STR_STATUS_EFFECT_BM_CHINA_ITEM1:
					pc.getSkillEffectTimerSet().setSkillEffect(7101, remaining_time * 1000);
					icon[18] = remaining_time / 16;
					icon[19] = 3;
					break;
				case STR_STATUS_EFFECT_BM_CHINA_ITEM2:
					icon[18] = remaining_time / 16;
					icon[19] = 4;
					break;
				case STATUS_FRUIT:// 유그드라 //
					icon[30] = remaining_time / 4;
					break;
				case EXP_POTION:
					icon[17] = remaining_time / 16;
					break;
				case STATUS_EXP_UP_II:
					icon[17] = remaining_time / 16;
					break;

				case STATUS_DRAGONMAAN_EARTH:// 지룡의 마안
					icon[35] = (remaining_time + 16) / 32;
					icon[36] = 46;
					pc.MaanDodge = true;
					pc.getResistance().addPetrifaction(3);
					break;
				case STATUS_DRAGONMAAN_WATER:// 수룡의 마안
					icon[35] = (remaining_time + 16) / 32;
					icon[36] = 47;
					pc.MaanMagicIm = true;
					pc.getResistance().addFreeze(3);
					break;
				case STATUS_DRAGONMAAN_WIND:// // 풍룡의 마안
					icon[35] = (remaining_time + 16) / 32;
					icon[36] = 48;
					pc.MaanMagicDmg = true;
					pc.getResistance().addSleep(3);
					break;
				case STATUS_DRAGONMAAN_FIRE:// 화룡의 마안
					icon[35] = (remaining_time + 16) / 32;
					icon[36] = 49;
					pc.MaanAddDmg = true;
					pc.getResistance().addStun(3);
					break;
				case STATUS_DRAGONMAAN_BIRTH:// 탄생의 마안
					icon[35] = (remaining_time + 16) / 32;
					icon[36] = 50;
					pc.MaanDodge = true;
					pc.MaanMagicIm = true;
					pc.getResistance().addBlind(3);
					break;
				case STATUS_DRAGONMAAN_SHAPE:// 형상의 마안
					icon[35] = (remaining_time + 16) / 32;
					icon[36] = 51;
					pc.MaanDodge = true;
					pc.MaanMagicIm = true;
					pc.MaanMagicDmg = true;
					pc.getResistance().addHold(3);
					break;
				case STATUS_DRAGONMAAN_LIFE:// 생명의 마안
					icon[35] = (remaining_time + 16) / 32;
					icon[36] = 52;
					pc.MaanDodge = true;
					pc.MaanMagicIm = true;
					pc.MaanMagicDmg = true;
					pc.MaanAddDmg = true;
					break;
				case FEATHER_BUFF_A:// 운세에 따른 깃털 버프 // 매우좋은
					icon[33] = remaining_time / 16;
					icon[34] = 70;
					pc.addDmgup(2);
					pc.addHitup(2);
					pc.getAbility().addSp(2);
					pc.sendPackets(new S_SPMR(pc));
					pc.addHpr(3);
					pc.addMaxHp(50);
					pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
					if (pc.isInParty()) {
						pc.getParty().updateMiniHP(pc);
					}
					pc.addMpr(3);
					pc.addMaxMp(30);
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
					break;
				case FEATHER_BUFF_B:// 운세에 따른 깃털 버프 // 좋은
					icon[33] = remaining_time / 16;
					icon[34] = 71;
					pc.addHitup(2);
					pc.getAbility().addSp(1);
					pc.sendPackets(new S_SPMR(pc));
					pc.addMaxHp(50);
					pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
					if (pc.isInParty()) {
						pc.getParty().updateMiniHP(pc);
					}
					pc.addMaxMp(30);
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
					break;
				case FEATHER_BUFF_C:// 운세에 따른 깃털 버프 // 보통
					icon[33] = remaining_time / 16;
					icon[34] = 72;
					pc.addMaxHp(50);
					pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
					if (pc.isInParty()) {
						pc.getParty().updateMiniHP(pc);
					}
					pc.addMaxMp(30);
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
					pc.getAC().addAc(-2);
					break;
				case FEATHER_BUFF_D:// 운세에 따른 깃털 버프 // 나쁜
					icon[33] = remaining_time / 16;
					icon[34] = 73;
					pc.getAC().addAc(-1);
					break;
				case DRAGON_EMERALD_NO:
					int emerald_no = remaining_time - time;
					remaining_time = emerald_no;
					pc.sendPackets(new S_PacketBox(S_PacketBox.EMERALD_EVA, 0x01, remaining_time));
					break;
				case DRAGON_EMERALD_YES:
					int emerald_yes = remaining_time - time;
					remaining_time = emerald_yes;
					pc.sendPackets(new S_PacketBox(S_PacketBox.EMERALD_EVA, 0x02, remaining_time));
					break;
				default:
					new L1SkillUse().handleCommands(clientthread.getActiveChar(), skillid, pc.getId(), pc.getX(),
							pc.getY(), null, remaining_time, L1SkillUse.TYPE_LOGIN);
					continue;
				}
				pc.getSkillEffectTimerSet().setSkillEffect(skillid, remaining_time * 1000);
				pc.sendPackets(new S_UnityIcon(icon[0], icon[1], icon[2], icon[3], icon[4], icon[5], icon[6], icon[7],
						icon[8], icon[9], icon[10], icon[11], icon[12], icon[13], icon[14], icon[15], icon[16],
						icon[17], icon[18], icon[19], icon[20], icon[21], icon[22], icon[23], icon[24], icon[25],
						icon[26], icon[27], icon[28], icon[29], icon[30], icon[31], icon[32], icon[33], icon[34]));
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void serchPc(L1PcInstance pc, LineageClient client) {
		Connection con = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm2 = null;
		ResultSet find = null;
		ResultSet find2 = null;
		String[] FindPc = null;
		int PcCount = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT COUNT(account_name) FROM characters WHERE account_name=?");
			pstm.setString(1, pc.getAccountName());
			find = pstm.executeQuery();
			if (find.next()) {
				PcCount = find.getInt(1);
			}
			pstm2 = con.prepareStatement("SELECT char_name FROM characters WHERE account_name=?");
			pstm2.setString(1, pc.getAccountName());
			find2 = pstm2.executeQuery();
			if (find2.next()) {
				FindPc = new String[PcCount];
				for (int i = 0; i < PcCount; i++) {
					FindPc[i] = find2.getString(1);
					L1PcInstance OtherPc = L1World.getInstance().getPlayer(FindPc[i]);
					if (OtherPc != null && !OtherPc.isPrivateShop() && !OtherPc.isAutoClanjoin()) {
						client.kick();
						client.close();
						OtherPc.sendPackets(new S_Disconnect());
						break;
					}
					find2.next();
				}
			}
		} catch (SQLException e) {
			client.kick();
			client.close();
			_log.info("중복 로그인 버그 체크 오류!");
		} finally {
			SQLUtil.close(find2);
			SQLUtil.close(find);
			SQLUtil.close(pstm2);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public String getType() {
		return C_LOGIN_TO_SERVER;
	}
}