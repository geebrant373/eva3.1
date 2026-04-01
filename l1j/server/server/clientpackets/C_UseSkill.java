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

package l1j.server.server.clientpackets;

import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.CALL_CLAN;
import static l1j.server.server.model.skill.L1SkillId.FIRE_WALL;
import static l1j.server.server.model.skill.L1SkillId.LIFE_STREAM;
import static l1j.server.server.model.skill.L1SkillId.MASS_TELEPORT;
import static l1j.server.server.model.skill.L1SkillId.MEDITATION;
import static l1j.server.server.model.skill.L1SkillId.RUN_CLAN;
import static l1j.server.server.model.skill.L1SkillId.TELEPORT;
import static l1j.server.server.model.skill.L1SkillId.TRUE_TARGET;
import l1j.server.Config;
import l1j.server.MJ3SEx.EActionCodes;
import l1j.server.MJ3SEx.Loader.SpriteInformationLoader;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Skills;
import server.LineageClient;

//Referenced classes of package l1j.server.server.clientpackets:
//ClientBasePacket

public class C_UseSkill extends ClientBasePacket {

	public C_UseSkill(byte abyte0[], LineageClient client) throws Exception {
		super(abyte0);
		int row = readC();
		int column = readC();
		int skillId = (row * 8) + column + 1;
		String charName = null;
		String message = null;
		int targetId = 0;
		int targetX = 0;
		int targetY = 0;
		L1PcInstance pc = client.getActiveChar();

		if (pc.isTeleport() || pc.isDead()) {
			return;
		}
		if (pc.getMapId() == 701) {
			return;
		}
		if (!pc.isSkillMastery(skillId)) {
			return;
		}
		if (pc.getMapId() >= 49 && pc.getMapId() <= 51 || pc.getMapId() >= 522 && pc.getMapId() <= 524) {
			return;
		}
		if (!pc.getMap().isUsableSkill()) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1 여기에서는 사용할 수 없습니다.
			return;
		}
		if (skillId == L1SkillId.SHAPE_CHANGE) {
			if (pc.getMapId() == 5153 || pc.getMapId() == 5001) {
				pc.sendPackets(new S_SystemMessage("현재맵에서는 변신할 수 없습니다."));
				return;
			}
		}
		if (pc.getClan().getClanId() == 0) {
			return;
		}
		// 요구 간격을 체크한다
		if (Config.CHECK_SPELL_INTERVAL) {
			int result;
			// FIXME 어느 스킬이 dir/no dir일까의 판단이 적당
			if (SkillsTable.getInstance().getTemplate(skillId).getActionId() == ActionCodes.ACTION_SkillAttack) {
				result = pc.getAcceleratorChecker().checkInterval(AcceleratorChecker.ACT_TYPE.SPELL_DIR);
			} else {
				result = pc.getAcceleratorChecker().checkInterval(AcceleratorChecker.ACT_TYPE.SPELL_NODIR);
			}
			if (result == AcceleratorChecker.R_DISCONNECTED) {
				return;
			}
		}

		if (abyte0.length > 4) {
			try {
				switch (skillId) {
				case CALL_CLAN:
				case RUN_CLAN:
					charName = readS();
					break;
				case TRUE_TARGET:
					targetId = readD();
					targetX = readH();
					targetY = readH();
					message = readS();
					break;
				case TELEPORT:
				case MASS_TELEPORT:
					readH(); // MapID
					targetId = readD(); // Bookmark ID
					break;
				case FIRE_WALL:
				case LIFE_STREAM:
					targetX = readH();
					targetY = readH();
					break;
				default: {
					targetId = readD();
					targetX = readH();
					targetY = readH();
				}
					break;
				}
			} catch (Exception e) {
				// _log.log(Level.SEVERE, "", e);
			}
		}

		if (pc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) { // 아브소르트바리아의 해제
			pc.getSkillEffectTimerSet().killSkillEffectTimer(ABSOLUTE_BARRIER);
			pc.startHpRegenerationByDoll();
			pc.startMpRegenerationByDoll();
		}

		pc.getSkillEffectTimerSet().killSkillEffectTimer(MEDITATION);

		try {
			long currentMillis = System.currentTimeMillis();
			if (skillId == CALL_CLAN || skillId == RUN_CLAN) {
				if (charName.isEmpty()) {
					return;
				}

				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < charName.length(); i++) {
					if (charName.charAt(i) == '[') {
						break;
					}
					sb.append(charName.charAt(i));
				}

				L1PcInstance target = L1World.getInstance().getPlayer(sb.toString());

				if (target == null) {
					pc.sendPackets(new S_ServerMessage(73, charName));
					return;
				}
				if (pc.getClanid() != target.getClanid()) {
					pc.sendPackets(new S_ServerMessage(414));
					return;
				}
				targetId = target.getId();
				if (skillId == CALL_CLAN) {
					int callClanId = pc.getCallClanId();
					if (callClanId == 0 || callClanId != targetId) {
						pc.setCallClanId(targetId);
						pc.setCallClanHeading(pc.getMoveState().getHeading());
					}
				}
			}
			/*
			 * switch(skillId){ case 4: //에너지 볼트 case 6: //아이스 대거 case 7: //윈드 커터 case 10:
			 * //칠 터치 case 15: //파이어 애로우 case 16: //스탈락 case 28: //뱀파이어릭 터치 case 30: //어스 재일
			 * case 34: //콜 라이트닝 case 38: //콘 오브 콜드 case 45: //이럽션 case 46: //선 버스트 case 77:
			 * //디스인티그레이트 case 108: //파이널 번 case 132: //트리플 애로우 case 187: //포우 슬레이어 case
			 * 203: //스매쉬 new AttackSkill(pc, skillId, targetId, targetX, targetY); return;
			 * }
			 */

			L1SkillUse l1skilluse = new L1SkillUse();
			L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
			long j = pc.getCurrentSpriteInterval(
					skill.getTarget().equals("attack") ? EActionCodes.spell_dir : EActionCodes.spell_nodir);
			long l = (SpriteInformationLoader.getInstance().getUseSpellInterval(pc, skillId) + j) - 5L;
			pc.lastSpellUseMillis = currentMillis + (l > 0 ? l : 0L);
			l1skilluse.handleCommands(pc, skillId, targetId, targetX, targetY, message, 0, L1SkillUse.TYPE_NORMAL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
