package guis;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

public class GUITexture {

	private int texture;
	private Vector2f position;
	private Vector2f scale;

	public GUITexture(int texture, Vector2f position, Vector2f scale) {
		this.texture = texture;
		this.scale = new Vector2f(2 * scale.x / Display.getWidth(), 2 * scale.y / Display.getHeight());
		this.position = new Vector2f(2 * position.x / Display.getWidth() - 1 + this.scale.x, -2 * position.y / Display.getHeight() + 1 - this.scale.y);
	}

	public int getTexture() {
		return texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScale() {
		return scale;
	}

}
