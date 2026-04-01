package l1j.server.server.model;

import l1j.server.Config;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class DungeonTimer implements Runnable {

	private static DungeonTimer instance;

	public static final int SleepTime = 1 * 60 * 1000; //1분 마다 체크
	
	public static DungeonTimer getInstance(){
		if (instance == null){
			instance = new DungeonTimer();
		}
		return instance;
	}

	@Override
	public void run() {
		try {
			for (L1PcInstance use : L1World.getInstance().getAllPlayers()){
				if (use == null || use.getNetConnection() == null || use.noPlayerCK){
					continue;
				} else {
					try {
						if (use.getMapId() >= 49 && use.getMapId() <= 51) { // 개미던전
							AntDundeonCheck(use);
						}
						if (use.getMapId() >= 522 && use.getMapId() <= 524) { // 그림자신전
							ShadowTempleCheck(use);
						}
						if (use.getMapId() == 13 || use.getMapId() == 36) { // 용던본던
							DragonGludioCheck(use);
						}
						if (use.getMapId() >= 53 && use.getMapId() <= 54) { // 기감
							GiranPrisonCheck(use);
						}
						if (use.getMapId() == 303) { // 몽섬
							DreamislandCheck(use);
						}
						if (use.getMapId() >= 530 && use.getMapId() <= 533) { // 라스타바드
							LastabardCheck(use);
						}
					} catch (Exception a){
					}
				}
			}
		} catch (Exception a){
			System.out.println("DungeonTimer 에러~~~");
		}
	}
	
	private void DragonGludioCheck(L1PcInstance pc) {
		if (pc.getAccount().getDragonGludioTime() >= Config.용던본던시간){
			L1Teleport.teleport(pc, 33429, 32814, (short) 4, 0, true);
			pc.sendPackets(new S_SystemMessage("경고: [용던6층&본던7층] 던전 시간이 만료되었습니다."));
		}
		pc.getAccount().setDragonGludioTime(pc.getAccount().getDragonGludioTime() + 1);
		pc.getAccount().updateDragonGludio();
	}
	
	private void LastabardCheck(L1PcInstance pc) {
		if (pc.getAccount().getLastabardTime() >= Config.라던시간 ){
			L1Teleport.teleport(pc, 33429, 32814, (short) 4, 0, true);
			pc.sendPackets(new S_SystemMessage("경고: [라스타바드] 던전 시간이 만료되었습니다."));
		}
		pc.getAccount().setLastabardTime(pc.getAccount().getLastabardTime() + 1);
		pc.getAccount().updateLastabard();
	}
	
	private void GiranPrisonCheck(L1PcInstance pc) {
		if (pc.getAccount().getGiranPrisonTime() >= Config.기감시간){
			L1Teleport.teleport(pc, 33429, 32814, (short) 4, 0, true);
			pc.sendPackets(new S_SystemMessage("경고: [기감] 던전 시간이 만료되었습니다."));
		}
		pc.getAccount().setGiranPrisonTime(pc.getAccount().getGiranPrisonTime() + 1);
		pc.getAccount().updateGiranPrison();
	}
	
	private void AntDundeonCheck(L1PcInstance pc) {
		if (pc.getAccount().getAntDundeonTime() >= Config.개미던전시간){
			if (pc.getAutoHunt()) {
				removeAuto(pc,"던전 시간 만료되어 자동 사냥이 종료됩니다.");
			}
			L1Teleport.teleport(pc, 33429, 32814, (short) 4, 0, true);
			pc.sendPackets(new S_SystemMessage("경고: [개미던전] 던전 시간이 만료되었습니다."));
		}
		pc.getAccount().setAntDundeonTime(pc.getAccount().getAntDundeonTime() + 1);
		pc.getAccount().updateAntDundeon();
	}
	
	private void ShadowTempleCheck(L1PcInstance pc) {
		if (pc.getAccount().getShadowTempleTime() >= Config.그림자신전시간){
			if (pc.getAutoHunt()) {
				removeAuto(pc,"던전 시간 만료되어 자동 사냥이 종료됩니다.");
			}
			L1Teleport.teleport(pc, 33429, 32814, (short) 4, 0, true);
			pc.sendPackets(new S_SystemMessage("경고: [그림자신전] 던전 시간이 만료되었습니다."));
		}
		pc.getAccount().setShadowTempleTime(pc.getAccount().getShadowTempleTime() + 1);
		pc.getAccount().updateShadowTemple();
	}
	
	private void DreamislandCheck(L1PcInstance pc){
		if (pc.getAccount().getDreamIslandTime() >= Config.몽섬시간){
			L1Teleport.teleport(pc, 33429, 32814, (short) 4, 0, true);
			pc.sendPackets(new S_SystemMessage("경고: [몽환의 섬] 시간이 만료되었습니다."));
		}
		pc.getAccount().setDreamIslandTime(pc.getAccount().getDreamIslandTime() + 1);
		pc.getAccount().updateDreamIsland();
	}
	
	public void removeAuto(L1PcInstance pc, String ment) {
		pc.toCharacterRefresh();
		if (pc != null) {
			pc.resetAuto();
			pc.sendPackets(new S_SystemMessage(String.format("%s", ment)));
		}
		pc.EndAutoController();
	}
}