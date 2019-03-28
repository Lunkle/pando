package terrains;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class Terrain {

	public static final float SIZE = 800;
	public static final float HEXAGON_SIDE_LENGTH = 5;
	public static final float HEXAGON_HALF_SQRTHREE_LENGTH = HEXAGON_SIDE_LENGTH * (float) Math.sqrt(3) / 2;
	public static final float HEXAGON_SQRTHREE_LENGTH = HEXAGON_HALF_SQRTHREE_LENGTH * 2;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;

	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;

	private float[][] heights;
	float gridSquareSize;

	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texture, TerrainTexture blendMap,
			String heightMap) {
		texturePack = texture;
		this.blendMap = blendMap;
		x = gridX * SIZE;
		z = gridZ * SIZE;
//		gridSquareSize = SIZE / (float) (heights.length - 1);
		TerrainGen gen = new TerrainGen(20, 20, "map", "res");
		gen.makeDefaultFile();
		model = generateHexagonMeshTerrain(loader, heightMap);
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

	public float getHeightOfHexagonMeshTerrain(float worldX, float worldZ) {
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		if (gridX < 0 || gridX > heights.length - 1 || gridZ < 0 || gridZ > heights.length - 1) {
			return 0;
		}
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float yCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float result;
		if (xCoord <= (1 - yCoord)) {
			result = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, yCoord));
		} else {
			result = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, yCoord));
		}
		return result;
	}

	public float getHeightOfTriangleMeshTerrain(float worldX, float worldZ) {
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		if (gridX < 0 || gridX > heights.length - 1 || gridZ < 0 || gridZ > heights.length - 1) {
			return 0;
		}
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float yCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float result;
		if (xCoord <= (1 - yCoord)) {
			result = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, yCoord));
		} else {
			result = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, yCoord));
		}
		return result;
	}

	private RawModel generateHexagonMeshTerrain(Loader loader, String heightMap) {
		BufferedReader reader = null;
		int gridSizeX;
		int gridSizeY;
		int[] data;
		try {
			reader = new BufferedReader(new FileReader("res/" + heightMap + ".txt"));
			String line = reader.readLine();
			if (line != null) {
				data = Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray();
				gridSizeX = data[0];
				gridSizeY = data[1];
			} else {
				reader.close();
				System.out.println("Failed to read x and y size data from height map at res/" + heightMap + ".txt");
				return null;
			}
		} catch (IOException e) {
			System.out.println("Failed to read any data from height map at res/" + heightMap + ".txt");
			e.printStackTrace();
			return null;
		}
		heights = new float[gridSizeY][gridSizeX];
		int count = gridSizeX * gridSizeY;
		float[] vertices = new float[(int) (count * 18)];
		float[] normals = new float[(int) (count * 18)];
		float[] textureCoords = new float[(int) (count * 12)];
		try {
			String line;
			int rowNumber = 0;
			boolean isOffsetFromLeft = false;
			float terrainSizeX = (gridSizeX + 0.5f) * HEXAGON_SQRTHREE_LENGTH;
			float terrainSizeY = (gridSizeY * 1.5f + 0.5f) * HEXAGON_SIDE_LENGTH;
			while ((line = reader.readLine()) != null) {
				data = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
				for (int columnNumber = 0; columnNumber < data.length; columnNumber++) {
					int height = data[columnNumber];
					heights[rowNumber][columnNumber] = height;
					float referencePointX = HEXAGON_SQRTHREE_LENGTH * (columnNumber + (isOffsetFromLeft ? 0.5f : 0));
					float referencePointZ = 1.5f * HEXAGON_SIDE_LENGTH * rowNumber;
					int startingVerticeIndex = 18 * (rowNumber * gridSizeX + columnNumber);
					vertices[startingVerticeIndex] = referencePointX + HEXAGON_HALF_SQRTHREE_LENGTH;
					vertices[startingVerticeIndex + 2] = referencePointZ;
					vertices[startingVerticeIndex + 3] = referencePointX;
					vertices[startingVerticeIndex + 5] = referencePointZ + 0.5f * HEXAGON_SIDE_LENGTH;
					vertices[startingVerticeIndex + 6] = referencePointX;
					vertices[startingVerticeIndex + 8] = referencePointZ + 1.5f * HEXAGON_SIDE_LENGTH;
					vertices[startingVerticeIndex + 9] = referencePointX + HEXAGON_HALF_SQRTHREE_LENGTH;
					vertices[startingVerticeIndex + 11] = referencePointZ + 2 * HEXAGON_SIDE_LENGTH;
					vertices[startingVerticeIndex + 12] = referencePointX + HEXAGON_SQRTHREE_LENGTH;
					vertices[startingVerticeIndex + 14] = referencePointZ + 1.5f * HEXAGON_SIDE_LENGTH;
					vertices[startingVerticeIndex + 15] = referencePointX + HEXAGON_SQRTHREE_LENGTH;
					vertices[startingVerticeIndex + 17] = referencePointZ + 0.5f * HEXAGON_SIDE_LENGTH;
					for (int i = 0; i < 6; i++) {
						vertices[startingVerticeIndex + 1 + i * 3] = height;
					}
					Vector3f center = new Vector3f(referencePointX + HEXAGON_HALF_SQRTHREE_LENGTH, height,
							referencePointZ + HEXAGON_SIDE_LENGTH);
					for (int i = 0; i < 6; i++) {
						Vector3f vertice = new Vector3f(vertices[startingVerticeIndex + i * 3], height,
								vertices[startingVerticeIndex + i * 3 + 2]);
						Vector3f normal = calculateHexagonMeshNormal(center, vertice);
						normal = new Vector3f(0, 1, 0);
						normals[startingVerticeIndex + i * 3] = normal.x;
						normals[startingVerticeIndex + i * 3 + 1] = normal.y;
						normals[startingVerticeIndex + i * 3 + 2] = normal.z;
					}
					int startingTextureIndex = 12 * (rowNumber * gridSizeX + columnNumber);
					for (int i = 0; i < 6; i++) {
						textureCoords[startingTextureIndex + i * 2] = vertices[startingVerticeIndex + i * 3]
								/ terrainSizeX;
						textureCoords[startingTextureIndex + i * 2 + 1] = vertices[startingVerticeIndex + i * 3 + 2]
								/ terrainSizeY;
					}
				}
				rowNumber++;
				isOffsetFromLeft = !isOffsetFromLeft;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		int[] indices = new int[12 * count];
		for (int y = 0; y < gridSizeY; y++) {
			for (int x = 0; x < gridSizeX; x++) {
				int startingIndiceIndex = 12 * (y * gridSizeX + x);
				int startingVerticeIndex = 6 * (y * gridSizeX + x);
				indices[startingIndiceIndex] = startingVerticeIndex;
				indices[startingIndiceIndex + 1] = startingVerticeIndex + 2;
				indices[startingIndiceIndex + 2] = startingVerticeIndex + 4;
				indices[startingIndiceIndex + 3] = startingVerticeIndex;
				indices[startingIndiceIndex + 4] = startingVerticeIndex + 1;
				indices[startingIndiceIndex + 5] = startingVerticeIndex + 2;
				indices[startingIndiceIndex + 6] = startingVerticeIndex + 2;
				indices[startingIndiceIndex + 7] = startingVerticeIndex + 3;
				indices[startingIndiceIndex + 8] = startingVerticeIndex + 4;
				indices[startingIndiceIndex + 9] = startingVerticeIndex;
				indices[startingIndiceIndex + 10] = startingVerticeIndex + 4;
				indices[startingIndiceIndex + 11] = startingVerticeIndex + 5;
			}
		}
//		for (float vertice : vertices) {
//			System.out.println(vertice); 
//		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	private RawModel generateTriangleMeshTerrain(Loader loader, String heightMap) {

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
				Vector3f normal = calculateTriangleMeshNormal(j, i, image);
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

	private Vector3f calculateHexagonMeshNormal(Vector3f center, Vector3f vertice) {
		Vector3f centerToPoint = null;
		centerToPoint = Vector3f.sub(vertice, center, null);
		centerToPoint.normalise();
		Vector3f normalVector = null;
		normalVector = Vector3f.add(centerToPoint, new Vector3f(0, 1, 0), null);
		normalVector.normalise();
		return normalVector;
	}

	// Optimize this because we are calculating the same vertices over and over
	// again
	private Vector3f calculateTriangleMeshNormal(int x, int z, BufferedImage image) {
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

	public float[] pixel_to_pointy_hex(float x, float y) {
		float q = (float) ((Math.sqrt(3) / 3 * x - 1f / 3 * y) / HEXAGON_SIDE_LENGTH);
		float r = (2f / 3 * y) / HEXAGON_SIDE_LENGTH;
		return cubeRound(q, 0, r);
	}
	
	private float[] cubeRound(float x, float y, float z) {
		    float rx = Math.round(x);
		    float ry = Math.round(y);
		    float rz = Math.round(z);

		    float x_diff = Math.abs(rx - x);
		    float y_diff = Math.abs(ry - y);
		    float z_diff = Math.abs(rz - z);

		    if (x_diff > y_diff && x_diff > z_diff)
		        rx = -ry-rz;
		    else if (y_diff > z_diff)
		        ry = -rx-rz;
		    else
		        rz = -rx-ry;

		    return new float[]{rx, ry, rz};
	}

}
