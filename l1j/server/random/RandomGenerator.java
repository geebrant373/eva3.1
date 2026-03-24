package l1j.server.random;

public abstract interface RandomGenerator {
	public abstract int nextInt();

	public abstract int nextInt(int paramInt);

	public abstract int range(int paramInt1, int paramInt2);

	public abstract int choice(int[] paramArrayOfInt);

	public abstract boolean isChance(int paramInt);

	public abstract double nextDouble(double paramDouble);

	public abstract boolean isChance(double paramDouble);
}