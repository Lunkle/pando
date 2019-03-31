package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity {

	private static final float RUN_SPEED = 20; // Units are pixels per second.
	private static final float TURN_SPEED = 70; // Units are degrees per second.
	private static final float JUMP_POWER = 50; // Units are pixels per second.

	private static final float GRAVITY = -150; // Units are pixels per second squared.

	private boolean onGround = true;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public void move(Terrain[][] terrains) {
		checkInputs();
		float frameTime = DisplayManager.getFrameTimeSeconds();
		super.increaseRotation(0, currentTurnSpeed * frameTime, 0);
		upwardsSpeed += GRAVITY * frameTime;
		float distance = currentSpeed * frameTime;
		float xDisplacement = (float) (Math.sin(Math.toRadians(getRotY())) * distance);
		float zDisplacement = (float) (Math.cos(Math.toRadians(getRotY())) * distance);
		super.increasePosition(xDisplacement, upwardsSpeed * frameTime, zDisplacement);
//		float terrainHeight = Terrain.getHeightOfHexagonMeshTerrain(super.getPosition().x, super.getPosition().z, terrains);
		float terrainHeight = 0;
		if (super.getPosition().y <= terrainHeight) {
			upwardsSpeed = 0;
			super.getPosition().y = terrainHeight;
			onGround = true;
		}
	}

	private void jump() {
		if (onGround) {
			this.upwardsSpeed += JUMP_POWER;
			onGround = false;
		}
	}

	private void checkInputs() {
		currentSpeed = 0;
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			this.currentSpeed += RUN_SPEED;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			this.currentSpeed -= RUN_SPEED;
		}

		currentTurnSpeed = 0;
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			currentTurnSpeed += TURN_SPEED;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			currentTurnSpeed -= TURN_SPEED;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}

	}

}
