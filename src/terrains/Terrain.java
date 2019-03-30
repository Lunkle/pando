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

	public static final int NUM_HEXAGONS_X = 10;
	public static final int NUM_HEXAGONS_Z = 10;
	public static final float HEXAGON_SIDE_LENGTH = 5;
	public static final float HEXAGON_SQRTHREE_LENGTH = HEXAGON_SIDE_LENGTH * (float) Math.sqrt(3);
	public static final float HEXAGON_HALF_SQRTHREE_LENGTH = HEXAGON_SQRTHREE_LENGTH / 2;
	public static final float HEXAGON_MINIMUM_TRIANGLE_SIZE = HEXAGON_SIDE_LENGTH * HEXAGON_HALF_SQRTHREE_LENGTH;
	public static final float X_SIZE = NUM_HEXAGONS_X * HEXAGON_SQRTHREE_LENGTH;
	public static final float Z_SIZE = 1.5f * NUM_HEXAGONS_Z * HEXAGON_SIDE_LENGTH;

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
//		gridSquareSize = SIZE / (float) (heights.length - 1);
//		TerrainGen gen = new TerrainGen(10, 10, "map", "res");
//		gen.makeDefaultFile();
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

	public static float[] findHexCoords(float x, float y) {
		int xh = (int) (x / HEXAGON_SQRTHREE_LENGTH);
		int yh = (int) (y / (HEXAGON_SIDE_LENGTH * 1.5));

		boolean evenY = yh % 2 == 0;

		float cornerX = xh * HEXAGON_SQRTHREE_LENGTH + HEXAGON_HALF_SQRTHREE_LENGTH;
		float cornerY = (float) (yh * 1.5 * HEXAGON_SIDE_LENGTH + (evenY ? 0 : 0.5 * HEXAGON_SIDE_LENGTH));

		float slope;

		try {
			slope = (y - cornerY) / (x - cornerX);
		} catch (ArithmeticException e) {
			return new float[] { xh, (evenY ? yh : yh + 1) };
		}

		if (slope > TAN_PI_BY_SIX) {
			if (evenY)
				return new float[] { xh, yh };
			return new float[] { xh, yh + 1 };
		}

		if (x > cornerX)
			return new float[] { xh, yh };
		return new float[] { xh - 1, yh };

	}

	public static final float TAN_PI_BY_SIX = (float) (1f / Math.sqrt(3));

	public static float[] getHexagon(float worldX, float worldZ, Terrain[][] terrains) {
		int worldHexZ = (int) Math.floor(worldZ / (HEXAGON_SIDE_LENGTH * 1.5));
		int isOffset = worldHexZ % 2;
		float xOffset = HEXAGON_HALF_SQRTHREE_LENGTH * isOffset;
		int worldHexX = (int) Math.floor((worldX - xOffset) / HEXAGON_SQRTHREE_LENGTH);

		float tileZ = worldZ % (HEXAGON_SIDE_LENGTH * 1.5f);
		int terrainGridX = (int) Math.floor(worldHexX / NUM_HEXAGONS_X);
		int terrainGridZ = (int) Math.floor(worldHexZ / NUM_HEXAGONS_Z);

		if (terrainGridX < 0 || terrainGridX > terrains[0].length || terrainGridZ < 0 || terrainGridZ > terrains.length) {
			System.out.println("out of bounds");
			return new float[] { 0 };
		}

		int terrainHexX = worldHexX % NUM_HEXAGONS_X;
		int terrainHexZ = worldHexZ % NUM_HEXAGONS_Z;

		if (tileZ > 0.5f * HEXAGON_SIDE_LENGTH) {
			return new float[] { worldHexX, worldHexZ };
		} else {
			Vector2f left = new Vector2f(xOffset + worldHexX * HEXAGON_SQRTHREE_LENGTH, (worldHexZ + 1) * 1.5f * HEXAGON_SIDE_LENGTH);
			Vector2f right = new Vector2f(left.x + HEXAGON_SQRTHREE_LENGTH, left.y);
			Vector2f bottom = new Vector2f(left.x + HEXAGON_HALF_SQRTHREE_LENGTH, left.y + 0.5f * HEXAGON_SIDE_LENGTH);
			float leftSize = Maths.areaOfTriangle(bottom, left, new Vector2f(worldX, worldZ));
			float rightSize = Maths.areaOfTriangle(bottom, right, new Vector2f(worldX, worldZ));
			System.out.println(leftSize + " " + rightSize);
			boolean inFromLeft = leftSize <= HEXAGON_MINIMUM_TRIANGLE_SIZE;
			boolean inFromRight = rightSize <= HEXAGON_MINIMUM_TRIANGLE_SIZE;
			if (inFromLeft && inFromRight) {
				return new float[] { worldHexX, worldHexZ };
			} else if (inFromLeft) {
				// is in left hexagon
				return new float[] { worldHexX - 1 + isOffset, worldHexZ - 1 };
			} else if (inFromRight) {
				// is in right hexagon
				return new float[] { worldHexX + isOffset, worldHexZ - 1 };
			} else {
				System.out.println(leftSize + " " + rightSize + " " + HEXAGON_MINIMUM_TRIANGLE_SIZE);
				System.out.println("im a poop");
			}
		}
		return new float[] { 0 };
	}

	public static float getHeightOfHexagonMeshTerrain(float worldX, float worldZ, Terrain[][] terrains) {
		int worldHexZ = (int) Math.floor(worldZ / (HEXAGON_SIDE_LENGTH * 1.5));
		int isOffset = worldHexZ % 2;
		float xOffset = HEXAGON_HALF_SQRTHREE_LENGTH * isOffset;
		int worldHexX = (int) Math.floor((worldX - xOffset) / HEXAGON_SQRTHREE_LENGTH);

		float tileZ = worldZ % (HEXAGON_SIDE_LENGTH * 1.5f);
		int terrainGridX = (int) Math.floor(worldHexX / NUM_HEXAGONS_X);
		int terrainGridZ = (int) Math.floor(worldHexZ / NUM_HEXAGONS_Z);

		if (terrainGridX < 0 || terrainGridX > terrains[0].length || terrainGridZ < 0 || terrainGridZ > terrains.length) {
			System.out.println("out of bounds");
			return 0;
		}

		int terrainHexX = worldHexX % NUM_HEXAGONS_X;
		int terrainHexZ = worldHexZ % NUM_HEXAGONS_Z;

		if (tileZ > 0.5f * HEXAGON_SIDE_LENGTH) {
			return terrains[terrainGridZ][terrainGridX].heights[terrainHexZ][terrainHexX];
		} else {
			Vector2f left = new Vector2f(xOffset + worldHexX * HEXAGON_SQRTHREE_LENGTH, (worldHexZ + 1) * 1.5f * HEXAGON_SIDE_LENGTH);
			Vector2f right = new Vector2f(left.x + HEXAGON_SQRTHREE_LENGTH, left.y);
			Vector2f bottom = new Vector2f(left.x + HEXAGON_HALF_SQRTHREE_LENGTH, left.y + 0.5f * HEXAGON_SIDE_LENGTH);
			float leftSize = Maths.areaOfTriangle(bottom, left, new Vector2f(worldX, worldZ));
			float rightSize = Maths.areaOfTriangle(bottom, right, new Vector2f(worldX, worldZ));
			boolean inFromLeft = leftSize <= HEXAGON_MINIMUM_TRIANGLE_SIZE;
			boolean inFromRight = rightSize <= HEXAGON_MINIMUM_TRIANGLE_SIZE;
			if (inFromLeft && inFromRight) {
				return terrains[terrainGridZ][terrainGridX].heights[terrainHexZ][terrainHexX];
			} else if (inFromLeft) {
				// is in left hexagon
				return terrains[terrainGridZ - 1][terrainGridX - 1 + isOffset].heights[terrainHexZ][terrainHexX];
			} else if (inFromRight) {
				// is in right hexagon
				return terrains[terrainGridZ - 1][terrainGridX + isOffset].heights[terrainHexZ][terrainHexX];
			} else {
				System.out.println("im a poop");
			}
		}
		return 0;
	}

	public static boolean pointFromAboveInHexagon(Vector2f point, Vector2f hexagonCenter) {
		Vector2f leftPoint = new Vector2f(hexagonCenter.x - HEXAGON_HALF_SQRTHREE_LENGTH, hexagonCenter.y - 0.5f * HEXAGON_SIDE_LENGTH);
		Vector2f rightPoint = new Vector2f(hexagonCenter.x + HEXAGON_HALF_SQRTHREE_LENGTH, leftPoint.y);
		Vector2f bottomPoint = new Vector2f(hexagonCenter.x, leftPoint.y);
//		if() {
//			
//		}
		return false;
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
					Vector3f center = new Vector3f(referencePointX + HEXAGON_HALF_SQRTHREE_LENGTH, height, referencePointZ + HEXAGON_SIDE_LENGTH);
					for (int i = 0; i < 6; i++) {
						Vector3f vertice = new Vector3f(vertices[startingVerticeIndex + i * 3], height, vertices[startingVerticeIndex + i * 3 + 2]);
						Vector3f normal = calculateHexagonMeshNormal(center, vertice);
//						normal = new Vector3f(0, 1, 0);
						normals[startingVerticeIndex + i * 3] = normal.x;
						normals[startingVerticeIndex + i * 3 + 1] = normal.y;
						normals[startingVerticeIndex + i * 3 + 2] = normal.z;
					}
					int startingTextureIndex = 12 * (rowNumber * gridSizeX + columnNumber);
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
}
