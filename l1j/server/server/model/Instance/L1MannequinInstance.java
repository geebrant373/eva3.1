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
package l1j.server.server.model.Instance;

import java.util.HashMap;
import java.util.Map;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.serverpackets.S_AttackMissPacket;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.templates.L1Npc;

public class L1MannequinInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;
	
	
	private final Map<Integer, L1DollInstance> _dolllist = new HashMap<Integer, L1DollInstance>();

	public void addDoll(L1DollInstance doll) {
		_dolllist.put(doll.getId(), doll);
	}

	public void removeDoll(L1DollInstance doll) {
		_dolllist.remove(doll.getId());
	}

	/**
	 * @param template
	 */
	public L1MannequinInstance(L1Npc template) {
		super(template);
	}
	
	@Override
	public void onAction(L1PcInstance pc) {
		pc.sendPackets(new S_AttackMissPacket(pc, getId()));
		Broadcaster.broadcastPacket(pc, new S_AttackMissPacket(pc, getId()));
	}
	
	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.getNearObjects().addKnownObject(this);
		perceivedFrom.sendPackets(new S_NPCPack(this));
	}
	
	//public Map<Integer, L1DollInstance> getDollList() { return _dolllist; 	\
	

}
