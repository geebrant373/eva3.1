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

package l1j.server.server;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastTable;
import server.GameServer;
import server.LineageClient;
import server.system.autoshop.AutoShop;
import server.system.autoshop.AutoShopManager;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.SpecialEventHandler;
import l1j.server.EventSystem.EventSystemLoader;
import l1j.server.EventSystem.EventSystemSpawner;
import l1j.server.EventSystem.EventSystemTimeController;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.GameSystem.Robot.Robot_Fish;
import l1j.server.GameSystem.Robot.Robot_Hunt;
import l1j.server.GameSystem.MiniGame.BattleZone;
import l1j.server.MJTemplate.Command.MJCommandArgs;
import l1j.server.MJTemplate.MJSqlHelper.Executors.Updator;
import l1j.server.MJTemplate.MJSqlHelper.Handler.Handler;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.command.L1Commands;
import l1j.server.server.command.executor.L1CommandExecutor;
import l1j.server.server.datatables.BoardTable;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.ModelSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_Ability;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_CheckRanking;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_InvGfx;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShopSellList;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Test;
import l1j.server.server.templates.L1Command;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.IntRange;
import l1j.server.server.utils.L1SpawnUtil;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.utils.SystemUtil;
import manager.LinAllManager;

//Referenced classes of package l1j.server.server:
//ClientThread, Shutdown, IpTable, MobTable,
//PolyTable, IdFactory

public class GMCommands {

	private static Logger _log = Logger.getLogger(GMCommands.class.getName());
	
	public static boolean restartBot = false;
	public static boolean bugbearBot = false;
	public static boolean clanBot = false;
	public static boolean fishBot = false;
	public static boolean huntBot = false;
	
	private static GMCommands _instance;

	private GMCommands() {
	}

	public static GMCommands getInstance() {
		if (_instance == null) {
			_instance = new GMCommands();
		}
		return _instance;
	}

	private String complementClassName(String className) {
		if (className.contains(".")) {
			return className;
		}
		return "l1j.server.server.command.executor." + className;
	}

	private boolean executeDatabaseCommand(L1PcInstance pc, String name, String arg) {
		try {
			L1Command command = L1Commands.get(name);
			if (command == null) {
				return false;
			}
			
			if (pc.getAccessLevel() < command.getLevel()) {
				pc.sendPackets(new S_ServerMessage(74, "[Command] 커멘드 " + name)); // \f1%0은
																					// 사용할
																					// 수
																					// 없습니다.
				return true;
			}
			
			Class<?> cls = Class.forName(complementClassName(command.getExecutorClassName()));
			L1CommandExecutor exe = (L1CommandExecutor) cls.getMethod("getInstance").invoke(null);
			exe.execute(pc, name, arg);
			LinAllManager.getInstance().GmAppend(pc.getName(), name, arg);
			// _log.info('('+ pc.getName() + ")가 " + name + " " + arg + "운영자
			// 명령어를 사용했습니다. ");
			return true;
		} catch (Exception e) {
			// _log.log(Level.SEVERE, "error gm command", e);
		}
		return false;
	}

