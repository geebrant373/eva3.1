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

import server.LineageClient;
import l1j.server.Config;
//import l1j.server.channel.ChatMonitorChannel;
import l1j.server.server.GMCommands;
import l1j.server.server.Opcodes;
import l1j.server.server.UserCommands;
import l1j.server.server.datatables.ChatLogTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.monitor.Logger;
import l1j.server.server.monitor.LoggerInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import manager.LinAllManager;
//비스킷 추가, 비스킷 수정 주사위 주사위 주사위
import l1j.server.GameSystem.biscuitGame.Akduk1GameSystem;
import l1j.server.GameSystem.biscuitGame.Akduk2GameSystem;
import l1j.server.GameSystem.biscuitGame.Akduk3GameSystem;
import l1j.server.GameSystem.biscuitGame.Akduk4GameSystem;
//비스킷 추가, 비스킷 수정 주사위 주사위 주사위

//Referenced classes of package l1j.server.server.clientpackets:
//ClientBasePacket

//chat opecode type
//통상 0x44 0x00
//절규(! ) 0x44 0x00
//속삭임(") 0x56 charname
//전체(&) 0x72 0x03
//트레이드($) 0x44 0x00
//PT(#) 0x44 0x0b
//혈맹(@) 0x44 0x04
//연합(%) 0x44 0x0d
//CPT(*) 0x44 0x0e

public class C_Chat extends ClientBasePacket {

	private static final String C_CHAT = "[C] C_Chat";

