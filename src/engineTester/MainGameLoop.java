package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.Light;
import entities.Player;
import entities.ThirdPersonCamera;
import guis.GUIRenderer;
import guis.GUITexture;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		ModelData fernData = OBJFileLoader.loadOBJ("fern");
		RawModel rawFernModel = loader.loadToVAO(fernData);
		TexturedModel fernModel = new TexturedModel(rawFernModel, new ModelTexture(loader.loadTexture("fern")));
		fernModel.getTexture().setHasTransparency(true);
		fernModel.getTexture().setUseFakeLighting(true);

		ModelData oakTreeStage1Data = OBJFileLoader.loadOBJ("oakTreeStage1");
		RawModel rawOakTreeStage1Model = loader.loadToVAO(oakTreeStage1Data);
		TexturedModel oakTreeStage1Model = new TexturedModel(rawOakTreeStage1Model, new ModelTexture(loader.loadTexture("oakTreeStage1")));
//		oakTreeStage1Data.

		List<Entity> ferns = new ArrayList<Entity>();
		List<Entity> oaks = new ArrayList<Entity>();
		ArrayList<Terrain> terrains = new ArrayList<Terrain>();

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		terrains.add(new Terrain(0, 0, loader, texturePack, blendMap, "heightmap"));
//		terrains.add(new Terrain(1, 0, loader, texturePack, blendMap, "heightmap"));

		Random random = new Random();
		for (int i = 0; i < 50; i++) {
			float entityX = random.nextFloat() * 800;
			float entityZ = random.nextFloat() * 800;
			float entityY = findCurrentTerrain(entityX, entityZ, terrains).getHeightOfTerrain(entityX, entityZ);
			ferns.add(new Entity(fernModel, new Vector3f(entityX, entityY, entityZ), 0, 0, 0, 3));
		}
		for (int i = 0; i < 5000; i++) {
			float entityX = random.nextFloat() * 800;
			float entityZ = random.nextFloat() * 800;
			float entityY = findCurrentTerrain(entityX, entityZ, terrains).getHeightOfTerrain(entityX, entityZ);
			oaks.add(new Entity(oakTreeStage1Model, new Vector3f(entityX, entityY, entityZ), 0, random.nextFloat() * 360, 0, 3));
		}

		Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(1, 1, 1));

		ModelTexture whiteTexture = new ModelTexture(loader.loadTexture("white"));
		ModelData stanfordBunnyData = OBJFileLoader.loadOBJ("bunny");
		RawModel rawStanfordBunnyModel = loader.loadToVAO(stanfordBunnyData);
		TexturedModel stanfordBunnyModel = new TexturedModel(rawStanfordBunnyModel, whiteTexture);

		List<GUITexture> guis = new ArrayList<GUITexture>();
		GUITexture gui = new GUITexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);

		Player player = new Player(stanfordBunnyModel, new Vector3f(100, 0, 150), 0.0f, 0.0f, 0.0f, 1.0f);
		ThirdPersonCamera camera = new ThirdPersonCamera(player);

		MasterRenderer masterRenderer = new MasterRenderer();
		GUIRenderer guiRenderer = new GUIRenderer(loader);

		while (!Display.isCloseRequested()) {
			Terrain playerTerrain = findCurrentTerrain(player.getPosition().x, player.getPosition().z, terrains);
			camera.move();
			player.move(playerTerrain);
			masterRenderer.processEntity(player);
			for (Terrain terrain : terrains) {
				masterRenderer.processTerrain(terrain);
			}
			for (Entity entity : ferns) {
				masterRenderer.processEntity(entity);
			}
			for (Entity entity : oaks) {
				masterRenderer.processEntity(entity);
			}
			masterRenderer.render(light, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}

		guiRenderer.cleanUp();
		masterRenderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

	private static Terrain findCurrentTerrain(float playerX, float playerZ, ArrayList<Terrain> terrains) {
		for (Terrain terrain : terrains) {
			if (playerX > terrain.getX() && playerX < terrain.getX() + Terrain.SIZE && playerZ > terrain.getZ() && playerZ < terrain.getZ() + Terrain.SIZE) {
				return terrain;
			}
		}
		return terrains.get(0);
	}

}
