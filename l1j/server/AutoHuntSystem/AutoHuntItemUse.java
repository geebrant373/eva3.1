package l1j.server.AutoHuntSystem;

import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.DECAY_POTION;
import static l1j.server.server.model.skill.L1SkillId.GREATER_HASTE;
import static l1j.server.server.model.skill.L1SkillId.HASTE;
import static l1j.server.server.model.skill.L1SkillId.HOLY_WALK;
import static l1j.server.server.model.skill.L1SkillId.MOVING_ACCELERATION;
import static l1j.server.server.model.skill.L1SkillId.POLLUTE_WATER;
import static l1j.server.server.model.skill.L1SkillId.SLOW;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BLUE_POTION;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BRAVE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL2;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL3;
import static l1j.server.server.model.skill.L1SkillId.STATUS_ELFBRAVE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HASTE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_WISDOM_POTION;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Cooking;
import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_DRAGONPERL;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconWisdomPotion;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Skills;

public class AutoHuntItemUse {
	private L1PcInstance owner = null;
	private static Random _random = new Random();

	public AutoHuntItemUse(L1PcInstance pc) {
		owner = pc;
	}

	public void toPolyScroll() {
		if (owner.getAutoPolyID() == 0)
			return;
		if (owner.getCurrentSpriteId() == owner.getAutoPolyID())
			return;
		toUseScroll(40088);
	}

	public void toUseItem() {
		환상크러스트집게발구이(owner);
		환상코카(owner);
		환상드레이크구이(owner);
		환상버섯스프(owner);
		블레스트아머(owner);
		//드다(owner);
		근거리버프(owner);
		원거리버프(owner);
		전투강화주문서(owner);
		useCurePotion(owner);
		useHealPotion();
		useGreenPotion();
		useDragonPearlPotion();
		if (owner.isKnight()) {
			useBravePotion();
		} else if (owner.isCrown()) {
			useCrownBravePotion();
		} else if (owner.isElf()) {
			useElfBravePotion();
		} else if (owner.isWizard()) {
			useWisdomPotion();
			useBluePotion();
		}
	}

