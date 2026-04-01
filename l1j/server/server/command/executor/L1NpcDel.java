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
import l1j.server.server.GameServerSetting;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1MannequinInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1ShopNpcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1NpcDel implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1NpcDel.class.getName());

	private L1NpcDel() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1NpcDel();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try{
			L1MannequinInstance npc = null;
			L1ShopNpcInstance ShopNpc = null;
			StringTokenizer st = new StringTokenizer(arg);
			String param = st.nextToken();
			String name = st.nextToken();
			GameServerSetting num = GameServerSetting.getInstance();

			if (param.equalsIgnoreCase("무인")) {
				ShopNpc = ShopNpcSystem.getInstance().getShopNpc(name);
				if (ShopNpc != null){
					ShopNpc.deleteMe();
					num.set_fakePlayerNum(num.get_fakePlayerNum() -1);
					pc.sendPackets(new S_SystemMessage("무인NPC " +ShopNpc.getNameId()+" 삭제 되었습니다."));
				} else {
					pc.sendPackets(new S_SystemMessage("존재 하지 않는 무인NPC 이름입니다."));
				}
			} else if(param.equalsIgnoreCase("마네킹")) {
				npc = MannequinSystem.getInstance().getMannequin(name);
				if (npc != null){
					npc.deleteMe();

					for (L1DollInstance doll : npc.getDollList().values()) {
						if(doll == null)
							continue;
						doll.deleteMe();
					}

					num.set_fakePlayerNum(num.get_fakePlayerNum() -1);
					pc.sendPackets(new S_SystemMessage("마네킹 "+npc.getNameId()+" 삭제 되었습니다."));
				} else {
					pc.sendPackets(new S_SystemMessage("존재 하지 않는 마네킹NPC 이름입니다."));
				}
			}
		}catch(Exception e){
			pc.sendPackets(new S_SystemMessage(cmdName+" [무인 or 마네킹] [NPC이름] 를 입력해주세요."));
		}
	}
}