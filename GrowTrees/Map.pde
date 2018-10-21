class Map {
    static final int MAP_HEIGHT = 4;
    Block[][][] map;

    Map(String mapFile) {
        loadMap(mapFile);
    }

    void loadMap(String mapFile) {
        String[] temp = loadStrings(mapFile+".txt");
        println(temp.length);
        int mapSizeX = int(temp[0]), mapSizeY = int(temp[1]);
        map = new Block[MAP_HEIGHT][mapSizeY][mapSizeX];
        for (int i  = 0; i < MAP_HEIGHT; i++)
            for (int j = 0; j < mapSizeY; j++) {
                String dataLine = temp[2 + i*mapSizeY + j];

                String[] data = dataLine.split(",");
                for (int k = 0; k < mapSizeX; k++) {
                    switch(data[k].charAt(0)) {
                    case 'D':
                        map[i][j][k] = new Dirt(k, j, i);
                        break;
                    case 'G':
                        map[i][j][k] = new Grass(k, j, i);
                        break;
                    case 'A':
                        map[i][j][k] = new Air(k, j, i);
                        break;
                    case 'W':
                        map[i][j][k] = new Water(k, j, i);
                        break;
                    case 'S':
                        map[i][j][k] = new Stone(k, j, i);
                        break;
                    }
                }
            }

        println("Done");
    }

    void displayMap() {
        stroke(0);
        pushMatrix();
        //rotateY(PI/2);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                for (int k = 0; k < map[i][j].length; k++) {
                    map[i][j][k].display(this.map);
                }
            }
        }
        popMatrix();
    }
}
