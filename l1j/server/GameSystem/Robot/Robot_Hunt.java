package l1j.server.GameSystem.Robot;

import static l1j.server.server.model.skill.L1SkillId.HASTE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;


import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.GMCommands;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class Robot_Hunt {

	public static boolean 기감 = false;
	private static Random _random = new Random(System.nanoTime());
	private static Queue<L1RobotInstance> _queue;
	private static Robot_Hunt _instance;
	private int itemid = 0;

	public static Robot_Hunt getInstance() {
		if (_instance == null) {
			_instance = new Robot_Hunt();
		}
		return _instance;
	}

	public Robot_Hunt() {
		_queue = new ConcurrentLinkedQueue<L1RobotInstance>();
		loadbot();
		ArrayList<L1RobotInstance> list = new ArrayList<L1RobotInstance>();
		while (_queue.size() > 0) {
			L1RobotInstance ro = _queue.poll();
			list.add(ro);
		}
		Collections.shuffle(list);
		for (L1RobotInstance ro : list) {
			_queue.offer(ro);
		}
	}

	public void put(L1RobotInstance bot) {
		synchronized (_queue) {
			_queue.offer(bot);

		}
	}

	// 용계리뉴얼로인해 제외 15마리
	private static final String[] mapName = { 
			"기감1층", "기감2층", 
			"용던1층", "용던2층", "용던3층", "용던4층", "용던5층", "용던6층", "용던7층",
			"본던1층", "본던2층", "본던3층", "본던4층", "본던5층", "본던6층", "본던7층" };

	private static final int[] mapCount = { 
			10, 0, 
			10, 0, 0, 0, 0, 0, 0, 
			10, 0, 0, 0, 0, 0, 0,
			};

	public void start_spawn() {
		for (int a = 0; a < mapCount.length; a++) {
			for (int i = 0; i < mapCount[a]; i++) {
				L1RobotInstance bot = _queue.poll();
				if (bot == null) {
					continue;
				}
				GeneralThreadPool.getInstance().schedule(new botVisible(bot, mapName[a]),
						6000 + (_random.nextInt(120) + 1)); // 원래 120
			}
		}
	}

	private void direct_spawn(String 위치) {
		if (!GMCommands.huntBot)
			return;
		synchronized (_queue) {
			L1RobotInstance bot = _queue.poll();
			if (bot == null)
				return;
			GeneralThreadPool.getInstance().schedule(new botVisible(bot, 위치), 1 * (_random.nextInt(2) + 1));
		}
	}

	public void delay_spawn(String 위치, int time) {
		if (!GMCommands.huntBot)
			return;
		synchronized (_queue) {
			L1RobotInstance bot = _queue.poll();
			if (bot == null)
				return;
			GeneralThreadPool.getInstance().schedule(new botVisible(bot, 위치), time);
		}
	}

	// private static boolean spawning = false;
	class botVisible implements Runnable {
		private L1RobotInstance bot;
		private String 사냥위치;

		public botVisible(L1RobotInstance bot, String _사냥위치) {
			this.bot = bot;
			this.사냥위치 = _사냥위치;
		}

		@Override
		public void run() {
			// TODO 자동 생성된 메소드 스텁
			try {
				L1PcInstance rob = L1World.getInstance().getPlayer(bot.getName());
				if (rob != null) {
					put(bot);
					direct_spawn(사냥위치);
					return;
				}
				if (!GMCommands.huntBot) {
					put(bot);
					return;
				}
				int map = _random.nextInt(5);
				while (true) {
					// 좌표 설정
					switch (map) {
					case 0:// 기란
						bot.setX(33432 + _random.nextInt(30));
						bot.setY(32811 + _random.nextInt(30));
						break;
					case 1:// 은기사
						bot.setX(33078 + _random.nextInt(10));
						bot.setY(33386 + _random.nextInt(20));
						break;
					case 2:// 오렌
						bot.setX(34055 + _random.nextInt(30));
						bot.setY(32278 + _random.nextInt(30));
						break;
					case 3:// 아덴
					case 4:// 아덴
						bot.setX(33927 + _random.nextInt(10));
						bot.setY(33348 + _random.nextInt(10));
						break;
					case 5:// 하이네
						bot.setX(33614 + _random.nextInt(20));
						bot.setY(33245 + _random.nextInt(20));
						break;
					default:
						break;
					}
					bot.setMap((short) 4); // 봇시작위치
					boolean ck = false;
					if (ck)
						continue;
					if (bot.getMap().isInMap(bot.getX(), bot.getY()) && bot.getMap().isPassable(bot.getX(), bot.getY()))
						break;
					Thread.sleep(100);
				}

				bot.getAC().setAc(-80);
				if (bot.getCurrentWeapon() == 20) {
					bot.addBowHitup(50);
					bot.addBowDmgup(100);
				} else {
					bot.addHitup(50);
					bot.addDmgup(100);
				}
				bot.사냥봇 = true;
				bot.사냥봇_위치 = 사냥위치;
				bot._스레드종료 = false;
				bot.getMoveState().setHeading(_random.nextInt(8));
				bot.getMoveState().setMoveSpeed(1);
				bot.getSkillEffectTimerSet().setSkillEffect(HASTE, (_random.nextInt(400) + 1700) * 1000);
				if (bot.isKnight() || bot.isCrown()) {
					bot.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_BRAVE,
							(_random.nextInt(600) + 400) * 1000);
					bot.getMoveState().setBraveSpeed(1);
				} else if (bot.isElf()) {
					int ran = _random.nextInt(100) + 1;
					if (bot.getCurrentWeapon() == 20) {
						if (ran < 50) {
							bot.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_ELFBRAVE,
									(_random.nextInt(600) + 400) * 1000);
							Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(), 751), true);
							Broadcaster.broadcastPacket(bot, new S_SkillBrave(bot.getId(), 3, 0), true);
						} else {
							if (!bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE)) {
								bot.getMoveState().setBraveSpeed(1);
								Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(), 16531), true);
								Broadcaster.broadcastPacket(bot, new S_SkillBrave(bot.getId(), 10, 0), true);
							}
						}
					} else {
						if (!bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.FIRE_BLESS)
								&& !bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE)) {
							bot.getSkillEffectTimerSet().setSkillEffect(L1SkillId.FIRE_BLESS,
									(_random.nextInt(50) + 250) * 1000);
							bot.getMoveState().setBraveSpeed(1);
							Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(), 11775), true);
							Broadcaster.broadcastPacket(bot, new S_SkillBrave(bot.getId(), 1, 0), true);
						}

					}
				} else if (bot.isDarkelf()) {
					bot.getSkillEffectTimerSet().setSkillEffect(L1SkillId.MOVING_ACCELERATION,
							(_random.nextInt(600) + 400) * 1000);
					bot.getMoveState().setBraveSpeed(4);
				}
				L1Clan clan = L1World.getInstance().getClan(bot.getClanname());
				if (clan != null) {
					if (bot.getClanid() == clan.getClanId() && // 크란을 해산해, 재차,
																// 동명의 크란이 창설되었을
																// 때의 대책
							bot.getClanname().toLowerCase().equals(clan.getClanName().toLowerCase())) {
						clan.addOnlineClanMember(bot.getName(), bot);
						for (L1PcInstance clanMember : clan.getOnlineClanMember()) {
							if (clanMember.getId() != bot.getId()) {
								// 지금, 혈맹원의%0%s가 게임에 접속했습니다.
								clanMember.sendPackets(
										new S_SystemMessage("혈맹원 " + bot.getName() + "님께서 방금 게임에 접속하셨습니다."), true);
							}
						}
					}
				}
				Robot.poly(bot);
				bot.getSkillEffectTimerSet().setSkillEffect(L1SkillId.SHAPE_CHANGE, 1800 * 1000);
				L1World.getInstance().storeObject(bot);
				L1World.getInstance().addVisibleObject(bot);
				Robot.clan_join(bot);
				bot.updateconnect(true);
				bot.딜레이(3000 + _random.nextInt(15000));
				// if ((_random.nextInt(100)+1) >= 60) //타격귀환 원래 false
				bot.타격귀환무시 = true;
				bot.Hunt_Exit_Time = System.currentTimeMillis() + (600000000 * (60 + _random.nextInt(40)));
				bot.startAI();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void loadbot() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM robots");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1RobotInstance newPc = new L1RobotInstance();
				newPc.setId(ObjectIdFactory.getInstance().nextId());
				newPc.setAccountName("");
				newPc.setName(rs.getString("name"));
				// rs.getInt("step");
				int level = rs.getInt("level");
				newPc.setHighLevel(level);
				newPc.setLevel(level);
				newPc.setExp(ExpTable.getExpByLevel(level) + _random.nextInt(ExpTable.getNeedExpNextLevel(level)));
				newPc.getAC().setAc(-80);
				newPc.addHitup(20);
				newPc.addBowHitup(20);

				if (_random.nextInt(100) > 85)
					newPc.setLawful(12767);
				else
					newPc.setLawful(32767);
				newPc.setDead(false);
				newPc.getResistance().addMr(100 + _random.nextInt(30));
				newPc.setTitle(rs.getString("title"));
				newPc.set_sex(rs.getInt("sex"));

				newPc.setClassId(rs.getInt("class"));
				newPc.getGfxId().setTempCharGfx(rs.getInt("class"));
				newPc.getGfxId().setGfxId(rs.getInt("class"));

				if (newPc.isKnight()) {
					newPc.setCurrentWeapon(50);
					newPc.setType(1);
				} else if (newPc.isElf()) {
					newPc.setCurrentWeapon(20);
					newPc.setType(2);
				} else if (newPc.isDarkelf()) {
					newPc.setCurrentWeapon(54);
					newPc.setType(4);
				} else if (newPc.isCrown()) {
					newPc.setCurrentWeapon(4);
					newPc.setType(0);
				} else if (newPc.isWizard()) {
					newPc.setCurrentWeapon(40);
					newPc.setType(3);
				}
				StatSetting(newPc);
				ItemSetting(newPc);
				HpMpUp(newPc);

				newPc.getMoveState().setMoveSpeed(0);
				newPc.getMoveState().setBraveSpeed(0);
				newPc.getMoveState().setHeading(0);

				newPc.set_food(225);
				newPc.setClanid(rs.getInt("clanid"));
				newPc.setClanname(rs.getString("clanname"));
				newPc.setClanRank(0);
				newPc.setElfAttr(0);
				newPc.set_PKcount(0);
				newPc.setExpRes(0);
				newPc.setPartnerId(0);
				newPc.setAccessLevel((short) 0);
				newPc.setGm(false);
				newPc.setMonitor(false);
				newPc.setHomeTownId(0);
				newPc.setContribution(0);
				newPc.setHellTime(0);
				newPc.setBanned(false);
				newPc.setKarma(0);
				newPc.setReturnStat(0);
				newPc.setGmInvis(false);
				newPc.noPlayerCK = true;
				newPc.setActionStatus(0);
				newPc.setOnlineStatus(1);
				newPc.setRobot(true);
				newPc.getLight().turnOnOffLight();

				if (_random.nextInt(1000) > 200) {
					newPc.setKills(0);
					newPc.setDeaths(0);
				} else {
					newPc.setKills(0);
					newPc.setDeaths(_random.nextInt(10));
				}
				newPc.setNetConnection(null);
				put(newPc);
			}
		} catch (SQLException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private static void StatSetting(L1RobotInstance bot) {
		if (bot.isKnight()) {
			bot.getAbility().setBaseStr(20);
			bot.getAbility().setStr(bot.getLevel() - 10);
			bot.getAbility().setBaseCon(14);
			bot.getAbility().setCon(14);
			bot.getAbility().setBaseDex(12);
			bot.getAbility().setDex(12);
			bot.getAbility().setBaseCha(12);
			bot.getAbility().setCha(12);
			bot.getAbility().setBaseInt(8);
			bot.getAbility().setInt(8);
			bot.getAbility().setBaseWis(9);
			bot.getAbility().setWis(9);
		} else if (bot.isElf()) {
			if (bot.getCurrentWeapon() != 20) {
				bot.getAbility().setBaseStr(18);
				bot.getAbility().setStr(bot.getLevel() - 10);
				bot.getAbility().setBaseCon(12);
				bot.getAbility().setCon(12);
				bot.getAbility().setBaseDex(12);
				bot.getAbility().setDex(12);
			} else {
				bot.getAbility().setBaseStr(11);
				bot.getAbility().setStr(11);
				bot.getAbility().setBaseCon(13);
				bot.getAbility().setCon(13);
				bot.getAbility().setBaseDex(18);
				bot.getAbility().setDex(bot.getLevel() - 10);
			}
			bot.getAbility().setBaseCha(9);
			bot.getAbility().setCha(9);
			bot.getAbility().setBaseInt(12);
			bot.getAbility().setInt(12);
			bot.getAbility().setBaseWis(12);
			bot.getAbility().setWis(12);
		} else if (bot.isWizard()) {
			bot.getAbility().setBaseStr(8);
			bot.getAbility().setStr(8);
			bot.getAbility().setBaseCon(16);
			bot.getAbility().setCon(16);
			bot.getAbility().setBaseDex(7);
			bot.getAbility().setDex(7);
			bot.getAbility().setBaseCha(8);
			bot.getAbility().setCha(8);
			bot.getAbility().setBaseInt(18);
			bot.getAbility().setInt(bot.getLevel() - 10);
			bot.getAbility().setBaseWis(18);
			bot.getAbility().setWis(bot.getLevel() - 55);
			bot.getAbility().addSp(45);
		} else if (bot.isDarkelf()) {
			bot.getAbility().setBaseStr(18);
			bot.getAbility().setStr(bot.getLevel() - 10);
			bot.getAbility().setBaseCon(12);
			bot.getAbility().setCon(12);
			bot.getAbility().setBaseDex(15);
			bot.getAbility().setDex(15);
			bot.getAbility().setBaseCha(9);
			bot.getAbility().setCha(9);
			bot.getAbility().setBaseInt(11);
			bot.getAbility().setInt(11);
			bot.getAbility().setBaseWis(10);
			bot.getAbility().setWis(10);
		}
	}

	private int Robot_Weapon(L1RobotInstance bot) {
		if (bot.isKnight()) {
			itemid = 61; // 집행검
		} else if (bot.isElf()) {
			itemid = 450009; // 격노
		} else if (bot.isDarkelf()) {
			itemid = 450010; // 포효
		} else if (bot.isCrown()) {
			itemid = 12; // 군주의번개검
		} else if (bot.isWizard()) {
			itemid = 134; // 수결지
		}
		return itemid;
	}

	private void ItemSetting(L1RobotInstance bot) {
		if (bot.isKnight()) {
			L1ItemInstance item = ItemTable.getInstance().createItem(Robot_Weapon(bot));
			item.setEnchantLevel(_random.nextInt(10));
			bot.setWeapon(item);
			bot.getInventory().setEquipped(item, true);
		} else if (bot.isElf()) {
			L1ItemInstance item = ItemTable.getInstance().createItem(Robot_Weapon(bot));
			item.setEnchantLevel(_random.nextInt(10));
			bot.setWeapon(item);
			bot.getInventory().setEquipped(item, true);
		} else if (bot.isWizard()) {
			L1ItemInstance item = ItemTable.getInstance().createItem(Robot_Weapon(bot));
			item.setEnchantLevel(_random.nextInt(10));
			bot.setWeapon(item);
			bot.getInventory().setEquipped(item, true);
		} else if (bot.isDarkelf()) {
			L1ItemInstance item = ItemTable.getInstance().createItem(Robot_Weapon(bot));
			item.setEnchantLevel(_random.nextInt(10));
			bot.setWeapon(item);
			bot.getInventory().setEquipped(item, true);
		}
	}

	private static void HpMpUp(L1RobotInstance bot) {
		bot.addBaseMaxHp((short) (35 + _random.nextInt(5)));
		bot.setCurrentHp(bot.getBaseMaxHp());
		bot.addBaseMaxMp((short) (14 + _random.nextInt(7)));
		bot.setCurrentMp(bot.getBaseMaxMp());
		for (int i = 0; i < bot.getLevel(); i++) {
			if (bot.isCrown() || bot.isKnight()) {
				int randomHp = 40;
				int randomMp = 100;
				bot.addBaseMaxHp((short) randomHp);
				bot.addBaseMaxMp((short) randomMp);
				bot.setCurrentHp(bot.getBaseMaxHp());
				bot.setCurrentMp(bot.getBaseMaxMp());
			} else {
				int randomHp = 30;
				int randomMp = 100;
				bot.addBaseMaxHp((short) randomHp);
				bot.addBaseMaxMp((short) randomMp);
				bot.setCurrentHp(bot.getBaseMaxHp());
				bot.setCurrentMp(bot.getBaseMaxMp());
			}
		}
	}
}
