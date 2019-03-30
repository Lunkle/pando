package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class FloatingCamera extends Camera {
	private Vector3f vel = new Vector3f(0, 0, 0);
	private float turnVel = 0f;
	private int START_HEIGHT = 50;
	private float TURN_SPEED = 4f;
	private boolean TURN_ON_DA = true;
	private float MOVE_SPEED = 0.05f;
	private float MAX_SPEED = 1;
	
	public FloatingCamera(int startX, int startZ) {
		position.x = startX;
		position.z = startZ;
		position.y = START_HEIGHT;
		
		pitch = 45;
		roll = 0;
		yaw = 0;
	}
	
	public void move() {
		calculateLoc();
	}
	
	private void calculateLoc() {
		vel.y -= Mouse.getDWheel()*0.05f;
		position.y += vel.y;
		if (Mouse.getDWheel() == 0) {
			vel.y = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			if (vel.z <= MAX_SPEED) {
				vel.z += MOVE_SPEED;
			}
		} else if(vel.z > MOVE_SPEED){
			vel.z -= MOVE_SPEED;
		} else if(vel.z > 0) {
			vel.z = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			if (vel.z >= -MAX_SPEED) {
				vel.z -= MOVE_SPEED;
			}
		} else if(vel.z < -MOVE_SPEED) {
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
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				if (vel.x <= MAX_SPEED) {
					vel.x += MOVE_SPEED;
				}
			} else if(vel.x > MOVE_SPEED){
				vel.x -= MOVE_SPEED;
			} else if(vel.x > 0) {
				vel.x = 0;
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				if (vel.x >= -MAX_SPEED) {
					vel.x -= MOVE_SPEED;
				}
			} else if(vel.x < -MOVE_SPEED) {
				vel.x += MOVE_SPEED;
			} else if (vel.x < 0) {
				vel.x = 0;
			}
		}
//		currentTurnSpeed = 0;
//		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
//			currentTurnSpeed += TURN_SPEED;
//		}
//		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
//			currentTurnSpeed -= TURN_SPEED;
//		}
//
//		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
//			jump();
//		}
		position.x += vel.x;
		position.z += vel.z;
	}
	
//	private int 
}
