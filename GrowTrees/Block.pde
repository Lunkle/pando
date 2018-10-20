class Block{
    static final int BLOCK_SIZE = 100;
    int gridX, gridY, gridZ;
    
    Block(int gridX, int gridY, int gridZ){
        this.gridX = gridX;
        this.gridY = gridY;
        this.gridZ = gridZ;
    }
    
    void display(){
        pushMatrix();
        translate(gridX, gridY, gridZ);
        popMatrix();
    }
}
