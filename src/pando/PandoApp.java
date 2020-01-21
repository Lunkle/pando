package pando;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import data.EntityData;
import data.PlayerData;
import data.TerrainData;
import data.UpdaterThread;
import entities.Entity;
import entities.FloatingCamera;
import entities.Light;
import entities.Player;
import guis.GUIRenderer;
import guis.GUITexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;

public class PandoApp {

	public static void main(String[] args) {

		PlayerData playerdata = new PlayerData();

		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		// main
//		Client client = new Client(ShutdownHook.socket, ShutdownHook.socketStream);
//		try {
//			client.connect();
//		} catch (java.io.IOException io) {
//			System.out.println(io);
//		}
//		System.out.println(client.socket.equals(ShutdownHook.socket));

		System.out.println("hi");
		DisplayManager.createDisplay();
		EntityData.init();

//		ModelData oakTreeStage2Data = OBJFileLoader.loadOBJ("oakTreeStage1");
//		RawModel rawOakTreeStage2Model = loader.loadToVAO(oakTreeStage2Data);
//		TexturedModel oakTreeStage2Model = new TexturedModel(rawOakTreeStage2Model, new ModelTexture(loader.loadTexture("oakTreeStage2")));

//		TerrainGen gen = new TerrainGen(100, 10, 100, 10, "map", "res");
//		gen.makeDefaultFile();

		List<Entity> ferns = new ArrayList<Entity>();
		List<Entity> oaks = new ArrayList<Entity>();
		Entity centerSprout = new Entity(EntityData.oakTreeStage0Model, new Vector3f(0, 0, 0), 0, 0, 0, 1);

		TerrainTexture backgroundTexture = new TerrainTexture(Loader.loadTexture("green"));
		TerrainTexture rTexture = new TerrainTexture(Loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(Loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(Loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(Loader.loadTexture("blendMap"));

//		TerrainData serverTerrainData = new TerrainData(10, 10, texturePack, blendMap);
		TerrainData clientTerrainData = new TerrainData(10, 10, texturePack, blendMap);

		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			float entityX = 10 + random.nextFloat() * 50;
			float entityZ = 10 + random.nextFloat() * 50;
			float entityY = clientTerrainData.getHeightByWorldCoords(entityX, entityZ);
			ferns.add(new Entity(EntityData.fernModel, random.nextInt(4), new Vector3f(entityX, entityY, entityZ), 0, 0, 0, 1));
		}
		for (int i = 0; i < 10; i++) {
			float entityX = 10 + random.nextFloat() * 50;
			float entityZ = 10 + random.nextFloat() * 50;
			float entityY = clientTerrainData.getHeightByWorldCoords(entityX, entityZ);
			oaks.add(new Entity(EntityData.oakTreeStage0Model, new Vector3f(entityX, entityY, entityZ), 0, random.nextFloat() * 360, 0, 3));
		}

		Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(1, 1, 1));

		List<GUITexture> guis = new ArrayList<GUITexture>();
		GUITexture gui = new GUITexture(Loader.loadTexture("seeds"), new Vector2f(1000, 10), new Vector2f(128, 128));
		guis.add(gui);

		Player player = new Player(EntityData.oakTreeStage0Model, new Vector3f(10, 0, 15), 0.0f, 0.0f, 0.0f, 1f);
		FloatingCamera camera = new FloatingCamera(100, 100);
//		ThirdPersonCamera camera = new ThirdPersonCamera(player);

		MasterRenderer masterRenderer = new MasterRenderer();
		GUIRenderer guiRenderer = new GUIRenderer();

		MousePicker picker = new MousePicker(camera, masterRenderer.getProjectionMatrix());

		UpdaterThread updater = new UpdaterThread();
		updater.start();

		while (!Display.isCloseRequested()) {
			player.move(clientTerrainData);
			camera.move(clientTerrainData);

			picker.update(clientTerrainData, player);

			masterRenderer.processEntity(player);
			masterRenderer.processTerrainData(clientTerrainData, camera);
			for (Entity fern : ferns) {
				masterRenderer.processEntity(fern);
			}
			Vector3f pPos = player.getPosition();
			Vector2f coords = clientTerrainData.getHexagon(pPos.x, pPos.z);
			float height = clientTerrainData.getHeightByHexCoords(coords);
			Vector2f posCoords = clientTerrainData.hexToWorldCoords(coords);
			centerSprout.setPosition(posCoords.x, height, posCoords.y);

			masterRenderer.processEntity(centerSprout);

			masterRenderer.render(light, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}

		guiRenderer.cleanUp();
		masterRenderer.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
		updater.setDoneThread(true);

	}

}
