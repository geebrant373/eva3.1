package manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.utils.SystemUtil;

public class LinAllManagerInfoThread implements Runnable {
	public static Long AdenMake = Long.valueOf(0L);
	public static Long AdenConsume = Long.valueOf(0L);
	public static int AdenTax = 0;
	public static float Bugdividend = 0.0F;
	public static int AccountCount = 0;
	public static int CharCount = 0;
	public static int PvPCount = 0;
	public static int PenaltyCount = 0;
	public static int ClanMaker = 0;
	public static int MaxUser = 0;
	public static int count = 0;
	public static NumberFormat nf = NumberFormat.getInstance();
	private static LinAllManagerInfoThread _instance;
	private final int _runTime;

	public static LinAllManagerInfoThread getInstance() {
		if (_instance == null) {
			_instance = new LinAllManagerInfoThread();
			LinAllManager.getInstance().ServerInfoPrint("" + AdenMake, "" + AdenConsume, "" + AdenTax, "" + nf.format(Bugdividend),
					"" + AccountCount, "" + CharCount, "" + PvPCount, "" + PenaltyCount, "" + ClanMaker, "" + MaxUser,
					"" + Thread.activeCount(), "" + SystemUtil.getUsedMemoryMB());
			_instance.start();
		}
		return _instance;
	}

	public LinAllManagerInfoThread() {
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);
		_runTime = 500;
	}

	public void start() {
		GeneralThreadPool.getInstance().scheduleAtFixedRate(_instance, 0L, this._runTime);
	}

	public void run() {
		try {
			if (++count >= 120) {
				count = 0;
				save();
			} else {
				LinAllManager.getInstance().progressBarPrint(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		LinAllManager.getInstance().ServerInfoPrint("" + AdenMake, "" + AdenConsume, "" + AdenTax, "" + nf.format(Bugdividend),
				"" + AccountCount, "" + CharCount, "" + PvPCount, "" + PenaltyCount, "" + ClanMaker, "" + MaxUser,
				"" + Thread.activeCount(), "" + SystemUtil.getUsedMemoryMB());
	}

	public static String getDate() {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		return localSimpleDateFormat.format(Calendar.getInstance().getTime());
	}
	
	public synchronized void ServerInfoUPDATE() {
	    try (
	        Connection con = L1DatabaseFactory.getInstance().getConnection();
	    ) {
	        int i = 0;
	        // Check if the record exists
	        try (PreparedStatement pstm = con.prepareStatement("SELECT count(*) as cnt FROM serverinfo WHERE id = ?")) {
	            pstm.setString(1, getDate());
	            try (ResultSet rs = pstm.executeQuery()) {
	                if (rs.next()) {
	                    i = rs.getInt("cnt");
	                }
	            }
	        }

	        if (i == 0) {
	            // Insert new record
	            try (PreparedStatement pstm = con.prepareStatement(
	                "INSERT INTO serverinfo (adenmake, adenconsume, adentax, bugdividend, accountcount, charcount, pvpcount, penaltycount, clanmaker, maxuser, id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
	                
	                pstm.setLong(1, AdenMake);
	                pstm.setLong(2, AdenConsume);
	                pstm.setInt(3, AdenTax);
	                pstm.setFloat(4, Bugdividend);
	                pstm.setInt(5, AccountCount);
	                pstm.setInt(6, CharCount);
	                pstm.setInt(7, PvPCount);
	                pstm.setInt(8, PenaltyCount);
	                pstm.setInt(9, ClanMaker);
	                pstm.setInt(10, MaxUser);
	                pstm.setString(11, getDate());
	                pstm.executeUpdate();
	            }
	        } else {
	            // Update existing record
	            try (PreparedStatement pstm = con.prepareStatement(
	                "UPDATE serverinfo SET adenmake = ?, adenconsume = ?, adentax = ?, bugdividend = ?, accountcount = ?, charcount = ?, pvpcount = ?, penaltycount = ?, clanmaker = ?, maxuser = ? WHERE id = ?")) {
	                
	                pstm.setLong(1, AdenMake);
	                pstm.setLong(2, AdenConsume);
	                pstm.setInt(3, AdenTax);
	                pstm.setFloat(4, Bugdividend);
	                pstm.setInt(5, AccountCount);
	                pstm.setInt(6, CharCount);
	                pstm.setInt(7, PvPCount);
	                pstm.setInt(8, PenaltyCount);
	                pstm.setInt(9, ClanMaker);
	                pstm.setInt(10, MaxUser);
	                pstm.setString(11, getDate());
	                pstm.executeUpdate();
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void ServerInfoLoad() {
	    try (
	        Connection con = L1DatabaseFactory.getInstance().getConnection();
	        PreparedStatement pstm = con.prepareStatement("SELECT * FROM serverinfo WHERE id=?");
	    ) {
	        pstm.setString(1, getDate());
	        try (ResultSet rs = pstm.executeQuery()) {
	            if (!rs.next())
	                return;
	            AdenMake = Long.valueOf(rs.getLong("adenmake"));
	            AdenConsume = Long.valueOf(rs.getLong("adenconsume"));
	            AdenTax = rs.getInt("adentax");
	            Bugdividend = rs.getInt("bugdividend");
	            AccountCount = rs.getInt("accountcount");
	            CharCount = rs.getInt("charcount");
	            PvPCount = rs.getInt("pvpcount");
	            PenaltyCount = rs.getInt("penaltycount");
	            ClanMaker = rs.getInt("clanmaker");
	            MaxUser = rs.getInt("maxuser");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
}