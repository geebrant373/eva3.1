package l1j.server.server.TimeController;

import l1j.server.Config;

import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class PremiumTimeController implements Runnable {

	public static final int SLEEP_TIME = Config.FEATHER_TIME * 60000; // ¿øº» 600ÃÊ

	private static PremiumTimeController _instance;

	public static PremiumTimeController getInstance() {
		if (_instance == null) {
			_instance = new PremiumTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			checkPremiumTime();
		} catch (Exception e1) {
		}
	}

	private void checkPremiumTime() {// ÀÏÁ¤½Ã°£ ±êÅÐÁö±Þ
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (!pc.isAutoClanjoin() && !pc.isPrivateShop() && !pc.noPlayerCK && pc != null && !pc.isDead()) {
				int FN = Config.FEATHER_NUM;
				int CLN = Config.FEATHER_NUM1;
				int CAN = Config.FEATHER_NUM2;
				int FN2 = Config.useritem;// ¾ÆÀÌÅÛ¹øÈ£
				int FN3 = Config.usercount;// °¹¼ö
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if (Config.ÀüÃ¼¼±¹°ÀÛµ¿À¯¹«) {
					if (pc.getInventory().countItems(FN2) <= 100) {
						pc.getInventory().storeItem(FN2, FN3);
						pc.sendPackets(new S_SystemMessage("¾Ë¸²: °æÇèÄ¡¹°¾à (" + FN3 + ") È¹µæ ÇÏ¼Ì½À´Ï´Ù."));
					}
				}
				if (pc.getClanid() == 0) { // ¹«Ç÷
					pc.getInventory().storeItem(41159, FN);
					pc.sendPackets(new S_SystemMessage("¾Ë¸²: ±êÅÐ (" + FN + ") È¹µæ ÇÏ¼Ì½À´Ï´Ù."));
				}
				if (clan != null) {
					if (clan.getCastleId() == 0 && pc.getClanid() != 0) { // Ç÷¸Í
						pc.getInventory().storeItem(41159, (CLN + FN));
						pc.sendPackets(new S_SystemMessage("¾Ë¸²: ±êÅÐ (" + (FN + CLN) + ") È¹µæ ÇÏ¼Ì½À´Ï´Ù."));
					}
					if (clan.getCastleId() != 0) { // ¼ºÇ÷
						pc.getInventory().storeItem(41159, (CAN + FN));
						pc.sendPackets(new S_SystemMessage("¾Ë¸²: ±êÅÐ (" + (FN + CAN) + ") È¹µæÇÏ¼Ì½À´Ï´Ù."));
					}
				}
			}
		}
	}
}