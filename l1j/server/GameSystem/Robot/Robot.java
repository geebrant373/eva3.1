package l1j.server.GameSystem.Robot;

import static l1j.server.server.model.skill.L1SkillId.HASTE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_UNDERWATER_BREATH;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javolution.util.FastMap;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharTitle;
import l1j.server.server.serverpackets.S_DRAGONPERL;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.utils.SQLUtil;

public class Robot {

	private static Random _random = new Random(System.currentTimeMillis());
	public static boolean 인형 = false;
	
	
	
	public static void poly(L1RobotInstance bot) {
		polyNormal(bot);
	}

	private static void polyNormal(L1RobotInstance bot) {
		if (bot.getLevel() < 70) {
			polyNormal51(bot);
		} else if (bot.getLevel() >= 70 && bot.getLevel() < 80) { // 80레벨 이상
			polyNormal75(bot);
		} else {
			polyNormal80(bot);
		}
	}

	private static void polyNormal51(L1RobotInstance bot) { // 로봇변신
		if (bot.isElf() && bot.getCurrentWeapon() == 20) {
			bot.getGfxId().setTempCharGfx(6160);
		} else if (bot.isCrown() || bot.isKnight()) {
			bot.getGfxId().setTempCharGfx(6157);
		} else if (bot.isDarkelf()) {
			bot.getGfxId().setTempCharGfx(6158);
		} else if (bot.isWizard()) {
			bot.getGfxId().setTempCharGfx(6157);
		}
	}

	private static void polyNormal75(L1RobotInstance bot) { // 로봇변신
		if (bot.isElf() && bot.getCurrentWeapon() == 20) {
			bot.getGfxId().setTempCharGfx(6269);
		} else if (bot.isCrown() || bot.isKnight()) {
			bot.getGfxId().setTempCharGfx(6267);
		} else if (bot.isDarkelf()) {
			bot.getGfxId().setTempCharGfx(6279);
		} else if (bot.isWizard()) {
			bot.getGfxId().setTempCharGfx(6268);
		}
	}

	private static void polyNormal80(L1RobotInstance bot) { // 로봇변신
		if (bot.isElf() && bot.getCurrentWeapon() == 20) {
			bot.getGfxId().setTempCharGfx(6278);
		} else if (bot.isCrown() || bot.isKnight()) {
			bot.getGfxId().setTempCharGfx(6276);
		} else if (bot.isDarkelf()) {
			bot.getGfxId().setTempCharGfx(6282);
		} else if (bot.isWizard()) {
			bot.getGfxId().setTempCharGfx(6277);
		}
	}

