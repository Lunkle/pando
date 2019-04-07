package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.lwjgl.util.vector.Vector2f;

import entities.Camera;
import terrains.Terrain;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class TerrainData {

	// This 3D array stores the different types of data for the hexagons, which
	// currently includes height, water level, greenscale
	private int[][][] dataGrid = new int[3][][];

	public Terrain terrainGrid[][];

	public enum Direction {
		DOWN_LEFT, DOWN_RIGHT, LEFT, RIGHT, UP_LEFT, UP_RIGHT
	}

	public TerrainData(int gridSizeX, int gridSizeZ, TerrainTexturePack texturePack, TerrainTexture blendMap) {
		terrainGrid = new Terrain[gridSizeZ][gridSizeX];
		for (int i = 0; i < terrainGrid.length; i++) {
			for (int j = 0; j < terrainGrid[i].length; j++) {
				terrainGrid[j][i] = (new Terrain(j, i, texturePack, blendMap, "map"));
			}
		}
	}

	public static void readData(String location) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("res/terrainData/" + location + ".txt"));
		} catch (IOException e) {
			System.out.println("Failed to read any data from height map at res/" + location + ".txt");
			e.printStackTrace();
		}
		try {
			String line;
			try (Stream<String> lines = Files.lines(Paths.get("res/terrainData/" + location + ".txt"))) {
				line = lines.skip(100).findFirst().get();
			}
			System.out.println(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadTerrainDataFromFile() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("res/terrainData.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Vector2f hexToWorldCoords(Vector2f hexCoords) {
		return hexToWorldCoords((int) hexCoords.x, (int) hexCoords.y);
	}

	public static Vector2f hexToWorldCoords(int hexCoordX, int hexCoordZ) {
		return new Vector2f((hexCoordZ % 2) * Terrain.HEX_HALF_SQRT3 + (hexCoordX + 0.5f) * Terrain.HEX_SQRT3, (hexCoordZ * 1.5f + 1) * Terrain.HEX_SIDE);
	}

	public Vector2f getHexagon(Vector2f worldLocation) {
		return getHexagon(worldLocation.x, worldLocation.y);
	}

	public Vector2f getHexagon(float worldX, float worldZ) {
		int worldHexZ = (int) Math.floor(worldZ / (Terrain.HEX_SIDE * 1.5));
		int isOffset = worldHexZ % 2;
		float xOffset = Terrain.HEX_HALF_SQRT3 * isOffset;
		int worldHexX = (int) Math.floor((worldX - xOffset) / Terrain.HEX_SQRT3);

		float tileZ = worldZ % (Terrain.HEX_SIDE * 1.5f);
		int terrainGridX = (int) Math.floor(worldHexX / Terrain.NUM_HEXAGONS_X);
		int terrainGridZ = (int) Math.floor(worldHexZ / Terrain.NUM_HEXAGONS_Z);

		if (terrainGridX < 0 || terrainGridX > terrainGrid[0].length || terrainGridZ < 0 || terrainGridZ > terrainGrid.length) {
			System.out.println("out of bounds");
			return null;
		}

		if (tileZ > 0.5f * Terrain.HEX_SIDE) {
			return new Vector2f(worldHexX, worldHexZ);
		} else {
			Vector2f left = new Vector2f(xOffset + worldHexX * Terrain.HEX_SQRT3, (worldHexZ + 1) * 1.5f * Terrain.HEX_SIDE);
			Vector2f right = new Vector2f(left.x + Terrain.HEX_SQRT3, left.y);
			Vector2f bottom = new Vector2f(left.x + Terrain.HEX_HALF_SQRT3, left.y + 0.5f * Terrain.HEX_SIDE);
			float leftSize = Maths.areaOfTriangle(bottom, left, new Vector2f(worldX, worldZ));
			float rightSize = Maths.areaOfTriangle(bottom, right, new Vector2f(worldX, worldZ));
			boolean inFromLeft = leftSize <= Terrain.HEX_MIN_TRI_AREA;
			boolean inFromRight = rightSize <= Terrain.HEX_MIN_TRI_AREA;
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

	public float getHeightByWorldCoords(float worldPosX, float worldPosZ) {
		Vector2f worldHexCoords = getHexagon(worldPosX, worldPosZ);
		return getHeightByHexCoords((int) worldHexCoords.x, (int) worldHexCoords.y);
	}

	public float getHeightByHexCoords(int worldHexX, int worldHexZ) {
		int terrainGridX = (int) Math.floor(worldHexX / Terrain.NUM_HEXAGONS_X);
		int terrainGridZ = (int) Math.floor(worldHexZ / Terrain.NUM_HEXAGONS_Z);
		int terrainHexX = worldHexX % Terrain.NUM_HEXAGONS_X;
		int terrainHexZ = worldHexZ % Terrain.NUM_HEXAGONS_Z;
		return terrainGrid[terrainGridZ][terrainGridX].heights[terrainHexZ][terrainHexX];
	}

	public Vector2f getHexagonByDirection(Direction direction, int hexX, int hexZ) {
		int offset = hexZ % 2;
		switch (direction) {
		case DOWN_LEFT:
			return new Vector2f(hexX - 1 + offset, hexZ - 1);
		case DOWN_RIGHT:
			return new Vector2f(hexX + offset, hexZ - 1);
		case LEFT:
			return new Vector2f(hexX - 1, hexZ);
		case RIGHT:
			return new Vector2f(hexX + 1, hexZ);
		case UP_LEFT:
			return new Vector2f(hexX - 1 + offset, hexZ + 1);
		case UP_RIGHT:
			return new Vector2f(hexX + offset, hexZ + 1);
		default:
			return null;
		}
	}

	public Vector2f getTerrain(Vector2f worldCoords, int range) {
		return getTerrain(worldCoords.x, worldCoords.y, range);
	}

	public Vector2f getTerrain(float worldPosX, float worldPosZ, int range) {
		int terrainGridX = (int) Math.floor(worldPosX / Terrain.X_SIZE);
		int terrainGridZ = (int) Math.floor(worldPosZ / Terrain.Z_SIZE);
		return new Vector2f(terrainGridX, terrainGridZ);
	}

	private int getTerrainBoundedX(int value) {
		return Math.max(0, Math.min(value, terrainGrid[0].length - 1));
	}

	private int getTerrainBoundedY(int value) {
		return Math.max(0, Math.min(value, terrainGrid.length - 1));
	}

	public Terrain[] getClosestTerrains(Camera camera, int range) {
		Vector2f cameraCoords = new Vector2f(camera.getPosition().x, camera.getPosition().z);
		Vector2f centerTerrain = getTerrain(cameraCoords, range);
		int yMin = getTerrainBoundedY((int) (centerTerrain.y - range));
		int yMax = getTerrainBoundedY((int) (centerTerrain.y + range));
		int xMin = getTerrainBoundedX((int) (centerTerrain.x - range));
		int xMax = getTerrainBoundedX((int) (centerTerrain.x + range));
		Terrain[] terrains = new Terrain[(yMax - yMin + 1) * (xMax - xMin + 1)];
		int index = 0;
		for (int i = yMin; i < yMax + 1; i++) {
			for (int j = xMin; j < xMax + 1; j++) {
				terrains[index] = terrainGrid[j][i];
				index++;
			}
		}
		return terrains;
	}

}
