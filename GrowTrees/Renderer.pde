boolean[][] seenGrid;
int gridLength = 10;
int gridWidth = 10;

//TEMP Function
void drawBlocks(){
    for(int i = 0; i < gridLength; i++){
        for(int j = 0; j < gridWidth; j++){
            if(seenGrid[i][j]){
                pushMatrix();
                fill(0);
                translate(100*i, 0, 100*j);
                rotateY(PI/4);
                box(100, 100, 100);
                popMatrix();
            }
        }
    }
}
