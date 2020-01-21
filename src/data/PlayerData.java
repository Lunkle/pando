package data;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import terrains.Territory;

public class PlayerData {

	List<Territory> territories = new ArrayList<Territory>();
	int playerID = 4;
	private int seeds = 3000;

	public PlayerData() {
//		loadDataFromFiles();
//		loadDataFromServer();
		System.out.println("hi");
		saveData();
	}

	private void loadDataFromFiles() {
		Gson gson = new Gson();
		PlayerData data = null;
		try {
			data = gson.fromJson(new FileReader("res/saveData/playerData/localData.json"), PlayerData.class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			System.out.println("Could not find local player data");
			e.printStackTrace();
			return;
		}
		this.seeds = data.seeds;
		this.playerID = data.playerID;
		this.territories = data.territories;
	}

	private void loadDataFromServer() {
		// TODO Auto-generated method stub

	}

	private void saveData() {
		Gson gson = new Gson();
		BufferedWriter writer = null;
		String data = gson.toJson(this);
		try {
			System.out.println("tryna save da data");
			gson.toJson(this, new FileWriter("res/saveData/playerData/localData.json"));
		} catch (JsonIOException | IOException e) {
			System.out.println("Somehow cant write player data, I hate my life");
			e.printStackTrace();
		}
	}

}
