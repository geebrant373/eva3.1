package server.threads.pc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.RealTime;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.L1SpawnUtil;
import l1j.server.server.utils.SQLUtil;

public class PremiumAinThread extends Thread{

	private static PremiumAinThread _instance;
	private static Logger _log = Logger.getLogger(PremiumAinThread.class.getName());

	public static PremiumAinThread getInstance(){
		if (_instance == null){
			_instance = new PremiumAinThread();
			_instance.start();
		}
		return _instance;
	}
	public PremiumAinThread(){

	}


	public void run(){
		System.out.println(PremiumAinThread.class.getName()  + " 시작");
		while(true){
			try {
				for (L1PcInstance _client : L1World.getInstance().getAllPlayers()) {
					if (_client == null || _client.getNetConnection() == null) {
						continue;
					} else {
						try {
							int tc = _client.getTimeCount();
							int tc1 = _client.getTimeCount();
							if (_client.getLevel() >= 51) {
								if (tc1 >= Config.RANKTIME){
									infoRanking(_client);
								}
								else {
									_client.setTimeCount(tc + 1);
								}
							}
							if (_client.getLevel() >= 52) {
								int sc = _client.getSafeCount();
								if (CharPosUtil.getZoneType(_client) == 1 && !_client.isPrivateShop() && !_client.isAutoClanjoin()) {
									if (sc >= 14) {
										if (_client.getAinHasad() <= 2499999)
										_client.setSafeCount(0);
									} else {
										_client.setSafeCount(sc + 1);
									}
								} else {
									if (sc > 0)
										_client.setSafeCount(0);
								}
							}

							int keycount = _client.getInventory().countItems(L1ItemId.DRAGON_KEY);
							if (keycount > 0)
								DragonkeyTimeCheck(_client, keycount);

						} catch (Exception e) {
							_log.warning("Primeum give failure.");
							_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
							throw e;
						}
					}
				}
				Thread.sleep(60000);
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				//cancel();
			}
		}
	}

