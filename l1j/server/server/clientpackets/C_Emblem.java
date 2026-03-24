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

import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


import server.LineageClient;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Emblem;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_SystemMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Emblem extends ClientBasePacket {

	private static final String C_EMBLEM = "[C] C_Emblem";
	private static Logger _log = Logger.getLogger(C_Emblem.class.getName());

	public C_Emblem(byte abyte0[], LineageClient clientthread)
			throws Exception {
		super(abyte0);

		L1PcInstance player = clientthread.getActiveChar();
		if (player.getClanid() != 0) {
			int newEmblemId = ObjectIdFactory.getInstance().nextId();
			String emblem_file = String.valueOf(newEmblemId);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream("emblem/" + emblem_file);
				for (short cnt = 0; cnt < 384; cnt++) {
					fos.write(readC());
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				throw e;
			} finally {
				if (null != fos) {
					fos.close();
				}
				fos = null;
			}
			
			L1Clan clan = ClanTable.getInstance().getTemplate(player.getClanid());
			clan.setEmblemId(newEmblemId);
			ClanTable.getInstance().updateClan(clan);
			///player.sendPackets(new S_Emblem(newEmblemId));
			
			//L1World.getInstance().broadcastPacketToAll(new S_Emblem(newEmblemId));
			for(L1PcInstance pc : clan.getOnlineClanMember()){
				//L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(),pc.getMoveState().getHeading(), false);	
				pc.sendPackets(new S_ReturnedStat(pc.getId(), newEmblemId));
				Broadcaster.broadcastPacket(pc, new S_ReturnedStat(pc.getId(), newEmblemId));
				pc.sendPackets(new S_SystemMessage("혈마크가 변경되었습니다. 재접후에 적용됩니다."));
			}
		}
	}

	@Override
	public String getType() {
		return C_EMBLEM;
	}
}
