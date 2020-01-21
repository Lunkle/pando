package data;

import entities.Entity;
import terrains.Hexagon;

public class Action {

	ActionType action;
	private Hexagon hexTarget;

	public enum ActionType {
		PLACE_OBJECT, DESTROY_OBJECT
	}

	public Action(ActionType type, Hexagon target) {

	}

	@Override
	public String toString() {
		return hexTarget.getWorldHexX() + " " + hexTarget.getWorldHexX();
	}

	public void processAction(TerrainData targetTerrainData) {
		switch (action) {
		case PLACE_OBJECT:
			hexTarget.placeObject(new Entity(EntityData.fernModel, hexTarget.getWorldHexX(), hexTarget.getHeight(), hexTarget.getWorldHexY(), 0, 0, 0, 1));
			break;
		case DESTROY_OBJECT:
			break;
		}
	}

}
