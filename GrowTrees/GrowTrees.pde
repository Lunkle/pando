Map testMap;
Player player;

void setup() {
    fullScreen(P3D);
    player = new Player();
    testMap = new Map("map");
}

void draw() {
    background(25, 229, 229);

    drawAxis();

    keyRespond();
    player.updatePlayer();
    stroke(0);
    testMap.displayMap();
    println(frameRate);
}

void drawAxis() {
    strokeWeight(20);
    stroke(255, 0, 0);
    line(0, 0, 0, 500, 0, 0);
    stroke(0, 255, 0);
    line(0, 0, 0, 0, 500, 0);
    stroke(0, 0, 255);
    line(0, 0, 0, 0, 0, 500);
    strokeWeight(1);
}
