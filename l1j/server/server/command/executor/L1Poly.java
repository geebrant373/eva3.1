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

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAttribute;

import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Poly implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Poly.class.getName());
	private static HashMap<String, L1Poly> _dataMap = new HashMap<String, L1Poly>();
	private L1Poly() {
	}
	public static L1Poly get(String name) {
		return (L1Poly) _dataMap.get(name);
	}
	private int getPolyId() {
		return this._poly_id;
	}
	@XmlAttribute(name = "PolyId")
	private int _poly_id;
	public static L1CommandExecutor getInstance() {
		return new L1Poly();
	}
	public boolean execute(L1PcInstance pc, int time) {
		try {
			L1PolyMorph.doPoly(pc, getPolyId(), time, 2);
			return true;
		} catch (Exception e) {
			_log.log(Level.SEVERE, "error gm command", e);
		}
		return false;
	}
	public static String getList() {
		StringBuilder result = new StringBuilder();
		for (String poly : _dataMap.keySet()) {
			if (result.length() > 0) {
				result.append(", ");
			}
			result.append(poly);
		}

		return result.toString();
	}
	@Override
	
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer token = new StringTokenizer(arg);
			String name = token.nextToken();

			if (name.equals("-목록")) {
				pc.sendPackets(new S_SystemMessage(getList()));
				return;
			}
			String poly = null;
			if (token.hasMoreTokens()) {
				poly = token.nextToken();
			}
			int time = 7200;
			if (token.hasMoreTokens()) {
				time = Integer.parseInt(token.nextToken());
			}
			L1PcInstance target = L1World.getInstance(). getPlayer(name);
			
			if (target == null) {
			
			} else if (poly == null) {
				L1PolyMorph.undoPoly(target);
				pc.sendPackets(new S_SystemMessage("<변신> " + name + ": 변신 해제"));
			} else {
				int polyId = 0;
				try {
					polyId = Integer.parseInt(poly);
					L1PolyMorph.doPoly(target, polyId, time, 2);
				} catch (NumberFormatException e) {
					L1Poly gmpoly = get(poly);
					if (gmpoly != null) {
						gmpoly.execute(target, time);
					} else {
						throw new Exception();
					}
				}
				
				pc.sendPackets(new S_SystemMessage( "<변신> " + name + ": " + polyId));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( "." + cmdName + " <캐릭터명> <변신번호/명> <시간(초)>"));
			pc.sendPackets(new S_SystemMessage( "변신명은 GMPoly.xml 참조"));
		}
	}
}
		
	