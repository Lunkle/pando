package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.Light;
import entities.Player;
import entities.ThirdPersonCamera;
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

		List<Entity> ferns = new ArrayList<Entity>();
		List<Terrain> terrains = new ArrayList<Terrain>();

		Random random = new Random();
		for (int i = 0; i < 50; i++) {
			ferns.add(new Entity(fernModel, new Vector3f(random.nextFloat() * 800, 0, random.nextFloat() * 600), 0, 0, 0, 3));
		}

		Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(1, 1, 1));

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		terrains.add(new Terrain(0, 0, loader, texturePack, blendMap, "heightmap"));
//		terrains.add(new Terrain(1, 0, loader, texturePack, blendMap, "heightmap"));

		ModelTexture whiteTexture = new ModelTexture(loader.loadTexture("white"));
		ModelData stanfordBunnyData = OBJFileLoader.loadOBJ("bunny");
		RawModel rawStanfordBunnyModel = loader.loadToVAO(stanfordBunnyData);
		TexturedModel stanfordBunnyModel = new TexturedModel(rawStanfordBunnyModel, whiteTexture);

		Player player = new Player(stanfordBunnyModel, new Vector3f(100, 0, 150), 0.0f, 0.0f, 0.0f, 1.0f);
		ThirdPersonCamera camera = new ThirdPersonCamera(player);
		MasterRenderer renderer = new MasterRenderer();

		while (!Display.isCloseRequested()) {
			camera.move();
			player.move();
			renderer.processEntity(player);
			for (Terrain terrain : terrains) {
				renderer.processTerrain(terrain);
			}
			for (Entity entity : ferns) {
				renderer.processEntity(entity);
			}
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
