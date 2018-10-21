class Animal extends Model {
    int size;
    Animal(PVector position, PVector rotation, float scale, PShape model, int size) { 
        super(position, rotation, scale, model);
        this.rotation = rotation;
        this.scale = scale;
        this.model = model;
        this.size = size;
    }

    void die() {
    }

    void excrete(PVector position) {
        int gridX = round(position.x/Block.BLOCK_SIZE);
        int gridY = round(position.y/Block.BLOCK_SIZE);
        int gridZ = round(position.z/Block.BLOCK_SIZE);
        if (testMap.mapData[gridX][gridY][gridZ] instanceof Dirt) {
            ((Dirt) testMap.mapData[gridX][gridY][gridZ]).nutrients++;
        }
    }

    void breed() {
    }



    void helpTree(Tree tree) {
        tree.seedDispersal += 2;
        //tree.Height += 3;
    }

    void killTree(Tree tree) {
        //tree animation of falling down

        tree = null;
    }
}
