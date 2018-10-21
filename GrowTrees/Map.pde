class Map {
    static final int mapHeight = 4;
    int mapSizeX, mapSizeY;
    Block[][][] map;

    Map(int mapSizeX, int mapSizeY) {
        initiateMap();        
        println("the size of this map in pixels is: " + Block.BLOCK_SIZE * mapSizeX + ", " + Block.BLOCK_SIZE * mapSizeY); //Better delete this crap.
    }

    Map(int mapSizeX, int mapSizeY, String mapFile) {
        loadMap(mapFile);
        println("the size of this map in pixels is: " + Block.BLOCK_SIZE * mapSizeX + ", " + Block.BLOCK_SIZE * mapSizeY); //Better delete this crap.
    }

    Map(String mapFile) {
        loadMap(mapFile);
    }

    void initiateMap() { //TODO
        map = new Block[mapSizeX][mapSizeY][mapHeight];
    }

    void loadMap(String mapFile) {
        int buffer = 0;
        String[] temp = loadStrings(mapFile+".txt");
        int mapSizeX = int(temp[0]), mapSizeY = int(temp[1]);
        map = new Block[mapSizeX][mapSizeY][mapHeight];
        for (int k  = 0; k < mapHeight; k++)
            for (int i = 0; i < mapSizeX; i++) {
                String dataLine = temp[2 + k*mapSizeX + i];

                String[] data = dataLine.split(",");
                for (int j = 0; j < mapSizeY; j++) {
                    println(data[j]);
                    switch(data[j].charAt(0)) {
                    case 'D':
                        map[i][j][k] = new Dirt(i, j, k);
                        break;
                    case 'G':
                        map[i][j][k] = new Grass(i, j, k);
                        break;
                    case 'A':
                        map[i][j][k] = new Air(i, j, k);
                        break;
                    case 'W':
                        map[i][j][k] = new Water(i, j, k);
                        break;
                    }
                }
            }
    }

    void displayMap() {
        pushMatrix();
        //rotate(PI/4);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                for (int k = 0; k < map[i][j].length; k++) {
                    map[i][j][k].display();
                }
            }
        }
        popMatrix();
    }
}
