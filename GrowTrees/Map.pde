class Map{
    int mapSizeX, mapSizeY;
    Block[][][] map;
    
    Map(int mapSizeX, int mapSizeY){
        map = new Block[mapSizeX][mapSizeY][200];
        println("the size of this map in pixels is: " + Block.BLOCK_SIZE * mapSizeX + ", " + Block.BLOCK_SIZE * mapSizeY);
    }
    
}
