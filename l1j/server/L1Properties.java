package l1j.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class L1Properties {
	private static Logger _log = Logger.getLogger(L1Properties.class.getName());
	private final Properties _props = new Properties();

	private L1Properties() {
	}

	private L1Properties(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		if (file.getPath().endsWith(".xml")) {
			this._props.loadFromXML(is);
		} else {
			this._props.load(is);
		}
		is.close();
	}

	public static L1Properties load(File file) {
		_log.log(Level.FINE, "Loading properties file: {0}", file.getName());
		try {
			return new L1Properties(file);
		} catch (IOException e) {
			return new NullProperties(e);
		}
	}

	private void notifyLoadingDefault(String key, boolean allowDefaultValue) {
		if (!allowDefaultValue) {
			throw new RuntimeException(key + " does not exists. It has not default value.");
		}

		_log.log(Level.INFO, String.format("%s does not exists. Server use default value.", new Object[] { key }));
	}

	public String getProperty(String key, boolean allowDefaultValue) {
		if (!this._props.containsKey(key)) {
			notifyLoadingDefault(key, allowDefaultValue);
			return null;
		}
		return this._props.getProperty(key);
	}

	public boolean isNull() {
		return false;
	}

	public IOException getException() {
		throw new UnsupportedOperationException();
	}

	private static class NullProperties extends L1Properties {
		private IOException _e;

		public NullProperties(IOException e) {
			super();
			this._e = e;
		}

		public String getProperty(String key, boolean allowDefaultValue) {
			return null;
		}

		public boolean isNull() {
			return true;
		}

		public IOException getException() {
			return this._e;
		}
	}
}