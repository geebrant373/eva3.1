package l1j.server.GameSystem.Auto;

import static l1j.server.server.model.skill.L1SkillId.MOB_CURSEPARALYZ_18;
import static l1j.server.server.model.skill.L1SkillId.MOB_CURSEPARALYZ_19;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BLUE_POTION;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL2;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL3;
import static l1j.server.server.model.skill.L1SkillId.STATUS_POISON;
import static l1j.server.server.model.skill.L1SkillId.STATUS_POISON_PARALYZING;
import static l1j.server.server.model.skill.L1SkillId.STATUS_POISON_SILENCE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_WISDOM_POTION;

import java.util.ArrayList;
import java.util.Collection;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.clientpackets.C_ItemUSe;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Cooking;
import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.item.function.BluePotion;
import l1j.server.server.model.item.function.BravePotion;
import l1j.server.server.model.item.function.CurePotion;
import l1j.server.server.model.item.function.GreenPotion;
import l1j.server.server.model.item.function.HealingPotion;
import l1j.server.server.model.item.function.WisdomPotion;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_DRAGONPERL;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Skills;

public class AutoPotionController implements Runnable {

	private static AutoPotionController _instance;

	public static AutoPotionController getInstance() {
		if (_instance == null) {
			_instance = new AutoPotionController();
		}
		return _instance;
	}

	public AutoPotionController() {
		GeneralThreadPool.getInstance().execute(this);
	}

	private Collection<L1PcInstance> list = null;

