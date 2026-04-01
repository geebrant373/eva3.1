package l1j.server.server.TimeController;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.L1SpawnUtil;

public class IvoryTowerController implements Runnable {
    private static IvoryTowerController _instance;
    public boolean isgameStart = false;
    public int Status = 0;
    private final int 대기 = 0;
    private final int 오픈 = 1;
    private final int 진행 = 2;
    private final int 종료 = 3;

    public static IvoryTowerController getInstance() {
        if (_instance == null) {
            _instance = new IvoryTowerController();
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
                    L1World.getInstance().broadcastServerMessage("\\aH알림: 잠시후 [상아탑8층] 입장이 가능합니다.");
                    continue;
                case 오픈:
                    L1World.getInstance().broadcastServerMessage("\\aH알림: 상아탑8층 2시간 사냥가능하오니,조심하세요.");
                    L1World.getInstance().broadcastServerMessage("\\aH알림: 시간이 되면 강제귀환 됩니다.");
                    System.out.println("...... 상아탑8층 열림");
                    Status = 진행;
                    continue;
                case 진행:
                    Thread.sleep(3 * 1000);
                    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                    	pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\aI5분뒤 상아탑8층 보스 데몬이 출현합니다."));
                    	pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\aI5분뒤 상아탑8층 보스 데몬이 출현합니다."));
                    }
                    Thread.sleep(300 * 1000);
                    L1SpawnUtil.spawn2(32717, 32829, (short) 82, 45649, 0, 3600 * 1000, 0);
                    Thread.sleep(7200000L);
                    TelePort();
                    close();
                    Thread.sleep(5000L);
                    TelePort2();
                    Status = 종료;
                    continue;
                case 종료:
                    L1World.getInstance().broadcastServerMessage("\\aH알림: 상아탑8층 종료되었습니다.");
                    System.out.println("...... 상아탑8층 종료됨");
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
            case 15410:
                c.stopHpRegenerationByDoll();
                c.stopMpRegenerationByDoll();
                L1Teleport.teleport(c, 33970, 33246, (short) 4, 0, true);
                c.sendPackets(new S_SystemMessage("상아탑8층 닫혔습니다."));
                break;
            default:
                break;
            }
        }
    }

    private void close() {
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc.getMap().getId() == 15410 && pc.isDead()) {
                pc.stopHpRegenerationByDoll();
                pc.stopMpRegenerationByDoll();
                pc.sendPackets(new S_Disconnect());
            }
        }
    }

    private void TelePort2() {
        for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
            switch (c.getMap().getId()) {
            case 15410:
                c.stopHpRegenerationByDoll();
                c.stopMpRegenerationByDoll();
                L1Teleport.teleport(c, 33970, 33246, (short) 4, 0, true);
                c.sendPackets(new S_SystemMessage("상아탑8층 닫혔습니다."));
                break;
            default:
                break;
            }
        }
    }
}
