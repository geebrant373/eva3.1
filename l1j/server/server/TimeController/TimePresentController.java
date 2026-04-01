package l1j.server.server.TimeController;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;

public class TimePresentController implements Runnable {
    private static TimePresentController _instance;
    public boolean isgameStart = false;
    public int Status = 0;
    private final int 대기 = 0;
    private final int 오픈 = 1;
    private final int 진행 = 2;
    private final int 종료 = 3;

    public static TimePresentController getInstance() {
        if (_instance == null) {
            _instance = new TimePresentController();
        }
        return _instance;
    }

    @Override
    public void run() {
        try {
            while (true) {
                switch (Status) {
                case 대기:
                    Thread.sleep(10000);
                    if (isgameStart == false) {
                        continue;
                    }
                    Status = 오픈;
                    continue;
                case 오픈:
                    L1World.getInstance().broadcastServerMessage("알림 : 아이템을 순차적으로 지급하고 있습니다.");
                    Status = 진행;
                    continue;
                case 진행:
                    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                    	pc.getInventory().storeItem(875640508, 1);
                    	pc.sendPackets(new S_SystemMessage("알림 : 아이템이 지급되었습니다."));
                    }
                    Status = 종료;
                    continue;
                case 종료:
                    isgameStart = false;
                    Status = 대기;
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
