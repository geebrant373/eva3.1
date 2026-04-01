/* This program is free software; you can redistribute it and/or modify
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
package l1j.server.server.clientpackets;

import static l1j.server.server.model.Instance.L1PcInstance.REGENSTATE_MOVE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;

//import java.util.Random; // 시간의 균열 - 티칼용 주석

import l1j.server.GameSystem.CrockSystem;
import l1j.server.GameSystem.PetRacing;
import l1j.server.server.ActionCodes;
import l1j.server.server.Opcodes;
import l1j.server.server.TimeController.TebeController;
import l1j.server.server.datatables.EvaSystemTable;
import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.DungeonRandom;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1EvaSystem;
import server.LineageClient;

//Referenced classes of package l1j.server.server.clientpackets:
//ClientBasePacket

public class C_MoveChar extends ClientBasePacket {

	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };	
	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	// 이동
	public C_MoveChar(byte decrypt[], LineageClient client) throws Exception {
		super(decrypt);
		int locx = readH();
		int locy = readH();
		int heading = readC();
		boolean ck = false;
		L1PcInstance pc = client.getActiveChar();
		if (pc == null) return;
		if (pc.isTeleport()) { return; }
		/** 뚫어버그 */
		if (!pc.getMap().ismPassable(locx, locy, heading)){
			ck = true;
		}
		
		// 이부분 추가 영자 뚫어 안되면 삭제 - 투명상태일때만220303 무빙
