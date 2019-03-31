package toolbox;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;

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

	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
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
