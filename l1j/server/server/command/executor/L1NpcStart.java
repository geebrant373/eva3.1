/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.GameSystem.MannequinSystem;
import l1j.server.GameSystem.ShopNpcSystem;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1NpcStart implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1NpcStart.class.getName());

	private L1NpcStart() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1NpcStart();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try{

			StringTokenizer st = new StringTokenizer(arg);
			String param = st.nextToken();

			if (param.equalsIgnoreCase("무인")) {
				boolean power = ShopNpcSystem.getInstance().isPower();
				if(power) {
					pc.sendPackets(new S_SystemMessage("\\fW이미 실행중 입니다."));
				} else {
					ShopNpcSystem.getInstance().npcShopStart();
					pc.sendPackets(new S_SystemMessage("\\fWNPC 상점 스폰을 시작합니다."));
				}
			} else if(param.equalsIgnoreCase("마네킹")) {
				boolean power = MannequinSystem.getInstance().isPower();
				if(power) {
					pc.sendPackets(new S_SystemMessage("\\fY이미 실행중 입니다."));
				} else {
					MannequinSystem.getInstance().MannequinStart();
					pc.sendPackets(new S_SystemMessage("\\fY마네킹 스폰을 시작합니다."));
				}
			}
		}catch(Exception e){
			pc.sendPackets(new S_SystemMessage(".시작 [무인 or 마네킹] 를 입력해주세요."));
		}
	}
}

