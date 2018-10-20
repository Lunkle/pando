void setup(){
    fullScreen(P3D);
    seenGrid = new boolean[gridLength][gridWidth];
}

void draw(){
    background(25, 229, 229);
    
    fill(255, 150, 100);
    textSize(50);
    text("hi", width / 2, height / 2);
}
