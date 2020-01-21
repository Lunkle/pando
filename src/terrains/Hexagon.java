package terrains;

import data.TerrainData;
import entities.Entity;

public class Hexagon {

	private int worldHexX;
	private int worldHexY;
	private int height;
	private int fertility;
	private Territory territory;
	Entity entity;

	public Hexagon(int x, int y, float height) {
		setWorldHexX(x);
		setWorldHexY(y);
		setTerritory(TerrainData.unowned);
	}

	public Hexagon(int x, int y, Territory territory) {
		setWorldHexX(x);
		setWorldHexY(y);
		setTerritory(territory);
	}

	public Territory getTerritory() {
		return territory;
	}

	public void setTerritory(Territory territory) {
		this.territory = territory;
	}

	public void destroyObject() {
		entity = null;
	}

	public void placeObject(Entity placeEntity) {
		entity = placeEntity;
	}

	public int getFertility() {
		return fertility;
	}

	public void setFertility(int fertility) {
		this.fertility = fertility;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWorldHexX() {
		return worldHexX;
	}

	public void setWorldHexX(int worldHexX) {
		this.worldHexX = worldHexX;
	}

	public int getWorldHexY() {
		return worldHexY;
	}

	public void setWorldHexY(int worldHexY) {
		this.worldHexY = worldHexY;
	}

}
