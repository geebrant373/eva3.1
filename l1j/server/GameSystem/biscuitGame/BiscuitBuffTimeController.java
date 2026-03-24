package l1j.server.GameSystem.biscuitGame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.ShopNpcSystem;
import l1j.server.server.UserCommands;
import l1j.server.server.datatables.ShopNpcTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class BiscuitBuffTimeController implements Runnable {
	private static Logger _log = Logger.getLogger(UserCommands.class.getName());
	
	private static BiscuitBuffTimeController _instance;
	
	private SimpleDateFormat sdf = null;
	private int nowtime = 0;
	
	public static BiscuitBuffTimeController getInstance() {
		if (_instance == null) {
			_instance = new BiscuitBuffTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				sdf = new SimpleDateFormat("HHmm");
				nowtime = Integer.valueOf(sdf.format(getRealTime().getTime()));
				
				radeExciting(); // 
				shopReload();
			} catch (Exception e) {
				_log.log(Level.SEVERE, "BuffTimeController에서 에러발생", e);
			} finally {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void shopReload() { //1시간단위갱신
		try {
			if(nowtime == 100 || nowtime == 200	|| nowtime == 300 || nowtime == 400 
					|| nowtime == 500 || nowtime == 600 || nowtime == 700|| nowtime == 800
					||nowtime == 900 || nowtime == 1000	|| nowtime == 1100 || nowtime == 1200 
					|| nowtime == 1300 || nowtime == 1400 || nowtime == 1500|| nowtime == 1600
					||nowtime == 1700 || nowtime == 1800	|| nowtime == 1900 || nowtime == 2000 				
					||nowtime == 2100 || nowtime ==2200	|| nowtime == 2300 || nowtime == 0 ){
				boolean power = ShopNpcSystem.getInstance().isPower();
			    if (power) {
			    	ShopNpcTable.reloding();
			    	for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
						//pc.sendPackets(new S_SystemMessage("메티스 시장상점 품목이 갱신 되었습니다.1시간 단위 자동갱신"));//녹색으로 멘트좀요
						//L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("시장이 초기화 되었습니다.1시간 단위 초기화"));
			    	}
			    }
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "BuffTimeController_shopReload() 에서 에러발생", e);
		}
		
	}

	private void radeExciting() {
		try {
			if(nowtime == 900){//9시면 수치가멀가요아하  현실에서 1초가 리니지에선59초
				
				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					pc.setGdungeonTime(7200000);//2시간
					pc.sendPackets(new S_SystemMessage("기감시간이 초기화 되었습니다.매일오전9시 초기화"));//녹색으로 멘트좀요
					//L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("기란던전 시간이 초기화 되었습니다.매일오전9시 초기화"));			
				}
			
				Connection con = null;
				PreparedStatement pstm = null;
				try {
					con = L1DatabaseFactory.getInstance().getConnection();
					pstm = con.prepareStatement("UPDATE characters SET GdungeonTime = 7200000");
					pstm.execute();
				} catch (SQLException e) {
					System.out.println("업데이트에 실패했다!!");
				} finally {
					SQLUtil.close(pstm);
					SQLUtil.close(con);
				}
				
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "BuffTimeController_radeExciting() 에서 에러발생", e);
		}
	}

	private Calendar getRealTime() {
		TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(_tz);
		return cal;
	}
}
