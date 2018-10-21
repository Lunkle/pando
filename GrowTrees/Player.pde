class Player {
    Camera camera;
    int camMoveAmount = 100;

    Player() {
        camera = new Camera(0, -50, 0, -HALF_PI, 0);
    }
    
    void updatePlayer(){
        camera.applyCamera();
    }
}