	public C_Chat(byte abyte0[], LineageClient clientthread) {
		super(abyte0);
		L1PcInstance pc = clientthread.getActiveChar();
		int chatType = readC();
		String chatText = readS();

		if (pc.waitAutoAuth()) {
			if (chatText.equals(pc.getAutoAuthCode())) {
				pc.resetAutoInfo();
				pc.sendPackets(new S_SystemMessage("오토 방지 코드가 인증되었습니다."));
				return;
			}
		}
		
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.AREA_OF_SILENCE)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_POISON_SILENCE)) {
			return;
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED)) { // 채팅 금지중
			pc.sendPackets(new S_ServerMessage(242)); // 현재 채팅 금지중입니다.
			return;
		}

		if (pc.isDeathMatch() && !pc.isGhost()) {
			pc.sendPackets(new S_ServerMessage(912)); // 채팅을 할 수 없습니다.
			return;
		}
		
		
		switch (chatType) {
		case 0: {
			if (pc.isGhost() && !(pc.isGm() || pc.isMonitor())) {
				return;
			}
			// GM커멘드
			if (chatText.startsWith(".")) {
				if (pc.getAccessLevel() == Config.GMCODE || pc.getAccessLevel() == 1) {
					String cmd = chatText.substring(1);
					GMCommands.getInstance().handleCommands(pc, cmd);
					return;
				} else {
					String cmd = chatText.substring(1);
					UserCommands.getInstance().handleCommands(pc, cmd);
					return;
				}
			}

			if (chatText.startsWith("$")) {
				String text = chatText.substring(1);
				chatWorld(pc, text, 12);
				if (!pc.isGm()) {
					pc.checkChatInterval();
				}
				return;
			}

			// 비스킷 추가, 비스킷 수정 주사위 주사위 주사위
			/** 주사위 */
			Akduk2GameSystem gam = new Akduk2GameSystem(); // 주사위
			if (pc.isGambling()) {
				if (chatText.startsWith("홀")) {
					gam.Gambling2(pc, chatText, 1);
					return;
				} else if (chatText.startsWith("짝")) {
					gam.Gambling2(pc, chatText, 2);
					return;
				} else if (chatText.startsWith("1")) {
					gam.Gambling2(pc, chatText, 3);
					return;
				} else if (chatText.startsWith("2")) {
					gam.Gambling2(pc, chatText, 4);
					return;
				} else if (chatText.startsWith("3")) {
					gam.Gambling2(pc, chatText, 5);
					return;
				} else if (chatText.startsWith("4")) {
					gam.Gambling2(pc, chatText, 6);
					return;
				} else if (chatText.startsWith("5")) {
					gam.Gambling2(pc, chatText, 7);
					return;
				} else if (chatText.startsWith("6")) {
					gam.Gambling2(pc, chatText, 8);
					return;
				}
			}
			/** 묵 찌 빠 */

			if (pc.isGambling4()) {
				Akduk4GameSystem gam2 = new Akduk4GameSystem();
				if (chatText.startsWith("묵")) {
					gam2.Gambling4(pc, chatText, 1);
					return;
				} else if (chatText.startsWith("찌")) {
					gam2.Gambling4(pc, chatText, 2);
					return;
				} else if (chatText.startsWith("빠")) {
					gam2.Gambling4(pc, chatText, 3);
					return;

				}
			}

			if (pc.isGambling1()) { // 소막 큰버전
				Akduk3GameSystem gam1 = new Akduk3GameSystem();
				if (chatText.startsWith("오크전사")) {
					gam1.Gambling1(pc, chatText, 1);
					return;
				} else if (chatText.startsWith("스파토이")) {
					gam1.Gambling1(pc, chatText, 2);
					return;
				} else if (chatText.startsWith("멧돼지")) {
					gam1.Gambling1(pc, chatText, 3);
					return;
				} else if (chatText.startsWith("슬라임")) {
					gam1.Gambling1(pc, chatText, 4);
					return;
				} else if (chatText.startsWith("해골")) {
					gam1.Gambling1(pc, chatText, 5);
					return;
				} else if (chatText.startsWith("늑대인간")) {
					gam1.Gambling1(pc, chatText, 6);
					return;
				} else if (chatText.startsWith("버그베어")) {
					gam1.Gambling1(pc, chatText, 7);
					return;
				} else if (chatText.startsWith("장로")) {
					gam1.Gambling1(pc, chatText, 8);
					return;
				} else if (chatText.startsWith("괴물눈")) {
					gam1.Gambling1(pc, chatText, 9);
					return;
				} else if (chatText.startsWith("난쟁이")) { // 난쟁이
					gam1.Gambling1(pc, chatText, 10);
					return;
				} else if (chatText.startsWith("오크")) {
					gam1.Gambling1(pc, chatText, 11);
					return;
				} else if (chatText.startsWith("라이칸")) {
					gam1.Gambling1(pc, chatText, 12);
					return;
				} else if (chatText.startsWith("개구리")) {
					gam1.Gambling1(pc, chatText, 13);
					return;
				} else if (chatText.startsWith("늑대")) {
					gam1.Gambling1(pc, chatText, 14);
					return;
				} else if (chatText.startsWith("가스트")) {
					gam1.Gambling1(pc, chatText, 15);
					return;
				} else if (chatText.startsWith("좀비")) {
					gam1.Gambling1(pc, chatText, 16);
					return;
				} else if (chatText.startsWith("리자드맨")) {
					gam1.Gambling1(pc, chatText, 17);
					return;
				} else if (chatText.startsWith("도베르만")) {
					gam1.Gambling1(pc, chatText, 18);
					return;
				}
			}

			if (pc.isGambling3()) { // 소막
				Akduk1GameSystem gam1 = new Akduk1GameSystem();
				if (chatText.startsWith("오크전사")) {
					gam1.Gambling3(pc, chatText, 1);
					return;
				} else if (chatText.startsWith("스파토이")) {
					gam1.Gambling3(pc, chatText, 2);
					return;
				} else if (chatText.startsWith("멧돼지")) {
					gam1.Gambling3(pc, chatText, 3);
					return;
				} else if (chatText.startsWith("슬라임")) {
					gam1.Gambling3(pc, chatText, 4);
					return;
				} else if (chatText.startsWith("해골")) {
					gam1.Gambling3(pc, chatText, 5);
					return;
				} else if (chatText.startsWith("늑대인간")) {
					gam1.Gambling3(pc, chatText, 6);
					return;
				} else if (chatText.startsWith("버그베어")) {
					gam1.Gambling3(pc, chatText, 7);
					return;
				} else if (chatText.startsWith("장로")) {
					gam1.Gambling3(pc, chatText, 8);
					return;
				} else if (chatText.startsWith("괴물눈")) {
					gam1.Gambling3(pc, chatText, 9);
					return;
				}
			}
			// /////추사위 추가, 주사위 수정 주사위 주사위 주사위

			S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_NORMALCHAT, 0);
			if (!pc.getExcludingList().contains(pc.getName())) {
				pc.sendPackets(s_chatpacket);
			}
			for (L1PcInstance listner : L1World.getInstance().getRecognizePlayer(pc)) {
				if (!listner.getExcludingList().contains(pc.getName())) {
					listner.sendPackets(s_chatpacket);
				}
			}
			// 돕펠 처리
			L1MonsterInstance mob = null;
			for (L1Object obj : pc.getNearObjects().getKnownObjects()) {
				if (obj instanceof L1MonsterInstance) {
					mob = (L1MonsterInstance) obj;
					if (mob.getNpcTemplate().is_doppel() && mob.getName().equals(pc.getName())) {
						Broadcaster.broadcastPacket(mob, new S_NpcChatPacket(mob, chatText, 0));
					}
				}
			}
			LinAllManager.getInstance().NomalchatAppend(pc.getName(), chatText);
			LoggerInstance.getInstance().addChat(Logger.ChatType.Normal, pc, chatText);
		}
			break;
		case 2: {
			if (pc.isGhost()) {
				return;
			}
			// ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_NORMALCHAT, 2);
			if (!pc.getExcludingList().contains(pc.getName())) {
				pc.sendPackets(s_chatpacket);
			}
			for (L1PcInstance listner : L1World.getInstance().getVisiblePlayer(pc, 50)) {
				if (!listner.getExcludingList().contains(pc.getName())) {
					listner.sendPackets(s_chatpacket);
				}
			}
			// 돕펠 처리
			L1MonsterInstance mob = null;
			for (L1Object obj : pc.getNearObjects().getKnownObjects()) {
				if (obj instanceof L1MonsterInstance) {
					mob = (L1MonsterInstance) obj;
					if (mob.getNpcTemplate().is_doppel() && mob.getName().equals(pc.getName())) {
						for (L1PcInstance listner : L1World.getInstance().getVisiblePlayer(mob, 50)) {
							listner.sendPackets(new S_NpcChatPacket(mob, chatText, 2));
						}
					}
				}
			}
		}
			break;
		case 3: {
			chatWorld(pc, chatText, chatType);
			LoggerInstance.getInstance().addChat(Logger.ChatType.Global, pc, chatText);
		}
			break;
		case 4: {
			if (pc.getClanid() != 0) { // 크란 소속중
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				int rank = pc.getClanRank();
				if (clan != null && (rank == L1Clan.CLAN_RANK_PUBLIC || rank == L1Clan.CLAN_RANK_GUARDIAN
						|| rank == L1Clan.CLAN_RANK_PRINCE)) {
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_MSG, 4);

					LinAllManager.getInstance().ClanChatAppend(pc.getClanname(), pc.getName(), chatText);
					LoggerInstance.getInstance().addChat(Logger.ChatType.Clan, pc, chatText);
					for (L1PcInstance listner : clan.getOnlineClanMember()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							listner.sendPackets(s_chatpacket);
						}
					}
				}
			}
		}
			break;
		case 11: {
			if (pc.isInParty()) { // 파티중
				// ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_MSG, 11);
				for (L1PcInstance listner : pc.getParty().getMembers()) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						listner.sendPackets(s_chatpacket);
					}
				}
				LoggerInstance.getInstance().addChat(Logger.ChatType.Party, pc, chatText);
				LinAllManager.getInstance().PartyChatAppend(pc.getName(), chatText);
				/** 파일로그저장 **/
				ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
			}
		}
			break;
		case 12: {
			chatWorld(pc, chatText, chatType);
		}
			break;
		case 13: { // 연합 채팅
			if (pc.getClanid() != 0) { // 혈맹 소속중
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				int rank = pc.getClanRank();
				if (clan != null && (rank == L1Clan.CLAN_RANK_GUARDIAN || rank == L1Clan.CLAN_RANK_PRINCE)) {
					// ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_MSG, 4);
					for (L1PcInstance listner : clan.getOnlineClanMember()) {
						int listnerRank = listner.getClanRank();
						if (!listner.getExcludingList().contains(pc.getName())
								&& (listnerRank == L1Clan.CLAN_RANK_GUARDIAN
										|| listnerRank == L1Clan.CLAN_RANK_PRINCE)) {
							listner.sendPackets(s_chatpacket);
						}
					}
				}
				// monitoring
				// ChatMonitorChannel.getInstance().sendMsg(ChatMonitorChannel.CHAT_MONITOR_CLAN,
				// chatText, pc);
			}
		}
			break;
		case 14: { // 채팅 파티
			if (pc.isInChatParty()) { // 채팅 파티중
				// ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_NORMALCHAT, 14);
				for (L1PcInstance listner : pc.getChatParty().getMembers()) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						listner.sendPackets(s_chatpacket);
					}
				}
			}
			// monitoring
			// ChatMonitorChannel.getInstance().sendMsg(ChatMonitorChannel.CHAT_MONITOR_PARTY,
			// chatText, pc);
		}
			break;
		case 15:
			if (pc.getClan() != null && pc.getClan().getAlliance() != null) {
				pc.getClan().getAlliance().AllianceChat(pc, chatText);
			}
			break;
		}
		if (!pc.isGm()) {
			pc.checkChatInterval();
		}
	}

	private void chatWorld(L1PcInstance pc, String chatText, int chatType) {
		if (pc.isGm() || pc.getAccessLevel() == 1) {
			L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_MSG, chatType));
			pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "[******] " + chatText));
			L1World.getInstance()
					.broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "[******] " + chatText));
			LinAllManager.getInstance().AllChatAppend(pc.getName(), chatText);
		} else if (pc.getLevel() >= Config.GLOBAL_CHAT_LEVEL) {
			if (L1World.getInstance().isWorldChatElabled()) {
				if (pc.get_food() >= 12) { // 5%겟지?
					// ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
					pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));
					for (L1PcInstance listner : L1World.getInstance().getAllPlayers()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							if (listner.isShowTradeChat() && chatType == 12) {
								listner.sendPackets(new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_MSG, chatType));
							} else if (listner.isShowWorldChat() && chatType == 3) {
								listner.sendPackets(new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_MSG, chatType));
								LinAllManager.getInstance().AllChatAppend(pc.getName(), chatText);
							}
						}
					}
				} else {
					pc.sendPackets(new S_ServerMessage(462));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(510));
			}
		} else {
			pc.sendPackets(new S_ServerMessage(195, String.valueOf(Config.GLOBAL_CHAT_LEVEL)));
		}
	}

	@Override
	public String getType() {
		return C_CHAT;
	}
}