	public void toUseScroll(int itemId) {
		L1ItemInstance item = owner.getInventory().findItemId(itemId);
		if (item == null) {
			return;
		}

		if (!isUseCheck(item))
			return;

		switch (itemId) {
		case 46175:
			if (owner.getMap().isEscapable() || owner.isGm()) {
				int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_GIRAN);
				L1Teleport.teleport(owner, loc[0], loc[1], (short) loc[2], owner.getHeading(), true);
				L1ItemDelay.onItemUse(owner, item);
			}
			break;
		case 40100:
			L1Location newLocation = owner.getLocation().randomLocation(200, true);
			int newX = newLocation.getX();
			int newY = newLocation.getY();
			short mapId = (short) newLocation.getMapId();

			L1Teleport.teleport(owner, newX, newY, (short) mapId, owner.getHeading(), true);
			owner.getInventory().removeItem(item, 1);
			L1ItemDelay.onItemUse(owner, item);
			break;
		case 40088:
			L1PolyMorph.doPoly(owner, owner.getAutoPolyID(), 1800, L1PolyMorph.MORPH_BY_ITEMMAGIC);
			owner.getInventory().removeItem(item, 1);
			L1ItemDelay.onItemUse(owner, item);
			break;
		}
	}

	private void useHealPotion() {
		if (owner.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) { // 디케이포션 상태
			owner.sendPackets(new S_ServerMessage(698)); // 마력에 의해 아무것도 마실 수가 없습니다.
			return;
		}

		int itemId = owner.getAutoPotion();
		L1ItemInstance item = owner.getInventory().findItemId(itemId);
		if (item == null) { 
			return;
		}
		if (!isUseCheck(item))
			return;

		if (owner.getCurrentHpPercent() > owner.get_자동피퍼센트()) {
			return;
		}

		if (itemId == 40010) {
			UseHeallingPotion(owner, Config.빨갱이회복량, 189);
		} else if (itemId == 40011) {
			UseHeallingPotion(owner, Config.주홍이회복량, 194);
		} else if (itemId == 40012) {
			UseHeallingPotion(owner, Config.맑갱이회복량, 197);
		}

		owner.getInventory().removeItem(item, 1);
		L1ItemDelay.onItemUse(owner, item);
	}

	private void UseHeallingPotion(L1PcInstance pc, int healHp, int gfxid) {
		// 아브소르트바리아의 해제
		cancelAbsoluteBarrier(pc);
		pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));
		if (pc.getSkillEffectTimerSet().hasSkillEffect(POLLUTE_WATER)) { // 포르트워타중은 회복량1/2배
			healHp /= 2;
		}
		pc.setCurrentHp(pc.getCurrentHp() + healHp); 
	}

	private void cancelAbsoluteBarrier(L1PcInstance pc) { // 아브소르트바리아의 해제
		if (pc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(ABSOLUTE_BARRIER);
		}
	}

	private void useGreenPotion() {
	    L1ItemInstance item = owner.getInventory().findItemId(40013);
	    if (item == null) {
	        item = owner.getInventory().findItemId(40018);
	    }
	    
	    if (owner.getMoveState().getMoveSpeed() != 0)
	        return;
	    if (item == null) { // 둘 다 없을 경우 종료
	        return;
	    }
	    if (!isUseCheck(item))
	        return;
	    if (owner.getSkillEffectTimerSet().hasSkillEffect(71) == true) {
	        return;
	    }
	    int time = 1800;
	    owner.sendPackets(new S_SkillSound(owner.getId(), 191));
	    owner.broadcastPacket(new S_SkillSound(owner.getId(), 191));
	    if (owner.getHasteItemEquipped() > 0) {
	        return;
	    }
	    owner.setDrink(false);
	    if (owner.getSkillEffectTimerSet().hasSkillEffect(HASTE)) {
	        owner.getSkillEffectTimerSet().killSkillEffectTimer(HASTE);
	        owner.sendPackets(new S_SkillHaste(owner.getId(), 0, 0));
	        owner.broadcastPacket(new S_SkillHaste(owner.getId(), 0, 0));
	        owner.getMoveState().setMoveSpeed(0);
	    } else if (owner.getSkillEffectTimerSet().hasSkillEffect(GREATER_HASTE)) {
	        owner.getSkillEffectTimerSet().killSkillEffectTimer(GREATER_HASTE);
	        owner.sendPackets(new S_SkillHaste(owner.getId(), 0, 0));
	        owner.broadcastPacket(new S_SkillHaste(owner.getId(), 0, 0));
	        owner.getMoveState().setMoveSpeed(0);
	    } else if (owner.getSkillEffectTimerSet().hasSkillEffect(STATUS_HASTE)) {
	        owner.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_HASTE);
	        owner.sendPackets(new S_SkillHaste(owner.getId(), 0, 0));
	        owner.broadcastPacket(new S_SkillHaste(owner.getId(), 0, 0));
	        owner.getMoveState().setMoveSpeed(0);
	    }
	    if (owner.getSkillEffectTimerSet().hasSkillEffect(SLOW)) {
	        owner.getSkillEffectTimerSet().killSkillEffectTimer(SLOW);
	        owner.sendPackets(new S_SkillHaste(owner.getId(), 0, 0));
	        owner.broadcastPacket(new S_SkillHaste(owner.getId(), 0, 0));
	    } else {
	        owner.sendPackets(new S_SkillHaste(owner.getId(), 1, time));
	        owner.broadcastPacket(new S_SkillHaste(owner.getId(), 1, 0));
	        owner.getMoveState().setMoveSpeed(1);
	        owner.getSkillEffectTimerSet().setSkillEffect(STATUS_HASTE, time * 1000);
	    }
	    owner.getInventory().removeItem(item, 1);
	    L1ItemDelay.onItemUse(owner, item);
	}

	private void useBravePotion() {
		L1ItemInstance item = owner.getInventory().findItemId(40014);
		if (item == null) {
	        item = owner.getInventory().findItemId(41415);
	    }
		if (owner.getMoveState().getBraveSpeed() != 0) {
			return;
		}
		if (item == null) {
			// 용기의 물약이 없다
			return;
		}
		if (!isUseCheck(item))
			return;
		if (owner.getSkillEffectTimerSet().hasSkillEffect(71) == true) {
			return;
		}
		cancelAbsoluteBarrier();
		int time = 0;
		if (item.getItemId() == L1ItemId.POTION_OF_EMOTION_BRAVERY) {
			time = 300;
		} else if (item.getItemId() == L1ItemId.B_POTION_OF_EMOTION_BRAVERY) {
			time = 350;
		} else if (item.getItemId() == 40068) {
			time = 480;
			if (owner.getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE)) {
				owner.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_BRAVE);
				owner.sendPackets(new S_SkillBrave(owner.getId(), 0, 0));
				owner.broadcastPacket(new S_SkillBrave(owner.getId(), 0, 0));
				owner.getMoveState().setBraveSpeed(0);
			}
		} else if (item.getItemId() == 41415) {
			time = 700;
			if (owner.getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE)) {
				owner.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_BRAVE);
				owner.sendPackets(new S_SkillBrave(owner.getId(), 0, 0));
				owner.broadcastPacket(new S_SkillBrave(owner.getId(), 0, 0));
				owner.getMoveState().setBraveSpeed(0);
			}
		} else if (item.getItemId() == 40733) {
			time = 600;
			if (owner.getSkillEffectTimerSet().hasSkillEffect(HOLY_WALK)) {
				owner.getSkillEffectTimerSet().killSkillEffectTimer(HOLY_WALK);
				owner.sendPackets(new S_SkillBrave(owner.getId(), 0, 0));
				owner.broadcastPacket(new S_SkillBrave(owner.getId(), 0, 0));
				owner.getMoveState().setBraveSpeed(0);
			}
			if (owner.getSkillEffectTimerSet().hasSkillEffect(MOVING_ACCELERATION)) {
				owner.getSkillEffectTimerSet().killSkillEffectTimer(MOVING_ACCELERATION);
				owner.sendPackets(new S_SkillBrave(owner.getId(), 0, 0));
				owner.broadcastPacket(new S_SkillBrave(owner.getId(), 0, 0));
				owner.getMoveState().setBraveSpeed(0);
			}
		}
		if (item.getItemId() == 40068 || item.getItemId() == 140068) { // 엘븐 와퍼
			owner.sendPackets(new S_SkillBrave(owner.getId(), 1, time));
			owner.broadcastPacket(new S_SkillBrave(owner.getId(), 1, 0));
			owner.getSkillEffectTimerSet().setSkillEffect(STATUS_ELFBRAVE, time * 1000);
		} else {
			owner.sendPackets(new S_SkillBrave(owner.getId(), 1, time));
			owner.broadcastPacket(new S_SkillBrave(owner.getId(), 1, 0));
			owner.getSkillEffectTimerSet().setSkillEffect(STATUS_BRAVE, time * 1000);
			
		}
		owner.sendPackets(new S_SkillSound(owner.getId(), 751));
		owner.broadcastPacket(new S_SkillSound(owner.getId(), 751));
		owner.getMoveState().setBraveSpeed(1);
		owner.getInventory().removeItem(item, 1);
		L1ItemDelay.onItemUse(owner, item);
	}

	private void useCrownBravePotion() {
		L1ItemInstance item = owner.getInventory().findItemId(40031);
		if (owner.getMoveState().getBraveSpeed() != 0) {
			return;
		}
		if (!owner.isCrown()) {
			return;
		}
		if (item == null) {
			return;
		}
		if (!isUseCheck(item))
			return;
		if (owner.getSkillEffectTimerSet().hasSkillEffect(71) == true) {
			return;
		}
		cancelAbsoluteBarrier();
		int time = 0;
		if (item.getItemId() == 40031) {
			time = 480;
			if (owner.getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE)) {
				owner.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_BRAVE);
				owner.sendPackets(new S_SkillBrave(owner.getId(), 0, 0));
				owner.broadcastPacket(new S_SkillBrave(owner.getId(), 0, 0));
				owner.getMoveState().setBraveSpeed(0);
			}
		}
		owner.sendPackets(new S_SkillBrave(owner.getId(), 1, time));
		owner.broadcastPacket(new S_SkillBrave(owner.getId(), 1, 0));
		owner.getSkillEffectTimerSet().setSkillEffect(STATUS_BRAVE, time * 1000);

		owner.sendPackets(new S_SkillSound(owner.getId(), 751));
		owner.broadcastPacket(new S_SkillSound(owner.getId(), 751));
		owner.getMoveState().setBraveSpeed(1);
		owner.getInventory().removeItem(item, 1);
		L1ItemDelay.onItemUse(owner, item);
	}
	
	private void useElfBravePotion() {
		L1ItemInstance item = owner.getInventory().findItemId(40068);
		if (owner.getMoveState().getBraveSpeed() != 0) {
			return;
		}
		if (item == null) {
			return;
		}
		if (!isUseCheck(item))
			return;
		if (owner.getSkillEffectTimerSet().hasSkillEffect(71) == true) {
			return;
		}
		cancelAbsoluteBarrier();
		int time = 0;
		if (item.getItemId() == L1ItemId.POTION_OF_EMOTION_BRAVERY) {
			time = 300;
		} else if (item.getItemId() == L1ItemId.B_POTION_OF_EMOTION_BRAVERY) {
			time = 350;
		} else if (item.getItemId() == 40068) {
			time = 480;
			if (owner.getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE)) {
				owner.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_BRAVE);
				owner.sendPackets(new S_SkillBrave(owner.getId(), 0, 0));
				owner.broadcastPacket(new S_SkillBrave(owner.getId(), 0, 0));
				owner.getMoveState().setBraveSpeed(0);
			}
		} else if (item.getItemId() == 41415) {
			time = 700;
			if (owner.getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE)) {
				owner.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_BRAVE);
				owner.sendPackets(new S_SkillBrave(owner.getId(), 0, 0));
				owner.broadcastPacket(new S_SkillBrave(owner.getId(), 0, 0));
				owner.getMoveState().setBraveSpeed(0);
			}
		} else if (item.getItemId() == 40733) {
			time = 600;
			if (owner.getSkillEffectTimerSet().hasSkillEffect(HOLY_WALK)) {
				owner.getSkillEffectTimerSet().killSkillEffectTimer(HOLY_WALK);
				owner.sendPackets(new S_SkillBrave(owner.getId(), 0, 0));
				owner.broadcastPacket(new S_SkillBrave(owner.getId(), 0, 0));
				owner.getMoveState().setBraveSpeed(0);
			}
			if (owner.getSkillEffectTimerSet().hasSkillEffect(MOVING_ACCELERATION)) {
				owner.getSkillEffectTimerSet().killSkillEffectTimer(MOVING_ACCELERATION);
				owner.sendPackets(new S_SkillBrave(owner.getId(), 0, 0));
				owner.broadcastPacket(new S_SkillBrave(owner.getId(), 0, 0));
				owner.getMoveState().setBraveSpeed(0);
			}
		}
		if (item.getItemId() == 40068 || item.getItemId() == 140068) { // 엘븐 와퍼
			owner.sendPackets(new S_SkillBrave(owner.getId(), 1, time));
			owner.broadcastPacket(new S_SkillBrave(owner.getId(), 1, 0));
			owner.getSkillEffectTimerSet().setSkillEffect(STATUS_ELFBRAVE, time * 1000);
		} else {
			owner.sendPackets(new S_SkillBrave(owner.getId(), 1, time));
			owner.broadcastPacket(new S_SkillBrave(owner.getId(), 1, 0));
			owner.getSkillEffectTimerSet().setSkillEffect(STATUS_BRAVE, time * 1000);
			
		}
		owner.sendPackets(new S_SkillSound(owner.getId(), 751));
		owner.broadcastPacket(new S_SkillSound(owner.getId(), 751));
		owner.getMoveState().setBraveSpeed(1);
		owner.getInventory().removeItem(item, 1);
		L1ItemDelay.onItemUse(owner, item);
	}
	
	private void useDragonPearlPotion() {
		if (owner.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGONPERL))
			return;

		L1ItemInstance item = owner.getInventory().findItemId(437011);
		if (item == null) {
			// 드래곤의 진주가 없다
			return;
		}
		if (!isUseCheck(item))
			return;

		if (owner.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION) == true) { // 디케이포션 상태
			owner.sendPackets(new S_ServerMessage(698));
			return;
		}

		if (owner.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGONPERL)) {
			owner.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.STATUS_DRAGONPERL);
			owner.sendPackets(new S_PacketBox(S_PacketBox.DRAGONPERL, 0, 0));
			owner.broadcastPacket(new S_DRAGONPERL(owner.getId(), 0));
			owner.sendPackets(new S_DRAGONPERL(owner.getId(), 0));
		}
		owner.cancelAbsoluteBarrier();
		int time = 600 * 1000;
		int stime = ((time / 1000) / 4) - 2;
		owner.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_DRAGONPERL, time);
		owner.sendPackets(new S_PacketBox(S_PacketBox.DRAGONPERL, stime, 8));
		owner.sendPackets(new S_DRAGONPERL(owner.getId(), 8));
		owner.broadcastPacket(new S_DRAGONPERL(owner.getId(), 8));
		owner.sendPackets(new S_SkillSound(owner.getId(), 197));
		owner.broadcastPacket(new S_SkillSound(owner.getId(), 197));

		owner.getInventory().removeItem(item, 1);
		L1ItemDelay.onItemUse(owner, item);
		owner.sendPackets(new S_ServerMessage(1065));
	}

	private void useWisdomPotion() {
		if (owner.getSkillEffectTimerSet().hasSkillEffect(STATUS_WISDOM_POTION))
			return;
		L1ItemInstance item = owner.getInventory().findItemId(210113);
		if (item == null) {
			// 지혜의 물약이 없다
			return;
		}
		if (!isUseCheck(item))
			return;
		if (owner.getSkillEffectTimerSet().hasSkillEffect(71) == true) {
			return;
		}
		cancelAbsoluteBarrier();
		int time = 1000;

		if (!owner.getSkillEffectTimerSet().hasSkillEffect(STATUS_WISDOM_POTION)) {
			owner.getAbility().addSp(2);
			owner.addMpr(2);
		}
		owner.sendPackets(new S_SkillIconWisdomPotion((int) (time)));
		owner.sendPackets(new S_SkillSound(owner.getId(), 750));
		owner.broadcastPacket(new S_SkillSound(owner.getId(), 750));
		owner.getSkillEffectTimerSet().setSkillEffect(STATUS_WISDOM_POTION, time * 1000);
		owner.getInventory().removeItem(item, 1);
		L1ItemDelay.onItemUse(owner, item);
	}

	private void useBluePotion() {
		if (owner.getSkillEffectTimerSet().hasSkillEffect(STATUS_BLUE_POTION))
			return;
		L1ItemInstance item = owner.getInventory().findItemId(210114);
		if (item == null) {
			// 파란물약이 없다
			return;
		}
		if (!isUseCheck(item))
			return;
		if (owner.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
			return;
		}
		cancelAbsoluteBarrier();
		int time = 1800;

		// owner.sendPackets(new S_SkillIconGFX(34, time, true));
		owner.sendPackets(new S_SkillSound(owner.getId(), 190));
		owner.broadcastPacket(new S_SkillSound(owner.getId(), 190));
		owner.getSkillEffectTimerSet().setSkillEffect(STATUS_BLUE_POTION, time * 1000);
		owner.sendPackets(new S_ServerMessage(1007));
		owner.getInventory().removeItem(item, 1);
		L1ItemDelay.onItemUse(owner, item);
	}

	public void useCurePotion(L1PcInstance pc) {
		L1ItemInstance item = owner.getInventory().findItemId(40507);
		if (item == null) {
			return;
		}
		
		if (!isUseCheck(item)) {
			return;
		}
		
		if (owner.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
			return;
		}
		
		if (owner.getSkillEffectTimerSet().hasSkillEffect(1006)||owner.getSkillEffectTimerSet().hasSkillEffect(1008)
				||owner.getSkillEffectTimerSet().hasSkillEffect(1009)||owner.getSkillEffectTimerSet().hasSkillEffect(1010)
				||owner.getSkillEffectTimerSet().hasSkillEffect(1011)
				|| owner.getSkillEffectTimerSet().hasSkillEffect(1007)||owner.getSkillEffectTimerSet().hasSkillEffect(30007)
				|| owner.getSkillEffectTimerSet().hasSkillEffect(30002)) {
			owner.cancelAbsoluteBarrier();
			owner.sendPackets(new S_SkillSound(owner.getId(), 192), true);
			Broadcaster.broadcastPacket(owner, new S_SkillSound(owner.getId(), 192), true);
			owner.curePoison();
			owner.getInventory().removeItem(item, 1);
			L1ItemDelay.onItemUse(owner, item);
		}
	}

	public void 환상크러스트집게발구이(L1PcInstance pc) {
		L1ItemInstance item = owner.getInventory().findItemId(436017);
		if (item == null) {
			return;
		}
		if (!pc.isElf()) {
			return;
		}
		if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_16_S)) {
			L1Cooking.useCookingItem(pc, item);
		}
	}
	
	public void 환상코카(L1PcInstance pc) {
		L1ItemInstance item = owner.getInventory().findItemId(436019);
		if (item == null) {
			return;
		}
		if (!pc.isCrown() && !pc.isKnight()) {
			return;
		}
		if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_18_S)) {
			L1Cooking.useCookingItem(pc, item);
		}
	}
	
	public void 환상드레이크구이(L1PcInstance pc) {
		L1ItemInstance item = owner.getInventory().findItemId(436022);
		if (item == null) {
			return;
		}
		if (!pc.isWizard()) {
			return;
		}
		if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_21_S)) {
			L1Cooking.useCookingItem(pc, item);
		}
	}
	
	public void 환상버섯스프(L1PcInstance pc) {
		L1ItemInstance item = owner.getInventory().findItemId(41292);
		if (item == null) {
			return;
		}
		if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_7_S)) {
			L1Cooking.useCookingItem(pc, item);
		}
	}
	
	public void 블레스트아머(L1PcInstance pc) {
		L1Skills skill = SkillsTable.getInstance().getTemplate(21);
		L1ItemInstance item = owner.getInventory().findItemId(40879);
		if (item == null) {
			return;
		}
		for (L1ItemInstance item1 : pc.getInventory().getItems()) {
			if (item1 == null)
				continue;
			if (item1._isRunningArmor) {
				continue;
			}
			if (!item1.isEquipped()) {
				continue;
			}
			if (item1.getItem().getType2() == 2 && item1.getItem().getType() == 2) {
				pc.sendPackets(
						new S_ServerMessage(161, String.valueOf(item1.getLogName()).trim(), "$245", "$247"));
				item1.setSkillArmorEnchant(pc, L1SkillId.BLESSED_ARMOR, skill.getBuffDuration() * 1000);
				pc.sendPackets(new S_SkillSound(pc.getId(), 748));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 748));
				pc.getInventory().consumeItem(40879, 1);
				break;
			}
		}
	}
	
	public void 드다(L1PcInstance pc) {
		L1ItemInstance item = owner.getInventory().findItemId(437010);
		if (item == null) {
			return;
		}
		if (pc.getAinHasad() < 1000000) {
			pc.calAinHasad(1000000);
			pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc.getAinHasad()));
			pc.sendPackets(new S_SystemMessage("\\fT드래곤의 다이아몬드(1)가 자동 복용되었습니다."));
			pc.getInventory().removeItem(item, 1);
			L1ItemDelay.onItemUse(pc, item); // 아이템 지연 개시
		}
	}
	
	public void 근거리버프(L1PcInstance pc) {
		L1ItemInstance item = owner.getInventory().findItemId(31117);
		if (item == null) {
			return;
		}
		if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.FIRE_WEAPON)) {
			int[] allBuffSkill = { 148, 26, 42,54, 48, 151 };
			L1SkillUse l1skilluse = new L1SkillUse();
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
						L1SkillUse.TYPE_GMBUFF);
			}
			pc.getInventory().consumeItem(31117, 1);
		}
	}
	
	public void 원거리버프(L1PcInstance pc) {
		L1ItemInstance item = owner.getInventory().findItemId(31118);
		if (item == null) {
			return;
		}
		if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STORM_SHOT)) {
			int[] allBuffSkill = {  26, 42,54, 48, 166 };//스톰샷
			L1SkillUse l1skilluse = new L1SkillUse();
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
						L1SkillUse.TYPE_GMBUFF);
			}
			pc.getInventory().consumeItem(31118, 1);
		}
	}
	
	public void 전투강화주문서(L1PcInstance pc) {
		L1ItemInstance item = owner.getInventory().findItemId(437004);
		if (item == null) {
			return;
		}
		if (!pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CASHSCROLL3)) {
			useCashScroll(pc, 437004);
			pc.getInventory().consumeItem(437004, 1);
		}
	}
	
	private boolean isUseCheck(L1ItemInstance item) {
		int delay_id = 0;
		if (item.getItem().getType2() == 0) {
			if (item.getItem() instanceof L1EtcItem) {
				delay_id = ((L1EtcItem) item.getItem()).get_delayid();
			}
		}
		if (delay_id != 0) {
			if (owner.hasItemDelay(delay_id) == true) {
				return false;
			}
		}
		return true;
	}

	private void cancelAbsoluteBarrier() {
		if (owner.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) {
			owner.getSkillEffectTimerSet().removeSkillEffect(ABSOLUTE_BARRIER);
		}
	}
	
	private void useCashScroll(L1PcInstance pc, int item_id) {
		int time = 3600;
		int scroll = 0;

		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CASHSCROLL)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_CASHSCROLL);
			pc.addHpr(-4);
			pc.addMaxHp(-50);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CASHSCROLL2)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_CASHSCROLL2);
			pc.addMpr(-4);
			pc.addMaxMp(-40);
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CASHSCROLL3)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_CASHSCROLL3);
			pc.addDmgup(-3);
			pc.addHitup(-3);
			pc.getAbility().addSp(-3);
			pc.sendPackets(new S_SPMR(pc));
		}

		if (item_id == L1ItemId.INCRESE_HP_SCROLL || item_id == L1ItemId.CHUNSANG_HP_SCROLL) {
			scroll = 6993;
			pc.addHpr(4);
			pc.addMaxHp(50);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		} else if (item_id == L1ItemId.INCRESE_MP_SCROLL || item_id == L1ItemId.CHUNSANG_MP_SCROLL) {
			scroll = 6994;
			pc.addMpr(4);
			pc.addMaxMp(40);
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		} else if (item_id == L1ItemId.INCRESE_ATTACK_SCROLL || item_id == L1ItemId.CHUNSANG_ATTACK_SCROLL) {
			scroll = 6995;
			pc.addDmgup(3);
			pc.addHitup(3);
			pc.getAbility().addSp(3);
			pc.sendPackets(new S_SPMR(pc));
		}

		pc.sendPackets(new S_SkillSound(pc.getId(), scroll));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), scroll));
		pc.getSkillEffectTimerSet().setSkillEffect(scroll, time * 1000);
	}
}
