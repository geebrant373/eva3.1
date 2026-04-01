package l1j.server.server.TimeController;

import java.util.ArrayList;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.function.MagicDoll;

public class AutoDollController extends Thread {

    private static AutoDollController _instance;

    public static void startController() {
        if (_instance == null || !_instance.isAlive()) {
            _instance = new AutoDollController();
            _instance.start();
        }
    }

    public static AutoDollController getInstance() {
        startController();
        return _instance;
    }

    @Override
    public void run() {
        System.out.println("자동인형 컨트롤러 시작");
        try {
            while (true) {
                try {
                    Thread.sleep(2000);
                    자동인형();
                } catch (Exception innerEx) {
                    System.err.println("오류 발생:");
                    innerEx.printStackTrace();
                }
            }
        } catch (Throwable t) {
            System.err.println("치명적 오류 발생  스레드 재시작 시도");
            t.printStackTrace();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {}
            startController();
        }
    }

    private void 자동인형() {
        for (L1PcInstance pc : new ArrayList<>(L1World.getInstance().getAllPlayers())) {
            try {
                if (pc != null && pc.isAutoDollFlag()) {
                    MagicDoll.checkAutoDoll(pc);
                }
            } catch (Exception e) {
                System.err.println("특정 캐릭터 인형 처리 오류: " + (pc != null ? pc.getName() : "null"));
                e.printStackTrace();
            }
        }
    }
}
