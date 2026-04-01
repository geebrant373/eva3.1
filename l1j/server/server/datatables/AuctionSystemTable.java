package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.utils.SQLUtil;

//TODO 중개 거래 게시판
public class AuctionSystemTable {

	private static Logger _log = Logger.getLogger(AuctionSystemTable.class.getName());

	private volatile static AuctionSystemTable _instance;

	private AuctionSystemTable() {
	}

	public static synchronized AuctionSystemTable getInstance() {

		if (_instance == null) {
			_instance = new AuctionSystemTable();
		}
		return _instance;
	}

	public void writeTopic(L1PcInstance player, String selltype, L1ItemInstance item, int count, int sellcount, String bank, String banknumber, String bankname) {
		int counts = 0;

		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM Auction ORDER BY id DESC");
			rs = pstm1.executeQuery();
			SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
			TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
			String date = sdf.format(Calendar.getInstance(tz).getTime());
			if (rs.next()) {
				counts = rs.getInt("id");
			}

			pstm2 = con.prepareStatement(
					"INSERT INTO Auction SET id=?, name=?, item_id=?, item_name=?, count=?, sellcount=?, AccountName=?, selltype=?, status=?, bank=?, banknumber=?, bankname=?, date=?");
			pstm2.setInt(1, (counts + 1));
			pstm2.setString(2, player.getName());
			pstm2.setInt(3, item.getItemId());
			pstm2.setString(4, item.getName());
			pstm2.setInt(5, count);
			pstm2.setInt(6, sellcount);
			pstm2.setString(7, player.getAccountName());
			pstm2.setString(8, selltype);
			pstm2.setInt(9, 0);
			pstm2.setString(10, bank);
			pstm2.setString(11, banknumber);
			pstm2.setString(12, bankname);
			pstm2.setString(13, date);
			pstm2.execute();

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}
	
	public void AuctionUpdate(int number, String buyer, String buyeraccount, String sellstatus, int status) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(
					"UPDATE Auction SET selltype=?, status=?, buyername=?, buyerAccountName=? WHERE id=?");
			pstm.setString(1, sellstatus);
			pstm.setInt(2, status);
			pstm.setString(3, buyer);
			pstm.setString(4, buyeraccount);
			pstm.setInt(5, number);
			pstm.execute();

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public void AuctionComplete(int number) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String status = "판매완료";
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(
					"UPDATE Auction SET selltype=?, status=? WHERE id=?");
			pstm.setString(1, status);
			pstm.setInt(2, 2);
			pstm.setInt(3, number);
			pstm.execute();

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public void deleteTopic(String number) {
		StringTokenizer st = new StringTokenizer(number);
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int id = 0;
			id = Integer.parseInt(st.nextToken());
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM Auction WHERE id=?");
			pstm.setInt(1, id);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void deleteTopic(int number) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM Auction WHERE id=?");
			pstm.setInt(1, number);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	/**구버전에 편지 시스템에 맞게 추가 by 명월이*/
	public void writeLetterTrade(int itemObjectId, int code, String sender,
			String receiver, String date, int templateId, String subject,
			String content) {

		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM auction_letter ORDER BY item_object_id");
			rs = pstm1.executeQuery();
			pstm2 = con
			.prepareStatement("INSERT INTO auction_letter SET item_object_id=?, code=?, sender=?, receiver=?, date=?, template_id=?, subject=?, content=?");
			pstm2.setInt(1, itemObjectId);
			pstm2.setInt(2, code);
			pstm2.setString(3, sender);
			pstm2.setString(4, receiver);
			pstm2.setString(5, date);
			pstm2.setInt(6, templateId);
			pstm2.setString(7, subject);
			pstm2.setString(8, content);
			pstm2.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}
	
	public boolean writeLetter(int letterCode, String letterSender, String letterReceiver, String subject, String Content) {
		L1ItemInstance item = ItemTable.getInstance().createItem(430027);
		item.setCount(1);
		if (sendLetter(letterReceiver, item, true)) {
				saveLetter(item.getId(), letterCode, letterSender, letterReceiver, subject, Content);
		} else {
			return false;
		}
		return true;
	}
	
	public boolean sendLetter(String name, L1ItemInstance item, boolean isFailureMessage) {
		L1PcInstance target = L1World.getInstance().getPlayer(name);
		if (target != null) {
				target.getInventory().storeItem(item);
				target.sendPackets(new S_SkillSound(target.getId(), 1091));
				target.sendPackets(new S_ServerMessage(428));
		} else {
			if (CharacterTable.doesCharNameExist(name)) {
				try {
					int targetId = CharacterTable.getInstance().restoreCharacter(name).getId();
					CharactersItemStorage storage = CharactersItemStorage.create();
						storage.storeItem(targetId, item);
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			} else {
				return false;
			}
		}
		return true;
	}
	
	public void saveLetter(int itemObjectId, int code, String sender, String receiver, String subject, String content) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		String date = sdf.format(Calendar.getInstance(tz).getTime());
		writeLetterTrade(itemObjectId, code, sender, receiver, date, 0, subject, content);
	}
	
	public void deleteLetter(int itemObjectId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM auction_letter WHERE item_object_id=?");
			pstm.setInt(1, itemObjectId);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	/**구버전에 편지 시스템에 맞게 추가 by 명월이*/
}