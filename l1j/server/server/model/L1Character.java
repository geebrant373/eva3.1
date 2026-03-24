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

package l1j.server.server.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import l1j.server.MJ3SEx.EActionCodes;
import l1j.server.MJ3SEx.SpriteInformation;
import l1j.server.MJ3SEx.Loader.SpriteInformationLoader;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.poison.L1Poison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.utils.IntRange;

//Referenced classes of package l1j.server.server.model:
//L1Object, Die, L1PcInstance, L1MonsterInstance,
//L1World, ActionFailed

public class L1Character extends L1Object {
	private static final long serialVersionUID = 1L;
	
	// 케릭터 기본
	//private BasicProperty basic;
	
	private String _name; 
	private String _title;
	
	private int _level; 
	private int _exp;

	private int _lawful; 
	private int _karma;
	public boolean isantarun = false;
		
	private int _currentHp;
	private int _trueMaxHp;
	private short _maxHp;
	public long skilldelayTime;
	private int _currentMp;
	private int _trueMaxMp;
	private short _maxMp;
	
	private L1Poison _poison = null;
	private boolean _paralyzed;
	private boolean _sleeped;
	private L1Paralysis _paralysis;
	private boolean _isDead; 

	protected GfxId gfx;					// 케릭터 그래픽 ID
	private MoveState moveState;		// 이동속도, 바라보는 방향
	protected Light light;				// 케릭터 주위  빛
	protected Ability ability; 			// 능력치, SP, MagicBonus
	protected Resistance resistance;	// 저항 (마방, 불, 물, 바람, 땅, 스턴, 동빙, 슬립, 석화)
	protected AC ac;					// AC 방어
		
	private NearObjects nearObjects;	// 주위 객체 및 플레이어들
	private SkillEffectTimerSet skillEffectTimerSet;	// 스킬 타이머 

	private int _heading; // ● 방향 0. 좌상 1. 상 2. 우상 3. 오른쪽 4. 우하 5. 하 6. 좌하 7. 좌
	
	// 모르는거
	private boolean _isSkillDelay;
	private int _addAttrKind; 
	private int actionStatus; 

	// 데미지
	private int _dmgup; 
	private int _trueDmgup; 
	private int _bowDmgup; 
	private int _trueBowDmgup; 
	private int _hitup; 
	private int _trueHitup; 
	private int _bowHitup;
	private int _trueBowHitup; 
	
	private int _Hitup_skill;
	private int _Hitup_spirit;
	
	private int _Kills;

	public int getKills() {
		return _Kills;
	}

	public void setKills(int Kills) {
		_Kills = Kills;
	}

	private int _Deaths;

	public int getDeaths() {
		return _Deaths;
	}

	public void setDeaths(int Deaths) {
		_Deaths = Deaths;
	}
	
	private final Map<Integer, L1ItemDelay.ItemDelayTimer> _itemdelay = new HashMap<Integer, L1ItemDelay.ItemDelayTimer>();

	public L1Character() {
		_level = 1;
		ability = new Ability(this); 
		resistance = new Resistance(this);
		ac = new AC();
		moveState = new MoveState();
		light = new Light(this);
		nearObjects = new NearObjects();
		gfx = new GfxId();
		skillEffectTimerSet = new SkillEffectTimerSet(this);
	}

