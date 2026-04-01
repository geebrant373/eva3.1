package l1j.server.GameSystem;

import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Disconnect;

public class GomuSystem1 extends Thread {

	private static GomuSystem1 _instance;

	private boolean _DevilStart;

	public boolean getDevilStart1() {
		return _DevilStart;
	}

	public void setDevilStart1(boolean Devil) {
		_DevilStart = Devil;
	}

	private static long sTime = 0;// 밤12시시 기준으로 시작

	private String NowTime = "";
	// 시간 간격
	private static final int LOOP = 3;// 2시간마다 한번씩 오픈한다는 말

	private static final SimpleDateFormat s = new SimpleDateFormat("HH", Locale.KOREA); // 현제시각

	private static final SimpleDateFormat ss = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA); // 오픈날짜시간

	public static GomuSystem1 getInstance() {
		if (_instance == null) {
			_instance = new GomuSystem1();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(1000);
	
				/** 오픈 **/
				if (!isOpenDevil1())
					continue;
				if (L1World.getInstance().getAllPlayers().size() <= 0)
					continue;

				/** 오픈 메세지 **/
				System.out.println("용의쉼터 오픈");
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "사냥터 알림: 용의쉼터가 열렸습니다."));
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("사냥터 알림: 용의쉼터가 열렸습니다."));
				L1World.getInstance().broadcastServerMessage("\\fW사냥터 알림: 용의쉼터가 열렸습니다!");

				/** 악마왕영토 시작 **/
				setDevilStart1(true);

				/** 실행 1시간 시작 **/

				//Thread.sleep(3600000L); // 3800000L 1시간 10분정도
				Thread.sleep(3600000L); // 3800000L 1시간 10분정도
				//카운트다운();
				//Thread.sleep(10000L);
				/** 1시간 후 자동 텔레포트 **/
				TelePort();
				close(); // 추가(꼭여기에하삼)
				Thread.sleep(5000L);
				TelePort2();

				/** 종료 **/
				End();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private void 카운트다운() {
//		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
//			pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_10SECOND_COUNT));
//		}
//
//	}

	/**
	 * 오픈 시각을 가져온다
	 *
	 * @return (Strind) 오픈 시각(MM-dd HH:mm)
	 */
	public String DevilOpenTime1() { // 일단 수정 해봄
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(sTime);
		return ss.format(c.getTime());
	}

	/**
	 * 영토가 열려있는지 확인
	 *
	 * @return (boolean) 열려있다면 true 닫혀있다면 false
	 */
	private boolean isOpenDevil1() { // 일단 수정 해봄
		NowTime = getTime();
		if ((Integer.parseInt(NowTime) % LOOP) == 0)
			return true;
		return false;
	}

	/**
	 * 실제 현재시각을 가져온다
	 *
	 * @return (String) 현재 시각(HH:mm)
	 */
	private String getTime() {
		return s.format(Calendar.getInstance().getTime());
	}

	/** 아덴마을로 팅기게 **/
	private void TelePort() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			switch (c.getMap().getId()) {
			case 508: // 고무 mapid
				c.stopHpRegenerationByDoll();
				c.stopMpRegenerationByDoll();
				L1Teleport.teleport(c, 33442, 32818, (short)4, c.getMoveState().getHeading(), true);	
				c.sendPackets(new S_SystemMessage("용의쉼터가 닫혔습니다."));
				break;
			default:
				break;
			}
		}
	}

	/** 아덴마을로 팅기게 **/
	private void TelePort2() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			switch (c.getMap().getId()) {
			case 508: // mapid
				c.stopHpRegenerationByDoll();
				c.stopMpRegenerationByDoll();
				L1Teleport.teleport(c, 33442, 32818, (short)4, c.getMoveState().getHeading(), true);	
				c.sendPackets(new S_SystemMessage("용의쉼터가 닫혔습니다."));
				break;
			default:
				break;
			}
		}
	}

	/** 캐릭터가 죽었다면 종료시키기 **/
	private void close() {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc.getMap().getId() == 72 && pc.isDead()) {
				pc.stopHpRegenerationByDoll();
				pc.stopMpRegenerationByDoll();
				pc.sendPackets(new S_Disconnect());
			}
		}
	}

	/** 종료 **/
	private void End() {
		TelePort();
		close(); //추가
		L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, " 용의쉼터가 닫혔습니다. 3시간뒤에 다시 열립니다."));
		L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(" 용의쉼터가 닫혔습니다. 3시간뒤에 다시 열립니다."));
		L1World.getInstance().broadcastServerMessage("\\fW용의쉼터가 닫혔습니다. 사라졌습니다. 3시간뒤에 다시 열립니다.");
		setDevilStart1(false);
	}
}
