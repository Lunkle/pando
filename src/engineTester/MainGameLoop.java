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
import entities.FloatingCamera;
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
import toolbox.MousePicker;

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

//		ModelData oakTreeStage2Data = OBJFileLoader.loadOBJ("oakTreeStage2");
//		RawModel rawOakTreeStage2Model = loader.loadToVAO(oakTreeStage2Data);
//		TexturedModel oakTreeStage2Model = new TexturedModel(rawOakTreeStage2Model, new ModelTexture(loader.loadTexture("oakTreeStage2")));

		List<Entity> ferns = new ArrayList<Entity>();
		List<Entity> oaks = new ArrayList<Entity>();
		Entity centerSprout = new Entity(oakTreeStage1Model, new Vector3f(0, 0, 0), 0, 0, 0, 5);

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("green"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		Terrain[][] terrains = new Terrain[10][10];

		for (int i = 0; i < terrains.length; i++) {
			for (int j = 0; j < terrains[i].length; j++) {
				terrains[j][i] = (new Terrain(j, i, loader, texturePack, blendMap, "map"));
			}
		}

		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			float entityX = 10 + random.nextFloat() * 50;
			float entityZ = 10 + random.nextFloat() * 50;
			float entityY = Terrain.getHeightOfHexagonMeshTerrain(Terrain.getHexagon(entityX, entityZ, terrains), terrains);
//			float entityY = 0;
			ferns.add(new Entity(fernModel, random.nextInt(4), new Vector3f(entityX, entityY, entityZ), 0, 0, 0, 1));
		}
		for (int i = 0; i < 10; i++) {
			float entityX = 10 + random.nextFloat() * 50;
			float entityZ = 10 + random.nextFloat() * 50;
			float entityY = Terrain.getHeightOfHexagonMeshTerrain(Terrain.getHexagon(entityX, entityZ, terrains), terrains);
//			float entityY = 0;
			oaks.add(new Entity(oakTreeStage1Model, new Vector3f(entityX, entityY, entityZ), 0, random.nextFloat() * 360, 0, 3));
		}

		Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(1, 1, 1));

		List<GUITexture> guis = new ArrayList<GUITexture>();
		GUITexture gui = new GUITexture(loader.loadTexture("dukemascot"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);

		Player player = new Player(oakTreeStage1Model, new Vector3f(10, 0, 15), 0.0f, 0.0f, 0.0f, 0.5f);
		FloatingCamera camera = new FloatingCamera(10, 10);
//		ThirdPersonCamera camera = new ThirdPersonCamera(player);

		MasterRenderer masterRenderer = new MasterRenderer(loader);
		GUIRenderer guiRenderer = new GUIRenderer(loader);

//		TerrainGen gen = new TerrainGen(10, 10, "map", "res");
//		gen.makeDefaultFile();

		MousePicker picker = new MousePicker(camera, masterRenderer.getProjectionMatrix());

		while (!Display.isCloseRequested()) {
			player.move(terrains);
			camera.move(terrains);

			picker.update();
			System.out.println(picker.getCurrentRay());

			masterRenderer.processEntity(player);
			for (Terrain[] terrainArray : terrains) {
				for (Terrain terrain : terrainArray) {
					masterRenderer.processTerrain(terrain);
				}
			}
			for (Entity fern : ferns) {
				masterRenderer.processEntity(fern);
			}
			Vector3f pPos = player.getPosition();
			Vector2f coords = Terrain.getHexagon(pPos.x, pPos.z, terrains);
			centerSprout.setPosition(new Vector3f((coords.y % 2) * Terrain.HEXAGON_HALF_SQRTHREE_LENGTH + coords.x * Terrain.HEXAGON_SQRTHREE_LENGTH + Terrain.HEXAGON_HALF_SQRTHREE_LENGTH, 0, coords.y * 1.5f * Terrain.HEXAGON_SIDE_LENGTH + Terrain.HEXAGON_SIDE_LENGTH));

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
