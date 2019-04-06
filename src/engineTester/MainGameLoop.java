package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import data.TerrainData;
import entities.Entity;
import entities.FloatingCamera;
import entities.Light;
import entities.Player;
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

		ModelData fernData = OBJFileLoader.loadOBJ("fern");
		RawModel rawFernModel = Loader.loadToVAO(fernData);
		ModelTexture fernTextureAtlas = new ModelTexture(Loader.loadTexture("fern"));
		fernTextureAtlas.setTextureGridSize(2);
		TexturedModel fernModel = new TexturedModel(rawFernModel, fernTextureAtlas);
		fernModel.getTexture().setHasTransparency(true);
		fernModel.getTexture().setUseFakeLighting(true);

		ModelData oakTreeStage1Data = OBJFileLoader.loadOBJ("oakTreeStage1");
		RawModel rawOakTreeStage1Model = Loader.loadToVAO(oakTreeStage1Data);
		TexturedModel oakTreeStage1Model = new TexturedModel(rawOakTreeStage1Model, new ModelTexture(Loader.loadTexture("oakTreeStage1")));

//		ModelData oakTreeStage2Data = OBJFileLoader.loadOBJ("oakTreeStage1");
//		RawModel rawOakTreeStage2Model = loader.loadToVAO(oakTreeStage2Data);
//		TexturedModel oakTreeStage2Model = new TexturedModel(rawOakTreeStage2Model, new ModelTexture(loader.loadTexture("oakTreeStage2")));

//		TerrainGen gen = new TerrainGen(100, 10, 100, 10, "map", "res");
//		gen.makeDefaultFile();

		List<Entity> ferns = new ArrayList<Entity>();
		List<Entity> oaks = new ArrayList<Entity>();
		Entity centerSprout = new Entity(oakTreeStage1Model, new Vector3f(0, 0, 0), 0, 0, 0, 1);

		TerrainTexture backgroundTexture = new TerrainTexture(Loader.loadTexture("green"));
		TerrainTexture rTexture = new TerrainTexture(Loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(Loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(Loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(Loader.loadTexture("blendMap"));

		TerrainData terrainData = new TerrainData(10, 10, texturePack, blendMap);

		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			float entityX = 10 + random.nextFloat() * 50;
			float entityZ = 10 + random.nextFloat() * 50;
			float entityY = terrainData.getHeightByWorldCoords(entityX, entityZ);
			ferns.add(new Entity(fernModel, random.nextInt(4), new Vector3f(entityX, entityY, entityZ), 0, 0, 0, 1));
		}
		for (int i = 0; i < 10; i++) {
			float entityX = 10 + random.nextFloat() * 50;
			float entityZ = 10 + random.nextFloat() * 50;
			float entityY = terrainData.getHeightByWorldCoords(entityX, entityZ);
			oaks.add(new Entity(oakTreeStage1Model, new Vector3f(entityX, entityY, entityZ), 0, random.nextFloat() * 360, 0, 3));
		}

		Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(1, 1, 1));

		List<GUITexture> guis = new ArrayList<GUITexture>();
		GUITexture gui = new GUITexture(Loader.loadTexture("grassy"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);

		Player player = new Player(oakTreeStage1Model, new Vector3f(10, 0, 15), 0.0f, 0.0f, 0.0f, 1f);
		FloatingCamera camera = new FloatingCamera(10, 10);
//		ThirdPersonCamera camera = new ThirdPersonCamera(player);

		MasterRenderer masterRenderer = new MasterRenderer();
		GUIRenderer guiRenderer = new GUIRenderer();

		MousePicker picker = new MousePicker(camera, masterRenderer.getProjectionMatrix());

		while (!Display.isCloseRequested()) {
			player.move(terrainData);
			camera.move(terrainData);

			picker.update(terrainData, player);
			System.out.println(picker.getCurrentRay());

			masterRenderer.processEntity(player);
			masterRenderer.processTerrainData(terrainData, camera);
			for (Entity fern : ferns) {
				masterRenderer.processEntity(fern);
			}
			Vector3f pPos = player.getPosition();
			Vector2f coords = terrainData.getHexagon(pPos.x, pPos.z);
			centerSprout.setPosition(new Vector3f((coords.y % 2) * Terrain.HEX_HALF_SQRT3 + coords.x * Terrain.HEX_SQRT3 + Terrain.HEX_HALF_SQRT3, terrainData.getHeightByHexCoords((int) coords.x, (int) coords.y), coords.y * 1.5f * Terrain.HEX_SIDE + Terrain.HEX_SIDE));

			masterRenderer.processEntity(centerSprout);

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
