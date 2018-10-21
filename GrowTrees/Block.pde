abstract class Block {
    static final int BLOCK_SIZE = 30;
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
        rotateY(-HALF_PI);
        translate(-gridZ * BLOCK_SIZE, gridX * BLOCK_SIZE, -gridY * BLOCK_SIZE);
        box(BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        popMatrix();
    }

    void display(Block[][][] map) {
        int amountAround = 0;
        try {
            for (int i = -1; i < 2; i++) {
                if ((map[gridX + i][gridY][gridZ] != null || !(map[gridX + i][gridY][gridZ] instanceof Air)) && i !=0) {
                    amountAround++;
                }
            }
        } catch(IndexOutOfBoundsException e){
        
        }
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
        c = color(0, 191, 255, 30);
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