	private void infoRanking(L1PcInstance pc){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PreparedStatement pstm1 = null;
		ResultSet rs1 = null;
		int allRank = 0;
		int classRank = 0;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM ( SELECT @RNUM:=@RNUM+1 AS ROWNUM , C.Exp,C.char_name,c.objid,c.type  FROM (SELECT @RNUM:=0) R, characters c  WHERE C.AccessLevel = 0  ORDER BY C.Exp DESC ) A  WHERE objid = ?");
			pstm.setInt(1, pc.getId());
			rs = pstm.executeQuery();
			if (rs.next()){
				allRank = rs.getInt(1);
			}
			
			pstm1 = con.prepareStatement("SELECT * FROM ( SELECT @RNUM:=@RNUM+1 AS ROWNUM , C.Exp,C.char_name,c.objid,c.type  FROM (SELECT @RNUM:=0) R, characters c  WHERE C.AccessLevel = 0  and c.type =? ORDER BY C.Exp DESC ) A  WHERE objid = ?");
			pstm1.setInt(1, pc.getType());
			pstm1.setInt(2, pc.getId());
			rs1 = pstm1.executeQuery();
			
			if (rs1.next()){
				classRank = rs1.getInt(1);
				pc.setclassRankLevel(classRank);
			}
		
		} catch (SQLException e) {
			System.out.println("랭킹 조회 실패");
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(rs1);
			SQLUtil.close(pstm1);
			SQLUtil.close(con);
		}
		
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.RANKING_BUFF_1)) {
			pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.RANKING_BUFF_1);
		} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.RANKING_BUFF_2)) {
			pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.RANKING_BUFF_2);
		} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.RANKING_BUFF_3)) {
			pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.RANKING_BUFF_3);
		} else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.RANKING_BUFF_4)) {
			pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.RANKING_BUFF_4);
		}
		
		if (allRank >= 1 && allRank <= 5) {
			pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.RANKING_BUFF_1, 1000 * 60 * 60 * 24 * 2);
			pc.getAC().addAc(-3);
			pc.addMaxHp(200);
			pc.addPVPDamageReduction(2);
			pc.addDmgup(2);
			pc.addBowDmgup(2);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_OwnCharStatus(pc));
			pc.sendPackets(new S_SkillSound(pc.getId(), 8942)); // 별4
		} else if  (allRank >= 6 && allRank <= 10) {
			pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.RANKING_BUFF_2, 1000 * 60 * 60 * 24 * 2);
			pc.getAC().addAc(-2);
			pc.addMaxHp(150);
			pc.addPVPDamageReduction(1);
			pc.addDmgup(1);
			pc.addBowDmgup(1);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_OwnCharStatus(pc));
			pc.sendPackets(new S_SkillSound(pc.getId(), 8941)); // 별3
		} else if  (allRank >= 11 && allRank <= 30) {
			pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.RANKING_BUFF_3, 1000 * 60 * 60 * 24 * 2);
			pc.getAC().addAc(-1);
			pc.addMaxHp(100);
			pc.addPVPDamageReduction(1);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_OwnCharStatus(pc));
			pc.sendPackets(new S_SkillSound(pc.getId(), 8940)); // 별2
		} else if  (allRank >= 31) {
			pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.RANKING_BUFF_4, 1000 * 60 * 60 * 24 * 2);
			pc.getAC().addAc(-1);
			pc.addMaxHp(70);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_OwnCharStatus(pc));
			pc.sendPackets(new S_SkillSound(pc.getId(), 8939)); // 별1
		}
 
		pc.setTimeCount(0);
		if (!pc.isGm()) {
			pc.setRankLevel(allRank);
			pc.sendPackets(new S_SkillSound(pc.getId(), L1SkillId.STR_STATUS_EFFECT_TAIWAN_STONE_BUFF20)); // 삭제
			pc.sendPackets(new S_SkillSound(pc.getId(), L1SkillId.STR_STATUS_EFFECT_TAIWAN_STONE_BUFF21)); // 별4
			pc.sendPackets(new S_SkillSound(pc.getId(), L1SkillId.STR_STATUS_EFFECT_TAIWAN_STONE_BUFF22)); // 별4
			pc.sendPackets(new S_SkillSound(pc.getId(), L1SkillId.STR_STATUS_EFFECT_TAIWAN_STONE_BUFF23)); // 별4
			pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE," 서버 랭킹이 초기화 되었습니다. "));
			pc.sendPackets(new S_SystemMessage(" 서버 랭킹이 초기화 되었습니다. "));
			pc.sendPackets(new S_SystemMessage(	"\\fY[** "+ pc.getName() +"님의 랭킹 내용 **]"));
			pc.sendPackets(new S_SystemMessage(
					"\\fY전체 : " + allRank +
					"위 // 클래스 : " + classRank +"위")); 
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
		}
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
		delenpc(900009622);
		NpcTable.reload();
		L1SpawnUtil.spawn2(Config.랭킹동상x좌표, Config.랭킹동상y좌표, (short) 4, 900009622, 0, 0, 0); 
	}
	
	private static void delenpc(int npcid) {
		L1NpcInstance npc = null;
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1NpcInstance) {
				npc = (L1NpcInstance) object;
				if (npc.getNpcTemplate().get_npcId() == npcid) {
					if (npc != null)
						npc.deleteMe();
					npc = null;
				}
			}
		}
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
	
	private void giveFeather(L1PcInstance pc) {
		pc.setTimeCount(0);
		pc.getInventory().storeItem(41159, Config.wing); // 신비한 날개깃털 지급 
		pc.sendPackets(new S_ServerMessage(403, "$5116 ("+Config.wing+")"));
	}
	
	private void giveexppotion(L1PcInstance pc) {
		pc.setTimeCount(0);
		pc.getInventory().storeItem(90000000, 1);
		pc.sendPackets(new S_ServerMessage(403, "$5116 ("+1+")"));
	}
	
	private void DragonkeyTimeCheck(L1PcInstance pc, int count) {
		long nowtime = System.currentTimeMillis();
		if(count == 1){
			L1ItemInstance item = pc.getInventory().findItemId(L1ItemId.DRAGON_KEY);
			if(nowtime > item.getEndTime().getTime())
				pc.getInventory().removeItem(item);
		}else{
			L1ItemInstance[] itemList = pc.getInventory().findItemsId(L1ItemId.DRAGON_KEY);
			for (int i = 0; i < itemList.length; i++) {
				if(nowtime > itemList[i].getEndTime().getTime())
					pc.getInventory().removeItem(itemList[i]);		
			}
		}
	}

	private void GungeonTimeCheck(L1PcInstance pc) {
		RealTime time = RealTimeClock.getInstance().getRealTime();
		int entertime = pc.getGdungeonTime() % 1000;
		int enterday = pc.getGdungeonTime() / 1000;
		int dayofyear = time.get(Calendar.DAY_OF_YEAR);

		if(dayofyear == 365)
			dayofyear += 1;

		if(entertime > 120){
			// 메세지를 주고
			L1Teleport.teleport(pc, 33419, 32810, (short) 4, 5, true);
		} else if(enterday < dayofyear){
			pc.setGdungeonTime(time.get(Calendar.DAY_OF_YEAR) * 1000);
		} else {
			if(entertime > 60){
				int a = 120- entertime;
				pc.sendPackets(new S_ServerMessage(1527, ""+a+""));// 체류시간이  %분 남았다.
			}
			pc.setGdungeonTime(pc.getGdungeonTime() + 1);
		}
	}
}
