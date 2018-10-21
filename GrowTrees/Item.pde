class Item extends Model {
    Item(PVector position, float scale, PShape model) {
        super(position, new PVector (0, 0, 0), scale, model);
    }
}

class Poop extends Model {
    Poop(PVector position, float scale, PShape model) {
        super(position, new PVector (0, 0, 0), scale, model);
    }
}
