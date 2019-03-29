package terrains;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class Terrain {

	public static final float NUM_HEXAGONS_X = 2;
	public static final float NUM_HEXAGONS_Y = 2;
	public static final float HEXAGON_SIDE_LENGTH = 5;
	public static final float HEXAGON_SQRTHREE_LENGTH = HEXAGON_SIDE_LENGTH * (float) Math.sqrt(3);
	public static final float HEXAGON_HALF_SQRTHREE_LENGTH = HEXAGON_SQRTHREE_LENGTH / 2;
	public static final float X_SIZE = NUM_HEXAGONS_X * HEXAGON_SQRTHREE_LENGTH;
	public static final float Z_SIZE = (1.5f * NUM_HEXAGONS_Y + 0.5f) * HEXAGON_SIDE_LENGTH;

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
		x = gridX * X_SIZE;
		z = gridZ * Z_SIZE;
//		gridSquareSize = SIZE / (float) (heights.length - 1);
		TerrainGen gen = new TerrainGen(10, 10, "map", "res");
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
//		float terrainX = worldX - this.x;
//		float terrainZ = worldZ - this.z;
//
//		int gridZ = (int) Math.floor((terrainZ - HEXAGON_SIDE_LENGTH / 2) / gridSquareSize);
//		float offset = HEXAGON_HALF_SQRTHREE_LENGTH * (gridZ % 2);
//		int gridX = (int) Math.floor((terrainX - offset) / gridSquareSize);
//		if (gridX < 0 || gridX > heights.length - 1 || gridZ < 0 || gridZ > heights.length - 1) {
////			return null;
//		}
//		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
//		float yCoord = (terrainX % gridSquareSize) / gridSquareSize;
//		float result;
//		if (xCoord <= (1 - yCoord)) {
//			result = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, yCoord));
//		} else {
//			result = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, yCoord));
//		}
		return 0;
	}

	private Vector3f calculateHexagonMeshNormal(Vector3f center, Vector3f vertice) {
		Vector3f centerToPoint = null;
		centerToPoint = Vector3f.sub(vertice, center, null);
		centerToPoint.normalise();
		Vector3f normalVector = null;
		normalVector = Vector3f.add(centerToPoint, new Vector3f(0, 5, 0), null);
		normalVector.normalise();
		return normalVector;
	}

	public static Terrain findCurrentTerrain(float positionX, float positionZ, Terrain[][] terrains) {
//		float terrainsX = positionX / Terrain.X_SIZE;
//		float terrainsZ = positionZ / Terrain.Z_SIZE;
//		int terrainGridX = (int) Math.floor(terrainsX);
//		int terrainGridZ = (int) Math.floor(terrainsZ);
//		Terrain[] possibleTerrains = new Terrain[9];
//		int index = 0;
//		for (int i = -1; i < 2; i++) {
//			for (int j = -1; j < 2; j++) {
//				possibleTerrains[index++] = terrains[terrainGridZ + i][terrainGridX + j];
//			}
//		}
//		for (Terrain possibleTerrain : possibleTerrains) {
//
//		}
		return terrains[0][0];
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
					float height = data[columnNumber] / 2.0f;
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
//						normal = new Vector3f(0, 1, 0);
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
		int hexSideIndicesCount = 3 * (gridSizeX - 1) * (gridSizeY - 1) + gridSizeX + gridSizeY - 2;
		int[] indices = new int[12 * count + hexSideIndicesCount];
		int startingIndiceIndex = 0;
		int startingVerticeIndex = 0;
		int[] hexIndiceArray = new int[] { 0, 2, 4, 0, 1, 2, 2, 3, 4, 0, 4, 5 };
		for (int y = 0; y < gridSizeY; y++) {
			for (int x = 0; x < gridSizeX; x++) {
				for (int i = 0; i < 12; i++) {
					indices[startingIndiceIndex++] = startingVerticeIndex + hexIndiceArray[i];
				}
				startingVerticeIndex += 6;
			}
		}
		for (int i = 0; i < gridSizeY - 1; i++) {
			for (int j = 0; j < gridSizeX - 1; j++) {
//				indice[startingIndiceIndex];
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	public float[] pixel_to_pointy_hex(float x, float y) {
		float q = (float) ((Math.sqrt(3) / 3 * x - 1f / 3 * y) / HEXAGON_SIDE_LENGTH);
		float r = (2f / 3 * y) / HEXAGON_SIDE_LENGTH;
		return hexRound(new float[] {q,r});
	}

	private float[] cubeRound(float x, float y, float z) {
		float rx = Math.round(x);
		float ry = Math.round(y);
		float rz = Math.round(z);

		float x_diff = Math.abs(rx - x);
		float y_diff = Math.abs(ry - y);
		float z_diff = Math.abs(rz - z);

		if (x_diff > y_diff && x_diff > z_diff)
			rx = -ry - rz;
		else if (y_diff > z_diff)
			ry = -rx - rz;
		else
			rz = -rx - ry;

		return new float[] { rx, ry, rz };
	}

	private float[] axial_to_cube(float[] hex) {
		float x = hex[0];
		float z = hex[1];
		float y = -x - z;
		return new float[] { x, y, z };
	}

	private float[] cube_to_axial(float[] cube) {
	    float q = cube[0];
	    float r = cube[2];
	    return new float[] {q,r};
	}
	
	private float[] hexRound(float[] hex) {
		float[] inCube = axial_to_cube(hex);
		float[] roundCube = cubeRound(inCube[0], inCube[1], inCube[2]);
		return cube_to_axial(roundCube);
	}
}
