class Map {
    int mapSizeX, mapSizeY;
    int mapSizeZ = 7;
    Block[][][] mapData;

    Map(String mapFile) {
        loadMap(mapFile);
    }

    void loadMap(String mapFile) {
        String[] temp = loadStrings(mapFile+".txt");
        println(temp.length);
        mapSizeX = int(temp[0]);
        mapSizeY = int(temp[1]);
        mapData = new Block[mapSizeZ][mapSizeY][mapSizeX];
        for (int i  = 0; i < mapSizeZ; i++)
            for (int j = 0; j < mapSizeY; j++) {
                String dataLine = temp[2 + i*mapSizeY + j];

                String[] data = dataLine.split(",");
                for (int k = 0; k < mapSizeX; k++) {
                    switch(data[k].charAt(0)) {
                    case 'D':
                        mapData[i][j][k] = new Dirt(k, j, i);
                        break;
                    case 'G':
                        mapData[i][j][k] = new Grass(k, j, i);
                        break;
                    case 'A':
                        mapData[i][j][k] = new Air(k, j, i);
                        break;
                    case 'W':
                        mapData[i][j][k] = new Water(k, j, i);
                        break;
                    case 'S':
                        mapData[i][j][k] = new Stone(k, j, i);
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
        for (int i = 0; i < mapData.length; i++) {
            for (int j = 0; j < mapData[i].length; j++) {
                for (int k = 0; k < mapData[i][j].length; k++) {
                    mapData[i][j][k].display(this);
                }
            }
        }
        popMatrix();
    }
}
