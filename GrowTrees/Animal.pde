class Animal extends Model {
    int size;
    Animal(PVector position, float rotation, float scale, PShape model, int size) { 
        super(position, rotation, scale, model);
        this.rotation = rotation;
        this.scale = scale;
        this.model = model;
        this.size = size;
    }

    void die() {
    }

    void excrete() {
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
