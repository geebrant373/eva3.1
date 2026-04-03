package l1j.server.AutoHuntSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import l1j.server.Config;
import l1j.server.MJ3SEx.EActionCodes;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.monitor.L1PcMonitor;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_SystemMessage;

public class AutoHuntController extends L1PcMonitor {
	public AutoHuntController(int oId) {
		super(oId);
	}

	@Override
	public void execTask(L1PcInstance pc) {
		try {
			if (owner != pc) {
				owner = pc;
			}
			getSource(owner);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Random _rnd = new Random(System.nanoTime());

	public final int AUTO_STATUS_NONE = -1;
	public final int AUTO_STATUS_WALK = 0;
	public final int AUTO_STATUS_ATTACK = 1;
	public final int AUTO_STATUS_PICKUP_ITEM = 2;
	private int moveDelayCount = 0;
	private final java.util.Map<L1Character, Integer> attackCountMap = new java.util.concurrent.ConcurrentHashMap<>();
	private L1PcInstance owner;
	private long lastSearchTime = 0;

	// ?Å¥?ûò?ä§ ?ÉÅ?ã®?óê Ï∂îÍ??
	private long lastCleanupTime = System.currentTimeMillis();

	private void cleanupAttackCountMap() {
		long now = System.currentTimeMillis();
		// 1Î∂ÑÎßà?ã§ ?†ïÎ¶?
		if (now - lastCleanupTime > 60000) {
			attackCountMap.entrySet()
					.removeIf(entry -> entry.getKey().isDead() || entry.getKey().getMapId() != owner.getMapId());
			lastCleanupTime = now;
		}
	}

	private void getSource(L1PcInstance pc) {
		if (owner == null) {
			removeAuto("?ûê?èô ?Ç¨?É•?ùÑ Ï¢ÖÎ£å ?ï©?ãà?ã§.");
			return;
		}

		cleanupAttackCountMap(); // Ï∂îÍ??

		// ?ôú ?ûê?èô?ù¥ Î©àÏ∂î?äîÏß? Ï≤¥ÌÅ¨?ï¥Î≥¥Í∏∞
		// System.out.println("========================================");
		// System.out.println("AutoStatus: " + owner.getAutoStatus());
		// System.out.println("AutoTarget: " + (owner.getAutoTarget() != null ?
		// owner.getAutoTarget().getName() : "null"));
		// System.out.println("TargetList: " +
		// owner.getAutoTargetList().toTargetArrayList().size());
		// System.out.println("========================================");

		int percent = (int) Math.round(((double) owner.getCurrentHp() / (double) owner.getMaxHp()) * 100);
		if (percent < owner.get_?ûê?èôÍ∑??ôò?çº?Ñº?ä∏()) {
			removeAuto("HPÎ∂?Ï°±ÏúºÎ°? ?ûê?èô ?Ç¨?É•?ùÑ Ï¢ÖÎ£å ?ï©?ãà?ã§.");
			AutoHuntItemUse itemuse = new AutoHuntItemUse(owner);
			itemuse.toUseScroll(46175);
			return;
		}

		if (owner != null && owner.isElf()) {
			if (owner.getWeapon() != null && owner.getWeapon().getItem() != null
					&& owner.getWeapon().getItem().getType1() == 20) {
				if (owner.getInventory().getArrow() == null) {
					AutoHuntItemUse itemuse = new AutoHuntItemUse(owner);
					itemuse.toUseScroll(46175);
					removeAuto("?ôî?Ç¥?ù¥ ?ñ®?ñ¥?†∏?Ñú ?ûê?èô ?Ç¨?É•?ùÑ Ï¢ÖÎ£å ?ï©?ãà?ã§.");
					return;
				}
			}
		}

		if (!owner.getInventory().checkItem(40100)) {
			removeAuto("?àúÍ∞ÑÏù¥?èô Ï£ºÎ¨∏?ÑúÍ∞? Î∂?Ï°±Ìïò?ó¨ ?ûê?èô?Ç¨?É•?ùÑ Ï¢ÖÎ£å ?ï©?ãà?ã§.");
			AutoHuntItemUse itemuse = new AutoHuntItemUse(owner);
			itemuse.toUseScroll(46175);
			return;
		}

		if (owner.isNonAction(owner)) {
			return;
		}
		
		if (owner.getAutoDropTime() != 0) {
			if (System.currentTimeMillis() < owner.getAutoDropTime() + 1000) {
				return;
			} else {
				owner.setAutoDropTime(0);
			}
		}

		if ((owner.getAutoStatus() != AUTO_STATUS_WALK && owner.getAutoStatus() != AUTO_STATUS_ATTACK
				&& owner.getAutoStatus() != AUTO_STATUS_PICKUP_ITEM)) {
			owner.setAutoStatus(AUTO_STATUS_WALK);
		}

		if (owner.isDead()) {
			removeAuto("Ï∫êÎ¶≠?Ñ∞Í∞? ?Ç¨ÎßùÌïò?ó¨ ?ûê?èô?Ç¨?É•?ùÑ Ï¢ÖÎ£å ?ï©?ãà?ã§.");
			return;

		}

		else {
			AutoHuntItemUse itemuse = new AutoHuntItemUse(owner);
			itemuse.toUseItem();
			itemuse.toPolyScroll();

			AutoHuntSkillUse skilluse = new AutoHuntSkillUse(owner);
			skilluse.toUseSkills();
			skilluse.toUseHealingMagic();
		}
		//System.out.println("status = "+ owner.getAutoStatus());
		switch (owner.getAutoStatus()) {
		case AUTO_STATUS_WALK:
			// System.out.println(">>> WALK Î™®Îìú ÏßÑÏûÖ"); //?Öî????äîÍ∞?? Ï≤¥ÌÅ¨Ï§?
			searchTarget();
			if (owner.getAutoTarget() == null) {
				toRandomWalk(pc); // Î∞îÎ°ú?Öî???Î©? Î∂àÌïÑ?öî?ï®! WALKÎ™®Îìú ÏßÑÏûÖ?ãú Î∞îÎ°ú ?Öî???Í∏∞ÎäîÍ±? ?ôï?ù∏
			}
			if (pc.getAutoTell()) {
				if (pc.getMap().isTeleportable()) {
					noTargetTeleport(pc);
				} else {
					if (pc.getMapId() == 101 && pc.getInventory().checkItem(5370617)) {
						noTargetTeleport(pc);
					} else if (pc.getMapId() == 102 && pc.getInventory().checkItem(5370618)) {
						noTargetTeleport(pc);
					} else if (pc.getMapId() == 103 && pc.getInventory().checkItem(5370619)) {
						noTargetTeleport(pc);
					} else if (pc.getMapId() == 104 && pc.getInventory().checkItem(5370620)) {
						noTargetTeleport(pc);
					} else if (pc.getMapId() == 105 && pc.getInventory().checkItem(5370621)) {
						noTargetTeleport(pc);
					} else if (pc.getMapId() == 106 && pc.getInventory().checkItem(5370622)) {
						noTargetTeleport(pc);
					} else if (pc.getMapId() == 107 && pc.getInventory().checkItem(5370623)) {
						noTargetTeleport(pc);
					} else if (pc.getMapId() == 108 && pc.getInventory().checkItem(5370624)) {
						noTargetTeleport(pc);
					} else if (pc.getMapId() == 109 && pc.getInventory().checkItem(5370625)) {
						noTargetTeleport(pc);
					} else if (pc.getMapId() == 110 && pc.getInventory().checkItem(5370626)) {
						noTargetTeleport(pc);
					} else if (pc.getMapId() >= 15410 && pc.getMapId() <= 15440 && pc.getInventory().checkItem(900111)) {
						noTargetTeleport(pc);
					}
				}
			}
			if (owner.getAutoTargetList().toTargetArrayList().size() > 0) {
				owner.setAutoStatus(AUTO_STATUS_ATTACK);
			}
			break;
		case AUTO_STATUS_ATTACK:
			if (owner.getAutoTargetList().toTargetArrayList().size() == 0) {
				owner.getAutoTargetList().clear();
				owner.setAutoTarget(null);
				owner.setAutoStatus(AUTO_STATUS_WALK);
			}
			toAttackMonster(pc);
			break;
		default:
			break;
		}
	}

	// ?Å¥?ûò?ä§ ?ÉÅ?ã®
	private int lastStuckTargetId = 0; // ?Üê Í∞ùÏ≤¥Í∞? ?ïÑ?ãå ID!
	private long stuckStartTime = 0;

	private void toAttackMonster(L1PcInstance pc) {
		try {
			L1Character target = owner.getAutoTarget();
			if (target == null) {
	            lastStuckTargetId = 0;
	            stuckStartTime = 0;
	            owner.setAutoStatus(AUTO_STATUS_WALK);
	            return;
	        }
			if (!owner.glanceCheck(target.getX(), target.getY())) {
				owner.getAutoTargetList().clear();
				owner.setAutoTarget(null);
				owner.setAutoStatus(AUTO_STATUS_WALK);
				return;
			}
			
			// ?òÖ IDÎ°? ÎπÑÍµê!
			if (target != null) {
				int currentTargetId = target.getId(); // Î™¨Ïä§?Ñ∞ Í≥†Ïú† ID

				if (currentTargetId == lastStuckTargetId && lastStuckTargetId != 0) {
					if (stuckStartTime == 0) {
						stuckStartTime = System.currentTimeMillis();
						// System.out.println("DEBUG: ????ù¥Î®? ?ãú?ûë! ID=" + currentTargetId);
					} else {
						long elapsed = System.currentTimeMillis() - stuckStartTime;
						// System.out.println("DEBUG: Í≤ΩÍ≥º ?ãúÍ∞?=" + elapsed + "ms");

						if (elapsed > 10000) {
							owner.removeAutoTargetList(target);
							owner.setAutoTarget(null);
							attackCountMap.remove(target);
							noTargetTeleport(owner);
							lastStuckTargetId = 0;
							stuckStartTime = 0;
							System.out.println("???Í≤? ?ú†Ïß?: 10 Í≤ΩÍ≥º! ?Öî?†à?è¨?ä∏!");
							return;
						}
						//System.out.println("target="+elapsed);
					}
				} else {
					// System.out.println("DEBUG: ???Í≤? Î≥?Í≤?! " + lastStuckTargetId + " ?Üí " +
					// currentTargetId);
					lastStuckTargetId = currentTargetId;
					stuckStartTime = 0;
				}
			} else {
				lastStuckTargetId = 0;
				stuckStartTime = 0;
			}

			if (target != null && target.isDead()) {
				owner.removeAutoTargetList(target);
				owner.setAutoTarget(null);
				attackCountMap.remove(target); // Ï∂îÍ??!

				searchTarget();
				return;
			}

			if (owner.getAutoTarget() == null) {
				searchTarget();

				// ???Í≤? Î™? Ï∞æÏúºÎ©? WALK
				if (owner.getAutoTarget() == null) {
					owner.setAutoStatus(AUTO_STATUS_WALK);
				}
				return;
			}

			if (!isAttack(target)) {
				owner.removeAutoTargetList(target);
				owner.setAutoTarget(null);
				// lastStuckTarget = null; // Î≤ΩÎí§ ?ù∏?ãù Î¶¨ÏÖã
				// stuckStartTime = 0; // Î≤ΩÎí§ ?ù∏?ãù Î¶¨ÏÖã
				searchTarget(); // Ï∂îÍ??
				return; // Ï∂îÍ??
			}

			if (!isAutoAttackTime()) {
				return;
			}

			if (owner.getAutoTarget() == null) {
				owner.setAutoStatus(AUTO_STATUS_WALK);
				searchTarget(); // Ï∂îÍ??
				return;
			}

			L1Character newTarget = getTarget(); // Î≥??àò?óê Î®ºÏ?? ????û• Ï∂îÍ??
			if (newTarget != null && target != null) { // Ï∂îÍ??
				if (owner.getLocation().getTileLineDistance(newTarget.getLocation()) < owner.getLocation()
						.getTileLineDistance(target.getLocation())) {
					owner.removeAutoTargetList(target);
					owner.setAutoTarget(newTarget);
					target = newTarget;
				}
			}

			// checkTargetHpWithTimeout(owner, target); // ?ó¨Í∏∞Í?? ?õê?úÑÏπ? (?Ç¨Í±∞Î¶¨ Î∞ëÏúºÎ°? ?Ç¥?†∏?ùå)

			if (pc.isElf()) {
				if (pc.getWeapon().getItem().getType1() == 20) {
					pc.setAttackRang(8);
				} else {
					pc.setAttackRang(1);
				}
			} else if (pc.isWizard()) { // ?ö©Í∏∞ÏÇ¨ ÎßàÎ≤ï?Ç¨ ?ôò?à†?Ç¨ ?ëêÏπ? Í≥µÍ≤©
				pc.setAttackRang(2);
			} else {
				pc.setAttackRang(1);
			}

			// checkTargetHpWithTimeout(owner, target); // Ï£ºÏÑù?ùÑ ?ïò?Çò ?ïà?ïò?Çò Î©àÏ∂î?äîÍ±? ÎßàÏ∞¨Í∞?Ïß? Î∂àÌïÑ?öî ?ïò?ó¨ Ï£ºÏÑù

			if (isDistance(owner.getX(), owner.getY(), owner.getMapId(), target.getX(), target.getY(),
					target.getMapId(), owner.getAttackRang())) {
				if (owner.glanceCheck(target.getX(), target.getY())) {
					toAttack();
					moveDelayCount = 0; // ?ñ¥?Éù?Ñ±Í≥µÏãú Î¶¨ÏÖã Î∂àÌïÑ?öî?ïú ?Öî?ïòÏß??ïä?èÑÎ°? Ï∂îÍ??
				} else {
					toMoving(target.getX(), target.getY(), 0, true);
					moveDelayCount++;
					if (moveDelayCount >= 10) {
						AutoHuntItemUse autoitem = new AutoHuntItemUse(owner);
						autoitem.toUseScroll(40100);
						moveDelayCount = 0;
					}
				}
			} else {
				if (owner.getAutoAiTime() == 0) {
					owner.setAutoAiTime(System.currentTimeMillis());
				} else {
					if (System.currentTimeMillis() >= owner.getAutoAiTime() + 10000) {
						owner.setAutoAiTime(0);
						owner.removeAutoTargetList(target);
						owner.setAutoTarget(null);
						return; // Ï∂îÍ??
					}
				}
				toMoving(target.getX(), target.getY(), 0, true);
				moveDelayCount++;
				if (moveDelayCount >= 10) {
					owner.toCharacterRefresh();
					moveDelayCount = 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); // ?óê?ü¨ Î°úÍ∑∏ Ï∂úÎ†•
			owner.removeAutoTargetList(owner.getAutoTarget());
			owner.setAutoTarget(null);
			searchTarget(); // ?óê?ü¨ Î∞úÏÉù?ï¥?èÑ ???Í≤? ?û¨Í≤??Éâ Ï∂îÍ??
		}
	}

	private boolean isAttack(L1Character cha) {
		try {
			if (cha == null) {
				return false;
			}

			if (cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
				return false;
			}

			if (cha.getMap().isSafetyZone(cha.getLocation())) {
				// ?äπ?†ï ÎßµÏóê?Ñú?äî ?ïà?†ÑÏß???? Ï≤¥ÌÅ¨ Î¨¥Ïãú
				int mapId = cha.getMapId();
				if (mapId != 813 && mapId != 2 && mapId != 1700 && mapId != 785) {
					return false;
				}
			}

			if (cha.isDead())
				return false;

			if (cha.isInvisble())
				return false;
			// HP 10Îß? ?ù¥?ÉÅ Î™¨Ïä§?Ñ∞ Í≥µÍ≤©?ïòÏß? ?ïä?ùå
			if (cha.getMaxHp() >= 100000) {
				return false;
			}

			if (!isDistance(owner.getX(), owner.getY(), owner.getMapId(), cha.getX(), cha.getY(), cha.getMapId(), 12))
				return false;

			if (!owner.glanceCheck(cha.getX(), cha.getY()))
				return false;

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void toAttack() {
		try {
			L1Character target = owner.getAutoTarget();
			if (target == null) {
				owner.getAutoTargetList().clear();
				owner.setAutoStatus(AUTO_STATUS_WALK);
				return;
			}

			attackCountMap.put(target, attackCountMap.getOrDefault(target, 0) + 1);

			if (attackCountMap.get(target) >= 30) {
				attackCountMap.remove(target); // Ïπ¥Ïö¥?ä∏ Ï¥àÍ∏∞?ôî
				owner.removeAutoTargetList(target);
				owner.setAutoTarget(null);
				AutoHuntItemUse itemuse = new AutoHuntItemUse(owner);
				itemuse.toUseScroll(40100);
				return;
			}

			if (owner.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MEDITATION)) {
				owner.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.MEDITATION);
			}

			owner.delInvis();
			if (owner.isElf() && owner.getWeapon().getItem().getType1() == 20) {
				int chance = _rnd.nextInt(100) + 1;
				if (chance <= Config.?ûê?èô?Ç¨?É•?ä∏Î¶¨ÌîåÎ∞úÎèô?ôïÎ•?) {
					AutoHuntSkillUse skilluse = new AutoHuntSkillUse(owner);
					skilluse.toTripleArrow(target);
				} else {
					target.onAction(owner);
				}
			} else {
				AutoHuntSkillUse skilluse = new AutoHuntSkillUse(owner);
				int skillchance = _rnd.nextInt(100) + 1;
				List<String> activeSkills = new ArrayList<>();
				if (owner.getCurrentMpPercent() > owner.get_?ûê?èôÍ∑??ôò?çº?Ñº?ä∏()) {
					if (owner.getAutoskill1()) {
						activeSkills.add("?ïÑ?ù¥?ä§?ä§?åå?ù¥?Å¨");
					}
					if (owner.getAutoskill2()) {
						activeSkills.add("ÎØ∏Ìã∞?ñ¥");
					}
					if (!activeSkills.isEmpty()) {
						Random random = new Random();
						int randomIndex = random.nextInt(activeSkills.size());
						String skillToUse = activeSkills.get(randomIndex);
						if (skillchance <= Config.?ûê?èô?Ç¨?É•Î≤ïÏÇ¨Í≥µÍ≤©?ä§?Ç¨Î∞úÎèô?ôïÎ•?) {
							switch (skillToUse) {
							case "?ïÑ?ù¥?ä§?ä§?åå?ù¥?Å¨":
								skilluse.toIceSpike(owner, target);
								break;
							case "ÎØ∏Ìã∞?ñ¥":
								skilluse.toMeteorStrike(owner, target);
								break;
							}
						}
					}
				}
				target.onAction(owner);
			}
		} catch (Exception e) {
			owner.setAutoTarget(null);
			owner.getAutoTargetList().clear();
		}
	}

	private L1Character getTarget() {
		L1Character realTarget = null;
		try {
			for (int i = 0; i < owner.getAutoTargetList().toTargetArrayList().size(); i++) {
				L1Character target = owner.getAutoTargetList().toTargetArrayList().get(i);
				if (target.isDead()) {
					owner.removeAutoTargetList(target);
					attackCountMap.remove(target); // Ï∂îÍ??
					i--; // ?Üê Î∞òÎ≥µÎ¨∏Ïóê?Ñú Î¶¨Ïä§?ä∏Î•? Ïß??ö∏ ?ïå ?ù∏?ç±?ä§ Î¨∏Ï†ú Î∞©Ï??
					owner.setAutoTarget(null);
					continue;
				}
				if (!owner.glanceCheck(target.getX(), target.getY())) {
					owner.removeAutoTargetList(target);
					i--; // ?Üê Î∞òÎ≥µÎ¨∏Ïóê?Ñú Î¶¨Ïä§?ä∏Î•? Ïß??ö∏ ?ïå ?ù∏?ç±?ä§ Î¨∏Ï†ú Î∞©Ï??
					owner.setAutoTarget(null);
					continue;
				}

				if (realTarget == null) {
					realTarget = target;
				} else if (!target.isDead()
						&& getDistance(owner.getX(), owner.getY(), target.getX(), target.getY()) < getDistance(
								owner.getX(), owner.getY(), realTarget.getX(), realTarget.getY())) {
					realTarget = target;
				}
			}
			// Î¶¨Ïä§?ä∏?óê?Ñú Î™? Ï∞æÏïò?úºÎ©? searchTarget() ?ò∏Ï∂? Ï∂îÍ??
			if (realTarget == null) {
				searchTarget();
				if (owner.getAutoTarget() != null) {
					realTarget = owner.getAutoTarget();
				}
			}
			return realTarget;
		} catch (Exception e) {
			e.printStackTrace();
			owner.getAutoTargetList().clear();
			owner.setAutoTarget(null);
			return realTarget;
		}
	}

	private boolean isDistance(int x, int y, int m, int tx, int ty, int tm, int loc) {
		int distance = getDistance(x, y, tx, ty);
		if (loc < distance)
			return false;
		if (m != tm)
			return false;
		return true;
	}

	private int getDistance(int x, int y, int tx, int ty) {
		long dx = tx - x;
		long dy = ty - y;
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	private void searchTarget() {
		 long now = System.currentTimeMillis();
		if (now - lastSearchTime < 1000) {
			return;
		}
		lastSearchTime = now;
		if (owner.getWeapon() != null && owner.getWeapon().getItem().getType1() == 20) {
			AutoHuntSkillUse skilluse = new AutoHuntSkillUse(owner);
			skilluse.toBloodSoul(owner);
		}
		checkTarget();
		for (L1Object obj : L1World.getInstance().getVisibleObjects(owner)) {
			if (obj == null) {
				continue;
			}
			if (obj instanceof L1MonsterInstance) {
				L1MonsterInstance mon = (L1MonsterInstance) obj;
				if (mon.isDead()) {
					continue;
				}
				if (mon.getHiddenStatus() >= 1) {
					continue;
				}
				// HP 10Îß? ?ù¥?ÉÅ Î™¨Ïä§?Ñ∞ Í±¥ÎÑà?õ∞Í∏?
				if (mon.getMaxHp() >= 100000) {
					continue;
				}

				if (!owner.glanceCheck(mon.getX(), mon.getY())) {
					continue;
				}
				owner.addAutoTargetList(mon);

				if (owner.getAutoTarget() == null) {
					owner.setAutoTarget(mon);
				}
			}
		}
	}

	private void checkTarget() {
		try {
			L1Character target = owner.getAutoTarget();
			if (target == null || target.getMapId() != owner.getMapId() || target.isDead() || target.getCurrentHp() <= 0
					|| (target.isInvisble() && !owner.getAutoTargetList().containsKey(target))
					|| target.getMaxHp() >= 100000) { // HP 10Îß? ?ù¥?ÉÅ Î™¨Ïä§?Ñ∞ Í±¥ÎÑà?õ∞Í∏?
				if (target != null) {
					tagertClear();
				}

				if (!owner.getAutoTargetList().isEmpty()) {
					owner.setAutoTarget(owner.getAutoTargetList().getMaxHateCharacter());
					checkTarget();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void tagertClear() {
		L1Character target = owner.getAutoTarget();
		if (target == null) {
			return;
		}
		owner.getAutoTargetList().remove(target);
		owner.setAutoTarget(null);
		attackCountMap.remove(target); // Ïπ¥Ïö¥?ä∏ Ï¥àÍ∏∞?ôî
	}

	private void toRandomWalk(L1PcInstance pc) {
		if (!isAutoMoveTime()) {
			return;
		}

		if (owner.getAutoMoveCount() == 0) {
			int randomLocX = (int) ((Math.random() * 10) - 5);
			int randomLocY = (int) ((Math.random() * 10) - 5);
			int _locX = owner.getX() + randomLocX;
			int _locY = owner.getY() + randomLocY;
			owner.setAutoLocX(_locX);
			owner.setAutoLocY(_locY);
		}
		if (pc.getWeapon() != null && pc.getWeapon().getItem().getType1() == 20) {
			AutoHuntSkillUse skilluse = new AutoHuntSkillUse(owner);
			skilluse.toBloodSoul(pc);
		}
		int dir = owner.targetDirection(owner.getAutoLocX(), owner.getAutoLocY());
		toMoving(owner.getAutoLocX(), owner.getAutoLocY(), dir, true);
	}

	private void toMoving(int x, int y, int h, boolean astar) {
		try {
			if (astar) {
				owner.getAutoAstar().ResetPath();
				owner.setAutoTail(owner.getAutoAstar().FindPath(owner, x, y, owner.getMapId(), null));
				// Ï∂îÍ??
				if (owner.getAutoTail() == null) {
					owner.setAutoMoveCount(0);
					return;
				}
				// Ï∂îÍ??
				if (owner.getAutoTail() != null) {
					owner._autoCurrentPath = -1;
					owner.getAutoPath().clear();
					while (owner.getAutoTail() != null) {
						if (owner.getAutoTail().x == owner.getX() && owner.getAutoTail().y == owner.getY()) {
							break;
						}
						owner.getAutoPath().add(new int[] { owner.getAutoTail().x, owner.getAutoTail().y });
						owner.setAutoTail(owner.getAutoTail().prev);
					}
					if (owner.getAutoPath().isEmpty()) {
						owner.setAutoMoveCount(0);
						return;
					}
					owner._autoCurrentPath = owner.getAutoPath().size() - 1;
					int[] step = owner.getAutoPath().get(owner._autoCurrentPath);
					toMoving(step[0], step[1], calcheading(owner.getX(), owner.getY(), step[0], step[1]));
				}
			} else {
				toMoving(x, y, h);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void toMoving(final int x, final int y, final int h) {
		try {

			if (owner.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_FREEZE)) {
				return;
			}
			owner.getMap().setPassable(owner.getLocation(), true);
			owner.getLocation().set(x, y);
			owner.getMoveState().setHeading(h);
			L1WorldTraps.getInstance().onPlayerMoved(owner);
			owner.getMap().setPassable(owner.getLocation(), false);
			owner.sendPackets(new S_MoveCharPacket(owner));
			owner.broadcastPacket(new S_MoveCharPacket(owner));
			owner.setAutoMoveCount(owner.getAutoMoveCount() + 1);
			if (owner.getAutoMoveCount() >= 8) { // ?ûê?èô ?ôîÎ©? Î≥?Í≤ΩÌïòÍ∏? Ïπ¥Ïãú?ãò Í∏∞Î≥∏ 8
				owner.setAutoMoveCount(0);
				owner.toCharacterRefresh();
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	private int calcheading(int myx, int myy, int tx, int ty) {
		if (tx > myx && ty > myy) {
			return 3;
		} else if (tx < myx && ty < myy) {
			return 7;
		} else if (tx > myx && ty == myy) {
			return 2;
		} else if (tx < myx && ty == myy) {
			return 6;
		} else if (tx == myx && ty < myy) {
			return 0;
		} else if (tx == myx && ty > myy) {
			return 4;
		} else if (tx < myx && ty > myy) {
			return 5;
		} else {
			return 1;
		}
	}

	private boolean isAutoAttackTime() {
		long temp = System.currentTimeMillis() - owner.getAutoTimeAttack();
  
		if (owner.isNonAction(owner))
			return false;
		
		
		long interval = SprTable.getInstance().getAttackSpeed(owner.getGfxId().getTempCharGfx(), owner.getCurrentWeapon() + 1);
		if (owner.isHaste()) {
			interval *= 0.745;
		}
		
		if (owner.isBrave()) {
			interval *= 0.745;
		}		
		if (owner.isElfBrave()) {
			interval *= 0.874;
		}
		
		if (owner.isDragonPearl()) {
			interval *= 0.87;
		}
		
		interval *= Config.ATTACK_SPEED_VALUE;
		
		if (temp < interval) {
			return false;
		}
		if (temp >= interval) {
			owner.setAutoTimeAttack(System.currentTimeMillis());
			return true;
		}
		return false;
	}

	private boolean isAutoMoveTime() {
		long temp = System.currentTimeMillis() - owner.getAutoTimeMove();
		if (owner.isNonAction(owner))
			return false;
		long interval = SprTable.getInstance().getMoveSpeed(owner.getGfxId().getTempCharGfx(), owner.getCurrentWeapon());
		if (owner.isHaste()) {
			interval *= 0.745;
		}
		
		if (owner.isBrave()) {
			interval *= 0.745;
		}		
		if (owner.isElfBrave()) {
			interval *= 0.874;
		}
		
		if (owner.isDragonPearl()) {
			interval *= 0.87;
		}
		
		interval *= Config.MOVE_SPEED_VALUE;
		if (temp < interval) {
			return false;
		}
		if (temp >= interval) {
			owner.setAutoTimeMove(System.currentTimeMillis());
			return true;
		}
		return false;
	}

	protected L1ItemInstance _targetItem = null;
	protected List<L1ItemInstance> _targetItemList = new ArrayList<L1ItemInstance>();

	public void removeAuto(String ment) {
		owner.toCharacterRefresh();
		if (owner != null) {
			owner.resetAuto();
			owner.sendPackets(new S_SystemMessage(String.format("%s", ment)));
		}
		// Ï¢ÖÎ£å?ãú ?†ïÎ¶? Ï∂îÍ??
		attackCountMap.clear();
		owner.EndAutoController();
	}

	private void noTargetTeleport(L1PcInstance pc) {
		AutoHuntItemUse itemuse = new AutoHuntItemUse(owner);
		if (pc.getAutoAiTime() == 0) {
			pc.setAutoAiTime(System.currentTimeMillis());
		} else {
			if (pc.getAutoTargetList().toTargetArrayList().size() == 0
					&& System.currentTimeMillis() >= pc.getAutoAiTime() + 3000) {
				itemuse.toUseScroll(40100);
				pc.setAutoAiTime(System.currentTimeMillis());
			}
		}
	}

	public void checkItemCountWithTimeout(L1PcInstance pc, L1Character cha) {
		int arrowid = owner.getInventory().getArrow().getItemId();
		int initialCount = pc.getInventory().checkItemCount(arrowid);
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				int newCount = pc.getInventory().checkItemCount(arrowid);
				if (newCount == initialCount) {
					pc.removeAutoTargetList(cha);
					pc.setAutoTarget(null);
					searchTarget();
				}
				scheduler.shutdown();
			}
		}, 3, TimeUnit.SECONDS);
	}

	private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public void checkTargetHpWithTimeout(L1PcInstance pc, L1Character cha) {
		if (pc == null || cha == null || cha.isDead()) {
			return;
		}
		final int initialHp = cha.getCurrentHp();
		scheduler.schedule(() -> {
			try {
				if (pc.isDead() || cha.isDead()) {
					return;
				}
				if (pc == null || cha == null) {
					return;
				}

				int currentHp = cha.getCurrentHp();
				if (currentHp == initialHp) {
					pc.removeAutoTargetList(cha);
					pc.setAutoTarget(null);
					if (!pc.isDead()) {
						searchTarget();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 3, TimeUnit.SECONDS);
	}

}
