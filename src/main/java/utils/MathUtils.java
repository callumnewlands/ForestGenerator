package utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

public final class MathUtils {
	private MathUtils() {
	}

	public static Vector3f add(final Vector3f vec1, final Vector3f vec2) {
		return (new Vector3f(vec1)).add(vec2);
	}

	public static Vector3f subtract(final Vector3f vec1, final Vector3f vec2) {
		return (new Vector3f(vec1)).sub(vec2);
	}

	public static Vector3f multiply(final float coefficient, final Vector3f vec) {
		return (new Vector3f(vec)).mul(coefficient);
	}

	public static Vector4f multiply(final float coefficient, final Vector4f vec) {
		return (new Vector4f(vec)).mul(coefficient);
	}

	public static Vector3f cross(final Vector3f vec1, final Vector3f vec2) {
		return (new Vector3f(vec1)).cross(vec2);
	}

}
