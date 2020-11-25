import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera {

	enum MovementDirection {
		FORWARD,
		BACKWARD,
		LEFT,
		RIGHT,
		UP,
		DOWN
	}
	private static final Vector3f GLOBAL_UP = new Vector3f(0.0f, 1.0f, 0.0f);
	private static final float MOVEMENT_SPEED = 7.0f;
	private static final float MOUSE_SENSITIVITY = 0.1f;
	private static final float MAX_PITCH = 89.0f;

	private float yaw;
	private float pitch;

	private Vector3f position;
	private Vector3f direction;
	private Vector3f front;
	private Vector3f right;
	private Vector3f up;

	public Camera(final Vector3f position, final float yaw, final float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.position = position;
		this.front = new Vector3f();
		this.direction = new Vector3f();
		updateVectorsFromAngles();
	}

	private void updateVectorsFromAngles() {
		this.direction.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
		this.direction.y = (float) Math.sin(Math.toRadians(pitch));
		this.direction.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
		this.direction.normalize();
		this.front = new Vector3f(direction.x, 0.0f, direction.z);
		this.front.normalize();
		this.right = MathUtils.cross(front, GLOBAL_UP).normalize();
		this.up = MathUtils.cross(right, front).normalize();
	}

	public Matrix4f getViewMatrix() {
		Vector3f target = MathUtils.add(position, direction);
		return (new Matrix4f()).lookAt(position, target, up);
	}

	public void move(final MovementDirection direction, final float deltaTime) {

		final float velocity = MOVEMENT_SPEED * deltaTime;

		switch (direction) {
			case FORWARD:
				position = MathUtils.add(position, MathUtils.multiply(velocity, front));
				break;
			case BACKWARD:
				position = MathUtils.subtract(position, MathUtils.multiply(velocity, front));
				break;
			case LEFT:
				position = MathUtils.subtract(position, MathUtils.multiply(velocity, right));
				break;
			case RIGHT:
				position = MathUtils.add(position, MathUtils.multiply(velocity, right));
				break;
			case UP:
				position = MathUtils.add(position, MathUtils.multiply(velocity, up));
				break;
			case DOWN:
				position = MathUtils.subtract(position, MathUtils.multiply(velocity, up));
				break;
			default:
		}
	}

	public void processMouseMovement(final float xOffset, final float yOffset) {
		this.yaw += xOffset * MOUSE_SENSITIVITY;
		this.pitch += yOffset * MOUSE_SENSITIVITY;

		if (pitch > MAX_PITCH) {
			pitch = MAX_PITCH;
		} else if (pitch < -MAX_PITCH) {
			pitch = -MAX_PITCH;
		}

		updateVectorsFromAngles();
	}

	public Vector3f getPosition() {
		return position;
	}

	private static final class MathUtils {
		private MathUtils() { }
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
}
