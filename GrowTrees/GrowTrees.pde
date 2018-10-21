Map testMap;
Player player;

float yawRot, pitchRot;

void setup() {
    fullScreen(P3D);
    player = new Player();
    testMap = new Map("map");
    
    yawRot = player.camera.yaw;
    pitchRot = player.camera.pitch;
}

void draw() {
    background(25, 229, 229);

    keyRespond();
    player.updatePlayer();

    testMap.displayMap();

    drawAxis();
    hint(DISABLE_DEPTH_TEST);
    noFill();
    pushMatrix();
    translate(player.camera.xPos, player.camera.yPos, player.camera.zPos);
    rotateZ(-player.camera.yaw);
    rotateY(-player.camera.pitch);
    //box(1000,1000,1000);
    translate(500,0,0);
    
    fill(155);
    textSize(50);
    box(10);
    text("+", 0, 0);
    popMatrix();
    hint(ENABLE_DEPTH_TEST);
}
