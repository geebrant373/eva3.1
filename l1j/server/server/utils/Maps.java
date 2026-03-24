package l1j.server.server.utils;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Maps {
	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap();
	}

	public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
		return new ConcurrentHashMap();
	}
}