	/**
	 * 캐릭터를 부활시킨다.
	 * 
	 * @param hp
	 *            부활 후의 HP
	 */
	public void resurrect(int hp) {
		if (!isDead()) return;
		if (hp <= 0)   hp = 1;

		setCurrentHp(hp);
		setDead(false);
		setActionStatus(0);
		L1PolyMorph.undoPoly(this);

		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.sendPackets(new S_RemoveObject(this));
			pc.getNearObjects().removeKnownObject(this);
			pc.updateObject();
		}
	}

	public double getCurrentHpPercent() {
		return (100D / (double) getMaxHp()) * (double) getCurrentHp();
	}

	public double getCurrentMpPercent() {
		return (100D / (double) getMaxMp()) * (double) getCurrentMp();
	}
	
	/**
	 * 캐릭터의 현재의 HP를 돌려준다.
	 * 
	 * @return 현재의 HP
	 */
	public int getCurrentHp() {	return _currentHp; }

	/**
	 * 캐릭터의 HP를 설정한다.
	 * 
	 * @param i 캐릭터의 새로운 HP
	 */
	public void setCurrentHp(int i) {
		if (i >= getMaxHp()) {
			i = getMaxHp();
		}
		if (i < 0) i = 0;

		_currentHp = i;
	}

	/**
	 * 캐릭터의 현재의 MP를 돌려준다.
	 * 
	 * @return 현재의 MP
	 */
	public int getCurrentMp() {
		return _currentMp;
	}

	/**
	 * 캐릭터의 MP를 설정한다.
	 * 
	 * @param i 캐릭터의 새로운 MP
	 */
	public void setCurrentMp(int i) {
		if (i >= getMaxMp()) {
			i = getMaxMp();
		}
		if (i < 0) i = 0;

		_currentMp = i;
	}

	/**
	 * 캐릭터의 잠상태를 돌려준다.
	 * 
	 * @return 잠상태를 나타내는 값. 잠상태이면 true.
	 */
	public boolean isSleeped() {
		return _sleeped;
	}

	/**
	 * 캐릭터의 잠상태를 설정한다.
	 * 
	 * @param sleeped
	 *            잠상태를 나타내는 값. 잠상태이면 true.
	 */
	public void setSleeped(boolean sleeped) {
		_sleeped = sleeped;
	}

	/**
	 * 캐릭터의 마비 상태를 돌려준다.
	 * 
	 * @return 마비 상태를 나타내는 값. 마비 상태이면 true.
	 */
	public boolean isParalyzed() {
		return _paralyzed;
	}

	/**
	 * 캐릭터의 마비 상태를 돌려준다.
	 * 
	 * @return 마비 상태를 나타내는 값. 마비 상태이면 true.
	 */
	public void setParalyzed(boolean paralyzed) {
		_paralyzed = paralyzed;
	}

	public L1Paralysis getParalysis() {
		return _paralysis;
	}

	public void setParalaysis(L1Paralysis p) {
		_paralysis = p;
	}

	public void cureParalaysis() {
		if (_paralysis != null) {
			_paralysis.cure();
		}
	}
	/**
	 * 캐릭터의 가시 범위에 있는 플레이어에, 패킷을 송신한다.
	 * 
	 * @param packet 송신하는 패킷을 나타내는 ServerBasePacket 오브젝트.
	 */
	public void broadcastPacket(ServerBasePacket packet) {
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.sendPackets(packet);
		}
	}
	/**
	 * 캐릭터의 목록을 돌려준다.
	 * 
	 * @return 캐릭터의 목록을 나타내는, L1Inventory 오브젝트.
	 */
	public L1Inventory getInventory() {
		return null;
	}

	/**
	 * 캐릭터에, skill delay 추가
	 * 
	 * @param flag
	 */
	public void setSkillDelay(boolean flag) {
		_isSkillDelay = flag;
	}

	/**
	 * 캐릭터의 독 상태를 돌려준다.
	 * 
	 * @return 스킬 지연중인가.
	 */
	public boolean isSkillDelay() {
		return _isSkillDelay;
	}

	/**
	 * 캐릭터에, Item delay 추가
	 * 
	 * @param delayId
	 *            아이템 지연 ID.  통상의 아이템이면 0, 인비지비리티크로크, 바르로그브랏디크로크이면 1.
	 * @param timer
	 *            지연 시간을 나타내는, L1ItemDelay.ItemDelayTimer 오브젝트.
	 */
	public void addItemDelay(int delayId, L1ItemDelay.ItemDelayTimer timer) {
		_itemdelay.put(delayId, timer);
	}

	/**
	 * 캐릭터로부터, Item delay 삭제
	 * 
	 * @param delayId
	 *            아이템 지연 ID.  통상의 아이템이면 0, 인비지비리티크로크, 바르로그브랏디크로크이면 1.
	 */
	public void removeItemDelay(int delayId) {
		_itemdelay.remove(delayId);
	}

	/**
	 * 캐릭터에, Item delay 이 있을까
	 * 
	 * @param delayId
	 *            조사하는 아이템 지연 ID.  통상의 아이템이면 0, 인비지비리티크로크, 바르로그브랏디
	 *            클로크이면 1.
	 * @return 아이템 지연이 있으면 true, 없으면 false.
	 */
	public boolean hasItemDelay(int delayId) {
		return _itemdelay.containsKey(delayId);
	}

	/**
	 * 캐릭터의 item delay 시간을 나타내는, L1ItemDelay.ItemDelayTimer를 돌려준다.
	 * 
	 * @param delayId
	 *            조사하는 아이템 지연 ID.  통상의 아이템이면 0, 인비지비리티크로크, 바르로그브랏디
	 *            클로크이면 1.
	 * @return 아이템 지연 시간을 나타내는, L1ItemDelay.ItemDelayTimer.
	 */
	public L1ItemDelay.ItemDelayTimer getItemDelayTimer(int delayId) {
		return _itemdelay.get(delayId);
	}

	/**
	 * 캐릭터에, 독을 추가한다.
	 * 
	 * @param poison
	 *            독을 나타내는, L1Poison 오브젝트.
	 */
	public void setPoison(L1Poison poison) {
		_poison = poison;
	}

	/**
	 * 캐릭터의 독을 치료한다.
	 */
	public void curePoison() {
		if (_poison == null) {
			return;
		}
		_poison.cure();
	}

	/**
	 * 캐릭터의 독상태를 돌려준다.
	 * 
	 * @return 캐릭터의 독을 나타내는, L1Poison 오브젝트.
	 */
	public L1Poison getPoison() {
		return _poison;
	}

	/**
	 * 캐릭터에 독의 효과를 부가한다
	 * 
	 * @param effectId
	 * @see S_Poison#S_Poison(int, int)
	 */
	public void setPoisonEffect(int effectId) {
		Broadcaster.broadcastPacket(this, new S_Poison(getId(), effectId));
	}

	public int getExp() 		{ return _exp; }
	public void setExp(int exp) { _exp = exp;  }

	public String getName() 		{ return _name; }
	public void setName(String s) 	{ _name = s; 	}

	public String getTitle() { return _title; }
	public void setTitle(String s) { _title = s; }
	
	public synchronized int getLevel() 				{ return _level; 		}
	public synchronized void setLevel(long level) 	{ _level = (int) level; }

	public short getMaxHp() 	 { return _maxHp; 			 }
	public void addMaxHp(int i)  { setMaxHp(_trueMaxHp + i); }
	public void setMaxHp(int hp) {
		_trueMaxHp = hp;
		_maxHp = (short) IntRange.ensure(_trueMaxHp, 1, 32767);
		_currentHp = Math.min(_currentHp, _maxHp);
	}

	public short getMaxMp() 	 { return _maxMp; }
	public void setMaxMp(int mp) {
		_trueMaxMp = mp;
		_maxMp = (short) IntRange.ensure(_trueMaxMp, 0, 32767);
		_currentMp = Math.min(_currentMp, _maxMp);
	}

	public void addMaxMp(int i) { setMaxMp(_trueMaxMp + i); 		 }
	public void healHp(int pt)  { setCurrentHp(getCurrentHp() + pt); }
	
	public int getAddAttrKind() 	  { return _addAttrKind; }
	public void setAddAttrKind(int i) { _addAttrKind = i; 	 }

	public int getDmgup() {	return _dmgup; } 
	public void addDmgup(int i) {
		_trueDmgup += i;
		if      (_trueDmgup >=  127) { _dmgup = 127;		} 
		else if (_trueDmgup <= -128) { _dmgup = -128; 		} 
		else 						 { _dmgup = _trueDmgup; }
	}

	public int getBowDmgup() { return _bowDmgup; } 
	public void addBowDmgup(int i) {
		_trueBowDmgup += i;
		if 		(_trueBowDmgup >=  127) { _bowDmgup = 127;			 }	 
		else if (_trueBowDmgup <= -128) { _bowDmgup = -128;			 } 
		else 							{ _bowDmgup = _trueBowDmgup; }
	}

	public int getHitup() {	return _hitup; } 
	public void addHitup(int i) {
		_trueHitup += i;
		if 		(_trueHitup >=  127) { _hitup = 127;  		}	 
		else if (_trueHitup <= -128) { _hitup = -128; 		} 
		else 						 { _hitup = _trueHitup;	}
	}

	public int getBowHitup() { return _bowHitup; } 
	public void addBowHitup(int i) {
		_trueBowHitup += i;
		if 		(_trueBowHitup >=  127) { _bowHitup = 127;  		 } 
		else if (_trueBowHitup <= -128) { _bowHitup = -128;			 } 
		else 							{ _bowHitup = _trueBowHitup; }
	}
	
	public boolean isDead() { return _isDead; }
	public void setDead(boolean flag) { _isDead = flag;	}

	public int getActionStatus() { return actionStatus; }
	public void setActionStatus(int i) { actionStatus = i;	}

	private int _currentWeapon;
	
	public int getCurrentWeapon() {
		return _currentWeapon;
	}

	public void setCurrentWeapon(int i) {
		_currentWeapon = i;
	}
	
	public int getLawful() {
		return _lawful;
	}

	public void setLawful(int i) {
		_lawful = i;
	}

	public synchronized void addLawful(int i) {
		_lawful += i;
		if (_lawful > 32767) {
			_lawful = 32767;
		} else if (_lawful < -32768) {
			_lawful = -32768;
		}
	}
	public int checkMove() {
		if (getMap().isPassable(getLocation())) {
			return 1;
		} else {
			return 0;
		}
	}
	/** 캐릭터의 우호도을 돌려준다.	 */
	public int getKarma() {	return _karma; }
	/** 캐릭터의 우호도을 설정한다.	 */
	public void setKarma(int karma) { _karma = karma; }

	// ** 도우너 딜레이 타이머 수정 **// by 도우너
		private long _skilldelay2;

		public long getSkilldelay2() {
			return _skilldelay2;
		}

		public void setSkilldelay2(long skilldelay2) {
			_skilldelay2 = skilldelay2;
		}

		// ** 도우너 딜레이 타이머 수정 **// by 도우너
	
	public GfxId getGfxId()				{ return gfx;			}
	public NearObjects getNearObjects()	{ return nearObjects;	}
	public Light getLight() 			{ return light; 		} 
	public Ability getAbility() 		{ return ability; 		}
	public Resistance getResistance() 	{ return resistance; 	}
	public int getHitup_skill() {
        return _Hitup_skill;
    }

    public int addHitup_skill(int i) {
    	 return _Hitup_skill += i;
    }
    
    public int getHitup_spirit() {
        return _Hitup_spirit;
    }

    public int addHitup_spirit(int i) {
    	return _Hitup_spirit += i;
    }
	public AC getAC()					{ return ac;			}
	public MoveState getMoveState()		{ return moveState;		}
	public SkillEffectTimerSet getSkillEffectTimerSet() { return skillEffectTimerSet; }
	
	public boolean isInvisble() {
		return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INVISIBILITY) || 
				getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLIND_HIDING));
	}
	
	//추가  /** 버그관련수정 (아이템 마법 노딜수정) By 도우너 **/
    private long _itemdelayCheck;  
    
    public long getItemdelayCheck(){
        return _itemdelayCheck;
    } 
    
    public void setItemdelayCheck(long itemdelayCheck){
        _itemdelayCheck = itemdelayCheck;
    }
    
    private long _skilldelayCheck;  
    
    public long getSkilldelayCheck(){
         return _skilldelayCheck;
    } 

    public void setSkilldelayCheck(long skilldelayCheck){
         _skilldelayCheck= skilldelayCheck;
    } 
    public List<L1PcInstance> getKnownPlayers() {
		return _knownPlayer;
	}
    public void removeKnownObject(L1Object obj) {
		_knownObjects.remove(obj);
		if (obj instanceof L1PcInstance) {
			_knownPlayer.remove(obj);
		}
	}
	public void removeAllKnownObjects() {
		_knownObjects.clear();
		_knownPlayer.clear();
	}
	/**
	 * 캐릭터에, 새롭게 인식하는 오브젝트를 추가한다.
	 * 
	 * @param obj
	 *            새롭게 인식하는 오브젝트.
	 */
	
    private final List<L1Object> _knownObjects = new CopyOnWriteArrayList<L1Object>();
    private final List<L1PcInstance> _knownPlayer = new CopyOnWriteArrayList<L1PcInstance>();

	/**
	 * 지정된 오브젝트를, 캐릭터가 인식하고 있을까를 돌려준다.
	 * 
	 * @param obj 조사하는 오브젝트.
	 * @return 오브젝트를 캐릭터가 인식하고 있으면 true, 하고 있지 않으면 false. 자기 자신에 대해서는 false를 돌려준다.
	 */
	public boolean knownsObject(L1Object obj) {
		return _knownObjects.contains(obj);
	}

	/**
	 * 캐릭터가 인식하고 있는 모든 오브젝트를 돌려준다.
	 * 
	 * @return 캐릭터가 인식하고 있는 오브젝트를 나타내는 L1Object가 격납된 ArrayList.
	 */
	public List<L1Object> getKnownObjects() {
		return _knownObjects;
	}
	
	//추가  /** 버그관련수정 (아이템 마법 노딜수정) By 도우너 **/
    public void addKnownObject(L1Object obj) {
		if (!_knownObjects.contains(obj)) {
			_knownObjects.add(obj);
			if (obj instanceof L1PcInstance) {
				_knownPlayer.add((L1PcInstance) obj);
			}
		}
	}
    private int _tempCharGfx;
    public int getTempCharGfx() {
        return this._tempCharGfx;
      }
      
      public void setTempCharGfx(int i) {
        this._tempCharGfx = i;
      }
      /**mjSpr관련*/
  	protected SpriteInformation _currentSpriteInfo;

  	public int getCurrentSpriteId() {
  		return _currentSpriteInfo == null ? 1120 : _currentSpriteInfo.getSpriteId();
  	}

  	public SpriteInformation getCurrentSprite() {
  		return _currentSpriteInfo;
  	}

  	public void setCurrentSprite(int spriteId) {
  		if (!equalsCurrentSprite(spriteId))
  			_currentSpriteInfo = SpriteInformationLoader.getInstance().get(spriteId);
  	}

  	public boolean equalsCurrentSprite(int compareSpriteId) {
  		return getCurrentSpriteId() == compareSpriteId;
  	}

  	public long getCurrentSpriteInterval(EActionCodes actionCode) {
  		return (long) _currentSpriteInfo.getInterval(this, actionCode);
  	}

  	public long getCurrentSpriteInterval(int actionCode) {
  		return (long) _currentSpriteInfo.getInterval(this, actionCode);
  	}
  	
  	private int _moveSpeed; // ● 스피드 0. 통상 1. 헤이 파업 2. 슬로우
  	
  	public int getMoveSpeed() {
  		return _moveSpeed;
  	}

  	public void setMoveSpeed(int i) {
  		_moveSpeed = i;
  	}
  	public boolean isHaste() {
		return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HASTE) || getSkillEffectTimerSet().hasSkillEffect(L1SkillId.HASTE)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GREATER_HASTE) || getMoveSpeed() == 1);
	}
	
	private int _braveSpeed; // ● 치우침 이브 상태 0. 통상 1. 치우침 이브
	
	public int getBraveSpeed() {
		return _braveSpeed;
	}

	public void setBraveSpeed(int i) {
		_braveSpeed = i;
	}
	private L1DollInstance _doll;

	public L1DollInstance getMagicDoll() {
		return _doll;
	}

	public void setMagicDoll(L1DollInstance doll) {
		_doll = doll;
	}
