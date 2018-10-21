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
        translate((gridX + 0.5) * BLOCK_SIZE, (gridY + 0.5) * BLOCK_SIZE, (gridZ + 0.5) * BLOCK_SIZE);
        box(BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        popMatrix();
    }

    void display(Block[][][] map) {
        //pushMatrix();
        //translate(gridZ * BLOCK_SIZE, gridX * BLOCK_SIZE, -gridY * BLOCK_SIZE);
        //if(!(map[gridX + 1][gridY][gridZ] instanceof Air)){
        //    translate(HALF_BLOCK_SIZE, 0, 0);
        //    //rotate();
        //    //rect();
        //}
        //popMatrix();
        int amountAround = 0;
        try {
            for (int i = -1; i < 2; i+=2) {
                if ((!(map[gridX + i][gridY][gridZ] instanceof Air))) {
                    amountAround++;
                }
            }

            for (int i = -1; i < 2; i+=2) {
                if ((!(map[gridX][gridY+i][gridZ] instanceof Air))) {
                    amountAround++;
                }
            }
            for (int i = -1; i < 2; i+=2) {
                if ((!(map[gridX][gridY][gridZ + i] instanceof Air))) {
                    amountAround++;
                }
            }
        }        
        catch(IndexOutOfBoundsException e) {
        }

        if (amountAround != 6) {
            display();
        }
    }
}


class Dirt extends Block {
    int nutrients;
    Dirt(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
        c = color(139, 69, 19);
        nutrients = 30;
    }
}

class Air extends Block {
    Air(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
        c = color(0, 191, 255, 30);
    }

    void display() {
        super.display();
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

class Stone extends Block {
    Stone(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
        c = color(128, 128, 128);
    }
}
