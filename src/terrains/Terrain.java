package terrains;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class Terrain {

	public static final int NUM_HEXAGONS_X = 4;
	public static final int NUM_HEXAGONS_Z = 4;
	public static final float HEXAGON_SIDE_LENGTH = 5;
	public static final float HEXAGON_SQRTHREE_LENGTH = HEXAGON_SIDE_LENGTH * (float) Math.sqrt(3);
	public static final float HEXAGON_HALF_SQRTHREE_LENGTH = HEXAGON_SQRTHREE_LENGTH / 2;
	public static final float HEXAGON_MINIMUM_TRIANGLE_SIZE = HEXAGON_SIDE_LENGTH * HEXAGON_HALF_SQRTHREE_LENGTH;
	public static final float X_SIZE = NUM_HEXAGONS_X * HEXAGON_SQRTHREE_LENGTH;
	public static final float Z_SIZE = 1.5f * NUM_HEXAGONS_Z * HEXAGON_SIDE_LENGTH;

	// Number of indices constructing the hexagons
	public static final int NUM_INDICES_CONTRUCTING_HEX = NUM_HEXAGONS_X * NUM_HEXAGONS_Z * 12;
	// Number of vertices until reaching the next row of hexagon
	private static final int DIFF_NEXT_ROW = NUM_HEXAGONS_X * 6;

	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;

	private float[][] heights;

	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texture, TerrainTexture blendMap, String heightMap) {
		texturePack = texture;
		this.blendMap = blendMap;
		x = gridX * X_SIZE;
		z = gridZ * Z_SIZE;
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

	public static Vector2f getHexagon(float worldX, float worldZ, Terrain[][] terrains) {
		int worldHexZ = (int) Math.floor(worldZ / (HEXAGON_SIDE_LENGTH * 1.5));
		int isOffset = worldHexZ % 2;
		float xOffset = HEXAGON_HALF_SQRTHREE_LENGTH * isOffset;
		int worldHexX = (int) Math.floor((worldX - xOffset) / HEXAGON_SQRTHREE_LENGTH);

		float tileZ = worldZ % (HEXAGON_SIDE_LENGTH * 1.5f);
		int terrainGridX = (int) Math.floor(worldHexX / NUM_HEXAGONS_X);
		int terrainGridZ = (int) Math.floor(worldHexZ / NUM_HEXAGONS_Z);

		if (terrainGridX < 0 || terrainGridX > terrains[0].length || terrainGridZ < 0 || terrainGridZ > terrains.length) {
			System.out.println("out of bounds");
			return null;
		}

		if (tileZ > 0.5f * HEXAGON_SIDE_LENGTH) {
			return new Vector2f(worldHexX, worldHexZ);
		} else {
			Vector2f left = new Vector2f(xOffset + worldHexX * HEXAGON_SQRTHREE_LENGTH, (worldHexZ + 1) * 1.5f * HEXAGON_SIDE_LENGTH);
			Vector2f right = new Vector2f(left.x + HEXAGON_SQRTHREE_LENGTH, left.y);
			Vector2f bottom = new Vector2f(left.x + HEXAGON_HALF_SQRTHREE_LENGTH, left.y + 0.5f * HEXAGON_SIDE_LENGTH);
			float leftSize = Maths.areaOfTriangle(bottom, left, new Vector2f(worldX, worldZ));
			float rightSize = Maths.areaOfTriangle(bottom, right, new Vector2f(worldX, worldZ));
			boolean inFromLeft = leftSize <= HEXAGON_MINIMUM_TRIANGLE_SIZE;
			boolean inFromRight = rightSize <= HEXAGON_MINIMUM_TRIANGLE_SIZE;
			if (inFromLeft && inFromRight) {
				return new Vector2f(worldHexX, worldHexZ);
			} else if (inFromLeft) {
				// is in left hexagon
				return new Vector2f(worldHexX - 1 + isOffset, worldHexZ - 1);
			} else if (inFromRight) {
				// is in right hexagon
				return new Vector2f(worldHexX + isOffset, worldHexZ - 1);
			}
		}
		return null;
	}

	public static float getHeightOfHexagonMeshTerrain(Vector2f worldHexPos, Terrain[][] terrains) {
		int worldHexX = (int) worldHexPos.x;
		int worldHexZ = (int) worldHexPos.y;
		return getHeightOfHexagonMeshTerrain(worldHexX, worldHexZ, terrains);
	}

	public static float getHeightOfHexagonMeshTerrain(int worldHexX, int worldHexZ, Terrain[][] terrains) {
		int terrainGridX = (int) Math.floor(worldHexX / NUM_HEXAGONS_X);
		int terrainGridZ = (int) Math.floor(worldHexZ / NUM_HEXAGONS_Z);
		int terrainHexX = worldHexX % NUM_HEXAGONS_X;
		int terrainHexZ = worldHexZ % NUM_HEXAGONS_Z;
		return terrains[terrainGridZ][terrainGridX].heights[terrainHexZ][terrainHexX];
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

	private RawModel generateHexagonMeshTerrain(Loader loader, String heightMap) {
		BufferedReader reader = null;
		int[] data;
		try {
			reader = new BufferedReader(new FileReader("res/" + heightMap + ".txt"));
		} catch (IOException e) {
			System.out.println("Failed to read any data from height map at res/" + heightMap + ".txt");
			e.printStackTrace();
			return null;
		}
		heights = new float[NUM_HEXAGONS_Z][NUM_HEXAGONS_X];
		int count = NUM_HEXAGONS_X * NUM_HEXAGONS_Z;
		float[] vertices = new float[(int) (count * 18)];
		float[] normals = new float[(int) (count * 18)];
		float[] textureCoords = new float[(int) (count * 12)];
		try {
			String line;
			int rowNumber = 0;
			boolean isOffsetFromLeft = false;
			float terrainSizeX = (NUM_HEXAGONS_X + 0.5f) * HEXAGON_SQRTHREE_LENGTH;
			float terrainSizeY = (NUM_HEXAGONS_Z * 1.5f + 0.5f) * HEXAGON_SIDE_LENGTH;
			while (rowNumber < NUM_HEXAGONS_Z && (line = reader.readLine()) != null) {
				data = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
				for (int columnNumber = 0; columnNumber < NUM_HEXAGONS_X; columnNumber++) {
					float height = data[columnNumber] / 2.0f;
					heights[rowNumber][columnNumber] = height;
					float referencePointX = HEXAGON_SQRTHREE_LENGTH * (columnNumber + (isOffsetFromLeft ? 0.5f : 0));
					float referencePointZ = 1.5f * HEXAGON_SIDE_LENGTH * rowNumber;
					int startingVerticeIndex = 18 * (rowNumber * NUM_HEXAGONS_X + columnNumber);
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
					Vector3f center = new Vector3f(referencePointX + HEXAGON_HALF_SQRTHREE_LENGTH, height, referencePointZ + HEXAGON_SIDE_LENGTH);
					for (int i = 0; i < 6; i++) {
						Vector3f vertice = new Vector3f(vertices[startingVerticeIndex + i * 3], height, vertices[startingVerticeIndex + i * 3 + 2]);
						Vector3f normal = calculateHexagonMeshNormal(center, vertice);
//						normal = new Vector3f(0, 1, 0);
						normals[startingVerticeIndex + i * 3] = normal.x;
						normals[startingVerticeIndex + i * 3 + 1] = normal.y;
						normals[startingVerticeIndex + i * 3 + 2] = normal.z;
					}
					int startingTextureIndex = 12 * (rowNumber * NUM_HEXAGONS_X + columnNumber);
					for (int i = 0; i < 6; i++) {
						textureCoords[startingTextureIndex + i * 2] = vertices[startingVerticeIndex + i * 3] / terrainSizeX;
						textureCoords[startingTextureIndex + i * 2 + 1] = vertices[startingVerticeIndex + i * 3 + 2] / terrainSizeY;
					}
				}
				rowNumber++;
				isOffsetFromLeft = !isOffsetFromLeft;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		int hexSideIndicesCount = 6 * (3 * (NUM_HEXAGONS_X - 1) * (NUM_HEXAGONS_Z - 1) + NUM_HEXAGONS_X + NUM_HEXAGONS_Z - 2);
		int[] indices = new int[12 * count + hexSideIndicesCount];
		int startingIndiceIndex = 0;
		int startingVerticeIndex = 0;
		int[] hexIndiceArray = new int[] { 0, 2, 4, 0, 1, 2, 2, 3, 4, 0, 4, 5 };
		for (int y = 0; y < NUM_HEXAGONS_Z; y++) {
			for (int x = 0; x < NUM_HEXAGONS_X; x++) {
				for (int i = 0; i < 12; i++) {
					indices[startingIndiceIndex++] = startingVerticeIndex + hexIndiceArray[i];
				}
				startingVerticeIndex += 6;
			}
		}
		startingVerticeIndex = 0;
		int[] hexSideIndiceArray = new int[] { 4, DIFF_NEXT_ROW, DIFF_NEXT_ROW + 1, 4, 3, DIFF_NEXT_ROW + 1, 8, 4, 5, 8, 7, 5, DIFF_NEXT_ROW, 8, 9, DIFF_NEXT_ROW, DIFF_NEXT_ROW + 5, 9 };
		for (int y = 0; y < NUM_HEXAGONS_Z - 1; y++) {
			for (int x = 0; x < NUM_HEXAGONS_X - 1; x++) {
				for (int index : hexSideIndiceArray) {
					indices[startingIndiceIndex++] = startingVerticeIndex + index;
				}
				startingVerticeIndex += 6;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
}
