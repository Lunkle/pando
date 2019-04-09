package toolbox;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import data.TerrainData;
import data.TerrainData.Direction;
import entities.Camera;
import entities.Player;
import terrains.Terrain;

public class MousePicker {

	private Vector3f currentRay;
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private final float RAY_LENGTH = 500;
	private Camera camera;

	private enum BiasMode {
		VERTICAL, HORIZONTAL
	}

	public static final Direction[][] QUADRANT_DIRECTION_TABLE = new TerrainData.Direction[][] { { TerrainData.Direction.UP_LEFT, TerrainData.Direction.LEFT, TerrainData.Direction.UP_RIGHT }, { TerrainData.Direction.DOWN_LEFT, TerrainData.Direction.RIGHT, TerrainData.Direction.DOWN_RIGHT } };

	public MousePicker(Camera cam, Matrix4f projection) {
		this.camera = cam;
		this.projectionMatrix = projection;
		this.viewMatrix = Maths.createViewMatrix(cam);
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public void update(TerrainData terrain, Player player) {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
		player.setPosition(findCoords(terrain));
	}

	public Vector3f findCoords(TerrainData terrain) {
		Vector3f loc = new Vector3f(camera.getPosition());
		try {
			Vector2f mod = calculateJumpAmount();
			while (terrain.getHeightByWorldCoords(loc.x, loc.z) < loc.y) {
				loc.x += mod.x;
				loc.z += mod.y;
				loc.y += mod.x / currentRay.x * currentRay.y;
			}
		} catch (Exception e) {
//			System.out.println("mouse OOB");
			return camera.getPosition();
		}
		return loc;
	}

	public Vector2f calculateJumpAmount() {
		float angle = calculateRayAngle();

		float relAngle = Math.abs(angle % 30 - 15);
		float distance = Terrain.getHexSide();
		if (relAngle < 15 && relAngle > 0) {
			distance = (float) (Math.cos(Math.toRadians(15 - relAngle)) * Terrain.getHexHalfSqrt3());
		} else if (relAngle > 15) {
			distance = (float) (Math.cos(Math.toRadians(relAngle - 15)) * Terrain.getHexHalfSqrt3());
		} else if (relAngle == 15) {
			distance = Terrain.getHexHalfSqrt3();
		}

		Vector2f jump = new Vector2f(0, 0);

		jump.x = (float) Math.sin(Math.toRadians(angle)) * distance / 2;
		jump.y = (float) Math.cos(Math.toRadians(angle)) * distance / 2;
		return jump;
	}

	public float calculateRayAngle() {
		float angle = (float) (Math.toDegrees(Math.atan(currentRay.x / currentRay.z)) % 360);
		if (Double.isNaN(angle)) {
			if (currentRay.x > 0) {
				return 90f;
			} else if (currentRay.x < 0) {
				return -90f;
			}
		}

		if (currentRay.z < 0) {
			angle += 180;
		}

		return angle;
	}

	public Vector2f getIntersection(Vector2f p1, Vector2f d1, Vector2f p2, Vector2f d2) {
		Vector2f dp = Vector2f.sub(p2, p1, null);
		float a = (d1.x * dp.y - d1.y * dp.x) / (d1.y * d2.x - d1.x * d2.y);
		return new Vector2f(p2.x + a * d2.x, p2.y + a * d2.y);
	}

	public Vector2f getPointerHex(TerrainData terrainData) {
		Vector2f origin = new Vector2f(camera.getPosition().x, camera.getPosition().z);
		Vector2f originHex = terrainData.getHexagon(origin);

		if (terrainData.getHeightByHexCoords((int) originHex.x, (int) originHex.y) < camera.getPosition().y) {
			return originHex;
		}

		Vector3f ray = new Vector3f(currentRay.x, currentRay.y, currentRay.z);
		Vector2f end = new Vector2f(origin.x + RAY_LENGTH * ray.x, origin.y + RAY_LENGTH * ray.z);
		Vector2f endHex = terrainData.getHexagon(end);
		Vector2f currentHex = originHex;
		Vector2f prevHex = null;

		int offset = (int) (originHex.y % 2);
		float tileX = (origin.x - (0.5f * offset + originHex.x) * Terrain.getHexSqrt3());
		float tileZ = (origin.y - 2 * originHex.y * Terrain.getHexSide());

		float rawDeltaX = end.x - origin.x;
		float rawDeltaY = end.y - origin.y;
		Vector2f dVector = new Vector2f(Math.abs(rawDeltaX), Math.abs(rawDeltaY));
		dVector.normalise();
		int signX = (int) Math.signum(rawDeltaX);
		int signY = (int) Math.signum(rawDeltaY);

		BiasMode mode = dVector.y > 2 * dVector.x ? BiasMode.VERTICAL : BiasMode.HORIZONTAL;

		Direction lastDirection;
		Vector2f pickHex = null;
		Vector3f checkPoint = null;
		float yVal;

		if (mode == BiasMode.HORIZONTAL) {
			float epsilon = tileZ - Terrain.getHexHalfSide(); // Backwards 3
			float slope = Math.abs(dVector.y / dVector.x); // k
			float initialXStep = Terrain.getHexSqrt3() - tileX;
			float dVar = initialXStep / dVector.x;
			epsilon += slope * initialXStep;
			while (currentHex != endHex) {
				prevHex = currentHex;
				if (epsilon > Terrain.getHexHalfSide()) {
					lastDirection = getDirection(signX, signY);
					currentHex = terrainData.getHexagonByDirection(lastDirection, (int) prevHex.x, (int) prevHex.y);
					epsilon -= 1.5f * Terrain.getHexSide(); // Fixing epsilon to new hexagon
					epsilon += slope * Terrain.getHexHalfSqrt3(); // Advancing to next hexagon line
				} else if (epsilon > -0.5) {
					lastDirection = getDirection(signX, 0);
					currentHex = terrainData.getHexagonByDirection(lastDirection, (int) prevHex.x, (int) prevHex.y);
					epsilon += slope * Terrain.getHexSqrt3(); // Advancing to next hexagon line
				} else {
					lastDirection = getDirection(signX, signY);
					currentHex = terrainData.getHexagonByDirection(lastDirection, (int) prevHex.x, (int) prevHex.y);
					if (terrainData.getHeightByHexCoords((int) currentHex.x, (int) currentHex.x) < ray.y * dVar) {
						break;
					}
				}
				// if (0 < checkPoint.x, checkPoint.z)) {

				// }
//				terrainData.getHexagonByDirection(getDirection(arg1, arg2), currentHex.x,
				// currentHex.y)

				if (terrainData.getHeightByHexCoords((int) currentHex.x, (int) currentHex.x) < ray.y * dVar) {
					break;
				}
			}

		}
		return pickHex;
	}

	private TerrainData.Direction getDirection(int arg1, int arg2) {
		return QUADRANT_DIRECTION_TABLE[(int) ((arg1 + 1) * 0.5)][arg2 + 1];
	}

	public Vector3f getPointAtHeight(float height) {
		float scale = (camera.getPosition().y - height) / currentRay.y;
		return Vector3f.add(camera.getPosition(), Maths.multiplyVector(currentRay, scale), null);
	}

	public Vector3f calculateMouseRay() {
		float mouseX = Mouse.getX();
		float mouseY = Mouse.getY();
		Vector2f normalizedCoords = getNormalizedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1, 1);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f inverseView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(inverseView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}

	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f inverseProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(inverseProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1, 0);
	}

	private Vector2f getNormalizedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2 * mouseX) / Display.getWidth() - 1;
		float y = (2 * mouseY) / Display.getHeight() - 1;
		return new Vector2f(x, y);
	}
}