class Model {
    PVector position;
    PVector rotation;
    float scale;
    PShape model;

    Model(PVector position, PVector rotation, float scale, PShape model) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.model = model;
    }
}
