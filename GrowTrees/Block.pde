abstract class Block {
    static final int BLOCK_SIZE = 100;
    int gridX, gridY, gridZ;
    color c;

    Block(int gridX, int gridY, int gridZ) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.gridZ = gridZ;
    }

    void display() {
        pushMatrix();
        fill(c);
        translate(-gridZ * BLOCK_SIZE, gridX * BLOCK_SIZE, -gridY * BLOCK_SIZE);
        box(BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        popMatrix();
    }
}

class Dirt extends Block {
    Dirt(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
        c = color(139, 69, 19);
    }
}

class Air extends Block {
    Air(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
        c = color(0, 191, 255, 0);
    }

    void display() {
    }
}

class Water extends Block {
    Water(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
        c = color(0, 0, 255);
    }
}

class Grass extends Block {
    Grass(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
        c = color(0, 255, 0);
    }
}
