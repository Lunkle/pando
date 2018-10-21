Map map;
Player player;

void setup(){
    fullScreen(P3D);
    player = new Player();
    map = new Map("testMap");
}

void draw(){
    background(25, 229, 229);
    pushMatrix();
    camera(player.camX, player.camY, player.camZ, 0,0,0, player.camRotX, player.camRotY, player.camRotZ);
    popMatrix();
    
    fill(255, 150, 100);
    textSize(50);
    map.displayMap();
}

void keyPressed(){
    player.moveCam();
}
