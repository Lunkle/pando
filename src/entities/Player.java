package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;

public class Player extends Entity {

	private static final float RUN_SPEED = 20; // Units are pixels per second.
	private static final float TURN_SPEED = 160; // Units are degrees per second.
	private static final float JUMP_POWER = 30; // Units are pixels per second.

	private static final float GRAVITY = -50; // Units are pixels per second squared.

	private static final float TERRAIN_HEIGHT = 0;

	private boolean onGround = true;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public void move() {
		checkInputs();
		float frameTime = DisplayManager.getFrameTimeSeconds();
		super.increaseRotation(0, currentTurnSpeed * frameTime, 0);
		upwardsSpeed += GRAVITY * frameTime;
		float distance = currentSpeed * frameTime;
		float xDisplacement = (float) (Math.sin(Math.toRadians(getRotY())) * distance);
		float zDisplacement = (float) (Math.cos(Math.toRadians(getRotY())) * distance);
		super.increasePosition(xDisplacement, upwardsSpeed * frameTime, zDisplacement);
		if (super.getPosition().y <= TERRAIN_HEIGHT) {
			upwardsSpeed = 0;
			super.getPosition().y = TERRAIN_HEIGHT;
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
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed += RUN_SPEED;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed -= RUN_SPEED;
		}

		currentTurnSpeed = 0;
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			currentTurnSpeed += TURN_SPEED;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			currentTurnSpeed -= TURN_SPEED;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}

	}

}