///자동숫돌
    private boolean autoWeapon = false;

    public boolean isAutoWeapon() {
        return autoWeapon;
    }

    public void setAutoWeapon(boolean autoWeapon) {
        this.autoWeapon = autoWeapon;
    }
  ///자동숫돌
    private int healItemNum;

    public int getHealItemNum() {
        return healItemNum;
    }

    public void setHealItemNum(int healItemNum) {
        this.healItemNum = healItemNum;
    }

    private int healDelay = 0;

    public int getHealDelay() {
        return healDelay;
    }

    public void setHealDelay(int healDelay) {
        this.healDelay = healDelay;
    }

    private int healVal;

    public int getHealVal() {
        return healVal;
    }

    public void setHealVal(int healVal) {
        this.healVal = healVal;
    }

    /**
	 * 지정된 좌표에 대할 방향을 돌려준다.
	 * 
	 * @param tx
	 *            좌표의 X치
	 * @param ty
	 *            좌표의 Y치
	 * @return 지정된 좌표에 대할 방향
	 */
	public int targetDirection(int tx, int ty) {
		float dis_x = Math.abs(getX() - tx); // X방향의 타겟까지의 거리
		float dis_y = Math.abs(getY() - ty); // Y방향의 타겟까지의 거리
		float dis = Math.max(dis_x, dis_y); // 타겟까지의 거리

		if (dis == 0)
			return getHeading();

		int avg_x = (int) Math.floor((dis_x / dis) + 0.59f); // 상하 좌우가 조금 우선인 둥근
		int avg_y = (int) Math.floor((dis_y / dis) + 0.59f); // 상하 좌우가 조금 우선인 둥근

		int dir_x = 0;
		int dir_y = 0;

		if (getX() < tx)
			dir_x = 1;
		if (getX() > tx)
			dir_x = -1;

		if (getY() < ty)
			dir_y = 1;
		if (getY() > ty)
			dir_y = -1;

		if (avg_x == 0)
			dir_x = 0;
		if (avg_y == 0)
			dir_y = 0;

		if (dir_x == 1 && dir_y == -1)
			return 1; // 상
		if (dir_x == 1 && dir_y == 0)
			return 2; // 우상
		if (dir_x == 1 && dir_y == 1)
			return 3; // 오른쪽
		if (dir_x == 0 && dir_y == 1)
			return 4; // 우하
		if (dir_x == -1 && dir_y == 1)
			return 5; // 하
		if (dir_x == -1 && dir_y == 0)
			return 6; // 좌하
		if (dir_x == -1 && dir_y == -1)
			return 7; // 왼쪽
		if (dir_x == 0 && dir_y == -1)
			return 0; // 좌상

		return getHeading();
	}
	
	public int getHeading() {
		return _heading;
	}

	public void setHeading(int i) {
		_heading = i;
	}
	
	/**
	 * 지정된 좌표까지의 직선상에, 장애물이 존재*하지 않는가*를 돌려준다.
	 * 
	 * @param tx
	 *            좌표의 X치
	 * @param ty
	 *            좌표의 Y치
	 * @return 장애물이 없으면 true, 어느 false를 돌려준다.
	 */
	public boolean glanceCheck(int tx, int ty) {
		L1Map map = getMap();
		int chx = getX();
		int chy = getY();
		for (int i = 0; i < 15; i++) {

			int cx = Math.abs(chx - tx);
			int cy = Math.abs(chy - ty);
			if (cx <= 1 && cy <= 1)
				break;

			if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty)))
				return false;

			if (chx < tx)
				chx++;
			else if (chx > tx)
				chx--;
			if (chy < ty)
				chy++;
			else if (chy > ty)
				chy--;
		}

		return true;
	}
	
	private boolean autoPotion = false;

    public boolean isAutoPotion() {
        return autoPotion;
    }

    public void setAutoPotion(boolean autoPotion) {
        this.autoPotion = autoPotion;
    }
}
