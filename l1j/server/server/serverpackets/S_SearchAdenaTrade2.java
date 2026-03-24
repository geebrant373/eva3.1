package l1j.server.server.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.GMCommands;
import l1j.server.server.datatables.BoardAdenaTable;
import l1j.server.server.datatables.BoardBuyTable;
import l1j.server.server.datatables.BoardSellTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1BoardAdena;
import l1j.server.server.utils.SQLUtil;

public class S_SearchAdenaTrade2 extends ServerBasePacket {
  private static Logger _log = Logger.getLogger(GMCommands.class.getName());
  
  public S_SearchAdenaTrade2(L1PcInstance pc, int type, int tradenumber) {
    writeC(113);
    writeD(pc.getId());
    writeS("searchtrade2");
    writeC(0);
    if (type == 0) {
      writeH(5);
      L1BoardAdena board = null;
      if (tradenumber > 30000) {
        board = BoardBuyTable.getInstance().getBoardAdena(tradenumber);
      } else if (tradenumber > 20000) {
        board = BoardSellTable.getInstance().getBoardAdena(tradenumber);
      } else {
        board = BoardAdenaTable.getInstance().getBoardAdena(tradenumber);
      } 
      DecimalFormat priceformat = new DecimalFormat("#,###,###,###");
      if (board != null) {
        String boardtype = (board.getType() == 0) ? "판매중": ((board.getType() == 1) ? "거래중": "");
        if (tradenumber > 30000)
          boardtype = (board.getType() == 0) ? "매입중": ((board.getType() == 1) ? "거래중": ""); 
        writeS("현재상태: " + boardtype);
        writeS("물품번호: " + tradenumber);
        writeS("아덴수량: " + priceformat.format(board.getAdenaCount()));
        if (tradenumber > 30000) {
          writeS("매입금액: " + priceformat.format(board.getSellCount()));
        } else {
          writeS("판매금액: " + priceformat.format(board.getSellCount()));
        } 
        if (board.getTradeId() != 0) {
          writeS("구매캐릭: " + board.getTradeName());
        } else {
          writeS("구매캐릭: ");
        } 
      } else {
        writeS("판매중인 물품이 존재 하지 않습니다.!");
        writeS(" ");
        writeS(" ");
        writeS(" ");
        writeS(" ");
      } 
    } else if (type == 1) {
      writeH(9);
      L1BoardAdena board = null;
      if (tradenumber > 30000) {
        board = BoardBuyTable.getInstance().getBoardAdena(tradenumber);
      } else if (tradenumber > 20000) {
        board = BoardSellTable.getInstance().getBoardAdena(tradenumber);
      } else {
        board = BoardAdenaTable.getInstance().getBoardAdena(tradenumber);
      } 
      DecimalFormat priceformat = new DecimalFormat("#,###,###,###");
      if (board != null) {
        String boardtype = (board.getType() == 0) ? "판매중": ((board.getType() == 1) ? "거래중": "");
        writeS("현재상태: " + boardtype);
        writeS("물품번호: " + tradenumber);
        writeS("아덴수량: " + priceformat.format(board.getAdenaCount()));
        if (tradenumber > 30000) {
          writeS("매입금액: " + priceformat.format(board.getSellCount()));
        } else {
          writeS("판매금액: " + priceformat.format(board.getSellCount()));
        } 
        if (tradenumber > 30000) {
          writeS("매입캐릭: " + board.getChaName());
        } else if (board.getChaName() != null) {
          writeS("판매캐릭: " + board.getChaName());
        } else {
          writeS("판매캐릭: 운영자");
        } 
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
          con = L1DatabaseFactory.getInstance().getConnection();
          pstm = con.prepareStatement("SELECT * FROM board_adena_user WHERE objid=?");
          pstm.setInt(1, board.getChaId());
          rs = pstm.executeQuery();
          if (rs.next()) {
            writeS("핸드폰: " + rs.getString("phone_number"));
            writeS("금융기관(은행명): " + rs.getString("bank_name"));
            writeS("계좌번호: " + rs.getString("bank_number"));
            writeS("성함: " + rs.getString("name"));
          } 
        } catch (SQLException e) {
          _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
          SQLUtil.close(rs);
          SQLUtil.close(pstm);
          SQLUtil.close(con);
        } 
      } else {
        writeS("구매중인 물품이 존재하지 않습니다.!");
        writeS(" ");
        writeS(" ");
        writeS(" ");
        writeS(" ");
        writeS(" ");
        writeS(" ");
        writeS(" ");
        writeS(" ");
      } 
    } 
  }
  
  public byte[] getContent() {
    return getBytes();
  }
}
