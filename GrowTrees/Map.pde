class Map{
    int mapSizeX, mapSizeY;
    Block[][][] map;
    
    Map(int mapSizeX, int mapSizeY){
        initiateMap();        
        println("the size of this map in pixels is: " + Block.BLOCK_SIZE * mapSizeX + ", " + Block.BLOCK_SIZE * mapSizeY); //Better delete this crap.
    }
    
    Map(int mapSizeX, int mapSizeY, String mapFile){
        loadMap(mapFile);
        println("the size of this map in pixels is: " + Block.BLOCK_SIZE * mapSizeX + ", " + Block.BLOCK_SIZE * mapSizeY); //Better delete this crap.
    }
    
    void initiateMap(){
        map = new Block[mapSizeX][mapSizeY][200];
    }
    
    void loadMap(String mapFile){
        //TODO
        map = new Block[mapSizeX][mapSizeY][200];
    }
    
    void displayMap(){
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[i].length; j++){
                for(int k = 0; k < map[i][j].length; k++){
                    map[i][j][k].display();
                }
            }
        }
    }
    
}
