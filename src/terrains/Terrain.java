package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class Terrain {

	private static final float SIZE = 800;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;

	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;

	private float[][] heights;
	float gridSquareSize;

	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texture, TerrainTexture blendMap, String heightMap) {
		texturePack = texture;
		this.blendMap = blendMap;
		x = gridX * SIZE;
		z = gridZ * SIZE;
		model = generateTerrain(loader, heightMap);
		gridSquareSize = SIZE / (float) (heights.length - 1);
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}

	public float getHeightOfTerrain(float worldX, float worldY) {
		float terrainX = worldX - this.x;
		float terrainZ = worldY - this.z;
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridY = (int) Math.floor(terrainZ / gridSquareSize);
		if (gridX < 0 || gridX > heights.length - 1 || gridY < 0 || gridY > heights.length - 1) {
			return 0;
		}
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float yCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float result;
		if (xCoord <= (1 - yCoord)) {
			result = Maths.barryCentric(new Vector3f(0, heights[gridX][gridY], 0), new Vector3f(1, heights[gridX + 1][gridY], 0), new Vector3f(0, heights[gridX][gridY + 1], 1), new Vector2f(xCoord, yCoord));
		} else {
			result = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridY], 0), new Vector3f(1, heights[gridX + 1][gridY + 1], 1), new Vector3f(0, heights[gridX][gridY + 1], 1), new Vector2f(xCoord, yCoord));
		}
		return result;
	}

	private RawModel generateTerrain(Loader loader, String heightMap) {

		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/" + heightMap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int vertexCountX = image.getWidth();
		int vertexCountY = image.getHeight();

		heights = new float[vertexCountY][vertexCountX];

		int count = vertexCountX * vertexCountY;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (vertexCountX - 1) * (vertexCountY - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < vertexCountY; i++) {
			for (int j = 0; j < vertexCountX; j++) {
				// Calculating vertices
				float height = getHeight(j, i, image);
				heights[j][i] = height;
				vertices[vertexPointer * 3] = (float) j / ((float) vertexCountX - 1) * SIZE;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) vertexCountY - 1) * SIZE;
				// Calculating normals
				Vector3f normal = calculateNormal(j, i, image);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				// Calculating texture coordinates
				textureCoords[vertexPointer * 2] = (float) j / ((float) vertexCountX - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) vertexCountY - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < vertexCountY - 1; gz++) {
			for (int gx = 0; gx < vertexCountX - 1; gx++) {
				int topLeft = (gz * vertexCountX) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * vertexCountX) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	// Optimize this because we are calculating the same vertices over and over
	// again
	private Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x - 1, z, image);
		float heightR = getHeight(x + 1, z, image);
		float heightU = getHeight(x, z - 1, image);
		float heightD = getHeight(x, z + 1, image);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightU - heightD);
		normal.normalise();
		return normal;
	}

	private float getHeight(int x, int z, BufferedImage image) {
		if (x < 0 || x > image.getWidth() - 1 || z < 0 || z > image.getHeight() - 1) {
			return 0;
		}
		float height = image.getRGB(x, z);
		height = MAX_HEIGHT * (height) / MAX_PIXEL_COLOUR + 0.5f;
		return height;
	}

}
