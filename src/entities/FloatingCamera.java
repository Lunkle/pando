package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class FloatingCamera extends Camera {
	private Vector3f vel = new Vector3f(0, 0, 0);
	private boolean wasPressed = false;
	private int startX = 0;
	private long mousePress = 0l;
	private float turnVel = 0f;
	private int START_HEIGHT = 50;
	private float TURN_SPEED = 2.5f;
	private boolean TURN_ON_DA = false;
	private float MOVE_SPEED = 0.05f;
	private float MAX_SPEED = 1;
	private float HEIGHT_SPEED_MOD = 50f;
	private float MAX_ZOOM_SPEED = 3f;
	private int MIN_HEIGHT = 5;
	private int MAX_HEIGHT = 100;
	
	public FloatingCamera(int startX, int startZ) {
		position.x = startX;
		position.z = startZ;
		position.y = START_HEIGHT;
		
		pitch = 30;
		roll = 0;
		yaw = 0;
	}
	
	public void move() {
		calculateLoc();
	}
	
	private void calculateLoc() {		
		if (Math.abs(vel.y) < MAX_ZOOM_SPEED)
			vel.y -= Mouse.getDWheel()*0.01f;
		
		if (Mouse.getDWheel() == 0 && Math.abs(vel.y) > 0.1) {
			vel.y -= 0.1*vel.y/Math.abs(vel.y);
		} else if (Mouse.getDWheel() == 0 && Math.abs(vel.y) > 0) {
			vel.y = 0;
		}
		
		if (position.y + vel.y > MIN_HEIGHT && position.y + vel.y < MAX_HEIGHT) {
			position.y += vel.y;
		} else if (position.y < MIN_HEIGHT) {
			position.y = MIN_HEIGHT;
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
			calculateMouseTurn();
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
		newPos();
	}
	
	private void calculateMouseTurn() {
		if (Mouse.isButtonDown(0)) {
			if (wasPressed) {
				if (System.nanoTime() > mousePress + 250000000l) {
					float rawMouseDX = Mouse.getDX();
					float yawChange = rawMouseDX * 0.1f;
					yaw -= yawChange;
					
					yaw = yaw % 360;
				}
			} else {
				wasPressed = true;
				mousePress = System.nanoTime();
			}
		} else if (wasPressed) {
			wasPressed = false;
			mousePress = 0;
		}
	}
	
	private void newPos() {
		double moveMag = Math.sqrt(Math.pow(vel.x, 2) + Math.pow(vel.z, 2));
		double moveAng = Math.toDegrees(Math.atan(vel.x/vel.z));
		
		double newAng = moveAng + yaw;
				
		double zMod = Math.cos(Math.toRadians(newAng))*moveMag*vel.z/Math.abs(vel.z);
		double xMod = Math.sin(Math.toRadians(newAng))*moveMag;
				
//		float xMod = vel.x;
//		float zMod = vel.z;
//		System.out.print(yaw);
		
		System.out.print("\n\n" + vel.x + ' ' + vel.z + "\n\n");
		
		if (! Double.isNaN(xMod)) {
			position.x += xMod * position.y/HEIGHT_SPEED_MOD;
		}
		if (! Double.isNaN(zMod)) {
			position.z += zMod * position.y/HEIGHT_SPEED_MOD;
		}
	}
//	private int 
}