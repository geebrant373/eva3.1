package l1j.server.AutoHuntSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1GroundInventory;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.monitor.L1PcMonitor;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_AttackPacket;
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
	private int toggleCount = 0;
	private L1PcInstance owner;
	private L1Character lastTarget = null;
	private long lastTargetChangeTime = 0;
	private long lastAttackAttemptTime = 0;
	
	private void getSource(L1PcInstance pc) {
		if (owner == null) {
			removeAuto("자동 사냥을 종료 합니다.");
			return;
		}
		
		int percent = (int) Math.round(((double) owner.getCurrentHp() / (double) owner.getMaxHp()) * 100);
		if (percent < owner.get_자동귀환퍼센트()) {
			removeAuto("HP부족으로 자동 사냥을 종료 합니다.");
			AutoHuntItemUse itemuse = new AutoHuntItemUse(owner);
			itemuse.toUseScroll(46175);
			return;
		}

		if (owner.isElf() && owner.getWeapon().getItem().getType1() == 20) {
			if (owner.getInventory().getArrow() == null) {
				AutoHuntItemUse itemuse = new AutoHuntItemUse(owner);
				itemuse.toUseScroll(46175);
				removeAuto("화살이 떨어져서 자동 사냥을 종료 합니다.");
				return;	
			}
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
			removeAuto("캐릭터가 사망하여 자동사냥을 종료 합니다.");
			return;
		} else {
			AutoHuntItemUse itemuse = new AutoHuntItemUse(owner);
			itemuse.toUseItem();
			itemuse.toPolyScroll();

			AutoHuntSkillUse skilluse = new AutoHuntSkillUse(owner);
			skilluse.toUseSkills();
			skilluse.toUseHealingMagic();
			
			if (owner.getAutoPickup()) {
				toPickupItem();
			}
		}
		
		switch (owner.getAutoStatus()) {
		case AUTO_STATUS_WALK:
			searchTarget();
			if (owner.getAutoTarget() == null) {
				toRandomWalk(pc);
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
		case AUTO_STATUS_PICKUP_ITEM:
			PickUpItem(pc);
			break;
		default:
			break;
		}
	}

	private void toAttackMonster(L1PcInstance pc) {
		try {
			if (!isAutoAttackTime()) {
				return;
			}
			
			if (lastAttackAttemptTime != 0) {
			    if (System.currentTimeMillis() - lastAttackAttemptTime > 10000) {
			        AutoHuntItemUse autoitem = new AutoHuntItemUse(owner);
			        autoitem.toUseScroll(40100);

			        owner.removeAutoTargetList(owner.getAutoTarget());
			        owner.setAutoTarget(null);

			        lastAttackAttemptTime = System.currentTimeMillis();
			        return;
			    }
			}
			
			L1Character target = owner.getAutoTarget();

			if (target != null && target.isDead()) {
			    removeTargetWithDelay(target, 500); // 0.5초
			    return;
			}

			if (owner.getAutoTarget() == null) {
				owner.setAutoTarget(getTarget());
			}

			if (!isAttack(target)) {
				owner.removeAutoTargetList(target);
				owner.setAutoTarget(null);
			}

			if (owner.getAutoTarget() == null) {
				owner.setAutoStatus(AUTO_STATUS_WALK);
				return;
			}

			if (owner.getLocation().getTileLineDistance(getTarget().getLocation()) < owner.getLocation()
					.getTileLineDistance(target.getLocation())) {
				owner.removeAutoTargetList(target);
				owner.setAutoTarget(getTarget());
			}
			
	        // 타겟 변경 감지 및 10초 체크
	        if (lastTarget != target) {
	            lastTarget = target;
	            lastTargetChangeTime = System.currentTimeMillis();
	        } else {
	            // 같은 타겟이 10초 이상 지속되면 텔레포트
	            if (System.currentTimeMillis() - lastTargetChangeTime >= 20000) {
	                AutoHuntItemUse autoitem = new AutoHuntItemUse(owner);
	                autoitem.toUseScroll(40100);
	                lastTargetChangeTime = System.currentTimeMillis(); // 타이머 리셋
	                return;
	            }
	        }
	        
			if (pc.isElf()) {
				if (pc.getWeapon().getItem().getType1() == 20) {
					pc.setAttackRang(12);	
				} else {
					pc.setAttackRang(1);	
				}
			} else {
				if (pc.getAutoTurnUndead()) {
					pc.setAttackRang(4);
				} else {
					pc.setAttackRang(1);
				}
			}
			if (isDistance(owner.getX(), owner.getY(), owner.getMapId(), target.getX(), target.getY(), target.getMapId(), owner.getAttackRang())) {
				if (owner.getAutoTurnUndead() && owner.getCurrentMpPercent() > owner.get_자동마나퍼센트()) {
					AutoHuntSkillUse skilluse = new AutoHuntSkillUse(owner);
					skilluse.toTurnUndead(owner);
				} else {
					if (owner.glanceCheck(target.getX(), target.getY())) {
						toAttack();
					} else {
						toMoving(target.getX(), target.getY(), 0, true);
						moveDelayCount++;
						if (moveDelayCount >= 30) {
							AutoHuntItemUse autoitem = new AutoHuntItemUse(owner);
							autoitem.toUseScroll(40100);
							moveDelayCount = 0;
						}
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
					}
				}
				toMoving(target.getX(), target.getY(), 0, true);
				moveDelayCount++;
				if (moveDelayCount >= 30) {
					owner.toCharacterRefresh();
					moveDelayCount = 0;
				}
			}
		} catch (Exception e) {
			owner.removeAutoTargetList(owner.getAutoTarget());
			owner.setAutoTarget(null);
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
			if (cha.getMap().isSafetyZone(cha.getLocation()))
				return false;

			if (cha.isDead())
				return false;

			if (cha.isInvisble())
				return false;

			if (!isDistance(owner.getX(), owner.getY(), owner.getMapId(), cha.getX(), cha.getY(), cha.getMapId(), 16))
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

			lastAttackAttemptTime = System.currentTimeMillis();
			 
			if (!isValidAttackTarget(target)) {
		        owner.removeAutoTargetList(target);
		        owner.setAutoTarget(null);
		        owner.setAutoStatus(AUTO_STATUS_WALK);
		        return;
		    }
			
			if (owner.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MEDITATION)) {
				owner.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.MEDITATION);
			}
			
			owner.delInvis();
			if (owner.isElf() && owner.getWeapon().getItem().getType1() == 20) {
				int chance = _rnd.nextInt(100) + 1;
				if (owner.getAutoTripple() && chance <= Config.자동사냥트리플발동확률) {
					AutoHuntSkillUse skilluse = new AutoHuntSkillUse(owner);
					skilluse.toTripleArrow(target);
				} else {
					target.onAction(owner);
				}
			} else {
				AutoHuntSkillUse skilluse = new AutoHuntSkillUse(owner);
				int skillchance = _rnd.nextInt(100) + 1;
				List<String> activeSkills = new ArrayList<>();
				if (owner.getCurrentMpPercent() > owner.get_자동마나퍼센트()) {
					if (owner.getAutoskill1()) {
						activeSkills.add("선버스트");
					}
					if (owner.getAutoskill2()) {
						activeSkills.add("파이어스톰");
					}
					if (owner.getAutoskill3()) {
						activeSkills.add("아이스스파이크");
					}
					if (owner.getAutoskill4()) {
						activeSkills.add("미티어");
					}
					if (owner.getAutoskill5()) {
						activeSkills.add("칠터치");
					}
					if (owner.getAutoskill6()) {
						activeSkills.add("마나드레인");
					}
					if (!activeSkills.isEmpty()) {
						Random random = new Random();
						int randomIndex = random.nextInt(activeSkills.size());
						String skillToUse = activeSkills.get(randomIndex);
						if (skillchance <= Config.자동사냥법사공격스킬발동확률) {
							switch (skillToUse) {
							case "선버스트":
								skilluse.autoAttackSkill(owner, target, L1SkillId.SUNBURST);
								break;
							case "파이어스톰":
								skilluse.autoAttackSkill(owner, target, L1SkillId.FIRE_STORM);
								break;
							case "아이스스파이크":
								skilluse.autoAttackSkill(owner, target, L1SkillId.FREEZING_BLIZZARD);
								break;
							case "미티어":
								skilluse.autoAttackSkill(owner, target, L1SkillId.METEOR_STRIKE);
								break;
							case "칠터치":
								if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CHILL_TOUCH)) {
									return;
								}
								skilluse.autoAttackSkill(owner, target, L1SkillId.CHILL_TOUCH);
								break;
							case "마나드레인":
								if (owner.getCurrentMpPercent() >= 99) {
									return;
								}
								if (target.getCurrentMp() > 10) {
									skilluse.autoAttackSkill(owner, target, L1SkillId.MANA_DRAIN);
								}
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
					owner.setAutoTarget(null);
					continue;
				}
				if (!owner.glanceCheck(target.getX(), target.getY())) {
					owner.removeAutoTargetList(target);
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
				if (owner.getAutoAstar().FindPath(owner, mon.getX(), mon.getY(), owner.getMapId(), null) == null) {
				    continue; // 경로 없으면 타겟 제외
				}
				if (mon.isDead()) {
					continue;
				}
				if (mon.getHiddenStatus() >= 1) {
					continue;
				}

				if (!owner.glanceCheck(mon.getX(), mon.getY())) {
					continue;
				}
				owner.addAutoTargetList(mon);
			}
		}
	}

	private void checkTarget() {
		try {
			L1Character target = owner.getAutoTarget();
			if (target == null || target.getMapId() != owner.getMapId() || target.isDead() || target.getCurrentHp() <= 0
					|| (target.isInvisble() && !owner.getAutoTargetList().containsKey(target))) {
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
	}

	private void toRandomWalk(L1PcInstance pc) {
		if (!isAutoMoveTime()) {
			return;
		}
		
		if(owner.getAutoMoveCount() == 0) {
			int randomLocX = (int) ((Math.random() * 20) - 10);
			int randomLocY = (int) ((Math.random() * 20) - 10);
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
                if (owner.getAutoTail() != null) {
                    owner._autoCurrentPath = -1;
                    owner.getAutoPath().clear();
                    while (owner.getAutoTail() != null) {
                        if (owner.getAutoTail().x == owner.getX() && owner.getAutoTail().y == owner.getY()) {
                            break;
                        }
                        owner.getAutoPath().add(new int[]{owner.getAutoTail().x, owner.getAutoTail().y});
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
			owner.getMap().setPassable(owner.getLocation(), true);
			owner.getLocation().set(x, y);
			owner.getMoveState().setHeading(h);
			L1WorldTraps.getInstance().onPlayerMoved(owner);
			owner.getMap().setPassable(owner.getLocation(), false);
			owner.sendPackets(new S_MoveCharPacket(owner));
			owner.broadcastPacket(new S_MoveCharPacket(owner));
			owner.setAutoMoveCount(owner.getAutoMoveCount() + 1);
			if (owner.getAutoMoveCount() >= 7) {
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

	private void PickUpItem(L1PcInstance pc) {
		if (_targetItem != null) {
			L1Inventory groundInventory = L1World.getInstance().getInventory(_targetItem.getX(), _targetItem.getY(), _targetItem.getMapId());
			if (groundInventory.checkItem(_targetItem.getItemId())) {
				onTargetItem(pc);
			} else {
				_targetItemList.remove(_targetItem);
				_targetItem = null;
				return;
			}
		} else {
			owner.setAutoStatus(0);
		}
	}

	private void onTargetItem(L1PcInstance pc) {
		try {
			if (!isAutoMoveTime())
				return;
			if (owner.getLocation().getTileLineDistance(_targetItem.getLocation()) <= 1) {
				L1Inventory inv = L1World.getInstance().getInventory(_targetItem.getX(), _targetItem.getY(),
						_targetItem.getMapId());
				L1ItemInstance item = inv.getItem(_targetItem.getId());
				if (item != null) {
					inv.tradeItem(owner, item.getId(), item.getCount(), owner.getInventory());
					S_AttackPacket pck = new S_AttackPacket(owner, _targetItem.getId(), ActionCodes.ACTION_Pickup);
					owner.sendPackets(pck);
					owner.broadcastPacket(pck);
					_targetItemList.remove(_targetItem);
					_targetItem = null;
					inv = null;
					owner.setAutoDropTime(System.currentTimeMillis());
				}
			} else {
				if (owner.getAutoAstar().FindPath(owner, _targetItem.getX(), _targetItem.getY(), owner.getMapId(), null) == null) {
					owner.getAutoTargetList().clear();
					owner.setAutoTarget(null);
					owner.setAutoStatus(AUTO_STATUS_WALK);
					noTargetTeleport(pc);
				}
				toMoving(_targetItem.getX(), _targetItem.getY(), 0, true);
				toggleCount++;
				if (toggleCount == 10) {
					owner.getAutoTargetList().clear();
					owner.setAutoTarget(null);
					owner.setAutoStatus(AUTO_STATUS_WALK);
					noTargetTeleport(pc);
					toggleCount = 0;
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	private void toPickupItem() {
		checkTargetItem();
		if (_targetItem == null) {
			searchTargetItem();
		}

		if (_targetItem != null) {
			owner.setAutoStatus(AUTO_STATUS_PICKUP_ITEM);
		}
	}

	private void checkTargetItem() {
		if (_targetItem == null || _targetItem.getMapId() != owner.getMapId()
				|| owner.getLocation().getTileDistance(_targetItem.getLocation()) > 15) {
			if (!_targetItemList.isEmpty()) {
				_targetItem = _targetItemList.get(0);
				_targetItemList.remove(0);
				checkTargetItem();
			} else {
				_targetItem = null;
			}
		}
	}

	private void searchTargetItem() {
		ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();
		for (L1Object obj : L1World.getInstance().getVisibleObjects(owner)) {
			if (obj == null) {
				continue;
			}
			if (owner.getInventory().getSize() >= 178) {
				continue;
			}
			if (obj != null && obj instanceof L1GroundInventory) {
				gInventorys.add((L1GroundInventory) obj);
			}
		}
		if (gInventorys.size() == 0) {
			return;
		}

		int pickupIndex = (int) (Math.random() * gInventorys.size());
		L1GroundInventory inventory = gInventorys.get(pickupIndex);
		for (L1ItemInstance item : inventory.getItems()) {
			if (item == null)
				continue;
			if (owner.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
				_targetItem = item;
				_targetItemList.add(_targetItem);
			}
		}
	}

		public void removeAuto(String ment) {
			owner.toCharacterRefresh();
			if (owner != null) {
				owner.resetAuto();
				owner.sendPackets(new S_SystemMessage(String.format("%s", ment)));
			}
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
	
	private void removeTargetWithDelay(L1Character target, long delayMillis) {
	    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	    scheduler.schedule(() -> {
	        try {
	            // 아직 같은 타겟이고, 정말 죽어있을 때만 제거
	            if (owner != null
	                && owner.getAutoTarget() == target
	                && target.isDead()) {

	                owner.removeAutoTargetList(target);
	                owner.setAutoTarget(null);
	            }
	        } catch (Exception e) {
	            // 무시
	        } finally {
	            scheduler.shutdown();
	        }
	    }, delayMillis, TimeUnit.MILLISECONDS);
	}
	
	private boolean isValidAttackTarget(L1Character target) {
	    if (target == null) return false;
	    if (target.isDead()) return false;
	    if (target.getMapId() != owner.getMapId()) return false;
	    if (!owner.glanceCheck(target.getX(), target.getY())) return false;
	    if (!L1World.getInstance().getVisibleObjects(owner).contains(target)) return false;
	    return true;
	}
}
