package data;

import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.Loader;
import textures.ModelTexture;
import trees.Tree;

public class EntityData {

	public static TexturedModel fernModel;
	public static TexturedModel oakTreeStage0Model;
//	public static TexturedModel oakTreeStage1Model;

	public static void init() {
		Tree.initializeHashmaps();
		initializeModels();
	}

	private static void initializeModels() {
		ModelData fernData = OBJFileLoader.loadOBJ("fern");
		RawModel rawFernModel = Loader.loadToVAO(fernData);
		ModelTexture fernTextureAtlas = new ModelTexture(Loader.loadTexture("fern"));
		fernTextureAtlas.setTextureGridSize(2);
		fernModel = new TexturedModel(rawFernModel, fernTextureAtlas);
		fernModel.getTexture().setHasTransparency(true);
		fernModel.getTexture().setUseFakeLighting(true);
		ModelData oakTreeStage0Data = OBJFileLoader.loadOBJ("oakTreeStage0");
		RawModel rawOakTreeStage0Model = Loader.loadToVAO(oakTreeStage0Data);
		oakTreeStage0Model = new TexturedModel(rawOakTreeStage0Model, new ModelTexture(Loader.loadTexture("oakTreeStage0")));
	}

}
