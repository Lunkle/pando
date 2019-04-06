package data;

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

	public TerrainData(int gridSizeX, int gridSizeZ, TerrainTexturePack texturePack, TerrainTexture blendMap) {
		terrainGrid = new Terrain[gridSizeZ][gridSizeX];
		for (int i = 0; i < terrainGrid.length; i++) {
			for (int j = 0; j < terrainGrid[i].length; j++) {
				terrainGrid[j][i] = (new Terrain(j, i, texturePack, blendMap, "map"));
			}
		}
	}

	public Vector2f getHexagon(float worldX, float worldZ) {
		int worldHexZ = (int) Math.floor(worldZ / (Terrain.HEXAGON_SIDE_LENGTH * 1.5));
		int isOffset = worldHexZ % 2;
		float xOffset = Terrain.HEXAGON_HALF_SQRTHREE_LENGTH * isOffset;
		int worldHexX = (int) Math.floor((worldX - xOffset) / Terrain.HEXAGON_SQRTHREE_LENGTH);

		float tileZ = worldZ % (Terrain.HEXAGON_SIDE_LENGTH * 1.5f);
		int terrainGridX = (int) Math.floor(worldHexX / Terrain.NUM_HEXAGONS_X);
		int terrainGridZ = (int) Math.floor(worldHexZ / Terrain.NUM_HEXAGONS_Z);

		if (terrainGridX < 0 || terrainGridX > terrainGrid[0].length || terrainGridZ < 0 || terrainGridZ > terrainGrid.length) {
			System.out.println("out of bounds");
			return null;
		}

		if (tileZ > 0.5f * Terrain.HEXAGON_SIDE_LENGTH) {
			return new Vector2f(worldHexX, worldHexZ);
		} else {
			Vector2f left = new Vector2f(xOffset + worldHexX * Terrain.HEXAGON_SQRTHREE_LENGTH, (worldHexZ + 1) * 1.5f * Terrain.HEXAGON_SIDE_LENGTH);
			Vector2f right = new Vector2f(left.x + Terrain.HEXAGON_SQRTHREE_LENGTH, left.y);
			Vector2f bottom = new Vector2f(left.x + Terrain.HEXAGON_HALF_SQRTHREE_LENGTH, left.y + 0.5f * Terrain.HEXAGON_SIDE_LENGTH);
			float leftSize = Maths.areaOfTriangle(bottom, left, new Vector2f(worldX, worldZ));
			float rightSize = Maths.areaOfTriangle(bottom, right, new Vector2f(worldX, worldZ));
			boolean inFromLeft = leftSize <= Terrain.HEXAGON_MINIMUM_TRIANGLE_SIZE;
			boolean inFromRight = rightSize <= Terrain.HEXAGON_MINIMUM_TRIANGLE_SIZE;
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
		System.out.println(centerTerrain + " " + (centerTerrain.y - range));
		System.out.println(yMin + " " + yMax + " " + xMin + " " + xMax);
		for (int i = yMin; i < yMax + 1; i++) {
			for (int j = xMin; j < xMax + 1; j++) {
				terrains[index] = terrainGrid[j][i];
				index++;
			}
		}
		return terrains;
	}

}
