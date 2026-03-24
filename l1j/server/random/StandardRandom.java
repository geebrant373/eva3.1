package l1j.server.random;

import java.util.Random;

class StandardRandom implements RandomGenerator {
	private Random _random = new Random();

	public int nextInt() {
		return this._random.nextInt();
	}

	public int nextInt(int n) {
		if (n <= 0) {
			return 0;
		}
		return this._random.nextInt(n);
	}

	public int range(int min, int max) {
		return min + this._random.nextInt(max - min + 1);
	}

	public int choice(int[] numbers) {
		int[] list = (int[]) numbers.clone();
		return list[this._random.nextInt(numbers.length)];
	}

	public boolean isChance(int chance) {
		return nextInt(100) < chance;
	}

	public double nextDouble(double value) {
		if (value <= 0.0D) {
			return 0.0D;
		}

		return this._random.nextDouble() * value;
	}

	public boolean isChance(double chance) {
		return nextDouble(100.0D) < chance;
	}
}