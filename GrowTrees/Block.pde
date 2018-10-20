class Block {
    static final int BLOCK_SIZE = 100;
    int gridX, gridY, gridZ;

    Block(int gridX, int gridY, int gridZ) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.gridZ = gridZ;
    }

    void display() {
        pushMatrix();
        translate(gridX, gridY, gridZ);
        box(BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        popMatrix();
    }
}

class Dirt extends Block {
    color c;
    Dirt(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
        c = color(139, 69, 19);
    }
}

class Air extends Block {
    color c;
    Air(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
        c = color(0, 191, 255);
    }
}

class Water extends Block {
    color c;
    Water(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
        c = color(0, 0, 255);
    }
}