	public void handleCommands(L1PcInstance gm, String cmdLine) {
		StringTokenizer token = new StringTokenizer(cmdLine);
		// 최초의 공백까지가 커맨드, 그 이후는 공백을 단락으로 한 파라미터로서 취급한다
		String cmd = token.nextToken();
		String param = "";
		while (token.hasMoreTokens()) {
			param = new StringBuilder(param).append(token.nextToken()).append(' ').toString();
		}
		param = param.trim();

		// 데이타베이스화 된 커멘드
		if (executeDatabaseCommand(gm, cmd, param)) {
			if (!cmd.equalsIgnoreCase("재실행")) {
				_lastCommands.put(gm.getId(), cmdLine);
			}
			return;
		}

		if (gm.getAccessLevel() < 200) {
			gm.sendPackets(new S_ServerMessage(74, "[Command] 커맨드 " + cmd));
			return;
		}
		//System.out.println("cmdLine="+cmdLine);
		LinAllManager.getInstance().GmAppend(gm.getName(), cmd, param);
		// GM에 개방하는 커맨드는 여기에 쓴다
		if (cmd.equalsIgnoreCase("도움말")) {
			showHelp(gm);
		} else if (cmd.equalsIgnoreCase("ㅡ")) {
			System.out.println(SystemUtil.getUsedMemoryMB());
		} else if (cmd.equalsIgnoreCase("이벤트")) {
			Event_System(gm, param);
		} else if (cmd.equalsIgnoreCase("자동드다")) {
			자동드다(gm, param);
		} else if (cmd.equalsIgnoreCase("확률초기화")) {
			initialize_user_private_probability(new MJCommandArgs().setOwner(gm).setParam(param));
		} else if (cmd.equalsIgnoreCase("확률")) {
			update_user_private_probability(new MJCommandArgs().setOwner(gm).setParam(param));
		} else if (cmd.equalsIgnoreCase("확률확인")) {
			show_user_private_probability(new MJCommandArgs().setOwner(gm).setParam(param));
		} else if (cmd.equalsIgnoreCase("텔")) {
			tell(gm);
		} else if (cmd.equalsIgnoreCase("캐릭등록")) {
			chaTrade(gm, param);
		} else if (cmd.equalsIgnoreCase("판매")) {
			L1Object boss = L1World.getInstance().findNpc(70037);
			gm.sendPackets(new S_ShopSellList(boss.getId()));
		} else if (cmd.equalsIgnoreCase("캐릭조회")) {
			chaTrade2(gm, param);
		} else if (cmd.equalsIgnoreCase("이미지삭제")) {
			get_delete_gfx(gm);
		} else if (cmd.equalsIgnoreCase("박스")) {
			packetbox(gm, param);
		} else if (cmd.equalsIgnoreCase("계정추가")) {
			addaccount(gm, param);
		} else if (cmd.equalsIgnoreCase("확인")) {
			System.out.println("pvp추타="+gm.getMoveState().getHeading());
		} else if (cmd.equalsIgnoreCase("버경")) {
			SpecialEventHandler.getInstance().doBugRace();
		} else if (cmd.equalsIgnoreCase("전체버프")) {
			SpecialEventHandler.getInstance().doAllBuf();
		} else if (cmd.equalsIgnoreCase("코마버프")) {
			SpecialEventHandler.getInstance().doAllCOMA();
		} else if (cmd.equalsIgnoreCase("비번변경")) {
			changepassword(gm, param);
		} else if (cmd.equalsIgnoreCase("테스트")) {
			gm.sendPackets(new S_NPCTalkReturn(gm.getId(), "testtest"));
		} else if (cmd.equalsIgnoreCase("수표")) {
			checksearch(gm, param);
		} else if (cmd.equalsIgnoreCase("초기화")) {
			아지트정보초기화();
			클랜정보초기화();
			서버초기화();
		} else if (cmd.equalsIgnoreCase("낚시로봇")) {
			autobot(gm);
		} else if (cmd.equalsIgnoreCase("코드")) {
			CodeTest(gm, param);
		} else if (cmd.equalsIgnoreCase("로봇사냥")) {
			huntBot = true;
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					Robot_Hunt.getInstance().start_spawn();
				}
			};
			Timer timer = new Timer();
			timer.schedule(task, 2000);
		} else if (cmd.equalsIgnoreCase("정리")) {
			Clear(gm);
		} else if (cmd.equalsIgnoreCase("불")) {
			spawnmodel(gm, param);
			// } else if (cmd.equalsIgnoreCase("보스")){
			// BossSpawnTimeController.getBossTime(gm);
		} else if (cmd.equalsIgnoreCase("검색")) {
			searchDatabase(gm, param);
		} else if (cmd.equalsIgnoreCase("조사")) {
			josa(gm, param);
		} else if (cmd.equalsIgnoreCase("오픈대기")) {
			standBy(gm, param);
		} else if (cmd.equalsIgnoreCase("공속체크")) {
			gm.AttackSpeedCheck2 = 1;
			gm.sendPackets(new S_SystemMessage("\\fY허수아비를 10회공격해주세요."));
		} else if (cmd.equalsIgnoreCase("전체소환")) {
			allrecall(gm);
		} else if (cmd.equalsIgnoreCase("검")) { // #### 케릭검사
			chainfo(gm, param);
		} else if (cmd.equals("공성시작")) {
			castleWarStart(gm, param);
		} else if (cmd.equalsIgnoreCase("렙작")) {
			levelup3(gm, param);
		} else if (cmd.equalsIgnoreCase("상점킥")) {
			ShopKick(gm, param);
		} else if (cmd.equalsIgnoreCase("라이트")) {
			maphack(gm, param);
		} else if (cmd.equalsIgnoreCase("가라")) {
			nocall(gm, param);
		} else if (cmd.equalsIgnoreCase("전체선물")) {
			allpresent(gm, param);
		} else if (cmd.equalsIgnoreCase("고정버프")) {
			membership(gm, param);
		} else if (cmd.equalsIgnoreCase("서버정보")) {
			neoinfo(gm);
		} else if (cmd.equalsIgnoreCase("상점검사")) {
			상점검사(gm);
		} else if (cmd.equalsIgnoreCase("기운축복")) {
			기운축복(gm, param);
		} else if (cmd.equalsIgnoreCase("배틀존")) {
			if (BattleZone.getInstance().getDuelStart()) {
				gm.sendPackets(new S_SystemMessage("배틀존이 실행 중 입니다."));
			} else {
				BattleZone.getInstance().setGmStart(true);
				gm.sendPackets(new S_SystemMessage("배틀존이 실행 되었습니다."));
			}
		} else if (cmd.equalsIgnoreCase("스턴축복")) {
			스턴축복(gm, param);
		} else if (cmd.equalsIgnoreCase("개인밸런스")) {
			CharacterBalance(gm, param);
		} else if (cmd.equalsIgnoreCase("경험치")) {
			ExpTable.expPenaltyReLoad();
			effect(gm, param);
			gm.sendPackets(new S_SystemMessage("서버 경험치 리로드 완료"));
		} else if (cmd.equalsIgnoreCase("이팩트")) {
			effect(gm, param);
		} else if (cmd.equalsIgnoreCase("드랍")) {
			showDropMobList(gm, param);
		} else if (cmd.equalsIgnoreCase("인벤삭제")) {
			removeInventory(gm, param);
		} else if (cmd.equalsIgnoreCase("계좌등록")) {
			addBank(gm, param);
		} else if (cmd.equalsIgnoreCase("계좌확인")) {
			checkBank(gm, param);
		} else if (cmd.equalsIgnoreCase("판매등록")) {
			addSell(gm, param);
		} else if (cmd.equalsIgnoreCase("구매신청")) {
			requestAdena(gm, param);
		} else if (cmd.equalsIgnoreCase("판매완료")) {
			adenaSellDone(gm, param);
		} else if (cmd.equalsIgnoreCase("판매취소")) {
			deleteAdenaSell(gm, param);
		} else if (cmd.equalsIgnoreCase("닉네임")) {
			GmCharacterNameChange(gm, param);
		} else if (cmd.equalsIgnoreCase("인벤삭제")) {
			InventoryDelete(gm, param);
		} else if (cmd.equalsIgnoreCase("데미지체크")) {
			if (Config.ALT_ATKMSG) {
				Config.ALT_ATKMSG = false;
				gm.sendPackets(new S_SystemMessage("데미지체크 OFF"));
			} else {
				Config.ALT_ATKMSG = true;
				gm.sendPackets(new S_SystemMessage("데미지체크 ON"));
			}
		} else if (cmd.equalsIgnoreCase(".")) {
			if (!_lastCommands.containsKey(gm.getId())) {
				gm.sendPackets(new S_ServerMessage(74, "[Command] 커맨드 " + cmd));
				return;
			}
			redo(gm, param);
			return;
		} else {
			gm.sendPackets(new S_SystemMessage("운영자 커멘드 " + cmd + " 는 존재하지 않아 유저커멘드로 연결합니다. "));
			UserCommands.getInstance().handleCommands(gm, cmdLine);
		}
	}

	private static void 클랜정보초기화() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE house SET is_on_sale = '1' WHERE is_on_sale = '0'");
			pstm.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	private static void 아지트정보초기화() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("delete FROM clan_data WHERE clan_name != '중립'");
			pstm.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	private void 서버초기화() {
		Connection con = null;
		PreparedStatement pstm = null;
		String[] list = { "accounts", "board_adena", "board_adena_user", "board_character", "character_auto_hunt",
				"character_auto_set", "character_buddys", "character_buff", "character_config",
				"character_elf_warehouse", "character_items", "character_package_warehouse", "character_quests",
				"character_skills", "character_slot_items", "character_teleport", "character_warehouse", "characters",
				"clan_warehouse", "log_chat", "log_enchant", "log_ipcount", "letter" };

		try {
			for (int i = 0; i < list.length; i++) {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("TRUNCATE " + list[i]);
				pstm.executeUpdate();
				System.out.println("초기화 = " + list[i].toString());
			}
			
			File emblemDir = new File("emblem");
	        if (emblemDir.exists() && emblemDir.isDirectory()) {
	            File[] files = emblemDir.listFiles();
	            if (files != null) {
	                for (File f : files) {
	                    if (!f.getName().equals(String.valueOf(Config.중립혈엠블럼))) {
	                        if (f.delete()) {
	                            System.out.println("삭제됨 = " + f.getName());
	                        } else {
	                            System.out.println("삭제 실패 = " + f.getName());
	                        }
	                    }
	                }
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
			GameServer.getInstance().shutdown();
		}
	}
	
	public static void showDropMobList(L1PcInstance gm, String itemName) {
	    try {
	        StringTokenizer st = new StringTokenizer(itemName);
	        String name = st.nextToken();

	        Connection con = null;
	        PreparedStatement pstm = null;
	        ResultSet rs = null;

	        // LinkedHashSet으로 중복 제거 + 순서 유지
	        Set<String> resultSet = new LinkedHashSet<>();
	        resultSet.add("[" + name + "] 드랍 몬스터 리스트");

	        try {
	            con = L1DatabaseFactory.getInstance().getConnection();

	            String sql =
	                "SELECT d.mobname " +
	                "FROM droplist d " +
	                "JOIN ( " +
	                "    SELECT item_id FROM weapon WHERE REPLACE(name, ' ', '') LIKE ? " +
	                "    UNION " +
	                "    SELECT item_id FROM armor WHERE REPLACE(name, ' ', '') LIKE ? " +
	                "    UNION " +
	                "    SELECT item_id FROM etcitem WHERE REPLACE(name, ' ', '') LIKE ? " +
	                ") i ON d.itemId = i.item_id";

	            pstm = con.prepareStatement(sql);
	            // 부분 검색 → %검색어%
	            pstm.setString(1, "%" + name + "%");
	            pstm.setString(2, "%" + name + "%");
	            pstm.setString(3, "%" + name + "%");

	            rs = pstm.executeQuery();
	            boolean hasMonster = false;
	            while (rs.next()) {
	                resultSet.add(rs.getString("mobname"));
	                hasMonster = true;
	            }

	            if (!hasMonster) {
	                resultSet.add("없음");
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	            resultSet.add("조회 중 오류 발생");
	        } finally {
	            SQLUtil.close(rs);
	            SQLUtil.close(pstm);
	            SQLUtil.close(con);
	        }

	        // Set → ArrayList 변환
	        ArrayList<String> result = new ArrayList<>(resultSet);

	        if (!result.isEmpty()) {
	            gm.sendPackets(new S_NPCTalkReturn(
	                gm.getId(),
	                "droplistmon",
	                result.toArray(new String[result.size()])
	            ));
	        }
	    } catch (Exception e) {
	        gm.sendPackets(new S_SystemMessage("띄워쓰기하지 않고 아이템이름 입력해야됨."));
	        gm.sendPackets(new S_SystemMessage("예시) .드랍 무관의망토 입력."));
	    }
	}
	
	private void removeInventory(L1PcInstance gm, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String name = st.nextToken();

			L1PcInstance target = L1World.getInstance().getPlayer(name);

			if (target == null) {

				gm.sendPackets(new S_SystemMessage(target + "접속 중이 아닙니다."));
				return;
			}

			int cnt = 0;
			for (L1ItemInstance item : target.getInventory().getItems()) {
				target.getInventory().removeItem(item);
				cnt++;
			}
			gm.sendPackets(new S_SystemMessage(target.getName() + "의 아이템 " + cnt + "개를 삭제했습니다."));

		} catch (Exception e) {

			gm.sendPackets(new S_SystemMessage(".인벤삭제 [접속중인 캐릭명] 입력."));
		}
	}

	private void standBy(L1PcInstance gm, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String status = st.nextToken();
			if (status.equalsIgnoreCase("켬")) {
				if (Config.STANDBY_SERVER) {
					gm.sendPackets(new S_SystemMessage("이미 대기 상태로 돌입하였습니다."));
					return;
				}
				Config.STANDBY_SERVER = true;
				Config.RATE_XP = 0;// 오픈대기때 경험치 강제 0으로만듬
				Config.RATE_FISHING_EXP = 0;
				gm.sendPackets(new S_SystemMessage("오픈대기 상태로 전환됩니다."));
			} else if (status.equalsIgnoreCase("끔")) {
				if (!Config.STANDBY_SERVER) {
					gm.sendPackets(new S_SystemMessage("대기 상태가 아닙니다."));
					return;
				}
				Config.load();// 기존컨피그 경험치 리로드시켜버림
				Config.STANDBY_SERVER = false;
				gm.sendPackets(new S_SystemMessage("오픈대기 상태가 해제됩니다."));
			}
		} catch (Exception eee) {
			gm.sendPackets(new S_SystemMessage(".오픈대기 [켬/끔] 으로 입력하세요."));
			gm.sendPackets(new S_SystemMessage("켬 - 오픈대기 상태로 전환 | 끔 - 일반모드로 게임시작"));
		}
	}

	private void neoinfo(L1PcInstance gm) {
		// TODO Auto-generated method stub
		gm.sendPackets(new S_NPCTalkReturn(gm.getId(), "neoserver"));
	}

	private void maphack(L1PcInstance gm, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String on = st.nextToken();
			if (on.equalsIgnoreCase("켬")) {
				gm.sendPackets(new S_Ability(3, true));
				gm.sendPackets(new S_SystemMessage("라이트 : [켬]"));
			} else if (on.equals("끔")) {
				gm.sendPackets(new S_Ability(3, false));
				gm.sendPackets(new S_SystemMessage("라이트 : [끔]"));
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".라이트  [켬, 끔]"));
		}
	}

	private void membership(L1PcInstance gm, String param) {
		// TODO Auto-generated method stub
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			String s = stringtokenizer.nextToken();
			L1PcInstance target = null;
			target = L1World.getInstance().getPlayer(s);
			target.getInventory().storeItem(46176, 1);
			gm.sendPackets(new S_SystemMessage(target.getName() + "님 아이템 지급 완료 ."));
		} catch (Exception exception21) {
			gm.sendPackets(new S_SystemMessage(".고정버프 [캐릭터명]을 입력 해주세요."));
		}
	}

	private void allpresent(L1PcInstance gm, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			int itemid = Integer.parseInt(st.nextToken(), 10);
			int enchant = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);
			Collection<L1PcInstance> player = null;
			player = L1World.getInstance().getAllPlayers();
			for (L1PcInstance target : player) {
				if (target == null)
					continue;
				if (!target.isGhost() && !target.isAutoClanjoin() && !target.isPrivateShop() && !target.noPlayerCK) {
					L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
					item.setCount(count);
					item.setEnchantLevel(enchant);
					if (item != null) {
						if (target.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
							target.getInventory().storeItem(item);
						}
					}
					target.sendPackets(new S_SkillSound(target.getId(), 1091));// 비둘기액션
					target.sendPackets(new S_SkillSound(target.getId(), 4856));// 하트액션
					target.sendPackets(new S_SystemMessage("운영자가 전체유저에게 선물을 주었습니다."));
					target.sendPackets(new S_SystemMessage("아이템 :  [" + item.getViewName() + "]"));
				}
			}
		} catch (Exception exception) {
			gm.sendPackets(new S_SystemMessage(".전체선물 아이템ID 인첸트수 아이템수로 입력해 주세요."));
		}
	}

	private void nocall(L1PcInstance gm, String param) {
		// TODO Auto-generated method stub
		try {
			StringTokenizer tokenizer = new StringTokenizer(param);
			String pcName = tokenizer.nextToken();

			L1PcInstance target = null;
			target = L1World.getInstance().getPlayer(pcName);
			if (target != null) {
				L1Teleport.teleport(target, 33440, 32795, (short) 4, 5, true);
			} else {
				gm.sendPackets(new S_SystemMessage("접속중이지 않는 유저 ID 입니다."));
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".가라 (보낼케릭터명) 으로 입력해 주세요."));
		}
	}

	private void ShopKick(L1PcInstance gm, String param) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			String s = stringtokenizer.nextToken();
			AutoShopManager shopManager = AutoShopManager.getInstance();
			AutoShop shopPlayer = shopManager.getShopPlayer(s);
			if (shopPlayer != null) {
				shopPlayer.logout();
				shopManager.remove(shopPlayer);
				shopPlayer = null;
			} else {
				gm.sendPackets(new S_SystemMessage("무인상점 가동 케릭이 아닙니다."));
			}
			stringtokenizer = null;
			s = null;
		} catch (Exception exception21) {
			gm.sendPackets(new S_SystemMessage(".상점킥 [상점캐릭터명]을 입력 해주세요."));
		}
	}

	public void checksearch(L1PcInstance gm, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			String type = tok.nextToken();
			if (type.equalsIgnoreCase("인벤")) {
				gm.sendPackets(new S_CheckRanking(gm, 1));
			} else if (type.equalsIgnoreCase("창고")) {
				gm.sendPackets(new S_CheckRanking(gm, 2));
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".수표 [인벤,창고]"));
		}
	}
		
	public void levelup3(L1PcInstance gm, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			String user = tok.nextToken();
			L1PcInstance target = L1World.getInstance().getPlayer(user);
			int level = Integer.parseInt(tok.nextToken());
			if (level == target.getLevel()) {
				return;
			}
			if (!IntRange.includes(level, 1, 99)) {
				gm.sendPackets(new S_SystemMessage("1-99의 범위에서 지정해 주세요"));
				return;
			}
			target.setExp(ExpTable.getExpByLevel(level));
			gm.sendPackets(new S_SystemMessage(target.getName() + "님의 레벨이 변경됨! .검 [케릭명]으로 확인요망"));
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".렙작 [케릭명] [레벨] 입력"));
		}
	}

	private void chainfo(L1PcInstance gm, String param) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			String s = stringtokenizer.nextToken();
			gm.sendPackets(new S_Chainfo(1, s));
		} catch (Exception exception21) {
			gm.sendPackets(new S_SystemMessage(".검 [캐릭터명]을 입력 해주세요."));
		}
	}

	private void castleWarStart(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String name = tok.nextToken();
			int minute = Integer.parseInt(tok.nextToken());

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, minute);

			CastleTable.getInstance().updateWarTime(name, cal);
			WarTimeController.getInstance().setWarStartTime(name, cal);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			gm.sendPackets(new S_SystemMessage(String.format(".공성시간이 %s로 변경 되었습니다.", formatter.format(cal))));
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".공성시작 성이름두글자(켄트,오크,윈다,기란,하이,드워,아덴,디아) 분"));
		}
	}

	private void allrecall(L1PcInstance gm) {
		try {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if (!pc.isGm()) {
					recallnow(gm, pc);
				}
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".전체소환 커멘드 에러"));
		}

	}

	private void recallnow(L1PcInstance gm, L1PcInstance target) {
		try {
			L1Teleport.teleportToTargetFront(target, gm, 2);

			gm.sendPackets(new S_SystemMessage((new StringBuilder()).append(target.getName()).append("님을 소환했습니다.").toString()));
			target.sendPackets(new S_SystemMessage("게임 마스터에게 소환되었습니다."));
		} catch (Exception e) {
			_log.log(Level.SEVERE, "", e);
		}
	}

	private void josa(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String type = tok.nextToken();
			L1PcInstance pcc[] = new L1PcInstance[5];

			if (type.equalsIgnoreCase("체력")) {
				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					for (int i = 0; i < 5; i++) {
						if (pcc[i] == null) {
							pcc[i] = pc;
							break;
						} else if (pcc[i].getMaxHp() < pc.getMaxHp()) {
							for (int j = i + 1; j < 5; j++) {
								pcc[j] = pcc[j - 1];
							}
							pcc[i] = pc;
							break;
						}
					}
				}
				gm.sendPackets(new S_SystemMessage("1위 . 이름 :" + pcc[0].getName() + " 체력 :" + pcc[0].getMaxHp() + " 마나 :" + pcc[0].getMaxMp() + ""));
				gm.sendPackets(new S_SystemMessage("2위 . 이름 :" + pcc[1].getName() + " 체력 :" + pcc[1].getMaxHp() + " 마나 :" + pcc[1].getMaxMp() + ""));
				gm.sendPackets(new S_SystemMessage("3위 . 이름 :" + pcc[2].getName() + " 체력 :" + pcc[2].getMaxHp() + " 마나 :" + pcc[2].getMaxMp() + ""));
				gm.sendPackets(new S_SystemMessage("4위 . 이름 :" + pcc[3].getName() + " 체력 :" + pcc[3].getMaxHp() + " 마나 :" + pcc[3].getMaxMp() + ""));
				gm.sendPackets(new S_SystemMessage("5위 . 이름 :" + pcc[4].getName() + " 체력 :" + pcc[4].getMaxHp() + " 마나 :" + pcc[4].getMaxMp() + ""));
			} else if (type.equalsIgnoreCase("마나")) {

				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					for (int i = 0; i < 5; i++) {
						if (pcc[i] == null) {
							pcc[i] = pc;
							break;
						} else if (pcc[i].getMaxMp() < pc.getMaxMp()) {
							for (int j = i + 1; j < 5; j++) {
								pcc[j] = pcc[j - 1];
							}
							pcc[i] = pc;
							break;
						}
					}
				}
				gm.sendPackets(new S_SystemMessage("1위 . 이름 :" + pcc[0].getName() + " 체력 :" + pcc[0].getMaxHp() + " 마나 :" + pcc[0].getMaxMp() + ""));
				gm.sendPackets(new S_SystemMessage("2위 . 이름 :" + pcc[1].getName() + " 체력 :" + pcc[1].getMaxHp() + " 마나 :" + pcc[1].getMaxMp() + ""));
				gm.sendPackets(new S_SystemMessage("3위 . 이름 :" + pcc[2].getName() + " 체력 :" + pcc[2].getMaxHp() + " 마나 :" + pcc[2].getMaxMp() + ""));
				gm.sendPackets(new S_SystemMessage("4위 . 이름 :" + pcc[3].getName() + " 체력 :" + pcc[3].getMaxHp() + " 마나 :" + pcc[3].getMaxMp() + ""));
				gm.sendPackets(new S_SystemMessage("5위 . 이름 :" + pcc[4].getName() + " 체력 :" + pcc[4].getMaxHp() + " 마나 :" + pcc[4].getMaxMp() + ""));

			} else if (type.equalsIgnoreCase("힘")) {

				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					for (int i = 0; i < 5; i++) {
						if (pcc[i] == null) {
							pcc[i] = pc;
							break;
						} else if (pcc[i].getAbility().getTotalStr() < pc.getAbility().getTotalStr()) {
							for (int j = i + 1; j < 5; j++) {
								pcc[j] = pcc[j - 1];
							}
							pcc[i] = pc;
							break;
						}
					}
				}
				gm.sendPackets(new S_SystemMessage("1위 . 이름 :" + pcc[0].getName() + " 힘:" + pcc[0].getAbility().getTotalStr()));
				gm.sendPackets(new S_SystemMessage("2위 . 이름 :" + pcc[1].getName() + " 힘 :" + pcc[1].getAbility().getTotalStr()));
				gm.sendPackets(new S_SystemMessage("3위 . 이름 :" + pcc[2].getName() + " 힘 :" + pcc[2].getAbility().getTotalStr()));
				gm.sendPackets(new S_SystemMessage("4위 . 이름 :" + pcc[3].getName() + " 힘 :" + pcc[3].getAbility().getTotalStr()));
				gm.sendPackets(new S_SystemMessage("5위 . 이름 :" + pcc[4].getName() + " 힘 :" + pcc[4].getAbility().getTotalStr()));

			} else if (type.equalsIgnoreCase("덱스")) {

				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					for (int i = 0; i < 5; i++) {
						if (pcc[i] == null) {
							pcc[i] = pc;
							break;
						} else if (pcc[i].getAbility().getTotalDex() < pc.getAbility().getTotalDex()) {
							for (int j = i + 1; j < 5; j++) {
								pcc[j] = pcc[j - 1];
							}
							pcc[i] = pc;
							break;
						}
					}
				}
				gm.sendPackets(new S_SystemMessage("1위 . 이름 :" + pcc[0].getName() + " 덱스 :" + pcc[0].getAbility().getTotalDex()));
				gm.sendPackets(new S_SystemMessage("2위 . 이름 :" + pcc[1].getName() + " 덱스 :" + pcc[1].getAbility().getTotalDex()));
				gm.sendPackets(new S_SystemMessage("3위 . 이름 :" + pcc[2].getName() + " 덱스 :" + pcc[2].getAbility().getTotalDex()));
				gm.sendPackets(new S_SystemMessage("4위 . 이름 :" + pcc[3].getName() + " 덱스 :" + pcc[3].getAbility().getTotalDex()));
				gm.sendPackets(new S_SystemMessage("5위 . 이름 :" + pcc[4].getName() + " 덱스 :" + pcc[4].getAbility().getTotalDex()));
			}

		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage("[Command] .조사 [체력, 마나, 힘, 덱스] "));
		}
	}

	private void searchDatabase(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			int type = Integer.parseInt(tok.nextToken());
			String name = tok.nextToken();
			searchObject(gm, type, "%" + name + "%");
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".검색 [0~4] [name]을 입력 해주세요."));
			gm.sendPackets(new S_SystemMessage("0=잡템, 1=무기, 2=방어구, 3=엔피씨, 4=변신, 5=엔피씨이미지"));
			gm.sendPackets(new S_SystemMessage("name을 정확히 모르거나 띄워쓰기 되어있는 경우는"));
			gm.sendPackets(new S_SystemMessage("'%'를 앞이나 뒤에 붙여 쓰십시오."));
		}
	}

	private void searchObject(L1PcInstance gm, int type, String name) {
		try {
			String str1 = null;
			String str2 = null;
			int count = 0;
			java.sql.Connection con = null;
			con = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = null;

			switch (type) {
			case 0: // etcitem
				statement = con.prepareStatement("select item_id, name from etcitem where name Like '" + name + "'");
				break;
			case 1: // weapon
				statement = con.prepareStatement("select item_id, name from weapon where name Like '" + name + "'");
				break;
			case 2: // armor
				statement = con.prepareStatement("select item_id, name from armor where name Like '" + name + "'");
				break;
			case 3: // npc
				statement = con.prepareStatement("select npcid, name from npc where name Like '" + name + "'");
				break;
			case 4: // polymorphs
				statement = con.prepareStatement("select polyid, name from polymorphs where name Like '" + name + "'");
				break;
			case 5: // npc(gfxid)
				statement = con.prepareStatement("select gfxid, name,note from npc where name Like '" + name + "'");
				break;
			default:
				break;
			}
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				str1 = rs.getString(1);
				str2 = rs.getString(2);
				gm.sendPackets(new S_SystemMessage("id : [" + str1 + "], name : [" + str2 + "]"));
				count++;
			}
			rs.close();
			statement.close();
			con.close();
			gm.sendPackets(new S_SystemMessage("총 [" + count + "]개의 데이터가 검색되었습니다."));
		} catch (Exception e) {
		}
	}

	private void spawnmodel(L1PcInstance gm, String param) {
		StringTokenizer st = new StringTokenizer(param);
		int type = Integer.parseInt(st.nextToken(), 10);
		ModelSpawnTable.getInstance().insertmodel(gm, type);
		gm.sendPackets(new S_SystemMessage("[Command] 불 넣었다"));
	}

	private void showHelp(L1PcInstance pc) {
		pc.sendPackets(new S_SystemMessage("-------------------<GM 명령어>--------------------"));
		pc.sendPackets(new S_SystemMessage(".종료 .영구추방 .추방 .계정압류 .밴아이피 .킬"));
		pc.sendPackets(new S_SystemMessage(".엔피씨이미지 .인벤이미지 .스폰 .배치 .스킬보기"));
		pc.sendPackets(new S_SystemMessage(".버프 .변신 .소생 .올버프 .전체버프 .겜블 .설문"));
		pc.sendPackets(new S_SystemMessage(".아데나 .아이템 .세트아이템 .선물 .렙선물 .액션 "));
		pc.sendPackets(new S_SystemMessage(".채팅 .채금 .셋팅 .서먼 .청소 .날씨 .소환. 파티소환"));
		pc.sendPackets(new S_SystemMessage(".홈타운 .귀환 .출두 .스킬마스터 .레벨 .속도"));
		pc.sendPackets(new S_SystemMessage(".이동 .위치 .누구 .정보 .피바 .감시 .투명 .불투명"));
		pc.sendPackets(new S_SystemMessage(".리로드트랩 .쇼트랩 .리셋트랩 .실행 .재실행  .고스폰"));
		pc.sendPackets(new S_SystemMessage(".상점검사 .이팩트 .개인밸런스 .캐릭등록 .캐릭조회  .추가명령"));
		pc.sendPackets(new S_SystemMessage(".확률초기화 .확률 .확률확인 .로봇사냥 .수표 .시작"));
		pc.sendPackets(new S_SystemMessage(".낚시로봇 .드랍"));
		pc.sendPackets(new S_SystemMessage("--------------------------------------------------"));
	}

	private static Map<Integer, String> _lastCommands = new HashMap<Integer, String>();

	private void redo(L1PcInstance pc, String arg) {
		try {
			String lastCmd = _lastCommands.get(pc.getId());
			if (arg.isEmpty()) {
				pc.sendPackets(new S_SystemMessage("[Command] 커맨드 " + lastCmd + " 을(를) 재실행합니다."));
				handleCommands(pc, lastCmd);
			} else {
				StringTokenizer token = new StringTokenizer(lastCmd);
				String cmd = token.nextToken() + " " + arg;
				pc.sendPackets(new S_SystemMessage("[Command] 커맨드 " + cmd + " 을(를) 재실행합니다."));
				handleCommands(pc, cmd);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			pc.sendPackets(new S_SystemMessage("[Command] .재실행 커맨드에러"));
		}
	}

	public void 기운축복(L1PcInstance gm, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			String user = tok.nextToken();
			L1PcInstance target = L1World.getInstance().getPlayer(user);
			target.기운축복 = true;
			target.sendPackets(new S_SystemMessage("\\aL운영자께서 기운템 확률 100% 1번의 기회를 주셨습니다."));
			gm.sendPackets(new S_SystemMessage("\\aL[" + target.getName() + "]에게  100% 기운템 확률 1번의 기회를 드렸습니다."));
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".기운축복 [케릭명]"));
		}
	}

	private void 스턴축복(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String charName = st.nextToken();
			int addImpactStun = Integer.parseInt(st.nextToken());
			L1PcInstance player = L1World.getInstance().getPlayer(charName);
			player.addHitup_skill(addImpactStun);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".스턴축복 [캐릭명] [수치]"));
		}
	}

	private void CharacterBalance(L1PcInstance pc, String param) {
		Connection con = null;
		PreparedStatement pstm = null;

		try {
			StringTokenizer st = new StringTokenizer(param);

			String charName = st.nextToken();
			int addDamage = Integer.parseInt(st.nextToken());
			int addDamageRate = Integer.parseInt(st.nextToken());
			int addReduction = Integer.parseInt(st.nextToken());
			int addReductionRate = Integer.parseInt(st.nextToken());

			L1PcInstance player = L1World.getInstance().getPlayer(charName);

			if (player != null) {
				player.setAddDamage(addDamage);
				player.setAddDamageRate(addDamageRate);
				player.setAddReduction(addReduction);
				player.setAddReductionRate(addReductionRate);
				player.save();
			} else {
				int i = 0;
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement(
						"update characters set AddDamage = ?, AddDamageRate = ?, AddReduction = ?, AddReductionRate = ? where char_name = ?");
				pstm.setInt(++i, addDamage);
				pstm.setInt(++i, addDamageRate);
				pstm.setInt(++i, addReduction);
				pstm.setInt(++i, addReductionRate);
				pstm.setString(++i, charName);
				pstm.executeQuery();
			}

		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".밸런스 [캐릭명] [추타] [추타확률] [리덕] [리덕확률]"));
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void 스턴내성(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String charName = st.nextToken();
			int addImpactStun = Integer.parseInt(st.nextToken());
			L1PcInstance player = L1World.getInstance().getPlayer(charName);
			player.addHitup_skill(addImpactStun);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".스턴내성 [캐릭명] [수치]"));
		}
	}

	private void effect(L1PcInstance pc, String param) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			int sprid = Integer.parseInt(stringtokenizer.nextToken());
			pc.sendPackets(new S_SkillSound(pc.getId(), sprid));
			Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), sprid));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".이팩트 [숫자] 라고 입력해 주세요."));
		}
	}

	private void packetbox(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			int id = Integer.parseInt(st.nextToken(), 10);
			pc.sendPackets(new S_PacketBox(id));
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage("[Command] .패킷박스 [id] 입력"));
		}
	}
	// private static String encodePassword(String rawPassword) throws
	// NoSuchAlgorithmException, UnsupportedEncodingException {
	// byte buf[] = rawPassword.getBytes("UTF-8");
	// buf = MessageDigest.getInstance("SHA").digest(buf);
	// return Base64.encodeBytes(buf);
	// }

	private void AddAccount(L1PcInstance gm, String account, String passwd, String Ip, String Host) {
		try {
			String login = null;
			String password = null;
			java.sql.Connection con = null;
			con = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = null;
			PreparedStatement pstm = null;

			password = (passwd);

			statement = con.prepareStatement("select * from accounts where login Like '" + account + "'");
			ResultSet rs = statement.executeQuery();

			if (rs.next())
				login = rs.getString(1);
			if (login != null) {
				gm.sendPackets(new S_SystemMessage("[Command] 이미 계정이 있습니다."));
				return;
			} else {
				String sqlstr = "INSERT INTO accounts SET login=?,password=?,lastactive=?,access_level=?,ip=?,host=?,banned=?,charslot=?,gamepassword=?";
				pstm = con.prepareStatement(sqlstr);
				pstm.setString(1, account);
				pstm.setString(2, password);
				pstm.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
				pstm.setInt(4, 0);
				pstm.setString(5, Ip);
				pstm.setString(6, Host);
				pstm.setInt(7, 0);
				pstm.setInt(8, 6);
				pstm.setInt(9, 0);
				pstm.execute();
				gm.sendPackets(new S_SystemMessage("[Command] 계정 추가가 완료되었습니다."));
			}

			rs.close();
			pstm.close();
			statement.close();
			con.close();
		} catch (Exception e) {
		}
	}

	private static boolean isDisitAlpha(String str) {
		boolean check = true;
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i)) // 숫자가 아니라면
					&& Character.isLetterOrDigit(str.charAt(i)) // 특수문자라면
					&& !Character.isUpperCase(str.charAt(i)) // 대문자가 아니라면
					&& !Character.isLowerCase(str.charAt(i))) { // 소문자가 아니라면
				check = false;
				break;
			}
		}
		return check;
	}

	private FastTable<L1NpcInstance> list = new FastTable<L1NpcInstance>();

	public void add_list(L1NpcInstance npc) {
		synchronized (list) {
			if (!list.contains(npc))
				list.add(npc);
		}
	}

	private void get_delete_gfx(L1PcInstance pc) {
		try {
			Iterator<L1NpcInstance> iter = list.iterator();
			L1NpcInstance npc = null;

			while (iter.hasNext()) {
				npc = iter.next();
				if (npc == null)
					continue;
				npc.deleteMe();
			}

			list.clear();
			pc.sendPackets(new S_SystemMessage("'이미지 삭제' 가 완료 되었습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("커멘드: .이미지삭제 로 입력 바랍니다."));
		}
	}

	private void tell(L1PcInstance gm) {
		if (!gm.isParalyzed() && !gm.isSleeped()/* && !pc.isPinkName() */) {
			L1Teleport.teleport(gm, gm.getX(), gm.getY(), gm.getMapId(), gm.getMoveState().getHeading(), false);
		}
	}

	private void addaccount(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String user = tok.nextToken();
			String passwd = tok.nextToken();

			if (user.length() < 4) {
				gm.sendPackets(new S_SystemMessage("[Command] 입력하신 계정명의 자릿수가 너무 짧습니다."));
				gm.sendPackets(new S_SystemMessage("[Command] 최소 4자 이상 입력해 주십시오."));
				return;
			}
			if (passwd.length() < 4) {
				gm.sendPackets(new S_SystemMessage("[Command] 입력하신 암호의 자릿수가 너무 짧습니다."));
				gm.sendPackets(new S_SystemMessage("[Command] 최소 4자 이상 입력해 주십시오."));
				return;
			}

			if (passwd.length() > 12) {
				gm.sendPackets(new S_SystemMessage("[Command] 입력하신 암호의 자릿수가 너무 깁니다."));
				gm.sendPackets(new S_SystemMessage("[Command] 최대 12자 이하로 입력해 주십시오."));
				return;
			}

			if (isDisitAlpha(passwd) == false) {
				gm.sendPackets(new S_SystemMessage("[Command] 암호에 허용되지 않는 문자가 포함 되어 있습니다."));
				return;
			}
			AddAccount(gm, user, passwd, "127.0.0.1", "127.0.0.1");
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage("[Command] .계정추가 [계정명] [암호] 입력"));
		}
	}

	private void 테스트아인(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String user = tok.nextToken();
			int ain = Integer.parseInt(user);
			gm.setAinHasad(0);
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage("[Command] .비번변경 [계정] [현재비번] [바꿀비번] 입력"));
		}
	}
	
	private void changepassword(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String user = tok.nextToken();
			String oldpasswd = tok.nextToken();
			String newpasswd = tok.nextToken();

			if (user.length() < 4) {
				gm.sendPackets(new S_SystemMessage("[Command] 입력하신 계정명의 자릿수가 너무 짧습니다."));
				gm.sendPackets(new S_SystemMessage("[Command] 최소 4자 이상 입력해 주십시오."));
				return;
			}
			if (newpasswd.length() < 4) {
				gm.sendPackets(new S_SystemMessage("[Command] 입력하신 암호의 자릿수가 너무 짧습니다."));
				gm.sendPackets(new S_SystemMessage("[Command] 최소 4자 이상 입력해 주십시오."));
				return;
			}
			if (newpasswd.length() > 12) {
				gm.sendPackets(new S_SystemMessage("[Command] 입력하신 암호의 자릿수가 너무 깁니다."));
				gm.sendPackets(new S_SystemMessage("[Command] 최대 12자 이하로 입력해 주십시오."));
				return;
			}

			if (isDisitAlpha(newpasswd) == false) {
				gm.sendPackets(new S_SystemMessage("[Command] 암호에 허용되지 않는 문자가 포함 되어 있습니다."));
				return;
			}
			chkpassword(gm, user, oldpasswd, newpasswd);
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage("[Command] .비번변경 [계정] [현재비번] [바꿀비번] 입력"));
		}
	}

	private void chkpassword(L1PcInstance gm, String account, String oldpassword, String newpassword) {
		try {
			String password = null;
			java.sql.Connection con = null;
			con = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = null;
			PreparedStatement pstm = null;

			statement = con.prepareStatement("select password from accounts where login='" + account + "'");
			ResultSet rs = statement.executeQuery();

			if (rs.next())
				password = rs.getString(1);
			if (password == null) {
				gm.sendPackets(new S_SystemMessage("[Command] 입력하신 계정은 서버내에 존재 하지 않습니다."));
				return;
			}

			if (!isPasswordTrue(password, oldpassword)) {
				// System.out.println("현재 비번 : " + oldpassword+" - 체크 비번 :
				// "+password);
				gm.sendPackets(new S_SystemMessage("[Command] 기존 계정명의 비밀번호가 일치하지 않습니다. "));
				gm.sendPackets(new S_SystemMessage("[Command] 다시 확인하시고 실행해 주세요."));
				return;
			} else {
				String sqlstr = "UPDATE accounts SET password=password(?) WHERE login=?";
				pstm = con.prepareStatement(sqlstr);
				pstm.setString(1, newpassword);
				pstm.setString(2, account);
				pstm.execute();
				gm.sendPackets(new S_SystemMessage("[Command] 계정명 : " + account + " / 바뀐비밀번호 : " + newpassword));
				gm.sendPackets(new S_SystemMessage("[Command] 비밀번호 변경이 정상적으로 완료되었습니다."));
			}
			rs.close();
			pstm.close();
			statement.close();
			con.close();
		} catch (Exception e) {
		}
	}

	// 패스워드 맞는지 여부 리턴
	public static boolean isPasswordTrue(String Password, String oldPassword) {
		String _rtnPwd = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT password(?) as pwd ");

			pstm.setString(1, oldPassword);
			rs = pstm.executeQuery();
			if (rs.next()) {
				_rtnPwd = rs.getString("pwd");
			}
			if (_rtnPwd.equals(Password)) { // 동일하다면
				return true;
			} else
				return false;
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return false;
	}

	private void CodeTest(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			int codetest = Integer.parseInt(st.nextToken(), 10);
			// pc.sendPackets(new S_ServerMessage(161,"$580","$245", "$247"));
			// int time = 1020;
			// ↓ 테스트할 코드가 보여질 전달 패킷 부분
			pc.sendPackets(new S_Test(pc, codetest));
			// pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_AURA, codetest,
			// time)); // 그신,욕망 버프 테스트
			// pc.sendPackets(new S_CastleMaster(codetest, pc.getId())); // 왕관
			// 테스트
			// pc.sendPackets(new S_StatusReset(pc, codetest, 1)); // 스텟 초기화 테스트
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage("[Command] .코드 [숫자] 입력"));
		}
	}

	private void Clear(L1PcInstance gm) {
		for (L1Object obj : L1World.getInstance().getVisibleObjects(gm, 10)) { // 10
																				// 범위
																				// 내에
																				// 오브젝트를
																				// 찾아서
			if (obj instanceof L1MonsterInstance) { // 몬스터라면
				L1NpcInstance npc = (L1NpcInstance) obj;
				npc.receiveDamage(gm, npc.getMaxHp()); // 데미지
			} else if (obj instanceof L1PcInstance) { // pc라면
				L1PcInstance player = (L1PcInstance) obj;
				player.receiveDamage(player, 0, false); // 데미지
				if (player.getCurrentHp() <= 0) {
					// gm.sendPackets(new S_SkillSound(obj.getId() , 1815)); //
					// 디스
					// Broadcaster.broadcastPacket(gm, new
					// S_SkillSound(obj.getId() , 1815));
				} else {
					// gm.sendPackets(new S_SkillSound(obj.getId() , 1815));
					// Broadcaster.broadcastPacket(gm, new
					// S_SkillSound(obj.getId() , 1815));
				}
			}
		}
	}

	// 인벤삭제
	private void InventoryDelete(L1PcInstance pc, String param) {
		try {

			for (L1ItemInstance item : pc.getInventory().getItems()) {
				if (!item.isEquipped()) {
					pc.getInventory().removeItem(item);
				}
			}

		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".인벤삭제"));
		}
	}

	// 닉네임
	private void GmCharacterNameChange(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String name = "";
			try {
				name = tok.nextToken();
			} catch (Exception e) {
				name = "";
			}

			pc.setCharacterName(name);
			L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getMoveState().getHeading(), false);
			pc.sendPackets(new S_SystemMessage("\\fY캐릭명을 " + name + "으로 변경하였습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".닉네임 [캐릭명]"));
		}
	}

	private void 상점검사(L1PcInstance gm) {
		try {
			ArrayList<Integer> itemids = new ArrayList<Integer>();
			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			@SuppressWarnings("unused")
			int cnt;
			Iterator<Integer> i$;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("SELECT item_id FROM shop");
				rs = pstm.executeQuery();
				while (rs.next()) {
					if (!itemids.contains(Integer.valueOf(rs.getInt("item_id")))) {
						itemids.add(Integer.valueOf(rs.getInt("item_id")));
					}
				}
				cnt = 0;
				for (i$ = itemids.iterator(); i$.hasNext();) {
					int itemid = ((Integer) i$.next()).intValue();
					int 구매최저가 = 최소값(itemid);
					int 판매최고가 = 최대값(itemid);
					if ((구매최저가 != 0) && (구매최저가 < 판매최고가)) {
						gm.sendPackets(new S_SystemMessage("검출됨! [템 " + itemid + " : [구매값 " + 구매최저가 + "] [매입값 " + 판매최고가 + "]"));
					}
					cnt++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				SQLUtil.close(rs, pstm, con);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int 최소값(int itemid) {
		try {
			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("SELECT * FROM shop WHERE item_id = ? AND selling_price NOT IN (-1) ORDER BY selling_price ASC limit 1");
				pstm.setInt(1, itemid);
				rs = pstm.executeQuery();
				if (rs.next()) {
					int temp = 0;
					if (rs.getInt("pack_count") > 1)
						temp = rs.getInt("selling_price") / rs.getInt("pack_count");
					else {
						temp = rs.getInt("selling_price");
					}
					int i = temp;
					return i;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				SQLUtil.close(rs, pstm, con);
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private int 최대값(int itemid) {
		try {
			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("SELECT purchasing_price FROM shop WHERE item_id = ? ORDER BY purchasing_price DESC limit 1");
				pstm.setInt(1, itemid);
				rs = pstm.executeQuery();
				if (rs.next()) {
					int i = rs.getInt("purchasing_price");
					return i;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				SQLUtil.close(rs, pstm, con);
			}
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	private void addBank(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String realName = tok.nextToken();
			String bankName = tok.nextToken();
			String bankNumber = tok.nextToken();
			pc.setRealName((String) realName);
			pc.setBankName((String) bankName);
			pc.setBankNumber((String) bankNumber);
			pc.sendPackets(new S_SystemMessage("예금주[" + realName + "]" + " 은행[" + bankName + "]" + "계좌번호[" + bankNumber + "] 등록 완료."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".계좌등록 [실명] [은행이름] [계좌번호]"));
		}
	}

	private void checkBank(L1PcInstance pc, String param) {
		// TODO Auto-generated method stub
		try {
			if (pc.getRealName() != null || pc.getBankName() != null || pc.getBankNumber() != null) {
				pc.sendPackets(
						new S_SystemMessage("예금주[" + pc.getRealName() + "]" + " 은행[" + pc.getBankName() + "]" + "계좌번호[" + pc.getBankNumber() + "]"));
			} else {
				pc.sendPackets(new S_SystemMessage("계좌를 먼저 등록 하여 주십시오."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".계좌확인"));
		}
	}

	private static String currentTime() {
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(tz);
		int year = cal.get(Calendar.YEAR) - 2000;
		String year2;
		if (year < 10) {
			year2 = "0" + year;
		} else {
			year2 = Integer.toString(year);
		}
		int Month = cal.get(Calendar.MONTH) + 1;
		String Month2 = null;
		if (Month < 10) {
			Month2 = "0" + Month;
		} else {
			Month2 = Integer.toString(Month);
		}
		int date = cal.get(Calendar.DATE);
		String date2 = null;
		if (date < 10) {
			date2 = "0" + date;
		} else {
			date2 = Integer.toString(date);
		}
		return year2 + "/" + Month2 + "/" + date2;
	}

	private void addSell(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			int adena = Integer.parseInt(tok.nextToken());
			int price = Integer.parseInt(tok.nextToken());
			DecimalFormat comentAdena = new DecimalFormat("###,###");
			L1ItemInstance checkadena = pc.getInventory().findItemId(L1ItemId.ADENA);
			if (pc.getInventory().checkItem(40308, 1)) {
				if (pc.getRealName() == null || pc.getBankName() == null || pc.getBankNumber() == null) {
					pc.sendPackets(new S_SystemMessage("판매 등록: 계좌등록을 하지 않으면 물품을 등록 할 수 없습니다."));
					return;
				}
				if (adena > checkadena.getCount()) {
					pc.sendPackets(new S_SystemMessage("판매 등록: 아데나가 부족 합니다."));
					return;
				}
				if (pc.getAdenaSellCount() > 0) {
					pc.sendPackets(new S_SystemMessage("판매 등록: 이미 판매중인 물품이 있습니다."));
					return;
				}
				if (adena < 10000000) {
					pc.sendPackets(new S_SystemMessage("판매 등록: 판매 최소 금액은 1천만 아데나 입니다."));
					return;
				}
				if (adena >= 10000001 && adena <= 19999999 || adena >= 20000001 && adena <= 29999999 || adena >= 30000001 && adena <= 39999999
						|| adena >= 40000001 && adena <= 49999999 || adena >= 50000001 && adena <= 59999999 || adena >= 60000001 && adena <= 69999999
						|| adena >= 70000001 && adena <= 79999999 || adena >= 80000001 && adena <= 89999999 || adena >= 90000001 && adena <= 99999999
						|| adena >= 90000001 && adena <= 99999999) {
					pc.sendPackets(new S_SystemMessage("판매 등록: 판매 금액은 1천만 단위로 등록 하셔야 합니다."));
					return;
				}
				if (adena >= 100000001) {
					pc.sendPackets(new S_SystemMessage("판매 등록: 판매 최대 금액은 1억 아데나 입니다."));
					return;
				}
				pc.getSkillEffectTimerSet().setSkillEffect(12000, 3600 * 1000);
				pc.getInventory().consumeItem(L1ItemId.ADENA, adena);
				writeAdenaSell(pc, adena, price);
				pc.sendPackets(new S_SystemMessage("판매 등록: 아데나 (" + comentAdena.format(adena) + ") 가격: " + comentAdena.format(price) + "원 완료."));
			} else {
				pc.sendPackets(new S_SystemMessage("판매 등록: 아데나가 부족 합니다."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".판매등록 [아데나] [판매금액]"));
		}
	}

	public void writeAdenaSell(L1PcInstance pc, int adena, int price) {
		int count = 0;
		String date = currentTime();
		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		String comentAdena = "";
		// String comentPrice ="";
		DecimalFormat comentPrice = new DecimalFormat("###,###");
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM board_adena ORDER BY id DESC");
			rs = pstm1.executeQuery();
			if (rs.next()) {
				count = rs.getInt("id");
			}
			pstm2 = con.prepareStatement(
					"INSERT INTO board_adena SET id=?, name=?,  date=?, type=?, title=?, content=?, adena=?, pay=?, seller=?, bidder=?, coment=?, step=?,real_name=?,bank_name=?,bank_number=?");
			pstm2.setInt(1, (count + 1));
			pstm2.setString(2, pc.getName());
			pstm2.setString(3, date); // 날짜
			pstm2.setString(4, "adena"); // 타입
			pstm2.setString(5, "판매중"); // 타이틀
			pstm2.setString(6, null); // 내용
			pstm2.setInt(7, adena);
			pstm2.setLong(8, price);
			pstm2.setString(9, pc.getName());
			if (adena == 10000000) {
				comentAdena = "1천만";
			} else if (adena == 20000000) {
				comentAdena = "2천만";
			} else if (adena == 30000000) {
				comentAdena = "3천만";
			} else if (adena == 40000000) {
				comentAdena = "4천만";
			} else if (adena == 50000000) {
				comentAdena = "5천만";
			} else if (adena == 60000000) {
				comentAdena = "6천만";
			} else if (adena == 70000000) {
				comentAdena = "7천만";
			} else if (adena == 80000000) {
				comentAdena = "8천만";
			} else if (adena == 90000000) {
				comentAdena = "9천만";
			} else if (adena == 100000000) {
				comentAdena = "1억";
			}
			pstm2.setString(10, null);
			pstm2.setString(11, comentAdena + " - " + comentPrice.format(price) + "원");
			pstm2.setInt(12, 0);
			pstm2.setString(13, pc.getRealName());
			pstm2.setString(14, pc.getBankName());
			pstm2.setString(15, pc.getBankNumber());
			pstm2.executeUpdate();
			pc.setAdenaSellCount(count + 1);
		} catch (SQLException e) {
			pc.sendPackets(new S_SystemMessage(".판매등록 [판매아데나(숫자기입)] [받을금액(숫자기입)]"));
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	private void adenaSellDone(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			int id = Integer.parseInt(tok.nextToken());
			AdenaSellDone(pc, id);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".판매완료 [게시물 번호]"));
		}
	}

	public void AdenaSellDone(L1PcInstance pc, int id) {
		//
		// StringTokenizer tok = new StringTokenizer(param) ;
		// int id = Integer.parseInt(tok.nextToken()) ;

		String SellerName = null;
		String BidderName = null;
		String title = null;
		int adena = 0;
		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM board_adena WHERE type=?AND id=?");
			pstm1.setString(1, "adena");
			pstm1.setInt(2, id);
			rs = pstm1.executeQuery();

			if (rs.next()) {
				id = rs.getInt("id");
				title = rs.getString("title");
				adena = rs.getInt("adena");
				SellerName = rs.getString("seller");
				BidderName = rs.getString("bidder");
			}

			if (!SellerName.equalsIgnoreCase(pc.getName())) {
				pc.sendPackets(new S_SystemMessage("판매 완료: 자신이 등록한 물품 번호만 판매완료가 가능합니다."));
				return;
			}

			if (pc.getAdenaSellCount() < 0) {
				pc.sendPackets(new S_SystemMessage("판매 완료: 완료 가능한 물품이 없습니다."));
				return;
			}
			if (BidderName == null || title.equalsIgnoreCase("판매중")) {
				pc.sendPackets(new S_SystemMessage("판매 완료: 거래 신청자가 없습니다."));
				return;
			}
			if (title.equalsIgnoreCase("거래완료")) {
				pc.sendPackets(new S_SystemMessage("판매 완료: 이미 판매완료된 물품입니다."));
				return;
			}
			L1PcInstance target = L1World.getInstance().getPlayer(BidderName);
			if (target == null) {
				pc.sendPackets(new S_SystemMessage("판매 완료: 구매자가 접속중이지 않습니다."));
				return;
			}
			if (target != null) {
				// 여기서 아데나 주도록
				target.getInventory().storeItem(40308, adena);
				target.setAdenaBuyCount(0);
				target.sendPackets(new S_SystemMessage("판매 완료: 물품번호: " + id + "에 대한 구매가 완료되었습니다."));
			}
			pstm2 = con.prepareStatement("UPDATE board_adena SET  bidder=?, title=?, step=? WHERE id=?");
			pstm2.setString(1, BidderName);
			pstm2.setString(2, "거래완료");
			pstm2.setInt(3, 2);
			pstm2.setInt(4, id);
			pstm2.executeUpdate();
			pstm2.close();
			pc.setAdenaSellCount(0);
			pc.sendPackets(new S_SystemMessage("판매 완료: 물품번호: " + id + "에 대한 판매가 완료되었습니다."));
			// target.sendPackets(new S_SystemMessage("물품번호: " + id + "에 대한 구매가
			// 완료되었습니다."));
		} catch (SQLException e) {
			pc.sendPackets(new S_SystemMessage(".안대"));
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	private void deleteAdenaSell(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			int id = Integer.parseInt(tok.nextToken());
			CancelAdenaSell(pc, id);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".판매취소 [게시물 번호] 게시물 번호가 0001이면 1만 입력"));
		}
	}

	private void CancelAdenaSell(L1PcInstance pc, int id) {
		// TODO Auto-generated method stub
		String SellerName = null;
		String BidderName = null;
		String title = null;
		String coment = null;
		int adena = 0;
		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM board_adena WHERE type=? AND id=?");
			pstm1.setString(1, "adena");
			pstm1.setInt(2, id);
			rs = pstm1.executeQuery();

			if (rs.next()) {
				title = rs.getString("title");
				SellerName = rs.getString("name");
				BidderName = rs.getString("bidder");
				coment = rs.getString("coment");
				adena = rs.getInt("adena");
			}
			if (title.equalsIgnoreCase("거래중")) {
				L1PcInstance target = L1World.getInstance().getPlayer(BidderName);
				if (target != null) {
					target.sendPackets(new S_Message_YN(622, "상대방이 판매 취소를 원합니다 동의 하시겠습니까?"));
					target.setAttrMsgType(1);
					pc.sendPackets(new S_SystemMessage("판매 취소: 상대방의 동의를 얻고 있습니다. "));
				} else {
					pc.sendPackets(new S_SystemMessage("판매 취소: 구매자가 접속중이지 않습니다. "));
				}
				return;
			}
			BoardTable.getInstance().deleteAdena(pc.getAdenaSellCount());
			pc.getInventory().storeItem(40308, adena);
			pstm2 = con.prepareStatement("UPDATE board_adena SET bidder=?, title=?, step=? WHERE id=?");
			pstm2.setString(1, BidderName);
			pstm2.setString(2, "판매취소");
			pstm2.setInt(3, 3);
			pstm2.setInt(4, id);
			pstm2.executeUpdate();
			pc.setAdenaSellCount(0);
			pc.sendPackets(new S_SystemMessage("판매 취소: 등록하신 물품이 취소 되었습니다."));
			pstm2.close();
		} catch (SQLException e) {
			pc.sendPackets(new S_SystemMessage(".구매신청 [게시물 번호] 게시물 번호가 0001이면 1만 입력."));
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	private void requestAdena(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			int id = Integer.parseInt(tok.nextToken());
			writeAdenaBuy(pc, id);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".구매신청 [게시물 번호] 게시물 번호가 0001이면 1만 입력"));
		}
	}

	public void writeAdenaBuy(L1PcInstance pc, int id) {

		String SellerName = null;
		String BidderName = null;
		String title = null;
		String coment = null;

		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM board_adena WHERE type=? AND id=?");
			pstm1.setString(1, "adena");
			pstm1.setInt(2, id);
			rs = pstm1.executeQuery();

			if (rs.next()) {
				title = rs.getString("title");
				SellerName = rs.getString("name");
				BidderName = rs.getString("bidder");
				coment = rs.getString("coment");
			}
			if (SellerName == null) {
				pc.sendPackets(new S_SystemMessage("구매 신청: 신청할 게시물 번호가 없습니다. "));
				return;
			}
			if (title.equalsIgnoreCase("거래중")) {
				pc.sendPackets(new S_SystemMessage("구매 신청: 이미 거래중인 물품입니다."));
				return;
			}
			if (title.equalsIgnoreCase("거래완료")) {
				pc.sendPackets(new S_SystemMessage("구매 신청: 이미 판매완료된 물품입니다."));
				return;
			}
			if (SellerName.equalsIgnoreCase(pc.getName())) {
				pc.sendPackets(new S_SystemMessage("구매 신청: 자신이 등록한 게시물은 구매 신청 할 수 없습니다."));
				return;
			}
			if (pc.getAdenaBuyCount() > 0) {
				pc.sendPackets(new S_SystemMessage("구매 신청: 이미 거래중인 물품이 있습니다."));
				return;
			}
			L1PcInstance target = L1World.getInstance().getPlayer(SellerName);
			if (target == null) {
				pc.sendPackets(new S_SystemMessage("구매 신청: 판매자가 접속중이지 않습니다. "));
				return;
			}
			target.setAdenaTrade(true);
			pstm2 = con.prepareStatement("UPDATE board_adena SET  bidder=?, title=?, step=? WHERE id=?");
			pstm2.setString(1, pc.getName());
			pstm2.setString(2, "거래중");
			pstm2.setInt(3, 1);
			pstm2.setInt(4, id);
			pstm2.executeUpdate();
			// 여기에 변수하나 주자
			target.sendPackets(new S_SystemMessage("구매 신청: 물품번호 " + id + " 에 대한 구매 신청이 왔습니다."));
			pc.sendPackets(new S_SystemMessage("구매 신청: 물품번호 " + id + " 에 대한 구매 신청이 완료 되었습니다."));
			pc.setAdenaBuyCount(id);
			pstm2.close();

		} catch (SQLException e) {
			pc.sendPackets(new S_SystemMessage(".구매신청 [게시물 번호] 게시물 번호가 0001이면 1만 입력."));
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	public static void Event_System(L1PcInstance gm, String param) {
		try {
			StringTokenizer tokenizer = new StringTokenizer(param);
			String code = tokenizer.nextToken();
			if (code.equalsIgnoreCase("리로드")) {
				EventSystemLoader.reload();
				gm.sendPackets(new S_SystemMessage("이벤트 시스템을 리로드 합니다."));
				return;
			}
			int i = Integer.parseInt(tokenizer.nextToken(), 10);

			if (EventSystemLoader.getInstance().getEventSystemSize() < i) {
				gm.sendPackets(new S_SystemMessage("올바른 이벤트 넘버를 입력해 주시기 바랍니다."));
				return;
			}
			if (code.equalsIgnoreCase("시작")) {
				EventSystemTimeController.getInstance().initScs(i);
				GeneralThreadPool.getInstance().execute(new EventSystemSpawner(i, EventSystemTimeController.FS_START));
			} else if (code.equalsIgnoreCase("종료")) {
				GeneralThreadPool.getInstance().execute(new EventSystemSpawner(i, EventSystemTimeController.FS_END));
			}
		} catch (Exception e) {
			EventSystemLoader.getInstance().getEventSystemInfoCheck(gm);
			gm.sendPackets(new S_SystemMessage(".이벤트 [시작/종료/리로드] 이벤트넘버"));
		}
	}

	private void chaTrade2(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			int ChaId = Integer.parseInt(st.nextToken());
			if (isTradeCha(ChaId)) {
				String ChaName = CharacterTable.getTradeChaName(ChaId);

				L1PcInstance target = CharacterTable.getInstance().loadCharacter(ChaName);
				if (target != null) {
					target.getInventory().loadItems();
					pc.sendPackets(new S_InvGfx(target, target.getInventory()));
					StringBuffer info = new StringBuffer();
					pc.sendPackets(new S_SystemMessage("-----------캐릭정보--------------"));
					info.append("직업:" + target.getClassName() + " / ");
					int lv = target.getLevel();
					int currentLvExp = ExpTable.getExpByLevel(lv);
					int nextLvExp = ExpTable.getExpByLevel(lv + 1);
					double neededExp = nextLvExp - currentLvExp;
					double currentExp = target.getExp() - currentLvExp;
					int per = (int) ((currentExp / neededExp) * 100.0);
					info.append("레벨:" + target.getLevel() + "." + per + " / ");

					info.append("엘릭서복용:" + target.getAbility().getElixirCount() + "개");
					pc.sendPackets(new S_SystemMessage(info.toString()));

					StringBuffer info2 = new StringBuffer();
					info2.append("힘:" + target.getAbility().getBaseStr() + ", ");
					info2.append("덱스:" + target.getAbility().getBaseDex() + ", ");
					info2.append("인트:" + target.getAbility().getBaseInt() + ", ");
					info2.append("위즈:" + target.getAbility().getBaseWis() + ", ");
					info2.append("콘:" + target.getAbility().getBaseCon() + ", ");
					info2.append("카리:" + target.getAbility().getBaseCha() + ", ");
					pc.sendPackets(new S_SystemMessage(info2.toString()));

					StringBuffer info3 = new StringBuffer();
					info3.append("마법정보:");

					Connection con = null;
					PreparedStatement pstm = null;
					ResultSet rs = null;
					try {

						con = L1DatabaseFactory.getInstance().getConnection();
						pstm = con.prepareStatement("SELECT * FROM character_skills WHERE char_obj_id=?");
						pstm.setInt(1, target.getId());
						rs = pstm.executeQuery();
						while (rs.next()) {
							int skillId = rs.getInt("skill_id");
							switch (skillId) {
							case 28: // 뱀파
							case 60: // 인비지
							case 65: // 라톰
							case 67: // 셰이프 체인지
							case 68: // 이뮨
							case 71: // 디케이
							case 74: // 미티어
							case 77: // 디스
							case 78: // 앱솔
							case 79: // 어벤
							case 87: // 스턴
							case 90: // 솔리드 캐리지
							case 91: // 카베
							case 105: // 더블브레이크
							case 107: // 쉐도우펭
							case 114: // 글로잉 오라
							case 115: // 샤이닝오라
							case 117: // 브레이브 오라
							case 146: // 블러드 투 소울
							case 157: // 어바
							case 164: // 블레싱
							case 166: // 스톰샷
							case 174: // 스트라이커 게일
							case 175: // 소프
								L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
								info3.append(l1skills.getName() + ", ");
								break;
							}
						}
					} catch (SQLException e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					} finally {
						SQLUtil.close(rs);
						SQLUtil.close(pstm);
						SQLUtil.close(con);
					}
					pc.sendPackets(new S_SystemMessage(info3.toString()));

				} else {
					pc.sendPackets(new S_SystemMessage(ChaId + "는 등록되지 않았습니다."));
				}
			} else {
				pc.sendPackets(new S_SystemMessage(ChaId + "는 등록되지 않았습니다."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".캐릭조회 [id]"));
		}
	}

	private void chaTrade(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String name = st.nextToken();
			if (!pc.getInventory().checkItem(50111, 1)) {
				pc.sendPackets(new S_SystemMessage("빈 캐릭터 인형을 구입 후 등록해 주세요."));
				return;
			}

			if (pc.getName().equalsIgnoreCase(name)) {
				pc.sendPackets(new S_SystemMessage("접속한 캐릭터는 등록이 불가능합니다."));
				return;
			}

			// 계정명을 받아온다.
			String AccName = getTradeAccName(name);

			if (pc.getAccountName().equalsIgnoreCase(AccName)) {
				pc.sendPackets(new S_SystemMessage("내 계정에 존재한다."));
				L1PcInstance TradePc = CharacterTable.getInstance().loadCharacter(name);
				if (TradePc != null) {
					if (TradePc.getClanid() != 0) {
						pc.sendPackets(new S_SystemMessage("혈맹을 탈퇴한 후 등록해주세요."));
						return;
					}

					// 캐릭터 테이블 계정명을 비운다.
					CharacterTable.TradeAccUpdate("", name);
					L1ItemInstance TradeItem = pc.getInventory().findItemId(50111);
					TradeItem.setTradeCha(TradePc.getId());

					pc.getInventory().updateItem(TradeItem, L1PcInventory.COL_TRADE_CHA);
					pc.getInventory().saveItem(TradeItem, L1PcInventory.COL_TRADE_CHA);

					LineageClient client = pc.getNetConnection();
					/**
					 * client.CharReStart(true); client.sendPacket(new
					 * S_PacketBox(S_PacketBox.LOGOUT)); client.loginStatus2 =
					 * 1;
					 */
					pc.save();
					pc.saveInventory();

					// client.quitGame(pc);
					// pc.logout();
					// client.setActiveChar(null);

					client.kick();

					// new C_NoticeClick(client);
				}
			} else {
				pc.sendPackets(new S_SystemMessage("자신의 계정에 존재하는 캐릭만 등록가능합니다."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".캐릭등록 [캐릭명]"));
		}
	}

	private boolean isTradeCha(int chaid) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_items WHERE trade_char_id=?");
			pstm.setInt(1, chaid);
			rs = pstm.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			_log.warning("could not check existing charname:" + e.getMessage());
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return false;
	}

	private String getTradeAccName(String name) {
		String AccName = "";
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT account_name FROM characters WHERE char_name=?");
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			if (rs.next()) {
				AccName = rs.getString(1);
			}
		} catch (SQLException e) {
			_log.warning("could not check existing charname:" + e.getMessage());
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return AccName;
	}

	private void 자동드다(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String OnOff = st.nextToken();
			if (OnOff.equalsIgnoreCase("켬")) {
				pc.setDragonDia(true);
				pc.sendPackets(new S_SystemMessage("자동으로 드래곤의 다이아몬드를 복용합니다."));
			} else if (OnOff.equalsIgnoreCase("끔")) {
				pc.setDragonDia(false);
				pc.sendPackets(new S_SystemMessage("자동 드다 기능이 꺼졌습니다."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".자동드다 [켬/끔]"));
		}
	}

	private static void initialize_user_private_probability(MJCommandArgs args) {
		try {
			String type_name = args.nextString();
			if (type_name.equalsIgnoreCase("전체")) {
				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					pc.truncate_private_probability();
				}
				Updator.truncate("characters_private_probability");
				args.notify("모든 캐릭터들의 확률이 초기화되었습니다.");
			} else {
				String character_name = args.nextString();
				L1PcInstance pc = L1World.getInstance().getPlayer(character_name);
				if (pc == null) {
					args.notify(String.format("%s님을 찾을 수 없습니다.", character_name));
					return;
				}
				pc.truncate_private_probability();
				Updator.exec("delete from characters_private_probability where object_id=?", new Handler() {
					@Override
					public void handle(PreparedStatement pstm) throws Exception {
						pstm.setInt(1, pc.getId());
					}
				});
				args.notify(String.format("%s님의 확률이 초기화 되었습니다.", character_name));
			}
		} catch (Exception e) {
			args.notify(".확률초기화 캐릭터 [캐릭터명]");
			args.notify(".확률초기화 전체");
		}
	}

	private static void update_user_private_probability(MJCommandArgs args) {
		try {
			String character_name = args.nextString();
			int skill_id = args.nextInt();
			int added_probability = args.nextInt();
			L1PcInstance pc = L1World.getInstance().getPlayer(character_name);
			if (pc == null) {
				args.notify(String.format("%s님을 찾을 수 없습니다.", character_name));
				return;
			}

			pc.add_private_probability(skill_id, added_probability);
			args.notify(String.format("%s님은 %d번 스킬 사용에 대해 추가 확률 %d%%를 적용했습니다.", character_name, skill_id, added_probability));
		} catch (Exception e) {
			args.notify(".확률 [캐릭터명] [스킬아이디] [추가확률]");
		}
	}

	private static void show_user_private_probability(MJCommandArgs args) {
		try {
			String character_name = args.nextString();
			int skill_id = args.nextInt();
			L1PcInstance pc = L1World.getInstance().getPlayer(character_name);
			if (pc == null) {
				args.notify(String.format("%s님을 찾을 수 없습니다.", character_name));
				return;
			}
			args.notify(String.format("%s님의 %d번 스킬은 %d%%의 추가 확률이 적용되어 있습니다. ", character_name, skill_id, pc.get_private_probability(skill_id)));
		} catch (Exception e) {
			args.notify(".확률확인 [캐릭터명] [스킬아이디]");
		}
	}

	private void autobot(L1PcInstance gm) {
		// TODO 자동 생성된 메소드 스텁
		try {
			if (fishBot) {
				gm.sendPackets(new S_SystemMessage("낚시 봇은 현재 가동중입니다."), true);
				return;
			}
			fishBot = true;
			Robot_Fish.getInstance().start_spawn();
			gm.sendPackets(new S_SystemMessage("낚시봇 생성을 시작합니다."), true);
			return;
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(
					".낚시로봇 입력해주세요."), true);
		}
	}
	
	private static final String 멘트[] = { "손", "손!", "손~", "손!!", "손~!", "발", "발~", "손~~", "발" };
	private static Random _random = new Random(System.nanoTime());
	class 로봇부처핸섬 implements Runnable {
		public 로봇부처핸섬() {
		}

		public void run() {
			try {
				for (L1RobotInstance rob : L1World.getInstance().getAllRobot()) {
					int ran = _random.nextInt(100) + 1;
					if (ran < 50) {
						continue;
					}
					Thread.sleep(_random.nextInt(100) + 50);
					for (L1PcInstance listner : L1World.getInstance()
							.getAllPlayers()) {
						listner.sendPackets(
								new S_ChatPacket(rob, 멘트[_random.nextInt(멘트.length)], Opcodes.S_OPCODE_MSG, 3), true);
					}
				}
			} catch (Exception e) {
			}
		}
	}
	
}
