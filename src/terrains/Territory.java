package terrains;

import java.util.ArrayList;
import java.util.List;

public class Territory {

	private int playerID;
	private List<Hexagon> hexagons = new ArrayList<Hexagon>();

	public Territory(int id) {
		playerID = id;
	}

	public void addHexagon(Hexagon addHex) {
		hexagons.add(addHex);
		addHex.setTerritory(this);
	}

	public void addHexagons(Hexagon[] addHexes) {
		for (Hexagon addHex : addHexes) {
			addHexagon(addHex);
		}
	}

	public void addHexagons(List<Hexagon> addHexes) {
		for (Hexagon addHex : addHexes) {
			addHexagon(addHex);
		}
	}

	public int getPlayerID() {
		return playerID;
	}

}