	@Override
	public void run() {
		while (true) {
			try {
				list = L1World.getInstance().getAllPlayers();
				for (L1PcInstance pc : list) {
					if (pc == null) {
						continue;
					} 
					try {
						doAutoPotionAction(pc);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					Thread.sleep(300);
				} catch (Exception e) {
				}
			}
		}
	}

	private void doAutoPotionAction(L1PcInstance pc) {
		if (!pc.is_자동물약사용()) {
			return;
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {
			return;
		}
		if (pc.isTeleport() || pc.isDead()) {
			return;
		}
		if ((pc.getSkillEffectTimerSet().hasSkillEffect(71)) || (pc.getSkillEffectTimerSet().hasSkillEffect(87)) || (pc.getSkillEffectTimerSet().hasSkillEffect(30081)) || (pc.getSkillEffectTimerSet().hasSkillEffect(40007))
				|| (pc.getSkillEffectTimerSet().hasSkillEffect(30005)) || (pc.getSkillEffectTimerSet().hasSkillEffect(30006)) || (pc.getSkillEffectTimerSet().hasSkillEffect(22055))
				|| (pc.getSkillEffectTimerSet().hasSkillEffect(22025)) || (pc.getSkillEffectTimerSet().hasSkillEffect(22026)) || (pc.getSkillEffectTimerSet().hasSkillEffect(22027))
				|| (pc.getSkillEffectTimerSet().hasSkillEffect(157)) || (pc.getSkillEffectTimerSet().hasSkillEffect(30003)) || (pc.getSkillEffectTimerSet().hasSkillEffect(30004))
				|| (pc.getSkillEffectTimerSet().hasSkillEffect(208)) || (pc.getSkillEffectTimerSet().hasSkillEffect(212)) || (pc.getSkillEffectTimerSet().hasSkillEffect(103))
				|| (pc.getSkillEffectTimerSet().hasSkillEffect(66)) || (pc.getSkillEffectTimerSet().hasSkillEffect(33)) || (pc.getSkillEffectTimerSet().hasSkillEffect(1011))
				|| (pc.getSkillEffectTimerSet().hasSkillEffect(10101))) {
			return;
		}
		if (pc.isFishing()) {
			return;
		}
		if (pc.isPrivateShop() || pc.isAutoClanjoin()) {
			return;
		}

		if (!pc.getMap().isUsableItem() && !pc.isGm()) {
			return;
		}
		
		ArrayList<Integer> _물약리스트 = pc.get_자동물약리스트();
		if (_물약리스트 == null || _물약리스트.isEmpty()) {
			return;
		}
		for (int itemId : _물약리스트) {
			L1ItemInstance item = pc.getInventory().findItemId(itemId); 
			if (item == null) {
				continue;
			}
			if (item.getItem().getType2() != 0) {
				continue;
			}
			int delay_id = ((L1EtcItem) item.getItem()).get_delayid();
			if (delay_id != 0) { 
				if (pc.hasItemDelay(delay_id)) {
					continue;
				}
			}
			int 퍼센트 = 100 * pc.getCurrentHp() / pc.getMaxHp();
			switch(item.getItemId()) {
			case 40013:
			case 40018:
			case 140013:
			case 140018:
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.HASTE) && !pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GREATER_HASTE) && !pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HASTE)) {
					GreenPotion.useGreenPotion(pc, item.getItemId());
				}
				break;
			case 40024:
				if (퍼센트 < pc.get_자동물약퍼센트()) {
					HealingPotion.UseHeallingPotion(pc, 55, 197);
					pc.getInventory().consumeItem(40024, 1);
					L1ItemDelay.onItemUse(pc, item); // 아이템 지연 개시
				}
				break;
			case 40010: {
				if (퍼센트 < pc.get_자동물약퍼센트()) {
					HealingPotion.UseHeallingPotion(pc, Config.빨갱이회복량, 189);
					pc.getInventory().consumeItem(40010, 1);
					L1ItemDelay.onItemUse(pc, item); // 아이템 지연 개시
				}
			}
				break;
			case 40011:
				if (퍼센트 < pc.get_자동물약퍼센트()) {
					HealingPotion.UseHeallingPotion(pc, Config.주홍이회복량, 194);
					pc.getInventory().consumeItem(40011, 1);
					L1ItemDelay.onItemUse(pc, item); // 아이템 지연 개시
				}
				break;
			case 40012:
				if (퍼센트 < pc.get_자동물약퍼센트()) {
					HealingPotion.UseHeallingPotion(pc, Config.맑갱이회복량, 197);
					pc.getInventory().consumeItem(40012, 1);
					L1ItemDelay.onItemUse(pc, item); // 아이템 지연 개시
				}
				break;
			case 437010: // 드다
				if (pc.getAinHasad() < 1000000) {
					pc.calAinHasad(1000000);
					pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc.getAinHasad()));
					pc.sendPackets(new S_SystemMessage("\\fT드래곤의 다이아몬드(1)가 자동 복용되었습니다."));
					pc.getInventory().removeItem(item, 1);
					L1ItemDelay.onItemUse(pc, item); // 아이템 지연 개시
				}
				break;
			case 40014:
			case 41415:
			case 140014: // 용기의 물약
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BRAVE)) {
					BravePotion.checkCondition(pc, item);
				}
				break;
			case 40068: 
			case 140068: // 엘븐 와퍼
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_ELFBRAVE)) {
					BravePotion.checkCondition(pc, item);
				}
				break;
			case 40015:
			case 140015:
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_BLUE_POTION)) {
					BluePotion.checkCondition(pc, item);
				}
				break;
			case 1437011: // 드래곤의 진주
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGONPERL)) {
					useDragonPearl(pc);
				}
				break;
			case 437011: // 드래곤의 진주
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGONPERL)) {
					if (!pc.getInventory().checkItem(437011)) {
					} else {
						pc.getInventory().consumeItem(437011, 1);
						useDragonPearl(pc);
					}
				}
				break;
			case 31117: // 버프 물약 : 근거리
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.FIRE_WEAPON)) {
					int[] allBuffSkill = { 148, 26, 42,54, 48, 151 };
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
								L1SkillUse.TYPE_GMBUFF);
					}
					pc.getInventory().consumeItem(31117, 1);
				}
				break;
			case 31118: // 버프 물약 : 원거리
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STORM_SHOT)) {
					int[] allBuffSkill = {  26, 42,54, 48, 166 };//스톰샷
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
								L1SkillUse.TYPE_GMBUFF);
					}
					pc.getInventory().consumeItem(31118, 1);
				}
				break;
			case 437003: // 마력 증강의 주문서
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CASHSCROLL2)) {
					useCashScroll(pc, 437003);
					pc.getInventory().consumeItem(437003, 1);
				}
				break;
			case 437004: // 전투 강화의 주문서
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CASHSCROLL3)) {
					useCashScroll(pc, 437004);
					pc.getInventory().consumeItem(437004, 1);
				}
				break;
			case 40016:
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_WISDOM_POTION)) {
					WisdomPotion.checkCondition(pc, item);
				}
				break;
			case 40017: // 해독제
			case 40507:
				if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_POISON) || pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_POISON_PARALYZING)
						|| pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_POISON_SILENCE) || pc.getSkillEffectTimerSet().hasSkillEffect(MOB_CURSEPARALYZ_18)
						|| pc.getSkillEffectTimerSet().hasSkillEffect(MOB_CURSEPARALYZ_19)) {
					CurePotion.checkCondition(pc, item);
				}
				break;
			case 436017: // 환상크러스트집게발구이 원거리
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_16_S)) {
					L1Cooking.useCookingItem(pc, item);
				}
				break;
			case 436019: // 환상코카 근거리
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_18_S)) {
					L1Cooking.useCookingItem(pc, item);
				}
				break;
			case 436022: // 환상드레이크구이 SP
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_21_S)) {
					L1Cooking.useCookingItem(pc, item);
				}
				break;
			case 41292: // 환상버섯스프
				if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_7_S)) {
					L1Cooking.useCookingItem(pc, item);
				}
				break;
			case 40879: // 블레스트아머
				L1Skills skill = SkillsTable.getInstance().getTemplate(21);
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
	
	private void useDragonPearl(L1PcInstance pc) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(71)) {
			pc.sendPackets(new S_ServerMessage(698));
			return;
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGONPERL)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.STATUS_DRAGONPERL);	
			pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONPERL, 0, 0));
			Broadcaster.broadcastPacket(pc, new S_DRAGONPERL(pc.getId(), 0));
			pc.sendPackets(new S_DRAGONPERL(pc.getId(), 0));
		}
		pc.cancelAbsoluteBarrier();
		int time = 600 * 1000;
		int stime = ((time / 1000) / 4) - 2;
		pc.sendPackets(new S_SkillSound(pc.getId(), 197));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), 197));
		pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_DRAGONPERL, time);
		//pc.sendPackets(new S_DRAGONPERL(pc.getId(), 8));
		//pc.broadcastPacket(new S_DRAGONPERL(pc.getId(), 8));
		pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONPERL, 8, stime));
	}
}