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
<<<<<<< HEAD
    translate(200, 600, -200);
    //camera(player.camX, player.camY, player.camZ, 0,0,0, player.camRotX, player.camRotY, player.camRotZ);
=======
    camera(player.camX, player.camY, player.camZ, 0,0,0, player.camRotX, player.camRotY, player.camRotZ);
>>>>>>> parent of c79a19d... Movement and Camera Orientation
    
    fill(255, 150, 100);
    textSize(50);
    map.displayMap();
    popMatrix();
<<<<<<< HEAD
=======
}

void keyPressed(){
    player.moveCam();
>>>>>>> parent of c79a19d... Movement and Camera Orientation
}
