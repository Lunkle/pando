package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import data.PlayerData;
import data.TerrainData;
import entities.Entity;
import entities.FloatingCamera;
import entities.Light;
import entities.Player;
import guis.GUIRenderer;
import guis.GUITexture;
import models.RawModel;
import models.TexturedModel;
import network.Client;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;

public class MainGameLoop {

	public static void main(String[] args) {
		PlayerData playerdata = new PlayerData();

		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		// main
		Client client = new Client(ShutdownHook.socket, ShutdownHook.socketStream);
		try {
			client.connect();
		} catch (java.io.IOException io) {
			System.out.println(io);
		}
		
//		System.out.println(client.socket.equals(ShutdownHook.socket));

		DisplayManager.createDisplay();

		ModelData fernData = OBJFileLoader.loadOBJ("fern");
		RawModel rawFernModel = Loader.loadToVAO(fernData);
		ModelTexture fernTextureAtlas = new ModelTexture(Loader.loadTexture("fern"));
		fernTextureAtlas.setTextureGridSize(2);
		TexturedModel fernModel = new TexturedModel(rawFernModel, fernTextureAtlas);
		fernModel.getTexture().setHasTransparency(true);
		fernModel.getTexture().setUseFakeLighting(true);
		
		ModelData playerNewData = OBJFileLoader.loadOBJ("bunny");
		RawModel rawPlayerNewData = Loader.loadToVAO(playerNewData);
		TexturedModel playerNewModel = new TexturedModel(rawPlayerNewData, new ModelTexture(Loader.loadTexture("image")));

		ModelData oakTreeStage1Data = OBJFileLoader.loadOBJ("oakTreeStage1");
		RawModel rawOakTreeStage1Model = Loader.loadToVAO(oakTreeStage1Data);
		TexturedModel oakTreeStage1Model = new TexturedModel(rawOakTreeStage1Model, new ModelTexture(Loader.loadTexture("oakTreeStage1")));
		
		ModelData mediumTree1Data = OBJFileLoader.loadOBJ("lowPolyTree");
		RawModel rawMediumTree1Model = Loader.loadToVAO(mediumTree1Data);
		TexturedModel mediumTree1Model = new TexturedModel(rawMediumTree1Model, new ModelTexture(Loader.loadTexture("lowPolyTree")));

//		ModelData oakTreeStage2Data = OBJFileLoader.loadOBJ("oakTreeStage1");
//		RawModel rawOakTreeStage2Model = loader.loadToVAO(oakTreeStage2Data);
//		TexturedModel oakTreeStage2Model = new TexturedModel(rawOakTreeStage2Model, new ModelTexture(loader.loadTexture("oakTreeStage2")));

//		TerrainGen gen = new TerrainGen(100, 10, 100, 10, "map", "res");
//		gen.makeDefaultFile();

		List<Entity> ferns = new ArrayList<Entity>();
		List<Entity> oaks = new ArrayList<Entity>();
		List<Entity> trees = new ArrayList<Entity>();
		Entity centerSprout = new Entity(playerNewModel, new Vector3f(0, 0, 0), 0, 0, 0, 0.25f);

		TerrainTexture backgroundTexture = new TerrainTexture(Loader.loadTexture("green"));
		TerrainTexture rTexture = new TerrainTexture(Loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(Loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(Loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(Loader.loadTexture("blendMap"));

		TerrainData terrainData = new TerrainData(10, 10, texturePack, blendMap);

		Random random = new Random();
		random.setSeed(1234);
		for (int i = 0; i < 600; i++) {
			float entityX = 5 + random.nextFloat() * 9.9f * Terrain.X_SIZE;
			float entityZ = 5 + random.nextFloat() * 9.9f * Terrain.Z_SIZE;
			float entityY = terrainData.getHeightByWorldCoords(entityX, entityZ);
			ferns.add(new Entity(fernModel, random.nextInt(4), new Vector3f(entityX, entityY, entityZ), 0, 0, 0, 1));
		}
		for (int i = 0; i < 1000; i++) {
			float entityX = 5 + random.nextFloat() * 9.9f * Terrain.X_SIZE;
			float entityZ = 5 + random.nextFloat() * 9.9f * Terrain.Z_SIZE;
			float entityY = terrainData.getHeightByWorldCoords(entityX, entityZ);
			oaks.add(new Entity(oakTreeStage1Model, new Vector3f(entityX, entityY, entityZ), 0, random.nextFloat() * 360, 0, 3));
		}
		for (int i = 0; i < 150; i++) {
			float entityX = 5 + random.nextFloat() * 9.9f * Terrain.X_SIZE;
			float entityZ = 5 + random.nextFloat() * 9.9f * Terrain.Z_SIZE;
			float entityY = terrainData.getHeightByWorldCoords(entityX, entityZ);
			trees.add(new Entity(mediumTree1Model, new Vector3f(entityX, entityY, entityZ), 0, random.nextFloat() * 360, 0, 1));
		} 

		Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(1, 1, 1));

		List<GUITexture> guis = new ArrayList<GUITexture>();
		GUITexture gui = new GUITexture(Loader.loadTexture("grassy"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);

		Player player = new Player(oakTreeStage1Model, new Vector3f(10, 0, 15), 0.0f, 0.0f, 0.0f, 0.1f);
		FloatingCamera camera = new FloatingCamera(100, 100);
//		ThirdPersonCamera camera = new ThirdPersonCamera(player);

		MasterRenderer masterRenderer = new MasterRenderer();
		GUIRenderer guiRenderer = new GUIRenderer();

		MousePicker picker = new MousePicker(camera, masterRenderer.getProjectionMatrix());
		System.out.println(terrainData.getHexagonByDirection(TerrainData.Direction.DOWN_RIGHT, 0, 1));
		Vector2f mousePos = new Vector2f(0, 0);
		Vector2f mousePress = new Vector2f(0, 0);
		boolean wasPressed = false;
		
		while (!Display.isCloseRequested()) {
			mousePos.x = Mouse.getX();
			mousePos.y = Mouse.getY();
			
			if (Mouse.isButtonDown(0)) {
				if (!wasPressed) {
					wasPressed = true;
					mousePress = new Vector2f(mousePos);
					System.out.println("yay");
				} 
			} else if (wasPressed) {
				if (mousePos.equals(mousePress)) {
					trees.add(new Entity(mediumTree1Model, picker.findCoords(terrainData), 0, random.nextFloat() * 360, 0, 1));
					wasPressed = false;
				}
				wasPressed = false;
			}
			
			player.move(terrainData);
			camera.move(terrainData);

			picker.update(terrainData, player);

			masterRenderer.processEntity(player);
			masterRenderer.processTerrainData(terrainData, camera);
			for (Entity fern : ferns) {
				masterRenderer.processEntity(fern);
			}
			for (Entity oak : oaks) {
				masterRenderer.processEntity(oak);
			}
			for (Entity tree : trees) {
				masterRenderer.processEntity(tree);
			}
			Vector3f pPos = player.getPosition();
			Vector2f coords = terrainData.getHexagon(pPos.x, pPos.z);
			float height = terrainData.getHeightByHexCoords(coords);
			Vector2f posCoords = terrainData.hexToWorldCoords(coords);
//			centerSprout.setPosition(posCoords.x, height, posCoords.y);

//			masterRenderer.processEntity(centerSprout);

			masterRenderer.render(light, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}

		guiRenderer.cleanUp();
		masterRenderer.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
