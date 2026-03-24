package l1j.server.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.MiniGame.BattleZone;
import l1j.server.server.TimeController.DungeonQuitController;
import l1j.server.server.TimeController.ForgottenIsleController;
import l1j.server.server.TimeController.GludioDungeonController;
import l1j.server.server.TimeController.IvoryTowerController;
import l1j.server.server.TimeController.OmanTopFloorController;
import l1j.server.server.TimeController.TebeController;
import l1j.server.server.utils.CommonUtil;

public class LoopTimer extends Thread {
	public static final int SLEEP_TIME = 1000;
	private static Logger _log = Logger.getLogger(LoopTimer.class.getName());
	private static LoopTimer _instance;

	public boolean 잊혀진섬 = false;
	public boolean 본던 = false;
	public boolean 오만의탑정상 = false;
	public boolean 테베라스 = false;
	public boolean 상아탑8층 = false;
	public boolean 던전초기화 = false;
	public boolean 배틀존 = false;
	
	public static LoopTimer getInstance() {
		if (_instance == null) {
			_instance = new LoopTimer();
		}
		return _instance;
	}

	public LoopTimer() {
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
			String nowtime = CommonUtil.dateFormat("HHmm");
			if (Config.배틀존작동유무) {
				if (is배틀존(nowtime)) {
					배틀존 = false;
					if (BattleZone.getInstance().getDuelStart() == false) {
						BattleZone.getInstance().setGmStart(true);
						System.out.println("배틀존 오픈 : " + nowtime);
					}
				}
			}
			if (is본던(nowtime)) {
				본던 = false;
				if (GludioDungeonController.getInstance().isgameStart == false) {
					GludioDungeonController.getInstance().isgameStart = true;
					System.out.println("본던7 오픈 : " + nowtime);
				}
			}
			if (is잊혀진섬(nowtime)) {
				잊혀진섬 = false;
				if (ForgottenIsleController.getInstance().isgameStart == false) {
					ForgottenIsleController.getInstance().isgameStart = true;
					System.out.println("잊혀진섬 오픈 : " + nowtime);
				}
			}
			if (is오만의탑정상(nowtime)) {
				오만의탑정상 = false;
				if (OmanTopFloorController.getInstance().isgameStart == false) {
					OmanTopFloorController.getInstance().isgameStart = true;
					System.out.println("오만의탑정상 오픈 : " + nowtime);
				}
			}
			if (is테베라스(nowtime)) {
				테베라스 = false;
				if (TebeController.getInstance().isgameStart == false) {
					TebeController.getInstance().isgameStart = true;
					System.out.println("테베라스 오픈 : " + nowtime);
				}
			}
			if (is상아탑8층(nowtime)) {
				상아탑8층 = false;
				if (IvoryTowerController.getInstance().isgameStart == false) {
					IvoryTowerController.getInstance().isgameStart = true;
					System.out.println("상아탑8층 오픈 : " + nowtime);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
	
	private boolean is배틀존(String nowtime) {
		if (배틀존) {
			return true;
		}
		if (Config.배틀존_OPEN_TIME.contains(nowtime)) {
			return true;
		}
		return false;
	}
	
	private boolean is본던(String nowtime) {
		if (본던) {
			return true;
		}
		if (Config.본던_OPEN_TIME.contains(nowtime)) {
			return true;
		}
		return false;
	}
	
	private boolean is잊혀진섬(String nowtime) {
		if (잊혀진섬) {
			return true;
		}
		if (Config.잊혀진섬_OPEN_TIME.contains(nowtime)) {
			return true;
		}
		return false;
	}
	
	private boolean is오만의탑정상(String nowtime) {
		if (오만의탑정상) {
			return true;
		}
		if (Config.오만의탑정상_OPEN_TIME.contains(nowtime)) {
			return true;
		}
		return false;
	}
	
	private boolean is테베라스(String nowtime) {
		if (테베라스) {
			return true;
		}
		if (Config.테베라스_OPEN_TIME.contains(nowtime)) {
			return true;
		}
		return false;
	}
	
	private boolean is상아탑8층(String nowtime) {
		if (상아탑8층) {
			return true;
		}
		if (Config.상아탑8층_OPEN_TIME.contains(nowtime)) {
			return true;
		}
		return false;
	}
	
	private boolean is던전초기화(String nowtime) {
		if (던전초기화) {
			return true;
		}
		if (Config.던전초기화시간.contains(nowtime)) {
			return true;
		}
		return false;
	}
}