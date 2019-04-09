package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import data.TerrainData;
import terrains.Terrain;

public class FloatingCamera extends Camera {
	private Vector3f vel = new Vector3f(0, 0, 0);
	private boolean wasPressed = false;
	private int startX = 0;
	private Vector2f mousePress = new Vector2f(-1, -1);
	private float turnVel = 0f;
	private final int START_HEIGHT = 50;
	private final float TURN_SPEED = 2.5f;
	private final boolean TURN_ON_DA = false;
	private final float MOVE_SPEED = 0.025f;
	private final float MAX_SPEED = 1;
	private final float HEIGHT_SPEED_MOD = 50f;
	private final float MAX_ZOOM_SPEED = 3f;
	private final int MIN_HEIGHT = 25;
	private final int MAX_HEIGHT = 150;

	public FloatingCamera(int startX, int startZ) {
		position.x = startX;
		position.z = startZ;
		position.y = START_HEIGHT;

		pitch = 30;
		roll = 0;
		yaw = 0;
	}

	public void move(TerrainData terrainData) {
		calculateLoc(terrainData);
	}

	private void calculateLoc(TerrainData terrainData) {
		try {
			int minHeight = (int) (MIN_HEIGHT + terrainData.getHeightByWorldCoords(position.x, position.z));

			if (Math.abs(vel.y) < MAX_ZOOM_SPEED)
				vel.y -= Mouse.getDWheel() * 0.01f;

			if (Mouse.getDWheel() == 0 && Math.abs(vel.y) > 0.1) {
				vel.y -= 0.1 * vel.y / Math.abs(vel.y);
			} else if (Mouse.getDWheel() == 0 && Math.abs(vel.y) > 0) {
				vel.y = 0;
			}

			if (position.y + vel.y > minHeight && position.y + vel.y < MAX_HEIGHT) {
				position.y += vel.y;
			} else if (position.y < minHeight) {
				position.y = minHeight;
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				if (vel.z <= MAX_SPEED) {
					vel.z += MOVE_SPEED;
				}
			} else if (vel.z > MOVE_SPEED) {
				vel.z -= MOVE_SPEED;
			} else if (vel.z > 0) {
				vel.z = 0;
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				if (vel.z >= -MAX_SPEED) {
					vel.z -= MOVE_SPEED;
				}
			} else if (vel.z < -MOVE_SPEED) {
				vel.z += MOVE_SPEED;
			} else if (vel.z < 0) {
				vel.z = 0;
			}

			if (TURN_ON_DA) {
				if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
					yaw -= TURN_SPEED;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
					yaw += TURN_SPEED;
				}
			} else {
				calculateMouseTurn();
				if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
					if (vel.x <= MAX_SPEED) {
						vel.x += MOVE_SPEED;
					}
				} else if (vel.x > MOVE_SPEED) {
					vel.x -= MOVE_SPEED;
				} else if (vel.x > 0) {
					vel.x = 0;
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
					if (vel.x >= -MAX_SPEED) {
						vel.x -= MOVE_SPEED;
					}
				} else if (vel.x < -MOVE_SPEED) {
					vel.x += MOVE_SPEED;
				} else if (vel.x < 0) {
					vel.x = 0;
				}
			}
			newPos(minHeight);
		} catch (ArrayIndexOutOfBoundsException e) {
			float terrainWidth = terrainData.terrainGrid[0].length * Terrain.Z_SIZE;
			float terrainLength = terrainData.terrainGrid.length * Terrain.X_SIZE;

			position.x = (position.x - 0.25f + terrainLength) % (terrainLength) + vel.x;
			position.z = (position.z - 0.25f + terrainWidth) % (terrainWidth) + vel.z;

		}

	}

	private void calculateMouseTurn() {
		Vector2f mousePos = new Vector2f(Mouse.getX(), Mouse.getY());
		if (Mouse.isButtonDown(0)) {
			if (wasPressed) {
				if (!(mousePress == mousePos)) {
					float rawMouseDX = Mouse.getDX();
					float yawChange = rawMouseDX * 0.1f;
					yaw = (yaw - yawChange) % 360;
				}
			} else {
				wasPressed = true;
				mousePress = mousePos;
			}
		} else if (wasPressed) {
			wasPressed = false;
			mousePress.x = -1;
			mousePress.y = -1;
		}
	}

	private void newPos(int minHeight) {
		double moveMag = Math.sqrt(Math.pow(vel.x, 2) + Math.pow(vel.z, 2));

		// Limits speed
		if (moveMag > 1) {
			moveMag = 1;
		}

		float xMod = (float) (Math.sin(Math.toRadians(getMoveAngle())) * moveMag);
		float zMod = (float) (Math.cos(Math.toRadians(getMoveAngle())) * moveMag);

		if (!Double.isNaN(xMod)) {
			position.x += xMod * Math.abs(position.y - minHeight + 10) / HEIGHT_SPEED_MOD;
		}
		if (!Double.isNaN(zMod)) {
			position.z += zMod * Math.abs(position.y - minHeight + 10) / HEIGHT_SPEED_MOD;
		}
	}

	private float getMoveAngle() {
		float angle = (float) (Math.toDegrees(Math.atan(vel.x / vel.z)) % 360);

		if (Double.isNaN(angle)) {
			if (vel.x > 0) {
				return 90f;
			} else if (vel.x < 0) {
				return -90f;
			}
		}

		if (vel.x > 0 && vel.z > 0) {
			return angle - yaw;
		} else if (vel.x < 0 && vel.z > 0) {
			return angle - yaw;
		} else if (vel.x > 0 && vel.z < 0) {
			return angle - yaw + 180;
		} else if (vel.x < 0 && vel.z < 0) {
			return angle - yaw + 180;
		} else if (vel.x > 0 && vel.z == 0) {
			return -yaw + 90;
		} else if (vel.x < 0 && vel.z == 0) {
			return -yaw - 90;
		} else if (vel.z > 0 && vel.x == 0) {
			return angle - yaw;
		} else if (vel.z < 0 && vel.x == 0) {
			return -angle - yaw + 180;
		}

		return (float) angle + yaw;
	}
}
