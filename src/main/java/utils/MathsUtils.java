package utils;

public final class MathsUtils {

	private MathsUtils() {
	}

	/**
	 * Linear interpolation between x0 and x1 with factor p (in [0, 1])
	 */
	public static float lerp(float x0, float x1, float p) {
		return x0 + p * (x1 - x0);
	}
}
