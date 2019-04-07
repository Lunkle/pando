package toolbox;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import data.TerrainData;
import entities.Camera;
import entities.Player;
import terrains.Terrain;

public class MousePicker {

	private Vector3f currentRay;
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;

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

	public Vector3f calculateMouseRay() {
		float mouseX = Mouse.getX();
		float mouseY = Mouse.getY();
		Vector2f normalizedCoords = getNormalizedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1, 1);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	public Vector3f findCoords(TerrainData terrain) {
		Vector3f loc = new Vector3f(camera.getPosition());
		
		try {
			while (terrain.getHeightByWorldCoords(loc.x, loc.z) < loc.y) {
				Vector2f mod = calculateJumpAmount();
				
				loc.x += mod.x;
				loc.z += mod.y;
				loc.y += mod.x/currentRay.x*currentRay.y;
			}
		} catch (Exception e) {
			System.out.println("mouse OOB");
			return camera.getPosition();
		}
		
		loc.y = terrain.getHeightByWorldCoords(loc.x, loc.z);
		return loc;
	}
	
	public Vector2f calculateJumpAmount() {
		float angle = calculateRayAngle();
		
		float relAngle = Math.abs(angle%30-15);
		float distance = Terrain.HEXAGON_SIDE_LENGTH;
		if (relAngle<15 && relAngle>0) {
			distance = (float) (Math.cos(Math.toRadians(15-relAngle))*Terrain.HEXAGON_HALF_SQRTHREE_LENGTH);
		} else if (relAngle>15){
			distance = (float) (Math.cos(Math.toRadians(relAngle-15))*Terrain.HEXAGON_HALF_SQRTHREE_LENGTH);
		} else if (relAngle==15) {
			distance = Terrain.HEXAGON_HALF_SQRTHREE_LENGTH;
		}
		
		Vector2f jump = new Vector2f(0, 0);
		
		jump.x = (float) Math.sin(Math.toRadians(angle))*distance/2;
		jump.y = (float) Math.cos(Math.toRadians(angle))*distance/2;
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
