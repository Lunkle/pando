class Tree extends Model {  
    String name;
    int seedDispersal;
    float treeHeight;
    float treeAge;

    Tree(PVector position, PVector rotation, float scale, PShape model, String name, int seedDispersal, float treeHeight, float treeAge) {
        super(position, rotation, scale, model);
        this.name = name;
        this.seedDispersal = seedDispersal;
        this.treeHeight = treeHeight;
        this.treeAge = treeAge;
    }


    void killTree() {
    }

    void plantTree(Tree tree) {
        tree.seedDispersal --;
        //spawn tree
    }
}
