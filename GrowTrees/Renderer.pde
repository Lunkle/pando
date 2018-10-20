boolean[][] seenGrid;
int gridLength = 10;
int gridWidth = 10;

//TEMP Function
void drawBlocks(){
    rotateY(PI/4);
    for(int i = gridLength - 1; i > -1; i--){
        for(int j = gridWidth - 1; j > -1; j--){
            if(seenGrid[i][j]){
                pushMatrix();
                translate(100*i, 700, 100*j);
                
                box(100, 100, 100);
                popMatrix();
            }
        }
    }
}