//		ArrayList<L1Object> allList = L1World.getInstance().getVisibleObjects(pc, 5);
//		L1Object[] objs = allList.toArray(new L1Object[allList.size()]);
//		for(int i = 0; i < objs.length; i++){
//			if(objs[i] instanceof L1PcInstance){
//				L1PcInstance obpc =null;
//				obpc = (L1PcInstance)objs[i];
//				if(obpc.isDead() || obpc.isGm()){
//					continue;
//				}
//				if(objs[i].getX() == locx && objs[i].getY() == locy && objs[i].getMapId() == pc.getMapId()){
//					ck = true;
//					break;
//				}
//			}
//		}
//		objs = null;
//		allList.clear();

		if(heading < 0 || heading >7)
			return; 
		pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.MEDITATION);
		pc.setCallClanId(0);

		if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) { // 아브소르트바리아중은 아니다
			pc.setRegenState(REGENSTATE_MOVE);
		}
		
		/** 배틀존 **/
		if (pc.getMapId() == 5153) {
			if (pc.get_DuelLine() == 0 && !pc.isGm()) {
				L1Teleport.teleport(pc, 33419, 32810, (short) 4, 5, true);
			}
		}
		if (pc.getMapId() == 13 || pc.getMapId() == 36) {
			if (pc.getAccount().getDragonGludioTime() >= Config.용던본던시간) {
				L1Teleport.teleport(pc, 33432, 32817, (short) 4, 5, true);
				pc.getAccount().updateDragonGludio();
				pc.getAccount().setDragonGludioTime(Config.용던본던시간);
			}
		}
		if (pc.getMapId() == 49 || pc.getMapId() == 50 || pc.getMapId() == 51) {
			if (pc.getAccount().getAntDundeonTime() >= Config.개미던전시간) {
				L1Teleport.teleport(pc, 33432, 32817, (short) 4, 5, true);
				pc.getAccount().updateAntDundeon();
				pc.getAccount().setAntDundeonTime(Config.개미던전시간);
			}
		}
		if (pc.getMapId() == 522 || pc.getMapId() == 523 || pc.getMapId() == 524) {
			if (pc.getAccount().getShadowTempleTime() >= Config.그림자신전시간) {
				L1Teleport.teleport(pc, 33432, 32817, (short) 4, 5, true);
				pc.getAccount().updateShadowTemple();
				pc.getAccount().setShadowTempleTime(Config.그림자신전시간);
			}
		}
		if (pc.getMapId() == 53 || pc.getMapId() == 54) {
			if (pc.getAccount().getGiranPrisonTime() >= Config.기감시간) {
				L1Teleport.teleport(pc, 33432, 32817, (short) 4, 5, true);
				pc.getAccount().updateGiranPrison();
				pc.getAccount().setGiranPrisonTime(Config.기감시간);
			}
		}
		if (pc.getMapId() == 303) {
			if (pc.getAccount().getDreamIslandTime() >= Config.몽섬시간) {
				L1Teleport.teleport(pc, 33432, 32817, (short) 4, 5, true);
				pc.getAccount().updateDreamIsland();
				pc.getAccount().setDreamIslandTime(Config.기감시간);
			}
		}
		if (pc.getMapId() >= 530 && pc.getMapId() <= 533) {
			if (pc.getAccount().getLastabardTime() >= Config.라던시간) {
				L1Teleport.teleport(pc, 33432, 32817, (short) 4, 5, true);
				pc.getAccount().updateLastabard();
				pc.getAccount().setLastabardTime(Config.기감시간);
			}
		}
		//safeUpdate("giranprison");
		//pc.getAccount().setGiranPrisonTime(0);
		if (pc.getMapId() == 781 && TebeController.getInstance().isgameStart == false) {
			if (!pc.isGm()) {
				L1Teleport.teleport(pc, 33443, 32799, (short) 4, 5, true);
			}
		}
		pc.getMap().setPassable(pc.getLocation(), true);
		if (Config.CHECK_MOVE_INTERVAL) {
			int result;
			
			long nowtime = System.currentTimeMillis();
			if(nowtime > pc.getThunderWanddelayCheck()){
				result = pc.getAcceleratorChecker().checkInterval(AcceleratorChecker.ACT_TYPE.MOVE);
				if (result == AcceleratorChecker.R_DISCONNECTED) {				
					L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getMoveState().getHeading(), false);
					//pc.getMoveState().setHeading(heading);
					//pc.sendPackets(new S_PacketBox(S_PacketBox.유저빽스탭, pc));
					//Broadcaster.broadcastPacket(pc, new S_ChangeHeading(pc));
					return;
				}
			}
		}
		pc.sendPackets(new S_SkillSound(pc.getId(), 99));
		locx += HEADING_TABLE_X[heading];
		locy += HEADING_TABLE_Y[heading];

		if (Dungeon.getInstance().dg(locx, locy, pc.getMap().getId(), pc)) { // 지하 감옥에 텔레포트 했을 경우
			return;
		}		  

		if (DungeonRandom.getInstance().dg(locx, locy, pc.getMap().getId(),	pc)) { // 텔레포트처가 랜덤인 텔레포트 지점
			return;
		}
		if (ck){
			L1Teleport.teleport(pc, pc.getX(), pc.getY(),pc.getMapId(),pc.getMoveState().getHeading(), false);
			return;
		}
		BuffInfo[] buffList = { 
				new BuffInfo(101, L1SkillId.오만지배1층버프), new BuffInfo(102, L1SkillId.오만지배2층버프),
				new BuffInfo(103, L1SkillId.오만지배3층버프), new BuffInfo(104, L1SkillId.오만지배4층버프),
				new BuffInfo(105, L1SkillId.오만지배5층버프), new BuffInfo(106, L1SkillId.오만지배6층버프),
				new BuffInfo(107, L1SkillId.오만지배7층버프), new BuffInfo(108, L1SkillId.오만지배8층버프),
				new BuffInfo(109, L1SkillId.오만지배9층버프), new BuffInfo(111, L1SkillId.오만지배정상층버프) };

		for (BuffInfo buff : buffList) {
			if (pc.getMapId() != buff.mapId && pc.getSkillEffectTimerSet().hasSkillEffect(buff.skillId)) {
				pc.getSkillEffectTimerSet().removeSkillEffect(buff.skillId);
				pc.sendPackets(new S_SystemMessage("\\fT오만의 탑 지배버프가 소멸되었습니다."));
			}
		}
		
		pc.getLocation().set(locx, locy);
		pc.getMoveState().setHeading(heading);
		Broadcaster.broadcastPacket(pc, new S_MoveCharPacket(pc));

		if(CrockSystem.getInstance().isOpen()){
			L1EvaSystem eva = EvaSystemTable.getInstance().getSystem(1);
			int[] loc = CrockSystem.getInstance().loc();
			if(Math.abs(loc[0]-pc.getX())<=1 && Math.abs(loc[1] - pc.getY())<=1 && loc[2] == pc.getMap().getId()) {
				switch(eva.getMoveLocation()) {
				case 0: return;
				case 1: L1Teleport.teleport(pc, 32639, 32876, (short) 780, 2, false); break;// 테베
				case 2: L1Teleport.teleport(pc, 32793, 32754, (short) 783, 2, false); break;// 티칼
				}			
			}
		}
		if(pc.isPetRacing())	PetRacing.getInstance().RacingCheckPoint(pc);
		L1WorldTraps.getInstance().onPlayerMoved(pc);		
		pc.getMap().setPassable(pc.getLocation(), false);

	}
	
	private void safeUpdate(String columnName) {
        Connection cc = null;
        PreparedStatement p = null;
        try {
            cc = L1DatabaseFactory.getInstance().getConnection();
            cc.setAutoCommit(false); // 트랜잭션 처리
            p = cc.prepareStatement("UPDATE accounts SET " + columnName + "=0");
            p.executeUpdate();
            cc.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (cc != null) cc.rollback(); } catch (SQLException ignore) {}
        } finally {
            try { if (p != null) p.close(); } catch (SQLException ignore) {}
            try { if (cc != null) cc.close(); } catch (SQLException ignore) {}
        }
    }
	
	class BuffInfo {
	    int mapId;
	    int skillId;

	    BuffInfo(int mapId, int skillId) {
	        this.mapId = mapId;
	        this.skillId = skillId;
	    }
	}
}