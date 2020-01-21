package trees;

import java.util.HashMap;
import java.util.Map;

import models.TexturedModel;

public class Tree {

	public enum TreeType {
		OAK_TREE, SPRUCE_TREE, PANDO_TREE, PINE_TREE
	}

	private static Map<TreeType, Integer> ageMap = new HashMap<TreeType, Integer>();
	private static Map<TreeType, HashMap<Integer, TexturedModel>> modelMap = new HashMap<TreeType, HashMap<Integer, TexturedModel>>();

	private TreeType type;
	private int age = 0;
	private TexturedModel model;

	private boolean maxed = false;

	public static void initializeHashmaps() {
		ageMap.put(TreeType.OAK_TREE, 2);
		ageMap.put(TreeType.SPRUCE_TREE, 0);
		ageMap.put(TreeType.PANDO_TREE, 0);
		ageMap.put(TreeType.PINE_TREE, 0);
	}

	private static TexturedModel getModel(TreeType type, int age) {
		return modelMap.get(type).get(age);
	}

	private void grow() {
		if (age == ageMap.get(type)) {
			maxed = true;
		}
		if (!maxed) {
			age++;
			model = getModel(type, age);
			if (age == ageMap.get(type)) {
				maxed = true;
			}
		}

	}

	public TexturedModel getModel() {
		return model;
	}

}
