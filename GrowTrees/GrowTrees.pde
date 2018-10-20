void setup(){
    fullScreen(P3D);
    seenGrid = new boolean[gridLength][gridWidth];
    for(int i = 0; i < gridLength; i++){
        for(int j = 0; j < gridWidth; j++){
            seenGrid[i][j] = true;
        }
    }
}

void draw(){
    background(25, 229, 229);
    
    fill(255, 150, 100);
    textSize(50);
    text("hi", width / 2, height / 2);
    drawBlocks();
}
