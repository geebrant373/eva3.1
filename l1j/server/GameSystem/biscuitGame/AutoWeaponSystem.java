package l1j.server.GameSystem.biscuitGame;

import java.util.Collection;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

/** 자동 물약 시스템 **/
public class AutoWeaponSystem extends Thread {

	private static AutoWeaponSystem _instance;
	
	public static AutoWeaponSystem getInstance() {
		if (_instance == null){
			_instance = new AutoWeaponSystem();
			_instance.start();
		}
		return _instance;
	}

	public AutoWeaponSystem() {
		super("server.controller.AutoWeaponSystem");
	}

	private Collection<L1PcInstance> list = null;
	
	private L1ItemInstance weapon = null;

	public void run() {
		
		while (true) {
			try {
				list = L1World.getInstance().getAllPlayers();
				for (L1PcInstance pc : list) {

                    weapon = null;
                    if (pc.isAutoWeapon()) {
                        weapon = pc.getWeapon();
                        if (weapon != null) {
                            if (weapon.get_durability() > 0) {
                                if (pc.getInventory().checkItem(40317)) {
                                    pc.getInventory().recoveryDamage(weapon);
                                    L1ItemInstance item = pc.getInventory().findItemId(40317);
                                    pc.getInventory().removeItem(item, 1);
                                } else {
                                    pc.sendPackets(new S_SystemMessage("숫돌이 부족합니다."));
                                }
                            }
                        }
                    }

                }
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					Thread.sleep(1000);
					list = null;					
				} catch (Exception e) {
				}
			}
		}
	}

}