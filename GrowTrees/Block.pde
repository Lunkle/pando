abstract class Block {
    static final int BLOCK_SIZE = 30;
    int gridX, gridY, gridZ;

    Block(int gridX, int gridY, int gridZ) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.gridZ = gridZ;
    }

    void drawCube(boolean xPos, boolean xNeg, boolean yPos, boolean yNeg, boolean zPos, boolean zNeg) {
        pushMatrix();
        translate(gridX * BLOCK_SIZE, gridY * BLOCK_SIZE, gridZ * BLOCK_SIZE);
        if (xPos) {
            pushMatrix();
            translate(BLOCK_SIZE, 0, 0);
            rotateY(-HALF_PI);
            rect(0, 0, BLOCK_SIZE, BLOCK_SIZE);
            popMatrix();
        }
        if (xNeg) {
            pushMatrix();
            rotateY(-HALF_PI);
            rect(0, 0, BLOCK_SIZE, BLOCK_SIZE);
            popMatrix();
        }
        if (yPos) {
            pushMatrix();
            translate(0, BLOCK_SIZE, 0);
            rotateX(HALF_PI);
            rect(0, 0, BLOCK_SIZE, BLOCK_SIZE);
            popMatrix();
        }
        if (yNeg) {
            pushMatrix();
            rotateX(HALF_PI);
            rect(0, 0, BLOCK_SIZE, BLOCK_SIZE);
            popMatrix();
        }
        if (zPos) {
            pushMatrix();
            translate(0, 0, BLOCK_SIZE);
            rect(0, 0, BLOCK_SIZE, BLOCK_SIZE);
            popMatrix();
        }
        if (zNeg) {
            rect(0, 0, BLOCK_SIZE, BLOCK_SIZE);
        }
        popMatrix();
    }

    void display(Map map) {
        boolean xPos = true;
        boolean xNeg = true;
        boolean yPos = true;
        boolean yNeg = true;
        boolean zPos = true;
        boolean zNeg = true;
        if (gridX < map.mapSizeX - 1)
            xPos = map.mapData[gridZ][gridY][gridX + 1] instanceof Air;
        if (gridX > 0)
            xNeg = map.mapData[gridZ][gridY][gridX - 1] instanceof Air;
        if (gridY < map.mapSizeY - 1)
            yPos = map.mapData[gridZ][gridY + 1][gridX] instanceof Air;
        if (gridY > 0)
            yNeg = map.mapData[gridZ][gridY - 1][gridX] instanceof Air;
        if (gridZ < map.mapSizeZ - 1)
            zPos = map.mapData[gridZ + 1][gridY][gridX] instanceof Air;
        if (gridZ > 0)
            zNeg = map.mapData[gridZ - 1][gridY][gridX] instanceof Air;

        drawCube(xPos, xNeg, yPos, yNeg, zPos, zNeg);
    }
}


class Dirt extends Block {
    int nutrients;
    Dirt(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
        nutrients = 30;
    }

    void display(Map map) {
        fill(139, 69, 19);
        super.display(map);
    }
}

class Air extends Block {
    Air(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
    }

    void display(Map map) {
        //fill(0, 191, 255, 30);
        //super.display(map);
    }
}

class Water extends Block {
    Water(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
    }

    void display(Map map) {
        fill(0, 0, 255);
        super.display(map);
    }
}

class Grass extends Block {
    Grass(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
    }

    void display(Map map) {
        fill(0, 255, 0);
        super.display(map);
    }
}

class Stone extends Block {
    Stone(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
    }

    void display(Map map) {
        fill(128);
        super.display(map);
    }
}
