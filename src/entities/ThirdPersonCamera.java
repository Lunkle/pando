package entities;

import org.lwjgl.input.Mouse;

public class ThirdPersonCamera extends Camera {

	private enum Mode {
		STATIC, FOLLOW
	}

	private float distanceFromEntity = 100;
	private float angleAroundEntity = 0;
	private Mode mode;
	private Entity followEntity;

	public ThirdPersonCamera(Entity targetEntity) {
		followEntity = targetEntity;
	}

	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
	}

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		position.y = followEntity.getPosition().y + verticalDistance;
		float theta = followEntity.getRotY() + angleAroundEntity;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = followEntity.getPosition().x - offsetX;
		position.z = followEntity.getPosition().z - offsetZ;
		yaw = 180 - followEntity.getRotY() - angleAroundEntity;
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromEntity * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromEntity * Math.sin(Math.toRadians(pitch)));
	}

	public void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromEntity -= zoomLevel;
	}

	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			float rawMouseDY = Mouse.getDY();
			float pitchChange = rawMouseDY * 0.1f;
			pitch -= pitchChange;
		}
	}

	private void calculateAngleAroundPlayer() {
		if (Mouse.isButtonDown(0)) {
			float rawMouseDX = Mouse.getDX();
			float angleChange = rawMouseDX * 0.3f;
			angleAroundEntity -= angleChange;
		}
	}

}