	public static boolean 속도버프(L1RobotInstance bot) {
		// TODO 자동 생성된 메소드 스텁
		// 디케이 아닐때
		if (bot.getMap().isUnderwater()) {
			if (!bot.getSkillEffectTimerSet().hasSkillEffect(
					STATUS_UNDERWATER_BREATH)) {
				Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(),
						190), true);
				bot.getSkillEffectTimerSet().setSkillEffect(
						STATUS_UNDERWATER_BREATH, 1800 * 1000);
			}
		}
		if (bot.getMoveState().getMoveSpeed() == 0
				&& !bot.getSkillEffectTimerSet().hasSkillEffect(HASTE)
				&& !bot.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.DECAY_POTION)) {
			bot.getMoveState().setMoveSpeed(1);
			bot.getSkillEffectTimerSet().setSkillEffect(HASTE,
					(_random.nextInt(400) + 1700) * 1000);
			Broadcaster.broadcastPacket(bot,
					new S_SkillSound(bot.getId(), 191), true);
		}
		if (_random.nextInt(100) > 10)
			return false;
		if (bot.isKnight() || bot.isCrown()) {
			// 디케이아닐때
			if (!bot.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.STATUS_BRAVE)
					&& !bot.getSkillEffectTimerSet().hasSkillEffect(
							L1SkillId.DECAY_POTION)) {
				bot.getSkillEffectTimerSet().setSkillEffect(
						L1SkillId.STATUS_BRAVE,
						(_random.nextInt(600) + 400) * 1000);
				bot.getMoveState().setBraveSpeed(1);
				Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(),
						751), true);
				Broadcaster.broadcastPacket(bot, new S_SkillBrave(bot.getId(),
						1, 0), true);
				return true;
			}
			if (!bot.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.STATUS_DRAGONPERL)
					&& !bot.getSkillEffectTimerSet().hasSkillEffect(
							L1SkillId.DECAY_POTION)) {
				bot.getSkillEffectTimerSet().setSkillEffect(
						L1SkillId.STATUS_DRAGONPERL,
						(_random.nextInt(600) + 400) * 1000);
				Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(), 197), true);
				Broadcaster.broadcastPacket(bot, new S_DRAGONPERL(bot.getId(), 8), true);//
				return true;
			}
		} else if (bot.isElf()) {
			if (bot.getCurrentWeapon() != 20) {
				if (!bot.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.FIRE_BLESS)
						&& !bot.getSkillEffectTimerSet().hasSkillEffect(
								L1SkillId.SILENCE)) {
					bot.getSkillEffectTimerSet().setSkillEffect(
							L1SkillId.FIRE_BLESS,
							(_random.nextInt(50) + 250) * 1000);
					bot.getMoveState().setBraveSpeed(1);
					Broadcaster.broadcastPacket(bot,
							new S_SkillSound(bot.getId(), 11775), true);
					Broadcaster.broadcastPacket(bot,
							new S_SkillBrave(bot.getId(), 1, 0), true);
					return true;
				}
				if (!bot.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.STATUS_DRAGONPERL)
						&& !bot.getSkillEffectTimerSet().hasSkillEffect(
								L1SkillId.DECAY_POTION)) {
					bot.getSkillEffectTimerSet().setSkillEffect(
							L1SkillId.STATUS_DRAGONPERL,
							(_random.nextInt(600) + 400) * 1000);
					Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(), 197), true);
					Broadcaster.broadcastPacket(bot, new S_DRAGONPERL(bot.getId(), 8), true);//
					return true;
				}
			} else {
				int rnd = _random.nextInt(100) + 1;
				if(rnd >= 50){
					if (!bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_ELFBRAVE) && !bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION)) {
						   bot.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_ELFBRAVE,(_random.nextInt(600) + 400) * 1000);
						   bot.getMoveState().setBraveSpeed(3);
						   Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(), 751), true);
						   Broadcaster.broadcastPacket(bot, new S_SkillBrave(bot.getId(), 3, 0), true);
						   return true;
					  }
					if (!bot.getSkillEffectTimerSet().hasSkillEffect(
							L1SkillId.STATUS_DRAGONPERL)
							&& !bot.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.DECAY_POTION)) {
						bot.getSkillEffectTimerSet().setSkillEffect(
								L1SkillId.STATUS_DRAGONPERL,
								(_random.nextInt(600) + 400) * 1000);
						Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(), 197), true);
						Broadcaster.broadcastPacket(bot, new S_DRAGONPERL(bot.getId(), 8), true);//
						return true;
					}
				} else {
					if (!bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_ELFBRAVE)
							&& !bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE)) {
						bot.getMoveState().setBraveSpeed(1);
						Broadcaster.broadcastPacket(bot, new S_SkillBrave(bot.getId(), 10, 0), true);
						return true;
					}
					if (!bot.getSkillEffectTimerSet().hasSkillEffect(
							L1SkillId.STATUS_DRAGONPERL)
							&& !bot.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.DECAY_POTION)) {
						bot.getSkillEffectTimerSet().setSkillEffect(
								L1SkillId.STATUS_DRAGONPERL,
								(_random.nextInt(600) + 400) * 1000);
						Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(), 197), true);
						Broadcaster.broadcastPacket(bot, new S_DRAGONPERL(bot.getId(), 8), true);//
						return true;
					}
				}
			}
		} else if (bot.isDarkelf()) {
			int percent = (int) Math.round((double) bot.getCurrentMp()
					/ (double) bot.getMaxMp() * 100);
			if (percent < 20)
				return false;
			if (!bot.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.MOVING_ACCELERATION)
					&& !bot.getSkillEffectTimerSet().hasSkillEffect(
							L1SkillId.SILENCE)) {
				new L1SkillUse().handleCommands(bot,
						L1SkillId.MOVING_ACCELERATION, bot.getId(), bot.getX(),
						bot.getY(), null, 0, L1SkillUse.TYPE_NORMAL);
				return true;
			}
			if (!bot.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.STATUS_DRAGONPERL)
					&& !bot.getSkillEffectTimerSet().hasSkillEffect(
							L1SkillId.DECAY_POTION)) {
				bot.getSkillEffectTimerSet().setSkillEffect(
						L1SkillId.STATUS_DRAGONPERL,
						(_random.nextInt(600) + 400) * 1000);
				Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(), 197), true);
				Broadcaster.broadcastPacket(bot, new S_DRAGONPERL(bot.getId(), 8), true);//
				return true;
			}
		} else if (bot.isWizard()) {
			int percent = (int) Math.round((double) bot.getCurrentMp()
					/ (double) bot.getMaxMp() * 100);
			if (percent < 20)
				return false;
			if (!bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.HOLY_WALK)
					&& !bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE)) {
				bot.getSkillEffectTimerSet().setSkillEffect(L1SkillId.HOLY_WALK, (_random.nextInt(14) + 50) * 1000);
				bot.getMoveState().setBraveSpeed(4);
				Broadcaster.broadcastPacket(bot, new S_DoActionGFX(bot.getId(),19));
				Broadcaster.broadcastPacket(bot, new S_SkillBrave(bot.getId(),4, 0), true);
				Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(),3936), true);
				return true;
			}
			if (!bot.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.STATUS_DRAGONPERL)
					&& !bot.getSkillEffectTimerSet().hasSkillEffect(
							L1SkillId.DECAY_POTION)) {
				bot.getSkillEffectTimerSet().setSkillEffect(
						L1SkillId.STATUS_DRAGONPERL,
						(_random.nextInt(600) + 400) * 1000);
				Broadcaster.broadcastPacket(bot, new S_SkillSound(bot.getId(), 197), true);
				Broadcaster.broadcastPacket(bot, new S_DRAGONPERL(bot.getId(), 8), true);//
				return true;
			}
		}
		return false;
	}

	public static boolean 클래스버프(L1RobotInstance bot) {
		// TODO 자동 생성된 메소드 스텁
		if (bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE))
			return false;
		if (bot.isKnight()) {
			if (!bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.REDUCTION_ARMOR)) {
				new L1SkillUse().handleCommands(bot, L1SkillId.REDUCTION_ARMOR, bot.getId(), bot.getX(), bot.getY(), null, 600, L1SkillUse.TYPE_NORMAL);
				return true;
			}
			if (!bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BOUNCE_ATTACK)) {
				new L1SkillUse().handleCommands(bot, L1SkillId.BOUNCE_ATTACK, bot.getId(), bot.getX(), bot.getY(), null, 600, L1SkillUse.TYPE_NORMAL);
				return true;
			}
			if (!bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
				bot.getSkillEffectTimerSet().setSkillEffect(L1SkillId.COUNTER_BARRIER,128000);
				return true;
			}
		} else if (bot.isWizard()) {
			int rand =  _random.nextInt(100);
			if (bot.getCurrentHp() < 1300) {
				if (rand < 60) { 
				new L1SkillUse().handleCommands(bot, L1SkillId.IMMUNE_TO_HARM,bot.getId(), bot.getX(), bot.getY(), null, 60, L1SkillUse.TYPE_NORMAL);
				}
			}
			if (!bot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ADVANCE_SPIRIT)) {
				new L1SkillUse().handleCommands(bot, L1SkillId.ADVANCE_SPIRIT,bot.getId(), bot.getX(), bot.getY(), null, 960, L1SkillUse.TYPE_NORMAL);
				return true;
			}
		} 
		return false;
	}

	private static long joinTime = 0;

	public static void clan_join(L1RobotInstance bot) {
		// TODO 자동 생성된 메소드 스텁
		if (bot.getClanid() != 0 || bot.isCrown())
			return;
		if (_random.nextInt(10) == 0)
			return;
		if (joinTime == 0) {
			joinTime = System.currentTimeMillis()
					+ (60000 * (20 + _random.nextInt(21)));
			return;
		} else {
			if (joinTime > System.currentTimeMillis())
				return;
			joinTime = System.currentTimeMillis()
					+ (60000 * (20 + _random.nextInt(21)));
		}
		// 로봇중 가입 되어있는 케릭이 650케릭 이상인지
		// 가입하려는혈 총혈 다 받아와서 유저 비교 제일적은혈
		String clanname = robot_clan_count();
		if (clanname == null)
			return;
		L1Clan clan = L1World.getInstance().getClan(clanname);
		if (clan == null)
			return;
		L1PcInstance pc = L1World.getInstance().getPlayer(clan.getLeaderName());
		if (pc == null)
			return;
		// 군주근처에 혈원이 있는지
		for (L1PcInstance pp : L1World.getInstance().getVisiblePlayer(pc)) {
			if (!(pp instanceof L1RobotInstance)
					&& pc.getClanid() == pp.getClanid())
				return;
		}
		// 내 근처에 같은혈 있는지
		for (L1PcInstance pp : L1World.getInstance().getVisiblePlayer(bot)) {
			if (!(pp instanceof L1RobotInstance)
					&& pc.getClanid() == pp.getClanid())
				return;
		}
		// 가입
		for (L1PcInstance clanMembers : clan.getOnlineClanMember()) {
			clanMembers.sendPackets(new S_ServerMessage(94, bot.getName())); // \f1%0이
																				// 혈맹의
																				// 일원으로서
																				// 받아들여졌습니다.
		}
		bot.setClanid(clan.getClanId());
		bot.setClanname(clan.getClanName());
		bot.setClanRank(L1Clan.CLAN_RANK_PROBATION);
		bot.setTitle("");
		Broadcaster.broadcastPacket(bot, new S_CharTitle(bot.getId(), ""));
		clan.addClanMember(bot.getName(), bot.getClanRank(), 1, bot);
		//Broadcaster.broadcastPacket(bot, new S_ClanJoinLeaveStatus(bot));
		Broadcaster.broadcastPacket(bot, new S_ReturnedStat(bot, S_ReturnedStat.CLAN_JOIN_LEAVE));
		GeneralThreadPool.getInstance().schedule(
				new title((L1RobotInstance) pc, bot),
				3000 + _random.nextInt(2000));
		bot.updateclan(bot.getClanname(), bot.getClanid(), bot.getTitle(), true);
	}

	private static String robot_clan_count() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String clan = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM robots_crown");
			rs = pstm.executeQuery();
			while (rs.next()) {
				String clanname = rs.getString("clanname");
				int clanid = rs.getInt("clanid");
				if (clanid == 0)
					continue;
				Connection con2 = null;
				PreparedStatement pstm2 = null;
				ResultSet rs2 = null;
				try {
					con2 = L1DatabaseFactory.getInstance().getConnection();
					pstm2 = con2
							.prepareStatement("SELECT * FROM robots WHERE clanid=?");
					pstm2.setInt(1, clanid);
					rs2 = pstm2.executeQuery();
					if (!rs2.next()) {
						clan = clanname;
						break;
					}
				} catch (SQLException e) {

				} finally {
					SQLUtil.close(rs2);
					SQLUtil.close(pstm2);
					SQLUtil.close(con2);
				}
			}
		} catch (SQLException e) {

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		if (clan != null)
			return clan;

		int count = 0;
		FastMap<String, Integer> list = new FastMap<String, Integer>();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM robots");
			rs = pstm.executeQuery();
			while (rs.next()) {
				String clanname = rs.getString("clanname");
				int clanid = rs.getInt("clanid");
				if (clanid == 0)
					continue;
				count++;
				if (count > 650)
					break;
				try {
					int cc = list.get(clanname);
					list.put(clanname, cc + 1);
				} catch (Exception e) {
					list.put(clanname, 0);
				}
			}
		} catch (SQLException e) {

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		if (count > 650)
			return null;

		int ci = 1000;
		for (FastMap.Entry<String, Integer> e = list.head(), mapEnd = list
				.tail(); (e = e.getNext()) != mapEnd;) {
			int cu = e.getValue();
			if (ci >= cu) {
				ci = cu;
				clan = e.getKey();
			}
		}
		return clan;
	}

	static class title implements Runnable {

		private L1RobotInstance crown;
		private L1RobotInstance joinchar;

		public title(L1RobotInstance _crown, L1RobotInstance _joinchar) {
			crown = _crown;
			joinchar = _joinchar;
		}

		@Override
		public void run() {
			// TODO 자동 생성된 메소드 스텁
			try {

				if (crown._userTitle == null
						|| crown._userTitle.equalsIgnoreCase(""))
					return;
				if (L1World.getInstance().getPlayer(crown.getName()) == null
						|| L1World.getInstance().getPlayer(joinchar.getName()) == null)
					return;

				joinchar.setTitle(crown._userTitle);
				S_CharTitle ct = new S_CharTitle(joinchar.getId(),
						joinchar.getTitle());
				joinchar.sendPackets(ct);
				Broadcaster.broadcastPacket(joinchar, ct, true);
				try {
					if (joinchar instanceof L1RobotInstance)
						joinchar.updateclan(joinchar.getClanname(),
								joinchar.getClanid(), crown._userTitle, true);
					else
						joinchar.save(); // DB에 캐릭터 정보를 써 우
				} catch (Exception e) {
				}

				L1Clan clan = L1World.getInstance()
						.getClan(crown.getClanname());
				if (clan != null) {
					for (L1PcInstance clanPc : clan.getOnlineClanMember()) {
						// \f1%0이%1에 「%2라고 하는 호칭을 주었습니다.
						S_ServerMessage sm = new S_ServerMessage(203,
								crown.getName(), joinchar.getName(),
								joinchar.getTitle());
						clanPc.sendPackets(sm, true);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	static class poly implements Runnable {

		L1RobotInstance bot;

		public poly(L1RobotInstance _bot) {
			bot = _bot;
		}

		@Override
		public void run() {
			try {
				// TODO 자동 생성된 메소드 스텁
				if (bot.isDead() || bot._스레드종료 || L1World.getInstance().getPlayer(bot.getName()) == null)
					return;
				poly(bot);
				Broadcaster.broadcastPacket(bot, new S_ChangeShape(bot.getId(), bot.getGfxId().getTempCharGfx()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


}
