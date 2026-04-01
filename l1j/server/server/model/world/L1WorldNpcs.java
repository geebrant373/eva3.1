package l1j.server.server.model.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1NpcInstance;



public class L1WorldNpcs {
	private static Logger _log = Logger.getLogger(L1WorldNpcs.class.getName());

	private static L1WorldNpcs _instance;

	private final ConcurrentHashMap<Integer, L1NpcInstance> _isNpc;
	private Collection<L1NpcInstance> _allNpcValues;

	public static L1WorldNpcs getInstance() {
		if (_instance == null) {
			_instance = new L1WorldNpcs();
		}
		return _instance;
	}

	private L1WorldNpcs() {
		this._isNpc = new ConcurrentHashMap();
	}

	public Collection<L1NpcInstance> getAll() {
		try {
			Collection<L1NpcInstance> vs = this._allNpcValues;
			return vs != null ? vs : (this._allNpcValues = Collections.unmodifiableCollection(this._isNpc.values()));
		} catch (Exception e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
		return null;
	}

	public L1NpcInstance findObject(int id) {
		return (L1NpcInstance) this._isNpc.get(Integer.valueOf(id));
	}

	public ConcurrentHashMap<Integer, L1NpcInstance> map() {
		return this._isNpc;
	}

	public ArrayList<L1NpcInstance> getNpcInMap(int mapId) {
		ArrayList<L1NpcInstance> result = new ArrayList();

		for (L1NpcInstance element : getAll()) {
			if (mapId == element.getMapId()) {
				result.add(element);
			}
		}
		return result;
	}

	public void put(int key, L1NpcInstance value) {
		try {
			this._isNpc.put(Integer.valueOf(key), value);
		} catch (Exception e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void remove(int key) {
		try {
			L1NpcInstance npc = (L1NpcInstance) this._isNpc.get(Integer.valueOf(key));
			if (npc != null) {
				this._isNpc.remove(Integer.valueOf(key));
			}
		} catch (Exception e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}
}