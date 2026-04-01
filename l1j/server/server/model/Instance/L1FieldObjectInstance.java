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
package l1j.server.server.model.Instance;

import java.text.SimpleDateFormat;
import java.util.Locale;

import l1j.server.GameSystem.Antaras.AntarasRaid;
import l1j.server.GameSystem.Antaras.AntarasRaidSystem;
import l1j.server.GameSystem.Antaras.AntarasRaidTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

public class L1FieldObjectInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	public int moveMapId;
	public int Potal_Open_pcid = 0;
	public int dx = 0;
	public int dy = 0;
	public short dm = 0;
	private static final SimpleDateFormat ss = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);
	
	public L1FieldObjectInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance pc) {}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		int npcid = getNpcTemplate().get_npcId();
		switch(npcid){
		case 4212015: { // 드래곤 포탈
			// pc.system=1;
			pc.dragonmapid = (short) moveMapId;
			// pc.sendPackets(new S_Message_YN(622,
			// "안타라스 드래곤 포탈에 진입 하시겠습니까?(Y/N)"), true);
			pc.system = -1;// 초기화
			int count = 0;
			int trcount = 0;
			AntarasRaid ar = AntarasRaidSystem.getInstance().getAR(
					pc.dragonmapid);
			int count1 = ar.countLairUser();
			if (pc.getLevel() <= 55) {
				pc.sendPackets(new S_SystemMessage(
						"드래곤 레이드 레벨 제한으로인해 드래곤 포탈에 입장 할 수 없습니다."));
				return;
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.DRAGONRAID_BUFF)) {
				pc.sendPackets(new S_SystemMessage(
						"드래곤 레이드 마법으로 인해 드래곤 포탈에 입장 할 수 없습니다."));
				pc.sendPackets(
						new S_SystemMessage(ss.format(pc.getNetConnection()
								.getAccount().getDragonRaid())
								+ " 이후에 입장 가능합니다."));
				return;
			}
			if (count1 > 0) {
				for (L1PcInstance player : L1World.getInstance()
						.getAllPlayers()) {
					if (player.getMapId() == pc.dragonmapid) {
						trcount++;
					}
				}
				if (trcount == 0) {
					for (L1Object npc : L1World.getInstance().getObject()) {
						if (npc instanceof L1MonsterInstance) {
							if (npc.getMapId() == pc.dragonmapid) {
								L1MonsterInstance _npc = (L1MonsterInstance) npc;
								_npc.deleteMe();
							}
						}
					}
					ar.clLairUser();
					ar.setAntaras(false);
					ar.setanta(null);
				}
			}
			for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
				if (player.getMapId() == pc.dragonmapid) {
					count += 1;
					if (count > 31) {
						pc.sendPackets(new S_SystemMessage(
								"입장 가능 인원수를 초과 하였습니다."));
						return;
					}
				}
			}
			L1Teleport.teleport(pc, 32668, 32675, (short) pc.dragonmapid, 5,
					true);
			// L1Teleport.teleport(pc, 32600, 32741, (short) pc.dragonmapid, 5,
			// true);
		}
			break;
		case 4500102:
			안타레이드시작(pc, pc.dragonmapid);
			break;
			default: break;
		}
	}
	
	private synchronized void 안타레이드시작(L1PcInstance pc, int mapid) {
		int count = 0;
		AntarasRaid ar = AntarasRaidSystem.getInstance().getAR(mapid);
		count = ar.countLairUser();
		if (ar.isAntaras()) {
			pc.sendPackets(new S_ServerMessage(1537));// 드래곤이 깨서 진입 못한다
			return;
		} else if (count == 0) {
			for (L1Object npc : L1World.getInstance().getObject()) {
				if (npc instanceof L1MonsterInstance) {
					if (npc.getMapId() == mapid) {
						L1MonsterInstance _npc = (L1MonsterInstance) npc;
						_npc.deleteMe();
					}
				}
			}
			ar.clLairUser();
			ar.addLairUser(pc);
			ar.setAntaras(false);
			ar.setanta(null);
			if (ar.art != null) {
				ar.art.cancel();
				ar.art = null;
			}
			AntarasRaidTimer antastart = new AntarasRaidTimer(null, ar, 5,
					10 * 1000);// 2분 체크
			antastart.begin();
			// 몹소환 안타 깨어나서 접근금지.
			AntarasRaidTimer antaendtime = new AntarasRaidTimer(null, ar, 6,
					1000 * 60 * 120);// 22분 체크
			ar.art = antaendtime;
			antaendtime.begin();
			// 안타 잡기 실패 모두 텔.
		}
		ar.addLairUser(pc);
		L1Teleport.teleport(pc, 32796, 32664, (short) mapid, 5,true);
	}

	@Override
	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.getNearObjects().removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		getNearObjects().removeAllKnownObjects();
	}

	/**
	 * 지정된 맵의 32명이 넘는지 체크해서 텔시킨다
	 * @param pc
	 * @param mapid
	 */
	private void telAntarasRaidMap(L1PcInstance pc, int mapid){
		int count = 0;
		for(L1PcInstance player : L1World.getInstance().getAllPlayers()){
			if(player.getMapId() == mapid){
				count += 1;
				if(count > 31)
					return;
			}
		}
		L1Teleport.teleport(pc, 32930, 32726, (short) mapid, 5, true);
	}

	/**
	 * 이동할 맵을 설정한다.
	 * @param id
	 */
	public void setMoveMapId(int id){
		moveMapId = id;
	}

}
