/*
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.util.StringTokenizer;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CheckRanking;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1CheckSearch implements L1CommandExecutor {
	private L1CheckSearch() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1CheckSearch();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		StringTokenizer st = new StringTokenizer(arg);
		String charname = st.nextToken();
		String type = st.nextToken();
		L1PcInstance target = L1World.getInstance().getPlayer(charname);
		try {
			if (type.equalsIgnoreCase("인벤")) {
				target.sendPackets(new S_CheckRanking(target, 1));
			} else if (type.equalsIgnoreCase("창고")) {
				target.sendPackets(new S_CheckRanking(target, 2));
			}
		} catch (Exception e) {
			target.sendPackets(new S_SystemMessage(".수표조회 [캐릭명] [인벤,창고]"));
		}
	}
}
