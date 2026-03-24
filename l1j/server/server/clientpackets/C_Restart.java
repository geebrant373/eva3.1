/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import server.LineageClient;
import server.system.autoshop.AutoShop;
import server.system.autoshop.AutoShopManager;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.TimeController.FishingTimeController;
import l1j.server.server.TimeController.LiveTimeController;
import l1j.server.server.datatables.BoardTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class C_Restart extends ClientBasePacket {
	private static final String C_OPCODE_RESTART = "[C] C_Restart";
	private static Logger _log = Logger.getLogger(C_Restart.class.getName());

	public C_Restart(byte[] decrypt, LineageClient client) throws Exception {
		super(decrypt);
		client.CharReStart(true);
		client.sendPacket(new S_PacketBox(S_PacketBox.LOGOUT));

		if (client.getActiveChar() != null) {
			L1PcInstance pc = client.getActiveChar();
			LiveTimeController.getInstance().removeMember(pc); // 생존의외침 리스트 삭제
			FishingTimeController.getInstance().removeMember(pc);
			FishingTimeController.getInstance().endFishing(pc);
			if (!pc.isAdenaTrade()) {
        		pc.setAdenaSellCount(0);
        	}
			pc.setadFeature(1);
			pc.save();
			pc.saveInventory();
			if (pc.isFishingReady()) {
				FishingTimeController.getInstance().endFishing(pc);
			}
			if (pc.getAdenaSellCount() > 0) {
				CancelAdenaSell(pc, pc.getAdenaSellCount());
			}
			if(pc.isPrivateShop()){
				synchronized (pc) {
					AutoShopManager shopManager = AutoShopManager.getInstance();
					AutoShop autoshop = shopManager.makeAutoShop(pc);
					shopManager.register(autoshop);
					client.setActiveChar(null);
				}
			}else{
				_log.fine("Disconnect from: " + pc.getName());
				synchronized (pc) {
					client.quitGame(pc);
					pc.logout();
					pc.noPlayerCK = true;
					client.setActiveChar(null);
					//client.close();
				}
			}
		} else {
			_log.fine("Disconnect Request from Account : " + client.getAccountName());
		}
	}// 상점리스 뎅두배버그수정위치0326
	  private void CancelAdenaSell(L1PcInstance pc, int id) {
			// TODO Auto-generated method stub
			String SellerName = null;
			String BidderName = null;
			String title = null;
			String coment = null;
			int adena = 0;
			Connection con = null;
			PreparedStatement pstm1 = null;
			ResultSet rs = null;
			PreparedStatement pstm2 = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm1 = con.prepareStatement("SELECT * FROM board_adena WHERE type=? AND id=?");
				pstm1.setString(1, "adena");
				pstm1.setInt(2, id);
				rs = pstm1.executeQuery();
				
				if (rs.next()) {
					title = rs.getString("title");
					SellerName = rs.getString("name");
					BidderName = rs.getString("bidder");
					coment  = rs.getString("coment");
					adena = rs.getInt("adena");
				}
				if (title.equalsIgnoreCase("거래중")) {
					L1PcInstance target = L1World.getInstance().getPlayer(BidderName);
					if (target != null) {
						target.sendPackets(new S_Message_YN(622, "상대방이 판매 취소를 원합니다 동의 하시겠습니까?"));
						target.setAttrMsgType(1);
						pc.sendPackets(new S_SystemMessage("판매 취소: 상대방의 동의를 얻고 있습니다. "));
					} else {
						pc.sendPackets(new S_SystemMessage("판매 취소: 구매자가 접속중이지 않습니다. "));
					}
					return;
				}
				BoardTable.getInstance().deleteAdena(pc.getAdenaSellCount());
				pc.getInventory().storeItem(40308, adena);
				pc.sendPackets(new S_SystemMessage("판매 취소: 등록하신 물품이 취소 되었습니다."));
				pc.setAdenaSellCount(0);
				pstm2 = con.prepareStatement("UPDATE board_adena SET bidder=?, title=?, step=? WHERE id=?");
				pstm2.setString(1, BidderName);
				pstm2.setString(2, "판매취소");
				pstm2.setInt(3, 3);
				pstm2.setInt(4, id);
				pstm2.executeUpdate();
				pstm2.close();
			} catch (SQLException e) {
				pc.sendPackets(new S_SystemMessage(".구매신청 [게시물 번호] 게시물 번호가 0001이면 1만 입력."));
			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm1);
				SQLUtil.close(pstm2);
				SQLUtil.close(con);
			}
		}
	@Override
	public String getType() {
		return C_OPCODE_RESTART;
	}
}
