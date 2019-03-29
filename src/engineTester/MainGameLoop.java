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
		// main
		DisplayManager.createDisplay();
		Loader loader = new Loader();

		ModelData fernData = OBJFileLoader.loadOBJ("fern");
		RawModel rawFernModel = loader.loadToVAO(fernData);
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setTextureGridSize(2);
		TexturedModel fernModel = new TexturedModel(rawFernModel, fernTextureAtlas);
		fernModel.getTexture().setHasTransparency(true);
		fernModel.getTexture().setUseFakeLighting(true);

		ModelData oakTreeStage1Data = OBJFileLoader.loadOBJ("oakTreeStage1");
		RawModel rawOakTreeStage1Model = loader.loadToVAO(oakTreeStage1Data);
		TexturedModel oakTreeStage1Model = new TexturedModel(rawOakTreeStage1Model, new ModelTexture(loader.loadTexture("oakTreeStage1")));

		List<Entity> ferns = new ArrayList<Entity>();
		List<Entity> oaks = new ArrayList<Entity>();
		Entity centerSprout = new Entity(oakTreeStage1Model, new Vector3f(0, 0, 0), 0, 0, 0, 5);
		Terrain[][] terrains = new Terrain[1][1];

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		for (int i = 0; i < terrains.length; i++) {
			for (int j = 0; j < terrains[i].length; j++) {
				terrains[j][i] = (new Terrain(j, i, loader, texturePack, blendMap, "map"));
			}
		}

		Random random = new Random();
		for (int i = 0; i < 50; i++) {
			float entityX = random.nextFloat() * 800;
			float entityZ = random.nextFloat() * 800;
			float entityY = Terrain.findCurrentTerrain(entityX, entityZ, terrains).getHeightOfHexagonMeshTerrain(entityX, entityZ);
			ferns.add(new Entity(fernModel, random.nextInt(4), new Vector3f(entityX, entityY, entityZ), 0, 0, 0, 3));
		}
		for (int i = 0; i < 5000; i++) {
			float entityX = random.nextFloat() * 800;
			float entityZ = random.nextFloat() * 800;
			float entityY = Terrain.findCurrentTerrain(entityX, entityZ, terrains).getHeightOfHexagonMeshTerrain(entityX, entityZ);
			oaks.add(new Entity(oakTreeStage1Model, new Vector3f(entityX, entityY, entityZ), 0, random.nextFloat() * 360, 0, 3));
		}

		Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(1, 1, 1));

		ModelTexture whiteTexture = new ModelTexture(loader.loadTexture("white"));
		ModelData stanfordBunnyData = OBJFileLoader.loadOBJ("bunny");
		RawModel rawStanfordBunnyModel = loader.loadToVAO(stanfordBunnyData);
		TexturedModel stanfordBunnyModel = new TexturedModel(rawStanfordBunnyModel, whiteTexture);

		List<GUITexture> guis = new ArrayList<GUITexture>();
		GUITexture gui = new GUITexture(loader.loadTexture("dukemascot"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);

		Player player = new Player(stanfordBunnyModel, new Vector3f(10, 0, 15), 0.0f, 0.0f, 0.0f, 0.5f);
		ThirdPersonCamera camera = new ThirdPersonCamera(player);

		MasterRenderer masterRenderer = new MasterRenderer();
		GUIRenderer guiRenderer = new GUIRenderer(loader);

		while (!Display.isCloseRequested()) {
			Terrain playerTerrain = Terrain.findCurrentTerrain(player.getPosition().x, player.getPosition().z, terrains);
			player.move(playerTerrain);
			camera.move();
			masterRenderer.processEntity(player);
			for (Terrain[] terrainArray : terrains) {
				for (Terrain terrain : terrainArray) {
					masterRenderer.processTerrain(terrain);
				}
			}

			Vector3f pPos = player.getPosition();
			float[] coords = terrains[0][0].pixel_to_pointy_hex(pPos.x, pPos.z);
			centerSprout.setPosition(new Vector3f(coords[0] * Terrain.HEXAGON_SQRTHREE_LENGTH + Terrain.HEXAGON_HALF_SQRTHREE_LENGTH, 0, coords[2] * 2 * Terrain.HEXAGON_SIDE_LENGTH + Terrain.HEXAGON_SIDE_LENGTH));

//			System.out.println(pPos + "\t" + centerSprout.getPosition());

			masterRenderer.processEntity(centerSprout);

			masterRenderer.render(light, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}

		guiRenderer.cleanUp();
		masterRenderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
