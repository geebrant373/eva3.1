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

package l1j.server.server.model.item.function;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;

@SuppressWarnings("serial")
public class MagicDoll extends L1ItemInstance {

	public MagicDoll(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			int itemId = this.getItemId();
			useMagicDoll(pc, itemId, this.getId());
		}
	}

	public static void checkAutoDoll(L1PcInstance pc) {
	    if (!pc.isAutoDollFlag()) {
	        return;
	    }
	    if (!pc.getDollList().isEmpty()) {
	        return;
	    }
	    if (!pc.getInventory().checkItem(41246)) {
	        return;
	    }

	    int usedId = pc.getusedDollId();
	    if (usedId == 0) {
	        return; // 이전에 사용한 인형이 없음
	    }

	    L1ItemInstance item = pc.getInventory().getItem(usedId);
	    if (item == null) {
	        return; // 인형이 인벤에서 사라짐
	    }

	    if (!isAutoDollItem(item.getItemId())) {
	        return; // 자동소환 대상 아님
	    }

	    MagicDoll dollItem = new MagicDoll(item.getItem());
	    dollItem.useMagicDoll(pc, item.getItemId(), item.getId());
	}

	private static boolean isAutoDollItem(int itemId) {
	    // 자동 소환 적용할 모든 인형 아이템 (정상 itemId 기준)
	    switch (itemId) {
	        case 743:      // 마법인형 : 나이트발드
	        case 744:      // 마법인형 : 시어
	        case 41248:    // 마법인형 : 버그베어
	        case 41249:    // 마법인형 : 서큐버스
	        case 41250:    // 마법인형 : 늑대인간
	        case 41916:    // 마법인형 : 허수아비
	        case 430000:   // 마법인형 : 돌골렘
	        case 430001:   // 마법인형 : 장로
	        case 430002:   // 마법인형 : 크러스트시안
	        case 430003:   // 마법인형 : 시댄서
	        case 430004:   // 마법인형 : 에티
	        case 430500:   // 마법인형 : 코카트리스
	        case 430505:   // 마법인형 : 라미아
	        case 430506:   // 마법인형 : 스파토이
	        case 447016:   // 마법인형 : 리치
	        case 4370598:  // 마법인형 : 자이언트
	        case 4370599:  // 마법인형 : 사이클롭스
	        case 4370600:  // 마법인형 : 머미로드
	        case 5370600:  // 마법인형 : 데몬
	        case 5370601:  // 마법인형 : 데스나이트
	        case 5370602:  // 마법인형 : 얼음여왕
	        case 5370603:  // 마법인형 : 타락
	        case 5370604:  // 마법인형 : 바란카
	        case 5370605:  // 마법인형 : 안타라스
	        case 5370606:  // 마법인형 : 발라카스
	        case 5370607:  // 마법인형 : 린드비오르
	        case 5370608:  // 마법인형 : 파푸리온
	            return true;
	        default:
	            return false;
	    }
	}

    
	public void useMagicDoll(L1PcInstance pc, int itemId, int itemObjectId) {
		if (pc.isInvisble()) {
			return;
		}
		
		boolean isAppear = true;

		L1DollInstance doll = null;
		Object[] dollList = pc.getDollList().values().toArray();
		for (Object dollObject : dollList) {
			doll = (L1DollInstance) dollObject;
			if (doll.getItemObjId() == itemObjectId) { // 이미 꺼내고 있는 매직 실업 수당
				isAppear = false;
				break;
			}
		}
		
		if (isAppear) {

			int npcId = 0;
			int dollType = 0;
			int consumecount = 0;
			int dollTime = 0;

			switch (itemId) {
			case L1ItemId.DOLL_허수아비:
				npcId = 46214;
				dollType = L1DollInstance.DOLLTYPE_허수아비;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_BUGBEAR:
				npcId = 80106;
				dollType = L1DollInstance.DOLLTYPE_BUGBEAR;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_SUCCUBUS:
				npcId = 80107;
				dollType = L1DollInstance.DOLLTYPE_SUCCUBUS;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_WAREWOLF:
				npcId = 80108;
				dollType = L1DollInstance.DOLLTYPE_WAREWOLF;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_STONEGOLEM:
				npcId = 4500150;
				dollType = L1DollInstance.DOLLTYPE_STONEGOLEM;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_ELDER:
				npcId = 4500151;
				dollType = L1DollInstance.DOLLTYPE_ELDER;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_CRUSTACEA:
				npcId = 4500152;
				dollType = L1DollInstance.DOLLTYPE_CRUSTACEA;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_SEADANCER:
				npcId = 4500153;
				dollType = L1DollInstance.DOLLTYPE_SEADANCER;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_에티:
				npcId = 4500154;
				dollType = L1DollInstance.DOLLTYPE_에티;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_COCATRIS:
				npcId = 4500155;
				dollType = L1DollInstance.DOLLTYPE_COCATRIS;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_DRAGON_M:
				npcId = 4500156;
				dollType = L1DollInstance.DOLLTYPE_DRAGON_M;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_DRAGON_W:
				npcId = 4500157;
				dollType = L1DollInstance.DOLLTYPE_DRAGON_W;
				consumecount = 50;
				dollTime = 1800;
				break;
			// 3단계
			case L1ItemId.DOLLTYPE_사이클롭스:
				npcId = 47002;
				dollType = L1DollInstance.DOLLTYPE_사이클롭스;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLLTYPE_시어:
				npcId = 511;
				dollType = L1DollInstance.DOLLTYPE_시어;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLLTYPE_나이트발드:
				npcId = 510;
				dollType = L1DollInstance.DOLLTYPE_나이트발드;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLLTYPE_리치:
				npcId = 900224;
				dollType = L1DollInstance.DOLLTYPE_리치;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLLTYPE_데스나이트:
				npcId = 1600247;
				dollType = L1DollInstance.DOLLTYPE_데스나이트;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLLTYPE_데몬:
				npcId = 1600246;
				dollType = L1DollInstance.DOLLTYPE_데몬;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_SPATOI:
				npcId = 4500161;
				dollType = L1DollInstance.DOLLTYPE_스파토이;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLLTYPE_road:
				npcId = 47001;
				dollType = L1DollInstance.DOLLTYPE_road;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLLTYPE_타락:
				npcId = 410115;
				dollType = L1DollInstance.DOLLTYPE_타락;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLLTYPE_얼음여왕:
				npcId = 410118;
				dollType = L1DollInstance.DOLLTYPE_얼음여왕;
				consumecount = 50;
				dollTime = 1800;
				break;
			case L1ItemId.DOLLTYPE_바란카:
				npcId = 4000113;
				dollType = L1DollInstance.DOLLTYPE_바란카; 
				consumecount = 50; 
				dollTime = 1800;
				break;
			case L1ItemId.DOLLTYPE_안타라스:
				npcId = 410156;
				dollType = L1DollInstance.DOLLTYPE_안타라스; 
				consumecount = 50; 
				dollTime = 1800;
				break;
			case 5370606:
				npcId = 410159;
				dollType = L1DollInstance.DOLLTYPE_발라카스;
				consumecount = 50;
				dollTime = 1800;
				break;
			case 5370607:
				npcId = 410158;
				dollType = L1DollInstance.DOLLTYPE_린드비오르;
				consumecount = 50;
				dollTime = 1800;
				break;
			case 5370608:
				npcId = 410157;
				dollType = L1DollInstance.DOLLTYPE_파푸리온;
				consumecount = 50;
				dollTime = 1800;
				break;
			}
			
			if (!pc.getInventory().checkItem(41246, consumecount)) {
				pc.sendPackets(new S_ServerMessage(337, "$5240"));
				pc.setAutoDollFlag(false);
				return;
			}
			if (dollList.length >= Config.MAX_DOLL_COUNT) {
				// \f1 더 이상의 monster를 조종할 수 없습니다.
				pc.sendPackets(new S_ServerMessage(319));
				return;
			}
			if (itemId == 437018 && pc.getLevel() > 60) {
				pc.sendPackets(new S_SystemMessage("쫄법사 인형은 60까지 사용할 수 있습니다."));
				return;
			}
			L1Npc template = NpcTable.getInstance().getTemplate(npcId);
			doll = new L1DollInstance(template, pc, dollType, itemObjectId, dollTime * 1000);
			pc.setusedDollId(itemObjectId);
			pc.sendPackets(new S_SkillSound(doll.getId(), 5935));
			Broadcaster.broadcastPacket(pc, new S_SkillSound(doll.getId(), 5935));
			pc.sendPackets(new S_SkillIconGFX(56, dollTime));
			pc.sendPackets(new S_OwnCharStatus(pc));
			pc.getInventory().consumeItem(41246, consumecount);
		} else {
			doll.deleteDoll();
			pc.setusedDollId(0);
			pc.sendPackets(new S_SkillIconGFX(56, 0));
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
	}
}
