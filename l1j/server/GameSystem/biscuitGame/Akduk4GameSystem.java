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
package l1j.server.GameSystem.biscuitGame;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1BuffNpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_SkillSound;

public class Akduk4GameSystem {
	private static final Logger _log = Logger.getLogger(Akduk4GameSystem.class
			.getName());

	public void Gambling(L1PcInstance player, int bettingmoney) {
		try {
			for (L1Object l1object : L1World.getInstance().getObject()) {
				if (l1object instanceof L1BuffNpcInstance) {
					L1BuffNpcInstance Npc = (L1BuffNpcInstance) l1object;
					if (Npc.getNpcTemplate().get_npcId() == 41923) {
						
						// 비스킷 추가, 비스킷 수정 딜러 뒤로 이동
						L1Teleport.teleport(player, 33445, 32821, (short) 4, 5, true);
						
						L1BuffNpcInstance dealer = Npc;
						String chat = player.getName() + "님 " + bettingmoney
								+ "원 배팅하셨습니다.";
						player.sendPackets(new S_NpcChatPacket(dealer, chat, 0));
						Broadcaster.broadcastPacket(player,
								new S_NpcChatPacket(dealer, chat, 0));
												
						Thread.sleep(3000);
						String chat2 = "(묵,찌,빠)를 앞에서 말씀하세요~";
						player.sendPackets(new S_NpcChatPacket(dealer, chat2, 0));
						Broadcaster.broadcastPacket(player,
								new S_NpcChatPacket(dealer, chat2, 0));
						player.setGamblingMoney4(bettingmoney);
						player.setGambling4(true);
					}// 여기까지
				}
			}
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void Gambling4(L1PcInstance pc, String chatText, int type) {
		S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
				(Opcodes.S_OPCODE_NORMALCHAT), 0);
		if (!pc.getExcludingList().contains(pc.getName())) {
			pc.sendPackets(s_chatpacket);
		}
		for (L1PcInstance listner : L1World.getInstance()
				.getRecognizePlayer(pc)) {
			if (!listner.getExcludingList().contains(pc.getName())) {
				listner.sendPackets(s_chatpacket);
			}
		}
		Random random = new Random();
		try {
			for (L1Object l1object : L1World.getInstance().getObject()) {
				if (l1object instanceof L1BuffNpcInstance) {
					L1BuffNpcInstance Npc = (L1BuffNpcInstance) l1object;
					if (Npc.getNpcTemplate().get_npcId() == 41923) {
						L1BuffNpcInstance dealer = Npc;
						String chat9 = pc.getName() + "졌어요! 같은걸 내야 승리합니다.";
						String chat10 = pc.getName() + " 내가 지다니~한판더해! "
								+ pc.getGamblingMoney4() + "원의 3배를 입금했습니다.";
						String chat11 = pc.getName() + " 졌어요! 같은걸 내야 승리합니다.";
						int gfxid = 3229 + random.nextInt(3);
						int gfxid1 = 3229;
						int gfxid2 = 3230;
						int gfxid3 = 3231;
						switch (type) {
						case 1:
							Thread.sleep(500);
							String chat2 = pc.getName() + "님 묵을 선택 하셨습니다!";
							pc.sendPackets(new S_NpcChatPacket(dealer, chat2, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer,
									chat2, 0));
							Thread.sleep(8000);
							pc.sendPackets(new S_SkillSound(dealer.getId(),
									gfxid));
							pc.sendPackets(new S_SkillSound(pc.getId(), gfxid2));
							pc.broadcastPacket(new S_SkillSound(dealer.getId(),
									gfxid));
							pc.broadcastPacket(new S_SkillSound(pc.getId(),
									gfxid2));
							Thread.sleep(3000);
							if (gfxid == 3229) {
								pc.sendPackets(new S_NpcChatPacket(dealer,
										chat9, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer,
										chat9, 0));
							} else if (gfxid == 3230) {
								pc.sendPackets(new S_NpcChatPacket(dealer,
										chat10, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer,
										chat10, 0));
								pc.getInventory().storeItem(40308,
										pc.getGamblingMoney4() * 3);
							} else {
								pc.sendPackets(new S_NpcChatPacket(dealer,
										chat11, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer,
										chat11, 0));

							}
							break;
						case 2:
							Thread.sleep(500);
							String chat = pc.getName() + "님 찌를 선택 하셨습니다!";
							pc.sendPackets(new S_NpcChatPacket(dealer, chat, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer,
									chat, 0));
							Thread.sleep(8000);
							pc.sendPackets(new S_SkillSound(dealer.getId(),
									gfxid));
							pc.sendPackets(new S_SkillSound(pc.getId(), gfxid1));
							pc.broadcastPacket(new S_SkillSound(dealer.getId(),
									gfxid));
							pc.broadcastPacket(new S_SkillSound(pc.getId(),
									gfxid1));
							Thread.sleep(3000);
							if (gfxid == 3231) {
								pc.sendPackets(new S_NpcChatPacket(dealer,
										chat9, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer,
										chat9, 0));
							} else if (gfxid == 3229) {
								pc.sendPackets(new S_NpcChatPacket(dealer,
										chat10, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer,
										chat10, 0));
								pc.getInventory().storeItem(40308,
										pc.getGamblingMoney4() * 3);
							} else {
								pc.sendPackets(new S_NpcChatPacket(dealer,
										chat11, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer,
										chat11, 0));

							}
							break;
						case 3:
							Thread.sleep(500);
							String chat3 = pc.getName() + "님 빠를 선택 하셨습니다!";
							pc.sendPackets(new S_NpcChatPacket(dealer, chat3, 0));
							pc.broadcastPacket(new S_NpcChatPacket(dealer,
									chat3, 0));
							Thread.sleep(8000);
							pc.sendPackets(new S_SkillSound(dealer.getId(),
									gfxid));
							pc.sendPackets(new S_SkillSound(pc.getId(), gfxid3));
							pc.broadcastPacket(new S_SkillSound(dealer.getId(),
									gfxid));
							pc.broadcastPacket(new S_SkillSound(pc.getId(),
									gfxid3));
							Thread.sleep(3000);
							if (gfxid == 3230) {
								pc.sendPackets(new S_NpcChatPacket(dealer,
										chat9, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer,
										chat9, 0));
							} else if (gfxid == 3231) {
								pc.sendPackets(new S_NpcChatPacket(dealer,
										chat10, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer,
										chat10, 0));
								pc.getInventory().storeItem(40308,
										pc.getGamblingMoney4() * 3);
							} else {
								pc.sendPackets(new S_NpcChatPacket(dealer,
										chat11, 0));
								pc.broadcastPacket(new S_NpcChatPacket(dealer,
										chat11, 0));

							}
							break;
						}
						pc.setGambling4(false);
					}
				}
			}
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

}
