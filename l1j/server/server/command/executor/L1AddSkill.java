/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

//import java.util.logging.Logger;

import java.util.StringTokenizer;

import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Skills;

public class L1AddSkill implements L1CommandExecutor {
	//private static Logger _log = Logger.getLogger(L1AddSkill.class.getName());

	private L1AddSkill() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AddSkill();
	}

	@Override
	public void execute(L1PcInstance gm, String cmdName, String arg) {
		try {
			int cnt = 0; // 루프 카운터
			String skill_name = ""; // 스킬명
			int skill_id = 0; // 스킬 ID
			StringTokenizer st = new StringTokenizer(arg);
			String charname = st.nextToken();
			L1PcInstance pc = L1World.getInstance().getPlayer(charname);
			if(pc == null)
				gm.sendPackets(new S_SystemMessage("경고: 접속중 캐릭터가 아닙니다."));
			 int object_id = pc.getId();
				pc.sendPackets(new S_SkillSound(object_id, '\343')); // 마법 습득의 효과음을 울린다
				Broadcaster.broadcastPacket(pc, new S_SkillSound(object_id, '\343'));

			
		//	L1PcInstance pc = L1World.getInstance().getPlayer(charname);
			
			if (pc.isCrown()) {
				pc.sendPackets(new S_AddSkill(255, 255, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0));
				L1Skills l1skills = null;
				for (cnt = 1; cnt <= 16; cnt++) // LV1~2 마법
				{
					l1skills = SkillsTable.getInstance().getTemplate(cnt); // 스킬 정보를 취득
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillsTable.getInstance(). spellMastery(object_id, skill_id, skill_name, 0, 0); // DB에 등록
				}
				for (cnt = 113; cnt <= 120; cnt++) // 프리 마법
				{
					l1skills = SkillsTable.getInstance().getTemplate(cnt); // 스킬 정보를 취득
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillsTable.getInstance(). spellMastery(object_id, skill_id, skill_name, 0, 0); // DB에 등록
				}
			} else if (pc.isKnight()) {
				pc.sendPackets(new S_AddSkill(255, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						192, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0));
				L1Skills l1skills = null;
				for (cnt = 1; cnt <= 8; cnt++) // LV1 마법
				{
					l1skills = SkillsTable.getInstance().getTemplate(cnt); // 스킬 정보를 취득
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillsTable.getInstance(). spellMastery(object_id, skill_id, skill_name, 0, 0); // DB에 등록
				}
				for (cnt = 87; cnt <= 91; cnt++) // 나이트 마법
				{
					l1skills = SkillsTable.getInstance().getTemplate(cnt); // 스킬 정보를 취득
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillsTable.getInstance(). spellMastery(object_id, skill_id, skill_name, 0, 0); // DB에 등록
				}
			} else if (pc.isElf()) {
				pc.sendPackets(new S_AddSkill(255, 255, 127, 255, 255, 255, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 3, 255, 255, 255, 255,
						0, 0,0,0,0,0));
				L1Skills l1skills = null;
				for (cnt = 1; cnt <= 48; cnt++) // LV1~6 마법
				{
					l1skills = SkillsTable.getInstance().getTemplate(cnt); // 스킬 정보를 취득
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillsTable.getInstance(). spellMastery(object_id, skill_id, skill_name, 0, 0); // DB에 등록
				}
				for (cnt = 129; cnt <= 176; cnt++) // 에르프 마법
				{
					l1skills = SkillsTable.getInstance().getTemplate(cnt); // 스킬 정보를 취득
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillsTable.getInstance(). spellMastery(object_id, skill_id, skill_name, 0, 0); // DB에 등록
				}
			} else if (pc.isWizard()) {
				pc.sendPackets(new S_AddSkill(255, 255, 127, 255, 255, 255,
						255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0,0,0,0,0));
				L1Skills l1skills = null;
				for (cnt = 1; cnt <= 80; cnt++) // LV1~10 마법
				{
					l1skills = SkillsTable.getInstance().getTemplate(cnt); // 스킬 정보를 취득
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillsTable.getInstance(). spellMastery(object_id, skill_id, skill_name, 0, 0); // DB에 등록
				}
			} else if (pc.isDarkelf()) {
				pc.sendPackets(new S_AddSkill(255, 255, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 255, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0));
				L1Skills l1skills = null;
				for (cnt = 1; cnt <= 16; cnt++) // LV1~2 마법
				{
					l1skills = SkillsTable.getInstance().getTemplate(cnt); // 스킬 정보를 취득
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillsTable.getInstance(). spellMastery(object_id, skill_id, skill_name, 0, 0); // DB에 등록
				}
				for (cnt = 97; cnt <= 111; cnt++) // DE마법
				{
					l1skills = SkillsTable.getInstance().getTemplate(cnt); // 스킬 정보를 취득
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillsTable.getInstance(). spellMastery(object_id, skill_id, skill_name, 0, 0); // DB에 등록
				}
			}
			gm.sendPackets(new S_SystemMessage("모든 스킬을 배웠습니다."));
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".스킬마스터 [캐릭명] 명령하세요."));
		}
	}
}
