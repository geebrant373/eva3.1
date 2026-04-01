package server;

import java.sql.Timestamp;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.LittleEndianHeapChannelBuffer;
import org.jboss.netty.channel.Channel;

import l1j.server.Config;
import l1j.server.GameSystem.GhostHouse;
import l1j.server.GameSystem.PetRacing;
import l1j.server.GameSystem.MiniGame.DeathMatch;
import l1j.server.server.Account;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.PacketHandler;
import l1j.server.server.TimeController.FishingTimeController;
import l1j.server.server.datatables.CharBuffTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1FollowerInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.serverpackets.ServerBasePacket;
import server.netty.coder.LineageEncryption;
import server.netty.coder.manager.DecoderManager;

public class LineageClient {
	private static Logger _log = Logger.getLogger(LineageClient.class.getName());
	private GeneralThreadPool _threadPool = GeneralThreadPool.getInstance();
	public static final String CLIENT_KEY = "CLIENT";
	private LineageEncryption le;
	private String ID;
	private L1PcInstance activeCharInstance;
	public byte[] PacketD;
	public int PacketIdx;
	private boolean close = false;
	public boolean ckclose = false;
	public boolean notic = false;
	public boolean lintool = false;
	public boolean leaftool = false;
	public int noticcount = 0;
	public int noticcount2 = 0;
	public boolean firstpacket = true;
	public int packetStep = 0;
	public byte[] firstByte = null;
	public int bufsize = 0;
	public int maxsize = 0;
	public boolean bufchek = false;
	public boolean packet = false;
	public boolean synchron = false;
	public boolean keyword = false;
	private String authCode;

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String a) {
		authCode = a;
	}

	private int _loginStatus = 0;
	
	public void setLoginAvailable() {
		_loginStatus = 1;
	}
	
	private Timestamp connectTimestamp;

	public Timestamp getConnectTimestamp() {
		return connectTimestamp;
	}

	public void setConnectTimestamp(Timestamp connectTimestamp) {
		this.connectTimestamp = connectTimestamp;
	}

	private boolean authCheck = false;

	public boolean isAuthCheck() {
		return authCheck;
	}

	public void setAuthCheck(boolean authCheck) {
		this.authCheck = authCheck;
	}

	private PacketHandler packetHandler;
	private static final int H_CAPACITY = 40; // 행동 요구를 한 변에 받아들이는 최대 용량 // 기존
												// 30
	private static Timer observerTimer = new Timer();
	private int loginStatus = 0;
	private boolean charRestart = true;
	private int _loginfaieldcount = 0;
	private Account account;
	private String hostname;
	private int threadIndex = 0;
	public HcPacket hcPacket = null;
	public ServerPavketThread ServerPacket = null;
	private CircleArray Circle = new CircleArray(1024 * 6); // 기존 5

	public boolean DecodingCK = false;
	ClientThreadObserver observer = new ClientThreadObserver(Config.AUTOMATIC_KICK * 60 * 1000);

	public CircleArray getCircleArray() {
		return Circle;
	}

	private Channel chnnel = null;

	public LineageClient(Channel _chnnel, long key) {
		this.chnnel = _chnnel;
		le = new server.netty.coder.LineageEncryption();
		le.initKeys(key);
		PacketD = new byte[1024 * 6]; // 기존4
		PacketIdx = 0;
		if (Config.AUTOMATIC_KICK > 0) {
			observer.start();
		}
		packetHandler = new PacketHandler(this);
		keyword = false;
	}

	public void setthreadIndex(int ix) {
		threadIndex = ix;
	}

	public int getthreadIndex() {
		return this.threadIndex;
	}

	public void kick() {
		sendPacket(new S_Disconnect());
		if (chnnel != null)
			chnnel.close();
	}

	public void CharReStart(boolean flag) {
		this.charRestart = flag;
	}

	public boolean CharReStart() {
		return charRestart;
	}

	public void setloginStatus(int i) {
		loginStatus = i;
	}

	public void sendPacket(ServerBasePacket bp) {
		if (packetvirsion == true) {
			if (ServerPacket == null) {
				ServerPacket = new ServerPavketThread(150);
				_threadPool.execute(ServerPacket);
			}
			ServerPacket.requestWork(bp.getBytes());
			return;
		}
		ChannelBuffer buffer = Nettybuffer(encryptE(bp.getBytes()), bp.getLength());
		bp.close();
		chnnel.write(buffer);
	}

	public synchronized void sendPacket2(ServerBasePacket bp) {
		ChannelBuffer buffer = Nettybuffer(encryptE(bp.getBytes()), bp.getLength());
		bp.close();
		chnnel.write(buffer);
	}

	private ChannelBuffer Nettybuffer(byte[] data, int length) {
		byte[] size = new byte[2];
		size[0] |= length & 0xff;
		size[1] |= length >> 8 & 0xff;
		ChannelBuffer _buffer = new LittleEndianHeapChannelBuffer(length);
		_buffer.writeBytes(size);
		_buffer.writeBytes(data);
		return _buffer;
	}

	/**
	 * 종료시 호출
	 */
	public void close() {
		if (!close) {
			close = true;
			try {
				if (activeCharInstance != null) {
					quitGame(activeCharInstance);
					activeCharInstance.logout();
				}
			} catch (Exception e) {
			}
			try {
				LoginController.getInstance().logout(this);
				stopObsever();
				DecoderManager.getInstance().removeClient(this, threadIndex);

			} catch (Exception e) {
			}
			try {
				if (chnnel != null)
					chnnel.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 현재 클라이언트에 사용할 PC 객체를 설정한다.
	 * 
	 * @param pc
	 */
	public void setActiveChar(L1PcInstance pc) {
		activeCharInstance = pc;
	}

	/**
	 * 현재 클라이언트 사용하고 있는 PC 객체를 반환한다.
	 * 
	 * @return activeCharInstance;
	 */
	public L1PcInstance getActiveChar() {
		return activeCharInstance;
	}

	/**
	 * 현재 사용하는 계정을 설정한다.
	 * 
	 * @param account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * 현재 사용중인 계정은 반환한다.
	 * 
	 * @return account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * 현재 사용중인 계정명을 반환한다.
	 * 
	 * @return account.getName();
	 */
	public String getAccountName() {
		if (account == null) {
			return null;
		}
		String name = account.getName();

		return name;
	}

	/**
	 * 해당 LineageClient가 종료할때 호출
	 * 
	 * @param pc
	 */
	public void quitGame(L1PcInstance pc) {

		// _log.info("캐릭터 종료: char=" + pc.getName() + " account=" +
		// pc.getAccountName() + " host=" + L.getHostname());
		pc.setadFeature(1);
		pc.setDeathMatch(false);
		pc.setHaunted(false);
		pc.setPetRacing(false);

		// 사망하고 있으면(자) 거리에 되돌려, 공복 상태로 한다
		if (pc.isDead()) {
			int[] loc = Getback.GetBack_Location(pc, true);
			pc.setX(loc[0]);
			pc.setY(loc[1]);
			pc.setMap((short) loc[2]);
			pc.setCurrentHp(pc.getLevel());
			pc.set_food(39); // 10%

			loc = null;
		}

		// 트레이드를 중지한다
		if (pc.getTradeID() != 0) { // 트레이드중
			L1Trade trade = new L1Trade();
			trade.TradeCancel(pc);
		}

		// 결투중
		if (pc.getFightId() != 0) {
			pc.setFightId(0);
			L1PcInstance fightPc = (L1PcInstance) L1World.getInstance().findObject(pc.getFightId());
			if (fightPc != null) {
				fightPc.setFightId(0);
				fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
			}
		}

		// 파티를 빠진다
		if (pc.isInParty()) { // 파티중
			pc.getParty().leaveMember(pc);
		}

		// 채팅파티를 빠진다
		if (pc.isInChatParty()) { // 채팅파티중
			pc.getChatParty().leaveMember(pc);
		}

		FishingTimeController.getInstance().removeMember(pc);
		FishingTimeController.getInstance().endFishing(pc);

		if (pc.isFishingReady()) {
			FishingTimeController.getInstance().endFishing(pc);
			FishingTimeController.getInstance().removeMember(pc);
		}

		if (DeathMatch.getInstance().isEnterMember(pc)) {
			DeathMatch.getInstance().removeEnterMember(pc);
		}
		if (GhostHouse.getInstance().isEnterMember(pc)) {
			GhostHouse.getInstance().removeEnterMember(pc);
		}
		if (PetRacing.getInstance().isEnterMember(pc)) {
			PetRacing.getInstance().removeEnterMember(pc);
		}
		// 애완동물을 월드 MAP상으로부터 지운다
		for (Object petObject : pc.getPetList().values().toArray()) {
			if (petObject instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) petObject;
				pet.dropItem();
				int time = pet.getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.STATUS_PET_FOOD);
				PetTable.getInstance().storePetFoodTime(pet.getId(), pet.getFood(), time);
				pet.getSkillEffectTimerSet().clearSkillEffectTimer();
				pc.getPetList().remove(pet.getId());
				pet.deleteMe();
			} else if (petObject instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) petObject;
				for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(summon)) {
					visiblePc.sendPackets(new S_SummonPack(summon, visiblePc, false));
				}
			}
		}

		// 마법 인형을 월드 맵상으로부터 지운다
		for (L1DollInstance doll : pc.getDollList().values()) {
			doll.deleteDoll();
		}

		Object[] followerList = pc.getFollowerList().values().toArray();
		L1FollowerInstance follower = null;
		for (Object followerObject : followerList) {
			follower = (L1FollowerInstance) followerObject;
			follower.setParalyzed(true);
			follower.spawn(follower.getNpcTemplate().get_npcId(), follower.getX(), follower.getY(), follower.getMoveState().getHeading(),
					follower.getMapId());
			follower.deleteMe();
		}

		// 엔챤트를 DB의 character_buff에 보존한다
		CharBuffTable.DeleteBuff(pc);
		CharBuffTable.SaveBuff(pc);
		pc.getSkillEffectTimerSet().clearSkillEffectTimer();

		for (L1ItemInstance item : pc.getInventory().getItems()) {
			if (item.getCount() <= 0) {
				pc.getInventory().deleteItem(item);
			}
		}
		// 로그아웃 시간을 기록한
		pc.setLogOutTime();
		// pc의 모니터를 stop 한다.
		// pc.stopEtcMonitor();
		// 온라인 상태를 OFF로 해, DB에 캐릭터 정보를 기입한다
		pc.setOnlineStatus(0);
		pc.set로테시작(0);

		try {
			pc.save();
			pc.saveInventory();
			pc = null;
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

	}

	/**
	 * 현재 연결된 호스트명을 반환한다.
	 * 
	 * @return
	 */
	public String getHostname() {
		String HostName = null;
		if (chnnel == null)
			return null;
		if (chnnel.getRemoteAddress() == null)
			return null;
		StringTokenizer st = new StringTokenizer(chnnel.getRemoteAddress().toString().substring(1), ":");
		HostName = st.nextToken();
		st = null;
		return HostName;
	}

	/**
	 * 현재 로그인 실패한 카운트 수를 반환한다.
	 * 
	 * @return
	 */
	public int getLoginFailedCount() {
		return _loginfaieldcount;
	}

	/**
	 * 현재 로그인 실패한 카운트 수를 설정한다.
	 * 
	 * @param i
	 */
	public void setLoginFailedCount(int i) {
		_loginfaieldcount = i;
	}

	/**
	 * 패킷을 복호화 하고 패킷핸들러에 패킷을 전달한다.
	 * 
	 * @param data
	 */
	public void encryptD(byte[] temp2) {
		try {
			if (packetvirsion == true) {
				if (hcPacket == null) {
					hcPacket = new HcPacket(H_CAPACITY);
					_threadPool.execute(hcPacket);
				}
				hcPacket.requestWork(temp2);
			} else {
				int length = PacketSize(temp2) - 2;
				byte[] temp = new byte[length];
				char[] incoming = new char[length];
				System.arraycopy(temp2, 2, temp, 0, length);
				incoming = le.getUChar8().fromArray(temp, incoming, length);
				incoming = le.decrypt(incoming, length);
				temp2 = le.getUByte8().fromArray(incoming, temp);
				PacketHandler(temp2);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 패킷을 암호화한다.
	 * 
	 * @param data
	 * @return
	 */
	public byte[] encryptE(byte[] data) {
		try {
			char[] data1 = le.getUChar8().fromArray(data);
			data1 = le.encrypt(data1);
			data = le.getUByte8().fromArray(data1);
			return data;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 패킷 사이즈를 반환한다.
	 * 
	 * @param data
	 * @return
	 */
	private int PacketSize(byte[] data) {
		int length = data[0] & 0xff;
		length |= data[1] << 8 & 0xff00;
		return length;
	}

	/**
	 * ID를 반환한다.
	 * 
	 * @return
	 */
	public String getID() {
		return ID;
	}

	/**
	 * ID를 설정한다.
	 * 
	 * @param id
	 */
	public void setID(String id) {
		ID = id;
	}

	/**
	 * LineageClient의 접속 여부를 반환한다.
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return chnnel.isConnected();
	}

	/**
	 * 현재 접속중인 LineageClient에 IP를 반환한다.
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	public String getIp() {
		String _Ip = null;
		if (chnnel == null)
			return null;
		if (chnnel.getRemoteAddress() == null)
			return null;
		StringTokenizer st = new StringTokenizer(chnnel.getRemoteAddress().toString().substring(1), ":");
		if (st == null)
			return null;
		_Ip = st.nextToken();
		st = null;
		return _Ip;
	}

	/**
	 * 현재 실행중인 클라이언트 감시를 중단한다.
	 */
	public void stopObsever() {
		observer.cancel();
	}

	/**
	 * 현재 새션 종료상태를 반환한다.
	 * 
	 * @return
	 */
	public boolean isClosed() {
		if (!chnnel.isConnected())
			return true;
		else {
			return false;
		}
	}

	private int time = 0;
	private int length = 0;
	public int loginStatus2 = 0;

	public boolean chconnet(byte[] data) {
		if (data[0] == 1 && data[1] == 3 && data[2] == 6 && data[3] == 7 && data[4] == 9) {
			return true;
		}
		return false;
	}

	public boolean packetvirsion = false;
	public int packetcount = 0;
	private PacketHandler _handler = new PacketHandler(LineageClient.this);

	/**
	 * 패킷 구분하여 처리.
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void PacketHandler(byte[] data) throws Exception {

		int opcode = data[0] & 0xFF;
		Date now = new Date();
		int leng = data.length;
		length += leng;
		if (time == 0) {
			time = now.getSeconds();
		}
		if (now.getSeconds() != time) {
			length = 0;
			time = 0;
		}
		if (length > 2048) {
			close();
			System.out.println("허용된 패킷 초과 : " + getIp());
			return;
		}

		if (Config.STANDBY_SERVER) {
			if (opcode == Opcodes.C_OPCODE_ATTACK || opcode == Opcodes.C_OPCODE_USESKILL || opcode == Opcodes.C_OPCODE_ARROWATTACK) {
				return;
			}
		}

		if (opcode != Opcodes.C_OPCODE_KEEPALIVE) {
			observer.packetReceived();
		}
		if (CharReStart()) {
			if (!(opcode == Opcodes.C_OPCODE_SELECT_CHARACTER || opcode == Opcodes.S_OPCODE_DISCONNECT || opcode == Opcodes.C_OPCODE_RETURNTOLOGIN
					|| opcode == Opcodes.C_OPCODE_QUITGAME || opcode == Opcodes.C_OPCODE_CREATE_CHARACTER || opcode == Opcodes.C_OPCODE_DELETECHAR
					|| opcode == Opcodes.C_OPCODE_LOGINPACKET || opcode == Opcodes.C_OPCODE_NOTICECLICK || opcode == Opcodes.C_OPCODE_CLIENTVERSION
					|| opcode == Opcodes.C_OPCODE_LOGINTOSERVEROK)) {

				return;
			}
		}
		if (opcode == Opcodes.C_OPCODE_CLIENTVERSION) {
			packetvirsion = true;
		}
		if (activeCharInstance == null) {
			packetHandler.handlePacket(data, activeCharInstance);
			return;
		}

		if (opcode == Opcodes.C_OPCODE_TRADE || opcode == Opcodes.C_OPCODE_TRADEADDOK || opcode == Opcodes.C_OPCODE_DROPITEM
				|| opcode == Opcodes.C_OPCODE_PICKUPITEM || opcode == Opcodes.C_OPCODE_GIVEITEM || opcode == Opcodes.C_OPCODE_USESKILL
				|| opcode == Opcodes.C_OPCODE_TRADEADDITEM || opcode == Opcodes.C_OPCODE_SHOP_N_WAREHOUSE
				|| opcode == Opcodes.C_OPCODE_DELETEINVENTORYITEM || opcode == Opcodes.C_OPCODE_SHOP) {
			if (Config.KEYWORD_USE) {
				if (!this.keyword) {
					activeCharInstance.sendPackets("\\fT[!] 키워드인증을 진행해주세요.[.키인증 [숫자6자리]");
					activeCharInstance.sendPackets("\\fT[!] 인증완료되어야만 정상적으로 이용가능합니다.");
					return;
				}
			}
		}

		if (activeCharInstance.isReturnStatus == true) {
			if (opcode == Opcodes.C_OPCODE_ATTACK || opcode == Opcodes.C_OPCODE_TRADE || opcode == Opcodes.C_OPCODE_TRADEADDOK
					|| opcode == Opcodes.C_OPCODE_USEITEM || opcode == Opcodes.C_OPCODE_DROPITEM || opcode == Opcodes.C_OPCODE_PICKUPITEM
					|| opcode == Opcodes.C_OPCODE_ARROWATTACK || opcode == Opcodes.C_OPCODE_GIVEITEM || opcode == Opcodes.C_OPCODE_USESKILL
					|| opcode == Opcodes.C_OPCODE_TRADEADDITEM || opcode == Opcodes.C_OPCODE_SHOP_N_WAREHOUSE
					|| opcode == Opcodes.C_OPCODE_DELETEINVENTORYITEM || opcode == Opcodes.C_OPCODE_MOVECHAR || opcode == Opcodes.C_OPCODE_SHOP) {
				return;
			}
		} else if (activeCharInstance.isFreeze() || activeCharInstance.isParalyzed() || activeCharInstance.isSleeped()
				|| activeCharInstance.isStun()) {
			if (activeCharInstance.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_FREEZE)) {
				if (opcode == Opcodes.C_OPCODE_DROPITEM || opcode == Opcodes.C_OPCODE_PICKUPITEM || opcode == Opcodes.C_OPCODE_GIVEITEM
						|| opcode == Opcodes.C_OPCODE_RESTART) {
					return;
				}
			} else {
				if (opcode == Opcodes.C_OPCODE_ATTACK || opcode == Opcodes.C_OPCODE_TRADE || opcode == Opcodes.C_OPCODE_TRADEADDOK
						|| opcode == Opcodes.C_OPCODE_USEITEM || opcode == Opcodes.C_OPCODE_DROPITEM || opcode == Opcodes.C_OPCODE_PICKUPITEM
						|| opcode == Opcodes.C_OPCODE_ARROWATTACK || opcode == Opcodes.C_OPCODE_GIVEITEM || opcode == Opcodes.C_OPCODE_USESKILL
						|| opcode == Opcodes.C_OPCODE_RESTART || opcode == Opcodes.C_OPCODE_TRADEADDITEM
						|| opcode == Opcodes.C_OPCODE_RETURNTOLOGIN) {
					return;
				}
			}
		}
		_handler.handlePacket(data, activeCharInstance);
		if (opcode == Opcodes.C_OPCODE_USEITEM) {
			activeCharInstance.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		}
	}

	public String printData(byte[] data, int len) {
		StringBuffer result = new StringBuffer();
		int counter = 0;
		for (int i = 0; i < len; i++) {
			if (counter % 16 == 0) {
				result.append(fillHex(i, 4) + ": ");
			}
			result.append(fillHex(data[i] & 0xff, 2) + " ");
			counter++;
			if (counter == 16) {
				result.append("   ");
				int charpoint = i - 15;
				for (int a = 0; a < 16; a++) {
					int t1 = data[charpoint++];
					if (t1 > 0x1f && t1 < 0x80) {
						result.append((char) t1);
					} else {
						result.append('.');
					}
				}
				result.append("\n");
				counter = 0;
			}
		}

		int rest = data.length % 16;
		if (rest > 0) {
			for (int i = 0; i < 17 - rest; i++) {
				result.append("   ");
			}

			int charpoint = data.length - rest;
			for (int a = 0; a < rest; a++) {
				int t1 = data[charpoint++];
				if (t1 > 0x1f && t1 < 0x80) {
					result.append((char) t1);
				} else {
					result.append('.');
				}
			}

			result.append("\n");
		}
		return result.toString();
	}

	private String fillHex(int data, int digits) {
		String number = Integer.toHexString(data);

		for (int i = number.length(); i < digits; i++) {
			number = "0" + number;
		}
		return number;
	}

	/**
	 * 
	 * @author Developer
	 *
	 */
	public boolean obcheck = false;

	class ClientThreadObserver extends TimerTask {
		private int _checkct = 1;

		private final int _disconnectTimeMillis;

		public ClientThreadObserver(int disconnectTimeMillis) {
			_disconnectTimeMillis = disconnectTimeMillis;
		}

		public void start() {
			observerTimer.scheduleAtFixedRate(ClientThreadObserver.this, 1000 * 60, _disconnectTimeMillis);
		}

		@Override
		public void run() {
			try {
				if (!chnnel.isConnected()) {
					cancel();
					return;
				}
				if (_checkct > 0) {
					_checkct = 0;
					return;
				}
				if (activeCharInstance == null) {
					kick();
					_log.warning("일정시간 응답을 얻을 수 없었기 때문에(" + hostname + ")과(와)의 접속을 강제 절단 했습니다.");
					cancel();
					return;
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				cancel();
			}
		}

		public void packetReceived() {
			_checkct++;
		}
	}

	public class ServerPavketThread implements Runnable {
		private final BlockingQueue<byte[]> _queue;
		private byte[] c = { 1, 2, 3, 4 };

		public ServerPavketThread() {
			_queue = new LinkedBlockingQueue<byte[]>();
		}

		public ServerPavketThread(int capacity) {
			_queue = new LinkedBlockingQueue<byte[]>(capacity);
		}

		public void requestclose() {
			requestWork(c);
		}

		public void requestWork(byte data[]) {
			_queue.offer(data);
		}

		public void requestclear() {
			_queue.clear();
		}

		public void run() {
			byte[] data;
			while (chnnel.isConnected()) {
				try {
					data = _queue.poll(3000, TimeUnit.MILLISECONDS);
					if (data != null && chnnel.isConnected()) {
						try {
							ChannelBuffer buffer = Nettybuffer(encryptE(data), data.length + 2);
							chnnel.write(buffer);
						} catch (Exception e) {
						}
					}
				} catch (InterruptedException e1) {
				}
			}
			_queue.clear();
			return;
		}
	}

	public boolean packetch = false;

	public class HcPacket implements Runnable {
		private final BlockingQueue<byte[]> _queue;
		private byte[] c = { 1, 2, 3, 4 };

		public HcPacket() {
			_queue = new LinkedBlockingQueue<byte[]>();
			_handler = new PacketHandler(LineageClient.this);
		}

		public HcPacket(int capacity) {
			_queue = new LinkedBlockingQueue<byte[]>(capacity);
			_handler = new PacketHandler(LineageClient.this);
		}

		public void requestclose() {
			requestWork(c);
		}

		public void requestWork(byte data[]) {
			_queue.offer(data);
		}

		public void requestclear() {
			_queue.clear();
		}

		public void run() {
			byte[] data;
			while (chnnel.isConnected()) {
				try {
					data = _queue.poll(3000, TimeUnit.MILLISECONDS);
					if (data != null && chnnel.isConnected()) {
						try {
							int length = PacketSize(data) - 2;
							byte[] temp = new byte[length];
							char[] incoming = new char[length];
							System.arraycopy(data, 2, temp, 0, length);
							incoming = le.getUChar8().fromArray(temp, incoming, length);
							incoming = le.decrypt(incoming, length);
							data = le.getUByte8().fromArray(incoming, temp);
							PacketHandler(data);
						} catch (Exception e) {
						}
					}
				} catch (InterruptedException e1) {
				}
			}
			_queue.clear();
			return;
		}
	}

	private String HexToDex(int data, int digits) {
		String number = Integer.toHexString(data);
		for (int i = number.length(); i < digits; i++)
			number = "0" + number;
		return number;
	}

	public String DataToPacket(byte[] data, int len) {
		StringBuffer result = new StringBuffer();
		int counter = 0;
		for (int i = 0; i < len; i++) {
			if (counter % 16 == 0) {
				result.append(HexToDex(i, 4) + ": ");
			}
			result.append(HexToDex(data[i] & 0xff, 2) + " ");
			counter++;
			if (counter == 16) {
				result.append("   ");
				int charpoint = i - 15;
				for (int a = 0; a < 16; a++) {
					int t1 = data[charpoint++];
					if (t1 > 0x1f && t1 < 0x80) {
						result.append((char) t1);
					} else {
						result.append('.');
					}
				}
				result.append("\n");
				counter = 0;
			}
		}
		int rest = data.length % 16;
		if (rest > 0) {
			for (int i = 0; i < 17 - rest; i++) {
				result.append("   ");
			}
			int charpoint = data.length - rest;
			for (int a = 0; a < rest; a++) {
				int t1 = data[charpoint++];
				if (t1 > 0x1f && t1 < 0x80) {
					result.append((char) t1);
				} else {
					result.append('.');
				}
			}
			result.append("\n");
		}
		return result.toString();
	}
}
