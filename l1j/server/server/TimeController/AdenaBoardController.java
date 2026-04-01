package l1j.server.server.TimeController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.BoardAdenaTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1BoardAdena;
import l1j.server.server.utils.SQLUtil;

public class AdenaBoardController implements Runnable {

	private static AdenaBoardController _instance;

	public static AdenaBoardController getInstance() {
		if (_instance == null) {
			_instance = new AdenaBoardController();
		}
		return _instance;
	}

	public AdenaBoardController() {
		GeneralThreadPool.getInstance().schedule(this, 60000);
	}

	@Override
	public void run() {
		try {
			long time = 0;
			long Realtime = System.currentTimeMillis() / 1000;
			for(L1BoardAdena board : BoardAdenaTable.getInstance().getBoardAdenaList()) {
				if(board == null) {
					continue;
				}
				if(board.getType() == 2) { //판매완료 물품 제외
					continue;
				}
				time = board.getAdenaTime().getTimeInMillis() / 1000;
				
				if((Realtime - time) >= 7200) {
					int id = getBroadAdenaTable_Id();
					board.setId(id); //id를 최신으로 변경
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(System.currentTimeMillis());
					board.setAdenaTime(cal);
					//정보 업데이트
					BoardAdenaTable.getInstance().updateBoardAdena(board);
					
					L1PcInstance target = L1World.getInstance().getPlayer(board.getChaName());
					if(target != null) {
						target.sendPackets(new S_SkillSound(target.getId(), 1091));
						target.sendPackets(new S_SystemMessage("물품번호[" + board.getTradeNumber() + "] 2시간이 지나 재등록되었습니다."));
//						target.sendPackets(new S_ServerMessage(428)); // 편지가 도착했습니다.
//						target.sendPackets(new S_LetterList(target, 0 ,40));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		GeneralThreadPool.getInstance().schedule(this, 1000);
	}

	/**
	 * 편지에 사용할 시간 포멧
	 */
	private String getLetterTime() {
		int time = 0;
		String dTime = null;
		Calendar Cal = Calendar.getInstance();
		SimpleDateFormat Time = new SimpleDateFormat("yyyyMMdd");
		Cal.setTimeInMillis(System.currentTimeMillis());
		time = Integer.parseInt(Time.format(Cal.getTime()));
		dTime = Integer.toString(time);
		
		return dTime;
	}
	
	/**
	 * 테이블에 등록될 id를 받아온다.
	 */
	private int getBroadAdenaTable_Id() {
		int id = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM board_adena ORDER BY id DESC");
			rs = pstm.executeQuery();
			if (rs.next()) {
				id = rs.getInt("id");
			}
			id++;
		} catch (SQLException e) {
			e.getStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
			
		return id;
	}
}
