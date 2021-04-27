package rendering;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import params.ParameterLoader;
import params.Parameters;
import utils.VectorUtils;

@Getter
public class Camera {

	private static final Vector3f GLOBAL_UP = new Vector3f(0.0f, 1.0f, 0.0f);
	private static final float MOVEMENT_SPEED = 7.0f;
	private static final float MOUSE_SENSITIVITY = 0.1f;
	private static final float MAX_PITCH = 89.0f;
	private final Parameters parameters = ParameterLoader.getParameters();
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

	public Camera(final Vector3f position, final Vector3f direction) {
		this.direction = direction.normalize();
		this.pitch = (float) Math.toDegrees(Math.asin(this.direction.y));
		this.yaw = (float) Math.toDegrees(Math.atan2(this.direction.z, this.direction.x));
		this.position = position;
		this.front = new Vector3f(this.direction.x, 0.0f, this.direction.z);
		updateVectorsFromAngles();
	}

	private void updateVectorsFromAngles() {
		this.direction.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
		this.direction.y = (float) Math.sin(Math.toRadians(pitch));
		this.direction.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
		this.direction.normalize();
		this.front = new Vector3f(direction.x, 0.0f, direction.z);
		this.front.normalize();
		this.right = VectorUtils.cross(front, GLOBAL_UP).normalize();
		this.up = VectorUtils.cross(right, front).normalize();
	}

	public Matrix4f getViewMatrix() {
		Vector3f target = VectorUtils.add(position, direction);
		return (new Matrix4f()).lookAt(position, target, up);
	}

	public void move(final MovementDirection direction, final float deltaTime) {

		final float velocity = MOVEMENT_SPEED * deltaTime;

		switch (direction) {
			case FORWARD:
				position = VectorUtils.add(position, VectorUtils.multiply(velocity, front));
				break;
			case BACKWARD:
				position = VectorUtils.subtract(position, VectorUtils.multiply(velocity, front));
				break;
			case LEFT:
				position = VectorUtils.subtract(position, VectorUtils.multiply(velocity, right));
				break;
			case RIGHT:
				position = VectorUtils.add(position, VectorUtils.multiply(velocity, right));
				break;
			case UP:
				if (parameters.camera.verticalMovement) {
					position = VectorUtils.add(position, VectorUtils.multiply(velocity, up));
				}
				break;
			case DOWN:
				if (parameters.camera.verticalMovement) {
					position = VectorUtils.subtract(position, VectorUtils.multiply(velocity, up));
				}
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

	public enum MovementDirection {
		FORWARD,
		BACKWARD,
		LEFT,
		RIGHT,
		UP,
		DOWN
	}

}
