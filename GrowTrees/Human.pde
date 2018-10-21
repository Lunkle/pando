class Human extends Model {
    float speed;
    int intentionHostility; 
    int humanType;
    int destructiveWeapons;

    Human(PVector position, PVector rotation, float scale, PShape model, float speed, int intentionHostility, int humanType, int destructiveWeapons) {
        super(position, rotation, scale, model);
        this.intentionHostility = intentionHostility;
        this.speed = speed;
        this.humanType = humanType;
        this.destructiveWeapons = destructiveWeapons;
    }
}
