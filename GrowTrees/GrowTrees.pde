Map testMap;
Player player;

void setup() {
    fullScreen(P3D);
    player = new Player();
    testMap = new Map("map");
}

void draw() {
    background(25, 229, 229);

    keyRespond();
    player.updatePlayer();

    testMap.displayMap();

    drawAxis();
}
