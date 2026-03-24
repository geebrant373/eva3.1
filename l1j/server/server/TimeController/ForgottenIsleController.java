package l1j.server.server.TimeController;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SystemMessage;

public class ForgottenIsleController implements Runnable {
    private static ForgottenIsleController _instance;
    public boolean isgameStart = false;
    public int Status = 0;
    private final int 대기 = 0;
    private final int 오픈 = 1;
    private final int 진행 = 2;
    private final int 종료 = 3;

    public static ForgottenIsleController getInstance() {
        if (_instance == null) {
            _instance = new ForgottenIsleController();
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
                    L1World.getInstance().broadcastServerMessage("\\aH알림: 잠시후 [잊혀진 섬] 입장이 가능합니다.");
                    continue;
                case 오픈:
                    L1World.getInstance().broadcastServerMessage("\\aH알림: 잊혀진 섬 2시간 사냥가능하오니, 조심하시오.");
                    L1World.getInstance().broadcastServerMessage("\\aH알림: 시간이 되면 강제귀환 됩니다.");
                    System.out.println("...... 잊혀진섬 열림");
                    Status = 진행;
                    continue;
                case 진행:
                    Thread.sleep(7200000L);
                    TelePort();
                    close();
                    Thread.sleep(5000L);
                    TelePort2();
                    Status = 종료;
                    continue;
                case 종료:
                    L1World.getInstance().broadcastServerMessage("\\aH알림: 잊혀진섬이 종료되었습니다.");
                    System.out.println("...... 잊섬 종료됨");
                    isgameStart = false;
                    Status = 대기;
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void TelePort() {
        for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
            switch (c.getMap().getId()) {
            case 70:
                c.stopHpRegenerationByDoll();
                c.stopMpRegenerationByDoll();
                L1Teleport.teleport(c, 33970, 33246, (short) 4, 4, true);
                c.sendPackets(new S_SystemMessage("잊혀진섬이 닫혔습니다."));
                break;
            default:
                break;
            }
        }
    }

    private void close() {
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc.getMap().getId() == 70 && pc.isDead()) {
                pc.stopHpRegenerationByDoll();
                pc.stopMpRegenerationByDoll();
                pc.sendPackets(new S_Disconnect());
            }
        }
    }

    private void TelePort2() {
        for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
            switch (c.getMap().getId()) {
            case 1700:
            case 1703:
                c.stopHpRegenerationByDoll();
                c.stopMpRegenerationByDoll();
                L1Teleport.teleport(c, 33430, 32797, (short) 4, 4, true);
                c.sendPackets(new S_SystemMessage("잊혀진섬이 닫혔습니다."));
                break;
            default:
                break;
            }
        }
    }
}
