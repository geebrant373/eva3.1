package l1j.server;

import java.util.ArrayList;

class CustomLoaders {
	static abstract interface CustomLoader {
		public abstract Object loadValue(Annotations.Configure paramConfigure, Class<?> paramClass, String paramString);
	}

	static class DefaultLoader implements CustomLoaders.CustomLoader {
		public Object loadValue(Annotations.Configure config, Class<?> type, String value) {
			Object result = null;

			if (type.equals(Integer.TYPE)) {
				result = Integer.valueOf(Integer.parseInt(value));
			} else if (type.equals(Long.TYPE)) {
				result = Long.valueOf(Long.parseLong(value));
			} else if (type.equals(String.class)) {
				result = value;
			} else if (type.equals(Boolean.TYPE)) {
				result = Boolean.valueOf(Boolean.parseBoolean(value));
			} else if (type.equals(Float.TYPE)) {
				result = Float.valueOf(Float.parseFloat(value));
			} else if (type.equals(Double.TYPE)) {
				result = Double.valueOf(Double.parseDouble(value));
			} else if (type.equals(int[].class)) {
				if (value.isEmpty()) {
					return null;
				}
				String[] valueArray = value.replace("/", ",").split(",");
				int[] intArray = new int[valueArray.length];
				for (int i = 0; i < valueArray.length; i++) {
					intArray[i] = Integer.valueOf(valueArray[i]).intValue();
				}
				result = intArray;
			} else if (type.equals(long[].class)) {
				if (value.isEmpty()) {
					return null;
				}
				String[] valueArray = value.replace("/", ",").split(",");
				long[] longArray = new long[valueArray.length];
				for (int i = 0; i < valueArray.length; i++) {
					longArray[i] = Integer.valueOf(valueArray[i]).intValue();
				}
				result = longArray;
			} else if (type.equals(float[].class)) {
				if (value.isEmpty()) {
					return null;
				}
				String[] valueArray = value.replace("/", ",").split(",");
				float[] floatArray = new float[valueArray.length];
				for (int i = 0; i < valueArray.length; i++) {
					floatArray[i] = Float.valueOf(valueArray[i]).floatValue();
				}
				result = floatArray;
			} else if (type.equals(double[].class)) {
				if (value.isEmpty()) {
					return null;
				}
				String[] valueArray = value.replace("/", ",").split(",");
				double[] doubleArray = new double[valueArray.length];
				for (int i = 0; i < valueArray.length; i++) {
					doubleArray[i] = Double.valueOf(valueArray[i]).doubleValue();
				}
				result = doubleArray;
			} else if (type.equals(String[].class)) {
				if (value.isEmpty()) {
					return null;
				}
				result = value.replace("/", ",").split(",");
			} else if (type.equals(boolean[].class)) {
				if (value.isEmpty()) {
					return null;
				}
				String[] valueArray = value.replace("/", ",").split(",");
				boolean[] booleanArray = new boolean[valueArray.length];
				for (int i = 0; i < valueArray.length; i++) {
					booleanArray[i] = Boolean.valueOf(valueArray[i]).booleanValue();
				}
				result = booleanArray;
			}

			return result;
		}
	}

	static class WarTimeLoader implements CustomLoaders.CustomLoader {
		public Object loadValue(Annotations.Configure config, Class<?> type, String value) {
			String time = value.replaceFirst("^(\\d+)d|h|m$", "$1");
			return Integer.valueOf(Integer.parseInt(time));
		}
	}

	static class WarTimeUnitLoader implements CustomLoaders.CustomLoader {
		public Object loadValue(Annotations.Configure config, Class<?> type, String value) {
			String unit = value.replaceFirst("^\\d+(d|h|m)$", "$1");
			if (unit.equals("d")) {
				return Integer.valueOf(5);
			}
			if (unit.equals("h")) {
				return Integer.valueOf(11);
			}
			if (unit.equals("m")) {
				return Integer.valueOf(12);
			}
			throw new IllegalArgumentException();
		}
	}

	static class IntArrayListLoader implements CustomLoaders.CustomLoader {
		public ArrayList<Integer> loadValue(Annotations.Configure config, Class<?> type, String value) {
			if (value.isEmpty()) {
				return null;
			}
			String[] valueArray = value.replace("/", ",").split(",");
			ArrayList<Integer> array = new ArrayList<Integer>();
			for (int i = 0; i < valueArray.length; i++) {
				array.add(Integer.valueOf(valueArray[i]));
			}

			return array;
		}
	}

	static class IntArrayLoader implements CustomLoaders.CustomLoader {
		public Object loadValue(Annotations.Configure config, Class<?> type, String value) {
			if (value.isEmpty()) {
				return null;
			}
			String[] valueArray = value.replace("/", ",").split(" ");
			int[] intArray = new int[valueArray.length];
			for (int i = 0; i < valueArray.length; i++) {
				intArray[i] = Integer.valueOf(valueArray[i].split("]")[1]).intValue();
			}

			return intArray;
		}
	}

	static class FloatArrayLoader implements CustomLoaders.CustomLoader {
		public Object loadValue(Annotations.Configure config, Class<?> type, String value) {
			if (value.isEmpty()) {
				return null;
			}
			String[] valueArray = value.replace("/", ",").split(" ");
			float[] floatArray = new float[valueArray.length];
			for (int i = 0; i < valueArray.length; i++) {
				floatArray[i] = Float.valueOf(valueArray[i].split("]")[1]).floatValue();
			}

			return floatArray;
		}
	}
}