package l1j.server.random;

public class RandomGeneratorFactory {
	private static RandomGenerator _random = null;

	public static RandomGenerator getSharedRandom() {
		if (_random == null) {
			_random = newRandom();
		}
		return _random;
	}

	public static RandomGenerator newRandom() {
		return new StandardRandom();
	}
}